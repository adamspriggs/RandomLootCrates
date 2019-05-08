import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Factions;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import Crates.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static java.lang.Math.abs;

public class RandomChest implements Listener {

    private Crates crates;
    final private JavaPlugin plugin;

    public RandomChest(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateCrates(Crates crates) {
        this.crates = crates;
    }

    private void putWarzone(FLocation loc) {
        Board.getInstance().setFactionAt(Factions.getInstance().getWarZone(), loc);
    }

    private void putWilderness(FLocation loc) {
        Board.getInstance().setFactionAt(Factions.getInstance().getWilderness(), loc);
    }

    public Location spawnCrate(int x1, int z1, int x2, int z2, Location last, World w) {
        Crate c = rollCrate();
        Random r = new Random();
        int finalX = 0, finalZ = 0;
        FLocation fLoc = new FLocation();
        int y;
        Location loc = new Location(w, 0,0,0);
        int x = 0;

        while (true) {
            x++;
            if (x1 > x2) {
                int temp = abs(x1 - x2);
                finalX = x2 += r.nextInt(temp);
            } else {
                int temp = abs(x2 - x1);
                finalX = x1 += r.nextInt(temp);
            }
            if (z1 > z2) {
                int temp = abs(z1 - z2);
                finalZ = z2 += r.nextInt(temp);
            } else {
                int temp = abs(z2 - z1);
                finalZ = z1 += r.nextInt(temp);
            }
            y = w.getHighestBlockYAt(finalX, finalZ);
            loc = new Location(w, finalX, y, finalZ);
            fLoc = new FLocation(loc);
            if (Board.getInstance().getFactionAt(fLoc).isWilderness()) {
                if (loc.getBlockY() != 0) {
                    break;
                }
            }
            if (x > 10000) {
                return null;
            }
        }

        try {
            if (last.getBlock().hasMetadata("rlc.game")) {
                last.getBlock().setType(Material.AIR);
            }
        } catch (Exception e) {
            //do nothing
        }

        w.strikeLightningEffect(loc);
        putWarzone(fLoc);
//        if (Board.getInstance().getFactionAt(new FLocation(last)) == Factions.getInstance().getWarZone()){
//            putWilderness(new FLocation(last));
//        }
        plugin.getServer().getConsoleSender().sendMessage("3");
        loc.getBlock().setType(Material.ENDER_CHEST);
        plugin.getServer().getConsoleSender().sendMessage("c: " + c);
        loc.getBlock().setMetadata("rlc.game", new FixedMetadataValue(plugin, true));
        loc.getBlock().setMetadata("rlc.crate.type", new FixedMetadataValue(plugin, c.getName()));
        Bukkit.broadcastMessage(ChatColor.GOLD + "A " + getColour(c.getName()) + c.getName() + ChatColor.GOLD + " has spawned at X: " + finalX + " Z: " + finalZ + ". You have 10 minutes!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "Type /rlcwild to teleport within 500 blocks of the crate!");
        return loc;
    }

    public ChatColor getColour(String crateName) {
        if (crateName.equalsIgnoreCase("common")) {
            return ChatColor.GREEN;
        } else if (crateName.equalsIgnoreCase("rare")) {
            return ChatColor.BLUE;
        } else if (crateName.equalsIgnoreCase("epic")) {
            return ChatColor.DARK_PURPLE;
        } else if (crateName.equalsIgnoreCase("legendary")) {
            return ChatColor.RED;
        } else if (crateName.equalsIgnoreCase("ancient")) {
            return ChatColor.MAGIC;
        }
        return ChatColor.WHITE;
    }

    public void spawnMobs(String crateName, Location loc, World w) {
        if (crateName.equalsIgnoreCase("common")) {
            for (int i = 0; i < 3; i++) {
                Blaze b = (Blaze) w.spawnEntity(loc, EntityType.BLAZE);
                b.setCustomName("Flame Bandit");
                b.setCustomNameVisible(true);
                b.setMaxHealth(100);
                b.setHealth(100);

                Zombie z = (Zombie) w.spawnEntity(loc, EntityType.ZOMBIE);
                EntityEquipment zE = z.getEquipment();
                zE.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                zE.setHelmetDropChance(0);
                zE.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                zE.setChestplateDropChance(0);
                zE.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                zE.setLeggingsDropChance(0);
                zE.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                zE.setBootsDropChance(0);
                z.setCustomName("David the Explorer Jr.");
                z.setCustomNameVisible(true);
                z.setMaxHealth(100);
                z.setHealth(100);
                zE.setItemInHand(new ItemStack(Material.IRON_SWORD));
                zE.setItemInHandDropChance(0);

                Creeper c = (Creeper) w.spawnEntity(loc, EntityType.CREEPER);
                c.setCustomName("BOOM");
                c.setCustomNameVisible(true);
            }
        } else if (crateName.equalsIgnoreCase("rare")) {
            for (int i = 0; i < 8; i++) {
                Blaze b = (Blaze) w.spawnEntity(loc, EntityType.BLAZE);
                b.setCustomName("Flame Bandit");
                b.setCustomNameVisible(true);
                b.setMaxHealth(100);
                b.setHealth(100);

                Zombie z = (Zombie) w.spawnEntity(loc, EntityType.ZOMBIE);
                EntityEquipment zE = z.getEquipment();
                zE.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                zE.setHelmetDropChance(0);
                zE.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                zE.setChestplateDropChance(0);
                zE.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                zE.setLeggingsDropChance(0);
                zE.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                zE.setBootsDropChance(0);
                z.setCustomName("David the Explorer Jr.");
                z.setCustomNameVisible(true);
                z.setMaxHealth(150);
                z.setHealth(150);
                z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 2));
                zE.setItemInHand(new ItemStack(Material.GOLD_SWORD));
                zE.setItemInHandDropChance(0);

                Creeper c = (Creeper) w.spawnEntity(loc, EntityType.CREEPER);
                c.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 2));
                c.setCustomName("BOOM");
                c.setCustomNameVisible(true);
            }
        } else if (crateName.equalsIgnoreCase("epic")) {
            for (int i = 0; i < 2; i++) {
                Skeleton s = (Skeleton) w.spawnEntity(loc, EntityType.SKELETON);
                EntityEquipment sE = s.getEquipment();
                sE.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                sE.setHelmetDropChance(0);
                sE.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                sE.setChestplateDropChance(0);
                sE.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                sE.setLeggingsDropChance(0);
                sE.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                sE.setBootsDropChance(0);
                ItemStack bow = new ItemStack(Material.BOW);
                bow.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
                bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
                sE.setItemInHand(bow);
                s.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 18000, 2));
                s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 18000, 0));
                s.setCustomName("Skeletor the Buried");
                s.setCustomNameVisible(true);
                s.setMaxHealth(300);
                s.setHealth(300);
            }
            for (int i = 0; i < 10; i++) {
                Creeper c = (Creeper) w.spawnEntity(loc, EntityType.CREEPER);
                c.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 2));
                c.setCustomName("BOOM");
                c.setCustomNameVisible(true);
            }
        } else if (crateName.equalsIgnoreCase("legendary")) {
            Skeleton s = (Skeleton) w.spawnEntity(loc, EntityType.SKELETON);
            EntityEquipment sE = s.getEquipment();
            sE.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            sE.setHelmetDropChance(0);
            sE.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            sE.setChestplateDropChance(0);
            sE.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
            sE.setLeggingsDropChance(0);
            sE.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
            sE.setBootsDropChance(0);
            ItemStack bow = new ItemStack(Material.BOW);
            bow.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
            bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
            sE.setItemInHand(bow);
            s.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 18000, 2));
            s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 18000, 0));
            s.setCustomName("Skeletor the Buried");
            s.setCustomNameVisible(true);
            s.setMaxHealth(600);
            s.setHealth(600);

            for (int i = 0; i < 4; i++) {
                Blaze b = (Blaze) w.spawnEntity(loc, EntityType.BLAZE);
                b.setCustomName("Flame Bandit");
                b.setCustomNameVisible(true);
                b.setMaxHealth(100);
                b.setHealth(100);

                Zombie z = (Zombie) w.spawnEntity(loc, EntityType.ZOMBIE);
                EntityEquipment zE = z.getEquipment();
                zE.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                zE.setHelmetDropChance(0);
                zE.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                zE.setChestplateDropChance(0);
                zE.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                zE.setLeggingsDropChance(0);
                zE.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                zE.setBootsDropChance(0);
                z.setCustomName("David the Explorer Jr.");
                z.setCustomNameVisible(true);
                z.setMaxHealth(100);
                z.setHealth(100);
                zE.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
                zE.setItemInHandDropChance(0);

                Creeper c = (Creeper) w.spawnEntity(loc, EntityType.CREEPER);
                c.setCustomName("BOOM");
                c.setCustomNameVisible(true);
                c.setMaxHealth(40);
                c.setHealth(40);
            }
        } else if (crateName.equalsIgnoreCase("ancient")) {

            //David the Explorer, BOSS, 2x 750 HP
            for (int i = 0; i < 2; i++) {
                Zombie z = (Zombie) w.spawnEntity(loc, EntityType.ZOMBIE);
                EntityEquipment zE = z.getEquipment();
                ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                zE.setHelmet(helmet);
                zE.setHelmetDropChance(0);
                ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
                chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                zE.setChestplate(chest);
                zE.setChestplateDropChance(0);
                ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
                legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                zE.setLeggings(legs);
                zE.setLeggingsDropChance(0);
                ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                zE.setBoots(boots);
                zE.setBootsDropChance(0);
                ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                sword.addEnchantment(Enchantment.DAMAGE_ALL, 4);
                sword.addEnchantment(Enchantment.FIRE_ASPECT,1);
                zE.setItemInHand(sword);
                zE.setItemInHandDropChance(0);
                z.setCustomName("David the Explorer");
                z.setCustomNameVisible(true);
                z.setMaxHealth(750);
                z.setHealth(750);
                z.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 18000, 1));
                z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 2));
            }

            for (int i = 0; i < 6; i++) {
                Blaze b = (Blaze) w.spawnEntity(loc, EntityType.BLAZE);
                b.setCustomName("Flame Bandit");
                b.setCustomNameVisible(true);
                b.setMaxHealth(100);
                b.setHealth(100);

                Zombie z = (Zombie) w.spawnEntity(loc, EntityType.ZOMBIE);
                EntityEquipment zE = z.getEquipment();
                zE.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                zE.setHelmetDropChance(0);
                zE.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                zE.setChestplateDropChance(0);
                zE.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                zE.setLeggingsDropChance(0);
                zE.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                zE.setBootsDropChance(0);
                zE.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
                zE.setItemInHandDropChance(0);
                z.setCustomName("David the Explorer Jr.");
                z.setCustomNameVisible(true);
                z.setMaxHealth(100);
                z.setHealth(100);

                Creeper c = (Creeper) w.spawnEntity(loc, EntityType.CREEPER);
                c.setCustomName("BOOM");
                c.setCustomNameVisible(true);
                c.setMaxHealth(100);
                c.setHealth(100);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        try {
            Entity e = event.getDamager();
            if (e.getCustomName().equalsIgnoreCase("David the Explorer")) {
                event.setDamage(event.getDamage() * 4);
            } else if (e.getCustomName().equalsIgnoreCase("Skeletor the Buried")) {
                event.setDamage(event.getDamage() * 2);
            } else if (e.getCustomName().equalsIgnoreCase("BOOM")) {
                event.setDamage(event.getDamage() * 5);
            }
        } catch (Exception e) {
            //say nothing
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("rlc.game") && event.getBlock().getType().equals(Material.ENDER_CHEST)) {
            if (!event.getPlayer().hasPermission("MinecraftPlugin.spawn")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GOLD + "Right click this block to get your reward!");
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Random r = new Random();
        try {
            if (event.getClickedBlock().getType() != Material.AIR && event.getClickedBlock() != null) {
                if (event.getClickedBlock().hasMetadata("rlc.game")) {

                    putWilderness(new FLocation(event.getClickedBlock().getLocation()));
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.EXPLODE, 1, 0);
                    event.getClickedBlock().setType(Material.AIR);
                    Inventory inv = event.getPlayer().getInventory();
                    List<MetadataValue> metaList = event.getClickedBlock().getMetadata("rlc.crate.type");
                    String crateName = metaList.get(0).asString();
                    List<?> items = plugin.getConfig().getList(crateName);

                    spawnMobs(crateName, event.getClickedBlock().getLocation(), event.getPlayer().getWorld());

                    for (int i = 0; i < items.size()-1; i++) {
                        if ((ItemStack) items.get(i) == null || (ItemStack) items.get(i) == (new ItemStack(Material.DIRT))) {
                            continue;
                        }
                        int a = r.nextInt(64)+1;
                        if (a <= ((ItemStack) items.get(i+1)).getAmount()){
                            inv.addItem((ItemStack) items.get(i));
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public Color getColor(String paramString) {
        String temp = paramString;
        if (temp.equalsIgnoreCase("AQUA")) return Color.AQUA;
        if (temp.equalsIgnoreCase("BLACK")) return Color.BLACK;
        if (temp.equalsIgnoreCase("BLUE")) return Color.BLUE;
        if (temp.equalsIgnoreCase("FUCHSIA")) return Color.FUCHSIA;
        if (temp.equalsIgnoreCase("GRAY")) return Color.GRAY;
        if (temp.equalsIgnoreCase("GREEN")) return Color.GREEN;
        if (temp.equalsIgnoreCase("LIME")) return Color.LIME;
        if (temp.equalsIgnoreCase("MAROON")) return Color.MAROON;
        if (temp.equalsIgnoreCase("NAVY")) return Color.NAVY;
        if (temp.equalsIgnoreCase("OLIVE")) return Color.OLIVE;
        if (temp.equalsIgnoreCase("ORANGE")) return Color.ORANGE;
        if (temp.equalsIgnoreCase("PURPLE")) return Color.PURPLE;
        if (temp.equalsIgnoreCase("RED")) return Color.RED;
        if (temp.equalsIgnoreCase("SILVER")) return Color.SILVER;
        if (temp.equalsIgnoreCase("TEAL")) return Color.TEAL;
        if (temp.equalsIgnoreCase("WHITE")) return Color.WHITE;
        if (temp.equalsIgnoreCase("YELLOW")) return Color.YELLOW;
        return null;
    }

    public Crate rollCrate() {
        Random r = new Random();
        int roll = r.nextInt(100) + 1;
        Crate highestCrate = null;
        plugin.getServer().getConsoleSender().sendMessage("crates: " + crates.getCratesList());
        for (Crate c : crates.getCratesList()) {
            if (c.getRarity() <= roll) {
                if (highestCrate != null) {
                    if (c.getRarity() >= highestCrate.getRarity()) {
                        highestCrate = c;
                    }
                } else {
                    highestCrate = c;
                }
            }
        }
        plugin.getServer().getConsoleSender().sendMessage("Chest: " + highestCrate);
        return highestCrate;
    }

}
