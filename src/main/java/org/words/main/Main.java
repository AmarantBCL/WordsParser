package org.words.main;

import lombok.SneakyThrows;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String PATH_TO_ORIGINAL_WORDS = "src/main/resources/Original_Words.json";
    private static final Pattern LEVEL_PATTERN = Pattern.compile("Level\":(\\d+)");
    private static final Pattern PART_OF_SPEECH_PATTERN = Pattern.compile("PartOfSpeech\":(\\d+)");
    private static final Pattern WORD_PATTERN = Pattern.compile("Name\":\"(.+?)\",\"Translation");
    private static final Pattern TRANSLATION_PATTERN = Pattern.compile("Translation\":\"(.+?)\",\"PartOfSpeech");
    private static final Pattern EXAMPLES_PATTERN = Pattern.compile("Examples\":\\[\\{\"Name\":\"(.+?)\",\"Indexes");

    public static void main(String[] args) {
        File file = new File(PATH_TO_ORIGINAL_WORDS);
        if (file.exists()) {
            readWordsFromJson(file);
        } else {
            System.out.println("File doesn't exist");
        }
    }

    @SneakyThrows
    public static void readWordsFromJson(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int id = 0;
            while ((line = br.readLine()) != null) {
                Matcher levelMatch = LEVEL_PATTERN.matcher(line);
                Matcher partOfSpeechMatch = PART_OF_SPEECH_PATTERN.matcher(line);
                levelMatch.find();
                partOfSpeechMatch.find();
                if (Integer.parseInt(levelMatch.group(1)) <= 1 &&
                        Integer.parseInt(partOfSpeechMatch.group(1)) < 7) {
                    Matcher wordMatch = WORD_PATTERN.matcher(line);
                    Matcher translationMatch = TRANSLATION_PATTERN.matcher(line);
                    wordMatch.find();
                    translationMatch.find();
//                    System.out.println(wordMatch.group(1).toUpperCase() + " (" + translationMatch.group(1) + ") [" + id + "]");
                    Matcher examplesMatch = EXAMPLES_PATTERN.matcher(line);
                    String androidStr = "";
                    while (examplesMatch.find()) {
//                        System.out.println(examplesMatch.group(1));
                        String level = levelMatch.group(1).equals("0") ? "A1" : "A2";
                        androidStr = "{\"examples\":[{\"sentence\":\"" + examplesMatch.group(1) + "\"}],\"id\":" + id +
                                ",\"level\":\"" + level + "\",\"name\":\"" + wordMatch.group(1) +
                                "\",\"progress\":-1,\"stage\":0,\"time\":0,\"translation\":\"" + translationMatch.group(1) + "\"},";
                    }
                    System.out.println(androidStr);
                    id++;
                }
            }
        }
    }
}
