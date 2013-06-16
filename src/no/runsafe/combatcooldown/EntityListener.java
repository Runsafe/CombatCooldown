package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeLivingEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

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
		if (event.getEntity() instanceof RunsafePlayer)
		{
			RunsafePlayer victim = (RunsafePlayer) event.getEntity();
			if (!victim.isVanished())
			{
				RunsafePlayer attackingPlayer = null;
				RunsafeEntity attacker = event.getDamageActor();

				if (attacker instanceof RunsafePlayer)
				{
					attackingPlayer = (RunsafePlayer) attacker;
				}
				else if (attacker instanceof RunsafeProjectile)
				{
					RunsafeLivingEntity shooter = ((RunsafeProjectile) attacker).getShooter();
					if (shooter instanceof RunsafePlayer)
						attackingPlayer = (RunsafePlayer) shooter;
				}

				if (attackingPlayer != null && !attackingPlayer.isVanished())
				{
					this.combatMonitor.engageInCombat(attackingPlayer, victim);
					this.output.fine(String.format(
							"Player %s engaged in PvP with %s - Blocking commands",
							attackingPlayer.getName(),
							victim.getName()
					));
				}
			}
		}
	}

	private CombatMonitor combatMonitor = null;
	private final IOutput output;
}
