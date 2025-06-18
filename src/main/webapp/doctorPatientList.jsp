<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List,java.util.Map" %>
<%
    List<Map<String, Object>> patients = (List<Map<String, Object>>)request.getAttribute("patients");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>患者一覧（医師用）</title>
</head>
<body>
<h2>患者一覧（医師用）</h2>
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
<a href="doctor_menu.jsp">医師メニューへ戻る</a>
</body>
</html>
