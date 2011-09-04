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

    <h1>Foire aux questions</h1>

<p>
<b>Q: Le panneau d'information g�n�ral sur l'exploitation m'annonce 'Pr�vision non disponible'.</b><br/>
R: Effectivement, pour avoir les pr�visions m�t�orologiques, vous devez contr�ler une Pondeuse 
M�t�o &agrave; l'ext�rieur et en �tat de marche : pas d�activ�e par une marr�e 
ou une zone de feu adverse.
</p>
<p>
<b>Q: Bon j'ai sorti ma pondeuse mais je ne vois toujours pas la marr�e futur.</b><br/>
R: Vous ne verrez la marr�e futur qu'au prochain changement de tour ou incr�ments de temps.
</p>
<p>
<b>Q: Je n'arrive pas &agrave; charger mon Crabe dans ma Barge.</b><br/>
R: L'action de chargement co�te un point d'action et n�cessite que le v�hicule charg� 
ait suffisament de place. Un Crabe charg� de 2 chars occupe 4 places.
</p>
<p>
<b>Q: Je n'arrive pas � visualiser l'ensemble du plateau de jeu.</b><br/>
R: La zone que vous pouvez visuliser d'un seul coup d'&oelig;il est variable selon la r�solution
de votre �cran. Vous pouvez cependant utiliser les deux icons loupes [+/-] pour changer de vue.
Si cela ne suffit pas, vous pouvez aussi utiliser le zoom du navigateur: [CTRL] + Molette 
ou bien [CTRL] + [+/-]. <br/>
Pour les utilisateurs de mac: [Pomme] + [+]
</p>
<p>
<b>Q: comment orienter la barge comme on veut quand elle sort de l''astronef ?</b><br/>
R: La meilleure solution :<br/>
clic sur le pod<br/>
clic sur la barge<br/>
clic une case adjacente<br/>
clic la deuxi�me case (& v�rifier que �a nous convient)<br/>
valider le mouvement (clic m�me 2nd case ou bouton OK)
</p>
<p>
<b>Q: Comment choisir la couleur d'une unit� control�e par deux destructeurs de couleur diff�rente ?</b><br/>
R: Le premier destructeur s�lectionn� d�termine la future couleur de l'unit� contr�l�e.
</p>
<p>
Q: En mode tour par tour, je ne comprend pas comment sont calcul�s les dates limites de jeu.<br/>
R: 
</p>
<p>
<b>Q: .</b><br/>
R: .
</p>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>
