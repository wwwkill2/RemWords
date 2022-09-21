package com.example.remwords.utils;

import com.example.remwords.db.Word;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    public static List<Word> randomWords(List<Word> words) {
        Collections.shuffle(words, new Random());
        return words;
    }
}
