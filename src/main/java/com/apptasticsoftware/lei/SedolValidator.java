package com.apptasticsoftware.lei;

import java.util.Set;

public final class SedolValidator {
    private static final int[] WEIGHT = new int[] {1, 3, 1, 7, 3, 9, 1};
    private static final Set<Character> UPPERCASE_CONSONANTS = Set.of('B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z');

    private SedolValidator() {

    }

    /**
     * Validates if the format of the SEDOL code is valid
     * @param sedol SEDOL
     * @return true if valid otherwise false
     */
    public static boolean isValid(String sedol) {
        return sedol != null &&
               sedol.length() == 7 &&
               Character.isDigit(sedol.codePointAt(6)) &&
               isUppercaseAlphanumeric(sedol) &&
               isCheckDigitValid(sedol);
    }

    private static boolean isUppercaseAlphanumeric(final CharSequence cs) {
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            char c = cs.charAt(i);
            if (!((Character.isUpperCase(c) && isConsonant(c)) || Character.isDigit(c))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isConsonant(char c){
        return UPPERCASE_CONSONANTS.contains(c);
    }

    static boolean isCheckDigitValid(String sedol) {
        return sedol.codePointAt(6) - 48 == calculateCheckDigit(sedol);
    }

    static int calculateCheckDigit(String sedol) {
        int sum = 0;

        for (int i = 0; i < sedol.length()-1; ++i) {
            sum += WEIGHT[i] * Character.digit(sedol.charAt(i), 36);
        }

        return (10 - sum % 10) % 10;
    }
}
