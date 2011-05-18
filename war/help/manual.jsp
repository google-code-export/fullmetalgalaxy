<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body >

<%@include file="/include/header.jsp"%>

<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />

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
la cible doit �tre a port� des deux destructeurs.
<pre>
[Clic] destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible
[OK]
</pre>

<h3>Les tirs avanc�s</h3>
Si l'un des destructeurs est trop �loign� de la cible, il est possible de d�placer le destructeur 1 
dans la zone de feu adverse juste avant le tir:
<pre>
[Clic] destructeur 1
[Clic] case o� positionner le destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible
[OK]
</pre>

<h3>Les tirs tr�s avanc�s</h3>
Il est m�me possible de faire deux tirs apr�s le d�placement:
<pre>
[Clic] destructeur 1
[Clic] case o� positionner le destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible 1
[Clic] Cible 2
[OK]
</pre>
Ou bien :
<pre>
[Clic] destructeur 1
[Clic] case o� positionner le destructeur 1
[Clic] icon cible
[Clic] destructeur 2
[Clic] Cible 1
[Clic] icon cible
[Clic] destructeur 3
[Clic] Cible 2
[OK]
</pre>
A noter: d'autre s�quence fonctionnent, notaments la s�lections des pions dans le d�sordre
ou l'omission du d�placement initial. Mais dans certaine situation rare, seul les s�quences
propos� ici r�alise l'action exactement comme vous le souhaitez.

<h2>Les contr�les de pi�ce standard</h2>
<pre>
Les deux destructeurs doivent �tre au contact de la cible.
[Clic] destructeur 1
[Clic] icon main/controle
[Clic] destructeur 2
[Clic] Cible
[OK]
</pre>

<h2>Les contr�les d'astronefs</h2>
<p>
D�truisez les trois tourelles puis entrez dedans avec un destructeur, comme s'il s'agissait du votre.
</p>

<h2>Le d�chargement</h2>
<p>
Pour d�charger un v�hicule d'un autre:
</p>
<pre>
[Clic] transporteur
[Clic] v�hicule a d�charger (en bas a droite)
[Clic] case o� positionner le v�hicule
[OK]
</pre>
D�charger un v�hicule co�te un point d'action. Mais d�charger un v�hicule lui m�me charg� ne co�te aussi qu'un point d'action.
ex pour d�charger un crabe et deux char de la barge en un seul point d'action:
<pre>
[Clic] barge
[Clic] crabe
[Clic] case o� positionner le crabe
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

Pour positionner la barge comme on le souhaite, il est possible de s�lectioner les deux cases qu'elle doit
occuper:
<pre>
[Clic] pod de l'astronef
[Clic] barge
[Clic] 1iere case adjacente
[Clic] 2nd case (& v�rifier que �a nous convient)
[OK] (-> 1pt d'action)
</pre>

<h2>chat / joueurs connect�</h2>
<h2>timeline</h2>


<%@include file="/include/footer.jsp"%>
</body>
</HTML>
