package com.wata.primemo.model;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wata.primemo.R;

public class CategoryItemViewHolder extends RecyclerView.ViewHolder{
    TextView categoryID;
    TextView categoryName;
    TextView itemID;
    TextView itemName;
    ImageView itemResourceIcon;
    TextView itemNumber;


    public CategoryItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.categoryID = itemView.findViewById(R.id.category_id);
        this.categoryName = itemView.findViewById(R.id.category_name);
        this.itemID = itemView.findViewById(R.id.item_id);
        this.itemName = itemView.findViewById(R.id.item_name);
        this.itemResourceIcon = itemView.findViewById(R.id.item_icon);
        this.itemNumber = itemView.findViewById(R.id.item_number);
    }
}
