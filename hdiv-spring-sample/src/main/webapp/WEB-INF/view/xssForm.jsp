<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XSS Vulnerability Sample</title>
</head>
<body>
<form:form method="POST" action="showMessage3" modelAttribute="command">
   <table>
    <tr>
        <td><form:label path="text">Enter some text</form:label></td>
        <td><form:textarea path="text" /></td>
    <tr>
        <td colspan="2">
            <input type="submit" value="Submit"/>
        </td>
    </tr>
</table>  
</form:form>
</body>
</html>