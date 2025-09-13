package net.zbd.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReloadCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Component.text(
                    ConfigReader.t("command.usage"),
                    NamedTextColor.YELLOW
            ));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "config" -> {
                ConfigReader.loadConfig();
                source.sendMessage(Component.text(
                        ConfigReader.t("command.config"),
                        NamedTextColor.GREEN
                ));
            }
            case "messages" -> {
                ConfigReader.loadMessages();
                source.sendMessage(Component.text(
                        ConfigReader.t("command.messages"),
                        NamedTextColor.GREEN
                ));
            }
            case "all" -> {
                ConfigReader.loadConfig();
                ConfigReader.loadMessages();
                source.sendMessage(Component.text(
                        ConfigReader.t("command.all"),
                        NamedTextColor.GREEN
                ));
            }
            default -> source.sendMessage(Component.text(
                    ConfigReader.t("command.unknown", java.util.Map.of("arg", args[0])),
                    NamedTextColor.RED
            ));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("ddns.reload");
    }
}
