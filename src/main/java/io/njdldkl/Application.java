package io.njdldkl;

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
            if(bannerInputStream == null) {
                throw new IllegalArgumentException("资源未找到: /banner.txt");
            }
            BANNER = new String(bannerInputStream.readAllBytes());
        } catch (IOException e) {
            throw new IllegalStateException("无法读取banner文件", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(BANNER);
        log.info("Wordle游戏启动");
        // 设置系统外观为系统默认
        try {
            log.info("设置系统外观");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // 使用WindowsManager管理窗口
        WindowManager.getInstance().showMenuFrame();
    }
}
