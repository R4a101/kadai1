package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap; // 挿入順を保持するMap
import java.util.List;
import java.util.Map;
// UUID はこのバージョンでは不要 import java.util.UUID;

import model.TreatmentRecord;

public class TreatmentDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/r4a101webap2025?serverTimezone=Asia/Tokyo&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
    }

    public boolean insertTreatments(List<Map<String, String>> medicines, String patid, String empid) {
        // このメソッドは、画像のテーブル構造に合わせて created_at はDB側で自動設定されると仮定。
        // もし treatmentid が AUTO_INCREMENT でないなら、このメソッド内で設定が必要。
        // ここでは treatmentid は AUTO_INCREMENT と仮定。
        String sql = "INSERT INTO treatment (patid, empid, medicineid, dosage, instructions) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map<String, String> med : medicines) {
                    if (med.get("medicineid") == null || med.get("medicineid").isEmpty()) {
                        continue;
                    }
                    ps.setString(1, patid);
                    ps.setString(2, empid);
                    ps.setString(3, med.get("medicineid"));
                    ps.setString(4, med.get("dosage"));
                    ps.setString(5, med.get("instructions"));
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTreatmentByPatIdAndMedicineId(String patid, String medicineid) {
        // 画像のテーブル構造では、この削除は特定の薬剤レコードを対象とする。
        // もし特定の `treatmentid` のレコードを削除したいなら、引数に treatmentid を取る。
        String sql = "DELETE FROM treatment WHERE patid = ? AND medicineid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patid);
            ps.setString(2, medicineid);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 全ての処置履歴を取得します。
     * 提供されたテーブル画像に基づき、同じ患者、同じ従業員、近い時刻の薬剤レコードを
     * 1つの処置としてグループ化します。
     * @return TreatmentRecordのリスト
     */
    public List<TreatmentRecord> getAllTreatmentHistory() {
        List<TreatmentRecord> historyList = new ArrayList<>();
        // created_at カラムでソートして、Java側でグループ化
        String sql = "SELECT treatmentid, patid, empid, medicineid, dosage, instructions, created_at " +
                     "FROM treatment ORDER BY created_at DESC, patid ASC, empid ASC, treatmentid ASC";

        // グループ化のためのMap: キーは "patid-empid-yyyyMMddHHmm" (分単位でグループ化する例)
        Map<String, TreatmentRecord> groupedTreatments = new LinkedHashMap<>();

        System.out.println("DAO: getAllTreatmentHistory called. Executing SQL: " + sql); // ★デバッグログ

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            int rowCount = 0; // ★デバッグログ用
            while (rs.next()) {
                rowCount++; // ★デバッグログ用
                String dbTreatmentId = String.valueOf(rs.getInt("treatmentid")); // DBの主キー
                String patId = rs.getString("patid");
                String empId = rs.getString("empid");
                Timestamp createdAt = rs.getTimestamp("created_at");
                
                String medicineId = rs.getString("medicineid");
                String dosage = rs.getString("dosage");
                String instructions = rs.getString("instructions");

                System.out.println("  DAO: Processing row - treatmentid: " + dbTreatmentId + ", patid: " + patId + ", empid: " + empId + ", createdAt: " + createdAt); // ★デバッグログ

                // グループ化キーの生成 (例: patid + empid + 処置日時（分まで）)
                // 注意: このキー生成方法は、1トランザクション内で複数の薬剤がほぼ同時に登録されることを前提
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmm"); // 分まででグループ化
                String groupKey = patId + "-" + empId + "-" + (createdAt != null ? sdf.format(createdAt) : "UNKNOWN_TIME");
                
                System.out.println("    DAO: GroupKey: " + groupKey); // ★デバッグログ

                TreatmentRecord currentTreatment = groupedTreatments.get(groupKey);
                if (currentTreatment == null) {
                    // 新しい処置グループ
                    // TreatmentRecordのtreatmentIdには、グループキーまたは他の識別しやすい情報を入れる
                    // treatmentDateには、そのグループの代表的な日時（例:最初の薬剤のcreated_at）を入れる
                    currentTreatment = new TreatmentRecord(groupKey, patId, empId, createdAt, new ArrayList<>());
                    groupedTreatments.put(groupKey, currentTreatment);
                    System.out.println("    DAO: New group created for key: " + groupKey); // ★デバッグログ
                } else {
                    System.out.println("    DAO: Existing group found for key: " + groupKey); // ★デバッグログ
                }
                
                // 薬剤情報をリストに追加
                Map<String, String> med = new HashMap<>();
                med.put("medicineid", medicineId);
                med.put("dosage", dosage);
                med.put("instructions", instructions);
                currentTreatment.getMedicines().add(med);
                System.out.println("      DAO: Added medicine: " + medicineId + " to group: " + groupKey); // ★デバッグログ
            }
            System.out.println("DAO: Total rows processed from DB: " + rowCount); // ★デバッグログ
            System.out.println("DAO: Total groups created: " + groupedTreatments.size()); // ★デバッグログ

        } catch (SQLException e) {
            System.err.println("DAO: SQLException in getAllTreatmentHistory - " + e.getMessage()); // ★エラーログ
            e.printStackTrace();
        }
        
        historyList.addAll(groupedTreatments.values());
        System.out.println("DAO: Final historyList size to be returned: " + historyList.size()); // ★デバッグログ
        return historyList;
    }
}