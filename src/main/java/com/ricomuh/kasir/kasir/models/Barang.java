package com.ricomuh.kasir.kasir.models;

public class Barang {
    private final Integer id;
    private final String name;
    private final Integer code;
    private final Double price;

    public Barang(int id, String name, int code, double price) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public Double getPrice() {
        return price;
    }
}