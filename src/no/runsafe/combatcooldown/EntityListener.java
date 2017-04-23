package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.entity.IProjectileSource;
import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeLivingEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;

import java.util.List;

public class EntityListener implements IEntityDamageByEntityEvent
{
	public EntityListener(CombatMonitor combatMonitor, IDebug debugger, IServer server)
	{
		this.combatMonitor = combatMonitor;
		this.debugger = debugger;
		this.server = server;
	}

	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof IPlayer)
		{
			IPlayer victim = (IPlayer) event.getEntity();
			if (!victim.isVanished())
			{
				IPlayer attackingPlayer = null;
				RunsafeEntity attacker = event.getDamageActor();

				if (attacker instanceof IPlayer)
					attackingPlayer = (IPlayer) attacker;
				else if (attacker instanceof RunsafeProjectile)
				{
					RunsafeProjectile projectile = (RunsafeProjectile) attacker;
					if (!(projectile.getEntityType() == ProjectileEntity.Egg || projectile.getEntityType() == ProjectileEntity.Snowball))
						attackingPlayer = projectile.getShootingPlayer();
				}

				if (attackingPlayer == null || attackingPlayer.isVanished() || attackingPlayer.shouldNotSee(victim) || isSamePlayer(victim, attackingPlayer))
					return;

				this.combatMonitor.engageInCombat(attackingPlayer, victim);
				this.debugger.debugFine(String.format(
					"Player %s engaged in PvP with %s - Blocking commands",
					attackingPlayer.getName(),
					victim.getName()
				));
			}
		}
	}

	private boolean isSamePlayer(IPlayer one, IPlayer two)
	{
		return one.equals(two);
	}

	private IPlayer findPlayer(RunsafeLivingEntity entity)
	{
		List<IPlayer> onlinePlayers = server.getOnlinePlayers();
		for (IPlayer player : onlinePlayers)
			if (entity != null && player != null && entity.getEntityId() == player.getEntityId())
				return player;

		return null;
	}

	private CombatMonitor combatMonitor = null;
	private final IDebug debugger;
	private final IServer server;
}
