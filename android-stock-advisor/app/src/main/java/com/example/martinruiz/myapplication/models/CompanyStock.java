package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MartinRuiz on 8/19/2017.
 */

public class CompanyStock implements Serializable{
    private Company company;
    @SerializedName("list")
    private List<Stock> weeklyStock;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Stock> getWeeklyStock() {
        return weeklyStock;
    }

    public void getWeeklyStock(List<Stock> weeklyStock) {
        this.weeklyStock = weeklyStock;
    }
}

