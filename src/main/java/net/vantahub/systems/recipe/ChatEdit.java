package net.vantahub.systems.recipe;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.leonhard.storage.Yaml;

public class ChatEdit implements Listener{

	public static Yaml data = new Yaml("recipes", "plugins/Forge");
	public static String[] attributes = new String[] {"armor", "armor_toughness", "attack_damage", "attack_speed", "attack_knockback", "flying_speed", "follow_range", "knockback_resistance", "luck", "max_health", "movement_speed"};
	public static HashMap<Player, String> selectedRecipe = new HashMap<Player, String>();
	
	public static void createRecipe(Player player, String name) {
		data.set(name + ".creator", player.getName());
		data.set(name + ".enabled", false);
		data.set(name + ".pattern", null);
		data.set(name + ".material", null);
		data.set(name + ".name", null);
		data.set(name + ".permission", null);
		data.set(name + ".reqs", null);
		data.set(name + ".custommodeldata", null);
		data.set(name + ".lore", null);
		data.set(name + ".durability", null);
		data.set(name + ".rarity", null);
		data.set(name + ".level", null);
		for(String attribute : attributes) {
			data.set(name + ".attributes." + attribute + ".operation", null);
			data.set(name + ".attributes." + attribute + ".amount", null);
		}
	}

	public static void selectRecipe(Player pl, String recipe) {
		selectedRecipe.put(pl, recipe);
	}
	
	public static String getRecipe(Player pl) {
		return selectedRecipe.get(pl);
	}
	
	
}
