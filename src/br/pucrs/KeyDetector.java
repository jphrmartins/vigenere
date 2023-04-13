package br.pucrs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyDetector {

    private static final int FIRST_LETTER = 'a';

    public static List<String> findPossibleKeys(String fileContent, LanguageKeyDetected languageKeyDetected) {
        char[] splitContent = fileContent.toCharArray();
        String[] texts = Utils.buildPhrases(splitContent, languageKeyDetected.keySize());
        Character[] cryptKey = new Character[languageKeyDetected.keySize()];
        for (int i = 0; i < languageKeyDetected.keySize(); i++) {
            String phrase = texts[i];
            List<Character> letter = Utils.generateLetterFrequency(phrase)
                    .entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    //.peek((e) -> System.out.println(e.getValue() + " -> " + e.getKey()))
                    .map(Map.Entry::getKey)
                    .toList();
            cryptKey[i] = letter.get(0);
        }
        List<String> keys = languageKeyDetected.language().getTopMostFrequentLetters().stream()
                .map(letter -> Arrays.stream(cryptKey)
                        .map(it -> (char) (((it - letter + 26) % 26) + FIRST_LETTER))
                        .map(String::valueOf)
                        .collect(Collectors.joining()))
                .toList();

       System.out.println("PossibleKeys: " + keys);

        return keys;
    }
}
