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

@WebServlet("/EmployeeUpdateServlet")
public class EmployeeUpdateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Employee emp = (session != null) ? (Employee) session.getAttribute("employee") : null;
        if (emp == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String empid = emp.getEmpid();
        String lastName = request.getParameter("lastName") != null ? request.getParameter("lastName").trim() : "";
        String firstName = request.getParameter("firstName") != null ? request.getParameter("firstName").trim() : "";

        if (lastName.isEmpty() || firstName.isEmpty()) {
            request.setAttribute("error", "姓と名は必須入力です");
            request.getRequestDispatcher("employeeUpdate.jsp").forward(request, response);
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();
        boolean success = dao.updateEmployeeName(empid, lastName, firstName);

        if (success) {
            // セッション情報も更新
            emp.setLastName(lastName);
            emp.setFirstName(firstName);
            session.setAttribute("employee", emp);
            request.setAttribute("message", "従業員情報を更新しました。");
        } else {
            request.setAttribute("error", "更新に失敗しました");
        }
        request.getRequestDispatcher("employeeUpdate.jsp").forward(request, response);
    }
}
