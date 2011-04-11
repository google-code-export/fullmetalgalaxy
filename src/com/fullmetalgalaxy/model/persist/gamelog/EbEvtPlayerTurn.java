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
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Date;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.persist.EbConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;


/**
 * @author Vincent Legendre
 * change currents player turns and update action point for next player.
 */
public class EbEvtPlayerTurn extends AnEvent
{
  static final long serialVersionUID = 1;

  private long m_accountId = 0L;
  private int m_oldActionPt = 0;
  private Date m_endTurnDate = null;

  /**
   * 
   */
  public EbEvtPlayerTurn()
  {
    super();
    init();
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  private void init()
  {
    m_accountId = 0;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtPlayerTurn;
  }


  @Override
  public void check(EbGame p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( isAuto() )
    {
      // no check !
      return;
    }
    if( !p_game.isStarted() )
    {
      // TODO i18n
      throw new RpcFmpException( "Cette partie n'est pas demarre" );
    }
    if( p_game.isFinished() )
    {
      // no i18n
      throw new RpcFmpException( "Cette partie est termine" );
    }
    if( p_game.isAsynchron() && p_game.getCurrentTimeStep() > 0 )
    {
      // no i18n
      throw new RpcFmpException(
          "Cette partie ne se joue pas en tour par tour mais en mode asynchrone" );
    }
    if( getAccountId() != p_game.getCurrentPlayerRegistration().getAccountId() )
    {
      throw new RpcFmpException( "Seul le joueur dont c'est le tour peut ecourter sont tour de jeu" );
    }
    if( p_game.getCurrentTimeStep() <= p_game.getEbConfigGameTime().getDeploymentTimeStep() )
    {
      EbToken freighter = p_game.getFreighter( p_game.getCurrentPlayerRegistration() );
      if(freighter != null && freighter.getLocation() != Location.Board)
      {
        // TODO i18n
        throw new RpcFmpException( "Vous devez poser votre astronef avant." );
      }
    }
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(EbGame p_game) throws RpcFmpException
  {
    super.exec(p_game);
    EbGame game = p_game;
    assert game != null;
    
    // round down players action point
    EbRegistration currentPlayerRegistration = game.getCurrentPlayerRegistration();
    assert currentPlayerRegistration != null;
    // backup for unexec
    m_oldActionPt = currentPlayerRegistration.getPtAction();
    currentPlayerRegistration.setPtAction( currentPlayerRegistration.getRoundedActionPt() );
    
    // update all his tokens bullets count
    EnuColor currentPlayerColor = currentPlayerRegistration.getEnuColor();
    for( EbToken token : p_game.getSetToken() )
    {
      if( token.getBulletCount() < token.getMaxBulletCount()
          && currentPlayerColor.isColored( token.getColor() ) )
      {
        token.setBulletCount( token.getBulletCount()
            + game.getEbConfigGameTime().getBulletCountIncrement() );
        if( token.getBulletCount() > token.getMaxBulletCount() )
        {
          token.setBulletCount( token.getMaxBulletCount() );
        }
      }
    }

    // reset all end turn date
    for( EbRegistration player : game.getSetRegistration() )
    {
      player.setEndTurnDate( null );
    }

    // next player
    EbRegistration nextPlayerRegistration = game.getNextPlayerRegistration();
    if( nextPlayerRegistration.getOrderIndex() <= currentPlayerRegistration.getOrderIndex() )
    {
      // next turn !
      game.setCurrentTimeStep( game.getCurrentTimeStep() + 1 );
    }

    // if game is parallel (old asynchron) and turn 1, all players are landed:
    // start parallel mode
    if( game.isAsynchron() && game.getCurrentTimeStep() == 1 )
    {
      nextPlayerRegistration = null;
      // this is the real start time for parallele game
      // as player may took a while to land
      p_game.setLastTimeStepChange( new Date() );
    }
    else
    {
      int actionInc = EbConfigGameTime.getDefaultActionInc( p_game );
      int actionExtraPoint = game.getEbConfigGameTime().getActionPtPerExtraShip()
          * (nextPlayerRegistration.getEnuColor().getNbColor() - 1);
      actionInc += actionExtraPoint;
      int actionPt = nextPlayerRegistration.getPtAction() + actionInc;
      if( actionPt > game.getEbConfigGameVariant().getActionPtMaxReserve() + actionExtraPoint )
      {
        actionPt = game.getEbConfigGameVariant().getActionPtMaxReserve() + actionExtraPoint;
      }
      nextPlayerRegistration.setPtAction( actionPt );

      // set End turn date for future current player
      if( game.getCurrentTimeStep() != 0
          && game.getEbConfigGameTime().getTimeStepDurationInSec() != 0 )
      {
        if( m_endTurnDate == null )
        {
          m_endTurnDate = new Date( System.currentTimeMillis()
              + game.getEbConfigGameTime().getTimeStepDurationInMili() );
        }
        nextPlayerRegistration.setEndTurnDate( m_endTurnDate );
      }
    }

    game.setCurrentPlayerRegistration( nextPlayerRegistration );
  }

  /**
   * note that this method do not set the player's end turn date.
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(EbGame p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    EbGame game = p_game;
    assert game != null;

    // current player action points
    int actionInc = EbConfigGameTime.getDefaultActionInc( p_game );
    int actionExtraPoint = game.getEbConfigGameTime().getActionPtPerExtraShip()
        * (game.getCurrentPlayerRegistration().getEnuColor().getNbColor() - 1);
    actionInc += actionExtraPoint;
    int actionPt = game.getCurrentPlayerRegistration().getPtAction() - actionInc;
    if( actionPt < 0 )
    {
      actionPt = 0;
    }
    game.getCurrentPlayerRegistration().setPtAction( actionPt );

    // find index player
    int index = game.getCurrentPlayerRegistration().getOrderIndex();
    // previous player
    EbRegistration registration = null;
    do
    {
      index--;
      if( index < 0 )
      {
        // previous turn !
        index = game.getSetRegistration().size() - 1;
        game.setCurrentTimeStep( game.getCurrentTimeStep() - 1 );
      }
      registration = game.getRegistrationByOrderIndex( index );
      assert registration != null;
    } while( registration.getColor() == EnuColor.None );

    game.setCurrentPlayerRegistration( registration );
    registration.setPtAction( m_oldActionPt );
  }

  /**
   * @return the account
   */
  public long getAccountId()
  {
    return m_accountId;
  }

  /**
   * @param p_account the account to set
   */
  public void setAccountId(long p_id)
  {
    m_accountId = p_id;
  }
}
