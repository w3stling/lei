package com.apptastic.lei;

import org.apache.commons.lang3.StringUtils;

/**
 * Class for validating LEI codes
 */
public final class LeiCodeValidator {

    /**
     * Validates if the formate of the LEI code is valid
     * @param leiCode LEI code
     * @return true if valid other wise false
     */
    public static boolean isValid(String leiCode) {
        return leiCode != null &&
                leiCode.length() == 20 &&
                StringUtils.isAlphanumeric(leiCode.substring(0, 17)) &&
                Character.isDigit(leiCode.charAt(18)) &&
                Character.isDigit(leiCode.charAt(19)) &&
                isChecksumValid(leiCode);
    }

    private static boolean isChecksumValid(String leiCode) {
        long m = 0;

        for (int i = 0; i < leiCode.length(); ++i) {
            char c = leiCode.charAt(i);
            if (c >= '0' && c <= '9') {
                m = (m * 10 + c - 48) % 97;
            }
            else {
                m = (m * 100 + c - 55) % 97;
            }
        }

        return m == 1;
    }
}
