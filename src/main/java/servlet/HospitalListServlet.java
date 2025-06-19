package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSessionをインポート

import DAO.HospitalDAO;

@WebServlet("/HospitalListServlet")
public class HospitalListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 念のためGETでも設定

        // 管理者権限チェック
        // HospitalListServletは管理者のみアクセス可能と仮定
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (role == null || !"admin".equals(role)) { // "admin" 以外のロール、またはログインしていない場合はリダイレクト
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        HospitalDAO dao = new HospitalDAO();
        List<Map<String, String>> hospitals;

        String minCapitalStr = request.getParameter("minCapital");

        if (minCapitalStr != null && !minCapitalStr.isEmpty()) {
            try {
                int minCapital = Integer.parseInt(minCapitalStr);
                if (minCapital < 0) {
                    // 負の値を入力された場合はエラーメッセージを設定し、全件表示などにする
                    request.setAttribute("searchError", "検索する資本金は0以上の数値を入力してください。");
                    hospitals = dao.findAllHospitals(); // または空のリスト
                } else {
                    hospitals = dao.findHospitalsByMinCapital(minCapital);
                    request.setAttribute("searchedMinCapital", minCapital); // 検索値をJSPに戻す
                }
            } catch (NumberFormatException e) {
                // 数値変換エラーの場合はエラーメッセージを設定し、全件表示などにする
                request.setAttribute("searchError", "資本金は有効な数値で入力してください。");
                hospitals = dao.findAllHospitals(); // または空のリスト
            }
        } else {
            hospitals = dao.findAllHospitals();
        }

        request.setAttribute("hospitals", hospitals);
        request.getRequestDispatcher("/hospitalList.jsp").forward(request, response);
    }
    // doPostメソッドももしあれば、その冒頭にも管理者権限チェックを追加してください
}