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
package com.fullmetalgalaxy.client.game.board;


import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.tokens.TokenImages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Vincent Legendre
 * display the currently selected token and highlight hexagon
 */
public class WgtBoardLayerSelect extends WgtBoardLayerBase
{
  protected Image m_hexagonHightlight = new Image();

  protected Image m_hexagonSelect = new Image();

  /**
   * last update of the currently displayed action
   */
  protected long m_actionLastUpdate = 0;

  /**
   * 
   */
  public WgtBoardLayerSelect()
  {
    super();
    BoardIcons.select_hexagon( getZoom().getValue() ).applyTo( m_hexagonSelect );
    add( m_hexagonSelect, 0, 0 );
    m_hexagonSelect.setVisible( false );
    getHighLightImage().applyTo( m_hexagonHightlight );
    add( m_hexagonHightlight, 0, 0 );
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    EventsPlayBuilder action = GameEngine.model().getActionBuilder();
    if( action.getLastUpdate().getTime() != m_actionLastUpdate || p_forceRedraw )
    {
      redrawAction();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    getHighLightImage().applyTo( m_hexagonHightlight );
    BoardIcons.select_hexagon( getZoom().getValue() ).applyTo( m_hexagonSelect );
    redrawAction();
  }

  private AbstractImagePrototype getHighLightImage()
  {
    EventsPlayBuilder actionBuilder = GameEngine.model().getActionBuilder();
    if( actionBuilder.getSelectedAction() != null
        && (actionBuilder.getSelectedAction().getType() == GameLogType.EvtLand || actionBuilder
            .getSelectedAction().getType() == GameLogType.EvtDeployment) )
    {
      AnEventPlay action = (AnEventPlay)actionBuilder.getSelectedAction();
      return AbstractImagePrototype.create( TokenImages.getTokenImage(
          action.getToken( GameEngine.model().getGame() ), getZoom().getValue() ) );
    }
    return BoardIcons.hightlight_hexagon( getZoom().getValue() );
  }

  /**
   * redraw the full action layer.  
   */
  protected void redrawAction()
  {
    EventsPlayBuilder actionBuilder = GameEngine.model().getActionBuilder();
    m_actionLastUpdate = actionBuilder.getLastUpdate().getTime();

    if( actionBuilder.isBoardTokenSelected() || actionBuilder.isEmptyLandSelected() )
    {
      m_hexagonSelect.setVisible( true );
      setWidgetHexPosition( m_hexagonSelect, actionBuilder.getSelectedPosition() );
    }
    else
    {
      getHighLightImage().applyTo( m_hexagonHightlight );
      m_hexagonSelect.setVisible( false );
    }
  }


  public void moveHightLightHexagon(AnBoardPosition p_anBoardPosition)
  {
    AnEventPlay evDeploy = GameEngine.model().getActionBuilder().getSelectedAction();
    if( evDeploy != null && evDeploy.getType() == GameLogType.EvtDeployment )
    {
      int distance = GameEngine.coordinateSystem().getDiscreteDistance(
          evDeploy.getToken( GameEngine.model().getGame() ).getCarrierToken().getPosition(), p_anBoardPosition );
      if( distance > FmpConstant.deployementRadius )
      {
        BoardIcons.hightlight_hexagon( getZoom().getValue() ).applyTo( m_hexagonHightlight );
      }
      else
      {
        getHighLightImage().applyTo( m_hexagonHightlight );
      }
    }
    setWidgetHexPosition( m_hexagonHightlight, p_anBoardPosition );
  }

  public void setHexagonHightVisible(boolean p_visible)
  {
    m_hexagonHightlight.setVisible( p_visible );
  }

  @Override
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex,
      int p_cropBotomHex)
  {
    super.cropDisplay( p_cropLeftHex, p_cropTopHex, p_cropRightHex, p_cropBotomHex );
    redraw();
  }

}
