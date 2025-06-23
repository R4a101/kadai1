package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.EmployeeDAO;
import model.Employee;

@WebServlet("/EmployeePasswordChangeServlet")
public class EmployeePasswordChangeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Employee emp = null;
        if (session != null) {
            emp = (Employee) session.getAttribute("employee");
        }

        if (emp == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/employeePasswordChange.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Employee emp = null;
        if (session != null) {
            emp = (Employee) session.getAttribute("employee");
        }

        if (emp == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String passwordInput1 = request.getParameter("password1");
        String passwordInput2 = request.getParameter("password2");

        if (passwordInput1 == null || passwordInput1.isEmpty() ||
                passwordInput2 == null || passwordInput2.isEmpty()) {
            request.setAttribute("error", "新しいパスワードと確認用パスワードの両方を入力してください。");
            request.getRequestDispatcher("/employeePasswordChange.jsp").forward(request, response);
            return;
        }

        if (!passwordInput1.equals(passwordInput2)) {
            request.setAttribute("error", "新しいパスワードと確認用パスワードが一致しません。");
            request.getRequestDispatcher("/employeePasswordChange.jsp").forward(request, response);
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();

        // --- 現在のパスワードと同じかどうかのチェック (DAOの新しいメソッドを使用) ---
        try {
            if (dao.isNewPasswordSameAsCurrent(emp.getEmpid(), passwordInput1)) {
                request.setAttribute("error", "新しいパスワードが現在のパスワードと同じです。異なるパスワードを設定してください。");
                System.out.println("EmployeePasswordChangeServlet: New password is same as current for EMPID: " + emp.getEmpid());
                request.getRequestDispatcher("/employeePasswordChange.jsp").forward(request, response);
                return;
            }
        } catch (RuntimeException e) { // isNewPasswordSameAsCurrent内でsha256エラーが起きた場合など
            System.err.println("EmployeePasswordChangeServlet: Error during password same check for EMPID: " + emp.getEmpid());
            e.printStackTrace();
            request.setAttribute("error", "パスワードの検証処理中にエラーが発生しました。");
            request.getRequestDispatcher("/employeePasswordChange.jsp").forward(request, response);
            return;
        }
        // --- チェックここまで ---

        // パスワード更新処理
        boolean result = dao.updatePassword(emp.getEmpid(), passwordInput1);

        if (result) {
            request.setAttribute("message", "パスワード変更が完了しました。");
            System.out.println("EmployeePasswordChangeServlet: Password changed successfully for EMPID: " + emp.getEmpid());
        } else {
            request.setAttribute("error", "パスワード変更に失敗しました。データベースエラーの可能性があります。");
            System.err.println("EmployeePasswordChangeServlet: Password change failed for EMPID: " + emp.getEmpid());
        }
        request.getRequestDispatcher("/employeePasswordChange.jsp").forward(request, response);
    }
}