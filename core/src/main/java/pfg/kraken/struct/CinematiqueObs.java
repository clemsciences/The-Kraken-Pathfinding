/*
 * Copyright (C) 2013-2018 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.kraken.struct;

import java.io.Serializable;

import pfg.kraken.memory.MemPoolState;
import pfg.kraken.memory.Memorizable;
import pfg.kraken.obstacles.RectangularObstacle;

/**
 * Une cinématique + un obstacle associé (ainsi que des vitesses)
 * 
 * @author pf
 *
 */

public final class CinematiqueObs implements Memorizable, Serializable
{
	public final Cinematique cinem;
	
	/**
	 * No need to port
	 */
	private static final long serialVersionUID = 1L;
	public volatile RectangularObstacle obstacle;
	public volatile double maxSpeed; // in m/s
	public volatile double possibleSpeed; // in m/s

	/**
	 * No need to port
	 */
	private volatile int indiceMemory;
	
	public String toString()
	{
		return super.toString()+", maxSpeed = "+maxSpeed;
	}
	
	public CinematiqueObs(RectangularObstacle vehicleTemplate)
	{
		cinem = new Cinematique();
		obstacle = vehicleTemplate.clone();
		maxSpeed = -1;
	}

	public void copy(CinematiqueObs autre)
	{
		cinem.copy(autre.cinem);
		autre.maxSpeed = maxSpeed;
		autre.possibleSpeed = possibleSpeed;
		obstacle.copy(autre.obstacle);
	}

	/**
	 * No need to port
	 */
	@Override
	public void setIndiceMemoryManager(int indice)
	{
		indiceMemory = indice;
	}
	
	/**
	 * No need to port
	 */
	@Override
	public int getIndiceMemoryManager()
	{
		return indiceMemory;
	}

	/**
	 * Met à jour les données
	 * 
	 * @param x
	 * @param y
	 * @param orientation
	 * @param enMarcheAvant
	 * @param courbure
	 * @param vitesseMax
	 * @param obstacle
	 * @param r
	 */
	public void update(double x, double y, double orientationGeometrique, boolean enMarcheAvant, double courbure, double rootedMaxAcceleration, boolean stop, double maxSpeedLUT)
	{
		cinem.update(x, y, orientationGeometrique, enMarcheAvant, courbure, stop);
		maxSpeed = rootedMaxAcceleration * maxSpeedLUT;
		obstacle.update(cinem.position, cinem.orientationReelle);
	}
	
	public void updateWithMaxSpeed(double x, double y, double orientationGeometrique, boolean enMarcheAvant, double courbure, double rootedMaxAcceleration, double maxSpeed, boolean stop, double maxSpeedLUT)
	{
		cinem.update(x, y, orientationGeometrique, enMarcheAvant, courbure, stop);
		this.maxSpeed = Math.min(maxSpeed, rootedMaxAcceleration * maxSpeedLUT);
		obstacle.update(cinem.position, cinem.orientationReelle);
	}
	
/*	public void updateReel(double x, double y, double orientationReelle, double courbure, double rootedMaxAcceleration)
	{
		super.updateReel(x, y, orientationReelle, courbure);
		maxSpeed = rootedMaxAcceleration * maxSpeedLUT[(int) Math.round(Math.abs(10*courbure))];
		obstacle.update(position, orientationReelle);
	}*/

	/**
	 * No need to port
	 */
	private volatile MemPoolState state = MemPoolState.FREE;
	
	/**
	 * No need to port
	 */
	@Override
	public void setState(MemPoolState state)
	{
		this.state = state;
	}
	
	/**
	 * No need to port
	 */
	@Override
	public MemPoolState getState()
	{
		return state;
	}
	
	@Override
	public CinematiqueObs clone()
	{
		CinematiqueObs out = new CinematiqueObs(obstacle);
		copy(out);
		return out;
	}

	public void update(ItineraryPoint p)
	{
		cinem.update(p);
		maxSpeed = p.maxSpeed;
		possibleSpeed = p.possibleSpeed;
		obstacle.update(cinem.position, cinem.orientationReelle);
	}
}
