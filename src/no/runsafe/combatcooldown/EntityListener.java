package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.api.event.player.IPlayerCustomEvent;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeLivingEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

import java.util.List;

public class EntityListener implements IEntityDamageByEntityEvent, IPlayerCustomEvent
{
	public EntityListener(CombatMonitor combatMonitor, IDebug debugger, IServer server)
	{
		this.combatMonitor = combatMonitor;
		this.debugger = debugger;
		this.server = server;
	}

	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		if (!event.getEvent().equals("runsafe.dergon.mount"))
			return;

		IPlayer player = event.getPlayer();
		if (player.isVanished())
			return;

		combatMonitor.engageInDergonCombat(player);
		this.debugger.debugFine(String.format(
			"Player %s is being picked up by a Dergon - Blocking Commands if able.",
			player.getName()
		));
	}

	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		if (!(event.getEntity() instanceof IPlayer))
			return;

		IPlayer victim = (IPlayer) event.getEntity();
		if (victim.isVanished())
			return;

		IPlayer attackingPlayer = null;
		RunsafeEntity attacker = event.getDamageActor();

		this.debugger.debugFine(String.format(
			"Player %s is being attacked by a %s.",
			victim.getName(),
			attacker.getEntityType().getName()
		));

		if (attacker instanceof IPlayer)
			attackingPlayer = (IPlayer) attacker;
		else if (attacker instanceof RunsafeProjectile)
		{
			RunsafeProjectile projectile = (RunsafeProjectile) attacker;
			if (!(projectile.getEntityType() == ProjectileEntity.Egg || projectile.getEntityType() == ProjectileEntity.Snowball))
				attackingPlayer = projectile.getShootingPlayer();
		}

		if (attackingPlayer == null)
		{
			this.debugger.debugFine("Victim is not being attacked by a player.");
			return;
		}

		if (attackingPlayer.isVanished() || attackingPlayer.shouldNotSee(victim) || isSamePlayer(victim, attackingPlayer))
		{
			this.debugger.debugFine("Victim being attacked by exempted player.");
			return;
		}

		this.combatMonitor.engageInCombat(attackingPlayer, victim);
		this.debugger.debugFine(String.format(
			"Player %s engaged in PvP with %s - Blocking commands",
			attackingPlayer.getName(),
			victim.getName()
		));
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

	private final CombatMonitor combatMonitor;
	private final IDebug debugger;
	private final IServer server;
}
