package floristflow.model;

/**
 * Enum untuk mendefinisikan Role / Peran pengguna dalam FloristFlow.
 */
public enum Role {
    ADMIN("Administrator"),
    CASHIER("Kasir");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
