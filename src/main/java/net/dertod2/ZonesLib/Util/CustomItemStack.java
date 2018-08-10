package net.dertod2.ZonesLib.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Represents an all including {@link ItemStack}. Can converted to json an back to {@link CustomItemStack} or ItemStacks<br />
 * 
 * @author DerTod2
 *
 */
public class CustomItemStack {
	private int slot;
	
	private int typeId;
	private int amount;
	private byte data;
	private short durability;
	
	private ItemMeta itemMeta;
	
	/**
	 * Create an new {@link CustomItemStack} based on an {@link ItemStack}
	 * @param itemStack
	 */
	public CustomItemStack(ItemStack itemStack) {
		this(itemStack, -1);
	}
	
	/**
	 * Create an new {@link CustomItemStack} based on an {@link ItemStack}
	 * @param itemStack
	 */
	@SuppressWarnings("deprecation")
	public CustomItemStack(ItemStack itemStack, int slot) {
		this.slot = slot;
		
		if (itemStack == null || itemStack.getTypeId() == 0) {
			// Hotfix - Bukkit can't handle ItemStack's with AIR o.O
			this.typeId = 0;
			this.amount = 1;
			this.data = 0;
			this.durability = 1;
			
			this.itemMeta = Bukkit.getItemFactory().getItemMeta(Material.AIR);
		} else {
			this.typeId = itemStack.getTypeId();
			this.amount = itemStack.getAmount();
			this.data = itemStack.getData().getData();
			this.durability = itemStack.getDurability();
			
			this.itemMeta = itemStack.getItemMeta();
		}		
	}
	
	public CustomItemStack() { }
	
	/**
	 * Converts an {@link ItemStack} to an Json String for Database Saving
	 * @param itemStack
	 * @return String with Json Content
	 */
	public static JsonObject toJson(ItemStack itemStack) {
		CustomItemStack customItemStack = new CustomItemStack(itemStack);
		return customItemStack.toJson();
	}
	
	public static JsonObject toJson(ItemStack[] inventory) {
		JsonObject parentObject = new JsonObject();
		
		parentObject.addProperty("InventorySize", inventory.length);
		
		JsonArray jsonArray = new JsonArray();	
		for (int slot = 0; slot < inventory.length; slot++) {
			ItemStack itemStack = inventory[slot];
			
			CustomItemStack modItemStack = new CustomItemStack(itemStack, slot);
			if (modItemStack.typeId != 0) jsonArray.add(modItemStack.toJson());
		}

		parentObject.add("Inventory", jsonArray);
		
		return parentObject;
	}
	
