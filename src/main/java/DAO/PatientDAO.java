package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/r4a101webap2025?serverTimezone=Asia/Tokyo&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    // 患者情報を全件取得
    public List<Map<String, Object>> findAllPatients() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT patid, patfname, patlname, hokenmei, hokenexp FROM patient";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("patid", rs.getString("patid"));
                map.put("patfname", rs.getString("patfname"));
                map.put("patlname", rs.getString("patlname"));
                map.put("hokenmei", rs.getString("hokenmei"));
                map.put("hokenexp", rs.getDate("hokenexp"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 患者登録
    public boolean insertPatient(String patid, String patlname, String patfname, String hokenmei, String hokenexp, String registeredBy) {
        String sql = "INSERT INTO patient (patid, patlname, patfname, hokenmei, hokenexp, registered_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patid);
            ps.setString(2, patlname); // 姓
            ps.setString(3, patfname); // 名
            ps.setString(4, hokenmei);
            ps.setString(5, hokenexp);
            ps.setString(6, registeredBy);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 患者検索（キーワード・期限切れオプション）
    public List<Map<String, Object>> searchPatients(String keyword, boolean expiredOnly) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT patid, patfname, patlname, hokenmei, hokenexp FROM patient WHERE 1=1");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (patfname LIKE ? OR patlname LIKE ?)");
        }
        if (expiredOnly) {
            sql.append(" AND hokenexp < CURDATE()");
        }
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("patid", rs.getString("patid"));
                map.put("patfname", rs.getString("patfname"));
                map.put("patlname", rs.getString("patlname"));
                map.put("hokenmei", rs.getString("hokenmei"));
                map.put("hokenexp", rs.getDate("hokenexp"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

 // DAO.PatientDAO.java
 // (findPatientById は既存のままでOK)

 // 患者情報の更新（保険証情報など）
 public boolean updatePatient(String patid, String hokenmei, String hokenexp) {
     // hokenexpが空文字列の場合、DBにNULLをセットするなどの考慮が必要な場合がある
     // ここではDBが適切に処理することを期待
     String sql = "UPDATE patient SET hokenmei = ?, hokenexp = ? WHERE patid = ?";
     try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
          PreparedStatement ps = conn.prepareStatement(sql)) {
         ps.setString(1, hokenmei);

         if (hokenexp != null && hokenexp.isEmpty()) { // 有効期限が空文字で送信された場合
             ps.setNull(2, java.sql.Types.DATE);
         } else {
             // 文字列の日付 (yyyy-MM-dd) を java.sql.Date に変換
             // この変換はサーブレット側で行うか、DAOが文字列を受け入れるようにするか設計による
             // ここでは文字列をそのままセットし、DBの暗黙の型変換に頼る（MySQLなら動くことが多いが非推奨）
             // より安全なのは、サーブレットで java.sql.Date に変換して渡すか、
             // DAOで文字列を受け取って java.sql.Date に変換すること。
             // 今回は既存のDAOに合わせて文字列を渡す前提で進める。
             // もしDBが 'YYYY-MM-DD' 形式の文字列をDATE型に自動変換できない場合はエラーになる。
             ps.setString(2, hokenexp);
         }

         ps.setString(3, patid);
         int count = ps.executeUpdate();
         return count > 0;
     } catch (SQLException e) {
         e.printStackTrace();
         return false;
     }
     // catch (ParseException e) { // もしDAO内で日付文字列をパースする場合
     //    e.printStackTrace();
     //    return false;
     // }
 }

    // 患者情報の削除
    public boolean deletePatient(String patid) {
        String sql = "DELETE FROM patient WHERE patid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patid);
            int count = ps.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 個別患者情報取得（編集画面用など）
    public Map<String, Object> findPatientById(String patid) {
        String sql = "SELECT patid, patfname, patlname, hokenmei, hokenexp FROM patient WHERE patid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("patid", rs.getString("patid"));
                map.put("patfname", rs.getString("patfname"));
                map.put("patlname", rs.getString("patlname"));
                map.put("hokenmei", rs.getString("hokenmei"));
                map.put("hokenexp", rs.getDate("hokenexp"));
                return map;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
