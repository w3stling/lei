package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IsinLookupTest {
    private static final IsinLookup lookup = new IsinLookup();

    private static final IsinLookup lookup1 = new IsinLookup() {
        @Override
        String sendRequest2(String url, String data) {
            return null;
        }
    };
    private static final IsinLookup lookup2 = new IsinLookup() {
        @Override
        String sendRequest1(String url, String data) {
            return null;
        }
    };

    @Test
    void lookupByCusip() {
        assertTrue(lookup.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup.getIsinByCusip("931142103").isPresent());
    }

    @Test
    void lookupByCusipMethod1() {
        assertTrue(lookup1.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup1.getIsinByCusip("931142103").isPresent());
    }

    @Test
    void lookupByCusipMethod2() {
        assertTrue(lookup2.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup2.getIsinByCusip("931142103").isPresent());
    }

    @Test
    void lookupBySedol() {
        assertTrue(lookup.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup.getIsinBySedol("0884709").isPresent());
    }

    @Test
    void lookupBySedolMethod1() {
        assertTrue(lookup1.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup1.getIsinBySedol("0884709").isPresent());
    }

    @Test
    void lookupBySedolMethod2() {
        assertTrue(lookup2.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup2.getIsinBySedol("0884709").isPresent());
    }
}
