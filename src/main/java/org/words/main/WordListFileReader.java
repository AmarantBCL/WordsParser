package org.words.main;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class WordListFileReader {
    private static String FILE_NAME = "src/main/resources/Words.txt";

    @SneakyThrows
    public static List<String> readWords() {
        BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));
        String inputLine;
        List<String> wordList = new ArrayList<>();
        while ((inputLine = in.readLine()) != null) {
            wordList.add(inputLine);
        }
        return wordList;
    }
}
