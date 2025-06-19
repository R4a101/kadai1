package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.PatientDAO;
import model.Employee;

@WebServlet("/PatientUpdateServlet")
public class PatientUpdateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 受付権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"reception".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 既存のセッションチェックとEmployeeオブジェクト取得ロジック
        // if (session == null || session.getAttribute("employee") == null) {
        //     response.sendRedirect(request.getContextPath() + "/login.jsp");
        //     return;
        // }
        // Employee emp = (Employee) session.getAttribute("employee");
        // if (emp.getEmprole() != 0) { // 受付ユーザー(emprole==0)のみ
        //     response.sendRedirect(request.getContextPath() + "/login.jsp");
        //     return;
        // }

        String patid = request.getParameter("patid");
        String hokenmei = request.getParameter("hokenmei");
        String hokenexp = request.getParameter("hokenexp");

        // ... (以降の既存ロジック) ...
    }
    // doGetメソッドももしあれば、その冒頭にも追加してください
}