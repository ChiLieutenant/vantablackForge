package net.vantahub.systems.guis;

import java.util.*;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import net.vantahub.Forge;
import net.vantahub.systems.level.levelMethods;
import net.vantahub.systems.recipe.NBTEditor;
import net.vantahub.systems.recipe.Recipe;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ForgeGUI implements Listener{
	
	public static HashMap<Player, Inventory> forgeGuis = new HashMap<Player, Inventory>();
	public static String[] patterns = new String[] {"sword", "axe", "boots", "chestplate", "helmet", "leggings", "spearhead", "tools", "shortweapon"};


	public static int[] getPatternFromType(String type){
		int[] pattern = null;
		switch(type){
			case "sword":
				pattern = new int[] {20,28,29,30,31,32,33,34,38};
				break;
			case "axe":
				pattern = new int[] {21,22,30,31,32,39,40};
				break;
			case "boots":
				pattern = new int[] {21,23,29,30,32,33,38,39,41,42};
				break;
			case "chestplate":
				pattern = new int[] {20,21,23,24,29,30,31,32,33,38,39,40,41,42};
				break;
			case "helmet":
				pattern = new int[] {20,21,22,23,24,29,33,39,41};
				break;
			case "leggings":
				pattern = new int[] {20,21,22,23,24,29,30,32,33,38,39,41,42};
				break;
			case "spearhead":
				pattern = new int[] {22,30,31,32,39,40,41};
				break;
			case "tools":
				pattern = new int[] {20,21,22,23,24,28,29,33,34};
				break;
			case "shortweapon":
				pattern = new int[] {29,30,31,32,33};
				break;
		}
		return pattern;
	}

	public static void createForgeGUI(Player player, String type, ItemStack item) {
		Inventory gui = Bukkit.createInventory(null, 54, "");
		int[] pattern = getPatternFromType(type);
		forgeGuis.put(player, gui);
		gui.setItem(4, item);
		gui.setItem(8, ItemGUI.createGuiItem(10000, Material.BELL, ChatColor.DARK_GRAY + "Döv"));
		gui.setItem(53, ItemGUI.createGuiItem(10000, Material.WATER_BUCKET, ChatColor.AQUA + "Soğut"));
		for(int i : pattern) {
			gui.setItem(i, ItemGUI.createGuiItem(10000, Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "*"));
		}
		player.openInventory(gui);
		TexturedInventoryWrapper.setPlayerInventoryTexture(player, new FontImageWrapper("cafer:" + type + "anvil"));
	}
	
	public static void updateItem(Player player, int slot, ItemStack newItem) {
		Inventory gui = player.getOpenInventory().getTopInventory();
		gui.setItem(slot, newItem);
		player.updateInventory();
	}
	
	public static ItemStack getRandom3() {
		Random rand = new Random();
		int i = rand.nextInt(3);
		ItemStack item = null;
		if(i == 0) {
			item = ItemGUI.createGuiItem(10000, Material.ORANGE_STAINED_GLASS_PANE, ChatColor.RED + "*");
		}
		if(i == 1) {
			item = ItemGUI.createGuiItem(10000, Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "*");
		}
		if(i == 2) {
			item = ItemGUI.createGuiItem(10000, Material.RED_STAINED_GLASS_PANE, ChatColor.DARK_RED + "*");
		}
		return item;
	}
	
	public static ItemStack getRandom2() {
		Random rand = new Random();
		int i = rand.nextInt(2);
		ItemStack item = null;
		if(i == 0) {
			item = ItemGUI.createGuiItem(10000, Material.ORANGE_STAINED_GLASS_PANE, ChatColor.RED + "*");
		}
		if(i == 1) {
			item = ItemGUI.createGuiItem(10000, Material.RED_STAINED_GLASS_PANE, ChatColor.DARK_RED + "*");
		}
		return item;
	}

	public void finish(Player player, Inventory inv){
		int i = 0;
		int a = 0;
		for(ItemStack item : inv.getContents()){
			if(item == null || item.getType() == null){
				continue;
			}
			if(item.getType() == Material.ORANGE_STAINED_GLASS_PANE){
				i++;
			}
			if(item.getType().toString().toLowerCase().contains("glass_pane")){
				a++;
			}
		}
		if(i < 1){
			return;
		}
		ItemStack item = inv.getItem(4);
		Recipe rec = new Recipe(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Forge.main, "recipe"), PersistentDataType.STRING));
		NBTEditor.set(item, NBTEditor.getInt(item, "_durability") * (i/a), "_durability");
		if(levelMethods.getLevel(player, rec.getPatternType()) > 1){
			NBTEditor.set(item, NBTEditor.getInt(item, "_durability") * ((levelMethods.getLevel(player, rec.getPatternType())) * (10/100)));
		}
		levelMethods.addExp(player, rec.getPatternType(), i * rec.getLevel());
		List<String> lore = new ArrayList<>();
		if(item.getItemMeta().getLore() != null && !item.getItemMeta().getLore().isEmpty()){
			lore.addAll(item.getItemMeta().getLore());
		}
		lore.add(ChatColor.GRAY + "Bu eşya " + ChatColor.BOLD + "" + ChatColor.DARK_GRAY + player.getName() + ChatColor.GRAY + " tarafından dövüldü.");
		ItemMeta im = item.getItemMeta();
		im.setLore(lore);
		item.setItemMeta(im);
		player.getWorld().dropItem(player.getLocation().add(0, 0.4, 0), item);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 2, 0.2f);
		forgeGuis.remove(player);
	}


	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(!(event.getPlayer() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getPlayer();
		if(!forgeGuis.isEmpty() && forgeGuis != null && forgeGuis.containsKey(player)) {
			if (event.getInventory().equals(forgeGuis.get(player))) {
				finish(player, event.getInventory());
			}
		}
	}

	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	if(!(event.getWhoClicked() instanceof Player)) {
    		return;
    	}
    	Player player = (Player) event.getWhoClicked();
    	if(!forgeGuis.isEmpty() && forgeGuis != null && forgeGuis.containsKey(player)) {
    		if(event.getInventory().equals(forgeGuis.get(player))) {
    			event.setCancelled(true);
    			if(event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
    				if(event.getCurrentItem() == null){
    					return;
					}
					if(event.getClick() == ClickType.DOUBLE_CLICK) {
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 2, 0.5f);
						if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
							updateItem(player, event.getRawSlot(), getRandom3());
							return;
						}
						if (event.getCurrentItem().getType() == Material.YELLOW_STAINED_GLASS_PANE) {
							updateItem(player, event.getRawSlot(), getRandom2());
							return;
						}
						if (event.getCurrentItem().getType() == Material.ORANGE_STAINED_GLASS_PANE) {
							updateItem(player, event.getRawSlot(), ItemGUI.createGuiItem(10000, Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "*"));
							return;
						}
						if (event.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
							updateItem(player, event.getRawSlot(), ItemGUI.createGuiItem(10000, Material.BLACK_STAINED_GLASS_PANE, ChatColor.BLACK + "*"));
							return;
						}
					}
    				if(event.getRawSlot() == 53){
    					for(int i = 0; i < 54; i++){
    						ItemStack item = event.getClickedInventory().getItem(i);
    						if(item == null || item.getType() == null){
    							continue;
							}
    						if(item.getType() == Material.RED_STAINED_GLASS_PANE){
    							updateItem(player, i, ItemGUI.createGuiItem(10000, Material.ORANGE_STAINED_GLASS_PANE, ChatColor.RED + "*"));
							}
    						if(item.getType() == Material.ORANGE_STAINED_GLASS_PANE){
    							updateItem(player, i , ItemGUI.createGuiItem(10000, Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "*"));
							}
						}
    					player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 2, 0.2f);
						return;
    				}
    			}
				if(event.getRawSlot() == 8){
					finish(player, event.getClickedInventory());
					player.closeInventory();
				}
    		}
    	}
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
    	if(!(event.getWhoClicked() instanceof Player)) {
    		return;
    	}
    	Player player = (Player) event.getWhoClicked();
    	if(!forgeGuis.isEmpty() && forgeGuis != null && forgeGuis.containsKey(player)) {
    		if(event.getInventory().equals(forgeGuis.get(player))) {
    			event.setCancelled(true);
    		}
    	}
    }
	
}
