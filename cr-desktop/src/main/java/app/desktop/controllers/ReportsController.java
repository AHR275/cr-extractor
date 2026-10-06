package app.desktop.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import app.core.services.ExtractionService;
import app.core.services.UploadingFilesService;
import javafx.scene.Node;
import javafx.stage.FileChooser;

public class ReportsController {
    private final ExtractionService extractionService = new ExtractionService();
    private  final  UploadingFilesService ufs = new UploadingFilesService(); 
    public ReportsController(){

    }

    public void updateReport(String link){
           

        extractionService.updatingData(link);
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


    



}
