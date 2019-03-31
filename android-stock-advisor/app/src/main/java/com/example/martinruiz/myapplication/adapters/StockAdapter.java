package com.example.martinruiz.myapplication.adapters;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.interfaces.onSwipeListener;
import com.example.martinruiz.myapplication.models.StockInfo;
import com.example.martinruiz.myapplication.models.StockTimeSeries;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> implements onSwipeListener {
    List<StockInfo> stockInfoList;
    private int layoutReference;
    private OnItemClickListener listener;
    private Activity activity;
    private View parentView;

    public StockAdapter(List<StockInfo> stockInfoList, int layoutReference, Activity activity, OnItemClickListener listener) {
        this.stockInfoList = stockInfoList;
        this.layoutReference = layoutReference;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;
        View view = LayoutInflater.from(activity).inflate(layoutReference, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(StockAdapter.ViewHolder holder, int position) {
        holder.bind(stockInfoList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void onItemDelete(int poisition) {
        StockInfo stockInfo = stockInfoList.get(poisition);
        stockInfoList.remove(poisition);
        notifyItemRemoved(poisition);

        Snackbar.make(parentView, "Removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                addItem(stockInfo, poisition);
                }).show();
    };

    public void addItem(StockInfo stockInfo, int position) {
        stockInfoList.add(position, stockInfo);
        notifyItemInserted(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ticker_symbol) TextView tickerSymbol;
        @BindView(R.id.stock_name) TextView stockName;
        @BindView(R.id.stock_price) TextView stockPrice;
        @BindView(R.id.cardViewStockCard) CardView cardViewStockCard;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(StockInfo stockInfo, final OnItemClickListener listener) {
            stockName.setText(stockInfo.getStockMetaData().getSymbol());
            stockPrice.setText(stockInfo.getStockTimeSeries().get(0).getClose() + "");
            tickerSymbol.setText(stockInfo.getStockMetaData().getSymbol());

            cardViewStockCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(stockInfo,getAdapterPosition(), cardViewStockCard);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(StockInfo stockInfo, int position, View view);
    }
}
