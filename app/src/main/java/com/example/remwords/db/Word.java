package com.example.remwords.db;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {
    public String word;
    public String trans;
    public String key;

    public Word(String word, String trans, String key) {
        this.word = word;
        this.trans = trans;
        this.key = key;
    }

    public Word() {
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", trans='" + trans + '\'' +
                ", key='" + key + '\'' +
                '}';
    }

    public Word(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        this.word = data[0];
        this.trans = data[1];
        this.key = data[2];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.word, this.trans, this.key});
    }
}
