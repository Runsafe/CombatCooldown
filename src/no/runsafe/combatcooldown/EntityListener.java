package no.runsafe.combatcooldown;

import no.runsafe.framework.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.event.entity.IEntityDeathEvent;
import no.runsafe.framework.messaging.PlayerStatus;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.entity.RunsafeLivingEntity;
import no.runsafe.framework.server.entity.RunsafeProjectile;
import no.runsafe.framework.server.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.server.event.entity.RunsafeEntityDeathEvent;
import no.runsafe.framework.server.player.RunsafePlayer;

public class EntityListener implements IEntityDamageByEntityEvent, IEntityDeathEvent
{
	private CombatMonitor combatMonitor = null;
	private IOutput output;
	private PlayerStatus playerStatus;

	public EntityListener(CombatMonitor combatMonitor, IOutput output, PlayerStatus playerStatus)
	{
		this.combatMonitor = combatMonitor;
		this.output = output;
		this.playerStatus = playerStatus;
	}

	@Override
	public void OnEntityDeath(RunsafeEntityDeathEvent event)
	{
		if(event.getEntity() instanceof RunsafePlayer)
		{
			RunsafePlayer thePlayer = (RunsafePlayer) event.getEntity();
			output.fine(String.format("Player %s died - allowing commands.", thePlayer.getName()));
			if(this.combatMonitor.isInCombat(thePlayer.getName()))
			{
				this.combatMonitor.leaveCombat(thePlayer);
			}
		}
	}

	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof RunsafePlayer && event.getEntity() != event.getDamageActor())
		{
			RunsafePlayer victim = (RunsafePlayer) event.getEntity();

			if(event.getEntity() instanceof RunsafePlayer && event.getDamageActor() instanceof RunsafePlayer)
			{
				if(this.playerStatus.getVisibility(victim))
				{
					RunsafePlayer attacker = (RunsafePlayer) event.getDamageActor();
					output.fine(String.format("Player %s engaged in PvP with %s - blocking commands.", attacker.getName(), victim.getName()));
					this.combatMonitor.engageInCombat(victim, attacker);
				}
			} else if(event.getDamageActor() instanceof RunsafeProjectile)
			{
				if(this.playerStatus.getVisibility(victim))
				{
					RunsafeProjectile theProjectile = (RunsafeProjectile) event.getDamageActor();
					RunsafeLivingEntity theShooter = theProjectile.getShooter();

					if(theShooter instanceof RunsafePlayer)
					{
						output.fine(String.format("Player %s engaged in PvP with %s - blocking commands.", ((RunsafePlayer)theShooter).getName(), victim.getName()));
						this.combatMonitor.engageInCombat(victim, (RunsafePlayer) theShooter);
					}
				}
			}
		}
	}
}
