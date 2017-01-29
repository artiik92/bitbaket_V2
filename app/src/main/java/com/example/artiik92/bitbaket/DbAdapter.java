package com.example.artiik92.bitbaket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by artiik92 on 28.01.2017.
 */

public class DbAdapter {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    private static final String DATABASE_NAME = "rssReaderDatabase";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CHANNELS = "channels";
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";

    public static final String TABLE_ARTICLES = "articles";
    public static final String KEY_A_ID = "_id";
    public static final String KEY_CHANNEL_ID = "chid";
    public static final String KEY_A_TITLE = "title";
    public static final String KEY_A_URL = "url";
    public static final String KEY_A_DESCRIPTION = "description";
    public static final String KEY_A_DATE = "date";

    private static final String INIT_CHANNELS =
            "CREATE TABLE " + TABLE_CHANNELS + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TITLE + " TEXT NOT NULL, "
                    + KEY_URL + " TEXT NOT NULL)";

    private static final String INIT_ARTICLES =
            "CREATE TABLE " + TABLE_ARTICLES + " (" + KEY_A_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CHANNEL_ID + " INTEGER NOT NULL, " + KEY_A_TITLE + " TEXT NOT NULL, "
                    + KEY_A_URL + " TEXT NOT NULL, " + KEY_A_DESCRIPTION + " TEXT NOT NULL, " + KEY_A_DATE + " TEXT NOT NULL)";

    private static class DbHelper extends SQLiteOpenHelper {

        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INIT_CHANNELS);
            db.execSQL(INIT_ARTICLES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
            onCreate(db);
        }

    }

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open() {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
        dbHelper.close();
    }

    public Cursor getAllChannels() {
        return db.query(TABLE_CHANNELS, new String[] {KEY_ID, KEY_TITLE, KEY_URL},
                null, null, null, null, null);
    }

    public Cursor getAllArticles(int channelId) {
        return db.query(TABLE_ARTICLES, new String[] {KEY_A_ID, KEY_A_TITLE, KEY_A_URL, KEY_A_DESCRIPTION, KEY_A_DATE},
                KEY_CHANNEL_ID + " = " + channelId, null, null, null, null);
    }

    public void addChannel(String title, String url) {
        ContentValues content = new ContentValues();
        content.put(KEY_TITLE, title);
        content.put(KEY_URL, url);
        db.insert(TABLE_CHANNELS, null, content);
    }

    public void addArticle(int channelId, String title, String url, String description, String date) {
        ContentValues content = new ContentValues();
        content.put(KEY_CHANNEL_ID, channelId);
        content.put(KEY_A_TITLE, title);
        content.put(KEY_A_URL, url);
        content.put(KEY_A_DESCRIPTION, description);
        content.put(KEY_A_DATE, date);
        db.insert(TABLE_ARTICLES, null, content);
    }

    public void deleteChannel(int id) {
        deleteArticles(id);
        db.delete(TABLE_CHANNELS, KEY_A_ID + " = " + id, null);
    }

    public void deleteArticles(int channelId) {
        db.delete(TABLE_ARTICLES, KEY_CHANNEL_ID + "=" + channelId, null);
    }

}
