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
import me.dags.mount.data.MountDataMutable;
import me.dags.mount.data.MountKeys;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

@ConfigSerializable
public class MountCommands
{
    @Setting (comment = "Set the allowed range of speeds for Mounts")
    private MountSpeeds speeds = new MountSpeeds();
    @Setting (comment = "Set command message colors")
    private CommandColors colors = new CommandColors();

    private transient Path path = Paths.get("");

    protected MountCommands setPath(Path path)
    {
        this.path = path;
        return this;
    }

    @Command(aliases = {"reload", "r"}, parent = "mount", perm = Permissions.COMMAND_RELOAD)
    public void reload(@Caller Player player)
    {
        tell(player, text("Reloading..."));
        Optional<MountCommands> commands = MountsPlugin.fromHocon(path, MountCommands.class);
        if (commands.isPresent())
        {
            this.speeds = commands.get().speeds;
            this.colors = commands.get().colors;
        }
    }

    @Command(aliases = {"purge", "p"}, parent = "mount", desc = "Purge all spawned mounts", perm = Permissions.COMMAND_PURGE)
    public void purge(@Caller Player player)
    {
        tell(player, text("Purging mounts..."));
        MountsPlugin.clearMounts();
    }

    @Command(aliases = {"create", "c"}, parent = "mount", desc = "Create a new mount", perm = Permissions.COMMAND_CREATE)
    public void create0(@Caller Player player)
    {
        create1(player, "pig");
    }

    @Command(aliases = {"create", "c"}, parent = "mount", desc = "Create a new mount", perm = Permissions.COMMAND_CREATE)
    public void create1(@Caller Player player, @One("entity") String type)
    {
        if (!player.get(MountDataMutable.class).isPresent())
        {
            tell(player, text("See '"), stress("/help mount"), text("' for more on setting up your mount!"));
        }

        MountDataMutable data = new MountDataMutable();
        if (attemptSetType(player, data, type))
        {
            tell(player, text("Successfully created your new mount!"));
        }
    }

    @Command(aliases = {"type", "t"}, parent = "mount", desc = "Change your mount to a different creature", perm = Permissions.COMMAND_TYPE)
    public void type(@Caller Player player, @One("entity") String type)
    {
        Optional<MountDataMutable> optional = player.get(MountDataMutable.class);
        if (optional.isPresent())
        {
            MountDataMutable data = optional.get();
            attemptSetType(player, data, type);
        }
        else
        {
            tell(player, err("You must first create a mount before your can change it's type!"));
        }
    }

    @Command(aliases = {"item", "i"}, parent = "mount", desc = "Change the item you use to spawn your mount", perm = Permissions.COMMAND_ITEM)
    public void item(@Caller Player player)
    {
        Optional<MountDataMutable> optional = player.get(MountDataMutable.class);
        if (optional.isPresent())
        {
            Optional<ItemStack> inHand = player.getItemInHand();
            if (inHand.isPresent())
            {
                String item = inHand.get().getItem().getName();
                if (optional.get().setItemType(item))
                {
                    player.offer(optional.get());
                    tell(player, text("You have set your mount's item to: "), stress(item));
                }
                return;
            }
            tell(player, err("You must be holding an item to use this command!"));
            return;
        }
        tell(player, err("You must first create a mount before your can change its properties!"));
    }

    @Command(aliases = {"normal", "l"}, parent = "mount speed", desc = "Set your mount's speed", perm = Permissions.COMMAND_SPEED)
    public void speedNormal(@Caller Player player, @One("speed") double speed)
    {
        if (speeds.outsideOfRange(speed))
        {
            tell(player, stress(speed), err(" is not within the allowed range: "), stress(speeds.getRange()));
            return;
        }

        if (set(player, MountKeys.MOVE_SPEED, speed))
        {
            tell(player, text("Your mount's speed has been set to: "), stress(speed));
        }
    }

    @Command(aliases = {"leash", "leashed", "l"}, parent = "mount speed", desc = "Set your mount's leashed speed", perm = Permissions.COMMAND_SPEED)
    public void speedLeash(@Caller Player player, @One("speed") double speed)
    {
        if (speeds.outsideOfRange(speed))
        {
            tell(player, stress(speed), err(" is not within the allowed range: "), stress(speeds.getRange()));
            return;
        }

        if (set(player, MountKeys.LEASH_SPEED, speed))
        {
            tell(player, text("Your mount's leashed speed has been set to: "), stress(speed));
        }
    }

    @Command(aliases = {"canfly", "fly", "f"}, parent = "mount", desc = "Set whether or not your mount can fly", perm = Permissions.COMMAND_FLY)
    public void fly(@Caller Player player, @One boolean fly)
    {
        if (set(player, MountKeys.CAN_FLY, fly))
        {
            tell(player, text("Set your mount's ability to fly to: "), stress(fly));
        }
    }

    @Command(aliases = {"invincible", "i"}, parent = "mount", desc = "Set whether your mount is invincible or not", perm = Permissions.COMMAND_INVINCIBLE)
    public void invincible(@Caller Player player, @One boolean invincible)
    {
        if (set(player, MountKeys.INVINCIBLE, invincible))
        {
            tell(player, text("Set your mount's invincibility to: "), stress(invincible));
        }
    }

    private Text.Builder text(Object message)
    {
        return Text.builder(message.toString()).color(colors.textColor);
    }

    private Text.Builder stress(Object message)
    {
        return Text.builder(message.toString()).color(colors.highlightColor);
    }

    private Text.Builder err(Object message)
    {
        return Text.builder(message.toString()).color(colors.errorColor);
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
        Optional<MountDataMutable> optional = player.get(MountDataMutable.class);
        if (optional.isPresent())
        {
            MountDataMutable data = optional.get();
            data.set(key, value);
            player.offer(data);
            return true;
        }
        tell(player, err("You must first create a mount before your can change its properties!"));
        return false;
    }

    private boolean attemptSetType(Player player, MountDataMutable data, String type)
    {
        if (Permissions.allowedType(player, type))
        {
            if (data.setEntityType(type))
            {
                player.offer(data);
                tell(player, text("Set your mount to: "), stress(type));
                return true;
            }
            tell(player, stress(type), err(" is not a valid mount type!"));
            return true;
        }
        tell(player, err("You do not have permission for mount type: "), stress(type));
        return false;
    }

    @ConfigSerializable
    public static class MountSpeeds
    {
        @Setting
        private double minSpeed = 0.01D;
        @Setting
        private double maxSpeed = 5.0D;

        private boolean outsideOfRange(double test)
        {
            return test < minSpeed || test > maxSpeed;
        }

        private String getRange()
        {
            return minSpeed + " < x < " + maxSpeed;
        }
    }

    @ConfigSerializable
    public static class CommandColors
    {
        @Setting
        private TextColor textColor = TextColors.DARK_AQUA;
        @Setting
        private TextColor highlightColor = TextColors.DARK_PURPLE;
        @Setting
        private TextColor errorColor = TextColors.GRAY;
    }
}
