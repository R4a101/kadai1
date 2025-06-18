package servlet;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.PatientDAO;
import model.Employee;

@WebServlet("/PatientEditServlet")
public class PatientEditServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // セッションチェックと権限チェック (受付ユーザー emprole==0)
        if (session == null || session.getAttribute("employee") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Employee emp = (Employee) session.getAttribute("employee");
        if (emp.getEmprole() != 0) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // または権限エラーページ
            return;
        }

        String patid = request.getParameter("patid");

        if (patid == null || patid.isEmpty()) {
            request.setAttribute("errorMessage", "患者IDが指定されていません。");
            request.getRequestDispatcher("/patientList.jsp").forward(request, response); // 一覧へ戻す
            return;
        }

        PatientDAO dao = new PatientDAO();
        Map<String, Object> patient = dao.findPatientById(patid);

        if (patient == null) {
            request.setAttribute("errorMessage", "指定された患者IDの情報が見つかりません。");
            request.getRequestDispatcher("/patientList.jsp").forward(request, response); // 一覧へ戻す
            return;
        }

        request.setAttribute("patient", patient);
        request.getRequestDispatcher("/patientEditForm.jsp").forward(request, response);
    }
}