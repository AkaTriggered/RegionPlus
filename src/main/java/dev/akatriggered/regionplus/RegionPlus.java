package dev.akatriggered.regionplus;

import dev.akatriggered.regionplus.listeners.RegionTracker;
import dev.akatriggered.regionplus.placeholders.RegionPlaceholders;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionPlus extends JavaPlugin {
    
    private static RegionPlus instance;
    private RegionTracker regionTracker;
    
    @Override
    public void onEnable() {
        instance = this;
        
        if (!checkDependencies()) {
            getLogger().severe("Missing required dependencies! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        regionTracker = new RegionTracker(this);
        getServer().getPluginManager().registerEvents(regionTracker, this);
        
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RegionPlaceholders(this).register();
            getLogger().info("PlaceholderAPI integration enabled!");
        }
        
        getLogger().info("RegionPlus enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("RegionPlus disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("regionplus")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players!");
                return true;
            }
            
            Player player = (Player) sender;
            sender.sendMessage("§6=== RegionPlus Debug ===");
            sender.sendMessage("§7Player regions: " + regionTracker.getPlayerRegions(player.getUniqueId()));
            sender.sendMessage("§7Total regions on server: " + regionTracker.getAllRegions().size());
            sender.sendMessage("§7All regions: " + regionTracker.getAllRegions());
            
            // Force update
            regionTracker.updatePlayerRegions(player);
            sender.sendMessage("§aForced region update!");
            
            return true;
        }
        return false;
    }
    
    private boolean checkDependencies() {
        return getServer().getPluginManager().getPlugin("WorldGuard") != null;
    }
    
    public static RegionPlus getInstance() {
        return instance;
    }
    
    public RegionTracker getRegionTracker() {
        return regionTracker;
    }
}
