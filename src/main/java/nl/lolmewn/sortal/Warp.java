/*
 * Warp.java
 * 
 * Copyright (c) 2012 Lolmewn <info@lolmewn.nl>. 
 * 
 * Sortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Sortal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Sortal.  If not, see <http ://www.gnu.org/licenses/>.
 */
package nl.lolmewn.sortal;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Lolmewn <info@lolmewn.nl>
 */
public class Warp {

    private String name;
    private Location loc;
    private int price = -1;
    private boolean hasPrice;
    private int uses = -1;
    private int used;
    private boolean usedTotalBased;
    private String owner;

    public Warp(String name, Location loc) {
        this.name = name;
        this.loc = loc;
    }

    public boolean hasPrice() {
        return hasPrice;
    }

    public void setHasPrice(boolean hasPrice) {
        this.hasPrice = hasPrice;
    }

    public String getName() {
        return this.name;
    }

    public boolean isUsedTotalBased() {
        return usedTotalBased;
    }

    public void setUsedTotalBased(boolean usedTotalBased) {
        this.usedTotalBased = usedTotalBased;
    }

    public Location getLocation() {
        return this.loc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean hasOwner() {
        return this.owner == null ? false : true;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public String getLocationToString() {
        return this.loc.getWorld().getName()
                + "," + this.loc.getX()
                + "," + this.loc.getY()
                + "," + this.loc.getZ();
    }

    public void save(MySQL m, String table) {
        int updated = m.executeStatement("UPDATE " + table + " SET "
                + "world='" + loc.getWorld().getName() + "', "
                + "x=" + this.loc.getX() + ", "
                + "y=" + this.loc.getY() + ", "
                + "z=" + this.loc.getZ() + ", "
                + "yaw=" + (double) this.loc.getYaw() + ", "
                + "pitch=" + (double) this.loc.getPitch() + ", "
                + "price=" + this.getPrice() + ", "
                + "uses=" + this.uses + ", "
                + "used=" + this.used + ", "
                + "owner='" + this.owner + "'"
                + " WHERE name='" + this.name + "'");
        
        //It's not in the table at all
        if (updated == 0) {
            m.executeQuery("INSERT INTO " + table + "(name, world, x, y, z, yaw, pitch, price, uses, used, usedTotalBased, owner) VALUES ("
                    + "'" + this.getName() + "', "
                    + "'" + this.getLocation().getWorld().getName() + "', "
                    + this.getLocation().getX() + ", " + this.getLocation().getY()
                    + ", " + this.getLocation().getZ() + ", " + this.getLocation().getYaw()
                    + ", " + this.getLocation().getPitch() + ", " + this.getPrice() + ", "
                    + this.uses + ", " + this.used + ", " + this.usedTotalBased + ", '" + this.owner + "')");
        }
    }

    public void save(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, null, ex);
            }
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set(name + ".world", loc.getWorld().getName());
        c.set(name + ".x", loc.getX());
        c.set(name + ".y", loc.getY());
        c.set(name + ".z", loc.getZ());
        c.set(name + ".yaw", (double) loc.getYaw());
        c.set(name + ".pitch", (double) loc.getPitch());
        c.set(name + ".price", this.price);
        c.set(name + ".uses", this.uses);
        c.set(name + ".used", this.used);
        c.set(name + ".usedTotalBased", this.usedTotalBased);
        c.set(name + ".owner", this.owner);
        try {
            c.save(f);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public void delete(MySQL m, String warpTable) {
        m.executeStatement("DELETE FROM " + warpTable + " WHERE name='" + name + "' LIMIT 1");
    }

    public void delete(File warpFile) {
        YamlConfiguration c = YamlConfiguration.loadConfiguration(warpFile);
        c.set(name, null);
        try {
            c.save(warpFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
    }
}
