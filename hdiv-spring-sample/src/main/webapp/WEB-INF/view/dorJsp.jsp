<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html>
<head>
<title>Insecure Direct Object References</title>
</head>
<body>
Get Account Details:
Account Number : 

	<spring:url value="/getAccountDetails/2001010101" var="var" />
		<a href="${var}">2001010101</a>
</body>
</html>