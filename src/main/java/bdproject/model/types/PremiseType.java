package bdproject.model.types;

public enum PremiseType {

    FABBRICATO("Fabbricato"),
    TERRENO("Terreno");

    private final String name;

    private PremiseType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
