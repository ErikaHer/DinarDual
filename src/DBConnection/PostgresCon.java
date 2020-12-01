/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBConnection;

import java.sql.*;
import java.util.*;

/**
 *
 * @author claud
 */
public class PostgresCon {

    private final static String url = "jdbc:mysql://localhost:3306/DinarDuel";
    private final static String user = "root";
    private final static String password = "";
    private static final String querySelect = "SELECT username1,score1,username2,score2,date FROM Scoreboard;";
    private static final String queryInsert = "INSERT INTO Scoreboard"
            + " (username1, score1, username2, score2, date) VALUES" + " (?, ?, ?, ?, ?);";
    private int i = 0;
    private int size;

    public ArrayList<ScoreBoard> getScoreboard() {
        ArrayList scr = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement preparedStatement = connection.prepareStatement(querySelect);) {
            if (connection != null) {
                System.out.println("Connection Successful");
                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String username1 = resultSet.getString("username1");
                    String username2 = resultSet.getString("username2");
                    int score1 = resultSet.getInt("score1");
                    int score2 = resultSet.getInt("score2");
                    String date = resultSet.getString("date");
                    ScoreBoard data = new ScoreBoard(username1, score1, username2, score2, date);
                    scr.add(data);
                    System.out.println("ScoreBoard |" + i + "|: " + "/" + username1 + "/" + score1 + "/" + username2 + "/" + score2 + "/" + date + "/");
                    i++;
                }
            } else {
                System.out.println("Error!. Failed to make connection");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scr;
    }

    public void setScoreboard(String user1, String user2, int score1, int score2, String date) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement preparedStatement = connection.prepareStatement(queryInsert)) {
            if (connection != null) {
                System.out.println("Updating Scoreboard...");
                preparedStatement.setString(1, user1);
                preparedStatement.setInt(2, score1);
                preparedStatement.setString(3, user2);
                preparedStatement.setInt(4, score2);
                preparedStatement.setString(5, date);
                System.out.println(preparedStatement);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
