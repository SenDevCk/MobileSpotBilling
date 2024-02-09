package org.cso.TVS;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Victor Yang on 2016/6/17.
 * 去除 bitmap 无用的白色边框
 */
public class BitmapDeleteNoUseSpaceUtil {

    /**
     * 灰度化 bitmap
     * @param imgTheWidth
     * @param imgTheHeight
     * @param imgThePixels
     * @return
     */
    private static Bitmap getGrayImg(int imgTheWidth, int imgTheHeight, int[] imgThePixels) {
        int alpha = 0xFF << 24;  //设置透明度
        for (int i = 0; i < imgTheHeight; i++) {
            for (int j = 0; j < imgTheWidth; j++) {
                int grey = imgThePixels[imgTheWidth * i + j];
                int red = ((grey & 0x00FF0000) >> 16);  //获取红色灰度值
                int green = ((grey & 0x0000FF00) >> 8); //获取绿色灰度值
                int blue = (grey & 0x000000FF);         //获取蓝色灰度值
                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey; //添加透明度
                imgThePixels[imgTheWidth * i + j] = grey;   //更改像素色值
            }
        }
        Bitmap result =
                Bitmap.createBitmap(imgTheWidth, imgTheHeight, Bitmap.Config.RGB_565);
        result.setPixels(imgThePixels, 0, imgTheWidth, 0, 0, imgTheWidth, imgTheHeight);
        return result;
    }

    /**
     * 去除多余白框
     * @param originBitmap
     * @return
     */
    public static Bitmap deleteNoUseWhiteSpace(Bitmap originBitmap) {

        Log.e("","1宽度："+originBitmap.getWidth());

        int[] imgThePixels = new int[originBitmap.getWidth() * originBitmap.getHeight()];
        originBitmap.getPixels(
                imgThePixels,
                0,
                originBitmap.getWidth(),
                0,
                0,
                originBitmap.getWidth(),
                originBitmap.getHeight());

        // 灰度化 bitmap
        Bitmap bitmap = getGrayImg(
                originBitmap.getWidth(),
                originBitmap.getHeight(),
                imgThePixels);

        int top = 0;  // 上边框白色高度
        int left = 0; // 左边框白色高度
        int right = 0; // 右边框白色高度
        int bottom = 0; // 底边框白色高度

//        for (int h = 0; h < bitmap.getHeight(); h++) {
//            boolean holdBlackPix = false;
//            for (int w = 0; w < bitmap.getWidth(); w++) {
//                if (bitmap.getPixel(w, h) != -1) { // -1 是白色
//                    holdBlackPix = true; // 如果不是-1 则是其他颜色
//                    break;
//                }
//            }
//
//            if (holdBlackPix) {
//                break;
//            }
//            top++;
//        }
//
//        for (int w = 0; w < bitmap.getWidth(); w++) {
//            boolean holdBlackPix = false;
//            for (int h = 0; h < bitmap.getHeight(); h++) {
//                if (bitmap.getPixel(w, h) != -1) {
//                    holdBlackPix = true;
//                    break;
//                }
//            }
//            if (holdBlackPix) {
//                break;
//            }
//            left++;
//        }
//
//        for (int w = bitmap.getWidth() - 1; w >= 0; w--) {
//            boolean holdBlackPix = false;
//            for (int h = 0; h < bitmap.getHeight(); h++) {
//                if (bitmap.getPixel(w, h) != -1) {
//                    holdBlackPix = true;
//                    break;
//                }
//            }
//            if (holdBlackPix) {
//                break;
//            }
//            right++;
//        }

        for (int h = bitmap.getHeight() - 1; h >= 0; h--) {
            boolean holdBlackPix = false;
            for (int w = 0; w < bitmap.getWidth(); w++) {
                if (bitmap.getPixel(w, h) != -1) {
                    holdBlackPix = true;
                    break;
                }
            }
            if (holdBlackPix) {
                break;
            }
            bottom++;
        }

        // 获取内容区域的宽高
        int cropHeight = bitmap.getHeight() - bottom - top;
//        int cropWidth = bitmap.getWidth() - left - right;

        int cropWidth = bitmap.getWidth();

        Log.e("","2宽度："+cropWidth);

        // 获取内容区域的像素点
        int[] newPix = new int[cropWidth * cropHeight];

//        int i = 0;
//        for (int h = top; h < top + cropHeight; h++) {
//            for (int w = left; w < left +right+ cropWidth; w++) {
//                newPix[i++] = bitmap.getPixel(w, h);
//            }
//        }

        int i = 0;
        for (int h = top; h < top + cropHeight; h++) {
            for (int w = left; w < cropWidth; w++) {
                newPix[i++] = bitmap.getPixel(w, h);
            }
        }

        // 创建切割后的 bitmap， 针对彩色图，把 newPix 替换为 originBitmap 的 pixs
        return Bitmap.createBitmap(newPix, cropWidth, cropHeight, Bitmap.Config.ARGB_8888);

    }
}