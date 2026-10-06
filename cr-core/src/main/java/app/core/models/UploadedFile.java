package app.core.models;

import java.nio.file.Path;

public class UploadedFile {
    private String name ; 
    private String extension ; 
    private Path path ; 
    private long size ; 

    public UploadedFile(String n  , Path p , long s) {
        int dotIndex = n.lastIndexOf('.');
        String nameWithoutExtension = (dotIndex == -1) ? n : n.substring(0, dotIndex);
        this.name = nameWithoutExtension ; 
        this.extension = n.substring(dotIndex); 
        this.path = p; 
        this.size = s; 
    }

    public String getName(){
        return name ; 
    }

    public String getExtension(){
        return extension;
    }

    public String getFullName(){
        return name+"."+extension; 

    }

    public long getSize(){
        return size ; 

    }

    public Path getPath(){
        return path; 
    }

    

}
