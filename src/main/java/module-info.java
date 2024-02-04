module com.example.lr_5 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.lr_5 to javafx.fxml;
    exports com.example.lr_5;
}