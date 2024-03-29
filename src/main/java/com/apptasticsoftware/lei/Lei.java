/*
 * MIT License
 *
 * Copyright (c) 2022, Apptastic Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.apptasticsoftware.lei;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ISO 17442 - Legal Entity Identifier (LEI).
 */
public class Lei {
    String leiCode;
    String legalName;
    EntityStatus entityStatus;
    String entityLegalFormCode;
    String legalJurisdiction;
    EntityCategory entityCategory;
    Address legalAddress;
    Address headquartersAddress;
    RegistrationAuthority registrationAuthority;
    Registration registration;

    Lei() {
    }

    /**
     * The ISO 17442 compatible identifier for the legal entity.
     *
     * @return lei code
     */
    public String getLeiCode() {
        return leiCode;
    }

    /**
     * The legal name of the entity.
     *
     * @return legal name
     */
    public String getLegalName() {
        return legalName;
    }

    /**
     * The operational and/or legal registration status of the entity (may be ACTIVE or INACTIVE)
     * @return entity status
     */
    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    /**
     * A current code from the GLEIF-maintained list MUST be used. Values of the LegalFormEnum code list are maintained
     * by ISO / GLEIF through the Entity Legal Form (ELF), available from <a href="http://www.gleif.org">GLEIF</a>.
     *
     * @return entity legal form code
     */
    public String getEntityLegalFormCode() {
        return entityLegalFormCode;
    }

    /**
     * The jurisdiction of legal formation and registration of the entity (and upon which the <code>LegalForm</code>
     * data element is also dependent). Please note that the XML schema validates the format of
     * <code>LegalJurisdiction</code> codes but not the specific codes conforming to the ISO standards it requires.
     *
     * @return legal jurisdiction
     */
    public String getLegalJurisdiction() {
        return legalJurisdiction;
    }

    /**
     * Indicates (where applicable) the category of entity identified by this LEI data record, as a more specific
     * category within the broad definition given in ISO 17442. These categories are based on use cases specified
     * in LEI-ROC policies, found at <a href="http://www.leiroc.org/list/leiroc_gls/index.htm">LEI-ROC</a>
     *
     * @return entity category
     */
    public EntityCategory getEntityCategory() {
        return entityCategory;
    }

    /**
     * The address of the entity as recorded in the registration of the entity in its legal jurisdiction.
     *
     * @return legal address
     */
    public Address getLegalAddress() {
        return legalAddress;
    }

    /**
     * The address of the headquarters of the Entity.
     *
     * @return headquarters address
     */
    public Address getHeadquartersAddress() {
        return headquartersAddress;
    }

    /**
     * An identifier for the legal entity in a business registry in the jurisdiction of legal registration, or in the
     * appropriate registration authority.
     *
     * @return registration authority
     */
    public RegistrationAuthority getRegistrationAuthority() {
        return registrationAuthority;
    }

    /**
     * The <code>Registration</code> container element contains all information on the legal entity's LEI registration
     * with the <code>ManagingLOU</code>.
     *
     * @return registration
     */
    public Registration getRegistration() {
        return registration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lei lei = (Lei) o;
        return Objects.equals(getLeiCode(), lei.getLeiCode()) &&
               Objects.equals(getLegalName(), lei.getLegalName()) &&
               getEntityStatus() == lei.getEntityStatus() &&
               Objects.equals(getEntityLegalFormCode(), lei.getEntityLegalFormCode()) &&
               Objects.equals(getLegalJurisdiction(), lei.getLegalJurisdiction()) &&
               getEntityCategory() == lei.getEntityCategory() &&
               Objects.equals(getLegalAddress(), lei.getLegalAddress()) &&
               Objects.equals(getHeadquartersAddress(), lei.getHeadquartersAddress()) &&
               Objects.equals(getRegistrationAuthority(), lei.getRegistrationAuthority()) &&
               Objects.equals(getRegistration(), lei.getRegistration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLeiCode(), getLegalName(), getEntityStatus(), getEntityLegalFormCode(),
                            getLegalJurisdiction(), getEntityCategory(), getLegalAddress(), getHeadquartersAddress(),
                            getRegistrationAuthority(), getRegistration());
    }

    /**
     * Indicates (where applicable) the category of entity identified by this LEI data record, as a more specific
     * category within the broad definition given in ISO 17442. These categories are based on use cases specified
     * in LEI-ROC policies, found at <a href="http://www.leiroc.org/list/leiroc_gls/index.htm">LEI-ROC</a>
     */
    public enum EntityCategory {
        /**
         * The legal entity is a branch of another legal entity.
         */
        BRANCH,
        /**
         * The legal entity is a fund managed by another legal entity.
         */
        FUND,
        /**
         * The legal entity is an individual acting in a business capacity.
         */
        SOLE_PROPRIETOR,
        /**
         * The legal entity is a general entity.
         */
        GENERAL,
    }

