package io.njdldkl.serial;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * ImageIcon的反序列化器
 */
public class ImageIconDeserializer implements ObjectReader<ImageIcon> {
    @Override
    public ImageIcon readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        String base64;

        // 处理不同格式的输入
        if (jsonReader.isString()) {
            base64 = jsonReader.readString();
        } else {
            // 读取当前JSON值，无论什么类型
            Object value = jsonReader.readAny();
            if (value == null) {
                return null;
            }
            base64 = value.toString();
        }

        if (base64 == null || base64.isEmpty()) {
            return null;
        }

        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(bais);
            return new ImageIcon(image);
        } catch (Exception e) {
            return null;
        }
    }
}
