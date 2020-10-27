/*
 * MIT License
 *
 * Copyright (c) 2020, Apptastic Software
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
package com.apptastic.lei;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static java.util.function.Predicate.not;

/**
 * Class for doing Legal Entity Identifier (LEI) lookup.
 */
@SuppressWarnings("squid:S2629")
public class LeiLookup {
    private static final String LOGGER = "com.apptastic.lei";
    private static final String BASE_URL = "https://leilookup.gleif.org/api/v2/leirecords?lei=";
    private static LeiLookup instance;
    private final int cacheSize;
    private ConcurrentSkipListMap<String, Lei> cache;

    /**
     * Get instance for doing LEI lookups.
     * @return instance
     */
    public static LeiLookup getInstance() {
        return getInstance(1000000);
    }

    /**
     * Get instance for doing LEI lookups.
     * @param cacheSize - number of LEI to hold in cache
     * @return instance
     */
    public static LeiLookup getInstance(int cacheSize) {
        if (instance != null && instance.cacheSize == cacheSize)
            return instance;

        instance = new LeiLookup(cacheSize);
        return instance;
    }

    private LeiLookup(int cacheSize) {
        this.cacheSize = cacheSize;
        cache = new ConcurrentSkipListMap<>();
    }

    /**
     * Get LEI entry by LEI code.
     * @param leiCode - LEI code
     * @return lei
     */
    public Optional<Lei> getLei(String leiCode) {
        Lei lei = cache.computeIfAbsent(leiCode, x -> search(x).findFirst().orElse(null));

        if (cache.size() > cacheSize) {
            cache.pollLastEntry();
        }

        return Optional.ofNullable(lei);
    }

    /**
     * Get LEI entries by LEI codes.
     * @param leiCode - List of LEI codes
     * @return stream of LEIs
     */
    public Stream<Lei> getLei(List<String> leiCode) {
        return getLei(leiCode.toArray(String[]::new));
    }

    /**
     * Get LEI entries by LEI codes.
     * @param leiCode - Array of LEI codes
     * @return stream of LEIs
     */
    public Stream<Lei> getLei(String... leiCode) {
        String[] missingLeiCodes = Arrays.stream(leiCode)
                                         .distinct()
                                         .filter(not(cache::containsKey))
                                         .toArray(String[]::new);

        search(missingLeiCodes).forEach(l -> cache.put(l.getLeiCode(), l));

        while (cache.size() > cacheSize) {
            cache.pollLastEntry();
        }

        return Arrays.stream(leiCode).map(cache::get);
    }

    private Stream<Lei> search(String... leiCode) {
        String param = Arrays.stream(leiCode)
                             .distinct()
                             .filter(LeiCodeValidator::isValid)
                             .collect(Collectors.joining(","));

        if (param == null || param.isEmpty()) {
            return Stream.empty();
        }

        try {
            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init(null, null, null);

            HttpClient client = HttpClient.newBuilder()
                                          .sslContext(context)
                                          .followRedirects(HttpClient.Redirect.ALWAYS)
                                          .build();

            HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + param))
                                             .header("Accept-Encoding", "gzip")
                                             .GET()
                                             .build();

            var resp = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            var inputStream = resp.body();

            if (Optional.of("gzip").equals(resp.headers().firstValue("Content-Encoding")))
                inputStream = new GZIPInputStream(inputStream);

