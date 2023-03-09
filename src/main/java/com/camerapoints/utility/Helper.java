package com.camerapoints.utility;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Helper
{
    public static final String CONFIG_GROUP = "camerapoints";

    public static final Color BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
    public static final Color CONTENT_COLOR = ColorScheme.DARKER_GRAY_COLOR;

    public static boolean checkClick(MouseEvent event)
    {
        if (event.getButton() == MouseEvent.BUTTON1 && event.getSource() instanceof JComponent)
        {
            Point point = event.getPoint();
            Dimension size = ((JComponent)event.getSource()).getSize();
            return point.getX() < 0 || point.getX() > size.getWidth() || point.getY() < 0 || point.getY() > size.getHeight();
        }
        return true;
    }
}