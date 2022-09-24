package com.example.remwords.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remwords.R;
import com.example.remwords.db.Word;
import com.example.remwords.db.WordDatabase;
import com.example.remwords.ui.adapter.GroupAdapter;
import com.example.remwords.ui.adapter.GroupInfo;
import com.example.remwords.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordListActivity extends AppCompatActivity {

    public static void startActivity(Context context, WordDetailActivity.Mode mode) {
        Intent intent = new Intent(context, WordListActivity.class);
        intent.putExtra(INTENT_MODE, mode);
        context.startActivity(intent);
    }

    private static final String INTENT_MODE = "mode";

    private WordDetailActivity.Mode mMode;
    private RecyclerView mRecyclerView;
    private Button mBtnConfirm;
    private List<GroupInfo> mGroupInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        initData();
        initViews();
    }

    private void initData() {
        mMode = (WordDetailActivity.Mode) getIntent().getSerializableExtra(INTENT_MODE);
        WordDatabase db = new WordDatabase(this);
        List<String> groups = db.queryAllGroups();
        mGroupInfos = new ArrayList<>();
        for (String group : groups) {
            List<Word> words = db.queryWordsByGroupId(group);
            StringBuilder sb = new StringBuilder();
            for (Word word : words) {
                sb.append(word.word).append(" ");
            }
            GroupInfo info = new GroupInfo(group, sb.toString());
            mGroupInfos.add(info);
        }
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.rv_word_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new GroupAdapter(mGroupInfos));
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Word> selectedWords = getSelectedWords();
                if (selectedWords.isEmpty()) {
                    Toast.makeText(WordListActivity.this, "未选择单词", Toast.LENGTH_SHORT).show();
                    return;
                }
                WordDetailActivity.startActivity(WordListActivity.this,
                        selectedWords, mMode);
            }
        });
    }

    private List<Word> getSelectedWords() {
        WordDatabase db = new WordDatabase(this);
        List<Word> res = new ArrayList<>();
        for (GroupInfo info : mGroupInfos) {
            if (info.isSelected()) {
                res.addAll(db.queryWordsByGroupId(info.title));
            }
        }
        if (mMode == WordDetailActivity.Mode.MEM) {
            Collections.shuffle(res);
        }
        return res;
    }
}
