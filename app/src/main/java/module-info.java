module com.supermarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    
    exports com.supermarket;
    exports org.example;
    exports org.example.controller;
    exports org.example.dao;
    exports org.example.db;
    exports org.example.model;
    exports org.example.Util;
    
    opens org.example.controller to javafx.fxml;
    opens org.example.model to javafx.base;
}
