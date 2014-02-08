<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <title>view</title>
</head>
<body>

<div>
    <img src="${path}/simple/viewPic.do?executionId=${executionId}" style="position:absolute;left:0px;top:0px;">
</div>

</body>
</html>