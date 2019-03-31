package com.example.martinruiz.myapplication.API.APIServices;

import com.example.martinruiz.myapplication.models.StockInfo;
import com.example.martinruiz.myapplication.models.StockTimeSeries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockServices {
    @GET("query")
    Call<StockInfo> getStockPrice (@Query("function") String functionName, @Query("symbol") String tickerSymbol, @Query("interval") String interval, @Query("apikey") String apikey);
}
