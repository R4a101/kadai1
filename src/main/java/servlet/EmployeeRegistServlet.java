package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import DAO.EmployeeDAO;

@WebServlet("/EmployeeRegistServlet")
public class EmployeeRegistServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String empid = request.getParameter("empid");
        String lastName = request.getParameter("lastName");
        String firstName = request.getParameter("firstName");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        int emprole = Integer.parseInt(request.getParameter("emprole"));

        if (!password.equals(password2)) {
            request.setAttribute("error", "パスワードが一致しません。");
            request.getRequestDispatcher("employeeRegist.jsp").forward(request, response);
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();
        boolean result = dao.insertEmployee(empid, lastName, firstName, password, emprole);

        if (result) {
            response.sendRedirect("admin_menu.jsp");
        } else {
            request.setAttribute("error", "従業員登録に失敗しました。");
            request.getRequestDispatcher("employeeRegist.jsp").forward(request, response);
        }
    }
}
