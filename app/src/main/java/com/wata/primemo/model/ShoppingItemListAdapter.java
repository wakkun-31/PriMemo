package com.wata.primemo.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wata.primemo.R;

import java.util.ArrayList;

public class ShoppingItemListAdapter extends RecyclerView.Adapter<ShoppingItemViewHolder> {

    // 1つのカテゴリ内の品物リスト
    private ArrayList<ShoppingItem> shoppingItemList;

    public ShoppingItemListAdapter(ArrayList<ShoppingItem> itemList) {
        this.shoppingItemList = itemList;
    }

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ShoppingItemViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.line_shopping_item, parent, false);
        holder = new ShoppingItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShoppingItemViewHolder holder, int position) {
        holder.itemResourceIcon.setImageResource(this.shoppingItemList.get(position).getItemIconResource());
        holder.itemName.setText(this.shoppingItemList.get(position).getItemName());
        holder.itemNumber.setText(this.shoppingItemList.get(position).getItemNumber());
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }
}
