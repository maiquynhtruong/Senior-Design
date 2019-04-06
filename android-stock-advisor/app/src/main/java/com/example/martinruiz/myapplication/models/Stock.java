package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Stock implements Serializable {
    @SerializedName("01. symbol") private String symbol;
    @SerializedName("02. open") private String open;
    @SerializedName("03. high") private String high;
    @SerializedName("04. low") private String low;
    @SerializedName("05. price") private String price;
    @SerializedName("06. volume") private String volume;
    @SerializedName("07. latest trading day") private String latestTradingDay;
    @SerializedName("08. previous price") private String previousClose;
    @SerializedName("09. change") private String change;
    @SerializedName("10. change percent") private String changePercent;

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    private String lastUpdatedDate;
    private HashMap<String, Float> historicalData;

    public Stock(String symbol, String price) {
        this.symbol = symbol;
        this.price = price;
    }

    public HashMap<String, Float> getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(HashMap<String, Float> historicalData) {
        this.historicalData = historicalData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getLatestTradingDay() {
        return latestTradingDay;
    }

    public void setLatestTradingDay(String latestTradingDay) {
        this.latestTradingDay = latestTradingDay;
    }

    public String getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(String previousClose) {
        this.previousClose = previousClose;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }
}
