package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSessionを追加

import DAO.TreatmentDAO; // DAOを使用 (TreatmentDAO.javaの実装は別途必要)
import model.Employee;   // model.Employeeクラスのimport (Employee.javaの実装は別途必要)

@WebServlet("/TreatmentServlet") // このサーブレットは現在のメインフローでは未使用の可能性があります
public class TreatmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // doGetメソッドももしあれば、その冒頭にも追加してください
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 医師権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"doctor".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }

        // 既存のセッション取得
        // HttpSession session = request.getSession(); // 上で取得済み

        String patid = request.getParameter("patid");

        // 従業員IDの取得方法: セッションのEmployeeオブジェクトから取得する想定
        Employee emp = (Employee) session.getAttribute("employee");
        String empid = null;
        if (emp != null) {
            empid = emp.getEmpid(); // EmployeeクラスにgetEmpid()メソッドが必要
        }

        if (empid == null) {
            // 従業員情報がセッションにない場合はログインページなどにリダイレクト
            session.setAttribute("errorMessage", "ログインしていません。再度ログインしてください。");
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // login.jspは仮のパス
            return;
        }

        // ... (以降の既存ロジック) ...
    }
}