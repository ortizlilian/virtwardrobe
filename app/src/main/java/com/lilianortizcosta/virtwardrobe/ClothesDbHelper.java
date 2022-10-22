package com.lilianortizcosta.virtwardrobe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClothesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Clothes.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ClothesContract.ClothesEntry.TABLE_NAME + " (" +
                    ClothesContract.ClothesEntry._ID + " INTEGER PRIMARY KEY," +
                    ClothesContract.ClothesEntry.COLUMN_NAME_TITLE + " TEXT, " +
                    ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE + " INTEGER, " +
                    ClothesContract.ClothesEntry.COLUMN_NAME_COUNT + " INTEGER, " +
                    ClothesContract.ClothesEntry.COLUMN_NAME_DATE + " DATE, " +
                    ClothesContract.ClothesEntry.COLUMN_NAME_CATEGORY + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ClothesContract.ClothesEntry.TABLE_NAME;

    public ClothesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
