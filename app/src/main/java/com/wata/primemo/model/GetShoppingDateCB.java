package com.wata.primemo.model;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 買い物履歴テーブルからレコードをShoppingItemリストで取得する際のコールバック
 */
public interface GetShoppingDateCB {

    /**
     * DB処理成功時
     */
    void onFinish(HashSet<String> set);

    /**
     * 買い物リストを一度も保存していない状態で、本DB処理をしてしまった場合
     */
    void onNoDate();

    /**
     * DB処理失敗時
     */
    void onException();
}
