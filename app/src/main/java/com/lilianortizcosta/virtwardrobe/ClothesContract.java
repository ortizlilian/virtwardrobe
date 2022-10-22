package com.lilianortizcosta.virtwardrobe;

import android.provider.BaseColumns;

public final class ClothesContract {
    private ClothesContract() {}

    public static class ClothesEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_FAVOURITE = "favourite";
        public static final String COLUMN_NAME_COUNT = "count";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CATEGORY = "category";
    }
}
