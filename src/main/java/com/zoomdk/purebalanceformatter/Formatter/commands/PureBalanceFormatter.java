package com.zoomdk.purebalanceformatter.Formatter.commands;

import com.zoomdk.purebalanceformatter.Formatter.config.config;
import com.zoomdk.purebalanceformatter.Formatter.config.messages;
import com.zoomdk.purebalanceformatter.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;


public class PureBalanceFormatter implements CommandExecutor {



    public static String replaceText(String string) {
        Object[] placeholders = messages.get().getConfigurationSection("text_placeholders").getKeys(false).toArray();
        for (Object key : placeholders) {
            if (key.equals("currency")) {
                String navn = messages.get().getString("text_placeholders." + key);
                if (navn.equals("$")) {
                    navn = "\\$";
                }
                string = string.replaceAll("\\{" + key.toString() + "\\}", navn);
                string = string.replaceAll("&", "§");
            }
            else {
                String navn = messages.get().getString("text_placeholders." + key);
                string = string.replaceAll("\\{" + key.toString() + "\\}", navn);
                string = string.replaceAll("&", "§");
            }

        }
        return string;
    }
    @Deprecated
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("pbf.admin")) {
                long balanceadd = 1;
                Economy economy = Main.getEconomy();
                if (args.length == 0) {
                    List<String> helpmenu = messages.get().getStringList("helpmenu");
                    for (String messageSend : helpmenu) {
                        messageSend = replaceText(messageSend);
                        p.sendMessage(messageSend);
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    p.sendMessage(replaceText(messages.get().getString("admin.reload")));
                    config.reload();
                    messages.reload();
                    return true;
                }
                if (args[0].equalsIgnoreCase("give")) {
                    if (!(args.length >= 3)) {
                        p.sendMessage(replaceText(messages.get().getString("admin.give-correct-usage")));
                        return true;
                    }
                    Object[] formatter1 = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter1) {
                        if (args[2].toUpperCase(Locale.ROOT).contains(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT))) {

                            args[2] = args[2].toUpperCase(Locale.ROOT).replaceAll(String.valueOf(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT)), "");
                            try {
                                Double.parseDouble(args[2]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(replaceText(messages.get().getString("admin.give-correct-usage")));
                                return true;
                            }
                            balanceadd = (long) (config.get().getLong("formatter." + key + ".divide") * Double.parseDouble(args[2]));
                            args[2] = String.valueOf(balanceadd);
                        }
                    }
                    try {
                        Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        p.sendMessage(replaceText(messages.get().getString("admin.give-correct-usage")));
                        return true;
                    }
                    OfflinePlayer OFFLINEPlayer = Bukkit.getOfflinePlayer(args[1]);

                    Object[] formatter = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter) {
                        long l1 = config.get().getLong("formatter." + key + ".rangemin");
                        long l2 = config.get().getLong("formatter." + key + ".rangemax");
                        if (Double.parseDouble(args[2]) >= l1 && Double.parseDouble(args[2]) <= l2) {
                            double pBalance = (Double.parseDouble(args[2]) / config.get().getLong("formatter." + key + ".divide"));

                            DecimalFormat df = new DecimalFormat(config.get().getString("formatter." + key + ".decimalformat"));

                            p.sendMessage(replaceText(messages.get().getString("admin.give-message-send").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format"))).replaceAll("%player_send%", OFFLINEPlayer.getName())));
                            if (OFFLINEPlayer.isOnline()) {
                                Player ONLINEPlayer = Bukkit.getPlayer(args[1]);
                                ONLINEPlayer.sendMessage(replaceText(messages.get().getString("admin.give-message-recieve").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format")))));
                            }
                            economy.depositPlayer(OFFLINEPlayer, Double.parseDouble(args[2]));
                            return true;
                        }
                    }
                    DecimalFormat df = new DecimalFormat("#.##");
                    p.sendMessage(replaceText(messages.get().getString("admin.give-message-send").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[2]))).replaceAll("%player_send%", OFFLINEPlayer.getName())));
                    if (OFFLINEPlayer.isOnline()) {
                        Player ONLINEPlayer = Bukkit.getPlayer(args[1]);
                        ONLINEPlayer.sendMessage(replaceText(messages.get().getString("admin.give-message-recieve").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[2])))));
                    }
                    economy.depositPlayer(OFFLINEPlayer, Double.parseDouble(args[2]));
                    return true;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    if (!(args.length >= 3)) {
                        p.sendMessage(replaceText(messages.get().getString("admin.remove-correct-usage")));
                        return true;
                    }
                    Object[] formatter1 = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter1) {
                        if (args[2].toUpperCase(Locale.ROOT).contains(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT))) {

                            args[2] = args[2].toUpperCase(Locale.ROOT).replaceAll(String.valueOf(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT)), "");
                            try {
                                Double.parseDouble(args[2]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(replaceText(messages.get().getString("admin.remove-correct-usage")));
                                return true;
                            }
                            balanceadd = (long) (config.get().getLong("formatter." + key + ".divide") * Double.parseDouble(args[2]));
                            args[2] = String.valueOf(balanceadd);
                        }
                    }
                    try {
                        Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        p.sendMessage(replaceText(messages.get().getString("admin.remove-correct-usage")));
                        return true;
                    }
                    OfflinePlayer OFFLINEPlayer = Bukkit.getOfflinePlayer(args[1]);

                    Object[] formatter = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter) {
                        long l1 = config.get().getLong("formatter." + key + ".rangemin");
                        long l2 = config.get().getLong("formatter." + key + ".rangemax");
                        if (Double.parseDouble(args[2]) >= l1 && Double.parseDouble(args[2]) <= l2) {
                            double pBalance = (Double.parseDouble(args[2]) / config.get().getLong("formatter." + key + ".divide"));
                            DecimalFormat df = new DecimalFormat(config.get().getString("formatter." + key + ".decimalformat"));

                            p.sendMessage(replaceText(messages.get().getString("admin.remove-message-taken").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format"))).replaceAll("%player_send%", OFFLINEPlayer.getName())));
                            if (OFFLINEPlayer.isOnline()) {
                                Player ONLINEPlayer = Bukkit.getPlayer(args[1]);
                                ONLINEPlayer.sendMessage(replaceText(messages.get().getString("admin.remove-message-recieve").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format")))));
                            }
                            if (economy.getBalance(OFFLINEPlayer) <= Double.parseDouble(args[2])) {
                                economy.withdrawPlayer(OFFLINEPlayer, economy.getBalance(OFFLINEPlayer));
                                return true;
                            }
                            economy.withdrawPlayer(OFFLINEPlayer, Double.parseDouble(args[2]));
                            return true;
                        }
                    }
                    DecimalFormat df = new DecimalFormat("#.##");
                    p.sendMessage(replaceText(messages.get().getString("admin.remove-message-taken").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[2]))).replaceAll("%player_send%", OFFLINEPlayer.getName())));
                    if (OFFLINEPlayer.isOnline()) {
                        Player ONLINEPlayer = Bukkit.getPlayer(args[1]);
                        ONLINEPlayer.sendMessage(replaceText(messages.get().getString("admin.remove-message-recieve").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[2])))));
                    }
                    if (economy.getBalance(OFFLINEPlayer) <= Double.parseDouble(args[2])) {
                        economy.withdrawPlayer(OFFLINEPlayer, economy.getBalance(OFFLINEPlayer));
                        return true;
                    }
                    economy.withdrawPlayer(OFFLINEPlayer, Double.parseDouble(args[2]));
                    return true;
                }

                if (args[0].equalsIgnoreCase("set")) {
                    if (!(args.length >= 3)) {
                        p.sendMessage(replaceText(messages.get().getString("admin.set-correct-usage")));
                        return true;
                    }
                    Object[] formatter1 = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter1) {
                        if (args[2].toUpperCase(Locale.ROOT).contains(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT))) {

                            args[2] = args[2].toUpperCase(Locale.ROOT).replaceAll(String.valueOf(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT)), "");
                            try {
                                Double.parseDouble(args[2]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(replaceText(messages.get().getString("admin.set-correct-usage")));
                                return true;
                            }
                            balanceadd = (long) (config.get().getLong("formatter." + key + ".divide") * Double.parseDouble(args[2]));
                            args[2] = String.valueOf(balanceadd);
                        }
                    }
                    try {
                        Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        p.sendMessage(replaceText(messages.get().getString("admin.set-correct-usage")));
                        return true;
                    }
                    OfflinePlayer OFFLINEPlayer = Bukkit.getOfflinePlayer(args[1]);

                    Object[] formatter = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter) {
                        long l1 = config.get().getLong("formatter." + key + ".rangemin");
                        long l2 = config.get().getLong("formatter." + key + ".rangemax");
                        if (Double.parseDouble(args[2]) >= l1 && Double.parseDouble(args[2]) <= l2) {
                            double pBalance = (Double.parseDouble(args[2]) / config.get().getLong("formatter." + key + ".divide"));
                            DecimalFormat df = new DecimalFormat(config.get().getString("formatter." + key + ".decimalformat"));

                            p.sendMessage(replaceText(messages.get().getString("admin.set-message-send").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format"))).replaceAll("%player_send%", OFFLINEPlayer.getName())));
                            if (OFFLINEPlayer.isOnline()) {
                                Player ONLINEPlayer = Bukkit.getPlayer(args[1]);
                                ONLINEPlayer.sendMessage(replaceText(messages.get().getString("admin.set-message-recieve").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format")))));
                            }
                            economy.withdrawPlayer(OFFLINEPlayer, economy.getBalance(OFFLINEPlayer));
                            economy.depositPlayer(OFFLINEPlayer, Double.parseDouble(args[2]));
                            return true;
                        }
                    }
                    DecimalFormat df = new DecimalFormat("#.##");
                    p.sendMessage(replaceText(messages.get().getString("admin.set-message-send").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[2]))).replaceAll("%player_send%", OFFLINEPlayer.getName())));
                    if (OFFLINEPlayer.isOnline()) {
                        Player ONLINEPlayer = Bukkit.getPlayer(args[1]);
                        ONLINEPlayer.sendMessage(replaceText(messages.get().getString("admin.set-message-recieve").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[2])))));
                    }
                    economy.withdrawPlayer(OFFLINEPlayer, economy.getBalance(OFFLINEPlayer));
                    economy.depositPlayer(OFFLINEPlayer, Double.parseDouble(args[2]));
                    return true;
                }


                else {
                    List<String> helpmenu = messages.get().getStringList("helpmenu");
                    for (String messageSend : helpmenu) {
                        messageSend = replaceText(messageSend);
                        p.sendMessage(messageSend);
                    }
                }
                return true;
            }
            else {
                String messageSend = "{missing_permission}";
                messageSend = replaceText(messageSend);
                p.sendMessage(messageSend);
            }
        }


        return false;
    }
}
