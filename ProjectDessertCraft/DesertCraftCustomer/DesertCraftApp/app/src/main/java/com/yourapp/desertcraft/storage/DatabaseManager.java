package com.yourapp.desertcraft.storage;

import static com.yourapp.desertcraft.storage.DatabaseHelper.COLUMN_CREATED_ON;
import static com.yourapp.desertcraft.storage.DatabaseHelper.COLUMN_DID;
import static com.yourapp.desertcraft.storage.DatabaseHelper.COLUMN_ID;
import static com.yourapp.desertcraft.storage.DatabaseHelper.COLUMN_UID;
import static com.yourapp.desertcraft.storage.DatabaseHelper.TABLE_NAME_LIKE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Read all records with ordering by NAME DESC and limit to 10
    public Cursor getAllData() {
        return database.query(
                DatabaseHelper.TABLE_NAME,   // The table to query
                null,                        // The columns to return (null means all columns)
                null,                        // The columns for the WHERE clause
                null,                        // The values for the WHERE clause
                null,                        // Group the rows
                null,                        // Filter by row groups
                COLUMN_ID + " DESC", // The sort order
                "10"                         // The limit
        );
    }

    public Cursor getRecordById(long id) {
        String[] columns = {
                COLUMN_ID,
                COLUMN_UID,
                COLUMN_DID,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_DESC,
                DatabaseHelper.COLUMN_IMAGE,
                DatabaseHelper.COLUMN_CUSTOMIZE,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_PRICE,
                DatabaseHelper.COLUMN_QUANTITY,
                COLUMN_CREATED_ON
        };
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return database.query(DatabaseHelper.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    // Update some data with id
    public int updateRecord(long id, double price, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRICE, price);
        values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        return database.update(DatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
    }

    public long updateRecordById(int id, String uid, String did, String name, String desc, String image, String customization, String date, double price, int quantity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, uid);
        values.put(COLUMN_DID, did);
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_DESC, desc);
        values.put(DatabaseHelper.COLUMN_IMAGE, image);
        values.put(DatabaseHelper.COLUMN_CUSTOMIZE, customization);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_PRICE, price);
        values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);

        // Define the where clause for updating the record with the given ID
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };

        // Perform the update operation
        return database.update(DatabaseHelper.TABLE_NAME, values, whereClause, whereArgs);
    }



    // Delete
    public int deleteRecord(long id) {
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return database.delete(DatabaseHelper.TABLE_NAME, selection, selectionArgs);
    }


    public Cursor getExistingRecord(String name, String desc, String customization, String date) {
        String selection = DatabaseHelper.COLUMN_NAME + " = ? AND " +
                DatabaseHelper.COLUMN_DESC + " = ? AND " +
                DatabaseHelper.COLUMN_CUSTOMIZE + " = ? AND " +
                DatabaseHelper.COLUMN_DATE + " = ?";
        String[] selectionArgs = {name, desc, customization, date};

        return database.query(
                DatabaseHelper.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public long insertOrUpdateRecord(String uid, String did, String name, String desc, String image, String customization, String date, double price, int quantity) {

        Cursor cursor = getExistingRecord(name, desc, customization, date);

        if (cursor != null && cursor.moveToFirst()) {
            // Record exists, update it
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            long id = cursor.getLong(idIndex);
            cursor.close();

            return updateRecord(id, price, quantity);
        } else {
            // Record does not exist, insert new
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String createdOn = currentDateTime.format(formatter);

                ContentValues values = new ContentValues();
                values.put(COLUMN_UID, uid);
                values.put(COLUMN_DID, did);
                values.put(DatabaseHelper.COLUMN_NAME, name);
                values.put(DatabaseHelper.COLUMN_DESC, desc);
                values.put(DatabaseHelper.COLUMN_IMAGE, image);
                values.put(DatabaseHelper.COLUMN_CUSTOMIZE, customization);
                values.put(DatabaseHelper.COLUMN_DATE, date);
                values.put(DatabaseHelper.COLUMN_PRICE, price);
                values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
                values.put(COLUMN_CREATED_ON, createdOn);

                return database.insert(DatabaseHelper.TABLE_NAME, null, values);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return -1;
    }

    public boolean isPostInFavorites(int did) {
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME_LIKE + " WHERE " + COLUMN_DID + " = ?";
        String[] selectionArgs = {String.valueOf(did)};
        Cursor cursor = database.rawQuery(query, selectionArgs);
        boolean postExists = cursor.getCount() > 0;
        cursor.close();
        return postExists;
    }

    public long setLike(int did) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DID, did);
        return database.insert(TABLE_NAME_LIKE, null, values);
    }

    public int setUnLike(int did) {
        String selection = COLUMN_DID + " = ?";
        String[] selectionArgs = {String.valueOf(did)};
        return database.delete(TABLE_NAME_LIKE, selection, selectionArgs);
    }
}

