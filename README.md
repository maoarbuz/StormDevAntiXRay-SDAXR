**SDAntiXRay** is an advanced X-Ray cheater detection system for Minecraft servers. The plugin analyzes player behavior during resource extraction and identifies suspicious patterns using a smart scoring algorithm.

## ✨ Features

- **Smart detection algorithm** - weighted scoring system for different types of ores
- **Full 1.17+ support** - including all deep shale ores
- **Intuitive GUI interface** - view statistics and suspicious sessions
- **Flexible configuration** - setting up multipliers and thresholds of suspicion
- **Session system** - automatic division into mining sessions
- **Real-time notifications** - alerts for administrators
- **Detailed statistics** - full information on each player

## 📦 Installation

1. Download the latest version of the plugin from [Releases](https://github.com/yourname/SDAntiXRay/releases)
2. Place the file `SDAntiXRay.jar` to the `plugins/` folder of your server
3. Restart or start the server
4. Configure the plugin configuration

## 🎮 Usage

### Basic commands:

- `/sdaxr` - open the GUI with suspicious players
- `/sdaxr info` - open the information GUI
- `/sdaxr reload` - reload the configuration (requires permissions)

### Permissions:

- `sdaxr.info` - access to view information
- `sdaxr.admin` - access to the reload command
- `sdaxr.messages` - receive notifications about suspicions
