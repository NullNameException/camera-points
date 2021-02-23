package com.camerapoints.utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Helper
{
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
