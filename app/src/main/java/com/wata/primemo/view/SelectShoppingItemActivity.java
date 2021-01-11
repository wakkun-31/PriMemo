package com.wata.primemo.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wata.primemo.R;
import com.wata.primemo.controller.ShoppingItemController;
import com.wata.primemo.model.DisplaySelectDBListener;
import com.wata.primemo.model.ShoppingItem;
import com.wata.primemo.model.CategoryItemListAdapter;

import java.util.ArrayList;

public class SelectShoppingItemActivity extends AppCompatActivity
        implements View.OnClickListener,
        View.OnTouchListener,
        View.OnDragListener,
        RecyclerView.OnItemTouchListener,
        TextWatcher,
        View.OnFocusChangeListener,
        DisplaySelectDBListener {

    private static final String TAG = "ShoppingItemActivity";
    private final String PACKAGE_NAME = "com.wata.primemo";

    private ShoppingItemController mShoppingItemController;
    private InputMethodManager mInputMethodManager;

    private RecyclerView mCategoryItemListView;
    private ScrollView mScrollSelectItemLayout;
    private LinearLayout mLinearSelectItemLayout;
    private LayoutInflater mSelectItemInflater;
    // 定義されている全ての品物リスト
    private ArrayList<ShoppingItem> mAllItemList;
    // 選択されたカテゴリ内の品物リスト
    private ArrayList<ShoppingItem> mCategoryItemList = new ArrayList<>();
    // カテゴリ内でドラッグされたアイテムの位置
    private int mDragCategoryItemPosition = 0;
    // 選択されたショッピングアイテムを管理するマップ
    private ArrayList<ShoppingItem> mShoppingItemList = new ArrayList<>();
    // ショッピングアイテムのリストサイズ
    private int mSelectItemNewSize = 0;
    private int mSelectItemOldSize = 0;
    // 削除されるショッピングアイテムの位置
    private int mSelectItemRemovePosition = 0;

    private boolean initialJsonLoadSuccess = true;
    private int otherIconId = 0;
    private final String OTHER_CATEGORY_ID = "099";
    private final String OTHER_CATEGORY_NAME = "その他";
    private final String OTHER_ITEM_ID = "000";
    // 商品1行分のレイアウト位置
    private final int LO_POS_ICON_ID = 0;
    private final int LO_POS_CATEGORY_ID = 1;
    private final int LO_POS_CATEGORY_NAME = 2;
    private final int LO_POS_ITEM_ID = 3;
    private final int LO_POS_ITEM_NAME = 4;
    private final int LO_POS_MULTI_VIEW = 5;
    private final int LO_POS_ITEM_NUM = 6;
    // 商品個数を選択するためにタップしたか否か
//    private boolean isTapItemNum = false;


    // ドラッグする対象を判断するための
    private enum DragTarget {
        NONE,
        CATEGORY_LIST_ITEM,
        SELECT_LIST_ITEM;
    }
    private DragTarget mDragTarget = DragTarget.NONE;

    private static final long LONG_PRESS_TIME = 250;
    private long TAP_DELAY = 1000;
    private long mOldCurrentTime;

    private Button mVegetablesBtn;
    private Button mMeatBtn;
    private Button mFishBtn;
    private Button mSpiceBtn;
    private Button mBreadBtn;
    private Button mProcessedBtn;
    private Button mSnackBtn;
    private Button mFrozenBtn;
    private Button mAlcoholBtn;
    private Button mDailyNecessitiesBtn;
    private Button mOtherBtn;

    private View dummyFocusView;

    private Button mGarbageBtn;
    private Button mGoTodayListBtn;
    private Button mSaveOnlyBtn;

    // ============================================================================================
    // ライフサイクル
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate start!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_item);

        mShoppingItemController = ShoppingItemController.getInstance(getApplicationContext());

        showProgressDialog();

        // 定義されている商品リストを読み込んで、リストで取得しアクティビティで展開用意
        getAllItemList();
        setInitialCategoryUI();
        setInitialManager();
        getUIParts();
        setUIListener();
        mShoppingItemController.setListener(this);

        // DBに今日のレコードがある場合は、取得して選択レイアウトへ表示する
        mShoppingItemController.getTodayShoppingItemList();

        mOldCurrentTime = 0;
        Log.d(TAG, "onCreate() this is " + this + ".");
        Log.d(TAG, "onCreate end!");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent() start");
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent() end");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() start");
        super.onStart();
        mShoppingItemController.setListener(this);
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
        mShoppingItemController.removeListener(this);
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
        mShoppingItemController.removeListener(this);
        Log.d(TAG, "onDestroy end!");
    }

    // ============================================================================================
    // 初期設定
    // ============================================================================================
    /**
     * JSONファイルをから定義されてる品物をリスト形式で取得する
     */
    private void getAllItemList() {
        initialJsonLoadSuccess = mShoppingItemController.initialJsonLoad(getApplicationContext());

        Log.d(TAG, "initialJsonLoadSuccess = " + initialJsonLoadSuccess);
        if (initialJsonLoadSuccess) {
            mAllItemList = mShoppingItemController.getAllItemList();
        } else {
            // Todo エラー処理要
        }
    }

    /**
     * 初期カテゴリUIセッティング
     */
    private void setInitialCategoryUI() {
        Log.d(TAG, "setInitialCategoryUI start.");
        mCategoryItemListView = findViewById(R.id.lo_item_list_in_category);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mCategoryItemListView.setLayoutManager(manager);
    }

    /**
     * 操作/管理オブジェクトをセッティング
     */
    private void setInitialManager() {
        Log.d(TAG, "setInitialSelectUI start.");
        mSelectItemInflater = LayoutInflater.from(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * UI部品の取得
     */
    private void getUIParts() {
        Log.d(TAG, "getUIParts start.");

        // レイアウト
        mLinearSelectItemLayout = findViewById(R.id.lo_item_list_in_select);
        mScrollSelectItemLayout = findViewById(R.id.scr_item_list_in_select);

        // カテゴリボタン
        mVegetablesBtn = findViewById(R.id.btn_category_vegetables);
        mMeatBtn = findViewById(R.id.btn_category_meat);
        mFishBtn = findViewById(R.id.btn_category_fish);
        mSpiceBtn = findViewById(R.id.btn_category_spice);
        mBreadBtn = findViewById(R.id.btn_category_bread);
        mProcessedBtn = findViewById(R.id.btn_category_processed);
        mSnackBtn = findViewById(R.id.btn_category_snack);
        mFrozenBtn = findViewById(R.id.btn_category_frozen);
        mAlcoholBtn = findViewById(R.id.btn_category_alcohol);
        mDailyNecessitiesBtn = findViewById(R.id.btn_category_daily_necessities);
        mOtherBtn = findViewById(R.id.btn_category_other);

        // システムボタン
        mGarbageBtn = findViewById(R.id.btn_garbage_can);
        mGoTodayListBtn = findViewById(R.id.btn_go_today_item_list);
        mSaveOnlyBtn = findViewById(R.id.btn_save_only);

        // フォーカス変更用のダミー
        dummyFocusView = findViewById(R.id.dummy_focus);
    }

    /**
     * UI部品のリスナーを設定
     */
    private void setUIListener() {
        Log.d(TAG, "setUIListener start.");

        // 子ビューのLinearLayoutではなく親ビューのScrollViewに対してドラッグリスナーを
        // セットしないと、画面右側の選択リスト範囲へドロップできない
        mScrollSelectItemLayout.setOnDragListener(this);

        mVegetablesBtn.setOnClickListener(this);
        mMeatBtn.setOnClickListener(this);
        mFishBtn.setOnClickListener(this);
        mSpiceBtn.setOnClickListener(this);
        mBreadBtn.setOnClickListener(this);
        mProcessedBtn.setOnClickListener(this);
        mSnackBtn.setOnClickListener(this);
        mFrozenBtn.setOnClickListener(this);
        mAlcoholBtn.setOnClickListener(this);
        mDailyNecessitiesBtn.setOnClickListener(this);
        mOtherBtn.setOnClickListener(this);

        mGarbageBtn.setOnDragListener(this);
        mGarbageBtn.setOnClickListener(this);
        mGoTodayListBtn.setOnClickListener(this);
        mSaveOnlyBtn.setOnClickListener(this);

    }

    // ============================================================================================
    // OnClickListener
    // ============================================================================================
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() start");

        if(!initialJsonLoadSuccess){ return; }
        if(!isRepeatedTap()){ return; }

        String buttonName = ((Button) v).getText().toString();
        if(!buttonName.equals(OTHER_CATEGORY_NAME)) {
            // 「その他」以外はカテゴリ内の商品をセットする
            if (isCategoryButton(buttonName)) {
                setCategoryItemListUI(buttonName);
            }
        } else {
            // 「その他」の場合は、アイコンリソースを
            for(ShoppingItem item : mAllItemList) {
                if(item.getCategoryName().equals(OTHER_CATEGORY_NAME)) {
                    otherIconId = item.getItemIconResource();
                }
            }
        }

        String className = null;
        Log.d(TAG, "onClick() Button: " + buttonName);
        // さらにカテゴリボタン、システムボタンごとにクリック操作を追加する場合
        switch (v.getId()) {

            case R.id.btn_category_vegetables:
            case R.id.btn_category_meat:
            case R.id.btn_category_fish:
            case R.id.btn_category_spice:
            case R.id.btn_category_bread:
            case R.id.btn_category_processed:
            case R.id.btn_category_snack:
            case R.id.btn_category_frozen:
            case R.id.btn_category_alcohol:
            case R.id.btn_category_daily_necessities:
                dummyFocusView.requestFocus();
                break;

            case R.id.btn_garbage_can:
                // トースト表示
                displayToastInCenter(R.string.toast_message_drop_unnecessary);
                break;

            case R.id.btn_category_other:
                // 「その他」Viewを選択レイアウトに、新たにインフレートする
                expandOtherShoppingLine();
                break;

            case R.id.btn_go_today_item_list:
                showProgressDialog();

                // 押下で右画面の選択した商品リストをデータベースへ保存し、
                // 本日の商品リストページへ遷移する
                className = PACKAGE_NAME + ".view.TodayItemActivity";
                mShoppingItemList = packShoppingItemToList(mLinearSelectItemLayout);
                mShoppingItemController.saveTodayShoppingItemList(mShoppingItemList, false);

                break;

            case R.id.btn_save_only:
                showProgressDialog();

                // 押下で右画面の選択した商品リストをデータベースへ保存
                mShoppingItemList = packShoppingItemToList(mLinearSelectItemLayout);
                mShoppingItemController.saveTodayShoppingItemList(mShoppingItemList, true);

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
        Log.d(TAG, "onClick() end");
    }

    /**
     * 選択したカテゴリに応じてUIセッティング
     */
    private void setCategoryItemListUI(String categoryName) {

        // 選択したカテゴリの品物リストをクリアしてから取得
        if(!mCategoryItemList.isEmpty()) {
            mCategoryItemList.clear();
        }
        for(ShoppingItem item : mAllItemList) {
            if(item.getCategoryName().equals(categoryName)) {
                mCategoryItemList.add(item);
            }
        }

        // 品物リストをビューへセット
        CategoryItemListAdapter adapter = new CategoryItemListAdapter(mCategoryItemList);
        mCategoryItemListView.setAdapter(adapter);
        mCategoryItemListView.addOnItemTouchListener(this);
    }

    /**
     * 選択したボタンがカテゴリボタンか否か
     */
    private boolean isCategoryButton(String categoryName) {
        boolean isCategory = false;
        if (mShoppingItemController.getCategory().contains(categoryName)) {
            isCategory = true;
        }
        Log.d(TAG, "isCategoryButton: " + isCategory);
        return isCategory;
    }

    // ============================================================================================
    // OnItemTouchListener
    // ============================================================================================
    private Handler categoryPressHandler = new Handler();
    private Runnable categoryPressRunnable = null;

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "categoryItem onTouchEvent start. action is " + e.getAction());

        final View item = rv.findChildViewUnder(e.getX(), e.getY());
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent() event is \"down\".");

                categoryPressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "you can start to drag select item.");
                        if (item != null) {
                            setVisibilityItemNumber(item, false);
                            item.startDrag(null, new View.DragShadowBuilder(item), item, 0);
                            mDragTarget = DragTarget.CATEGORY_LIST_ITEM;
                        }
                    }
                };
                categoryPressHandler.postDelayed(categoryPressRunnable, LONG_PRESS_TIME);

                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent() event is \"up\".");
                categoryPressHandler.removeCallbacks(categoryPressRunnable);
                dummyFocusView.requestFocus();
                break;
            case MotionEvent.ACTION_MOVE:
                // ドラッグ開始
                Log.d(TAG, "onTouchEvent() event is \"move\".");
                // スクロールと区別がつかないため、DOWNイベントの長押しでドラッグイベントを開始させる
