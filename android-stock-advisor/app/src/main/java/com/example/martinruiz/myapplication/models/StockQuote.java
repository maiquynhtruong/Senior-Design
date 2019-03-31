package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockQuote implements Serializable {
    @SerializedName("Global Quote") private Stock stock;

    public Stock getStock() {
        if (stock == null) stock = new Stock();
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

}
