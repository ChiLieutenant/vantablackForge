package net.vantahub.systems.level;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import net.vantahub.systems.guis.ForgeGUI;
import net.vantahub.systems.guis.ItemGUI;
import net.vantahub.systems.recipe.NBTEditor;
import net.vantahub.systems.recipe.Recipe;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class LevelGUI implements Listener {

    public static HashMap<Player, Integer> patternPage = new HashMap<>();
    public static HashMap<Player, Integer> levelPage = new HashMap<>();
    public static HashMap<Player, String> openedPattern = new HashMap<>();
    public static HashMap<Player, Inventory> levelGuis = new HashMap<Player, Inventory>();

    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    public static List<ItemStack> getItemsByPattern(Player player, String pattern){
        List<ItemStack> items = new ArrayList<>();
        if(ItemGUI.getAllRecipes(player).isEmpty()){
            items.add(new ItemStack(Material.AIR));
        }else{
            List<Recipe> patternRecs = new ArrayList<>();
            for(Recipe rec : ItemGUI.getAllRecipes(player)){
                if(rec.getPatternType().equalsIgnoreCase(pattern)) patternRecs.add(rec);
            }
            if(patternRecs.isEmpty() || patternRecs == null){
                items.add(new ItemStack(Material.AIR));
            }else{
                for(Recipe rec : patternRecs){
                    items.add(rec.getItem());
                }
            }
        }
        return items;
    }

    public static List<ItemStack> getPatternItems(){
        List<ItemStack> items = new ArrayList<>();
        for(String pattern : ForgeGUI.patterns){
            if(pattern.equalsIgnoreCase("sword")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.IRON_SWORD, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("axe")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.IRON_AXE, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("boots")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.IRON_BOOTS, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("chestplate")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.IRON_CHESTPLATE, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("helmet")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.IRON_HELMET, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("leggings")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.IRON_LEGGINGS, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("spearhead")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.STICK, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("tools")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.IRON_PICKAXE, ChatColor.RED + s));
            }
            if(pattern.equalsIgnoreCase("shortweapon")){
                String str = levelMethods.getType(pattern);
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                items.add(createGuiItem(Material.STICK, ChatColor.RED + s));
            }
        }
        return items;
    }

    protected static ItemStack createGuiItem(final Material material, final String name) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    protected static ItemStack createGuiItem(final int customModelData, final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        meta.setCustomModelData(customModelData);
        for(EquipmentSlot slot : EquipmentSlot.values()){
            meta.removeAttributeModifier(slot);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void setLevel(Player p, String pattern){
        Inventory gui = p.getOpenInventory().getTopInventory();
        int level = levelMethods.getLevel(p, pattern);
        double exp = levelMethods.getExp(p, pattern);
        double maxexp = level * 100;
        double ratio = exp / maxexp;
        ratio *= 5;
        double floorRatio = Math.floor(ratio);
        for(double a = 1; a <= floorRatio; a+=1){
            gui.setItem((int) (46 + a), createGuiItem(10000, Material.BLUE_DYE, ChatColor.DARK_GRAY + "Seviye: " + ChatColor.GRAY + level, ChatColor.DARK_GRAY + "Exp: " + ChatColor.WHITE + levelMethods.getExp(p, pattern) + ChatColor.GRAY + "/" + level * 100));
        }
        if(ratio - floorRatio >= 0.5){
            //+half
            gui.setItem((int) (47 + floorRatio), createGuiItem(10001, Material.BLUE_DYE, ChatColor.DARK_GRAY + "Seviye: " + ChatColor.GRAY + level, ChatColor.DARK_GRAY + "Exp: " + ChatColor.WHITE + levelMethods.getExp(p, pattern) + ChatColor.GRAY + "/" + level * 100));
        }
    }


    public static void createLevelGui(Player p) {
        Inventory gui = Bukkit.createInventory(null, 54, "");
        levelGuis.put(p, gui);
        levelPage.put(p, 1);
        gui.setItem(16, createGuiItem(10002, Material.NAME_TAG, ChatColor.GRAY + "İleri >"));
        gui.setItem(10, createGuiItem(10003, Material.NAME_TAG, ChatColor.GRAY + "< Geri"));
        gui.setItem(35, createGuiItem(10002, Material.NAME_TAG, ChatColor.GRAY + "İleri >"));
        gui.setItem(27, createGuiItem(10003, Material.NAME_TAG, ChatColor.GRAY + "< Geri"));
        int i = 11;
        for(ItemStack item : chopped(getPatternItems(), 5).get(0)){
            gui.setItem(i, item);
            i++;
        }
        p.openInventory(gui);
        TexturedInventoryWrapper.setPlayerInventoryTexture(p, new FontImageWrapper("cafer:levelgui"));
    }

    public static void updateLevelGui(Player p, String pattern, int page){
        Inventory gui = p.getOpenInventory().getTopInventory();
        //clear items
        for(int i = 28; i < 35; i++) {
            gui.clear(i);
        }
        //clear level bar
        for(int i = 47; i < 52; i++) {
            gui.clear(i);
        }
        //put items
        int i = 28;
        for(ItemStack item : chopped(getItemsByPattern(p, pattern), 7).get(page - 1)){
            gui.setItem(i, item);
            i++;
        }
        //set level
        setLevel(p, pattern);
        openedPattern.put(p, pattern);
        patternPage.put(p, page);
        p.updateInventory();
    }

    public static void updateLevelGui(Player p, int page) {
        Inventory gui = p.getOpenInventory().getTopInventory();
        //clear patterns
        for(int i = 11; i < 16; i++) {
            gui.clear(i);
        }
        int i = 11;
        for(ItemStack item : chopped(getPatternItems(), 5).get(page - 1)){
            gui.setItem(i, item);
            i++;
        }
        levelPage.put(p, page);
        p.updateInventory();
    }

    public static String getPatternByType(String type){
        String pattern = null;
        for(Map.Entry<String, String> set : levelMethods.types.entrySet()){
            if(set.getValue().equalsIgnoreCase(type)){
                pattern = set.getKey();
            }
        }
        return pattern;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if(!levelGuis.isEmpty() && levelGuis != null && levelGuis.containsKey(player) && event.getClickedInventory() != null) {
            if(event.getInventory().equals(levelGuis.get(player))) {
                event.setCancelled(true);
                if(event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
                    if(10 < event.getRawSlot() && event.getRawSlot() < 16){
                        if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR){
                            ItemStack item = event.getCurrentItem();
                            String pattern = null;
                            for(String pat : ForgeGUI.patterns){
                                if (item.getItemMeta().getDisplayName().toLowerCase().contains(levelMethods.getType(pat))) pattern = pat;
                            }
                            updateLevelGui(player, pattern, 1);
                        }
                    }
                    if(event.getRawSlot() == 16){
                        if(levelPage == null || !levelPage.containsKey(player)){
                            updateLevelGui(player, 1);
                            return;
                        }
                        if(levelPage.get(player) >= chopped(getPatternItems(), 5).size()){
                            return;
                        }
                        updateLevelGui(player, levelPage.get(player) + 1);
                    }
                    if(event.getRawSlot() == 10){
                        if(levelPage == null || !levelPage.containsKey(player)){
                            updateLevelGui(player, 1);
                            return;
                        }
                        if(levelPage.get(player) <= 1){
                            return;
                        }
                        updateLevelGui(player, levelPage.get(player) - 1);
                    }
                    if(event.getRawSlot() == 35){
                        if(patternPage == null || !patternPage.containsKey(player) || !openedPattern.containsKey(player)){
                            return;
                        }
                        if(patternPage.get(player) >= chopped(getItemsByPattern(player, openedPattern.get(player)), 7).size()){
                            return;
                        }
                        updateLevelGui(player, openedPattern.get(player), patternPage.get(player) + 1);
                    }
                    if(event.getRawSlot() == 27){
                        if(patternPage == null || !patternPage.containsKey(player) || !openedPattern.containsKey(player)){
                            return;
                        }
                        if(patternPage.get(player) <= 1){
                            return;
                        }
                        updateLevelGui(player, openedPattern.get(player), patternPage.get(player) - 1);
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
        if(!levelGuis.isEmpty() && levelGuis != null && levelGuis.containsKey(player)) {
            if(event.getInventory().equals(levelGuis.get(player))) {
                event.setCancelled(true);
            }
        }
    }

}
