package no.runsafe.combatcooldown;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IPluginDisabled;
import no.runsafe.framework.event.IPluginEnabled;
import no.runsafe.framework.messaging.IMessagePump;
import no.runsafe.framework.messaging.Message;
import no.runsafe.framework.messaging.MessageBusStatus;
import no.runsafe.framework.messaging.Response;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CombatMonitor implements IPluginEnabled, IPluginDisabled
{
	private HashMap<String, Integer> combatTimers;
	private IConfiguration config;
	private IScheduler scheduler;
	private IOutput output;
	private IMessagePump messagePump;

	public CombatMonitor(IConfiguration config, IScheduler scheduler, IOutput output, IMessagePump messagePump)
	{
		this.combatTimers = null;
		this.config = config;
		this.scheduler = scheduler;
		this.output = output;
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

	private boolean playersInPvPZone(Player firstPlayer, Player secondPlayer)
	{
		Message bridgeMessage = new Message();
		bridgeMessage.setTargetService("WorldGuardBridge");
		bridgeMessage.setQuestion("PLAYER_IN_PVP_ZONE");

		bridgeMessage.setPlayer(new RunsafePlayer(firstPlayer));
		Response bridgeResponse = this.messagePump.HandleMessage(bridgeMessage);

		if (bridgeResponse.getStatus() == MessageBusStatus.NOT_OK)
			return false;

		bridgeMessage.setPlayer(new RunsafePlayer(secondPlayer));
		bridgeResponse = this.messagePump.HandleMessage(bridgeMessage);

		if (bridgeResponse.getStatus() == MessageBusStatus.NOT_OK)
			return false;

		return true;
	}

	private boolean monitoringWorld(World world)
	{
		if (this.config.getConfigValueAsList("worlds").contains(world.getName()))
			return true;

		return false;
	}

	public void engageInCombat(Player firstPlayer, Player secondPlayer)
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

	private void engagePlayer(Player player)
	{
		if (!isInCombat(player.getName()))
			player.sendMessage(Constants.warningEnteringCombat);

		this.registerPlayerTimer(player);
	}

	private void registerPlayerTimer(final Player player)
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
		}, this.config.getConfigValueAsInt("combatTime")));
	}

	public void leaveCombat(Player player)
	{
		this.combatTimers.remove(player.getName());
		player.sendMessage(Constants.warningLeavingCombat);
	}

	public boolean isInCombat(String playerName)
	{
		if (this.combatTimers.containsKey(playerName))
			return true;

		return false;
	}

}
