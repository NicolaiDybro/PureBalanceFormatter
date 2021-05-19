package com.zoomdk.purebalanceformatter.Formatter.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class config {

    private static File file;
    private static FileConfiguration customFile;
    //Finder filen eller laver den
    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("PureBalanceFormatter").getDataFolder(), "config.yml");

        if (!(file.exists())) {
            try {
                file.createNewFile();

            }catch (IOException e) {
                //fejl
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);

    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try {
            customFile.save(file);
        }catch (IOException e) {
            System.out.println("Could not load config.yml");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }


}

