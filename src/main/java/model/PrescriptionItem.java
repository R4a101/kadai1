package model;

import java.util.UUID;

public class PrescriptionItem {
    private String tempId; // セッション内で一意に識別するためのID
    private String medicineId;
    private String medicineName; // 表示用
    private String dosage; // 投与量 (例: 1回1錠)
    private String instructions; // 指示 (例: 1日3回毎食後)

    public PrescriptionItem() {
        this.tempId = UUID.randomUUID().toString(); // 一時IDを自動生成
    }

    public PrescriptionItem(String medicineId, String medicineName, String dosage, String instructions) {
        this(); // tempIdを生成
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    // Getters and Setters
    public String getTempId() { return tempId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    @Override
    public String toString() {
        return "PrescriptionItem [tempId=" + tempId + ", medicineId=" + medicineId + ", medicineName=" + medicineName +
               ", dosage=" + dosage + ", instructions=" + instructions + "]";
    }
}