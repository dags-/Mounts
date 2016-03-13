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
import me.dags.mount.data.PlayerMountDataBuilder;
import me.dags.mount.data.PlayerMountDataImmutable;
import me.dags.mount.data.PlayerMountDataMutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
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
import org.spongepowered.api.text.Text;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

@Plugin(name = "Mounts", id = "dags.mounts", version = "1.0.0", description = "Turn mobs into mounts!", authors = "dags", url = "https://github.com/dags-/Mounts")
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
    }

    @Listener
    public void init(GameInitializationEvent event)
    {
        reloadConfig();
        logger.info("Registering Commands...");
        CommandBus.newInstance(logger).register(new MountCommands(this)).submit(this);
    }

    @Listener
    public void serverStop(GameStoppingServerEvent event)
    {
        logger.info("Clearing Mounts before server stop...");
        clearMounts();
    }

    @Listener
    public void use(InteractBlockEvent.Secondary event, @Root Player player)
    {
        Optional<ItemStack> inHand = player.getItemInHand();
        if (player.hasPermission(Permissions.MOUNT_USE) && inHand.isPresent() && !player.getVehicle().isPresent())
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
                .forEach(w -> w.getEntities(e -> {
                    Optional<Text> name = e.get(Keys.DISPLAY_NAME);
                    return name.isPresent() && name.get().toPlain().endsWith("'s Epic Mount!");
                }).forEach(Entity::remove));
    }

    public void reloadConfig()
    {
        Optional<Config> optional = configLoader.fromHocon(configPath, Config.class);
        if (optional.isPresent())
        {
            this.config = optional.get();
            return;
        }
        configLoader.toHocon(this.config = new Config(), configPath);
    }
}
