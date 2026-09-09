package  app.core.documents; 
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO; 
public class ImageDocument extends  FileDocument {

    


    private final BufferedImage image;


    public ImageDocument(Path path){
        super(path);
        try {

            this.image =
                ImageIO.read(getPath().toFile());

        } catch(IOException e){

            throw new RuntimeException(e);

        }

    }

    public ImageDocument(BufferedImage image){

        this.image = image;
        

    }


    public BufferedImage getImage(){

        return this.image;

    }

    @Override
    public DocumentType getType(){
        return DocumentType.IMAGE;
    }



}