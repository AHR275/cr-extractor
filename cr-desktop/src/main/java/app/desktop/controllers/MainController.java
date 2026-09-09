package app.desktop.controllers;


import java.io.File;

import app.core.services.ExtractionService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;



public class MainController {


    private VBox root;


    private Label fileLabel;


    private TextArea resultArea;


    private File selectedFile;



    private ExtractionService service;





    public MainController(){


        service =
            new ExtractionService();




        Button chooseButton =
                new Button(
                    "Choose a  Document"
                );



        Button extractButton =
                new Button(
                    "Extract"
                );



        fileLabel =
                new Label(
                    "No file selected"
                );



        resultArea =
                new TextArea();



        resultArea.setEditable(false);




        chooseButton.setOnAction(e -> chooseFile());



        extractButton.setOnAction(e -> extract());




        root =
            new VBox(
                15,
                chooseButton,
                fileLabel,
                extractButton,
                resultArea
            );


    }






    private void chooseFile(){


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



        selectedFile =
            chooser.showOpenDialog(null);



        if(selectedFile != null){


            fileLabel.setText(
                selectedFile.getAbsolutePath()
            );

        }

    }







    private void extract(){


        if(selectedFile == null){


            resultArea.setText(
                "Please select a file first"
            );


            return;

        }




        try {


            String result =
                service.extract(
                    selectedFile.toPath()
                );



            resultArea.setText(result);



        } catch(Exception e){


            resultArea.setText(
                e.getMessage()
            );

        }


    }







    public VBox getView(){

        return root;

    }


}