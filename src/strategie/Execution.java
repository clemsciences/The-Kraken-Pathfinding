package strategie;

import hook.Hook;
import hook.types.HookFactory;

import java.util.ArrayList;

import container.Service;
import pathfinding.Pathfinding;
import enums.PathfindingNodes;
import enums.ScriptNames;
import exceptions.PathfindingException;
import exceptions.PathfindingRobotInObstacleException;
import exceptions.UnknownScriptException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.strategie.ScriptException;
import robot.RobotReal;
import scripts.Decision;
import scripts.Script;
import scripts.ScriptManager;
import smartMath.Vec2;
import utils.Config;
import utils.Log;
import utils.Sleep;

public class Execution implements Service {

	private GameState<RobotReal> gamestate;
	private Log log;
	private Config config;
	private ScriptManager scriptmanager;
	private Pathfinding pathfinding;
	private HookFactory hookfactory;
	
	private ArrayList<Hook> hooks_entre_scripts;
	
	public Execution(Log log, Config config, Pathfinding pathfinding, GameState<RobotReal> gamestate, ScriptManager scriptmanager, HookFactory hookfactory)
	{
		this.log = log;
		this.config = config;
		this.pathfinding = pathfinding;
		this.gamestate = gamestate;
		this.scriptmanager = scriptmanager;
		this.hookfactory = hookfactory;
		
	    // DEPENDS_ON_RULES
		// TODO: hook qui renverse les plots en passant
		hooks_entre_scripts = new ArrayList<Hook>();
	}

	public void executerScript(ArrayList<Decision> decisions, ScriptNames scriptSecours, int id_version) throws UnknownScriptException
	{
		for(int id_decision = 0; id_decision < decisions.size(); id_decision++)
		{
			Decision decision_actuelle = decisions.get(id_decision);
			Script s = scriptmanager.getScript(decision_actuelle.script_name);

			try {
				tryOnce(s, decision_actuelle.id_version);
			} catch (PathfindingException e) {
				// Problème de pathfinding: pas de chemin. On change de script.
				continue;
			} catch (ScriptException e) {
				// On a eu un problème au milieu du script. On change de script.
				// Note: le script lui-même possède des procédures de relance.
				continue;
			} catch (UnableToMoveException e) {
				// On a rencontré l'ennemi en chemin. On retente avec un autre chemin.
				try {
					tryOnce(s, decision_actuelle.id_version);
				} catch (Exception e1) {
					// Quelque soit le problème, on change de script.
					continue;
				}
			} catch (PathfindingRobotInObstacleException e) {
				// On est dans un obstacle. Le mieux est encore d'attendre un peu.
				Sleep.sleep(1000);
				break; // on annule l'exécution.
			}
			// Tout s'est bien passé? On s'arrête là.
			break;
			
		}
	}
	
	public void tryOnce(Script s, int id_version) throws PathfindingException, UnableToMoveException, ScriptException, PathfindingRobotInObstacleException
	{
		ArrayList<PathfindingNodes> chemin;
		chemin = pathfinding.computePath(gamestate.robot.getPosition(), s.point_entree(id_version), gamestate.gridspace);
		gamestate.robot.suit_chemin(chemin, hooks_entre_scripts);
		s.agit(id_version, gamestate, false);	
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub
		
	}
	
}