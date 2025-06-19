package servlet;

import java.io.IOException;
import java.util.regex.Pattern; // ★正規表現のためにインポート

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.HospitalDAO;

@WebServlet("/HospitalRegistServlet")
public class HospitalRegistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // ★電話番号の正規表現パターン（数字とハイフンのみ許容）
    private static final Pattern TEL_PATTERN = Pattern.compile("^[0-9\\-]+$");

    // doGetが必要な場合はdoGetにも追加
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }
        request.getRequestDispatcher("/hospitalRegist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 管理者権限チェック
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
            return;
        }
        // 既存のsessionチェックをこの共通コードで置き換え、admin以外のロールチェックを削除
        // if (session == null || !"admin".equals(session.getAttribute("role"))) {
        //    response.sendRedirect(request.getContextPath() + "/login.jsp");
        //    return;
        // }

        String hospid = request.getParameter("hospid");
        String hospname = request.getParameter("hospname");
        String address = request.getParameter("address");
        String tel = request.getParameter("tel");
        String email = request.getParameter("email");
        String capitalStr = request.getParameter("capital");

        // ... (以降の既存ロジック) ...
    }
}