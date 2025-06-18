<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Employee" %>
<%@ page import="java.util.List" %>
<%
List<Employee> employees = (List<Employee>)request.getAttribute("employees");
%>
<!DOCTYPE html>
<html>
<head><title>従業員一覧</title></head>
<body>
<h2>従業員一覧</h2>
<table border="1">
    <tr>
        <th>ID</th><th>氏名</th><th>ロール</th>
    </tr>
    <% for (Employee emp : employees) { %>
    <tr>
        <td><%= emp.getEmpid() %></td>
        <td><%= emp.getEmpname() %></td>
        <td><%= emp.getEmprole() == 0 ? "受付" : "医師" %></td>
    </tr>
    <% } %>
</table>
<a href="admin_menu.jsp">管理者メニューへ戻る</a>
</body>
</html>
