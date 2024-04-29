package httpclient.gui.theme;

import javax.swing.*;

/**
 * Light theme implementation of theme that is windows theme.
 */
public class LightTheme implements Theme {
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
