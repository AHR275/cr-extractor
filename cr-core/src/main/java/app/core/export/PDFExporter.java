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
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.TextDirection;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import app.core.normalizer.XLSXNormalizer;
import app.core.normalizer.XLSXNormalizer.XlsxField;
import app.core.normalizer.XLSXNormalizer.XlsxSheet;
import app.core.normalizer.XLSXNormalizer.XlsxTable;
import app.core.normalizer.XLSXNormalizer.XlsxWorkbook;



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
                        table-layout: fixed;
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

                    th:nth-child(3),
                    th:nth-child(4) {
                        direction: rtl;
                        text-align: right;
                    }

                    tr {
                        page-break-inside: avoid;
                    }

                    .heading-ar {
                        direction: rtl;
                        unicode-bidi: embed;
                        display: inline-block;
                        margin-left: 12px;
                    }

                    .heading-en {
                        direction: ltr;
                        display: inline-block;
                    }
                </style>
            </head>
            <body>
            """);

    html.append("<h1>CR Extractor Report</h1>");

    for (XlsxSheet sheet : workbook.sheets()) {
        html.append("<div class=\"section\">");

        html.append("<h2>")
                .append(bilingualHeading(
                        sheet.titleEn(),
                        sheet.titleAr()
                ))
                .append("</h2>");

        appendTables(html, sheet);

        html.append("</div>");
    }

    html.append("</body></html>");

    return html.toString();
}
private void appendTables(
        StringBuilder html,
        XlsxSheet sheet
) {
    String[] headers = {
        "English Field",
        "English Value",
        "القيمة بالعربية",
        "الحقل بالعربية"
    };

    for (XlsxTable table : sheet.tables()) {
        html.append("<h3>")
                .append(bilingualHeading(
                        table.titleEn(),
                        table.titleAr()
                ))
                .append("</h3>");

        html.append("<table>");

        // Render the same four-column header for every table.
        html.append("<thead><tr>");

        for (int i = 0; i < headers.length; i++) {
            html.append("<th>")
                    .append(escape(headers[i]))
                    .append("</th>");
        }

        html.append("</tr></thead><tbody>");

        // Array tables already contain a header row from the normalizer.
        // Skip that row because the exporter renders its own header.
        int startIndex = !table.rows().isEmpty()
                && isHeaderRow(table.rows().get(0))
                ? 1
                : 0;

        for (int i = startIndex; i < table.rows().size(); i++) {
            XlsxField row = table.rows().get(i);

            html.append("<tr>");

            for (int column = 0; column < 4; column++) {
                JsonNode cellValue = column < row.cells().size()
                        ? row.cells().get(column).value()
                        : null;

                String cssClass = column == 2 || column == 3
                        ? "ar"
                        : "en";

                html.append("<td class=\"")
                        .append(cssClass)
                        .append("\">")
                        .append(escape(value(cellValue)));

                html.append("</td>");
            }

            html.append("</tr>");
        }

        html.append("</tbody></table>");
    }
}
    private String bilingual(String english, String arabic) {
        StringBuilder result = new StringBuilder();

        if (english != null && !english.isBlank()) {
            result.append("<td class=\"en\">")
                    .append(escape(english))
                    .append("</td>");
        }

        if (arabic != null && !arabic.isBlank()) {
            result.append("<td class=\"ar\"  >")
                    .append(escape(normalizeArabic(arabic)))
                    .append("</td>");
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


    private boolean isHeaderRow(XlsxField row) {
    if (row.cells().size() < 4) {
        return false;
    }

    String[] expected = {
        "En Field",
        "En Value",
        "Ar Value",
        "Ar Field"
    };

    for (int i = 0; i < expected.length; i++) {
        JsonNode node = row.cells().get(i).value();

        if (node == null || !expected[i].equals(node.asText())) {
            return false;
        }
    }

    return true;
}

private String bilingualHeading(String english, String arabic) {
    StringBuilder result = new StringBuilder();

    if (english != null && !english.isBlank()) {
        result.append("<span class=\"heading-en\">")
                .append(escape(english))
                .append("</span>");
    }

    if (arabic != null && !arabic.isBlank()) {
        result.append("<span class=\"heading-ar\">")
                .append(escape(normalizeArabic(arabic)))
                .append("</span>");
    }

    return result.toString();
}
}