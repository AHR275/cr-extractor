package app.core.models;
import java.util.LinkedHashMap;
import  java.util.Map; 
public class NormalizedField {

    private Map<String, String> labels;
    private Map<String, String> values;

    public NormalizedField() {
        labels = new LinkedHashMap<>();
        values = new LinkedHashMap<>();
    }

    public void setLabel(String language, String value) {
        labels.put(language, value);
    }

    public void setValue(String language, String value) {
        values.put(language, value);
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public Map<String, String> getValues() {
        return values;
    }


    private NormalizedField createField(
        String labelAr,
        String labelEn,
        String valueAr,
        String valueEn
) {

    NormalizedField field = new NormalizedField();

    field.setLabel("ar", labelAr);
    field.setLabel("en", labelEn);

    field.setValue("ar", valueAr);
    field.setValue("en", valueEn);

    return field;
}
}