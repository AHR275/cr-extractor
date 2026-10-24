
package app.core.models;

public record ReportSearchCriteria(
        String query,
        String crNumber,
        String companyName,
        String status
) {
    public static ReportSearchCriteria empty() {
        return new ReportSearchCriteria("", "", "", "");
    }
}
