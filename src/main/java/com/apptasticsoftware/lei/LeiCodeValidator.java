/*
 * MIT License
 *
 * Copyright (c) 2022, Apptastic Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.apptasticsoftware.lei;

/**
 * Class for validating LEI codes
 */
public final class LeiCodeValidator {

    private LeiCodeValidator() {

    }

    /**
     * Validates if the format of the LEI code is valid
     * @param leiCode LEI code
     * @return true if valid otherwise false
     */
    public static boolean isValid(String leiCode) {
        return leiCode != null &&
                leiCode.length() == 20 &&
                Character.isDigit(leiCode.charAt(18)) &&
                Character.isDigit(leiCode.charAt(19)) &&
                isUppercaseAlphanumeric(leiCode.substring(0, 17)) &&
                isChecksumValid(leiCode);
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
