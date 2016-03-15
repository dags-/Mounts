# Mounts
[![build](https://img.shields.io/travis/dags-/Mounts.svg)]( "Build Status") 
[![latest](https://img.shields.io/badge/releases-latest-blue.svg)](https://github.com/dags-/Mounts/releases/latest "Grab the Latest Build") 
[![build](https://img.shields.io/badge/issues-track-orange.svg)](https://github.com/dags-/Mounts/issues "View/Create Issues") 

Mounts is a Sponge plugin that allows players to create personal, rideable mounts from Minecraft mobs.
[![squid](https://raw.githubusercontent.com/dags-/Mounts/img/flyingsquid.gif)](https://youtu.be/DJ_yejdiH98 "Click for demo video!")

## Quick Start
Create your mount by using the `/mount create` command. You will then be able to spawn your mount by right-clicking the assigned item (a saddle by default).

Your mount will automatically move in the direction that the player is looking (including flying up/down if enabled).

Mounts can travel at two different speeds, one whilst holding a 'leash', and one with any other (or no) item in hand.

See `/help mount` for a full list of commands that can be used to customise your mount. 

## Configuartion
The `mounts.conf` file should be fairly self-explanatory.  
There are 3 main sections:
- `command_colors` - configure the colors of `/mount` command messages
- `default_mount` - define the default properties of a newly created mount
- `speed_limits` - allows you to globally set minimum and maximum speeds for mounts  
_('&' color codes are not supported)_

## Commands
- `/mount create` - create a new mount
- `/mount fly` - toggle whether or not your mount can fly (certain flying mounts will fly regardless)
- `/mount invincible` - toggle whether or not your mount can take damge (including fall damage)
- `/mount item` - set the item type you must be holding to spawn your mount
- `/mount leash` - set the item type you must be holding to activate your mount's secondary speed
- `/mount remove` - delete your mount
- `/mount speed leashed <number>` - set the movement speed of your mount whilst you are holding a leash
- `/mount speed normal <number>` - set the normal movement speed of your mount
- `/mount type <entity_type>` - set your mount to the given entity type
- `/mount types` - list the available entity types
- `/mount purge` - removes all active mounts from all worlds
- `/mount reload` - reloads the mounts config file from disk

## Permissions
- `mounts.command.create` - allows use of the `/mount create` command
- `mounts.command.remove.self` - allows use of the `/mount remove` command
- `mounts.command.fly` - allows use of the `/mount fly` command
- `mounts.command.invincible` - allows use of the `/mount invincible` command
- `mounts.command.item` - allows use of the `/mount item` command
- `mounts.command.leash` - allows use of the `/mount leash` command
- `mounts.command.purge` - allows use of the `/mount purge` command
- `mounts.command.reload` - allows use of the `/mount reload` command
- `mounts.command.speed` - allows use of the `/mount speed` command
- `mounts.command.type` - allows use of the `/mount type` command
- `mounts.command.types` - allows use of the `/mount types` command
- `mounts.use` - allows user to right-click their mount item in order to spawn it
- `mounts.type.<entity_type>` - allows user to set their mount to a specific entity type

## Notes
- **If the server does not shut down correctly, some mount entities may persist in the world after it starts again**
- **Flying around quickly may put stress on the server's hardware as it has to load chunks more quickly than usual**
- User experience may vary depending on latency
- Aggressive mounts may still try to attack you in Survival/Adventure mode
- The player's riding position may not always be perfectly on the mounts back
