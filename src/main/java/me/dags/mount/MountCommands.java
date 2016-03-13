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

package me.dags.mount;

import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.dalib.commands.CommandMessenger;
import me.dags.mount.data.MountKeys;
import me.dags.mount.data.PlayerMountDataMutable;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

@ConfigSerializable
public class MountCommands
{
    private final MountsPlugin plugin;

    protected MountCommands(MountsPlugin plugin)
    {
        this.plugin = plugin;
    }

    private CommandMessenger.Builder messenger()
    {
        return plugin.config().commandMessenger.builder();
    }

    @Command(aliases = {"reload", "r"}, parent = "mount", perm = Permissions.COMMAND_RELOAD)
    public void reload(@Caller Player player)
    {
        messenger().info("Reloading...").tell(player);
        plugin.reloadConfig();
    }

    @Command(aliases = {"purge", "p"}, parent = "mount", perm = Permissions.COMMAND_PURGE)
    public void purge(@Caller Player player)
    {
        messenger().info("Reloading...").tell(player);
        plugin.clearMounts();
    }

    @Command(aliases = {"create", "c"}, parent = "mount", perm = Permissions.COMMAND_CREATE)
    public void create0(@Caller Player player)
    {
        create1(player, "pig");
    }

    @Command(aliases = {"create", "c"}, parent = "mount", perm = Permissions.COMMAND_CREATE)
    public void create1(@Caller Player player, @One("entity") String type)
    {
        if (!player.get(PlayerMountDataMutable.class).isPresent())
        {
            messenger().info("See '").stress("/help mount").info("' for more on setting up your mount!").tell(player);
        }

        PlayerMountDataMutable data = new PlayerMountDataMutable();
        if (attemptSetType(player, data, type))
        {
            messenger().info("Successfully created your new mount!").tell(player);
        }
    }

    @Command(aliases = {"type", "t"}, parent = "mount", perm = Permissions.COMMAND_TYPE)
    public void type(@Caller Player player, @One("entity") String type)
    {
        Optional<PlayerMountDataMutable> optional = player.get(PlayerMountDataMutable.class);
        if (optional.isPresent())
        {
            PlayerMountDataMutable data = optional.get();
            attemptSetType(player, data, type);
        }
        else
        {
            messenger().error("You must first create a mount before your can change it's type!").tell(player);
        }
    }

    @Command(aliases = {"item", "i"}, parent = "mount", perm = Permissions.COMMAND_ITEM)
    public void item(@Caller Player player)
    {
        Optional<PlayerMountDataMutable> optional = player.get(PlayerMountDataMutable.class);
        if (optional.isPresent())
        {
            Optional<ItemStack> inHand = player.getItemInHand();
            if (inHand.isPresent())
            {
                String item = inHand.get().getItem().getName();
                if (optional.get().setItemType(item))
                {
                    player.offer(optional.get());
                    messenger().info("You have set your mount's item to: ").stress(item).tell(player);
                }
                return;
            }
            messenger().error("You must be holding an item to use this command!").tell(player);
            return;
        }
        messenger().error("You must first create a mount before your can change its properties!").tell(player);
    }

    @Command(aliases = {"normal", "n"}, parent = "mount speed", perm = Permissions.COMMAND_SPEED)
    public void speedNormal(@Caller Player player, @One("speed") double speed)
    {
        if (plugin.config().mountSpeeds.outsideOfRange(speed))
        {
            messenger().stress(speed)
                    .info(" is not within the allowed range: ")
                    .stress(plugin.config().mountSpeeds.getRange())
                    .tell(player);
            return;
        }

        if (set(player, MountKeys.MOVE_SPEED, speed))
        {
            messenger().info("Your mount's speed has been set to: ").stress(speed).tell(player);
        }
    }

    @Command(aliases = {"leash", "leashed", "l"}, parent = "mount speed", perm = Permissions.COMMAND_SPEED)
    public void speedLeash(@Caller Player player, @One("speed") double speed)
    {
        if (plugin.config().mountSpeeds.outsideOfRange(speed))
        {
            messenger().stress(speed)
                    .info(" is not within the allowed range: ")
                    .stress(plugin.config().mountSpeeds.getRange())
                    .tell(player);
            return;
        }

        if (set(player, MountKeys.LEASH_SPEED, speed))
        {
            messenger().info("Your mount's leashed speed has been set to: ").stress(speed).tell(player);
        }
    }

    @Command(aliases = {"canfly", "fly", "f"}, parent = "mount", perm = Permissions.COMMAND_FLY)
    public void fly(@Caller Player player, boolean fly)
    {
        if (set(player, MountKeys.CAN_FLY, fly))
        {
            messenger().info("Set your mount's ability to fly to: ").stress(fly).tell(player);
        }
    }

    @Command(aliases = {"invincible", "i"}, parent = "mount", perm = Permissions.COMMAND_INVINCIBLE)
    public void invincible(@Caller Player player, boolean invincible)
    {
        if (set(player, MountKeys.INVINCIBLE, invincible))
        {
            messenger().info("Set your mount's invincibility to: ").stress(invincible).tell(player);
        }
    }

    private void tell(Player player, Text.Builder... builders)
    {
        Text.Builder wrapper = Text.builder();
        for (Text.Builder b : builders)
        {
            wrapper.append(b.build());
        }
        player.sendMessage(wrapper.build());
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

    private boolean attemptSetType(Player player, PlayerMountDataMutable data, String type)
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
