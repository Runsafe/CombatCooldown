package no.runsafe.combatcooldown;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.features.Events;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void pluginSetup()
	{
		this.addComponent(Events.class);
		this.addComponent(CombatMonitor.class);
		this.addComponent(EntityListener.class);
		this.addComponent(PlayerListener.class);
	}
}
