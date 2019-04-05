package com.example.martinruiz.myapplication.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.PredictionData;
import com.example.martinruiz.myapplication.models.StockQuote;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
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
    @BindView(R.id.prediction_chart) LineChart predictionChart;
    private StockQuote stockQuote;
    static boolean adviceShown = false;

    private double[] userData;
    private double[] MLData;
    private double[] realData;
    private PredictionData appData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(! bundle.isEmpty()){ stockQuote = (StockQuote) bundle.getSerializable("stockQuote"); }
        adviceShown = false;

        userData = PredictionData.userPreditions;
        MLData = PredictionData.MLPreditions;
        realData = PredictionData.realPrices;


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

        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();
        for (int i = 0; i < userData.length; i++) {
            entries.add(new Entry(i+1, (float) userData[i]));
            entries2.add(new Entry(i+1, (float) MLData[i]));
            entries3.add(new Entry(i+1, (float) realData[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "user prediction");
        dataSet.setAxisDependency((YAxis.AxisDependency.LEFT));
        LineDataSet dataSet2 = new LineDataSet(entries2, "ML prediction");
        dataSet.setAxisDependency((YAxis.AxisDependency.LEFT));
        LineDataSet dataSet3 = new LineDataSet(entries3, "price");
        dataSet.setAxisDependency((YAxis.AxisDependency.LEFT));

        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);

        dataSet2.setColor(Color.BLUE);
        dataSet2.setValueTextColor(Color.BLACK);

        dataSet3.setColor(Color.YELLOW);
        dataSet3.setValueTextColor(Color.BLACK);


        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);

        LineData lineData = new LineData(dataSets);
        predictionChart.setData(lineData);
        predictionChart.invalidate(); // refresh

    }
}
