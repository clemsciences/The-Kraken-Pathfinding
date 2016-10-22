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

package utils;

import java.io.Serializable;

/**
 * Vecteur en lecture seule
 * @author pf
 *
 */

public class Vec2RO implements Serializable
{
	private static final long serialVersionUID = 1L;
	protected volatile double x;
	protected volatile double y;
	
//	private static long t = System.currentTimeMillis();
	
	public Vec2RO(double longueur, double angle, boolean useless)
	{
//		if(System.currentTimeMillis() - t > 4000)
//			Thread.dumpStack();
		x = Math.cos(angle)*longueur;
		y = Math.sin(angle)*longueur;
	}

	public Vec2RO(double requestedX, double requestedY)
	{
//		if(System.currentTimeMillis() - t > 4000)
//			Thread.dumpStack();
		x = requestedX;
		y = requestedY;
	}

	public final double squaredLength()
	{
		return x*x + y*y;
	}

	public final double length()
	{
		return Math.hypot(x, y);
	}
	
	public final double dot(Vec2RO other)
	{
		return x*other.x + y*other.y;
	}

	public final Vec2RW plusNewVector(Vec2RO other)
	{
		return new Vec2RW(x + other.x, y + other.y);
	}
	
	public final Vec2RW minusNewVector(Vec2RO other)
	{
		return new Vec2RW(x - other.x, y - other.y);
	}

	@Override
	public final Vec2RW clone()
	{
		return new Vec2RW(this.x, this.y);
	}

	public final double squaredDistance(Vec2RO other)
	{
		double tmp_x = x-other.x, tmp_y = y-other.y;
		return tmp_x*tmp_x + tmp_y*tmp_y;
	}

	public final double distance(Vec2RO other)
	{
		return Math.hypot(x-other.x, y-other.y);
	}

	@Override
	public final String toString()
	{
		return "("+x+","+y+")";
	}
	
	public final boolean equals(Vec2RO other)
	{
		return x == other.x && y == other.y;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (!(obj instanceof Vec2RO))
			return false;

		Vec2RO other = (Vec2RO) obj;
		if (x != other.x || (y != other.y))
			return false;
		return true;
	}

	/**
	 * Copie this dans other.
	 * @param other
	 */
	public final void copy(Vec2RW other)
	{
	    other.x = x;
	    other.y = y;
	}

	public final Vec2RW rotateNewVector(double angle, Vec2RO centreRotation)
	{
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new Vec2RW(cos*(x-centreRotation.x)-sin*(y-centreRotation.y)+centreRotation.x,
		sin*(x-centreRotation.x)+cos*(y-centreRotation.y)+centreRotation.y);
	}

	public final double getArgument()
	{
		return Math.atan2(y, x);
	}

	@Override
	public int hashCode() {
		return (int) Math.round((x+1500)*2000+y);
	}
	
	public final double getX()
	{
		return x;
	}

	public final double getY()
	{
		return y;
	}

	public boolean isHorsTable()
	{
		return x < -1500 || x > 1500 || y < 0 || y > 2000;
	}
}
