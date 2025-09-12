package net.zbd.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.zbd.util.ConfigReader;

public class ReloadCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Component.text(
                    ConfigReader.t("command.reload.usage"),
                    NamedTextColor.YELLOW
            ));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "config" -> {
                ConfigReader.loadConfig();
                source.sendMessage(Component.text(
                        ConfigReader.t("command.reload.config"),
                        NamedTextColor.GREEN
                ));
            }
            case "messages" -> {
                ConfigReader.loadMessages(); // 根据当前配置的语言重载
                source.sendMessage(Component.text(
                        ConfigReader.t("command.reload.messages"),
                        NamedTextColor.GREEN
                ));
            }
            case "all" -> {
                ConfigReader.loadConfig();
                ConfigReader.loadMessages();
                source.sendMessage(Component.text(
                        ConfigReader.t("command.reload.all"),
                        NamedTextColor.GREEN
                ));
            }
            default -> source.sendMessage(Component.text(
                    ConfigReader.t("command.reload.unknown", java.util.Map.of("arg", args[0])),
                    NamedTextColor.RED
            ));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("ddns.reload");
    }
}
