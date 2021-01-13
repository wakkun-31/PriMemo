package com.wata.primemo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wata.primemo.R;
import com.wata.primemo.controller.ShoppingItemController;
import com.wata.primemo.model.DisplayTodayDBListener;
import com.wata.primemo.model.ShoppingItem;
import com.wata.primemo.model.ShoppingItemListAdapter;

import java.util.ArrayList;

public class TodayItemActivity extends AppCompatActivity
        implements View.OnClickListener,
        DisplayTodayDBListener {

    private static final String TAG = "TodayItemActivity";
    private final String PACKAGE_NAME = "com.wata.primemo";

    private ShoppingItemController mController;

    private RecyclerView mRvTodayItemList;
    private Button mEditButton;
    private Button mTopButton;
    private TextView mTvNotMemo;

    ProgressDialog mProgressDialog = null;

    // ============================================================================================
    // ライフサイクル
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate start!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_item);

        mController = ShoppingItemController.getInstance(getApplicationContext());

        showProgressDialog();

        getUIParts();
        setUIListener();
        mController.setListener(this);

        // DBに今日のレコードがある場合は、取得して選択レイアウトへ表示する
        mController.getTodayShoppingItemList();

        Log.d(TAG, "onCreate() this is " + this + ".");
        Log.d(TAG, "onCreate end!");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent() start");
        super.onNewIntent(intent);
        showProgressDialog();
        // Todo　対応要
        // アクティビティが破棄されずに、起動された場合は、本日の買い物リストに
        // 追加された可能性があるため、一度レイアウトを消去してからDBより本日分を取得
//        mController.getTodayShoppingItemList();

        Log.d(TAG, "onNewIntent() end");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() start");
        super.onStart();
        mController.setListener(this);
        Log.d(TAG, "onStart() end");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() start");
        super.onResume();
        Log.d(TAG, "onResume() end");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause() start");
        super.onPause();
        dismissProgressDialog();
        mController.removeListener(this);
        Log.d(TAG, "onPause() end");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop() start");
        super.onStop();
        Log.d(TAG, "onStop() end");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy start!");
        super.onDestroy();
        mController.removeListener(this);
        Log.d(TAG, "onDestroy end!");
    }

    // ============================================================================================
    // 初期設定
    // ============================================================================================
    /**
     * UI部品の取得
     */
    private void getUIParts() {
        Log.d(TAG, "getUIParts start.");

        setTodayListViewUI();
        mTopButton = findViewById(R.id.btn_top);
        mEditButton = findViewById(R.id.btn_edit_today);
        mTvNotMemo = findViewById(R.id.tv_not_yet_memo);
    }

    /**
     * UI部品のリスナーを設定
     */
    private void setUIListener() {
        Log.d(TAG, "setUIListener start.");

        mTopButton.setOnClickListener(this);
        mEditButton.setOnClickListener(this);

    }

    /**
     * 初期買い物リストビューUIセッティング
     */
    private void setTodayListViewUI() {
        Log.d(TAG, "setTodayListViewUI start.");
        mRvTodayItemList = findViewById(R.id.rv_today_item_list);
        LinearLayoutManager manager = new LinearLayoutManager((this));
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTodayItemList.setLayoutManager(manager);
    }

    // ============================================================================================
    // OnClickListener
    // ============================================================================================
    @Override
    public void onClick(View v) {
        String buttonName = ((Button) v).getText().toString();
        Log.d(TAG, "onClick Button: " + buttonName);

        String className = null;
        switch (v.getId()) {
            case R.id.btn_top:
                className = PACKAGE_NAME + ".view.MainTopActivity";
                break;
            case R.id.btn_edit_today:
                className = PACKAGE_NAME + ".view.SelectShoppingItemActivity";
                break;
            default:
                break;
        }

        if (className != null){
            Log.d(TAG, "onClick(): startActivity is success --> " + className);
            Intent intent = new Intent();
            intent.setClassName(PACKAGE_NAME, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Log.d(TAG, "onClick(): no intent action");
        }
    }

    // ============================================================================================
    // 画面表示
    // ============================================================================================
    @Override
    public void displayTodayShoppingList(ArrayList<ShoppingItem> todayList) {
        Log.d(TAG, "displayTodayShoppingList start.");

        dismissProgressDialog();

        mRvTodayItemList.setVisibility(View.VISIBLE);
        mTvNotMemo.setVisibility(View.GONE);

        ShoppingItemListAdapter adapter = new ShoppingItemListAdapter(todayList);
        mRvTodayItemList.setAdapter(adapter);
        Log.d(TAG, "displayTodayShoppingList end.");
    }

    @Override
    public void displayNotYetMemo(){
        Log.d(TAG, "displayNotYetMemo start.");

        dismissProgressDialog();

        mRvTodayItemList.setVisibility(View.GONE);
        mTvNotMemo.setVisibility(View.VISIBLE);
        Log.d(TAG, "displayNotYetMemo end.");
    }

    // ============================================================================================
    // その他
    // ============================================================================================

    /**
     * レイアウトに配置されているプログレスダイアログを取得/表示させる
     */
    public void showProgressDialog(){
        Log.d(TAG, "showProgressDialog() start");

        if(mProgressDialog != null){
            return;
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.title_progress_dialog);
        mProgressDialog.setMessage(getResources().getString(R.string.message_progress_dialog));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        // ユーザ操作を受け付けにようにするためProgressDialog採用
//        ProgressBar bar = findViewById(R.id.pb_get_today_item);
//        bar.setVisibility(View.VISIBLE);
    }

    /**
     * レイアウトに配置されているプログレスダイアログを取得/非表示させる
     */
    public void dismissProgressDialog(){
        Log.d(TAG, "dismissProgressDialog() start");

        if(mProgressDialog == null){
            return;
        }

        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

//        ProgressBar bar = findViewById(R.id.pb_get_today_item);
//        bar.setVisibility(View.GONE);
    }

}
