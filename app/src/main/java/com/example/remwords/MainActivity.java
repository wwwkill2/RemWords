package com.example.remwords;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.example.remwords.db.Word;
import com.example.remwords.db.WordDatabase;
import com.example.remwords.ui.WordDetailActivity;
import com.example.remwords.ui.WordListActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void memWord(View view) {
        WordDatabase wordDatabase = new WordDatabase(this);
        List<Word> words = wordDatabase.queryAllWords();
//        WordDetailActivity.startActivity(this, words.subList(0, 2), WordDetailActivity.Mode.MEM);
        WordListActivity.startActivity(this, WordDetailActivity.Mode.MEM);
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
                        WordDatabase wordDatabase = new WordDatabase(MainActivity.this);
                        wordDatabase.insertOrUpdateWords(words);
                    }
                })
                .setNegativeButton("取消", null);
        builder.show();
    }
}