package app.core.parsing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonReportWriter {

    private final ObjectMapper mapper;

    private final Path reportDirectory =
        Path.of("..", "sources/CR-Reports").toAbsolutePath().normalize();

    public JsonReportWriter() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path save(String json) {

        try {
            JsonNode root = mapper.readTree(json);
            System.out.println("root : " + root.toPrettyString());

           String crNumber =
                root
                    .get("data")
                    .get("crInformation")
                    .get("crNumber")
                    .get("values")
                    .get("en")
                    .asText()
                    ;

            if (crNumber == null ) {
                throw new RuntimeException(
                        "CR number not found in response"
                );
            }

            Files.createDirectories(reportDirectory);

            Path file =
                    reportDirectory.resolve(
                            crNumber + ".json"
                    );

            mapper.writeValue(file.toFile(), root);

            return file;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to save CR report",
                    e
            );
        }
    }
}