package app.core.parsing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import app.config.AppConfig;

public class JsonReportWriter {

    private final ObjectMapper mapper;

    private final Path reportDirectory = AppConfig.REPORTS;

    public JsonReportWriter() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path save(String json) {

        try {
            JsonNode root = mapper.readTree(json);

            String crNumber = root
                    .path("data")
                    .path("crInformation")
                    .path("crNationalNumber")
                    .path("values")
                    .path("en")
                    .asText(null);

            if (crNumber == null || crNumber.isBlank()) {
                throw new RuntimeException(
                        "CR number not found in response"
                );
            }

            // One folder for each CR
            Path crDirectory = reportDirectory.resolve(crNumber+"/versions");
            Path crDirectoryUploads = reportDirectory.resolve(crNumber+"/uploads");

            Files.createDirectories(crDirectory);
            Files.createDirectories(crDirectoryUploads);

            // Find latest saved report
            Path latestReport = findLatestReport(crDirectory);

            // If a previous report exists, compare it
            if (latestReport != null) {

                JsonNode previousRoot =
                        mapper.readTree(latestReport.toFile());

                if (previousRoot.equals(root)) {

                    // No data changed -> overwrite latest report
                    mapper.writeValue(
                            latestReport.toFile(),
                            root
                    );

                    return latestReport;
                }
            }

            // Data changed -> create a new copy
            int nextCopyNumber =
                    getNextCopyNumber(crDirectory);

            String date = LocalDate.now().toString();

            Path newReport = crDirectory.resolve(
                    nextCopyNumber + "-" + date + ".json"
            );

            mapper.writeValue(
                    newReport.toFile(),
                    root
            );

            return newReport;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save CR report",
                    e
            );
        }
    }


    private Path findLatestReport(Path directory)
            throws IOException {

        try (Stream<Path> files = Files.list(directory)) {

            return files
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                .toString()
                                .endsWith(".json")
                    )
                    .max(
                        Comparator.comparingInt(
                            this::getCopyNumber
                        )
                    )
                    .orElse(null);
        }
    }


    private int getNextCopyNumber(Path directory)
            throws IOException {

        try (Stream<Path> files = Files.list(directory)) {

            return files
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                .toString()
                                .endsWith(".json")
                    )
                    .mapToInt(this::getCopyNumber)
                    .max()
                    .orElse(0) + 1;
        }
    }


    private int getCopyNumber(Path path) {

        String filename =
                path.getFileName().toString();

        int dashIndex = filename.indexOf('-');

        if (dashIndex == -1) {
            return 0;
        }

        try {
            return Integer.parseInt(
                    filename.substring(0, dashIndex)
            );

        } catch (NumberFormatException e) {
            return 0;
        }
    }
}