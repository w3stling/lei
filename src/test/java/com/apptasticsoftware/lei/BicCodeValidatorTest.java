package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BicCodeValidatorTest {

    @Test
    void validBicCode() {
        assertTrue(BicCodeValidator.isValid("DEUTDEFF500"));
        assertTrue(BicCodeValidator.isValid("NEDSZAJJ"));
        assertTrue(BicCodeValidator.isValid("DABADKKK"));
        assertTrue(BicCodeValidator.isValid("UNCRITMM"));
        assertTrue(BicCodeValidator.isValid("DSBACNBXSHA"));
        assertTrue(BicCodeValidator.isValid("BNORPHMM"));
    }

    @Test
    void invalidBicCode() {
        assertFalse(BicCodeValidator.isValid("0003783310050"));
        assertFalse(BicCodeValidator.isValid("000378331000"));
        assertFalse(BicCodeValidator.isValid("000378331005"));
        assertFalse(BicCodeValidator.isValid("US03783310050"));
        assertFalse(BicCodeValidator.isValid("US0378331000"));
        assertFalse(BicCodeValidator.isValid("US0370331005"));
    }
}
