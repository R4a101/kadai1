package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/MedicinePrescriptionServlet")
public class MedicinePrescriptionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // エラーメッセージがあればリクエストスコープに設定
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        // 修正フローまたはエラーからの再表示の場合、セッションからデータを取得してリクエストスコープに設定
        String patid = (String) session.getAttribute("currentPatId");
        String empid = (String) session.getAttribute("currentEmpId");
        List<Map<String, String>> medicinesList = (List<Map<String, String>>) session.getAttribute("currentMedicinesList");

        if (patid == null && session.getAttribute("tempPatId") != null) { // 修正フローの場合
            patid = (String) session.getAttribute("tempPatId");
            empid = (String) session.getAttribute("tempEmpId");
            medicinesList = (List<Map<String, String>>) session.getAttribute("tempMedicinesList");
            
            // 修正の場合は一時データをクリアしない（確認画面で再度使うため）
            // session.removeAttribute("tempPatId");
            // session.removeAttribute("tempEmpId");
            // session.removeAttribute("tempMedicinesList");
        } else { // エラーからの再表示の場合、一時データをクリア
            session.removeAttribute("currentPatId");
            session.removeAttribute("currentEmpId");
            session.removeAttribute("currentMedicinesList");
        }


        if (patid != null) request.setAttribute("patid", patid);
        if (empid != null) request.setAttribute("empid", empid);
        if (medicinesList != null) request.setAttribute("medicinesList", medicinesList);
        
        request.getRequestDispatcher("medicinePrescription.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String patid = request.getParameter("patid");
        String empid = request.getParameter("empid");

        String[] medicineIds = request.getParameterValues("medicineid");
        String[] dosages = request.getParameterValues("dosage");
        String[] instructions = request.getParameterValues("instructions");

        // バリデーション
        if (patid == null || patid.trim().isEmpty() ||
            empid == null || empid.trim().isEmpty()) {
            session.setAttribute("errorMessage", "患者IDと従業員IDは必須です。");
            // 入力値をセッションに保存してリダイレクト
            saveCurrentInputsToSession(session, patid, empid, medicineIds, dosages, instructions);
            response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
            return;
        }

        if (medicineIds == null || medicineIds.length == 0) {
            session.setAttribute("errorMessage", "少なくとも1つの薬剤を指定してください。");
            saveCurrentInputsToSession(session, patid, empid, medicineIds, dosages, instructions);
            response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
            return;
        }

        List<Map<String, String>> medicinesList = new ArrayList<>();
        for (int i = 0; i < medicineIds.length; i++) {
            // 各薬剤の項目が空でないかチェック (より厳密に)
            if (medicineIds[i] != null && !medicineIds[i].isEmpty() &&
                dosages[i] != null && !dosages[i].trim().isEmpty() &&
                instructions[i] != null && !instructions[i].trim().isEmpty()) {

                Map<String, String> medicine = new HashMap<>();
                medicine.put("medicineid", medicineIds[i]);
                medicine.put("dosage", dosages[i]);
                medicine.put("instructions", instructions[i]);
                medicinesList.add(medicine);
            } else if (medicineIds.length == 1 && (medicineIds[i] == null || medicineIds[i].isEmpty())) {
                // 1行しかなく、薬剤IDが未選択の場合はエラー
                 session.setAttribute("errorMessage", "薬剤を選択してください。");
                 saveCurrentInputsToSession(session, patid, empid, medicineIds, dosages, instructions);
                 response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
                 return;
            } else if (medicineIds[i] != null && !medicineIds[i].isEmpty() && (dosages[i] == null || dosages[i].trim().isEmpty() || instructions[i] == null || instructions[i].trim().isEmpty())){
                 // 薬剤は選択されているが、投与量や指示が空の場合
                 session.setAttribute("errorMessage", "選択された薬剤の投与量と指示を入力してください。");
                 saveCurrentInputsToSession(session, patid, empid, medicineIds, dosages, instructions);
                 response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
                 return;
            }
        }

        if (medicinesList.isEmpty() && medicineIds != null && medicineIds.length > 0) {
             session.setAttribute("errorMessage", "有効な薬剤情報が入力されていません。各薬剤の情報をすべて入力してください。");
             saveCurrentInputsToSession(session, patid, empid, medicineIds, dosages, instructions);
             response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
             return;
        }
        if (medicinesList.isEmpty() && (medicineIds == null || medicineIds.length == 0)) { // このケースは上でカバー済みだが念のため
             session.setAttribute("errorMessage", "薬剤情報を入力してください。");
             saveCurrentInputsToSession(session, patid, empid, medicineIds, dosages, instructions);
             response.sendRedirect(request.getContextPath() + "/MedicinePrescriptionServlet");
             return;
        }


        // 確認画面用にセッションに一時保存
        session.setAttribute("tempPatId", patid);
        session.setAttribute("tempEmpId", empid);
        session.setAttribute("tempMedicinesList", medicinesList);
        session.setAttribute("fromPrescription", "true"); // 確認画面への正規の遷移であるフラグ

        // エラー再表示用の一時入力値をクリア
        session.removeAttribute("currentPatId");
        session.removeAttribute("currentEmpId");
        session.removeAttribute("currentMedicinesList");
        session.removeAttribute("errorMessage");


        response.sendRedirect(request.getContextPath() + "/TreatmentConfirmServlet");
    }

    private void saveCurrentInputsToSession(HttpSession session, String patid, String empid,
                                            String[] medicineIds, String[] dosages, String[] instructions) {
        session.setAttribute("currentPatId", patid);
        session.setAttribute("currentEmpId", empid);
        List<Map<String, String>> currentMedicinesList = new ArrayList<>();
        if (medicineIds != null) {
            for (int i = 0; i < medicineIds.length; i++) {
                Map<String, String> med = new HashMap<>();
                med.put("medicineid", medicineIds[i] != null ? medicineIds[i] : "");
                med.put("dosage", (dosages != null && i < dosages.length && dosages[i] != null) ? dosages[i] : "");
                med.put("instructions", (instructions != null && i < instructions.length && instructions[i] != null) ? instructions[i] : "");
                currentMedicinesList.add(med);
            }
        }
        session.setAttribute("currentMedicinesList", currentMedicinesList);
    }
}