/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import controller.FXMLDocumentController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;


/**
 *
 * @author heber
 */
public class client extends Thread{
    Socket socket;
    BufferedReader br;
    BufferedWriter bw;
    FXMLDocumentController controller;
    
    public client(FXMLDocumentController controller) {
        this.controller = controller;
    }
    
    public void conectarConElServidor(){
        try {
            socket = new Socket("localhost", 7777);
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void crearFlujos(){
        try {
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(socket.isConnected()){
            iniciar();
        }
    }
    
    public void enviarMensaje(String mensaje){
        try {
            bw.write(mensaje);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String recibirMensaje(){
        try {
            String mensaje = br.readLine();
            return mensaje;
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public void iniciar(){
        boolean serverComplete = false;
        String msg = recibirMensaje();
        while(!serverComplete){
            if(!msg.equals("")){
                controller.addMSG(msg);
                controller.getCardsValues();
                serverComplete = true;
            }
        }
    }
    
    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }
            String mensaje = recibirMensaje();
             Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                controller.addMSG(mensaje);
                            }
                        });
        }
    }
}
