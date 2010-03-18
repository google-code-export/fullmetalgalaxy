/**
 * 
 */
package com.fullmetalgalaxy.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * @author Kroc
 * 
 */
public interface Services extends RemoteService
{

  public static final String SERVICE_URI = "Services";

  public static class Util
  {

    public static ServicesAsync getInstance()
    {
      ServicesAsync instance = (ServicesAsync)GWT.create( Services.class );
      ServiceDefTarget target = (ServiceDefTarget)instance;
      target.setServiceEntryPoint( GWT.getModuleBaseURL() + SERVICE_URI );
      // AppMain.instance().startLoading();
      return instance;
    }
  }


  /**
   * simply ask server for user's identity. 
   */
  public EbAccount askForIdentity() throws RpcFmpException;


  /**
   * @param whereClause to select a subset of games.
   * @return an array of all games found.
   */
  public List<com.fullmetalgalaxy.model.persist.EbGamePreview> getGameList(GameFilter p_filter)
      throws RpcFmpException;


  /**
   * create (if id=0) or save a game.
   * @param game description of the game.
   * @return id/lastUpdate of the created/saved game
   */
  public EbBase saveGame(EbGame game) throws RpcFmpException;

  /**
   * same as above, but let user set a message about his modifications
   * @param game
   * @param p_modifDesc
   * @return id/lastUpdate of the created/saved game
   * @throws RpcFmpException
   */
  public EbBase saveGame(EbGame game, String p_modifDesc) throws RpcFmpException;

  /**
   * remove the given game from database
   * @param p_id
   * @throws RpcFmpException
   */
  public void deleteGame(long p_gameId) throws RpcFmpException;

  /**
   * cancel and history the given game.
   * @param p_id
   * @throws RpcFmpException
   */
  public void cancelGame(long p_gameId) throws RpcFmpException;

  /**
   * Get all informations concerning the specific game.
   * @param p_gameId Id of the requested game
   * @return
   */
  public ModelFmpInit getModelFmpInit(String p_gameId) throws RpcFmpException;



  public ModelFmpUpdate runEvent(AnEvent p_action, Date p_lastUpdate) throws RpcFmpException;

  public ModelFmpUpdate runAction(ArrayList<AnEventPlay> p_actionList, Date p_lastUpdate)
      throws RpcFmpException;


  /**
   * Get all changes in an fmp model since p_currentVersion and send back all needed data
   * to update the model.
   * @param p_lastVersion
   * @return model change between p_lastVersion date and current date.
   * @throws RpcFmpException
   */
  public ModelFmpUpdate getModelFmpUpdate(long p_gameId, Date p_lastUpdate)
      throws RpcFmpException;


  /**
   * 
   * @param p_message
   * @throws RpcFmpException
   */
  public ModelFmpUpdate sendChatMessage(ChatMessage p_message, Date p_lastUpdate)
      throws RpcFmpException;


}
