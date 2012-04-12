package no.runsafe.combatcooldown;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener
{
    private CombatMonitor combatMonitor = null;

    public PlayerListener(CombatMonitor combatMonitor)
    {
        this.combatMonitor = combatMonitor;
    }

    @EventHandler
    public void onPlayerCommandPreprocressEvent(PlayerCommandPreprocessEvent event)
    {
        Player thePlayer = event.getPlayer();
        if (this.combatMonitor.isInCombat(thePlayer.getName()))
        {
            event.setCancelled(true);
            thePlayer.sendMessage(Constants.warningNoCommandInCombat);
        }
    }
}
