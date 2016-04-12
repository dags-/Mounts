/*
 * The MIT License (MIT)
 *
 * Copyright (c) dags <https://dags.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.dags.mount.commands;

import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.dlib.commands.CommandMessenger;
import me.dags.mount.MountsPlugin;
import me.dags.mount.Permissions;
import me.dags.mount.data.MountKeys;
import me.dags.mount.data.player.PlayerMountDataMutable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author dags <dags@dags.me>
 */

public class UserCommands
{
    private final MountsPlugin plugin;

    public UserCommands(MountsPlugin plugin)
    {
        this.plugin = plugin;
    }

    private CommandMessenger.Builder messenger()
    {
        return plugin.config().messenger().builder();
    }

    @Command(aliases = {"reload", "r"}, parent = "mount", perm = Permissions.COMMAND_RELOAD)
    public void reload(@Caller CommandSource source)
    {
        messenger().info("Reloading...").tell(source);
        plugin.reloadConfig();
    }

    @Command(aliases = {"purge", "p"}, parent = "mount", perm = Permissions.COMMAND_PURGE)
    public void purge(@Caller CommandSource source)
    {
        messenger().info("Purging...").tell(source);
        plugin.clearMounts();
    }

    @Command(aliases = {"create", "c"}, parent = "mount", perm = Permissions.COMMAND_CREATE)
    public void create(@Caller Player player)
    {
        if (!player.get(PlayerMountDataMutable.class).isPresent())
        {
            messenger().info("See '").stress("/help mount").info("' for more on setting up your mount!").tell(player);
        }

        PlayerMountDataMutable data = new PlayerMountDataMutable();
        data.setEntityType(plugin.config().defaults().type());
        data.setSpawnItem(plugin.config().defaults().spawnItem());
        data.setLeashItem(plugin.config().defaults().leashItem());
        data.set(MountKeys.CAN_FLY, plugin.config().defaults().canFly());
        data.set(MountKeys.INVINCIBLE, plugin.config().defaults().invincible());
        data.set(MountKeys.MOVE_SPEED, plugin.config().defaults().normalSpeed());
        data.set(MountKeys.LEASH_SPEED, plugin.config().defaults().leashSpeed());
        player.offer(data);

        messenger().info("Successfully created your new mount!").tell(player);
    }

    @Command(aliases = {"canfly", "fly", "f"}, parent = "mount", perm = Permissions.COMMAND_FLY)
    public void fly(@Caller Player player)
    {
        toggle(player, MountKeys.CAN_FLY, "flight");
    }

    @Command(aliases = {"invincible", "i"}, parent = "mount", perm = Permissions.COMMAND_INVINCIBLE)
    public void invincible(@Caller Player player)
    {
        toggle(player, MountKeys.INVINCIBLE, "invincibility");
    }

    @Command(aliases = {"item", "i"}, parent = "mount", perm = Permissions.COMMAND_ITEM)
    public void item(@Caller Player player)
    {
        setItem(player, (d, i) -> d.setSpawnItem(i.getName()));
    }

    @Command(aliases = {"leash", "lead", "l"}, parent = "mount", perm = Permissions.COMMAND_LEASH)
    public void leash(@Caller Player player)
    {
        setItem(player, (d, i) -> d.setLeashItem(i.getName()));
    }

    @Command(aliases = {"reset", "r"}, parent = "mount", perm = Permissions.COMMAND_RESET_SELF)
    public void reset(@Caller Player player)
    {
        if (!player.get(PlayerMountDataMutable.class).isPresent())
        {
            messenger().error("You do not have a mount to reset!").tell(player);
            return;
        }

        player.remove(PlayerMountDataMutable.class);
        messenger().info("Reset your mount! Use '").stress("/mount create").info("' to create a new one").tell(player);
    }

    @Command(aliases = {"leash", "leashed", "l"}, parent = "mount speed", perm = Permissions.COMMAND_SPEED)
    public void speedLeash(@Caller Player player, @One("speed") double speed)
    {
        if (plugin.config().speeds().outsideOfRange(speed))
        {
            messenger().stress(speed)
                    .info(" is not within the allowed range: ")
                    .stress(plugin.config().speeds().getRange())
                    .tell(player);
            return;
        }

        if (set(player, MountKeys.LEASH_SPEED, speed))
        {
            messenger().info("Your mount's leashed speed has been set to: ").stress(speed).tell(player);
        }
    }

