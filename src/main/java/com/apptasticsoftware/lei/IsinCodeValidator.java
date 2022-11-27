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

import java.util.Arrays;
import java.util.Locale;

/**
 * Class for validating ISIN codes
 */
public final class IsinCodeValidator {

    private static final String [] COUNTRY_CODES = Locale.getISOCountries();

    private static final String [] SPECIALS_COUNTRY_CODES = {
            "EZ", // http://www.anna-web.org/standards/isin-iso-6166/
            "XS", // https://www.isin.org/isin/
    };

    static {
        Arrays.sort(COUNTRY_CODES);   // we cannot assume the codes are sorted
        Arrays.sort(SPECIALS_COUNTRY_CODES); // Just in case ...
    }


    private IsinCodeValidator() {

    }

    /**
     * Validates if the format of the ISIN code is valid
     * @param isinCode ISIN code
     * @return true if valid otherwise false
     */
    public static boolean isValid(String isinCode) {
        return isinCode != null &&
               isinCode.length() == 12 &&
               Character.isUpperCase(isinCode.charAt(0)) &&
               Character.isUpperCase(isinCode.charAt(1)) &&
               Character.isDigit(isinCode.charAt(11)) &&
               isUppercaseAlphanumeric(isinCode.substring(2, 11)) &&
               isCountryCode(isinCode.substring(0, 2)) &&
               isChecksumValid(isinCode);
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

    private static boolean isCountryCode(String countryCode) {
        return Arrays.binarySearch(COUNTRY_CODES, countryCode) >= 0 ||
               Arrays.binarySearch(SPECIALS_COUNTRY_CODES, countryCode) >= 0;
    }

    static boolean isChecksumValid(String isinCode) {
        return (isinCode.charAt(11) - 48) == calculateChecksum(isinCode);
    }

    static int calculateChecksum(String isinCode) {
        int[] odd = new int[24];
        int[] even = new int[24];
        int index = 0;
        int oddIndex = 0;
        int evenIndex = 0;
        boolean isRightMostCharacterOdd = false;

        for (int i = 0; i < isinCode.length() - 1; ++i) {
            int c = isinCode.charAt(i);
            if (Character.isUpperCase(c)) {
                c = c - 55;
                if (add(c / 10, index, oddIndex, odd, evenIndex, even)) {
                    ++oddIndex;
                } else {
                    ++evenIndex;
                }
                ++index;

                if (add(c % 10, index, oddIndex, odd, evenIndex, even)) {
                    ++oddIndex;
                    isRightMostCharacterOdd = true;
                } else {
                    ++evenIndex;
                    isRightMostCharacterOdd = false;
                }
                ++index;

            } else {
                c = c - 48;
                if (add(c, index, oddIndex, odd, evenIndex, even)) {
                    ++oddIndex;
                    isRightMostCharacterOdd = true;
                } else {
                    ++evenIndex;
                    isRightMostCharacterOdd = false;
                }
                ++index;
            }
        }

        int totalSum = 0;
        if (isRightMostCharacterOdd) {
            for (int i = 0; i <= oddIndex; i++) {
                odd[i] = odd[i] * 2;
                totalSum += sum(odd[i]);
            }
            for (int i = 0; i <= evenIndex; i++) {
                totalSum += sum(even[i]);
            }
        } else {
            for (int i = 0; i <= evenIndex; i++) {
                even[i] = even[i] * 2;
                totalSum += sum(even[i]);
            }
            for (int i = 0; i <= oddIndex; i++) {
                totalSum += sum(odd[i]);
            }
        }

        return (10 - (totalSum % 10)) % 10;
    }

    private static boolean add(int value, int index, int indexOdd, int[] odd, int indexEven, int[] even) {
        if ((index & 0x1) == 1) {
            odd[indexOdd] = value;
            return true;
        } else {
            even[indexEven] = value;
            return false;
        }
    }

    private static int sum(int value) {
        return value / 10 + value % 10;
    }
}
