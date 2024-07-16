package com.ricomuh.kasir.kasir;

import com.ricomuh.kasir.kasir.models.Customer;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    @FXML
    private TableView<Customer> tableView;
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> emailColumn;

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableView.setItems(customerList);
        loadCustomerItems();
    }

    private void loadCustomerItems() {
        customerList.clear();
        try (Connection conn = DBConnection.getConn();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM customer");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                customerList.add(new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
            setTableContent(customerList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTableContent(ObservableList<Customer> list) {
        tableView.setItems(list);
    }

    @FXML
    private void addCustomer(ActionEvent event) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");
        dialog.setHeaderText("Add Customer");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Customer(0, nameField.getText(), emailField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(customer -> {
            try (Connection conn = DBConnection.getConn();
                 PreparedStatement pst = conn.prepareStatement("INSERT INTO customer (name, email) VALUES (?, ?)")) {
                pst.setString(1, customer.getName());
                pst.setString(2, customer.getEmail());
                pst.executeUpdate();
                loadCustomerItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void deleteCustomer(ActionEvent event) {
        Customer selectedCustomer = tableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete customer?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try (Connection conn = DBConnection.getConn();
                     PreparedStatement pst = conn.prepareStatement("DELETE FROM customer WHERE id = ?")) {
                    pst.setInt(1, selectedCustomer.getId());
                    pst.executeUpdate();
                    loadCustomerItems();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void editCustomer(ActionEvent event) {
        Customer selectedCustomer = tableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Dialog<Customer> dialog = new Dialog<>();
            dialog.setTitle("Edit Customer");
            dialog.setHeaderText("Edit Customer");

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            TextField nameField = new TextField(selectedCustomer.getName());
            TextField emailField = new TextField(selectedCustomer.getEmail());

            GridPane grid = new GridPane();
            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(emailField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    return new Customer(selectedCustomer.getId(), nameField.getText(), emailField.getText());
                }
                return null;
            });

            dialog.showAndWait().ifPresent(customer -> {
                try (Connection conn = DBConnection.getConn();
                     PreparedStatement pst = conn.prepareStatement("UPDATE customer SET name = ?, email = ? WHERE id = ?")) {
                    pst.setString(1, customer.getName());
                    pst.setString(2, customer.getEmail());
                    pst.setInt(3, customer.getId());
                    pst.executeUpdate();
                    loadCustomerItems();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    private void refreshCustomer(ActionEvent event) {
        loadCustomerItems();
    }

}
