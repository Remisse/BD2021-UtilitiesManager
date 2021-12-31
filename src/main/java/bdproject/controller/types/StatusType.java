package bdproject.controller.types;

public enum StatusType {

    APPROVED("Approvata"),
    REJECTED("Respinta"),
    REVIEWING("In lavorazione");

    private final String toString;

    private StatusType(final String toString) {
        this.toString = toString;
    }

    public String toString() {
        return toString;
    }
}