            var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            List<Lei> leiList = parseResponse(new BufferedReader(reader));
            return leiList.stream();
        } catch (Exception e) {
            var logger = Logger.getLogger(LOGGER);
            logger.severe(e.getMessage());
        }

        return Stream.empty();
    }

    private List<Lei> parseResponse(BufferedReader reader) throws IOException {
        List<Lei> leiList = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(reader);

        if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
            return Collections.emptyList();
        }

        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            Lei lei = new Lei();
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("LEI".equals(name)) {
                    lei.leiCode = getValue(jsonReader);
                } else if ("Entity".equals(name)) {
                    parseEntity(jsonReader, lei);
                } else if ("Registration".equals(name)) {
                    lei.registration = parseRegistration(jsonReader);
                } else {
                    jsonReader.skipValue();
                }
            }

            if (lei.leiCode != null && lei.legalName != null) {
                leiList.add(lei);
            }

            jsonReader.endObject();
        }

        jsonReader.endArray();

        return leiList;
    }

    @SuppressWarnings("squid:S3776")
    private void parseEntity(JsonReader jsonReader, Lei lei) throws IOException {
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("LegalName".equals(name)) {
                lei.legalName = getValue(jsonReader);
            }
            else if ("LegalAddress".equals(name)) {
                Lei.Address address = parseAddress(jsonReader);
                lei.legalAddress = address;
            }
            else if ("HeadquartersAddress".equals(name)) {
                Lei.Address address = parseAddress(jsonReader);
                lei.headquartersAddress = address;
            }
            else if ("RegistrationAuthority".equals(name)) {
                Lei.RegistrationAuthority registrationAuthority = parseRegistrationAuthority(jsonReader);
                lei.registrationAuthority = registrationAuthority;
            }
            else if ("LegalJurisdiction".equals(name)) {
                lei.legalJurisdiction = getValue(jsonReader);
            }
            else if ("EntityCategory".equals(name)) {
                String value = getValue(jsonReader);
                if ("BRANCH".equals(value))
                    lei.entityCategory = Lei.EntityCategory.BRANCH;
                else if ("FUND".equals(value))
                    lei.entityCategory = Lei.EntityCategory.FUND;
                else if ("SOLE_PROPRIETOR".equals(value))
                    lei.entityCategory = Lei.EntityCategory.SOLE_PROPRIETOR;
                else {
                    var logger = Logger.getLogger(LOGGER);
                    logger.severe("Unknown value for entity category: " + value);
                }
            }
            else if ("LegalForm".equals(name)) {
                lei.entityLegalFormCode = parseEntityLegalFormCode(jsonReader);
            }
            else if ("EntityStatus".equals(name)) {
                String value = getValue(jsonReader);
                if ("ACTIVE".equals(value))
                    lei.entityStatus = Lei.EntityStatus.ACTIVE;
                else if ("INACTIVE".equals(value))
                    lei.entityStatus = Lei.EntityStatus.INACTIVE;
                else {
                    var logger = Logger.getLogger(LOGGER);
                    logger.severe("Unknown value for entity status: " + value);
                }
            }
            else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
    }

    private String parseEntityLegalFormCode(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String entityLegalFormCode = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("EntityLegalFormCode".equals(name)) {
                entityLegalFormCode = getValue(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return entityLegalFormCode;
    }

    private Lei.Address parseAddress(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        Lei.Address address = new Lei.Address();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("FirstAddressLine".equals(name)) {
                address.firstAddressLine = getValue(jsonReader);
            }
            else if ("City".equals(name)) {
                address.city = getValue(jsonReader);
            }
            else if ("Region".equals(name)) {
                address.region = getValue(jsonReader);
            }
            else if ("Country".equals(name)) {
                address.country = getValue(jsonReader);
            }
            else if ("PostalCode".equals(name)) {
                address.postalCode = getValue(jsonReader);
            }
            else if ("AdditionalAddressLine".equals(name)) {
                address.additionalAddressLine = getValues(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return address;
    }

    private Lei.RegistrationAuthority parseRegistrationAuthority(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        Lei.RegistrationAuthority registrationAuthority = new Lei.RegistrationAuthority();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("RegistrationAuthorityID".equals(name)) {
                registrationAuthority.registrationAuthorityID = getValue(jsonReader);
            }
            else if ("RegistrationAuthorityEntityID".equals(name)) {
                registrationAuthority.registrationAuthorityEntityID = getValue(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return registrationAuthority;
    }

    @SuppressWarnings("squid:S3776")
    private Lei.Registration parseRegistration(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        Lei.Registration registration = new Lei.Registration();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("InitialRegistrationDate".equals(name)) {
                registration.initialRegistrationDate = getValue(jsonReader);
            }
            else if ("LastUpdateDate".equals(name)) {
                registration.lastUpdateDate = getValue(jsonReader);
            }
            else if ("RegistrationStatus".equals(name)) {
                String value = getValue(jsonReader);
                if (value == null)
                    continue;

                switch (value) {
                    case "PENDING_VALIDATION":
                        registration.registrationStatus = Lei.RegistrationStatus.PENDING_VALIDATION;
                        break;
                    case "ISSUED":
                        registration.registrationStatus = Lei.RegistrationStatus.ISSUED;
                        break;
                    case "DUPLICATE":
                        registration.registrationStatus = Lei.RegistrationStatus.DUPLICATE;
                        break;
                    case "LAPSED":
                        registration.registrationStatus = Lei.RegistrationStatus.LAPSED;
                        break;
                    case "MERGED":
                        registration.registrationStatus = Lei.RegistrationStatus.MERGED;
                        break;
                    case "RETIRED":
                        registration.registrationStatus = Lei.RegistrationStatus.RETIRED;
                        break;
                    case "ANNULLED":
                        registration.registrationStatus = Lei.RegistrationStatus.ANNULLED;
                        break;
                    case "CANCELLED":
                        registration.registrationStatus = Lei.RegistrationStatus.CANCELLED;
                        break;
                    case "TRANSFERRED":
                        registration.registrationStatus = Lei.RegistrationStatus.TRANSFERRED;
                        break;
                    case "PENDING_TRANSFER":
                        registration.registrationStatus = Lei.RegistrationStatus.PENDING_TRANSFER;
                        break;
                    case "PENDING_ARCHIVAL":
                        registration.registrationStatus = Lei.RegistrationStatus.PENDING_ARCHIVAL;
                        break;
                    default:
                        var logger = Logger.getLogger(LOGGER);
                        logger.severe("Unknown value for registration status: " + value);
                }
            }
            else if ("NextRenewalDate".equals(name)) {
                registration.nextRenewalDate = getValue(jsonReader);
            }
            else if ("ManagingLOU".equals(name)) {
                registration.managingLOU = getValue(jsonReader);
            }
            else if ("ValidationSources".equals(name)) {
                String value = getValue(jsonReader);
                if ("PENDING".equals(value))
                    registration.validationSource = Lei.ValidationSource.PENDING;
                else if ("ENTITY_SUPPLIED_ONLY".equals(value))
                    registration.validationSource = Lei.ValidationSource.ENTITY_SUPPLIED_ONLY;
                else if ("PARTIALLY_CORROBORATED".equals(value))
                    registration.validationSource = Lei.ValidationSource.PARTIALLY_CORROBORATED;
                else if ("FULLY_CORROBORATED".equals(value))
                    registration.validationSource = Lei.ValidationSource.FULLY_CORROBORATED;
                else {
                    var logger = Logger.getLogger(LOGGER);
                    logger.severe("Unknown value for validation sources: " + value);
                }
            }
            else if ("ValidationAuthority".equals(name)) {
                jsonReader.beginObject();

                while (jsonReader.hasNext()) {
                    name = jsonReader.nextName();
                    if ("ValidationAuthorityID".equals(name)) {
                        registration.validationAuthorityID = getValue(jsonReader);
                    }
                    else if ("ValidationAuthorityEntityID".equals(name)) {
                        registration.validationAuthorityEntityID = getValue(jsonReader);
                    }
                    else {
                        jsonReader.skipValue();
                    }
                }

                jsonReader.endObject();
            }
            else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return registration;
    }

    private String getValue(JsonReader jsonReader) throws IOException {
        String value = null;
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("$".equals(name)) {
                value = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return value;
    }

    private List<String> getValues(JsonReader jsonReader) throws IOException {
        List<String> values = new ArrayList<>();
        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            String value = getValue(jsonReader);
            if (value != null) {
                values.add(value);
            }
        }

        jsonReader.endArray();
        return values.isEmpty() ? null : values;
    }
}
