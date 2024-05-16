<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search Result</title>
</head>
<body>
<h2>검색 결과</h2>
<!-- 여기에 검색 결과를 표시합니다. -->
<p>위도: ${latitude}, 경도: ${longitude}</p>
<p>그리드 X: ${gridX}, 그리드 Y: ${gridY}</p>
<!-- 오류가 있을 경우 오류 메시지를 표시합니다. -->
<p style="color: red;"><%= request.getAttribute("error") %></p>
</body>
</html>
