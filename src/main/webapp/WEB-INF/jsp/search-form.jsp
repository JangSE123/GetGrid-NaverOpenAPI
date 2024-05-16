<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Address Search</title>
</head>
<body>
<h2>주소 검색</h2>
<form action="/search" method="post">
    <input type="text" name="address" placeholder="검색할 주소를 입력하세요">
    <button type="submit">검색</button>
</form>
</body>
</html>
