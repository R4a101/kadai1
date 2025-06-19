package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.TreatmentDAO;

@WebServlet("/TreatmentConfirmServlet")
public class TreatmentConfirmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 医師権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"doctor".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }

        // 既存のセッション取得
        // HttpSession session = request.getSession(); // 上で取得済み

        String patid = (String) session.getAttribute("tempPatId");
        String empid = (String) session.getAttribute("tempEmpId");
        List<Map<String, String>> medicinesList = (List<Map<String, String>>) session.getAttribute("tempMedicinesList");
        String fromPrescription = (String) session.getAttribute("fromPrescription");

        // ... (以降の既存ロジック) ...
    }

    @SuppressWarnings("unchecked")
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

        String patid = (String) session.getAttribute("tempPatId");
        String empid = (String) session.getAttribute("tempEmpId");
        List<Map<String, String>> medicinesList = (List<Map<String, String>>) session.getAttribute("tempMedicinesList");

        // ... (以降の既存ロジック) ...
    }
}