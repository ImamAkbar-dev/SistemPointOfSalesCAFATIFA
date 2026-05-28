package ModelLogic;

public abstract class User {
    protected int idUser;
    protected String username;
    protected String password;
    protected String role;

    public User(int idUser, String username, String password, String role) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role) {
        this(0, username, password, role);
    }

    public abstract void showDashboard();

    public int getIdUser() { 
        return idUser; 
    }
    public String getUsername() { 
        return username; 
    }
    public String getPassword() { 
        return password; 
    }
    public String getRole() { 
        return role; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
}