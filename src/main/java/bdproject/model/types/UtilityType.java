package bdproject.model.types;

public enum UtilityType {

    GAS("Gas"),
    WATER("Acqua");

    private final String name;

    UtilityType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
