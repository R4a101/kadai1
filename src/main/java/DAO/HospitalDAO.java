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

public class HospitalDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/r4a101webap2025?serverTimezone=Asia/Tokyo&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password"; // ご自身のパスワードに変更してください

    // ... insertHospital, updateHospital, deleteHospital は変更なし ...
    
    // 病院登録
    public boolean insertHospital(String hospid, String hospname, String address, String tel, String email, int capital) {
        String sql = "INSERT INTO hospital (hospid, hospname, address, tel, email, capital) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hospid);
            ps.setString(2, hospname);
            ps.setString(3, address);
            ps.setString(4, tel);
            if (email != null && email.isEmpty()) {
                 ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                 ps.setString(5, email);
            }
            ps.setInt(6, capital);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ★修正★ 病院一覧取得 (資本金を桁区切りで返す)
    public List<Map<String, String>> findAllHospitals() {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT hospid, hospname, address, tel, email, capital FROM hospital ORDER BY hospid";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("hospid", rs.getString("hospid"));
                map.put("hospname", rs.getString("hospname"));
                map.put("address", rs.getString("address"));
                map.put("tel", rs.getString("tel"));
                map.put("email", rs.getString("email"));
                // ★資本金をフォーマットして格納
                map.put("capital", String.format("%,d", rs.getInt("capital")));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ★修正★ 病院個別取得 (資本金を桁区切りで返す)
    public Map<String, String> findHospitalById(String hospid) {
        String sql = "SELECT hospid, hospname, address, tel, email, capital FROM hospital WHERE hospid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hospid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("hospid", rs.getString("hospid"));
                map.put("hospname", rs.getString("hospname"));
                map.put("address", rs.getString("address"));
                map.put("tel", rs.getString("tel"));
                map.put("email", rs.getString("email"));
                // ★資本金をフォーマットして格納
                map.put("capital", String.format("%,d", rs.getInt("capital")));
                return map;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 病院情報更新
    public boolean updateHospital(String hospid, String hospname, String address, String tel, String email, int capital) {
        String sql = "UPDATE hospital SET hospname = ?, address = ?, tel = ?, email = ?, capital = ? WHERE hospid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hospname);
            ps.setString(2, address);
            ps.setString(3, tel);
            if (email != null && email.isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, email);
            }
            ps.setInt(5, capital);
            ps.setString(6, hospid);
            int count = ps.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 病院削除
    public boolean deleteHospital(String hospid) {
        String sql = "DELETE FROM hospital WHERE hospid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hospid);
            int count = ps.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ★修正★ 資本金で検索 (資本金を桁区切りで返す)
    public List<Map<String, String>> findHospitalsByMinCapital(int minCapital) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT hospid, hospname, address, tel, email, capital FROM hospital WHERE capital >= ? ORDER BY capital DESC, hospid";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, minCapital);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("hospid", rs.getString("hospid"));
                map.put("hospname", rs.getString("hospname"));
                map.put("address", rs.getString("address"));
                map.put("tel", rs.getString("tel"));
                map.put("email", rs.getString("email"));
                // ★資本金をフォーマットして格納
                map.put("capital", String.format("%,d", rs.getInt("capital")));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}