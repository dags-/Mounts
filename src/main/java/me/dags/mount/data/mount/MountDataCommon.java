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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

/**
 * Currently just marks an entity as a mount so that it can be cleared via the purge function.
 * Could hold some other info/data about the mount if future me wants to make things more fancy.
 */
public class MountDataCommon
{
    protected static final int CONTENT_VERSION = 1;

    protected final boolean dummy = true;

    protected MountDataCommon copy()
    {
        MountDataCommon common = new MountDataCommon();
        return common;
    }

    protected MountDataCommon set(DataView dataContainer)
    {
        return this;
    }

    public Value<Boolean> dummy()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MountKeys.IS_MOUNT, true, dummy);
    }

    public DataContainer toContainer()
    {
        return new MemoryDataContainer()
                .set(Queries.CONTENT_VERSION, CONTENT_VERSION)
                .set(MountKeys.IS_MOUNT, dummy);
    }

    public static Optional<MountDataCommon> fromContainer(DataView dataContainer)
    {
        if (!dataContainer.contains(MountKeys.IS_MOUNT.getQuery()))
        {
            return Optional.empty();
        }
        return Optional.of(new MountDataCommon().set(dataContainer));
    }
}
