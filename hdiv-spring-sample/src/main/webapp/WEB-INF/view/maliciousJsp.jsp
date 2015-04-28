<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>


<html>
<head>
<title>Malicious File Execution</title>
</head>
<body>

<form:form method="GET" action="maliciousJsp" modelAttribute="command">
    <p>File Name</p>
   <p>File_1</p>
   <p>File_2</p>
   
    <table>    
     <tr>
        <td><form:label path="fileName">Enter Name</form:label></td>
        <td><form:input path="fileName" /></td>
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