//                if (item != null) {
//                    setVisibilityItemNumber(item, false);
//                    item.startDrag(null, new View.DragShadowBuilder(item), item, 0);
//                    mDragTarget = DragTarget.CATEGORY_LIST_ITEM;
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent() event is \"cancel\".");
                // スクロールしたときは本イベントが来るので、スクロール時ドラッグを開始させないようにする
                categoryPressHandler.removeCallbacks(categoryPressRunnable);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        Log.d(TAG, "disallowIntercept: " + disallowIntercept);
    }

    // ============================================================================================
    // OnTouchListener
    // ============================================================================================
    private Handler selectPressHandler = new Handler();
    private Runnable selectPressRunnable = null;

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        Log.d(TAG, "selectItem onTouch start. action is " + event.getAction());

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouch() event is \"down\".");

                selectPressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "you can start to drag select item.");
                        if (v != null) {
                            // ドラッグする選択リスト内のアイテム位置をドラッグリスナーへ伝える
                            int mDragSelectItemPosition = mLinearSelectItemLayout.indexOfChild(v);
                            ClipData data = ClipData.newPlainText("", String.valueOf(mDragSelectItemPosition));

                            v.startDrag(data, new View.DragShadowBuilder(v), v, 0);
                            mDragTarget = DragTarget.SELECT_LIST_ITEM;
                        }
                    }
                };
                selectPressHandler.postDelayed(selectPressRunnable, LONG_PRESS_TIME);

                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouch() event is \"up\".");
                selectPressHandler.removeCallbacks(selectPressRunnable);
                dummyFocusView.requestFocus();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouch() event is \"move\".");
                // スクロールと区別がつかないため、DOWNイベントの長押しでドラッグイベントを開始させる
