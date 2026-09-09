package app.core.documents;


import java.nio.file.Files;
import java.nio.file.Path;





public class DocumentLoader {

   
    public Document load(Path path) {


        validate(path);


        String extension =
                getExtension(path);



        return switch(extension) {


            case "pdf" ->
                    new PDFDocument(path);


            case "png",
                 "jpg",
                 "jpeg" ->
                    new ImageDocument(path);


            default ->
                    throw new IllegalArgumentException(
                        "Unsupported document type: "
                        + extension
                    );

        };

    }





    private void validate(Path path) {


        if(path == null) {

            throw new IllegalArgumentException(
                "Path cannot be null"
            );

        }


        if(!Files.exists(path)) {

            throw new IllegalArgumentException(
                "File does not exist: " + path
            );

        }


        if(!Files.isRegularFile(path)) {

            throw new IllegalArgumentException(
                "Not a file: " + path
            );

        }

    }





    private String getExtension(Path path) {


        String name =
                path.getFileName()
                    .toString();


        int index =
                name.lastIndexOf(".");


        if(index == -1) {

            return "";

        }


        return name
                .substring(index + 1)
                .toLowerCase();

    }


}