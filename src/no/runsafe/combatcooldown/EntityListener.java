package no.runsafe.combatcooldown;

import no.runsafe.framework.interfaces.IPluginEnabled;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityListener implements Listener, IPluginEnabled
{
	private CombatMonitor combatMonitor = null;
	
	public EntityListener(CombatMonitor combatMonitor)
	{
		this.combatMonitor = combatMonitor;
	}

	@Override
	public void OnPluginEnabled()
	{
		// TODO Auto-generated method stub
	}
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
		{
			Player victim = (Player) event.getEntity();
			Player attack = (Player) event.getDamager();
			
			this.combatMonitor.engageInCombat(victim);
			this.combatMonitor.engageInCombat(attack);
		}
	}

}
