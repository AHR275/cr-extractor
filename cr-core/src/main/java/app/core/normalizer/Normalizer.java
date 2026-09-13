package app.core.normalizer;

// package app.core.normalizer;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import app.core.models.NormalizedField;

public interface Normalizer<T> {

    T normalize(JsonNode data);
    default  String normalizeName(String key) {

        String result = key
        .replaceAll("([a-z])([A-Z])", "$1 $2")
        .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");

        result =
                Character.toUpperCase(result.charAt(0))
                + result.substring(1);

        result = result.replaceAll("\\bCr\\b", "CR");
        result = result.replaceAll("\\bId\\b", "ID");
        result = result.replaceAll("\\bUrl\\b", "URL");
        result = result.replaceAll("\\bIsic\\b", "ISIC");
        result = result.replaceAll("\\bDesc\\b", "Description");
        

        return result;
        }
    default  NormalizedField createField(
            String labelAr,
            String labelEn,
            String valueAr,
            String valueEn
    ) {

        NormalizedField field =
                new NormalizedField();

        field.setLabel("ar", labelAr);
        field.setLabel("en", labelEn);

        field.setValue(
                "ar",
                valueAr
        );

        field.setValue(
                "en",
                valueEn 
        );

        return field;
    }

}