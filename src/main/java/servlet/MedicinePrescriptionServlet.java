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
import jakarta.servlet.http.HttpSession;

@WebServlet("/MedicinePrescriptionServlet")
public class MedicinePrescriptionServlet extends HttpServlet {
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

        // エラーメッセージがあればリクエストスコープに設定
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        // ... (以降の既存ロジック) ...
    }

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
        String empid = request.getParameter("empid");

        // ... (以降の既存ロジック) ...
    }

    private void saveCurrentInputsToSession(HttpSession session, String patid, String empid,
                                            String[] medicineIds, String[] dosages, String[] instructions) {
        // ... (既存ロジック) ...
    }
}