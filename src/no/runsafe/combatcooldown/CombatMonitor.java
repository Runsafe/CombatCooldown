package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.api.player.IPlayer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CombatMonitor implements IPluginDisabled, IConfigurationChanged
{
	public CombatMonitor(IScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public void leaveCombat(IPlayer player)
	{
		if (this.combatTimers.containsKey(player))
		{
			this.combatTimers.remove(player);
			player.sendColouredMessage(PlayerFeedbackMessages.warningLeavingCombat);
		}
	}

	public boolean isInCombat(IPlayer player)
	{
		return this.combatTimers.containsKey(player);
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
		if (world == null)
			return false;

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
			player.sendColouredMessage(PlayerFeedbackMessages.warningEnteringCombat);

		this.registerPlayerTimer(player);
	}

	private void registerPlayerTimer(final IPlayer player)
	{
		if (this.combatTimers.containsKey(player))
		{
			this.scheduler.cancelTask(this.combatTimers.get(player));
		}
		this.combatTimers.put(player, this.scheduler.startSyncTask(() -> leaveCombat(player), combatTime));
	}

	private final ConcurrentHashMap<IPlayer, Integer> combatTimers = new ConcurrentHashMap<>();
	private final IScheduler scheduler;
	private List<String> pvpWorlds;
	private int combatTime;
}
