package app.core.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import app.core.normalizer.XLSXNormalizer;
import app.core.normalizer.XLSXNormalizer.XlsxCell;
import app.core.normalizer.XLSXNormalizer.XlsxField;
import app.core.normalizer.XLSXNormalizer.XlsxSheet;
import app.core.normalizer.XLSXNormalizer.XlsxTable;
import app.core.normalizer.XLSXNormalizer.XlsxWorkbook;


import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.TextDirection;



public class PDFExporter implements Exporter {

    private final ObjectMapper mapper = new ObjectMapper();
    private final XLSXNormalizer normalizer = new XLSXNormalizer();

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.PDF;
    }

    @Override
    public Path export(Path source, Path destination)
            throws IOException {

        JsonNode report = mapper.readTree(source.toFile());
       
        XlsxWorkbook workbook = normalizer.normalize(report);

        destination = checkIfPathExists(destination);

        // Calls the PDF-rendering method from the previous implementation.
        return exportWorkbook(workbook, destination);
    }


    public Path exportWorkbook(XlsxWorkbook workbook, Path destination)
            throws IOException {

        if (workbook == null) {
            throw new IllegalArgumentException(
                    "Workbook cannot be null"
            );
        }

        if (destination == null) {
            throw new IllegalArgumentException(
                    "Destination cannot be null"
            );
        }

        if (!destination.getFileName()
                .toString()
                .toLowerCase()
                .endsWith(".pdf")) {

            throw new IllegalArgumentException(
                    "Destination must end with .pdf"
            );
        }

Path outputPath = destination.toAbsolutePath().normalize();
Path parent = outputPath.getParent();

if (parent != null) {
    Files.createDirectories(parent);
}

String html = buildHtml(workbook);

Path tempPath = Files.createTempFile(
        parent,
        "cr-report-",
        ".pdf"
);

try {
    try (OutputStream output = Files.newOutputStream(tempPath)) {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        // builder.withHtmlContent(htmlContent, null);


        builder.useUnicodeBidiSplitter(
            new ICUBidiSplitter.ICUBidiSplitterFactory()
        );

        builder.useUnicodeBidiReorderer(
            new ICUBidiReorderer()
        );

        builder.defaultTextDirection(TextDirection.LTR);


        builder.useFont(
    () -> {
        var stream = getClass().getResourceAsStream(
            "/fonts/NotoSansArabic.ttf"
        );

        if (stream == null) {
            throw new IllegalStateException(
                "Arabic font not found: /fonts/NotoSansArabic.ttf"
            );
        }
        else { 
            System.out.println("found the font : "+ stream);
        }

        return stream;
    },
    "Noto Sans Arabic"
);


        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(output);
        builder.run();
    }

    long size = Files.size(tempPath);

    if (size == 0) {
        throw new IOException("PDF renderer produced an empty file");
    }

    try (var input = Files.newInputStream(tempPath)) {
        String signature = new String(
                input.readNBytes(5),
                java.nio.charset.StandardCharsets.US_ASCII
        );

        if (!"%PDF-".equals(signature)) {
            throw new IOException(
                    "Invalid PDF signature: " + signature
            );
        }
    }

    Files.move(
            tempPath,
            outputPath,
            java.nio.file.StandardCopyOption.REPLACE_EXISTING
    );

    System.out.println("PDF created: " + outputPath);
    System.out.println("PDF size: " + Files.size(outputPath) + " bytes");

    return outputPath;

} catch (Exception e) {
    throw new IOException("Failed to export PDF", e);
} finally {
    Files.deleteIfExists(tempPath);
}

    }

    private String buildHtml(XlsxWorkbook workbook) {
        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8"/>
                    <style>
                        @page {
                            size: A4;
                            margin: 18mm 14mm;
                        }

                        body {
                            font-family: "Noto Sans Arabic", sans-serif;
                            font-size: 10pt;
                            color: #222;
                        }

                        .ar {
                            font-family: "Noto Sans Arabic", sans-serif;
                            direction: rtl;
                            text-align: right;
                        }

                        .en {
                            direction: ltr;
                            text-align: left;
                        }

                        h1 {
                            font-size: 20pt;
                            text-align: center;
                            margin-bottom: 24px;
                        }

                        h2 {
                            font-size: 15pt;
                            border-bottom: 2px solid #555;
                            padding-bottom: 6px;
                            margin-top: 24px;
                        }

                        h3 {
                            font-size: 12pt;
                            margin-top: 18px;
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin: 8px 0 18px;
                        }

                        th, td {
                            border: 1px solid #bbb;
                            padding: 6px;
                            vertical-align: top;
                            overflow-wrap: anywhere;
                        }

                        th {
                            background-color: #eee;
                        }

                        tr {
                            page-break-inside: avoid;
                        }

                        .section {
                            page-break-before: auto;
                        }

                        .ar {
                            direction: rtl;
                            text-align: right;
                        }

                        .en {
                            direction: ltr;
                            text-align: left;
                        }

                        .secondary {
                            color: #555;
                            font-size: 9pt;
                        }
                    </style>
                </head>
                <body>
                """);

        html.append("<h1>CR Extractor Report</h1>");

        for (XlsxSheet sheet : workbook.sheets()) {
            html.append("<div class=\"section\">");

            html.append("<h2>")
                    .append(bilingual(sheet.titleEn(), sheet.titleAr()))
                    .append("</h2>");

            appendFields(html, sheet);
            appendTables(html, sheet);

            html.append("</div>");
        }

        html.append("</body></html>");

        return html.toString();
    }

    private void appendFields(
            StringBuilder html,
            XlsxSheet sheet
    ) {
        if (sheet.fields().isEmpty()) {
            return;
        }

        html.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Field / الحقل</th>
                            <th>Value / القيمة</th>
                        </tr>
                    </thead>
                    <tbody>
                """);

        for (XlsxField field : sheet.fields()) {
            html.append("<tr><td>")
                    .append(bilingual(
                            field.labelEn(),
                            field.labelAr()
                    ))
                    .append("</td><td>")
                    .append(bilingual(
                            value(field.valueEn()),
                            value(field.valueAr())
                    ))
                    .append("</td></tr>");
        }

        html.append("</tbody></table>");
    }

    private void appendTables(
            StringBuilder html,
            XlsxSheet sheet
    ) {
        for (XlsxTable table : sheet.tables()) {
            html.append("<h3>")
                    .append(bilingual(
                            table.titleEn(),
                            table.titleAr()
                    ))
                    .append("</h3>");

            html.append("<table><thead><tr>");

            for (var column : table.columns()) {
                html.append("<th>")
                        .append(bilingual(
                                column.labelEn(),
                                column.labelAr()
                        ))
                        .append("</th>");
            }

            html.append("</tr></thead><tbody>");

            for (var row : table.rows()) {
                html.append("<tr>");

                for (XlsxCell cell : row) {
                    html.append("<td>")
                            .append(bilingual(
                                    value(cell.valueEn()),
                                    value(cell.valueAr())
                            ))
                            .append("</td>");
                }

                html.append("</tr>");
            }

            html.append("</tbody></table>");
        }
    }

    private String bilingual(String english, String arabic) {
        StringBuilder result = new StringBuilder();

        if (english != null && !english.isBlank()) {
            result.append("<div class=\"en\">")
                    .append(escape(english))
                    .append("</div>");
        }

        if (arabic != null && !arabic.isBlank()) {
            result.append("<div class=\"ar\"  >")
                    .append(escape(normalizeArabic(arabic)))
                    .append("</div>");
        }

        return result.toString();
    }

    private String value(com.fasterxml.jackson.databind.JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }

        if (node.isContainerNode()) {
            return node.toString();
        }

        return node.asText();
    }

    private String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }


    private String normalizeArabic(String text) {
    if (text == null) {
        return "";
    }

    return Normalizer.normalize(text, Normalizer.Form.NFKC);
    }

    private Path checkIfPathExists(Path path){
        Path destination = path ; 
         if(Files.exists(destination )){


            StringBuilder temp  = new StringBuilder(destination.getFileName().toString());
            temp.insert(temp.indexOf(".pdf"), "_new");

            destination = destination.getParent().resolve( temp.toString()) ;
            
            destination = checkIfPathExists(destination);




        }

        return destination;
    }
}