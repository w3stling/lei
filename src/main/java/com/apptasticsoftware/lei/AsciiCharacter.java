package com.apptasticsoftware.lei;

/**
 * Utility class for checking ASCII character properties.
 */
public final class AsciiCharacter {

    private AsciiCharacter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if the given character is an ASCII digit (0-9).
     *
     * @param ch the character to check
     * @return true if the character is an ASCII digit, false otherwise
     */
    public static boolean isDigit(char ch) {
        return isDigit((int) ch);
    }


    /**
     * Checks if the given code point is an ASCII digit (0-9).
     *
     * @param codePoint the code point to check
     * @return true if the code point is an ASCII digit, false otherwise
     */
    public static boolean isDigit(int codePoint) {
        return codePoint >= '0' && codePoint <= '9';
    }

    /**
     * Checks if the given character is an ASCII letter (A-Z or a-z).
     *
     * @param c the character to check
     * @return true if the character is an ASCII letter, false otherwise
     */
    public static boolean isUpperCase(int c) {
        return c >= 'A' && c <= 'Z';
    }

}
