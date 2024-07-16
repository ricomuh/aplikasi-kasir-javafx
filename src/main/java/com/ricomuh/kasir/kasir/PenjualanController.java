package com.ricomuh.kasir.kasir;

import com.ricomuh.kasir.kasir.models.Barang;
import com.ricomuh.kasir.kasir.models.Penjualan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class PenjualanController implements Initializable {
    @FXML
    private TableView<Penjualan> tableView;
    @FXML
    private TableColumn<Penjualan, Integer> idColumn;
    @FXML
    private TableColumn<Penjualan, Integer> barangIdColumn;
    @FXML
    private TableColumn<Penjualan, String> barangNameColumn;
    @FXML
    private TableColumn<Penjualan, Integer> barangPriceColumn;
    @FXML
    private TableColumn<Penjualan, Integer> customerIdColumn;
    @FXML
    private TableColumn<Penjualan, String> customerNameColumn;
    @FXML
    private TableColumn<Penjualan, Integer> quantityColumn;
    @FXML
    private TableColumn<Penjualan, Integer> totalPriceColumn;
    @FXML
    private TableColumn<Penjualan, String> createdAtColumn;

    private final ObservableList<Penjualan> penjualanList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        barangIdColumn.setCellValueFactory(new PropertyValueFactory<>("barangId"));
        barangNameColumn.setCellValueFactory(new PropertyValueFactory<>("barangName"));
        barangPriceColumn.setCellValueFactory(new PropertyValueFactory<>("barangPrice"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        tableView.setItems(penjualanList);
        loadPenjualanItems();
    }

    private void loadPenjualanItems() {
        penjualanList.clear();
        try(Connection conn = DBConnection.getConn();
            var pst = conn.prepareStatement("SELECT penjualan.*, barang.name as barang_name, barang.price as barang_price, customer.name as customer_name FROM penjualan JOIN barang ON penjualan.barang_id = barang.id JOIN customer ON penjualan.customer_id = customer.id");
            var rs = pst.executeQuery()) {
            while (rs.next()) {
                penjualanList.add(new Penjualan(
                        rs.getInt("id"),
                        rs.getInt("barang_id"),
                        rs.getString("barang_name"),
                        rs.getInt("barang_price"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("sale_date"))
                ));
            }
            setTableContent(penjualanList);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void setTableContent(ObservableList<Penjualan> list) {
        tableView.setItems(list);
    }

    @FXML
    private void add(ActionEvent event) {
//        get the dialog and then save to database
        Penjualan newPenjualan = showAddDialog();
        if (newPenjualan != null) {
            try (Connection conn = DBConnection.getConn();
                 var pst = conn.prepareStatement("INSERT INTO penjualan (barang_id, customer_id, quantity, sale_date) VALUES (?, ?, ?, ?)")) {
                pst.setInt(1, newPenjualan.getBarangId());
                pst.setInt(2, newPenjualan.getCustomerId());
                pst.setInt(3, newPenjualan.getQuantity());
                pst.setString(4, newPenjualan.getSaleDate().toString());
                pst.executeUpdate();
                loadPenjualanItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Penjualan showAddDialog() {
        Dialog<Penjualan> dialog = new Dialog<>();
        dialog.setTitle("Add Penjualan");
        dialog.setHeaderText("Add Penjualan");

        ButtonType saveButtonType = new ButtonType("Save");
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField barangIdField = new TextField();
        barangIdField.setPromptText("Barang ID");
        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Customer ID");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        GridPane grid = new GridPane();
        grid.add(new Label("Barang ID:"), 0, 0);
        grid.add(barangIdField, 1, 0);
        grid.add(new Label("Customer ID:"), 0, 1);
        grid.add(customerIdField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Penjualan(0, Integer.parseInt(barangIdField.getText()), null, 0, Integer.parseInt(customerIdField.getText()), null, Integer.parseInt(quantityField.getText()), LocalDate.now());
            }
            return null;
        });

        Optional<Penjualan> result = dialog.showAndWait();

        return result.orElse(null);
    }

    @FXML
    private void delete(ActionEvent event) {
        Penjualan selectedPenjualan = tableView.getSelectionModel().getSelectedItem();
        if (selectedPenjualan != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete penjualan?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try (Connection conn = DBConnection.getConn();
                     var pst = conn.prepareStatement("DELETE FROM penjualan WHERE id = ?")) {
                    pst.setInt(1, selectedPenjualan.getId());
                    pst.executeUpdate();
                    loadPenjualanItems();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void edit(ActionEvent event) {
        Penjualan selectedPenjualan = tableView.getSelectionModel().getSelectedItem();
        if (selectedPenjualan != null) {
            Penjualan editedPenjualan = showEditDialog(selectedPenjualan);
            if (editedPenjualan != null) {
                try (Connection conn = DBConnection.getConn();
                     var pst = conn.prepareStatement("UPDATE penjualan SET barang_id = ?, customer_id = ?, quantity = ?, sale_date = ? WHERE id = ?")) {
                    pst.setInt(1, editedPenjualan.getBarangId());
                    pst.setInt(2, editedPenjualan.getCustomerId());
                    pst.setInt(3, editedPenjualan.getQuantity());
                    pst.setString(4, editedPenjualan.getSaleDate().toString());
                    pst.setInt(5, editedPenjualan.getId());
                    pst.executeUpdate();
                    loadPenjualanItems();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Penjualan showEditDialog(Penjualan penjualan) {
        Dialog<Penjualan> dialog = new Dialog<>();
        dialog.setTitle("Edit Penjualan");
        dialog.setHeaderText("Edit Penjualan");

        ButtonType saveButtonType = new ButtonType("Save");
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField barangIdField = new TextField(String.valueOf(penjualan.getBarangId()));
        TextField customerIdField = new TextField(String.valueOf(penjualan.getCustomerId()));
        TextField quantityField = new TextField(String.valueOf(penjualan.getQuantity()));

        GridPane grid = new GridPane();
        grid.add(new Label("Barang ID:"), 0, 0);
        grid.add(barangIdField, 1, 0);
        grid.add(new Label("Customer ID:"), 0, 1);
        grid.add(customerIdField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Penjualan(penjualan.getId(), Integer.parseInt(barangIdField.getText()), null, 0, Integer.parseInt(customerIdField.getText()), null, Integer.parseInt(quantityField.getText()), LocalDate.now());
            }
            return null;
        });

        Optional<Penjualan> result = dialog.showAndWait();

        return result.orElse(null);
    }

}
