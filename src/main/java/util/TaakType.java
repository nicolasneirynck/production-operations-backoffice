package util;

public enum TaakType {

    ONDERHOUD("Onderhoud"),
    INSPECTIE("Inspectie"),
    HERSTEL("Herstel"),
    SCHOONMAAK("Schoonmaak"),
    ADMINISTRATIE("Administratie");

    private final String label;

    TaakType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
