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

	public static String warningEnteringCombat;
	public static String warningLeavingCombat;
	public static String warningNoCommandInCombat;
}
