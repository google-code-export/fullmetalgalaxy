<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<HTML>
<head>
<title>Pr�sentation de Full Metal Galaxy</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body >
<%@include file="include/header.jsp"%>


<h2>M�canismes</h2>
<p>
Il s'agit d'un wargame (ou jeu de strategie) gratuit jouable dans un navigateur sans aucun
plugin. La dur&eacute;e et la vitesse des parties sont configurables : 3 minutes 
par coup pour une partie de 1h30 ou un coup par jour pour une partie
d'un mois. <br/>
Il est possible de jouer chacun � son tour
ou tous en m&ecirc;me temps (mode asynchrone) � la fa&ccedil;on
des RTS.<BR/>
Chaque partie est ind&eacute;pendante, il n'y a donc pas de monde
persistant. La communaut&eacute; et le classement donnent une
consistante &agrave; l'univers, mais il vous est tout &agrave; fait possible de ne
jouer qu'une partie de temps &agrave; autre sans aucun d&eacute;savantages en
jeu.
</p>

<h2>R�sum� d'une partie</h2>

Vous posez votre astronef sur une plan�te pour une campagne d'extraction. 
Vous recevrez &agrave; intervale r�gulier (en mode asynchrone) ou au d�but de votre tour
(en mode tour par tour) un certain nombre de points d'action que vous pouvez d�penser 
� tout instant pour :
<ul>
<li>Ramasser des minerais � l'aide de vos transporteurs et les ramener dans votre Astronef</li>
<li>Cr�er des pi�ces suppl�mentaires � l'aide du minerai et de votre Pondeuse M�t�o,</li>
<li>Affaiblir l'adversaire avec vos destructeurs en lui d�truisant ou en lui capturant 
des pi�ces</li>
<li>Menacer ou g�ner ses mouvements en occupant les zones appropri�es</li>
<li>Capturer un ou plusieurs Astronefs, pour augmenter le nombre de pi�ces sous votre contr�le, 
b�n�ficier de points d'action suppl�mentaires et d�coller en fin de partie avec plusieurs 
astronefs et leur contenu (minerais et pi�ces).</li>
</ul>
<p>
Les mar�es changeront, en cours de partie, la topologie de la carte rendant 
certaines zones inacessibles par voie terrestre.
</p>
<p>
Pendant toute la partie, vous pourrez aussi �changer avec vos adversaires (via mail, 
messages priv�s ou chat int�gr�) pour 
lancer des actions communes.
</p>
<p>
Si tout se passe bien pour vous, vous d�collerez � la fin de la partie avec des v�hicules 
(1 point chacun) et des minerais (2 points chacun). Le joueur totalisant le plus grand nombre 
de points aura gagn�.
</p>

<h2>Copies d'�crans</h2>

<a href="/images/screenshots/tutorial1.jpg" >
    <img src="/images/screenshots/min/tutorial1.jpg" /> 
</a>
<a href="/images/screenshots/tutorial2.jpg" >
    <img src="/images/screenshots/min/tutorial2.jpg" />
</a>

<h2>Pour d�marrer</h2>
<p>
Le moyen le plus simple pour avoir un bon aper�u et s'initier au jeu est de faire le 
<a href="/game.jsp?id=/puzzles/tutorial/model.bin" >tutorial</a>.
Il ne dure qu'une dizaine de minutes et vous n'avez pas besoin de cr�er de compte.
</p>
<p>
Par la suite, vous pouvez tenter les quelques probl�mes propos�s ou vous inscrire &agrave; une
partie regroupant des d�butants. (Pour s'inscrire cliquez sur 
<img src="/puzzles/tutorial/images/Register32.png" alt="Register" />)
</p>

<h2>Pour initier une partie</h2>
<p>
Il est possible qu'une petite explication vous soit utile pour la cr�ation d'une nouvelle partie.
Pour cela jetez un coup d'&oelig;il <a href="help/newgame.jsp">ici</a>.
</p>

<%@include file="include/footer.jsp"%>
</body>
</HTML>