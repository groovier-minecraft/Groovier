package com.ericlam.mc.groovier;

import com.google.inject.Module;

/**
 * groovier addon
 */
public interface GroovierAddon {
	
	/**
	 * install guice module
	 * @param module module
	 */
	void installModule(Module module);
}
