package com.camerapoints.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.client.config.Keybind;

@Data
@AllArgsConstructor
public class CameraPoint
{
    private long id;
    private String name;
    private int pitch;
    private int yaw;
    private int zoom;
    private Keybind keybind;
}
