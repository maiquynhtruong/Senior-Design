package com.example.martinruiz.myapplication.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.PredictionData;
import com.example.martinruiz.myapplication.models.Stock;
import com.example.martinruiz.myapplication.models.StockQuote;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.api.client.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    @BindView(R.id.prediction_chart) LineChart lineChart;
    private StockQuote stockQuote;
    static boolean adviceShown = false;


    private List<Float> userData;
    private List<Float> MLData;
    private List<Float> realData;
    private PredictionData appData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(! bundle.isEmpty()){ stockQuote = (StockQuote) bundle.getSerializable("stockQuote"); }
        adviceShown = false;
        setCardData();
    }

    private void setCardData() {
        tvTickerSymbol.setText(stockQuote.getStock().getSymbol());
        tvStockName.setText(stockQuote.getStock().getName());
        tvStockPrice.setText(String.format("USD %s", stockQuote.getStock().getPrice()));
        String userPrediction = etUserPredict.getText().toString();
        showAdviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adviceShown) {
                    appPrediction.setVisibility(View.GONE);
                    appPredictionText.setVisibility(View.GONE);
                    showAdviceButton.setText("Show Advice");
                } else {
                    appPrediction.setVisibility(View.VISIBLE);
                    appPredictionText.setVisibility(View.VISIBLE);
                    showAdviceButton.setText("Close Advice");
                }
                adviceShown = !adviceShown;
            }
        });
    }

    private void drawGraph(Stock stock) {
        if (stock.getHistoricalData() == null || stock.getHistoricalData().size() == 0 || lineChart == null) {
            return;
        }
        lineChart.setVisibility(View.VISIBLE);

        int index = stock.getHistoricalData().size() - 1;
        Entry[] entries = new Entry[index + 1];
        HashMap<Integer, String> xAxisValueToTextMap = new HashMap<>();

        String key = stock.getLastUpdatedDate();
        while (index >= 0) {
            if (stock.getHistoricalData().containsKey(key)) {
                entries[index] = new Entry(index, stock.getHistoricalData().get(key).floatValue());
                xAxisValueToTextMap.put(index, key);
                index--;
            }

            Date date = DateUtils.convertStringToDate(key);
            date.setTime(date.getTime() - 2);
            key = DateUtils.convertDateToString(date);

        }


        Description description = new Description();
        description.setText(StringUtils.getString(R.string.stock_history));
        lineChart.setDescription(description);


        LineDataSet lineDataSet = new LineDataSet(Arrays.asList(entries), StringUtils.getString(R.string.stock_price));

        lineChart.getAxisRight().setEnabled(false);

        lineChart.getXAxis().setValueFormatter((value, axis) -> {
            Timber.d("x value for  " + value + " is " + xAxisValueToTextMap.get((int) value));
            return xAxisValueToTextMap.get((int) value);
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
        lineDataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });

        // create a data object with the datasets
        LineData data = new LineData(lineDataSet);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data

        lineChart.setData(data);
        lineChart.setMaxVisibleValueCount(10);
        lineChart.setVisibleXRangeMaximum(10);
        lineChart.moveViewToX(100);
        lineChart.setScaleX(1);
        lineDataSet.setColor(ContextCompat.getColor(StockTrackerApp.getContext(), R.color.holo_orange_dark));
        lineDataSet.setFillColor(ContextCompat.getColor(StockTrackerApp.getContext(), R.color.holo_orange_dark));

    }
}
