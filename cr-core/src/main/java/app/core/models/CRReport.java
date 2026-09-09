package app.core.models;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CRReport {

    private final Path file;
    private final CRSection data ;

    public CRReport(
            Path file 
            
    ) {
       this.file = file;

    ObjectMapper mapper = new ObjectMapper();

    try {
        JsonNode root = mapper.readTree(file.toFile());

        JsonNode data = root.get("data");

        this.data = mapSection(data);

    } catch (IOException e) {
        throw new RuntimeException(
            "Failed to read CR report: " + file,
            e
        );
    }




    
    }

    public Path getFile() {
        return file;
    }

    public CRSection getData() {
        return data;
    }


    private CRSection mapSection(JsonNode node) {

    CRSection section = new CRSection();

    node.fields().forEachRemaining(entry -> {

        String key = entry.getKey();
        JsonNode value = entry.getValue();


        // -------------------------
        // Normalized field
        // -------------------------

        if (value.isObject()
                && value.has("labels")
                && value.has("values")) {

            JsonNode labels = value.get("labels");
            JsonNode values = value.get("values");

            String enLabel =
                    labels.has("en")
                            ? labels.get("en").asText()
                            : key;

            String arLabel =
                    labels.has("ar")
                            ? labels.get("ar").asText()
                            : key;

            JsonNode enValue =
                    values.get("en");

            JsonNode arValue =
                    values.get("ar");

            section.addField(
                    key,
                    new CRField(
                            enLabel,
                            enValue,
                            arLabel,
                            arValue
                    )
            );

            return;
        }


        // -------------------------
        // Nested section
        // -------------------------

        if (value.isObject()) {

            section.addSection(
                    key,
                    mapSection(value)
            );

            return;
        }


        // -------------------------
        // Array
        // -------------------------

        if (value.isArray()) {

            List<CRSection> list = new ArrayList<>();
            CRSection tempCRSection = new CRSection(); 
            int x =0; 
            for (JsonNode item : value) {

                

                
                tempCRSection.addSection(key+"-"+Integer.toString(x), mapSection(item));

                
                x++; 
                
            }

    
            section.addSection(key, tempCRSection);
            
        
        }

    });

    // return section;

    return section;
}
}