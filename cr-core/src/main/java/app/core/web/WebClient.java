package app.core.web;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WebClient {

    private final HttpClient client;

    private static final String API_BASE =
            "https://api.saudibusiness.gov.sa/"
          + "sbc/externalgw/qrapi-nl/api/app/Qr/GetCrDetails";

    private static final String CLIENT_ID =
            "290644af026afba41b89aefcee65843d";

    public WebClient() {
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public String fetch(String qrUrl) {

        String encryptedCrNumber =
                extractEncryptedCrNumber(qrUrl);

        String encodedCrNumber =
                URLEncoder.encode(
                        encryptedCrNumber,
                        StandardCharsets.UTF_8
                );

        String apiUrl =
                API_BASE
                + "?mcEncryptedCrNumber="
                + encodedCrNumber;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Accept", "*/*")
                        .header("Accept-Language", "ar")
                        .header("Authorization", "Bearer undefined")
                        .header("ClientId", CLIENT_ID)
                        .header(
                                "Origin",
                                "https://qr.saudibusiness.gov.sa"
                        )
                        .header(
                                "Referer",
                                "https://qr.saudibusiness.gov.sa/"
                        )
                        .header(
                                "User-Agent",
                                "Mozilla/5.0"
                        )
                        .header(
                                "X-Correlation-Id",
                                UUID.randomUUID().toString()
                        )
                        .GET()
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println(
                    "API status: " + response.statusCode()
            );

            System.out.println(
                    "API response: " + response.body()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "GetCrDetails failed: "
                        + response.statusCode()
                        + "\n"
                        + response.body()
                );
            }

            return response.body();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Request interrupted",
                    e
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to fetch CR details",
                    e
            );
        }
    }

    private String extractEncryptedCrNumber(String url) {

        String query = URI.create(url).getQuery();

        if (query == null) {
            throw new RuntimeException(
                    "QR URL does not contain query parameters"
            );
        }

        for (String parameter : query.split("&")) {

            String[] parts = parameter.split("=", 2);

            if (parts.length == 2
                    && parts[0].equals("nCrNumber")) {

                return URLDecoder.decode(
                        parts[1],
                        StandardCharsets.UTF_8
                );
            }
        }

        throw new RuntimeException(
                "nCrNumber not found in QR URL"
        );
    }
}