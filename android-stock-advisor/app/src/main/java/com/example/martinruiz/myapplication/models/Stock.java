package com.example.martinruiz.myapplication.models;

import java.io.Serializable;

public class Stock implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    String name; // Company
    Double price;
    boolean trend;

}
