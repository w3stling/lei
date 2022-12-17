package com.apptasticsoftware.lei;

public final class CusipValidator {

    private CusipValidator() {

    }

    /**
     * Validates if the format of the CUSIP code is valid
     * @param cusip CUSIP
     * @return true if valid otherwise false
     */
    public static boolean isValid(String cusip) {
        return cusip != null &&
               cusip.length() == 9 &&
               Character.isDigit(cusip.codePointAt(8)) &&
               isUppercaseAlphanumeric(cusip) &&
               isCheckDigitValid(cusip);
    }

    private static boolean isUppercaseAlphanumeric(final CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }

        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            char c = cs.charAt(i);
            if (!(Character.isUpperCase(c) || Character.isDigit(c))) {
                return false;
            }
        }

        return true;
    }

    static boolean isCheckDigitValid(String cusip) {
        return cusip.codePointAt(8) - 48 == calculateCheckDigit(cusip.substring(0, 8));
    }

    static int calculateCheckDigit(String cusip) {
        int sum = 0;

        for (int p = 1; p < cusip.length(); ++p) {
            int value = 0;
            int c = cusip.codePointAt(p-1);
            if (Character.isDigit(c)) {
                value = c - 48;
            } else if (Character.isUpperCase(c)) {
                value = c - 64 + 9;
            } else if (c == '*') {
                value = 36;
            } else if (c == '@') {
                value = 37;
            } else if (c == '#') {
                value = 38;
            }

            if (p % 2 == 0) {
                value = value * 2;
            }

            sum += (value / 10) + (value % 10);
        }

        return (10 - (sum % 10)) % 10;
    }
}
