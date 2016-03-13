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

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import me.dags.mount.data.PlayerMountDataCommon;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author dags <dags@dags.me>
 */

public class Mount implements Consumer<Task>
{
    private final Player player;
    private final Living vehicle;
    private final PlayerMountDataCommon mountData;
    private final Handler movementHandler;

    private transient Optional<Task> task = Optional.empty();

    public Mount(Player player, Living mount, PlayerMountDataCommon data)
    {
        this.player = player;
        this.vehicle = mount;
        this.mountData = data;
        this.movementHandler = getHandler(mount.getType(), mountData.canFly());
    }

    public void mountPlayer(Object plugin)
    {
        Cause cause = Cause.source(SpawnCause.builder()
                .type(SpawnTypes.PLUGIN).build())
                .named(NamedCause.simulated(player)).build();

        if (player.getWorld().spawnEntity(vehicle, cause))
        {
            vehicle.offer(Keys.DISPLAY_NAME, Text.of(player.getName() + "'s Epic Mount!"));
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(1L)
                    .execute(() -> {
                        player.setVehicle(vehicle);
                        vehicle.offer(Keys.TAMED_OWNER, Optional.of(player.getUniqueId()));
                        Sponge.getScheduler().createTaskBuilder()
                                .delayTicks(1L)
                                .intervalTicks(1L)
                                .execute(this)
                                .submit(plugin);
                    }).submit(plugin);
            Sponge.getEventManager().registerListeners(plugin, this);
        }
    }

    private void dismount()
    {
        Sponge.getEventManager().unregisterListeners(this);
        vehicle.remove();
        if (task.isPresent())
        {
            task.get().cancel();
        }
    }

    @Listener
    public void onMountDamaged(DamageEntityEvent event)
    {
        if (event.getTargetEntity() == vehicle)
        {
            if (mountData.isInvincible())
            {
                event.setCancelled(true);
            }
            else if (event.willCauseDeath())
            {
                dismount();
            }
        }
    }

    @Listener
    public void destroyBlocks(ChangeBlockEvent e, @Root Living living)
    {
        if (living == vehicle)
        {
            e.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect e)
    {
        if (e.getTargetEntity() == player)
        {
            dismount();
        }
    }

    @Override
    public void accept(Task taskIn)
    {
        if (!task.isPresent())
        {
            task = Optional.of(taskIn);
        }
        if (!vehicle.getPassenger().isPresent() || !player.getVehicle().isPresent())
        {
            dismount();
            return;
        }
        movementHandler.move(this);
    }

    private interface Handler
    {
        void move(Mount mount);
    }

    private static Handler getHandler(EntityType type, boolean canFly)
    {
        return type == EntityTypes.ENDER_DRAGON ? flyingDragon() : canFly ? flyingEntity() : other();
    }

    private static Handler flyingEntity()
    {
        return mount -> {
            Optional<ItemStack> inHand = mount.player.getItemInHand();
            double speed = inHand.isPresent() && inHand.get().getItem() == ItemTypes.LEAD ? mount.mountData.getLeashSpeed() : mount.mountData.getMoveSpeed();
            Vector3d rotation = mount.player.getHeadRotation();
            Vector3d velocity = rotationToVelocity(rotation, speed, speed);
            mount.vehicle.setVelocity(velocity);
            mount.vehicle.setRotation(rotation);
            mount.vehicle.setHeadRotation(rotation);
        };
    }

    private static Handler flyingDragon()
    {
        return mount -> {
            Optional<ItemStack> inHand = mount.player.getItemInHand();
            double speed = inHand.isPresent() && inHand.get().getItem() == ItemTypes.LEAD ? mount.mountData.getLeashSpeed() : mount.mountData.getMoveSpeed();
            Vector3d rotation = mount.player.getRotation();
            Vector3d velocity = rotationToVelocity(rotation, speed, speed);
            mount.vehicle.setVelocity(velocity);
            mount.vehicle.setRotation(rotation.add(0, 180, 0));
        };
    }

    private static Handler other()
    {
        return mount -> {
            Optional<ItemStack> inHand = mount.player.getItemInHand();
            double speed = inHand.isPresent() && inHand.get().getItem() == ItemTypes.LEAD ? mount.mountData.getLeashSpeed() : mount.mountData.getMoveSpeed();

            Vector3d rotation = mount.player.getHeadRotation();
            Vector3d velocity = rotationToVelocity(mount.player.getRotation(), speed, 0).add(0, mount.vehicle.getVelocity().getY(), 0);
            Vector3d ahead = direction(rotation).mul(1, 0, 1);
            Optional<PassableProperty> passable = mount.vehicle.getLocation().add(ahead).getProperty(PassableProperty.class);

            if (passable.filter(p -> p.getValue() != null && !p.getValue()).isPresent() && canJump(mount.vehicle))
            {
                velocity = velocity.add(0, 0.15 + (speed * 0.15), 0);
            }

            mount.vehicle.setVelocity(velocity);
            mount.vehicle.setRotation(rotation);
        };
    }

    private static boolean canJump(Living mount)
    {
        if (mount.isOnGround())
        {
            return true;
        }
        Optional<PassableProperty> p1 = mount.getLocation()
                .getRelative(Direction.DOWN)
                .getRelative(Direction.DOWN)
                .getProperty(PassableProperty.class);

        return p1.isPresent() && p1.get().getValue() != null && !p1.get().getValue();
    }

    private static Vector3d direction(Vector3d rotation)
    {
        return Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
    }

    private static Vector3d rotationToVelocity(Vector3d rotation, double horizModifier, double vertModifier)
    {
        return direction(rotation).mul(horizModifier, vertModifier, horizModifier);
    }
}
