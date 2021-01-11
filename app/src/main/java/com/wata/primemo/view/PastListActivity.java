package com.wata.primemo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wata.primemo.R;
import com.wata.primemo.controller.ShoppingItemController;
import com.wata.primemo.model.DateItem;
import com.wata.primemo.model.DisplayPastDateDBListener;
import com.wata.primemo.model.ShoppingHistoryListAdapter;
import com.wata.primemo.model.ShoppingItem;

import java.util.ArrayList;

public class PastListActivity extends AppCompatActivity
        implements ListView.OnItemClickListener,
        View.OnClickListener,
        DisplayPastDateDBListener {

    private static final String TAG = "PastListActivity";
    private final String PACKAGE_NAME = "com.wata.primemo";

    private ShoppingItemController mController;

    private ListView mLvPastHistoryList;
    private Button mBackButton;
    private TextView mTvNotShopping;

    // Todo (今後)アイコンや場所項目追加で変更要
//    private final static int LO_POS_DATE_ICON = 0;
    private final static int LO_POS_DATE_TEXT = 0;
//    private final static int LO_POS_LOCATION = 2;

    // ============================================================================================
    // ライフサイクル
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate start!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_list);

        mController = ShoppingItemController.getInstance(getApplicationContext());
        getUIParts();
        setUIListener();

        // 過去の買い物履歴がある場合は、取得して選択レイアウトへ表示する
        mController.getShoppingDateList();

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
        mBackButton = findViewById(R.id.btn_back);
        mTvNotShopping = findViewById(R.id.tv_not_yet_shopping);
    }

    /**
     * UI部品のリスナーを設定
     */
    private void setUIListener() {
        Log.d(TAG, "setUIListener start.");
        mBackButton.setOnClickListener(this);
    }

    /**
     * 初期買い物リストビューUIセッティング
     */
    private void setTodayListViewUI() {
        Log.d(TAG, "setTodayListViewUI start.");
        mLvPastHistoryList = findViewById(R.id.lv_past_history_list);
        LinearLayoutManager manager = new LinearLayoutManager((this));
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mLvPastHistoryList.setOnItemClickListener(this);
    }

    // ============================================================================================
    // ClickListener
    // ============================================================================================
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvDate = (TextView) ((LinearLayout) view).getChildAt(LO_POS_DATE_TEXT);
        String strDate = (String) tvDate.getText();
        Log.d(TAG, "onItemClick Button: " + strDate);

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, PACKAGE_NAME + ".view.PastItemActivity");
        intent.putExtra("date", strDate);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String buttonName = ((Button) v).getText().toString();
        Log.d(TAG, "onClick Button: " + buttonName);

        String className = null;
        switch (v.getId()) {
            case R.id.btn_back:
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
    public void displayPastDateList(ArrayList<String> list) {
        Log.d(TAG, "displayPastDateList() start.");
        mLvPastHistoryList.setVisibility(View.VISIBLE);
        mTvNotShopping.setVisibility(View.GONE);

        // Todo （今後）DBへDateItemで保存/取得する
        ArrayList<DateItem> dateList = new ArrayList<>();
        int count = 0;
        for(String date : list){
            DateItem item = new DateItem();
            item.setDate(date);
            item.setDateId(count++);

            dateList.add(item);
        }
        Log.d(TAG, "displayPastDateList() past shopping count = " + count);
        ShoppingHistoryListAdapter adapter = new ShoppingHistoryListAdapter(this, dateList, R.layout.line_past_date);
        mLvPastHistoryList.setAdapter(adapter);
        Log.d(TAG, "displayPastDateList() end.");
    }

    @Override
    public void displayNotYetShopping() {
        Log.d(TAG, "displayNotYetShopping start.");
        mLvPastHistoryList.setVisibility(View.GONE);
        mTvNotShopping.setVisibility(View.VISIBLE);
        Log.d(TAG, "displayNotYetShopping end.");
    }
}
