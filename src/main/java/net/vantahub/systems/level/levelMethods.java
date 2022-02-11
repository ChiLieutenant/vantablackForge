package net.vantahub.systems.level;

import de.leonhard.storage.Json;
import dev.lone.itemsadder.api.ItemsAdder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.vantahub.Forge;
import net.vantahub.systems.guis.ForgeGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class levelMethods implements Listener {

    public static HashMap<String, String> types = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Json data = new Json(event.getPlayer().getUniqueId().toString(),"plugins/Jobs/Oyuncular");
        for(String pattern : ForgeGUI.patterns){
            data.setDefault(pattern + "level", 1);
            data.setDefault(pattern + "exp", 0);
        }
    }

    public static void createTypes(){
        for(String pattern : ForgeGUI.patterns){
            types.put(pattern, getType(pattern));
        }
    }

    public static String getType(String type){
        String newType = null;
        switch (type){
            case "sword":
                newType = "kılıç";
                break;
            case "axe":
                newType = "balta";
                break;
            case "boots":
                newType = "bot";
                break;
            case "chestplate":
                newType = "göğüslük";
                break;
            case "helmet":
                newType = "kask";
                break;
            case "spearhead":
                newType = "mızrak başı";
                break;
            case "leggings":
                newType = "pantolon";
                break;
            case "tools":
                newType = "alet";
                break;
            case "shortweapon":
                newType = "kısa alet";
                break;
        }
        return newType;
    }

    public static int getExp(Player player, String type){
        Json data = new Json(player.getUniqueId().toString(), "plugins/Jobs/Oyuncular");
        return data.getInt(type + "exp");
    }

    public static int getLevel(Player player, String type){
        Json data = new Json(player.getUniqueId().toString(), "plugins/Jobs/Oyuncular");
        return data.getInt(type + "level");
    }

    public static void addExp(Player player, String type, int exp){
        Json data = new Json(player.getUniqueId().toString(), "plugins/Jobs/Oyuncular");
        if(getExp(player, type) + exp >= getLevel(player, type) * 100){
            //level atlat
            addLevel(player, type);
        }else{
            //exp ver
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_GRAY + " " + getType(type) + ChatColor.GRAY + " dövmede " + ChatColor.DARK_GRAY + exp + "xp" + ChatColor.GRAY + " kazandın."));
            data.set(type + "exp", getExp(player, type) + exp);
        }
    }

    public static void addLevel(Player player, String type){
        Json data = new Json(player.getUniqueId().toString(), "plugins/Jobs/Oyuncular");
        if(getLevel(player, type) == 10) return;
        data.set(type + "level", getLevel(player, type) + 1);
        data.set(type + "exp", 0);
        new BukkitRunnable(){
            @Override
            public void run() {
                ItemsAdder.playTotemAnimation(player, "cafer:anim" + type);
                this.cancel();
            }
        }.runTaskLater(Forge.main, 10);
        player.sendMessage(Forge.prefix + ChatColor.DARK_GRAY + " " + getType(type) + ChatColor.GRAY + " dövmede ustalaşarak " + ChatColor.DARK_GRAY + getLevel(player, type) + ChatColor.GRAY + " seviye oldun.");
    }

}
