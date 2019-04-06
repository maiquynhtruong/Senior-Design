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
        String userPrediction = etUserPredict.getText().toString();

        drawGraph(stockQuote.getStock());
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

    private List<Float> getUserPredction (Stock stock) {
        List<Float> hsdta = stock.getHistoricalData();
        return stockQuote.createRandomList(hsdata);
    }

    private void drawGraph(Stock stock) {
        if (stock.getHistoricalData() == null || stock.getHistoricalData().size() == 0 || lineChart == null) {
            return;
        }
//        lineChart.setVisibility(View.VISIBLE);

        int index = stock.getHistoricalData().size() - 1;
        Entry[] entries = new Entry[index + 1];
        HashMap<Integer, String> xAxisValueToTextMap = new HashMap<>();

        String key = stock.getLastUpdatedDate();
        while (index >= 0) {
            if (stock.getHistoricalData().containsKey(key)) {
                entries[index] = new Entry(index, stock.getHistoricalData().get(key));
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
        lineDataSet.setColor(Color.rgb(244,125,66));
        lineDataSet.setFillColor(Color.rgb(244,125,66));

    }
}
