<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>処置完了確認</title>
    <style>
        body { font-family: sans-serif; margin: 20px; }
        h2 { color: #333; }
        .container { width: 600px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #f9f9f9;}
        .info-group { margin-bottom: 15px; }
        .info-group label { font-weight: bold; display: inline-block; width: 100px; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #e9e9e9; }
        .actions { margin-top: 20px; text-align: center; } /* 中央寄せに変更 */
        .actions a.button {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            font-size: 1em;
            background-color: #007bff; /* 医師メニューへのボタンの色 */
            color: white;
            display: inline-block;
        }
        .message { margin:15px 0; padding:10px; border-radius: 5px; }
        .success { border:1px solid green; color: green; background-color: #e6ffe6; text-align: center; font-weight: bold;}
    </style>
</head>
<body>
    <div class="container">
        <h2>処置完了確認</h2>

        <%
            // TreatmentConfirmServletからリダイレクト時にセットされた情報を取得
            String successMessage = (String) session.getAttribute("successMessage");
            String confirmedPatId = (String) session.getAttribute("confirmedPatId");
            String confirmedEmpId = (String) session.getAttribute("confirmedEmpId");
            List<Map<String, String>> confirmedMedicinesList = (List<Map<String, String>>) session.getAttribute("confirmedMedicinesList");

            if (successMessage != null) {
        %>
            <div class="message success"><%= successMessage %></div>
        <%
                // メッセージと表示用データは一度表示したらセッションから削除する
                session.removeAttribute("successMessage");
                session.removeAttribute("confirmedPatId");
                session.removeAttribute("confirmedEmpId");
                session.removeAttribute("confirmedMedicinesList");
            } else {
                // 直接アクセスされたり、情報がない場合は医師メニューへリダイレクト
                response.sendRedirect(request.getContextPath() + "/doctor_menu.jsp");
                return;
            }
        %>

        <h3>登録された処置内容</h3>
        <div class="info-group">
            <label>患者ID:</label>
            <span><%= confirmedPatId != null ? confirmedPatId : "N/A" %></span>
        </div>
        <div class="info-group">
            <label>従業員ID:</label>
            <span><%= confirmedEmpId != null ? confirmedEmpId : "N/A" %></span>
        </div>

        <h4>投与薬剤リスト</h4>
        <% if (confirmedMedicinesList != null && !confirmedMedicinesList.isEmpty()) { %>
            <table>
                <thead>
                    <tr>
                        <th>薬剤ID</th>
                        <th>投与量</th>
                        <th>指示</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Map<String, String> med : confirmedMedicinesList) { %>
                        <tr>
                            <td><%= med.get("medicineid") %></td>
                            <td><%= med.get("dosage") %></td>
                            <td><%= med.get("instructions") %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <p>投与された薬剤はありません。</p>
        <% } %>

        <div class="actions">
            <a href="<%= request.getContextPath() %>/doctor_menu.jsp" class="button">医師メニューへ戻る</a>
        </div>
    </div>
</body>
</html>