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

    <h1>Manuel d'utilisation</h1>


<h2>raccourcis claviers</h2>
<pre>
[ESC] annuler l'action en cours
[UP][DOWN][LEFT][RIGHT] scroll de la carte
[+][-] changement de zoom
[G] afficher la grille
[F] afficher les zones de feu
[CTRL]+[Clic] ou [Clic droit] Mouvement de plusieurs cases

[CTRL]+[M] afficher le menu
</pre>

<h2>Les tirs</h2>
la cible doit être a porté des deux destructeurs.
<pre>
[Clic] destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible
[OK]
</pre>

<h3>Les tirs avancés</h3>
Si l'un des destructeurs est trop éloigné de la cible, il est possible de déplacer le destructeur 1 
dans la zone de feu adverse juste avant le tir:
<pre>
[Clic] destructeur 1
[Clic] case où positionner le destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible
[OK]
</pre>

<h3>Les tirs très avancés</h3>
Il est même possible de faire deux tirs après le déplacement:
<pre>
[Clic] destructeur 1
[Clic] case où positionner le destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible 1
[Clic] Cible 2
[OK]
</pre>
Ou bien :
<pre>
[Clic] destructeur 1
[Clic] case où positionner le destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible 1
[Clic] icon cible
[Clic] destructeur 3
[Clic] Cible 2
[OK]
</pre>
A noter: d'autre séquence fonctionnent, notaments la sélections des pions dans le désordre
ou l'omission du déplacement initial. Mais dans certaine situation rare, seul les séquences
proposé ici réalise l'action exactement comme vous le souhaitez.

<h2>Les contrôles de pièce standard</h2>
<pre>
Les deux destructeurs doivent être au contact de la cible.
[Clic] destructeur 1
[Clic] icon main/controle
[Clic] destructeur 2
[Clic] Cible
[OK]
</pre>

<h2>Les contrôles d'astronefs</h2>
<p>
Détruisez les trois tourelles puis entrez dedans avec un destructeur, comme s'il s'agissait du votre.
</p>

<h2>Le déchargement</h2>
<p>
Pour décharger un véhicule d'un autre:
</p>
<pre>
[Clic] transporteur
[Clic] véhicule a décharger (en bas a droite)
[Clic] case où positionner le véhicule
[OK]
</pre>
Décharger un véhicule coûte un point d'action. Mais décharger un véhicule lui même chargé ne coûte aussi qu'un point d'action.
ex pour décharger un crabe et deux char de la barge en un seul point d'action:
<pre>
[Clic] barge
[Clic] crabe
[Clic] case où positionner le crabe
[OK] (-> 1pt d'action)
[Clic] barge
[Clic] char 1
[Clic] crabe
[OK] (-> 0pt d'action)
[Clic] barge
[Clic] char 2
[Clic] crabe
[OK] (-> 0pt d'action)
</pre>

Pour positionner la barge comme on le souhaite, il est possible de sélectioner les deux cases qu'elle doit
occuper:
<pre>
[Clic] pod de l'astronef
[Clic] barge
[Clic] 1iere case adjacente
[Clic] 2nd case (& vérifier que ça nous convient)
[OK] (-> 1pt d'action)
</pre>

<h2>chat / joueurs connecté</h2>
<h2>timeline</h2>


<%@include file="/include/footer.jsp"%>
</body>
</HTML>
