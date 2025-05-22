package io.njdldkl.pojo;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    // 用户ID
    private UUID id;
    // 用户名
    private String name;
    // 用户头像
    @JSONField(serializeUsing = ImageIconSerializer.class,
            deserializeUsing = ImageIconDeserializer.class)
    private ImageIcon avatar;

    /**
     * ImageIcon的序列化器
     */
    public static class ImageIconSerializer implements ObjectWriter<ImageIcon> {
        @Override
        public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
            ImageIcon icon = (ImageIcon) object;
            if (icon == null) {
                jsonWriter.writeNull();
                return;
            }

            try {
                BufferedImage image = new BufferedImage(
                        icon.getIconWidth(),
                        icon.getIconHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                icon.paintIcon(null, image.getGraphics(), 0, 0);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                jsonWriter.writeString(base64);
            } catch (Exception e) {
                jsonWriter.writeNull();
            }
        }
    }

    /**
     * ImageIcon的反序列化器
     */
    public static class ImageIconDeserializer implements ObjectReader<ImageIcon> {
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

}
