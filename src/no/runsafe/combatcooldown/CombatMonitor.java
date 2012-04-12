package no.runsafe.combatcooldown;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.subscriber.IPluginDisabled;
import no.runsafe.framework.event.subscriber.IPluginEnabled;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.timer.IScheduler;
import org.bukkit.entity.Player;

public class CombatMonitor implements IPluginEnabled, IPluginDisabled
{
	private HashMap<String, Integer> combatTimers;
	private IConfiguration config;
    private IScheduler scheduler;
    private IOutput output;

    private boolean isEnabled = false;

	public CombatMonitor(IConfiguration config, IScheduler scheduler, IOutput output)
	{
		this.combatTimers = null;
		this.config = config;
        this.scheduler = scheduler;
        this.output = output;
	}
	
	@Override
	public void OnPluginDisabled()
	{
        this.isEnabled = false;
		this.combatTimers.clear();
	}

	@Override
	public void OnPluginEnabled()
	{
        this.isEnabled = true;
        this.combatTimers = new HashMap<String, Integer>();
	}
	
	public void engageInCombat(Player player)
	{
        if (this.config.getConfigValueAsList("worlds").contains(player.getWorld().getName()))
        {
            if (!isInCombat(player.getName()))
                player.sendMessage(Constants.warningEnteringCombat);

            this.registerPlayerTimer(player);
        }
	}

    private void registerPlayerTimer(final Player player)
    {
        String playerName = player.getName();

        if (this.combatTimers.containsKey(playerName))
        {
            this.scheduler.cancelTimedEvent(this.combatTimers.get(playerName));
        }

        this.combatTimers.put(playerName, this.scheduler.setTimedEvent(new Runnable()
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
