package com.wata.primemo.model;

import java.util.ArrayList;

public interface DisplayPastDateDBListener extends BaseListener{

    /**
     * 買い物リストを表示
     */
    void displayPastDateList(ArrayList<String> list);

    /**
     * まだ本日の買い物リストを設定していない
     */
    void displayNotYetShopping();
}
