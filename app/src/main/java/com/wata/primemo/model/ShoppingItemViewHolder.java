package com.wata.primemo.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wata.primemo.R;

public class ShoppingItemViewHolder extends RecyclerView.ViewHolder{
    TextView itemName;
    ImageView itemResourceIcon;
    TextView itemNumber;


    public ShoppingItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemName = itemView.findViewById(R.id.item_name);
        this.itemResourceIcon = itemView.findViewById(R.id.item_icon);
        this.itemNumber = itemView.findViewById(R.id.item_number);
    }
}
