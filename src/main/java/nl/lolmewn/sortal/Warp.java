package nl.lolmewn.sortal;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Lolmewn
 */
public class Warp {
    
    private String name;
    private Location loc;
    private int price;
    
    public Warp(String name, Location loc, int price){
        this.name = name;
        this.loc = loc;
        this.price = price;
    }
    
    public String getName(){
        return this.name;
    }
    
    public Location getLocation(){
        return this.loc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
    public String getLocationToString(){
        return this.loc.getWorld().getName() + 
                "," + this.loc.getX() + 
                "," + this.loc.getY() + 
                "," + this.loc.getZ();
    }
    
    public void save(MySQL m, String table){
        ResultSet set = m.executeQuery("SELECT * FROM " + table + 
                " WHERE name='" + name + "'");
        if(set == null){
            //dafuq? 
            System.out.println("[Sortal] ERR: ResultSet returned null");
            return;
        }
        try {
            while(set.next()){
                //a warp already is in the database, gotta update it
                if(set.getDouble("x") == this.loc.getX() && 
                        set.getString("world").equals(this.loc.getWorld().getName()) &&
                        set.getDouble("y") == this.loc.getY() && 
                        set.getDouble("z") == this.loc.getZ() && 
                        set.getInt("price") == this.getPrice()){
                    //no need to update anything
                    return;
                }
                m.executeStatement("UPDATE " + table + " SET "
                        + "world='" + loc.getWorld().getName() + "', "
                        + "x=" + this.loc.getX() + ", "
                        + "y=" + this.loc.getY() + ", "
                        + "z=" + this.loc.getZ() + ", "
                        + "yaw=" + (double)this.loc.getYaw() + ", "
                        + "pitch=" + (double)this.loc.getPitch() + ", "
                        + "price=" + this.getPrice()
                        + " WHERE name='" + this.name + "'");
                return;
            }
            //It's not in the table at all
            m.executeQuery("INSERT INTO " + table + "(name, world, x, y, z, yaw, pitch, price) VALUES ("
                    + "'" + this.getName() + "', "
                    + "'" + this.getLocation().getWorld().getName() + "', " 
                    + this.getLocation().getX() + ", " + this.getLocation().getY()
                    + ", " + this.getLocation().getZ() + ", " + this.getLocation().getYaw()
                    + ", " + this.getLocation().getPitch() + ", " + this.getPrice() + ")");
        } catch (SQLException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
    }
    
    public void save(File f){
        if(!f.exists()){
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
        c.set(name + ".yaw", (double)loc.getYaw());
        c.set(name + ".pitch", (double)loc.getPitch());
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
