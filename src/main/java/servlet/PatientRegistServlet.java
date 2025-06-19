package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.PatientDAO;

@WebServlet("/PatientRegistServlet")
public class PatientRegistServlet extends HttpServlet {
    @Override
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

        // 既存のセッション取得
        // HttpSession session = request.getSession(); // 上で取得済み

        // パラメータ取得
        String patid = request.getParameter("patid");
        String patlname = request.getParameter("patlname"); // 姓
        String patfname = request.getParameter("patfname"); // 名
        String hokenmei = request.getParameter("hokenmei");
        String hokenexp = request.getParameter("hokenexp");

        // 登録者情報（セッションから取得）
        // roleチェックとは別に、empidがセッションにあるか確認する必要がある。
        // SessionからEmployeeオブジェクトを取得して、そこからempidを取得する方がより堅牢。
        // Employee emp = (Employee) session.getAttribute("employee");
        // String registeredBy = (emp != null) ? emp.getEmpid() : null;
        String registeredBy = (String) session.getAttribute("empid"); // 既存のempidセッション属性を利用する場合

        if (registeredBy == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "認証情報がありません");
            return;
        }

        // ... (以降の既存ロジック) ...
    }
    // doGetメソッドももしあれば、その冒頭にも追加してください
}