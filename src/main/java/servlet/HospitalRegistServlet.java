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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String hospid = request.getParameter("hospid");
        String hospname = request.getParameter("hospname");
        String address = request.getParameter("address");
        String tel = request.getParameter("tel");
        String email = request.getParameter("email");
        String capitalStr = request.getParameter("capital");

        // ★バリデーションを強化
        StringBuilder errorMsg = new StringBuilder();
        if (hospid == null || hospid.trim().isEmpty()) errorMsg.append("病院IDは必須です。<br>");
        if (hospname == null || hospname.trim().isEmpty()) errorMsg.append("病院名は必須です。<br>");
        if (address == null || address.trim().isEmpty()) errorMsg.append("住所は必須です。<br>");
        if (tel == null || tel.trim().isEmpty()) {
            errorMsg.append("電話番号は必須です。<br>");
        } else if (!TEL_PATTERN.matcher(tel).matches()) { // ★電話番号のフォーマットチェック
            errorMsg.append("電話番号は数字とハイフン(-)のみで入力してください。<br>");
        }

        int capital = 0;
        if (capitalStr != null && !capitalStr.trim().isEmpty()) {
            try {
                capital = Integer.parseInt(capitalStr.trim());
                if (capital < 0) {
                    errorMsg.append("資本金は0以上の数値を入力してください。<br>");
                }
            } catch (NumberFormatException e) {
                errorMsg.append("資本金は数値で入力してください。<br>");
            }
        }

        // エラーがあった場合、入力値を保持してJSPに戻す
        if (errorMsg.length() > 0) {
            request.setAttribute("error", errorMsg.toString());
            request.setAttribute("hospid", hospid);
            request.setAttribute("hospname", hospname);
            request.setAttribute("address", address);
            request.setAttribute("tel", tel);
            request.setAttribute("email", email);
            request.setAttribute("capital", capitalStr);
            request.getRequestDispatcher("/hospitalRegist.jsp").forward(request, response);
            return;
        }


        try {
            HospitalDAO dao = new HospitalDAO();
            boolean result = dao.insertHospital(hospid, hospname, address, tel, email, capital);

            if (result) {
                session.setAttribute("successMessage", "病院登録が完了しました。");
                response.sendRedirect(request.getContextPath() + "/admin_menu.jsp");
            } else {
                request.setAttribute("error", "病院登録に失敗しました。ID重複やDBエラーの可能性があります。");
                request.getRequestDispatcher("/hospitalRegist.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "システムエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/hospitalRegist.jsp").forward(request, response);
        }
    }
}