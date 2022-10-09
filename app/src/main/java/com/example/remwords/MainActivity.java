package com.example.remwords;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.remwords.db.Word;
import com.example.remwords.db.WordDatabase;
import com.example.remwords.ui.WordDetailActivity;
import com.example.remwords.ui.WordListActivity;
import com.example.remwords.utils.SPUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WordDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new WordDatabase(this);
    }

    public void memWord(View view) {
        List<Word> todayUnfinishedWords = SPUtils.getTodayUnfinishedWords(this);
        if (todayUnfinishedWords == null || todayUnfinishedWords.isEmpty()) {
            WordListActivity.startActivity(this, WordDetailActivity.Mode.MEM);
        } else {
            new android.app.AlertDialog.Builder(this)
                    .setMessage("今天还有未完成的单词，继续吗？")
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        WordDetailActivity.startActivity(MainActivity.this, todayUnfinishedWords, WordDetailActivity.Mode.MEM);
                    })
                    .setNegativeButton("取消", (dialogInterface, i) -> {
                        SPUtils.storeTodayUnfinishedWords(MainActivity.this, new ArrayList<>());
                        WordListActivity.startActivity(this, WordDetailActivity.Mode.MEM);
                    }).show();
        }
    }

    public void importWord(View view) {
        List<Word> words = JSON.parseArray(getWordsJsonStringFromAsserts(), Word.class);
        WordDatabase wordDatabase = new WordDatabase(this);
        wordDatabase.insertOrUpdateWords(words);
    }

    public String getWordsJsonStringFromAsserts() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getResources().getAssets().open("words.txt"));
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {

        }
        return null;
    }

    public void importWordByText(View view) {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_import, null);
        final EditText etImport = dialogView.findViewById(R.id.et_import);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("文本导入")
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String jsonStr = etImport.getText().toString();
                        List<Word> words = JSON.parseArray(jsonStr, Word.class);
                        if (words == null || words.isEmpty()) {
                            return;
                        }
                        db.insertOrUpdateWords(words);
                    }
                })
                .setNegativeButton("取消", null);
        builder.show();
    }

    public void searchWord(View view) {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_import, null);
        final EditText etImport = dialogView.findViewById(R.id.et_import);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("搜单词")
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String keyword = etImport.getText().toString();
                        if (TextUtils.isEmpty(keyword)) {
                            return;
                        }
                        List<Word> words = db.searchWords(keyword);
                        if (words.isEmpty()) {
                            Toast.makeText(MainActivity.this, "未查询到相关词汇", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        WordDetailActivity.startActivity(MainActivity.this, words, WordDetailActivity.Mode.STUDY);
                    }
                })
                .setNegativeButton("取消", null);
        builder.show();
    }

    public void reviewWord(View view) {
        List<Word> words = db.queryAllForgetWords();
        if (words.isEmpty()) {
            Toast.makeText(this, "没有需要复习的单词", Toast.LENGTH_SHORT).show();
            return;
        }
        Collections.shuffle(words);
        WordDetailActivity.startActivity(this, words, WordDetailActivity.Mode.REVIEW);
    }

    public void studyWord(View view) throws IOException {
        WordListActivity.startActivity(this, WordDetailActivity.Mode.STUDY);
    }

    public void importantWord(View view) {
        List<Word> words = db.queryImportantWords();
        if (words.isEmpty()) {
            Toast.makeText(this, "没有需要重点记忆的单词", Toast.LENGTH_SHORT).show();
            return;
        }
        Collections.shuffle(words);
        WordDetailActivity.startActivity(this, words, WordDetailActivity.Mode.REVIEW);
    }
}