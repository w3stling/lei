package com.apptasticsoftware.lei;

import java.util.Arrays;
import java.util.Locale;

/**
 * Class for validating BIC codes
 */
public final class BicCodeValidator {

    private static final String [] COUNTRY_CODES = Locale.getISOCountries();

    private static final String [] SPECIALS_COUNTRY_CODES = {
            "EZ", // http://www.anna-web.org/standards/isin-iso-6166/
            "XS", // https://www.isin.org/isin/
    };

    static {
        Arrays.sort(COUNTRY_CODES);   // we cannot assume the codes are sorted
        Arrays.sort(SPECIALS_COUNTRY_CODES); // Just in case ...
    }

    private BicCodeValidator() {

    }

    /**
     * Validates if the format of the BIC code is valid
     * @param bicCode BIC code
     * @return true if valid otherwise false
     */
    public static boolean isValid(String bicCode) {
        var valid = bicCode != null &&
               (bicCode.length() == 8 || bicCode.length() == 11) &&
               isUppercase(bicCode.substring(0, 4)) &&
               isCountryCode(bicCode.substring(4, 6)) &&
               isUppercaseAlphanumeric(bicCode.substring(6, 8));

        if (valid && bicCode.length() == 11) {
            valid = isUppercaseAlphanumeric(bicCode.substring(8, 11));
        }

        return valid;
    }

    private static boolean isUppercase(final CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }

        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            char c = cs.charAt(i);
            if (!(Character.isUpperCase(c))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCountryCode(String countryCode) {
        return Arrays.binarySearch(COUNTRY_CODES, countryCode) >= 0 ||
               Arrays.binarySearch(SPECIALS_COUNTRY_CODES, countryCode) >= 0;
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
}
