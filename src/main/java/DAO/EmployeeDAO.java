package DAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Employee;

public class EmployeeDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/r4a101webap2025?serverTimezone=Asia/Tokyo&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password"; // ご自身のパスワードに変更してください

    /**
     * 従業員登録
     */
    public boolean insertEmployee(String empid, String lastName, String firstName, String password, int emprole) {
        String sql = "INSERT INTO employee (empid, last_name, first_name, emppasswd, emprole) VALUES (?, ?, ?, ?, ?)";
        System.out.println("EmployeeDAO: Attempting to insert employee. EMPID: " + empid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empid);
            ps.setString(2, lastName);
            ps.setString(3, firstName);
            ps.setString(4, sha256(password));
            ps.setInt(5, emprole);
            int result = ps.executeUpdate();
            System.out.println("EmployeeDAO (insertEmployee): Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("EmployeeDAO (insertEmployee): SQL Exception for EMPID " + empid);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 従業員一覧を取得
     */
    public List<Employee> findAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT empid, last_name, first_name, emprole FROM employee";
        System.out.println("EmployeeDAO: Finding all employees.");
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Employee(
                    rs.getString("empid"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getInt("emprole")
                ));
            }
            System.out.println("EmployeeDAO (findAllEmployees): Found " + list.size() + " employees.");
        } catch (SQLException e) {
            System.err.println("EmployeeDAO (findAllEmployees): SQL Exception.");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 従業員認証（パスワードはSHA-256で比較）
     */
    public Employee validateEmployee(String empid, String password) {
        String sql = "SELECT empid, last_name, first_name, emppasswd, emprole FROM employee WHERE empid = ?";
        System.out.println("EmployeeDAO: Validating employee. EMPID: " + empid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedDbPassword = rs.getString("emppasswd");
                    String hashedInputPassword = sha256(password);
                    if (hashedDbPassword != null && hashedDbPassword.equals(hashedInputPassword)) {
                        System.out.println("EmployeeDAO (validateEmployee): Password match for EMPID: " + empid);
                        return new Employee(
                            rs.getString("empid"),
                            rs.getString("last_name"),
                            rs.getString("first_name"),
                            rs.getInt("emprole")
                        );
                    } else {
                        System.out.println("EmployeeDAO (validateEmployee): Password mismatch for EMPID: " + empid);
                    }
                } else {
                    System.out.println("EmployeeDAO (validateEmployee): No employee found with EMPID: " + empid);
                }
            }
        } catch (SQLException e) {
            System.err.println("EmployeeDAO (validateEmployee): SQL Exception for EMPID " + empid);
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("EmployeeDAO (validateEmployee): Hashing error for EMPID " + empid);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定された従業員IDの現在のハッシュ化されたパスワードを取得する。
     * @param empid 従業員ID
     * @return ハッシュ化されたパスワード文字列。該当従業員が見つからない場合はnull。
     */
    public String getCurrentHashedPassword(String empid) {
        String sql = "SELECT emppasswd FROM employee WHERE empid = ?";
        System.out.println("EmployeeDAO: Getting current hashed password for EMPID: " + empid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("emppasswd");
                }
            }
        } catch (SQLException e) {
            System.err.println("EmployeeDAO (getCurrentHashedPassword): SQL Exception for EMPID " + empid);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新しいパスワード（平文）が、指定された従業員の現在のパスワードと同じかどうかをチェックする。
     * @param empid 従業員ID
     * @param newRawPassword 新しいパスワード（平文）
     * @return 同じ場合はtrue、異なる場合または現在のパスワードが取得できない場合はfalse
     */
    public boolean isNewPasswordSameAsCurrent(String empid, String newRawPassword) {
        String currentHashedPassword = getCurrentHashedPassword(empid);
        if (currentHashedPassword == null) {
            System.out.println("EmployeeDAO (isNewPasswordSameAsCurrent): Current password not found for EMPID: " + empid + ". Assuming new password is not the same.");
            return false; // 現在のパスワードが取得できない場合は比較不可 (異なるものとして扱う)
        }
        String newHashedPassword;
        try {
            newHashedPassword = sha256(newRawPassword); // このDAO内のsha256メソッドを呼び出す
        } catch (RuntimeException e) {
            System.err.println("EmployeeDAO (isNewPasswordSameAsCurrent): Hashing error for new password for EMPID: " + empid);
            e.printStackTrace();
            return false; // ハッシュ化でエラーが発生した場合も比較不可 (異なるものとして扱う)
        }
        boolean isSame = currentHashedPassword.equals(newHashedPassword);
        System.out.println("EmployeeDAO (isNewPasswordSameAsCurrent): Is new password same as current for EMPID " + empid + "? " + isSame);
        return isSame;
    }


    /**
     * パスワードをSHA-256でハッシュ化 (privateのままでOK、isNewPasswordSameAsCurrentから呼び出される)
     */
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
            System.err.println("EmployeeDAO (sha256): SHA-256 algorithm not found.");
            e.printStackTrace();
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * パスワード変更
     */
    public boolean updatePassword(String empid, String newPassword) {
        String sql = "UPDATE employee SET emppasswd = ? WHERE empid = ?";
        System.out.println("EmployeeDAO: Attempting to update password for EMPID: " + empid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String hashedPassword = sha256(newPassword);
            // System.out.println("EmployeeDAO (updatePassword): New hashed password: " + hashedPassword); // ログはisNewPasswordSameAsCurrent内でも出るので省略可
            ps.setString(1, hashedPassword);
            ps.setString(2, empid);
            int count = ps.executeUpdate();
            System.out.println("EmployeeDAO (updatePassword): Rows updated: " + count + " for EMPID: " + empid);
            return count > 0;
        } catch (SQLException e) {
            System.err.println("EmployeeDAO (updatePassword): SQL Exception for EMPID " + empid);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 姓・名の変更
     */
    public boolean updateEmployeeName(String empid, String lastName, String firstName) {
        String sql = "UPDATE employee SET last_name = ?, first_name = ? WHERE empid = ?";
        System.out.println("EmployeeDAO: Attempting to update name for EMPID: " + empid);
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lastName);
            ps.setString(2, firstName);
            ps.setString(3, empid);
            int result = ps.executeUpdate();
            System.out.println("EmployeeDAO (updateEmployeeName): Rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("EmployeeDAO (updateEmployeeName): SQL Exception for EMPID " + empid);
            e.printStackTrace();
            return false;
        }
    }
}