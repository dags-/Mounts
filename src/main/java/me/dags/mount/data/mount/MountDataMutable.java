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

package me.dags.mount.data.mount;

import me.dags.mount.data.MountKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;

import java.util.Optional;

public class MountDataMutable extends AbstractData<MountDataMutable, MountDataImmutable>
{
    private final MountDataCommon mountData;

    public MountDataMutable()
    {
        this(new MountDataCommon());
    }

    protected MountDataMutable(MountDataCommon mountData)
    {
        this.mountData = mountData;
        registerGettersAndSetters();
    }

    public MountDataCommon getData()
    {
        return this.mountData;
    }

    @Override
    public MountDataImmutable asImmutable()
    {
        return new MountDataImmutable(mountData.copy());
    }

    @Override
    public MountDataMutable copy()
    {
        return new MountDataMutable(mountData.copy());
    }

    @Override
    public Optional<MountDataMutable> fill(DataHolder holder, MergeFunction merge)
    {
        return Optional.empty();
    }

    @Override
    public Optional<MountDataMutable> from(DataContainer data)
    {
        Optional<MountDataCommon> optional = MountDataCommon.fromContainer(data);
        if (optional.isPresent())
        {
            return Optional.of(new MountDataMutable(optional.get()));
        }
        return Optional.empty();
    }

    @Override
    public int compareTo(MountDataMutable o)
    {
        return 0;
    }

    @Override
    public int getContentVersion()
    {
        return MountDataCommon.CONTENT_VERSION;
    }

    @Override
    protected void registerGettersAndSetters()
    {
        registerFieldGetter(MountKeys.IS_MOUNT, () -> mountData.dummy);
        registerFieldSetter(MountKeys.IS_MOUNT, (s) -> {});
        registerKeyValue(MountKeys.IS_MOUNT, mountData::dummy);
    }

    @Override
    public DataContainer toContainer()
    {
        return mountData.toContainer();
    }
}
