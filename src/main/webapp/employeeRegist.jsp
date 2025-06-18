<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    String role = (session != null) ? (String)session.getAttribute("role") : null;
    if (role == null || !"admin".equals(role)) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>従業員登録</title>
    <style>
        body { font-family: sans-serif; }
        form { margin: 1.5em 0; }
        label { display: block; margin-bottom: 0.7em; }
        input[type="text"], input[type="password"], select { width: 200px; }
        .error { color: red; margin: 1em 0; }
        .success { color: green; margin: 1em 0; }
    </style>
    <script>
        function validateForm() {
            const pw1 = document.getElementById("pw1").value;
            const pw2 = document.getElementById("pw2").value;
            if (pw1 !== pw2) {
                alert("パスワードが一致しません。");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<h2>従業員登録</h2>
<form action="EmployeeRegistServlet" method="post" onsubmit="return validateForm();">
    <label>従業員ID: <input type="text" name="empid" required></label>
    <label>姓: <input type="text" name="lastName" required></label>
    <label>名: <input type="text" name="firstName" required></label>
    <label>パスワード: <input type="password" name="password" id="pw1" required></label>
    <label>パスワード(確認): <input type="password" name="password2" id="pw2" required></label>
    <label>ロール:
        <select name="emprole">
            <option value="0">受付</option>
            <option value="1">医師</option>
        </select>
    </label>
    <input type="submit" value="登録">
</form>

<% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("message") != null) { %>
    <div class="success"><%= request.getAttribute("message") %></div>
<% } %>

<a href="admin_menu.jsp">管理者メニューへ戻る</a>
</body>
</html>
