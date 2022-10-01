package com.example.remwords.download;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public class HttpUtil {

    private static final long TIME_OUT = 10000;

    private static final ConnectionPool mConnectionPool =
            new ConnectionPool(10, 30, TimeUnit.MINUTES);

    public static OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .callTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .connectionPool(mConnectionPool)
                .build();
    }
}
