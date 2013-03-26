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
 *  Copyright 2010, 2011, 2012 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server.ai;

import java.util.ArrayList;

import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;

/**
 * @author Vincent
 *
 */
public class PlayerHAL9000 implements AutomaticPlayer
{

  @Override
  public ArrayList<AnEvent> play(Game p_game, EbRegistration p_player)
  {
    ArrayList<AnEvent> events = new ArrayList<AnEvent>();
    
    if( p_game.getCurrentPlayerIds().contains( p_player.getId() ) && !p_game.isParallel() )
    {
      EbEvtPlayerTurn action = new EbEvtPlayerTurn();
      action.setGame( GameEngine.model().getGame() );
      action.setAccountId( p_player.getAccount().getId() );
      events.add( action );
    }
    
    return events;
  }

}
