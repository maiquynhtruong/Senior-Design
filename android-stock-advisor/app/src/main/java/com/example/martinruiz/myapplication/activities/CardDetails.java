package com.example.martinruiz.myapplication.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.martinruiz.myapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardDetails extends AppCompatActivity {
    @BindView(R.id.user_prediction) TextView tvUserPrediction;
    @BindView(R.id.app_prediction) TextView tvAppPrediction;
    @BindView(R.id.show_advice_btn) Button btShowAdvice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.show_advice_btn)
    public void showAdvice() {

    }
}
