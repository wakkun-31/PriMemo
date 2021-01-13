package com.wata.primemo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.wata.primemo.BuildConfig;
import com.wata.primemo.R;
import com.wata.primemo.controller.ShoppingItemController;
import com.wata.primemo.view.viewparts.TopHelpFragment;

public class MainTopActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "MainTopActivity";
//    private final String PACKAGE_NAME = "com.wata.primemo";
    private final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    private ShoppingItemController mController;

    private Button btnStart;
    private Button btnToday;
    private Button btnPast;
    private ImageButton btnHelp;
    private TopHelpFragment mTopHelpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mController = ShoppingItemController.getInstance(getApplicationContext());
        getUIParts();
        setUIListener();
        Log.d(TAG, "onCreate() this is " + this + ".");
        Log.d(TAG, "onCreate() end");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() start");
        super.onStart();
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
        Log.d(TAG, "onStop() end");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() start");
        super.onDestroy();
        Log.d(TAG, "onDestroy() end");
    }

    public void getUIParts(){
        btnStart = findViewById(R.id.btn_top_start);
        btnToday = findViewById(R.id.btn_top_today_item);
        btnPast = findViewById(R.id.btn_top_past_item);
        btnHelp = findViewById(R.id.btn_help);
        mTopHelpFragment = new TopHelpFragment();
    }

    public void setUIListener(){
        btnStart.setOnClickListener(this);
        btnToday.setOnClickListener(this);
        btnPast.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
    }

    // ============================================================================================
    // OnClickListener
    // ============================================================================================
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick(): view is " + v.toString());

        String className = null;

        switch(v.getId()){
            case R.id.btn_top_start:
                className = PACKAGE_NAME + ".view.SelectShoppingItemActivity";
                break;

            case R.id.btn_top_today_item:
                mController.setIsFromTop(true);
                className = PACKAGE_NAME + ".view.TodayItemActivity";
                break;

            case R.id.btn_top_past_item:
                className = PACKAGE_NAME + ".view.PastListActivity";
                break;

            case R.id.btn_help:
                // 簡単な説明フラグメントをインフレートする
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.fl_help_info, mTopHelpFragment);
                ft.commit();

                // ヘルプフラグメントが全面に出ているとき、後面アクティビティ(MainTopActivity)に
                // あるボタンが反応してしまうため、ボタンエリアをなくす
                findViewById(R.id.fl_top_main).setVisibility(View.GONE);

                break;
            default:
                break;
        }

        if (className != null){
            Log.d(TAG, "onClick(): startActivity is success --> " + className);
            Intent intent = new Intent();
            intent.setClassName(PACKAGE_NAME, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            Log.d(TAG, "onClick(): no intent action");
        }
    }
}
