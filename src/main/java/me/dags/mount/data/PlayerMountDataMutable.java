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

package me.dags.mount.data;

import com.flowpowered.math.vector.Vector3d;
import me.dags.mount.Mount;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

public class PlayerMountDataMutable extends AbstractData<PlayerMountDataMutable, PlayerMountDataImmutable>
{
    private final PlayerMountDataCommon common;

    public PlayerMountDataMutable()
    {
        this(new PlayerMountDataCommon());
    }

    public PlayerMountDataMutable(PlayerMountDataCommon info)
    {
        this.common = info;
        registerGettersAndSetters();
    }

    public Optional<Mount> createPlayMount(Player player)
    {
        Optional<Living> optional = create(common.entityType, Living.class, player.getLocation());
        if (optional.isPresent())
        {
            return Optional.of(new Mount(player, optional.get(), common));
        }
        return Optional.empty();
    }

    public ItemType itemType()
    {
        return common.itemType;
    }

    public boolean setEntityType(String type)
    {
        Optional<EntityType> entityType = Sponge.getRegistry().getType(EntityType.class, type);
        if (entityType.isPresent())
        {
            this.set(MountKeys.TYPE, type);
            this.common.setEntityType(entityType.get());
            return true;
        }
        return false;
    }

    public boolean setItemType(String type)
    {
        Optional<ItemType> itemType = Sponge.getRegistry().getType(ItemType.class, type);
        if (itemType.isPresent())
        {
            this.set(MountKeys.ITEM, type);
            this.common.setItemType(itemType.get());
            return true;
        }
        return false;
    }

    @Override
    protected void registerGettersAndSetters()
    {
        registerFieldGetter(MountKeys.TYPE, () -> this.common.type);
        registerFieldGetter(MountKeys.ITEM, () -> this.common.item);
        registerFieldGetter(MountKeys.CAN_FLY, () -> this.common.canFly);
        registerFieldGetter(MountKeys.INVINCIBLE, () -> this.common.invincible);
        registerFieldGetter(MountKeys.MOVE_SPEED, () -> this.common.moveSpeed);
        registerFieldGetter(MountKeys.LEASH_SPEED, () -> this.common.leashSpeed);

        registerFieldSetter(MountKeys.TYPE, e -> this.common.type = e);
        registerFieldSetter(MountKeys.ITEM, r -> this.common.item = r);
        registerFieldSetter(MountKeys.CAN_FLY, f -> this.common.canFly = f);
        registerFieldSetter(MountKeys.INVINCIBLE, i -> this.common.invincible = i);
        registerFieldSetter(MountKeys.MOVE_SPEED, s -> this.common.moveSpeed = s);
        registerFieldSetter(MountKeys.LEASH_SPEED, s -> this.common.leashSpeed = s);

        registerKeyValue(MountKeys.TYPE, common::type);
        registerKeyValue(MountKeys.ITEM, common::item);
        registerKeyValue(MountKeys.CAN_FLY, common::fly);
        registerKeyValue(MountKeys.INVINCIBLE, common::invincible);
        registerKeyValue(MountKeys.MOVE_SPEED, common::moveSpeed);
        registerKeyValue(MountKeys.LEASH_SPEED, common::leashSpeed);
    }

    @Override
    public Optional<PlayerMountDataMutable> fill(DataHolder dataHolder, MergeFunction mergeFunction)
    {
        return Optional.empty();
    }

    @Override
    public Optional<PlayerMountDataMutable> from(DataContainer dataContainer)
    {
        return from(dataContainer);
    }

    @Override
    public PlayerMountDataMutable copy()
    {
        return new PlayerMountDataMutable(common);
    }

    @Override
    public PlayerMountDataImmutable asImmutable()
    {
        return new PlayerMountDataImmutable(common);
    }

    @Override
    public int compareTo(PlayerMountDataMutable o)
    {
        return 0;
    }

    @Override
    public int getContentVersion()
    {
        return PlayerMountDataCommon.DATA_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return common.toContainer();
    }

    public static <T extends Entity> Optional<T> create(EntityType entityType, Class<T> type, Location<World> location)
    {
        return create(entityType, type, location.getExtent(), location.getPosition());
    }

    private static <T extends Entity> Optional<T> create(EntityType entityType, Class<T> type, World world, Vector3d vector3d)
    {
        Optional<Entity> entity = world.createEntity(entityType, vector3d);
        if (entity.isPresent() && type.isInstance(entity.get()))
        {
            T t = type.cast(entity.get());
            return Optional.of(t);
        }
        return Optional.empty();
    }
}
