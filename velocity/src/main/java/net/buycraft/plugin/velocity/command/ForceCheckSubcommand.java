package net.buycraft.plugin.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import net.buycraft.plugin.velocity.BuycraftPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class ForceCheckSubcommand implements Subcommand {
    private final BuycraftPlugin plugin;

    public ForceCheckSubcommand(final BuycraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (args.length != 0) {
            final TextComponent textComponent = Component.text().content(plugin.getI18n().get("no_params")).color(NamedTextColor.RED).build();
            sender.sendMessage(textComponent);
            return;
        }

        if (plugin.getApiClient() == null) {
            final TextComponent textComponent = Component.text().content(plugin.getI18n().get("need_secret_key")).color(NamedTextColor.RED).build();
            sender.sendMessage(textComponent);
            return;
        }

        if (plugin.getDuePlayerFetcher().inProgress()) {
            final TextComponent textComponent = Component.text().content(plugin.getI18n().get("already_checking_for_purchases")).color(NamedTextColor.RED).build();
            sender.sendMessage(textComponent);
            return;
        }

        plugin.getPlatform().executeAsync(() -> plugin.getDuePlayerFetcher().run(false));
        final TextComponent textComponent = Component.text().content(plugin.getI18n().get("forcecheck_queued")).color(NamedTextColor.GREEN).build();
        sender.sendMessage(textComponent);
    }

    @Override
    public String getDescription() {
        return plugin.getI18n().get("usage_forcecheck");
    }
}
