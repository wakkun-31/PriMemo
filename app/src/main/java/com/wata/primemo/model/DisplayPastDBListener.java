package com.wata.primemo.model;

import java.util.ArrayList;

public interface DisplayPastDBListener extends BaseListener{

    /**
     * 過去の買い物リストを表示
     */
    void displayPastShoppingList(ArrayList<ShoppingItem> list);

    /**
     * まだ過去の買い物リストを表示できない
     */
    void displayNotYetPastList();
}
