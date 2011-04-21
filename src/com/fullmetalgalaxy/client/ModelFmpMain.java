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
package com.fullmetalgalaxy.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fullmetalgalaxy.client.board.DlgMessageEvent;
import com.fullmetalgalaxy.client.board.MAppMessagesStack;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.ChatService;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.ModelUpdateListener;
import com.fullmetalgalaxy.model.ModelUpdateListenerCollection;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.Presence.ClientType;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtMessage;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;


/**
 * @author Vincent Legendre
 *
 */
public class ModelFmpMain implements SourceModelUpdateEvents, Window.ClosingHandler
{
  private static ModelFmpMain s_ModelFmpMain = new ModelFmpMain();

  /**
   * @return a unique instance of the model on client side
   */
  public static ModelFmpMain model()
  {
    return s_ModelFmpMain;
  }

  protected String m_gameId = null;
  protected Game m_game = new Game();

  /**
   * the list of all widget which will be refreshed after any model change. 
   */
  protected ModelUpdateListenerCollection m_listenerCollection = new ModelUpdateListenerCollection();

  protected EventsPlayBuilder m_actionBuilder = new EventsPlayBuilder();

  protected EbPublicAccount m_myAccount = new EbPublicAccount();

  protected boolean m_myAccountAdmin = false;

  // connected players (or any other peoples)
  protected PresenceRoom m_connectedUsers = new PresenceRoom( 0 );


  // interface
  private boolean m_isGridDisplayed = false;

  private boolean m_isFireCoverDisplayed = false;

  private EnuZoom m_zoomDisplayed = new EnuZoom( EnuZoom.Medium );
  // cloud layer
  private boolean m_isAtmosphereDisplayed = true;
  // standard land layer or custom map image
  private boolean m_isCustomMapDisplayed = false;

  /**
   * minimap or players connections informations
   */
  private boolean m_isMiniMapDisplayed = true;

  /**
   * if set, user can't do anything else:
   * - navigate in past actions
   * - exiting this mode
   * - if game is puzzle or standard (turn by turn, no time limit) validate to cancel some actions
   */
  private boolean m_isTimeLineMode = false;
  private int m_currentActionIndex = 0;
  /** game currentTimeStep at the moment we start time line mode */
  private int m_lastTurnPlayed = 0;
  

  private int m_successiveRpcErrorCount = 0;

