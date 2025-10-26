module com.example.hotelmanagementmine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens com.example.hotelmanagementmine to javafx.fxml;
    opens com.example.hotelmanagementmine.controller to javafx.fxml;
    opens com.example.hotelmanagementmine.model to javafx.base;
    exports com.example.hotelmanagementmine;
    exports com.example.hotelmanagementmine.controller;
    exports com.example.hotelmanagementmine.model;
}