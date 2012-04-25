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
package com.fullmetalgalaxy.model.persist.gamelog;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.ressources.Messages;


/**
 * @author Vincent Legendre
 *
 */
public class EbEvtControlFreighter extends AnEventPlay
{
  static final long serialVersionUID = 1;

  private EbBase m_packedOldRegistration = null;
  private int m_oldRegistrationSingleColor = EnuColor.None;
  private boolean m_wasColorlessFreighter = false;

  /**
   * 
   */
  public EbEvtControlFreighter()
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
    setCost( 0 );
    setAuto( true );
    m_packedOldRegistration = null;
  }

  @Override
  public GameLogType getType()
  {
    return GameLogType.EvtControlFreighter;
  }


  @Override
  // @Transient
  public AnBoardPosition getSelectedPosition(Game p_game)
  {
    if( getToken(p_game) != null )
    {
      return getToken(p_game).getPosition();
    }
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#check()
   */
  @Override
  public void check(Game p_game) throws RpcFmpException
  {
    super.check(p_game);

    if( getTokenCarrier(p_game).getType() != TokenType.Freighter )
    {
      // no i18n
      throw new RpcFmpException( "le pion controlé doit être un astronef" );
    }
    if( !getToken(p_game).isNeighbor( getTokenCarrier(p_game) ) )
    {
      // no i18n
      throw new RpcFmpException( getToken(p_game) + " doit etre au contact de " + getTokenCarrier(p_game)
          + " pour le controler" );
    }
    // check that token is colored
    if( getToken(p_game).getColor() == EnuColor.None )
    {
      // no i18n
      throw new RpcFmpException( "vous ne pouvez pas déplacer des pions incolores" );
    }

    // check that player control the token color
    EbRegistration myRegistration = getMyRegistration(p_game);
    assert myRegistration != null;
    if( !myRegistration.getEnuColor().isColored( getToken(p_game).getColor() ) )
    {
      throw new RpcFmpException( errMsg().CantMoveDontControl(
          Messages.getColorString( getAccountId(), getToken( p_game ).getColor() ),
          Messages.getColorString( getAccountId(), myRegistration.getColor() ) ) );
    }
    // check that token isn't under opponents fire covers
    EnuColor fireCoverColor = p_game.getOpponentFireCover( myRegistration.getColor(),
        getToken(p_game).getPosition() );
    if( fireCoverColor.getValue() != EnuColor.None )
    {
      throw new RpcFmpException( errMsg().CantMoveDisableFire( Messages.getTokenString( getAccountId(), getToken(p_game).getType()),
          ( Messages.getColorString( getAccountId(),  fireCoverColor.getValue() ))));
    }
    // check presence of turrets
    for( AnBoardPosition position : getTokenCarrier(p_game).getExtraPositions() )
    {
      EbToken turret = p_game.getToken( position, TokenType.Turret );
      if( turret != null )
      {
        // no i18n ?
        throw new RpcFmpException( "You must destroy all turrets of an opponents freighter before taking control of it." );
      }
    }

    // check that controlling token is a destroyer
    if( !getToken( p_game ).isDestroyer() )
    {
      throw new RpcFmpException( errMsg().OnlyDestroyerCanControl() );
    }

    // check that player have at least one action point to control freighter
    if( myRegistration.getPtAction() <= 0 )
    {
      throw new RpcFmpException( errMsg().NotEnouthActionPt() );
    }

    // player have extra action points.
    assert getCost() == -1 * p_game.getEbConfigGameVariant().getActionPtMaxPerExtraShip();

  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#exec()
   */
  @Override
  public void exec(Game p_game) throws RpcFmpException
  {
    super.exec(p_game);

    if( getTokenFreighter( p_game ).getColor() == EnuColor.None )
    {
      // uncolored freighter is a special case where no other player is
      // eliminated
      getTokenFreighter( p_game ).setColor( getToken( p_game ).getColor() );
      m_wasColorlessFreighter = true;
    }
    else
    {
      // backup old registration (used by unexec)
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        EnuColor color = registration.getEnuColor();
        if( color.isColored( getTokenFreighter( p_game ).getColor() ) )
        {
          setOldRegistration( registration );
          m_oldRegistrationSingleColor = registration.getSingleColor();
        }
      }

      if( getOldRegistration( p_game ) != null )
      {
        getOldRegistration( p_game ).setColor(
            EnuColor.removeColor( getOldRegistration( p_game ).getColor(),
                getTokenFreighter( p_game ).getColor() ) );
        if( getOldRegistration( p_game ).getColor() != EnuColor.None
            && !getOldRegistration( p_game ).getEnuColor().isColored(
                getOldRegistration( p_game ).getSingleColor() ) )
        {
          // player loose his main freighter but still have another one: change
          // his fire cover color
          getOldRegistration( p_game ).setSingleColor(
              getOldRegistration( p_game ).getEnuColor().getSingleColor().getValue() );
        }
      }
      // the new color owner
      getMyRegistration( p_game ).setColor(
          EnuColor.addColor( getMyRegistration( p_game ).getColor(), getTokenFreighter( p_game )
              .getColor() ) );
    }
    getMyRegistration(p_game).setTurretsToRepair( getMyRegistration(p_game).getTurretsToRepair() + 3 );

    execFireDisabling( p_game );
    // we need to force recomputing fire cover even if fire disabling flags was
    // computed to merge the two cover into a single color
    p_game.invalidateFireCover();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.AnAction#unexec()
   */
  @Override
  public void unexec(Game p_game) throws RpcFmpException
  {
    super.unexec(p_game);
    if( m_wasColorlessFreighter )
    {
      getTokenFreighter( p_game ).setColor( EnuColor.None );
    }
    else
    {
      // the new color owner
      getMyRegistration( p_game ).setColor(
          EnuColor.removeColor( getMyRegistration( p_game ).getColor(), getTokenFreighter( p_game )
              .getColor() ) );
      if( getOldRegistration( p_game ) != null )
      {
        getOldRegistration( p_game ).setColor(
            EnuColor.addColor( getOldRegistration( p_game ).getColor(), getTokenFreighter( p_game )
                .getColor() ) );
        // test is here for backward compatibility
        if( m_oldRegistrationSingleColor != EnuColor.None )
        {
          getOldRegistration( p_game ).setSingleColor( m_oldRegistrationSingleColor );
        }
      }
    }
    getMyRegistration(p_game).setTurretsToRepair( getMyRegistration(p_game).getTurretsToRepair() - 3 );

    p_game.invalidateFireCover();
    unexecFireDisabling( p_game );
  }

  /**
   * @return the packedOldRegistration
   */
  private EbBase getPackedOldRegistration()
  {
    return m_packedOldRegistration;
  }

  public EbToken getTokenFreighter(Game p_game)
  {
    return getTokenCarrier( p_game );
  }

  public void setTokenFreighter(EbToken p_token)
  {
    setTokenCarrier( p_token );
  }

  // cache to avoid researching again and again
  // and to implement getter
  // ===========================================
  transient private EbRegistration m_oldRegistration = null;

  public EbRegistration getOldRegistration(Game p_game)
  {
    if( m_oldRegistration == null )
    {
      m_oldRegistration = p_game.getRegistration( getPackedOldRegistration().getId() );
    }
    return m_oldRegistration;
  }

  private void setOldRegistration(EbRegistration p_oldRegistration)
  {
    m_packedOldRegistration = p_oldRegistration.createEbBase();
    m_oldRegistration = p_oldRegistration;
  }

}
