package no.runsafe.combatcooldown;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.event.IPluginDisabled;
import no.runsafe.framework.event.IPluginEnabled;
import no.runsafe.framework.messaging.IMessagePump;
import no.runsafe.framework.messaging.Message;
import no.runsafe.framework.messaging.MessageBusStatus;
import no.runsafe.framework.messaging.Response;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;
import java.util.List;

public class CombatMonitor implements IPluginEnabled, IPluginDisabled, IConfigurationChanged
{
	HashMap<String, Integer> combatTimers;
	final IScheduler scheduler;
	int combatTime;
	final IMessagePump messagePump;
	List<String> pvpWorlds;

	public CombatMonitor(IScheduler scheduler, IMessagePump messagePump)
	{
		this.combatTimers = null;
		this.scheduler = scheduler;
		this.messagePump = messagePump;
	}

	@Override
	public void OnPluginDisabled()
	{
		this.combatTimers.clear();
	}

	@Override
	public void OnPluginEnabled()
	{
		this.combatTimers = new HashMap<String, Integer>();
	}

	private boolean playersInPvPZone(RunsafePlayer firstPlayer, RunsafePlayer secondPlayer)
	{
		Message bridgeMessage = new Message();
		bridgeMessage.setTargetService("WorldGuardBridge");
		bridgeMessage.setQuestion("PLAYER_IN_PVP_ZONE");

		bridgeMessage.setPlayer(firstPlayer);
		Response bridgeResponse = this.messagePump.HandleMessage(bridgeMessage);

		if (bridgeResponse.getStatus() == MessageBusStatus.NOT_OK)
			return false;

		bridgeMessage.setPlayer(secondPlayer);
		bridgeResponse = this.messagePump.HandleMessage(bridgeMessage);

		return bridgeResponse.getStatus() != MessageBusStatus.NOT_OK;
	}

	private boolean monitoringWorld(RunsafeWorld world)
	{
		return pvpWorlds.contains(world.getName());
	}

	public void engageInCombat(RunsafePlayer firstPlayer, RunsafePlayer secondPlayer)
	{
		if (this.monitoringWorld(firstPlayer.getWorld()) && this.monitoringWorld(secondPlayer.getWorld()))
		{
			if (this.playersInPvPZone(firstPlayer, secondPlayer))
			{
				this.engagePlayer(firstPlayer);
				this.engagePlayer(secondPlayer);
			}
		}
	}

	private void engagePlayer(RunsafePlayer player)
	{
		if (!isInCombat(player.getName()))
			player.sendMessage(Constants.warningEnteringCombat);

		this.registerPlayerTimer(player);
	}

	private void registerPlayerTimer(final RunsafePlayer player)
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

	public void leaveCombat(RunsafePlayer player)
	{
		this.combatTimers.remove(player.getName());
		player.sendMessage(Constants.warningLeavingCombat);
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
}
