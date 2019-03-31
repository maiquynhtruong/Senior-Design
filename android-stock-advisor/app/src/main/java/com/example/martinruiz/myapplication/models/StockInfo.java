package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StockInfo implements Serializable {
    public StockMetaData getStockMetaData() {
        return stockMetaData;
    }

    public void setStockMetaData(StockMetaData stockMetaData) {
        this.stockMetaData = stockMetaData;
    }

    public StockTimeSeries getStockTimeSeries() {
        return stockTimeSeries;
    }

    public void setStockTimeSeries(StockTimeSeries stockTimeSeries) {
        this.stockTimeSeries = stockTimeSeries;
    }

    @SerializedName("Meta Data")
    private StockMetaData stockMetaData;

    @SerializedName("Time Series (5min)")
    private StockTimeSeries stockTimeSeries;



}
