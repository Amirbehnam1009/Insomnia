package httpclient.gui.theme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

/**
 * Dark theme implementation of theme.
 */
public class DarkTheme extends DefaultMetalTheme implements Theme {
    public static final ColorUIResource primary1 = new ColorUIResource(53, 56, 58);
    private static final ColorUIResource white = new ColorUIResource(128, 128, 128);
    private static final ColorUIResource darkBlue = new ColorUIResource(0, 44, 63);
    private static final ColorUIResource lightGray = new ColorUIResource(109, 109, 109);
    private static final ColorUIResource primary2 = new ColorUIResource(50, 66, 114);
    private static final ColorUIResource primary3 = new ColorUIResource(53, 69, 91);
    private final ColorUIResource myControlHighlightColor = new ColorUIResource(108, 111, 113);
    private final ColorUIResource myControlDarkShadowColor = new ColorUIResource(39, 42, 44);
    private final ColorUIResource myControlColor = new ColorUIResource(0x3c3f41);
    private final ColorUIResource mySeparatorForeground = new ColorUIResource(53, 56, 58);

    public String getName() {
        return "Dark theme";
    }

    public ColorUIResource getControl() {
        return myControlColor;
    }

    @Override
    public ColorUIResource getControlHighlight() {
        return myControlHighlightColor;
    }

    @Override
    public ColorUIResource getControlDarkShadow() {
        return myControlDarkShadowColor;
    }

    public ColorUIResource getSeparatorBackground() {
        return getControl();
    }

    public ColorUIResource getSeparatorForeground() {
        return mySeparatorForeground;
    }

    public ColorUIResource getMenuBackground() {
        return lightGray;
    }

    public ColorUIResource getMenuSelectedBackground() {
        return darkBlue;
    }

    public ColorUIResource getMenuSelectedForeground() {
        return white;
    }

    public ColorUIResource getAcceleratorSelectedForeground() {
        return white;
    }

    public ColorUIResource getFocusColor() {
        return new ColorUIResource(Color.black);
    }

    protected ColorUIResource getPrimary1() {
        return primary1;
    }

    protected ColorUIResource getPrimary2() {
        return primary2;
    }

    protected ColorUIResource getPrimary3() {
        return primary3;
    }

    @Override
    public String getLookAndFeel() {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Windows".equals(info.getName())) {
                return info.getClassName();
            }
        }
        return "";
    }
}
