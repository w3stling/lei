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
    }

    @Test
    void invalidCusipCode() {
        assertFalse(CusipValidator.isValid("0378331000"));
        assertFalse(CusipValidator.isValid("03783310"));
        assertFalse(CusipValidator.isValid("68389X10A"));
        assertFalse(CusipValidator.isValid("38259Q508"));
    }
}
