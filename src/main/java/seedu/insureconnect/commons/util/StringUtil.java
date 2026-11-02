package seedu.insureconnect.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.Arrays;

/**
 * Helper functions for handling strings.
 */
public final class StringUtil {
    private StringUtil() {
    }


    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = normalizeForComparison(word.trim());
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = normalizeForComparison(sentence);
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns true if any whitespace-separated word in {@code sentence} starts with {@code prefix}, ignoring case.
     *
     * @param sentence cannot be null
     * @param prefix cannot be null, cannot be empty, must be a single word; surrounding whitespace is ignored
     */
    public static boolean hasWordStartingWithIgnoreCase(String sentence, String prefix) {
        requireNonNull(sentence);
        requireNonNull(prefix);
        String trimmedPrefix = normalizeForComparison(prefix.trim());
        checkArgument(!trimmedPrefix.isEmpty(), "Prefix parameter cannot be empty");
        checkArgument(trimmedPrefix.split("\\s+").length == 1, "Prefix parameter should be a single word");
        String normalizedSentence = normalizeForComparison(sentence);
        return Arrays.stream(normalizedSentence.split("\\s+"))
            .anyMatch(word -> word.regionMatches(true, 0, trimmedPrefix, 0, trimmedPrefix.length()));
    }

    /**
     * Converts canonically equivalent Unicode text to the same representation for comparison.
     */
    private static String normalizeForComparison(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFC);
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
