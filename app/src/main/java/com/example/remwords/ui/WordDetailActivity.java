package com.example.remwords.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.remwords.R;
import com.example.remwords.db.Word;
import com.example.remwords.db.WordDatabase;
import com.example.remwords.utils.SPUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public enum Mode implements Serializable {
        STUDY, MEM, REVIEW
    }

    private WordDatabase db;
    private TextView mTvProgress;
    private TextView mTvWord;
    private TextView mTvTrans;
    private Button mBtnPrev;
    private Button mBtnNext;
    private Button mBtnVague;
    private RelativeLayout mRlContent;
    private List<Word> mWords;
    private Mode mMode;
    private int mCurWordIndex = 0;

    public static void startActivity(Context context, List<Word> words, Mode mode) {
        Intent intent = new Intent(context, WordDetailActivity.class);
        ArrayList<Word> args;
        if (words instanceof ArrayList) {
            args = (ArrayList<Word>) words;
        } else {
            args = new ArrayList<>(words);
        }
        intent.putParcelableArrayListExtra(INTENT_ARGS, args);
        intent.putExtra(INTENT_MODE, mode);
        context.startActivity(intent);
    }

    private static final String INTENT_ARGS = "words";
    private static final String INTENT_MODE = "mode";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        initData();
        initViews();
        refreshViewByData();
    }

    private void initViews() {
        mTvProgress = findViewById(R.id.tv_progress);
        mTvWord = findViewById(R.id.tv_word);
        mTvTrans = findViewById(R.id.tv_trans);
        mBtnPrev = findViewById(R.id.btn_prev);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnVague = findViewById(R.id.btn_vague);
        mRlContent = findViewById(R.id.rl_content);
        mBtnPrev.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mRlContent.setOnClickListener(this);
        mBtnVague.setOnClickListener(this);
        if (mMode == Mode.STUDY) {
            mBtnVague.setVisibility(View.INVISIBLE);
        } else if (mMode == Mode.MEM) {
            mBtnVague.setVisibility(View.VISIBLE);
            mBtnVague.setText("模糊");
        } else {
            mBtnVague.setVisibility(View.VISIBLE);
            mBtnVague.setText("认识");
        }
    }

    private void initData() {
        db = new WordDatabase(this);
        ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(INTENT_ARGS);
        mWords = new ArrayList<>();
        for (Parcelable parcelable : list) {
            mWords.add((Word) parcelable);
        }
        mMode = (Mode) getIntent().getSerializableExtra(INTENT_MODE);
    }

    private void refreshViewByData() {
        mBtnPrev.setEnabled(mCurWordIndex != 0);
        mBtnNext.setEnabled(mCurWordIndex != mWords.size() - 1);
        Word curWord = mWords.get(mCurWordIndex);
        mTvWord.setText(curWord.word);
        mTvTrans.setText(curWord.trans);
        if (mMode == Mode.MEM || mMode == Mode.REVIEW) {
            mTvTrans.setVisibility(View.INVISIBLE);
        }
        mTvProgress.setText((mCurWordIndex + 1) + "/" + mWords.size());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prev:
                if (mCurWordIndex > 0) {
                    --mCurWordIndex;
                    refreshViewByData();
                }
                break;
            case R.id.btn_next:
                if (mCurWordIndex < mWords.size() - 1) {
                    ++mCurWordIndex;
                    refreshViewByData();
                }
                break;
            case R.id.rl_content:
                if (mMode == Mode.MEM || mMode == Mode.REVIEW) {
                    if (mTvTrans.getVisibility() == View.VISIBLE) {
                        mTvTrans.setVisibility(View.INVISIBLE);
                    } else {
                        mTvTrans.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.btn_vague:
                String word = mWords.get(mCurWordIndex).word;
                if (mMode == Mode.MEM) {
                    db.markForget(word);
                    mBtnNext.performClick();
                } else if (mMode == Mode.REVIEW) {
                    db.markRemember(word);
                    mBtnNext.performClick();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mMode == Mode.MEM && mCurWordIndex != mWords.size() - 1) {
            new AlertDialog.Builder(this)
                    .setMessage("还有剩余单词，想要保留进度吗？")
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        SPUtils.storeTodayUnfinishedWords(WordDetailActivity.this, mWords.subList(mCurWordIndex, mWords.size()));
                        super.onBackPressed();
                    })
                    .setNegativeButton("取消", (dialogInterface, i) -> {
                        SPUtils.storeTodayUnfinishedWords(WordDetailActivity.this, new ArrayList<>());
                        super.onBackPressed();
                    }).show();
        } else {
            super.onBackPressed();
        }
    }
}
