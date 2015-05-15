package threads;

import container.Service;
import exceptions.FinMatchException;
import exceptions.SerialConnexionException;
import robot.stm.STMcard;
import serial.SerialManager;
import table.ObstacleManager;
import utils.Config;
import utils.ConfigInfo;
import utils.Log;
import utils.Sleep;

/**
 * Thread qui s'occupe de la gestion du temps: début du match, péremption des obstacles
 * C'est lui qui active les capteurs en début de match.
 * @author pf
 *
 */

public class ThreadTimer extends RobotThread implements Service
{

	// Dépendance
	private Log log;
	private Config config;
	private ObstacleManager obstaclemanager;
	private SerialManager serialmanager;
	private STMcard stm;
	
	private long dureeMatch = 90000;
	private long dateFin;
	private int obstacleRefreshInterval = 500; // temps en ms entre deux appels par le thread timer du rafraichissement des obstacles de la table

	public ThreadTimer(Log log, Config config, ObstacleManager obstaclemanager, STMcard stm, SerialManager serialmanager)
	{
		this.log = log;
		this.config = config;
		this.obstaclemanager = obstaclemanager;
		this.stm = stm;
		this.serialmanager = serialmanager;
		
		updateConfig();
		Thread.currentThread().setPriority(1);
	}

	@Override
	public void run()
	{
		log.debug("Lancement du thread timer");

		// les capteurs sont initialement éteints
		Config.capteursOn = false;
		
		try {
			ThreadLock lock = ThreadLock.getInstance();
			synchronized(lock)
			{
				lock.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		config.setDateDebutMatch();
		Config.capteursOn = true;

		log.debug("LE MATCH COMMENCE !");

		dateFin = dureeMatch + Config.getDateDebutMatch();

		// Le match a démarré. On retire périodiquement les obstacles périmés
		while(System.currentTimeMillis() < dateFin)
		{
			if(stopThreads)
			{
				log.debug("Arrêt du thread timer");
				return;
			}
			obstaclemanager.supprimerObstaclesPerimes();
			Sleep.sleep(obstacleRefreshInterval);
		}

		onMatchEnded();
		
		log.debug("Fin du thread timer");
		
	}
	
	private void onMatchEnded()
	{
		log.debug("Fin du Match !");
		
		finMatch = true;

		// DEPENDS_ON_RULES
		// potentielle attente avant de tout désactiver afin de laisser la funny action

		// fin du match : désasser final
		try {
			stm.disableRotationalFeedbackLoop();
			stm.disableTranslationalFeedbackLoop();
		} catch (SerialConnexionException e) {
			e.printStackTrace();
		} catch (FinMatchException e) {
			e.printStackTrace();
		}
		
		serialmanager.close();
	}
	
	public void updateConfig()
	{
		// facteur 1000 car temps_match est en secondes et duree_match en ms
		dureeMatch = 1000*config.getInt(ConfigInfo.DUREE_MATCH_EN_S);
		log.updateConfig();
		obstaclemanager.updateConfig();
		stm.updateConfig();
		serialmanager.updateConfig();
		obstacleRefreshInterval = config.getInt(ConfigInfo.OBSTACLE_REFRESH_INTERVAL);
	}
	
}
