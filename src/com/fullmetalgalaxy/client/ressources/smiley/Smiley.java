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
package com.fullmetalgalaxy.client.ressources.smiley;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Smiley extends ClientBundle
{
  Smiley INSTANCE = GWT.create(Smiley.class);

  ImageResource bell();

  ImageResource cool();

  @Source("sad.png")
  ImageResource cry();

  ImageResource devil();

  ImageResource grimace();

  ImageResource heart();

  @Source("robot.png")
  ImageResource indifferent();

  ImageResource lol();

  ImageResource no();

  ImageResource poo();

  ImageResource robot();

  ImageResource rock();

  ImageResource sad();

  ImageResource skeptical();

  ImageResource smile();

  ImageResource tongue();

  ImageResource wink();

}
