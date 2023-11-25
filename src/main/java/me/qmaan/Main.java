package me.qmaan;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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

    @Override
    public void onEnable() {

        File configFile = new File(this.getDataFolder(), "messages.yml");
        if (!configFile.exists()) {
            this.saveResource("messages.yml", false);
        }

        getCommand("broadcast").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sendAll(args[0], null);
            return true;
        }

        sendAll(args[0], player.getName());


        return true;
    }


    private void sendAll(String message, String name) {

        String depend = getConfig().getString("broadcast");

        if (name != null) {
            if (depend != null) {
                depend = depend.replace("{player}", name);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null) {
                player.sendMessage(colorhex(depend + message));
            }
        }

    }

}
