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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

public class PlayerMountDataCommon
{
    protected static final int DATA_VERSION = 1;

    protected String type = EntityTypes.PIG.getName();
    protected String item = ItemTypes.SADDLE.getName();
    protected boolean canFly = true;
    protected boolean invincible = true;
    protected double moveSpeed = 0.25D;
    protected double leashSpeed = 0.05D;

    protected transient EntityType entityType = EntityTypes.PIG;
    protected transient ItemType itemType = ItemTypes.SADDLE;

    protected PlayerMountDataCommon()
    {}

    protected PlayerMountDataCommon(PlayerMountDataCommon info)
    {
        this.type = info.type;
        this.item = info.item;
        this.canFly = info.canFly;
        this.invincible = info.invincible;
        this.moveSpeed = info.moveSpeed;
        this.leashSpeed = info.leashSpeed;
        this.entityType = info.entityType;
        this.itemType = info.itemType;
    }

    public PlayerMountDataCommon set(DataView dataContainer)
    {
        this.type = dataContainer.getString(MountKeys.TYPE.getQuery()).get();
        this.item = dataContainer.getString(MountKeys.ITEM.getQuery()).get();
        this.canFly = dataContainer.getBoolean(MountKeys.CAN_FLY.getQuery()).get();
        this.invincible = dataContainer.getBoolean(MountKeys.INVINCIBLE.getQuery()).get();
        this.moveSpeed = dataContainer.getDouble(MountKeys.MOVE_SPEED.getQuery()).get();
        this.leashSpeed = dataContainer.getDouble(MountKeys.LEASH_SPEED.getQuery()).get();
        Optional<EntityType> t = Sponge.getRegistry().getType(EntityType.class, this.type);
        Optional<ItemType> i = Sponge.getRegistry().getType(ItemType.class, this.item);
        this.entityType = t.isPresent() ? t.get() : entityType;
        this.itemType = i.isPresent() ? i.get() : itemType;
        return this;
    }

    public boolean canFly()
    {
        return canFly;
    }

    public boolean isInvincible()
    {
        return invincible;
    }

    public double getMoveSpeed()
    {
        return moveSpeed;
    }

    public double getLeashSpeed()
    {
        return leashSpeed;
    }

    public void setEntityType(EntityType type)
    {
        this.entityType = type;
    }

    public void setItemType(ItemType type)
    {
        this.itemType = type;
    }

    public Value<String> type()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.TYPE, "pig", type);
    }

    public Value<String> item()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.ITEM, "lead", item);
    }

    public Value<Boolean> fly()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.CAN_FLY, false, canFly);
    }

    public Value<Boolean> invincible()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.INVINCIBLE, false, invincible);
    }

    public Value<Double> moveSpeed()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.MOVE_SPEED, 0.2D, moveSpeed);
    }

    public Value<Double> leashSpeed()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.LEASH_SPEED, 0.1D, leashSpeed);
    }

    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(MountKeys.TYPE, type)
                .set(MountKeys.ITEM, item)
                .set(MountKeys.CAN_FLY, canFly)
                .set(MountKeys.INVINCIBLE, invincible)
                .set(MountKeys.MOVE_SPEED, moveSpeed)
                .set(MountKeys.LEASH_SPEED, leashSpeed);
    }

    public static Optional<PlayerMountDataCommon> fromContainer(DataView dataContainer)
    {
        if (!dataContainer.contains(
                MountKeys.TYPE.getQuery(),
                MountKeys.ITEM.getQuery(),
                MountKeys.CAN_FLY.getQuery(),
                MountKeys.INVINCIBLE.getQuery(),
                MountKeys.MOVE_SPEED.getQuery(),
                MountKeys.LEASH_SPEED.getQuery()))
        {
            return Optional.empty();
        }
        return Optional.of(new PlayerMountDataCommon().set(dataContainer));
    }
}
