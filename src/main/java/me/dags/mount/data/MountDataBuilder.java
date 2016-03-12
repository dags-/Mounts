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

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

public class MountDataBuilder implements DataManipulatorBuilder<MountDataMutable, MountDataImmutable>
{
    @Override
    public MountDataMutable create()
    {
        return new MountDataMutable();
    }

    @Override
    public Optional<MountDataMutable> createFrom(DataHolder dataHolder)
    {
        return Optional.of(dataHolder.get(MountDataMutable.class).orElse(create()));
    }

    @Override
    public Optional<MountDataMutable> build(DataView dataView)
    {
        Optional<MountDataCommon> mountInfo = MountDataCommon.from(dataView);
        if (mountInfo.isPresent())
        {
            return Optional.of(new MountDataMutable(mountInfo.get()));
        }
        return Optional.empty();
    }
}
