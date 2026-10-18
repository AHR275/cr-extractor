package app.core.export;

import java.io.IOException;
import java.nio.file.Path;

public interface Exporter {

    ExportFormat getFormat();

    Path export(Path source, Path destination) throws IOException;
}