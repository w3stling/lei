package com.apptasticsoftware.lei;

import org.jsoup.Jsoup;
import org.jsoup.Connection;

import javax.net.ssl.SSLContext;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * ISIN lookup from CUSIP and SEDOL
 */
public class IsinLookup {
    private static final String LOGGER = "com.apptasticsoftware.lei";
    private static final String CUSIP_URL = "https://www.isindb.com/action/c.php";
    private static final String SEDOL_URL = "https://www.isindb.com/action/d.php";
    private final int cacheSize;
    private final ConcurrentSkipListMap<String, String> cache;
    private final List<BiFunction<String, String, String>> SEND_REQUEST_FUNCTIONS;


    /**
     * Constructor
     */
    public IsinLookup() {
        this(500000);
    }

    /**
     * Constructor
     * @param cacheSize cache size
     */
    public IsinLookup(int cacheSize) {
        SEND_REQUEST_FUNCTIONS = List.of(this::sendRequest1, this::sendRequest2);
        cache = new ConcurrentSkipListMap<>();
        this.cacheSize = cacheSize;
    }

    /**
     * Returns ISIN number from CUSIP
     * @param cusip CUSIP
     * @return ISIN
     */
    public Optional<String> getIsinByCusip(String cusip) {
        if (!CusipValidator.isValid(cusip)) {
            return Optional.empty();
        }

        return SEND_REQUEST_FUNCTIONS.stream()
                .map(f -> {
                    var isin = f.apply(CUSIP_URL, "cusip=US" + cusip);
                    if (isin != null) {
                        return isin;
                    }

                    isin = f.apply(CUSIP_URL, "cusip=CA" + cusip);
                    if (isin != null) {
                        return isin;
                    }

                    return f.apply(CUSIP_URL, "cusip=BM" + cusip);
                })
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Returns ISIN number from SEDOL
     * @param sedol SEDOL
     * @return ISIN
     */
    public Optional<String> getIsinBySedol(String sedol) {
        if (!SedolValidator.isValid(sedol)) {
            return Optional.empty();
        }

        return SEND_REQUEST_FUNCTIONS.stream()
                .map(f -> {
                        String isin = f.apply(SEDOL_URL, "sedol=GB" + sedol);
                        if (isin != null) {
                          return isin;
                        }

                        return f.apply(SEDOL_URL, "sedol=IE" + sedol);
                    })
                .filter(Objects::nonNull)
                .findFirst();
    }

    String sendRequest1(String url, String data) {
        var res = cache.get(data);
        if (res != null) {
            return res;
        }

        var referer = CUSIP_URL.equals(url) ? "https://www.isindb.com/convert-cusip-to-isin/" : "https://www.isindb.com/convert-sedol-to-isin/";

        try {
            var request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(data))
                    .header("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("accept", "*/*")
                    .header("accept-encoding","gzip, deflate")
                    .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8,sv;q=0.7")
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("origin", "https://www.isindb.com")
                    .header("referer", referer)
                    .header("x-requested-with","XMLHttpRequest")
                    .build();

            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init(null, null, null);

            var response = HttpClient.newBuilder()
                    .sslContext(context)
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());

            var inputStream = response.body();

            if (Optional.of("gzip").equals(response.headers().firstValue("Content-Encoding"))) {
                inputStream = new GZIPInputStream(inputStream);
            }

            var text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            var index = text.indexOf("<strong>");
            if (index != -1) {
                var isin = text.substring(index + 8, index + 20);
                cacheResult(data, isin);
                return isin;
            }

            index = text.indexOf("data-clipboard-text=\"");
            if (index != -1) {
                var isin = text.substring(index + 21, index + 33);
                cacheResult(data, isin);
                return isin;
            }
        } catch (Exception e) {
            var logger = Logger.getLogger(LOGGER);
            logger.severe(e.getMessage());
        }

        return null;
    }

    String sendRequest2(String url, String data) {
        var res = cache.get(data);
        if (res != null) {
            return res;
        }

        var referer = CUSIP_URL.equals(url) ? "https://www.isindb.com/convert-cusip-to-isin/" : "https://www.isindb.com/convert-sedol-to-isin/";

        try {
            Connection.Response loginForm = Jsoup.connect(referer)
                    .method(Connection.Method.GET)
                    .followRedirects(true)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .execute();

            String[] dataParam = data.split("=");
            var response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("accept", "*/*")
                    .header("accept-encoding", "gzip, deflate")
                    .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8,sv;q=0.7")
                    .header("content-length", String.valueOf(data.length()))
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("origin", "https://www.isindb.com")
                    .header("referer", referer)
                    .header("x-requested-with", "XMLHttpRequest")
                    .method(Connection.Method.POST)
                    .data(dataParam[0], dataParam[1])
                    .cookies(loginForm.cookies())
                    .followRedirects(true)
                    .execute();

            var document = response.parse();
            var text = document.body().html();
            var index = text.indexOf("<strong>");
            if (index != -1) {
                var isin = text.substring(index + 8, index + 20);
                cacheResult(data, isin);
                return isin;
            }

            index = text.indexOf("data-clipboard-text=\"");
            if (index != -1) {
                var isin = text.substring(index + 21, index + 33);
                cacheResult(data, isin);
                return isin;
            }
        } catch (Exception e) {
            var logger = Logger.getLogger(LOGGER);
            logger.severe(e.getMessage());
        }

        return null;
    }

    private void cacheResult(String key, String value) {
        if (cache.containsKey(key)) {
            return;
        }
        cache.put(key, value);
        if (cache.size() > cacheSize) {
            cache.pollLastEntry();
        }
    }
}
