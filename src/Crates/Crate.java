package Crates;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Crate {
    private String name;
    private String colour;
    private int rarity; // x / 100
    private List<ItemStack> items;

    public Crate() {
        name = "unknown";
        rarity = -1;
        colour = "unknown";
        items = null;
    }

    public Crate(String n, int r, String c) {
        name = n;
        rarity = r;
        colour = c;
    }

    public String toString() {
        String ret = "Name: " + name + " Colour: " + colour + " Rarity: " + rarity; //+ " Items: ";
//        for (ItemStack item : items) {
//            ret += "\n" + item.toString();
//        }
        return ret;
    }

    public String getColour() {
        return colour;
    }

    public int getRarity() {
        return rarity;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public void setName(String name) {
        this.name = name;
    }




}
