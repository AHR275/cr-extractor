package app.core.models;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;

public class SummaryCRReport {

    private final Path directory;
    private final String crNumber;
    private final String companyName;
    private final String ownerName ; 
    private final String status;
    private final String encNumber ; 
    private final JsonNode data;

    public SummaryCRReport(
            Path directory,
            String crNumber,
            String companyName,
            String ownerName,
            String status,
            String encNumber, 
            JsonNode data) {

        this.directory = directory;
        this.crNumber = crNumber;
        this.companyName = companyName;
        this.status = status;
        this.data = data;
        this.encNumber = encNumber; 
        this.ownerName = ownerName; 
    }

    public Path getDirectory() {
        return directory;
    }

    public String getCrNumber() {
        return crNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getOwnerName(){
        return ownerName; 
    }

    public String getStatus() {
        return status;
    }
    public String getEncNumber() {
        return encNumber;
    }


    public JsonNode getData() {
        return data;
    }
}