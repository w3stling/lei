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
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.util.function.Predicate.not;

/**
 * Class for doing Legal Entity Identifier (LEI) lookup.
 */
@SuppressWarnings("squid:S2629")
public class LeiLookup {
    private static final String LOGGER = "com.apptastic.lei";
    private final static Integer DEFAULT_VALUE = 5;
    private static final String BASE_URL_LEI = "https://api.gleif.org/api/v1/lei-records?filter[lei]=";
    private static final String BASE_URL_ISIN = "https://api.gleif.org/api/v1/lei-records?filter[isin]=";
    private static final String BASE_URL_BIC = "https://api.gleif.org/api/v1/lei-records?filter[bic]=";
    private static LeiLookup instance;
    private final int cacheSize;
    private final int searchMissCacheSize;
    private final ConcurrentSkipListMap<String, Lei> cache;
    private final ConcurrentSkipListMap<String, Integer> searchMissCache;


    /**
     * Get instance for doing LEI lookups.
     * @return instance
     */
    public static LeiLookup getInstance() {
        return getInstance(50000, 500000);
    }

    /**
     * Get instance for doing LEI lookups.
     * @param cacheSize - number of LEI to hold in cache
     * @param searchMissCacheSize - number of search misses to cache to prevent searching for it again
     * @return instance
     */
    public static LeiLookup getInstance(int cacheSize, int searchMissCacheSize) {
        if (instance != null && instance.cacheSize == cacheSize)
            return instance;

        instance = new LeiLookup(cacheSize, searchMissCacheSize);
        return instance;
    }

    private LeiLookup(int cacheSize, int searchMissCacheSize) {
        this.cacheSize = cacheSize;
        this.searchMissCacheSize = searchMissCacheSize;
        cache = new ConcurrentSkipListMap<>();
        searchMissCache = new ConcurrentSkipListMap<>();
    }

    /**
     * Get LEI entry by LEI code.
     * @param leiCode - LEI code
     * @return lei
     */
    public Optional<Lei> getLei(String leiCode) {
        return getLei(new String[] {leiCode}).stream().findFirst();
    }

    /**
     * Get LEI entries by LEI codes.
     * @param leiCode - List of LEI codes
     * @return List of LEI codes
     */
    public List<Lei> getLei(Set<String> leiCode) {
        return getLei(leiCode.toArray(String[]::new));
    }

    /**
     * Get LEI entries by LEI codes.
     * @param leiCodes - Array of LEI codes
     * @return List of LEI codes
     */
    public List<Lei> getLei(String... leiCodes) {
        return getLei(BASE_URL_LEI, Lei::getLeiCode, LeiCodeValidator::isValid, leiCodes);
    }

    /**
     * Get LEI entry by ISIN code.
     * @param isinCode - ISIN code
     * @return lei
     */
    public Optional<Lei> getLeiByIsin(String isinCode) {
        return getLei(BASE_URL_ISIN, lei -> isinCode, IsinCodeValidator::isValid, isinCode).stream().findFirst();
    }

    /**
     * Get LEI entry by BIC code.
     * @param bicCode - BIC code
     * @return lei
     */
    public Optional<Lei> getLeiByBic(String bicCode) {
        return getLei(BASE_URL_BIC, lei -> bicCode, BicCodeValidator::isValid, bicCode).stream().findFirst();
    }

