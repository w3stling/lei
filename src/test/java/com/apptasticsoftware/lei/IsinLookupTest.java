package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsinLookupTest {
    private static final IsinLookup lookup = new IsinLookup();

    private static final IsinLookup lookup1 = new IsinLookup() {
        @Override
        String sendRequest2(String url, String data) {
            return null;
        }

        @Override
        String parseIsin2(String text) {
            return null;
        }
    };

    private static final IsinLookup lookup1Parse1 = new IsinLookup() {
        @Override
        String sendRequest2(String url, String data) {
            return null;
        }

        @Override
        String parseIsin2(String text) {
            return null;
        }
    };

    private static final IsinLookup lookup1Parse2 = new IsinLookup() {
        @Override
        String sendRequest2(String url, String data) {
            return null;
        }

        @Override
        String parseIsin1(String text) {
            return null;
        }
    };

    private static final IsinLookup lookup2 = new IsinLookup() {
        @Override
        String sendRequest1(String url, String data) {
            return null;
        }
    };

    private static final IsinLookup lookup2Parse1 = new IsinLookup() {
        @Override
        String sendRequest1(String url, String data) {
            return null;
        }

        @Override
        String parseIsin2(String text) {
            return null;
        }
    };

    private static final IsinLookup lookup2Parse2 = new IsinLookup() {
        @Override
        String sendRequest1(String url, String data) {
            return null;
        }

        @Override
        String parseIsin1(String text) {
            return null;
        }
    };


    @Test
    void lookupByCusip() {
        assertTrue(lookup.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup.getIsinByCusip("931142103").isPresent());
        assertFalse(lookup.getIsinByCusip("").isPresent());
        assertFalse(lookup.getIsinByCusip("0@7833105").isPresent());
    }

    @Test
    void lookupByCusipMethod1() {
        assertTrue(lookup1.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup1.getIsinByCusip("931142103").isPresent());
        assertFalse(lookup.getIsinByCusip("").isPresent());
    }

    @Test
    void lookupByCusipMethod1Parse1() {
        assertTrue(lookup1Parse1.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup1Parse1.getIsinByCusip("931142103").isPresent());
        assertFalse(lookup1Parse1.getIsinByCusip("").isPresent());
    }

    @Test
    void lookupByCusipMethod1Parse2() {
        assertTrue(lookup1Parse2.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup1Parse2.getIsinByCusip("931142103").isPresent());
        assertFalse(lookup1Parse2.getIsinByCusip("").isPresent());
    }

    @Test
    void lookupByCusipMethod2() {
        assertTrue(lookup2.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup2.getIsinByCusip("931142103").isPresent());
        assertFalse(lookup.getIsinByCusip("").isPresent());
    }

    @Test
    void lookupByCusipMethod2Parse1() {
        assertTrue(lookup2Parse1.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup2Parse1.getIsinByCusip("931142103").isPresent());
        assertFalse(lookup2Parse1.getIsinByCusip("").isPresent());
    }

    @Test
    void lookupByCusipMethod2Parse2() {
        assertTrue(lookup2Parse2.getIsinByCusip("931142103").isPresent());
        assertTrue(lookup2Parse2.getIsinByCusip("931142103").isPresent());
        assertFalse(lookup2Parse2.getIsinByCusip("").isPresent());
    }

    @Test
    void lookupBySedol() {
        assertTrue(lookup.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup.getIsinBySedol("0884709").isPresent());
        assertFalse(lookup.getIsinBySedol("").isPresent());
    }

    @Test
    void lookupBySedolMethod1() {
        assertTrue(lookup1.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup1.getIsinBySedol("0884709").isPresent());
        assertFalse(lookup.getIsinBySedol("").isPresent());
    }

    @Test
    void lookupBySedolMethod1Parse1() {
        assertTrue(lookup1Parse1.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup1Parse1.getIsinBySedol("0884709").isPresent());
        assertFalse(lookup1Parse1.getIsinBySedol("").isPresent());
    }

    @Test
    void lookupBySedolMethod1Parse12() {
        assertTrue(lookup1Parse2.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup1Parse2.getIsinBySedol("0884709").isPresent());
        assertFalse(lookup1Parse2.getIsinBySedol("").isPresent());
    }

    @Test
    void lookupBySedolMethod2() {
        assertTrue(lookup2.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup2.getIsinBySedol("0884709").isPresent());
        assertFalse(lookup.getIsinBySedol("").isPresent());
    }

    @Test
    void lookupBySedolMethod2Parse1() {
        assertTrue(lookup2Parse1.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup2Parse1.getIsinBySedol("0884709").isPresent());
        assertFalse(lookup2Parse1.getIsinBySedol("").isPresent());
    }

    @Test
    void lookupBySedolMethod2Parse2() {
        assertTrue(lookup2Parse2.getIsinBySedol("0884709").isPresent());
        assertTrue(lookup2Parse2.getIsinBySedol("0884709").isPresent());
        assertFalse(lookup2Parse2.getIsinBySedol("").isPresent());
    }
}
