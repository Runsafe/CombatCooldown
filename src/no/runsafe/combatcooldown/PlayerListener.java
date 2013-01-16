package no.runsafe.combatcooldown;

import no.runsafe.framework.event.player.IPlayerCommandPreprocessEvent;
import no.runsafe.framework.event.player.IPlayerDeathEvent;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.event.player.RunsafePlayerCommandPreprocessEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.server.player.RunsafePlayer;

public class PlayerListener implements IPlayerCommandPreprocessEvent, IPlayerDeathEvent
{
	public PlayerListener(CombatMonitor combatMonitor, IOutput console)
	{
		this.combatMonitor = combatMonitor;
		this.console = console;
	}

	@Override
	public void OnBeforePlayerCommand(RunsafePlayerCommandPreprocessEvent event)
	{
		RunsafePlayer player = event.getPlayer();
		if (this.combatMonitor.isInCombat(player.getName()))
		{
			console.fine(String.format("Blocking %s from running command %s during combat", player.getName(), event.getMessage()));
			event.setCancelled(true);
			player.sendColouredMessage(Constants.warningNoCommandInCombat);
		}
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		combatMonitor.leaveCombat(event.getEntity());
	}

	private CombatMonitor combatMonitor = null;
	private final IOutput console;
}
