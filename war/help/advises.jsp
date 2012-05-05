<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body >

<%@include file="/include/header.jsp"%>

<div class="inline-ul" >
<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />
</div>

    <h1>Conseils aux d&eacute;butants</h1>

<ul>
  <li>
    Lors de l'inscription &agrave; une partie, bien regardez le type de partie (nombre de joueurs, 
    mode asynchrone, vitesse du jeu, etc...). N'hésitez pas &agrave; contacter les autres joueurs
    pour connaître leurs niveaux, leurs fréquences de jeu. 
  </li>
</ul>
 
<h1>
Ces conseils étaits données à la fin du livre de règle.
</h1>

<ul>
  <li>
    Avant d'entammer une série d'action, ou un plan machiavelique, vérifiez bien que votre 
    stock de point d'action vous permet de mener &agrave; bien votre plan.
  </li>
  <li>
Lisez la règle : vos partenaires ont sûrement omis de vous expliquer quelques détails. En
outre, des questions trop précises de votre part risquent de dévoiler vos intentions.
  </li>
  <li>  
N'exposez pas inutilement votre Pondeuse Météo, votre Gros Tas ou votre Barge : ce sont des pièces
difficilement remplaçables.
  </li>
  <li>
En fonction de votre position et de la position de vos adversaires (et de leur niveau), 
faites-vous
une stratégie dès les premiers tours de jeu et essayez de vous y tenir.
  </li>
  <li>
Économisez vos points d'actions
dès que vous le pourrez : la possibilité de répliquer avec 20 ou
25 points au lieu de 15 peut constituer, de manière dissuasive, votre meilleure défense. Si
vous décidez d'attaquer, ces points supplémentaires vous seront précieux.
  </li>
  <li>
Si un adversaire vous jure qu'il ne serait pas assez rat pour s'attaquer à l'Astronef 
d'un débutant,
ne le croyez jamais.
  </li>
</ul>

<h1>Il y a aussi de véritables cours !</h1>
<ul>
  <li>
<a href="http://fullmetalplanete.forum2jeux.com/trucs-et-astuces-f16/les-lecons-d-alpha-t203.htm">
Les leçons d'Alpha
</a>
  </li>
  <li>
<a href="http://fullmetalplanete.forum2jeux.com/t42-conseils-de-sages">
Les conseils de Grzon
</a>
</li>
</ul>
	
<%@include file="/include/footer.jsp"%>
</body>
</HTML>
