package no.runsafe.combatcooldown;

import no.runsafe.framework.RunsafeConfigurablePlugin;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(CombatMonitor.class);
		this.addComponent(EntityListener.class);
		this.addComponent(PlayerListener.class);
	}
}
