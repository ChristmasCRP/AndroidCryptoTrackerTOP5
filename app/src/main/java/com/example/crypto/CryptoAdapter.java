package com.example.crypto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {
    private final List<CryptoItem> cryptoList;
    private final Context context;
    public CryptoAdapter(List<CryptoItem> cryptoList, Context context) {
        this.cryptoList = cryptoList;
        this.context = context;
    }
    @NonNull



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crypto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CryptoItem item = cryptoList.get(position);
        holder.nameView.setText(item.name + " - $" + item.price);
        holder.capView.setText("Kapitalizacja: $" + item.marketCap);

        Glide.with(holder.itemView.getContext())
                .load(item.imageUrl)
                .into(holder.iconView);

        holder.itemView.setOnClickListener(v -> {
            String msg = item.name + "\nCena: $" + item.price + "\nKapitalizacja: $" + item.marketCap;
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, capView;
        ImageView iconView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.coinName);
            capView = itemView.findViewById(R.id.coinCap);
            iconView = itemView.findViewById(R.id.coinIcon);
        }
    }
}
