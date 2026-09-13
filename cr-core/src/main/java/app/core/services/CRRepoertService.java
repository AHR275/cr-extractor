package app.core.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.config.AppConfig;
import app.core.models.CRReport;
import app.core.models.SummaryCRReport;

public class CRRepoertService {

    private Path reportDirectory;
    private final ObjectMapper mapper;

    public CRRepoertService() {

        this.mapper = new ObjectMapper();

        setReportDirectory(AppConfig.REPORTS);
    }

    public void setReportDirectory(Path path) {
        this.reportDirectory = path;
    }

    public List<SummaryCRReport> getSummaryReports() {

        List<SummaryCRReport> reports = new ArrayList<>();

        if (!Files.exists(reportDirectory)) {
            return reports;
        }

        try (Stream<Path> directories = Files.list(reportDirectory)) {

            directories
                .filter(Files::isDirectory)
                .forEach(directory -> {

                    SummaryCRReport report =
                            loadReport(Path.of(directory.toString()+ "/versions"));

                    if (report != null) {
                        reports.add(report);
                    }
                });

        } catch (IOException e) {

            throw new RuntimeException(
                "Failed to load CR reports",
                e
            );
        }

        return reports;
    }

    private SummaryCRReport loadReport(Path directory) {

        try {

            Path latestFile = findLatestCopy(directory);

            if (latestFile == null) {
                return null;
            }

            JsonNode root =
                    mapper.readTree(latestFile.toFile());

            JsonNode information =
                    root.get("data")
                        .get("crInformation");

            String crNumber =
                    information
                        .get("crNumber")
                        .get("values")
                        .get("en")
                        .asText();

            String companyName =
                    information
                        .get("entityFullName")
                        .get("values")
                        .get("en")
                        .asText();

            String status =
                    information
                        .get("crStatus")
                        .get("crStatusDesc")
                        .get("values")
                        .get("en")
                        .asText();
            String encNumber = 
                root
                .get("link")

                .asText(); 

            return new SummaryCRReport(
                    directory,   // important: DIRECTORY
                    crNumber,
                    companyName,
                    status,
                    encNumber,
                    root
            );

        } catch (IOException e) {

            System.err.println(
                "Failed to load: " + directory
            );

            return null;
        }
    }

    public CRReport getReport(Path path) {

        return new CRReport(path);
    }
public List<CRReport> getReports(
        SummaryCRReport summaryReport
) {

    List<CRReport> copies = new ArrayList<>();

    Path directory = summaryReport.getDirectory();

    System.out.println("Directory: " + directory);

    if (!Files.exists(directory)) {
        System.out.println("Directory does not exist!");
        return copies;
    }

    try (Stream<Path> files = Files.list(directory)) {

        files
            .filter(Files::isRegularFile)
            .filter(path ->
                path.toString()
                    .toLowerCase()
                    .endsWith(".json")
            )
            .sorted(
                Comparator.comparingInt(
                    this::getCopyNumber
                )
            )
            .forEach(file -> {

                System.out.println(
                    "Loading: " + file
                );

                copies.add(
                    new CRReport(file)
                );
            });

    } catch (IOException e) {

        e.printStackTrace();

        throw new RuntimeException(
            "Failed to load report copies",
            e
        );
    }

    return copies;
}

private Path findLatestCopy(Path directory)
            throws IOException {

        try (Stream<Path> files = Files.list(directory)) {

            return files
                .filter(Files::isRegularFile)
                .filter(path ->
                    path.toString()
                        .toLowerCase()
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

    private int getCopyNumber(Path file) {

        String filename =
                file.getFileName().toString();

        int dashIndex =
                filename.indexOf('-');

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