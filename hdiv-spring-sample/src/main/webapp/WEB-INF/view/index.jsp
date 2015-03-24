<!DOCTYPE html>
    
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
	<head>
		<meta charset="utf-8">
		<title>Welcome</title>
	</head> 
	<body>
		<spring:url value="/login" var="messageUrl" />
		<a href="${messageUrl}">Click to enter</a>
	</body>
</html>
