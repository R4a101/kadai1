<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>処置内容確認</title>
    <style>
        body { font-family: sans-serif; margin: 20px; }
        h2 { color: #333; }
        .container { width: 600px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #f9f9f9;}
        .info-group { margin-bottom: 15px; }
        .info-group label { font-weight: bold; display: inline-block; width: 100px; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #e9e9e9; }
        .actions { margin-top: 20px; text-align: right; }
        .actions input[type="submit"], .actions a.button {
            padding: 10px 20px;
            margin-left: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            font-size: 1em;
        }
        .actions input[type="submit"] { background-color: #4CAF50; color: white; }
        .actions a.button { background-color: #f44336; color: white; display: inline-block; line-height: normal; } /* aタグのbuttonスタイル調整 */
        .message { margin:15px 0; padding:10px; border-radius: 5px; }
        .error { border:1px solid red; color: red; background-color: #ffe6e6; }
    </style>
</head>
<body>
    <div class="container">
        <h2>処置内容確定画面</h2>

        <%-- 
            この画面で表示するエラーメッセージは、主にTreatmentConfirmServletのdoGetで
            データ不備などにより薬指示画面に戻される直前にセットされるもの、
            または何らかの理由でこのページにエラー情報が渡された場合を想定。
            DB登録失敗時は、薬指示画面に戻ってエラーが表示されるフローになっています。
        --%>
        <%
            String errorMessage = (String) session.getAttribute("errorMessage"); // 汎用エラーメッセージ
            if (errorMessage == null) { // リクエストスコープも確認（フォワードされた場合）
                 errorMessage = (String) request.getAttribute("errorMessage");
            }
            if (errorMessage != null) {
        %>
            <div class="message error"><%= errorMessage %></div>
        <%
                session.removeAttribute("errorMessage"); // セッションのエラーは表示後削除
            }
        %>

        <%
            String patid = (String) request.getAttribute("patid");
            String empid = (String) request.getAttribute("empid");
            List<Map<String, String>> medicinesList = (List<Map<String, String>>) request.getAttribute("medicinesList");
        %>

        <div class="info-group">
            <label>患者ID:</label>
            <span><%= patid != null ? patid : "N/A" %></span>
        </div>
        <div class="info-group">
            <label>従業員ID:</label>
            <span><%= empid != null ? empid : "N/A" %></span>
        </div>

        <h3>投与薬剤リスト</h3>
        <% if (medicinesList != null && !medicinesList.isEmpty()) { %>
            <table>
                <thead>
                    <tr>
                        <th>薬剤ID</th>
                        <th>投与量</th>
                        <th>指示</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Map<String, String> med : medicinesList) { %>
                        <tr>
                            <td><%= med.get("medicineid") %></td>
                            <td><%= med.get("dosage") %></td>
                            <td><%= med.get("instructions") %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <p>投与される薬剤はありません。</p>
        <% } %>

        <p>上記の内容で処置を確定しますか？</p>

        <form action="<%= request.getContextPath() %>/TreatmentConfirmServlet" method="post" style="display: inline;">
            <div class="actions">
                <%-- 修正ボタンはMedicinePrescriptionServletのGETを呼び出し、セッションのtemp情報を使ってフォームを復元 --%>
                <a href="<%= request.getContextPath() %>/MedicinePrescriptionServlet" class="button">修正する</a>
                <input type="submit" value="確定する">
            </div>
        </form>
        <br>
        <a href="<%= request.getContextPath() %>/doctor_menu.jsp">医師メニューへ戻る</a>
    </div>
</body>
</html>