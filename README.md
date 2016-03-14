# Mounts
Mounts is a Sponge plugin that allows players to create personal, rideable mounts from Minecraft mobs.

[![Build Status](https://travis-ci.org/dags-/Mounts.svg?branch=master)](https://travis-ci.org/dags-/Mounts)

![squid](https://raw.githubusercontent.com/dags-/Mounts/img/flyingsquid.gif)

## Usage
Once a mount has been created using the `/mount create` command, the user can spawn and ride this mount by right-clicking with a saddle (customisable) in their hand. The mount is immediately removed from the world when it is dismounted (by sneaking).

Mounts will automatically move in the direction that the player is looking (including flying up/down if enabled).

Mounts can travel at two different speeds, one when holding a leash item, and one with any other (or no) item in hand (both speeds are configurable). Global minumum and maximum speeds can be set in the config file.

## Commands
- `/mount create` - create a new mount
- `/mount remove` - delete your mount
- `/mount type <entity_type>` - set your mount to the given entity type
- `/mount item` - set the item type you must be holding to spawn your mount
- `/mount leash` - set the item type you must be holding to activate your mount's secondary speed
- `/mount fly` - toggle whether or not your mount can fly (certain flying mounts will fly regardless)
- `/mount invincible` - toggle whether or not your mount can take damge (including fall damage)
- `/mount speed normal <number>` - set the normal movement speed of your mount
- `/mount speed leashed <number>` - set the movement speed of your mount whilst you are holding a leash
- `/mount reload` - reloads the mounts config file from disk
- `/mount purge` - removes all active mounts from all worlds

_Entity names are as defined by SpongeAPI, and generally single, no-spaces words_

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
- `mounts.use` - allows user to right-click their mount item in order to spawn it
- `mounts.type.<entity_type>` - allows user to set their mount to a specific entity type

## Notes
- **If the server does not shut down correctly, some mount entities may persist in the world after it starts again**
- **Flying around quickly may put stress on the server's hardware as it has to load chunks more quickly than usual**
- User experience may vary depending on latency
- Aggressive mounts may still try to attack you in Survival/Adventure mode
- The player's riding position may not always be perfectly on the mounts back
