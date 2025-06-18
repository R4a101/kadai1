<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="model.Employee" %>
<%
    // セッションで受付ユーザーのみ許可（emprole==0）
    Employee emp = (Employee)session.getAttribute("employee");
    if (emp == null || emp.getEmprole() != 0) {
        response.sendRedirect("login.jsp");
        return;
    }

    Map<String, Object> patient = (Map<String, Object>)request.getAttribute("patient");
    String errorMessage = (String)request.getAttribute("errorMessage");
    String successMessage = (String)request.getAttribute("successMessage");

    String patid = "";
    String patlname = "";
    String patfname = "";
    String hokenmei = "";
    String hokenexpStr = "";

    if (patient != null) {
        patid = patient.get("patid") != null ? (String)patient.get("patid") : "";
        patlname = patient.get("patlname") != null ? (String)patient.get("patlname") : "";
        patfname = patient.get("patfname") != null ? (String)patient.get("patfname") : "";
        hokenmei = patient.get("hokenmei") != null ? (String)patient.get("hokenmei") : "";
        if (patient.get("hokenexp") != null) {
            // java.sql.Date から yyyy-MM-dd の文字列に変換
            hokenexpStr = new SimpleDateFormat("yyyy-MM-dd").format(patient.get("hokenexp"));
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>患者保険証情報編集</title>
    <style>
        body { font-family: sans-serif; }
        .container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
        label { display: block; margin-bottom: 8px; font-weight: bold; }
        input[type="text"], input[type="date"] { width: 95%; padding: 10px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; }
        input[type="submit"], .btn-back {
            padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer;
            text-decoration: none; color: white;
        }
        input[type="submit"] { background-color: #2ecc71; }
        input[type="submit"]:hover { background-color: #27ae60; }
        .btn-back { background-color: #3498db; display: inline-block; margin-top:10px;}
        .btn-back:hover { background-color: #2980b9; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb;}
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb;}
    </style>
    <script>
        function validateForm() {
            var hokenmei = document.forms["editForm"]["hokenmei"].value;
            var hokenexp = document.forms["editForm"]["hokenexp"].value;

            if (hokenmei.trim() === "" && hokenexp.trim() === "") {
                // 両方空の場合は更新しないという仕様もアリだが、ここではどちらかは入力必須とする例
                // alert("保険証名または有効期限のどちらかは入力してください。");
                // return false;
            }
            // 日付の形式チェックなど、より詳細なバリデーションもここに追加可能
            return true;
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>患者保険証情報編集</h2>

        <% if (errorMessage != null) { %>
            <div class="message error"><%= errorMessage %></div>
        <% } %>
        <% if (successMessage != null) { %>
            <div class="message success"><%= successMessage %></div>
        <% } %>

        <% if (patient != null) { %>
            <p><strong>患者ID:</strong> <%= patid %></p>
            <p><strong>氏名:</strong> <%= patlname %> <%= patfname %></p>
            <hr>
            <form name="editForm" action="PatientUpdateServlet" method="post" onsubmit="return validateForm();">
                <input type="hidden" name="patid" value="<%= patid %>">

                <label for="hokenmei">保険証名:</label>
                <input type="text" id="hokenmei" name="hokenmei" value="<%= hokenmei %>">

                <label for="hokenexp">有効期限:</label>
                <input type="date" id="hokenexp" name="hokenexp" value="<%= hokenexpStr %>">
                <%-- type="date" は yyyy-MM-dd 形式を期待する --%>

                <input type="submit" value="更新">
            </form>
        <% } else if (errorMessage == null) { // patientがnullで、特にエラーメッセージもない場合(通常はありえない)
            // 通常、PatientEditServletで患者が見つからない場合はerrorMessageがセットされる
        %>
            <p class="message error">表示する患者情報がありません。</p>
        <% } %>
        <br>
        <a href="PatientListServlet" class="btn-back">患者一覧へ戻る</a>
    </div>
</body>
</html>