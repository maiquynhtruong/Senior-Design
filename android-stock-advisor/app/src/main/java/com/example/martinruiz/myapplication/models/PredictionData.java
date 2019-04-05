package com.example.martinruiz.myapplication.models;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionData {

    Map<String, List<Float>> userPredictions = new HashMap<>();
    Map<String, List<Float>> appPredictions = new HashMap<>();

    public  static double[] testArray3 = new double[] {121.3, 112.4, 123.5, 146.6, 155.5, 134.6, 112.7, 111.8, 123.9, 122.9};
}
