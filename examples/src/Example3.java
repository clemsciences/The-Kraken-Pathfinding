/*
 * Copyright (C) 2013-2018 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

import java.util.ArrayList;
import java.util.List;

import pfg.kraken.Kraken;
import pfg.kraken.SearchParameters;
import pfg.kraken.exceptions.PathfindingException;
import pfg.kraken.obstacles.CircularObstacle;
import pfg.kraken.obstacles.Obstacle;
import pfg.kraken.obstacles.RectangularObstacle;
import pfg.kraken.struct.ItineraryPoint;
import pfg.kraken.struct.XY;
import pfg.kraken.struct.XYO;
import pfg.kraken.astar.DirectionStrategy;


/**
 * Advanced options
 * @author pf
 *
 */

public class Example3
{

	public static void main(String[] args)
	{
		List<Obstacle> obs = new ArrayList<Obstacle>();
		obs.add(new RectangularObstacle(new XY(800,200), 200, 200));
		obs.add(new RectangularObstacle(new XY(800,300), 200, 200));
		obs.add(new RectangularObstacle(new XY(-800,1200), 100, 200));
		obs.add(new RectangularObstacle(new XY(-1000,300), 500, 500));
		obs.add(new RectangularObstacle(new XY(200,1600), 800, 300));
		obs.add(new RectangularObstacle(new XY(1450,700), 300, 100));
		obs.add(new CircularObstacle(new XY(500,600), 100));

		RectangularObstacle robot = new RectangularObstacle(250, 80, 110, 110); 

		Kraken kraken = new Kraken(robot, obs, new XY(-1500,0), new XY(1500, 2000), "kraken-examples.conf", "trajectory"/*, "detailed"*/);

		/*
		 * You can perfectly use two instances of Kraken (for example if two robots have different size)
		 */
		RectangularObstacle secondRobot = new RectangularObstacle(20, 20, 20, 20); 
		Kraken krakenSecondRobot = new Kraken(secondRobot, obs, new XY(-1500,0), new XY(1500, 2000), "kraken-examples.conf", "trajectory"/*, "detailed"*/);

		try
		{
			/*
			 * You can force the moving direction. By default, the pathfinding can move the vehicle backward and forward.
			 * In example 1, the path makes the vehicle going backward. Let's force a forward motion.
			 */
			SearchParameters sp = new SearchParameters(new XYO(0, 200, 0), new XY(1000, 1000));
			sp.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);
			sp.setMaxSpeed(1.);
			kraken.initializeNewSearch(sp);
			
			List<ItineraryPoint> path = kraken.search();
			
			System.out.println("\nBlack robot search :");
			for(ItineraryPoint p : path)
			{
				System.out.println(p);
			}
			
			/*
			 * The second robot is smaller, so it can take a different path (blue trajectory)
			 * Furthermore, this path should be taken fast.
			 */
			sp.setMaxSpeed(3.);
			krakenSecondRobot.initializeNewSearch(sp);
			path = krakenSecondRobot.search();
			
			System.out.println("\nBlue robot search :");
			for(ItineraryPoint p : path)
			{
				System.out.println(p);
			}			

		}
		catch(PathfindingException e)
		{
			e.printStackTrace();
		}
	}
}
