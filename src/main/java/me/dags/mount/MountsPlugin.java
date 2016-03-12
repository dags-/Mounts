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
import me.dags.mount.data.MountDataBuilder;
import me.dags.mount.data.MountDataImmutable;
import me.dags.mount.data.MountDataMutable;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.AbstractConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

@Plugin(name = "Mounts", id = "me.dags.mounts", version = "1.0")
public class MountsPlugin
{
    private final Logger logger = LoggerFactory.getLogger("Mounts");

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Listener
    public void preinit(GamePreInitializationEvent event)
    {
        logger.info("Registering MountData...");
        Sponge.getDataManager().register(MountDataMutable.class, MountDataImmutable.class, new MountDataBuilder());
    }

    @Listener
    public void init(GameInitializationEvent event)
    {
        logger.info("Registering Commands...");
        CommandBus.newInstance(logger).register(mountCommands()).submit(this);
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
        if (inHand.isPresent() && !player.getVehicle().isPresent())
        {
            player.get(MountDataMutable.class)
                    .filter(data -> data.itemType() == inHand.get().getItem())
                    .flatMap(data -> data.getPlayerMount(player))
                    .ifPresent(mount -> mount.mountPlayer(this));
        }
    }

    private MountCommands mountCommands()
    {
        Path commandsPath = configDir.resolve("commands.cfg");
        Optional<MountCommands> optional = fromHocon(commandsPath, MountCommands.class);
        if (optional.isPresent())
        {
            return optional.get().setPath(commandsPath);
        }
        MountCommands commands = new MountCommands().setPath(commandsPath);
        toHocon(commands, commandsPath);
        return commands;
    }

    public static void clearMounts()
    {
        Sponge.getServer().getWorlds()
                .forEach(w -> w.getEntities(e -> {
                    Optional<Text> name = e.get(Keys.DISPLAY_NAME);
                    return name.isPresent() && name.get().toPlain().endsWith("'s Epic Mount!");
                }).forEach(Entity::remove));
    }

    public static <T> Optional<T> fromHocon(Path path, Class<T> type)
    {
        if (!Files.exists(path))
        {
            return Optional.empty();
        }
        return from(HoconConfigurationLoader.builder().setPath(path).build(), type);
    }

    public static <T> boolean toHocon(T object, Path path)
    {

        return to(HoconConfigurationLoader.builder().setPath(path).build(), path, object);
    }

    private static <T> Optional<T> from(AbstractConfigurationLoader<?> loader, Class<T> type)
    {
        try
        {
            ConfigurationNode node = loader.load();
            T t = ObjectMapper.forClass(type).bindToNew().populate(node);
            if (t != null)
            {
                return Optional.of(t);
            }
        }
        catch (IOException | ObjectMappingException e)
        {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static <T> boolean to(AbstractConfigurationLoader<?> loader, Path path, T object)
    {
        try
        {
            Files.createDirectories(path.getParent());
            ConfigurationNode node = loader.createEmptyNode();
            ObjectMapper.forObject(object).populate(node);
            loader.save(node);
            return true;
        }
        catch (IOException | ObjectMappingException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
