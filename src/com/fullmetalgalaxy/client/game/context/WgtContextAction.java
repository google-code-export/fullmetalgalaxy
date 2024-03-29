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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.context;


import java.util.ArrayList;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.AppRoot;
import com.fullmetalgalaxy.client.MAppMessagesStack;
import com.fullmetalgalaxy.client.event.ModelUpdateEvent;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.DlgJoinGame;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.widget.WgtView;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EventBuilderMsg;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author Vincent Legendre
 *
 */

public class WgtContextAction extends WgtView implements ClickHandler
{
  HorizontalPanel m_panel = new HorizontalPanel();
  PushButton m_btnOk = new PushButton( new Image( Icons.s_instance.ok32() ) );
  PushButton m_btnCancel = new PushButton( new Image( Icons.s_instance.cancel32() ) );
  PushButton m_btnRepairTurret = new PushButton( new Image( Icons.s_instance.repair32() ) );
  PushButton m_btnFire = new PushButton( new Image( Icons.s_instance.shoot32() ) );
  PushButton m_btnControl = new PushButton( new Image( Icons.s_instance.control32() ) );
  PushButton m_btnFireCoverOn = new PushButton( new Image( Icons.s_instance.fireCoverOn32() ) );
  PushButton m_btnFireCoverOff = new PushButton( new Image( Icons.s_instance.fireCoverOff32() ) );
  PushButton m_btnEndTurn = new PushButton( new Image( Icons.s_instance.endTurn32() ) );
  PushButton m_btnZoomIn = new PushButton( new Image( Icons.s_instance.zoomIn32() ) );
  PushButton m_btnZoomOut = new PushButton( new Image( Icons.s_instance.zoomOut32() ) );
  PushButton m_btnGridOn = new PushButton( new Image( Icons.s_instance.gridOn32() ) );
  PushButton m_btnGridOff = new PushButton( new Image( Icons.s_instance.gridOff32() ) );
  PushButton m_btnRegister = new PushButton( new Image( Icons.s_instance.register32() ) );
  PushButton m_btnPractice = new PushButton( new Image( Icons.s_instance.practice32() ) );
  PushButton m_btnPracticeOk = new PushButton( new Image( Icons.s_instance.ok32() ) );
  PushButton m_btnPracticeCancel = new PushButton( new Image( Icons.s_instance.cancel32() ) );
  private int m_actionIndexBeforePracticeMode = 0;


  FocusPanel m_pnlRegister = null;
  FocusPanel m_pnlWait = null;
  FocusPanel m_pnlLand = null;
  FocusPanel m_pnlDeploy = null;
  FocusPanel m_pnlPause = null;
  FocusPanel m_pnlEndTurn = null;
  FocusPanel m_pnlTakeOff = null;
  FocusPanel m_pnlPractice = null;
  PushButton m_btnTakeOff = new PushButton( new Image( Icons.s_instance.takeOff32() ) );
  Image m_iconAction = new Image( Icons.s_instance.action16() );
  Label m_lblAction = new Label( "" );

