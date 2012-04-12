package no.runsafe.combatcooldown;

import no.runsafe.framework.output.IOutput;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener
{
	private CombatMonitor combatMonitor = null;
    private IOutput output;
	
	public EntityListener(CombatMonitor combatMonitor, IOutput output)
	{
		this.combatMonitor = combatMonitor;
        this.output = output;
    }
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{
        if (event.getEntity() instanceof Player)
        {
            Player victim = (Player) event.getEntity();
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
            {
                Player attacker = (Player) event.getDamager();

                this.combatMonitor.engageInCombat(victim);
                this.combatMonitor.engageInCombat(attacker);
            }
            else if (event.getDamager() instanceof Arrow)
            {
                Arrow theArrow = (Arrow) event.getDamager();
                LivingEntity theShooter = theArrow.getShooter();

                if (theShooter instanceof Player)
                {
                    this.combatMonitor.engageInCombat(victim);
                    this.combatMonitor.engageInCombat((Player) theShooter);
                }
            }
            else if (event.getDamager() instanceof Projectile)
            {
                Projectile theProjectile = (Projectile) event.getDamager();
                LivingEntity theShooter = theProjectile.getShooter();

                if (theShooter instanceof Player)
                {
                    this.combatMonitor.engageInCombat(victim);
                    this.combatMonitor.engageInCombat((Player) theShooter);
                }
            }
        }
	}

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player thePlayer = (Player) event.getEntity();

            if (this.combatMonitor.isInCombat(thePlayer.getName()))
            {
                this.combatMonitor.leaveCombat(thePlayer);
            }
        }
    }

}
