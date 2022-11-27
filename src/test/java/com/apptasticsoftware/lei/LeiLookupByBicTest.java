package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeiLookupByBicTest {

    @Test
    void testFound() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei1 = leiLookup.getLeiByBic("ALETITMMXXX");
        assertTrue(lei1.isPresent());

        Optional<Lei> lei2 = leiLookup.getLeiByBic("DEUTDEFFXXX");
        assertTrue(lei2.isPresent());

        Optional<Lei> lei3 = leiLookup.getLeiByBic("BUKBGB22XXX");
        assertTrue(lei3.isPresent());

        Optional<Lei> lei4 = leiLookup.getLeiByBic("HBUKGB4BXXX");
        assertTrue(lei4.isPresent());

        Optional<Lei> lei5 = leiLookup.getLeiByBic("ABBYGB2LXXX");
        assertTrue(lei5.isPresent());
    }

    @Test
    void testMissing() {
        LeiLookup leiLookup = LeiLookup.getInstance();

        Optional<Lei> lei = leiLookup.getLeiByIsin("DEUTDEFFXXX!!!");
        assertFalse(lei.isPresent());
    }
}
