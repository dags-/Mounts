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

import me.dags.dlib.commands.CommandMessenger;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * @author dags <dags@dags.me>
 */

@ConfigSerializable
public class Config
{
    @Setting (value = "speed_limits", comment = "Set the allowed range of speeds for Mounts")
    private MountSpeeds mountSpeeds = new MountSpeeds();
    @Setting (value = "command_colors", comment = "Set command message colors")
    private CommandMessenger commandMessenger = new CommandMessenger();
    @Setting (value = "default_mount", comment = "Configure the default Mount for users")
    private DefaultMount defaultMount = new DefaultMount();

    public MountSpeeds speeds()
    {
        return mountSpeeds;
    }

    public CommandMessenger messenger()
    {
        return commandMessenger;
    }

    public DefaultMount defaults()
    {
        return defaultMount;
    }

    @ConfigSerializable
    public static class DefaultMount
    {
        @Setting
        private String entityType = "PIG";
        @Setting
        private String spawnItem = "SADDLE";
        @Setting
        private String leashItem = "LEAD";
        @Setting
        private boolean canFly = false;
        @Setting
        private boolean invincible = false;
        @Setting
        private double normalSpeed = 0.25D;
        @Setting
        private double leashSpeed = 0.05D;

        public String type()
        {
            return entityType;
        }

        public String spawnItem()
        {
            return spawnItem;
        }

        public String leashItem()
        {
            return leashItem;
        }

        public boolean canFly()
        {
            return canFly;
        }

        public boolean invincible()
        {
            return invincible;
        }

        public double normalSpeed()
        {
            return normalSpeed;
        }

        public double leashSpeed()
        {
            return leashSpeed;
        }
    }

    @ConfigSerializable
    public static class MountSpeeds
    {
        @Setting
        double minSpeed = 0.01D;
        @Setting
        double maxSpeed = 5.0D;

        public boolean outsideOfRange(double test)
        {
            return test < minSpeed || test > maxSpeed;
        }

        public String getRange()
        {
            return minSpeed + " < x < " + maxSpeed;
        }

        public double clamp(double speedIn)
        {
            return speedIn < minSpeed ? minSpeed : speedIn > maxSpeed ? maxSpeed : speedIn;
        }
    }
}
