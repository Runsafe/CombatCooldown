package no.runsafe.combatcooldown;

import no.runsafe.framework.event.player.IPlayerCommandPreprocessEvent;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.event.player.RunsafePlayerCommandPreprocessEvent;
import no.runsafe.framework.server.player.RunsafePlayer;

public class PlayerListener implements IPlayerCommandPreprocessEvent
{
	public PlayerListener(CombatMonitor combatMonitor, IOutput console)
	{
		this.combatMonitor = combatMonitor;
		this.console = console;
	}

	@Override
	public void OnBeforePlayerCommand(RunsafePlayerCommandPreprocessEvent event)
	{
		RunsafePlayer thePlayer = event.getPlayer();
		if (this.combatMonitor.isInCombat(thePlayer.getName()))
		{
			console.fine(String.format("Blocking %s from running command %s during combat", thePlayer.getName(), event.getMessage()));
			event.setCancelled(true);
			thePlayer.sendMessage(Constants.warningNoCommandInCombat);
		}
	}

	private CombatMonitor combatMonitor = null;
	private final IOutput console;
}
