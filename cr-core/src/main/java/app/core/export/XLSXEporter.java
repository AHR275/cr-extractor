
package app.core.export;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.core.normalizer.XLSXNormalizer;
import app.core.normalizer.XLSXNormalizer.XlsxField;
import app.core.normalizer.XLSXNormalizer.XlsxSheet;
import app.core.normalizer.XLSXNormalizer.XlsxTable;
import app.core.normalizer.XLSXNormalizer.XlsxWorkbook;

public class XLSXEporter implements   Exporter {

    private final ObjectMapper mapper = new ObjectMapper();
    private final XLSXNormalizer normalizer = new XLSXNormalizer();

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.XLSX;
    }

    /**
     * Loads a normalized JSON report, converts it to an Excel-ready
     * model, creates the workbook, saves it, and returns its path.
     *
     * destination must be the complete output file path,
     * including the .xlsx extension.
     */
    @Override 
    public Path export(Path jsonFile, Path destination)
            throws IOException {

        if (jsonFile == null || !Files.isRegularFile(jsonFile)) {
            throw new IllegalArgumentException(
                    "JSON report file does not exist: " + jsonFile
            );
        }

        if (destination == null) {
            throw new IllegalArgumentException(
                    "Destination cannot be null"
            );
        }

        if (!destination.getFileName().toString()
                .toLowerCase()
                .endsWith(".xlsx")) {

            throw new IllegalArgumentException(
                    "Destination must have an .xlsx extension"
            );
        }

        // 1. Load the JSON file.
        JsonNode root;

        try (InputStream input = Files.newInputStream(jsonFile)) {
            root = mapper.readTree(input);
        }

        if (root == null || root.isNull()) {
            throw new IOException("The JSON report is empty");
        }

        // 2. Extract the data node if this is a response wrapper.
        JsonNode data = root;

        if (root.path("data").isObject()) {
            data = root.path("data");
        }

        if (!data.isObject()) {
            throw new IOException(
                    "The report data must be a JSON object"
            );
        }

        // 3. Normalize into the Excel-specific workbook model.
        XlsxWorkbook model = normalizer.normalize(data);

        // 4. Create and populate the workbook.
        Path absoluteDestination = destination.toAbsolutePath();
        Path parent = absoluteDestination.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);

            CellStyle labelStyle = createLabelStyle(workbook);
            CellStyle valueStyle = createValueStyle(workbook);

            CellStyle arabicLabelStyle = createLabelStyle(workbook);
            arabicLabelStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle arabicValueStyle = createValueStyle(workbook);
            arabicValueStyle.setAlignment(HorizontalAlignment.RIGHT);

            Set<String> usedSheetNames = new HashSet<>();

            for (XlsxSheet sheetModel : model.sheets()) {

                String sheetName = uniqueSheetName(
                        sheetModel.titleEn(),
                        usedSheetNames
                );

                Sheet sheet = workbook.createSheet(sheetName);

                writeSheet(
                        workbook,
                        sheet,
                        sheetModel,
                        titleStyle,
                        headerStyle,
                        labelStyle,
                        valueStyle,
                        arabicLabelStyle,
                        arabicValueStyle
                );
            }

            try (OutputStream output =
                         Files.newOutputStream(absoluteDestination)) {

                workbook.write(output);
            }
        }

        // 5. Return the actual output path.
        return absoluteDestination;
    }

    private void writeSheet(
            Workbook workbook,
            Sheet sheet,
            XlsxSheet model,
            CellStyle titleStyle,
            CellStyle headerStyle,
            CellStyle labelStyle,
            CellStyle valueStyle,
            CellStyle arabicLabelStyle,
            CellStyle arabicValueStyle
    ) {
        int rowIndex = 0;

        // Worksheet title
        Row titleRow = sheet.createRow(rowIndex++);
        titleRow.setHeightInPoints(30);

        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(
                model.titleEn() + " / " + model.titleAr()
        );
        titleCell.setCellStyle(titleStyle);

        sheet.addMergedRegion(
                new CellRangeAddress(0, 0, 0, 3)
        );

        String[] headers = {
                "English Field",
                "English Value",
                "القيمة بالعربية",
                "الحقل بالعربية"
        };

        // Every sheet contains tables only.
        for (XlsxTable table : model.tables()) {

            if (rowIndex > 1) {
                rowIndex++;
            }

            // Table title
            Row tableTitleRow = sheet.createRow(rowIndex++);
            tableTitleRow.setHeightInPoints(24);

            Cell tableTitle = tableTitleRow.createCell(0);
            tableTitle.setCellValue(
                    table.titleEn() + " / " + table.titleAr()
            );
            tableTitle.setCellStyle(titleStyle);

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            tableTitleRow.getRowNum(),
                            tableTitleRow.getRowNum(),
                            0,
                            3
                    )
            );

            // Detect whether the normalizer already included a header row.
            boolean hasHeader = !table.rows().isEmpty()
                    && isHeaderRow(table.rows().get(0));

            // Write the header exactly once.
            Row headerRow = sheet.createRow(rowIndex++);

            for (int column = 0; column < headers.length; column++) {
                Cell cell = headerRow.createCell(column);

                if (hasHeader) {
                    setJsonCell(
                            cell,
                            table.rows().get(0).cells().get(column).value(),
                            headerStyle
                    );
                } else {
                    cell.setCellValue(headers[column]);
                    cell.setCellStyle(headerStyle);
                }
            }

            // Skip the existing header row when present.
            int firstDataRow = hasHeader ? 1 : 0;

            for (int i = firstDataRow; i < table.rows().size(); i++) {
                XlsxField field = table.rows().get(i);

                Row row = sheet.createRow(rowIndex++);

                for (int column = 0; column < 4; column++) {
                    Cell cell = row.createCell(column);

                    JsonNode value = column < field.cells().size()
                            ? field.cells().get(column).value()
                            : null;

                    CellStyle style = switch (column) {
                        case 0 -> labelStyle;
                        case 1 -> valueStyle;
                        case 2 -> arabicValueStyle;
                        case 3 -> arabicLabelStyle;
                        default -> valueStyle;
                    };

                    setJsonCell(cell, value, style);
                }
            }
        }

        // Four columns shared by every table.
        for (int column = 0; column < 4; column++) {
            sheet.setColumnWidth(column, 45 * 256);
        }

        // Freeze the worksheet title and first table's header.
        sheet.createFreezePane(0, 2);
    }
    private void setTextCell(
            Cell cell,
            String value,
            CellStyle style
    ) {
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private void setJsonCell(
            Cell cell,
            JsonNode value,
            CellStyle style
    ) {

        if (value == null || value.isNull()) {
            cell.setBlank();

        } else if (value.isBoolean()) {
            cell.setCellValue(value.booleanValue());

        } else if (value.isNumber()) {
            cell.setCellValue(value.doubleValue());

        } else if (value.isTextual()) {
            cell.setCellValue(value.textValue());

        } else {
            // Preserve nested arrays/objects as readable JSON text.
            cell.setCellValue(value.toString());
        }

        cell.setCellStyle(style);
    }

    private CellStyle createTitleStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(
                IndexedColors.LIGHT_BLUE.getIndex()
        );
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);

        style.setFont(font);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex()
        );
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorders(style);

        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.LEFT);
        setBorders(style);

        return style;
    }

    private CellStyle createValueStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setAlignment(HorizontalAlignment.LEFT);

        style.setWrapText(true);
        setBorders(style);

        return style;
    }

    private void setBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private String uniqueSheetName(
            String requestedName,
            Set<String> usedNames
    ) {

        String base = requestedName == null
                ? "Report"
                : requestedName.replaceAll("[\\\\/?*\\[\\]:]", " ")
                        .trim();

        if (base.isBlank()) {
            base = "Report";
        }

        base = base.substring(0, Math.min(base.length(), 31));

        String candidate = base;
        int suffix = 2;

        while (!usedNames.add(candidate.toLowerCase())) {

            String ending = " (" + suffix++ + ")";
            int maxBaseLength = 31 - ending.length();

            candidate = base.substring(
                    0,
                    Math.min(base.length(), maxBaseLength)
            ) + ending;
        }

        return candidate;
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
        JsonNode value = row.cells().get(i).value();

        if (value == null
                || !expected[i].equals(value.asText())) {
            return false;
        }
    }

    return true;
}
}
