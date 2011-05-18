<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body >

<%@include file="/include/header.jsp"%>

<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />

    <h1>Initier une nouvelle partie</h1>
    <p>
    La cr�ation d'une partie est relativement simple. Cependant, cette partie du site
    �tant la moins aboutie, certain bug ou probl�me de mise en page peuvent surprendrent...
    </p>
    <h2>Cr�ation rapide</h2>
    <p>
    Sur la page "Tableau des missions" cliquez sur "Nouvelle exploitation".
    </p>
    Ici, seul 6 champs sont indispenssables:
    <ul>
        <li>Nom : C'est le nom de la partie.</li>
        <li>Description : Ajoutez ici toute les info utiles aux futurs joueurs (ex: niveau des joueurs).</li>
        <li>Nombre max de joueur : Le maximum d'inscription.</li>
        <li>Taille de la carte : Elle est calcul� en fonction du nombre de joueur.</li>
        <li>Vitesse du jeu : </li>
        <ul>
        <li>Standard : tour par tour</li>
        <li>StandardAsynch : Aynchrone, 3pt d'action toute les 4.8 heures (ie un tour par jour)</li>
        <li>QuickTurnBased : un tour toutes les 3 minutes</li>
        <li>QuickAsynch : Aynchrone, 8pt d'action toute les 100 secondes (ie un tour toutes les 3 minutes)</li>
        </ul>
    </ul>
    Les autres champs sont de simples indications sur vos choix.<br/>
    Note: Comme pour le jeu de plateau, pour �tre int�ressant les parties de 
    Full Metal Galaxy doivent comporter entre 3 et 9 joueurs.
    <p>
    C'est tout, cliquez sur "Sauver/c�er la partie" (utilisez l'ascenseur).
    </p>
    
    <h2>Personalisation</h2>
	    <h3>Onglet "Carte"</h3>
	    <p>
	    Cet onglet vous permet d'�diter la carte case par case. Attention, d�s que vous
	    visit� cet onglet, la g�n�ration automatique est d�activ�.<br/>
	    Vous pouvez :</p>
	    <ul>
           <li>Modifier le th�me de la plan�te.</li>
           <li>Modifier la taille de la carte sans tenir compte du nombre de joueur.</li>
           <li>Modifier la forme de la carte. (utilisez le bouton "effacer")</li>
           <li>Editer chaque case comme un dessin.</li>
           <li>Charger une carte toute faite. (pour l'instant la carte de base uniquement)</li>
	    </ul>
	    <h3>Onglet "Pions"</h3>
	    <p>
	    Attention, d�s que vous visit� cet onglet, la pose automatique des minerais
	    est d�activ�e.<br/>
	    Cet onglet vous permet de positionner les minerais o� bon vous semble.
	    Vous pouvez aussi positioner n'importe quelle autre pi�ce avant le d�but de la partie.<br/>
	    Pour faire tourner un pion avant de le poser, cliquez dessus. (en haut a gauche)<br/>
	    Pour effacer un pion, utilisez le clic droit de la souris.<br/>
	    </p>
	    <h3>Les autres onglets</h3>
	    <p>
	    Les autres onglets ne sont pour l'instant pas ouvert au public, ils m'ont, entre autre,
	    servis � la saisie du tutorial.
	    </p>
    
    <h2>D�marrage</h2>
    <p>
    La partie est cr��, mais personne n'est inscrit et elle n'est pas encore d�mar�. En fait
    la partie est cr�� en pause.
    </p>
    <h3>Inscrivez-vous</h3>
    <p>
    A priori, si vous avez cr�� une partie, c'est pour y jouer. Inscrivez vous en cliquant
    sur l'icone d'action <img src="/puzzles/tutorial/images/Register32.png" alt="Register" />. Choisissez
    ensuite la couleur qui vous repr�sentera durant toute la partie. 
    Votre Astronef apparait en bas avec tous les Astronefs en orbite.
    </p>
    <p>
    Attendez que le nombre de joueur maximum soit atteint. En effet, il vous est parfaitement 
    possible de jouer une partie � 3 joueurs sur une carte pr�vu pour 4, mais la partie
    risque d'�tre moins int�ressante.<br/>
    Note : Il n'est possible de s'inscrire que si la partie est en pause.
    </p>
    <h3>Lancez la partie</h3>
    <p>
    Comme la partie est cr�� en pause il vous reste encore � retirer la pause pour pouvoir
    jouer. Pour cela cliquez sur l'icon d�tail de la partie 
    <img src="/puzzles/tutorial/images/Info32.png" alt="Info" />, puis cliquez sur le bouton "Play".
    </p>
    
    <h2>Administration de la partie</h2>
    <p>
    Tous les joueurs inscrit � une partie, peuvent activer/d�activer la pause. 
    Cependant, et afin de d�tecter la triche, toutes les actions des joueurs sont enregistr�es.
    Vous pouvez avoir un apper�u en cliquant l'icon 
    <img src="/puzzles/tutorial/images/Info32.png" alt="Info" />
    puis l'onglet "log" et "admin log".
    </p>
    <p>
    En tant que cr�ateur de la partie, vous avez d'autres droits comme l'�dition de case 
    ou de pions. A priori vous n'aurez pas besoin de cette fonctionalit�.<br/>
    Si vous d�sirez banir un joueur (qui ne joue plus par exemple) contactez l'administrateur.
    </p>
    <p>
    Cet aspect du jeu n'�tant pas encore tr�s d�velopp�, n'h�sitez pas � faire vos demandent
    ou remarques sur le forum.
    </p>
    
<%@include file="/include/footer.jsp"%>
</body>
</HTML>
