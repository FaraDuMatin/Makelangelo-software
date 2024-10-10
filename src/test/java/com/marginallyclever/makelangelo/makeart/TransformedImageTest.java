package com.marginallyclever.makelangelo.makeart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

public class TransformedImageTest {
    private TransformedImage transformedImage;
    private BufferedImage testImage;

    @BeforeEach
    public void setup() {
        //un BufferedImage pour les tests
        testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < testImage.getWidth(); x++) {
            for (int y = 0; y < testImage.getHeight(); y++) {
                testImage.setRGB(x, y, 0xFFFFFF);
            }
        }
        transformedImage = new TransformedImage(testImage);
    }

    @Test
    public void testDeepCopyImage() {
        // Arrange
        BufferedImage copiedImage = transformedImage.deepCopy(testImage);

        // Act & Assert
        assertNotSame(testImage, copiedImage, "La copie de l'image doit être une instance différente de l'image originale.");
        assertEquals(testImage.getWidth(), copiedImage.getWidth(), "La largeur de l'image copiée doit correspondre à l'originale.");
        assertEquals(testImage.getHeight(), copiedImage.getHeight(), "La hauteur de l'image copiée doit correspondre à l'originale.");
    }
}
