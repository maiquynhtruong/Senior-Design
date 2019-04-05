package com.example.martinruiz.myapplication.models;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionData {

    private Map<String, List<Float>> userPredictions = new HashMap<>();
    private Map<String, List<Float>> appPredictions = new HashMap<>();

    public static double[] userPreditions = new double[]{121.3, 112.4, 123.5, 146.6, 155.5, 134.6, 112.7, 111.8, 123.9, 122.9};
    public static double[] MLPreditions = new double[]{124.3, 103.4, 173.5, 106.6, 195.5, 124.6, 102.7, 101.8, 143.9, 112.9};
    public static double[] realPrices = new double[]{123.3, 132.4, 143.5, 126.6, 135.5, 114.6, 142.7, 131.8, 128.9, 125.9};

    public PredictionData() {
        List<Float> list = new ArrayList<Float>();
        list.add((float) 12.89);
        list.add((float) 13.89);
        list.add((float) 12.19);
        list.add((float) 8.89);
        list.add((float) 10.89);
        list.add((float) 9.89);
        list.add((float) 12.89);
        list.add((float) 18.89);
        list.add((float) 5.89);
        list.add((float) 12.89);
        userPredictions.put("FB", list);
        appPredictions.put("FB", list);
    }

    public Map<String, List<Float>> getUserPredictions() {
        return userPredictions;
    }

    public void setUserPredictions(Map<String, List<Float>> userPredictions) {
        this.userPredictions = userPredictions;
    }

    public Map<String, List<Float>> getAppPredictions() {
        return appPredictions;
    }

    public void setAppPredictions(Map<String, List<Float>> appPredictions) {
        this.appPredictions = appPredictions;
    }

    public List<Float> getUserPredictions(String stockName) {
        return userPredictions.get(stockName);
    }

    public List<Float> getAppPredictions(String stockName) {
        return appPredictions.get(stockName);
    }

    public void inputUserPredictionList(String stockName, List<Float> list) {
        userPredictions.put(stockName, list);
    }

    public void inputAppPredictionList(String stockName, List<Float> list) {
        appPredictions.put(stockName, list);
    }
}
