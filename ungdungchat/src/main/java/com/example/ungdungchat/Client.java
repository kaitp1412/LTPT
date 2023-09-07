package com.example.ungdungchat;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        }catch (IOException e){
            System.out.println(e.getMessage());
            CloseEveryThing(socket,bufferedWriter,bufferedReader);
        }

    }
    public void sendMessageToClient(String messageFrom){
        try {
            ImportToDatabase importToDatabase = new ImportToDatabase();
            importToDatabase.AddToDatabase("Client : " +messageFrom);
            if (messageFrom.equals("")){
                importToDatabase.DeleteAll();
            }
            bufferedWriter.write(messageFrom);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }catch (IOException | SQLException e){
            System.out.println(e.getMessage());
            CloseEveryThing(socket,bufferedWriter,bufferedReader);
        }
    }
    public void receiveMessage(VBox vBox){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                    try {
                        String message = bufferedReader.readLine();
                        HelloController.addLabel(message,vBox);
                    }catch (IOException e){
                        System.out.println(e.getMessage());
                        CloseEveryThing(socket,bufferedWriter,bufferedReader);
                        break;
                    }
                }

            }
        }).start();
    }

    public void CloseEveryThing(Socket socket , BufferedWriter bufferedWriter , BufferedReader bufferedReader){
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
class ImportToDatabase {
    private String localhost = "localhost:3306";
    private String dbname = "mexu";
    private String username = "root";
    private String password = "kaitp";
    private String URLConnect = "jdbc:mysql://" + localhost + "/" + dbname;

    public Connection ConnectToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(URLConnect, username, password);
        return connection;
    }

    public void AddToDatabase(String message) throws SQLException {
        ImportToDatabase importToDatabase = new ImportToDatabase();
        Connection connection = importToDatabase.ConnectToDatabase();
        String result = message;
        String query = "insert into Message(Message) values ('" + result + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public void DeleteAll() throws SQLException {
        ImportToDatabase importToDatabase = new ImportToDatabase();
        Connection connection = importToDatabase.ConnectToDatabase();
        String query = "TRUNCATE TABLE Message";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }
}