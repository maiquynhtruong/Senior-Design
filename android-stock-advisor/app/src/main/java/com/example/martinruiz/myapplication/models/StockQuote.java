package com.example.martinruiz.myapplication.models;

import com.example.martinruiz.myapplication.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StockQuote implements Serializable {
    @SerializedName("Global Quote") private Stock stock;

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

}
