<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    List<Map<String, String>> hospitals = (List<Map<String, String>>)request.getAttribute("hospitals");
    String searchError = (String)request.getAttribute("searchError");
    Object searchedMinCapitalObj = request.getAttribute("searchedMinCapital");
    String searchedMinCapital = "";
    if (searchedMinCapitalObj != null) {
        searchedMinCapital = searchedMinCapitalObj.toString();
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>病院一覧</title>
    <style>
        body { font-family: sans-serif; }
        table { border-collapse: collapse; margin-top: 1em; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f0f0f0; }
        .search-form { margin-bottom: 1em; }
        .search-form input[type="number"] { width: 100px; }
        .error { color: red; }
    </style>
</head>
<body>
<h2>病院一覧</h2>

<%-- 資本金検索フォーム --%>
<div class="search-form">
    <form method="get" action="HospitalListServlet">
        資本金 (万円など) <input type="number" name="minCapital" min="0" value="<%= searchedMinCapital %>"> 以上で検索
        <input type="submit" value="検索">
        <a href="HospitalListServlet">全件表示</a> <%-- 検索解除用リンク --%>
    </form>
    <% if (searchError != null) { %>
        <p class="error"><%= searchError %></p>
    <% } %>
</div>

<table border="1">
    <tr>
        <th>ID</th>
        <th>病院名</th>
        <th>住所</th>
        <th>電話番号</th>
        <th>メール</th>
        <th>資本金</th> <%-- 資本金カラムヘッダーを追加 --%>
    </tr>
    <% if (hospitals != null && !hospitals.isEmpty()) {
        for (Map<String, String> hosp : hospitals) { %>
            <tr>
                <td><%= hosp.get("hospid") %></td>
                <td><%= hosp.get("hospname") %></td>
                <td><%= hosp.get("address") %></td>
                <td><%= hosp.get("tel") %></td>
                <td><%= hosp.get("email") != null ? hosp.get("email") : "" %></td>
                <td><%= hosp.get("capital") != null ? hosp.get("capital") : "" %></td> <%-- 資本金データを表示 --%>
            </tr>
    <%  }
    } else { %>
        <tr>
            <td colspan="6">該当する病院情報はありません。</td>
        </tr>
    <% } %>
</table>
<br>
<a href="admin_menu.jsp">管理者メニューへ戻る</a>
</body>
</html>