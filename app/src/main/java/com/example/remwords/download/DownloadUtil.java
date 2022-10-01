package com.example.remwords.download;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtil {

    private static final String URL_PREFIX = "https://dict.youdao.com/dictvoice?type=0&audio=";

    public static void downloadFile(String word, File file) {
        try {
            String url = URL_PREFIX + word;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = HttpUtil.getHttpClient().newCall(request).execute();
            byte[] bytes = response.body().bytes();
            Files.write(file.toPath(), bytes, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
