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
        HttpSession session = request.getSession(false);
        Employee adminUser = (session != null) ? (Employee) session.getAttribute("employee") : null;
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (adminUser == null || !"admin".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }
        request.getRequestDispatcher("adminSelfPasswordChange.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        
        Employee adminUser = (session != null) ? (Employee) session.getAttribute("employee") : null;
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (adminUser == null || !"admin".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("error", "新しいパスワードと確認用パスワードの両方を入力してください。");
            request.getRequestDispatcher("adminSelfPasswordChange.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "新しいパスワードが一致しません。");
            request.getRequestDispatcher("adminSelfPasswordChange.jsp").forward(request, response);
            return;
        }

        LoginDAO loginDao = new LoginDAO();
        String adminUserId = adminUser.getEmpid(); // セッションのEmployeeオブジェクトから管理者IDを取得

        // ★★★修正・追加★★★ 新しいパスワードが現在のパスワードと同じでないかチェック
        if (loginDao.isNewPasswordSameAsCurrentAdmin(adminUserId, newPassword)) {
            request.setAttribute("error", "現在のパスワードと同じです。別のパスワードを設定してください。");
            request.getRequestDispatcher("adminSelfPasswordChange.jsp").forward(request, response);
            return; // 処理を中断
        }
        
        // パスワード更新処理
        boolean result;
        try {
            result = loginDao.updateAdminPassword(adminUserId, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "パスワードの変更中に予期せぬエラーが発生しました。");
            request.getRequestDispatcher("adminSelfPasswordChange.jsp").forward(request, response);
            return;
        }
        
        if (result) {
            request.setAttribute("message", "管理者パスワードの変更が完了しました。");
        } else {
            request.setAttribute("error", "管理者パスワードの変更に失敗しました。データベース接続などを確認してください。");
        }
        request.getRequestDispatcher("adminSelfPasswordChange.jsp").forward(request, response);
    }
}