    /**
     * The operational and/or legal registration status of the entity (may be <code>ACTIVE</code> or
     * <code>INACTIVE</code>).
     */
    public enum EntityStatus {
        /**
         * As of the last report or update, the legal entity reported that it was legally registered and operating.
         */
        ACTIVE,
        /**
         * It has been determined that the entity that was assigned the LEI is no longer legally registered and/or
         * operating, whether as a result of business closure, acquisition by or merger with another (or new) entity,
         * or determination of illegitimacy.
         */
        INACTIVE
    }

    /**
     * The level of validation of the reference data provided by the registrant.
     */
    public enum ValidationSource {
        /**
         * The validation of the reference data provided by the registrant has not yet occurred.
         */
        PENDING,
        /**
         * Based on the validation procedures in use by the LOU responsible for the record, the information associated
         * with this record has significant reliance on the information that a submitter provided due to the
         * unavailability of corroborating information.
         */
        ENTITY_SUPPLIED_ONLY,
        /**
         * Based on the validation procedures in use by the LOU responsible for the record, the information supplied by
         * the registrant can be partially corroborated by public authoritative sources, while some of the record is
         * dependent upon the information that the registrant submitted, either due to conflicts with authoritative
         * information, or due to data unavailability.
         */
        PARTIALLY_CORROBORATED,
        /**
         * Based on the validation procedures in use by the LOU responsible for the record, there is sufficient
         * information contained in authoritative public sources to corroborate the information that the submitter has
         * provided for the record.
         */
        FULLY_CORROBORATED
    }

    /**
     * The status of the legal entity's LEI registration with the <code>ManagingLOU</code>.
     */
    public enum RegistrationStatus {
        /**
         * An application for an LEI that has been submitted and which is being processed and validated.
         */
        PENDING_VALIDATION,
        /**
         * An LEI Registration that has been validated and issued, and which identifies an entity that was an operating
         * legal entity as of the last update.
         */
        ISSUED,
        /**
         * An LEI Registration that has been determined to be a duplicate registration of the same legal entity as
         * another LEI Registration; the DUPLICATE status is assigned to the non-surviving registration (i.e. the LEI
         * that should no longer be used).
         */
        DUPLICATE,
        /**
         * An LEI registration that has not been renewed by the <code>NextRenewalDate</code> and is not known by public
         * sources to have ceased operation.
         */
        LAPSED,
        /**
         * An LEI registration for an entity that has been merged into another legal entity, such that this lega lentity
         * no longer exists as an operating entity.
         */
        MERGED,
        /**
         * An LEI registration for an entity that has ceased operation, without having been merged into another entity.
         */
        RETIRED,
        /**
         * An LEI registration that was marked as erroneous or invalid after it was issued.
         */
        ANNULLED,
        /**
         * An LEI registration that was abandoned prior to issuance of an LEI.
         */
        CANCELLED,
        /**
         * An LEI registration that has been transferred to a different LOU as the managing LOU.
         */
        TRANSFERRED,
        /**
         * An LEI registration that has been requested to be transferred to another LOU. The request is being processed
         * at the sending LOU.
         */
        PENDING_TRANSFER,
        /**
         * An LEI registration is about to be transferred to a different LOU, after which its registration status will
         * revert to a non-pending status.
         */
        PENDING_ARCHIVAL
    }

    /**
     * An address for the legal entity.
     */
    public static class Address {
        String firstAddressLine;
        List<String> additionalAddressLine;
        String city;
        String region;
        String country;
        String postalCode;

        Address() {
            additionalAddressLine = Collections.emptyList();
        }

        /**
         * The mandatory first address line element.
         *
         * @return first address line
         */
        public String getFirstAddressLine() {
            return firstAddressLine;
        }

        /**
         * One to three optional additional address line elements.
         *
         * @return additional address line
         */
        public List<String> getAdditionalAddressLine() {
            return additionalAddressLine;
        }

        /**
         * The mandatory name of the city.
         *
         * @return city
         */
        public String getCity() {
            return city;
        }

        /**
         * The (optional) 4- to 6-character ISO 3166-2 region code of the region.
         *
         * @return region
         */
        public String getRegion() {
            return region;
        }

        /**
         * The 2-character ISO 3166-1 country code of the country.
         *
         * @return contry
         */
        public String getCountry() {
            return country;
        }

