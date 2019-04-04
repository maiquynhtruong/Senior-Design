package com.example.martinruiz.myapplication.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionData {

    private Map<String, List<Float>> userPredictions = new HashMap<>();
    private Map<String, List<Float>> appPredictions = new HashMap<>();

    public PredictionData() {
        List<Float> list = new ArrayList<Float>();
        list.add((float)12.89);
        list.add((float)13.89);
        list.add((float)12.19);
        list.add((float)8.89);
        list.add((float)10.89);
        list.add((float)9.89);
        list.add((float)12.89);
        list.add((float)18.89);
        list.add((float)5.89);
        list.add((float)12.89);
        userPredictions.put("FB", list );
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
