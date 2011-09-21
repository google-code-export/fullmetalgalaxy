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
package com.fullmetalgalaxy.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;

import com.fullmetalgalaxy.model.ChatMessage;
import com.fullmetalgalaxy.model.GameServices;
import com.fullmetalgalaxy.model.GameType;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.Presence;
import com.fullmetalgalaxy.model.PresenceRoom;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventUser;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdmin;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtCancel;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.fullmetalgalaxy.server.EbAccount.AllowMessage;
import com.fullmetalgalaxy.server.image.MiniMapProducer;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * TODO create only one dataStore per RPC request
 * @author Vincent
 *
 */
public class GameServicesImpl extends RemoteServiceServlet implements GameServices
{
  public static final long serialVersionUID = 1;
  private final static FmpLogger log = FmpLogger.getLogger( GameServicesImpl.class.getName() );
  protected static String s_basePath = null;

  /**
   * constructor: 
   */
  public GameServicesImpl()
  {
    super();
  }

  /**
   * servlet initialisation
   * initialize SGBD connexion.
   */
  @Override
  public void init() throws ServletException
  {
    super.init();
    ServerUtil.setBasePath( getServletContext().getRealPath( "/" ) );
    if( s_basePath == null || s_basePath.isEmpty() )
    {
      s_basePath = getServletContext().getRealPath( "/" );
    }
  }


  protected static boolean storeMinimap(Game p_game, byte[] p_data)
  {
    // Get a file service
    FileService fileService = FileServiceFactory.getFileService();

    try
    {
      // Create a new Blob file with mime-type "text/plain"
      AppEngineFile file = fileService.createNewBlobFile( "image/png" );

      // Open a channel to write to it
      FileWriteChannel writeChannel = fileService.openWriteChannel( file, true );
      writeChannel.write( ByteBuffer.wrap( p_data ) );
      // Now finalize
      writeChannel.closeFinally();

      // Now read from the file using the Blobstore API
      BlobKey blobKey = fileService.getBlobKey( file );

      // update game
      p_game.setMinimapUri( ImagesServiceFactory.getImagesService().getServingUrl( blobKey ) );
      p_game.setMinimapBlobKey( blobKey.getKeyString() );

    } catch( Exception e )
    {
      ServerUtil.logger.severe( e.getMessage() );
      return false;
    }
    return true;
  }


  @Override
  public EbBase saveGame(Game p_game) throws RpcFmpException
  {
    return saveGame( p_game, null );
  }

