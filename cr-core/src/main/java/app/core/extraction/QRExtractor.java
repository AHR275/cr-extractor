package app.core.extraction;


import java.awt.image.BufferedImage;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import app.core.documents.ImageDocument;



public class QRExtractor {



    public String extract(ImageDocument document) {


        try {


            BufferedImage image =
                    document.getImage();



            BufferedImageLuminanceSource source =
                    new BufferedImageLuminanceSource(
                        image
                    );



            BinaryBitmap bitmap =
                    new BinaryBitmap(
                        new HybridBinarizer(
                            source
                        )
                    );



            Result result =
                    new MultiFormatReader()
                        .decode(bitmap);



            return result.getText();



        } catch(Exception e) {


            throw new RuntimeException(
                "QR code not found or unreadable",
                e
            );

        }

    }


}