package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSessionをインポート

import DAO.EmployeeDAO;

@WebServlet("/EmployeeRegistServlet")
public class EmployeeRegistServlet extends HttpServlet {
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

        String empid = request.getParameter("empid");
        String lastName = request.getParameter("lastName");
        String firstName = request.getParameter("firstName");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        int emprole = Integer.parseInt(request.getParameter("emprole"));

        // ... (以降の既存ロジック) ...
    }
}