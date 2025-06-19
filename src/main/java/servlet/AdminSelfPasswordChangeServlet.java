package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.LoginDAO; // LoginDAOを使用
import model.Employee;

@WebServlet("/AdminSelfPasswordChangeServlet")
public class AdminSelfPasswordChangeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }
        // 以下は既存のadminUserチェック。AdminSelfPasswordChangeServletではEmployeeオブジェクトが必要なため残す。
        Employee adminUser = (session != null) ? (Employee) session.getAttribute("employee") : null;
        if (adminUser == null) { // roleがadminでもemployeeオブジェクトがなければおかしい状況
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.getRequestDispatcher("adminSelfPasswordChange.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }
        // 以下は既存のadminUserチェック。AdminSelfPasswordChangeServletではEmployeeオブジェクトが必要なため残す。
        Employee adminUser = (session != null) ? (Employee) session.getAttribute("employee") : null;
        if (adminUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // ... (以降の既存ロジック) ...
    }
}