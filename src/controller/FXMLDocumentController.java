/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.client;
import classes.server;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author heber
 */
public class FXMLDocumentController implements Initializable {
    
    String values[];
    ImageView imageView[];
    int roundValues[];
    Random random;
    
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
    private ImageView image_1;
    @FXML
    private ImageView image_2;
    @FXML
    private ImageView image_3;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        values = new String[10];
        imageView = new ImageView[3];
        roundValues = new int[3];
        
        buttonsOff();
        fillArray();
        
        random = new Random();
        getCardsValues();
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
    
    //Asigna las cartas a cada imageView
    public void getCardsValues(){
        int number,value;
        Image image;
        for(int i=0; i<3; i++){
            number = random.nextInt(10);
            if(isAvailable(number, i)){
                image = new Image("/images/" + values[number] + ".png", true);
                imageView[i].setImage(image);
                //saveValue(number, i);
                value = Integer.parseInt(values[number].split("-")[0]);
                saveValue(value,i);
            }
            else{
                i--;
            }
        }
    }
    
    //Verifica que la carta no se repita
    public boolean isAvailable(int number, int i){
        boolean available = true;
        //ignora la primer asignación
        if(i != 0){
            switch(i){
                case 1:
                    //verifica si es el mismo valor que la carta 1
                    if(roundValues[0] == number)
                        available = false;
                break;
                case 2:
                    //verifica si es el mismo valor que la carta 1 o la carta 2
                    if(roundValues[0] == number || roundValues[1] == number)
                        available = false;
                break;
            }
        }
        
        return available;
    }
    
    //Guarda los valores que se van asignando
    public void saveValue(int cardValue, int pos){
        roundValues[pos] = cardValue;
    }

    @FXML
    public void onCardClick(MouseEvent event){
        ImageView clickedImageView = (ImageView) event.getSource();
        int clickedImage = Integer.parseInt(clickedImageView.getId().split("_")[1]);
        int cardValue = roundValues[clickedImage-1];
        tuMovimiento = cardValue + " de Oro";
        sendMSG(tuMovimiento);
    }
    
    public void buttonsOff(){
        image_1.setDisable(true);
        image_2.setDisable(true);
        image_3.setDisable(true);
    }
    
    public void buttonsOn(){
        image_1.setDisable(false);
        image_2.setDisable(false);
        image_3.setDisable(false);
    }
    
    public void comprobar(){
        System.out.println(tuMovimiento +"---"+ movimientoRival);
        if(!movimientoRival.equals("") && !tuMovimiento.equals("")){
            int ownMove, rivalMove;
            ownMove = Integer.parseInt(tuMovimiento.split(" ")[0]);
            rivalMove = Integer.parseInt(movimientoRival.split(" ")[0]);
            
            if(ownMove > rivalMove){
                win();
            } else if(ownMove < rivalMove){
                lose();
            } else {
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
    
    public void fillArray(){
        values[0] = "1-Oro";
        values[1] = "2-Oro";
        values[2] = "3-Oro";
        values[3] = "4-Oro";
        values[4] = "5-Oro";
        values[5] = "6-Oro";
        values[6] = "7-Oro";
        values[7] = "10-Oro";
        values[8] = "11-Oro";
        values[9] = "12-Oro";
        
        imageView[0] = image_1;
        imageView[1] = image_2;
        imageView[2] = image_3;
    }
}
