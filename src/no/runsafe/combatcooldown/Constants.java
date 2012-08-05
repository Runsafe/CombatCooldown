package no.runsafe.combatcooldown;

import org.bukkit.ChatColor;

public class Constants
{
	public static String defaultConfigurationFile = "defaultConfig.yml";
	public static String configurationFile = "plugins/CombatCooldown/config.yml";

	public static String warningEnteringCombat = ChatColor.DARK_RED + "You are now entering PvP combat.";
	public static String warningLeavingCombat = ChatColor.DARK_RED + "You are now leaving PvP combat.";
	public static String warningNoCommandInCombat = ChatColor.DARK_RED + "You may not do this during PvP combat.";
}
