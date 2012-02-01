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

import java.util.logging.Logger;

import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePause;
import com.fullmetalgalaxy.model.persist.gamelog.EbAdminTimePlay;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControl;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtControlFreighter;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtFire;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTimeStep;
import com.fullmetalgalaxy.server.EbAccount.NotificationQty;

/**
 * @author Vincent
 * 
 * aim of this class is to send email or private message to player during their game.
 * 
 */
public class GameNotification
{
  public final static Logger logger = Logger.getLogger( "GameNotification" );

  /**
   * eventually send an email to people that need it.
   * This method have to be called after p_update have been successfully ran.
   * @param action
   * @return true if at least one mail was sended. in this case you must save provided Game
   */
  public static boolean sendMail(Game p_game, ModelFmpUpdate p_update)
  {
    boolean mailSended = false;
    for( AnEvent action : p_update.getGameEvents() )
    {
      if( ((action instanceof EbAdminTimePlay) || (action instanceof EbEvtPlayerTurn))
          && !p_game.isFinished() )
      {
        if( p_game.getCurrentPlayerRegistration() == null )
        {
          // Parallel mode is starting
          // send email to all players
          FmgMessage msg = new FmgMessage( "paralleleGameStart", p_game );
          if( p_game.getCurrentTimeStep() > 1 )
          {
            msg = new FmgMessage( "paralleleGameUnpause", p_game );
          }
          send2AllPlayers( msg, p_game, NotificationQty.Min, false );
          mailSended = true;
        }
        else if( p_game.getCurrentTimeStep() <= 1 )
        {
          // new turn in begin game => email to current player
          send2Player( new FmgMessage( "newTurn", p_game ), p_game,
              p_game.getCurrentPlayerRegistration(), NotificationQty.Min, false );
          mailSended = true;
        }
        else
        {
          // new turn => email to current player
          send2Player( new FmgMessage( "newTurn", p_game ), p_game,
              p_game.getCurrentPlayerRegistration(), NotificationQty.Std, false );
          mailSended = true;
        }
      }

      if( action instanceof EbAdminTimePause )
      {
        send2AllPlayers( new FmgMessage( "gamePause", p_game ), p_game, NotificationQty.Std, false );
        mailSended = true;
      }

      if( action instanceof EbEvtTimeStep )
      {
        for( EbRegistration registration : p_game.getSetRegistration() )
        {
          if( registration.getPtAction() > registration.getMaxActionPt( p_game )
              - p_game.getEbConfigGameTime().getActionPtPerTimeStep() )
          {
            send2Player( new FmgMessage( "tooManyPA", p_game ), p_game, registration,
                NotificationQty.Std, true );
            mailSended = true;
          }
        }
      }

      if( action instanceof EbEvtControlFreighter )
      {
        EbRegistration looser = ((EbEvtControlFreighter)action).getOldRegistration( p_game );
        send2Player( new FmgMessage( "loseFreighter", p_game ), p_game, looser,
            NotificationQty.Min, false );
        mailSended = true;
      }

      if( action instanceof EbEvtControl )
      {
        EbRegistration registration = p_game.getRegistrationByColor( ((EbEvtControl)action)
            .getOldColor() );
        if( registration != null )
        {
          send2Player( new FmgMessage( "loseUnit", p_game ), p_game, registration,
              NotificationQty.Max, true );
          mailSended = true;
        }
      }
      if( action instanceof EbEvtFire )
      {
        EbRegistration registration = p_game.getRegistrationByColor( ((EbEvtFire)action)
            .getTokenTarget( p_game ).getColor() );
        if( registration != null )
        {
          send2Player( new FmgMessage( "loseUnit", p_game ), p_game, registration,
              NotificationQty.Max, true );
          mailSended = true;
        }
      }
    }

    if( p_game.getStatus() == GameStatus.History )
    {
      send2AllPlayers( new FmgMessage( "gameFinish", p_game ), p_game, NotificationQty.Min, false );
      mailSended = true;
    }

    return mailSended;
  }


  private static void send2AllPlayers(FmgMessage p_msg, Game p_game, NotificationQty p_level,
      boolean p_checkDoubleSend)
  {
    for( EbRegistration registration : p_game.getSetRegistration() )
    {
      send2Player( p_msg, p_game, registration, p_level, p_checkDoubleSend );
    }
  }

  /**
   * 
   * @param p_msg
   * @param p_game
   * @param p_registration
   * @param p_level
   * @param p_checkDoubleSend if true, this message can only be send once between two game action
   */
  private static void send2Player(FmgMessage p_msg, Game p_game, EbRegistration p_registration,
      NotificationQty p_level, boolean p_checkDoubleSend)
  {
    if( p_registration == null
        || (p_registration.isNotifSended( p_msg.getName() ) && p_checkDoubleSend) )
    {
      logger.finest( "game " + p_game.getName() + ", notification " + p_msg.getName() + "player "
          + " already receive his notif" );
      return;
    }
    EbAccount account = null;
    if( p_registration != null && p_registration.getAccount() != null )
    {
      account = FmgDataStore.dao().get( EbAccount.class, p_registration.getAccount().getId() );
    }
    if( account == null )
    {
      logger.warning( "game " + p_game.getName() + ", notification " + p_msg.getName()
          + " couldn't be send because account wasn't found" );
      return;
    }
    if( account.getNotificationQty().ordinal() < p_level.ordinal() )
    {
      logger.fine( "game " + p_game.getName() + ", notification " + p_msg.getName() + "player "
          + account.getPseudo() + ": don't want this message" );
      return;
    }
    // for high verbosity notification or high speed game, check player's room
    // presence
    if( (p_level == NotificationQty.Max || p_game.getConfigGameTime().isQuick())
        && ChannelManager.getRoom( p_game.getId() ).isConnected( account.getPseudo() ) )
    {
      logger.fine( "game " + p_game.getName() + ", notification " + p_msg.getName() + "player "
          + account.getPseudo() + " is connected: we don't need to send an email" );
      return;
    }
    // finally send message
    if( p_msg.send( account ) )
    {
      p_registration.addNotifSended( p_msg.getName() );
    }
  }


}
