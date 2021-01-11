package com.wata.primemo.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.wata.primemo.R;
import com.wata.primemo.model.BaseListener;
import com.wata.primemo.model.DisplayPastDBListener;
import com.wata.primemo.model.DisplayPastDateDBListener;
import com.wata.primemo.model.DisplaySelectDBListener;
import com.wata.primemo.model.DisplayTodayDBListener;
import com.wata.primemo.model.GetShoppingDateCB;
import com.wata.primemo.model.GetShoppingHistoryCB;
import com.wata.primemo.model.SetShoppingHistoryCB;
import com.wata.primemo.model.ShoppingItem;
import com.wata.primemo.utility.JsonParseUtility;
import com.wata.primemo.utility.ShoppingItemDbUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ShoppingItemController {

    private static final String TAG = "ShoppingItemController";
    private static ShoppingItemController mInstance;
    private static final Map<String, Integer> ICON_MAP;
    private JsonNode mItemDefinitionNode;
    private HashSet<String> mCategory = new HashSet<>();
    private HashSet<String> mPastDate = new HashSet<>();

    private Context mContext;
    private ShoppingItemDbUtility mShoppingItemDbUtility;

    private ArrayList<BaseListener> mListeners;
    private DisplayTodayDBListener mDisplayTodayDBListener;
    private DisplaySelectDBListener mDisplaySelectDBListener;
    private DisplayPastDateDBListener mDisplayPastDateDBListener;
    private DisplayPastDBListener mDisplayPastDBListener;

    private ShoppingItemController(){
        mShoppingItemDbUtility = ShoppingItemDbUtility.getInstance();
    }

    private ShoppingItemController(Context context){
        mContext = context;
        mShoppingItemDbUtility = ShoppingItemDbUtility.getInstance(context);
    }

    public static ShoppingItemController getInstance() {
        if (mInstance == null) {
            mInstance = new ShoppingItemController();
        }
        return mInstance;
    }

    public static ShoppingItemController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ShoppingItemController(context);
        }
        return mInstance;
    }

    // =====================================================================
    // Listener関連
    // =====================================================================
    // TodayItemActivity用
    public void setListener(DisplayTodayDBListener listener){ mDisplayTodayDBListener = listener; }
    public void removeListener(DisplayTodayDBListener listener){ mDisplayTodayDBListener = null; }

    // SelectShoppingItemActivity用
    public void setListener(DisplaySelectDBListener listener){ mDisplaySelectDBListener = listener; }
    public void removeListener(DisplaySelectDBListener listener){ mDisplaySelectDBListener = null; }

    // PastListActivity用
    public void setListener(DisplayPastDateDBListener listener){ mDisplayPastDateDBListener = listener; }
    public void removeListener(DisplayPastDateDBListener listener){ mDisplayPastDateDBListener = null; }

    // PastItemActivity用
    public void setListener(DisplayPastDBListener listener){ mDisplayPastDBListener = listener; }
    public void removeListener(DisplayPastDBListener listener){ mDisplayPastDBListener = null; }


    // =====================================================================
    // JSON関連
    // =====================================================================
    /**
     * 定義されているショッピング品JsonファイルをJsonオブジェクトへ変換
     */
    public boolean initialJsonLoad(Context context) {

        boolean successFlag = true;
        try{
            mItemDefinitionNode = JsonParseUtility.loadJson(context);
        } catch (Exception e) {
            e.printStackTrace();
            successFlag = false;
        }
        return successFlag;
    }

    /**
     * 読み込んだJsonオブジェクトをShoppingItemのリスト形式で取得
     */
    public ArrayList<ShoppingItem> getAllItemList() {
        Log.d(TAG, "getAllItemList(): start");
        ArrayList<ShoppingItem> itemList = new ArrayList<>();

        if(mItemDefinitionNode != null) {
            JsonNode nodeList = mItemDefinitionNode.path("shoppingItemDefinition");
            JsonNode nodeItemList;

            if(!nodeList.isMissingNode()) {
                ShoppingItem item = new ShoppingItem();
                for (JsonNode nodes : nodeList) {
                    item.setCategoryID(nodes.get("categoryID").asText());
                    item.setCategoryName(nodes.get("categoryName").asText());
                    mCategory.add(nodes.get("categoryName").asText());
                    nodeItemList = nodes.path("itemList");

                    if(!nodeItemList.isMissingNode()) {
                        for (JsonNode nodeItem : nodeItemList) {
                            ShoppingItem clone = item.clone();
                            clone.setItemID(nodeItem.get("id").asText());
                            clone.setItemName(nodeItem.get("name").asText());
                            clone.setItemIconResource(getIconResource(clone.getCategoryID(), clone.getItemID()));
                            if (clone.isJsonNullCheck()) {
                                // Jsonから取得したShoppingItemの項目にnullが1つ以上
                                // Todo エラー処理要
                                Log.e(TAG, "There is null in ShoppingItem.");
                            } else {
                                itemList.add(clone);
                            }
                        }
                    }
                }
            }
        } else {
            // 読み込んだJsonオブジェクト自体がnull
            // あるいは読み込む(initialJsonLoad)前に呼ばれている
            // Todo エラー処理要
            Log.e(TAG, "JsonNode is null.");
        }
        Log.d(TAG, "getAllItemList(): end");
        return itemList;
    }

    /**
     *
     */
    public HashSet<String> getCategory() {
        return mCategory;
    }

    /**
     *
     */

    // =====================================================================
    // アイコンリソースの割付関連
    // =====================================================================

    static{
        Map<String, Integer> iconsMap = new HashMap<String, Integer>();

        // iconsMap.put("カテゴリID-品物ID", アイコンリソース);
        iconsMap.put("010-001", R.drawable.ic_asparagus_120px);
        iconsMap.put("010-002", R.drawable.ic_eringi_120px);
        iconsMap.put("010-003", R.drawable.ic_okra_120px);
        iconsMap.put("010-004", R.drawable.ic_cabbage_120px);
        iconsMap.put("010-005", R.drawable.ic_cucumber_120px);
        iconsMap.put("010-006", R.drawable.ic_burdock_120px);
        iconsMap.put("010-007", R.drawable.ic_komatuna_120px);
        iconsMap.put("010-008", R.drawable.ic_sweet_potato_120px);
        iconsMap.put("010-009", R.drawable.ic_mushroom_120px);
        iconsMap.put("010-010", R.drawable.ic_potato_120px);
        iconsMap.put("010-011", R.drawable.ic_crowndaisy_120px);
        iconsMap.put("010-012", R.drawable.ic_celery_120px);
        iconsMap.put("010-013", R.drawable.ic_radish_120px);
        iconsMap.put("010-014", R.drawable.ic_onion_120px);
        iconsMap.put("010-015", R.drawable.ic_bok_choy_120px);
        iconsMap.put("010-016", R.drawable.ic_bean_seedling_120px);
        iconsMap.put("010-017", R.drawable.ic_tomato_120px);
        iconsMap.put("010-018", R.drawable.ic_eggplant_120px);
        iconsMap.put("010-019", R.drawable.ic_japanese_leek_120px);
        iconsMap.put("010-020", R.drawable.ic_chinese_chive_120px);
        iconsMap.put("010-021", R.drawable.ic_carrot_120px);
        iconsMap.put("010-022", R.drawable.ic_chinese_cabbage_120px);
        iconsMap.put("010-023", R.drawable.ic_green_pepper_120px);
        iconsMap.put("010-024", R.drawable.ic_broccoli_120px);
        iconsMap.put("010-025", R.drawable.ic_spinach_120px);
        iconsMap.put("010-026", R.drawable.ic_maitake_120px);
        iconsMap.put("010-027", R.drawable.ic_mizuna_120px);
        iconsMap.put("010-028", R.drawable.ic_bean_sprouts_120px);
        iconsMap.put("010-029", R.drawable.ic_lotus_root_120px);
        iconsMap.put("011-001", R.drawable.ic_pork_120px);
        iconsMap.put("011-002", R.drawable.ic_pork_120px);
        iconsMap.put("011-003", R.drawable.ic_pork_120px);
        iconsMap.put("011-004", R.drawable.ic_pork_120px);
        iconsMap.put("011-005", R.drawable.ic_pork_120px);
        iconsMap.put("011-006", R.drawable.ic_pork_120px);
        iconsMap.put("011-007", R.drawable.ic_chicken_120px);
        iconsMap.put("011-008", R.drawable.ic_chicken_120px);
        iconsMap.put("011-009", R.drawable.ic_chicken_120px);
        iconsMap.put("011-010", R.drawable.ic_chicken_120px);
        iconsMap.put("011-011", R.drawable.ic_chicken_120px);
        iconsMap.put("011-012", R.drawable.ic_chicken_120px);
        iconsMap.put("011-013", R.drawable.ic_chicken_120px);
        iconsMap.put("011-014", R.drawable.ic_chicken_120px);
        iconsMap.put("011-015", R.drawable.ic_beef_120px);
        iconsMap.put("011-016", R.drawable.ic_beef_120px);
        iconsMap.put("011-017", R.drawable.ic_beef_120px);
        iconsMap.put("011-018", R.drawable.ic_beef_120px);
        iconsMap.put("011-019", R.drawable.ic_beef_120px);
        iconsMap.put("011-020", R.drawable.ic_minced_120px);
        iconsMap.put("012-001", R.drawable.ic_horse_mackerel_120px);
        iconsMap.put("012-002", R.drawable.ic_conger_eel_120px);
        iconsMap.put("012-003", R.drawable.ic_grunt_120px);
        iconsMap.put("012-004", R.drawable.ic_eel_120px);
        iconsMap.put("012-005", R.drawable.ic_sardine_120px);
        iconsMap.put("012-006", R.drawable.ic_bonito_120px);
        iconsMap.put("012-007", R.drawable.ic_barracuda_120px);
        iconsMap.put("012-008", R.drawable.ic_greater_amberjack_120px);
        iconsMap.put("012-009", R.drawable.ic_mackerel_120px);
        iconsMap.put("012-010", R.drawable.ic_pike_120px);
        iconsMap.put("012-011", R.drawable.ic_salmon_120px);
        iconsMap.put("012-012", R.drawable.ic_sea_bass_120px);
        iconsMap.put("012-013", R.drawable.ic_yellowtail_120px);
        iconsMap.put("012-014", R.drawable.ic_atka_mackerel_120px);
        iconsMap.put("012-015", R.drawable.ic_tuna_120px);
        iconsMap.put("012-016", R.drawable.ic_mexicali_120px);
        iconsMap.put("013-001", R.drawable.ic_olive_oil_120px);
        iconsMap.put("013-002", R.drawable.ic_ketchup_120px);
        iconsMap.put("013-003", R.drawable.ic_pepper_120px);
        iconsMap.put("013-004", R.drawable.ic_sesame_oil_120px);
        iconsMap.put("013-005", R.drawable.ic_consomme_120px);
        iconsMap.put("013-006", R.drawable.ic_suger_120px);
        iconsMap.put("013-007", R.drawable.ic_salada_oil_120px);
        iconsMap.put("013-008", R.drawable.ic_salt_120px);
        iconsMap.put("013-009", R.drawable.ic_seven_spice_blend_120px);
        iconsMap.put("013-010", R.drawable.ic_ginger_tube_120px);
        iconsMap.put("013-011", R.drawable.ic_say_soource_120px);
        iconsMap.put("013-012", R.drawable.ic_vinegar_120px);
        iconsMap.put("013-013", R.drawable.ic_source_120px);
        iconsMap.put("013-014", R.drawable.ic_dashi_120px);
        iconsMap.put("013-015", R.drawable.ic_doubanjiang_120px);
        iconsMap.put("013-016", R.drawable.ic_chicken_glass_base_120px);
        iconsMap.put("013-017", R.drawable.ic_garlic_tube_120px);
        iconsMap.put("013-018", R.drawable.ic_ponzu_sauce_120px);
        iconsMap.put("013-019", R.drawable.ic_mayonnaise_120px);
        iconsMap.put("013-020", R.drawable.ic_miso_120px);
        iconsMap.put("013-021", R.drawable.ic_sweet_sake_120px);
        iconsMap.put("013-022", R.drawable.ic_noodle_soup_120px);
        iconsMap.put("013-023", R.drawable.ic_yakiniku_sauce_120px);
        iconsMap.put("013-024", R.drawable.ic_cooking_sake_120px);
        iconsMap.put("014-001", R.drawable.ic_plain_bread_120px);
        iconsMap.put("014-002", R.drawable.ic_sweet_bread_120px);
        iconsMap.put("014-003", R.drawable.ic_side_dish_bread_120px);
        iconsMap.put("015-001", R.drawable.ic_atsuage_120px);
        iconsMap.put("015-002", R.drawable.ic_cup_noodles_120px);
        iconsMap.put("015-003", R.drawable.ic_potato_starch_120px);
        iconsMap.put("015-004", R.drawable.ic_milk_120px);
        iconsMap.put("015-005", R.drawable.ic_flour_120px);
        iconsMap.put("015-006", R.drawable.ic_konjac_120px);
        iconsMap.put("015-007", R.drawable.ic_jam_120px);
        iconsMap.put("015-008", R.drawable.ic_sausage_120px);
        iconsMap.put("015-009", R.drawable.ic_cheese_120px);
        iconsMap.put("015-010", R.drawable.ic_tofu_120px);
        iconsMap.put("015-011", R.drawable.ic_natto_120px);
        iconsMap.put("015-012", R.drawable.ic_chikuwa_120px);
        iconsMap.put("015-013", R.drawable.ic_dried_pasta_noodles_120px);
        iconsMap.put("015-014", R.drawable.ic_butter_120px);
        iconsMap.put("015-015", R.drawable.ic_honey_120px);
        iconsMap.put("015-016", R.drawable.ic_ham_120px);
        iconsMap.put("015-017", R.drawable.ic_bread_crumbs_120px);
        iconsMap.put("015-018", R.drawable.ic_bacon_120px);
        iconsMap.put("016-001", R.drawable.ic_potato_chips_120px);
        iconsMap.put("016-002", R.drawable.ic_chocolate_120px);
        iconsMap.put("016-003", R.drawable.ic_baked_confectionery_120px);
        iconsMap.put("016-004", R.drawable.ic_japanese_sweets_120px);
        iconsMap.put("016-005", R.drawable.ic_appetizers_120px);
        iconsMap.put("017-001", R.drawable.ic_ice_120px);
        iconsMap.put("017-002", R.drawable.ic_frozen_side_dishes_120px);
        iconsMap.put("017-003", R.drawable.ic_frozen_dumplings_120px);
        iconsMap.put("017-004", R.drawable.ic_frozen_fried_rice_120px);
        iconsMap.put("017-005", R.drawable.ic_hash_browns_120px);
        iconsMap.put("018-001", R.drawable.ic_beer_120px);
        iconsMap.put("018-002", R.drawable.ic_chuhai_120px);
        iconsMap.put("018-003", R.drawable.ic_wine_120px);
        iconsMap.put("018-004", R.drawable.ic_shochu_120px);
        iconsMap.put("018-005", R.drawable.ic_sake_120px);
        iconsMap.put("019-001", R.drawable.ic_aluminum_foil_120px);
        iconsMap.put("019-002", R.drawable.ic_clothes_detergent_120px);
        iconsMap.put("019-003", R.drawable.ic_trash_bags_120px);
        iconsMap.put("019-004", R.drawable.ic_conditioner_120px);
        iconsMap.put("019-005", R.drawable.ic_saran_wrap_120px);
        iconsMap.put("019-006", R.drawable.ic_shampoo_120px);
        iconsMap.put("019-007", R.drawable.ic_softener_120px);
        iconsMap.put("019-008", R.drawable.ic_edible_detergent_120px);
        iconsMap.put("019-009", R.drawable.ic_sponge_120px);
        iconsMap.put("019-010", R.drawable.ic_facial_wash_120px);
        iconsMap.put("019-011", R.drawable.ic_kitchen_detergent_120px);
        iconsMap.put("019-012", R.drawable.ic_tish_120px);
        iconsMap.put("019-013", R.drawable.ic_toilet_paper_120px);
        iconsMap.put("019-014", R.drawable.ic_tooth_paste_120px);
        iconsMap.put("019-015", R.drawable.ic_toothbrush_120px);
        iconsMap.put("019-016", R.drawable.ic_hand_soap_120px);
        iconsMap.put("019-017", R.drawable.ic_body_cream_120px);
        iconsMap.put("019-018", R.drawable.ic_body_soap_120px);
        iconsMap.put("019-019", R.drawable.ic_mask_120px);
        iconsMap.put("099-000", R.drawable.ic_other_item_120px);  // その他

        // 定数化
        ICON_MAP = Collections.unmodifiableMap(iconsMap);
    }
    /**
     * ShoppingItemのカテゴリIDとアイテムIDからアイコンリソースを取得
     */
    private int getIconResource(String categoryID, String itemID) {

        // 一致するカテゴリ名＋アイテム名が無ければ、リソースを「0」として返却する
        int resourceIconID = 0;
        String iconIDKey = categoryID + "-" + itemID;

        if(ICON_MAP.containsKey(iconIDKey)) {
            resourceIconID = ICON_MAP.get(iconIDKey);
        } else {
            // Todo エラー処理要
        }
        Log.d(TAG, "getIconResource() resourcePath: " + mContext.getResources().getResourceName(resourceIconID));
        return resourceIconID;
    }

    // =====================================================================
    // DB関連
    // =====================================================================

    private ArrayList<ShoppingItem> mTodayItemList = new ArrayList<>();
    private void setTodayItemList(ArrayList<ShoppingItem> todayItemList){
        mTodayItemList = todayItemList;
    }
    private ArrayList<ShoppingItem> getTodayItemList(){
        return mTodayItemList;
    }

    /**
     * MainTopActivity --> TodayItemActivityへの遷移か否か
     */
    private boolean isFromTop = true;
    public boolean getIsFromTop(){ return isFromTop; }
    public void setIsFromTop(boolean flag){ isFromTop = flag; }

    /**
     * 本日選択した商品をDBへ登録/更新
     */
    public void saveTodayShoppingItemList(ArrayList<ShoppingItem> todaylist, final boolean saveOnly){

        String strCurrentDate = getTodayDate();
        setIsFromTop(false);

        mShoppingItemDbUtility.insertShoppingList(strCurrentDate, todaylist, new SetShoppingHistoryCB() {
            @Override
            public void onFinish() {
                // 保存のみ：処理成功をユーザへ伝える
                if(saveOnly){
                    mDisplaySelectDBListener.displaySaveOnly();
                } else {
                    getTodayShoppingItemList();
                }
            }

            @Override
            public void onException() {
                // Todo エラー処理要
            }
        });
    }


    /**
     * DBから今日の日付と一致するShoppingItemを取得する
     * @return true:欠損なくすべて取得成功、 false:欠損あり/DB処理失敗
     */
    public void getTodayShoppingItemList() {
        Log.d(TAG, "getTodayShoppingItemList(): start");

        String strCurrentDate = getTodayDate();

        mShoppingItemDbUtility.selectAllItemFromDate(strCurrentDate, new GetShoppingHistoryCB() {
            // DBよりレコードを正常に取得できたとき
            @Override
            public void onFinish(ArrayList<ShoppingItem> todayFullList) {
                Log.d(TAG, "getTodayShoppingItemList(): onFinish");
                setTodayItemList(todayFullList);

                if(mTodayItemList == null || mTodayItemList.isEmpty()){
                    if(mDisplayTodayDBListener != null){
                        mDisplayTodayDBListener.displayNotYetMemo();
                    }

                    if(mDisplaySelectDBListener != null){
                        mDisplaySelectDBListener.displayNotYetMemo();
                    }
                } else {
                    for (ShoppingItem item : mTodayItemList){
                        item.setItemIconResource(getIconResource(item.getCategoryID(), item.getItemID()));
                    }

                    if(mDisplayTodayDBListener != null){
                        mDisplayTodayDBListener.displayTodayShoppingList(mTodayItemList);
                    }

                    if(mDisplaySelectDBListener != null){
                        mDisplaySelectDBListener.displayTodayShoppingList(mTodayItemList);
                    }
                }
            }

            // DBから取得したデータにNull値が含まれているとき
            @Override
            public void onLossData(ArrayList<ShoppingItem> todayPartList) {
                Log.d(TAG, "getTodayShoppingItemList(): onLossData");
                // Todo Null箇所のみ空白（検討要）
            }

            // DBからのデータ取得に失敗したとき
            @Override
            public void onException() {
                Log.d(TAG, "getTodayShoppingItemList(): onException");
                // Todo エラー処理
            }
        });
    }

    /**
     * 本日以外の、今までの買い物履歴日を取得する
     */
    public void getShoppingDateList(){
        Log.d(TAG, "getShoppingDateList(): start.");

        mShoppingItemDbUtility.getAllShoppingDateList(new GetShoppingDateCB() {
            @Override
            public void onFinish(HashSet<String> set) {
                Log.d(TAG, "getShoppingDateList() onFinish: start.");
                ArrayList<String> ret = new ArrayList<>(set);

                // 今日のレコードを削除
                try{
                    for(String date : ret){
                        if(date.equals(getTodayDate())){
                            ret.remove(date);
                        }
                    }
                } catch(ConcurrentModificationException e){
                    // 今日のレコードがリストの最後以外と最後から2番目以外にある場合は、
                    // java.util.ConcurrentModificationExceptionが発生する
                    // forの最中に、反復対象のリストを削除したため、イテレータのカーソル位置と
                    // サイズの関係により例外が発生
                    e.printStackTrace();
                } catch(Exception e){
                    e.printStackTrace();
                }

                // 日付が新しい順へ並べ替えして表示
                Collections.sort(ret, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o2.compareTo(o1);
                    }
                });
                mDisplayPastDateDBListener.displayPastDateList(ret);
            }

            @Override
            public void onNoDate() {
                Log.d(TAG, "getShoppingDateList() onNoDate: start.");
                mDisplayPastDateDBListener.displayNotYetShopping();
            }

            @Override
            public void onException() {
                Log.d(TAG, "getShoppingDateList() onException: start.");
                // Todo エラー処理要
            }
        });

        Log.d(TAG, "getShoppingDateList(): end.");
    }

    /**
     * 選択した過去の日付の買い物履歴を取得する
     * @return
     */
    public void getPastShoppingItemList(String date){
        Log.d(TAG, "getPastShoppingItemList(): start.");
        mShoppingItemDbUtility.selectAllItemFromDate(date, new GetShoppingHistoryCB() {
            @Override
            public void onFinish(ArrayList<ShoppingItem> fullList) {
                Log.d(TAG, "getPastShoppingItemList() onFinish: start.");

                if(fullList == null || fullList.isEmpty()) {
                    if (mDisplayPastDBListener != null) {
                        mDisplayPastDBListener.displayNotYetPastList();
                    }
                } else {
                    if (mDisplayPastDBListener != null) {
                        for (ShoppingItem item : fullList){
                            item.setItemIconResource(getIconResource(item.getCategoryID(), item.getItemID()));
                        }
                        mDisplayPastDBListener.displayPastShoppingList(fullList);
                    }
                }
            }

            @Override
            public void onLossData(ArrayList<ShoppingItem> partList) {
                Log.d(TAG, "getPastShoppingItemList() onLossData: start.");
            }

            @Override
            public void onException() {
                Log.d(TAG, "getPastShoppingItemList() onException: start.");
            }
        });
        Log.d(TAG, "getPastShoppingItemList(): end.");
    }


    // =====================================================================
    // その他
    // =====================================================================

    private String getTodayDate(){

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String strCurrentDate = format.format(currentDate);
        Log.d(TAG, "today is " + strCurrentDate + ".");

        return strCurrentDate;
    }
}
