package com.wata.primemo.model;

public interface GetIdNameCB{
    /**
     * DB処理成功時
     */
    void onFinish(String name);

    /**
     * DB処理失敗時
     */
    void onException();
}
