package controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;

/**
 * This class responsible for operations with image files.
 */
public class ImageReader {

    /**
     * This method first read the image file by the given filepath, then
     * code it by base64 encoder and give back the created String.
     * @param filePath route of the file
     * @return String encoded by Base64 encoder
     */
    public static String base64image(String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] res = Base64.getEncoder().encode(baos.toByteArray());
            String encodedImage = "data:image/jpeg;base64," + new String(res);
            return encodedImage;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Some problem has occurred.Please try again");
        }

        return null;
    }

}

