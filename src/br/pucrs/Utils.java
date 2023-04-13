package br.pucrs;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static String[] buildPhrases(char[] originalContent, Integer size) {
        StringBuilder[] phrases = new StringBuilder[size];
        for (int i = 0; i < originalContent.length; i++) {
            int indexOf = i % size;
            if (phrases[indexOf] == null) {
                phrases[indexOf] = new StringBuilder(String.valueOf(originalContent[i]));
            } else {
                phrases[indexOf] = phrases[indexOf].append(originalContent[i]);
            }
        }
        return Arrays.stream(phrases).map(StringBuilder::toString).toArray(String[]::new);
    }

    public static Map<Character, Integer> generateLetterFrequency(String phrase) {
        Map<Character, Integer> lettersFrequency = Arrays.stream(ALPHABET.split(""))
                .collect(Collectors.toMap(it -> it.charAt(0), it -> 0));

        char[] letters = phrase.toCharArray();
        for (char letter : letters) {
            lettersFrequency.compute(letter, (k, v) -> v == null ? 1 : v + 1);
        }
        return lettersFrequency;
    }
}
