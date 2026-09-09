
package app.core.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.core.models.CRReport;
import app.core.models.SummaryCRReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class CRRepoertService {

    private  Path reportDirectory;
    private final ObjectMapper mapper;

    public CRRepoertService() {
        // this.reportDirectory = reportDirectory;
        this.mapper = new ObjectMapper();
        setreportDirectory(                        
            Path.of("..", "sources/CR-Reports")
                                .toAbsolutePath()
                                .normalize()
                            
                            );
    }

    public void setreportDirectory(Path path){
        this.reportDirectory = path; 
    }

    public List<SummaryCRReport> getReports() {

        List<SummaryCRReport> reports = new ArrayList<>();

        if (!Files.exists(reportDirectory)) {
            return reports;
        }

        try (Stream<Path> files = Files.list(reportDirectory)) {

            files
                .filter(Files::isRegularFile)
                .filter(path ->
                    path.toString()
                        .toLowerCase()
                        .endsWith(".json"))
                .forEach(file -> {

                    SummaryCRReport report =
                            loadReport(file);

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

    private SummaryCRReport loadReport(Path file) {

        try {

            JsonNode root =
                    mapper.readTree(file.toFile());

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

            return new SummaryCRReport(
                    file,
                    crNumber,
                    companyName,
                    status,
                    root
            );

        } catch (IOException e) {

            System.err.println(
                "Failed to load: " + file
            );

            return null;
        }
    }

    public CRReport getReport(SummaryCRReport summaryReport) {

        return new CRReport(summaryReport.getFile());
    }
}

