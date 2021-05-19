package com.zoomdk.purebalanceformatter;

import com.zoomdk.purebalanceformatter.Formatter.commands.PayBalance;
import com.zoomdk.purebalanceformatter.Formatter.commands.PureBalanceFormatter;

import com.zoomdk.purebalanceformatter.Formatter.config.config;
import com.zoomdk.purebalanceformatter.Formatter.config.messages;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {
    private static Economy econ = null;
    private static File file;


    @Override
    public void onEnable() {

        int pluginId = 11404;
        Metrics metrics = new Metrics(this, pluginId);


        if (!setupEconomy() ) {
            System.out.println("Economy virker ikke.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        file = new File(Bukkit.getServer().getPluginManager().getPlugin("PureBalanceFormatter").getDataFolder(), "messages.yml");
        if (!(file.exists())) {
            saveResource("messages.yml", false);
            System.out.println("[§ePureBalanceFormatter§r] Thanks for installing §aPureBalance Formatter");
            System.out.println("[§ePureBalanceFormatter§r] Please report, if you find any §4errors§r!");
        }
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("PureBalanceFormatter").getDataFolder(), "config.yml");
        if (!(file.exists())) {
            saveResource("config.yml", false);
        }
        config.setup();
        messages.setup();
        config.get().options().copyDefaults(true);
        messages.get().options().copyDefaults(true);
        System.out.println("PureBalanceFormatter v0.3 successfully §astarted");
        getCommand("purebalanceformatter").setExecutor(new PureBalanceFormatter());
        getCommand("pay").setExecutor(new PayBalance());
        getCommand("balance").setExecutor(new PayBalance());

    }

    @Override
    public void onDisable() {
        System.out.println("PureBalanceFormatter v.0.3 successfully §cdisabled");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }


}
