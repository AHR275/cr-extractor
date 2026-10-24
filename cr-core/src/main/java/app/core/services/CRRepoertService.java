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

import java.text.Normalizer;
import java.util.Locale;

import app.core.models.ReportSearchCriteria;

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
                        .get("crNationalNumber")
                        .get("values")
                        .get("en")
                        .asText();

            String companyName =
                    information
                        .get("entityFullName")
                        .get("values")
                        .get("en")
                        .asText();
            String OwnerName = 
                information 
                    .get("ownerName")
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
                    directory.getParent(),   // important: DIRECTORY
                    crNumber,
                    companyName,
                    OwnerName,
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

    Path directory = summaryReport.getDirectory().resolve("versions");


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




// Add these methods inside CRRepoertService.

public List<SummaryCRReport> searchReports(
        ReportSearchCriteria criteria
) {
    if (criteria == null) {
        criteria = ReportSearchCriteria.empty();
    }

    String query = normalize(criteria.query());
    String crNumber = normalize(criteria.crNumber());
    String companyName = normalize(criteria.companyName());
    String status = normalize(criteria.status());

    return getSummaryReports().stream()
            .filter(report ->
                    query.isEmpty()
                    || contains(report.getCrNumber(), query)
                    || contains(report.getCompanyName(), query)
                    || contains(report.getStatus(), query)
            )
            .filter(report ->
                    crNumber.isEmpty()
                    || contains(report.getCrNumber(), crNumber)
            )
            .filter(report ->
                    companyName.isEmpty()
                    || contains(report.getCompanyName(), companyName)
            )
            .filter(report ->
                    status.isEmpty()
                    || contains(report.getStatus(), status)
            )
            .toList();
}

private boolean contains(String value, String query) {
    return normalize(value).contains(query);
}

private String normalize(String value) {
    if (value == null || value.isBlank()) {
        return "";
    }

    return Normalizer.normalize(value, Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT)
            .strip()
            .replace('\u0640', ' ')
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ى', 'ي')
            .replaceAll("\\s+", " ");
}

}