package com.example.martinruiz.myapplication.adapters;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.Stock;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    List<Stock> stockList;
    private int layoutReference;
    private OnItemClickListener listener;
    private Activity activity;
    private View parentView;

    public StockAdapter(List<Stock> stockList, int layoutReference, Activity activity, OnItemClickListener listener) {
        this.stockList = stockList;
        this.layoutReference = layoutReference;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void onItemDelete(int poisition) {
        Stock stock = stockList.get(poisition);
        stockList.remove(poisition);
        notifyItemRemoved(poisition);

        Snackbar.make(parentView, "Removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                addItem(stock, poisition);
                }).show();
    };

    public void addItem(Stock stock, int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ticker_symbol) TextView tickerSymbol;
        @BindView(R.id.stock_name) TextView stockName;
        @BindView(R.id.stock_price) TextView stockPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Stock stock) {

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Stock stock, int position, View view);
    }
}
