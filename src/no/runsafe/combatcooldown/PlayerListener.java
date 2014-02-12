package no.runsafe.combatcooldown;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorldEffect;
import no.runsafe.framework.api.event.player.IPlayerCommandPreprocessEvent;
import no.runsafe.framework.api.event.player.IPlayerDeathEvent;
import no.runsafe.framework.api.event.player.IPlayerQuitEvent;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.WorldBlockEffect;
import no.runsafe.framework.minecraft.WorldBlockEffectType;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerCommandPreprocessEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerQuitEvent;

public class PlayerListener implements IPlayerCommandPreprocessEvent, IPlayerDeathEvent, IPlayerQuitEvent
{
	public PlayerListener(CombatMonitor combatMonitor, IDebug console)
	{
		this.combatMonitor = combatMonitor;
		this.debugger = console;
		effect = new WorldBlockEffect(WorldBlockEffectType.BLOCK_DUST, Item.BuildingBlock.Bedrock);
	}

	@Override
	public void OnBeforePlayerCommand(RunsafePlayerCommandPreprocessEvent event)
	{
		debugger.debugFine("Checking if %s is engaged in combat", event.getPlayer().getName());
		IPlayer player = event.getPlayer();
		if (this.combatMonitor.isInCombat(player.getName()))
		{
			debugger.debugFine("Blocking %s from running command %s during combat", player.getName(), event.getMessage());
			event.cancel();
			player.sendColouredMessage(Constants.warningNoCommandInCombat);
		}
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		IPlayer player = event.getPlayer();
		if (combatMonitor.isInCombat(player.getName()))
		{
			player.damage(500D); // This should kill them
			ILocation location = player.getLocation();
			if (location != null)
				location.playEffect(effect, 0.3F, 100, 50);
		}
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		combatMonitor.leaveCombat(event.getEntity());
	}

	private final CombatMonitor combatMonitor;
	private final IDebug debugger;
	private final IWorldEffect effect;
}
