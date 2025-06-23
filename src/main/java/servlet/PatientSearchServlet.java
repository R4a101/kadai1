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

@WebServlet("/PatientSearchServlet")
public class PatientSearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        PatientDAO dao = new PatientDAO();
        List<Map<String, Object>> patients = dao.searchPatients(keyword, false);
        request.setAttribute("patients", patients);
        request.getRequestDispatcher("patientSearch.jsp").forward(request, response);
    }
}
