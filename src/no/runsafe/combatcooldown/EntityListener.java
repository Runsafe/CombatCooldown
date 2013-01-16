package no.runsafe.combatcooldown;

import no.runsafe.framework.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.entity.RunsafeLivingEntity;
import no.runsafe.framework.server.entity.RunsafeProjectile;
import no.runsafe.framework.server.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.server.player.RunsafePlayer;

public class EntityListener implements IEntityDamageByEntityEvent
{
	public EntityListener(CombatMonitor combatMonitor, IOutput output)
	{
		this.combatMonitor = combatMonitor;
		this.output = output;
	}

	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		output.fine(String.format("%s engaging in combat with %s", event.getDamageActor().getEntityId(), event.getEntity().getEntityId()));
		if (event.getEntity() instanceof RunsafePlayer && event.getEntity() != event.getDamageActor())
		{
			RunsafePlayer victim = (RunsafePlayer) event.getEntity();
			output.fine(String.format("%s attacked by %s", victim.getName(), event.getDamageActor().getEntityId()));
			if (event.getEntity() instanceof RunsafePlayer && event.getDamageActor() instanceof RunsafePlayer)
			{
				if (!victim.isVanished())
				{
					RunsafePlayer attacker = (RunsafePlayer) event.getDamageActor();
					output.fine(String.format("Player %s engaged in PvP with %s - blocking commands.", attacker.getName(), victim.getName()));
					this.combatMonitor.engageInCombat(victim, attacker);
				}
				else
					output.fine("Victim is vanished");
			}
			else if (event.getDamageActor() instanceof RunsafeProjectile)
			{
				output.fine(String.format("%s attacked by projectile", victim.getName()));
				if (!victim.isVanished())
				{
					RunsafeProjectile theProjectile = (RunsafeProjectile) event.getDamageActor();
					RunsafeLivingEntity theShooter = theProjectile.getShooter();

					if (theShooter instanceof RunsafePlayer)
					{
						output.fine(String.format("Player %s engaged in PvP with %s - blocking commands.", ((RunsafePlayer) theShooter).getName(), victim.getName()));
						this.combatMonitor.engageInCombat(victim, (RunsafePlayer) theShooter);
					}
				}
				else
					output.fine("Victim is vanished");
			}
		}
	}

	private CombatMonitor combatMonitor = null;
	private final IOutput output;
}
