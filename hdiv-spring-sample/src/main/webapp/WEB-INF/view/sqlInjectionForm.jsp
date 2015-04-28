<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
<title>SQL Injection Test Form</title>
</head>
<body>
<form:form method="POST" action="sqlInjection" modelAttribute="command">
Enter code here: 

<table>
    <tr>
        <td><form:label path="code">code</form:label></td>
        <td><form:input path="code" /></td>
    </tr>
    
    <tr>
        <td colspan="2">
            <input type="submit" value="Submit"/>
        </td>
    </tr>
</table>  
</form:form>
</body>
</html>