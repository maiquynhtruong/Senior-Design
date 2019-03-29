package com.example.martinruiz.myapplication.models;

import java.io.Serializable;

public class Stock implements Serializable {
    String name; // company
    Double price;
    boolean trend;

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

}
