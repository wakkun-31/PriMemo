package com.wata.primemo.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * 本クラスは、UIスレッドでの使用は避け、非同期処理で行う
 */

public class ShoppingItemDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ShoppingItemDbHelper";

    // assetsフォルダの商品定義リストを変更する際は、バージョンを上げる
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "appDB.db";
    private static ShoppingItemDbHelper mShoppingItemDbHelper = null;
    private static SQLiteDatabase mSQLiteDatabase = null;
    private Context mContext;
    // data/data/(package)/databases内へのDBファイルパス
    private File mDatabasePath;
    // SQLiteDBにテーブルが存在する(true)か否(false)か
    private boolean tableExist = false;

    private static final String TABLE_NAME_SHOPPING_HISTORY = "shopping_history";
    private static final String COLUMN_NAME_SHOPPING_DATE = "shopping_date";
    private static final String COLUMN_NAME_CATEGORY_ID = "category_id";
    private static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
    private static final String COLUMN_NAME_ITEM_ID = "item_id";
    private static final String COLUMN_NAME_ITEM_NAME = "item_name";
    private static final String COLUMN_NAME_ITEM_NUMBER = "item_number";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String AND = " AND ";
    private static final String EQUAL = " = ";

    private static final String CREATE_TABLE_SHOPPING_HISTORY
            = "CREATE TABLE " + TABLE_NAME_SHOPPING_HISTORY
            + "(" + COLUMN_NAME_SHOPPING_DATE + " TEXT" + ", "
            + COLUMN_NAME_CATEGORY_ID + " TEXT" + ", "
            + COLUMN_NAME_CATEGORY_NAME + " TEXT" + ", "
            + COLUMN_NAME_ITEM_ID + " TEXT" + ", "
            + COLUMN_NAME_ITEM_NAME + " TEXT" + ", "
            + COLUMN_NAME_ITEM_NUMBER + " TEXT" + ")";
    private static final String DELETE_TABLE_SHOPPING_HISTORY
            = "DROP TABLE " + TABLE_NAME_SHOPPING_HISTORY;
    private static final String VACUUM = "VACUUM";


    private ShoppingItemDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        // androidアプリ内のデータベースパスを取得する
        mDatabasePath = mContext.getDatabasePath(DATABASE_NAME);
    }

    public static ShoppingItemDbHelper getInstance(Context context){
        Log.d(TAG, "getInstance");
        if(mShoppingItemDbHelper == null){
            mShoppingItemDbHelper = new ShoppingItemDbHelper(context);
        }
        return mShoppingItemDbHelper;
    }

    public static ShoppingItemDbHelper getInstance(){
        Log.d(TAG, "getInstance");
        return mShoppingItemDbHelper;
    }

    /**
     * @brief 初回にSQLiteDatabaseオブジェクトが作成された場合にコールバック
     * getWritableDatabaseやgetReadableDatabaseの初回呼び出し時
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate() start");
        db.execSQL(CREATE_TABLE_SHOPPING_HISTORY);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        Log.d(TAG, "onOpen() start");
        super.onOpen(db);
    }

    /**
     * @brief DB定義が変更された場合にコールバック
     * @deprecated DB定義変更とは：　DBのバージョンが上がる
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() start");
        // バージョンが上がった時だけ、処理する
        if(oldVersion >= newVersion){
            return;
        }

        // デバック用
        // バージョンを上げた際、一度テーブル内容をすべて消去し
        String dbPath = this.mDatabasePath.getAbsolutePath();
        File dbFile = new File(dbPath);
        if(dbFile.exists()){
            // Todo テーブル定義更新処理
            db.execSQL(DELETE_TABLE_SHOPPING_HISTORY);
            onCreate(db);
            }
        }


    /**
     * 編集可能なDBオブジェクトを取得
     * @return SQLiteDatabase
     */
    @Override
    public SQLiteDatabase getWritableDatabase(){
        Log.d(TAG, "getWritableDatabase(): start");
        return super.getWritableDatabase();
    }
}
