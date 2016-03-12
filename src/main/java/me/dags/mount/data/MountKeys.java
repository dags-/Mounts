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

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

/**
 * @author dags <dags@dags.me>
 */

public class MountKeys
{
    public static final Key<Value<String>> TYPE = KeyFactory.makeSingleKey(String.class, Value.class, DataQuery.of("ENTITY_TYPE"));
    public static final Key<Value<String>> ITEM = KeyFactory.makeSingleKey(String.class, Value.class, DataQuery.of("SPAWN_ITEM"));
    public static final Key<Value<Boolean>> CAN_FLY = KeyFactory.makeSingleKey(Boolean.class, Value.class, DataQuery.of("CAN_FLY"));
    public static final Key<Value<Boolean>> INVINCIBLE = KeyFactory.makeSingleKey(Boolean.class, Value.class, DataQuery.of("INVINCIBLE"));
    public static final Key<Value<Double>> MOVE_SPEED = KeyFactory.makeSingleKey(Double.class, Value.class, DataQuery.of("MOVE_SPEED"));
    public static final Key<Value<Double>> LEASH_SPEED = KeyFactory.makeSingleKey(Double.class, Value.class, DataQuery.of("LEASH_SPEED"));
}
