package model;

public class Patient {
    private String patientId;
    private String patientLastName; // 患者姓
    private String patientFirstName; // 患者名

    // Constructors
    public Patient() {}

    public Patient(String patientId, String patientLastName, String patientFirstName) {
        this.patientId = patientId;
        this.patientLastName = patientLastName;
        this.patientFirstName = patientFirstName;
    }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientLastName() { return patientLastName; }
    public void setPatientLastName(String patientLastName) { this.patientLastName = patientLastName; }
    public String getPatientFirstName() { return patientFirstName; }
    public void setPatientFirstName(String patientFirstName) { this.patientFirstName = patientFirstName; }

    public String getFullName() {
        return (patientLastName != null ? patientLastName : "") + " " + (patientFirstName != null ? patientFirstName : "");
    }

    @Override
    public String toString() {
        return "Patient [patientId=" + patientId + ", fullName=" + getFullName() + "]";
    }
}