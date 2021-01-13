package com.wata.primemo.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.wata.primemo.model.GetIdNameCB;
import com.wata.primemo.model.GetShoppingDateCB;
import com.wata.primemo.model.GetShoppingHistoryCB;
import com.wata.primemo.model.SetShoppingHistoryCB;
import com.wata.primemo.model.ShoppingItem;

import java.util.ArrayList;
import java.util.HashSet;

public class ShoppingItemDbUtility {

    private static final String TAG = "ShoppingItemDbUtility";

    private static final String TABLE_NAME_SHOPPING_HISTORY = "shopping_history";
    private static final String TABLE_NAME_CATEGORY_VS_ITEM = "category_vs_item";
    private static final String COLUMN_NAME_SHOPPING_DATE = "shopping_date";
    private static final String COLUMN_NAME_CATEGORY_ID = "category_id";
    private static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
    private static final String COLUMN_NAME_ITEM_ID = "item_id";
    private static final String COLUMN_NAME_ITEM_NAME = "item_name";
    private static final String COLUMN_NAME_ITEM_NUMBER = "item_number";
    private static final String AND = " AND ";
    private static final String EQUAL = " = ";
    private static final String VALUES = " VALUES ";
    private static final String ON_CONFLICT = " ON CONFLICT ";
    private static final String INSERT = "INSERT INTO ";
    private static final String UPDATE = " DO UPDATE SET ";

    private static final String SELECT_TODAY_ALL_RECORD
            = "SELECT * " + "FROM " + TABLE_NAME_SHOPPING_HISTORY +
            " WHERE " + COLUMN_NAME_SHOPPING_DATE + " = ";
    private static final String SELECT_ALL_DATE
            = "SELECT " + COLUMN_NAME_SHOPPING_DATE + " FROM " + TABLE_NAME_SHOPPING_HISTORY;
    private static final String SELECT_CATEGORY_NAME
            = "SELECT " + COLUMN_NAME_CATEGORY_NAME +
            " FROM " + TABLE_NAME_CATEGORY_VS_ITEM + " WHERE " + COLUMN_NAME_CATEGORY_ID + EQUAL;
    private static final String SELECT_ITEM_NAME
            = "SELECT " + COLUMN_NAME_ITEM_NAME +
            " FROM " + TABLE_NAME_CATEGORY_VS_ITEM + " WHERE " + COLUMN_NAME_CATEGORY_ID + EQUAL;
    private static final String DELETE_TARGET_DAY_RECORD
            = "DELETE FROM " + TABLE_NAME_SHOPPING_HISTORY +
            " WHERE " + COLUMN_NAME_SHOPPING_DATE + " = ";

    private  ShoppingItemDbHelper mShoppingItemDbHelper;
//    private  ShoppingItemDbHelper mShoppingItemDbHelper = ShoppingItemDbHelper.getInstance();
    private static ShoppingItemDbUtility mInstance = null;
    private static Context mContext;
    private Object mDBLock = new Object();

    private ShoppingItemDbUtility(){}

    private ShoppingItemDbUtility(Context context){
        mContext = context;
        mShoppingItemDbHelper = ShoppingItemDbHelper.getInstance(mContext);
    }

    public static ShoppingItemDbUtility getInstance(){
        Log.d(TAG, "getInstance");
        if (mInstance == null){
            mInstance = new ShoppingItemDbUtility();
        }
        return mInstance;
    }

    public static ShoppingItemDbUtility getInstance(Context context){
        Log.d(TAG, "getInstance");
        if (mInstance == null){
            mInstance = new ShoppingItemDbUtility(context);
        }
        return mInstance;
    }

    // =====================================================================
    // 買い物履歴テーブル
    // =====================================================================

    /**
     * 買い物履歴から、指定した日付と一致するレコードを全て取得する
     * @param: String 日付(yyyy/MM/dd), callback
     * @return true:取得成功、 false:取得失敗
     */
    public void selectAllItemFromDate(final String date, final GetShoppingHistoryCB callback){
        Log.d(TAG, "selectAllItemFromDate(): start.");

        synchronized (this) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "selectAllItemFromDate(): start in Thread.");

                    ArrayList<ShoppingItem> retRecordList = new ArrayList<>();
                    ShoppingItem item = new ShoppingItem();
                    SQLiteDatabase db = mShoppingItemDbHelper.getWritableDatabase();
                    boolean isNullRecord = false;
                    boolean dbResult = true;

