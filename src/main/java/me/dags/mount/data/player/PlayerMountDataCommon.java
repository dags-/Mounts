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

package me.dags.mount.data.player;

import me.dags.mount.Config;
import me.dags.mount.data.MountKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Queries;
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
    static final int DATA_VERSION = 1;

    String type = EntityTypes.PIG.getName();
    String item = ItemTypes.SADDLE.getName();
    String leash = ItemTypes.LEAD.getName();
    // TODO
    String name = "Dennis";

    boolean canFly = true;
    boolean invincible = true;
    double moveSpeed = 0.25D;
    double leashSpeed = 0.05D;

    transient EntityType entityType = EntityTypes.PIG;
    transient ItemType spawnItem = ItemTypes.SADDLE;
    private transient ItemType leashItem = ItemTypes.LEAD;

    PlayerMountDataCommon()
    {}

    PlayerMountDataCommon(PlayerMountDataCommon info)
    {
        this.type = info.type;
        this.item = info.item;
        this.leash = info.leash;
        this.canFly = info.canFly;
        this.invincible = info.invincible;
        this.moveSpeed = info.moveSpeed;
        this.leashSpeed = info.leashSpeed;
        this.entityType = info.entityType;
        this.spawnItem = info.spawnItem;
        this.leashItem = info.leashItem;
    }

    private PlayerMountDataCommon set(DataView dataContainer)
    {
        this.type = dataContainer.getString(MountKeys.TYPE.getQuery()).get();
        this.item = dataContainer.getString(MountKeys.SPAWN_ITEM.getQuery()).get();
        this.canFly = dataContainer.getBoolean(MountKeys.CAN_FLY.getQuery()).get();
        this.invincible = dataContainer.getBoolean(MountKeys.INVINCIBLE.getQuery()).get();
        this.moveSpeed = dataContainer.getDouble(MountKeys.MOVE_SPEED.getQuery()).get();
        this.leashSpeed = dataContainer.getDouble(MountKeys.LEASH_SPEED.getQuery()).get();
        this.entityType = Sponge.getRegistry().getType(EntityType.class, type).orElse(entityType);
        this.spawnItem = Sponge.getRegistry().getType(ItemType.class, this.item).orElse(spawnItem);
        return this;
    }

    public void clampSpeeds(Config.MountSpeeds speeds)
    {
        moveSpeed = speeds.clamp(moveSpeed);
        leashSpeed = speeds.clamp(leashSpeed);
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

    public String name()
    {
        return name;
    }

    public ItemType getSpawnItem()
    {
        return spawnItem;
    }

    public ItemType getLeashItem()
    {
        return leashItem;
    }

    void setEntityType(EntityType type)
    {
        this.entityType = type;
    }

    void setSpawnItem(ItemType type)
    {
        this.spawnItem = type;
    }

    void setLeashItem(ItemType type)
    {
        this.leashItem = type;
    }

    Value<String> type()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.TYPE, "pig", type);
    }

    Value<String> spawnItem()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.SPAWN_ITEM, "saddle", item);
    }

    Value<String> leashItem()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.LEASH_ITEM, "lead", leash);
    }

    Value<Boolean> fly()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.CAN_FLY, false, canFly);
    }

    Value<Boolean> invincible()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.INVINCIBLE, false, invincible);
    }

    Value<Double> moveSpeed()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.MOVE_SPEED, 0.2D, moveSpeed);
    }

    Value<Double> leashSpeed()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.LEASH_SPEED, 0.1D, leashSpeed);
    }

    DataContainer toContainer()
    {
        return new MemoryDataContainer()
                .set(Queries.CONTENT_VERSION, DATA_VERSION)
                .set(MountKeys.TYPE, type)
                .set(MountKeys.SPAWN_ITEM, item)
                .set(MountKeys.LEASH_ITEM, leash)
                .set(MountKeys.CAN_FLY, canFly)
                .set(MountKeys.INVINCIBLE, invincible)
                .set(MountKeys.MOVE_SPEED, moveSpeed)
                .set(MountKeys.LEASH_SPEED, leashSpeed);
    }

    static Optional<PlayerMountDataCommon> fromContainer(DataView dataContainer)
    {
        if (!dataContainer.contains(
                MountKeys.TYPE.getQuery(),
                MountKeys.SPAWN_ITEM.getQuery(),
                MountKeys.LEASH_ITEM.getQuery(),
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
