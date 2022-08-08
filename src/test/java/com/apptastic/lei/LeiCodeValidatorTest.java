package com.apptastic.lei;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LeiCodeValidatorTest {

    @Test
    public void validLeiCode() {
        assertTrue(LeiCodeValidator.isValid("W22LROWP2IHZNBB6K528"));
        assertTrue(LeiCodeValidator.isValid("254900G6F27CHW2A7F31"));
        assertTrue(LeiCodeValidator.isValid("254900A1XHW5VXMTHH06"));
        assertTrue(LeiCodeValidator.isValid("254900Q6R4N7GSNMZJ65"));
    }

    @Test
    public void invalidLeiCode() {
        assertFalse(LeiCodeValidator.isValid("W22LROWP2IHZNBB6K52"));
        assertFalse(LeiCodeValidator.isValid("W22LROWP2I@ZNBB6K528"));
        assertFalse(LeiCodeValidator.isValid("W22LROWP2IHZNBB6K52A"));
        assertFalse(LeiCodeValidator.isValid("W22LROWP2IHZNBB6K5A8"));
        assertFalse(LeiCodeValidator.isValid("W22LROWP2I2ZNBB6K528"));
    }
}
