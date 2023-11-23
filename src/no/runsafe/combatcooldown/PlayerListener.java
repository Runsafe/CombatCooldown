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
	public PlayerListener(CombatMonitor combatMonitor, IDebug console, CombatCooldownConfig config)
	{
		this.combatMonitor = combatMonitor;
		this.debugger = console;
		this.config = config;
		effect = new WorldBlockEffect(WorldBlockEffectType.BLOCK_DUST, Item.BuildingBlock.Bedrock);
	}

	@Override
	public void OnBeforePlayerCommand(RunsafePlayerCommandPreprocessEvent event)
	{
		IPlayer player = event.getPlayer();
		String playerName = player.getName();
		String commandString = event.getMessage();

		debugger.debugFine("Checking if %s is engaged in combat", playerName);
		if (this.combatMonitor.isInCombat(player) && !canRunCommand(player, commandString))
		{
			debugger.debugFine("Blocking %s from running command %s during combat", playerName, commandString);
			event.cancel();
			player.sendColouredMessage(config.getNoCommandsInCombatMessage());
		}
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		IPlayer player = event.getPlayer();
		if (combatMonitor.isInCombat(player))
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

	private boolean canRunCommand(IPlayer player, String commandString)
	{
		String[] commandParts = commandString.replaceAll("/", "").split(" ");
		return player.hasPermission("runsafe.combat.command." + commandParts[0]);
	}

	private final CombatMonitor combatMonitor;
	private final IDebug debugger;
	private final CombatCooldownConfig config;
	private final IWorldEffect effect;
}
