package com.example.remwords.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.example.remwords.db.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SPUtils {

    public static final String SP_NAME = "TODAY_UNFINISHED";

    public static List<Word> getTodayUnfinishedWords(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String json = sp.getString(today, null);
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(json, Word.class);
    }

    public static void storeTodayUnfinishedWords(Context context, List<Word> words) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        editor.putString(today, JSON.toJSONString(words));
        editor.apply();
    }

}
