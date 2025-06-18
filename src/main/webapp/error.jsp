<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>エラー</title>
</head>
<body>
    <h2>エラー</h2>
    <p style="color:red;">
        <%= request.getAttribute("error") %>
    </p>
    <a href="login.jsp">戻る</a>
</body>
</html>
