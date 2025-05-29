package io.njdldkl.serial;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * ImageIcon的序列化器
 */
public class ImageIconSerializer implements ObjectWriter<ImageIcon> {
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
