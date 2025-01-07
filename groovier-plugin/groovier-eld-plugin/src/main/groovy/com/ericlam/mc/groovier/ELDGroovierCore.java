package com.ericlam.mc.groovier;

import com.ericlam.mc.eld.AddonInstallation;
import com.google.inject.Injector;

public final class ELDGroovierCore extends GroovierCore {

	public void onLoad(ScriptPlugin plugin, AddonInstallation installation) {
		super.onLoad(plugin);
		installation.installModule(this.groovierModule);
	}

	@Override
	public void onEnable(Injector injector) {
		super.onEnable(injector);
	}

}