//                if (v != null) {
//                    // ドラッグする選択リスト内のアイテム位置をドラッグリスナーへ伝える
//                    int mDragSelectItemPosition = mLinearSelectItemLayout.indexOfChild(v);
//                    ClipData data = ClipData.newPlainText("", String.valueOf(mDragSelectItemPosition));
//
//                    v.startDrag(data, new View.DragShadowBuilder(v), v, 0);
//                    mDragTarget = DragTarget.SELECT_LIST_ITEM;
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouch() event is \"cancel\".");
                // スクロールしたときは本イベントが来るので、スクロール時ドラッグを開始させないようにする
                selectPressHandler.removeCallbacks(selectPressRunnable);
            default:
                break;

        }

        return true;
    }

    // ============================================================================================
    // OnDragListener
    // ============================================================================================
    @Override
    public boolean onDrag(View v, DragEvent event) {

        // Todo フォーカス可能なオブジェクトへドロップしたらFATAL
        // Todo EditTextクラスをサブクラス化して、ドラッグリスナーをオーバーライドする必要あり？
        // 画面右側のレイアウトに対してのみリスナー処理
        Log.d(TAG, "DragEvent: " + event.getAction());
        switch(event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                break;
            case DragEvent.ACTION_DROP:

                // ドロップする場所に応じて処理を振り分ける
                if(v.equals(mScrollSelectItemLayout)) {
                    Log.d(TAG, "Action Drop from " + mDragTarget + " to select list screen.");
                    mSelectItemOldSize = mSelectItemNewSize;

                    // ドロップするアイテムはどこからドラッグされてきたか判断する
                    if(mDragTarget == DragTarget.CATEGORY_LIST_ITEM){
                        View dragCategoryItem = (View) event.getLocalState();
                        mDragCategoryItemPosition = ((RecyclerView) dragCategoryItem.getParent()).getChildLayoutPosition(dragCategoryItem);
                        ((RecyclerView) dragCategoryItem.getParent()).removeView(dragCategoryItem);
                        mLinearSelectItemLayout.addView(dragCategoryItem);
                        mSelectItemNewSize = mLinearSelectItemLayout.getChildCount();

                        Log.d(TAG, "Drag item position: " + mDragCategoryItemPosition + " in " + mCategoryItemList.get(mDragCategoryItemPosition).getCategoryName());

                    } else if(mDragTarget == DragTarget.SELECT_LIST_ITEM){

                    } else if(mDragTarget == DragTarget.NONE){

                    } else {
                        // enum指定以外が入ってきた場合のため超異常系
                        // 通常ここには来ない
                    }

                } else if(v.equals(mGarbageBtn)){
                    Log.d(TAG, "Action Drop from " + mDragTarget + " to Garbage box.");
                    mSelectItemOldSize = mSelectItemNewSize;

                    // 選択した商品リストをドラッグしたときの位置情報を取得
                    // ClipDataはACTION_DROPのタイミングでしか受け取れないため、ここで処理する
                    ClipData.Item item = null;
                    if (event.getClipData() != null) {
                        item = event.getClipData().getItemAt(0);
                    }

                    // ドロップするアイテムはどこからドラッグされてきたか判断する
                    if(mDragTarget == DragTarget.CATEGORY_LIST_ITEM){
                        //
                    } else if(mDragTarget == DragTarget.SELECT_LIST_ITEM){
                        // タッチリスナーから送られてきた位置情報を元に、ゴミ箱へドロップしたビューを削除する
                        Integer dragSelectItemPosition = Integer.parseInt(String.valueOf(item.getText()));
                        mLinearSelectItemLayout.removeViewAt(dragSelectItemPosition);
                        mSelectItemNewSize = mLinearSelectItemLayout.getChildCount();
                        mSelectItemRemovePosition = dragSelectItemPosition;

                    } else if(mDragTarget == DragTarget.NONE){
                        //
                    } else {
                        // enum指定以外が入ってきた場合のため超異常系
                        // 通常ここには来ない
                    }
                } else {
                    // Todo 別途Dragリスナーを追加した際
                }
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(TAG, "Drag on Drop result: " + event.getResult());

                // ドロップ場所：　画面右側の選択した商品リスト
                if(v.equals(mScrollSelectItemLayout)) {
                    Log.d(TAG, "Drop to select list item screen.");

                    if (event.getResult()) {
                        // ドラッグ＆ドロップ成功時
                        Log.d(TAG, "Drop success from " + mDragTarget + ".");
                        View shoppingItemLastLine = mLinearSelectItemLayout.getChildAt(mSelectItemNewSize - 1);

                        if (mDragTarget == DragTarget.CATEGORY_LIST_ITEM) {
                            // ドロップされ、右画面の一番後ろに追加されたアイテムにリスナーセット
                            shoppingItemLastLine.setOnTouchListener(this);
                            // アイテム個数関連のビューを見える化/有効化/リスナーセット
                            setVisibilityItemNumber(shoppingItemLastLine, true);

                        } else if (mDragTarget == DragTarget.SELECT_LIST_ITEM) {
                            // パターン１　右画面内でドラッグ&ドロップ成功

                            // パターン2　ゴミ箱へドラッグ&ドロップ成功
                            //
                        } else if (mDragTarget == DragTarget.NONE) {
                            //
                        } else {
                            //
                        }
                    } else {
                        // ドラッグ＆ドロップ失敗時
                        Log.d(TAG, "Drop failed from " + mDragTarget + ".");
                        if(mDragTarget == DragTarget.CATEGORY_LIST_ITEM){
                            //
                        } else if (mDragTarget == DragTarget.SELECT_LIST_ITEM) {
                            //
                        } else if (mDragTarget == DragTarget.NONE) {
                            //
                        } else {
                            //
                        }
                    }
                }
                // ドロップ場所：　画面下側のゴミ箱画像
                else if(v.equals(mGarbageBtn)){
                    Log.d(TAG, "Drop to Garbage box.");

                    if(event.getResult()) {
                        // ドラッグ＆ドロップ成功時
                        if(mDragTarget == DragTarget.CATEGORY_LIST_ITEM){
                            //
                        } else if(mDragTarget == DragTarget.SELECT_LIST_ITEM){
                            // パターン１　右画面内でドラッグ&ドロップ成功
                            // パターン2　ゴミ箱へドラッグ&ドロップ成功

                        } else if(mDragTarget == DragTarget.NONE){
                            //
                        } else {
                            //
                        }
                    } else {
                        // ドラッグ＆ドロップ失敗時
                        if(mDragTarget == DragTarget.CATEGORY_LIST_ITEM){
                            //
                        } else if (mDragTarget == DragTarget.SELECT_LIST_ITEM) {
                            //
                        } else if (mDragTarget == DragTarget.NONE) {
                            //
                        } else {
                            //
                        }
                    }
                } else {
                    // Todo 別途Dragリスナーを追加した際
                }
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            default:
                Log.d(TAG, "no action");
                break;
        }
        return true;
    }

    // ============================================================================================
    // キーボード入力関連
    // ============================================================================================
    private CharSequence preChr = "1";  // 文字列変更前
    private CharSequence postChr = "1";  // 文字列変更後
    private final int NUMBER_SIZE_ONE = 1;  // 個数欄の桁数
    private final int NUMBER_SIZE_TWO = 2;  // 個数欄の桁数
    private final int NUMBER_POSITION_ONE = 1;  // 個数欄の位置
    private final int NUMBER_POSITION_TWO = 2;  // 個数欄の位置
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        preChr = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        postChr = s;
    }

    /**
     * フォーカス可否によるリスナー
     * xml --> focusableInTouchMode:true ユーザのタッチイベントで動的にフォーカスを充てられる
     * xml --> focusable:true requestFocus()で動的にフォーカスを充てられる
     * デバイスがタッチ可能なら、タッチモードのフォーカスが優先される
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "onFocusChange() targetView focus is " + v.isFocused());
        Log.d(TAG, "onFocusChange(): dummyFocusView --> isFocusable = " + dummyFocusView.isFocusable() +
                ", isInTouchMode = " + dummyFocusView.isFocusable() + ", isFocused = " + dummyFocusView.isFocused());

        if(!hasFocus){
            // フォーカスOFFの時
            if(postChr.equals("") || postChr == null || postChr.length() == 0){
                ((EditText) v).setText("1");  // デフォルト個数は「1」
            }
            mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            v.clearFocus();
        } else {
            // フォーカスONの時
//            if(isTapItemNum){
//                isTapItemNum = false;
//            } else {
//                v.clearFocus();
//            }
            mInputMethodManager.toggleSoftInputFromWindow(v.getWindowToken(), InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            // デフォルトでカーソルを、初期値：「1」の後ろに移動させておく
            if(v.getId() == R.id.item_number){
                if(((EditText) v).getText().length() == NUMBER_SIZE_ONE){
                    ((EditText) v).setSelection(NUMBER_POSITION_ONE);
                } else if(((EditText) v).getText().length() == NUMBER_SIZE_TWO){
                    ((EditText) v).setSelection(NUMBER_POSITION_TWO);
                }
            }
        }
    }

    /**
     * キーイベント
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        Log.d(TAG, "onKeyUp() start");
        Log.d(TAG, "onKeyUp() event is " + event.getAction());
        Log.d(TAG, "onKeyUp() press key is " + event.getKeyCode());
        switch (keyCode){
            case KeyEvent.KEYCODE_ENTER:
                Log.d(TAG, "onKeyUp(): press key is Enter");
                dummyFocusView.requestFocus();
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    // ============================================================================================
    // 画面表示
    // ============================================================================================
    @Override
    public void displaySaveOnly(){
        Log.d(TAG, "displaySaveOnly(): start");
        dismissProgressDialog();
        displayToastInCenter(R.string.toast_message_save_only_success);
        Log.d(TAG, "displaySaveOnly(): end");
    }

    @Override
    public void displayNotYetMemo() {
        Log.d(TAG, "displayNotYetMemo(): start");
        dismissProgressDialog();
        displayToastInCenter(R.string.toast_message_please_drop);
        Log.d(TAG, "displayNotYetMemo(): end");
    }

    @Override
    public void displayTodayShoppingList(ArrayList<ShoppingItem> todayList){
        Log.d(TAG, "displayTodayShoppingList(): start");

        expandShoppingList(todayList);
        mSelectItemNewSize = mLinearSelectItemLayout.getChildCount();
        Log.d(TAG, "displayTodayShoppingList() mSelectItemNewSize = " + mSelectItemNewSize);

        dismissProgressDialog();

        Log.d(TAG, "displayTodayShoppingList(): end");
    }

    /**
     * 画面中央にトースト表示
     */
    private void displayToastInCenter(int resourceId){
        Log.d(TAG, "displayToastInCenter(): start");
        Toast toast = Toast.makeText(this, resourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Log.d(TAG, "displayToastInCenter(): end");
    }

    // ============================================================================================
    // その他
    // ============================================================================================

    ProgressDialog mProgressDialog = null;
    /**
     * レイアウトに配置されているプログレスバーを取得/表示させる
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

//        ProgressBar bar = findViewById(R.id.pb_get_select_item);
//        bar.setVisibility(View.VISIBLE);
    }

    /**
     * レイアウトに配置されているプログレスバーを取得/非表示させる
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

//        ProgressBar bar = findViewById(R.id.pb_get_select_item);
//        bar.setVisibility(View.GONE);
    }

    /**
     * アイテム個数関連のビューの表示を操作する
     * @param: アイテムリストの1行分
     * @param: 表示可否
     */
    private void setVisibilityItemNumber(View line, boolean see){
        Log.d(TAG, "setVisibility: " + see);

        if(line instanceof LinearLayout){
        } else {
            return;
        }

        for(int i = 0; i < ((LinearLayout) line).getChildCount(); i++){
            View child = ((LinearLayout) line).getChildAt(i);
            String tag = String.valueOf(child.getTag());
            EditText et;
            if(child != null) {
                switch(tag) {
                    case "multi":
                        if (see) {
                            child.setVisibility(View.VISIBLE);
                        } else {
                            child.setVisibility(View.GONE);
                        }
                        break;
                    case "num":
                        if (see) {
                            et = (EditText) child;
                            et.setVisibility(View.VISIBLE);
                            et.setEnabled(true);
                            et.addTextChangedListener(this);
                            et.setOnFocusChangeListener(this);
                        } else {
                            child.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 連打対策
     */
    private boolean isRepeatedTap(){
        long currentTime = System.currentTimeMillis();
        long diff = Math.abs(currentTime - mOldCurrentTime);

        if(diff < TAP_DELAY){
            return false;
        }
        return true;
    }

    /**
     * 選択した商品をレイアウトエリアからShoppingItemのList型へ詰め込む
     */
    private ArrayList<ShoppingItem> packShoppingItemToList(LinearLayout layout){

        LinearLayout shoppingItemLine;
        ArrayList<ShoppingItem> selectList = new ArrayList<>();

        // Todo
        for(int i=0; i < mSelectItemNewSize; i++){
            shoppingItemLine = (LinearLayout) mLinearSelectItemLayout.getChildAt(i);

            int resourceId = shoppingItemLine.getChildAt(LO_POS_ICON_ID).getId();
            String categoryID = ((TextView) shoppingItemLine.getChildAt(LO_POS_CATEGORY_ID)).getText().toString();
            String categoryName = ((TextView) shoppingItemLine.getChildAt(LO_POS_CATEGORY_NAME)).getText().toString();
            String itemID = ((TextView) shoppingItemLine.getChildAt(LO_POS_ITEM_ID)).getText().toString();
            String itemName = ((TextView) shoppingItemLine.getChildAt(LO_POS_ITEM_NAME)).getText().toString();
            String itemNumber = ((EditText)shoppingItemLine.getChildAt(LO_POS_ITEM_NUM)).getText().toString();

            ShoppingItem item = new ShoppingItem();
            item.setItemIconResource(resourceId);
            item.setCategoryID(categoryID);
            item.setCategoryName(categoryName);
            item.setItemID(itemID);
            item.setItemName(itemName);
            item.setItemNumber(itemNumber);

            selectList.add(item);
        }
        return selectList;
    }

    /**
     * ShoppingItemのListを選択レイアウトエリアへ展開する
     */
    private void expandShoppingList(ArrayList<ShoppingItem> shoppingList){

        LayoutInflater inflater = LayoutInflater.from(this);
        for(ShoppingItem item : shoppingList){

            inflater.inflate(R.layout.line_category_item, mLinearSelectItemLayout, true);
            mSelectItemNewSize = mLinearSelectItemLayout.getChildCount();
            Log.d(TAG, "expandShoppingList() add item: category=" + item.getCategoryName()
                    + ", name=" + item.getItemName() + ", number=" + item.getItemNumber());

            LinearLayout lineLayout = (LinearLayout) mLinearSelectItemLayout.getChildAt(mSelectItemNewSize - 1);
            ((ImageView) lineLayout.findViewById(R.id.item_icon)).setImageResource(item.getItemIconResource());
            ((TextView) lineLayout.findViewById(R.id.category_id)).setText(item.getCategoryID());
            ((TextView) lineLayout.findViewById(R.id.category_name)).setText(item.getCategoryName());
            ((TextView) lineLayout.findViewById(R.id.item_id)).setText(item.getItemID());
            ((TextView) lineLayout.findViewById(R.id.item_name)).setText(item.getItemName());
            ((TextView) lineLayout.findViewById(R.id.item_number)).setText(item.getItemNumber());

            lineLayout.setOnTouchListener(this);
            setVisibilityItemNumber(lineLayout, true);
        }
    }

    /**
     * 「その他」を選択レイアウトエリアへ展開する
     */
    private void expandOtherShoppingLine(){
        Log.d(TAG, "expandOtherShoppingLine() add other item line");
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        inflater.inflate(R.layout.line_other_item, mLinearSelectItemLayout, true);
        mSelectItemNewSize = mLinearSelectItemLayout.getChildCount();

        LinearLayout shoppingLastLine = (LinearLayout) mLinearSelectItemLayout.getChildAt(mSelectItemNewSize - 1);
        ((ImageView)shoppingLastLine.getChildAt(LO_POS_ICON_ID)).setImageResource(otherIconId);
        ((TextView)shoppingLastLine.getChildAt(LO_POS_CATEGORY_ID)).setText(OTHER_CATEGORY_ID);
        ((TextView)shoppingLastLine.getChildAt(LO_POS_CATEGORY_NAME)).setText(OTHER_CATEGORY_NAME);
        ((TextView)shoppingLastLine.getChildAt(LO_POS_ITEM_ID)).setText(OTHER_ITEM_ID);

        shoppingLastLine.setOnTouchListener(this);
        shoppingLastLine.getChildAt(LO_POS_ITEM_NAME).setOnFocusChangeListener(this);
        shoppingLastLine.getChildAt(LO_POS_ITEM_NUM).setOnFocusChangeListener(this);

    }

}
