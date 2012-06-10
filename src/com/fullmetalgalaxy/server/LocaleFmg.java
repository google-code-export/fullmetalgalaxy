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

import java.util.Locale;

/**
 * @author Vincent Legendre
 *
 * All supported locale in FMG
 */
public enum LocaleFmg
{
  en, fr;
  
  /**
   * 
   * @return corresponding standard java locale
   */
  public Locale locale()
  {
    switch( this )
    {
    case fr:
      return Locale.FRENCH;
    default:
    case en:
      return Locale.ENGLISH;
    }
  }

  public static LocaleFmg getDefault()
  {
    return LocaleFmg.fr;
  }
  
  public static LocaleFmg fromString(String p_locale)
  {
    LocaleFmg locale = null;
    try
    {
      locale = LocaleFmg.valueOf( p_locale );
    } catch( Exception e )
    {
    }
    if( locale == null )
    {
      locale = getDefault();
    }
    return locale;
  }

}
