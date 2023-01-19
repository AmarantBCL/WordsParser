package org.words.main;

import lombok.SneakyThrows;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CambridgeParser {
    private static final List<String> WORD_LIST = WordListFileReader.readWords();
    private static final String CATEGORY = "CRIME";
    private static final String OUTPUT_STRING =
            "{\"categories\":[\"" + CATEGORY + "\"],\"examples\":[{\"sentence\":\"EXAMPLE\"}],\"id\":999,\"level\":\"A1\",\"name\":\"WORD\",\"progress\":-1,\"stage\":0,\"time\":0,\"translation\":\"ПЕРЕВОД\"},";
    private static final String PATH_TO_HTML_RESPONSE_FILE = "src/main/resources/Cambridge_Http_Response.html";
    private static final String PATH_TO_OUTPUT_CSV_FILE = "src/main/resources/Output_Csv_Words.csv";
    private static final String BASE_URL = "https://dictionary.cambridge.org/dictionary/english-russian/";
    private static final Pattern TRANSLATION_PATTERN = Pattern.compile(
            "trans dtrans dtrans\\-se\\s\"\\slang=\"ru\"\\>(.+)\\<\\/span\\>");

    @SneakyThrows
    public static void main(String[] args) {
        for (String word : WORD_LIST) {
            connectToWebsite(word);
        }
    }

    @SneakyThrows
    private static void connectToWebsite(String wordQuery) {
        URL url = new URL(BASE_URL + wordQuery.replace(' ', '-'));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(50000);
        connection.setReadTimeout(50000);
        readContent(connection, wordQuery);
    }

    @SneakyThrows
    private static void readContent(HttpURLConnection connection, String wordQuery) {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine + "\n");
        }
        in.close();
        writeContentToFile(content.toString());
        parseContent(content.toString(), wordQuery);
    }

    private static void parseContent(String content, String wordQuery) {
        Matcher translationMatcher = TRANSLATION_PATTERN.matcher(content);
        List<String> translations = new ArrayList<>();
        while (translationMatcher.find()) {
            String translation = translationMatcher.group(1).trim();
            translations.add(translation);
        }
        String allTranslations = String.join(" | ", translations);
        String allExamples = OxfordParser.start(wordQuery);
        String result = OUTPUT_STRING.replace("WORD", wordQuery)
                .replace("ПЕРЕВОД", allTranslations)
                .replace("EXAMPLE", allExamples);
        System.out.println(result);
        writeResultToCsvFile(result + "\n");
    }

    @SneakyThrows
    private static void writeContentToFile(String content) {
        File file = new File(PATH_TO_HTML_RESPONSE_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        }
    }

    @SneakyThrows
    private static void writeResultToCsvFile(String result) {
        File file = new File(PATH_TO_OUTPUT_CSV_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(result);
        }
    }
}
