package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Saksham Dhawan on 2/17/18.
 */

public class StockData {

    @SerializedName("1. open")
    Float open;
    @SerializedName("2. high")
    Float high;
    @SerializedName("3. low")
    Float low;
    @SerializedName("4. close")
    Float close;
    @SerializedName("5. volume")
    Float volume;


    public Float getOpen() {
        return open;
    }

    public void setOpen(Float open) {
        this.open = open;
    }

    public Float getHigh() {
        return high;
    }

    public void setHigh(Float high) {
        this.high = high;
    }

    public Float getLow() {
        return low;
    }

    public void setLow(Float low) {
        this.low = low;
    }

    public Float getClose() {
        return close;
    }

    public void setClose(Float close) {
        this.close = close;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }
}