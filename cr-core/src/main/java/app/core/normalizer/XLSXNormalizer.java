
package app.core.normalizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import app.core.translator.Translator;

public class XLSXNormalizer
        implements Normalizer<XLSXNormalizer.XlsxWorkbook> {

    private final Translator translator = new Translator();

    /**
     * Converts normalized CR JSON into an Excel-ready workbook model.
     *
     * Accepts either the normalized data object directly or a response
     * wrapper containing a "data" object.
     */
    @Override
    public XlsxWorkbook normalize(JsonNode input) {
        Objects.requireNonNull(input, "Input cannot be null");

        JsonNode data = input;

        if (input.path("data").isObject()) {
            data = input.path("data");
        }

        if (!data.isObject()) {
            throw new IllegalArgumentException(
                    "CR report data must be a JSON object"
            );
        }

        List<XlsxSheet> sheets = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> sections = data.fields();

        while (sections.hasNext()) {
            Map.Entry<String, JsonNode> section = sections.next();

            String key = section.getKey();
            JsonNode value = section.getValue();

            if ("error".equals(key) || value == null || value.isNull()) {
                continue;
            }

            String titleEn = humanize(key);
            String titleAr = translate(titleEn);

            SectionContent content = new SectionContent();

            if (value.isArray()) {
                content.tables.add(
                        createTable(key, titleEn, titleAr, value)
                );
            } else if (isField(value)) {
                content.fields.add(createField(key, value));
            } else if (value.isObject()) {
                collectContent(value, "", "", content);
            } else {
                content.fields.add(createRawField(key, value));
            }

            if (!content.fields.isEmpty() || !content.tables.isEmpty()) {
                sheets.add(new XlsxSheet(
                        key,
                        titleEn,
                        titleAr,
                        content.fields,
                        content.tables
                ));
            }
        }

        return new XlsxWorkbook(sheets);
    }

    /**
     * Recursively collects fields and nested tables from a JSON object.
     */
    private void collectContent(
            JsonNode node,
            String pathEn,
            String pathAr,
            SectionContent content
    ) {
        if (node == null || node.isNull()) {
            return;
        }

        if (isField(node)) {
            content.fields.add(createField(pathEn, pathAr, node));
            return;
        }

        if (node.isArray()) {
            String titleEn = pathEn.isBlank() ? "Items" : pathEn;
            String titleAr = pathAr.isBlank()
                    ? translate(titleEn)
                    : pathAr;

            content.tables.add(
                    createTable(pathEn, titleEn, titleAr, node)
            );
            return;
        }

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();

                String labelEn = humanize(entry.getKey());
                String labelAr = translate(labelEn);

                collectContent(
                        entry.getValue(),
                        join(pathEn, labelEn),
                        join(pathAr, labelAr),
                        content
                );
            }

            return;
        }

        content.fields.add(createRawField(pathEn, pathAr, node));
    }

    private boolean isField(JsonNode node) {
        return node != null
                && node.isObject()
                && node.path("labels").isObject()
                && node.path("values").isObject();
    }

    private XlsxField createField(String path, JsonNode field) {
        return createField(path, "", field);
    }

    private XlsxField createField(
            String pathEn,
            String pathAr,
            JsonNode field
    ) {
        JsonNode labels = field.path("labels");
        JsonNode values = field.path("values");

        String labelEn = text(labels.path("en"), humanize(pathEn));
        String labelAr = text(
                labels.path("ar"),
                translate(humanize(pathEn))
        );

        return new XlsxField(
                join(pathEn, labelEn),
                values.get("en"),
                join(pathAr, labelAr),
                values.get("ar")
        );
    }

    private XlsxField createRawField(String key, JsonNode value) {
        return createRawField(key, "", value);
    }

    private XlsxField createRawField(
            String keyEn,
            String keyAr,
            JsonNode value
    ) {
        String labelEn = humanize(keyEn);
        String labelAr = keyAr == null || keyAr.isBlank()
                ? translate(labelEn)
                : keyAr;

        return new XlsxField(labelEn, value, labelAr, value);
    }

    /**
     * Builds either a regular horizontal table or a vertical key/value table.
     *
     * Both layouts use the same recursive field-collection method.
     * Manager-list records are represented vertically; other arrays retain
     * the existing horizontal layout.
     */
    private XlsxTable createTable(
            String key,
            String titleEn,
            String titleAr,
            JsonNode array
    ) {
        boolean vertical = isManagerList(key)
                || isManagerList(titleEn);

        Map<String, ColumnDefinition> definitions = new LinkedHashMap<>();
        List<Map<String, XlsxCell>> flattenedRows = new ArrayList<>();

        for (JsonNode item : array) {
            List<TableEntry> entries = new ArrayList<>();

            collectTableFields(item, "", "", entries);

            Map<String, XlsxCell> row = new LinkedHashMap<>();

            for (TableEntry entry : entries) {
                String columnKey = entry.pathEn().isBlank()
                        ? "value"
                        : entry.pathEn();

                definitions.putIfAbsent(
                        columnKey,
                        new ColumnDefinition(
                                entry.labelEn(),
                                entry.labelAr()
                        )
                );

                row.put(
                        columnKey,
                        new XlsxCell(
                                entry.valueEn(),
                                entry.valueAr()
                        )
                );
            }

            if (vertical) {
                for (TableEntry entry : entries) {
                    String keyEn = join(
                            entry.pathEn(),
                            entry.labelEn()
                    );

                    String keyAr = join(
                            entry.pathAr(),
                            entry.labelAr()
                    );

                    row.put(
                            "\u0000manager-key-" + entries.indexOf(entry),
                            new XlsxCell(
                                    TextNode.valueOf(keyEn),
                                    TextNode.valueOf(keyAr)
                            )
                    );
                }

                // Store each collected entry as a separate key/value row.
                for (TableEntry entry : entries) {
                    String keyEn = join(
                            entry.pathEn(),
                            entry.labelEn()
                    );

                    String keyAr = join(
                            entry.pathAr(),
                            entry.labelAr()
                    );

                    row.put(
                            "\u0000manager-value-" + entries.indexOf(entry),
                            new XlsxCell(
                                    entry.valueEn(),
                                    entry.valueAr()
                            )
                    );
                }
            }

            flattenedRows.add(row);
        }

        return createVerticalTable(
                titleEn,
                titleAr,
                array
        );
        // if (vertical) {
        // }

        // List<XlsxColumn> columns = new ArrayList<>();

        // for (Map.Entry<String, ColumnDefinition> entry
        //         : definitions.entrySet()) {

        //     ColumnDefinition definition = entry.getValue();

        //     columns.add(new XlsxColumn(
        //             entry.getKey(),
        //             definition.labelEn(),
        //             definition.labelAr()
        //     ));
        // }

        // List<List<XlsxCell>> rows = new ArrayList<>();

        // for (Map<String, XlsxCell> flattenedRow : flattenedRows) {
        //     List<XlsxCell> row = new ArrayList<>();

        //     for (XlsxColumn column : columns) {
        //         XlsxCell cell = flattenedRow.get(column.key());

        //         row.add(cell != null ? cell : new XlsxCell(null, null));
        //     }

        //     rows.add(row);
        // }

        // return new XlsxTable(titleEn, titleAr, columns, rows);
    }
   
    /**
     * Uses the same collected field representation to create vertical rows.
     * Each record's properties become separate Key/Value rows.
     */
    private XlsxTable createVerticalTable(
            String titleEn,
            String titleAr,
            JsonNode array
    ) {
        List<XlsxColumn> columns = List.of(
                new XlsxColumn("key", "English Field ", " الحقل بالعربية "),
                new XlsxColumn("value", "English Value", " القيمة بالعربية ")
        );

        List<List<XlsxCell>> rows = new ArrayList<>();

        for (JsonNode item : array) {
            List<TableEntry> entries = new ArrayList<>();

            collectTableFields(item, "", "", entries);

            for (TableEntry entry : entries) {
                String keyEn = join(
                        entry.pathEn(),
                        entry.labelEn()
                );

                String keyAr = join(
                        entry.pathAr(),
                        entry.labelAr()
                );

                rows.add(List.of(
                        new XlsxCell(
                                TextNode.valueOf(keyEn),
                                TextNode.valueOf(keyAr)
                        ),
                        new XlsxCell(
                                entry.valueEn(),
                                entry.valueAr()
                        )
                ));
            }
            rows.add(List.of(
                        new XlsxCell(
                                TextNode.valueOf(" "),
                                TextNode.valueOf(" ")
                        ),
                        new XlsxCell(
                                TextNode.valueOf(" "),
                                TextNode.valueOf(" ")
                        )
                ));
        }

        return new XlsxTable(titleEn, titleAr, columns, rows);
    }

    /**
     * Recursively collects table fields while preserving both languages.
     * This method is shared by horizontal and vertical table layouts.
     */
    private void collectTableFields(
            JsonNode node,
            String pathEn,
            String pathAr,
            List<TableEntry> entries
    ) {
        if (node == null || node.isNull()) {
            return;
        }

        if (isField(node)) {
            JsonNode labels = node.path("labels");
            JsonNode values = node.path("values");

            String labelEn = text(
                    labels.path("en"),
                    humanize(pathEn)
            );

            String labelAr = text(
                    labels.path("ar"),
                    translate(humanize(pathEn))
            );

            entries.add(new TableEntry(
                    pathEn,
                    pathAr,
                    labelEn,
                    labelAr,
                    values.get("en"),
                    values.get("ar")
            ));

            return;
        }

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();

                String labelEn = humanize(entry.getKey());
                String labelAr = translate(labelEn);

                collectTableFields(
                        entry.getValue(),
                        join(pathEn, labelEn),
                        join(pathAr, labelAr),
                        entries
                );
            }

            return;
        }

        String labelEn = pathEn.isBlank()
                ? "Value"
                : pathEn;

        String labelAr = pathAr.isBlank()
                ? translate(labelEn)
                : pathAr;

        entries.add(new TableEntry(
                pathEn,
                pathAr,
                labelEn,
                labelAr,
                node,
                node
        ));
    }

    private boolean isManagerList(String keyOrTitle) {
        if (keyOrTitle == null || keyOrTitle.isBlank()) {
            return false;
        }

        String lastSegment = keyOrTitle.substring(
                keyOrTitle.lastIndexOf(" / ") + 3
        );

        String normalized = lastSegment
                .replaceAll("[\\s_/-]", "")
                .toLowerCase();

        return normalized.equals("managerlist");
    }

    private String humanize(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }

        return normalizeName(key)
                .replace('_', ' ')
                .replace('-', ' ')
                .trim();
    }

    private String translate(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String translated = translator.translate(text);

        return translated == null || translated.isBlank()
                ? text
                : translated;
    }

    private String text(JsonNode node, String fallback) {
        if (node == null || node.isNull()) {
            return fallback;
        }

        String value = node.asText();

        return value.isBlank() ? fallback : value;
    }

    

    private String join(String parent, String child) {
        if (parent == null || parent.isBlank()) {
            return child == null ? "" : child;
        }

        if (child == null || child.isBlank()) {
            return parent;
        }

        if (child.equals(parent)) {
            return parent;
        }

        return parent + " / " + child;
    }

    private static class SectionContent {
        private final List<XlsxField> fields = new ArrayList<>();
        private final List<XlsxTable> tables = new ArrayList<>();
    }

    private record ColumnDefinition(
            String labelEn,
            String labelAr
    ) {}

    private record TableEntry(
            String pathEn,
            String pathAr,
            String labelEn,
            String labelAr,
            JsonNode valueEn,
            JsonNode valueAr
    ) {}

    // Excel-ready model

    public record XlsxWorkbook(List<XlsxSheet> sheets) {
        public XlsxWorkbook {
            sheets = List.copyOf(sheets);
        }
    }

    public record XlsxSheet(
            String key,
            String titleEn,
            String titleAr,
            List<XlsxField> fields,
            List<XlsxTable> tables
    ) {
        public XlsxSheet {
            fields = List.copyOf(fields);
            tables = List.copyOf(tables);
        }
    }

    public record XlsxField(
            String labelEn,
            JsonNode valueEn,
            String labelAr,
            JsonNode valueAr
    ) {}

    public record XlsxTable(
            String titleEn,
            String titleAr,
            List<XlsxColumn> columns,
            List<List<XlsxCell>> rows
    ) {
        public XlsxTable {
            columns = List.copyOf(columns);
            rows = rows.stream()
                    .map(List::copyOf)
                    .toList();
        }
    }

    public record XlsxColumn(
            String key,
            String labelEn,
            String labelAr
    ) {}

    public record XlsxCell(
            JsonNode valueEn,
            JsonNode valueAr
    ) {}
}
