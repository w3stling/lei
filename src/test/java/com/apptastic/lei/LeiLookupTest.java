package com.apptastic.lei;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeiLookupTest {

    @Test
    public void testLookup() {
        LeiLookup leiLookup = LeiLookup.getInstance();
        Optional<Lei> lei = leiLookup.getLei("7LTWFZYICNSX8D621K86");

        assertTrue(lei.isPresent());
        assertEquals("7LTWFZYICNSX8D621K86", lei.get().getLeiCode());
        assertEquals("DEUTSCHE BANK AKTIENGESELLSCHAFT", lei.get().getLegalName());
        assertEquals("6QQB", lei.get().getEntityLegalFormCode());
        assertEquals("DE", lei.get().getLegalJurisdiction());
        assertEquals(Lei.EntityStatus.ACTIVE, lei.get().getEntityStatus());

        assertEquals("Taunusanlage 12", lei.get().getHeadquartersAddress().getFirstAddressLine());
        assertEquals("60325", lei.get().getHeadquartersAddress().getPostalCode());
        assertEquals("Frankfurt am Main", lei.get().getHeadquartersAddress().getCity());
        assertEquals("DE-HE", lei.get().getHeadquartersAddress().getRegion());
        assertEquals("DE", lei.get().getHeadquartersAddress().getCountry());

        assertEquals("Taunusanlage 12", lei.get().getLegalAddress().getFirstAddressLine());
        assertEquals("60325", lei.get().getLegalAddress().getPostalCode());
        assertEquals("Frankfurt am Main", lei.get().getLegalAddress().getCity());
        assertEquals("DE-HE", lei.get().getLegalAddress().getRegion());
        assertEquals("DE", lei.get().getLegalAddress().getCountry());

        assertEquals("2012-06-06T17:51:15+02:00", lei.get().getRegistration().getInitialRegistrationDate());
        assertEquals("2020-04-24T17:03:13+02:00", lei.get().getRegistration().getLastUpdateDate());
        assertEquals("5299000J2N45DDNE4Y28", lei.get().getRegistration().getManagingLOU());
        assertEquals("2021-06-02T08:47:59+02:00", lei.get().getRegistration().getNextRenewalDate());
        assertEquals(Lei.RegistrationStatus.ISSUED, lei.get().getRegistration().getRegistrationStatus());
        assertEquals("HRB 30000", lei.get().getRegistration().getValidationAuthorityEntityID());
        assertEquals("RA000242", lei.get().getRegistration().getValidationAuthorityID());
        assertEquals(Lei.ValidationSource.FULLY_CORROBORATED, lei.get().getRegistration().getValidationSource());

        assertEquals("HRB 30000", lei.get().getRegistrationAuthority().getRegistrationAuthorityEntityID());
        assertEquals("RA000242", lei.get().getRegistrationAuthority().getRegistrationAuthorityID());
    }

    @Test
    public void testLookupList() {
        LeiLookup leiLookup = LeiLookup.getInstance();
        long count1 = leiLookup.getLei("5493001KJTIIGC8Y1R12", "4469000001AVO26P9X86", "029200067A7K6CH0H586", "029200067A7K6CH0H586")
                              .count();
        assertEquals(4L, count1);

        long count2 = leiLookup.getLei(Arrays.asList("5493001KJTIIGC8Y1R12", "4469000001AVO26P9X86", "029200067A7K6CH0H586", "029200067A7K6CH0H586"))
                               .count();
        assertEquals(4L, count2);
    }

    @Test
    public void testLookupCache() {
        LeiLookup leiLookup = LeiLookup.getInstance(1);
        Optional<Lei> lei1 = leiLookup.getLei("5493001KJTIIGC8Y1R12");
        assertTrue(lei1.isPresent());

        Optional<Lei> lei2 = leiLookup.getLei("4469000001AVO26P9X86");
        assertTrue(lei2.isPresent());

        Optional<Lei> lei3 = leiLookup.getLei("029200067A7K6CH0H586");
        assertTrue(lei3.isPresent());

        Optional<Lei> lei4 = leiLookup.getLei("029200067A7K6CH0H586");
        assertTrue(lei4.isPresent());
    }

    @Test
    public void testLookupFailed() {
        LeiLookup leiLookup = LeiLookup.getInstance();
        Optional<Lei> lei = leiLookup.getLei("ABC123");

        assertFalse(lei.isPresent());
    }
}
