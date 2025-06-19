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

@WebServlet("/PatientDeleteServlet")
public class PatientDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 受付権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"reception".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }

        // 既存のセッションチェックとEmployeeオブジェクト取得ロジック（AuthFilterを導入しない場合残す）
        // if (session == null || session.getAttribute("employee") == null) {
        //     System.out.println("PatientDeleteServlet: Session or employee attribute is null. Redirecting to login.");
        //     response.sendRedirect(request.getContextPath() + "/login.jsp");
        //     return;
        // }
        // Employee emp = (Employee) session.getAttribute("employee");
        // if (emp.getEmprole() != 0) { // 0 が受付と仮定
        //     System.out.println("PatientDeleteServlet: Employee role is not 0 (reception). Emprole: " + emp.getEmprole() + ". Redirecting to login.");
        //     response.sendRedirect(request.getContextPath() + "/login.jsp"); // または権限エラーページ
        //     return;
        // }

        String patid = request.getParameter("patid");
        System.out.println("PatientDeleteServlet: Attempting to delete patient with patid: " + patid);

        // ... (以降の既存ロジック) ...
    }

    // doGetは通常このサーブレットでは使用しないが、誤ってアクセスされた場合のために実装
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("PatientDeleteServlet: doGet called. Redirecting to PatientListServlet as this servlet should be accessed via POST.");
        // 受付権限チェック (GETアクセスでも必要なら追加)
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"reception".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        // GETでアクセスされた場合は、何もせず一覧に戻すか、エラーメッセージを表示する
        response.sendRedirect(request.getContextPath() + "/PatientListServlet");
    }
}