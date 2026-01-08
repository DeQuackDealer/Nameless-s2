# Nameless-s2

A custom Paper plugin for Minecraft 1.21+ featuring unique custom items and mechanics.

## Features

### Lifesteal Sword
- Custom sword with Netherite-level stats and Diamond sword damage
- Heals 50% of damage dealt on hit
- Enters 25-second cooldown after 10 successful lifesteal hits
- Crafted with Netherite Ingots and Blaze Rod
- Repairable with Netherite Ingots

### Swap Rod (Boogie Woogie)
- Custom fishing rod that swaps positions with hooked living entities
- Plays Enderman teleport sound on successful swap
- Has Netherite-level durability (2031 uses)
- Crafted with Netherite Ingots and String
- Repairable with Netherite Ingots

### Diamond Apple
- Ultra-powerful consumable item
- Grants effects stronger than Enchanted Golden Apple:
  - Absorption IV (5 minutes)
  - Resistance II (3 minutes)
  - Regeneration III (45 seconds)
  - Strength II (3 minutes)
  - Fire Resistance (10 minutes)
  - Speed II (2 minutes)
- Crafted with Diamond Blocks surrounding a Golden Apple

### Spear Progression System
- Players receive a Wooden Spear on first join
- Kill players to upgrade your spear through tiers:
  - Wood → Copper (1 kill)
  - Copper → Iron (2 kills)
  - Iron → Diamond (3 kills)
  - Diamond → Netherite (4 kills)
- Death causes spear downgrade by one tier
- Each tier has appropriate durability and can be repaired with matching materials

## Commands

- `/custompluginauthor` - Display plugin author information

## Requirements

- Paper 1.21+
- Java 21

## Installation

1. Download the latest release from [Releases](https://github.com/DeQuackDealer/Nameless-s2/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server

## Building

```bash
mvn clean package
```

The compiled JAR will be in the `target` folder.

## License

MIT License - see [LICENSE](LICENSE) for details.

## Author

**DeQuackDealer**
- GitHub: https://github.com/DeQuackDealer
- Discord: dequackdea1er

Open for hire! Message me on Discord!
