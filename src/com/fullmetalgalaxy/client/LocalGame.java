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
import java.util.Date;
import java.util.List;

import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.triggers.EbTrigger;
import com.fullmetalgalaxy.model.persist.triggers.actions.AnAction;
import com.fullmetalgalaxy.model.persist.triggers.conditions.AnCondition;
import com.fullmetalgalaxy.model.persist.triggers.conditions.EbCndPuzzleLoad;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Vincent Legendre
 * This class replace standards call to RPC service while playing a puzzle game.
 */
public class LocalGame
{
  /**
   * This method is called after a successful load of a local game.
   * @param callback
   */
  public static void loadGame(ModelFmpMain p_model)
  {
    ModelFmpUpdate updates = new ModelFmpUpdate();
    List<AnEvent> events = new ArrayList<AnEvent>();
    EbGame game = ModelFmpMain.model().getGame();
    updates.setFromVersion( game.getVersion() );
    for( EbTrigger trigger : game.getTriggers() )
    {
      if( trigger.isEnable() )
      {
        for( AnCondition condition : trigger.getConditions() )
        {
          if( condition instanceof EbCndPuzzleLoad )
          {
            for( AnAction action : trigger.getActions() )
            {
              events.addAll( action.createEvents( game, new ArrayList<Object>() ) );
            }
          }
        }
      }
    }
    updates.setGameEvents( events );
    updates.setToVersion( game.getVersion() );
    p_model.receiveModelUpdate( updates );
  }

  public static void runEvent(AnEvent p_action, AsyncCallback<Void> callbackUpdates,
      ModelFmpMain p_model)
  {
    EbGame game = ModelFmpMain.model().getGame();
    ModelFmpUpdate updates = new ModelFmpUpdate();
    updates.setFromVersion( game.getVersion() );
    List<AnEvent> events = new ArrayList<AnEvent>();
    events.add( p_action );
    events.addAll( game.createTriggersEvents() );
    updates.setGameEvents( events );
    updates.setToVersion( game.getVersion() );
    callbackUpdates.onSuccess( null );
    p_model.receiveModelUpdate( updates );

    // check triggers
    events = new ArrayList<AnEvent>();
    events.addAll( game.createTriggersEvents() );
    updates = new ModelFmpUpdate();
    updates.setFromVersion( game.getVersion() );
    updates.setGameEvents( events );
    updates.setToVersion( game.getVersion() );
    callbackUpdates.onSuccess( null );
    p_model.receiveModelUpdate( updates );
  }

  public static void runAction(ArrayList<AnEventPlay> p_actionList,
      AsyncCallback<Void> callbackUpdates, ModelFmpMain p_model)
  {
    EbGame game = ModelFmpMain.model().getGame();
    ModelFmpUpdate updates = new ModelFmpUpdate();
    updates.setFromPageId( ModelFmpMain.model().getPageId() );
    updates.setFromPseudo( ModelFmpMain.model().getMyAccount().getPseudo() );
    updates.setFromVersion( game.getVersion() );
    List<AnEvent> events = new ArrayList<AnEvent>();
    for( AnEvent eventPlay : p_actionList )
    {
      events.add( eventPlay );
    }
    updates.setGameEvents( events );
    updates.setToVersion( game.getVersion() );
    callbackUpdates.onSuccess( null );
    p_model.receiveModelUpdate( updates );

    // check triggers
    events = new ArrayList<AnEvent>();
    events.addAll( game.createTriggersEvents() );
    updates = new ModelFmpUpdate();
    updates.setFromPageId( ModelFmpMain.model().getPageId() );
    updates.setFromPseudo( ModelFmpMain.model().getMyAccount().getPseudo() );
    updates.setFromVersion( game.getVersion() );
    updates.setGameEvents( events );
    updates.setToVersion( game.getVersion() );
    callbackUpdates.onSuccess( null );
    p_model.receiveModelUpdate( updates );
  }


  /**
   * Get all changes in an fmp model since p_currentVersion and send back all needed data
   * to update the model.
   * @param p_lastVersion
   * @return model change between p_lastVersion date and current date.
   * @throws RpcFmpException
   */
  public static void modelUpdate(long p_gameId, Date p_lastVersion,
      AsyncCallback<Void> callbackUpdates, ModelFmpMain p_model)
  {
    // check triggers
    EbGame game = ModelFmpMain.model().getGame();
    List<AnEvent> events = new ArrayList<AnEvent>();
    events.addAll( game.createTriggersEvents() );
    ModelFmpUpdate updates = new ModelFmpUpdate();
    updates.setFromVersion( game.getVersion() );
    updates.setGameEvents( events );
    updates.setToVersion( game.getVersion() );
    callbackUpdates.onSuccess( null );
    p_model.receiveModelUpdate( updates );
  }

}
