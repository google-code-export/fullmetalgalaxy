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
package com.fullmetalgalaxy.client.chat;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.MAppMessagesStack;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.client.game.board.MAppBoard;
import com.fullmetalgalaxy.model.ChatMessage;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Vincent Legendre
 *
 */
public class DlgChatInput extends DialogBox implements ClickHandler, KeyDownHandler
{
  // UI
  private Button m_btnOk = new Button( MAppBoard.s_messages.ok() );
  private Panel m_panel = new HorizontalPanel();
  private TextBox m_text = new TextBox();
  private boolean m_isChatMode = false;

  private static DlgChatInput m_dlg = null;
  
  public static void showDialog()
  {
    if( m_dlg == null )
    {
      m_dlg = new DlgChatInput();
    }
    m_dlg.center();
    m_dlg.show();
  }
  
  public DlgChatInput()
  {
    // auto hide / modal
    super( true, true );

    // Set the dialog box's caption.
    setText( "tapez votre message" );
    m_text.addKeyDownHandler( this );
    m_text.setWidth( "400px" );
    m_panel.add( m_text );

    m_btnOk.addClickHandler( this );
    m_btnOk.setWidth( "50px" );
    m_panel.add( m_btnOk );

    setWidget( m_panel );
  }




  protected void sendMessage()
  {
    ChatEngine.sendMessage( m_text.getText() );
    hide();
  }


  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent p_event)
  {
    if( p_event.getSource() == m_btnOk )
    {
      sendMessage();
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.KeyUpHandler#onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent)
   */
  @Override
  public void onKeyDown(KeyDownEvent p_event)
  {
    switch( p_event.getNativeKeyCode() )
    {
    case KeyCodes.KEY_ESCAPE:
      hide();
      p_event.stopPropagation();
      break;
    case KeyCodes.KEY_ENTER:
      sendMessage();
      p_event.stopPropagation();
      p_event.getNativeEvent().stopPropagation();
      break;
    default:
      break;
    }
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show()
  {
    if( isChatMode() )
    {
      return;
    }
    m_text.setText( "" );
    m_isChatMode = true;
    // center call show method
    // center();
    DeferredCommand.addCommand( new Command()
    {
      public void execute()
      {
        m_text.setFocus( true );
      }
    } );
    super.show();
  }

  /**
   * this timer is here to avoid show event right after hide event
   */
  private Timer m_setChatFalseTimer = new Timer()
  {
    @Override
    public void run()
    {
      m_isChatMode = false;
    }
  };


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#hide()
   */
  @Override
  public void hide()
  {
    super.hide();
    m_setChatFalseTimer.schedule( 300 );
  }


  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#hide(boolean)
   */
  @Override
  public void hide(boolean p_autoClosed)
  {
    super.hide( p_autoClosed );
    m_setChatFalseTimer.schedule( 300 );
  }


  public static boolean isChatMode()
  {
    if( m_dlg == null )
    {
      return false;
    }
    return m_dlg.m_isChatMode;
  }

}