  /**
   * 
   */
  public WgtContextAction()
  {
    m_btnOk.addClickHandler( this );
    m_btnOk.setTitle( MAppBoard.s_messages.validAction() );
    m_btnOk.setStyleName( "fmp-PushButton32" );
    m_btnCancel.addClickHandler( this );
    m_btnCancel.setTitle( MAppBoard.s_messages.cancelAction() );
    m_btnCancel.setStyleName( "fmp-PushButton32" );
    m_btnRepairTurret.addClickHandler( this );
    m_btnRepairTurret.setTitle( MAppBoard.s_messages.repairTurret() );
    m_btnRepairTurret.setStyleName( "fmp-PushButton32" );
    m_btnTakeOff.addClickHandler( this );
    m_btnTakeOff.setTitle( MAppBoard.s_messages.takeOff() );
    m_btnTakeOff.setStyleName( "fmp-PushButton32" );
    m_btnFire.addClickHandler( this );
    m_btnFire.setTitle( MAppBoard.s_messages.fire() );
    m_btnFire.setStyleName( "fmp-PushButton32" );
    m_btnControl.addClickHandler( this );
    m_btnControl.setTitle( MAppBoard.s_messages.control() );
    m_btnControl.setStyleName( "fmp-PushButton32" );
    m_btnFireCoverOn.addClickHandler( this );
    m_btnFireCoverOn.setTitle( MAppBoard.s_messages.displayFireCover() );
    m_btnFireCoverOn.setStyleName( "fmp-PushButton32" );
    m_btnFireCoverOff.addClickHandler( this );
    m_btnFireCoverOff.setTitle( MAppBoard.s_messages.hideFireCover() );
    m_btnFireCoverOff.setStyleName( "fmp-PushButton32" );
    m_btnEndTurn.addClickHandler( this );
    m_btnEndTurn.setTitle( MAppBoard.s_messages.endTurn() );
    m_btnEndTurn.setStyleName( "fmp-PushButton32" );
    m_btnZoomIn.addClickHandler( this );
    m_btnZoomIn.setTitle( MAppBoard.s_messages.tacticalZoom() );
    m_btnZoomIn.setStyleName( "fmp-PushButton32" );
    m_btnZoomOut.addClickHandler( this );
    m_btnZoomOut.setTitle( MAppBoard.s_messages.strategyZoom() );
    m_btnZoomOut.setStyleName( "fmp-PushButton32" );
    m_btnGridOn.addClickHandler( this );
    m_btnGridOn.setTitle( MAppBoard.s_messages.displayGrid() );
    m_btnGridOn.setStyleName( "fmp-PushButton32" );
    m_btnGridOff.addClickHandler( this );
    m_btnGridOff.setTitle( MAppBoard.s_messages.hideGrid() );
    m_btnGridOff.setStyleName( "fmp-PushButton32" );
    m_btnRegister.addClickHandler( this );
    m_btnRegister.setTitle( MAppBoard.s_messages.joinGame() );
    m_btnRegister.setStyleName( "fmp-PushButton32" );
    m_btnPractice.addClickHandler( this );
    m_btnPractice.setTitle( MAppBoard.s_messages.trainningMode() );
    m_btnPractice.setStyleName( "fmp-PushButton32" );
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.register32()) );
    hPanel.add( new Label( MAppBoard.s_messages.joinThisGame() ) );
    m_pnlRegister = new FocusPanel( hPanel );
    m_pnlRegister.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.pause32()) );
    hPanel.add( new Label( MAppBoard.s_messages.waitGameStarting() ) );
    m_pnlWait = new FocusPanel( hPanel );
    hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.takeOff32()) );
    hPanel.add( new Label( MAppBoard.s_messages.mustLandFreighter() ) );
    m_pnlLand = new FocusPanel( hPanel );
    m_pnlLand.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.takeOff32()) );
    hPanel.add( new Label( MAppBoard.s_messages.mustDeployUnits() ) );
    m_pnlDeploy = new FocusPanel( hPanel );
    m_pnlDeploy.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.pause32()) );
    hPanel.add( new Label( MAppBoard.s_messages.pauseGameAllowNewPlayer() ) );
    m_pnlPause = new FocusPanel( hPanel );
    hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.endTurn32()) );
    hPanel.add( new Label( MAppBoard.s_messages.mustEndYourTurn() ) );
    m_pnlEndTurn = new FocusPanel( hPanel );
    m_pnlEndTurn.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.takeOff32()) );
    hPanel.add( new Label( MAppBoard.s_messages.SelectFreighterToTakeOff() ) );
    m_pnlTakeOff = new FocusPanel( hPanel );
    m_pnlTakeOff.addClickHandler( this );
    hPanel = new HorizontalPanel();
    hPanel.add( new Image( Icons.s_instance.practice32()) );
    hPanel.add( new Label( MAppBoard.s_messages.trainningMode() ) );
    hPanel.add( m_btnPracticeCancel );
    m_btnPracticeCancel.addClickHandler( this );
    hPanel.add( m_btnPracticeOk );
    m_btnPracticeOk.addClickHandler( this );
    m_pnlPractice = new FocusPanel( hPanel );


    m_iconAction.setTitle( MAppBoard.s_messages.costInPA() );
    m_lblAction.setStyleName( "fmp-status-text" );

    // subscribe all needed models update event
    AppRoot.getEventBus().addHandler( ModelUpdateEvent.TYPE, this );

    m_panel.setSize( "100%", "100%" );
    m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    m_panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    initWidget( m_panel );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    Object sender = p_event.getSource();
    try
    {
      EventsPlayBuilder actionBuilder = GameEngine.model().getActionBuilder();
      if( sender == m_btnOk )
      {
        actionBuilder.userOk();
        GameEngine.model().runCurrentAction();
      }
      else if( sender == m_btnCancel )
      {
        actionBuilder.userCancel();
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
      }
      else if( sender == m_btnRepairTurret )
      {
        EventBuilderMsg eventBuilderMsg = actionBuilder.userAction( GameLogType.EvtRepair );
        if( eventBuilderMsg == EventBuilderMsg.MustRun )
        {
          GameEngine.model().runSingleAction( actionBuilder.getSelectedAction() );
        }
      }
      else if( sender == m_btnFire )
      {
        actionBuilder.userAction( GameLogType.EvtFire );
        if( ((EbEvtFire)actionBuilder.getSelectedAction()).getTokenTarget( GameEngine.model()
            .getGame() ) == null )
        {
          MAppMessagesStack.s_instance
              .showMessage( MAppBoard.s_messages.selectDestroyerTarget() );
        }
        else
        {
          MAppMessagesStack.s_instance.showMessage( MAppBoard.s_messages.selectTwoDestroyers() );
        }
      }
      else if( sender == m_btnControl )
      {
        actionBuilder.userAction( GameLogType.EvtControl );
        if( ((EbEvtFire)actionBuilder.getSelectedAction()).getTokenTarget( GameEngine.model()
            .getGame() ) == null )
        {
          MAppMessagesStack.s_instance
              .showMessage( MAppBoard.s_messages.selectDestroyerTarget() );
        }
        else
        {
          MAppMessagesStack.s_instance
              .showMessage( MAppBoard.s_messages.selectTwoDestroyers() );
        }
      }
      else if( sender == m_btnFireCoverOn )
      {
        GameEngine.model().setFireCoverDisplayed( true );
      }
      else if( sender == m_btnFireCoverOff )
      {
        GameEngine.model().setFireCoverDisplayed( false );
      }
      else if( sender == m_btnGridOn )
      {
        GameEngine.model().setGridDisplayed( true );
      }
      else if( sender == m_btnGridOff )
      {
        GameEngine.model().setGridDisplayed( false );
      }
      else if( sender == m_btnZoomIn )
      {
        GameEngine.model().setZoomDisplayed( EnuZoom.Medium );
      }
      else if( sender == m_btnZoomOut )
      {
        GameEngine.model().setZoomDisplayed( EnuZoom.Small );
      }
      else if( sender == m_btnRegister || sender == m_pnlRegister )
      {
        DlgJoinGame.instance().show();
        DlgJoinGame.instance().center();
      }
      else if( sender == m_btnPractice || sender == m_btnPracticeCancel )
      {
        if( GameEngine.model().getGame().getGameType() == GameType.MultiPlayer
            || GameEngine.model().getGame().getGameType() == GameType.Initiation )
        {
          Window
              .alert( MAppBoard.s_messages.activateTrainningMode() );
          m_actionIndexBeforePracticeMode = GameEngine.model().getGame().getLogs().size();
          GameEngine.model().getGame().setGameType( GameType.Practice );
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
        }
        else
        {
          // Window.alert( MAppBoard.s_messages.deactivateTrainningMode() );
          // ClientUtil.reload();
          // cancel locally
          EbEvtCancel evtCancel = new EbEvtCancel();
          evtCancel.setGame( GameEngine.model().getGame() );
          evtCancel.setFromActionIndex( GameEngine.model().getGame().getLogs().size()
              + GameEngine.model().getGame().getAdditionalEventCount()
              + GameEngine.model().getMyRegistration().getTeam( GameEngine.model().getGame() )
                  .getMyEvents().size() - 1 );
          evtCancel.setToActionIndex( GameEngine.model().getGame().getAdditionalEventCount() 
              + m_actionIndexBeforePracticeMode );
          evtCancel.setAccountId( AppMain.instance().getMyAccount().getId() );
          GameEngine.model().runSingleAction( evtCancel );
          GameEngine.model().getGame().setGameType( GameType.MultiPlayer );
          MAppMessagesStack.s_instance.removeMessage( m_pnlPractice );
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent( GameEngine.model() ) );
        }
      }
      else if( sender == m_btnPracticeOk )
      {
        // after playing offline, user want to commit his turn to server
        // put all actions to event builder
        GameEngine.model().getActionBuilder().clear();
        int iAction = m_actionIndexBeforePracticeMode;
        ArrayList<AnEventPlay> eventsBackup = new ArrayList<AnEventPlay>();
        while( GameEngine.model().getGame().getLogs().size() > iAction )
        {
          AnEvent event = GameEngine.model().getGame().getLogs().get( iAction );
          if( event instanceof AnEventPlay )
          {
            eventsBackup.add( (AnEventPlay)event );
          }
          iAction++;
        }
        // cancel locally
        EbEvtCancel evtCancel = new EbEvtCancel();
        evtCancel.setGame( GameEngine.model().getGame() );
        evtCancel.setFromActionIndex( GameEngine.model().getGame().getLogs().size()
            + GameEngine.model().getGame().getAdditionalEventCount()
            + GameEngine.model().getMyRegistration().getTeam( GameEngine.model().getGame() )
                .getMyEvents().size() - 1 );
        evtCancel.setToActionIndex( m_actionIndexBeforePracticeMode );
        evtCancel.setAccountId( AppMain.instance().getMyAccount().getId() );
        GameEngine.model().runSingleAction( evtCancel );
        // finally send actions to server
        GameEngine.model().getGame().setGameType( GameType.MultiPlayer );
        GameEngine.model().getActionBuilder().getActionList().addAll( eventsBackup );
        GameEngine.model().runCurrentAction();
        MAppMessagesStack.s_instance.removeMessage( m_pnlPractice );
      }
      else if( sender == m_btnEndTurn || sender == m_pnlEndTurn )
      {
        String msg = null;
        int oldPt = GameEngine.model().getMyRegistration().getPtAction();
        int newPt = GameEngine.model().getMyRegistration().getRoundedActionPt(GameEngine.model().getGame());
        if( oldPt == newPt )
        {
          msg = MAppBoard.s_messages.confirmEndTurn( oldPt );
        }
        else
        {
          msg = MAppBoard.s_messages.confirmEndTurnRoundedPA( oldPt, newPt );
        }
        if( Window.confirm( msg ) )
        {
          EbEvtPlayerTurn action = new EbEvtPlayerTurn();
          action.setGame( GameEngine.model().getGame() );
          action.setAccountId( AppMain.instance().getMyAccount().getId() );
          GameEngine.model().runSingleAction( action );
        }
      }
      else if( sender == m_btnTakeOff )
      {
        if( Window.confirm( MAppBoard.s_messages.confirmTakeOff( Messages.getTokenString( 0, actionBuilder.getSelectedToken() ) ) ) )
        {
          EbEvtTakeOff action = new EbEvtTakeOff();
          action.setGame( GameEngine.model().getGame() );
          action.setRegistration( GameEngine.model().getMyRegistration() );
          action.setToken( actionBuilder.getSelectedToken() );
          GameEngine.model().runSingleAction( action );
        }
      }
      else if( sender == m_pnlLand )
      {
        // search for my freighter to land
        GameEngine
            .model()
            .getActionBuilder()
            .userTokenClick(
                GameEngine.model().getGame()
                    .getFreighter( GameEngine.model().getMyRegistration() ) );
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
      }
      else if( sender == m_pnlDeploy )
      {
        // search for any unit to deploy
        EbToken myFreighter = GameEngine.model().getGame()
            .getFreighter( GameEngine.model().getMyRegistration() );
        if( myFreighter.containToken() )
        {
          EbToken firstToken = myFreighter.getContains().iterator().next();
          GameEngine.model().getActionBuilder().userTokenClick( firstToken );
          AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
        }
      }
      else if( sender == m_pnlTakeOff )
      {
        GameEngine.model().getActionBuilder().clear();
        // search for my freighter to take off
        for( EbToken token : GameEngine.model().getGame()
            .getAllFreighter( GameEngine.model().getMyRegistration().getColor() ) )
        {
          if( token.getLocation() == Location.Board )
          {
            GameEngine.model().getActionBuilder().userTokenClick( token );
            break;
          }
        }
        AppRoot.getEventBus().fireEvent( new ModelUpdateEvent(GameEngine.model()) );
      }
    } catch( RpcFmpException e )
    {
      MAppMessagesStack.s_instance.showWarning( e.getLocalizedMessage() );
    }
  }


  protected void redraw()
  {
    m_panel.clear();
    GameEngine model = GameEngine.model();
    if( (model == null) )
    {
      return;
    }

    EventsPlayBuilder action = GameEngine.model().getActionBuilder();
    EbToken mainSelectedToken = action.getSelectedToken();
    EbRegistration myRegistration = model.getMyRegistration();


    if( !action.isTokenSelected() || GameEngine.model().getGame().isFinished() )
    {
      // add standard actions icon set
      if( GameEngine.model().isFireCoverDisplayed() )
      {
        m_panel.add( m_btnFireCoverOff );
      }
      else
      {
        m_panel.add( m_btnFireCoverOn );
      }
      if( GameEngine.model().isGridDisplayed() )
      {
        m_panel.add( m_btnGridOff );
      }
      else
      {
        m_panel.add( m_btnGridOn );
      }
      if( GameEngine.model().getZoomDisplayed().getValue() == EnuZoom.Small )
      {
        m_panel.add( m_btnZoomIn );
      }
      else
      {
        m_panel.add( m_btnZoomOut );
      }
      if( !GameEngine.model().isTimeLineMode() )
      {
        // display wait panel advise
        if( (myRegistration != null) && (model.getGame().getCurrentTimeStep() <= 0)
            && (model.getGame().getStatus() == GameStatus.Open || model.getGame().getStatus() == GameStatus.Pause) )
        {
          MAppMessagesStack.s_instance.showMessage( m_pnlWait );
        }
        // display end turn button ?
        if( (!GameEngine.model().getGame().isParallel()) && (myRegistration != null)
            && (GameEngine.model().getGame().getCurrentPlayerIds().contains( myRegistration.getId()))
            && (model.getGame().getStatus() == GameStatus.Running)
            && (model.getGame().getGameType() != GameType.Practice) )
        {
          m_panel.add( m_btnEndTurn );

          // and even end turn panel !?
          if( myRegistration.getPtAction() <= 0 )
          {
            
            if( GameEngine.model().getGame().getCurrentTimeStep() > GameEngine.model()
                .getGame().getEbConfigGameTime().getDeploymentTimeStep() )
            {
              MAppMessagesStack.s_instance.showMessage( m_pnlEndTurn );
            }
            else 
            {
              EbToken myFreighter = GameEngine.model().getGame().getFreighter( myRegistration );
              if( (GameEngine.model().getGame().getCurrentTimeStep() < GameEngine.model()
                .getGame().getEbConfigGameTime().getDeploymentTimeStep())
                && (myFreighter.getLocation() == Location.Board) )
              {
                MAppMessagesStack.s_instance.showMessage( m_pnlEndTurn );
              }
              else if( (GameEngine.model().getGame().getCurrentTimeStep() == GameEngine.model()
                  .getGame().getEbConfigGameTime().getDeploymentTimeStep()) )
              {
                if( !myFreighter.containToken() )
                {
                  MAppMessagesStack.s_instance.showMessage( m_pnlEndTurn );
                }
                else
                {
                  MAppMessagesStack.s_instance.showMessage( m_pnlDeploy );
                }
              }
            }
          }
        }
        
        // display register icon and advise
        if( GameEngine.model().isLogged()
            && myRegistration == null
            && (model.getGame().getStatus() == GameStatus.Open || model.getGame().getStatus() == GameStatus.Pause)
            && GameEngine.model().getGame().getMaxNumberOfPlayer() > GameEngine.model()
                .getGame().getCurrentNumberOfRegiteredPlayer() )
        {
          m_panel.add( m_btnRegister );
          MAppMessagesStack.s_instance.showMessage( m_pnlRegister );
        }
        
        // should we display pause to allow subscription advise ?
        if( (GameEngine.model().getGame().getCurrentNumberOfRegiteredPlayer() < GameEngine.model().getGame().getMaxNumberOfPlayer())
            && (GameEngine.model().getGame().getStatus() == GameStatus.Running) )
        {
          MAppMessagesStack.s_instance.showMessage( m_pnlPause );
        }
        
        // should we display landing advise ?
        if( GameEngine.model().getGame().getCurrentTimeStep() < GameEngine.model()
            .getGame().getEbConfigGameTime().getDeploymentTimeStep())
        {
          EbToken myFreighter = GameEngine.model().getGame().getFreighter( myRegistration );
          if( myFreighter != null
              && model.getGame().getStatus() == GameStatus.Running
              && myFreighter.getLocation() == Location.Orbit
              && myRegistration != null
              && (GameEngine.model().getGame().getCurrentPlayerIds().contains( myRegistration
                  .getId() )) )
          {
            MAppMessagesStack.s_instance.showMessage( m_pnlLand );
          }
        }

        // should we display take off advise ?
        if( (model.getGame().getAllowedTakeOffTurns().contains( model.getGame()
            .getCurrentTimeStep() ))
            && myRegistration != null
            && model.getGame().getCurrentPlayerIds().contains( myRegistration.getId() ) )
        {
          MAppMessagesStack.s_instance.showMessage( m_pnlTakeOff );
        }

        // display practice icon and advise
        if( GameEngine.model().getGame().getGameType() == GameType.Practice )
        {
          MAppMessagesStack.s_instance.showPersitentMessage( m_pnlPractice );
        }
        else if( GameEngine.model().getGame().getGameType() == GameType.MultiPlayer
            || GameEngine.model().getGame().getGameType() == GameType.Initiation )
        {
          m_panel.add( m_btnPractice );
        }
      }
    }
    else if( myRegistration == null )
    {
      m_panel.add( m_btnCancel );
    }
    else if( action.isRunnable() )
    {
      m_panel.add( m_btnOk );
      m_panel.add( m_iconAction );
      m_lblAction.setText( String.valueOf( GameEngine.model().getActionBuilder().getCost() ) );
      m_panel.add( m_lblAction );
      m_panel.add( m_btnCancel );
    }
    else
    {
      try
      {
        action.check();
      } catch( RpcFmpException e )
      {
        if( (e.getMessage() != null) && !(e.getMessage().trim().length() == 0) )
        {
          MAppMessagesStack.s_instance.showWarning( e.getMessage() );
        }
      }

      // add current actions icon set
      if( (action.isTokenSelected()) || (action.isActionsPending())
          || (action.getActionList().size() != 0) )
      {
        // add cancel button
        m_panel.add( m_btnCancel );
      }

      if( !mainSelectedToken.getEnuColor().contain( myRegistration.getColor() ) )
      {
        if( mainSelectedToken.canBeATarget( GameEngine.model().getGame() ) )
        {
          m_panel.add( m_btnFire );
        }
      }
      else if( ((GameEngine.model().getGame().getTokenFireLength( mainSelectedToken ) > 0) || (mainSelectedToken
          .getType() == TokenType.Freighter && GameEngine.model().getGame()
          .getToken( action.getSelectedPosition(), TokenType.Turret ) != null)) )
      {
        try
        {
          action.exec();
          if( (mainSelectedToken.getBulletCount() > 0)
              || (mainSelectedToken.getType() == TokenType.Freighter) )
          {
            m_panel.add( m_btnFire );
          }
        } catch( RpcFmpException e )
        {
        } finally
        {
          try
          {
            action.unexec();
          } catch( RpcFmpException e )
          {
          }
        }
      }
      if( action.getSelectedToken().canControlNeighbor( GameEngine.model().getGame(),
          action.getSelectedPosition() ) )
      {
        m_panel.add( m_btnControl );
      }

      if( (action.isBoardTokenSelected()) && (!action.isActionsPending())
          && (mainSelectedToken.getType() == TokenType.Freighter) && (myRegistration != null)
          && (mainSelectedToken.getBulletCount() > 0) && (myRegistration.getPtAction() >= 2)
          && (myRegistration.getEnuColor().isColored( mainSelectedToken.getColor() ))
          && (model.getGame().getToken( action.getSelectedPosition(), TokenType.Turret ) == null)
          && (!mainSelectedToken.getPosition().equals( action.getSelectedPosition() )) )
      {
        // player select a destroyed pod. of a freighter he own
        // add the repair turret button
        m_panel.add( m_btnRepairTurret );
      }

      if( (model.getGame().getAllowedTakeOffTurns().contains( model.getGame().getCurrentTimeStep() )) )
      {
        if( (action.isBoardTokenSelected()) && (!action.isActionsPending())
            && (mainSelectedToken.getType() == TokenType.Freighter)
            && (mainSelectedToken.getEnuColor().contain( myRegistration.getColor() )) )
        {
          m_panel.add( m_btnTakeOff );
        }
      }
    }

  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(GameEngine p_ModelSender)
  {
    redraw();
  }


}
