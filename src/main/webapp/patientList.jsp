<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="model.Employee" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession jspSession = request.getSession(false);
    Employee emp = null;
    if (jspSession != null) {
        emp = (Employee)jspSession.getAttribute("employee");
    }

    if (emp == null || emp.getEmprole() != 0) { // 受付(0)以外はリダイレクト
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    @SuppressWarnings("unchecked") // List<Map<String, Object>> へのキャスト警告を抑制
    List<Map<String, Object>> patients = (List<Map<String, Object>>)request.getAttribute("patients");
    String searchKeyword = request.getParameter("keyword") != null ? request.getParameter("keyword") : "";
    boolean searchExpiredOnly = "true".equals(request.getParameter("expiredOnly"));

    String successMessage = null;
    String errorMessage = null;
    if (jspSession != null) {
        successMessage = (String) jspSession.getAttribute("successMessage");
        if (successMessage != null) {
            jspSession.removeAttribute("successMessage");
        }
        errorMessage = (String) jspSession.getAttribute("errorMessage");
        if (errorMessage != null) {
            jspSession.removeAttribute("errorMessage");
        }
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 日付フォーマットは一度だけ生成
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>患者一覧・検索</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 20px;
            color: #333;
        }
        .container {
            max-width: 900px; /* 幅を少し広げる */
            margin: 20px auto;
            padding: 25px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 25px;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
        }

        /* メッセージエリア */
        .message-area {
            margin-bottom: 20px;
            text-align: center;
        }
        .message-area .message {
            padding: 12px 20px;
            border-radius: 5px;
            font-weight: 500;
            display: inline-block; /* メッセージの幅を内容に合わせる */
            max-width: 90%;
        }
        .message-area .success {
            color: #155724;
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
        }
        .message-area .error {
            color: #721c24;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
        }

        /* 検索フォーム */
        .search-form {
            margin-bottom: 25px;
            padding: 20px;
            background-color: #ecf0f1;
            border-radius: 6px;
            display: flex; /* Flexboxで要素を横並び */
            align-items: center; /* 垂直方向中央揃え */
            gap: 15px; /* 要素間のスペース */
        }
        .search-form label {
            font-weight: 500;
            white-space: nowrap; /* ラベルが改行しないように */
        }
        .search-form input[type="text"] {
            padding: 8px 10px;
            border-radius: 4px;
            border: 1px solid #bdc3c7;
            flex-grow: 1; /* テキストボックスが残りのスペースを埋める */
        }
        .search-form input[type="checkbox"] {
            margin-left: 5px;
            vertical-align: middle;
        }
        .search-form .btn { /* フォーム内のボタン共通スタイル */
            padding: 8px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            color: white;
            font-weight: 500;
            transition: background-color 0.2s ease;
        }
        .search-form .btn-search { background-color: #f39c12; }
        .search-form .btn-search:hover { background-color: #e67e22; }
        .search-form .btn-clear-search { background-color: #7f8c8d; }
        .search-form .btn-clear-search:hover { background-color: #6c7a7d; }


        /* 患者一覧テーブル */
        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 20px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }
        th, td {
            padding: 12px 15px; /* パディングを少し増やす */
            border: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #3498db;
            color: #ffffff;
            font-weight: 600;
            text-transform: uppercase; /* ヘッダーテキストを大文字に */
            letter-spacing: 0.5px;
        }
        tr:nth-child(even) { /* ストライプ表示 */
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1; /* ホバー時の行の背景色 */
        }

        /* 操作ボタン */
        .actions form {
            display: inline-block;
            margin-right: 5px;
        }
        .actions .btn {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            color: white;
            font-size: 13px; /* 少し小さく */
            font-weight: 500;
            transition: opacity 0.2s ease;
        }
        .actions .btn:hover {
            opacity: 0.85;
        }
        .actions .btn-edit { background-color: #2ecc71; } /* 緑 */
        .actions .btn-delete { background-color: #e74c3c; } /* 赤 */
        /* アイコンをCSSで表現するのは限界があるので、将来的にはSVGやFont Iconを検討
           例： .btn-edit::before { content: "✏️ "; } */

        .no-patients {
            text-align: center;
            padding: 20px;
            color: #777;
            font-style: italic;
        }

        .back-link {
            display: inline-block;
            margin-top: 25px;
            padding: 10px 20px;
            background-color: #95a5a6;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: 500;
            transition: background-color 0.2s ease;
        }
        .back-link:hover {
            background-color: #7f8c8d;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>患者一覧・検索</h2>

        <div class="message-area">
            <% if (successMessage != null) { %>
                <div class="message success">🎉 <%= successMessage %></div>
            <% } %>
            <% if (errorMessage != null) { %>
                <div class="message error">⚠️ <%= errorMessage %></div>
            <% } %>
        </div>

        <div class="search-form">
            <form action="<%= request.getContextPath() %>/PatientListServlet" method="get" style="display: flex; width: 100%; gap: 15px; align-items: center;">
                <label for="keyword">氏名検索:</label>
                <input type="text" id="keyword" name="keyword" value="<%= searchKeyword %>" placeholder="姓または名を入力">
                <div> <%-- チェックボックスとラベルをグループ化 --%>
                    <input type="checkbox" id="expiredOnly" name="expiredOnly" value="true" <%= searchExpiredOnly ? "checked" : "" %>>
                    <label for="expiredOnly" style="font-weight:normal;">保険証期限切れ</label>
                </div>
                <button type="submit" class="btn btn-search">検索</button>
                <a href="<%= request.getContextPath() %>/PatientListServlet" class="btn btn-clear-search">全件表示</a>
            </form>
        </div>

        <% if (patients == null || patients.isEmpty()) { %>
            <p class="no-patients">該当する患者情報は見つかりませんでした。</p>
        <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>患者ID</th>
                    <th>姓</th>
                    <th>名</th>
                    <th>保険証名</th>
                    <th>有効期限</th>
                    <th style="width: 130px;">操作</th> <%-- 操作列の幅を固定 --%>
                </tr>
            </thead>
            <tbody>
            <% for (Map<String, Object> pat : patients) { %>
            <tr>
                <td><%= pat.getOrDefault("patid", "") %></td>
                <td><%= pat.getOrDefault("patlname", "") %></td>
                <td><%= pat.getOrDefault("patfname", "") %></td>
                <td><%= pat.getOrDefault("hokenmei", "") %></td>
                <td><%= pat.get("hokenexp") != null ? sdf.format(pat.get("hokenexp")) : "" %></td>
                <td class="actions">
                    <form action="<%= request.getContextPath() %>/PatientEditServlet" method="get">
                        <input type="hidden" name="patid" value="<%= pat.get("patid") %>">
                        <button type="submit" class="btn btn-edit">編集</button> <%-- Font Awesomeなら <i class="fas fa-edit"></i>編集 --%>
                    </form>
                    <form action="<%= request.getContextPath() %>/PatientDeleteServlet" method="post" onsubmit="return confirm('患者ID: <%= pat.get("patid") %> を本当に削除しますか？');">
                        <input type="hidden" name="patid" value="<%= pat.get("patid") %>">
                        <button type="submit" class="btn btn-delete">削除</button> <%-- Font Awesomeなら <i class="fas fa-trash-alt"></i>削除 --%>
                    </form>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } %>
        <br>
        <a href="<%= request.getContextPath() %>/reception_menu.jsp" class="back-link">受付メニューへ戻る</a>
    </div>
</body>
</html>