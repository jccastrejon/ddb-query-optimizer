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
		<h3><fmt:message key="optimize.sqlQuery"/></h3>
		<form:form method="post" commandName="query">
			<form:hidden path="id"/>
			<form:errors path="sql" cssClass="error"/><br/>
			<fmt:message key="optimize.sql"/>:<br/>
			<form:textarea cols="40" rows="10" path="sql"/></br>
			<input type="submit" align="center" value="<fmt:message key="optimize.startButton"/>">
		</form:form>
	</div>
	
	<c:if test="${not empty query.queryData}">
		<!-- Relational Algebra -->
		<div id="algebra">
			<h3><fmt:message key="optimize.relationalAlgebra"/></h3>
			</br>
			<c:out value="${query.queryData}" escapeXml="false"/>
		</div>

		<!-- Rewriting -->
		<div id="operatorTrees">
			<h3><fmt:message key="optimize.operatorTrees"/></h3></br>
			<c:forEach var="intermediateOperatorTree" begin="0" end="${query.intermediateOperatorTrees}">
				<img src="img/${query.id}/${query.id}-${intermediateOperatorTree}.png"/>
			</c:forEach>
		</div>
	</c:if>
</body>
</html>