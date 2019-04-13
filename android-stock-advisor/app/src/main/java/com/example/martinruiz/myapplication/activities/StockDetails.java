package com.example.martinruiz.myapplication.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
    @BindView(R.id.scoreUser) TextView tvScoreUser;
    @BindView(R.id.scoreML) TextView tvScoreML;
    @BindView(R.id.MLCorr) TextView tvMLCorr;
    @BindView(R.id.MLMSE) TextView tvMLMse;
    @BindView(R.id.userCorr) TextView tvUserCorr;
    @BindView(R.id.userMse) TextView tvUserMSE;
    @BindView(R.id.submit_btn) Button submitBtn;

    Stock stock;
    Entry[] entries; // stock price
    Entry[] entries2; //user random prediction
    Entry[] entries3; //machine leanring random prediction

    private StockQuote stockQuote;
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
        stock = stockQuote.getStock();
        this.entries = new Entry[stock.getHistoricalData().size()];
        this.entries2 = new Entry[stock.getHistoricalData().size()];
        this.entries3 = new Entry[stock.getHistoricalData().size()];
        initializeRandomList(stock, this.entries2); //set user prediction with random data
        initializeRandomList(stock, this.entries3); // //set ML prediction with random data


        drawGraph(stock);
        tvScoreUser.setText(""+calculateScore(this.entries, this.entries2));
        tvUserCorr.setText(""+ calculateCorrelation(this.entries, this.entries2));
        tvUserMSE.setText(""+calculateMSE(this.entries, this.entries2));
        tvScoreML.setText(""+calculateScore(this.entries, this.entries3));
        tvMLCorr.setText(""+ calculateCorrelation(this.entries, this.entries3));
        tvMLMse.setText(""+calculateMSE(this.entries, this.entries3));

    }

    private void initializePriceList() {
        if (stock.getHistoricalData() == null || stock.getHistoricalData().size() == 0) {
            return;
        }
        int index = stock.getHistoricalData().size() - 1;
    }

    private void initializeRandomList(Stock stock, Entry[] prediction) {
        if (stock.getHistoricalData() == null || stock.getHistoricalData().size() == 0) {
            return;
        }
        int index = stock.getHistoricalData().size() - 1;
        String key = stock.getLastUpdatedDate();
        while (index >= 0) {
            if (stock.getHistoricalData().containsKey(key)) {
                this.entries[index] = new Entry(index, stock.getHistoricalData().get(key));
                prediction[index] = new Entry(index, getRandomElement(stock.getHistoricalData().get(key)));
                index--;
            }

            Date date = DateUtils.convertStringToDate(key);
            date.setTime(date.getTime() - 2);
            key = DateUtils.convertDateToString(date);
        }
    }

    private int calculateScore(Entry[] prices, Entry[] prediction) {
        int score = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i].getY() > prices[i - 1].getY() && prediction[i].getY() > prediction[i - 1].getY()) {
                score++;
            } else if (prices[i].getY() < prices[i - 1].getY() && prediction[i].getY() < prediction[i - 1].getY()) {
                score++;
            } else {
                score--;
            }
        }
        return score;
    }

    private float calculateCorrelation(Entry[] X, Entry[] Y) {
        float corr = 0;
        int n = X.length;
        float sum_X = 0, sum_Y = 0, sum_XY = 0;
        float squareSum_X = 0, squareSum_Y = 0;
        for (int i = 0; i < n; i++) {
            sum_X = sum_X + X[i].getY();
            sum_Y = sum_Y + Y[i].getY();
            sum_XY = sum_XY + X[i].getY() * Y[i].getY();
            squareSum_X = squareSum_X + X[i].getY() * X[i].getY();
            squareSum_Y = squareSum_Y + Y[i].getY() * Y[i].getY();
        }
        corr = (n * sum_XY - sum_X * sum_Y) / (float)(Math.sqrt((n * squareSum_X - sum_X * sum_X) * (n * squareSum_Y - sum_Y * sum_Y)));
        System.out.println("sum_X:" + sum_X + ", sum_Y:" + sum_Y + ", sum_XY: " + sum_XY + ", squareSum_X: " + squareSum_X + ", squareSum_Y: " + squareSum_Y);
        Log.e("corr", String.format("%6f", corr));
        return corr;
    }

    private float calculateMSE(Entry[] X, Entry[] Y) {
        float sum_sq = 0;
        float mse;
        for (int i = 0; i < X.length; i++) {
            float p1 = X[i].getY();
            float p2 = Y[i].getY();
            float error = p1 - p2;
            sum_sq += (error * error);
        }
        mse = sum_sq / (X.length * X.length);
        return mse;
    }

    private void setButtons() {
        submitBtn.setOnClickListener(v -> {
            String userPrediction = etUserPredict.getText().toString();
            if (userPrediction.isEmpty()) {
                Snackbar.make(v, R.string.pred_empty, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(v, R.string.add_prediction, Snackbar.LENGTH_LONG).show();

                addToList(Float.valueOf(userPrediction), entries2);
            }
        });

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

    private void addToList(float prediction, Entry[] entries) {
        int length = entries.length;
        for (int i = 0; i < length - 1; i++) {
            entries[i] = entries[i+1];
        }
        entries[length-1] = new Entry(length-1, Float.valueOf(prediction));
    }

    private float getRandomElement (float f) {
        int flag = 1;
        Random random = new Random();
        if (random.nextBoolean())  flag = 2;
        return f + f * (float)Math.random()* (float)0.01 * (float)Math.pow(-1, flag);
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


        lineChart.getDescription().setEnabled(false);
        LineDataSet lineDataSet = new LineDataSet(Arrays.asList(this.entries), "Stock Price");
        LineDataSet lineDataSet2 = new LineDataSet(Arrays.asList(this.entries2), "User Prediction");
        LineDataSet lineDataSet3 = new LineDataSet(Arrays.asList(this.entries3), "ML Prediction");
        lineChart.getAxisRight().setEnabled(true);
        lineChart.setTouchEnabled(true);

        Legend l = lineChart.getLegend();
        LegendEntry[] labs = new LegendEntry[3];
        labs[0] = new LegendEntry("Stock Price", Legend.LegendForm.LINE, 30, 3, null, (Color.rgb(0,0,66)));
        labs[1] = new LegendEntry("ML Prediction", Legend.LegendForm.LINE, 30, 3, null, Color.rgb(128,0,0));
        labs[2] = new LegendEntry("User Prediction", Legend.LegendForm.LINE, 30, 3, null, Color.rgb(0,100,0));
        l.setEnabled(true);
        l.setCustom(labs);
        l.setTextSize(15);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

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
        lineDataSet.setValueTextSize(80);

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