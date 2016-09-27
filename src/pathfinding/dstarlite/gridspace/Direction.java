/*
Copyright (C) 2016 Pierre-François Gimenez

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package pathfinding.dstarlite.gridspace;

/**
 * Les différentes directions dans une grille
 * @author pf
 *
 */

public enum Direction {

	NO(-1,1),SE(1,-1),NE(1,1),SO(-1,-1),
	N(0,1),S(0,-1),O(-1,0),E(1,0);

	public final int deltaX, deltaY;
	public final double distance;
	
	private Direction(int deltaX, int deltaY)
	{
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		distance = (Math.max(deltaX, deltaY) + 0.414 * Math.min(deltaX, deltaY)) * PointGridSpace.DISTANCE_ENTRE_DEUX_POINTS;
	}
	
	/**
	 * Cette direction est-elle diagonale ?
	 * @return
	 */
	public boolean isDiagonal()
	{
		return ordinal() < 4;
	}
	
	/**
	 * Fournit la direction opposée
	 * @return
	 */
	public Direction getOppose()
	{
		return values()[ordinal() ^ 1]; // ouais ouais
	}
	
	private final static double seuil = Math.sqrt(2 - Math.sqrt(2)) / 2; // cos(3pi/8)
	
	/**
	 * Fournit la direction la plus proche de l'orientation donnée
	 * @param orientation
	 * @return
	 */
	public static Direction getDirection(double orientation)
	{
		double cos = Math.cos(orientation);
		double sin = Math.sin(orientation);
		
		int deltaX = 0;
		if(cos > seuil)
			deltaX = 1;
		else if(cos < -seuil)
			deltaX = -1;
		
		int deltaY = 0;
		if(sin > seuil)
			deltaY = 1;
		else if(sin < -seuil)
			deltaY = -1;
		
		for(Direction d : values())
			if(d.deltaX == deltaX && d.deltaY == deltaY)
				return d;
		
		return null;
	}
	
}
