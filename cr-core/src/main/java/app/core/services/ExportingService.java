package app.core.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import app.core.export.ExportFormat;
import app.core.export.Exporter;
import app.core.export.PDFExporter;
import app.core.export.XLSXEporter;

public class ExportingService {

    private final Map<ExportFormat, Exporter> exporters =
            new EnumMap<>(ExportFormat.class);

    public ExportingService() {
        registerExporter(new XLSXEporter());
        registerExporter(new PDFExporter());
    }

    /**
     * Registers an exporter for a supported format.
     */
    public void registerExporter(Exporter exporter) {

        Objects.requireNonNull(exporter, "Exporter cannot be null");

        ExportFormat format = Objects.requireNonNull(
                exporter.getFormat(),
                "Exporter format cannot be null"
        );

        if (exporters.containsKey(format)) {
            throw new IllegalArgumentException(
                    "An exporter is already registered for " + format
            );
        }

        exporters.put(format, exporter);
    }

    /**
     * Exports a JSON report into the requested format.
     *
     * @param source      path to the JSON report
     * @param format      desired export format
     * @param destination path where the exported file will be saved
     * @return path to the generated file
     */
    public Path export(
            Path source,
            ExportFormat format,
            Path destination
    ) throws IOException {

        Objects.requireNonNull(source, "Source cannot be null");
        Objects.requireNonNull(format, "Format cannot be null");
        Objects.requireNonNull(
                destination,
                "Destination cannot be null"
        );

        Exporter exporter = exporters.get(format);

        if (exporter == null) {
            throw new UnsupportedOperationException(
                    "Export format is not supported: " + format
            );
        }

        String expectedExtension =
                "." + format.name().toLowerCase(Locale.ROOT);

        String filename = destination.getFileName().toString();

        if (!filename.toLowerCase(Locale.ROOT)
                .endsWith(expectedExtension)) {
            throw new IllegalArgumentException(
                    "Destination must end with "
                            + expectedExtension
                            + "; received: " + filename
            );
        }

        return exporter.export(source, destination);
    }
}