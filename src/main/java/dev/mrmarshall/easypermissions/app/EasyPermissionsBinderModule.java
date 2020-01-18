package dev.mrmarshall.easypermissions.app;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class EasyPermissionsBinderModule extends AbstractModule {

    private final EasyPermissions plugin;

    public EasyPermissionsBinderModule(EasyPermissions plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(EasyPermissions.class).toInstance(plugin);
    }
}