    protected List<Lei> getLei(String url, Function<Lei, String> cacheKey, Function<String, Boolean> validator, String... codes) {
        List<String> searchForCodes = Arrays.stream(codes)
                .distinct()
                .filter(validator::apply)
                .filter(not(searchMissCache::containsKey))
                .filter(not(cache::containsKey))
                .collect(Collectors.toList());

        Set<String> notFound = new HashSet<>(searchForCodes);

        search(searchForCodes, url).forEach(lei -> {
            notFound.remove(cacheKey.apply(lei));
            put(cacheKey.apply(lei), lei);
        });

        notFound.forEach(this::put);

        return Arrays.stream(codes)
                .map(cache::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void put(String code, Lei lei) {
        cache.put(code, lei);
        if (cache.size() > cacheSize) {
            cache.pollLastEntry();
        }
    }

    private void put(String lei) {
        searchMissCache.put(lei, DEFAULT_VALUE);
        if (searchMissCache.size() > searchMissCacheSize) {
            searchMissCache.pollLastEntry();
        }
    }

    private List<Lei> search(List<String> leiCodes, String url) {
        String param = leiCodes.stream()
                               .distinct()
                               .collect(Collectors.joining(","));

        if (param.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init(null, null, null);

            HttpClient client = HttpClient.newBuilder()
                                          .sslContext(context)
                                          .followRedirects(HttpClient.Redirect.ALWAYS)
                                          .build();

            HttpRequest request = HttpRequest.newBuilder(URI.create(url + param))
                                             .header("Accept-Encoding", "gzip")
                                             .GET()
                                             .build();

            var resp = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            var inputStream = resp.body();

            if (Optional.of("gzip").equals(resp.headers().firstValue("Content-Encoding")))
                inputStream = new GZIPInputStream(inputStream);

            var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return parseResponse(new BufferedReader(reader));
        } catch (InterruptedException e) {
            var logger = Logger.getLogger(LOGGER);
            logger.severe(e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            var logger = Logger.getLogger(LOGGER);
            logger.severe(e.getMessage());
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("java:S3776")
    private List<Lei> parseResponse(BufferedReader reader) throws IOException {
        List<Lei> leiList = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(reader);

        if (jsonReader.peek() != JsonToken.BEGIN_OBJECT) {
            return Collections.emptyList();
        }

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("data".equals(name)) {

                jsonReader.beginArray();

                while (jsonReader.hasNext()) {
                    Lei lei = new Lei();
                    jsonReader.beginObject();

                    boolean isLeiRecord = false;
                    while (jsonReader.hasNext()) {
                        name = jsonReader.nextName();

                        if ("type".equals(name)) {
                            isLeiRecord = "lei-records".equals(jsonReader.nextString());
                        }
                        else if (isLeiRecord && "attributes".equals(name)) {
                            parseAttributes(jsonReader, lei);
                        } else {
                            jsonReader.skipValue();
                        }
                    }

                    if (lei.leiCode != null) {
                        leiList.add(lei);
                    }

                    jsonReader.endObject();
                }

                jsonReader.endArray();

            } else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();

        return leiList;
    }

    private void parseAttributes(JsonReader jsonReader, Lei lei) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if ("lei".equals(name)) {
                lei.leiCode = jsonReader.nextString();
            } else if ("entity".equals(name)) {
                parseEntity(jsonReader, lei);
            } else if ("registration".equals(name)) {
                lei.registration = parseRegistration(jsonReader);
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
    }

    @SuppressWarnings("squid:S3776")
    private void parseEntity(JsonReader jsonReader, Lei lei) throws IOException {
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("legalName".equals(name)) {
                lei.legalName = parseLegalName(jsonReader);
            }
            else if ("legalAddress".equals(name)) {
                lei.legalAddress = parseAddress(jsonReader);
            }
            else if ("headquartersAddress".equals(name)) {
                lei.headquartersAddress = parseAddress(jsonReader);
            }
            else if ("registeredAt".equals(name)) {
                Lei.RegistrationAuthority registrationAuthority = parseRegistrationAuthority(jsonReader);
                if (lei.registrationAuthority == null) {
                    lei.registrationAuthority = registrationAuthority;
                } else {
                    lei.registrationAuthority.registrationAuthorityID = registrationAuthority.registrationAuthorityID;
                }
            }
            else if ("registeredAs".equals(name)) {
                if (lei.registrationAuthority == null) {
                    Lei.RegistrationAuthority registrationAuthority = new Lei.RegistrationAuthority();
                    registrationAuthority.registrationAuthorityEntityID = jsonReader.nextString();
                    lei.registrationAuthority = registrationAuthority;
                } else {
                    lei.registrationAuthority.registrationAuthorityEntityID = jsonReader.nextString();
                }
            }
            else if ("jurisdiction".equals(name)) {
                lei.legalJurisdiction = jsonReader.nextString();
            }
            else if ("category".equals(name)) {
                String value = jsonReader.nextString();
                if ("BRANCH".equals(value))
                    lei.entityCategory = Lei.EntityCategory.BRANCH;
                else if ("FUND".equals(value))
                    lei.entityCategory = Lei.EntityCategory.FUND;
                else if ("SOLE_PROPRIETOR".equals(value))
                    lei.entityCategory = Lei.EntityCategory.SOLE_PROPRIETOR;
                else if ("GENERAL".equals(value))
                    lei.entityCategory = Lei.EntityCategory.GENERAL;
                else {
                    var logger = Logger.getLogger(LOGGER);
                    logger.severe("Unknown value for entity category: " + value);
                }
            }
            else if ("legalForm".equals(name)) {
                lei.entityLegalFormCode = parseLegalForm(jsonReader);
            }
            else if ("status".equals(name)) {
                String value = jsonReader.nextString();
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

    private String parseLegalName(JsonReader jsonReader) throws IOException {
        String legalName = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("name".equals(name)) {
                legalName = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return legalName;
    }

    @SuppressWarnings("java:S3776")
    private Lei.Address parseAddress(JsonReader jsonReader) throws IOException {
        Lei.Address legalAddress = new Lei.Address();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("addressLines".equals(name)) {
                jsonReader.beginArray();
                String firstLine = null;
                List<String> additionalAddressLine = new ArrayList<>();
                while (jsonReader.hasNext()) {
                    String value = jsonReader.nextString();
                    if (firstLine == null) {
                        firstLine = value;
                    } else {
                        additionalAddressLine.add(value);
                    }
                }
                legalAddress.firstAddressLine = firstLine;
                if (!additionalAddressLine.isEmpty()) {
                    legalAddress.additionalAddressLine = additionalAddressLine;
                }
                jsonReader.endArray();
            } else if ("postalCode".equals(name)) {
                legalAddress.postalCode = jsonReader.nextString();
            } else if ("region".equals(name)) {
                legalAddress.region = jsonReader.nextString();
            } else if ("city".equals(name)) {
                legalAddress.city = jsonReader.nextString();
            } else if ("country".equals(name)) {
                legalAddress.country = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return legalAddress;
    }

    private String parseLegalForm(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String entityLegalFormCode = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("id".equals(name)) {
                entityLegalFormCode = jsonReader.nextString();
            }
            else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return entityLegalFormCode;
    }

    private Lei.RegistrationAuthority parseRegistrationAuthority(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        Lei.RegistrationAuthority registrationAuthority = new Lei.RegistrationAuthority();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("id".equals(name)) {
                registrationAuthority.registrationAuthorityID = jsonReader.nextString();
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
            if ("initialRegistrationDate".equals(name)) {
                registration.initialRegistrationDate = jsonReader.nextString();
            }
            else if ("lastUpdateDate".equals(name)) {
                registration.lastUpdateDate = jsonReader.nextString();
            }
            else if ("status".equals(name)) {
                String value = jsonReader.nextString();
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
            else if ("nextRenewalDate".equals(name)) {
                registration.nextRenewalDate = jsonReader.nextString();
            }
            else if ("managingLou".equals(name)) {
                registration.managingLOU = jsonReader.nextString();
            }
            else if ("corroborationLevel".equals(name)) {
                String value = jsonReader.nextString();
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
            else if ("validatedAt".equals(name)) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    name = jsonReader.nextName();
                    if ("id".equals(name)) {
                        registration.validationAuthorityID = jsonReader.nextString();
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            }
            else if ("validatedAs".equals(name)) {
                registration.validationAuthorityEntityID = jsonReader.nextString();
            }
            else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return registration;
    }
}
