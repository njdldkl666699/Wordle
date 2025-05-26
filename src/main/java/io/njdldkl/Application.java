package io.njdldkl;

import com.alibaba.fastjson2.JSON;
import io.njdldkl.pojo.User;
import io.njdldkl.view.WindowManager;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class Application {

    public static final String BANNER;

    static {
        try (InputStream bannerInputStream = Application.class.getResourceAsStream("/banner.txt")) {
            // 读取整个文件内容
            if (bannerInputStream == null) {
                throw new IllegalArgumentException("资源未找到: /banner.txt");
            }
            BANNER = new String(bannerInputStream.readAllBytes());
        } catch (IOException e) {
            throw new IllegalStateException("无法读取banner文件", e);
        }
    }

    public static void main(String[] args) {
        JSON.register(ImageIcon.class, new User.ImageIconSerializer());
        JSON.register(ImageIcon.class, new User.ImageIconDeserializer());

        System.out.println(BANNER);

        // 在启动时设置DPI感知（Java 9+）
        // 但是要重新设计UI
//        System.setProperty("sun.java2d.uiScale", "1");  // 禁用Java自己的缩放
//        System.setProperty("sun.java2d.dpiaware", "true");  // 启用DPI感知

        log.info("Wordle游戏启动");

        SwingUtilities.invokeLater(() -> {
            // 设置系统外观为系统默认
            try {
                log.info("设置系统外观");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            // 使用WindowsManager管理窗口
            WindowManager.getInstance().showMenuFrame();
        });
    }
}
