package app.core.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class CRSection {

    private final Map<String, CRField> fields;
    private final Map<String, CRSection> sections;

    public CRSection() {
        fields = new LinkedHashMap<>();
        sections = new LinkedHashMap<>();
    }

    public void addField(String key, CRField field) {
        fields.put(key, field);
    }

    public void addSection(String key, CRSection section) {
        sections.put(key, section);
    }

    public Map<String, CRField> getFields() {
        return fields;
    }

    public Map<String, CRSection> getSections() {
        return sections;
    }
}