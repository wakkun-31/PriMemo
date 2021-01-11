package com.wata.primemo.model;

import java.util.ArrayList;

public interface DisplayTodayDBListener extends BaseListener{

    /**
     * 本日の買い物リストを表示
     */
    void displayTodayShoppingList(ArrayList<ShoppingItem> todayList);

    /**
     * まだ本日の買い物リストを設定していない
     */
    void displayNotYetMemo();
}
