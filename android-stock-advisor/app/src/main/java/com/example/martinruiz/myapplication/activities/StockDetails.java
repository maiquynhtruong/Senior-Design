package com.example.martinruiz.myapplication.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.StockInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

//work on the view file first
public class StockDetails extends AppCompatActivity {
    @BindView(R.id.ticker_symbol) TextView tvTickerSymbol;
    @BindView(R.id.stock_name) TextView tvStockName;
    @BindView(R.id.stock_price) TextView tvStockPrice;
    private StockInfo stockInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(! bundle.isEmpty()){ stockInfo = (StockInfo) bundle.getSerializable("stockInfo"); }

        setCardData();
    }

    private void setCardData() {
        tvTickerSymbol.setText(stockInfo.getStockMetaData().getSymbol());
        tvStockName.setText(stockInfo.getStockMetaData().getSymbol());
        tvStockPrice.setText("$" + stockInfo.getStockTimeSeries().getClose());

    }
}
