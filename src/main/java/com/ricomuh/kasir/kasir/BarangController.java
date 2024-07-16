package com.ricomuh.kasir.kasir;

import com.ricomuh.kasir.kasir.models.Barang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class BarangController implements Initializable {
    @FXML
    private TableView<Barang> tableView;
    @FXML
    private TableColumn<Barang, Integer> idColumn;
    @FXML
    private TableColumn<Barang, String> nameColumn;
    @FXML
    private TableColumn<Barang, Integer> codeColumn;
    @FXML
    private TableColumn<Barang, Double> priceColumn;

    private final ObservableList<Barang> barangList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));


        tableView.setItems(barangList);
        loadBarangItems();
    }

    private void loadBarangItems() {
        barangList.clear();
        try {
            Connection conn = DBConnection.getConn();
            String query = "SELECT * FROM barang";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                barangList.add(new Barang(rs.getInt("id"), rs.getString("name"), rs.getInt("code"), rs.getDouble("price")));
            }
            pst.close();

            setTableContent(barangList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setTableContent(ObservableList<Barang> list) {
        tableView.setItems(list);
    }

    public void onAdd(ActionEvent actionEvent) {
        // Show add dialog and get new Barang
        Barang newBarang = showAddDialog();
        if (newBarang != null) {
            try {
                // Insert newBarang into the database
                Connection conn = DBConnection.getConn();
                String query = "INSERT INTO barang (name, code, price) VALUES (?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, newBarang.getName());
                pst.setInt(2, newBarang.getCode());
                pst.setDouble(3, newBarang.getPrice());
                pst.executeUpdate();
                pst.close();
                onRefresh(null); // Refresh the table view
            } catch (Exception e) {
                e.printStackTrace();
                // Show error message
            }
        }
    }

    private Barang showAddDialog() {
        // Create the custom dialog.
        Dialog<Barang> dialog = new Dialog<>();
        dialog.setTitle("Add New Barang");

        // Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the id, name, code, and price labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField code = new TextField();
        code.setPromptText("Code");
        TextField price = new TextField();
        price.setPromptText("Price");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Code:"), 0, 1);
        grid.add(code, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(price, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a barang when the save button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int parsedCode = Integer.parseInt(code.getText());
                    double parsedPrice = Double.parseDouble(price.getText());
                    return new Barang(0, name.getText(), parsedCode, parsedPrice); // Assuming ID is auto-generated
                } catch (NumberFormatException e) {
                    // Handle invalid input
                    return null;
                }
            }
            return null;
        });

        Optional<Barang> result = dialog.showAndWait();

        return result.orElse(null);
    }

    public void onEdit(ActionEvent actionEvent) {
        Barang selectedBarang = tableView.getSelectionModel().getSelectedItem();
        if (selectedBarang != null) {
            Barang updatedBarang = showEditDialog(selectedBarang);
            if (updatedBarang != null) {
                try {
                    Connection conn = DBConnection.getConn();
                    String query = "UPDATE barang SET name = ?, code = ?, price = ? WHERE id = ?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, updatedBarang.getName());
                    pst.setInt(2, updatedBarang.getCode());
                    pst.setDouble(3, updatedBarang.getPrice());
                    pst.setInt(4, updatedBarang.getId());
                    pst.executeUpdate();
                    pst.close();
                    onRefresh(null); // Refresh the table view
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error updating item: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an item to edit.");
            alert.showAndWait();
        }
    }

    private Barang showEditDialog(Barang barang) {
        Dialog<Barang> dialog = new Dialog<>();
        dialog.setTitle("Edit Barang");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(barang.getName());
        TextField code = new TextField(String.valueOf(barang.getCode()));
        TextField price = new TextField(String.valueOf(barang.getPrice()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Code:"), 0, 1);
        grid.add(code, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(price, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int parsedCode = Integer.parseInt(code.getText());
                    double parsedPrice = Double.parseDouble(price.getText());
                    return new Barang(barang.getId(), name.getText(), parsedCode, parsedPrice);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Barang> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public void onDelete(ActionEvent actionEvent) {
        Barang selectedBarang = tableView.getSelectionModel().getSelectedItem();
        if (selectedBarang != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this item?", ButtonType.YES, ButtonType.NO);
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        Connection conn = DBConnection.getConn();
                        String query = "DELETE FROM barang WHERE id = ?";
                        PreparedStatement pst = conn.prepareStatement(query);
                        pst.setInt(1, selectedBarang.getId());
                        pst.executeUpdate();
                        pst.close();
                        onRefresh(null); // Refresh the table view
                    } catch (Exception e) {
                        e.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error deleting item: " + e.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an item to delete.");
            alert.showAndWait();
        }
    }

    public void onRefresh(ActionEvent actionEvent) {
        // Handle refresh action, e.g., reload items from the source
        loadBarangItems();
    }
}