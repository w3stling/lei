package com.apptasticsoftware.lei;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class LeiLookupTest {

    @Test
    void testLookup() {
        LeiLookup leiLookup = LeiLookup.getInstance();
        Optional<Lei> lei = leiLookup.getLei("7LTWFZYICNSX8D621K86");

        assertTrue(lei.isPresent());
        assertEquals("7LTWFZYICNSX8D621K86", lei.get().getLeiCode());
        assertEquals("DEUTSCHE BANK AKTIENGESELLSCHAFT", lei.get().getLegalName());
        assertEquals("6QQB", lei.get().getEntityLegalFormCode());
        assertEquals("DE", lei.get().getLegalJurisdiction());
        assertEquals(Lei.EntityStatus.ACTIVE, lei.get().getEntityStatus());

        assertEquals("Taunusanlage 12", lei.get().getHeadquartersAddress().getFirstAddressLine());
        assertEquals(0, lei.get().getLegalAddress().getAdditionalAddressLine().size());
        assertEquals("60325", lei.get().getHeadquartersAddress().getPostalCode());
        assertEquals("Frankfurt am Main", lei.get().getHeadquartersAddress().getCity());
        assertEquals("DE-HE", lei.get().getHeadquartersAddress().getRegion());
        assertEquals("DE", lei.get().getHeadquartersAddress().getCountry());
        assertEquals(Lei.EntityCategory.GENERAL, lei.get().getEntityCategory());

        assertEquals("Taunusanlage 12", lei.get().getLegalAddress().getFirstAddressLine());
        assertEquals(0, lei.get().getHeadquartersAddress().getAdditionalAddressLine().size());
        assertEquals("60325", lei.get().getLegalAddress().getPostalCode());
        assertEquals("Frankfurt am Main", lei.get().getLegalAddress().getCity());
        assertEquals("DE-HE", lei.get().getLegalAddress().getRegion());
        assertEquals("DE", lei.get().getLegalAddress().getCountry());

        assertEquals("2012-06-06T15:51:15Z", lei.get().getRegistration().getInitialRegistrationDate());
        assertEquals(ZonedDateTime.parse("2012-06-06T15:51:15Z"), lei.get().getRegistration().getInitialRegistrationDateZonedDateTime());
        assertEquals("2022-04-20T16:01:41Z", lei.get().getRegistration().getLastUpdateDate());
        assertEquals(ZonedDateTime.parse("2022-04-20T16:01:41Z"), lei.get().getRegistration().getLastUpdateDateZonedDateTime());
        assertEquals("5299000J2N45DDNE4Y28", lei.get().getRegistration().getManagingLOU());
        assertEquals("2023-06-02T06:47:59Z", lei.get().getRegistration().getNextRenewalDate());
        assertEquals(ZonedDateTime.parse("2023-06-02T06:47:59Z"), lei.get().getRegistration().getNextRenewalDateZonedDateTime());
        assertEquals(Lei.RegistrationStatus.ISSUED, lei.get().getRegistration().getRegistrationStatus());
        assertEquals("HRB 30000", lei.get().getRegistration().getValidationAuthorityEntityID());
        assertEquals("RA000242", lei.get().getRegistration().getValidationAuthorityID());
        assertEquals(Lei.ValidationSource.FULLY_CORROBORATED, lei.get().getRegistration().getValidationSource());

        assertEquals("HRB 30000", lei.get().getRegistrationAuthority().getRegistrationAuthorityEntityID());
        assertEquals("RA000242", lei.get().getRegistrationAuthority().getRegistrationAuthorityID());
    }

    @Test
    void testLookupList() {
        LeiLookup leiLookup = LeiLookup.getInstance();
        long count1 = leiLookup.getLei("5493001KJTIIGC8Y1R12", "4469000001AVO26P9X86", "029200067A7K6CH0H586", "029200067A7K6CH0H586")
                              .count();
        assertEquals(4L, count1);

        long count2 = leiLookup.getLei(Arrays.asList("5493001KJTIIGC8Y1R12", "4469000001AVO26P9X86", "029200067A7K6CH0H586", "029200067A7K6CH0H586"))
                               .count();
        assertEquals(4L, count2);
    }

    @Test
    void testLookupCache() {
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
    void testLookupFailed() {
        LeiLookup leiLookup = LeiLookup.getInstance();
        Optional<Lei> lei = leiLookup.getLei("ABC123");

        assertFalse(lei.isPresent());
    }
}
