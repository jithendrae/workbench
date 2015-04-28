<!DOCTYPE html>
    
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
	<head>
		<meta charset="utf-8">
		<title>Welcome</title>
	</head> 
	<body>
	
		<spring:url value="/login" var="messageUrl" />
		<a href="${messageUrl}">Click to check Hdiv Form Validation</a>
		
		<br />
				<br />
				
	
		<spring:url value="/xssVulnerability" var="messageUrl" />
		<a href="${messageUrl}">Click to check XSS Vulnerability</a>
		
		<br />
				<br />
		
		
		<spring:url value="/maliciousExecution" var="messageUrl" />
		<a href="${messageUrl}">Click to check Malicious File Executions</a>
		<br />
				<br />
		
				
		<spring:url value="/directObjectReference" var="messageUrl" />
		<a href="${messageUrl}">Click to check Object References / CSRF / Restrict Url Access</a>
		
		<br />
				<br />
		
		
		<spring:url value="/codeInjection" var="messageUrl" />
		<a href="${messageUrl}">Click to check Code Injection</a>
		
	</body>
</html>
