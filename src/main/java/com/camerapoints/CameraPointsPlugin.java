package com.camerapoints;

import com.camerapoints.utility.Direction;
import com.camerapoints.utility.Helper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
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
import okio.Timeout;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@PluginDescriptor(
        name = "Camera Points",
        description = "Allows you to save and load your camera zoom and a compass direction",
        tags = { "save", "load", "camera", "zoom", "compass", "direction", "hotkey" } )
public class CameraPointsPlugin extends Plugin implements KeyListener
{
    private static final int TOPLEVEL_COMPASS_OP_SCRIPT_ID = 1050;
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
        setTyping(false);
        loadConfig(configManager.getConfiguration(Helper.CONFIG_GROUP, CONFIG_KEY));

        keyManager.registerKeyListener(this);

        pluginPanel = new CameraPointsPluginPanel(this);

        navigationButton = NavigationButton.builder()
                .tooltip("Camera Points")
                .icon(ImageUtil.loadImageResource(getClass(), "panel_icon.png"))
                .priority(4)
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
        if (cameraPoints.isEmpty() && event.getGroup().equals(Helper.CONFIG_GROUP) && event.getKey().equals(CONFIG_KEY))
        {
            loadConfig(configManager.getConfiguration(Helper.CONFIG_GROUP, CONFIG_KEY));
        }
    }

    public void addCameraPoint()
    {
        cameraPoints.add(new CameraPoint(Instant.now().toEpochMilli(), "Camera Point " + (cameraPoints.size() + 1), Direction.NONE, true, getZoom(), Keybind.NOT_SET));
        updateConfig();
    }

    private int getZoom()
    {
        return client.getVarcIntValue(VarClientInt.CAMERA_ZOOM_FIXED_VIEWPORT);
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
            configManager.unsetConfiguration(Helper.CONFIG_GROUP, CONFIG_KEY);
            return;
        }

        configManager.setConfiguration(Helper.CONFIG_GROUP, CONFIG_KEY, gson.toJson(cameraPoints));
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
            if (point.isApplyZoom())
            {
                client.runScript(ScriptID.CAMERA_DO_ZOOM, point.getZoom(), point.getZoom());
            }
            if (point.getDirection() != Direction.NONE)
            {
                client.runScript(TOPLEVEL_COMPASS_OP_SCRIPT_ID, point.getDirection().getValue());
            }
        });
    }

    private boolean chatboxFocused()
    {
        Widget chatboxParent = client.getWidget(WidgetInfo.CHATBOX_PARENT);
        if (chatboxParent == null || chatboxParent.getOnKeyListener() == null)
        {
            return false;
        }

        Widget worldMapSearch = client.getWidget(WidgetInfo.WORLD_MAP_SEARCH);
        return worldMapSearch == null || client.getVarcIntValue(VarClientInt.WORLD_MAP_SEARCH_FOCUSED) != 1;
    }

    private boolean isDialogOpen()
    {
        return isHidden(WidgetInfo.CHATBOX_MESSAGES) || isHidden(WidgetInfo.CHATBOX_TRANSPARENT_LINES) || !isHidden(WidgetInfo.BANK_PIN_CONTAINER);
    }

    private boolean isHidden(WidgetInfo widgetInfo)
    {
        Widget w = client.getWidget(widgetInfo);
        return w == null || w.isSelfHidden();
    }

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean typing;

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if ((!isTyping() && !isDialogOpen()) || !config.disableWhileTyping())
        {
            for (CameraPoint point : cameraPoints) {
                if (point.getKeybind().matches(e)) {
                    setCamera(point);
                    return;
                }
            }
        }

        if (!chatboxFocused())
        {
            return;
        }

        if (isTyping())
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_ENTER:
                    setTyping(false);
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if (Strings.isNullOrEmpty(client.getVarcStrValue(VarClientStr.CHATBOX_TYPED_TEXT)))
                    {
                        setTyping(false);
                    }
                    break;
            }
        }
        else
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SLASH:
                case KeyEvent.VK_COLON:
                    setTyping(true);
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
