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

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

public class PlayerMountDataBuilder extends AbstractDataBuilder<PlayerMountDataMutable> implements DataManipulatorBuilder<PlayerMountDataMutable, PlayerMountDataImmutable>
{
    public PlayerMountDataBuilder()
    {
        super(PlayerMountDataMutable.class, PlayerMountDataCommon.DATA_VERSION);
    }

    @Override
    public PlayerMountDataMutable create()
    {
        return new PlayerMountDataMutable();
    }

    @Override
    public Optional<PlayerMountDataMutable> createFrom(DataHolder dataHolder)
    {
        return Optional.of(dataHolder.get(PlayerMountDataMutable.class).orElse(create()));
    }

    @Override
    protected Optional<PlayerMountDataMutable> buildContent(DataView container) throws InvalidDataException
    {
        Optional<PlayerMountDataCommon> mountInfo = PlayerMountDataCommon.fromContainer(container);
        if (mountInfo.isPresent())
        {
            return Optional.of(new PlayerMountDataMutable(mountInfo.get()));
        }
        return Optional.empty();
    }
}
