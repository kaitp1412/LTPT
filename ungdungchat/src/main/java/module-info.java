module com.example.ungdungchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.ungdungchat to javafx.fxml;
    exports com.example.ungdungchat;
}