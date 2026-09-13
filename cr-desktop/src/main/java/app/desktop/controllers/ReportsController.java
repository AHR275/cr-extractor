package app.desktop.controllers;

import app.config.AppConfig;
import app.core.services.ExtractionService;

public class ReportsController {
    private final ExtractionService extractionService = new ExtractionService();
    public ReportsController(){

    }

    public void updateReport(String link){
           

        extractionService.updatingData(link);
    }
}
