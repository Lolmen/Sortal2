package nl.lolmewn.sortal;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Lolmewn
 */
public class Localisation {
    
    private File localisation = new File("plugins" + File.separator + "Sortal"
            + File.separator + "localisation.yml");
    
    private String noPlayer;
    private String noPerms;
    private String createNameForgot;
    private String deleteNameForgot;
    private String nameInUse;
    private String warpCreated;
    private String warpDeleted;
    private String warpNotFound;
    private String paymentComplete;
    private String noMoney;

    public Localisation() {
        this.checkFile();
        this.loadFile();
    }

    private void checkFile() {
        if(!this.localisation.exists()){
            try {
            Bukkit.getLogger().info("[Sortal] Trying to create default language file...");
            try {
                InputStream in = this.getClass().
                        getClassLoader().getResourceAsStream("localisation.yml");
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(this.localisation));
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                out.flush();
                out.close();
                in.close();
                Bukkit.getLogger().info("[Sortal] Default language file created succesfully!");
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("[Sortal] Error creating language file! Using default settings!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

    private void loadFile() {
        YamlConfiguration c = YamlConfiguration.loadConfiguration(this.localisation);
        this.createNameForgot = c.getString("commands.createNameForgotten");
        this.deleteNameForgot = c.getString("commands.deleteNameForgotten");
        this.nameInUse = c.getString("commands.nameInUse");
        this.noMoney = c.getString("noMoney");
        this.noPerms = c.getString("noPermissions");
        this.paymentComplete = c.getString("paymentComplete");
        this.warpCreated = c.getString("commands.warpCreated");
        this.warpDeleted = c.getString("commands.warpDeleted");
        this.warpNotFound = c.getString("commands.warpNotFound");
        Bukkit.getLogger().info("[Sortal] Localisation loaded!");
    }

    public String getCreateNameForgot() {
        return createNameForgot;
    }

    public String getDeleteNameForgot() {
        return deleteNameForgot;
    }

    public String getNameInUse(String warp) {
        if(this.nameInUse.contains("$WARP") && warp != null && !warp.equals("")){
            return nameInUse.replace("$WARP", warp);
        }
        return nameInUse;
    }

    public String getNoMoney(String money) {
        if(this.nameInUse.contains("$WARP") && money != null && !money.equals("")){
            return nameInUse.replace("$MONEY", money);
        }
        return noMoney;
    }

    public String getNoPerms() {
        return noPerms;
    }

    public String getNoPlayer() {
        return noPlayer;
    }

    public String getPaymentComplete(String money) {
        if(this.nameInUse.contains("$WARP") && money != null && !money.equals("")){
            return nameInUse.replace("$WARP", money);
        }
        return paymentComplete;
    }

    public String getWarpCreated(String warp) {
        if(this.nameInUse.contains("$WARP") && warp != null && !warp.equals("")){
            return nameInUse.replace("$WARP", warp);
        }
        return warpCreated;
    }

    public String getWarpDeleted(String warp) {
        if(this.nameInUse.contains("$WARP") && warp != null && !warp.equals("")){
            return nameInUse.replace("$WARP", warp);
        }
        return warpDeleted;
    }

    public String getWarpNotFound(String warp) {
        if(this.nameInUse.contains("$WARP") && warp != null && !warp.equals("")){
            return nameInUse.replace("$WARP", warp);
        }
        return warpNotFound;
    }

    void addOld(HashMap<String, String> local) {
        try {
            YamlConfiguration c = YamlConfiguration.loadConfiguration(this.localisation);
            for(String path : local.keySet()){
                c.set(path, local.get(path));
            }
            c.save(localisation);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
        this.loadFile();
    }
    
}
