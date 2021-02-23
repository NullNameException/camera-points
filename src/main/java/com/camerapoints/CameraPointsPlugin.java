package com.camerapoints;

import com.camerapoints.utility.Direction;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.camerapoints.ui.CameraPointsPluginPanel;
import com.camerapoints.utility.CameraPoint;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
        name = "Camera Points",
        description = "Allows you to save and load camera positions, angles and zooms",
        tags = { "save", "load", "direction", "zoom" } )
public class CameraPointsPlugin extends Plugin implements KeyListener
{
    private static final int TOPLEVEL_COMPASS_OP_SCRIPT_ID = 1050;
    private static final String CONFIG_GROUP = "camerapoints";
    private static final String CONFIG_KEY = "points";

    @Inject
    private Gson gson;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    public CameraPointsConfig config;

    private CameraPointsPluginPanel pluginPanel;
    private NavigationButton navigationButton;

    @Getter
    private final List<CameraPoint> cameraPoints = new ArrayList<>();

    @Provides
    CameraPointsConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CameraPointsConfig.class);
    }

    @Override
    protected void startUp()
    {
        loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY));

        keyManager.registerKeyListener(this);

        pluginPanel = new CameraPointsPluginPanel(this);

        navigationButton = NavigationButton.builder()
                .tooltip("Camera Points")
                .icon(ImageUtil.loadImageResource(getClass(), "panel_icon.png"))
                .priority(5)
                .panel(pluginPanel)
                .build();

        clientToolbar.addNavigation(navigationButton);
    }

    @Override
    protected void shutDown()
    {
        keyManager.unregisterKeyListener(this);
        cameraPoints.clear();
        clientToolbar.removeNavigation(navigationButton);
        pluginPanel = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (cameraPoints.isEmpty() && event.getGroup().equals(CONFIG_GROUP) && event.getKey().equals(CONFIG_KEY))
        {
            loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY));
        }
    }

    public CameraPoint getCurrentPoint()
    {
        return new CameraPoint(-1, null, Direction.NONE, getZoom(), null);
    }

    public void addCameraPoint()
    {
        cameraPoints.add(new CameraPoint(Instant.now().toEpochMilli(), "Camera Point " + (cameraPoints.size() + 1), Direction.NONE, getZoom(), Keybind.NOT_SET));
        updateConfig();
    }

    private int getZoom()
    {
        return client.getVar(VarClientInt.CAMERA_ZOOM_FIXED_VIEWPORT);
    }

    public void removeCameraPoint(CameraPoint point)
    {
        cameraPoints.remove(point);
        pluginPanel.rebuild();
        updateConfig();
    }

    public void updateValues(CameraPoint point)
    {
        point.setZoom(getZoom());
        updateConfig();
    }

    public void updateConfig()
    {
        if (cameraPoints.isEmpty())
        {
            configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
            return;
        }

        configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, gson.toJson(cameraPoints));
    }

    private void loadConfig(String json)
    {
        if (Strings.isNullOrEmpty(json))
        {
            return;
        }

        cameraPoints.addAll(gson.fromJson(json, new TypeToken<ArrayList<CameraPoint>>(){ }.getType()));
    }

    public void setCamera(CameraPoint point)
    {
        clientThread.invoke(() -> {
            client.runScript(ScriptID.CAMERA_DO_ZOOM, point.getZoom(), point.getZoom());
            if (point.getDirection() != Direction.NONE)
            {
                client.runScript(TOPLEVEL_COMPASS_OP_SCRIPT_ID, point.getDirection().getValue());
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e)
    {
        for (CameraPoint point : cameraPoints)
        {
            if (point.getKeybind().matches(e))
            {
                setCamera(point);
                return;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
