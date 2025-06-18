<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html>
<head><title>患者検索</title></head>
<body>
<h2>患者検索</h2>
<form action="PatientSearchServlet" method="get">
    <label>患者名: <input type="text" name="keyword"></label>
    <input type="submit" value="検索">
</form>
<% if (request.getAttribute("errorMessage") != null) { %>
    <div style="color:red"><%= request.getAttribute("errorMessage") %></div>
<% } %>
<%
    List<Map<String, Object>> patients = (List<Map<String, Object>>)request.getAttribute("patients");
    if (patients != null) {
%>
<table border="1">
    <tr>
        <th>患者ID</th><th>姓</th><th>名</th><th>保険証名</th><th>有効期限</th>
    </tr>
    <% for (Map<String, Object> pat : patients) { %>
    <tr>
        <td><%= pat.get("patid") %></td>
        <td><%= pat.get("patlname") %></td>
        <td><%= pat.get("patfname") %></td>
        <td><%= pat.get("hokenmei") %></td>
        <td><%= pat.get("hokenexp") %></td>
    </tr>
    <% } %>
</table>
<% } %>
<a href="reception_menu.jsp">受付メニューへ戻る</a>
</body>
</html>

