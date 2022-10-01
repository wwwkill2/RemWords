package com.example.remwords.download;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;

public class DownloadTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private String mWord;

    public DownloadTask(Context context, String word) {
        mContext = context;
        mWord = word;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File file = new File(mContext.getExternalFilesDir(null), mWord + ".mp3");
        DownloadUtil.downloadFile(mWord, file);
        return null;
    }


}
