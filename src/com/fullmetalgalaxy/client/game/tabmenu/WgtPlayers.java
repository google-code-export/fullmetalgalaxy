/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.game.tabmenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.chat.DlgChatInput;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminBan;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author vlegendr
 *
 */
public class WgtPlayers extends Composite implements ClickHandler
{
  private Map<Widget, EbRegistration> m_banButtons = new HashMap<Widget, EbRegistration>();
  private PushButton m_btnSkipTurn = new PushButton( Icons.s_instance.endTurn32().createImage() );
  private Panel m_playerPanel = new FlowPanel();
  
  private Button m_btnChat = new Button( "chat" );
  
  public WgtPlayers()
  {
    super();
    
    m_btnChat.addClickHandler( this );
    m_btnSkipTurn.setTitle( "Fin de tour" );
    m_btnSkipTurn.setStyleName( "fmp-PushButton32" );
    m_btnSkipTurn.addClickHandler( this );
    
    initPlayerPanel();
    initWidget( m_playerPanel );
  }
  
  private void initPlayerPanel()
  {
    m_banButtons.clear();
    m_playerPanel.clear();
    int PlayerCount = GameEngine.model().getGame().getSetRegistration().size();
    m_playerPanel.add( new Label( MAppBoard.s_messages.xPlayers( PlayerCount ) ) );

    // message to all link
    String pseudoList[] = new String[PlayerCount];
    int i = 0;
    for( EbRegistration registration : GameEngine.model().getGame().getSetRegistration() )
    {
      if( registration.getAccount() != null )
      {
        pseudoList[i] = registration.getAccount().getPseudo();
      }
      else
      {
        pseudoList[i] = "";
      }
      i++;
    }
    m_playerPanel.add( new HTML( "<a href='"
        + EbPublicAccount
            .getPMUrl( "[FMG] " + GameEngine.model().getGame().getName(), pseudoList )
        + "' >Envoyer un message à tous</a>" ) );

    // get player order
    List<EbRegistration> sortedRegistration = GameEngine.model().getGame()
        .getRegistrationByPlayerOrder();


    Grid m_playerGrid = new Grid( sortedRegistration.size() + 1, 9 );
    m_playerGrid.setStyleName( "fmp-array" );

    m_playerGrid.setText( 0, 0, "" ); // avatar
    m_playerGrid.setText( 0, 1, "login" );
    m_playerGrid.setText( 0, 2, "couleur(s)" );
    m_playerGrid.setText( 0, 3, "pt d'action" );
    m_playerGrid.setHTML( 0, 4, "pt de victoire<br/>(estimation)" );
    m_playerGrid.setText( 0, 5, "" ); // must play before
    m_playerGrid.setText( 0, 6, "" ); // messages
    m_playerGrid.setText( 0, 7, "" ); // ban
    m_playerGrid.setText( 0, 8, "" ); // skip turn
    m_playerGrid.getRowFormatter().addStyleName( 0, "fmp-home-gameline-caption" );

    int index = 0;
    for( EbRegistration registration : sortedRegistration )
    {
      index++;

      String html = "";
      if( registration.haveAccount() )
      {
        // display avatar
        m_playerGrid.setHTML( index, 0, "<IMG SRC='" + registration.getAccount().getAvatarUrl()
            + "' WIDTH=60 HEIGHT=60 BORDER=0 />" );
        
        // display login
        // if player is connected, display in bold font
        if( AppMain.instance().isUserConnected( registration.getAccount().getPseudo() ) )
        {
          html += "<b>";
        }
        String login = registration.getAccount().getPseudo();
        html += "<a href='" + registration.getAccount().getProfileUrl() + "' target='_blank'>"
            + login + "</a>";
        if( AppMain.instance().isUserConnected( registration.getAccount().getPseudo() ) )
        {
          html += "</b>";
        }
      }
      else
      {
        // display avatar
        m_playerGrid.setHTML( index, 0,
            "<IMG SRC='/images/avatar-default.jpg' WIDTH=60 HEIGHT=60 BORDER=0 />" );
        // display login
        html = "???";
      }

      if( GameEngine.model().getGame().getCurrentPlayerRegistration() == registration )
      {
        html += Icons.s_instance.action16().getHTML();
      }
      m_playerGrid.setHTML( index, 1, html );

      // display all colors
      EnuColor color = registration.getEnuColor();
      int colorIndex = 0;
      String htmlColors = "";
      for( colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
      {
        if( color.isColored( EnuColor.getColorFromIndex( colorIndex ) ) )
        {
          htmlColors += BoardIcons.icon16( EnuColor.getColorFromIndex( colorIndex ).getValue() )
              .getHTML();
        }
      }
      if( color.getValue() == EnuColor.None )
      {
        htmlColors += " <IMG SRC='images/board/icon.gif' WIDTH=16 HEIGHT=16 BORDER=0 TITLE='"
            + Messages.getColorString( 0, color.getValue() ) + "'> ";
      }
      m_playerGrid.setHTML( index, 2, htmlColors );

      // display action points
      m_playerGrid
          .setText(
              index,
              3,
              ""  + registration.getPtAction()
                  + "/"
                  + (GameEngine.model().getGame().getEbConfigGameVariant()
                      .getActionPtMaxReserve() + ((registration.getEnuColor().getNbColor() - 1) * GameEngine
                      .model().getGame().getEbConfigGameVariant().getActionPtMaxPerExtraShip())) );

      // display Wining points
      m_playerGrid.setText( index, 4, "" + registration.estimateWinningScore(GameEngine.model().getGame()) );

      // display 'must play before'
      if( (!GameEngine.model().getGame().isAsynchron())
          && (registration.getEndTurnDate() != null) )
      {
        m_playerGrid.setText( 0, 5, "doit jouer avant" );
        m_playerGrid.setText( index, 5, ClientUtil.s_dateTimeFormat.format( registration
            .getEndTurnDate() ) );
      }

      // display email messages
      String htmlMail = "";
      if( registration.getAccount() != null )
      {
        htmlMail = "<a target='_blank' href='" + registration.getAccount().getPMUrl()
            + "'><img src='" + "/images/css/icon_pm.gif' border=0 alt='PM'></a>";
      }
      m_playerGrid.setHTML( index, 6, htmlMail );

      // display admin button
      if( (GameEngine.model().getGame().getAccountCreator() != null
          && AppMain.instance().getMyAccount().getId() == GameEngine.model().getGame()
          .getAccountCreator().getId()) || AppMain.instance().iAmAdmin() ) 
      {
        if( registration.haveAccount() )
        {
          // display ban button
          Image banImage = new Image();
          banImage.setUrl( "/images/icons/ban.gif" );
          banImage.setAltText( "BAN" );
          banImage.setTitle( "Banir un joueur de cette partie" );
          banImage.addClickHandler( this );
          m_playerGrid.setWidget( index, 7, banImage );
          m_banButtons.put( banImage, registration );
        }
        
        // display endTurn button
        if( (GameEngine.model().getGame().getCurrentPlayerRegistration() == registration) )
        {
          m_playerGrid.setWidget( index, 8, m_btnSkipTurn );
        }
        
      }
      
    }

    m_playerPanel.add( m_playerGrid );
    
    
    // come from old WgtContextPlayers
    //
    Game game = GameEngine.model().getGame();
    if( game.getGameType() == GameType.MultiPlayer )
    {
      VerticalPanel vpanel = new VerticalPanel();

      // other connected User
      vpanel.add( new Label( "Visiteur(s) :" ) );
      for( Presence user : AppMain.instance().getPresenceRoom() )
      {
        if( !contain( sortedRegistration, user.getPseudo() ) )
        {
          HTML html = new HTML( "<b>" + user.getPseudo() + "</b>" );
          html.setWidth( "100%" );
          vpanel.add( html );
        }
      }
      if( game.getGameType() == GameType.MultiPlayer )
      {
        vpanel.add( m_btnChat );
        vpanel.add( new HTML("<a href='/chat.jsp?id="+game.getId()+"' target='_blank'><img src='/images/icon_new_window.gif'/></a>") );
      }
      m_playerPanel.add( vpanel );
    }
  }

  private boolean contain(List<EbRegistration> p_players, String p_pseudo)
  {
    for( EbRegistration player : p_players )
    {
      if( player != null && player.haveAccount()
          && player.getAccount().getPseudo().equalsIgnoreCase( p_pseudo ) )
      {
        return true;
      }
    }
    return false;
  }




  

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnChat )
    {
      DlgChatInput.showDialog();
    }
    else if( p_event.getSource() == m_btnSkipTurn )
    {
      EbRegistration registration = GameEngine.model().getGame().getCurrentPlayerRegistration();
      if( Window.confirm( "Voulez-vous réellement sauter le tour de "
          + registration.getAccount().getPseudo()
          + ", il lui reste "+registration.getPtAction()+" points d'action.") )
      {
        EbEvtPlayerTurn action = new EbEvtPlayerTurn();
        action.setGame( GameEngine.model().getGame() );
        action.setAccountId( AppMain.instance().getMyAccount().getId() );
        // ok itsn't an automatic action, but with this trick I can track of the guy which
        // end this turn and pass through action checking
        action.setAuto( true );
        GameEngine.model().runSingleAction( action );
      }
    }
    else if( m_banButtons.get( p_event.getSource() ) != null )
    {
      // want to ban player
      EbRegistration registration = m_banButtons.get( p_event.getSource() );
      if( Window.confirm( "Voulez-vous réellement banir " + registration.getAccount().getPseudo()
          + " de la partie " + GameEngine.model().getGame().getName() ) )
      {
        EbAdminBan gameLog = new EbAdminBan();
        gameLog.setAccountId( AppMain.instance().getMyAccount().getId() );
        gameLog.setRegistrationId( registration.getId() );
        gameLog.setGame( GameEngine.model().getGame() );
        GameEngine.model().runSingleAction( gameLog );
      }
    }
  }


}