                    try {

                        Cursor cursor = db.rawQuery(SELECT_TODAY_ALL_RECORD + "'" + date + "'", null);
                        int clmIdxCategoryID = cursor.getColumnIndex(COLUMN_NAME_CATEGORY_ID);
                        int clmIdxCategoryName = cursor.getColumnIndex(COLUMN_NAME_CATEGORY_NAME);
                        int clmIdxItemID = cursor.getColumnIndex(COLUMN_NAME_ITEM_ID);
                        int clmIdxItemName = cursor.getColumnIndex(COLUMN_NAME_ITEM_NAME);
                        int clmIdxItemNumber = cursor.getColumnIndex(COLUMN_NAME_ITEM_NUMBER);


                        while (cursor.moveToNext()) {
                            if (cursor.isNull(clmIdxCategoryID) || cursor.isNull(clmIdxItemID) || cursor.isNull(clmIdxItemNumber)) {
                                Log.d(TAG, "selectAllItemFromDate(): part is null.");
                                // 一つでもNull値があれば検出する
                                isNullRecord = true;
                            }

                            ShoppingItem shoppingItem = item.clone();
                            shoppingItem.setCategoryID(cursor.getString(clmIdxCategoryID));
                            shoppingItem.setCategoryName(cursor.getString(clmIdxCategoryName));
                            shoppingItem.setItemID(cursor.getString(clmIdxItemID));
                            shoppingItem.setItemName(cursor.getString(clmIdxItemName));
                            shoppingItem.setItemNumber(cursor.getString(clmIdxItemNumber));
                            Log.d(TAG, "selectAllItemFromDate() today select item = " + cursor.getString(clmIdxItemName));

                            retRecordList.add(shoppingItem);
                        }
                        Log.d(TAG, "selectAllItemFromDate(): success.");

                    } catch (Exception e) {
                        Log.d(TAG, "selectAllItemFromDate(): failed.");
                        dbResult = false;
                        e.printStackTrace();
                    } finally {
                        db.close();
                        final ArrayList<ShoppingItem> dbRet = retRecordList;
                        final boolean isSuccess = dbResult;
                        final boolean isNull = isNullRecord;

                        // DB処理はすべて非同期のため、コールバックはHandlerを使用してUIスレッドへ通知
                        if (callback != null) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isSuccess) {
                                        if (isNull) {
                                            callback.onLossData(dbRet);
                                        } else {
                                            callback.onFinish(dbRet);
                                        }
                                    } else {
                                        callback.onException();
                                    }
                                }
                            });
                        }
                    }
                    Log.d(TAG, "selectAllItemFromDate(): end in Thread.");
                }
            }).start();
        }
        Log.d(TAG, "selectAllItemFromDate(): end.");
    }

    /**
     * 選択した商品をDBへ挿入
     * 今日のレコードは一度消去される
     */
    public void insertShoppingList(String date, ArrayList<ShoppingItem> list, final SetShoppingHistoryCB callback){
        Log.d(TAG, "insertShoppingList(): start.");

        final String today = date;
        final ArrayList<ShoppingItem> shoppingList = list;

        synchronized (this) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "insertShoppingList(): start in Thread.");
                    SQLiteDatabase db = mShoppingItemDbHelper.getWritableDatabase();
                    boolean result = true;
                    int count = 0;

                    try {
                        if (!shoppingList.isEmpty()) {
                            db.execSQL(DELETE_TARGET_DAY_RECORD + "'" + today + "'");
                            Log.d(TAG, "insertShoppingList() shopping list size = " + shoppingList.size());
                            for (ShoppingItem item : shoppingList) {

                                String insertValues = "'" + item.getCategoryID() + "'" + ", " + "'" + item.getCategoryName() + "'"
                                        + ", " + "'" + item.getItemID() + "'" + ", " + "'" + item.getItemName() + "'"
                                        + ", " + "'" + item.getItemNumber() + "'";
//                        String upsertValues = COLUMN_NAME_CATEGORY_ID + " = " + item.getCategoryID() + ", "
//                                + COLUMN_NAME_CATEGORY_NAME + " = " + item.getCategoryName() + ", "
//                                + COLUMN_NAME_ITEM_ID + " = " + item.getItemID() + ", "
//                                + COLUMN_NAME_ITEM_NAME + " = " + item.getItemName() + ", "
//                                + COLUMN_NAME_ITEM_NUMBER + " = " + item.getItemNumber();

                                // 対象日付のレコードを削除してから、新たに挿入する
                                db.execSQL(INSERT + TABLE_NAME_SHOPPING_HISTORY + VALUES
                                        + "( '" + today + "', " + insertValues + " )");

                                Log.d(TAG, "insertShoppingList() count up: " + ++count);
                            }
                            Log.d(TAG, "insertShoppingList(): success.");
                        } else {
                            db.execSQL(DELETE_TARGET_DAY_RECORD + "'" + today + "'");
                            Log.d(TAG, "insertShoppingList(): no insert and only delete.");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "insertShoppingList(): failed.");
                        e.printStackTrace();
                        result = false;
                    } finally {
                        final boolean ret = result;

                        if (callback != null) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ret) {
                                        callback.onFinish();
                                    } else {
                                        callback.onException();
                                    }
                                }
                            });
                        }
                    }
                    Log.d(TAG, "insertShoppingList(): end in Thread.");
                }
            }).start();
        }
        Log.d(TAG, "insertShoppingList(): end.");
    }

    /**
     * 過去の買い物日をset形式で全て取得
     */
    public void getAllShoppingDateList(final GetShoppingDateCB callback){
        Log.d(TAG, "getAllShoppingDateList(): start.");

        synchronized (this) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "getAllShoppingDateList(): start in Thread.");
                    SQLiteDatabase db = mShoppingItemDbHelper.getWritableDatabase();
                    HashSet<String> dateList = new HashSet<>();
                    boolean result = true;
                    boolean isNull = false;

                    try {

                        Cursor cursor = db.rawQuery(SELECT_ALL_DATE, null);

                        while (cursor.moveToNext()) {
                            if (cursor.isNull(0)) {
                                Log.d(TAG, "getAllShoppingDateList(): date column is null.");
                                isNull = true;
                            } else {
                                dateList.add(cursor.getString(0));
                            }
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "getAllShoppingDateList(): failed.");
                        e.printStackTrace();
                        result = false;
                    } finally {
                        final HashSet<String> set = dateList;
                        final boolean ret = result;
                        final boolean isNoShopping = isNull;

                        if (callback != null) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ret) {
                                        if (!isNoShopping) {
                                            callback.onFinish(set);
                                        } else {
                                            callback.onNoDate();
                                        }
                                    } else {
                                        callback.onException();
                                    }
                                }
                            });
                        }
                    }
                    Log.d(TAG, "getAllShoppingDateList(): end in Thread.");
                }
            }).start();
        }
        Log.d(TAG, "getAllShoppingDateList(): end.");
    }

    // 以下、中止
    // =====================================================================
    // 定義テーブル（カテゴリIDとアイテムIDで一意に特定）
    // =====================================================================
    /**
     * カテゴリIDからカテゴリ名を取得
     */
    public void selectCategoryName(final String categoryID, final GetIdNameCB callback){
        Log.d(TAG, "selectCategoryName(): start.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = mShoppingItemDbHelper.getWritableDatabase();
                String categoryName = "";
                boolean dbResult = true;

                try{
                    Cursor cursor = db.rawQuery(SELECT_CATEGORY_NAME + categoryID, null);
                    categoryName = cursor.getString(0);
                    Log.d(TAG, "selectCategoryName(): success.");
                } catch (Exception e){
                    Log.d(TAG, "selectCategoryName(): failed.");
                    dbResult = false;
                    e.printStackTrace();
                } finally {
                    db.close();
                    final String dbRet = categoryName;
                    final boolean isSuccess = dbResult;

                    if(callback != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (isSuccess) {
                                    callback.onFinish(dbRet);
                                } else {
                                    callback.onException();
                                }

                            }
                        });
                    }
                }
            }
        }).start();
    }


    /**
     * カテゴリIDとアイテムIDからアイテム名を取得
     */
    public void selectItemName(final String categoryID, final String itemID, final GetIdNameCB callback){
        Log.d(TAG, "selectItemName(): start.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = mShoppingItemDbHelper.getWritableDatabase();
                String itemName = "";
                boolean dbResult = true;

                try{
                    Cursor cursor = db.rawQuery(SELECT_ITEM_NAME + categoryID +
                            AND + COLUMN_NAME_ITEM_ID + EQUAL + itemID, null);
                    itemName = cursor.getString(0);
                    Log.d(TAG, "selectItemName(): success.");
                } catch (Exception e){
                    Log.d(TAG, "selectItemName(): failed.");
                    dbResult = false;
                    e.printStackTrace();
                } finally {
                    db.close();
                    final String dbRet = itemName;
                    final boolean isSuccess = dbResult;

                    if(callback != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (isSuccess) {
                                    callback.onFinish(dbRet);
                                } else {
                                    callback.onException();
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }
    // ここまで中止

}
