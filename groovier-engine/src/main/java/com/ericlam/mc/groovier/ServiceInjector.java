package com.ericlam.mc.groovier;

import com.google.inject.Injector;

import javax.inject.Provider;
import java.util.Optional;

/**
 * to get injector with services
 */
public interface ServiceInjector extends Provider<Optional<Injector>> {
}
