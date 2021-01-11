package com.wata.primemo.model;

import java.util.ArrayList;

/**
 * 買い物履歴テーブルへレコードを挿入/更新した際のコールバック
 */
public interface SetShoppingHistoryCB {

    /**
     * DB処理成功時
     */
    void onFinish();

    /**
     * DB処理失敗時
     */
    void onException();
}
