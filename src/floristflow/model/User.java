package floristflow.model;

/**
 * Superclass abstrak untuk merepresentasikan entitas Pengguna dalam sistem FloristFlow.
 * Menerapkan pilar OOP:
 * 1. Abstraction: Menyediakan kerangka dasar pengguna dan abstract method getAccessDescription().
 * 2. Encapsulation: Seluruh atribut bertipe private dengan method getter/setter yang terkontrol.
 */
public abstract class User {
    private String username;
    private String password;
    private String fullName;
    private Role role;

    public User(String username, String password, String fullName, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    /**
     * Polymorphic method: Setiap role memiliki deskripsi akses yang berbeda.
     * @return deskripsi izin akses pengguna
     */
    public abstract String getAccessDescription();

    /**
     * Validasi kecocokan password.
     * @param inputPassword password yang diinputkan pengguna
     * @return true jika cocok, false jika tidak
     */
    public boolean validatePassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
