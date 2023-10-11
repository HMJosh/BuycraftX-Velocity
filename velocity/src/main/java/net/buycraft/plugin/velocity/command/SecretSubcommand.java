package net.buycraft.plugin.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import net.buycraft.plugin.BuyCraftAPI;
import net.buycraft.plugin.data.responses.ServerInformation;
import net.buycraft.plugin.velocity.BuycraftPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;
import java.util.logging.Level;

public class SecretSubcommand implements Subcommand {
    private final BuycraftPlugin plugin;

    public SecretSubcommand(final BuycraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSource sender, final String[] args) {
        if (!sender.equals(plugin.getServer().getConsoleCommandSource())) {
            final TextComponent textComponent = Component.text().content(plugin.getI18n().get("secret_console_only")).color(NamedTextColor.RED).build();
            sender.sendMessage(textComponent);
            return;
        }

        if (args.length != 1) {
            final TextComponent textComponent = Component.text().content(plugin.getI18n().get("secret_need_key")).color(NamedTextColor.RED).build();
            sender.sendMessage(textComponent);
            return;
        }

        plugin.getPlatform().executeAsync(() -> {
            BuyCraftAPI client = BuyCraftAPI.create(args[0], plugin.getHttpClient());
            try {
                plugin.updateInformation(client);
            } catch (IOException e) {
                plugin.getLogger().error("Unable to verify secret", e);
                final TextComponent textComponent = Component.text().content(plugin.getI18n().get("secret_does_not_work")).color(NamedTextColor.RED).build();
                sender.sendMessage(textComponent);
                return;
            }

            ServerInformation information = plugin.getServerInformation();
            plugin.setApiClient(client);
            plugin.getConfiguration().setServerKey(args[0]);
            try {
                plugin.saveConfiguration();
            } catch (IOException e) {
                final TextComponent textComponent = Component.text().content(plugin.getI18n().get("secret_cant_be_saved")).color(NamedTextColor.RED).build();
                sender.sendMessage(textComponent);
            }

            final TextComponent textComponent = Component.text().content(plugin.getI18n().get("secret_success",
                    information.getServer().getName(), information.getAccount().getName())).color(NamedTextColor.GREEN).build();
            sender.sendMessage(textComponent);
            plugin.getPlatform().executeAsync(plugin.getDuePlayerFetcher());
        });
    }

    @Override
    public String getDescription() {
        return "Sets the secret key to use for this server.";
    }
}
