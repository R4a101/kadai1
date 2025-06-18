<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>薬剤投与指示・削除</title>
    <style>
        body { font-family: sans-serif; margin: 20px; }
        h2, h3 { color: #333; }
        .container { max-width: 800px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9;}
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        select, input[type="text"] { width: calc(100% - 22px); padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px; }
        input[type="submit"], button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right: 5px; }
        button.delete { background-color: #dc3545; }
        button:hover, input[type="submit"]:hover { opacity: 0.9; }
        .error-message { color: red; margin-bottom: 15px; }
        .success-message { color: green; margin-bottom: 15px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f0f0f0; }
        .action-buttons { text-align: right; margin-top: 20px; }
        .patient-info { padding: 10px; background-color: #e9ecef; border-radius: 4px; margin-bottom: 20px;}
    </style>
</head>
<body>
<div class="container">
    <h2>薬剤投与指示・削除</h2>

    <%-- エラーメッセージ表示 --%>
    <c:if test="${not empty errorMessage}">
        <p class="error-message">${errorMessage}</p>
    </c:if>
    <%-- リダイレクト後の成功メッセージ表示 --%>
    <c:if test="${not empty sessionScope.successMessage}"> 
        <p class="success-message">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>


    <!-- 患者選択フォーム -->
    <form action="PrescriptionManagementServlet" method="post">
        <input type="hidden" name="action" value="selectPatient">
        <div class="form-group">
            <label for="patientId">患者選択:</label>
            <select name="patientId" id="patientId" onchange="this.form.submit()" required>
                <option value="">-- 患者を選択してください --</option>
                <c:forEach var="patient" items="${availablePatients}">
                    <option value="${patient.patientId}" ${selectedPatient.patientId == patient.patientId ? 'selected' : ''}>
                        ${patient.fullName} (ID: ${patient.patientId})
                    </option>
                </c:forEach>
            </select>
        </div>
    </form>

    <%-- 患者が選択されている場合のみ以下のセクションを表示 --%>
    <c:if test="${not empty selectedPatient}">
        <div class="patient-info">
            <h3>対象患者: ${selectedPatient.fullName}</h3>
        </div>

        <!-- 薬剤追加フォーム -->
        <h3>薬剤追加</h3>
        <form action="PrescriptionManagementServlet" method="post">
            <input type="hidden" name="action" value="addMedicine">
            <div class="form-group">
                <label for="medicineId">薬剤:</label>
                <select name="medicineId" id="medicineId" required>
                    <option value="">-- 薬剤を選択 --</option>
                    <c:forEach var="med" items="${availableMedicines}">
                        <option value="${med.medicineId}">${med.medicineName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="dosage">投与量 (例: 1回1錠):</label>
                <input type="text" name="dosage" id="dosage" required>
            </div>
            <div class="form-group">
                <label for="instructions">指示 (例: 1日3回毎食後):</label>
                <input type="text" name="instructions" id="instructions" required>
            </div>
            <button type="submit">薬剤をリストに追加</button>
        </form>

        <!-- 現在の処方内容 -->
        <h3>現在の処方内容</h3>
        <c:choose>
            <c:when test="${not empty currentPrescriptionItems}">
                <table>
                    <thead>
                        <tr>
                            <th>薬剤名</th>
                            <th>投与量</th>
                            <th>指示</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${currentPrescriptionItems}">
                            <tr>
                                <td>${item.medicineName}</td>
                                <td>${item.dosage}</td>
                                <td>${item.instructions}</td>
                                <td>
                                    <form action="PrescriptionManagementServlet" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="deleteMedicine">
                                        <input type="hidden" name="tempId" value="${item.tempId}">
                                        <button type="submit" class="delete">削除</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>現在、処方内容はありません。</p>
            </c:otherwise>
        </c:choose>

        <!-- 処方内容を確定 -->
        <div class="action-buttons">
            <form action="PrescriptionManagementServlet" method="post">
                <input type="hidden" name="action" value="confirmPrescription">
                <input type="submit" value="処方内容を確定" <c:if test="${empty currentPrescriptionItems}">disabled</c:if>>
            </form>
        </div>
    </c:if> <%-- 患者が選択されている場合のif終了 --%>

    <p><a href="doctor_menu.jsp">医師メニューへ戻る</a></p>
</div>
</body>
</html>