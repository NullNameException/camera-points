package com.camerapoints;

import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("camerapoints")
public interface CameraPointsConfig
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
}
