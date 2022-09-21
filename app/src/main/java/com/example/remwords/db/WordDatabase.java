package com.example.remwords.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class WordDatabase {

    private WordModel wordModel;
    private SQLiteDatabase mWritableDatabase;

    public WordDatabase(Context context) {
        wordModel = new WordModel(context);
        mWritableDatabase = wordModel.getWritableDatabase();
    }

    public void insertOrUpdateWords(List<Word> words) {
        for (Word word : words) {
            ContentValues cv = new ContentValues();
            cv.put(WordModel.COLUMN_WORD, word.word);
            cv.put(WordModel.COLUMN_TRANS, word.trans);
            cv.put(WordModel.COLUMN_KEY, word.key);
            mWritableDatabase.insertWithOnConflict(WordModel.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public List<Word> queryAllWords() {
        Cursor cursor = null;
        List<Word> res = new ArrayList<>();
        try {
            cursor = mWritableDatabase.rawQuery("select * from " + WordModel.TABLE_NAME + ";", new String[0]);
            while (cursor.moveToNext()) {
                res.add(this.queryWordFromCursor(cursor));
            }
        } finally {
            closeCursor(cursor);
        }
        return res;
    }

    public List<Word> queryAllForgetWords() {
        Cursor cursor = null;
        List<Word> res = new ArrayList<>();
        try {
            cursor = mWritableDatabase.rawQuery("select * from " + WordModel.TABLE_NAME + " where " +
                    WordModel.COLUMN_FORGET + "=1", new String[0]);
            while (cursor.moveToNext()) {
                res.add(this.queryWordFromCursor(cursor));
            }
        } finally {
            closeCursor(cursor);
        }
        return res;
    }

    public List<Word> queryWordsByGroupId(String groupId) {
        Cursor cursor = null;
        List<Word> res = new ArrayList<>();
        try {
            cursor = mWritableDatabase.rawQuery("select * from " + WordModel.TABLE_NAME
                    + " where " + WordModel.COLUMN_KEY + "=?"
                    + ";", new String[]{groupId});
            while (cursor.moveToNext()) {
                res.add(this.queryWordFromCursor(cursor));
            }
        } finally {
            closeCursor(cursor);
        }
        return res;
    }

    public List<String> queryAllGroups() {
        Cursor cursor = null;
        List<String> res = new ArrayList<>();
        try {
            cursor = mWritableDatabase.rawQuery("select distinct " + WordModel.COLUMN_KEY + " from " + WordModel.TABLE_NAME + ";", new String[0]);
            while (cursor.moveToNext()) {
                res.add(this.queryGroupIdFromCursor(cursor));
            }
        } finally {
            closeCursor(cursor);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            res.sort((s, t) -> {
                int a = Integer.parseInt(s.split("~")[0]);
                int b = Integer.parseInt(t.split("~")[0]);
                return Integer.compare(a, b);
            });
        }
        return res;
    }

    public List<Word> searchWords(String keyword) {
        Cursor cursor = null;
        List<Word> res = new ArrayList<>();
        try {
            cursor = mWritableDatabase.rawQuery("select * from " + WordModel.TABLE_NAME
                    + " where " + WordModel.COLUMN_WORD + " like " + "'%" + keyword + "%'", new String[0]);
            while (cursor.moveToNext()) {
                res.add(this.queryWordFromCursor(cursor));
            }
        } finally {
            closeCursor(cursor);
        }
        return res;
    }

    public void markForget(String word) {
        ContentValues cv = new ContentValues();
        cv.put(WordModel.COLUMN_FORGET, 1);
        mWritableDatabase.update(WordModel.TABLE_NAME, cv,
                WordModel.COLUMN_WORD + "=?", new String[]{word});
    }

    public void markRemember(String word) {
        ContentValues cv = new ContentValues();
        cv.put(WordModel.COLUMN_FORGET, 0);
        mWritableDatabase.update(WordModel.TABLE_NAME, cv,
                WordModel.COLUMN_WORD + "=?", new String[]{word});
    }

    private Word queryWordFromCursor(Cursor cursor) {
        String word = cursor.getString(cursor.getColumnIndexOrThrow(WordModel.COLUMN_WORD));
        String trans = cursor.getString(cursor.getColumnIndexOrThrow(WordModel.COLUMN_TRANS));
        String key = cursor.getString(cursor.getColumnIndexOrThrow(WordModel.COLUMN_KEY));
        return new Word(word, trans, key);
    }

    private String queryGroupIdFromCursor(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(WordModel.COLUMN_KEY));
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
