package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.PatientDAO;
import model.Employee;

@WebServlet("/PatientUpdateServlet")
public class PatientUpdateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // セッションチェックと権限チェック
        if (session == null || session.getAttribute("employee") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Employee emp = (Employee) session.getAttribute("employee");
        if (emp.getEmprole() != 0) { // 受付ユーザー(emprole==0)のみ
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String patid = request.getParameter("patid");
        String hokenmei = request.getParameter("hokenmei");
        String hokenexp = request.getParameter("hokenexp"); // yyyy-MM-dd 形式で送られてくる

        // 簡単なバリデーション
        if (patid == null || patid.isEmpty()) {
            request.setAttribute("errorMessage", "患者IDが不正です。");
            // 編集フォームにフォワードするか、エラーページに飛ばす
            request.getRequestDispatcher("/patientEditForm.jsp").forward(request, response);
            return;
        }
        // hokenmei や hokenexp が空でもDB側でNULLを許容するならこれ以上のバリデーションは任意
        // ただし、日付形式のバリデーションはより厳密に行うのが望ましい
        // ここではDAOにそのまま渡す


        PatientDAO dao = new PatientDAO();
        // DAOのupdatePatientメソッドはhokenmeiとhokenexpを更新するようになっているのでそのまま利用
        boolean success = dao.updatePatient(patid, hokenmei, hokenexp);

        if (success) {
            // 成功メッセージをセッションに格納して一覧にリダイレクト（PRGパターン）
            session.setAttribute("successMessage", "患者ID: " + patid + " の保険証情報を更新しました。");
            response.sendRedirect(request.getContextPath() + "/PatientListServlet");
        } else {
            // 失敗した場合、エラーメッセージをリクエストにセットして編集フォームに戻す
            request.setAttribute("errorMessage", "保険証情報の更新に失敗しました。");
            // 再度編集フォームを表示するために、元の患者情報を取得してセットする必要がある
            request.setAttribute("patient", dao.findPatientById(patid));
            request.getRequestDispatcher("/patientEditForm.jsp").forward(request, response);
        }
    }
}