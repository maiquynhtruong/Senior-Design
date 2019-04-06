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

    public List<Float> createRandomList (List<Float> list) {
        List<Float> randomList =  new ArrayList<>();
        Random random = new Random();
        for (float f : list) {
            int flag = 1;
            if (random.nextBoolean())  flag = 2;
            randomList.add(f + (float)Math.random() * 20 * (float)Math.pow(-1, flag));
        }
        return randomList;
    }

}
