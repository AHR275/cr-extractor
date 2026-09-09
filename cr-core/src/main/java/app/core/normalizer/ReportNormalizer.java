package app.core.normalizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import app.core.translator.Translator;

public class ReportNormalizer implements Normalizer {

    private final Translator translator = new Translator();

    @Override
    public JsonNode normalize(JsonNode data) {

        ObjectNode result =
                JsonNodeFactory.instance.objectNode();

        data.fields().forEachRemaining(entry -> {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            // Nested object
            if (value.isObject()) {

                result.set(
                        key,
                        normalize(value)
                );

                return;
            }



            // Array
            if (value.isArray()) {
                ArrayNode array =
                        JsonNodeFactory.instance.arrayNode();

                for (JsonNode v : value) {
                        array.add(normalize(v));
                }

                result.set(key, array);

                return;
            }

            // Leaf field
            String fieldKey = key;

            if (key.endsWith("Ar")) {

                fieldKey =
                        key.substring(0, key.length() - 2);

            } else if (key.endsWith("En")) {

                return;
            }

            String fieldName =
                    normalizeName(fieldKey);

            String labelAr =
                    translator.translate(fieldName);

            JsonNode valueAr =
                    data.get(fieldKey + "Ar");

            JsonNode valueEn =
                    data.get(fieldKey + "En");

            // No Ar/En pair
            if (valueAr == null && valueEn == null) {

                valueAr = data.get(fieldKey);
                valueEn = data.get(fieldKey);
            }

            result.set(
                    fieldKey,
                    createField(
                            labelAr,
                            fieldName,
                            valueAr,
                            valueEn
                    )
            );
        });

        return result;
    }


    private ObjectNode createField(
            String labelAr,
            String labelEn,
            JsonNode valueAr,
            JsonNode valueEn
    ) {

        ObjectNode field =
                JsonNodeFactory.instance.objectNode();

        ObjectNode labels =
                field.putObject("labels");

        labels.put("ar", labelAr);
        labels.put("en", labelEn);

        ObjectNode values =
                field.putObject("values");

        values.set("ar", valueAr);
        values.set("en", valueEn);

        return field;
    }


    public String normalizeJSON(String json) {

        ObjectMapper mapper =
                new ObjectMapper();

        try {

            JsonNode root =
                    mapper.readTree(json);

            JsonNode data =
                    root.get("data");

            JsonNode error =
                    root.get("error");

            JsonNode normalizedData =
                    normalize(data);

            ObjectNode result =
                    mapper.createObjectNode();

            result.set("data", normalizedData);
            result.set("error", error);

            return mapper.writeValueAsString(result);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to normalize JSON",
                    e
            );
        }
    }
}