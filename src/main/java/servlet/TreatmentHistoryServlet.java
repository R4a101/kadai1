package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSessionを追加

import DAO.TreatmentDAO;
import model.TreatmentRecord; // 作成したTreatmentRecordモデル

@WebServlet("/TreatmentHistoryServlet")
public class TreatmentHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("TreatmentHistoryServlet: doGet method called."); // ★デバッグログ

        // 医師権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"doctor".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }

        TreatmentDAO dao = new TreatmentDAO();
        List<TreatmentRecord> historyList = null; // 初期化
        String daoErrorMessage = null; // DAOからのエラーメッセージ用

        // ... (以降の既存ロジック) ...
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("TreatmentHistoryServlet: doPost method called, delegating to doGet."); // ★デバッグログ
        // doPostでも医師権限チェックが必要であれば、ここにも追加
        // 医師権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"doctor".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }
        doGet(request, response);
    }
}