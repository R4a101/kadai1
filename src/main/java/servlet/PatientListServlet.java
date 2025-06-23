package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSession をインポート

import DAO.PatientDAO;
import model.Employee; // model.Employee をインポート

@WebServlet("/PatientListServlet")
public class PatientListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); // doGetでも文字化け対策
        HttpSession session = request.getSession(false);

        // セッションチェックと権限チェック
        if (session == null || session.getAttribute("employee") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Employee emp = (Employee) session.getAttribute("employee");
        if (emp.getEmprole() != 0) { // 0 が受付と仮定
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // もしくは権限エラーページ
            return;
        }


        String keyword = request.getParameter("keyword");
        // "true" という文字列で送られてくるので、nullチェックと文字列比較でbooleanに変換
        boolean expiredOnly = "true".equals(request.getParameter("expiredOnly"));

        PatientDAO dao = new PatientDAO();
        List<Map<String, Object>> patients;

        if ((keyword != null && !keyword.isEmpty()) || expiredOnly) {
            patients = dao.searchPatients(keyword, expiredOnly);
        } else {
            patients = dao.findAllPatients(); // 条件がなければ全件検索
        }

        request.setAttribute("patients", patients);
        // 検索条件もJSPへ渡してフォームに再表示できるようにする (JSP側で既に対応済み)
        // request.setAttribute("searchKeyword", keyword);
        // request.setAttribute("searchExpiredOnly", expiredOnly);

        request.getRequestDispatcher("/patientList.jsp").forward(request, response);
    }
}