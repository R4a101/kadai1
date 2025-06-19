package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSessionを追加

import DAO.PatientDAO;

@WebServlet("/PatientSearchServlet")
public class PatientSearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 受付権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"reception".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String keyword = request.getParameter("keyword");
        PatientDAO dao = new PatientDAO();
        List<Map<String, Object>> patients = dao.searchPatients(keyword, false);
        request.setAttribute("patients", patients);
        request.getRequestDispatcher("patientSearch.jsp").forward(request, response);
    }
    // doPostメソッドももしあれば、その冒頭にも追加してください
}