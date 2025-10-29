package dev.akatriggered.regionplus.placeholders;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.akatriggered.regionplus.RegionPlus;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.Set;

public class RegionPlaceholders extends PlaceholderExpansion {
    
    private final RegionPlus plugin;
    
    public RegionPlaceholders(RegionPlus plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "regionplus";
    }
    
    @Override
    public String getAuthor() {
        return "akatriggered";
    }
    
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";
        
        // %regionplus_current_region%
        if (params.equals("current_region")) {
            Set<String> regions = plugin.getRegionTracker().getPlayerRegions(player.getUniqueId());
            return regions.isEmpty() ? "wilderness" : regions.iterator().next();
        }
        
        // %regionplus_current_regions%
        if (params.equals("current_regions")) {
            Set<String> regions = plugin.getRegionTracker().getPlayerRegions(player.getUniqueId());
            return regions.isEmpty() ? "wilderness" : String.join(", ", regions);
        }
        
        // %regionplus_region_count%
        if (params.equals("region_count")) {
            return String.valueOf(plugin.getRegionTracker().getPlayerRegions(player.getUniqueId()).size());
        }
        
        // %regionplus_players_<region>%
        if (params.startsWith("players_")) {
            String regionName = params.substring(8);
            return String.valueOf(plugin.getRegionTracker().getRegionPlayerCount(regionName));
        }
        
        // %regionplus_in_<region>%
        if (params.startsWith("in_")) {
            String regionName = params.substring(3);
            Set<String> regions = plugin.getRegionTracker().getPlayerRegions(player.getUniqueId());
            return regions.contains(regionName) ? "true" : "false";
        }
        
        // %regionplus_priority_<region>%
        if (params.startsWith("priority_")) {
            String regionName = params.substring(9);
            RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));
            if (manager != null) {
                ProtectedRegion region = manager.getRegion(regionName);
                if (region != null) {
                    return String.valueOf(region.getPriority());
                }
            }
            return "0";
        }
        
        // %regionplus_owner_<region>%
        if (params.startsWith("owner_")) {
            String regionName = params.substring(6);
            RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));
            if (manager != null) {
                ProtectedRegion region = manager.getRegion(regionName);
                if (region != null && !region.getOwners().getPlayers().isEmpty()) {
                    return region.getOwners().getPlayers().iterator().next().toString();
                }
            }
            return "none";
        }
        
        // %regionplus_is_owner_<region>%
        if (params.startsWith("is_owner_")) {
            String regionName = params.substring(9);
            RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));
            if (manager != null) {
                ProtectedRegion region = manager.getRegion(regionName);
                if (region != null) {
                    return region.getOwners().contains(player.getUniqueId()) ? "true" : "false";
                }
            }
            return "false";
        }
        
        // %regionplus_is_member_<region>%
        if (params.startsWith("is_member_")) {
            String regionName = params.substring(10);
            RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));
            if (manager != null) {
                ProtectedRegion region = manager.getRegion(regionName);
                if (region != null) {
                    return region.getMembers().contains(player.getUniqueId()) ? "true" : "false";
                }
            }
            return "false";
        }
        
        // %regionplus_total_regions%
        if (params.equals("total_regions")) {
            return String.valueOf(plugin.getRegionTracker().getAllRegions().size());
        }
        
        return null;
    }
}
