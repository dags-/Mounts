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
import me.dags.dalib.commands.CommandMessenger;
import me.dags.mount.MountsPlugin;
import me.dags.mount.Permissions;
import me.dags.mount.data.mount.MountDataMutable;
import me.dags.mount.data.player.PlayerMountDataMutable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

/**
 * @author dags <dags@dags.me>
 */

public class AdminCommands
{
    private final MountsPlugin plugin;

    public AdminCommands(MountsPlugin plugin)
    {
        this.plugin = plugin;
    }

    private CommandMessenger.Builder messenger()
    {
        return plugin.config().messenger().builder();
    }

    @Command(aliases = {"purge", "p"}, parent = "mount", perm = Permissions.COMMAND_PURGE)
    public void purge(@Caller CommandSource source)
    {
        messenger().info("Purging all mounts...").tell(source);
        plugin.clearMounts();
    }

    @Command(aliases = {"purge", "p"}, parent = "mount", perm = Permissions.COMMAND_PURGE)
    public void purgeUser(@Caller CommandSource source, @One("target") Player target)
    {
        if (target.getVehicle().isPresent() && target.getVehicle().get().get(MountDataMutable.class).isPresent())
        {
            messenger().info("Purging ").stress(target.getName()).info("'s mount...").tell(source);
            messenger().stress(source.getName()).info(" purged your mount!").tell(target);
            target.getVehicle().get().remove();
        }
    }

    @Command(aliases = {"reload", "r"}, parent = "mount", perm = Permissions.COMMAND_RELOAD)
    public void reload(@Caller CommandSource source)
    {
        messenger().info("Reloading...").tell(source);
        plugin.reloadConfig();
    }


    @Command(aliases = {"reset", "r"}, parent = "mount", perm = Permissions.COMMAND_RESET_OTHER)
    public void resetOther(@Caller Player player, @One("target") Player target)
    {
        if (!target.get(PlayerMountDataMutable.class).isPresent())
        {
            messenger().stress(target.getName()).error(" does not have a mount to reset!").tell(player);
            return;
        }

        target.remove(PlayerMountDataMutable.class);
        messenger().stress(player.getName()).info(" reset your mount! Use '").stress("/mount create").info("' to create a new one!").tell(target);
        messenger().info("You reset ").stress(target.getName()).info("'s mount!").tell(target);
    }
}
