package net.vantahub.systems.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.vantahub.systems.guis.ForgeGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ChatEditTabCompleter implements TabCompleter {

	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("create");
            commands.add("list");
            commands.add("select");
            commands.add("enable");
            commands.add("disable");
            commands.add("material");
            commands.add("pattern");
            commands.add("permission");
            commands.add("custommodeldata");
            commands.add("name");
            commands.add("lore");
            commands.add("durability");
            commands.add("level");
            commands.add("rarity");
            commands.add("requirements");
            commands.add("attributes");
            commands.add("idiosyncrasy");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("select")) {
            	commands.add("[tarif ismi]");
            }
            if(args[0].equalsIgnoreCase("material") || args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("lore") || args[0].equalsIgnoreCase("durability") || args[0].equalsIgnoreCase("level") || args[0].equalsIgnoreCase("rarity") || args[0].equalsIgnoreCase("pattern") || args[0].equalsIgnoreCase("permission") || args[0].equalsIgnoreCase("custommodeldata")) {
            	commands.add("set");
            	commands.add("remove");
            }
            if(args[0].equalsIgnoreCase("requirements")) {
            	commands.add("add");
            	commands.add("remove");
            	commands.add("edit");
            }
            if(args[0].equalsIgnoreCase("attributes")) {
            	commands.add("set");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3){
            if(args[0].equalsIgnoreCase("material") && args[1].equalsIgnoreCase("set")) {
            	for(Material mat : Material.values()) {
            		commands.add(mat.toString());
            	}
            }
            if(args[0].equalsIgnoreCase("pattern") && args[1].equalsIgnoreCase("set")) {
                commands.addAll(Arrays.asList(ForgeGUI.patterns));
            }
            if(args[0].equalsIgnoreCase("requirements")) {
            	if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("edit")) {
            		Player player = (Player) sender;
            		if(ChatEdit.selectedRecipe.containsKey(player)) {
            			for(String s : ChatEdit.data.singleLayerKeySet(ChatEdit.getRecipe(player) + ".reqs")) {
            				commands.add(s);
            			}
            		}
            	}
            }
            if(args[0].equalsIgnoreCase("attributes") && args[1].equalsIgnoreCase("set")) {
            	for(String s : ChatEdit.attributes) {
            		commands.add(s);
            	}
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        } else if (args.length == 4){
        	if(args[0].equalsIgnoreCase("requirements") && args[1].equalsIgnoreCase("edit")) {
        		commands.add("name");
        		commands.add("lore");
        		commands.add("material");
        		commands.add("count");
        		commands.add("hand");
        	}
        	StringUtil.copyPartialMatches(args[3], commands, completions);
        } else if (args.length == 5){
        	if(args[0].equalsIgnoreCase("requirements") && args[1].equalsIgnoreCase("edit") && args[3].equalsIgnoreCase("material")) {
        		for(Material mat : Material.values()) {
            		commands.add(mat.toString());
            	}
        	}
            if(args[0].equalsIgnoreCase("attributes") && args[1].equalsIgnoreCase("set")){
                commands.add("add");
                commands.add("scalar");
            }
        	StringUtil.copyPartialMatches(args[4], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }

}