	/**
	 * Creates an {@link CustomItemStack} out of an Json String<br />
	 * The crated ModItemStack includes all Item specified Data.
	 * @param String with the Json Content
	 * @return ModItemStack
	 */
	public static CustomItemStack toItemStack(String json) {
		try {
			JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
			
			CustomItemStack customItemStack = new CustomItemStack();
			customItemStack.deserialize(jsonObject);
			
			return customItemStack;
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	} 
	
	public static ItemStack[] toInventory(String json) {
		try {
			JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
			ItemStack[] inventory = new ItemStack[jsonObject.get("InventorySize").getAsInt()];

			for (int i = 0; i < inventory.length; i++) { inventory[i] = new ItemStack(Material.AIR); }
			
			JsonArray jsonArray = jsonObject.get("Inventory").getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				CustomItemStack customItemStack = CustomItemStack.toItemStack(jsonArray.get(i).getAsJsonObject());
				inventory[customItemStack.slot] = customItemStack.toItemStack();
			}
			
			return inventory;
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	} 
	
	/**
	 * Creates an {@link CustomItemStack} out of an Json String<br />
	 * The created CustomItemStack includes all Item specified Data.
	 * @param String with the Json Content
	 * @return ModItemStack
	 */
	public static CustomItemStack toItemStack(JsonObject jsonObject) {
		try {
			CustomItemStack customItemStack = new CustomItemStack();
			customItemStack.deserialize(jsonObject);
			
			return customItemStack;
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}  
	
	/**
	 * Converts this {@link CustomItemStack} to an {@link ItemStack}
	 * @return ItemStack
	 */
	@SuppressWarnings("deprecation")
	public ItemStack toItemStack() {
		ItemStack itemStack = new ItemStack(this.typeId, this.amount, this.durability);
		itemStack.getData().setData(this.data);
		itemStack.setItemMeta(this.itemMeta);
		
		return itemStack;
	}
	
	/**
	 * Converts this {@link ModItemStack} to an Json String for Database Saving
	 * @return String with Json Content
	 */
	public JsonObject toJson() {
		try {
			return this.serialize();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	private JsonObject serialize() {
		Gson gson = new Gson();
		
		JsonObject parentObject = new JsonObject();
		
		parentObject.addProperty("slot", this.slot);
		
		parentObject.addProperty("type-id", this.typeId);
		parentObject.addProperty("amount", this.amount);
		parentObject.addProperty("data", this.data);
		parentObject.addProperty("durability", this.durability);

		if (this.typeId == 0) return parentObject;
		
		parentObject.addProperty("display-name", this.itemMeta.getDisplayName());
		parentObject.add("lore", gson.toJsonTree(this.itemMeta.getLore(), new TypeToken<List<String>>() {}.getType()));
		
		// Enchantments
		Map<String, Integer> enchantmentList = new LinkedHashMap<String, Integer>();
		for (Enchantment enchantment :  this.itemMeta.getEnchants().keySet()) {
			enchantmentList.put(enchantment.getName(), this.itemMeta.getEnchants().get(enchantment));
		}
		
		parentObject.add("enchantments", gson.toJsonTree(enchantmentList, new TypeToken<LinkedHashMap<String, Integer>>() {}.getType()));
		
		// Meta Data
		if (itemMeta instanceof BannerMeta) {
			BannerMeta bannerMeta = (BannerMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "banner");
			parentObject.addProperty("banner-color", bannerMeta.getBaseColor().name());

			Map<String, String> serializedPatterns = new LinkedHashMap<String, String>();
			for (Pattern pattern : bannerMeta.getPatterns()) {
				serializedPatterns.put(pattern.getPattern().name(), pattern.getColor().name());
			}
			
			parentObject.add("banner-patterns", gson.toJsonTree(serializedPatterns, new TypeToken<LinkedHashMap<String, String>>() {}.getType()));
		} else if (itemMeta instanceof BookMeta) {
			BookMeta bookMeta = (BookMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "book");
			parentObject.addProperty("book-author", bookMeta.getAuthor());
			parentObject.addProperty("book-title", bookMeta.getTitle());
			parentObject.add("book-pages", gson.toJsonTree(bookMeta.getPages(), new TypeToken<List<String>>() {}.getType()));
		} else if (itemMeta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "enchantmentstorage");
			
			Map<String, Integer> serializedStoredEnchantment = new LinkedHashMap<String, Integer>();
			for (Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
				serializedStoredEnchantment.put(enchantment.getName(), enchantmentStorageMeta.getStoredEnchants().get(enchantment));
			}
			
			parentObject.add("enchantmentstorage-stored", gson.toJsonTree(serializedStoredEnchantment, new TypeToken<LinkedHashMap<String, Integer>>() {}.getType()));
		} else if (itemMeta instanceof FireworkEffectMeta) {
			FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "fireworkeffect");
			parentObject.addProperty("fireworkeffect-haseffect", fireworkEffectMeta.hasEffect());
			
			List<Integer> serializedColors = new ArrayList<Integer>();
			for (Color color : fireworkEffectMeta.getEffect().getColors()) serializedColors.add(color.asRGB());
			parentObject.add("fireworkeffect-colors", gson.toJsonTree(serializedColors, new TypeToken<List<Integer>>() {}.getType()));
			
			List<Integer> serializedFadeColors = new ArrayList<Integer>();
			for (Color color : fireworkEffectMeta.getEffect().getFadeColors()) serializedFadeColors.add(color.asRGB());
			parentObject.add("fireworkeffect-fadecolors", gson.toJsonTree(serializedFadeColors, new TypeToken<List<Integer>>() {}.getType()));
			
			parentObject.addProperty("fireworkeffect-type", fireworkEffectMeta.getEffect().getType().name());
			parentObject.addProperty("fireworkeffect-flicker", fireworkEffectMeta.getEffect().hasFlicker());
			parentObject.addProperty("fireworkeffect-trail", fireworkEffectMeta.getEffect().hasTrail());
		} else if (itemMeta instanceof FireworkMeta) {
			FireworkMeta fireworkMeta = (FireworkMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "firework");
			parentObject.addProperty("firework-power", fireworkMeta.getPower());
			
			List<JsonObject> fireworkEffects = new ArrayList<JsonObject>();
			for (FireworkEffect fireworkEffect : fireworkMeta.getEffects()) {
				JsonObject jsonObject = new JsonObject();
				
				List<Integer> serializedColors = new ArrayList<Integer>();
				for (Color color : fireworkEffect.getColors()) serializedColors.add(color.asRGB());
				jsonObject.add("colors", gson.toJsonTree(serializedColors, new TypeToken<List<Integer>>() {}.getType()));
				
				List<Integer> serializedFadeColors = new ArrayList<Integer>();
				for (Color color : fireworkEffect.getFadeColors()) serializedFadeColors.add(color.asRGB());
				jsonObject.add("fadecolors", gson.toJsonTree(serializedFadeColors, new TypeToken<List<Integer>>() {}.getType()));
				
				jsonObject.addProperty("type", fireworkEffect.getType().name());
				jsonObject.addProperty("flicker", fireworkEffect.hasFlicker());
				jsonObject.addProperty("trail", fireworkEffect.hasTrail());
				
				fireworkEffects.add(jsonObject);
			}
			
			parentObject.add("firework-effects", gson.toJsonTree(fireworkEffects, new TypeToken<List<JsonObject>>() {}.getType()));
		} else if (itemMeta instanceof LeatherArmorMeta) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "leather-armor");
			parentObject.addProperty("leather-armor-color", leatherArmorMeta.getColor().asRGB());
		} else if (itemMeta instanceof MapMeta) {
			MapMeta mapMeta = (MapMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "map");
			parentObject.addProperty("map-scaling", mapMeta.isScaling());
		} else if (itemMeta instanceof PotionMeta) {
			PotionMeta potionMeta = (PotionMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "potion");
			
			List<JsonObject> potionEffects = new ArrayList<JsonObject>();
			for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
				JsonObject jsonObject = new JsonObject();
				
				jsonObject.addProperty("type", potionEffect.getType().getName());
				jsonObject.addProperty("amplifier", potionEffect.getAmplifier());
				jsonObject.addProperty("duration", potionEffect.getDuration());
				jsonObject.addProperty("ambient", potionEffect.isAmbient());
				
				potionEffects.add(jsonObject);
			}
			
			parentObject.add("potion-effects", gson.toJsonTree(potionEffects, new TypeToken<List<JsonObject>>() {}.getType()));
		} else if (itemMeta instanceof SkullMeta) {
			SkullMeta skullMeta = (SkullMeta) itemMeta;
			
			parentObject.addProperty("meta-type", "skull");
			parentObject.addProperty("skull-owner", skullMeta.getOwner());
		} else {
			//Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ItemMeta Type " + ChatColor.GOLD + itemMeta.getClass().getName() + ChatColor.RED + " not handled by CustomItemStack.");
			parentObject.addProperty("meta-type", "unknown");
		}

