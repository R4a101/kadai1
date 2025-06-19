package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSessionをインポート

import DAO.EmployeeDAO;
import model.Employee;

@WebServlet("/EmployeeListServlet")
public class EmployeeListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> employees = dao.findAllEmployees();
        request.setAttribute("employees", employees);
        request.getRequestDispatcher("employeeList.jsp").forward(request, response);
    }
    // doPostメソッドももしあれば、その冒頭にも管理者権限チェックを追加してください
}
