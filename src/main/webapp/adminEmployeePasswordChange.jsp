<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    // 管理者権限チェック
    HttpSession currentSession = request.getSession(false);
    String role = (currentSession != null) ? (String)currentSession.getAttribute("role") : null;
    if (role == null || !"admin".equals(role)) {
        response.sendRedirect("login.jsp");
        return;
    }

    String empidValue = "";
    if (request.getAttribute("empidForForm") != null) { // POSTエラーで戻ってきた場合
        empidValue = (String)request.getAttribute("empidForForm");
    } else if (request.getAttribute("empid") != null) { // GETで初期表示された場合 (ServletのdoGetでセット)
        empidValue = (String)request.getAttribute("empid");
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>従業員パスワード変更 (管理者用)</title>
    <style>
        body { font-family: sans-serif; }
        .container { max-width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #f9f9f9; }
        h2 { text-align: center; margin-bottom: 1.5em;}
        label { display: block; margin-bottom: 0.7em; font-weight: bold; }
        input[type="text"], input[type="password"] { 
            width: calc(100% - 16px); /* padding分を考慮 */
            padding: 8px; 
            margin-bottom: 1em; 
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box; /* paddingとborderをwidthに含める */
        }
        input[type="submit"] { 
            background:#4CAF50; 
            color:white; 
            padding:10px 20px; 
            border:none; 
            cursor:pointer; 
            border-radius: 4px; 
            font-size: 1em;
            display: block;
            width: 100%;
            margin-top: 1em;
        }
        input[type="submit"]:hover {
            background: #45a049;
        }
        .error { color: red; margin: 1em 0; padding: 10px; background-color: #ffebee; border: 1px solid #ef9a9a; border-radius: 4px; }
        .success { color: green; margin: 1em 0; padding: 10px; background-color: #e8f5e9; border: 1px solid #a5d6a7; border-radius: 4px; }
        .back-link { display:block; margin-top:20px; text-align: center; color: #007bff; text-decoration: none; }
        .back-link:hover { text-decoration: underline; }
    </style>
    <script>
        function validateAdminPasswordChangeForm() {
            const pw1 = document.getElementById("newPassword").value;
            const pw2 = document.getElementById("confirmPassword").value;
            const empid = document.getElementById("empid").value;

            if (empid.trim() === "") {
                alert("従業員IDは必須です。");
                document.getElementById("empid").focus();
                return false;
            }
            if (pw1.trim() === "") {
                alert("新しいパスワードは必須です。");
                document.getElementById("newPassword").focus();
                return false;
            }
            if (pw2.trim() === "") {
                alert("新しいパスワード（確認）は必須です。");
                document.getElementById("confirmPassword").focus();
                return false;
            }
            if (pw1 !== pw2) {
                alert("パスワードが一致しません。");
                document.getElementById("newPassword").focus();
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>従業員パスワード変更 (管理者用)</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("message") != null) { %>
            <div class="success"><%= request.getAttribute("message") %></div>
        <% } %>

        <form action="AdminEmployeePasswordChangeServlet" method="post" onsubmit="return validateAdminPasswordChangeForm();">
            <div>
                <label for="empid">対象従業員ID:</label>
                <input type="text" id="empid" name="empid" value="<%= empidValue %>" required>
            </div>
            <div>
                <label for="newPassword">新しいパスワード:</label>
                <input type="password" id="newPassword" name="newPassword" required>
            </div>
            <div>
                <label for="confirmPassword">新しいパスワード（確認）:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            
            <input type="submit" value="パスワード変更">
        </form>
        <a href="admin_menu.jsp" class="back-link">← 管理者メニューへ戻る</a>
    </div>
</body>
</html>