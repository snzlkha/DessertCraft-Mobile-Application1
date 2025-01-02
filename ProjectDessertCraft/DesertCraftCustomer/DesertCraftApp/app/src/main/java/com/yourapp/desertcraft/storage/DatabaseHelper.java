package com.yourapp.desertcraft.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "YourDatabaseName.db";
    public static final String TABLE_NAME = "carts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_DID = "did";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESC = "descrpt";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CUSTOMIZE = "customization";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_CREATED_ON = "created_on";

    public static final String TABLE_NAME_LIKE = "likes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_UID + " TEXT, "
                + COLUMN_DID + " TEXT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DESC + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_CUSTOMIZE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_PRICE + " REAL, "
                + COLUMN_QUANTITY + " INTEGER, "
                + COLUMN_CREATED_ON + " TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_LIKE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_DID + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LIKE);
        onCreate(db);
    }

    public void closeDatabase() {
        getWritableDatabase().close();
    }
}

