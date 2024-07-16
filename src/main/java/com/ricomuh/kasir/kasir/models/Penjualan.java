package com.ricomuh.kasir.kasir.models;

import java.time.LocalDate;

public class Penjualan {
    private Integer id;
    private Integer barang_id;
    private String barang_name;
    private Integer barang_price;
    private Integer customer_id;
    private String customer_name;
    private Integer quantity;
    private Integer total_price;
    private LocalDate sale_date;

    public Penjualan(Integer id, Integer barang_id, String barang_name, Integer barang_price, Integer customer_id, String customer_name, Integer quantity, LocalDate sale_date) {
        this.id = id;
        this.barang_id = barang_id;
        this.barang_name = barang_name;
        this.barang_price = barang_price;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.quantity = quantity;
        this.total_price = barang_price * quantity;
        this.sale_date = sale_date;
    }

    public Integer getId() {
        return id;
    }

    public Integer getBarangId() {
        return barang_id;
    }

    public String getBarangName() {
        return barang_name;
    }

    public Integer getBarangPrice() {
        return barang_price;
    }

    public Integer getCustomerId() {
        return customer_id;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getTotalPrice() {
        return total_price;
    }

    public LocalDate getSaleDate() {
        return sale_date;
    }




}
