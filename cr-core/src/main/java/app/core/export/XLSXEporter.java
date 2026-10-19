
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
                        valueStyle
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
            CellStyle valueStyle
    ) {

        int rowIndex = 0;

        // Main worksheet title.
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

        // Bilingual key-value fields.
        if (!model.fields().isEmpty()) {

            Row header = sheet.createRow(rowIndex++);

            String[] headers = {
                    "English Field",
                    "English Value",
                    "الحقل بالعربية",
                    "القيمة بالعربية"
            };

            for (int column = 0; column < headers.length; column++) {
                Cell cell = header.createCell(column);
                if (column >= headers.length/2){

                    cell.setCellValue(headers[(headers.length+1)- column]);
                }else{

                    cell.setCellValue(headers[column]);
                }
                cell.setCellStyle(headerStyle);
            }

            for (XlsxField field : model.fields()) {

                Row row = sheet.createRow(rowIndex++);
                
                valueStyle.setAlignment(HorizontalAlignment.LEFT);
                labelStyle.setAlignment(HorizontalAlignment.LEFT);

                setTextCell(
                        row.createCell(0),
                        field.labelEn(),
                        labelStyle
                );

                setJsonCell(
                        row.createCell(1),
                        field.valueEn(),
                        valueStyle
                );
                labelStyle.setAlignment(HorizontalAlignment.RIGHT);
                valueStyle.setAlignment(HorizontalAlignment.RIGHT);

                setTextCell(
                        row.createCell(3),
                        field.labelAr(),
                        labelStyle
                );

                setJsonCell(
                        row.createCell(2),
                        field.valueAr(),
                        valueStyle
                );
            }
        }

        // Array-based tables, such as activities.
        for (XlsxTable table : model.tables()) {

            if (rowIndex > 1) {
                rowIndex++;
            }

            int columnCount = table.columns().size();

            if (columnCount == 0) {
                continue;
            }

            int totalColumns = columnCount * 2;

            Row tableTitleRow = sheet.createRow(rowIndex++);
            Cell tableTitle = tableTitleRow.createCell(0);

            tableTitle.setCellValue(
                    table.titleEn() + " / " + table.titleAr()
            );
            tableTitle.setCellStyle(titleStyle);

            if (totalColumns > 1) {
                sheet.addMergedRegion(
                        new CellRangeAddress(
                                tableTitleRow.getRowNum(),
                                tableTitleRow.getRowNum(),
                                0,
                                totalColumns - 1
                        )
                );
            }

            // English columns first; Arabic columns on the right.
            
Row tableHeader = sheet.createRow(rowIndex++);

for (int i = 0; i < columnCount; i++) {

    // English columns: left to right
    Cell englishHeader = tableHeader.createCell(i);
    englishHeader.setCellValue(table.columns().get(i).labelEn());
    englishHeader.setCellStyle(headerStyle);

    // Arabic columns: reverse order, right to left
    int arabicIndex = columnCount - 1 - i;

    Cell arabicHeader = tableHeader.createCell(columnCount + i);
    arabicHeader.setCellValue(
            table.columns().get(arabicIndex).labelAr()
    );
    arabicHeader.setCellStyle(headerStyle);
}

   
for (var tableRow : table.rows()) {

    Row row = sheet.createRow(rowIndex++);

    // English values
    for (int i = 0; i < columnCount; i++) {
        setJsonCell(
                row.createCell(i),
                tableRow.get(i).valueEn(),
                valueStyle
        );
    }

    // Arabic values, in reverse column order
    for (int i = 0; i < columnCount; i++) {
        int arabicIndex = columnCount - 1 - i;

        setJsonCell(
                row.createCell(columnCount + i),
                tableRow.get(arabicIndex).valueAr(),
                valueStyle
        );
    }
}

        }

        // Four fixed columns for key-value sections.
        sheet.setColumnWidth(0, 45 * 300);
        sheet.setColumnWidth(1, 45 * 300);
        sheet.setColumnWidth(2, 45 * 300);
        sheet.setColumnWidth(3, 45 * 300);

        // Give additional columns reasonable widths for tables.
        for (int column = 4; column < sheet.getRow(0)
                .getLastCellNum(); column++) {
            sheet.setColumnWidth(column, 25 * 256);
        }

        sheet.createFreezePane(0, 1);
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
}