        /**
         * The (optional) postal code of this address as specified by the local postal service.
         *
         * @return postal code
         */
        public String getPostalCode() {
            return postalCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Address address = (Address) o;
            return Objects.equals(getFirstAddressLine(), address.getFirstAddressLine()) &&
                   Objects.equals(getAdditionalAddressLine(), address.getAdditionalAddressLine()) &&
                   Objects.equals(getCity(), address.getCity()) &&
                   Objects.equals(getRegion(), address.getRegion()) &&
                   Objects.equals(getCountry(), address.getCountry()) &&
                   Objects.equals(getPostalCode(), address.getPostalCode());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getFirstAddressLine(), getAdditionalAddressLine(), getCity(), getRegion(),
                                getCountry(), getPostalCode());
        }
    }

    /**
     * An identifier for the legal entity in a business registry in the jurisdiction of legal registration, or in the
     * appropriate registration authority.
     */
    public static class RegistrationAuthority {
        String registrationAuthorityID;
        String registrationAuthorityEntityID;

        RegistrationAuthority() {

        }

        /**
         * The reference code of the registration authority, taken from the Registration Authorities Code List
         * maintained by GLEIF.
         *
         * @return registration authority ID
         */
        public String getRegistrationAuthorityID() {
            return registrationAuthorityID;
        }

        /**
         * The identifier of the entity at the indicated registration authority. Typically, the identifier of the
         * legal entity as maintained by a business registry in the jurisdiction of legal registration, or if the
         * entity is one that is not recorded in a business registry (e.g. one of the varieties of funds registered
         * instead with financial regulators), the identifier of the entity in the appropriate registration authority.
         *
         * @return registration authority entity ID
         */
        public String getRegistrationAuthorityEntityID() {
            return registrationAuthorityEntityID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RegistrationAuthority that = (RegistrationAuthority) o;
            return Objects.equals(getRegistrationAuthorityID(), that.getRegistrationAuthorityID()) &&
                   Objects.equals(getRegistrationAuthorityEntityID(), that.getRegistrationAuthorityEntityID());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getRegistrationAuthorityID(),
                                getRegistrationAuthorityEntityID());
        }
    }

    /**
     * The <code>Registration</code> container element contains all information on the legal entity's LEI registration
     * with the <code>ManagingLOU</code>.
     */
    public static class Registration {
        String initialRegistrationDate;
        String lastUpdateDate;
        RegistrationStatus registrationStatus;
        String nextRenewalDate;
        String managingLOU;
        ValidationSource validationSource;
        String validationAuthorityID;
        String validationAuthorityEntityID;

        Registration() {

        }

        /**
         * Date/time the LEI record was created.
         *
         * @return initial registration date
         */
        public String getInitialRegistrationDate() {
            return initialRegistrationDate;
        }

        /**
         * Date/time the LEI record was created.
         *
         * @return initial registration date
         */
        public ZonedDateTime getInitialRegistrationDateZonedDateTime() {
            return ZonedDateTime.parse(initialRegistrationDate);
        }

        /**
         * Date/time the LEI record was most recently updated.
         *
         * @return last update date
         */
        public String getLastUpdateDate() {
            return lastUpdateDate;
        }

        /**
         * Date/time the LEI record was most recently updated.
         *
         * @return last update date
         */
        public ZonedDateTime getLastUpdateDateZonedDateTime() {
            return ZonedDateTime.parse(lastUpdateDate);
        }

        /**
         * The status of the legal entity's LEI registration with the <code>ManagingLOU</code>.
         *
         * @return registration status
         */
        public RegistrationStatus getRegistrationStatus() {
            return registrationStatus;
        }

        /**
         * The next date by which the LEI registration should be renewed and re-certified by the legal entity.
         *
         * @return next renewal date
         */
        public String getNextRenewalDate() {
            return nextRenewalDate;
        }

        /**
         * The next date by which the LEI registration should be renewed and re-certified by the legal entity.
         *
         * @return next renewal date
         */
        public ZonedDateTime getNextRenewalDateZonedDateTime() {
            return ZonedDateTime.parse(nextRenewalDate);
        }

        /**
         * The LEI of the LOU that is responsible for administering this LEI registration.
         *
         * @return managing LOU
         */
        public String getManagingLOU() {
            return managingLOU;
        }

        /**
         * The level of validation of the reference data provided by the registrant.
         *
         * @return validation source
         */
        public ValidationSource getValidationSource() {
            return validationSource;
        }

        /**
         * The reference code of the registration authority, taken from the Registration Authorities Code List (RAL)
         * maintained by GLEIF.
         *
         * @return validation authority ID
         */
        public String getValidationAuthorityID() {
            return validationAuthorityID;
        }

        /**
         * The identifier of the entity at the indicated registration authority. Typically, the identifier of the
         * legal entity as maintained by a business registry in the jurisdiction of legal registration, or if the
         * entity is one that is not recorded in a business registry (e.g. one of the varieties of funds registered
         * instead with financial regulators), the identifier of the entity in the appropriate registration authority.
         *
         * @return validation authority entity ID
         */
        public String getValidationAuthorityEntityID() {
            return validationAuthorityEntityID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Registration that = (Registration) o;
            return Objects.equals(getInitialRegistrationDate(), that.getInitialRegistrationDate()) &&
                   Objects.equals(getLastUpdateDate(), that.getLastUpdateDate()) &&
                   getRegistrationStatus() == that.getRegistrationStatus() &&
                   Objects.equals(getNextRenewalDate(), that.getNextRenewalDate()) &&
                   Objects.equals(getManagingLOU(), that.getManagingLOU()) &&
                   getValidationSource() == that.getValidationSource() &&
                   Objects.equals(getValidationAuthorityID(), that.getValidationAuthorityID()) &&
                   Objects.equals(getValidationAuthorityEntityID(), that.getValidationAuthorityEntityID());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getInitialRegistrationDate(), getLastUpdateDate(), getRegistrationStatus(),
                                getNextRenewalDate(), getManagingLOU(), getValidationSource(),
                                getValidationAuthorityID(), getValidationAuthorityEntityID());
        }
    }
}
