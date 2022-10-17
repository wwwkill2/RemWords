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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordListActivity extends AppCompatActivity {

    private Map<Integer, String> dayToGroup = new HashMap<>();

    {
        dayToGroup.put(Calendar.FRIDAY, "1~3");
        dayToGroup.put(Calendar.SATURDAY, "4~6");
        dayToGroup.put(Calendar.SUNDAY, "7~9");
        dayToGroup.put(Calendar.MONDAY, "10~12");
        dayToGroup.put(Calendar.TUESDAY, "13~15");
        dayToGroup.put(Calendar.WEDNESDAY, "16~18");
        dayToGroup.put(Calendar.THURSDAY, "19~21");
    }

    private int endGroupStartNum = 364;

    public static void startActivity(Context context, WordDetailActivity.Mode mode) {
        Intent intent = new Intent(context, WordListActivity.class);
        intent.putExtra(INTENT_MODE, mode);
        context.startActivity(intent);
    }

    private static final String INTENT_MODE = "mode";

    private WordDetailActivity.Mode mMode;
    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;
    private Button mBtnConfirm;
    private Button mBtnYesterday;
    private Button mBtnToday;
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
        mAdapter = new GroupAdapter(mGroupInfos);
        mRecyclerView.setAdapter(mAdapter);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnYesterday = findViewById(R.id.btn_yesterday);
        mBtnToday = findViewById(R.id.btn_today);
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
        mBtnYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                selectAllByDay(calendar);
            }
        });
        mBtnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                selectAllByDay(calendar);
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

    private void selectAllByDay(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Set<String> selectedGroups = new HashSet<>();
        String firstGroup = dayToGroup.get(dayOfWeek);
        int start = Integer.parseInt(firstGroup.split("~")[0]);
        int end = Integer.parseInt(firstGroup.split("~")[1]);
        while (start <= endGroupStartNum) {
            String group = start + "~" + end;
            selectedGroups.add(group);
            start += 21;
            end += 21;
        }
        for (GroupInfo info : mGroupInfos) {
            if (selectedGroups.contains(info.title)) {
                info.setSelected(true);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
