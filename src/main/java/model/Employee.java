package model;

public class Employee {
    private String empid;
    private String lastName;
    private String firstName;
    private int emprole;

    public Employee(String empid, String lastName, String firstName, int emprole) {
        this.empid = empid;
        this.lastName = lastName;
        this.firstName = firstName;
        this.emprole = emprole;
    }

    public String getEmpid() { return empid; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public int getEmprole() { return emprole; }
    public void setEmprole(int emprole) { this.emprole = emprole; }

    public String getEmpname() { return lastName + " " + firstName; }
    public String getRoleName() {
        return emprole == 0 ? "reception" : "doctor";
    }
}
