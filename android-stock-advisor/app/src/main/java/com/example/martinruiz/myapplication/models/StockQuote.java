package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockQuote implements Serializable {
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @SerializedName("Global Quote") private Stock stock;

}
