package app.core.services;


import java.nio.file.Path;
import java.util.List;

import app.core.documents.Document;
import app.core.documents.DocumentLoader;
import app.core.documents.ImageDocument;
import app.core.documents.PDFDocument;
import app.core.extraction.PDFExtractor;
import app.core.extraction.QRExtractor;
import app.core.normalizer.ReportNormalizer;
import app.core.parsing.JsonReportWriter;
import app.core.web.WebClient;



public class ExtractionService {


    private final DocumentLoader loader;

    private final PDFExtractor pdfExtractor;

    private final QRExtractor qrExtractor;

    private final WebClient webClient;

    private final JsonReportWriter JReportWriter ; 
    private  final ReportNormalizer reportNormalizer ; 



    public ExtractionService(){


        loader =
            new DocumentLoader();


        pdfExtractor =
            new PDFExtractor();


        qrExtractor =
            new QRExtractor();


        webClient =
            new WebClient();
  

        JReportWriter = new JsonReportWriter(); 
        reportNormalizer = new ReportNormalizer() ; 

    }



public String extract(Path file) {

    Document document = loader.load(file);

    String qr;

    if (document instanceof ImageDocument image) {
        qr = processImage(image);

    } else if (document instanceof PDFDocument pdf) {
        qr = processPDF(pdf);

    } else {
        throw new RuntimeException("Unsupported document type");
    }

    return updatingData(qr);
}

public String updatingData(String link) {

    try {
        String json = callJsonFromLink(link);

        String normalizedJson =
                reportNormalizer.normalizeJSON(json,link);

        JReportWriter.save(normalizedJson);

        return "Data has been saved , please check Reports page ";

    } catch (Exception e) {
        throw new RuntimeException("Couldn't save the data", e);
    }
}








    private String processImage(
            ImageDocument image
    ){


        String qrData =
                qrExtractor.extract(image);


        return qrData; 

    }








    private String processPDF(
            PDFDocument pdf
    ){


        List<ImageDocument> pages =
                pdfExtractor.extractPages(pdf);



        for(ImageDocument page : pages){


            try {


                String qrData =
                        qrExtractor.extract(page);


                return qrData;



            } catch(Exception ignored){

                // try next page

            }

        }



        throw new RuntimeException(
            "No QR code found in PDF"
        );


    }








    private String callJsonFromLink(
            String link
    ){


        System.out.println(
            "Link DATA:"
        );


        System.out.println(link);



        if(link.startsWith("https://")){


            return webClient.fetch(link);

        }



        return link;


    }

    


}