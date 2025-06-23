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
import jakarta.servlet.http.HttpSession; // HttpSessionを追加

import DAO.TreatmentDAO; // DAOを使用 (TreatmentDAO.javaの実装は別途必要)
import model.Employee;   // model.Employeeクラスのimport (Employee.javaの実装は別途必要)

@WebServlet("/TreatmentServlet") // このサーブレットは現在のメインフローでは未使用の可能性があります
public class TreatmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(); // セッション取得

        String patid = request.getParameter("patid");

        // 従業員IDの取得方法: セッションのEmployeeオブジェクトから取得する想定
        Employee emp = (Employee) session.getAttribute("employee");
        String empid = null;
        if (emp != null) {
            empid = emp.getEmpid(); // EmployeeクラスにgetEmpid()メソッドが必要
        }

        if (empid == null) {
            // 従業員情報がセッションにない場合はログインページなどにリダイレクト
            session.setAttribute("errorMessage", "ログインしていません。再度ログインしてください。");
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // login.jspは仮のパス
            return;
        }

        List<Map<String, String>> medicines = new ArrayList<>();
        String[] medicineids = request.getParameterValues("medicineid");
        String[] dosages = request.getParameterValues("dosage");
        String[] instructions = request.getParameterValues("instructions");

        if (medicineids != null && dosages != null && instructions != null) {
            for (int i = 0; i < medicineids.length; i++) {
                // 入力チェックを強化することが望ましい
                if (medicineids[i] != null && !medicineids[i].isEmpty() &&
                        dosages[i] != null && !dosages[i].isEmpty() &&
                        instructions[i] != null && !instructions[i].isEmpty()) {
                    Map<String, String> med = new HashMap<>();
                    med.put("medicineid", medicineids[i]);
                    med.put("dosage", dosages[i]);
                    med.put("instructions", instructions[i]);
                    medicines.add(med);
                }
            }
        }

        if (medicines.isEmpty()) {
            session.setAttribute("errorMessage", "有効な薬剤情報が入力されていません。");
            // 元の入力画面に戻す (medicinePrescription.jspを想定)
            // このサーブレットがどこから呼ばれるかによって適切なパスに変更が必要
            request.setAttribute("patid", patid); // 患者IDを保持
            // 必要であれば他の入力値も保持
            request.getRequestDispatcher("medicinePrescription.jsp").forward(request, response);
            return;
        }

        TreatmentDAO dao = new TreatmentDAO();
        boolean result = dao.insertTreatments(medicines, patid, empid);

        if (result) {
            session.setAttribute("successMessage", "投与指示を登録しました。");
            response.sendRedirect(request.getContextPath() + "/doctor_menu.jsp"); // doctor_menu.jspは医師のメニュー画面を想定
        } else {
            session.setAttribute("errorMessage", "投与指示の登録に失敗しました。");
            request.setAttribute("patid", patid); // 患者IDを保持
            // 薬剤情報もリクエストスコープに再度セットして入力画面に戻すなど検討
            // request.setAttribute("medicinesList", medicines); // これはMapのリストなのでJSP側での扱いに注意
            request.getRequestDispatcher("medicinePrescription.jsp").forward(request, response);
        }
    }
}