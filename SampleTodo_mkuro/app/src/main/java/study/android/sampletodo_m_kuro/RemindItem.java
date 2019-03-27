package study.android.sampletodo_m_kuro;

import android.text.Editable;

class RemindItem {
    private String mName;
    private String mDate;
    private String mPlace;
    private String mMemo;

    private boolean mCompleteFlag;

    RemindItem(Editable name, Editable date, Editable place, Editable memo) {
        if (name != null) {
            mName = name.toString();
        }
        if (date != null) {
            mDate = date.toString();
        }
        if (place != null) {
            mPlace = place.toString();
        }
        if (memo != null) {
            mMemo = memo.toString();
        }
    }

    void setName(String name) {
        mName = name;
    }

    String getName() {
        return mName;
    }

    void setDate(String date) {
        mDate = date;
    }

    String getDate() {
        return mDate;
    }

    void setPlace(String place) {
        mPlace = place;
    }

    String getPlace() {
        return mPlace;
    }

    void setMemo(String memo) {
        mMemo = memo;
    }

    public String getMemo() {
        return mMemo;
    }

    void setComplete(boolean isComplete) {
        mCompleteFlag = isComplete;
    }

    boolean isComplete() {
        return mCompleteFlag;
    }
}
