<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Employee" %>
<%
    Employee emp = (Employee)session.getAttribute("employee");
    if (emp == null || emp.getEmprole() != 0) {
        response.sendRedirect("login.jsp");
        return;
    }
    String[] names = emp.getEmpname().split(" ", 2);
    String lastName = names.length > 0 ? names[0] : "";
    String firstName = names.length > 1 ? names[1] : "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>従業員情報更新</title>
    <style>
        .form-container {
            max-width: 400px;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 8px;
            background-color: #f9f9f9;
        }
        label { display: block; margin-bottom: 10px; }
        input[type="text"] { width: 100%; padding: 8px; margin-bottom: 15px; }
        .error { color: red; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>従業員情報更新</h2>
        <form action="EmployeeUpdateServlet" method="post">
            <input type="hidden" name="empid" value="<%= emp.getEmpid() %>">
            
            <label>姓: 
                <input type="text" name="lastName" value="<%= lastName %>" required>
            </label>
            
            <label>名: 
                <input type="text" name="firstName" value="<%= firstName %>" required>
            </label>

            <% if (request.getAttribute("error") != null) { %>
                <div class="error"><%= request.getAttribute("error") %></div>
            <% } %>

            <input type="submit" value="更新" style="background:#4CAF50;color:white;padding:10px 20px;border:none;cursor:pointer;">
        </form>
        <a href="reception_menu.jsp" style="display:block;margin-top:15px;">← 受付メニューに戻る</a>
    </div>
</body>
</html>
