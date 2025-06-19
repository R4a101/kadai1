package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.TreatmentDAO;

@WebServlet("/MedicineDeleteServlet")
public class MedicineDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 受付または管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || (!"reception".equals(role))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 薬投与削除画面へフォワード
        request.getRequestDispatcher("medicineDelete.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 受付または管理者権限チェック
        HttpSession session = request.getSession(false); // doPostでも新たにセッションを取得し直す
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || (!"reception".equals(role) && !"admin".equals(role))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        // 既存のセッション取得 (doPostの冒頭でHttpSession session = request.getSession(); が既にありますが、
        // 権限チェックでsessionを再取得しているので、既存のものは削除するか、この後の処理で使用するなら残してください。)
        // ここでは、権限チェックで取得したsession変数をそのまま使うことを推奨します。
        // HttpSession session = request.getSession(); // <- これは削除またはコメントアウトしても良い

        String patid = request.getParameter("patid");
        String medicineid = request.getParameter("medicineid");

        if (patid == null || patid.trim().isEmpty() ||
                medicineid == null || medicineid.trim().isEmpty()) {
            session.setAttribute("errorMessage", "患者IDと薬剤IDは必須です。");
            response.sendRedirect(request.getContextPath() + "/MedicineDeleteServlet");
            return;
        }

        TreatmentDAO dao = new TreatmentDAO();
        boolean success = dao.deleteTreatmentByPatIdAndMedicineId(patid, medicineid);

        if (success) {
            session.setAttribute("successMessage", "患者ID: " + patid + " の薬剤ID: " + medicineid + " の投与情報を削除しました。");
        } else {
            session.setAttribute("errorMessage", "薬剤ID: " + medicineid + " の投与情報の削除に失敗しました。該当データがないか、データベースエラーの可能性があります。");
        }
        // 削除画面にリダイレクトして結果を表示
        response.sendRedirect(request.getContextPath() + "/MedicineDeleteServlet");
    }
}