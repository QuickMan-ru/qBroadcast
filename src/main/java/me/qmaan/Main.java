package me.qmaan;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin {


    public static String colorhex(String message) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        int subVersion = Integer.parseInt(version.split("_")[1]);

        if (subVersion >= 16) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        } else {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        return message;
    }


    static FileConfiguration config;
    final File configFile = new File(this.getDataFolder(), "messages.yml");


    @Override
    public void onEnable() {

        loadConfig();
        config = YamlConfiguration.loadConfiguration(configFile);

        getCommand("broadcast").setExecutor(this);
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            this.saveResource("messages.yml", false);
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String allArgs = String.join(" ", args);

        if (!(sender instanceof Player player)) {

            if (args.length < 1) {
                sender.sendMessage("/broadcast <message>|reload");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                this.reloadConfig();
                sender.sendMessage("Config reloaded.");
                return true;
            }

            sendAll(allArgs, null);
            return true;
        }

        sendAll(allArgs, player.getName());
        return true;
    }


    private void sendAll(String message, String name) {

        String depend = config.getString("broadcast");
        String depend2 = config.getString("broadcast_console");

        String replacedMessage = null;
        if (name != null) {
            if (depend != null) {
                replacedMessage = depend.replace("{player}", name);
            } else {
                Bukkit.getServer().getLogger().severe("Pls check config");
                loadConfig();
            }
        } else {
            replacedMessage = depend2;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null) {
                player.sendMessage(colorhex((replacedMessage != null ? replacedMessage : "") + message));
            }
        }

    }

}
