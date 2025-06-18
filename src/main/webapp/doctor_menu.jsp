<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Employee" %> <%-- Employeeクラスをインポート --%>
<%@ page import="jakarta.servlet.http.HttpSession" %> <%-- HttpSessionをインポート --%>
<%
    // セッションからログイン中のEmployeeオブジェクトを取得
    HttpSession currentSession = request.getSession(false); // 既存のセッションを取得、なければnull
    Employee loggedInDoctor = null;
    String doctorName = "ゲスト"; // デフォルト名
    String doctorRole = null;

    if (currentSession != null) {
        loggedInDoctor = (Employee) currentSession.getAttribute("employee");
        doctorRole = (String) currentSession.getAttribute("role"); // ロールも取得（医師であることの確認用）
    }

    // ログインしていない、またはEmployeeオブジェクトがない、またはロールが医師でない場合はログインページへ
    // EmployeeモデルのgetRoleName()が "doctor" を返すか、emproleが医師を示す値(例:1)であることを確認
    if (loggedInDoctor == null || !"doctor".equals(doctorRole)) {
        // 医師ロールの判定をより厳密にする場合:
        // if (loggedInDoctor == null || loggedInDoctor.getEmprole() != 1) { // 例: emprole == 1 が医師の場合
        
        // デバッグ用出力
        System.out.println("doctor_menu.jsp: Session check failed. loggedInDoctor is " +
            (loggedInDoctor == null ? "null" : "not null (" + loggedInDoctor.getEmpid() + ")") +
            ", doctorRole is " + doctorRole);

        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    } else {
        // Employeeオブジェクトから氏名を取得
        doctorName = loggedInDoctor.getEmpname(); // getEmpname() は "姓 名" を返す想定
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>医師メニュー</title>
    <style>
        body {
            font-family: sans-serif;
            margin: 20px;
            background-color: #f4f7f6; /* 少し背景色 */
        }
        .container {
            width: 350px; /* コンテナ幅 */
            margin: 50px auto;
            padding: 30px;
            border-radius: 8px;
            background-color: #fff;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 15px;
            font-size: 24px;
        }
        .welcome-message {
            text-align: center;
            margin-bottom: 30px;
            color: #555;
            font-size: 16px;
        }
        .menu-btn {
            display: block; /* 各ボタンをブロック要素にして縦に並べる */
            width: 100%;    /* 幅をコンテナに合わせる */
            box-sizing: border-box; /* paddingとborderをwidthに含める */
            text-decoration: none;
            color: white;
            background-color: #007bff; /* 元の青色 */
            padding: 10px 20px; /* パディング */
            margin-bottom: 10px; /* ボタン間の余白 */
            border-radius: 4px; /* 角丸 */
            font-size: 16px;    /* フォントサイズ */
            text-align: center; /* テキスト中央揃え */
            transition: background-color 0.3s ease;
        }
        .menu-btn:hover {
            background-color: #0056b3; /* ホバー時の色 */
        }
        .menu-btn:last-child {
            margin-bottom: 0; /* 最後のボタンの下マージンを削除 */
        }
        .message { margin: 15px auto; padding:10px; border-radius: 5px; width: 90%; box-sizing: border-box; text-align: center;}
        .success { border:1px solid green; color: green; background-color: #e6ffe6; }
        .error { border:1px solid red; color: red; background-color: #ffe6e6; }
    </style>
</head>
<body>
    <div class="container">
        <h2>医師メニュー</h2>

        <%-- ようこそメッセージ --%>
        <p class="welcome-message">ようこそ、<%= doctorName %> さん</p>

        <%-- メッセージ表示 --%>
        <%
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
        %>
            <div class="message success"><%= successMessage %></div>
        <%
                session.removeAttribute("successMessage"); // 表示後はセッションから削除
            }
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
            <div class="message error"><%= errorMessage %></div>
        <%
                session.removeAttribute("errorMessage"); // 表示後はセッションから削除
            }
        %>

        <%-- メニューボタン --%>
        <a href="<%= request.getContextPath() %>/DoctorPatientSearchServlet" class="menu-btn">患者検索（全件）</a>
        <a href="<%= request.getContextPath() %>/MedicinePrescriptionServlet" class="menu-btn">薬投与指示（追加）</a>
        <a href="<%= request.getContextPath() %>/MedicineDeleteServlet" class="menu-btn">薬投与削除（削除）</a>
        <a href="<%= request.getContextPath() %>/TreatmentHistoryServlet" class="menu-btn">処置履歴一覧表示</a>
        <a href="<%= request.getContextPath() %>/logout" class="menu-btn">ログアウト</a>

    </div>
</body>
</html>