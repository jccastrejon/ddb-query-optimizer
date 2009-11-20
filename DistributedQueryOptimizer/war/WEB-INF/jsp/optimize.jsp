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
		
		<!-- Unsupported query -->
		<c:if test="${(not empty query.sql) and (empty query.queryData)}">
			<span id="sql.errors" class="error"><fmt:message key="optimize.unsupportedQuery"/></span>
		</c:if>
		
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

		<!-- Rewriting Steps -->
		<div id="operatorTrees">
			<h3><fmt:message key="optimize.operatorTrees"/></h3></br>
			<table width="100%" border="1">
				<tr>
				<c:forEach var="step" begin="0" end="${query.operatorTree.rewritingSteps}">
					<c:if test="${(step gt 0) and ((step mod 2) eq 0)}">
						</tr>
						<tr>
					</c:if>				
					<td width="50%" align="center">
						<img src="img/${query.id}/${query.id}-${step}.png" />
					</td>
				</c:forEach>
				</tr>
			</table>
		</div>
	</c:if>
</body>
</html>