  /**
   * @see ModelFmpInit.m_channelToken
   */
  private String m_channelToken = null;
  private int m_pageId = 0;
  private boolean m_isChannelConnected = false;



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.SourceModelUpdateEvents#subscribeModelUpdateEvent(com.fullmetalgalaxy.client.ModelUpdateListener)
   */
  @Override
  public void subscribeModelUpdateEvent(ModelUpdateListener p_listener)
  {
    m_listenerCollection.add( p_listener );
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.SourceModelUpdateEvents#removeModelUpdateEvent(com.fullmetalgalaxy.model.ModelUpdateListener)
   */
  @Override
  public void removeModelUpdateEvent(ModelUpdateListener p_listener)
  {
    m_listenerCollection.remove( p_listener );
  }

  /**
   * This method call 'notifyModelUpdate()' of all childs and father controller.
   * You should call this method every time the model associated with this controller is updated.
   * the sub method should first call 'super.notifyModelUpdate()'
   */
  public void notifyModelUpdate()
  {
    // setLastUpdate( new Date() );
    fireModelUpdate();
  }

  public void fireModelUpdate()
  {
    m_listenerCollection.fireModelUpdate( this );
  }

  /**
   * 
   */
  public ModelFmpMain()
  {
    // disconnect if leaving this page
    Window.addWindowClosingHandler( this );

    loadAccountInfoFromPage();
    getActionBuilder().setAccountId( getMyAccount().getId() );
  }

  public void load(Game p_model)
  {
    if( p_model==null )
    {
      // TODO i18n
      Window.alert( "Partie non trouve...\nVerifier l'url, mais il ce peut qu'elle ai ete suprime" );
      return;
    }
    m_game = p_model;
    getActionBuilder().setGame( getGame() );
    notifyModelUpdate();
    if( p_model.getGameType() != GameType.MultiPlayer )
    {
      LocalGame.loadGame( this );
    }
  }


  public EbRegistration getMyRegistration()
  {
    if( getGame().getGameType() == GameType.Puzzle )
    {
      return getGame().getCurrentPlayerRegistration();
    }
    if( !isLogged() )
    {
      return null;
    }
    for( Iterator<EbRegistration> it = getGame().getSetRegistration().iterator(); it.hasNext(); )
    {
      EbRegistration registration = (EbRegistration)it.next();
      if( registration.haveAccount() 
          && registration.getAccount().getId() == getMyAccount().getId() )
      {
        return registration;
      }
    }
    return null;
  }


  private void loadAccountInfoFromPage()
  {
    getMyAccount().setPseudo( ClientUtil.readGwtPropertyString( "fmp_userpseudo" ) );
    getMyAccount().setId( ClientUtil.readGwtPropertyLong( "fmp_userid" ) );
    m_myAccountAdmin = ClientUtil.readGwtPropertyBoolean( "fmp_useradmin" );
    m_pageId = ClientUtil.readGwtPropertyLong( "fmp_pageid" ).intValue();

    m_channelToken = ClientUtil.readGwtProperty( "fmp_channelToken" );
    if( m_channelToken != null && getGame().getGameType() == GameType.MultiPlayer )
    {
      m_reconnectCallback.onSuccess( m_channelToken );
    }
  }


  public EbPublicAccount getMyAccount()
  {
    return m_myAccount;
  }

  public boolean iAmAdmin()
  {
    return m_myAccountAdmin;
  }



  /**
   * create a new instance of my presence
   * @return
   */
  public Presence getMyPresence()
  {
    Presence presence = new Presence( getMyAccount().getPseudo(), getPresenceRoom().getGameId(),
        getPageId() );
    presence.setClientType( ClientType.GAME );
    return presence;
  }

  public boolean isLogged()
  {
    return getMyAccount().getId() != 0;
  }


  public boolean isJoined()
  {
    return getMyRegistration() != null;
  }

  /**
   * @return the action
   */
  public ArrayList<AnEventPlay> getActionList()
  {
    return getActionBuilder().getActionList();
  }

  public void reinitGame()
  {
    m_game = new Game();
    m_gameId = null;
    notifyModelUpdate();
  }

  public Game getGame()
  {
    return m_game;
  }

  public void setGameId(String p_id)
  {
    m_gameId = p_id;
  }

  public String getGameId()
  {
    if( m_gameId == null )
    {
      return "" + getGame().getId();
    }
    return m_gameId;
  }


  private boolean m_isActionPending = false;

  private FmpCallback<Void> m_callbackEvents = new FmpCallback<Void>()
  {
    @Override
    public void onSuccess(Void p_result)
    {
      if( !m_isChannelConnected
          && ModelFmpMain.model().getGame().getGameType() == GameType.MultiPlayer )
      {
        // we just receive an action acknowledge but we arn't connected with
        // channel. ie we won't receive update event... reload page
        ClientUtil.reload();
      }
      super.onSuccess( p_result );
      m_successiveRpcErrorCount = 0;
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().clear();
      ModelFmpMain.model().notifyModelUpdate();
    }

    @Override
    public void onFailure(Throwable p_caught)
    {
      m_successiveRpcErrorCount++;
      //super.onFailure( p_caught );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      ModelFmpMain.model().notifyModelUpdate();
      // maybe the action failed because the model isn't up to date
      if( m_successiveRpcErrorCount <= 2 )
      {
        if( p_caught instanceof RpcFmpException )
        {
          MAppMessagesStack.s_instance
              .showWarning( Messages.getString( (RpcFmpException)p_caught ) );
        }
      }
      else
      {
        // TODO i18n
        Window.alert( "another error occur: reload page" );
        ClientUtil.reload();
      }
    }
  };

  private AsyncCallback<Void> m_dummyCallback = new AsyncCallback<Void>()
  {
    @Override
    public void onFailure(Throwable p_caught)
    {
    }
    @Override
    public void onSuccess(Void p_result)
    {
    }
  };

  private AsyncCallback<String> m_reconnectCallback = new AsyncCallback<String>()
  {
    @Override
    public void onFailure(Throwable p_caught)
    {
      Window.alert( "server (re)connexion error !!!" );
    }
    @Override
    public void onSuccess(String p_result)
    {
      m_channelToken = p_result;
      if( m_channelToken == null || m_channelToken.equalsIgnoreCase( "null" ) )
      {
        Window.alert( "server (re)connexion error !!!" );
      }
      else
      {
        ChannelFactory.createChannel( m_channelToken, m_callbackChannel );
      }
    }
  };

  private ChannelCreatedCallback m_callbackChannel = new ChannelCreatedCallback()
  {
    @Override
    public void onChannelCreated(Channel channel)
    {
      channel.open( new SocketListener()
      {
        @Override
        public void onOpen()
        {
          m_isChannelConnected = true;
        }

        @Override
        public void onMessage(String message)
        {
          Object object = deserialize( message );

          if( object instanceof ChatMessage )
          {
            receiveChatMessage( (ChatMessage)object );
          }
          else if( object instanceof PresenceRoom )
          {
            receivePresenceRoom( (PresenceRoom)object );
          }
          else if( object instanceof ModelFmpUpdate )
          {
            receiveModelUpdate( (ModelFmpUpdate)object );
          }
          else
          {
            MAppMessagesStack.s_instance.showWarning( "Error: " + message );
          }

        }

        @Override
        public void onError(SocketError error)
        {
          m_isChannelConnected = false;
          MAppMessagesStack.s_instance.showWarning( "Error: " + error.getDescription() );
        }

        @Override
        public void onClose()
        {
          m_isChannelConnected = false;
          // This occur after two hours. in this case, we ask server for a new
          // channel token
          GameServices.Util.getInstance().reconnect( getMyPresence(), m_reconnectCallback );
        }
      } );
    }
  };


  private Object deserialize(String p_serial)
  {
    Object object = null;
    try
    {
      // Decode chat data
      SerializationStreamFactory factory = GWT.create( ChatService.class );
      SerializationStreamReader reader = factory.createStreamReader( p_serial );
      object = reader.readObject();
      if( object != null )
      {
        return object;
      }
    } catch( SerializationException e )
    {
    }

    try
    {
      // Decode game data
      SerializationStreamFactory factory = GWT.create( GameServices.class );
      SerializationStreamReader reader = factory.createStreamReader( p_serial );
      object = reader.readObject();
      if( object != null )
      {
        return object;
      }
    } catch( SerializationException e )
    {
    }
    return object;
  }


  protected void receivePresenceRoom(PresenceRoom p_room)
  {
    if( p_room == null )
    {
      return;
    }
    // handle connected player
    //
    m_connectedUsers = p_room;
    ModelFmpMain.model().fireModelUpdate();
  }

  
  protected void receiveChatMessage(ChatMessage p_msg)
  {
    // handle chat messages
    //
    if( p_msg.isEmpty() )
    {
      // empty message: server ask if we are still connected
      ChatMessage message = new ChatMessage();
      message.setGameId( ModelFmpMain.model().getGame().getId() );
      message.setFromPageId( ModelFmpMain.model().getPageId() );
      message.setFromPseudo( ModelFmpMain.model().getMyAccount().getPseudo() );
      GameServices.Util.getInstance().sendChatMessage( message, m_dummyCallback );
    }
    else
    {
      // real message
      MAppMessagesStack.s_instance.showMessage( p_msg.getFromPseudo() + " : " + p_msg.getText() );
    }
  }


  protected void receiveModelUpdate(ModelFmpUpdate p_result)
  {
    if( p_result == null )
    {
      // this shouldn't occur anymore !
      return;
    }

    try
    {
      if( getGame().getVersion() != p_result.getFromVersion() )
      {
        Window.alert( "Error: receive incoherant model update. reload page" );
        // RpcUtil.logDebug(
        // "model update 'from' is after 'lastUpdate', ignore it..." );
        ClientUtil.reload();
        return;
      }

      if( p_result.getAccountId() == getMyAccount().getId() )
      {
        getActionBuilder().clear();
      }

      // handle game events first
      //
      List<AnEvent> events = p_result.getGameEvents();
      for( AnEvent event : events )
      {
        if( event.getType() == GameLogType.EvtMessage )
        {
          DlgMessageEvent dlgMsg = new DlgMessageEvent( (EbEvtMessage)event );
          dlgMsg.center();
          dlgMsg.show();
        }
        if( getGame() != null )
        {
          if( event.getType() == GameLogType.EvtCancel )
          {
            ((EbEvtCancel)event).execCancel( getGame() );
          }

          event.exec( getGame() );
          // getGame().getLastUpdate().setTime(
          // event.getLastUpdate().getTime() );
          if( event.getType() != GameLogType.EvtCancel )
          {
            getGame().addEvent( event );
          }
          getGame().updateLastTokenUpdate( null );
        }
      }

      // assume that if we receive an update, something has changed !
      ModelFmpMain.model().fireModelUpdate();

    } catch( Throwable e )
    {
      RpcUtil.logError( "error ", e );
      Window.alert( "unexpected error : " + e );
    }
  }




  /**
   * rpc call to run the current action.
   * Clear the current action.
   */
  public void runSingleAction(AnEvent p_action)
  {
    if( m_isActionPending )
    {
      Window.alert( "Une action a déjà été envoyé au serveur... sans réponse pour l'instant" );
      return;
    }
    m_isActionPending = true;
    AppMain.instance().startLoading();

    try
    {
      if( !ModelFmpMain.model().isLogged() && getGame().getGameType() == GameType.MultiPlayer )
      {
        // TODO i18n ???
        throw new RpcFmpException( "You must be logged to do this action" );
      }
      // do not check player is logged to let him join action
      // action.check();
      if( getGame().getGameType() == GameType.MultiPlayer )
      {
        GameServices.Util.getInstance().runEvent( p_action, m_callbackEvents );
      }
      else
      {
        LocalGame.runEvent( p_action, m_callbackEvents, this );
      }
    } catch( RpcFmpException ex )
    {
      Window.alert( Messages.getString( ex ) );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      ModelFmpMain.model().notifyModelUpdate();
    } catch( Throwable p_caught )
    {
      Window.alert( "Unknown error on client: " + p_caught );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
    }
  }

  /**
   * rpc call to run the current action.
   * Clear the current action.
   */
  public void runCurrentAction()
  {
    if( m_isActionPending )
    {
      Window.alert( "Une action a déjà été envoyé au serveur... sans réponse pour l'instant" );
      return;
    }
    m_isActionPending = true;
    AppMain.instance().startLoading();

    try
    {
      if( !ModelFmpMain.model().isJoined() )
      {
        // no i18n ?
        throw new RpcFmpException( "you didn't join this game." );
      }
      // action.check();
      getActionBuilder().unexec();
      if( getGame().getGameType() == GameType.MultiPlayer )
      {
        GameServices.Util.getInstance()
            .runAction( getActionBuilder().getActionList(), m_callbackEvents );
      }
      else
      {
        LocalGame.runAction( getActionBuilder().getActionList(), m_callbackEvents, this );
      }
    } catch( RpcFmpException ex )
    {
      Window.alert( Messages.getString( ex ) );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
    } catch( Throwable p_caught )
    {
      Window.alert( "Unknown error on client: " + p_caught );
      m_isActionPending = false;
      AppMain.instance().stopLoading();
      getActionBuilder().cancel();
      ModelFmpMain.model().notifyModelUpdate();
    }
  }


  /**
   * rpc call to run the current action.
   * Clear the current action.
   */
  public void endTurn()
  {
    EbEvtPlayerTurn action = new EbEvtPlayerTurn();
    action.setAccountId( model().getMyAccount().getId() );
    action.setGame( model().getGame() );
    runSingleAction( action );
  }






  /**
   * @return the actionBuilder
   */
  public EventsPlayBuilder getActionBuilder()
  {
    return m_actionBuilder;
  }

  /**
   * @return the isGridDisplayed
   */
  public boolean isGridDisplayed()
  {
    return m_isGridDisplayed;
  }

  /**
   * @param p_isGridDisplayed the isGridDisplayed to set
   */
  public void setGridDisplayed(boolean p_isGridDisplayed)
  {
    m_isGridDisplayed = p_isGridDisplayed;
    fireModelUpdate();
  }

  /**
   * @return the isAtmosphereDisplayed
   */
  public boolean isAtmosphereDisplayed()
  {
    return m_isAtmosphereDisplayed;
  }

  /**
   * @param p_isAtmosphereDisplayed the isAtmosphereDisplayed to set
   */
  public void setAtmosphereDisplayed(boolean p_isAtmosphereDisplayed)
  {
    m_isAtmosphereDisplayed = p_isAtmosphereDisplayed;
    fireModelUpdate();
  }

  /**
   * @return the isStandardLandDisplayed
   */
  public boolean isCustomMapDisplayed()
  {
    return m_isCustomMapDisplayed;
  }

  /**
   * @param p_isCustomMapDisplayed the isStandardLandDisplayed to set
   */
  public void setCustomMapDisplayed(boolean p_isCustomMapDisplayed)
  {
    m_isCustomMapDisplayed = p_isCustomMapDisplayed;
    fireModelUpdate();
  }

  /**
   * @return the isFireCoverDisplayed
   */
  public boolean isFireCoverDisplayed()
  {
    return m_isFireCoverDisplayed;
  }

  /**
   * @param p_isFireCoverDisplayed the isFireCoverDisplayed to set
   */
  public void setFireCoverDisplayed(boolean p_isFireCoverDisplayed)
  {
    m_isFireCoverDisplayed = p_isFireCoverDisplayed;
    fireModelUpdate();
  }

  /**
   * @return the zoomValueDisplayed
   */
  public EnuZoom getZoomDisplayed()
  {
    return m_zoomDisplayed;
  }

  /**
   * @param p_zoomValueDisplayed the zoomValueDisplayed to set
   */
  public void setZoomDisplayed(int p_zoomValueDisplayed)
  {
    m_zoomDisplayed = new EnuZoom( p_zoomValueDisplayed );
    fireModelUpdate();
  }

  public void setZoomDisplayed(EnuZoom p_zoomDisplayed)
  {
    m_zoomDisplayed = p_zoomDisplayed;
    fireModelUpdate();
  }

  /**
   * @return the isMiniMapDisplayed
   */
  public boolean isMiniMapDisplayed()
  {
    return m_isMiniMapDisplayed;
  }

  /**
   * @param p_isMiniMapDisplayed the isMiniMapDisplayed to set
   */
  public void setMiniMapDisplayed(boolean p_isMiniMapDisplayed)
  {
    m_isMiniMapDisplayed = p_isMiniMapDisplayed;
    fireModelUpdate();
  }

  /**
   * @return the isTimeLineMode
   */
  public boolean isTimeLineMode()
  {
    return m_isTimeLineMode;
  }

  /**
   * @param p_isTimeLineMode the isTimeLineMode to set
   */
  public void setTimeLineMode(boolean p_isTimeLineMode)
  {
    getActionBuilder().clear();
    m_isTimeLineMode = p_isTimeLineMode;
    m_lastTurnPlayed = getGame().getCurrentTimeStep();
    if( !p_isTimeLineMode )
    {
      timePlay( 99999 );
    }
    m_currentActionIndex = getGame().getLogs().size();
    fireModelUpdate();
  }

  public void timeBack(int p_actionCount)
  {
    List<AnEvent> logs = getGame().getLogs();
    while( (m_currentActionIndex > 0) && (p_actionCount > 0) )
    {
      m_currentActionIndex--;
      AnEvent action = logs.get( m_currentActionIndex );
      if( !(action instanceof EbAdmin) 
          && !(action instanceof EbGameJoin)
          && !(action instanceof EbEvtCancel) )
      {
        // unexec action
        try
        {
          logs.get( m_currentActionIndex ).unexec( getGame() );
        } catch( RpcFmpException e )
        {
          RpcUtil.logError( "error ", e );
          Window.alert( "unexpected error : " + e );
          fireModelUpdate();
          return;
        }
        // don't count automatic action as one action to play
        if( !action.isAuto() )
        {
          p_actionCount--;
        }
        // if previous action is EvtConstruct, then unexec too
        if( m_currentActionIndex>0 
            && logs.get( m_currentActionIndex-1 ).getType() == GameLogType.EvtConstruct )
        {
          p_actionCount++;
        }
      }
    }
    fireModelUpdate();
  }


  public void timePlay(int p_actionCount)
  {
    List<AnEvent> logs = getGame().getLogs();
    while( (m_currentActionIndex < logs.size()) && (p_actionCount > 0) )
    {
      AnEvent action = logs.get( m_currentActionIndex );
      if( !(action instanceof EbAdmin) 
          && !(action instanceof EbGameJoin)
          && !(action instanceof EbEvtCancel) )
      {
        // exec action
        try
        {
          action.exec( getGame() );
        } catch( RpcFmpException e )
        {
          RpcUtil.logError( "error ", e );
          Window.alert( "unexpected error : " + e );
          return;
        }
        // don't count automatic action as one action to play
        if( !action.isAuto() && action.getType() != GameLogType.EvtConstruct )
        {
          p_actionCount--;
        }
        // if next action is automatic, then exec too
        if( m_currentActionIndex<logs.size()-1 
            && logs.get( m_currentActionIndex+1 ).isAuto() )
        {
          p_actionCount++;
        }
      }
      m_currentActionIndex++;
    }
    fireModelUpdate();
  }

  public int getCurrentActionIndex()
  {
    return m_currentActionIndex;
  }

  /**
   * User is allowed to cancel action if in puzzle or turn by turn on several day.
   * He must also be in his own turn.
   * @return true if user is allowed to cancel action up to 'getCurrentActionIndex()'
   */
  public boolean canCancelAction()
  {
    if( getGame().getGameType()==GameType.Puzzle )
    {
      return true;
    }
    if( iAmAdmin() )
    {
      return true;
    }
    if( getMyRegistration() == null )
    {
      return false;
    }
    if( m_lastTurnPlayed != getGame().getCurrentTimeStep() )
    {
      return false;
    }
    if( !getGame().getEbConfigGameTime().isAsynchron()
        && getMyRegistration() == getGame().getCurrentPlayerRegistration() )
    {
      return true;
    }
    if( getGame().getConfigGameTime() == ConfigGameTime.StandardAsynch )
    {
      AnEvent event = null;
      for( int i = m_currentActionIndex; i < getGame().getLogs().size(); i++ )
      {
        event = getGame().getLogs().get( i );
        if( event == null || !(event instanceof AnEventPlay)
            || ((AnEventPlay)event).getAccountId() != getMyAccount().getId() )
        {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  


  public boolean isUserConnected(String p_pseudo)
  {
    assert p_pseudo != null;
    return m_connectedUsers.isConnected( p_pseudo );
  }


  public PresenceRoom getPresenceRoom()
  {
    return m_connectedUsers;
  }


  /**
   * @return the channelToken
   */
  protected String getChannelToken()
  {
    return m_channelToken;
  }

  /**
   * @param p_channelToken the channelToken to set
   */
  protected void setChannelToken(String p_channelToken)
  {
    m_channelToken = p_channelToken;
  }

  /**
   * @return the pageId
   */
  public int getPageId()
  {
    return m_pageId;
  }

  /**
   * @param p_pageId the pageId to set
   */
  public void setPageId(int p_pageId)
  {
    m_pageId = p_pageId;
  }

  @Override
  public void onWindowClosing(ClosingEvent p_event)
  {
    GameServices.Util.getInstance().disconnect(
        getMyPresence(), m_dummyCallback );
  }

  /**
   * @return the isChannelConnected
   */
  public boolean isChannelConnected()
  {
    return m_isChannelConnected;
  }




}
