package httpclient.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Implementation of a button that looks like an icon.
 */
class JIconButton extends JButton {
    /**
     * Constructor of icon button that configs a button so that it looks like an icon.
     *
     * @param icon         button icon
     * @param rolloverIcon icon shown when mouse pointer comes over the button
     */
    JIconButton(Icon icon, Icon rolloverIcon) {
        setBorderPainted(false);
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));
        setContentAreaFilled(false);
        setIcon(icon);
        setRolloverIcon(rolloverIcon);
    }
}
