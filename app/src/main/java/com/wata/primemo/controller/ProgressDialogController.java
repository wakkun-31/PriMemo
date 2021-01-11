package com.wata.primemo.controller;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.wata.primemo.view.SelectShoppingItemActivity;
import com.wata.primemo.view.TodayItemActivity;

public class ProgressDialogController {

    private static final String TAG = "ProgressDialogControl";
    private static ProgressDialogController mInstance;
    private AppCompatActivity mActivity;

    private ProgressDialogController(){}

    public static ProgressDialogController getInstance(){
        if(mInstance == null){
            mInstance = new ProgressDialogController();
        }
        return mInstance;
    }

    /**
     * フォアグラウンドにあるActivityをセット
     */
    public void setActivity(AppCompatActivity activity){
        mActivity = activity;
    }

    /**
     * フォアグラウンドにあるアクティビティでダイアログを表示する
     */
    public void showProgressDialog(){
        Log.d(TAG, "showProgressDialog() start");
        if(mActivity == null){
            return;
        }
        if(mActivity instanceof SelectShoppingItemActivity){
            ((SelectShoppingItemActivity) mActivity).showProgressDialog();
        } else if( mActivity instanceof TodayItemActivity){
            ((TodayItemActivity) mActivity).showProgressDialog();
        }
    }

    /**
     * フォアグラウンドにあるアクティビティでダイアログを閉じる
     */
    public void dismissProgressDialog(){
        Log.d(TAG, "dismissProgressDialog() start");
        if(mActivity == null){
            return;
        }
        if(mActivity instanceof SelectShoppingItemActivity){
            ((SelectShoppingItemActivity) mActivity).dismissProgressDialog();
        } else if( mActivity instanceof TodayItemActivity){
            ((TodayItemActivity) mActivity).dismissProgressDialog();
        }
    }
}
