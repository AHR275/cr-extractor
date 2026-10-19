package app.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {

    private AppConfig() {}

    public static final Path USER_HOME =
            Paths.get(System.getProperty("user.home"));

    public static final Path DOCUMENTS =
            USER_HOME.resolve("Documents");

    public static final Path APP_DATA =
            DOCUMENTS.resolve("CR Extractor");
    public static final Path EXPORTED_REPORTS  = 
            APP_DATA.resolve("Exported Reports");

    public static final Path REPORTS =
            APP_DATA.resolve("Reports");
    public static final String CRWebLink = 
            "https://qr.saudibusiness.gov.sa/viewcr?nCrNumber=";
}