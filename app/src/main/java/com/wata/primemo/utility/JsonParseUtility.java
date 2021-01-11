package com.wata.primemo.utility;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;

public class JsonParseUtility {

    private static final String TAG = "JsonParseUtility";
    private static final String JSON_SHOPPING_ITEM_DEFINITION = "itemlist_shopping.jsonc";

    // =====================================================================
    // ショッピングアイテム関連
    // =====================================================================
    /**
     * Assetsで定義されているショッピング品データを読み込んでJsonオブジェクトで返却
     */
    public static JsonNode loadJson(Context context) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        AssetManager manager = context.getAssets();

        InputStream inputStream = manager.open(JSON_SHOPPING_ITEM_DEFINITION);
        JsonNode node = mapper.readTree(inputStream);

        if(node == null){
            // 読み込み失敗時は要求側で処理
            Log.e(TAG, "shopping json data is not loaded.");
        }
        return node;
    }
}
