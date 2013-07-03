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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server.ai;

import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtLand;

/**
 * @author Vincent
 * 
 * some algorithm that are useful for creating automatic player
 */
public class AiUtils
{

  static public AnBoardPosition findLandingPosition(Game p_game, EbToken p_freighter)
  {
    AnBoardPosition Pos = new AnBoardPosition();
    for( Pos.setY( 2 ); Pos.getY() < p_game.getLandHeight() - 3; Pos.setY( Pos.getY() + 1 ) )
    {
      for( Pos.setX( 2 ); Pos.getX() < p_game.getLandWidth() - 3; Pos.setX( Pos.getX() + 1 ) )
      {
        Pos.setSector( Sector.North );
        if( EbEvtLand.isValidLandingPosition( p_game, Pos ) )
          return Pos;
        Pos.setSector( Sector.South );
        if( EbEvtLand.isValidLandingPosition( p_game, Pos ) )
          return Pos;

      }
    }
    return Pos;
  }



  /**
   * this method is taken from fmp.exe source code and translated into java
   * @author Jean-Marc Leroy
   * @author Vincent
   * 
   * @param p_game
   * @return
   */
  /*static public AnBoardPosition findLandingPosition(Game p_game, EbToken p_freighter)
  {
     AnBoardPosition Pos = new AnBoardPosition();
     int Selection;
     int Nb_Selection;
     int Pod, Cpt;
  
     AnBoardPosition Position = new AnBoardPosition();
     if( p_freighter.getLocation() != Location.Orbit )
     {
       return Position;
     }
     
     // Essai 0 : Plaine ou Marecage + 2 Mer + Montagne
     Nb_Selection = 0;
     Selection = 0;
     do {
        for (Pos.setY(1); Pos.getY() < p_game.getLandHeight()-1; Pos.setY(Pos.getY()+1))
        {
           for (Pos.setX(1); Pos.getX() < p_game.getLandWidth()-1; Pos.setX(Pos.getX()+1))
           {
             Pos.setSector( Sector.North );
              if (EbEvtLand.isValidLandingPosition( p_game, Pos) &&
                  ((p_game.getLand( Pos.getNeighbour( Sector.NorthEast ) ) == LandType.Montain) ||
                   (p_game.getLand(Pos.getNeighbour( Sector.NorthWest )) == LandType.Montain) ||
                   (p_game.getLand(Pos.getNeighbour( Sector.South )) == LandType.Montain)))
              {
                 Cpt = 0;
                 for (Pod = 0; Pod < 6; Pod += 2)
                 {
                    for (T_Secteur Secteur = k_Min_Couronne_1; Secteur <= k_Max_Couronne_1; Secteur++)
                    {
                       if (p_game.getLand(Pos[Pod][Secteur]) == e_Terrain_Mer)
                       {
                          if (Region.Taille(e_Domaine_Maritime, Pos[Pod][Secteur], e_Maree_Basse) > 10)
                          {
                             Cpt++;
                             break;
                          }
                       }
                    }
                 }
                 if (Cpt > 1)
                 {
                    Position    = Pos;
                    Orientation = k_Angle_000;
                    Nb_Selection++;
                    if (Nb_Selection == Selection) return;
                 }
              }
              Pos.setSector( Sector.South );
              if (EbEvtLand.isValidLandingPosition( p_game, Pos) &&
                  ((p_game.getLand(Pos[k_Secteur_000]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_120]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_240]) == LandType.Montain)))
              {
                 Cpt = 0;
                 for (Pod = 1; Pod < 6; Pod += 2)
                 {
                    for (T_Secteur Secteur = k_Min_Couronne_1; Secteur <= k_Max_Couronne_1; Secteur++)
                    {
                       if (p_game.getLand(Pos[Pod][Secteur]) == e_Terrain_Mer)
                       {
                          if (Region.Taille(e_Domaine_Maritime, Pos[Pod][Secteur], e_Maree_Basse) > 10)
                          {
                             Cpt++;
                             break;
                          }
                       }
                    }
                 }
                 if (Cpt > 1)
                 {
                    Position    = Pos;
                    Orientation = k_Angle_180;
                    Nb_Selection++;
                    if (Nb_Selection == Selection) return;
                 }
              }
           }
        }
        if (Nb_Selection > 0)
        {
           Selection = Systeme::Tirage(Nb_Selection) + 1;
           Nb_Selection = 0;
        }
     } while (Selection > 0);
  
     // Essai 1 : Plaine ou Marecage + e_Terrain_Mer + LandType.Montain
     Nb_Selection = 0;
     Selection = 0;
     do {
        for (Pos.Lig = 1; Pos.Lig < Carte.Nb_Ligne - 1; Pos.Lig++)
        {
           for (Pos.Col = 1; Pos.Col < Carte.Nb_Colonne - 1; Pos.Col++)
           {
              if (! Partie.Terrain_Atterrissage_Valide(Pos)) continue;
              if (! Partie.A_Distance_Autre_Astronef(Pos)) continue;
  
              if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_000) &&
                  ((p_game.getLand(Pos[k_Secteur_060]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_180]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_300]) == LandType.Montain)))
              {
                 for (T_Secteur Secteur = k_Min_Couronne_1; Secteur <= k_Max_Couronne_2; Secteur++)
                 {
                    if (! C_Astronef::Accessible_Depuis_Astronef(Secteur, k_Angle_000)) continue;
                    if (p_game.getLand(Pos[Secteur]) == e_Terrain_Mer)
                    {
                       if (Region.Taille(e_Domaine_Maritime, Pos[Secteur], e_Maree_Normale) > 10)
                       {
                          Position    = Pos;
                          Orientation = k_Angle_000;
                          Nb_Selection++;
                          if (Nb_Selection == Selection) return;
                          break;
                       }
                    }
                 }
              }
              if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_180) &&
                  ((p_game.getLand(Pos[k_Secteur_000]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_120]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_240]) == LandType.Montain)))
              {
                 for (T_Secteur Secteur = k_Min_Couronne_1; Secteur <= k_Max_Couronne_2; Secteur++)
                 {
                    if (! C_Astronef::Accessible_Depuis_Astronef(Secteur, k_Angle_180)) continue;
                    if (p_game.getLand(Pos[Secteur]) == e_Terrain_Mer)
                    {
                       if (Region.Taille(e_Domaine_Maritime, Pos[Secteur], e_Maree_Normale) > 10)
                       {
                          Position    = Pos;
                          Orientation = k_Angle_180;
                          Nb_Selection++;
                          if (Nb_Selection == Selection) return;
                          break;
                       }
                    }
                 }
              }
           }
        }
        if (Nb_Selection > 0)
        {
           Selection = Systeme::Tirage(Nb_Selection) + 1;
           Nb_Selection = 0;
        }
     } while (Selection > 0);
  
     // Essai 2 : Plaine ou Marecage + e_Terrain_Mer ou Recif ou Marecage
     for (Maree = e_Maree_Basse; Maree <= e_Maree_Haute; Maree = T_Maree (int (Maree) + 1))
     {
        Nb_Selection = 0;
        Selection = 0;
        do {
           for (Pos.Lig = 1; Pos.Lig < Carte.Nb_Ligne - 1; Pos.Lig++)
           {
              for (Pos.Col = 1; Pos.Col < Carte.Nb_Colonne - 1; Pos.Col++)
              {
                 if (! Partie.Terrain_Atterrissage_Valide(Pos)) continue;
                 if (! Partie.A_Distance_Autre_Astronef(Pos)) continue;
  
                 if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_000))
                 {
                    for (T_Secteur Secteur = k_Min_Couronne_1; Secteur <= k_Max_Couronne_2; Secteur++)
                    {
                       if (! C_Astronef::Accessible_Depuis_Astronef(Secteur, k_Angle_000)) continue;
                       if (p_game.getLand(Pos[Secteur], Maree) == e_Terrain_Mer)
                       {
                          if (Region.Taille(e_Domaine_Maritime, (Pos[Secteur]), e_Maree_Normale) > 10)
                          {
                             Position    = Pos;
                             Orientation = k_Angle_000;
                             Nb_Selection++;
                             if (Nb_Selection == Selection) return;
                             break;
                          }
                       }
                    }
                 }
                 if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_180))
                 {
                    for (T_Secteur Secteur = k_Min_Couronne_1; Secteur <= k_Max_Couronne_2; Secteur++)
                    {
                       if (! C_Astronef::Accessible_Depuis_Astronef(Secteur, k_Angle_180)) continue;
                       if (p_game.getLand(Pos[Secteur]) == e_Terrain_Mer)
                       {
                          if (Region.Taille(e_Domaine_Maritime, (Pos[Secteur]), e_Maree_Normale) > 10)
                          {
                             Position    = Pos;
                             Orientation = k_Angle_180;
                             Nb_Selection++;
                             if (Nb_Selection == Selection) return;
                             break;
                          }
                       }
                    }
                 }
              }
           }
           if (Nb_Selection > 0)
           {
              Selection = Systeme::Tirage(Nb_Selection) + 1;
              Nb_Selection = 0;
           }
        } while (Selection > 0);
     }
  
     // Essai 3 : Plaine ou Marecage
     Nb_Selection = 0;
     Selection = 0;
     do {
        for (Pos.Lig = 1; Pos.Lig < Carte.Nb_Ligne - 1; Pos.Lig++)
        {
           for (Pos.Col = 1; Pos.Col < Carte.Nb_Colonne - 1; Pos.Col++)
           {
              if (! Partie.Terrain_Atterrissage_Valide(Pos)) continue;
              if (! Partie.A_Distance_Autre_Astronef(Pos)) continue;
  
              if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_000) &&
                  ((p_game.getLand(Pos[k_Secteur_060]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_180]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_300]) == LandType.Montain)))
              {
                 Position    = Pos;
                 Orientation = k_Angle_000;
                 Nb_Selection++;
                 if (Nb_Selection == Selection) return;
              }
              if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_180) &&
                  ((p_game.getLand(Pos[k_Secteur_000]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_120]) == LandType.Montain) ||
                   (p_game.getLand(Pos[k_Secteur_240]) == LandType.Montain)))
              {
                 Position    = Pos;
                 Orientation = k_Angle_180;
                 Nb_Selection++;
                 if (Nb_Selection == Selection) return;
              }
           }
        }
        if (Nb_Selection > 0)
        {
           Selection = Systeme::Tirage(Nb_Selection) + 1;
           Nb_Selection = 0;
        }
     } while (Selection > 0);
  
     
     // Essai 4 : sans condition
     Nb_Selection = 0;
     Selection = 0;
     do {
        for (Pos.Lig = 1; Pos.Lig < Carte.Nb_Ligne - 1; Pos.Lig++)
        {
           for (Pos.Col = 1; Pos.Col < Carte.Nb_Colonne - 1; Pos.Col++)
           {
              if (! Partie.Terrain_Atterrissage_Valide(Pos)) continue;
              if (! Partie.A_Distance_Autre_Astronef(Pos)) continue;
  
              if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_000))
              {
                 Position    = Pos;
                 Orientation = k_Angle_000;
                 Nb_Selection++;
                 if (Nb_Selection == Selection) return;
              }
              if (Partie.Terrain_Atterrissage_Valide(Pos, k_Angle_180))
              {
                 Position    = Pos;
                 Orientation = k_Angle_180;
                 Nb_Selection++;
                 if (Nb_Selection == Selection) return;
              }
           }
        }
        if (Nb_Selection > 0)
        {
           Selection = Systeme::Tirage(Nb_Selection) + 1;
           Nb_Selection = 0;
        }
     } while (Selection > 0);
  }
  */

}
