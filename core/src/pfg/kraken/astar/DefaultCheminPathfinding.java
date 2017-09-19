/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.kraken.astar;

import java.awt.Graphics;
import java.util.LinkedList;
import pfg.config.Config;
import pfg.graphic.Chart;
import pfg.graphic.GraphicPanel;
import pfg.graphic.PrintBuffer;
import pfg.graphic.printable.Layer;
import pfg.graphic.printable.Printable;
import pfg.graphic.printable.PrintablePoint;
import pfg.kraken.ConfigInfoKraken;
import pfg.kraken.ColorKraken;
import pfg.kraken.robot.Cinematique;
import pfg.kraken.robot.ItineraryPoint;
import pfg.log.Log;

/**
 * Faux chemin, sert à la prévision d'itinéraire
 * 
 * @author pf
 *
 */

public class DefaultCheminPathfinding implements CheminPathfindingInterface, Printable
{
	private static final long serialVersionUID = -9020733512144987231L;
	private LinkedList<ItineraryPoint> path;
	protected Log log;
	private PrintBuffer buffer;
	private boolean print;
	private PrintablePoint[] aff = new PrintablePoint[256];

	public DefaultCheminPathfinding(Log log, Config config, PrintBuffer buffer)
	{
		this.log = log;
		this.buffer = buffer;
		print = config.getBoolean(ConfigInfoKraken.GRAPHIC_TRAJECTORY_FINAL);
		if(print)
			buffer.add(this);
	}

	@Override
	public synchronized void addToEnd(LinkedList<ItineraryPoint> points)
	{
		path = points;
		if(print)
			synchronized(buffer)
			{
				buffer.notify();
			}
		notify();
	}

	@Override
	public void setUptodate()
	{}

	public LinkedList<ItineraryPoint> getPath()
	{
		LinkedList<ItineraryPoint> out = path;
		path = null;
		return out;
	}

	@Override
	public void print(Graphics g, GraphicPanel f, Chart a)
	{
		int i = 0;
		if(path != null)
			for(ItineraryPoint c : path)
			{
				aff[i] = new PrintablePoint(c.x, c.y, ColorKraken.TRAJECTOIRE.layer, ColorKraken.TRAJECTOIRE.color);
				buffer.addSupprimable(aff[i]);
				i++;
			}
	}

	@Override
	public int getLayer()
	{
		return Layer.FOREGROUND.ordinal();
	}

	@Override
	public boolean aAssezDeMarge()
	{
		return true;
	}

	@Override
	public boolean needStop()
	{
		return false;
	}

	@Override
	public Cinematique getLastValidCinematique()
	{
		return null;
	}

	public void clear()
	{
		path = null;
	}
}
