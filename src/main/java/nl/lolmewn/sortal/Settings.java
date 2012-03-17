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
public class Settings {

    private boolean useMySQL;
    private boolean update;
    private double version;
    private boolean debug;
    private int warpCreatePrice;
    private int warpUsePrice;
    private String signContains;
    private String dbUser, dbPass, dbPrefix, dbHost, dbDatabase;
    private int dbPort;
    private File settingsFile = new File("plugins" + File.separator + "Sortal"
            + File.separator + "settings.yml");
    
    private Localisation localisation;
    
    public Localisation getLocalisation(){
        return this.localisation;
    }

    protected String getDbDatabase() {
        return dbDatabase;
    }

    protected String getDbHost() {
        return dbHost;
    }

    protected String getDbPass() {
        return dbPass;
    }

    protected int getDbPort() {
        return dbPort;
    }

    protected String getDbPrefix() {
        return dbPrefix;
    }

    protected String getDbUser() {
        return dbUser;
    }

    public String getSignContains() {
        return signContains;
    }

    public boolean useMySQL() {
        return useMySQL;
    }

    public int getWarpCreatePrice() {
        return warpCreatePrice;
    }

    public int getWarpUsePrice() {
        return warpUsePrice;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isUpdate() {
        return update;
    }

    public double getVersion() {
        return version;
    }
    
    /*
     * Only used if there's an error when starting MySQL
     */
    protected void setUseMySQL(boolean use){
        this.useMySQL = use;
    }
    
    /*
     * Constructor
     */
    public Settings() {
        this.localisation = new Localisation();
        this.checkFile();
        this.loadSettings();
    }

    private void checkFile() {
        if (!this.settingsFile.exists()) {
            this.extractSettings();
        }
    }

    private void loadSettings() {
        YamlConfiguration c = YamlConfiguration.loadConfiguration(this.settingsFile);
        if(c.contains("showWhenWarpGetsLoaded")){
            //Old version of config file
            this.convert(c);
        }
        
        this.useMySQL = c.getBoolean("useMySQL");
        this.dbUser = c.getString("MySQL-User");
        this.dbPass = c.getString("MySQL-Pass");
        this.dbHost = c.getString("MySQL-Host");
        this.dbPort = c.getInt("MySQL-Port");
        this.dbDatabase = c.getString("MySQL-Database");
        this.dbPrefix = c.getString("MySQL-Prefix");
        this.warpCreatePrice = c.getInt("warpCreatePrice");
        this.warpUsePrice = c.getInt("warpUsePrice");
        this.signContains = c.getString("signContains", "[Sortal]");
        this.update = c.getBoolean("update", true);
        this.version = c.getDouble("version");
        this.debug = c.getBoolean("debug", false);
        if(this.isDebug()){
            this.printSettings(c);
        }
    }

    private void extractSettings() {
        try {
            Bukkit.getLogger().info("Trying to create default config...");
            try {
                InputStream in = this.getClass().
                        getClassLoader().getResourceAsStream("settings.yml");
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(this.settingsFile));
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                out.flush();
                out.close();
                in.close();
                Bukkit.getLogger().info("Default config created succesfully!");
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("Error creating settings file! Using default settings!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printSettings(YamlConfiguration c) {
        for(String path : c.getConfigurationSection("").getKeys(true)){
            Bukkit.getLogger().info("[Sortal - Debug] CONFIG: " + path + ":" + c.get(path, null));
        }
    }

    private void convert(YamlConfiguration c) {
        final HashMap<String, Object> values = new HashMap<String, Object>();
        boolean useVault = c.getBoolean("plugins.useVault", false);
        if(useVault){
            values.put("warpCreatePrice", c.getInt("warpCreatePrice"));
            values.put("warpUsePrice", c.getInt("warpUsePrice"));
        }
        values.put("signContains" , c.getString("signContains", "[Sortal]"));
        values.put("update", c.getBoolean("auto-update", true));
        values.put("debug", c.getBoolean("debug", false));
        values.put("useMySQL", c.getBoolean("useMySQL", false));
        if(useMySQL){
            values.put("MySQL-User", c.getString("MySQL.username", "root"));
            values.put("MySQL-Pass", c.getString("MySQL.password", "p4ssw0rd"));
            values.put("MySQL-Database", c.getString("MySQL.database", "minecraft"));
            values.put("MySQL-Host", c.getString("MySQL.host", "localhost"));
        }
        
        HashMap<String, String> local = new HashMap<String, String>();
        local.put("noPermissions", c.getString("no-permissions"));
        local.put("commands.createNameForgotten", c.getString("warpCreateNameForgotten"));
        local.put("commands.deleteNameForgotten", c.getString("warpDeleteNameForgotten"));
        local.put("commands.nameInUse", c.getString("nameInUse"));
        local.put("paymentComplete", c.getString("moneyPayed"));
        local.put("commands.warpCreated", c.getString("warpCreated"));
        local.put("noMoney", c.getString("notEnoughMoney"));
        local.put("commands.warpDeleted", c.getString("warpDeleted"));
        local.put("commands.warpNotFound", c.getString("warpDoesNotExist"));
        local.put("noPlayer", c.getString("notAplayer"));
        
        this.getLocalisation().addOld(local);
        
        if(this.settingsFile.delete()){
            Bukkit.getLogger().info("Old Config deleted, values stored..");
            this.extractSettings();
            this.addSettingsToConfig(settingsFile, values);
        }else{
            Bukkit.getLogger().warning("Couldn't delete old settings file! Using all defaults");
            Bukkit.getLogger().warning("Deleting as soon as possible!");
            new Thread(new Runnable(){
                public void run() {
                    while(!settingsFile.delete()){
                        //keep trying
                    }
                    Bukkit.getLogger().info("Old setting file deleted, creating new one..");
                    extractSettings();
                    addSettingsToConfig(settingsFile, values);
                }
            }).start();
        }
    }
    
    private void addSettingsToConfig(File f, HashMap<String, Object> values){
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        for(String path : values.keySet()){
            c.set(path, values.get(path));
        }
        try {
            c.save(f);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
        Bukkit.getLogger().info("Saved old settings in new settings file!");
    }
}
