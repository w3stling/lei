package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsinCodeValidatorTest {

    @Test
    void validIsinCode() {
        assertTrue(IsinCodeValidator.isValid("US0378331005"));
        assertTrue(IsinCodeValidator.isValid("US92857W3088"));
        assertTrue(IsinCodeValidator.isValid("SE0000108656"));
        assertTrue(IsinCodeValidator.isValid("SE0000825820"));
    }

    @Test
    void invalidIsinCode() {
        assertFalse(IsinCodeValidator.isValid("0003783310050"));
        assertFalse(IsinCodeValidator.isValid("00037833100"));
        assertFalse(IsinCodeValidator.isValid("000378331005"));
        assertFalse(IsinCodeValidator.isValid("US03783310050"));
        assertFalse(IsinCodeValidator.isValid("US037833100"));
        assertFalse(IsinCodeValidator.isValid("US0370331005"));
    }
}
