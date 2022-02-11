package net.vantahub.systems.recipe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.vantahub.Forge;
import net.vantahub.systems.recipe.NBTEditor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.leonhard.storage.Yaml;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Recipe {

	Yaml data                    = new Yaml("recipes", "plugins/Forge");
	private String name;
	private String itemname;
	private Material material;
	private List<ItemStack> reqs = new ArrayList<ItemStack>();
	private int customModelData;
	private List<String> lore    = new ArrayList<String>();
	private int durability;
	private int level;
	private int rarity;
	private boolean enabled;
	private String pattern;
	private String permission;
	//attribute eklencek
	
	public Recipe(String name) {
		this.name = name;
		this.itemname = data.getString(name + ".name");
		this.material = Material.getMaterial(data.getString(name + ".material").toUpperCase());
		this.enabled = data.getBoolean(name + ".enabled");
		for(String req : data.singleLayerKeySet(name + ".reqs")) {
			Material reqmat      = Material.getMaterial(data.getString(name + ".reqs." + req + ".material").toUpperCase());
			String reqname       = data.getString(name + ".reqs." + req + ".name");
			int reqcount         = data.getInt(name + ".reqs." + req + ".count");
			List<String> reqlore = data.getStringList(name + ".reqs." + req + ".lore");
			if(reqmat != null) {
			ItemStack reqitem    = new ItemStack(reqmat);
			ItemMeta im          = reqitem.getItemMeta();
			if(reqname != null && !reqname.equalsIgnoreCase("")) {
				im.setDisplayName(c(reqname));
			}
			if(!reqlore.isEmpty() && reqlore != null) {
				im.setLore(c(reqlore));
			}
			reqitem.setItemMeta(im);
			reqitem.setAmount(reqcount);
			
			reqs.add(reqitem);
			}
		}
		this.customModelData = data.getInt(name + ".custommodeldata");
		this.lore            = data.getStringList(name + ".lore");
		this.durability      = data.getInt(name + ".durability");
		this.rarity          = data.getInt(name + ".rarity");
		this.level           = data.getInt(name + ".level");
		this.pattern		 = data.getString(name + ".patternType");
		this.permission      = data.getString(name + ".permission");
	}

	public String getCreator(){
	    return data.getString(name + ".creator");
    }

	public String getPermission(){
		return this.permission;
	}

	public String getPatternType(){
		return pattern;
	}

	public List<String> c(List<String> lore){
	    return lore.stream().map(this::c).collect(Collectors.toList());
	}
	
	public String c(final String c) {
		return ChatColor.translateAlternateColorCodes('&', c);
	}
	
	public double getAttAmount(String attribute) {
		return data.getDouble(name + ".attributes." + attribute + ".amount");
	}
	
	public String getAttOp(String attribute) {
		return data.getString(name + ".attributes." + attribute + ".operation");
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public String getName() {
		return name;
	}
	
	public String getItemName() {
		return itemname;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public List<ItemStack> getReqs(){
		return reqs;
	}
	
	public int getcustomModelData() {
		return customModelData;
	}
	
	public List<String> getLore(){
		return lore;
	}
	
	public int getDurability() {
		return durability;
	}
	
	public void addAttribute(ItemStack item, String attribute, String operation, double amount) {
		Operation op = null;
		switch(operation) {
			case "add":
				op = Operation.ADD_NUMBER;
				break;
			case "scalar":
				op = Operation.ADD_SCALAR;
				break;
		}
		Attribute atrb = null;
		switch(attribute) {
			case "armor":
				atrb = Attribute.GENERIC_ARMOR;
				break;
			case "armor_toughness":
				atrb = Attribute.GENERIC_ARMOR_TOUGHNESS;
				break;
			case "attack_damage":
				atrb = Attribute.GENERIC_ATTACK_DAMAGE;
				break;
			case "attack_knockback":
				atrb = Attribute.GENERIC_ATTACK_KNOCKBACK;
				break;
			case "attack_speed":
				atrb = Attribute.GENERIC_ATTACK_SPEED;
				break;
			case "flying_speed":
				atrb = Attribute.GENERIC_FLYING_SPEED;
				break;
			case "follow_range":
				atrb = Attribute.GENERIC_FOLLOW_RANGE;
				break;
			case "knockback_resistance":
				atrb = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
				break;
			case "luck":
				atrb = Attribute.GENERIC_LUCK;
				break;
			case "max_health":
				atrb = Attribute.GENERIC_MAX_HEALTH;
				break;
			case "movement_speed":
				atrb = Attribute.GENERIC_MOVEMENT_SPEED;
				break;
		}
		ItemMeta im = item.getItemMeta();
		if(item.getType().toString().toLowerCase().contains("sword") || item.getType().toString().toLowerCase().contains("hoe") || item.getType().toString().toLowerCase().contains("axe") || item.getType().toString().toLowerCase().contains("pickaxe") || item.getType().toString().toLowerCase().contains("shovel")) {
			im.addAttributeModifier(atrb, new AttributeModifier(UUID.randomUUID(), "generic." + attribute, amount, op, EquipmentSlot.HAND));
			im.addAttributeModifier(atrb, new AttributeModifier(UUID.randomUUID(), "generic." + attribute, amount, op, EquipmentSlot.OFF_HAND));
		}
		if(item.getType().toString().toLowerCase().contains("helmet")) {
			im.addAttributeModifier(atrb, new AttributeModifier(UUID.randomUUID(), "generic." + attribute, amount, op, EquipmentSlot.HEAD));
		}
		if(item.getType().toString().toLowerCase().contains("chestplate")) {
			im.addAttributeModifier(atrb, new AttributeModifier(UUID.randomUUID(), "generic." + attribute, amount, op, EquipmentSlot.CHEST));
		}
		if(item.getType().toString().toLowerCase().contains("leggings")) {
			im.addAttributeModifier(atrb, new AttributeModifier(UUID.randomUUID(), "generic." + attribute, amount, op, EquipmentSlot.LEGS));
		}
		if(item.getType().toString().toLowerCase().contains("boots")) {
			im.addAttributeModifier(atrb, new AttributeModifier(UUID.randomUUID(), "generic." + attribute, amount, op, EquipmentSlot.FEET));
		}
		item.setItemMeta(im);
	}
	
	public void setAttributes(ItemStack item) {
		if(getAttAmount("armor") != 0) {
			addAttribute(item, "armor", getAttOp("armor"), getAttAmount("armor"));
		}
		if(getAttAmount("armor_toughness") != 0) {
			addAttribute(item, "armor_toughness", getAttOp("armor_toughness"), getAttAmount("armor_toughness"));
		}
		if(getAttAmount("attack_damage") != 0) {
			addAttribute(item, "attack_damage", getAttOp("attack_damage"), getAttAmount("attack_damage"));
		}
		if(getAttAmount("attack_speed") != 0) {
			addAttribute(item, "attack_speed", getAttOp("attack_speed"), getAttAmount("attack_speed"));
		}
		if(getAttAmount("attack_knockback") != 0) {
			addAttribute(item, "attack_knockback", getAttOp("attack_knockback"), getAttAmount("attack_knockback"));
		}
		if(getAttAmount("flying_speed") != 0) {
			addAttribute(item, "flying_speed", getAttOp("flying_speed"), getAttAmount("flying_speed"));
		}
		if(getAttAmount("follow_range") != 0) {
			addAttribute(item, "follow_range", getAttOp("follow_range"), getAttAmount("follow_range"));
		}
		if(getAttAmount("knockback_resistance") != 0) {
			addAttribute(item, "knockback_resistance", getAttOp("knockback_resistance"), getAttAmount("knockback_resistance"));
		}
		if(getAttAmount("luck") != 0) {
			addAttribute(item, "luck", getAttOp("luck"), getAttAmount("luck"));
		}
		if(getAttAmount("max_health") != 0) {
			addAttribute(item, "max_health", getAttOp("max_health"), getAttAmount("max_health"));
		}
		if(getAttAmount("movement_speed") != 0) {
			addAttribute(item, "movement_speed", getAttOp("movement_speed"), getAttAmount("movement_speed"));
		}
	}
	
	public ItemStack getItem() {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		if(!itemname.equalsIgnoreCase("") && itemname != null) {
			im.setDisplayName(c(itemname));
		}
		if(!lore.isEmpty() && lore != null) {
			im.setLore(c(lore));
		}
		if(customModelData > 0) {
			im.setCustomModelData(customModelData);
		}
		if(durability > 0) {
			NBTEditor.set(item, durability, "_durability");
		}
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(new NamespacedKey(Forge.main, "recipe"), PersistentDataType.STRING, this.name);
		item.setItemMeta(im);
		setAttributes(item);
		return item;
	}
}
