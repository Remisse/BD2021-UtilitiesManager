package bdproject.controller.types;

public enum ActivationType {

    NEW_ACTIVATION("Nuova attivazione"),
    SUBENTRO("SUBENTRO"),
    VOLTURA("Voltura");

    private final String name;

    private ActivationType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
