package com.example.remwords.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WordModel extends SQLiteOpenHelper {

    public static final String DB_NAME = "words.db";
    public static final String TABLE_NAME = "word_table";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANS = "trans";
    public static final String COLUMN_KEY = "group_id";
    public static final String COLUMN_FORGET = "forget";
    public static final String COLUMN_FORGET_TIMES = "forget_times";

    public WordModel(@Nullable Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_WORD + " TEXT PRIMARY KEY, " +
                COLUMN_TRANS + " TEXT, " +
                COLUMN_FORGET + " INTEGER DEFAULT 0 NOT NULL, " +
                COLUMN_FORGET_TIMES + " INTEGER DEFAULT 0 NOT NULL, " +
                COLUMN_KEY + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_FORGET_TIMES + " INTEGER DEFAULT 0 NOT NULL;");
        }
    }
}
