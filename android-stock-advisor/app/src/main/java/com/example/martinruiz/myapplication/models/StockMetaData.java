package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MartinRuiz on 8/19/2017.
 */

public class StockMetaData implements Serializable{
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(String lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }

    @SerializedName("2. Symbol")
    String symbol;

    @SerializedName("3. Last Refreshed")
    String lastRefreshed;
}

