package no.runsafe.combatcooldown;

import java.util.HashMap;

import org.bukkit.entity.Player;

import no.runsafe.framework.interfaces.IConfiguration;
import no.runsafe.framework.interfaces.IPluginDisabled;
import no.runsafe.framework.interfaces.IPluginEnabled;

public class CombatMonitor implements IPluginEnabled, IPluginDisabled
{
	private HashMap<String, Integer> combatTimers;
	private IConfiguration config;

	public CombatMonitor(IConfiguration config)
	{
		this.combatTimers = new HashMap<String, Integer>();
		this.config = config;
	}
	
	@Override
	public void OnPluginDisabled()
	{
		this.combatTimers.clear();
	}

	@Override
	public void OnPluginEnabled()
	{
		
	}
	
	public void engageInCombat(Player player)
	{
		player.sendMessage(Constants.warningEnteringCombat);
		this.combatTimers.put(player.getName(), this.config.getConfigValueAsInt("combatTime"));
	}
	
	public void leaveCombat(Player player)
	{
		player.sendMessage(Constants.warningLeavingCombat);
	}

}
