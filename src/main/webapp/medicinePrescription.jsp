<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map, java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>薬投与指示</title>
    <script>
        function addMedicineRow() {
            const container = document.getElementById("medicines-container");
            const newRow = document.createElement("div");
            newRow.className = "medicine-row";
            // 薬剤選択肢はDBから動的に取得することが望ましい
            // このサンプルでは固定値を維持
            newRow.innerHTML = `
                <select name="medicineid" required>
                    <option value="">-- 薬剤選択 --</option>
                    <option value="MED001">アスピリン</option>
                    <option value="MED002">ロキソニン</option>
                    <option value="MED003">イブプロフェン</option>
                    <option value="MED004">パラセタモール</option>
                    <%-- 他の薬剤オプションをここに追加 --%>
                </select>
                投与量: <input type="text" name="dosage" required>
                指示: <input type="text" name="instructions" required>
                <button type="button" onclick="removeMedicineRow(this)" class="remove-btn">この行を削除</button>
            `;
            container.appendChild(newRow);
        }

        function removeMedicineRow(button) {
            const rowToRemove = button.parentNode;
            // 最後の1行は削除できないようにする (任意)
            // if (document.getElementById("medicines-container").children.length > 1) {
            //    rowToRemove.remove();
            // } else {
            //    alert("少なくとも1つの薬剤情報は必要です。");
            // }
            rowToRemove.remove(); // 常に削除可能にする場合
        }
    </script>
    <style>
        body { font-family: sans-serif; margin: 20px; }
        h2 { color: #333; }
        .container { width: 700px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #f9f9f9;}
        .medicine-row { margin-bottom: 10px; padding: 10px; border: 1px solid #ccc; background-color: #f0f0f0; border-radius: 4px; display: flex; align-items: center; }
        .medicine-row select, .medicine-row input[type="text"] { margin-right: 10px; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
        .medicine-row input[name="dosage"] { width: 80px; }
        .medicine-row input[name="instructions"] { flex-grow: 1; }
        .remove-btn { padding: 8px 12px; background-color: #ffdddd; border: 1px solid #ffaaaa; cursor: pointer; border-radius: 4px; }
        .action-buttons button, .action-buttons input[type="submit"] { padding: 10px 20px; margin-right:10px; border:none; border-radius:4px; cursor:pointer; font-size: 1em;}
        .action-buttons button { background-color: #007bff; color:white;}
        .action-buttons input[type="submit"] { background-color: #28a745; color:white;}
        .message { margin: 15px 0; padding:10px; border-radius: 5px; }
        .success { border:1px solid green; color: green; background-color: #e6ffe6; }
        .error { border:1px solid red; color: red; background-color: #ffe6e6; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="submit"], button { margin-top: 5px; margin-bottom:10px;}
        .form-group { margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="container">
        <h2>薬投与指示</h2>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage"); // サーブレットからセットされたエラー
            if (errorMessage == null) { // フォールバックとしてセッションからも確認
                errorMessage = (String) session.getAttribute("errorMessage");
            }

            if (errorMessage != null) {
        %>
            <div class="message error"><%= errorMessage %></div>
        <%
                session.removeAttribute("errorMessage"); // 表示後にセッションから削除
                // requestスコープのものはフォワードなので消える
            }

            // フォームの値を復元するための準備
            String patid_val = request.getAttribute("patid") != null ? (String)request.getAttribute("patid") : "";
            String empid_val = request.getAttribute("empid") != null ? (String)request.getAttribute("empid") : "";
            List<Map<String, String>> medicinesList_val = (List<Map<String, String>>) request.getAttribute("medicinesList");
            if (medicinesList_val == null) {
                medicinesList_val = new ArrayList<>();
            }
        %>

        <form action="<%= request.getContextPath() %>/MedicinePrescriptionServlet" method="post">
            <div class="form-group">
                <label for="patid">患者ID:</label>
                <input type="text" id="patid" name="patid" value="<%= patid_val %>" required>
            </div>
            <div class="form-group">
                <label for="empid">従業員ID (医師ID):</label>
                <input type="text" id="empid" name="empid" value="<%= empid_val %>" required>
            </div>

            <hr>
            <h3>薬剤リスト</h3>
            <div id="medicines-container">
                <% if (medicinesList_val.isEmpty()) { // 初期表示またはエラーでリストが空の場合 %>
                    <div class="medicine-row">
                        <select name="medicineid" required>
                            <option value="">-- 薬剤選択 --</option>
                            <option value="MED001">アスピリン</option>
                            <option value="MED002">ロキソニン</option>
                            <option value="MED003">イブプロフェン</option>
                            <option value="MED004">パラセタモール</option>
                        </select>
                        投与量: <input type="text" name="dosage" required>
                        指示: <input type="text" name="instructions" required>
                        <%-- 最初の行は削除ボタンなしにするか、必要に応じて追加 --%>
                        <%-- <button type="button" onclick="removeMedicineRow(this)" class="remove-btn">この行を削除</button> --%>
                    </div>
                <% } else { %>
                    <% for (Map<String, String> med : medicinesList_val) { %>
                        <div class="medicine-row">
                            <select name="medicineid" required>
                                <option value="">-- 薬剤選択 --</option>
                                <option value="MED001" <%= "MED001".equals(med.get("medicineid")) ? "selected" : "" %>>アスピリン</option>
                                <option value="MED002" <%= "MED002".equals(med.get("medicineid")) ? "selected" : "" %>>ロキソニン</option>
                                <option value="MED003" <%= "MED003".equals(med.get("medicineid")) ? "selected" : "" %>>イブプロフェン</option>
                                <option value="MED004" <%= "MED004".equals(med.get("medicineid")) ? "selected" : "" %>>パラセタモール</option>
                            </select>
                            投与量: <input type="text" name="dosage" value="<%= med.get("dosage") %>" required>
                            指示: <input type="text" name="instructions" value="<%= med.get("instructions") %>" required>
                            <button type="button" onclick="removeMedicineRow(this)" class="remove-btn">この行を削除</button>
                        </div>
                    <% } %>
                <% } %>
            </div>
            <div class="action-buttons">
                <button type="button" onclick="addMedicineRow()">薬剤行追加</button>
                <input type="submit" value="確認画面へ進む">
            </div>
        </form>
        <br>
        <a href="<%= request.getContextPath() %>/doctor_menu.jsp">医師メニューへ戻る</a>
    </div>
</body>
</html>