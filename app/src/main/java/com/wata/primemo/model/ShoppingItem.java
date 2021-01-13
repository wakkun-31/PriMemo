package com.wata.primemo.model;

public class ShoppingItem implements Cloneable{

    /**
     * Field member
     * @param categoryID 品物カテゴリのID
     * @param categoryName 品物カテゴリ名
     * @param itemID カテゴリ内での追番
     * @param itemName 品名
     * @param itemIconResource 品名のアイコンリソース
     * @param itemNumber 品物の個数
     */
    private String categoryID = "";
    private String categoryName = "";
    private String itemID = "";
    private String itemName = "";
    private int itemIconResource = 0;
    private String itemNumber = "1";

    public String getCategoryID() { return categoryID; }
    public String getCategoryName() { return categoryName; }
    public String getItemID() { return itemID; }
    public String getItemName() { return itemName; }
    public int getItemIconResource() { return itemIconResource; }
    public String getItemNumber() { return itemNumber; }

    public void setCategoryID(String id) { this.categoryID = id; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setItemID(String id) { this.itemID = id; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemIconResource(int resource) { this.itemIconResource = resource; }
    public void setItemNumber(String number) { this.itemNumber = number; }

    public boolean isJsonNullCheck(){

        boolean isNull = false;
        if(categoryID == null || categoryName == null || itemID == null || itemName == null) {
            isNull = true;
        }
        return isNull;
    }

    @Override
    public ShoppingItem clone() {
        ShoppingItem ret = new ShoppingItem();
        try {
            ret = (ShoppingItem) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
