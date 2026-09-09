package app.core.documents;


import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFDocument extends FileDocument {

   private final PDDocument document;


    public PDFDocument(Path path){
        super(path);
         try {

        this.document =
                Loader.loadPDF(
                    path.toFile()
                );

        } catch(IOException e){

            throw new RuntimeException(e);

        }
    }

    public PDDocument getPDF(){

        return this.document;

    }



    @Override
    public DocumentType getType(){
        return DocumentType.PDF;
    }



}