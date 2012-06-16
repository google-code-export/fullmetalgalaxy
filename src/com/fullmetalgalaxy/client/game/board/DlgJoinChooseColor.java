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
package com.fullmetalgalaxy.client.game.board;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;
import com.fullmetalgalaxy.model.ressources.Messages;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Kroc
 * 
 * During the game join process, this dialog ask player to choose his color.
 */

public class DlgJoinChooseColor extends DialogBox
{
  // UI
  private ListBox m_colorSelection = new ListBox();
  private Image m_preview = new Image();

  private Button m_btnOk = new Button( MAppBoard.s_messages.ok() );
  private Button m_btnCancel = new Button( MAppBoard.s_messages.cancel() );
  private Panel m_panel = new FlowPanel();

  private static DlgJoinChooseColor s_dlg = null;

  public static DlgJoinChooseColor instance()
  {
    if( s_dlg == null )
    {
      s_dlg = new DlgJoinChooseColor();
    }
    return s_dlg;
  }

  /**
   * 
   */
  public DlgJoinChooseColor()
  {
    // auto hide / modal
    super( false, true );

    // Set the dialog box's caption.
    setText( "Choisissez votre couleur" );

    // add color list widget
    for( int colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
    {
      EnuColor color = EnuColor.getColorFromIndex( colorIndex );
      m_colorSelection.addItem( Messages.getColorString( 0, color.getValue() ) );
    }
    m_colorSelection.addChangeHandler( new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent p_event)
      {
        EnuColor color = EnuColor.getColorFromIndex( m_colorSelection.getSelectedIndex() );
        m_preview.setUrl( "/images/board/" + color.toString() + "/preview.png" );
      }
    } );
    m_panel.add( m_colorSelection );
    m_panel.add( m_preview );

    // add cancel button
    m_btnCancel.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        hide();
      }
    } );
    m_panel.add( m_btnCancel );

    // add OK button
    m_btnOk.addClickHandler( new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent p_event)
      {
        EnuColor color = EnuColor.getColorFromIndex( m_colorSelection.getSelectedIndex() );
        EbGameJoin action = new EbGameJoin();
        action.setGame( GameEngine.model().getGame() );
        action.setAccountId( AppMain.instance().getMyAccount().getId() );
        action.setAccount( AppMain.instance().getMyAccount() );
        action.setColor( color.getValue() );
        GameEngine.model().runSingleAction( action );
        hide();
      }
    } );
    m_panel.add( m_btnCancel );


    setWidget( m_panel );
  }

}
