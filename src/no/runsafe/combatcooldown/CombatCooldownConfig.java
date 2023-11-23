package no.runsafe.combatcooldown;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

import java.util.List;

public class CombatCooldownConfig implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		warningEnteringCombat = configuration.getConfigValueAsString("warningEnteringCombat");
		warningLeavingCombat = configuration.getConfigValueAsString("warningLeavingCombat");
		warningNoCommandInCombat = configuration.getConfigValueAsString("warningNoCommandInCombat");
		pvpWorlds = configuration.getConfigValueAsList("worlds");
		combatTime = configuration.getConfigValueAsInt("combatTime");
		shouldIncludeDergons = configuration.getConfigValueAsBoolean("shouldIncludeDergons");
	}

	public String getEnteringCombatMessage()
	{
		return warningEnteringCombat;
	}

	public String getLeavingCombatMessage()
	{
		return warningLeavingCombat;
	}

	public String getNoCommandsInCombatMessage()
	{
		return warningNoCommandInCombat;
	}

	public List<String> getPvpWorlds()
	{
		return pvpWorlds;
	}

	public int getCombatTime()
	{
		return combatTime;
	}

	public boolean shouldIncludeDergons()
	{
		return shouldIncludeDergons;
	}

	private static String warningEnteringCombat;
	private static String warningLeavingCombat;
	private static String warningNoCommandInCombat;
	private static List<String> pvpWorlds;
	private static int combatTime;
	private static boolean shouldIncludeDergons;
}
