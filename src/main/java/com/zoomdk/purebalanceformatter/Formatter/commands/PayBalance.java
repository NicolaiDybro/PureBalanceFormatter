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
import java.util.Locale;

public class PayBalance implements CommandExecutor {


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
            long balanceadd = 1;
            Economy economy = Main.getEconomy();
            Player p = (Player) sender;
            if (command.getName().equalsIgnoreCase("pay")) {
                if (config.get().getString("settings.pay").equals("disabled"))  { return false; }
                if (!(p.hasPermission("pbf.pay"))) {
                    String messageSend = "{missing_permission}";
                    messageSend = replaceText(messageSend);
                    p.sendMessage(messageSend);
                    return true;
                }
                if (!(args.length >= 2)) {
                    p.sendMessage(replaceText(messages.get().getString("pay.pay-correct-usage")));
                    return true; }
                Object[] formatter1 = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                for (Object key : formatter1) {
                    if (args[1].toUpperCase(Locale.ROOT).contains(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT))) {
                        args[1] = args[1].toUpperCase(Locale.ROOT).replaceAll(String.valueOf(config.get().getString("formatter." + key + ".format").toUpperCase(Locale.ROOT)), "");
                        try {
                            Double.parseDouble(args[1]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(replaceText(messages.get().getString("admin.give-correct-usage")));
                            return true;
                        }
                        balanceadd = (long) (config.get().getLong("formatter." + key + ".divide") * Double.parseDouble(args[1]));
                        args[1] = String.valueOf(balanceadd);
                    }
                }
                try {
                    Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage(replaceText(messages.get().getString("pay.pay-correct-usage")));
                    return true;
                }

                Player op = Bukkit.getPlayer(args[0]);
                OfflinePlayer OFFLINEop = Bukkit.getOfflinePlayer(args[0]);
                if (!(OFFLINEop.isOnline())) {
                    p.sendMessage(replaceText(messages.get().getString("pay.pay-player-not-online").replaceAll("%player%", OFFLINEop.getName())));
                    return true; }
                if (op.getName().equals(p.getName())) {
                    p.sendMessage(replaceText(messages.get().getString("pay.pay-message-yourself").replaceAll("%player%", op.getName())));
                    return true; }
                if (economy.getBalance(p) < Double.parseDouble(args[1])) {
                    p.sendMessage(replaceText(messages.get().getString("pay.pay-message-not-enough").replaceAll("%player%", op.getName())));
                    return true; }
                Object[] formatter = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                for (Object key : formatter) {
                    long l1 = config.get().getLong("formatter." + key + ".rangemin");
                    long l2 = config.get().getLong("formatter." + key + ".rangemax");
                    if (Double.parseDouble(args[1]) >= l1 && Double.parseDouble(args[1]) <= l2) {
                        long pBalance = (long) (Double.parseDouble(args[1]) / config.get().getLong("formatter." + key + ".divide"));
                        long takeBalance = (long) Double.parseDouble(args[1]);
                        DecimalFormat df = new DecimalFormat(config.get().getString("formatter." + key + ".decimalformat"));
                        p.sendMessage(replaceText(messages.get().getString("pay.pay-message-other").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format"))).replaceAll("%paid_player%", op.getName())));
                        op.sendMessage(replaceText(messages.get().getString("pay.pay-message-recieve").replaceAll("%balance_send_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format"))).replaceAll("%recieve_player%", p.getName())));

                        economy.withdrawPlayer(p, takeBalance);
                        economy.depositPlayer(op, takeBalance);
                        return true;
                    }
                }
                DecimalFormat df = new DecimalFormat("#.##");
                p.sendMessage(replaceText(messages.get().getString("pay.pay-message-other").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[1]))).replaceAll("%paid_player%", op.getName())));
                op.sendMessage(replaceText(messages.get().getString("pay.pay-message-recieve").replaceAll("%balance_send_formatted%", df.format(Double.parseDouble(args[1]))).replaceAll("%recieve_player%", p.getName())));
                economy.withdrawPlayer(p, Double.parseDouble(args[1]));
                economy.depositPlayer(op, Double.parseDouble(args[1]));
                return true;
                }




            if (command.getName().equalsIgnoreCase("balance")) {
                if (config.get().getString("settings.balance").equals("disabled"))  { return false; }

                if (args.length == 1) {
                    if (!(p.hasPermission("pbf.balance.others"))) {
                        String messageSend = "{missing_permission}";
                        messageSend = replaceText(messageSend);
                        p.sendMessage(messageSend);
                        return true;
                    }
                    OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
                    Object[] formatter = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter) {
                        long l1 = config.get().getLong("formatter." + key + ".rangemin");
                        long l2 = config.get().getLong("formatter." + key + ".rangemax");


                        if (economy.getBalance(op) >= l1 && economy.getBalance(op) <= l2) {
                            double pBalance = (economy.getBalance(op) / config.get().getLong("formatter." + key + ".divide"));
                            DecimalFormat df = new DecimalFormat(config.get().getString("formatter." + key + ".decimalformat"));
                            p.sendMessage(replaceText(messages.get().getString("balance.balance-message-other").replaceAll("%balance_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format"))).replaceAll("%player%", op.getName())));
                            return true;
                        }
                    }
                    p.sendMessage(replaceText(messages.get().getString("balance.balance-message-other").replaceAll("%balance_formatted%", String.valueOf(economy.getBalance(op))).replaceAll("%player%", op.getName())));
                    return true;
                    }
                else {
                    if (!(p.hasPermission("pbf.balance"))) {
                        String messageSend = "{missing_permission}";
                        messageSend = replaceText(messageSend);
                        p.sendMessage(messageSend);
                        return true;
                    }
                    Object[] formatter = config.get().getConfigurationSection("formatter").getKeys(false).toArray();
                    for (Object key : formatter) {
                        long l1 = config.get().getLong("formatter." + key + ".rangemin");
                        long l2 = config.get().getLong("formatter." + key + ".rangemax");
                        if (economy.getBalance(p) >= l1 && economy.getBalance(p) <= l2) {
                            double pBalance = (economy.getBalance(p) / config.get().getLong("formatter." + key + ".divide"));
                            DecimalFormat df = new DecimalFormat(config.get().getString("formatter." + key + ".decimalformat"));
                            p.sendMessage(replaceText(messages.get().getString("balance.balance-message-yourself").replaceAll("%balance_formatted%", df.format(pBalance) + (config.get().getString("formatter." + key + ".format")))));
                            return true;
                        }


                }
                    p.sendMessage(replaceText(messages.get().getString("balance.balance-message-yourself").replaceAll("%balance_formatted%", String.valueOf(economy.getBalance(p))).replaceAll("%player%", p.getName())));
                    return true;
                }
                }


                return true;
            }


            return false;
        }
    }
