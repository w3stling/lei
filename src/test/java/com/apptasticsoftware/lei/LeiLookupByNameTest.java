package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeiLookupByNameTest {

    @Test
    void testFound() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        List<Lei> lei1 = leiLookup.getLeiByLegalName("Apple");
        assertTrue(lei1.size() > 200);
    }

    @Test
    void testMissing() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei = leiLookup.getLeiByIsin("---");
        assertFalse(lei.isPresent());
    }
}
