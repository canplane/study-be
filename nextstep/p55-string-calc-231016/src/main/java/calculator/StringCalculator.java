// p.55: Refactoring + TDD

package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }

    private String[] CustomSplit(String token) {
        // "//;\n2:3,5;7" -> { "1:3,5", "7" }
        String[] tokens = { token };
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(token);
        if (m.find()) {
            String customDelimiter = m.group(1);
            tokens = (m.group(2)).split(customDelimiter);
        }
        return tokens;
    }
    private String[] BasicSplit(String token) {
        // "1:3,5" -> { "1", "3", "5" }
        return token.split("[,:]");
    }
    private String[] BasicSplits(String[] tokens) {
        // { "1:3,5", "7" } -> { "1", "3", "5", "7" }
        List<String> li = new ArrayList<>();
        for (String token : tokens) {
            li.addAll(Arrays.asList(BasicSplit(token)));
        }
        return li.toArray(new String[0]);
    }
    private String[] split(String text) {
        return BasicSplits(CustomSplit(text));
    }

    private int toPositive(String token) throws RuntimeException {
        int number = Integer.parseInt(token);
        if (number < 0) {
            throw new RuntimeException("Negative number exception");
        }
        return number;
    }
    private int[] toInts(String[] tokens) throws RuntimeException {
        // { "1", "3", "5", "7" } -> { 1, 3, 5, 7 }
        int[] numbers = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            numbers[i] += toPositive(tokens[i]);
        }
        return numbers;
    }

    private int sum(int[] numbers) {
        // { 1, 3, 5, 7 } -> 16
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    int add(String text) throws RuntimeException {
        if (isBlank(text)) {
            return 0;
        }
        return sum(toInts(split(text)));
    }
}