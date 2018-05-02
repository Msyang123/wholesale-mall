package com.lhiot.mall.wholesale.base;

import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;

/**
 * 二维码生成
 */
public class QRCodeUtil{
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }
}