package com.example.martinruiz.myapplication.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.StockTimeSeries;

import butterknife.BindView;
import butterknife.ButterKnife;

//work on the view file first
public class StockDetails extends AppCompatActivity {
    @BindView(R.id.ticker_symbol) TextView tvTickerSymbol;
    @BindView(R.id.stock_name) TextView tvStockName;
    @BindView(R.id.stock_price) TextView tvStockPrice;
    private StockTimeSeries stockTimeSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(! bundle.isEmpty()){ stockTimeSeries = (StockTimeSeries) bundle.getSerializable("stockTimeSeries"); }

        setCardData();
    }

    private void setCardData() {
        tvTickerSymbol.setText(stockTimeSeries.getTickerSymbol());
        tvStockName.setText(stockTimeSeries.getName());
        tvStockPrice.setText("$" + stockTimeSeries.getPrice());

    }
}
