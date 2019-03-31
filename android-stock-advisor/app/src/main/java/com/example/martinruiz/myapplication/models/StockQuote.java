package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockQuote implements Serializable {
    @SerializedName("Global Quote") private Stock stock;

}
