package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import DAO.PatientDAO;
import jakarta.servlet.http.HttpSession;

@WebServlet("/DoctorPatientSearchServlet")
public class DoctorPatientSearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"doctor".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }
        PatientDAO dao = new PatientDAO();
        List<Map<String, Object>> patients = dao.findAllPatients();
        request.setAttribute("patients", patients);
        request.getRequestDispatcher("doctorPatientList.jsp").forward(request, response);
    }
}
