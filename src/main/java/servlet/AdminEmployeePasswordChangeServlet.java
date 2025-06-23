package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.EmployeeDAO;

@WebServlet("/AdminEmployeePasswordChangeServlet")
public class AdminEmployeePasswordChangeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }

        // employeeList.jspなどからempidが渡された場合、フォームに初期表示する
        String empidFromQuery = request.getParameter("empid");
        if (empidFromQuery != null && !empidFromQuery.trim().isEmpty()) {
            request.setAttribute("empid", empidFromQuery);
        }

        request.getRequestDispatcher("adminEmployeePasswordChange.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // 管理者権限チェック
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String empid = request.getParameter("empid");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // バリデーション
        if (empid == null || empid.trim().isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("error", "全ての項目を入力してください。");
            request.setAttribute("empidForForm", empid); // 入力値をJSPに戻す
            request.getRequestDispatcher("adminEmployeePasswordChange.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "新しいパスワードが一致しません。");
            request.setAttribute("empidForForm", empid);
            request.getRequestDispatcher("adminEmployeePasswordChange.jsp").forward(request, response);
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();

        // ★修正・追加：新しいパスワードが現在のパスワードと同じでないかチェック
        if (dao.isNewPasswordSameAsCurrent(empid.trim(), newPassword)) {
            request.setAttribute("error", "現在のパスワードと同じです。別のパスワードを設定してください。");
            request.setAttribute("empidForForm", empid); // 入力値を保持
            request.getRequestDispatcher("adminEmployeePasswordChange.jsp").forward(request, response);
            return; // 処理を中断
        }

        // パスワード更新処理
        boolean success = dao.updatePassword(empid.trim(), newPassword);

        if (success) {
            request.setAttribute("message", "従業員ID: " + empid.trim() + " のパスワードを変更しました。");
            // 成功時はempidの入力値をクリアするため、empidForFormはセットしない
        } else {
            // このエラーは、IDが存在しない場合にも発生する
            request.setAttribute("error", "パスワードの変更に失敗しました。指定された従業員IDが存在しないか、データベースエラーが発生した可能性があります。");
            request.setAttribute("empidForForm", empid); // 失敗時は入力値を保持
        }
        request.getRequestDispatcher("adminEmployeePasswordChange.jsp").forward(request, response);
    }
}