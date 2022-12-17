package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CusipValidatorTest {

    @Test
    void validCusipCode() {
        assertTrue(CusipValidator.isValid("037833100"));
        assertTrue(CusipValidator.isValid("17275R102"));
        assertTrue(CusipValidator.isValid("38259P508"));
        assertTrue(CusipValidator.isValid("594918104"));
        assertTrue(CusipValidator.isValid("68389X105"));

        // Dummy CUSIP
        assertTrue(CusipValidator.isValid("0*7833107"));
        assertTrue(CusipValidator.isValid("0@7833105"));
        assertTrue(CusipValidator.isValid("0#7833103"));
    }

    @Test
    void invalidCusipCode() {
        assertFalse(CusipValidator.isValid("0378331000"));
        assertFalse(CusipValidator.isValid("03783310"));
        assertFalse(CusipValidator.isValid("68389X10A"));
        assertFalse(CusipValidator.isValid("38259Q508"));
        assertFalse(CusipValidator.isValid("17275r102"));
        assertFalse(CusipValidator.isValid(""));
        assertFalse(CusipValidator.isValid(null));
    }
}
