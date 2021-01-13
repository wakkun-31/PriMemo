package com.wata.primemo.model;

public class DateItem implements Cloneable{

    /**
     * Field member
     * @param dateIconResource 日付アイコン
     * @param dateId 日付Id
     * @param date 日付
     * @param location 購入場所
     */

    // Todo (今後)アイコンや場所項目追加で変更要
//    private int dateIconResource = 0;
    private long dateId = 0;
    private String date = "";
//    private String location = "";

//    public String getLocation() { return location; }
    public long getDateId() { return dateId; }
    public String getDate() { return date; }
//    public int getDateIconResource() { return dateIconResource; }

//    public void setLocation(String id) { this.location = id; }
    public void setDateId(long dateId) { this.dateId = dateId; }
    public void setDate(String date) { this.date = date; }
//    public void setDateIconResource(int resource) { this.dateIconResource = resource; }

    @Override
    public DateItem clone() {
        DateItem ret = new DateItem();
        try {
            ret = (DateItem) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
