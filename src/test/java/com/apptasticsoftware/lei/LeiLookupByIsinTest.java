package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeiLookupByIsinTest {

    @Test
    void testFound() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei1 = leiLookup.getLeiByIsin("US0378331005");
        assertTrue(lei1.isPresent());

        Optional<Lei> lei2 = leiLookup.getLeiByIsin("US0378331005");
        assertTrue(lei2.isPresent());
    }

    @Test
    void testMissing() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei = leiLookup.getLeiByIsin("US0378331005!!!");
        assertFalse(lei.isPresent());
    }
}
