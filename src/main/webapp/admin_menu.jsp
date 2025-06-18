<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String userid = (session != null) ? (String)session.getAttribute("userid") : null;
    String role = (session != null) ? (String)session.getAttribute("role") : null;

    // useridだけでなく、roleもチェックして管理者であることを確認
    if (userid == null || !"admin".equals(role)) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>管理者ホーム</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f4f4f4;
        }
        h2 {
            color: #333;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
        }
        p {
            font-size: 1.1em;
            color: #555;
        }
        .menu-container {
            margin-top: 20px;
        }
        .menu-section {
            margin-bottom: 20px;
            padding: 15px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .menu-section h3 {
            margin-top: 0;
            color: #3498db;
        }
        .menu-btn {
            display: inline-block;
            margin: 5px 5px 5px 0; /* 上下左右に少しマージン */
            padding: 10px 25px; /* 少し小さめに調整 */
            font-size: 15px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            transition: background 0.2s ease-in-out;
        }
        .menu-btn:hover {
            background: #217dbb;
        }
        .account-menu .menu-btn { /* アカウント関連のボタンは少し目立たせる色にするなど */
            background: #2ecc71; /* エメラルドグリーン */
        }
        .account-menu .menu-btn:hover {
            background: #27ae60;
        }
        .logout-btn {
            background: #e74c3c; /* レッド */
        }
        .logout-btn:hover {
            background: #c0392b;
        }
    </style>
</head>
<body>
    <h2>管理者ホーム</h2>
    <p>ようこそ、<%= userid %> さん</p>

    <div class="menu-container">
        <div class="menu-section">
            <h3>従業員管理</h3>
            <a href="employeeRegist.jsp" class="menu-btn">従業員登録</a>
            <a href="EmployeeListServlet" class="menu-btn">従業員一覧</a>
            <a href="AdminEmployeePasswordChangeServlet" class="menu-btn">他従業員パスワード変更</a>
        </div>

        <div class="menu-section">
            <h3>病院情報管理</h3>
            <a href="hospitalRegist.jsp" class="menu-btn">病院登録</a>
            <a href="HospitalListServlet" class="menu-btn">他病院一覧</a>
        </div>
        
        <div class="menu-section account-menu">
            <h3>アカウント設定</h3>
            <a href="AdminSelfPasswordChangeServlet" class="menu-btn">管理者パスワード変更</a>
            <a href="logout" class="menu-btn logout-btn">ログアウト</a>
        </div>
    </div>
</body>
</html>