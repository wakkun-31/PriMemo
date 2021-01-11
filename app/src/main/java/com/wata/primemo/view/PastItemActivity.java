package com.wata.primemo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wata.primemo.R;
import com.wata.primemo.controller.ShoppingItemController;
import com.wata.primemo.model.DisplayPastDBListener;
import com.wata.primemo.model.ShoppingItem;
import com.wata.primemo.model.ShoppingItemListAdapter;

import java.util.ArrayList;

public class PastItemActivity extends AppCompatActivity
        implements View.OnClickListener,
        DisplayPastDBListener {

    private static final String TAG = "PastItemActivity";
    private final String PACKAGE_NAME = "com.wata.primemo";

    private ShoppingItemController mController;

    private RecyclerView mRvPastItemList;
    private Button mBackButton;
    private Button mTopButton;
    private TextView mTvTitleDate;
    private TextView mTvNotShopping;

    // ============================================================================================
    // ライフサイクル
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate start!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_item);

        mController = ShoppingItemController.getInstance(getApplicationContext());
        getUIParts();
        setUIListener();
        mController.setListener(this);

        // 選択した過去の日付のレコードを取得して、レイアウトへ表示する
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        mTvTitleDate.setText(date);
        mController.getPastShoppingItemList(date);

        Log.d(TAG, "onCreate() this is " + this + ".");
        Log.d(TAG, "onCreate end!");
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
        Log.d(TAG, "onPause() end");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop() start");
        super.onStop();
        mController.removeListener(this);
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
        mBackButton = findViewById(R.id.btn_back);
        mTvTitleDate = findViewById(R.id.tv_past_shopping_date);
        mTvNotShopping = findViewById(R.id.tv_not_yet_past_shopping);
    }

    /**
     * UI部品のリスナーを設定
     */
    private void setUIListener() {
        Log.d(TAG, "setUIListener start.");

        mTopButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
    }

    /**
     * 初期買い物リストビューUIセッティング
     */
    private void setTodayListViewUI() {
        Log.d(TAG, "setTodayListViewUI start.");
        mRvPastItemList = findViewById(R.id.rv_today_item_list);
        LinearLayoutManager manager = new LinearLayoutManager((this));
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvPastItemList.setLayoutManager(manager);
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
            case R.id.btn_back:
                className = PACKAGE_NAME + ".view.PastListActivity";
                break;
            case R.id.btn_top:
                className = PACKAGE_NAME + ".view.MainTopActivity";
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
    public void displayPastShoppingList(ArrayList<ShoppingItem> list) {
        Log.d(TAG, "displayPastShoppingList start.");
        // Todo 要
        mRvPastItemList.setVisibility(View.VISIBLE);
        mTvNotShopping.setVisibility(View.GONE);

        ShoppingItemListAdapter adapter = new ShoppingItemListAdapter(list);
        mRvPastItemList.setAdapter(adapter);
        Log.d(TAG, "displayPastShoppingList end.");
    }

    @Override
    public void displayNotYetPastList() {
        Log.d(TAG, "displayNotYetPastList start.");
        mRvPastItemList.setVisibility(View.GONE);
        mTvNotShopping.setVisibility(View.VISIBLE);
        Log.d(TAG, "displayNotYetPastList end.");
    }
}