		return parentObject;
	}

	
	@SuppressWarnings("deprecation")
	private void deserialize(JsonObject parentObject) {	
		this.slot = parentObject.get("slot").getAsInt(); 
		
		this.typeId = parentObject.get("type-id").getAsInt();
		this.amount = parentObject.get("amount").getAsInt();
		this.data = parentObject.get("data").getAsByte();
		this.durability = parentObject.get("durability").getAsShort();
		
		this.itemMeta = Bukkit.getItemFactory().getItemMeta(Material.getMaterial(this.typeId));
		if (this.typeId == 0) return;
		
		if (!parentObject.get("display-name").isJsonNull()) this.itemMeta.setDisplayName(parentObject.get("display-name").getAsString());
		if (!parentObject.get("lore").isJsonNull()) {
			List<String> loreList = new ArrayList<String>();
			JsonArray jsonArray = parentObject.get("lore").getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) loreList.add(jsonArray.get(i).getAsString());
			this.itemMeta.setLore(loreList);
		}
		
		// Enchantments
		JsonObject enchantmentArray = parentObject.get("enchantments").getAsJsonObject();
		for (Entry<String, JsonElement> jsonElement : enchantmentArray.entrySet()) {
			this.itemMeta.addEnchant(Enchantment.getByName(jsonElement.getKey()), jsonElement.getValue().getAsInt(), true);
		}
		
		// Item Meta
		String metaType = parentObject.get("meta-type").getAsString();
		if (metaType == null || metaType.equals("none")) return;

		if (metaType.equals("banner")) {
			BannerMeta bannerMeta = (BannerMeta) this.itemMeta;
			
			bannerMeta.setBaseColor(DyeColor.valueOf(parentObject.get("banner-color").getAsString()));
			
			JsonObject patternArray = parentObject.get("banner-patterns").getAsJsonObject();
			for (Entry<String, JsonElement> jsonElement : patternArray.entrySet()) {
				bannerMeta.addPattern(new Pattern(DyeColor.valueOf(jsonElement.getValue().getAsString()), PatternType.valueOf(jsonElement.getKey())));
			}
		} else if (metaType.equals("book")) {
			BookMeta bookMeta = (BookMeta) this.itemMeta;
			
			bookMeta.setAuthor(parentObject.get("book-author").getAsString());
			bookMeta.setTitle(parentObject.get("book-title").getAsString());
			
			List<String> pageList = new ArrayList<String>();
			JsonArray jsonArray = parentObject.get("book-pages").getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) pageList.add(jsonArray.get(i).getAsString());
			bookMeta.setPages(pageList);
		} else if (metaType.equals("enchantmentstorage")) {
			EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) this.itemMeta;
			
			JsonObject enchantmentStorageArray = parentObject.get("enchantmentstorage-stored").getAsJsonObject();
			for (Entry<String, JsonElement> jsonElement : enchantmentStorageArray.entrySet()) {
				enchantmentStorageMeta.addEnchant(Enchantment.getByName(jsonElement.getKey()), jsonElement.getValue().getAsInt(), true);
			}
		} else if (metaType.equals("fireworkeffect")) {
			FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) this.itemMeta;

			Builder builder = FireworkEffect.builder();
			if (parentObject.get("fireworkeffect-haseffect").getAsBoolean()) {
				
				JsonArray serializedColors = parentObject.get("fireworkeffect-colors").getAsJsonArray();
				for (int i = 0; i < serializedColors.size(); i++) builder.withColor(Color.fromRGB(serializedColors.get(i).getAsInt()));
				
				JsonArray serializedFadeColors = parentObject.get("fireworkeffect-fadecolors").getAsJsonArray();
				for (int i = 0; i < serializedFadeColors.size(); i++) builder.withFade(Color.fromRGB(serializedFadeColors.get(i).getAsInt()));
				
				builder.with(FireworkEffect.Type.valueOf(parentObject.get("fireworkeffect-type").getAsString()));
				builder.flicker(parentObject.get("fireworkeffect-flicker").getAsBoolean());
				builder.trail(parentObject.get("fireworkeffect-trail").getAsBoolean());
			}

			fireworkEffectMeta.setEffect(builder.build());
		} else if (metaType.equals("firework")) {
			FireworkMeta fireworkMeta = (FireworkMeta) this.itemMeta;

			fireworkMeta.setPower(parentObject.get("firework-power").getAsInt());

			JsonArray fireworkEffects = parentObject.get("firework-effects").getAsJsonArray();
			for (int i = 0; i < fireworkEffects.size(); i++) {
				Builder builder = FireworkEffect.builder();
				
				JsonObject jsonObject = fireworkEffects.get(i).getAsJsonObject();
				
				JsonArray colorsArray = jsonObject.get("colors").getAsJsonArray();
				for (int j = 0; j < colorsArray.size(); j++) builder.withColor(Color.fromRGB(colorsArray.get(j).getAsInt()));
				
				JsonArray fadeColorsArray = jsonObject.get("fadecolors").getAsJsonArray();
				for (int j = 0; j < fadeColorsArray.size(); j++) builder.withFade(Color.fromRGB(fadeColorsArray.get(j).getAsInt()));
				
				builder.with(FireworkEffect.Type.valueOf(jsonObject.get("type").getAsString()));
				builder.flicker(jsonObject.get("flicker").getAsBoolean());
				builder.trail(jsonObject.get("trail").getAsBoolean());
				
				fireworkMeta.addEffect(builder.build());
			}	
		} else if (metaType.equals("leather-armor")) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemMeta;
			
			leatherArmorMeta.setColor(Color.fromRGB(parentObject.get("leather-armor-color").getAsInt()));
		} else if (metaType.equals("map")) {
			MapMeta mapMeta = (MapMeta) this.itemMeta;
			
			mapMeta.setScaling(parentObject.get("map-scaling").getAsBoolean());
		} else if (metaType.equals("potion")) {
			PotionMeta potionMeta = (PotionMeta) this.itemMeta;
			
			JsonArray potionEffects =  parentObject.get("potion-effects").getAsJsonArray();
			for (int i = 0; i < potionEffects.size(); i++) {
				JsonObject jsonObject = potionEffects.get(i).getAsJsonObject();
				
				potionMeta.addCustomEffect(new PotionEffect(
						PotionEffectType.getByName(jsonObject.get("type").getAsString()),
						jsonObject.get("duration").getAsInt(), 
						jsonObject.get("amplifier").getAsInt(), 
						jsonObject.get("ambient").getAsBoolean()), 
						true);
			}
		} else if (metaType.equals("skull")) {
			SkullMeta skullMeta = (SkullMeta) this.itemMeta;
			
			if (!parentObject.get("skull-owner").isJsonNull()) skullMeta.setDisplayName(parentObject.get("skull-owner").getAsString());
			skullMeta.setOwner(parentObject.get("skull-owner").getAsString());
		}
	}
	
	public String toString() {
		return "CustomItemStack {" + this.typeId + ", " + this.amount + ", " + this.data + ", " + this.durability + "}";
	}
}