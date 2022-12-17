package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeiLookupBySedolTest {

    @Test
    void testFound() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei1 = leiLookup.getLeiBySedol("0263494");
        assertTrue(lei1.isPresent());

        Optional<Lei> lei2 = leiLookup.getLeiBySedol("B033F22");
        assertTrue(lei2.isPresent());

        Optional<Lei> lei3 = leiLookup.getLeiBySedol("BH4HKS3");
        assertTrue(lei3.isPresent());

        Optional<Lei> lei4 = leiLookup.getLeiBySedol("3134865");
        assertTrue(lei4.isPresent());
    }

    @Test
    void testMissing() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei = leiLookup.getLeiBySedol("313486A");
        assertFalse(lei.isPresent());
    }
}
