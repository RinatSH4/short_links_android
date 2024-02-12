package com.example.graduationandroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    private static final String db_name = "graduation";
    private static final int db_version = 1;
    private static final String db_table = "short_links";
    private static final String db_column_link = "link";
    private static final String db_column_short_link = "short_link";

    public DB(@Nullable Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL);", db_table, db_column_link, db_column_short_link);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s", db_table);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertLink(String link, String short_link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_column_link, link);
        values.put(db_column_short_link, short_link);
        db.insertWithOnConflict(db_table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteLink(String short_link) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(db_table, db_column_short_link + "=?", new String[] {short_link});
        db.close();
    }

    public boolean isExistsShortLink(String short_link) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", db_table, db_column_short_link);
        Cursor cursor = db.rawQuery(query, new String[]{short_link});
        if(cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        } else {
            cursor.close();
            return false;
        }
    }

    public String findLink(String link) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT %s FROM %s WHERE %s = ?", db_column_link, db_table, db_column_short_link);

        Cursor cursor = db.rawQuery(query, new String[] {link});
        cursor.moveToFirst();
        @SuppressLint("Range") String full_link = cursor.getString(cursor.getColumnIndex(db_column_link));
        cursor.close();
        return full_link;
    }

    public ArrayList<String> getAllLinks() {
        ArrayList<String> allLinks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(db_table, new String[]{db_column_link, db_column_short_link}, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(db_column_short_link);
            allLinks.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        return allLinks;
    }
}
