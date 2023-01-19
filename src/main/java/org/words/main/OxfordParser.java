package org.words.main;

import lombok.SneakyThrows;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OxfordParser {
    private static final String BASE_URL = "https://www.oxfordlearnersdictionaries.com/definition/english/";
    private static final String PATH_TO_HTML_RESPONSE_FILE = "src/main/resources/Oxford_Http_Response.html";
    private static final Pattern EXAMPLE_PATTERN = Pattern.compile(
            "\\<span\\sclass\\=\"x\"\\>([a-zA-Z0-9 ,./?!'()\\-\"=<>‘’£$]+?)\\<\\/li\\>");

    @SneakyThrows
    public static void main(String[] args) {
        List<String> words = List.of("drink");
        for (String word : words) {
            connectToWebsite(word);
        }
    }

    @SneakyThrows
    public static String start(String wordQuery) {
        return connectToWebsite(wordQuery);
    }

    @SneakyThrows
    private static String connectToWebsite(String wordQuery) {
        URL url = new URL(BASE_URL + wordQuery.replace(' ', '-'));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(50000);
        connection.setReadTimeout(50000);
        return readContent(connection);
    }

    @SneakyThrows
    private static String readContent(HttpURLConnection connection) {
        if (connection.getResponseCode() == 404) {
            return "";
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine + "\n");
        }
        in.close();
        writeContentToFile(content.toString());
        return parseContent(content.toString());
    }

    private static String parseContent(String content) {
        Matcher exampleMatcher = EXAMPLE_PATTERN.matcher(content);
        List<String> examples = new ArrayList<>();
        while (exampleMatcher.find()) {
            String example = exampleMatcher.group(1).trim();
            char firstLetter = example.charAt(0);
            if (Character.isUpperCase(firstLetter)) {
                examples.add(example);
            }
        }
        return String.join(" | ", examples)
                .replace("</span>", "")
                .replace("<span class=\"cl\">", "")
                .replace("<span class=\"gloss\" htag=\"span\" hclass=\"gloss\">", "")
                .replace("<span class=\"gloss\" hclass=\"gloss\" htag=\"span\">", "");
    }

    @SneakyThrows
    private static void writeContentToFile(String content) {
        File file = new File(PATH_TO_HTML_RESPONSE_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        }
    }
}
