package bdproject.model.types;

public enum EmployeeType {
    OPERATOR("Operatore"),
    ADMIN("Amministratore");

    private final String name;

    private EmployeeType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
