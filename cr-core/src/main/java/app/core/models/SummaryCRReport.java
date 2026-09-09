package app.core.models;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;

public class SummaryCRReport {

    private final Path file;
    private final String crNumber;
    private final String companyName;
    private final String status;
    private final JsonNode data;

    public SummaryCRReport(
            Path file,
            String crNumber,
            String companyName,
            String status,
            JsonNode data) {

        this.file = file;
        this.crNumber = crNumber;
        this.companyName = companyName;
        this.status = status;
        this.data = data;
    }

    public Path getFile() {
        return file;
    }

    public String getCrNumber() {
        return crNumber;
    }

    public String getEntityFullName() {
        return companyName;
    }

    public String getStatus() {
        return status;
    }

    public JsonNode getData() {
        return data;
    }
}


