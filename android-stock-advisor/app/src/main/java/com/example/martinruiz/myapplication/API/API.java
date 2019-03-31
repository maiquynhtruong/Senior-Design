package com.example.martinruiz.myapplication.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MartinRuiz on 8/20/2017.
 */

public class API {
    public static final String ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/";
    public static final String ALPHA_VANTAGE_QUOTE = "GLOBAL_QUOTE";
    public static final String ALPHA_VANTAGE_SYMBOL_SEARCH = "SYMBOL_SEARCH";
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String KEY = "79badf94102e008963c2d50b6cfa43f2";


    private static Retrofit retrofit = null;

    public static Retrofit getApi(){
        if(retrofit == null){
            retrofit =new Retrofit.Builder()
                    .baseUrl(ALPHA_VANTAGE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
