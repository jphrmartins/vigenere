package br.pucrs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LanguageDetector {
    private static final Integer SCALE = 10;
    private static final Integer BATCH_SIZE = 16;

    public static LanguageKeyDetected detect(String fileContent) {
        List<String[]> batchs = generateBatchs(fileContent);
        System.out.println("\n\n");
        return batchs.stream()
                .map(LanguageDetector::calculateCoincidenceIndexBatch)
                .filter(it -> !it.language().equals(Language.UNKW))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not determinate language"));
    }

    private static LanguageKeyDetected calculateCoincidenceIndexBatch(String[] batches) {
        System.out.printf("%s: ", batches.length);
        List<BigDecimal> allOfIt = new ArrayList<>();
        for (String batch : batches) {
            BigDecimal ci = calculateCoincidenceIndex(batch);
            allOfIt.add(ci);
            System.out.print("[" + ci.toPlainString() + "] ");
        }
        BigDecimal avg = allOfIt.stream()
                .reduce(new BigDecimal("0"), BigDecimal::add)
                .divide(new BigDecimal(allOfIt.size()), SCALE, RoundingMode.HALF_EVEN);
        Language language = Language.indentifyLanguage(avg.doubleValue());
        System.out.println("-> " + avg + " = " + language.name());
        return new LanguageKeyDetected(language, batches.length);
    }

    private static List<String[]> generateBatchs(String fileContent) {
        char[] spitContent = fileContent.toCharArray();
        List<String[]> batch = new ArrayList<>();
        batch.add(new String[]{fileContent});
        for (int i = 2; i <= BATCH_SIZE; i++) {
            String[] phrases = Utils.buildPhrases(spitContent, i);
            batch.add(phrases);
        }
        return batch;
    }

    private static BigDecimal calculateCoincidenceIndex(String phrase) {
        Map<Character, Integer> lettersFrequency = Utils.generateLetterFrequency(phrase);
        BigDecimal phraseSize = new BigDecimal(phrase.length());
        BigDecimal sum = lettersFrequency.values().stream()
                .filter(integer -> integer > 0)
                .map(it -> new BigDecimal(String.valueOf(it)))
                .reduce(new BigDecimal("0"), (acc, cur) -> {
                    BigDecimal multiplicand = cur.subtract(new BigDecimal("1"));
                    BigDecimal value = cur.multiply(multiplicand);
                    return acc.add(value);
                });
        BigDecimal divisor = phraseSize.multiply(phraseSize.subtract(new BigDecimal(1)));
        return sum.divide(divisor, SCALE, RoundingMode.HALF_EVEN);
    }
}
