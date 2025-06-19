package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Medicine;
import model.Patient;
import model.PrescriptionItem;

@WebServlet("/PrescriptionManagementServlet")
public class PrescriptionManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // モックデータ - 実際のアプリではデータベースから取得します
    private List<Patient> availablePatients = new ArrayList<>();
    private List<Medicine> availableMedicines = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        super.init();
        // モックデータの初期化
        availablePatients.add(new Patient("P001", "山田", "太郎"));
        availablePatients.add(new Patient("P002", "鈴木", "花子"));
        availablePatients.add(new Patient("P003", "佐藤", "次郎"));

        availableMedicines.add(new Medicine("MED001", "アスピリン 100mg"));
        availableMedicines.add(new Medicine("MED002", "ロキソニン 60mg"));
        availableMedicines.add(new Medicine("MED003", "アモキシシリン 250mg"));
        availableMedicines.add(new Medicine("MED004", "イブプロフェン 200mg"));
    }

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // 患者リストと薬剤リストをJSPで利用可能にする
        request.setAttribute("availablePatients", availablePatients);
        request.setAttribute("availableMedicines", availableMedicines);

        // セッションから現在の処方項目を取得
        List<PrescriptionItem> currentPrescriptionItems = (List<PrescriptionItem>) session.getAttribute("currentPrescriptionItems");
        if (currentPrescriptionItems == null) {
            currentPrescriptionItems = new ArrayList<>();
            session.setAttribute("currentPrescriptionItems", currentPrescriptionItems);
        }
        
        // セッションから選択された患者を取得
        Patient selectedPatient = (Patient) session.getAttribute("selectedPatient");
        request.setAttribute("selectedPatient", selectedPatient);


        request.getRequestDispatcher("/prescriptionManagement.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        List<PrescriptionItem> currentPrescriptionItems = (List<PrescriptionItem>) session.getAttribute("currentPrescriptionItems");
        if (currentPrescriptionItems == null) {
            currentPrescriptionItems = new ArrayList<>(); // defensive coding
        }

        if ("selectPatient".equals(action)) {
            String patientId = request.getParameter("patientId");
            Optional<Patient> patientOpt = availablePatients.stream()
                                             .filter(p -> p.getPatientId().equals(patientId))
                                             .findFirst();
            if (patientOpt.isPresent()) {
                session.setAttribute("selectedPatient", patientOpt.get());
                // 新しい患者が選択されたら、以前の項目をクリアするか、この患者の既存の項目をロードします
                // この例ではクリアします。実際のアプリでは、保存された処方箋をロードするかもしれません。
                currentPrescriptionItems.clear(); 
            } else {
                session.removeAttribute("selectedPatient");
                currentPrescriptionItems.clear();
            }
        } else if ("addMedicine".equals(action)) {
            Patient selectedPatient = (Patient) session.getAttribute("selectedPatient");
            if (selectedPatient == null) {
                request.setAttribute("errorMessage", "患者を選択してください。");
            } else {
                String medicineId = request.getParameter("medicineId");
                String dosage = request.getParameter("dosage");
                String instructions = request.getParameter("instructions");

                if (medicineId != null && !medicineId.isEmpty() && dosage != null && !dosage.isEmpty() && instructions != null && !instructions.isEmpty()) {
                    Optional<Medicine> medOpt = availableMedicines.stream()
                                                 .filter(m -> m.getMedicineId().equals(medicineId))
                                                 .findFirst();
                    if (medOpt.isPresent()) {
                        PrescriptionItem newItem = new PrescriptionItem(medicineId, medOpt.get().getMedicineName(), dosage, instructions);
                        currentPrescriptionItems.add(newItem);
                    } else {
                        request.setAttribute("errorMessage", "選択された薬剤が見つかりません。");
                    }
                } else {
                    request.setAttribute("errorMessage", "薬剤、投与量、指示はすべて必須です。");
                }
            }
        } else if ("deleteMedicine".equals(action)) {
            String tempIdToDelete = request.getParameter("tempId");
            currentPrescriptionItems = currentPrescriptionItems.stream()
                                       .filter(item -> !item.getTempId().equals(tempIdToDelete))
                                       .collect(Collectors.toList());
        } else if ("confirmPrescription".equals(action)) {
            Patient selectedPatient = (Patient) session.getAttribute("selectedPatient");
            if (selectedPatient != null && !currentPrescriptionItems.isEmpty()) {
                // ここで通常、処方箋をデータベースに保存します
                // 例: prescriptionDAO.savePrescription(selectedPatient, currentPrescriptionItems);
                System.out.println("患者の処方箋が確定されました: " + selectedPatient.getFullName());
                for (PrescriptionItem item : currentPrescriptionItems) {
                    System.out.println(" - " + item);
                }
                session.removeAttribute("selectedPatient");
                currentPrescriptionItems.clear();
                session.setAttribute("successMessage", "処方箋が確定されました。"); // 成功メッセージを設定
                response.sendRedirect("doctor_menu.jsp"); // または確認ページへ
                return; // sendRedirectの後はreturnが重要
            } else if (selectedPatient == null) {
                 request.setAttribute("errorMessage", "患者が選択されていません。");
            } else {
                 request.setAttribute("errorMessage", "処方内容がありません。");
            }
        }

        session.setAttribute("currentPrescriptionItems", currentPrescriptionItems);
        // POSTアクションの後、フォーム再送信の問題を防ぐためにGETにリダイレクトします
        // または、エラーメッセージなどのリクエスト属性を引き継ぐ必要がある場合はフォワードします
        // 同じページにエラー/メッセージを表示する簡単さのため、フォワードします
        doGet(request, response); 
        // POSTに対するより良いパターンはPost-Redirect-Get (PRG)です
        // response.sendRedirect(request.getContextPath() + "/PrescriptionManagementServlet");
    }
}