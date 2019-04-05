package com.example.martinruiz.myapplication.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinruiz.myapplication.API.API;
import com.example.martinruiz.myapplication.API.APIServices.StockServices;
import com.example.martinruiz.myapplication.API.GCloudAPI;
import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.adapters.StockAdapter;
import com.example.martinruiz.myapplication.interfaces.onSwipeListener;
import com.example.martinruiz.myapplication.models.Company;
import com.example.martinruiz.myapplication.models.CompanyMatches;
import com.example.martinruiz.myapplication.models.Stock;
import com.example.martinruiz.myapplication.models.StockApiResponse;
import com.example.martinruiz.myapplication.models.StockQuote;
import com.example.martinruiz.myapplication.utils.ItemTouchHelperCallback;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivityStock extends AppCompatActivity {

    private List<StockQuote> stockQuoteList;
    @BindView(R.id.fabAddStock) FloatingActionButton fabAddStock;
    @BindView(R.id.recycler_view_stock) RecyclerView rvStock;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.swipe_to_refresh) SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTapTargetPrompt mFabPrompt;

    private StockServices stockServices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_stock);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        stockQuoteList = getStocks();
        if (stockQuoteList.size() == 0) { showFabPrompt(); }

        stockServices = API.getApi().create(StockServices.class);

        layoutManager = new LinearLayoutManager(this);
        adapter = new StockAdapter(stockQuoteList, R.layout.stock_card, this, (stockQuote, position, clickView) -> {
            Intent intent = new Intent(MainActivityStock.this, StockDetails.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivityStock.this, clickView,
                    "StockCardTransition");

            intent.putExtra("stockQuote",  stockQuote);
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

        fabAddStock.setOnClickListener(view -> showAddStockAlert(getString(R.string.add_stock_title), getString(R.string.add_stock_prompt)));

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshData());

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((onSwipeListener) adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStock);
    }

    public void showFabPrompt() {
        if (mFabPrompt != null) { return; }

        mFabPrompt = new MaterialTapTargetPrompt.Builder(MainActivityStock.this)
                .setTarget(findViewById(R.id.fabAddStock))
                .setFocalPadding(R.dimen.std_padding)
                .setPrimaryText(R.string.add_first_stock)
                .setSecondaryText(R.string.stock_updates)
                .setBackButtonDismissEnabled(true)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setPromptStateChangeListener((prompt, state) -> {
                    //Do something such as storing a value so that this prompt is never shown again
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSING ) { mFabPrompt = null; }
                }).create();
        mFabPrompt.show();
    }

    private void refreshData() {
        for (int i = 0; i < stockQuoteList.size(); i++) {
            updateStock(stockQuoteList.get(0).getStock().getSymbol(), i);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private String stockToAdd ="";
    protected void showAddStockAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_stock,null);
        builder.setView(view);
        final TextView editTextAddStockName = view.findViewById(R.id.editText_add_stock_name);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        builder.setPositiveButton("Add", (dialog, which) -> {
            stockToAdd = editTextAddStockName.getText().toString();
            addStock(stockToAdd);
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
            Toast.makeText(MainActivityStock.this,R.string.cancel,Toast.LENGTH_LONG).show();
        });
        builder.create().show();
    }

    protected void addStock(String stockName) {
        String stockTrend = GCloudAPI.getTrend(stockName);
        Log.i("stockTrend", String.valueOf(stockTrend));

        Call<CompanyMatches> companyMatchesCall = stockServices.getCompanyMatches(API.ALPHA_VANTAGE_SYMBOL_SEARCH, stockName, getString(R.string.alpha_vantage_api_key));
        companyMatchesCall.enqueue(new Callback<CompanyMatches>() {
            @Override
            public void onResponse(Call<CompanyMatches> call, Response<CompanyMatches> response) {
                if (response.code() == 200) {
                    CompanyMatches matches = response.body();
                    Company bestMatchedCompany = matches.getBestMatchedCompany();

                    if (bestMatchedCompany != null)
                        addToAdapter(stockName, bestMatchedCompany.getName());
                    else
                        addToAdapter(stockName, getString(R.string.unknown_company));

                    Log.e("CompanyMatchesResponse:", new Gson().toJson(response.body()) );
                } else {
                    Toast.makeText(MainActivityStock.this, R.string.company_name_not_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CompanyMatches> call, Throwable t) {
                Log.e("CompanyMatchesFailure:", t.getMessage());
                Toast.makeText(MainActivityStock.this, R.string.company_name_unavailable, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void updateStock(String stockName, int index) {
        Call<CompanyMatches> companyMatchesCall = stockServices.getCompanyMatches(API.ALPHA_VANTAGE_SYMBOL_SEARCH, stockName, getString(R.string.alpha_vantage_api_key));

        final Company[] company = {null};
        companyMatchesCall.enqueue(new Callback<CompanyMatches>() {
            @Override
            public void onResponse(Call<CompanyMatches> call, Response<CompanyMatches> response) {
                if (response.code() == 200) {
                    CompanyMatches matches = response.body();
                    Company bestMatchedCompany = matches.getBestMatchedCompany();

                    if (bestMatchedCompany != null) updateAdapter(stockName, index, bestMatchedCompany.getName());
                    else updateAdapter(stockName, index, getString(R.string.unknown_company));

                    Log.e("CompanyMatchesResponse", new Gson().toJson(response.body()) );
                } else {
                    Toast.makeText(MainActivityStock.this, R.string.company_name_not_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CompanyMatches> call, Throwable t) {
                Log.e("CompanyMatchesFailure", t.getMessage());
                Toast.makeText(MainActivityStock.this, R.string.company_name_unavailable, Toast.LENGTH_LONG).show();
            }
        });
    }

    List<StockQuote> getStocks() {
        return new ArrayList<>();
    }

    void addToAdapter(String stockName, String companyName) {
        Call<StockQuote> stockRetrofit = stockServices.getStockQuote(API.ALPHA_VANTAGE_QUOTE, stockName, getString(R.string.alpha_vantage_api_key));
        stockRetrofit.enqueue(new Callback<StockQuote>() {
            @Override
            public void onResponse(Call<StockQuote> call, Response<StockQuote> response) {
                if (response.code() == 200) {
                    StockQuote stockQuote = response.body();
                    Log.e("AddStockResponse", new Gson().toJson(response.body()) );

                    if (stockQuote.getStock() == null) {
                        stockQuote.setStock(new Stock("UNKNOWN", "0"));
                        stockQuote.getStock().setName(getString(R.string.unknown_company));
                    } else {
                        stockQuote.getStock().setSymbol(stockName);
                        stockQuote.getStock().setName(companyName);
                    }

                    getHistoricalData(stockQuote.getStock());
                    stockQuoteList.add(stockQuote);
                    adapter.notifyItemInserted(stockQuoteList.size() - 1);
                    rvStock.scrollToPosition(stockQuoteList.size() - 1);

                } else {
                    Toast.makeText(MainActivityStock.this, R.string.stock_not_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StockQuote> call, Throwable t) {
                Log.e("AddStockFailure", t.getMessage());
                Toast.makeText(MainActivityStock.this, R.string.stock_service_unavailable, Toast.LENGTH_LONG).show();
            }
        });
    }

    void updateAdapter(String stockName, int index, String companyName) {
        Call<StockQuote> stockRetrofit = stockServices.getStockQuote(API.ALPHA_VANTAGE_QUOTE, stockName, getString(R.string.alpha_vantage_api_key));
        stockRetrofit.enqueue(new Callback<StockQuote>() {
            @Override
            public void onResponse(Call<StockQuote> call, Response<StockQuote> response) {
                if (response.code() == 200) {
                    StockQuote stockQuote = response.body();
                    stockQuote.getStock().setName(companyName);
                    stockQuoteList.remove(index);
                    stockQuoteList.add(index, stockQuote);
                    adapter.notifyItemChanged(index);
                    Log.e("updateStockResponse", new Gson().toJson(response.body()) );
                } else {
                    Toast.makeText(MainActivityStock.this, R.string.stock_unable_to_refresh, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StockQuote> call, Throwable t) {
                Log.e("updateStockFailure", t.getMessage());
                Toast.makeText(MainActivityStock.this, R.string.stock_service_unavailable, Toast.LENGTH_LONG).show();
            }
        });
    }

    void getHistoricalData(Stock stock) {
        Call<StockApiResponse> stockRetrofit = stockServices.getStockData(API.ALPHA_VANTAGE_DAILY, stock.getSymbol(), getString(R.string.alpha_vantage_api_key));

        stockRetrofit.enqueue(new Callback<StockApiResponse>() {
            @Override
            public void onResponse(Call<StockApiResponse> call, Response<StockApiResponse> response) {
                if (response.code() == 200) {
                    StockApiResponse stockApiResponse = response.body();
                    Log.e("GetHistoricalData", new Gson().toJson(response.body()));

                    HashMap<String, Float> stockDatePriceMap = new HashMap<>();
                    for (String key : stockApiResponse.getTimeSeries15min().keySet())
                        stockDatePriceMap.put(key, stockApiResponse.getTimeSeries15min().get(key).getClose());
                    stock.setHistoricalData(stockDatePriceMap);

                } else {
                    Toast.makeText(MainActivityStock.this, R.string.stock_not_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StockApiResponse> call, Throwable t) {
                Log.e("AddStockFailure", t.getMessage());
                Toast.makeText(MainActivityStock.this, R.string.stock_service_unavailable, Toast.LENGTH_LONG).show();
            }
        });
    }
}
