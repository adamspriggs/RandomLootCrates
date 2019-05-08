import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import Crates.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomLootCrates extends JavaPlugin {

    private Location last;              // last spawned location
    private World world;
    private int time;                   // timer for each crate to spawn
    private int x1, x2, z1, z2;         // corners of the defined region
    Crates crates = new Crates();
    RandomChest rChest = new RandomChest(this);

    public void onEnable() {
        saveDefaultConfig();
        world = getServer().getWorld(getConfig().getString("world"));
        time = getConfig().getInt("timer");
        x1 = getConfig().getInt("corner_1_x");
        z1 = getConfig().getInt("corner_1_z");
        x2 = getConfig().getInt("corner_2_x");
        z2 = getConfig().getInt("corner_2_z");
        if (last == null) {
            last = new Location(world, 0, 0, 0);
        }

        crateSetup();

        getServer().getPluginManager().registerEvents(new RandomChest(this), this);
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Random Loot Crates successfully loaded.");
    }

    public void onDisable() {
        try {
            if (last.getBlock().hasMetadata("rlc.game")) {
                last.getBlock().setType(Material.AIR);
                last.getBlock().removeMetadata("rlc.game", this);
            }
        } catch (Exception e) {
            //do nothing
        }

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "Plugin disabled.");
        saveConfig();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rlc")) { // /rlc

            //
            //      Force spawn a crate
            //      /rlc spawn
            //
            if (args[0].equalsIgnoreCase("spawn")) {
                spawnCrate();
                return true;
            }

            //
            //      Start the timer
            //      /rlc start
            //
            if (args[0].equalsIgnoreCase("start")) {
                sender.sendMessage(ChatColor.GOLD + "A random crate will spawn every " + time/60 + " minutes.");
                run();
                return true;
            }

            //
            //      Stop the timer
            //      /rlc stop
            //
            if (args[0].equalsIgnoreCase("stop")) {
                sender.sendMessage(ChatColor.GOLD + "Timer stopped.");
                stop();
                return true;
            }

            //
            //      List crates
            //      /rlc list
            //
            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(listCrates());
                return true;
            }

            //
            //      Set Chest
            //      /rlc setchest <chest name>
            //      While looking at a chest, use this command to set the items
            //
            if (args[0].equalsIgnoreCase("setchest")) { // /rlc setchest-0 <chest>-1
                Player player = getServer().getPlayer(sender.getName());
                Block b = player.getTargetBlock((Set)null, 10);
                BlockState state = b.getState();
                if (state instanceof Chest) {
                    Chest c = (Chest) state;
                    Inventory inv = c.getBlockInventory();
                    ItemStack[] items = inv.getContents();
                    List<ItemStack> itemsToSet = new ArrayList<>();
                    for (ItemStack i : items) {
                        itemsToSet.add(i);
                    }
                    getConfig().set(args[1], itemsToSet);
                    sender.sendMessage("Chest set for " + args[1] + ".");
                    return true;
                }
            }

            //
            //      Corner Positioning - CORNER 1
            //      /rlc corner1 set
            //
            if (args[0].equalsIgnoreCase("corner1")) {
                if (args[1].equalsIgnoreCase("set")) {
                    Player player = getServer().getPlayer(sender.getName());
                    Location loc = player.getLocation();
                    x1 = loc.getBlockX();
                    getConfig().set("corner_1_x", x1);
                    z1 = loc.getBlockZ();
                    getConfig().set("corner_1_z", z1);
                    sender.sendMessage(ChatColor.GOLD + "Corner 1 set at X: " + x1 + " Z: " + z1);
                    return true;
                }
            }

            //
            //      Corner Positioning - CORNER 2
            //      /rlc corner2 set
            //
            if (args[0].equalsIgnoreCase("corner2")) {
                if (args[1].equalsIgnoreCase("set")) {
                    Player player = getServer().getPlayer(sender.getName());
                    Location loc = player.getLocation();
                    x2 = loc.getBlockX();
                    getConfig().set("corner_2_x", x2);
                    z2 = loc.getBlockZ();
                    getConfig().set("corner_2_z", z2);
                    sender.sendMessage(ChatColor.GOLD + "Corner 2 set at X: " + x2 + " Z: " + z2);
                    return true;
                }
            }

            //
            //      Display set region
            //      /rlc region
            //
            if (args[0].equalsIgnoreCase("region")) {
                sender.sendMessage(ChatColor.GOLD + "Corner 1 set at X: " + x1 + " Z: " + z1);
                sender.sendMessage(ChatColor.GOLD + "Corner 2 set at X: " + x2 + " Z: " + z2);
                return true;
            }

        //
        //      Teleport to a location within 500 blocks of the crate
        //      /rlcwild
        //
        } else if (cmd.getName().equalsIgnoreCase("rlcwild")) { // /rlcwild
            sender.sendMessage(ChatColor.GOLD + "Teleporting in 5 seconds...");
            teleport(getServer().getPlayer(sender.getName()));
            return true;
        }

        //      Anything else
        return false;
    }

    public void crateSetup() {
        Crate temp = crates.addCrate(new CommonCrate());
        List<ItemStack> items = (List<ItemStack>) getConfig().get("common");
        temp.setItems(items);

        temp = crates.addCrate(new RareCrate());
        items = (List<ItemStack>) getConfig().get("rare");
        temp.setItems(items);

        temp = crates.addCrate(new EpicCrate());
        items = (List<ItemStack>) getConfig().get("epic");
        temp.setItems(items);

        temp = crates.addCrate(new LegendaryCrate());
        items = (List<ItemStack>) getConfig().get("legendary");
        temp.setItems(items);

        temp = crates.addCrate(new AncientCrate());
        items = (List<ItemStack>) getConfig().get("ancient");
        temp.setItems(items);
    }

    public void run() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                spawnCrate();
            }
        }, 20*time, 20*time);
    }

    public void stop() {
        getServer().getScheduler().cancelTasks(this);
    }

    public void spawnCrate() {
        rChest.updateCrates(crates);
        last = rChest.spawnCrate(x1, z1, x2, z2, last, world);
    }

    public void teleport(Player p) {
        new BukkitRunnable() {
            public void run () {
                Random r = new Random();
                int x1w, x2w, z1w, z2w;
                FLocation fLoc = new FLocation();
                Location loc = new Location(world, 0, 0, 0);
                int finalX = 0;
                int finalZ = 0;

                while (Board.getInstance().getFactionAt(fLoc).isWilderness() == false || loc.getBlockX() == 0 && ((finalX <= 2500 || finalX >= -2500 || finalZ <= 2500 || finalZ >= 2500) && finalX == 0)) {
                    x1w = last.getBlockX() - 500;
                    x2w = last.getBlockX() + 500;
                    z1w = last.getBlockZ() - 500;
                    z2w = last.getBlockZ() + 500;
                    finalX = x1w += r.nextInt(x2w - x1w);
                    finalZ = z1w += r.nextInt(z2w - z1w);
                    int y = world.getHighestBlockYAt(finalX, finalZ);
                    loc = new Location(world, finalX, y, finalZ);
                    fLoc = new FLocation(loc);
                }

                p.teleport(loc);
            }
        }.runTaskLater(this, 20*5);
    }

    public String listCrates() {
        String ret = "";
        for (Crate c : crates.getCratesList()) {
            ret += c.getName() + "\n";
        }
        return ret;
    }
}
