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

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.BaseValue;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

public class PlayerMountDataImmutable extends AbstractImmutableData<PlayerMountDataImmutable, PlayerMountDataMutable>
{
    protected final PlayerMountDataCommon common;

    public PlayerMountDataImmutable()
    {
        this(new PlayerMountDataCommon());
    }

    public PlayerMountDataImmutable(PlayerMountDataCommon info)
    {
        this.common = new PlayerMountDataCommon(info);
        registerGetters();
    }

    @Override
    protected void registerGetters()
    {
        registerFieldGetter(MountKeys.TYPE, () -> this.common.type);
        registerFieldGetter(MountKeys.SPAWN_ITEM, () -> this.common.item);
        registerFieldGetter(MountKeys.LEASH_ITEM, () -> this.common.leash);
        registerFieldGetter(MountKeys.CAN_FLY, () -> this.common.canFly);
        registerFieldGetter(MountKeys.INVINCIBLE, () -> this.common.invincible);
        registerFieldGetter(MountKeys.MOVE_SPEED, () -> this.common.moveSpeed);
        registerFieldGetter(MountKeys.LEASH_SPEED, () -> this.common.leashSpeed);

        registerKeyValue(MountKeys.TYPE, () -> common.type().asImmutable());
        registerKeyValue(MountKeys.SPAWN_ITEM, () -> common.spawnItem().asImmutable());
        registerKeyValue(MountKeys.LEASH_ITEM, () -> common.leashItem().asImmutable());
        registerKeyValue(MountKeys.CAN_FLY, () -> common.fly().asImmutable());
        registerKeyValue(MountKeys.INVINCIBLE, () -> common.invincible().asImmutable());
        registerKeyValue(MountKeys.MOVE_SPEED, () -> common.moveSpeed().asImmutable());
        registerKeyValue(MountKeys.LEASH_SPEED, () -> common.leashSpeed().asImmutable());
    }

    @Override
    public <E> Optional<PlayerMountDataImmutable> with(Key<? extends BaseValue<E>> key, E e)
    {
        return Optional.empty();
    }

    @Override
    public PlayerMountDataMutable asMutable()
    {
        return new PlayerMountDataMutable(common);
    }

    @Override
    public int compareTo(PlayerMountDataImmutable o)
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
}
