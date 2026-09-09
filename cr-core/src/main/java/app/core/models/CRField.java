package app.core.models;

public class CRField {

   private final String enLabel;
    private final Object enValue;

    private final String arLabel;
    private final Object arValue;

    public CRField(
            String enLabel,
            Object enValue,
            String arLabel,
            Object arValue
    ) {
        this.enLabel = enLabel;
        this.enValue = enValue;
        this.arLabel = arLabel;
        this.arValue = arValue;
    }

public String[] getLabels() {
    return new String[] {
        enLabel,
        arLabel
    };
}

    public Object[] getValue() {
        return new Object[]{
            enValue,
            arValue
        };
    }
}