package no.runsafe.combatcooldown;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.configuration.IConfigurationFile;

import java.io.InputStream;

public class Plugin extends RunsafePlugin implements IConfigurationFile
{
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
