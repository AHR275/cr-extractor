package app.core.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import app.core.models.UploadedFile;

public class UploadingFilesService {

    public UploadingFilesService(){

    }

    public Path saveFile(File file, Path reportPath) throws IOException {
        
        Path uploadsPath = reportPath.resolve("uploads");

        Files.createDirectories(uploadsPath);

        Path destination = uploadsPath.resolve(file.getName());

        Files.copy(
            file.toPath(),
            destination,
            StandardCopyOption.REPLACE_EXISTING
        );

        return destination;
    }
public List<UploadedFile> getUploadedFiles(Path reportPath) {

    List<UploadedFile> ufs = new ArrayList<>();
    try {
        
        Files.createDirectories(reportPath.resolve("uploads"));
    } catch (Exception e) {
        System.out.println("wrong : "+ e.getMessage());
    }

    Path uploadsPath = reportPath.resolve("uploads");

    try (Stream<Path> files = Files.list(uploadsPath)) {

        for (Path file : (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {

            ufs.add(
                new UploadedFile(
                    file.getFileName().toString(),
                    file,
                    Files.size(file)
                )
            );
        }

    } catch (IOException e) {
        System.out.println("Something went wrong: " + e.getMessage());
    }

    return ufs;
}
    
}