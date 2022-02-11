package net.vantahub;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.vantahub.systems.level.LevelGUI;
import net.vantahub.systems.level.levelMethods;
import net.vantahub.systems.recipe.ItemUpdateListener;
import net.vantahub.systems.recipe.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.vantahub.systems.recipe.ChatEdit;
import net.vantahub.systems.recipe.ChatEditTabCompleter;
import net.vantahub.systems.guis.ForgeGUI;
import net.vantahub.systems.guis.ItemGUI;


public class Forge extends JavaPlugin implements Listener{

    public static Forge main;
	public static String prefix = ChatColor.DARK_RED + "[" + ChatColor.RED + "VantaBlack" + ChatColor.DARK_RED + "]";
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new ForgeGUI(), this);
        Bukkit.getPluginManager().registerEvents(new ItemGUI(), this);
		Bukkit.getPluginManager().registerEvents(new LevelGUI(), this);
		Bukkit.getPluginManager().registerEvents(new levelMethods(), this);
        Bukkit.getPluginManager().registerEvents(new ItemUpdateListener(), this);
		levelMethods.createTypes();
        this.getCommand("fr").setTabCompleter(new ChatEditTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    public List<String> c(List<String> lore){
	    return lore.stream().map(this::c).collect(Collectors.toList());
	}
	
	public String c(final String c) {
		return ChatColor.translateAlternateColorCodes('&', c);
	}
    
    public ItemStack getItemStackbyReq(String name, String req) {
		Material reqmat      = Material.getMaterial(ChatEdit.data.getString(name + ".reqs." + req + ".material").toUpperCase());
		String reqname       = ChatEdit.data.getString(name + ".reqs." + req + ".name");
		int reqcount         = ChatEdit.data.getInt(name + ".reqs." + req + ".count");
		List<String> reqlore = ChatEdit.data.getStringList(name + ".reqs." + req + ".lore");
		if(reqmat != null) {
			ItemStack reqitem    = new ItemStack(reqmat);
			ItemMeta im          = reqitem.getItemMeta();
			if(reqname != null && !reqname.equalsIgnoreCase("")) {
			im.setDisplayName(c(reqname));
			}
			if(!reqlore.isEmpty()) {
				im.setLore(c(reqlore));
			}
			reqitem.setItemMeta(im);
			reqitem.setAmount(reqcount);
			return reqitem;
		}
		return null;
    }
    
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandlbl, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(commandlbl.equalsIgnoreCase("levelgui")){
				LevelGUI.createLevelGui(player);
			}
			if(commandlbl.equalsIgnoreCase("fr") && sender.hasPermission("demirci.admin")) {
				if(args.length == 0 && !ChatEdit.selectedRecipe.isEmpty() && ChatEdit.selectedRecipe.containsKey(player)) {
					player.sendMessage(prefix + " " + ChatColor.GRAY + "Seçili olan tarif: " + ChatColor.DARK_GRAY + ChatEdit.getRecipe(player));
				}
				if(!args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("select") && !args[0].equalsIgnoreCase("create") && (!ChatEdit.selectedRecipe.containsKey(player) || ChatEdit.selectedRecipe.isEmpty())) {
					player.sendMessage(prefix + " " + ChatColor.RED + "Bir tarifi düzenlemek için öncelikle tarif seçmeniz gerekli. Tarif seçmek için: /fr select (tarif). Tariflerin listesini görmek için: /fr list");
					return false;
				}
				if(args[0].equalsIgnoreCase("list")) {
					if(ChatEdit.data.getData().isEmpty()) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Hiçbir tarif bulunmuyor. Oluşturmak için: /fr create (tarif)");
					}else {
						int i = 0;
						player.sendMessage(prefix + ChatColor.DARK_GRAY + " Tarifler:");
						for(String recipe : ChatEdit.data.getData().keySet()) {
							i++;
							TextComponent msg = new TextComponent(prefix + " " + ChatColor.DARK_GRAY + "" + i + "-) " + ChatColor.GRAY + recipe);
							msg.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/fr select " + recipe));
							msg.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Content[]{new Text(ChatColor.GRAY + "Bu tarifi seçmek için tıklayın.")}));
							player.spigot().sendMessage(msg);
						}
					}
				}
				if(args[0].equalsIgnoreCase("create")) {
					if(args.length != 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr create (tarifismi)");
						return false;
					}
					if(ChatEdit.data.getData().containsKey(args[1])) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Böyle bir tarif zaten bulunuyor. Seçmek için: /fr select (tarif)");
						return false;
					}
					player.sendMessage(prefix + " " + ChatColor.GRAY + "Başarıyla " + args[1] + " isimli tarifi oluşturdun.");
					ChatEdit.createRecipe(player, args[1]);
				}
				if(args[0].equalsIgnoreCase("select")) {
					if(args.length != 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr select (tarif)");
						return false;
					}
					if(!ChatEdit.data.getData().containsKey(args[1])) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Böyle bir tarif bulunmuyor. Oluşturmak için: /fr create (tarifismi)");
						return false;
					}
					player.sendMessage(prefix + " " + ChatColor.GRAY + args[1] + " isimli tarifi seçtin.");
					ChatEdit.selectRecipe(player, args[1]);
				}
				if(args[0].equalsIgnoreCase("enable")) {
					if(args.length == 1) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".enabled", true);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifi aktifleştirdin..");
					}
				}
				if(args[0].equalsIgnoreCase("disable")) {
					if(args.length == 1) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".enabled", false);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifi deaktifleştirdin..");
					}
				}
				if(args[0].equalsIgnoreCase("pattern")) {
					if(args.length > 3 || args.length < 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr pattern (set/remove) [pattern]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".patternType", args[2]);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin kalıbını " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".patternType", null);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin kalıbını sildin.");
					}
				}
                if(args[0].equalsIgnoreCase("permission")) {
                    if(args.length > 3 || args.length < 2) {
                        player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr permission (set/remove) [permission]");
                        return false;
                    }
                    if(args[1].equalsIgnoreCase("set")) {
                        ChatEdit.data.set(ChatEdit.getRecipe(player) + ".permission", args[2]);
                        player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin yetkisini " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " olarak değiştirdin.");
                    }else if(args[1].equalsIgnoreCase("remove")) {
                        ChatEdit.data.set(ChatEdit.getRecipe(player) + ".permission", null);
                        player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin yetkisini sildin.");
                    }
                }
				if(args[0].equalsIgnoreCase("custommodeldata")) {
					if(args.length > 3 || args.length < 2 || !isNumeric(args[2])) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr custommodeldata (set/remove) [sayı]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".custommodeldata", args[2]);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin custommodeldatasını " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".custommodeldata", null);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin custommodeldatasını sildin.");
					}
				}
				if(args[0].equalsIgnoreCase("material")) {
					if(args.length > 3 || args.length < 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr material (set/remove) [materyal]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".material", args[2]);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin materyalini " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".material", null);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin materyalini sildin.");
					}
				}
				if(args[0].equalsIgnoreCase("name")) {
					if(args.length < 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr name (set/remove) [isim]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						StringBuilder sb = new StringBuilder();
						for (int i = 2; i < args.length; i++){
							sb.append(args[i]).append(" ");
						}
						String allArgs = sb.toString().trim();
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".name", allArgs);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin ismini " + ChatColor.DARK_GRAY + allArgs + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".name", null);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin ismini sildin.");
					}
				}
				if(args[0].equalsIgnoreCase("lore")) {
					if(args.length < 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr lore (set/remove) [lore]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						StringBuilder sb = new StringBuilder();
						for (int i = 2; i < args.length; i++){
							sb.append(args[i]).append(" ");
						}
						String allArgs = sb.toString().trim();
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".lore", Arrays.asList(allArgs));
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin loreunu " + ChatColor.DARK_GRAY + allArgs + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".lore", null);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin loreunu sildin.");
					}
				}
				if(args[0].equalsIgnoreCase("durability")) {
					if(args.length > 3 || args.length < 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr durability (set/remove) [sayı]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".durability", Integer.parseInt(args[2]));
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin dayanıklılığını " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".durability", 0);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin dayanıklılığını sildin.");
					}
				}
				if(args[0].equalsIgnoreCase("level")) {
					if(args.length > 3 || args.length < 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr level (set/remove) [sayı]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".level", Integer.parseInt(args[2]));
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin seviye gereksinimini " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".level", 0);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin seviye gereksinimini sildin.");
					}
				}
				if(args[0].equalsIgnoreCase("rarity")) {
					if(args.length > 3 || args.length < 2) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr rarity (set/remove) [sayı]");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".rarity", Integer.parseInt(args[2]));
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin nadirliğini " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " olarak değiştirdin.");
					}else if(args[1].equalsIgnoreCase("remove")) {
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".rarity", 0);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " isimli tarifin nadirliğini sildin.");
					}
				}
				if(args[0].equalsIgnoreCase("requirements")){
					if(args.length == 1) {
						if(ChatEdit.data.get(ChatEdit.getRecipe(player) + ".reqs") == null) {
							player.sendMessage(prefix + " " + ChatColor.RED + "Seçili olan tarif için herhangi bir gereksinim bulunmuyor.");
							return false;
						}
						player.sendMessage(prefix + " " + ChatColor.DARK_GRAY + "Gereksinimler:");
						for(String s : ChatEdit.data.singleLayerKeySet(ChatEdit.getRecipe(player) + ".reqs")) {
							ItemStack req = getItemStackbyReq(ChatEdit.getRecipe(player), s);
							player.sendMessage(ChatColor.GRAY + "- " + s + ":");
							player.sendMessage(ChatColor.WHITE + "İsim: " + req.getItemMeta().getDisplayName());
							player.sendMessage(ChatColor.WHITE + "Material: " + req.getType());
							player.sendMessage(ChatColor.WHITE + "Lore: " + req.getItemMeta().getLore());
							player.sendMessage(ChatColor.WHITE + "Adet: " + req.getAmount());
							player.sendMessage(" ");
						}
					}
					if(args.length == 3) {
						if(args[1].equalsIgnoreCase("add")) {
							if(ChatEdit.data.singleLayerKeySet(ChatEdit.getRecipe(player) + ".reqs").contains(args[2])) {
								player.sendMessage(prefix + " " + ChatColor.RED + ChatEdit.getRecipe(player) + " adlı tarifin bu isimde bir gereksinimi zaten bulunuyor.");
								return false;
							}
							ChatEdit.data.set(ChatEdit.getRecipe(player) + ".reqs." + args[2] + ".name", null);
							ChatEdit.data.set(ChatEdit.getRecipe(player) + ".reqs." + args[2] + ".material", null);
							ChatEdit.data.set(ChatEdit.getRecipe(player) + ".reqs." + args[2] + ".lore", null);
							ChatEdit.data.set(ChatEdit.getRecipe(player) + ".reqs." + args[2] + ".count", null);
							player.sendMessage(prefix + " " + ChatColor.GRAY + "Başarıyla " + ChatEdit.getRecipe(player) + " tarifi için " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " isminde bir gereksinim oluşturdun.");
						}
						if(args[1].equalsIgnoreCase("remove")) {
							if(!ChatEdit.data.singleLayerKeySet(ChatEdit.getRecipe(player) + ".reqs").contains(args[2])) {
								player.sendMessage(prefix + " " + ChatColor.RED + ChatEdit.getRecipe(player) + " adlı tarifin bu isimde bir gereksinimi zaten bulunmuyor.");
								return false;
							}
							ChatEdit.data.remove(ChatEdit.getRecipe(player) + ".reqs." + args[2]);
							player.sendMessage(prefix + " " + ChatColor.GRAY + "Başarıyla " + ChatEdit.getRecipe(player) + " tarifi için " + ChatColor.DARK_GRAY + args[2] + ChatColor.GRAY + " ismindeki gereksinimi sildin.");
						}
					}
					if(args.length > 3) {
						if(args[1].equalsIgnoreCase("edit")) {
							if(!ChatEdit.data.singleLayerKeySet(ChatEdit.getRecipe(player) + ".reqs").contains(args[2])) {
								player.sendMessage(prefix + " " + ChatColor.RED + ChatEdit.getRecipe(player) + " adlı tarifin bu isimde bir gereksinimi zaten bulunmuyor.");
								return false;
							}
							String reqPath = ChatEdit.getRecipe(player) + ".reqs." + args[2];
							if(args[3].equalsIgnoreCase("name")) {
								StringBuilder sb = new StringBuilder();
								for (int i = 4; i < args.length; i++){
									sb.append(args[i]).append(" ");
								}
								String allArgs = sb.toString().trim();
								ChatEdit.data.set(reqPath + ".name", allArgs);
								player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " adlı tarifin " + args[2] + " gereksiniminin ismini " + args[4] + " olarak değiştirdin.");
							}
							if(args[3].equalsIgnoreCase("material")) {
								ChatEdit.data.set(reqPath + ".material", args[4]);
								player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " adlı tarifin " + args[2] + " gereksiniminin materyalini " + args[4] + " olarak değiştirdin.");
							}
							if(args[3].equalsIgnoreCase("lore")) {
								StringBuilder sb = new StringBuilder();
								for (int i = 4; i < args.length; i++){
									sb.append(args[i]).append(" ");
								}
								String allArgs = sb.toString().trim();
								ChatEdit.data.set(reqPath + ".lore", Arrays.asList(allArgs));
								player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " adlı tarifin " + args[2] + " gereksiniminin ismini " + allArgs + " olarak değiştirdin.");
							}
							if(args[3].equalsIgnoreCase("count")) {
								ChatEdit.data.set(reqPath + ".count", args[4]);
								player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " adlı tarifin " + args[2] + " gereksiniminin sayısını " + args[4] + " olarak değiştirdin.");
							}
							if(args[3].equalsIgnoreCase("hand")) {
								if(player.getInventory().getItemInMainHand().getType() == Material.AIR) {
									player.sendMessage(prefix + " " + ChatColor.RED + "Elinde bir eşya tutman gerekli.");
									return false;
								}
								ItemStack item = player.getInventory().getItemInMainHand();
								ChatEdit.data.set(reqPath + ".name", item.getItemMeta().getDisplayName());
								if(item.getItemMeta().getLore() != null) {
									ChatEdit.data.set(reqPath + ".lore", item.getItemMeta().getLore());
								}
								ChatEdit.data.set(reqPath + ".count", item.getAmount());
								ChatEdit.data.set(reqPath + ".material", item.getType().toString());
								player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " adlı tarifin " + args[2] + " gereksinimini elindeki eşya olarak değiştirdin.");
							}
						}
					}
				}
				if(args[0].equalsIgnoreCase("attributes")) {
					if(args.length != 5) {
						player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr attributes set (attribute ismi) (sayı) (operation)");
						return false;
					}
					if(args[1].equalsIgnoreCase("set")) {
						if(!Arrays.asList(ChatEdit.attributes).contains(args[2])) {
							player.sendMessage(prefix + " " + ChatColor.RED + args[2] + " geçerli bir attribute değil.");
							return false;
						}
						if(!isNumeric(args[3])) {
							player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr attributes set (attribute ismi) (sayı) (operation) /// Lütfen bir sayı girin.");
							return false;
						}
						if(!args[4].equalsIgnoreCase("scalar") && !args[4].equalsIgnoreCase("add")) {
							player.sendMessage(prefix + " " + ChatColor.RED + "Doğru kullanım: /fr attributes set (attribute ismi) (sayı) (operation) /// Operation geçersiz.");
							return false;
						}
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".attributes." + args[2] + ".amount", Double.parseDouble(args[3]));
						ChatEdit.data.set(ChatEdit.getRecipe(player) + ".attributes." + args[2] + ".operation", args[4]);
						player.sendMessage(prefix + " " + ChatColor.GRAY + ChatEdit.getRecipe(player) + " adlı tarifin " + args[2] + " attributeunu değiştirdin.");
					}
				}
				if(args[0].equalsIgnoreCase("idiosyncrasy")) {
					if(args.length != 1) {
						return false;
					}
					Recipe rec = new Recipe(ChatEdit.getRecipe(player));
					player.sendMessage(prefix + " " + ChatColor.DARK_GRAY + ChatEdit.getRecipe(player) + ChatColor.GRAY + " adlı tarifin özellikleri: ");
					player.sendMessage(c("&8Oluşturan: &7" + rec.getCreator()));
					player.sendMessage(c("&8Aktif: &7" + rec.isEnabled()));
					player.sendMessage(c("&8Kalıp: &7" + rec.getPatternType()));
					player.sendMessage(c("&8Materyal: &7" + rec.getMaterial()));
					player.sendMessage(c("&8İsim: &7" + rec.getName()));
					player.sendMessage(c("&8Yetki: &7" + rec.getPermission()));
					player.sendMessage(c("&8CustomModelData: &7" + rec.getcustomModelData()));
					player.sendMessage(c("&8Lore: &7" + rec.getLore()));
					player.sendMessage(c("&8Dayanıklılık: &7" + rec.getDurability()));
					player.sendMessage(c("&8Seviye: &7" + rec.getLevel()));
					player.sendMessage(c("&8Nitelikler: "));
					for(String attribute : ChatEdit.data.singleLayerKeySet(ChatEdit.getRecipe(player) + ".attributes")) {
						player.sendMessage(c("&f-&c" + attribute + "&8:"));
						player.sendMessage(c("&f-    &7Değer: &f") + rec.getAttAmount(attribute));
						player.sendMessage(c("&f-    &7Operasyon: &f") + rec.getAttOp(attribute));
					}
					player.sendMessage(ChatColor.DARK_GRAY + "Gereksinimler:");
					for(String s : ChatEdit.data.singleLayerKeySet(ChatEdit.getRecipe(player) + ".reqs")) {
						ItemStack req = getItemStackbyReq(ChatEdit.getRecipe(player), s);
						player.sendMessage(ChatColor.GRAY + "- " + s + ":");
						player.sendMessage(ChatColor.WHITE + "İsim: " + req.getItemMeta().getDisplayName());
						player.sendMessage(ChatColor.WHITE + "Material: " + req.getType());
						player.sendMessage(ChatColor.WHITE + "Lore: " + req.getItemMeta().getLore());
						player.sendMessage(ChatColor.WHITE + "Adet: " + req.getAmount());
						player.sendMessage(" ");
					}
				}
    		}
		}
		return false;
    }
}
