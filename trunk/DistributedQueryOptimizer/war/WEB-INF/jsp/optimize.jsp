<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
  <title><fmt:message key="optimize.title"/></title>
  <style>
    .error { color: red; }
  </style>
</head>
<body>
	<!-- SQL -->
	
	<div id="sql">
		<br/>
		<br/>
		<h3><fmt:message key="optimize.sqlQuery"/></h3>
		<br/>
		<br/>
		<form:form method="post" commandName="query">
		  <table width="95%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
		    <tr>
		       <td align="right" width="10%"><fmt:message key="optimize.sql"/>:</td>
		         <td width="60%">
		           <form:textarea cols="40" rows="10" path="sql"/>
		         </td>
		         <td width="30%">
		           <form:errors path="sql" cssClass="error"/>
		         </td>
		    </tr>
		  </table>
		  <br>
		  <input type="submit" align="center" value="<fmt:message key="optimize.startButton"/>">
		</form:form>
	</div>
	
	<!-- Relational Algebra -->
	
	<c:if test="${not empty query.queryData}">
		<div id="algebra">
			<br/>
			<br/>
			<h3><optimize.relationalAlgebra></h3>
			<br/>
			<br/>
			<fmt:message key="optimize.relationalAlgebra"/>:</br>
			<c:out value="${query.queryData}" escapeXml="false"/>
		</div>
	</c:if>
</body>
</html>