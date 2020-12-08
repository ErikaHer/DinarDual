/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.client;
import classes.server;
import DBConnection.PostgresCon;
import DBConnection.ScoreBoard;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

/**
 *
 * @author ESUMA
 */
public class FXMLDocumentController implements Initializable {

    String values[];
    ImageView imageView[];
    int roundValues[];
    Random random;
    String clientCardAvailable[];

    server sv = new server(this);
    client cl = new client(this);
    int rol;
    int turno = 0;
    int scorep1 = 0;
    int scorep2 = 0;
    String movimientoRival = "";
    String tuMovimiento = "";
    private int ciclos = 0;

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
    
    @FXML
    private Text description;

    private ImageView clickedImageView;
    private boolean[][] usedImages;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        values = new String[10];
        imageView = new ImageView[3];
        roundValues = new int[3];
        usedImages = new boolean[3][3];
        for (int i = 0; i<3; i++) {
            for(int j=0;j<3;j++){
                usedImages[i][j] = false;
            }
        }

        buttonsOff();
        fillArray();

        random = new Random();
    }

    public void guardarScore(int num) throws ParseException {
        if (num == 3) {
            String fecha = generarFecha();
            PostgresCon conn = new PostgresCon();
            conn.setScoreboard("Player1", "Player2", scorep1, scorep2, fecha);
            ArrayList<ScoreBoard> board = conn.getScoreboard();
            mostrarBoard(board, AlertType.INFORMATION);
        }
    }

    public void mostrarBoard( ArrayList<ScoreBoard> scores , AlertType alertType) {
        ObservableList<ScoreBoard> terminal = FXCollections.observableArrayList(scores);
        TableView table = new TableView();
        TableColumn col1 = new TableColumn("username1");
        col1.setCellValueFactory(
                new PropertyValueFactory<ScoreBoard, String>("user1"));
        TableColumn col2 = new TableColumn("score1");
        col2.setCellValueFactory(
                new PropertyValueFactory<ScoreBoard, String>("score1"));
        TableColumn col3 = new TableColumn("username2");
        col3.setCellValueFactory(
                new PropertyValueFactory<ScoreBoard, String>("user2"));
        TableColumn col4 = new TableColumn("score2");
        col4.setCellValueFactory(
                new PropertyValueFactory<ScoreBoard, String>("score2"));
        TableColumn col5 = new TableColumn("date");
        col5.setCellValueFactory(
                new PropertyValueFactory<ScoreBoard, String>("date"));
        scores.forEach((score) -> {
            System.out.println("Score: "+score.getUser1()+"|"+score.getScore1()+"|"+score.getUser2()+"|"+score.getScore2()+"|"+score.getDate());
        });
        table.getItems().removeAll();
        table.getItems().setAll(terminal);
        table.getColumns().setAll(col1,col2,col3,col4,col5);
        System.out.println(table.getItems().toString());
        
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle("ScoreBoard");
        alert.getDialogPane().setContent(table);
        alert.getDialogPane().getContent().setStyle("-fx-background-color: transparent;");
        alert.setResizable(true);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public String generarFecha(){
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String year = Integer.toString(localDate.getYear());
        String month = Integer.toString(localDate.getMonthValue());
        String day = Integer.toString(localDate.getDayOfMonth());
        String fecha = day+"/"+month+"/"+year+"/";
        return fecha;
    }

    public void runClient() throws ParseException {
        buttonsOn(false);
        rol = 2;
        score1.setText("Score jugador 1: ");
        score2.setText("Score jugador 2: ");
        player1.setDisable(true);
        player2.setDisable(true);
        description.setVisible(false);
        cl.conectarConElServidor();
        label.setText("Eres el Jugador 2");
        cl.crearFlujos();
        cl.setDaemon(true);
        cl.start();
    }

    public void runServer() {
        buttonsOn(false);
        rol = 1;
        turno = 1;
        score1.setText("Score jugador 1: ");
        score2.setText("Score jugador 2: ");
        player1.setDisable(true);
        player2.setDisable(true);
        description.setVisible(false);
        sv.abrirPuerto();
        sv.esperarAlCliente();
        label.setText("Eres el jugador 1");
        sv.crearFlujos();
        sv.setDaemon(true);
        sv.start();
    }

    public void sendMSG(String mensaje) throws ParseException {
        buttonsOff();
        if (rol == 1) {
            sv.enviarMensaje(mensaje);
        } else if (rol == 2) {
            cl.enviarMensaje(mensaje);
        }
        comprobar();
    }

    //Asigna las cartas a cada imageView
    public void getCardsValues() {
        int number, value;
        Image image;
        for (int i = 0; i < 3; i++) {
            number = random.nextInt(10);
            value = Integer.parseInt(values[number].split("-")[0]);
            System.out.println(isAvailable(value, i) + "-number: " + value);
            if (isAvailable(value, i)) {
                image = new Image("/images/" + values[number] + ".png", true);
                imageView[i].setImage(image);
                //saveValue(number, i);
                value = Integer.parseInt(values[number].split("-")[0]);
                saveValue(value, i);
            } else {
                i--;
            }
        }
        if (rol == 1) {
            sv.enviarMensaje(roundValues[0] + "-" + roundValues[1] + "-" + roundValues[2]);
        }
    }

    public void addNotAvailable(String mensaje) {
        System.out.println(mensaje);
    }

    //Verifica que la carta no se repita
    public boolean isAvailable(int number, int i) {
        boolean available = true;
        boolean cardsAvailable = true;
        //ignora la primer asignación
        if (rol == 2) {
            if (clientCardAvailable[0].equals("" + number) || clientCardAvailable[1].equals("" + number) || clientCardAvailable[2].equals("" + number)) {
                available = false;
                cardsAvailable = false;
            }
        }
        if (i != 0 && cardsAvailable) {
            switch (i) {
                case 1:
                    //verifica si es el mismo valor que la carta 1
                    if (roundValues[0] == number) {
                        available = false;
                    }
                    break;
                case 2:
                    //verifica si es el mismo valor que la carta 1 o la carta 2
                    if (roundValues[0] == number || roundValues[1] == number) {
                        available = false;
                    }
                    break;
            }
        }

        return available;
    }

    //Guarda los valores que se van asignando
    public void saveValue(int cardValue, int pos) {
        roundValues[pos] = cardValue;
    }

    @FXML
    public void onCardClick(MouseEvent event) throws ParseException {
        clickedImageView = (ImageView) event.getSource();
        int clickedImage = Integer.parseInt(clickedImageView.getId().split("_")[1]);
        int cardValue = roundValues[clickedImage - 1];
        tuMovimiento = cardValue + " de Oro";

        clickedImageView = (ImageView) event.getSource();
        String currentImageView = clickedImageView.getId();
        tuMovimiento = cardValue + " de Oro";

        switch (currentImageView) {
            case "image_1":
                usedImages[0][0] = true;
                break;

            case "image_2":
                usedImages[1][0] = true;
                break;

            case "image_3":
                usedImages[2][0] = true;
                break;
        }
        sendMSG(tuMovimiento);
    }

    public void buttonsOff() {
        image_1.setDisable(true);
        image_2.setDisable(true);
        image_3.setDisable(true);
    }

    public void buttonsOn(boolean won) {
        if (!usedImages[0][0]) {
            image_1.setDisable(false);
        } else if(!usedImages[0][1]) {
            if (won) {
                image_1.setImage(new Image("/images/round_won.png"));
            } else {
                image_1.setImage(new Image("/images/round_lost.png"));
            }
            usedImages[0][1] = true;
        }

        if (!usedImages[1][0]) {
            image_2.setDisable(false);
        } else if(!usedImages[1][1]) {
            if (won) {
                image_2.setImage(new Image("/images/round_won.png"));
            } else {
                image_2.setImage(new Image("/images/round_lost.png"));
            }
            usedImages[1][1] = true;
        }

        if (!usedImages[2][0]) {
            image_3.setDisable(false);
        } else if(!usedImages[2][1]) {
            if (won) {
                image_3.setImage(new Image("/images/round_won.png"));
            } else {
                image_3.setImage(new Image("/images/round_lost.png"));
            }
            usedImages[2][1] = true;
        }
    }

    public void comprobar() throws ParseException {
        if (!movimientoRival.equals("") && !tuMovimiento.equals("")) {
            int ownMove, rivalMove;
            ownMove = Integer.parseInt(tuMovimiento.split(" ")[0]);
            rivalMove = Integer.parseInt(movimientoRival.split(" ")[0]);
            if (ownMove > rivalMove) {
                win();
                ciclos++;
                System.out.println(ciclos);
                guardarScore(ciclos);
                
            } else if (ownMove < rivalMove) {
                lose();
                ciclos++;
                System.out.println(ciclos);
            } else {
                empate();
                ciclos++;
                System.out.println(ciclos);
            }
        }
    }

    public void win() {
        boolean won = true;
        aviso.setText("¡Ganaste! El rival eligió " + movimientoRival);
        if (rol == 1) {
            scorep1 += 1;
        } else if (rol == 2) {
            scorep2 += 1;
        }
        score1.setText("Score jugador 1: " + scorep1);
        score2.setText("Score jugador 2: " + scorep2);
        tuMovimiento = "";
        movimientoRival = "";
        turno = 1;
        buttonsOn(won);
    }

    public void lose() {
        boolean won = false;
        if (rol == 1) {
            scorep2 += 1;
        } else if (rol == 2) {
            scorep1 += 1;
        }
        aviso.setText("Perdiste, el rival eligió " + movimientoRival);
        score1.setText("Score jugador 1: " + scorep1);
        score2.setText("Score jugador 2: " + scorep2);
        tuMovimiento = "";
        movimientoRival = "";
        turno = 1;
        buttonsOn(won);
    }

    public void empate() {
        aviso.setText("¡Empate! El rival tambien eligió " + movimientoRival);
        scorep1++;
        scorep2++;
        score1.setText("Score jugador 1: " + scorep1);
        score2.setText("Score jugador 2: " + scorep2);
        tuMovimiento = "";
        movimientoRival = "";
        turno = 1;
        buttonsOn(false);
    }

    public void addMSG(String mensaje) throws ParseException {
        if (turno == 0) {
            clientCardAvailable = mensaje.split("-");
            turno = 1;
        } else if (turno == 1) {
            movimientoRival = mensaje;
            turno = 2;
            comprobar();
        }
    }

    public void fillArray() {
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
