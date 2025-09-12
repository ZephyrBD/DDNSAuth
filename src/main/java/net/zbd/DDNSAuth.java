package net.zbd;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.zbd.util.ReloadCommand;
import net.zbd.util.ConfigReader;
import net.zbd.util.JudgeIP;
import org.slf4j.Logger;

import static net.zbd.util.ConfigReader.t;

@Plugin(
        id = "ddnsauth",
        name = "DDNSAuth",
        version = "1.0",
        description = "Restrict access by DDNS domain, country whitelist, and intranet.",
        authors = {"ZephyrBD"}
)
public class DDNSAuth {

    private static DDNSAuth instance;

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer server;

    @Inject
    private CommandManager commandManager;

    public DDNSAuth() {
        instance = this;
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 加载配置 & 多语言
        ConfigReader.init();

        // 注册事件监听
        server.getEventManager().register(this, new JudgeIP());

        // 注册命令
        commandManager.register(
                commandManager.metaBuilder("ddnsreload").build(),
                new ReloadCommand()
        );

        logger.info(t("pluginLoaded"));
    }
}
