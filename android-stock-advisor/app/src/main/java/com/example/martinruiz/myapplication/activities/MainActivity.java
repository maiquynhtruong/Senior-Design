package com.example.martinruiz.myapplication.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.adapters.StockAdapter;
import com.example.martinruiz.myapplication.interfaces.onSwipeListener;
import com.example.martinruiz.myapplication.models.CompanyStock;
import com.example.martinruiz.myapplication.models.Stock;
import com.example.martinruiz.myapplication.utils.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private List<Stock> stockList;

    @BindView(R.id.fabAddStock) FloatingActionButton fabAddStock;
    @BindView(R.id.recycler_view_stock) RecyclerView rvStock;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_stock);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        stockList = getStocks();
        if (stockList.size() == 0) {
      //      showFabPrompt();
        }

        gcpServices = API.getApi().create(GcpServices.class);

        layoutManager = new LinearLayoutManager(this);

        adapter = new StockAdapter(stockList, R.layout.stock_card, this, new StockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Stock stock, int position, View view) {
                Intent intent = new Intent(MainActivity.this,StockDetails.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        MainActivity.this,clickView,
                        "stockCardTransition");

                intent.putExtra("company",  companyStock);
                startActivity(intent,options.toBundle());
            }
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
                if (dy >0) {
                    // Scroll Down
                    if (fabAddStock.isShown()) {
                        fabAddStock.hide();
                    }
                }
                else if (dy <0) {
                    // Scroll Up
                    if (!fabAddStock.isShown()) {
                        fabAddStock.show();
                    }
                }
            }
        });

        fabAddStock.setOnClickListener(view -> {
            showAddStockAlert("Add Company","Type the company stock you want to add");
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((onSwipeListener) adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStock);

    }

    protected void showAddStockAlert(String title, String message) {

    }

    protected void addStock(String stockName) {
        Call<CompanyStock> stocks = gcpServices.getWeatherCity(stockName, API.KEY, "metric",6);
        stocks.enqueue(new Callback<CompanyStock>() {
            @Override
            public void onResponse(Call<CompanyStock> call, Response<CompanyStock> response) {
                if(response.code()==200){
                    CompanyStock companyStock = response.body();
                    stocks.add(companyStock);
                    adapter.notifyItemInserted(stocks.size()-1);
                    recyclerView.scrollToPosition(stocks.size()-1);

                }else{
                    Toast.makeText(MainActivity.this,"Sorry, city not found",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CompanyStock> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Sorry, weather services are currently unavailable",Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void updateStock(String stockName, int index) {
        Call<CompanyStock> stocks = gcpServices.geStocks(stockName, API.KEY, "metric",6);
        stocks.enqueue(new Callback<CompanyStock>() {
            @Override
            public void onResponse(Call<CompanyStock> call, Response<CompanyStock> response) {
                if(response.code()==200){
                    CompanyStock companyStock = response.body();
                    stockList.remove(index);
                    stockList.add(index,companyStock);
                    adapter.notifyItemChanged(index);
                }
            }

            @Override
            public void onFailure(Call<CompanyStock> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Sorry, can't refresh right now.",Toast.LENGTH_LONG).show();
            }
        });
    }

    List<Stock> getStocks() {
        return new ArrayList<Stock>();
    }
}
