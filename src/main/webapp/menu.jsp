<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    
    String username = (session != null) ? (String)session.getAttribute("username") : null;
    if (username == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>メニュー</title>
</head>
<body>
    <h2>ようこそ、<%= username %>さん</h2>
    <a href="login.jsp">ログアウト</a>
</body>
</html>
