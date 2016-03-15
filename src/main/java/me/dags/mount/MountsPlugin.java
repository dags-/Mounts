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

import com.google.inject.Inject;
import me.dags.commandbus.CommandBus;
import me.dags.dalib.config.ConfigLoader;
import me.dags.mount.commands.AdminCommands;
import me.dags.mount.commands.UserCommands;
import me.dags.mount.data.mount.MountDataBuilder;
import me.dags.mount.data.mount.MountDataImmutable;
import me.dags.mount.data.mount.MountDataMutable;
import me.dags.mount.data.player.PlayerMountDataBuilder;
import me.dags.mount.data.player.PlayerMountDataImmutable;
import me.dags.mount.data.player.PlayerMountDataMutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

@Plugin(name = "Mounts", id = "mounts", version = "1.0.0", description = "Turn mobs into mounts!", authors = "dags", url = "https://github.com/dags-/Mounts")
public class MountsPlugin
{
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configPath;
    private Config config = new Config();

    private final Logger logger = LoggerFactory.getLogger("Mounts");
    private final ConfigLoader configLoader = ConfigLoader.newInstance();

    @Listener
    public void preinit(GamePreInitializationEvent event)
    {
        logger.info("Registering MountData...");
        Sponge.getDataManager().register(PlayerMountDataMutable.class, PlayerMountDataImmutable.class, new PlayerMountDataBuilder());
        Sponge.getDataManager().register(MountDataMutable.class, MountDataImmutable.class, new MountDataBuilder());
    }

    @Listener
    public void init(GameInitializationEvent event)
    {
        reloadConfig();
        logger.info("Registering Commands...");
        CommandBus.newSilentInstance().register(new UserCommands(this)).register(new AdminCommands(this)).submit(this);
    }

    @Listener
    public void serverStop(GameStoppingServerEvent event)
    {
        logger.info("Clearing Mounts...");
        clearMounts();
    }

    @Listener
    public void use(InteractBlockEvent.Secondary event, @Root Player player)
    {
        if (!player.hasPermission(Permissions.MOUNT_USE))
        {
            return;
        }

        Optional<ItemStack> inHand = player.getItemInHand();
        if (inHand.isPresent() && !player.getVehicle().isPresent())
        {
            player.get(PlayerMountDataMutable.class)
                    .filter(data -> data.itemType() == inHand.get().getItem())
                    .flatMap(data -> data.createPlayMount(player))
                    .ifPresent(mount -> mount.mountPlayer(this));
        }
    }

    public Config config()
    {
        return config;
    }

    public void clearMounts()
    {
        Sponge.getServer().getWorlds()
                .forEach(w -> w.getEntities(e -> e.get(MountDataMutable.class).isPresent())
                        .forEach(Entity::remove));
    }

    public void reloadConfig()
    {
        logger.info("Loading Config...");
        Optional<Config> optional = configLoader.fromHocon(configPath, Config.class);
        config = optional.isPresent() ? optional.get() : new Config();
        configLoader.toHocon(config, configPath);
    }
}
