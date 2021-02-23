package com.camerapoints;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CameraPointsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CameraPointsPlugin.class);
		RuneLite.main(args);
	}
}