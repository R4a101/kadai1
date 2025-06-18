package DAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Employee;

public class LoginDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/r4a101webap2025?serverTimezone=Asia/Tokyo&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password"; // ご自身のDBパスワード

    public static final int ADMIN_ROLE_VALUE = 99; // 管理者ロール値

    /**
     * 管理者認証を行い、認証成功時に管理者のEmployeeオブジェクトを返す。
     * adminテーブルのパスワードは平文で比較。
     */
    public Employee validateAdminAndGetEmployee(String userid, String password) {
        String sql = "SELECT userid FROM admin WHERE userid = ? AND password = ?";
        System.out.println("LoginDAO: Validating admin. UserID: " + userid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userid);
            stmt.setString(2, password); // 平文パスワードで比較
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("LoginDAO: Admin validation successful for UserID: " + userid);
                    String adminLastName = "システム";
                    String adminFirstName = "管理者";
                    return new Employee(
                        rs.getString("userid"),
                        adminLastName,
                        adminFirstName,
                        ADMIN_ROLE_VALUE
                    );
                } else {
                    System.out.println("LoginDAO: Admin validation failed for UserID: " + userid);
                }
            }
        } catch (SQLException e) {
            System.err.println("LoginDAO (validateAdminAndGetEmployee): SQL Exception for UserID " + userid);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 従業員認証（パスワードはSHA-256で比較）
     */
    public Employee validateEmployee(String empid, String password) {
        String sql = "SELECT empid, last_name, first_name, emppasswd, emprole FROM employee WHERE empid = ?";
        System.out.println("LoginDAO: Validating employee. EmpID: " + empid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedDbPassword = rs.getString("emppasswd");
                    String hashedInputPassword = sha256(password);
                    if (hashedDbPassword != null && hashedDbPassword.equals(hashedInputPassword)) {
                        System.out.println("LoginDAO (validateEmployee): Password match for EmpID: " + empid);
                        return new Employee(
                            rs.getString("empid"),
                            rs.getString("last_name"),
                            rs.getString("first_name"),
                            rs.getInt("emprole")
                        );
                    } else {
                         System.out.println("LoginDAO (validateEmployee): Password mismatch for EmpID: " + empid);
                    }
                } else {
                    System.out.println("LoginDAO (validateEmployee): No employee found with EmpID: " + empid);
                }
            }
        } catch (SQLException e) {
            System.err.println("LoginDAO (validateEmployee): SQL Exception for EmpID " + empid);
            e.printStackTrace();
        } catch (RuntimeException e) {
             System.err.println("LoginDAO (validateEmployee): Error during password hashing for EmpID " + empid);
             e.printStackTrace();
        }
        return null;
    }

    // ★★★修正・追加★★★
    /**
     * 現在の管理者パスワード（平文）をDBから取得する。
     * @param userid 取得対象の管理者ID
     * @return DBに保存されているパスワード文字列。見つからない場合はnull。
     */
    private String getCurrentAdminPassword(String userid) {
        String sql = "SELECT password FROM admin WHERE userid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (SQLException e) {
            System.err.println("LoginDAO (getCurrentAdminPassword): SQL Exception for UserID " + userid);
            e.printStackTrace();
        }
        return null;
    }
    
    // ★★★修正・追加★★★
    /**
     * 新しいパスワードが現在のパスワードと同じかチェックする (平文比較)。
     * @param userid チェック対象の管理者ID
     * @param newRawPassword フォームから入力された新しいパスワード
     * @return 同じ場合はtrue, 異なる場合またはユーザーが見つからない場合はfalse
     */
    public boolean isNewPasswordSameAsCurrentAdmin(String userid, String newRawPassword) {
        String currentPassword = this.getCurrentAdminPassword(userid);
        if (currentPassword == null) {
            // ユーザーが存在しない場合、比較は意味をなさないのでfalseを返す
            return false;
        }
        // 平文同士で比較
        return currentPassword.equals(newRawPassword);
    }


    /**
     * 管理者のパスワードを更新する (adminテーブル)。
     * 現在のDB構造に合わせて、新しいパスワードも平文で保存。
     * セキュリティ警告: 本番環境ではパスワードはハッシュ化して保存すべきです。
     * @param userid 更新対象の管理者ID
     * @param newPassword 新しいパスワード (平文)
     * @return 更新が成功した場合はtrue、失敗した場合はfalse
     */
    public boolean updateAdminPassword(String userid, String newPassword) {
        String sql = "UPDATE admin SET password = ? WHERE userid = ?";
        System.out.println("LoginDAO: Attempting to update admin password for UserID: " + userid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword); // 新しいパスワードを平文で設定
            ps.setString(2, userid);
            int count = ps.executeUpdate();
            System.out.println("LoginDAO (updateAdminPassword): Rows updated: " + count + " for UserID: " + userid);
            return count > 0;
        } catch (SQLException e) {
            System.err.println("LoginDAO (updateAdminPassword): SQL Exception for UserID " + userid);
            e.printStackTrace();
            return false;
        }
    }

    private String sha256(String base) {
        if (base == null) base = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("LoginDAO (sha256): SHA-256 algorithm not found.");
            e.printStackTrace();
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}