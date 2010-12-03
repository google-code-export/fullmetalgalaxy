<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.datastore.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Profil du joueur</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<h1>Message privé</h1>

<%
long id = 0;
try
{
	id = Long.parseLong( request.getParameter( "id" ) );
} catch( NumberFormatException e )
{
}

EbAccount account = FmgDataStore.sgetAccount( id );
if( account == null ) 
{ 
	out.println("<h2>Le profil " + request.getParameter( "id" ) + " n'existe pas.</h2>" );
	return;
}
if( !Auth.isUserLogged( request, response ) )
{
	out.println("<h2>Vous devez être connecté pour envoyer un message a un autre joueur.</h2>" );
	return;
}
if( !account.isAllowPrivateMsg() || !account.haveEmail() )
{
	out.println("<h2>" + account.getPseudo() + " ne souhaite pas être contacté par mail.</h2>" );
	return;
}
%>

&nbsp;A : <%= account.getPseudo() %><br/>

<form name="myform" action="/PMServlet" method="post" enctype="multipart/form-data" accept-charset="utf-8">
<input type="hidden" name="fromid" value="<%= Auth.getUserAccount( request, response ).getId() %>"/>
<input type="hidden" name="toid" value="<%= account.getId() %>"/>
Objet :
<input type="text" name="subject" value=""/><br/>
<textarea cols="50" rows="10" name="msg">
</textarea><br/>

<input type="submit" name="Submit" value="Envoyer"/>
<input type="reset" value="Annuler">
</form>

<%@include file="include/footer.jsp"%>
</body>
</html>
