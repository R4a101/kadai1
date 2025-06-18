<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Employee" %>
<%
    // セッションチェック (サーブレットのdoGetでも行っているが、JSP直アクセス対策)
    Employee emp = (session != null) ? (Employee)session.getAttribute("employee") : null;
    if (emp == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    // このページはどの従業員ロールでもアクセス可能と仮定
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>パスワード変更</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: calc(100vh - 40px);
        }
        .container {
            background-color: #fff;
            padding: 30px 40px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            width: 400px;
            text-align: center;
        }
        h2 {
            color: #333;
            margin-bottom: 25px;
            font-size: 24px;
        }
        form {
            display: flex;
            flex-direction: column;
        }
        label {
            text-align: left;
            margin-bottom: 8px;
            font-weight: bold;
            color: #555;
            font-size: 15px;
        }
        input[type="password"] {
            width: calc(100% - 22px); /* paddingとborderを考慮 */
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }
        input[type="submit"] {
            padding: 12px 20px;
            background-color: #28a745; /* 緑色 */
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            transition: background-color 0.2s;
        }
        input[type="submit"]:hover {
            background-color: #218838;
        }
        .message-area {
            margin-top: 20px;
            padding: 10px;
            border-radius: 5px;
            font-weight: 500;
        }
        .error {
            color: #D8000C;
            background-color: #FFD2D2;
            border: 1px solid #D8000C;
        }
        .success {
            color: #008000; /* 緑 */
            background-color: #DFF2BF; /* 薄緑 */
            border: 1px solid #4F8A10;
        }
        .back-link {
            display: inline-block;
            margin-top: 25px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 15px;
            transition: background-color 0.2s;
        }
        .back-link:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>パスワード変更</h2>
        <form action="<%= request.getContextPath() %>/EmployeePasswordChangeServlet" method="post">
            <label for="password_1">新しいパスワード:</label>
            <input type="password" id="password_1" name="password1" required>

            <label for="password_2">新しいパスワード（確認）:</label>
            <input type="password" id="password_2" name="password2" required>

            <input type="submit" value="変更する">
        </form>

        <div class="message-area">
            <% if (request.getAttribute("error") != null) { %>
                <div class="error"><%= request.getAttribute("error") %></div>
            <% } %>
            <% if (request.getAttribute("message") != null) { %>
                <div class="success"><%= request.getAttribute("message") %></div>
            <% } %>
        </div>

        <%-- 戻り先はログインしたユーザーのロールによって動的に変更するのが望ましい --%>
        <%-- ここでは、受付メニューに戻るリンクを例として残す --%>
        <%
            String backLink = "reception_menu.jsp"; // デフォルト
            if (emp != null) {
                if (emp.getEmprole() == DAO.LoginDAO.ADMIN_ROLE_VALUE) { // LoginDAOからADMIN_ROLE_VALUEを参照
                    backLink = "admin_menu.jsp";
                } else if (emp.getEmprole() == 1) { // 医師
                    backLink = "doctor_menu.jsp";
                }
                // emprole == 0 (受付) はデフォルトのまま
            }
        %>
        <a href="<%= request.getContextPath() %>/<%= backLink %>" class="back-link">メニューへ戻る</a>
    </div>
</body>
</html>