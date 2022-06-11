package com.ericlam.mc.groovier;

import com.google.inject.Injector;

import javax.inject.Provider;
import java.util.Optional;

public interface ServiceInjector extends Provider<Optional<Injector>> {
}
