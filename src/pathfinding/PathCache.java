/*
Copyright (C) 2013-2017 Pierre-François Gimenez

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

package pathfinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import container.Service;
import exceptions.PathfindingException;
import pathfinding.astar.AStarCourbe;
import pathfinding.astar.arcs.CercleArrivee;
import pathfinding.chemin.CheminPathfinding;
import pathfinding.chemin.IteratorCheminPathfinding;
import robot.Cinematique;
import robot.CinematiqueObs;
import robot.Speed;
import scripts.Script;
import scripts.ScriptDeposeMinerai;
import scripts.ScriptManager;
import table.GameElementNames;
import utils.Log;

/**
 * Service qui contient les chemins précalculés
 * @author pf
 *
 */

public class PathCache implements Service
{
	private Log log;
	private AStarCourbe astar;
	public HashMap<Cinematique, HashMap<Script, List<CinematiqueObs>>> paths;
	
	public PathCache(Log log, ScriptManager smanager, ChronoGameState chrono, AStarCourbe astar, IteratorCheminPathfinding iterator, CheminPathfinding chemin)
	{
		this.log = log;
		Cinematique start = new Cinematique(200, 1800, Math.PI, true, 0, Speed.STANDARD.translationalSpeed);
		this.astar = astar;
		paths = new HashMap<Cinematique, HashMap<Script, List<CinematiqueObs>>>();
		if(!new File("paths/").exists())
			new File("paths/").mkdir();
		loadAll(smanager, chrono, start, iterator, chemin);
	}
	
	private void savePath(String file, List<CinematiqueObs> path)
	{
    	log.debug("Sauvegarde d'une trajectoire : "+file);
        try {
            FileOutputStream fichier;
            ObjectOutputStream oos;

            new File(file).createNewFile();
            fichier = new FileOutputStream(file);
            oos = new ObjectOutputStream(fichier);
            oos.writeObject(path);
            oos.flush();
            oos.close();
        	log.debug("Sauvegarde terminée");
        }
        catch(IOException e)
        {
            log.critical("Erreur lors de la sauvegarde de la trajectoire ! "+e);
        }
	}
	
	public void computeNewPathToCircle(boolean shoot) throws PathfindingException
	{
		astar.initializeNewSearchToCircle(shoot);
		astar.process();
	}
	
	public void computeNewPath(Cinematique arrivee, SensFinal sens, boolean shoot) throws PathfindingException
	{
		astar.initializeNewSearch(arrivee, sens, shoot);
		astar.process();
	}
	
	public void computeNewPath(Cinematique arrivee, boolean shoot) throws PathfindingException
	{
		astar.initializeNewSearch(arrivee, shoot);
		astar.process();
	}
	
	private void loadAll(ScriptManager smanager, ChronoGameState chrono, Cinematique start, IteratorCheminPathfinding iterator, CheminPathfinding chemin)
	{
		smanager.reinit();
		boolean[] shoot = {true, false};
		for(int i = 0; i < 2; i++)
		{
			while(smanager.hasNext())
			{
				Script script = smanager.next();
				String fileName = "paths/"+start.hashCode()+"->"+script+"-s="+shoot[i]+".dat";
				List<CinematiqueObs> path = loadPath(fileName);
				if(script instanceof ScriptDeposeMinerai)
					continue;
				
				log.debug(script);
				if(path == null)
				{
					chrono.robot.setCinematique(start);
					script.setUpCercleArrivee();
					try {
						astar.initializeNewSearchToCircle(shoot[i]);
						astar.process();
						iterator.reinit();
						path = new ArrayList<CinematiqueObs>();
						while(iterator.hasNext())
							path.add(iterator.next());
						savePath(fileName, path);
						chemin.clear();
					} catch (PathfindingException e) {
						log.critical("Le précalcul du chemin a échoué");
					}
					finally
					{
						astar.stopSearch();
					}
				}
				if(path != null)
				{
					HashMap<Script, List<CinematiqueObs>> map = paths.get(start);
					if(map == null)
					{
						map = new HashMap<Script, List<CinematiqueObs>>();
						paths.put(start, map);
					}
					map.put(script, path);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<CinematiqueObs> loadPath(String file)
	{
    	log.debug("Chargement d'une trajectoire : "+file);
        try {
            FileInputStream fichier = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fichier);
            List<CinematiqueObs> path = (List<CinematiqueObs>) ois.readObject();
            ois.close();
            return path;
        }
        catch(IOException | ClassNotFoundException e)
        {
        	log.critical("Chargement échoué !");
        }
        return null;
	}
	
	/**
	 * Le chemin a été entièrement parcouru.
	 */
	public synchronized void stopSearch()
	{
		astar.stopSearch();
	}
}
