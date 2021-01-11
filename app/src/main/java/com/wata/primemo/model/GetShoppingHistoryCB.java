package com.wata.primemo.model;

import java.util.ArrayList;

/**
 * 買い物履歴テーブルからレコードをShoppingItemリストで取得する際のコールバック
 */
public interface GetShoppingHistoryCB {

    /**
     * DB処理成功時
     */
    void onFinish(ArrayList<ShoppingItem> fullList);

    /**
     * DB処理成功 & DBデータに不足(Null)あり
     */
    void onLossData(ArrayList<ShoppingItem> partList);

    /**
     * DB処理失敗時
     */
    void onException();
}
