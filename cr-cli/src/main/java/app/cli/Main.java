package app.cli;


import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import app.core.documents.Document;
import app.core.documents.DocumentLoader;
import app.core.documents.ImageDocument;
import app.core.documents.PDFDocument;
import app.core.extraction.PDFExtractor;
import app.core.extraction.QRExtractor;
import app.core.web.WebClient;


public class Main {


    public static void main(String[] args){

        URL url =
            Main.class
                .getClassLoader()
                .getResource("cr.pdf");


        if(url == null){
            throw new RuntimeException("PDF not found");
        }


        String path =
        URLDecoder.decode(
            url.toString()
            .substring(url.toString().indexOf(":") + 2),
            StandardCharsets.UTF_8
        );
        Path filepath = Path.of(path);

        DocumentLoader loader =
                new DocumentLoader();


        Document doc =
                loader.load(
                    Path.of(path)
                );


        System.out.println(doc.getName());

        System.out.println(doc.getType());

        System.out.println(doc.getSize());










        DocumentLoader loader1 =
        new DocumentLoader();


Document document =
        loader1.load(
            Path.of(path)
        );


PDFDocument pdf =
        (PDFDocument) document;


    PDFExtractor pdfExtractor = new PDFExtractor(); 
    
    List<ImageDocument> images = pdfExtractor.extractPages(pdf);


    QRExtractor extractor =
            new QRExtractor();
            WebClient webClient = new WebClient() ; 
    for (int i = 0; i < images.size(); i++) {
        
        String data =
                extractor.extract(images.get(i));
        System.out.println(data);
        if(data.startsWith("https://")){

            String html = webClient.fetch(data);
        
        System.out.println(html);
        }
    }







    }

}