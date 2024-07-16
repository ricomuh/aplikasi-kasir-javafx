package com.ricomuh.kasir.kasir;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    AnchorPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try {
            home();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setContentArea(Node node){
        contentArea.getChildren().clear();
        contentArea.getChildren().add((Node) node);
    }

    //  home clicked
    public void home() throws Exception{
        setContentArea(FXMLLoader.load(getClass().getResource("home.fxml")));
    }

    //  barang clicked
    public void barang() throws Exception {
        setContentArea(FXMLLoader.load(getClass().getResource("barang.fxml")));
    }

    //  penjualan clicked
    public void penjualan() throws Exception {
        setContentArea(FXMLLoader.load(getClass().getResource("penjualan.fxml")));
    }

    //  customer clicked
    public void customer() throws Exception {
        setContentArea(FXMLLoader.load(getClass().getResource("customer.fxml")));
    }

}
