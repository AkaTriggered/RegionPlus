package dev.akatriggered.regionplus.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.akatriggered.regionplus.RegionPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegionTracker implements Listener {
    
    private final RegionPlus plugin;
    private final Map<UUID, Set<String>> playerRegions = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> regionPlayers = new ConcurrentHashMap<>();
    
    public RegionTracker(RegionPlus plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        updatePlayerRegions(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Delay to ensure player is fully loaded
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            updatePlayerRegions(event.getPlayer());
        }, 20L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        Set<String> regions = playerRegions.remove(playerId);
        if (regions != null) {
            for (String region : regions) {
                Set<UUID> players = regionPlayers.get(region);
                if (players != null) {
                    players.remove(playerId);
                    if (players.isEmpty()) {
                        regionPlayers.remove(region);
                    }
                }
            }
        }
    }
    
    public void updatePlayerRegions(Player player) {
        try {
            RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));
            
            if (manager == null) {
                plugin.getLogger().warning("RegionManager is null for world: " + player.getWorld().getName());
                return;
            }
            
            ApplicableRegionSet regions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
            Set<String> currentRegions = new HashSet<>();
            
            for (ProtectedRegion region : regions) {
                currentRegions.add(region.getId());
                plugin.getLogger().info("Player " + player.getName() + " is in region: " + region.getId());
            }
            
            UUID playerId = player.getUniqueId();
            Set<String> previousRegions = playerRegions.getOrDefault(playerId, new HashSet<>());
            
            // Remove player from old regions
            for (String region : previousRegions) {
                if (!currentRegions.contains(region)) {
                    Set<UUID> players = regionPlayers.get(region);
                    if (players != null) {
                        players.remove(playerId);
                        if (players.isEmpty()) {
                            regionPlayers.remove(region);
                        }
                    }
                }
            }
            
            // Add player to new regions
            for (String region : currentRegions) {
                regionPlayers.computeIfAbsent(region, k -> new HashSet<>()).add(playerId);
            }
            
            playerRegions.put(playerId, currentRegions);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error updating player regions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Set<String> getPlayerRegions(UUID playerId) {
        return playerRegions.getOrDefault(playerId, new HashSet<>());
    }
    
    public int getRegionPlayerCount(String regionName) {
        Set<UUID> players = regionPlayers.get(regionName);
        int count = players != null ? players.size() : 0;
        plugin.getLogger().info("Region " + regionName + " has " + count + " players");
        return count;
    }
    
    public Set<UUID> getRegionPlayers(String regionName) {
        return regionPlayers.getOrDefault(regionName, new HashSet<>());
    }
    
    public List<String> getAllRegions() {
        List<String> allRegions = new ArrayList<>();
        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(world));
            if (manager != null) {
                allRegions.addAll(manager.getRegions().keySet());
            }
        }
        return allRegions;
    }
}
