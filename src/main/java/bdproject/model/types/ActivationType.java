package bdproject.model.types;

public enum ActivationType {

    NEW_ACTIVATION("Nuova attivazione"),
    SUBENTRO("Subentro"),
    VOLTURA("Voltura");

    private final String name;

    private ActivationType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
