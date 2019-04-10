package com.example.martinruiz.myapplication.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.Stock;
import com.example.martinruiz.myapplication.models.StockQuote;
import com.example.martinruiz.myapplication.utils.DateUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import butterknife.BindView;
import butterknife.ButterKnife;

//work on the view file first
public class StockDetails extends AppCompatActivity {
    @BindView(R.id.ticker_symbol) TextView tvTickerSymbol;
    @BindView(R.id.stock_name) TextView tvStockName;
    @BindView(R.id.stock_price) TextView tvStockPrice;
    @BindView(R.id.user_prediction) EditText etUserPredict;
    @BindView(R.id.show_advice_btn) Button showAdviceButton;
    @BindView(R.id.app_prediction) TextView appPrediction;
    @BindView(R.id.app_prediction_title) TextView appPredictionText;
    @BindView(R.id.show_prediction_stat) Button showStatButton;
    @BindView(R.id.state_title) TextView tvStatTitle;
    @BindView(R.id.stat_table) TableLayout tlStatTable;
    @BindView(R.id.graph_title) TextView tvGraphTitle;
    @BindView(R.id.prediction_chart) LineChart lineChart;

    Entry[] entries; // stock price
    Entry[] entries2; //user random prediction
    Entry[] entries3; //machine leanring random prediction

    private StockQuote stockQuote;
    Stock stock;
    static boolean adviceShown = false;
    static boolean statShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(! bundle.isEmpty()){ stockQuote = (StockQuote) bundle.getSerializable("stockQuote"); }

        adviceShown = false;
        statShown = false;

