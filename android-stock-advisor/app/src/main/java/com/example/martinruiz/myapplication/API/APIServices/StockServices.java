package com.example.martinruiz.myapplication.API.APIServices;
import com.example.martinruiz.myapplication.models.StockQuote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockServices {
    @GET("query")
    Call<StockQuote> getStockPrice (@Query("function") String functionName, @Query("symbol") String tickerSymbol, @Query("apikey") String apikey);
}
