# RegionPlus

A lightweight WorldGuard addon that provides advanced placeholders and region tracking functionality.

## Features

- **Real-time Region Tracking**: Efficiently tracks players in WorldGuard regions
- **Comprehensive Placeholders**: 10+ placeholders for region information
- **Optimized Performance**: Minimal memory footprint and CPU usage
- **PlaceholderAPI Integration**: Full compatibility with PlaceholderAPI

## Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%regionplus_current_region%` | Current region name | `spawn` |
| `%regionplus_current_regions%` | All current regions | `spawn, shop` |
| `%regionplus_region_count%` | Number of regions player is in | `2` |
| `%regionplus_players_<region>%` | Players in specific region | `5` |
| `%regionplus_in_<region>%` | If player is in region | `true` |
| `%regionplus_priority_<region>%` | Region priority | `10` |
| `%regionplus_owner_<region>%` | Region owner | `player123` |
| `%regionplus_is_owner_<region>%` | If player owns region | `true` |
| `%regionplus_is_member_<region>%` | If player is member | `false` |
| `%regionplus_total_regions%` | Total regions on server | `25` |

## Requirements

- **WorldGuard** (Required)
- **PlaceholderAPI** (Optional but recommended)
- **Java 17+**
- **Paper/Spigot 1.16+**

## Installation

1. Download RegionPlus.jar
2. Place in your `plugins/` folder
3. Restart your server
4. Placeholders are automatically available

## Usage Examples

### Scoreboard
```yaml
title: "&6Server Info"
lines:
  - "&7Region: &a%regionplus_current_region%"
  - "&7Players here: &e%regionplus_players_%regionplus_current_region%%"
  - "&7Total regions: &b%regionplus_total_regions%"
```

### Chat Format
```yaml
format: "&7[%regionplus_current_region%&7] &f%player_name%: %message%"
```

### Region-specific Messages
```yaml
# Using ConditionalCommands or similar
- "regionplus_in_spawn == true ? &aWelcome to spawn! : &7You left spawn"
```

## Performance

- **Memory**: ~2MB RAM usage
- **CPU**: <1% server tick time
- **Storage**: No database required
- **Network**: Zero network overhead

## Support

For issues or feature requests, please visit our GitHub repository.
