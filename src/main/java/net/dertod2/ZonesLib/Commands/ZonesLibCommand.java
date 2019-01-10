package net.dertod2.ZonesLib.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.dertod2.ZonesLib.Binary.ZonesLib;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ZonesLibCommand implements TabExecutor {
    private final Map<String, String> tabCategoryMap = ImmutableMap.<String, String>builder()
            .put("reload", "zoneslib.commands.zoneslib.reload").put("update", "zoneslib.commands.zoneslib.update")
            .build();
    private Map<String, BaseComponent[]> helpMap;

    public ZonesLibCommand() {
        this.helpMap = new HashMap<String, BaseComponent[]>();

        this.helpMap.put("zoneslib.commands.zoneslib.reload",
                new ComponentBuilder("/zl ").color(ChatColor.DARK_GRAY).append("reload").color(ChatColor.GRAY)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                TextComponent.fromLegacyText("Reloads the plugin and settings")))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zoneslib:zl reload")).create());
        this.helpMap.put("zoneslib.commands.zoneslib.update",
                new ComponentBuilder("/zl ").color(ChatColor.DARK_GRAY).append("update").color(ChatColor.GRAY)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                TextComponent.fromLegacyText("Reloads the plugin and settings")))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zoneslib:zl update")).create());
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")
                && sender.hasPermission(this.tabCategoryMap.get("reload"))) {
            ZonesLib.getInstance().reloadConfig();
            Bukkit.getPluginManager().disablePlugin(ZonesLib.getInstance());
            Bukkit.getPluginManager().enablePlugin(ZonesLib.getInstance());
            sender.sendMessage(ChatColor.GREEN + "Plugin and configuration reloaded!");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("update")
                && sender.hasPermission(this.tabCategoryMap.get("update"))) {
            sender.sendMessage(ChatColor.GREEN
            + "Checking for updates... When no further messages appears there are no"
            + "updates available!");
            ZonesLib.updater.check(sender);
        } else {
            for (Entry<String, BaseComponent[]> entry : this.helpMap.entrySet()) {
                if (sender.hasPermission(entry.getKey())) {
                    if (sender instanceof Player) {
                        ((Player) sender).spigot().sendMessage(entry.getValue());
                    } else {
                        sender.sendMessage(TextComponent.toLegacyText(entry.getValue()));
                    }
                }
            }
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return ZonesLibCommand.tabCompleteList(args[0], this.tabCategoryMap, sender);
        }

        return ImmutableList.<String>of();
    }

    private static List<String> tabCompleteList(final String token, final Map<String, String> originals,
            final Permissible permissible) {
        List<String> collection = new ArrayList<String>(originals.size());

        for (Entry<String, String> entry : originals.entrySet()) {
            if (entry.getValue().length() == 0 || permissible.hasPermission(entry.getValue())) {
                if (StringUtil.startsWithIgnoreCase(entry.getKey(), token)) {
                    collection.add(entry.getKey());
                }
            }
        }

        return collection;
    }
}