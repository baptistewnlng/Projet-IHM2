module com.projetihm {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.projetihm to javafx.fxml;
    exports com.projetihm;
}
