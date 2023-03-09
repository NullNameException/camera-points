package com.camerapoints;

import com.camerapoints.utility.Helper;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(Helper.CONFIG_GROUP)
public interface CameraPointsConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "disableWhileTyping",
            name = "Disable hotkeys while typing",
            description = "When enabled, will not load any camera points while typing in a chatbox."
    )
    default boolean disableWhileTyping() {
        return true;
    }
}
