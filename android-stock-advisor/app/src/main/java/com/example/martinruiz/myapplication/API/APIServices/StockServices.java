package com.example.martinruiz.myapplication.API.APIServices;
import com.example.martinruiz.myapplication.models.CompanyMatches;
import com.example.martinruiz.myapplication.models.StockApiResponse;
import com.example.martinruiz.myapplication.models.StockQuote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockServices {
    @GET("query")
    Call<CompanyMatches> getCompanyMatches(@Query("function") String functionName,
                                           @Query("keywords") String keyword,
                                           @Query("apikey") String apikey);

    @GET("query")
    Call<StockQuote> getStockQuote(@Query("function") String functionName,
                                   @Query("symbol") String tickerSymbol,
                                   @Query("apikey") String apikey);

    @GET("query")
    Call<StockApiResponse> getStockData(@Query("function") String functionName,
                                              @Query("symbol") String tickerSymbol,
                                              @Query("apikey") String apiKey);

}
