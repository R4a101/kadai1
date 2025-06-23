package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import DAO.TreatmentDAO;
import model.TreatmentRecord; // 作成したTreatmentRecordモデル

@WebServlet("/TreatmentHistoryServlet")
public class TreatmentHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("TreatmentHistoryServlet: doGet method called."); // ★デバッグログ

        TreatmentDAO dao = new TreatmentDAO();
        List<TreatmentRecord> historyList = null; // 初期化
        String daoErrorMessage = null; // DAOからのエラーメッセージ用

        try {
            historyList = dao.getAllTreatmentHistory();
            System.out.println("TreatmentHistoryServlet: historyList size from DAO: " + (historyList != null ? historyList.size() : "null")); // ★デバッグログ

            if (historyList != null && !historyList.isEmpty()) {
                // 最初の数件のデータをログに出力して中身を確認（データが多い場合は注意）
                int logCount = 0;
                for (TreatmentRecord record : historyList) {
                    if (logCount < 3) { // 最初の3件までログ表示
                        System.out.println("  Record - TreatmentID: " + record.getTreatmentId() +
                                ", PatID: " + record.getPatId() +
                                ", EmpID: " + record.getEmpId() +
                                ", Date: " + record.getTreatmentDate() +
                                ", Medicines count: " + (record.getMedicines() != null ? record.getMedicines().size() : "0"));
                        if (record.getMedicines() != null && !record.getMedicines().isEmpty()) {
                            for (java.util.Map<String, String> med : record.getMedicines()) {
                                System.out.println("    Medicine - ID: " + med.get("medicineid") + ", Dosage: " + med.get("dosage"));
                            }
                        }
                        logCount++;
                    } else {
                        break;
                    }
                }
            } else if (historyList == null) {
                System.out.println("TreatmentHistoryServlet: historyList is null after DAO call.");
            } else {
                System.out.println("TreatmentHistoryServlet: historyList is empty after DAO call.");
            }

        } catch (Exception e) {
            // DAO呼び出し中に予期せぬ例外が発生した場合
            daoErrorMessage = "処置履歴の取得中にエラーが発生しました: " + e.getMessage();
            System.err.println("TreatmentHistoryServlet: Exception during DAO call - " + e.getMessage());
            e.printStackTrace(); // スタックトレースをコンソールに出力
        }

        request.setAttribute("treatmentHistoryList", historyList);
        if (daoErrorMessage != null) {
            request.setAttribute("daoErrorMessage", daoErrorMessage); // ★JSPにエラーメッセージを渡す
        }

        // JSPをWEB-INF配下に置く場合: /WEB-INF/jsp/treatmentHistory.jsp
        // Webルート直下の場合: treatmentHistory.jsp
        request.getRequestDispatcher("treatmentHistory.jsp").forward(request, response);
        System.out.println("TreatmentHistoryServlet: Forwarded to treatmentHistory.jsp"); // ★デバッグログ
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("TreatmentHistoryServlet: doPost method called, delegating to doGet."); // ★デバッグログ
        doGet(request, response);
    }
}