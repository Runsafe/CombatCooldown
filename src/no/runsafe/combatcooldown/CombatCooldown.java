package no.runsafe.combatcooldown;

import java.io.InputStream;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.configuration.IConfigurationDefaults;
import no.runsafe.framework.configuration.IConfigurationFile;

public class CombatCooldown extends RunsafePlugin implements IConfigurationFile, IConfigurationDefaults
{

	public CombatCooldown()
	{
		super();
	}
	
	@Override
	protected void PluginSetup()
	{
		this.addComponent(CombatMonitor.class);
		this.addComponent(EntityListener.class);
        this.addComponent(PlayerListener.class);
	}

	@Override
	public InputStream getDefaultConfiguration()
	{
		return getResource(Constants.defaultConfigurationFile);
	}

	@Override
	public String getConfigurationPath()
	{
		return Constants.configurationFile;
	}
	
}
