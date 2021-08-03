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
            keyName = "preview",
            name = "Preview on hover",
            description = "Enables/Disables the camera preview on hovering the name of the Camera Point"
    )
    default boolean showPreview()
    {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "keyRemap",
            name = "Key remapping plugin enabled",
            description = "When enabled, will not change camera while typing."
    )
    default boolean keyRemap() {
        return true;
    }
}
