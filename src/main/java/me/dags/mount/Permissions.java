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

import org.spongepowered.api.entity.living.player.Player;

/**
 * @author dags <dags@dags.me>
 */

public class Permissions
{
    public static final String COMMAND_CREATE = "mounts.command.create";
    public static final String COMMAND_FLY = "mounts.command.fly";
    public static final String COMMAND_INVINCIBLE = "mounts.command.invincible";
    public static final String COMMAND_ITEM = "mounts.command.item";
    public static final String COMMAND_LEASH = "mounts.command.leash";
    public static final String COMMAND_PURGE = "mounts.command.purge";
    public static final String COMMAND_RELOAD = "mounts.command.reload";
    public static final String COMMAND_REMOVE_SELF = "mounts.command.remove.self";
    public static final String COMMAND_REMOVE_OTHER = "mounts.command.remove.other";
    public static final String COMMAND_SPEED = "mounts.command.speed";
    public static final String COMMAND_TYPE = "mounts.command.type";

    public static final String MOUNT_USE = "mounts.use";

    public static final String TYPE_STUB = "mounts.type.";

    public static boolean allowedType(Player player, String type)
    {
        return player.hasPermission(TYPE_STUB + type);
    }
}
