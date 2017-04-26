package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.api.player.IPlayer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatMonitor implements IPluginDisabled, IConfigurationChanged
{
	public CombatMonitor(IScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public void leaveCombat(IPlayer player)
	{
		if (this.combatTimers.containsKey(player.getUniqueId()))
		{
			this.combatTimers.remove(player.getUniqueId());
			player.sendColouredMessage(Constants.warningLeavingCombat);
		}
	}

	public boolean isInCombat(IPlayer player)
	{
		return this.combatTimers.containsKey(player.getUniqueId());
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
		if (!isInCombat(player))
			player.sendColouredMessage(Constants.warningEnteringCombat);

		this.registerPlayerTimer(player);
	}

	private void registerPlayerTimer(final IPlayer player)
	{
		if (this.combatTimers.containsKey(player.getUniqueId()))
		{
			this.scheduler.cancelTask(this.combatTimers.get(player.getUniqueId()));
		}

		this.combatTimers.put(player.getUniqueId(), this.scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				leaveCombat(player);
			}
		}, combatTime));
	}

	private final ConcurrentHashMap<UUID, Integer> combatTimers = new ConcurrentHashMap<UUID, Integer>();
	private final IScheduler scheduler;
	private List<String> pvpWorlds;
	private int combatTime;
}
