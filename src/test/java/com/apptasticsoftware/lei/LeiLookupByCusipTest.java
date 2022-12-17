package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeiLookupByCusipTest {

    @Test
    void testFound() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei1 = leiLookup.getLeiByCusip("037833100");
        assertTrue(lei1.isPresent());

        Optional<Lei> lei2 = leiLookup.getLeiByCusip("17275R102");
        assertTrue(lei2.isPresent());

        Optional<Lei> lei3 = leiLookup.getLeiByCusip("594918104");
        assertTrue(lei3.isPresent());

        Optional<Lei> lei4 = leiLookup.getLeiByCusip("68389X105");
        assertTrue(lei4.isPresent());
    }

    @Test
    void testMissing() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei = leiLookup.getLeiByCusip("68389X10A");
        assertFalse(lei.isPresent());
    }
}
