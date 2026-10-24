package app.desktop.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JFileChooser;

import app.config.AppConfig;
import app.core.export.ExportFormat;
import app.core.models.CRReport;
import app.core.models.ReportSearchCriteria;
import app.core.models.SummaryCRReport;
import app.core.models.UploadedFile;
import app.core.services.CRRepoertService;
import app.core.services.ExportingService;
import app.core.services.ExtractionService;
import app.core.services.UploadingFilesService;
import javafx.scene.Node;
import javafx.stage.FileChooser;

public class ReportsController {
    private final ExtractionService extractionService = new ExtractionService();
    private  final  UploadingFilesService ufs = new UploadingFilesService(); 
    private final ExportingService exs = new ExportingService();
    private final CRRepoertService reportService = new CRRepoertService();  

    private SummaryCRReport selectedReport;
    


    private final Path EXPORTED_REPORTS_PATH  = AppConfig.EXPORTED_REPORTS;
    public ReportsController(){

    }

    public void updateReport(String link){
           

        extractionService.updatingData(link);
    }


    public Path chooseDirPath(){
         JFileChooser chooser = new JFileChooser();
        
        // 2. Set the starting directory (optional - "." defaults to the project folder)
        chooser.setCurrentDirectory(EXPORTED_REPORTS_PATH.toFile());
        
        // 3. CRITICAL: Configure it to select folders/directories only
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // 4. Disable the "All Files" filter so users only see directories
        chooser.setAcceptAllFileFilterUsed(false);
        
        // 5. Open the dialog and capture the user's action
        int result = chooser.showOpenDialog(null);
        
        // 6. Handle the result
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            return Path.of(selectedFolder.getAbsolutePath());
        } else {
            System.out.println("No folder was selected.");
            return null;
        }
    }

    
public void exportingReport(
        Node source,
        Path reportPath,
        ExportFormat format
) {
    Path directory = chooseDirPath();

    // The user cancelled the directory selection.
    if (directory == null) {
        return;
    }

    String fileName =(reportPath.getParent().getParent().getFileName()+"-"+reportPath.getFileName())
            .toString();

    // Replace the existing extension with the export format.
    int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex > 0) {
        fileName = fileName.substring(0, dotIndex);
    }

    String extension = format.toString().toLowerCase();
    Path destination = directory.resolve(
            fileName + "." + extension
    );

    try {
        Path file = exs.export(reportPath, format, destination);

        System.out.println(
                "Report exported successfully: " + file
        );

    } catch (Exception e) {
        System.err.println(
                "Something went wrong: " + e.getMessage()
        );
        e.printStackTrace();
    }
}


public  File chooseFile(Node source) {

        FileChooser chooser =
                new FileChooser();

        chooser.getExtensionFilters()
               .add(
                    new FileChooser.ExtensionFilter(
                        "Documents",
                        "*.pdf",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                    )
               );

        return 
                chooser.showOpenDialog(
                        source.getScene().getWindow()
                );

        

    
    }


    public void uploadFile(Node source , Path reportPath ){

        File file = chooseFile(source);
        try {
            Path savedFile = ufs.saveFile(file, reportPath);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        


    }



// Return all cached report summaries.
public List<SummaryCRReport> getSummaryReports() {
    return reportService.getSummaryReports();
}

// Search cached summaries.
public List<SummaryCRReport> searchReports(
        ReportSearchCriteria criteria
) {
    return reportService.searchReports(criteria);
}

// Reload summaries after a report is created or updated.
// public void refreshSummaryReports() {
//     reportService.refreshSummaryReports();
// }

// Load the versions belonging to one summary report.
public List<CRReport> getReports(
        SummaryCRReport summaryReport
) {
    if (summaryReport == null) {
        return List.of();
    }

    return reportService.getReports(summaryReport);
}

// Load uploaded files for a report directory.
public List<UploadedFile> getUploadedFiles(Path reportDirectory) {
    if (reportDirectory == null) {
        return List.of();
    }

    return ufs.getUploadedFiles(reportDirectory);
}






    public SummaryCRReport getSelectedReport() {
        return selectedReport;
    }

    public void setSelectedReport(SummaryCRReport selectedReport) {
        this.selectedReport = selectedReport;

        System.out.println("selected report  : "+  selectedReport.getCompanyName());


        
    }

    
}


    




