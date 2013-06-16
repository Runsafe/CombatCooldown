package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeLivingEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.List;

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
					attackingPlayer = (RunsafePlayer) attacker;
				else if (attacker instanceof RunsafeProjectile)
					attackingPlayer = this.findPlayer(((RunsafeProjectile) attacker).getShooter());

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

	private RunsafePlayer findPlayer(RunsafeLivingEntity entity)
	{
		List<RunsafePlayer> onlinePlayers = RunsafeServer.Instance.getOnlinePlayers();
		for (RunsafePlayer player : onlinePlayers)
			if (entity.getEntityId() == player.getEntityId())
				return player;

		return null;
	}

	private CombatMonitor combatMonitor = null;
	private final IOutput output;
}
