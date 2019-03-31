package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockInfo {
    public StockMetaData getStockMetaData() {
        return stockMetaData;
    }

    public void setStockMetaData(StockMetaData stockMetaData) {
        this.stockMetaData = stockMetaData;
    }

    public List<StockTimeSeries> getStockTimeSeries() {
        return stockTimeSeries;
    }

    public void setStockTimeSeries(List<StockTimeSeries> stockTimeSeries) {
        this.stockTimeSeries = stockTimeSeries;
    }

    @SerializedName("Meta Data")
    private StockMetaData stockMetaData;

    @SerializedName("Time Series (5min)")
    private List<StockTimeSeries> stockTimeSeries;



}
