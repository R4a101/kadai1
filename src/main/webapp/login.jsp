<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>ログイン</title></head>
<body>
<h2>ログイン</h2>
<form action="LoginServlet" method="post">
    <label>ログイン種別:
        <select name="role">
            <option value="admin">管理者</option>
            <option value="employee">従業員</option>
        </select>
    </label><br>
    <label>ユーザID: <input type="text" name="userid" required></label><br>
    <label>パスワード: <input type="password" name="password" required></label><br>
    <input type="submit" value="ログイン">
</form>
<% if (request.getAttribute("error") != null) { %>
    <div style="color:red"><%= request.getAttribute("error") %></div>
<% } %>
</body>
</html>
