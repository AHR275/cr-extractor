package app.core.normalizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import app.core.translator.Translator;

public class ReportNormalizer implements Normalizer {

    private final Translator translator = new Translator();

    @Override
    public ObjectNode normalize(JsonNode data) {

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

            JsonNode valueAr = data.get(fieldKey + "Ar");
            JsonNode valueEn = data.get(fieldKey + "En");


                

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

        public String normalizeJSON(String json, String link) {

        ObjectMapper mapper = new ObjectMapper();

        try {

                JsonNode root = mapper.readTree(json);

                ObjectNode data = (ObjectNode) root.get("data");

                JsonNode error = root.get("error");

                // Add link to data BEFORE normalization
                ObjectNode linkObject = mapper.createObjectNode();

                linkObject.put("ar", link);
                linkObject.put("en", link);

                data.set("link", linkObject);

                // Now normalize everything, including link
                ObjectNode normalizedData =
                        (ObjectNode) normalize(data);

                // Create final result
                ObjectNode result =
                        mapper.createObjectNode();

                result.set("data", normalizedData);
                result.set("error", error);

                // Keep the outer link as metadata
                result.put("link", link);

                return mapper.writeValueAsString(result);

        } catch (JsonProcessingException e) {

                throw new RuntimeException(
                        "Failed to normalize JSON",
                        e
                );
        }
        }
}