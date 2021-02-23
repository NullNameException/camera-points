package com.camerapoints.ui;

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

class CameraPointPanel extends JPanel
{
    private static final int PITCH_LIMIT_MIN = 128;
    private static final int PITCH_LIMIT_MAX = 512;
    private static final int YAW_LIMIT_MIN = 0;
    private static final int YAW_LIMIT_MAX = 2047;
    private static final int ZOOM_LIMIT_MIN = -272;
    private static final int ZOOM_LIMIT_MAX = 1004;

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
    private CameraPoint previous;

    private final FlatTextField nameInput = new FlatTextField();
    private final JLabel saveLabel = new JLabel("Save");
    private final JLabel cancelLabel = new JLabel("Cancel");
    private final JLabel renameLabel = new JLabel("Rename");

    private final JSpinner pitchSpinner = new JSpinner(new SpinnerNumberModel(PITCH_LIMIT_MIN, PITCH_LIMIT_MIN, PITCH_LIMIT_MAX, 1));
    private final JSpinner yawSpinner = new JSpinner(new SpinnerNumberModel(YAW_LIMIT_MIN, YAW_LIMIT_MIN, YAW_LIMIT_MAX, 1));
    private final JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(ZOOM_LIMIT_MIN, ZOOM_LIMIT_MIN, ZOOM_LIMIT_MAX, 1));

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
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
                BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)));

        JPanel nameActions = new JPanel(new BorderLayout(4, 0));
        nameActions.setBorder(new EmptyBorder(0, 4, 0, 8));
        nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

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
        nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
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

                if (previous != null)
                {
                    plugin.setCamera(previous);
                    previous = null;
                }

                nameInput.setEditable(true);
                updateNameActions(true);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                if (!renameLabel.isVisible())
                {
                    return;
                }

                previous = plugin.getCurrentPoint();
                plugin.setCamera(point);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                if (previous == null)
                {
                    return;
                }

                plugin.setCamera(previous);
                previous = null;
            }
        });

        nameWrapper.add(nameInput, BorderLayout.CENTER);
        nameWrapper.add(nameActions, BorderLayout.EAST);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(new EmptyBorder(8, 8, 8, 8));
        bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel spinnerPanel = new JPanel(new BorderLayout(8, 0));
        spinnerPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        spinnerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel innerSpinnerPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        innerSpinnerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        pitchSpinner.setToolTipText("Pitch value");
        pitchSpinner.setValue(point.getPitch());
        pitchSpinner.addChangeListener(e ->
        {
            point.setPitch((int)pitchSpinner.getValue());
            plugin.updateConfig();
        });
        pitchSpinner.setPreferredSize(new Dimension(55, 20));

        yawSpinner.setToolTipText("Yaw value");
        yawSpinner.setValue(point.getYaw());
        yawSpinner.addChangeListener(e ->
        {
            point.setYaw((int)yawSpinner.getValue());
            plugin.updateConfig();
        });
        yawSpinner.setPreferredSize(new Dimension(0, 20));

        zoomSpinner.setToolTipText("Zoom value");
        zoomSpinner.setValue(point.getZoom());
        zoomSpinner.addChangeListener(e ->
        {
            point.setZoom((int)zoomSpinner.getValue());
            plugin.updateConfig();
        });
        zoomSpinner.setPreferredSize(new Dimension(0, 20));

        innerSpinnerPanel.add(yawSpinner);
        innerSpinnerPanel.add(zoomSpinner);

        spinnerPanel.add(pitchSpinner, BorderLayout.WEST);
        spinnerPanel.add(innerSpinnerPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(4, 0));
        centerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        centerPanel.setPreferredSize(new Dimension(0, 20));

        JPanel actionPanel = new JPanel(new BorderLayout(4, 0));
        actionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JButton hotkeyButton = new JButton();
        //hotkeyButton.setPreferredSize(new Dimension(0, 20));
        hotkeyButton.setToolTipText("Load point hotkey");
        hotkeyButton.setText(point.getKeybind().toString());
        hotkeyButton.setFont(FontManager.getDefaultFont().deriveFont(12.f));
        hotkeyButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                hotkeyButton.setText(Keybind.NOT_SET.toString());
                updateHotkey(Keybind.NOT_SET);
            }
        });
        hotkeyButton.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                Keybind hotkey = new Keybind(e);
                hotkeyButton.setText(hotkey.toString());
                updateHotkey(hotkey);
            }
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
        fromGameLabel.setToolTipText("Get current point from game");
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
                        "Are you sure you want override this camera point?",
                        " Get current point from game", JOptionPane.OK_CANCEL_OPTION);

                if (result == 0)
                {
                    plugin.updateValues(point);
                    pitchSpinner.setValue(point.getPitch());
                    yawSpinner.setValue(point.getYaw());
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

        actionPanel.add(loadLabel, BorderLayout.WEST);
        actionPanel.add(fromGameLabel, BorderLayout.CENTER);
        actionPanel.add(deleteLabel, BorderLayout.EAST);

        centerPanel.add(hotkeyButton, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.EAST);

        bottomContainer.add(spinnerPanel, BorderLayout.NORTH);
        bottomContainer.add(centerPanel, BorderLayout.CENTER);

        add(nameWrapper, BorderLayout.NORTH);
        add(bottomContainer, BorderLayout.CENTER);

        /*if (nameInput.getFontMetrics(nameInput.getFont()).stringWidth(nameInput.getText()) > nameInput.getWidth())
        {
            System.out.println(nameInput.getFontMetrics(nameInput.getFont()).stringWidth(nameInput.getText()));
            System.out.println(nameInput.getWidth());
            nameInput.setToolTipText(point.getName());
        }*/
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

    private void updateHotkey(Keybind hotkey)
    {
        point.setKeybind(hotkey);
        plugin.updateConfig();
    }
}
