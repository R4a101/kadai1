<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, model.TreatmentRecord, java.sql.Timestamp, java.text.SimpleDateFormat" %>
<%-- Employee と HttpSession のインポートも追加 (権限チェックなどを行う場合) --%>
<%@ page import="model.Employee" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    // --- 医師ロールのセッションチェック ---
    HttpSession currentSession = request.getSession(false);
    Employee loggedInUser = null;
    String userRole = null;

    if (currentSession != null) {
        loggedInUser = (Employee) currentSession.getAttribute("employee");
        userRole = (String) currentSession.getAttribute("role");
    }

    // ログインしていない、またはロールが医師でない場合はログインページへ
    if (loggedInUser == null || !"doctor".equals(userRole)) {
        // もし特定のemproleで医師を判定するなら:
        // if (loggedInUser == null || loggedInUser.getEmprole() != 1) { // 例: 1 が医師
        
        System.out.println("treatmentHistoryList.jsp: Session check failed or not a doctor. Redirecting to login.");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    // --- セッションチェックここまで ---


    String daoErrorMessage = (String) request.getAttribute("daoErrorMessage");

    @SuppressWarnings("unchecked") // treatmentHistoryListのキャスト警告抑制
    List<TreatmentRecord> historyList = (List<TreatmentRecord>) request.getAttribute("treatmentHistoryList");

    // 日付フォーマッタは一度だけ生成
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>処置履歴一覧</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f4f7f6;
            color: #333;
        }
        .container {
            width: 90%; /* 幅を広げる */
            max-width: 1200px; /* 最大幅も設定 */
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
            border-bottom: 2px solid #007bff; /* 医師メニューに合わせた色 */
            padding-bottom: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 25px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px 12px; /* パディング調整 */
            text-align: left;
            vertical-align: top; /* 複数行になる場合、上揃え */
        }
        th {
            background-color: #007bff; /* 医師メニューに合わせた色 */
            color: #ffffff;
            font-weight: 600;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        .actions {
            margin-top: 25px;
            text-align: center;
        }
        .actions a.button {
            padding: 10px 25px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-size: 1em;
            background-color: #007bff;
            color: white;
            transition: background-color 0.2s ease;
        }
        .actions a.button:hover {
            background-color: #0056b3;
        }
        .no-data {
            text-align: center;
            color: #777;
            padding: 20px 0;
            font-style: italic;
        }
        .medicine-details {
            font-size: 0.9em;
            padding-left: 0; /* ulのデフォルトpaddingをリセットするため */
        }
        .medicine-details ul {
            list-style-type: disc;
            margin: 5px 0 5px 20px; /* ul自体の左マージン */
            padding-left: 0; /* liのマーカーのためのpaddingはブラウザ依存 */
        }
        .medicine-details li {
            margin-bottom: 3px;
        }
        .error-message-display { /* DAOエラーメッセージ用のスタイル */
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            text-align: center;
            font-weight: 500;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>処置履歴一覧</h2>

        <%-- DAOエラーメッセージ表示 (デバッグ文は削除) --%>
        <% if (daoErrorMessage != null) { %>
            <div class="error-message-display">
                <strong>エラー:</strong> <%= daoErrorMessage %>
                <br><em>システム管理者にお問い合わせください。</em>
            </div>
        <% } %>

        <%-- historyList の null チェックはサーブレット側で行い、nullなら空リストを渡す方がJSPはシンプルになる --%>
        <%-- ここではJSP側でも念のためチェック --%>
        <% if (historyList != null && !historyList.isEmpty()) { %>
            <table>
                <thead>
                    <tr>
                        <th>処置ID</th>
                        <th>患者ID</th>
                        <th>担当者ID</th>
                        <th>処置日時</th>
                        <th>投与薬剤詳細</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (TreatmentRecord record : historyList) { %>
                        <tr>
                            <td><%= record.getTreatmentId() != null ? record.getTreatmentId() : "---" %></td>
                            <td><%= record.getPatId() != null ? record.getPatId() : "---" %></td>
                            <td><%= record.getEmpId() != null ? record.getEmpId() : "---" %></td>
                            <td>
                                <%
                                Timestamp ts = record.getTreatmentDate();
                                String formattedDate = "---";
                                if (ts != null) {
                                    formattedDate = sdf.format(ts);
                                }
                                %>
                                <%= formattedDate %>
                            </td>
                            <td>
                                <% List<Map<String, String>> medicines = record.getMedicines(); %>
                                <% if (medicines != null && !medicines.isEmpty()) { %>
                                    <div class="medicine-details">
                                        <ul>
                                        <% for (Map<String, String> med : medicines) { %>
                                            <li>
                                                薬剤ID: <%= med.getOrDefault("medicineid", "N/A") %>,
                                                投与量: <%= med.getOrDefault("dosage", "N/A") %>,
                                                指示: <%= med.getOrDefault("instructions", "N/A") %>
                                            </li>
                                        <% } %>
                                        </ul>
                                    </div>
                                <% } else { %>
                                    薬剤情報なし
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else if (daoErrorMessage == null) { // DAOエラーがなく、リストが空かnullの場合のみ「処置履歴はありません」を表示 %>
            <p class="no-data">現在、表示できる処置履歴はありません。</p>
        <% } %>

        <div class="actions">
            <a href="<%= request.getContextPath() %>/doctor_menu.jsp" class="button">医師メニューへ戻る</a>
        </div>
    </div>
</body>
</html>