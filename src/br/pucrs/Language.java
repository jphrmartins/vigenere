package br.pucrs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Language {
    EN(List.of('e', 't', 'a', 'o'), 0.066, 0.068),
    BR(List.of('a', 'e', 'o', 's'), 0.080, 0.082),
    UNKW(Collections.emptyList(), 1, -1);

    private final List<Character> mostFrequentLetters;
    private final double lowerBounder;
    private final double upperBounder;

    Language(List<Character> mostFrequentLetters, double lowerBounder, double upperBounder) {
        this.mostFrequentLetters = mostFrequentLetters;
        this.lowerBounder = lowerBounder;
        this.upperBounder = upperBounder;
    }

    public List<Character> getMostFrequentLetters() {
        return mostFrequentLetters;
    }

    public List<Character> getTopMostFrequentLetters() {
        return mostFrequentLetters.subList(0,2);
    }

    public static Language indentifyLanguage(double coincidenceIndex) {
        return Arrays.stream(values())
                .filter(it -> it.lowerBounder <= coincidenceIndex && coincidenceIndex <= it.upperBounder)
                .findFirst()
                .orElse(Language.UNKW);
    }

}
