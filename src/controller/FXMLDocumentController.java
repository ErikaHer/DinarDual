/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.client;
import classes.server;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 *
 * @author heber
 */
public class FXMLDocumentController implements Initializable {
    
    server sv = new server(this);
    client cl = new client(this);
    int rol;
    int turno = 1;
    int scorep1 = 0;
    int scorep2 = 0;
    String movimientoRival = "";
    String tuMovimiento = "";
    
    @FXML
    private Label label;
    @FXML
    private Label aviso;
    @FXML
    private Label score1;
    @FXML
    private Label score2;
    @FXML
    private Button player1;
    @FXML
    private Button player2;
    @FXML
    private ImageView piedra;
    @FXML
    private ImageView papel;
    @FXML
    private ImageView tijera;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        buttonsOff();
    }
    
    public void runClient() {
        buttonsOn();
        rol = 2;
        score1.setText("score jugador 1:");
        score2.setText("score jugador 2:");
        player1.setDisable(true);
        player2.setDisable(true);
        cl.conectarConElServidor();
        label.setText("Eres el Jugador 2");
        cl.crearFlujos();
        cl.setDaemon(true);
        cl.start();
    }
    public void runServer() {
        buttonsOn();
        rol = 1;
        score1.setText("score jugador 1:");
        score2.setText("score jugador 2:");
        player1.setDisable(true);
        player2.setDisable(true);
        sv.abrirPuerto();
        sv.esperarAlCliente();
        label.setText("Eres el jugador 1");
        sv.crearFlujos();
        sv.setDaemon(true);
        sv.start();
    }
    
    public void sendMSG(String mensaje){
        buttonsOff();
        if(rol == 1){
            sv.enviarMensaje(mensaje);
        }else if(rol == 2){
            cl.enviarMensaje(mensaje);
        }
        comprobar();
    }
    
    public void piedra(){
        tuMovimiento = "piedra";
        sendMSG("piedra");
    }
    
    public void papel(){
        tuMovimiento = "papel";
        sendMSG("papel");
    }
    
    public void tijera(){
        tuMovimiento = "tijera";
        sendMSG("tijera");
    }
    
    public void buttonsOff(){
        piedra.setDisable(true);
        papel.setDisable(true);
        tijera.setDisable(true);
    }
    
    public void buttonsOn(){
        piedra.setDisable(false);
        papel.setDisable(false);
        tijera.setDisable(false);
    }
    
    public void comprobar(){
        System.out.println(tuMovimiento +"---"+ movimientoRival);
        if(!movimientoRival.equals("") && !tuMovimiento.equals("")){
            if(tuMovimiento.equals("piedra") && movimientoRival.equals("tijera")){
                win();
            } else if(tuMovimiento.equals("piedra") && movimientoRival.equals("papel")){
                lose();
            } else if(tuMovimiento.equals("papel") && movimientoRival.equals("piedra")){
                win();
            } else if(tuMovimiento.equals("papel") && movimientoRival.equals("tijera")){
                lose();
            } else if(tuMovimiento.equals("tijera") && movimientoRival.equals("papel")){
                win();
            } else if(tuMovimiento.equals("tijera") && movimientoRival.equals("piedra")){
                lose();
            } else if(tuMovimiento.equals(movimientoRival)){
                empate();
            }
        }
    }
    
    public void win(){
        aviso.setText("¡Ganaste! El rival eligió " + movimientoRival);
        if(rol == 1){
        scorep1+=1;
        }else if(rol == 2){
        scorep2+=1;
        }
        score1.setText("score jugador 1: " + scorep1);
        score2.setText("score jugador 2: " + scorep2);
        tuMovimiento="";
        movimientoRival="";
        turno = 1;
        buttonsOn();
    }
    
    public void lose(){
        if(rol == 1){
        scorep2+=1;
        }else if(rol == 2){
        scorep1+=1;
        }
        aviso.setText("Perdiste :c El rival eligió " + movimientoRival);
        score1.setText("score jugador 1: " + scorep1);
        score2.setText("score jugador 2: " + scorep2);
        tuMovimiento="";
        movimientoRival="";
        turno = 1;
        buttonsOn();
    }
    
    public void empate(){
        aviso.setText("¡Empate! El rival tambien eligió " + movimientoRival);
        scorep1++;
        scorep2++;
        score1.setText("score jugador 1: " + scorep1);
        score2.setText("score jugador 2: " + scorep2);
        tuMovimiento="";
        movimientoRival="";
        turno = 1;
        buttonsOn();
    }
    
    public void addMSG(String mensaje){
        if(turno == 1) {
            movimientoRival = mensaje;
            turno = 2;
        }
        comprobar();
    }
}
