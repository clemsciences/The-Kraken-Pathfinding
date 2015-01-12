package hook.methods;

import robot.RobotChrono;
import scripts.ScriptHookNames;
import strategie.GameState;
import hook.Executable;
import exceptions.ScriptHookException;

/**
 * Lève une exception prévenant la possibilité d'un script de hook
 * @author pf
 *
 */

public class ThrowsScriptHook implements Executable {

	private ScriptHookNames script;
	private int version;
	
	public ThrowsScriptHook(ScriptHookNames script, int version)
	{
		this.script = script;
		this.version = version;
	}
	
	@Override
	public void execute() throws ScriptHookException
	{
		/**
		 * Si le robot n'est pas capable d'effectuer cette action,
		 * pas besoin de lui demander de le faire.
		 */
		if(script.canIDoIt())
			throw new ScriptHookException(script, version);
	}
	
	@Override
	public void updateGameState(GameState<RobotChrono> state)
	{}

}
