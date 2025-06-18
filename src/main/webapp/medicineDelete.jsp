<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>薬投与削除</title>
    <style>
        .message { margin:15px 0; padding:10px; border-radius: 5px; }
        .success { border:1px solid green; color: green; background-color: #e6ffe6; }
        .error { border:1px solid red; color: red; background-color: #ffe6e6; }
        label { display: block; margin-bottom: 5px; }
        input[type="text"], input[type="submit"] { margin-top: 5px; margin-bottom:10px; padding: 5px;}
    </style>
</head>
<body>
    <h2>薬投与削除</h2>

    <%
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");

        if (successMessage != null) {
    %>
        <div class="message success"><%= successMessage %></div>
    <%
            session.removeAttribute("successMessage");
        }
        if (errorMessage != null) {
    %>
        <div class="message error"><%= errorMessage %></div>
    <%
            session.removeAttribute("errorMessage");
        }
    %>

    <form action="MedicineDeleteServlet" method="post">
        <div>
            <label for="patid_del">患者ID:</label>
            <input type="text" id="patid_del" name="patid" required>
        </div>
        <div>
            <label for="medicineid_del">削除したい薬剤ID:</label>
            <input type="text" id="medicineid_del" name="medicineid" required>
        </div>
        <br>
        <input type="submit" value="削除実行">
    </form>
    <br>
    <a href="doctor_menu.jsp">医師メニューへ戻る</a>
</body>
</html>