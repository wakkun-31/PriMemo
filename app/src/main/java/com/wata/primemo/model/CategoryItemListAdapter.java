package com.wata.primemo.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wata.primemo.R;

import java.util.ArrayList;

public class CategoryItemListAdapter extends RecyclerView.Adapter<CategoryItemViewHolder> {

    // 1つのカテゴリ内の品物リスト
    private ArrayList<ShoppingItem> shoppingItemList;

    public CategoryItemListAdapter(ArrayList<ShoppingItem> itemList) {
        this.shoppingItemList = itemList;
    }

    @NonNull
    @Override
    public CategoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CategoryItemViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.line_category_item, parent, false);
        holder = new CategoryItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CategoryItemViewHolder holder, int position) {
        holder.itemResourceIcon.setImageResource(this.shoppingItemList.get(position).getItemIconResource());
        holder.categoryID.setText(this.shoppingItemList.get(position).getCategoryID());
        holder.categoryName.setText(this.shoppingItemList.get(position).getCategoryName());
        holder.itemID.setText(this.shoppingItemList.get(position).getItemID());
        holder.itemName.setText(this.shoppingItemList.get(position).getItemName());
        holder.itemNumber.setText(this.shoppingItemList.get(position).getItemNumber());
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }
}
