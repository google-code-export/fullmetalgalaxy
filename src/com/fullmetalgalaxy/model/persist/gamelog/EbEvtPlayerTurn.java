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
package com.fullmetalgalaxy.model.persist.gamelog;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.SharedMethods;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.MessagesRpcException;
import com.fullmetalgalaxy.model.ressources.SharedI18n;


/**
 * @author Vincent Legendre
 * change currents player turns and update action point for next player.
 */
public class EbEvtPlayerTurn extends AnEvent
{
  static final long serialVersionUID = 1;

  /** player that end his turn */
  private long m_accountId = 0L;
  private int m_oldActionPt = 0;
  private Date m_endTurnDate = null;

  // data used by timeline mode
  private short m_oldTurn = 0;
  private short m_newTurn = 0;
  /** ie EbRegistration ID */
  private long m_oldPlayerId = 0;
  private int m_oldCurrentPlayersCount = 0;
  private Set<EbToken> oreToRemoveWhileUnexec = null;

  // private long m_newPlayerId = 0;

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

  /**
   * if player id isn't set, this method will read it from account id
   * @param p_game
   * @return
   */
  public long getOldPlayerId(Game p_game)
  {
    if( p_game != null && m_oldPlayerId <= 0 )
    {
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        if( p_game.getCurrentPlayerIds().contains( registration.getId() )
            && registration.getAccount().getId() == getAccountId() )
        {
          m_oldPlayerId = registration.getId();
        }
      }
    }
    return m_oldPlayerId;
  }

  public void setOldPlayerId(long p_oldPlayerId)
  {
    m_oldPlayerId = p_oldPlayerId;
  }

  /* * Warning for old data, this method may return 0 */
  /*public long getNewPlayerId()
  {
    return m_newPlayerId;
  }*/

  @Override
  public String toString()
  {
    String str = "End turn [" + getAccountId() + "]: " + getOldTurn();
    if( getOldTurn() != getNewTurn() )
    {
      str += "->" + getNewTurn();
    }
    return str;
  }


  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);
    if( isAuto() )
    {
      // no check !
      return;
    }
    if( p_game.getStatus() != GameStatus.Running )
    {
      throw new RpcFmpException( errMsg().gameNotStarted() );
    }
    if( p_game.isFinished() )
    {
      // no i18n
      throw new RpcFmpException( "This game is finished" );
    }
    if( p_game.isParallel() && p_game.getCurrentTimeStep() > 1 )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "You can't end your turn in parallele mode" );
    }
    if( p_game.getCurrentPlayerIds().isEmpty() )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "You can't end your turn: no current player" );
    }
    EbRegistration myRegistration = p_game.getRegistration( getOldPlayerId( p_game ) );
    if( myRegistration == null )
    {
      // no i18n as HMI won't allow this action
      throw new RpcFmpException( "Not your turn" );
    }
    if( p_game.getCurrentTimeStep() <= p_game.getEbConfigGameTime().getDeploymentTimeStep() )
    {
      EbToken freighter = p_game.getFreighter( myRegistration );
      if( freighter != null && freighter.getLocation() == Location.Orbit )
      {
        throw new RpcFmpException( errMsg().mustLandFreighter() );
      }
    }
    // check that current player have no cheating tank (ie two tank on same
    // montain)
    if( !isAuto() )
    {
      EnuColor playerColor = myRegistration.getEnuColor();
      for( EbToken token : p_game.getSetToken() )
      {
        if( token.getColor() != EnuColor.None && playerColor.isColored( token.getColor() )
            && p_game.getTankCheating( token ) != null )
        {
          throw new RpcFmpException( errMsg().cantEndTurnTwoTankMontain() );
        }
      }
    }
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);
    Game game = p_game;
    assert game != null;


    // round down players action point
    EbRegistration currentPlayerRegistration = p_game.getRegistration( getOldPlayerId( p_game ) );
    if( currentPlayerRegistration == null )
    {
      // special case where this action is automatic and no player have to play
      // this likely to quickly finish the game
      game.setCurrentTimeStep( (short)game.getCurrentTimeStep() + 1 );
      return;
    }
    // backup for unexec
    m_oldActionPt = currentPlayerRegistration.getPtAction();
    currentPlayerRegistration.setPtAction( currentPlayerRegistration.getRoundedActionPt(p_game) );
    m_oldCurrentPlayersCount = game.getCurrentPlayerIds().size();
    m_oldTurn = (short)game.getCurrentTimeStep();
    m_newTurn = m_oldTurn;


    if( game.getCurrentPlayerIds().size() > 1 )
    {
      // several player are playing at the same time.
      // so this action only end turn of one single player
      game.getCurrentPlayerIds().remove( (Long)getOldPlayerId( p_game ) );
    }
    else
    {
      // assume there is one and only one current player

      // next player
      EbTeam nextTeam = null;
      if( game.isTimeStepParallelHidden( m_oldTurn ) )
      {
        nextTeam = game.getNextTeam2Play( -1 );
      }
      else
      {
        nextTeam = game.getNextTeam2Play( currentPlayerRegistration.getTeam(p_game)
            .getOrderIndex() );
      }

      if( nextTeam.getOrderIndex() <= currentPlayerRegistration.getTeam(p_game).getOrderIndex() )
      {
        // next turn !
        m_newTurn++;
        if( game.isTimeStepParallelHidden( m_oldTurn ) )
        {
          // play in main event stack all events in registrations
          playRegistrationEvents( p_game );

          // compute next turn
          while( game.isTimeStepParallelHidden( m_newTurn ) )
            m_newTurn++;

          nextTeam = game.getNextTeam2Play( -1 );
        }
        game.setCurrentTimeStep( m_newTurn );
        if( game.isParallel() )
        {
          // this is the real start time for parallele game
          // as player may took a while to land
          // start parallel mode
          p_game.setLastTimeStepChange( new Date() );
        }
      }

      // reset all end turn date
      for( EbTeam team : game.getTeams() )
      {
        team.setEndTurnDate( null );
      }
      game.getCurrentPlayerIds().clear();

      // set current player
      if( game.isTimeStepParallelHidden( game.getCurrentTimeStep() )
          || (game.isParallel() && game.getCurrentTimeStep() > 1) )
      {
        // all players become current players !
        // except those without any landed freighter
        for( EbRegistration registration : game.getSetRegistration() )
        {
          addCurrentPlayer( game, registration );
        }
      }
      else
      {
        for( EbRegistration registration : nextTeam.getPlayers( p_game.getPreview() ) )
        {
          addCurrentPlayer( game, registration );
        }
      }
      
      // update ore generator
      for( EbToken token : p_game.getSetToken() )
      {
        if( (token.getType() == TokenType.Ore2Generator || token.getType() == TokenType.Ore3Generator)
            && token.getLocation() == Location.Board )
        {
          if( game.getAllToken( token.getPosition() ).size() >= 2 )
          {
            token.setBulletCount( 0 );
          } else if( token.getBulletCount() >= game.getTeams().size() ) {
            // create new ore token !
            token.setBulletCount( 0 );
            EbToken oreToken = new EbToken( TokenType.Ore );
            if( token.getType() == TokenType.Ore3Generator )
            {
              oreToken.setType( TokenType.Ore3 );
            }
            game.moveToken( oreToken, token.getPosition() );
            addOreToRemoveWhileUnexec( oreToken );
          } else {
            token.setBulletCount( token.getBulletCount()+1 );
          }
        }
      }
      if( oreToRemoveWhileUnexec != null )
      {
        for( EbToken ore : oreToRemoveWhileUnexec )
        {
          game.addToken( ore );
        }
      }
      
    }
  }

  private void addOreToRemoveWhileUnexec(EbToken ore)
  {
    if( oreToRemoveWhileUnexec == null )
    {
      oreToRemoveWhileUnexec = new HashSet<EbToken>();
    }
    oreToRemoveWhileUnexec.add( ore );
  }

  private void addCurrentPlayer(Game p_game, EbRegistration nextPlayerRegistration)
  {
    if( nextPlayerRegistration == null )
      return;
    if( nextPlayerRegistration.getOnBoardFreighterCount( p_game ) <= 0
        || !nextPlayerRegistration.haveAccount() )
    {
      return;
    }

    // update all his tokens bullets count
    EnuColor nextPlayerColor = nextPlayerRegistration.getEnuColor();
    for( EbToken token : p_game.getSetToken() )
    {
      if( token.getType() != TokenType.Freighter
          && token.getBulletCount() < token.getType().getMaxBulletCount()
          && nextPlayerColor.isColored( token.getColor() ) )
      {
        token.setBulletCount( token.getBulletCount()
            + p_game.getEbConfigGameTime().getBulletCountIncrement() );
      }
    }

    // parallel hidden phase correspond to deployment or take off phase:
    // don't add action points
    if( !p_game.isTimeStepParallelHidden( p_game.getCurrentTimeStep() ) )
    {
      int actionInc = nextPlayerRegistration.getActionInc( p_game );
      int actionPt = nextPlayerRegistration.getPtAction() + actionInc;
      if( actionPt > nextPlayerRegistration.getMaxActionPt( p_game ) )
      {
        actionPt = nextPlayerRegistration.getMaxActionPt( p_game );
      }
      nextPlayerRegistration.setPtAction( actionPt );
    }

    // set End turn date for future current player
    if( p_game.getCurrentTimeStep() > 1
        && p_game.getEbConfigGameTime().getTimeStepDurationInSec() != 0 )
    {
      if( m_endTurnDate == null )
      {
        m_endTurnDate = new Date( SharedMethods.currentTimeMillis()
            + p_game.getEbConfigGameTime().getTimeStepDurationInMili() );
      }
      nextPlayerRegistration.getTeam(p_game).setEndTurnDate( m_endTurnDate );
    }
    p_game.getCurrentPlayerIds().add( nextPlayerRegistration.getId() );

  }

  private void playRegistrationEvents(Game p_game)
  {
    List<EbTeam> teams = p_game.getTeamByPlayOrder();
    int eventIndex[] = new int[teams.size()];
    boolean morePrivateEvent = true;
    while( morePrivateEvent )
    {
      morePrivateEvent = false;
      for( int playerIndex = 0; playerIndex < teams.size(); playerIndex++ )
      {
        boolean eventAdded = false;
        while( !eventAdded
            && eventIndex[playerIndex] < teams.get( playerIndex ).getMyEvents().size() )
        {
          morePrivateEvent = true;
          try
          {
            AnEvent event = teams.get( playerIndex ).getMyEvents().get( eventIndex[playerIndex] );
            event.exec( p_game );
            p_game.addEvent( event );
            eventAdded = true;
          } catch( RpcFmpException e )
          {
          }
          eventIndex[playerIndex]++;
        }
      }
    }
    // remove all private events
    for( EbTeam team : teams )
    {
      team.clearMyEvents();
    }
  }

  /**
   * note that this method do not set the player's end turn date.
   * @see com.fullmetalgalaxy.model.persist.gamelog.AnEvent2#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    assert p_game != null;

    EbRegistration oldPlayerRegistration = p_game.getRegistration( getOldPlayerId( p_game ) );
    if( oldPlayerRegistration == null )
    {
      // special case where this action is automatic and no player have to play
      // this likely to quickly finish the game
      p_game.setCurrentTimeStep( (short)p_game.getCurrentTimeStep() - 1 );
      return;
    }

    if( m_oldCurrentPlayersCount > 1 )
    {
      p_game.getCurrentPlayerIds().add( (Long)getOldPlayerId( p_game ) );
    }
    else
    {
      // remove action point to all current players
      for( long currentPlayerId : p_game.getCurrentPlayerIds() )
      {
        EbRegistration registration = p_game.getRegistration( currentPlayerId );
        if( registration != null )
        {
          int actionInc = registration.getActionInc( p_game );
          int actionPt = registration.getPtAction() - actionInc;
          if( actionPt < 0 )
          {
            actionPt = 0;
          }
          registration.setPtAction( actionPt );
        }
      }

      // previous player
      p_game.getCurrentPlayerIds().clear();
      EbRegistration previousPlayer = p_game.getRegistration( getOldPlayerId( p_game ) );
      if( previousPlayer != null )
      {
        previousPlayer.setPtAction( m_oldActionPt );
        p_game.getCurrentPlayerIds().add( previousPlayer.getId() );
      }
      p_game.setCurrentTimeStep( m_oldTurn );
    }

    // eventually remove ore from generator
    if( oreToRemoveWhileUnexec != null )
    {
      for( EbToken ore : oreToRemoveWhileUnexec )
      {
        for( EbToken token : p_game.getAllToken( ore.getPosition() ) )
        {
          if( token.getType() == TokenType.Ore2Generator
              || token.getType() == TokenType.Ore3Generator )
          {
            token.setBulletCount( 2 );
          }
        }
      }
      p_game.getSetToken().removeAll( oreToRemoveWhileUnexec );
    }

  }

  protected MessagesRpcException errMsg()
  {
    return SharedI18n.getMessagesError( getAccountId() );
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

  public short getOldTurn()
  {
    return m_oldTurn;
  }

  public short getNewTurn()
  {
    return m_newTurn;
  }



}