    @Command(aliases = {"normal", "n"}, parent = "mount speed", perm = Permissions.COMMAND_SPEED)
    public void speedNormal(@Caller Player player, @One("speed") double speed)
    {
        if (plugin.config().speeds().outsideOfRange(speed))
        {
            messenger().stress(speed)
                    .info(" is not within the allowed range: ")
                    .stress(plugin.config().speeds().getRange())
                    .tell(player);
            return;
        }

        if (set(player, MountKeys.MOVE_SPEED, speed))
        {
            messenger().info("Your mount's speed has been set to: ").stress(speed).tell(player);
        }
    }

    @Command(aliases = {"type", "t"}, parent = "mount", perm = Permissions.COMMAND_TYPE)
    public void type(@Caller Player player, @One("entity") String type)
    {
        Optional<PlayerMountDataMutable> optional = player.get(PlayerMountDataMutable.class);
        if (optional.isPresent())
        {
            PlayerMountDataMutable data = optional.get();
            setType(player, data, type);
        }
        else
        {
            messenger().error("You must first create a mount before your can change it's type!").tell(player);
        }
    }

    @Command(aliases = "types", parent = "mount", perm = Permissions.COMMAND_TYPES)
    public void types(@Caller CommandSource source)
    {
        List<String> names = Sponge.getRegistry().getAllOf(EntityType.class).stream()
                .filter(t -> Living.class.isAssignableFrom(t.getEntityClass()))
                .distinct()
                .map(EntityType::getName)
                .sorted()
                .collect(Collectors.toList());
        messenger().info("Available Mount types: ").stress(names).tell(source);
    }

    private boolean toggle(Player player, Key<Value<Boolean>> key, String settingName)
    {
        Optional<PlayerMountDataMutable> optional = player.get(PlayerMountDataMutable.class);
        if (optional.isPresent())
        {
            PlayerMountDataMutable data = optional.get();
            Boolean value = !data.get(key).get();
            player.offer(data.set(key, value));
            messenger().info("Toggled ").stress(settingName).info(" " + (value ? "on" : "off")).tell(player);
            return true;
        }
        messenger().error("You must first create a mount before your can change its properties!").tell(player);
        return false;
    }

    private void setItem(Player player, BiFunction<PlayerMountDataMutable, ItemType, Boolean> operation)
    {
        Optional<PlayerMountDataMutable> optional = player.get(PlayerMountDataMutable.class);
        if (optional.isPresent())
        {
            Optional<ItemStack> inHand = player.getItemInHand();
            if (inHand.isPresent())
            {
                String item = inHand.get().getItem().getName();
                if (operation.apply(optional.get(), inHand.get().getItem()))
                {
                    player.offer(optional.get());
                    messenger().info("Item set to: ").stress(item).tell(player);
                }
                return;
            }
            messenger().error("You must be holding an item to use this command!").tell(player);
            return;
        }
        messenger().error("You must first create a mount before your can change its properties!").tell(player);
    }

    private <T> boolean set(Player player, Key<Value<T>> key, T value)
    {
        Optional<PlayerMountDataMutable> optional = player.get(PlayerMountDataMutable.class);
        if (optional.isPresent())
        {
            PlayerMountDataMutable data = optional.get();
            data.set(key, value);
            player.offer(data);
            return true;
        }
        messenger().error("You must first create a mount before your can change its properties!").tell(player);
        return false;
    }

    private boolean setType(Player player, PlayerMountDataMutable data, String type)
    {
        if (Permissions.allowedType(player, type))
        {
            if (data.setEntityType(type))
            {
                player.offer(data);
                messenger().info("Set your mount to: ").stress(type).tell(player);
                return true;
            }
            messenger().stress(type).info(" is not a valid mount type!").tell(player);
            return true;
        }
        messenger().warn("You do not have permission for mount type: ").stress(type).tell(player);
        return false;
    }
}
