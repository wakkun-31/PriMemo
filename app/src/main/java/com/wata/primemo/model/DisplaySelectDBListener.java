package com.wata.primemo.model;

import java.util.ArrayList;

public interface DisplaySelectDBListener extends BaseListener{

    /**
     * Activity生成時のレコード取得処理後
     */
    void displayTodayShoppingList(ArrayList<ShoppingItem> list);

    /**
     * 保存のみボタン押下後
     */
    void displaySaveOnly();

    /**
     * 本日の買い物リストがまだない時
     */
    void displayNotYetMemo();
}
