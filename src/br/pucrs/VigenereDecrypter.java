package br.pucrs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VigenereDecrypter {
    public static void main(String[] args) {
        List<FileWithIndex> files = IntStream.range(0,32)
                .mapToObj(i -> new FileWithIndex("./textosCifrados/cipher" + i + ".txt", i))
                .toList();

        files.forEach(file -> {
            String fileContent = getFileContent(file.filePath());
            LanguageKeyDetected languageKeyDetected = LanguageDetector.detect(fileContent);
            System.out.println("File " + file + " has language of: " + languageKeyDetected);
            List<String> possibleKeys = KeyDetector.findPossibleKeys(fileContent, languageKeyDetected);
            possibleKeys.stream()
                    .collect(Collectors.toMap(Function.identity(), key -> decrypt(key, fileContent)))
                    .entrySet()
                    .stream()
                    .filter(e -> Utils.generateLetterFrequency(e.getValue())
                            .entrySet().stream()
                            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                            .map(Map.Entry::getKey)
                            .limit(4)
                            .allMatch(languageKeyDetected.language().getMostFrequentLetters()::contains))
                    .findFirst()
                    .ifPresentOrElse(keyClearText -> {
                        System.out.println("File: " + file + " Has Key: " + keyClearText.getKey());
                        writeInto(keyClearText.getValue(), "./decrypted/decript-" + file.index() + ".txt");
                    }, () -> System.out.println("Could not break cryptographic for file: " + file));
        });
    }

    private static String decrypt(String key, String cryptText) {
        char[] splitKey = key.toCharArray();
        char[] cryptTextChar = cryptText.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cryptTextChar.length; i += key.length()) {
            for (int k = 0; k < key.length(); k++) {
                if (i + k == cryptTextChar.length) break;
                builder.append(decryptChar(cryptTextChar[i + k], splitKey[k]));
            }
        }
        return builder.toString();
    }

    private static char decryptChar(char cryptLetter, char keyLetter) {
        return (char) ((cryptLetter - keyLetter + 26) % 26 + 'a');
    }

    private static String getFileContent(String filePath) {
        File file = new File(filePath);
        try {
            var content = Files.readAllLines(file.toPath());
            return String.join("", content);
        } catch (IOException e) {
            System.err.println("Error trying to get file: " + filePath);
            throw new RuntimeException(e);
        }
    }

    private static void writeInto(String text, String filePath) {
        File file = new File(filePath);
        try {
            Files.writeString(file.toPath(), text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}