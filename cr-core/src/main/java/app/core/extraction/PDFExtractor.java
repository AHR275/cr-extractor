package app.core.extraction;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.rendering.PDFRenderer;

import app.core.documents.ImageDocument;
import app.core.documents.PDFDocument;



public class PDFExtractor {



    private final int dpi;



    public PDFExtractor(){

        this(300);

    }



    public PDFExtractor(int dpi){

        this.dpi = dpi;

    }





    public List<ImageDocument> extractPages(
            PDFDocument document
    ){


        List<ImageDocument> pages =
                new ArrayList<>();


        try {


            PDFRenderer renderer =
                    new PDFRenderer(
                        document.getPDF()
                    );



            int pageCount =
                    document.getPDF()
                            .getNumberOfPages();



            for(int i = 0; i < pageCount; i++){


                BufferedImage image =
                        renderer.renderImageWithDPI(
                            i,
                            this.dpi
                        );



                pages.add(
                    new ImageDocument(image)
                );

            }



        } catch(IOException e){


            throw new RuntimeException(
                "Failed to extract PDF pages",
                e
            );

        }



        return pages;

    }

}