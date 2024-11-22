module com.example.gaussjordan.gaussjordan {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gaussjordan to javafx.fxml;
    exports com.example.gaussjordan;
}