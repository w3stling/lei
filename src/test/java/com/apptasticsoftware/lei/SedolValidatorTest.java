package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SedolValidatorTest {

    @Test
    void validSedolCode() {
        assertTrue(SedolValidator.isValid("0263494"));
        assertTrue(SedolValidator.isValid("0540528"));
        assertTrue(SedolValidator.isValid("B1XH2C0"));
        assertTrue(SedolValidator.isValid("BH4HKS3"));
        assertTrue(SedolValidator.isValid("BMDGCK2"));

        assertTrue(SedolValidator.isValid("0736554"));
        assertTrue(SedolValidator.isValid("B033F22"));
        assertTrue(SedolValidator.isValid("B74CDH8"));
        assertTrue(SedolValidator.isValid("B07KD36"));
        assertTrue(SedolValidator.isValid("B138NB9"));
        assertTrue(SedolValidator.isValid("BLNN3L4"));

        assertTrue(SedolValidator.isValid("0056650"));

        assertTrue(SedolValidator.isValid("3134865"));
        assertTrue(SedolValidator.isValid("0884709"));
        assertTrue(SedolValidator.isValid("0798059"));
    }

    @Test
    void invalidSedolCode() {
        assertFalse(SedolValidator.isValid("026349"));
        assertFalse(SedolValidator.isValid("02634941"));
    }
}
