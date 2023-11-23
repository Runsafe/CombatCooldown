package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.api.player.IPlayer;

import java.util.concurrent.ConcurrentHashMap;

public class CombatMonitor implements IPluginDisabled
{
	public CombatMonitor(IScheduler scheduler, CombatCooldownConfig config)
	{
		this.scheduler = scheduler;
		this.config = config;
	}

	public void leaveCombat(IPlayer player)
	{
		if (this.combatTimers.containsKey(player))
		{
			this.combatTimers.remove(player);
			player.sendColouredMessage(config.getLeavingCombatMessage());
		}
	}

	public boolean isInCombat(IPlayer player)
	{
		return this.combatTimers.containsKey(player);
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

		return config.getPvpWorlds().contains(world.getName());
	}

	public void engageInDergonCombat(IPlayer player)
	{
		if (config.shouldIncludeDergons() && monitoringWorld(player.getWorld()) && player.isPvPFlagged())
			engagePlayer(player);
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
			player.sendColouredMessage(config.getEnteringCombatMessage());

		this.registerPlayerTimer(player);
	}

	private void registerPlayerTimer(final IPlayer player)
	{
		if (this.combatTimers.containsKey(player))
		{
			this.scheduler.cancelTask(this.combatTimers.get(player));
		}
		this.combatTimers.put(player, this.scheduler.startSyncTask(() -> leaveCombat(player), config.getCombatTime()));
	}

	private final ConcurrentHashMap<IPlayer, Integer> combatTimers = new ConcurrentHashMap<>();
	private final IScheduler scheduler;
	private final CombatCooldownConfig config;
}
