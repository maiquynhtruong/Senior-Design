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
//        if (stockList.size() == 0)

//        gcpServices =

        layoutManager = new LinearLayoutManager(this);

        adapter = new StockAdapter(stockList, R.layout.stock_card, this, new StockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Stock stock, int position, View view) {
                // passing intent to a class
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
            showAddStockAlert("Add city","Type the city you want to add");
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((onSwipeListener) adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStock);

    }

    protected void showAddStockAlert(String title, String message) {

    }

    protected void addStock(String stockName) {

    }

    protected void updateStock(String stockName) {

    }

    List<Stock> getStocks() {
        return new ArrayList<Stock>();
    }
}
