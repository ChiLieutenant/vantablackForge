package net.vantahub.systems.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import net.vantahub.Forge;
import net.vantahub.systems.recipe.Recipe;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.leonhard.storage.Yaml;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemGUI implements Listener{

	public static HashMap<Player, Recipe> currRecipe = new HashMap<Player, Recipe>();
	public static HashMap<Player, Inventory> itemGuis = new HashMap<Player, Inventory>();
	
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		openFirstItemGUI(event.getPlayer());
	}

	public static List<Recipe> getAllRecipes(){
		List<Recipe> recipes = new ArrayList<Recipe>();
		for(String s : new Yaml("recipes", "plugins/Forge").getData().keySet()) {
			Recipe rec = new Recipe(s);
			if(rec.getMaterial() != null && rec.getItemName() != null && rec.getLevel() > 0 && rec.isEnabled() && Arrays.asList(ForgeGUI.patterns).contains(rec.getPatternType())) {
				recipes.add(rec);
			}
		}
		return recipes;
	}

	public static List<Recipe> getAllRecipes(Player player){
		List<Recipe> recipes = new ArrayList<Recipe>();
		for(String s : new Yaml("recipes", "plugins/Forge").getData().keySet()) {
			Recipe rec = new Recipe(s);
			if(rec.getMaterial() != null && rec.getItemName() != null && rec.getLevel() > 0 && rec.isEnabled() && Arrays.asList(ForgeGUI.patterns).contains(rec.getPatternType())) {
				if(rec.getPermission() != null && !rec.getPermission().equalsIgnoreCase("")){
					if(player.hasPermission(rec.getPermission())){
						recipes.add(rec);
					}
				}else{
					recipes.add(rec);
				}
			}
		}
		return recipes;
	}
	
	public static boolean hasNext(Recipe recipe, Player player) {
		ListIterator<Recipe> iterator = getAllRecipes(player).listIterator();
		while(iterator.hasNext()) {
			Recipe currRecipe = iterator.next();
			if(currRecipe.getName().equalsIgnoreCase(recipe.getName())) {
				return iterator.hasNext();
			}
		}
		return false;
	}
	
	public static boolean hasPrev(Recipe recipe, Player player) {
		ListIterator<Recipe> iterator = getAllRecipes(player).listIterator(getAllRecipes(player).size());
		while(iterator.hasPrevious()) {
			Recipe currRecipe = iterator.previous();
			if(currRecipe.getName().equalsIgnoreCase(recipe.getName())) {
				return iterator.hasPrevious();
			}
		}
		return false;
	}
	
	public static Recipe getNext(Recipe recipe, Player player) {
		if(hasNext(recipe, player)) {
			ListIterator<Recipe> iterator = getAllRecipes(player).listIterator();
			while(iterator.hasNext()) {
				Recipe currRecipe = iterator.next();
				if(currRecipe.getName().equalsIgnoreCase(recipe.getName())) {
					return iterator.next();
				}
			}
		}
		return null;
	}
	
	public static Recipe getPrev(Recipe recipe, Player player) {
		if(hasPrev(recipe, player)) {
			ListIterator<Recipe> iterator = getAllRecipes(player).listIterator(getAllRecipes(player).size());
			while(iterator.hasPrevious()) {
				Recipe currRecipe = iterator.previous();
				if(currRecipe.getName().equalsIgnoreCase(recipe.getName())) {
					return iterator.previous();
				}
			}
		}
		return null;
	}

	public static void openFirstItemGUI(Player player){
		if(getAllRecipes(player).isEmpty()){
			return;
		}
		createItemGui(player, getAllRecipes(player).get(0));
	}

	//public static int getLevel(Player player) {
		//return (int) Variables.getVariable("vDb." + player.getName() + "::joblvl::1", null, false);
	//}
	
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }
    
    protected static ItemStack createGuiItem(final int customModelData, final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        meta.setCustomModelData(customModelData);
        
        item.setItemMeta(meta);

        return item;
    }
	
    public static void createItemGui(Player p, Recipe recipe) {
    	currRecipe.put(p, recipe);
    	Inventory gui = Bukkit.createInventory(null, 54, "");
    	itemGuis.put(p, gui);
    	gui.setItem(13, recipe.getItem());
    	gui.setItem(49, createGuiItem(10158, Material.PAPER, ChatColor.DARK_GRAY + "Döv", ChatColor.RED + "Bu eşyayı dövebilmek için en az " + recipe.getLevel() + " seviye olmalısın."));
    	int i = 27;
		for(ItemStack req : recipe.getReqs()) {
			i++;
			gui.setItem(i, req);
		}
		gui.setItem(14, createGuiItem(10002, Material.NAME_TAG, ChatColor.GRAY + "İleri >"));
		gui.setItem(12, createGuiItem(10003, Material.NAME_TAG, ChatColor.GRAY + "< Geri"));
		p.openInventory(gui);
		TexturedInventoryWrapper.setPlayerInventoryTexture(p, new FontImageWrapper("cafer:crafting"));
    }
    
    public static void updateItemGui(Player p, Recipe recipe) {
    	Inventory gui = p.getOpenInventory().getTopInventory();
    	//clear reqs
    	for(int i = 27; i < 34; i++) {
    		gui.clear(i);
    	}
    	gui.setItem(13, recipe.getItem());
    	gui.setItem(49, createGuiItem(10158, Material.PAPER, ChatColor.DARK_GRAY + "Döv", ChatColor.RED + "Bu eşyayı dövebilmek için en az " + recipe.getLevel() + " seviye olmalısın."));
    	int i = 27;
		for(ItemStack req : recipe.getReqs()) {
			i++;
			gui.setItem(i, req);
		}
		p.updateInventory();
		currRecipe.put(p, recipe);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	if(!(event.getWhoClicked() instanceof Player)) {
    		return;
    	}
    	Player player = (Player) event.getWhoClicked();
    	if(!itemGuis.isEmpty() && itemGuis != null && itemGuis.containsKey(player)) {
    		if(event.getInventory().equals(itemGuis.get(player))) {
    			event.setCancelled(true);
    			
    			if(event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
    				if(event.getRawSlot() == 14) {
    					//ileri
    				   	if(currRecipe == null || currRecipe.isEmpty() || !currRecipe.containsKey(player)) {
    			    		return;
    			    	}
    				   	if(!hasNext(currRecipe.get(player), player)) {
    				   		return;
    				   	}
    				   	updateItemGui(player, getNext(currRecipe.get(player), player));
    				   	player.getWorld().playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 2, 0.8f);
    				}
    				if(event.getRawSlot() == 12) {
    					//geri
    				   	if(currRecipe == null || currRecipe.isEmpty() || !currRecipe.containsKey(player)) {
    			    		return;
    			    	}
    				   	if(!hasPrev(currRecipe.get(player), player)) {
    				   		return;
    				   	}
    				   	updateItemGui(player, getPrev(currRecipe.get(player), player));
    				   	player.getWorld().playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 2, 0.8f);
    				}
    				if(event.getRawSlot() == 49){
    					//döv
						if(currRecipe == null || currRecipe.isEmpty() || !currRecipe.containsKey(player)) {
							return;
						}
						if(!(player.hasPermission("admin.demirci") && player.getGameMode() == GameMode.CREATIVE)) {
                            if (!isPlayerHas(player, currRecipe.get(player).getReqs())) {
                                return;
                            }
                            if (currRecipe.get(player).getPermission() != null && !currRecipe.get(player).getPermission().equalsIgnoreCase("")){
                            	if(!player.hasPermission(currRecipe.get(player).getPermission())){
                            		return;
								}
							}
                            removeReqsFromPlayer(player, currRecipe.get(player).getReqs());
                        }
						ForgeGUI.createForgeGUI(player, currRecipe.get(player).getPatternType(), event.getClickedInventory().getItem(13));
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 2, 0.6f);
					}
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
    	if(!itemGuis.isEmpty() && itemGuis != null && itemGuis.containsKey(player)) {
    		if(event.getInventory().equals(itemGuis.get(player))) {
    			event.setCancelled(true);
    		}
    	}
    }
	
	public static boolean isPlayerHas(Player player, List<ItemStack> reqs) {
		for(ItemStack item : reqs) {
			if(!player.getInventory().containsAtLeast(item, item.getAmount())) {
				return false;
			}
		}
		return true;
	}
	
	public static void removeReqsFromPlayer(Player player, List<ItemStack> reqs) {
		for(ItemStack req : reqs) {
			player.getInventory().removeItem(req);
		}
	}
	
	
}
