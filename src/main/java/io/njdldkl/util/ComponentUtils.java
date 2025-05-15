package io.njdldkl.util;

import java.awt.*;

public class ComponentUtils {

    /**
     * 使窗口在屏幕上居中显示
     * 确保窗口的中心点与屏幕中心点重合
     */
    public static void setCenterWindowOnScreen(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = component.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;

        // 确保坐标不为负
        x = Math.max(0, x);
        y = Math.max(0, y);

        component.setLocation(x, y);
    }
}
