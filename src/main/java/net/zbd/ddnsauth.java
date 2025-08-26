package net.zbd;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "ddnsauth",
        name = "DDNSAuth",
        version = "1.0",
        description = "Restrict access by DDNS domain, country whitelist, and intranet.",
        authors = {"ZephyrBD"}
)
public class ddnsauth {

    @Inject
    public static Logger logger;

    @Inject
    private ProxyServer server;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config.loadConfig();
    }
}
