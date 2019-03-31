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
import com.example.martinruiz.myapplication.models.StockQuote;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> implements onSwipeListener {
    private List<StockQuote> stockQuoteList;
    private int layoutReference;
    private OnItemClickListener listener;
    private Activity activity;
    private View parentView;

    public StockAdapter(List<StockQuote> stockInfoList, int layoutReference, Activity activity, OnItemClickListener listener) {
        this.stockQuoteList = stockInfoList;
        this.layoutReference = layoutReference;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;
        View view = LayoutInflater.from(activity).inflate(layoutReference, parent, false);
        StockAdapter.ViewHolder viewHolder = new StockAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StockAdapter.ViewHolder holder, int position) {
        holder.bind(stockQuoteList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return stockQuoteList.size();
    }

    public void onItemDelete(int poisition) {
        StockQuote stockQuote = stockQuoteList.get(poisition);
        stockQuoteList.remove(poisition);
        notifyItemRemoved(poisition);

        Snackbar.make(parentView, "Removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> addItem(stockQuote, poisition)).show();
    };

    public void addItem(StockQuote stockQuote, int position) {
        stockQuoteList.add(position, stockQuote);
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

        public void bind(StockQuote stockQuote, final OnItemClickListener listener) {
            stockName.setText(stockQuote.getStock().getName());
            stockPrice.setText(stockQuote.getStock().getPrice());
            tickerSymbol.setText(stockQuote.getStock().getSymbol());

            cardViewStockCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(stockQuote,getAdapterPosition(), cardViewStockCard);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(StockQuote stockQuote, int position, View view);
    }
}
