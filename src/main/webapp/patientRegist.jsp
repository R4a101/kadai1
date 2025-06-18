<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Employee" %>
<%
    Employee emp = (session != null) ? (Employee)session.getAttribute("employee") : null;
    if (emp == null || emp.getEmprole() != 0) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>患者登録</title>
    <style>
        body { font-family: sans-serif; }
        label { display: block; margin-bottom: 0.7em; }
        input[type="text"], input[type="date"] { width: 200px; }
        .error { color: red; margin: 1em 0; }
        .success { color: green; margin: 1em 0; }
    </style>
</head>
<body>
<h2>患者登録</h2>
<form action="PatientRegistServlet" method="post">
    <label>患者ID: <input type="text" name="patid" required></label>
    <label>姓: <input type="text" name="patlname" required></label>
    <label>名: <input type="text" name="patfname" required></label>
    <label>保険証名: <input type="text" name="hokenmei" required></label>
    <label>有効期限: <input type="date" name="hokenexp" required></label>
    <input type="submit" value="登録">
</form>
<% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (session.getAttribute("success") != null) { %>
    <div class="success"><%= session.getAttribute("success") %></div>
    <% session.removeAttribute("success"); %>
<% } %>
<a href="reception_menu.jsp">受付メニューへ戻る</a>
</body>
</html>
