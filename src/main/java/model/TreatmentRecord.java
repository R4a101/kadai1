package model; 
 
import java.sql.Timestamp; // または java.util.Date
import java.util.List;
import java.util.Map;

public class TreatmentRecord {
    private String treatmentId; // 処置を一意に識別するID (DBにPKがあれば)
    private String patId;
    private String empId;
    private Timestamp treatmentDate; // 処置が行われた日時
    private List<Map<String, String>> medicines; // この処置で投与された薬剤のリスト

    // Constructor, Getters, Setters
    public TreatmentRecord(String patId, String empId, List<Map<String, String>> medicines) {
        this.patId = patId;
        this.empId = empId;
        this.medicines = medicines;
    }
    
    public TreatmentRecord(String treatmentId, String patId, String empId, Timestamp treatmentDate, List<Map<String, String>> medicines) {
        this.treatmentId = treatmentId;
        this.patId = patId;
        this.empId = empId;
        this.treatmentDate = treatmentDate;
        this.medicines = medicines;
    }


    public String getTreatmentId() { return treatmentId; }
    public void setTreatmentId(String treatmentId) { this.treatmentId = treatmentId; }
    public String getPatId() { return patId; }
    public void setPatId(String patId) { this.patId = patId; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public Timestamp getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(Timestamp treatmentDate) { this.treatmentDate = treatmentDate; }
    public List<Map<String, String>> getMedicines() { return medicines; }
    public void setMedicines(List<Map<String, String>> medicines) { this.medicines = medicines; }
}