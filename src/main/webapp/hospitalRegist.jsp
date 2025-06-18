<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String role = (session != null) ? (String)session.getAttribute("role") : null;
    if (role == null || !"admin".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String error = (String)request.getAttribute("error");
    
    // ★エラー時にフォームの値を復元するための処理
    String hospidVal = request.getAttribute("hospid") != null ? (String)request.getAttribute("hospid") : "";
    String hospnameVal = request.getAttribute("hospname") != null ? (String)request.getAttribute("hospname") : "";
    String addressVal = request.getAttribute("address") != null ? (String)request.getAttribute("address") : "";
    String telVal = request.getAttribute("tel") != null ? (String)request.getAttribute("tel") : "";
    String emailVal = request.getAttribute("email") != null ? (String)request.getAttribute("email") : "";
    String capitalVal = request.getAttribute("capital") != null ? (String)request.getAttribute("capital") : "";

%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>病院登録</title>
    <style>
        body { font-family: sans-serif; }
        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; }
        form { margin: 1.5em 0; }
        label { display: block; margin-bottom: 0.8em; font-weight: bold; }
        input[type="text"], input[type="tel"], input[type="email"], input[type="number"] { width: 95%; padding: 8px; margin-top: 4px; }
        .error { color: red; margin: 1em 0; padding: 10px; background-color: #ffebee; border: 1px solid #ef9a9a; border-radius: 4px; }
        input[type="submit"] { padding: 10px 20px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
        input[type="submit"]:hover { background-color: #0056b3; }
        a { color: #007bff; }
    </style>
    <script>
        function validateForm() {
            var hospid = document.forms["regForm"]["hospid"].value.trim();
            var hospname = document.forms["regForm"]["hospname"].value.trim();
            var address = document.forms["regForm"]["address"].value.trim();
            var tel = document.forms["regForm"]["tel"].value.trim();
            var capital = document.forms["regForm"]["capital"].value.trim();
            
            // ★正規表現パターン
            var telPattern = /^[0-9\-]+$/;

            if (!hospid || !hospname || !address || !tel) {
                alert("必須項目が入力されていません。");
                return false;
            }
            
            if (!tel.match(telPattern)) {
                alert("電話番号は数字とハイフン(-)のみで入力してください。");
                return false;
            }

            if (capital && (isNaN(parseInt(capital, 10)) || parseInt(capital, 10) < 0)) {
                alert("資本金は0以上の数値を入力してください。");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>病院登録</h2>

        <% if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>

        <form name="regForm" action="HospitalRegistServlet" method="post" onsubmit="return validateForm();">
            <label>病院ID（必須）:
                <input type="text" name="hospid" value="<%= hospidVal %>" required>
            </label>
            <label>病院名（必須）:
                <input type="text" name="hospname" value="<%= hospnameVal %>" required>
            </label>
            <label>住所（必須）:
                <input type="text" name="address" value="<%= addressVal %>" required>
            </label>
            <label>電話番号（必須）:
                <input type="tel" name="tel" value="<%= telVal %>" required pattern="[0-9\-]+">
            </label>
            <label>メールアドレス:
                <input type="email" name="email" value="<%= emailVal %>">
            </label>
            <label>資本金 (万円):
                <input type="number" name="capital" min="0" value="<%= capitalVal %>">
            </label>
            <input type="submit" value="登録">
        </form>

        <a href="admin_menu.jsp">管理者メニューへ戻る</a>
    </div>
</body>
</html>