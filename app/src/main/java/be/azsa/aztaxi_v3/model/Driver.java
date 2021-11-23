package be.azsa.aztaxi_v3.model;

public class Driver {

    private long idDriver;
    private int driverNum, password;
    private String firstname, lastname, email, phone;

    // Constructeur Vide
    public Driver() { }

    //Constructeur avec paramètres
    public Driver(String firstname, String lastname, int driverNum, int password, String email, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.driverNum = driverNum;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    //Getters&Setters
    public long getIdDriver() { return idDriver; }
    public void setIdDriver(long idDriver) { this.idDriver = idDriver; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public int getDriverNum() { return driverNum; }
    public void setDriverNum(int driverNum) { this.driverNum = driverNum; }
    public int getPassword() { return password; }
    public void setPassword(int password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    //ToString

    @Override
    public String toString() {
        return "{" +
                "idDriver=" + idDriver +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", driverNum='" + driverNum + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
