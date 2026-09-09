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

        throw new RuntimeException(
            "Unsupported document type"
        );
    }
    
    String json = handleQRData(qr);
    String normalizedJson = reportNormalizer.normalizeJSON(json);
    // System.out.println(normalizedJson.topre);
    JReportWriter.save(normalizedJson);
    return json; 
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








    private String handleQRData(
            String qrData
    ){


        System.out.println(
            "QR DATA:"
        );


        // System.out.println(qrData);



        if(qrData.startsWith("https://")){


            return webClient.fetch(qrData);

        }



        return qrData;


    }


}