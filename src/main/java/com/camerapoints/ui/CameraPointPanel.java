package com.camerapoints.ui;

import com.camerapoints.utility.Direction;
import net.runelite.client.config.Keybind;
import com.camerapoints.utility.CameraPoint;
import com.camerapoints.CameraPointsPlugin;
import com.camerapoints.utility.Helper;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

class CameraPointPanel extends JPanel
{
    private static final int ZOOM_LIMIT_MIN = -272;
    private static final int ZOOM_LIMIT_MAX = 1400;

    private static final ImageIcon FROM_GAME_ICON;
    private static final ImageIcon FROM_GAME_HOVER_ICON;
    private static final ImageIcon FROM_GAME_PRESSED_ICON;

    private static final ImageIcon LOAD_POINT_ICON;
    private static final ImageIcon LOAD_POINT_HOVER_ICON;
    private static final ImageIcon LOAD_POINT_PRESSED_ICON;

    private static final ImageIcon DELETE_ICON;
    private static final ImageIcon DELETE_HOVER_ICON;
    private static final ImageIcon DELETE_PRESSED_ICON;

    private final CameraPointsPlugin plugin;
    private final CameraPoint point;

    private final FlatTextField nameInput = new FlatTextField();
    private final JLabel saveLabel = new JLabel("Save");
    private final JLabel cancelLabel = new JLabel("Cancel");
    private final JLabel renameLabel = new JLabel("Rename");

