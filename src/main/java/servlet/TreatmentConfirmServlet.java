package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.TreatmentDAO;

@WebServlet("/TreatmentConfirmServlet")
public class TreatmentConfirmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String patid = (String) session.getAttribute("tempPatId");
        String empid = (String) session.getAttribute("tempEmpId");
        List<Map<String, String>> medicinesList = (List<Map<String, String>>) session.getAttribute("tempMedicinesList");
        String fromPrescription = (String) session.getAttribute("fromPrescription");

        if (patid == null || empid == null || medicinesList == null || medicinesList.isEmpty() || fromPrescription == null) {
            session.setAttribute("errorMessage", "処方情報が不十分か、不正なアクセスです。再度入力してください。");
            session.removeAttribute("fromPrescription");
            response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
            return;
        }

        request.setAttribute("patid", patid);
        request.setAttribute("empid", empid);
        request.setAttribute("medicinesList", medicinesList);

        // JSPをWEB-INF配下に置く場合: /WEB-INF/jsp/treatmentConfirm.jsp
        // Webルート直下の場合: treatmentConfirm.jsp
        request.getRequestDispatcher("treatmentConfirm.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        request.setCharacterEncoding("UTF-8");

        String patid = (String) session.getAttribute("tempPatId");
        String empid = (String) session.getAttribute("tempEmpId");
        List<Map<String, String>> medicinesList = (List<Map<String, String>>) session.getAttribute("tempMedicinesList");

        if (patid == null || empid == null || medicinesList == null || medicinesList.isEmpty()) {
            session.setAttribute("errorMessage", "登録処理中にエラーが発生しました。処方情報が見つかりません。再度入力してください。");
            response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
            return;
        }

        TreatmentDAO dao = new TreatmentDAO();
        boolean success = dao.insertTreatments(medicinesList, patid, empid);

        // 登録処理に使用した一時セッション情報をクリア
        session.removeAttribute("tempPatId");
        session.removeAttribute("tempEmpId");
        session.removeAttribute("tempMedicinesList");
        session.removeAttribute("fromPrescription");
        session.removeAttribute("currentPatId"); // 念のためクリア
        session.removeAttribute("currentEmpId"); // 念のためクリア
        session.removeAttribute("currentMedicinesList"); // 念のためクリア


        if (success) {
            // 登録成功後、結果表示画面用に情報をセッションにセット
            session.setAttribute("successMessage", "処置情報を確定し、登録しました。");
            session.setAttribute("confirmedPatId", patid);
            session.setAttribute("confirmedEmpId", empid);
            session.setAttribute("confirmedMedicinesList", medicinesList); // DB登録に使ったリストをそのまま渡す

            response.sendRedirect(request.getContextPath() + "/treatmentResult.jsp"); // ★変更点: 結果表示画面へ
        } else {
            session.setAttribute("errorMessage", "処置情報の登録に失敗しました。データベースエラーまたは入力内容に問題がある可能性があります。");
            response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
        }
    }
}