  /* (non-Javadoc)
  * @see com.fullmetalgalaxy.model.GameCreationServices#createGame(com.fullmetalgalaxy.model.DbbGame)
  */
  @Override
  public EbBase saveGame(Game p_game, String p_modifDesc) throws RpcFmpException
  {
    if( !isLogged() )
    {
      throw new RpcFmpException(
          "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
    }
    FmgDataStore dataStore = new FmgDataStore(false);
    EbAccount account = Auth.getUserAccount( getThreadLocalRequest(), getThreadLocalResponse() );

    // should we construct minimap image ?
    if( p_game.getMinimapUri() == null )
    {
      MiniMapProducer miniMapProducer = new MiniMapProducer( s_basePath, p_game );
      storeMinimap( p_game, miniMapProducer.getImage() );
    }

    boolean isNewlyCreated = true;
    if( !p_game.isTrancient() )
    {
      isNewlyCreated = false;
      // then add an admin event
      if( p_modifDesc == null || p_modifDesc.trim().length() == 0 )
      {
        p_modifDesc = "Unknown edition event";
      }
      EbAdmin adminEvent = new EbAdmin();
      adminEvent.setGame( p_game );
      adminEvent.setMessage( p_modifDesc );

      if( (!Auth.isUserAdmin( getThreadLocalRequest(), getThreadLocalResponse() )) )
      {
        throw new RpcFmpException(
            "Vous n'avez pas les droits suffisants pour effectuer cette operation" );
      }
      adminEvent.setAccountId( account.getId() );
      p_game.addEvent( adminEvent );
    }

    dataStore.put( p_game );
    dataStore.close();

    if( isNewlyCreated )
    {
      // game is just created
      GameWorkflow.gameOpen( p_game );
      AccountStatsManager.gameCreate( account.getId(), p_game );
    }

    return p_game.createEbBase();
  }


  static protected Game getEbGame(long p_gameId)
  {
    FmgDataStore dataStore = new FmgDataStore(false);
    Game model = dataStore.getGame( p_gameId );
    if( model == null )
    {
      return null;
    }


    // anything to update ?
    try
    {
      boolean wasHistory = model.isHistory();
      ModelFmpUpdate modelUpdate = new ModelFmpUpdate();
      modelUpdate.setGameId( model.getId() );
      modelUpdate.setFromVersion( model.getVersion() );

      ArrayList<AnEvent> events = GameWorkflow.checkUpdate( model );

      if( !events.isEmpty() || wasHistory != model.isHistory() )
      {
        dataStore.put( model );
      }
      if( !events.isEmpty() )
      {
        modelUpdate.setGameEvents( events );
        modelUpdate.setToVersion( model.getVersion() );

        ChannelManager.broadcast( ChannelManager.getRoom( model.getId() ), modelUpdate );

        // do we need to send an email ?
        sendMail( model, modelUpdate );
      }
    } catch( RpcFmpException e )
    {
      log.error( e );
    }


    dataStore.close();

    if( model.getGameType() != GameType.MultiPlayer )
    {
      model.setLastTimeStepChange( new Date( System.currentTimeMillis() ) );
    }
    return model;
  }

  public static ModelFmpInit sgetModelFmpInit(String p_gameId)
  {
    long gameId = 0;
    try
    {
      gameId = Long.parseLong( p_gameId );
    } catch( NumberFormatException e )
    {
    }
    ModelFmpInit modelInit = new ModelFmpInit();
    Game game = null;
    if( gameId == 0 )
    {
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try
      {
        if( s_basePath != null )
        {
          fis = new FileInputStream( new File( s_basePath + p_gameId ) );
          in = new ObjectInputStream( fis );
          modelInit = ModelFmpInit.class.cast( in.readObject() );
          in.close();
          fis.close();
        }
      } catch( Exception ex )
      {
        ex.printStackTrace();
      }
    }
    else
    {
      game = getEbGame( gameId );
      if( game != null )
      {
        modelInit.setGame( game );
        // FmpUpdateStatus.loadAllAccounts( modelInit.getMapAccounts(), game );
      }
    }

    return modelInit;
  }


  @Override
  public ModelFmpInit getModelFmpInit(String p_gameId) throws RpcFmpException
  {
    ModelFmpInit modelInit = sgetModelFmpInit( p_gameId );

    if( modelInit.getGame() != null && modelInit.getGame().getId() != 0 )
    {
      // ModelFmpUpdate modelUpdate = FmpUpdateStatus.getModelUpdate(
      // Auth.getUserPseudo(
      // getThreadLocalRequest(), getThreadLocalResponse() ),
      // modelInit.getGame().getId(), null );
      modelInit.setPresenceRoom( ChannelManager.getRoom( modelInit.getGame().getId() ) );
    }
    return modelInit;
  }



  private boolean isLogged()
  {
    return Auth.isUserLogged( getThreadLocalRequest(), getThreadLocalResponse() );
  }

  /**
   * This service is only here to serialize a ModelFmpUpdate class with RPC.encodeResponseForSuccess
   */
  @Override
  public ModelFmpUpdate getModelFmpUpdate(long p_gameId) throws RpcFmpException
  {
    assert p_gameId != 0;
    // return FmpUpdateStatus.waitForModelUpdate( Auth.getUserPseudo(
    // getThreadLocalRequest(),
    // getThreadLocalResponse() ), p_gameId, p_lastUpdate );
    return null;
  }



  /**
   * eventually send an email to people that need it.
   * This method have to be called after p_action have been successfully ran.
   * @param action
   */
  protected static void sendMail(Game p_game, ModelFmpUpdate p_update)
  {
    for( AnEvent action : p_update.getGameEvents() )
    {
      if( (action instanceof EbAdminTimePlay) || (action instanceof EbEvtPlayerTurn) )
      {
        EbAccount currentPlayer = null;
        if( p_game.getCurrentPlayerRegistration() != null )
        {
          FmgDataStore dataStore = new FmgDataStore(true);
          currentPlayer = dataStore.get( EbAccount.class, p_game.getCurrentPlayerRegistration()
              .getAccount().getId() );
        }
        if( currentPlayer == null )
        {
          // TODO send email to all players: it's an asynchron game
          log.error( "New turn email couldn't be send" );
          return;
        }
        if( ChannelManager.getRoom( p_game.getId() ).isConnected( currentPlayer.getPseudo() ) )
        {
          log.fine( "player is connected: we don't need to send an email" );
          return;
        }
        if( currentPlayer.getAllowMsgFromGame() == AllowMessage.No || !currentPlayer.haveEmail() )
        {
          // player don't want any notification
          log.fine( "player " + currentPlayer.getPseudo() + " don't want any notification" );
          return;
        }
        String subject = "FMG: Notification de tour de jeux sur " + p_game.getName();
        String body = "Bonjour " + currentPlayer.getPseudo() + "\n\n"
            + "Vous pouvez des a present vous connecter a la partie " + p_game.getName()
            + " http://www.fullmetalgalaxy.com/game.jsp?id=" + p_game.getId()
            + " pour jouer votre tour " + p_game.getCurrentTimeStep() + ".\n";
        PMServlet.sendMail( subject, body, currentPlayer.getEmail() );
        return;
      }
    }
  }




  @Override
  public void runModelUpdate(ModelFmpUpdate p_modelUpdate) throws RpcFmpException
  {
    if( p_modelUpdate.getGameEvents().isEmpty() )
    {
      throw new RpcFmpException( "No actions provided" );
    }
    FmgDataStore dataStore = new FmgDataStore(false);
    Game game = dataStore.getGame( p_modelUpdate.getGameId() );
    if( game == null )
    {
      throw new RpcFmpException( "run action on unknown game: "+p_modelUpdate.getGameId());
    }
    if( game.getVersion() != p_modelUpdate.getFromVersion() )
    {
      throw new RpcFmpException( "Send action on wrong game version" );
    }
    // security check
    /*if( p_action.getAccountId() != 0L )
    {
      EbAccount account = askForIdentity();
      if( account.getId() != p_action.getAccountId() )
      {
        throw new RpcFmpException( "account " + account.getLogin() + "(" + account.getId()
            + ") thief account " + p_action.getAccountId() );
      }
    }*/
    ModelFmpUpdate modelUpdate = new ModelFmpUpdate();
    AnEvent lastAction = null;
    
    modelUpdate.setGameId( game.getId() );
    modelUpdate.setFromVersion( game.getVersion() );
    modelUpdate.setGameEvents( GameWorkflow.checkUpdate( game ) );

    // execute actions
    try
    {
      // an automatic update before run event ?
      modelUpdate.getGameEvents().addAll( GameWorkflow.checkUpdate( game, p_modelUpdate.getGameEvents() ) );
      
      // then run all provided event
      for( AnEvent event : p_modelUpdate.getGameEvents() )
      {
        if( event instanceof AnEventUser )
        {
          ((AnEventUser)event).setRemoteAddr( getThreadLocalRequest().getRemoteAddr() );
        }
        event.setLastUpdate( ServerUtil.currentDate() );
        
        if(event.getType() == GameLogType.EvtCancel)
        {
          // cancel action doesn't work in exact same way as other event
          ((EbEvtCancel)event).execCancel( game );
        }
        else
        {
          // execute action
          event.checkedExec( game );
          game.addEvent( event );

        }
        lastAction = event;
      }
      modelUpdate.getGameEvents().addAll( p_modelUpdate.getGameEvents() );
      // another automatic update after running event ?
      modelUpdate.getGameEvents().addAll( GameWorkflow.checkUpdate( game ) );
      
    } catch( RpcFmpException e )
    {
      dataStore.rollback();
      throw e;
    }


    // Some special cases
    // ///////////////////
    if( game.isAsynchron() && lastAction != null && lastAction.getType() == GameLogType.EvtLand )
    {
      // a player is just landed and game is parallel: next player
      EbEvtPlayerTurn action = new EbEvtPlayerTurn();
      action.setLastUpdate( ServerUtil.currentDate() );
      action.setAccountId( ((EbEvtLand)lastAction).getAccountId() );
      action.setGame( game );
      action.checkedExec( game );
      game.addEvent( action );
      modelUpdate.getGameEvents().add( action );
    }


    dataStore.put( game );
    dataStore.close();
    modelUpdate.setToVersion( game.getVersion() );

    // do we need to send an email ?
    sendMail( game, modelUpdate );

    // broadcast changes to all clients
    ChannelManager.broadcast( ChannelManager.getRoom( game.getId() ), modelUpdate );

  
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.GameServices#sendChatMessage(com.fullmetalgalaxy.model.ChatMessage)
   */
  @Override
  public void sendChatMessage(ChatMessage p_message) throws RpcFmpException
  {
    // we could check pseudo to detect cheater...
    //p_message
    //    .setFromPseudo( Auth.getUserPseudo( getThreadLocalRequest(), getThreadLocalResponse() ) );
    p_message.setDate( ServerUtil.currentDate() );
    
    PresenceRoom room = ChannelManager.getRoom( p_message.getGameId() );
    ChannelManager.broadcast( room, p_message );
  }

  @Override
  public void disconnect(Presence p_presence)
  {
    ChannelManager.disconnect( p_presence );
  }
  
  @Override
  public String reconnect(Presence p_presence)
  {
    return ChannelManager.connect( p_presence );
  }

  @Override
  public void checkUpdate(long p_gameId) throws RpcFmpException
  {
    getEbGame( p_gameId );
  }
  
  /**
   * This service is only here to serialize a ChatMessage class with RPC.encodeResponseForSuccess
   */
  @Override
  public ChatMessage getChatMessage(long p_gameId)
  {
    return new ChatMessage();
  }

  /**
   * return non null PresenceRoom class associated with p_gameId.
   * This service is also here to serialize a PresenceRoom class with RPC.encodeResponseForSuccess
   */
  @Override
  public PresenceRoom getRoom(long p_gameId)
  {
    return ChannelManager.getRoom( p_gameId );
  }



}