    static
    {
        FROM_GAME_ICON = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "from_game_icon.png"));
        final BufferedImage fromGameImg = ImageUtil.loadImageResource(CameraPointsPlugin.class, "from_game_icon_blue.png");
        FROM_GAME_HOVER_ICON = new ImageIcon(fromGameImg);
        FROM_GAME_PRESSED_ICON = new ImageIcon(ImageUtil.alphaOffset(fromGameImg, -50));

        LOAD_POINT_ICON = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "load_point_icon.png"));
        final BufferedImage loadPointImg = ImageUtil.loadImageResource(CameraPointsPlugin.class, "load_point_icon_green.png");
        LOAD_POINT_HOVER_ICON = new ImageIcon(loadPointImg);
        LOAD_POINT_PRESSED_ICON = new ImageIcon(ImageUtil.alphaOffset(loadPointImg, -50));

        DELETE_ICON = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "delete_icon.png"));
        final BufferedImage deleteImg = ImageUtil.loadImageResource(CameraPointsPlugin.class, "delete_icon_red.png");
        DELETE_HOVER_ICON = new ImageIcon(deleteImg);
        DELETE_PRESSED_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, -50));
    }

    CameraPointPanel(CameraPointsPlugin plugin, CameraPoint point)
    {
        this.plugin = plugin;
        this.point = point;

        setLayout(new BorderLayout());
        setBackground(Helper.CONTENT_COLOR);

        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(Helper.CONTENT_COLOR);
        nameWrapper.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Helper.BACKGROUND_COLOR), BorderFactory.createLineBorder(Helper.CONTENT_COLOR)));

        JPanel nameActions = new JPanel(new BorderLayout(4, 0));
        nameActions.setBorder(new EmptyBorder(0, 4, 0, 8));
        nameActions.setBackground(Helper.CONTENT_COLOR);

        saveLabel.setVisible(false);
        saveLabel.setFont(FontManager.getRunescapeSmallFont());
        saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
        saveLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.brighter());
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                save();
                saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
            }
        });

        cancelLabel.setVisible(false);
        cancelLabel.setFont(FontManager.getRunescapeSmallFont());
        cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
        cancelLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.brighter());
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                cancel();
                cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
            }
        });

        renameLabel.setFont(FontManager.getRunescapeSmallFont());
        renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
        renameLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                nameInput.setEditable(true);
                updateNameActions(true);
                renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            }
        });

        nameActions.add(saveLabel, BorderLayout.EAST);
        nameActions.add(cancelLabel, BorderLayout.WEST);
        nameActions.add(renameLabel, BorderLayout.CENTER);

        nameInput.setText(point.getName());
        nameInput.setBorder(null);
        nameInput.setEditable(false);
        nameInput.setBackground(Helper.CONTENT_COLOR);
        nameInput.setPreferredSize(new Dimension(0, 24));
        nameInput.getTextField().setForeground(Color.WHITE);
        nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
        nameInput.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    save();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    cancel();
                }
            }
        });
        nameInput.getTextField().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent) || mouseEvent.getClickCount() < 2 || !renameLabel.isVisible())
                {
                    return;
                }

                nameInput.setEditable(true);
                updateNameActions(true);
            }
        });

        nameWrapper.add(nameInput, BorderLayout.CENTER);
        nameWrapper.add(nameActions, BorderLayout.EAST);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(new EmptyBorder(8, 8, 8, 8));
        bottomContainer.setBackground(Helper.CONTENT_COLOR);

        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        controlPanel.setBackground(Helper.CONTENT_COLOR);

        GridBagConstraints directionConstraints = new GridBagConstraints();
        directionConstraints.fill = GridBagConstraints.HORIZONTAL;
        directionConstraints.weightx = 0.6;

        GridBagConstraints zoomConstraints = new GridBagConstraints();
        zoomConstraints.fill = GridBagConstraints.HORIZONTAL;
        zoomConstraints.weightx = 0.4;

        JComboBox<Direction> directionBox = new JComboBox<>(Direction.values());
        directionBox.setToolTipText("Compass direction");
        directionBox.setSelectedIndex(point.getDirection().getValue());
        directionBox.setPreferredSize(new Dimension(0, 20));
        directionBox.addActionListener(e ->
        {
            point.setDirection((Direction)directionBox.getSelectedItem());
            plugin.updateConfig();
        });

        JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(ZOOM_LIMIT_MIN, ZOOM_LIMIT_MIN, ZOOM_LIMIT_MAX, 1));
        zoomSpinner.setToolTipText("Zoom value");
        zoomSpinner.setValue(point.getZoom());
        zoomSpinner.setEnabled(point.isApplyZoom());
        zoomSpinner.addChangeListener(e ->
        {
            point.setZoom((int)zoomSpinner.getValue());
            plugin.updateConfig();
        });
        zoomSpinner.setPreferredSize(new Dimension(0, 20));

        controlPanel.add(directionBox, directionConstraints);
        controlPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        controlPanel.add(zoomSpinner, zoomConstraints);

        JPanel centerPanel = new JPanel(new BorderLayout(4, 0));
        centerPanel.setBackground(Helper.CONTENT_COLOR);
        centerPanel.setPreferredSize(new Dimension(0, 20));

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Helper.CONTENT_COLOR);

        GridBagConstraints applyZoomConstraints = new GridBagConstraints();
        applyZoomConstraints.fill = GridBagConstraints.HORIZONTAL;
        applyZoomConstraints.weightx = 0.25;

        GridBagConstraints loadConstraints = new GridBagConstraints();
        loadConstraints.fill = GridBagConstraints.HORIZONTAL;
        loadConstraints.weightx = 0.25;

        GridBagConstraints saveConstraints = new GridBagConstraints();
        saveConstraints.fill = GridBagConstraints.HORIZONTAL;
        saveConstraints.weightx = 0.25;

        GridBagConstraints deleteConstraints = new GridBagConstraints();
        deleteConstraints.fill = GridBagConstraints.HORIZONTAL;
        deleteConstraints.weightx = 0.25;

        JButton hotkeyButton = new JButton();
        hotkeyButton.setToolTipText("Load point hotkey");
        hotkeyButton.setText(point.getKeybind().toString());
        hotkeyButton.setFont(FontManager.getDefaultFont().deriveFont(12.f));
        hotkeyButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                hotkeyButton.setText(Keybind.NOT_SET.toString());
                point.setKeybind(Keybind.NOT_SET);
                plugin.updateConfig();
            }
        });
        hotkeyButton.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                Keybind hotkey = new Keybind(e);
                hotkeyButton.setText(hotkey.toString());
                point.setKeybind(hotkey);
                plugin.updateConfig();
            }
        });

        JCheckBox applyZoomCheck = new JCheckBox("", true);
        applyZoomCheck.setToolTipText("Apply zoom when loading");
        applyZoomCheck.setSelected(point.isApplyZoom());
        applyZoomCheck.addChangeListener(e -> {
            zoomSpinner.setEnabled(applyZoomCheck.isSelected());
            point.setApplyZoom(applyZoomCheck.isSelected());
            plugin.updateConfig();
        });

        JLabel loadLabel = new JLabel();
        loadLabel.setIcon(LOAD_POINT_ICON);
        loadLabel.setToolTipText("Load this point");
        loadLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                loadLabel.setIcon(LOAD_POINT_PRESSED_ICON);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                plugin.setCamera(point);
                loadLabel.setIcon(LOAD_POINT_HOVER_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                loadLabel.setIcon(LOAD_POINT_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                loadLabel.setIcon(LOAD_POINT_ICON);
            }
        });

        JLabel fromGameLabel = new JLabel();
        fromGameLabel.setIcon(FROM_GAME_ICON);
        fromGameLabel.setToolTipText("Get current zoom value from game");
        fromGameLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                fromGameLabel.setIcon(FROM_GAME_PRESSED_ICON);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                int result = JOptionPane.showConfirmDialog(CameraPointPanel.this,
                        "Are you sure you want override the current zoom value?",
                        " Get current zoom value from game", JOptionPane.OK_CANCEL_OPTION);

                if (result == 0)
                {
                    plugin.updateValues(point);
                    zoomSpinner.setValue(point.getZoom());
                }

                fromGameLabel.setIcon(FROM_GAME_HOVER_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                fromGameLabel.setIcon(FROM_GAME_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                fromGameLabel.setIcon(FROM_GAME_ICON);
            }
        });

        JLabel deleteLabel = new JLabel();
        deleteLabel.setIcon(DELETE_ICON);
        deleteLabel.setToolTipText("Delete camera point");
        deleteLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                deleteLabel.setIcon(DELETE_PRESSED_ICON);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (Helper.checkClick(mouseEvent))
                {
                    return;
                }

                int result = JOptionPane.showConfirmDialog(CameraPointPanel.this,
                        "Are you sure you want to permanently delete this camera point?",
                        " Delete camera point", JOptionPane.OK_CANCEL_OPTION);

                if (result == 0)
                {
                    plugin.removeCameraPoint(point);
                }

                deleteLabel.setIcon(DELETE_HOVER_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                deleteLabel.setIcon(DELETE_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                deleteLabel.setIcon(DELETE_ICON);
            }
        });

        actionPanel.add(applyZoomCheck, applyZoomConstraints);
        actionPanel.add(loadLabel, loadConstraints);
        actionPanel.add(fromGameLabel, saveConstraints);
        actionPanel.add(deleteLabel, deleteConstraints);

        centerPanel.add(hotkeyButton, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.EAST);

        bottomContainer.add(controlPanel, BorderLayout.NORTH);
        bottomContainer.add(centerPanel, BorderLayout.CENTER);

        add(nameWrapper, BorderLayout.NORTH);
        add(bottomContainer, BorderLayout.CENTER);
    }

    private void save()
    {
        nameInput.setEditable(false);
        point.setName(nameInput.getText());
        plugin.updateConfig();
        updateNameActions(false);
        requestFocusInWindow();
    }

    private void cancel()
    {
        nameInput.setEditable(false);
        nameInput.setText(point.getName());
        updateNameActions(false);
        requestFocusInWindow();
    }

    private void updateNameActions(boolean saveAndCancel)
    {
        saveLabel.setVisible(saveAndCancel);
        cancelLabel.setVisible(saveAndCancel);
        renameLabel.setVisible(!saveAndCancel);

        if (saveAndCancel)
        {
            nameInput.getTextField().requestFocusInWindow();
            nameInput.getTextField().selectAll();
        }
    }
}