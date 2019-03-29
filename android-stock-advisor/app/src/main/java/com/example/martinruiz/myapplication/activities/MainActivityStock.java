package com.example.martinruiz.myapplication.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.example.martinruiz.myapplication.API.API;
import com.example.martinruiz.myapplication.API.APIServices.StockServices;
import com.example.martinruiz.myapplication.API.GCloudAPI;
import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.adapters.StockAdapter;
import com.example.martinruiz.myapplication.interfaces.onSwipeListener;
import com.example.martinruiz.myapplication.models.Stock;
import com.example.martinruiz.myapplication.utils.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityStock extends AppCompatActivity {

    private List<Stock> stockList;
    private String stockToAdd ="aapl";

    @BindView(R.id.recycler_view_stock) RecyclerView recyclerView;
    @BindView(R.id.fabAddStock) FloatingActionButton fabAddStock;
    @BindView(R.id.recycler_view_stock) RecyclerView rvStock;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.swipe_to_refresh) SwipeRefreshLayout swipeRefreshLayout;

    private StockServices stockServices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_stock);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        stockList = getStocks();
        if (stockList.size() == 0) { showFabPrompt(); }

        String GCPServices = GCloudAPI.getTrend(stockToAdd);

        layoutManager = new LinearLayoutManager(this);

        adapter = new StockAdapter(stockList, R.layout.stock_card, this, (stock, position, clickView) -> {
            Intent intent = new Intent(MainActivityStock.this, StockDetails.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivityStock.this, clickView,
                    "weatherCardTransition");

            intent.putExtra("stock",  stock);
            startActivity(intent,options.toBundle());
        });

        rvStock.setHasFixedSize(true);
        rvStock.setItemAnimator(new DefaultItemAnimator());
        rvStock.setLayoutManager(layoutManager);
        rvStock.setAdapter(adapter);

        rvStock.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { // Scroll Down
                    if (fabAddStock.isShown()) { fabAddStock.hide(); }
                } else if (dy < 0) { // Scroll Up
                    if (!fabAddStock.isShown()) { fabAddStock.show(); }
                }
            }
        });

        fabAddStock.setOnClickListener(view -> showAddStockAlert("Add stock", getString(R.string.add_stock_prompt)));

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((onSwipeListener) adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStock);

    }

    public void showFabPrompt() {

    }

    private void refreshData() {

    }

    protected void showAddStockAlert(String title, String message) {

    }

    protected void addStock(String stockName) {
        // Substitute alpha_vantage_api_key with your key
        Call<Stock> stockRetrofit = stockServices.getStockPrice(API.ALPHA_VANTAGE_FUNCTION, stockName, "5min", getString(R.string.alpha_vantage_api_key));
        String stockTrend = GCloudAPI.getTrend(stockName);
        stockRetrofit.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.code() == 200) {
                    Stock stock = response.body();
                    stockList.add(stock);
                    adapter.notifyItemInserted(stockList.size() - 1);
                    recyclerView.scrollToPosition(stockList.size() - 1);
                } else {
                    Toast.makeText(MainActivityStock.this, R.string.stock_not_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                Toast.makeText(MainActivityStock.this, R.string.stock_service_unavailable, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void updateStock(String stockName) {

    }

    List<Stock> getStocks() {
        return new ArrayList<Stock>();
    }
}
