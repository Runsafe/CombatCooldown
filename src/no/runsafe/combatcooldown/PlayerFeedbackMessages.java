package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

class PlayerFeedbackMessages implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		warningEnteringCombat = configuration.getConfigValueAsString("warningEnteringCombat");
		warningLeavingCombat = configuration.getConfigValueAsString("warningLeavingCombat");
		warningNoCommandInCombat = configuration.getConfigValueAsString("warningNoCommandInCombat");
	}

	public static String warningEnteringCombat = "&4You are now entering PvP combat.";
	public static String warningLeavingCombat = "&4You are now leaving PvP combat.";
	public static String warningNoCommandInCombat = "&4You may not do this during PvP combat.";
}
