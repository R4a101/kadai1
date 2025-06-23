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

@WebServlet("/PatientDeleteServlet")
public class PatientDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // セッションチェックと権限チェック (受付ユーザー emprole==0)
        if (session == null || session.getAttribute("employee") == null) {
            System.out.println("PatientDeleteServlet: Session or employee attribute is null. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Employee emp = (Employee) session.getAttribute("employee");
        if (emp.getEmprole() != 0) { // 0 が受付と仮定
            System.out.println("PatientDeleteServlet: Employee role is not 0 (reception). Emprole: " + emp.getEmprole() + ". Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // または権限エラーページ
            return;
        }

        String patid = request.getParameter("patid");
        System.out.println("PatientDeleteServlet: Attempting to delete patient with patid: " + patid);


        if (patid == null || patid.trim().isEmpty()) {
            // 通常、jspのhidden inputで送られるのでnullや空は考えにくいが、念のため
            System.out.println("PatientDeleteServlet: patid is null or empty.");
            session.setAttribute("errorMessage", "削除対象の患者IDが指定されていません。");
            response.sendRedirect(request.getContextPath() + "/PatientListServlet"); // 一覧へ戻す
            return;
        }

        PatientDAO dao = new PatientDAO();
        boolean success = false;
        try {
            success = dao.deletePatient(patid);
        } catch (Exception e) {
            System.err.println("PatientDeleteServlet: Exception during patient deletion for patid: " + patid);
            e.printStackTrace();
            session.setAttribute("errorMessage", "患者情報の削除中に予期せぬエラーが発生しました。");
            response.sendRedirect(request.getContextPath() + "/PatientListServlet");
            return;
        }


        if (success) {
            System.out.println("PatientDeleteServlet: Successfully deleted patient with patid: " + patid);
            session.setAttribute("successMessage", "患者ID: " + patid + " の情報を削除しました。");
        } else {
            System.out.println("PatientDeleteServlet: Failed to delete patient with patid: " + patid + ". Patient might not exist or DB error.");
            session.setAttribute("errorMessage", "患者ID: " + patid + " の情報の削除に失敗しました。該当の患者が存在しないか、データベースエラーの可能性があります。");
        }

        // 処理後、患者一覧ページにリダイレクトして結果を反映
        // PRG (Post-Redirect-Get) パターン
        response.sendRedirect(request.getContextPath() + "/PatientListServlet");
    }

    // doGetは通常このサーブレットでは使用しないが、誤ってアクセスされた場合のために実装
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("PatientDeleteServlet: doGet called. Redirecting to PatientListServlet as this servlet should be accessed via POST.");
        // GETでアクセスされた場合は、何もせず一覧に戻すか、エラーメッセージを表示する
        response.sendRedirect(request.getContextPath() + "/PatientListServlet");
    }
}