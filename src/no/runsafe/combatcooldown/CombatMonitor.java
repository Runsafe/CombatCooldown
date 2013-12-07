package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.api.player.IPlayer;

import java.util.HashMap;
import java.util.List;

public class CombatMonitor implements IPluginDisabled, IConfigurationChanged
{
	public CombatMonitor(IScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public void leaveCombat(IPlayer player)
	{
		if (this.combatTimers.containsKey(player.getName()))
		{
			this.combatTimers.remove(player.getName());
			player.sendColouredMessage(Constants.warningLeavingCombat);
		}
	}

	public boolean isInCombat(String playerName)
	{
		return this.combatTimers.containsKey(playerName);
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		pvpWorlds = configuration.getConfigValueAsList("worlds");
		combatTime = configuration.getConfigValueAsInt("combatTime");
	}

	@Override
	public void OnPluginDisabled()
	{
		this.combatTimers.clear();
	}

	private boolean monitoringWorld(IWorld world)
	{
		return pvpWorlds.contains(world.getName());
	}

	public void engageInCombat(IPlayer firstPlayer, IPlayer secondPlayer)
	{
		if (this.monitoringWorld(firstPlayer.getWorld()) && this.monitoringWorld(secondPlayer.getWorld()))
		{
			if (firstPlayer.isPvPFlagged() && secondPlayer.isPvPFlagged())
			{
				this.engagePlayer(firstPlayer);
				this.engagePlayer(secondPlayer);
			}
		}
	}

	private void engagePlayer(IPlayer player)
	{
		if (!isInCombat(player.getName()))
			player.sendColouredMessage(Constants.warningEnteringCombat);

		this.registerPlayerTimer(player);
	}

	private void registerPlayerTimer(final IPlayer player)
	{
		String playerName = player.getName();

		if (this.combatTimers.containsKey(playerName))
		{
			this.scheduler.cancelTask(this.combatTimers.get(playerName));
		}

		this.combatTimers.put(playerName, this.scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				leaveCombat(player);
			}
		}, combatTime));
	}

	private final HashMap<String, Integer> combatTimers = new HashMap<String, Integer>();
	private final IScheduler scheduler;
	private List<String> pvpWorlds;
	private int combatTime;
}
