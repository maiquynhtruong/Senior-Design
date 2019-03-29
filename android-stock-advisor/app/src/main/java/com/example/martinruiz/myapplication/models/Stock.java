package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Stock implements Serializable {
    String tickerSymbol;
    String name; // company
    Double price;
    boolean trend;
    private List<Stock> dailyPrices;
    @SerializedName("Time Series (5min)") private String timeSeries;
    @SerializedName("1. open") private String open;
    @SerializedName("2. high") private String high;
    @SerializedName("3. low") private String low;
    @SerializedName("4. close") private String close;
    @SerializedName("5. volume") private String volume;

    public boolean isTrend() {
        return trend;
    }

    public String getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(String timeSeries) {
        this.timeSeries = timeSeries;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public boolean getTrend() {
        return trend;
    }

    public void setTrend(boolean trend) {
        this.trend = trend;
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

    public List<Stock> getDailyPrices() {
        return dailyPrices;
    }
}
