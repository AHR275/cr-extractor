package app.core.documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

abstract public class FileDocument implements  Document {
    protected Path path ; 


    public FileDocument(Path path){
        this.path =path;
    }
    public FileDocument(){
        
    }

    @Override
    public String getName(){
        return path.getFileName().toString();
    }


    @Override
    public long getSize(){
        try {
            return Files.size(path);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getPath(){
        return path;
    }

}
