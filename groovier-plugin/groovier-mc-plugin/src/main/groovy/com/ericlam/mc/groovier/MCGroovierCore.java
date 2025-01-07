package com.ericlam.mc.groovier;

import com.google.inject.Guice;

public final class MCGroovierCore extends GroovierCore {

	@Override
	public void onLoad(ScriptPlugin plugin) {
		super.onLoad(plugin);
	}

	public void onEnable() {
		super.onEnable(Guice.createInjector(this.groovierModule));
	}

}
