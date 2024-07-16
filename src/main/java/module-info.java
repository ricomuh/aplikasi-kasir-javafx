module com.ricomuh.kasir.kasir {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.ricomuh.kasir.kasir to javafx.fxml;
    opens com.ricomuh.kasir.kasir.models to javafx.base;
    exports com.ricomuh.kasir.kasir;


}