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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.EnuZoom;


/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerGrid extends WgtBoardLayerBase
{

  /**
   * 
   */
  public WgtBoardLayerGrid()
  {
    setStyleName( "fmp-grid-tactic" );
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    switch( p_zoom.getValue() )
    {
    default:
    case EnuZoom.Medium:
      setStyleName( "fmp-grid-tactic" );
      break;
    case EnuZoom.Small:
      setStyleName( "fmp-grid-strategy" );
      break;
    }
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.WgtBoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    if( isVisible() != ModelFmpMain.model().isGridDisplayed() )
    {
      setVisible( ModelFmpMain.model().isGridDisplayed() );
    }
  }



  @SuppressWarnings("unused")
  private static int s_firstGridRuleIndex = createGridRules();

  public static int createGridRules()
  {
    int oldLength = ClientUtil.setCssRule( ".fmp-grid-tactic",
        "{background: url(images/board/desert/tactic/grid.gif);}" ) - 1;
    ClientUtil.setCssRule( ".fmp-grid-strategy",
        "{background: url(images/board/desert/strategy/grid.gif);}" );
    return oldLength;
  }
}
