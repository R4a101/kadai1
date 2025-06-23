package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.PatientDAO;

@WebServlet("/PatientRegistServlet")
public class PatientRegistServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        // パラメータ取得
        String patid = request.getParameter("patid");
        String patlname = request.getParameter("patlname"); // 姓
        String patfname = request.getParameter("patfname"); // 名
        String hokenmei = request.getParameter("hokenmei");
        String hokenexp = request.getParameter("hokenexp");

        // 登録者情報（セッションから取得）
        String registeredBy = (String) session.getAttribute("empid");
        if (registeredBy == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "認証情報がありません");
            return;
        }

        // バリデーション
        if (!isValidInput(patid, patlname, patfname, hokenmei, hokenexp)) {
            request.setAttribute("error", "入力内容に不備があります");
            request.getRequestDispatcher("patientRegist.jsp").forward(request, response);
            return;
        }

        PatientDAO dao = new PatientDAO();
        boolean result = dao.insertPatient(patid, patlname, patfname, hokenmei, hokenexp, registeredBy);

        if (result) {
            session.setAttribute("success", "患者登録が完了しました");
            response.sendRedirect("reception_menu.jsp");
        } else {
            request.setAttribute("error", "患者登録に失敗しました");
            request.getRequestDispatcher("patientRegist.jsp").forward(request, response);
        }
    }

    private boolean isValidInput(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

