package net.vantahub.systems.recipe;

import net.vantahub.Forge;
import net.vantahub.systems.guis.ForgeGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemUpdateListener implements Listener {

    public void updateItem(ItemStack item, Player player){
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if(pdc.has(new NamespacedKey(Forge.main, "recipe"), PersistentDataType.STRING)){
            Recipe rec = new Recipe(pdc.get(new NamespacedKey(Forge.main, "recipe"), PersistentDataType.STRING));
            ItemStack i = rec.getItem();
            if(!i.getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName()) || !hasSameAttributes(i, item)){
                ItemMeta im = rec.getItem().getItemMeta();
                List<String> lore = new ArrayList<>();
                if(im.getLore() != null && !im.getLore().isEmpty()){
                    lore.addAll(im.getLore());
                }
                lore.add(ChatColor.GRAY + "Bu eşya " + ChatColor.BOLD + "" + ChatColor.DARK_GRAY + player.getName() + ChatColor.GRAY + " tarafından dövüldü.");
                im.setLore(lore);
                item.setItemMeta(im);
            }
        }
    }


    public boolean hasSameAttributes(ItemStack recipe, ItemStack item){
        for(Attribute att : Attribute.values()){
            if(recipe.getItemMeta().getAttributeModifiers(att) != null) {
                for (AttributeModifier attribute : recipe.getItemMeta().getAttributeModifiers(att)) {
                    if(item.getItemMeta().getAttributeModifiers(att) != null) {
                        for (AttributeModifier attitem : item.getItemMeta().getAttributeModifiers(att)) {
                            if (attribute.getAmount() != attitem.getAmount() || attribute.getOperation() != attitem.getOperation()) {
                                return false;
                            }
                        }
                    }else{
                        return false;
                    }
                }
            }else{
                if(item.getItemMeta().getAttributeModifiers(att) != null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        ItemStack item = event.getCursor();
        if(item == null || item.getItemMeta() == null){
            return;
        }
        if(event.getWhoClicked().getType() != EntityType.PLAYER){
            return;
        }
        updateItem(item, (Player) event.getWhoClicked());
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event){
        ItemStack item =  event.getPlayer().getInventory().getItem(event.getNewSlot());
        if(item == null || item.getItemMeta() == null){
            return;
        }
        updateItem(item, event.getPlayer());
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event){
        ItemStack item = event.getItem().getItemStack();
        if(item == null || item.getItemMeta() == null){
            return;
        }
        updateItem(item, event.getPlayer());
    }

}