        setCardData();
        setButtons();
    }

    private void setCardData() {
        tvTickerSymbol.setText(stockQuote.getStock().getSymbol());
        tvStockName.setText(stockQuote.getStock().getName());
        tvStockPrice.setText(String.format("USD %s", stockQuote.getStock().getPrice()));
        String userPrediction = etUserPredict.getText().toString();
        stock = stockQuote.getStock();
        this.entries2 = initializeRandomList(stock, this.entries2); //set user prediction with random data
        this.entries3 = initializeRandomList(stock, this.entries3); // //set ML prediction with random data

        calculateScore(entries, entries2);
        drawGraph(stock);
    }

    private Entry[] initializeRandomList(Stock stock, Entry[] entries2) {
        if (stock.getHistoricalData() == null || stock.getHistoricalData().size() == 0) {
            return null;
        }
        int index = stock.getHistoricalData().size() - 1;
        this.entries = new Entry[index + 1];
        entries2 = new Entry[index + 1];
        String key = stock.getLastUpdatedDate();
        while (index >= 0) {
            if (stock.getHistoricalData().containsKey(key)) {
                entries[index] = new Entry(index, stock.getHistoricalData().get(key));
                entries2[index] = new Entry(index, getRandomElement(stock.getHistoricalData().get(key)));
                index--;
            }

            Date date = DateUtils.convertStringToDate(key);
            date.setTime(date.getTime() - 2);
            key = DateUtils.convertDateToString(date);
        }
        return entries2;
    }

    private int calculateScore(Entry[] prices, Entry[] prediction) {
        int score = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i].getX() > prices[i - 1].getX() && prediction[i].getX() > prediction[i - 1].getX()) {
                score++;
            } else if (prices[i].getX() < prices[i - 1].getX() && prediction[i].getX() < prediction[i - 1].getX()) {
                score++;
            } else {
                score--;
            }
        }
        return score;
    }

    private float calculateCoefficient(Entry[] X, Entry[] Y) {
        float corr = 0;
        int n = X.length;
        float sum_X = 0, sum_Y = 0, sum_XY = 0;
        float squareSum_X = 0, squareSum_Y = 0;
        for (int i = 0; i < n; i++) {
            sum_X = sum_X + X[i].getX();
            sum_Y = sum_Y + Y[i].getX();
            sum_XY = sum_XY + X[i].getX() * Y[i].getX();
            squareSum_X = squareSum_X + X[i].getX() * X[i].getX();
            squareSum_Y = squareSum_Y + Y[i].getX() * Y[i].getX();
        }
        corr = (float)(n * sum_XY - sum_X * sum_Y)/ (float)(Math.sqrt((n * squareSum_X - sum_X * sum_X) * (n * squareSum_Y - sum_Y * sum_Y)));
        return corr;
    }

    private float calculateMSE(Entry[] X, Entry[] Y) {
        float sum_sq = 0;
        float mse;
        for (int i = 0; i < X.length; i++) {
            float p1 = X[i].getX();
            float p2 = Y[i].getX();
            float error = p1 - p2;
            sum_sq += (error * error);
        }
        mse = sum_sq / (X.length * X.length);
        return mse;
    }

    private void setButtons() {
        showAdviceButton.setOnClickListener(v -> {
            if (adviceShown) {
                appPrediction.setVisibility(View.GONE);
                appPredictionText.setVisibility(View.GONE);
                showAdviceButton.setText("SHOW ADVICE");
            } else {
                appPrediction.setVisibility(View.VISIBLE);
                appPredictionText.setVisibility(View.VISIBLE);
                showAdviceButton.setText("CLOSE ADVICE");
            }
            adviceShown = !adviceShown;
        });

        showStatButton.setOnClickListener(v -> {
            if (statShown) {
                tvStatTitle.setVisibility(View.GONE);
                tlStatTable.setVisibility(View.GONE);
                tvGraphTitle.setVisibility(View.GONE);
                lineChart.setVisibility(View.GONE);
                showStatButton.setText("SHOW STATISTICS");
            } else {
                tvStatTitle.setVisibility(View.VISIBLE);
                tlStatTable.setVisibility(View.VISIBLE);
                tvGraphTitle.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.VISIBLE);
                showStatButton.setText("CLOSE STATISTICS");

            }
            statShown = !statShown;
        });
    }

    private float getRandomElement (float f) {
        int flag = 1;
        Random random = new Random();
        if (random.nextBoolean())  flag = 2;
        return f + (float)Math.random() * 20 * (float)Math.pow(-1, flag);
    }

    private void drawGraph(Stock stock) {
        if (stock.getHistoricalData() == null || stock.getHistoricalData().size() == 0 || lineChart == null) {
            return;
        }
//        lineChart.setVisibility(View.VISIBLE);

        HashMap<Integer, String> xAxisValueToTextMap = new HashMap<>();
        int index = stock.getHistoricalData().size() - 1;
        String key = stock.getLastUpdatedDate();
        while (index >= 0) {
            if (stock.getHistoricalData().containsKey(key)) {
                xAxisValueToTextMap.put(index, key);
                index--;
            }

            Date date = DateUtils.convertStringToDate(key);
            date.setTime(date.getTime() - 2);
            key = DateUtils.convertDateToString(date);

        }


        Description description = new Description();
        description.setText("Stock price history");
        lineChart.setDescription(description);


        LineDataSet lineDataSet = new LineDataSet(Arrays.asList(entries), "Stock price");
        LineDataSet lineDataSet2 = new LineDataSet(Arrays.asList(entries2), "user prediction");
        LineDataSet lineDataSet3 = new LineDataSet(Arrays.asList(entries3), "ML prediction");
        lineChart.getAxisRight().setEnabled(false);

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisValueToTextMap.get((int) value);
            }
        });

        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(1.8f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setFillColor(Color.WHITE);
        lineDataSet.setFillAlpha(100);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setFillFormatter((dataSet, dataProvider) -> -10);

        lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet2.setCubicIntensity(0.2f);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setLineWidth(1.8f);
        lineDataSet2.setCircleRadius(4f);
        lineDataSet2.setCircleColor(Color.WHITE);
        lineDataSet2.setHighLightColor(Color.rgb(244, 117, 117));
        lineDataSet2.setColor(Color.WHITE);
        lineDataSet2.setFillColor(Color.WHITE);
        lineDataSet2.setFillAlpha(100);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setFillFormatter((dataSet, dataProvider) -> -10);

        lineDataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet3.setCubicIntensity(0.2f);
        lineDataSet3.setDrawCircles(false);
        lineDataSet3.setLineWidth(1.8f);
        lineDataSet3.setCircleRadius(4f);
        lineDataSet3.setCircleColor(Color.WHITE);
        lineDataSet3.setHighLightColor(Color.rgb(244, 117, 117));
        lineDataSet3.setColor(Color.WHITE);
        lineDataSet3.setFillColor(Color.WHITE);
        lineDataSet3.setFillAlpha(100);
        lineDataSet3.setDrawHorizontalHighlightIndicator(false);
        lineDataSet3.setFillFormatter((dataSet, dataProvider) -> -10);

        // create a data object with the datasets
        LineData data = new LineData(lineDataSet);
        data.addDataSet(lineDataSet2);
        data.addDataSet(lineDataSet3);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data

        lineChart.setData(data);
        lineChart.setMaxVisibleValueCount(10);
        lineChart.setVisibleXRangeMaximum(10);
        lineChart.moveViewToX(100);
        lineChart.setScaleX(1);
        lineDataSet.setColor(Color.rgb(0,0,66));
        lineDataSet.setFillColor(Color.rgb(0,0,66));
        lineDataSet2.setColor(Color.rgb(128,0,0));
        lineDataSet2.setFillColor(Color.rgb(128,0,0));
        lineDataSet3.setColor(Color.rgb(0,100,0));
        lineDataSet3.setFillColor(Color.rgb(0,100,0));

    }
}
