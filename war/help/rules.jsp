<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body >

<%@include file="/include/header.jsp"%>

<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />

<jsp:include page="<%= I18n.localize(request,response,\"/help/rules.html\") %>" />

<%@include file="/include/footer.jsp"%>
</body>
</HTML>