package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import org.apache.struts.upload.FormFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片处理：过滤上传的图片中携带的非图片信息，保证上传图片的安全性。
 * 
 * @Title: Img.java
 * @Description:
 * @Company: hjsj
 * @Create time: 2014-8-20 下午02:16:45
 * @author chenxg
 * @version 1.0
 */
public class ImageBO {

    /**
     * 返回象元的RGB数组
     * 
     * @param pixel
     * @return
     */
    private static int[] getSplitRGB(int pixel) {
        int[] rgbs = new int[3];
        rgbs[0] = (pixel & 0xff0000) >> 16;
        rgbs[1] = (pixel & 0xff00) >> 8;
        rgbs[2] = (pixel & 0xff);
        return rgbs;
    }

    /**
     * 
     * @param bimg
     * @return
     */
    private static int[] getPixes(BufferedImage bimg) {
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        int[] rgbs = new int[h * w];
        bimg.getRGB(0, 0, w, h, rgbs, 0, w);
        return rgbs;
    }

    /**
     * 获取RGB矩阵
     * 
     * @param image
     * @return int[3][y]x[] RGB矩阵
     */
    private static int[][][] getRGBMat(BufferedImage bimg) {
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        int[][][] rgbmat = new int[3][h][w];
        int[] pixes = getPixes(bimg);
        int k = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] rgb = getSplitRGB(pixes[k++]);
                rgbmat[0][y][x] = rgb[0];
                rgbmat[1][y][x] = rgb[1];
                rgbmat[2][y][x] = rgb[2];
            }
        }
        return rgbmat;
    }

    /**
     * 根据rg阵返回图片
     * 
     * @param rgbmat
     * @return
     */
    private static BufferedImage getImg(int[][][] rgbmat) {
        int w = rgbmat[0][0].length, h = rgbmat[0].length;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int r = rgbmat[0][y][x] << 16, g = rgbmat[1][y][x] << 8, b = rgbmat[2][y][x];
                int pixel = 0xff000000 | r | g | b;
                image.setRGB(x, y, pixel);
            }
        }
        return image;
    }

    /**
     * 复制一张新图片并转成 InputStream
     * 
     * @param image
     *            图片
     * @param type
     *            图片类型
     * @return
     */
    public static InputStream imgStream(FormFile image, String type) {
        InputStream im = null;
        InputStream inputStream=null;
        try {
        	inputStream=image.getInputStream();
        	im = imgToInputStream(inputStream, type);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally{
            PubFunc.closeResource(inputStream);   
        }
        return im;
    }
    /**
     * 复制一张新图片并转成 InputStream
     * 
     * @param image
     *            图片
     * @param type
     *            图片类型
     * @return
     */
    public static InputStream imgStream(File image, String type) {
        InputStream im = null;
        InputStream inputStream=null;
        try {
        	inputStream= new FileInputStream(image);
        	im = imgToInputStream(inputStream, type);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally{
            PubFunc.closeResource(inputStream);   
        }
        return im;
    }
    /**
     * 复制一张新图片并转成 InputStream
     * 
     * @param inputStream
     *            图片
     * @param type
     *            图片类型
     * @return
     */
    public static InputStream imgToInputStream(InputStream inputStream, String type) {
        InputStream im = null;
        try {
        	BufferedImage img = null;
        	ByteArrayOutputStream imbyte = new ByteArrayOutputStream();
        	if(type.startsWith(".")) {
                type = type.substring(1);
            }
        	if(!isImageFile(type)) {
                return im;
            }
        	// 读取图片
        	img = ImageIO.read(inputStream);
        	// 转换为rgb阵
        	int[][][] rgbMat = getRGBMat(img);
        	// 再转换为图片
        	img = getImg(rgbMat);
        	
        	ImageOutputStream imOut = ImageIO.createImageOutputStream(imbyte);
        	ImageIO.write(img, type, imOut);
        	im = new ByteArrayInputStream(imbyte.toByteArray());
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return im;
    }

    /**
     * 复制一张新图片并转成byte[]
     * 
     * @param image
     *            图片
     * @param type
     *            图片类型
     * @return
     */
    public static byte[] imgByte(FormFile image, String type) {
        byte[] imgb = null;
        InputStream in = null;
        try {
            in=image.getInputStream();
            imgb = imgToByte(in, type);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	PubFunc.closeIoResource(in);
        }

        return imgb;
    }
    
    /**
     * 复制一张新图片并转成byte[]
     * 
     * @param image
     *            图片
     * @param type
     *            图片类型
     * @return
     */
    public static byte[] imgByte(File image, String type) {
        byte[] imgb = null;
        InputStream in = null;
        try {
            in= new FileInputStream(image);
            imgb = imgToByte(in, type);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	PubFunc.closeIoResource(in);
        }

        return imgb;
    }
    /**
     * 复制一张新图片并转成byte[]
     * @param in  
     * 			图片
     * @param type  
     * 			图片类型
     * @return
     */
    public static byte[] imgToByte(InputStream in, String type) {
        BufferedImage img = null;
        byte[] imgb = null;
        ByteArrayOutputStream imbyte = new ByteArrayOutputStream();
        try {
            if(type.startsWith(".")) {
                type = type.substring(1);
            }
            if(!isImageFile(type)) {
                return imgb;
            }
            // 读取图片
            img = ImageIO.read(in);
            // 转换为rgb阵
            int[][][] rgbMat = getRGBMat(img);
            // 再转换为图片
            img = getImg(rgbMat);
            ImageIO.write(img, type, imbyte);
            imgb = imbyte.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	PubFunc.closeIoResource(in);
        }

        return imgb;
    }
    /**
     * 是否是图片文件（jbp,gif,bmp)
     * 
     * @Title: isImageFile
     * @Description:
     * @param fileExt
     *            文件扩展名
     * @return
     */
    public static boolean isImageFile(String fileExt) {
        if (fileExt.startsWith(".")) {
            fileExt = fileExt.substring(1);
        }

        return "jpg".equalsIgnoreCase(fileExt) || "jpeg".equalsIgnoreCase(fileExt)
                || "gif".equalsIgnoreCase(fileExt) || "bmp".equalsIgnoreCase(fileExt)
                || "png".equalsIgnoreCase(fileExt);
    }
}
