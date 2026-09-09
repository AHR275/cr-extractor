package app.core.documents;

import java.nio.file.Path;


public   interface Document {

    Path getPath();

    String getName();

    DocumentType getType();

    long getSize();

}