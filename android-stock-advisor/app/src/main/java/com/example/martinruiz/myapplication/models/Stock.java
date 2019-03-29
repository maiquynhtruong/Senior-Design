package com.example.martinruiz.myapplication.models;

import java.io.Serializable;
import java.util.List;

public class Stock implements Serializable {
    String tickerSymbol;
    String name; // company
    Double price;
    boolean trend;
    private List<Stock> dailyPrices;

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public boolean isTrend() {
        return trend;
    }

    public void setTrend(boolean trend) {
        this.trend = trend;
    }

    public List<Stock> getDailyPrices() {
        return dailyPrices;
    }

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

    public void setDailyPrices(List<Stock> dailyPrices) {
        this.dailyPrices = dailyPrices;
    }

}
