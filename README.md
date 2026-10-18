# CR Extractor

**CR Extractor** is a Java desktop application designed to simplify the extraction and organization of information from Saudi commercial registration (CR) documents.

It uses QR-code extraction and web-based data retrieval to collect business information, normalize the results, and present them in structured reports.

## Features

- **Document processing** — load and process supported commercial registration documents.
- **QR-code extraction** — extract QR data to help retrieve associated business information.
- **Web-based data retrieval** — retrieve additional details from the linked online source.
- **Data normalization** — organize extracted information into consistent, readable fields.
- **Structured reports** — generate and browse reports containing the extracted business data.
- **Report history** — keep generated reports available for later review.
- **File management** — save and access uploaded documents through the desktop interface.

## Technology Stack

- **Language:** Java
- **Desktop UI:** JavaFX
- **Build system:** Maven
- **Web and HTML parsing:** Jsoup
- **Architecture:** Multi-module Maven project

## Project Structure

The repository is organized into three Maven modules:

```text
cr-extractor/
├── cr-core/       # Core extraction logic and shared models
├── cr-desktop/    # JavaFX desktop application
├── cr-cli/        # Command-line module
└── pom.xml        # Parent Maven project
```

## Requirements

- JDK compatible with the project's configured Java version
- Maven
- Internet access for features that retrieve data from online sources

JavaFX dependencies are managed through Maven. Check the parent and module `pom.xml` files for the exact versions configured in your checkout.

## Build and Run

Clone the repository and enter its root directory:

```bash
git clone <repository-url>
cd cr-extractor
```

Build the project:

```bash
mvn clean package
```

Run the JavaFX desktop application:

```bash
mvn -pl cr-desktop javafx:run
```

If the JavaFX launch command fails, confirm that the configured JDK and JavaFX versions are compatible and that the JavaFX Maven plugin is configured in `cr-desktop/pom.xml`.

## How It Works

1. Load a supported commercial registration document.
2. Extract its QR-code data.
3. Retrieve the associated business information from the linked online source.
4. Normalize the extracted fields into a consistent structure.
5. Save the results as a report that can be reviewed later.

The exact fields available depend on the source document and the data returned by the online service.

## Output

Reports organize extracted information into sections such as:

- Commercial registration information
- Business activities
- Entity address
- Capital details
- Contact information
- Management information

## Notes

- An internet connection may be required to retrieve information from online services.
- Extraction results depend on document quality and the availability of data from the source.
- Do not upload or process documents unless you are authorized to use their contents.
- The project is under active development; commands and features may change.

## Contributing

Contributions and suggestions are welcome. Open an issue to report a bug or propose an improvement, or submit a pull request with a clear description of the change.

## License

No license has been specified yet. Unless a license is added to this repository, reuse and redistribution are not automatically permitted.
