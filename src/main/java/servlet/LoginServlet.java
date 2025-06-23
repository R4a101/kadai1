package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// import DAO.EmployeeDAO; // LoginDAOで完結するため、EmployeeDAOの直接利用は不要になる
import DAO.LoginDAO;
import model.Employee;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // HttpServletを継承する場合、念のため追加
    private LoginDAO loginDAO = new LoginDAO();
    // private EmployeeDAO employeeDAO = new EmployeeDAO(); // LoginDAOでEmployeeオブジェクトを取得するため不要に

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String roleParam = request.getParameter("role");
        String userid = request.getParameter("userid");
        String password = request.getParameter("password");

        HttpSession session = request.getSession(); // 新規セッションまたは既存セッションを取得

        if ("admin".equals(roleParam)) {
            // LoginDAOのvalidateAdminAndGetEmployeeメソッドを使用
            Employee admin = loginDAO.validateAdminAndGetEmployee(userid, password);
            if (admin != null) {
                session.setAttribute("role", "admin");      // セッションにロールを"admin"として設定
                session.setAttribute("employee", admin);    // ★ セッションに管理者のEmployeeオブジェクトを設定
                session.setAttribute("userid", admin.getEmpid()); // admin_menu.jspなどで表示用に使っているなら設定
                // (employeeオブジェクトから取得できるので必須ではない)
                System.out.println("LoginServlet: Admin login successful. EmpID: " + admin.getEmpid() + ", Name: " + admin.getEmpname() + ", Role set to 'admin'"); //デバッグ
                response.sendRedirect("admin_menu.jsp");
            } else {
                System.out.println("LoginServlet: Admin login failed for userid: " + userid); //デバッグ
                request.setAttribute("error", "ユーザIDまたはパスワードが違います");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else if ("employee".equals(roleParam)) {
            Employee emp = loginDAO.validateEmployee(userid, password);
            if (emp != null) {
                // Employeeオブジェクトからロール名を取得してセッションに設定
                // EmployeeモデルのgetRoleName()が "reception" や "doctor" を返す想定
                session.setAttribute("role", emp.getRoleName());
                session.setAttribute("employee", emp);      // ★ セッションに従業員のEmployeeオブジェクトを設定
                session.setAttribute("empid", emp.getEmpid()); // userid/empidも設定 (employeeオブジェクトから取得できるので必須ではない)
                System.out.println("LoginServlet: Employee login successful. EmpID: " + emp.getEmpid() + ", Name: " + emp.getEmpname() + ", Role: " + emp.getRoleName()); //デバッグ

                if (emp.getEmprole() == 0) { // 受付
                    response.sendRedirect("reception_menu.jsp");
                } else if (emp.getEmprole() == 1) { // 医師
                    response.sendRedirect("doctor_menu.jsp");
                } else {
                    // LoginDAO.ADMIN_ROLE_VALUE のような他の定義済みロールの場合の分岐も考慮可能
                    // (ただし、管理者は上の "admin".equals(roleParam) で処理される想定)
                    System.out.println("LoginServlet: Employee login successful but role (" + emp.getEmprole() + ") has no specific menu. Redirecting to login."); //デバッグ
                    request.setAttribute("error", "あなたの役割に対応するメニューが見つかりません。");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } else {
                System.out.println("LoginServlet: Employee login failed for userid: " + userid); //デバッグ
                request.setAttribute("error", "ユーザIDまたはパスワードが違います");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            System.out.println("LoginServlet: Invalid role parameter: " + roleParam); //デバッグ
            request.setAttribute("error", "役割が選択されていません。");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    // doGetメソッドも実装しておくと、直接URLでアクセスされた場合のエラー処理などができる
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 例えば、ログインページにリダイレクトする、またはエラーメッセージを表示するなど
        System.out.println("LoginServlet: doGet called. Redirecting to login.jsp."); //デバッグ
        response.sendRedirect("login.jsp");
    }
}