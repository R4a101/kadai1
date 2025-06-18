package model;

public class Medicine {
    private String medicineId;
    private String medicineName;
    // 他にも単価などの情報があれば追加

    // Constructors
    public Medicine() {}

    public Medicine(String medicineId, String medicineName) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
    }

    // Getters and Setters
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    @Override
    public String toString() {
        return "Medicine [medicineId=" + medicineId + ", medicineName=" + medicineName + "]";
    }
}