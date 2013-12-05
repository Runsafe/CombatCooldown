package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IDebug;
import no.runsafe.framework.api.event.player.IPlayerCommandPreprocessEvent;
import no.runsafe.framework.api.event.player.IPlayerDeathEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerCommandPreprocessEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class PlayerListener implements IPlayerCommandPreprocessEvent, IPlayerDeathEvent
{
	public PlayerListener(CombatMonitor combatMonitor, IDebug console)
	{
		this.combatMonitor = combatMonitor;
		this.debugger = console;
	}

	@Override
	public void OnBeforePlayerCommand(RunsafePlayerCommandPreprocessEvent event)
	{
		debugger.debugFine("Checking if %s is engaged in combat", event.getPlayer().getName());
		RunsafePlayer player = event.getPlayer();
		if (this.combatMonitor.isInCombat(player.getName()))
		{
			debugger.debugFine("Blocking %s from running command %s during combat", player.getName(), event.getMessage());
			event.cancel();
			player.sendColouredMessage(Constants.warningNoCommandInCombat);
		}
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		combatMonitor.leaveCombat(event.getEntity());
	}

	private CombatMonitor combatMonitor = null;
	private final IDebug debugger;
}
