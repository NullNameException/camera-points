package com.camerapoints.ui;

import com.camerapoints.utility.CameraPoint;
import com.camerapoints.CameraPointsPlugin;
import com.camerapoints.utility.Helper;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CameraPointsPluginPanel extends PluginPanel
{
    private static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_HOVER_ICON;
    private static final ImageIcon ADD_PRESSED_ICON;

    private final JLabel title = new JLabel();
    private final PluginErrorPanel noPointsPanel = new PluginErrorPanel();
    private final JPanel pointsView = new JPanel(new GridBagLayout());

    private final CameraPointsPlugin plugin;

    static
    {
        final BufferedImage addIcon = ImageUtil.loadImageResource(CameraPointsPlugin.class, "add_icon.png");
        ADD_ICON = new ImageIcon(addIcon);
        ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, -100));
        ADD_PRESSED_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, -50));
    }

    public CameraPointsPluginPanel(CameraPointsPlugin plugin)
    {
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

        title.setText("Camera Points");
        title.setForeground(Color.WHITE);

        JLabel addPoint = new JLabel(ADD_ICON);

        northPanel.add(title, BorderLayout.WEST);
        northPanel.add(addPoint, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Helper.BACKGROUND_COLOR);

        pointsView.setBackground(Helper.BACKGROUND_COLOR);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = constraints.gridy = 0;
        constraints.weightx = 1;

        noPointsPanel.setContent("Camera Points", "Save and load camera points.");

        addPoint.setToolTipText("Add new camera point");
        addPoint.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                addPoint.setIcon(ADD_PRESSED_ICON);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                create();
                addPoint.setIcon(ADD_HOVER_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                addPoint.setIcon(ADD_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                addPoint.setIcon(ADD_ICON);
            }
        });

        centerPanel.add(pointsView, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        rebuild();
    }

    public void rebuild()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = constraints.gridy = 0;
        constraints.weightx = 1;

        pointsView.removeAll();

        if (plugin.getCameraPoints().isEmpty())
        {
            title.setVisible(false);
            pointsView.add(noPointsPanel, constraints);
            constraints.gridy++;
        }
        else
        {
            title.setVisible(true);
            for (final CameraPoint point : plugin.getCameraPoints())
            {
                pointsView.add(new CameraPointPanel(plugin, point), constraints);
                constraints.gridy++;

                pointsView.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
                constraints.gridy++;
            }
        }

        repaint();
        revalidate();
    }

    public void create()
    {
        plugin.addCameraPoint();
        rebuild();
    }
}
