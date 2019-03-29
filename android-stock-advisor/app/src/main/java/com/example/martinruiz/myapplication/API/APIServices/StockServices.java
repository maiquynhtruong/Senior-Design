package com.example.martinruiz.myapplication.API.APIServices;

import com.example.martinruiz.myapplication.models.Stock;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockServices {
    @GET("query")
    Call<Stock> getStockPrice (@Query("function") String functionName, @Query("symbol") String tickerSymbol, @Query("interval") String interval, @Query("apikey") String apikey);
}
