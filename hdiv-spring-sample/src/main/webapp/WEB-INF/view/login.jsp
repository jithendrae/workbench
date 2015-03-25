<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HDIV Form</title>
</head>
<body>

	<form:form method="post" action="showMessage" modelAttribute="command">
	<p> Click submit to test form submission with Hdiv integration</p>
		      <input type="submit" value="Submit" />  
	</form:form>	

</body>
</html>