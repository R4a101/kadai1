<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Employee" %>
<%
    // セッションでログインチェック（emprole==0が受付）
    Employee emp = (Employee)session.getAttribute("employee");
    if (emp == null || emp.getEmprole() != 0) { // 受付(0)以外はリダイレクト
        response.sendRedirect(request.getContextPath() + "/login.jsp"); // コンテキストパスを追加
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>受付メニュー</title>
    <style>
        body {
            font-family: 'Helvetica Neue', Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f0f2f5; /* 少し明るい背景色 */
            color: #333;
            display: flex;
            justify-content: center; /* 水平中央揃え */
            align-items: center;     /* 垂直中央揃え (コンテンツが少ない場合) */
            min-height: calc(100vh - 40px); /* 画面の高さに合わせる */
        }
        .container {
            width: 400px; /* コンテナの幅を少し広げる */
            padding: 30px 40px;
            background-color: #ffffff;
            border-radius: 10px; /* 角丸を少し大きく */
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            text-align: center; /* コンテナ内の要素を中央揃え */
        }
        h2 {
            font-size: 28px; /* 見出しを大きく */
            color: #2c3e50; /* 少し濃いめの色 */
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #3498db; /* 下線を追加 */
        }
        .welcome-message {
            font-size: 18px;
            color: #555;
            margin-bottom: 35px; /* ボタンとの間隔を広げる */
        }
        .menu-btn {
            display: block;
            width: 100%; /* ボタン幅をコンテナに合わせる */
            box-sizing: border-box; /* paddingとborderをwidthに含める */
            margin: 15px 0; /* ボタン間のマージンを調整 */
            padding: 15px 10px; /* パディングを上下に少し増やす */
            font-size: 17px; /* フォントサイズを少し調整 */
            background-color: #3498db; /* プライマリカラー */
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
            transition: background-color 0.2s ease-in-out, transform 0.1s ease; /* transform効果追加 */
            line-height: 1.4; /* 行の高さを調整して、テキストが詰まるのを防ぐ */
        }
        .menu-btn:hover {
            background-color: #2980b9; /* ホバー時の色を少し濃く */
            transform: translateY(-2px); /* 少し浮き上がる効果 */
        }
        .menu-btn:active {
            transform: translateY(0px); /* クリック時の効果 */
        }

        /* 特定のボタンのテキストが長い場合、個別に調整も可能 */
        /* 例:
        .menu-btn.long-text {
            padding-top: 18px;
            padding-bottom: 18px;
        }
        */
    </style>
</head>
<body>
    <div class="container">
        <h2>受付メニュー</h2>
        <p class="welcome-message">ようこそ、<%= emp.getEmpname() %> さん</p>

        <%-- 従業員情報変更ボタンをグループ化するなどの工夫も可能 --%>
        <a href="<%= request.getContextPath() %>/employeePasswordChange.jsp" class="menu-btn">パスワード変更</a>
        <a href="<%= request.getContextPath() %>/employeeUpdate.jsp" class="menu-btn">氏名変更</a>
        <hr style="border: none; border-top: 1px solid #eee; margin: 25px 0;"> <%-- 区切り線 --%>
        <a href="<%= request.getContextPath() %>/patientRegist.jsp" class="menu-btn">患者登録</a>
        <a href="<%= request.getContextPath() %>/PatientListServlet" class="menu-btn">患者管理（一覧・編集・検索）</a>
        <%-- patientSearch.jspへの直接リンクはPatientListServletに統合されているなら不要かも --%>
        <%-- <a href="<%= request.getContextPath() %>/patientSearch.jsp" class="menu-btn">患者検索機能</a> --%>
        <hr style="border: none; border-top: 1px solid #eee; margin: 25px 0;"> <%-- 区切り線 --%>
        <a href="<%= request.getContextPath() %>/logout" class="menu-btn" style="background-color: #e74c3c;">ログアウト</a> <%-- ログアウトボタンの色を変える --%>
    </div>
</body>
</html>

