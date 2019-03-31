package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Company implements Serializable {

    @SerializedName("1. symbol") private String symbol;
    @SerializedName("2. name") private String name;
    @SerializedName("3. type") private String type;
    @SerializedName("4. region") private String region;
    @SerializedName("5. marketOpen") private String open;
    @SerializedName("6. marketClose") private String close;
    @SerializedName("7. timezone") private String timezone;
    @SerializedName("8. currency") private String currency;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }

    @SerializedName("9. matchScore") private String matchScore;

}
