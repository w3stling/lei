package com.apptasticsoftware.lei;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class LeiTest {

    @Test
    void leiSimpleEqualsContract() {
        EqualsVerifier.simple().forClass(Lei.class).verify();
    }

    @Test
    void addressSimpleEqualsContract() {
        EqualsVerifier.simple().forClass(Lei.Address.class).verify();
    }

    @Test
    void registrationSimpleEqualsContract() {
        EqualsVerifier.simple().forClass(Lei.Registration.class).verify();
    }

    @Test
    void registrationAuthoritySimpleEqualsContract() {
        EqualsVerifier.simple().forClass(Lei.RegistrationAuthority.class).verify();
    }

}
