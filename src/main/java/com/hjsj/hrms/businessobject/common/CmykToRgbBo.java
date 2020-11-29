package com.hjsj.hrms.businessobject.common;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
/**
 * 
 * 
 * Title:CmykToRgbBo.java
 * Description:处理照片模式，CMYK模式为打印模式
 * Company:hjsj
 * Create time:May 16, 2014:4:48:13 PM
 * @author zhaogd
 * @version 6.x
 */
public class CmykToRgbBo {
	public static boolean isCMYK(String filename) {
		boolean result = false;
		boolean isRGB = false;
		boolean isCMYK = false;
		BufferedImage img = null;
		File file = new File(filename);
		boolean isEXISTS = file.exists();
		if(isEXISTS){
			try {
				img = ImageIO.read(file);
				if (img != null) {
					int colorSpaceType = img.getColorModel().getColorSpace().getType();
					isRGB = colorSpaceType == ColorSpace.TYPE_RGB;
				}
			} catch (IOException e) {
				try {
					img = readImage(file);
					if (img != null) {
						int colorSpaceType = img.getColorModel().getColorSpace().getType();
						isCMYK = colorSpaceType == ColorSpace.TYPE_RGB;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		if(isEXISTS&&!isRGB&&isCMYK){
			result = true;
		}
		return result;
	}
	
	public static boolean isCMYK(File file) {
		boolean result = false;
		boolean isRGB = false;
		boolean isCMYK = false;
		BufferedImage img = null;
		boolean isEXISTS = file.exists();
		if(isEXISTS){
			try {
				img = ImageIO.read(file);
				if (img != null) {
					int colorSpaceType = img.getColorModel().getColorSpace().getType();
					isRGB = colorSpaceType == ColorSpace.TYPE_RGB;
				}
			} catch (IOException e) {
				try {
					img = readImage(file);
					if (img != null) {
						int colorSpaceType = img.getColorModel().getColorSpace().getType();
						isCMYK = colorSpaceType == ColorSpace.TYPE_RGB;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		if(isEXISTS&&!isRGB&&isCMYK){
			result = true;
		}
		return result;
	}

	public static BufferedImage readImage(File file) throws IOException {
		try {
			ImageInputStream input = ImageIO.createImageInputStream(file);
			Iterator readers = ImageIO.getImageReaders(input);
			if (readers == null || !readers.hasNext()) {
				return null;
			}
			ImageReader reader = (ImageReader) readers.next();
			reader.setInput(input);
			String format = reader.getFormatName();
			if ("JPEG".equalsIgnoreCase(format)
					|| "JPG".equalsIgnoreCase(format)) {
				try {
					Raster raster = reader.readRaster(0, null);// CMYK
					if (input != null) {
						input.close();
					}
					reader.dispose();
					return createJPEG4(raster, 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return null;
	}

	private static BufferedImage createJPEG4(Raster raster, int xform) {
		try {
			int w = raster.getWidth();
			int h = raster.getHeight();
			byte[] rgb = new byte[w * h * 3];
			if (xform == 2) { // YCCK --
				float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
				float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
				float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
				float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);
				for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
					float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];
					double val = y + 1.402 * (cr - 128) - k;
					val = (val - 128) * .65f + 128;
					rgb[base] = val < 0.0 ? (byte) 0
							: val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
					val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
					val = (val - 128) * .65f + 128;
					rgb[base + 1] = val < 0.0 ? (byte) 0
							: val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
					val = y + 1.772 * (cb - 128) - k;
					val = (val - 128) * .65f + 128;
					rgb[base + 2] = val < 0.0 ? (byte) 0
							: val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
				}
			} else {
				int[] C = raster.getSamples(0, 0, w, h, 0, (int[]) null);
				int[] M = raster.getSamples(0, 0, w, h, 1, (int[]) null);
				int[] Y = raster.getSamples(0, 0, w, h, 2, (int[]) null);
				int[] K = raster.getSamples(0, 0, w, h, 3, (int[]) null);
				for (int i = 0, imax = C.length, base = 0; i < imax; i++, base += 3) {
					int c = 255 - C[i];
					int m = 255 - M[i];
					int y = 255 - Y[i];
					int k = 255 - K[i];
					float kk = k / 255f;
					rgb[base] = (byte) (255 - Math.min(255f, c * kk + k));
					rgb[base + 1] = (byte) (255 - Math.min(255f, m * kk + k));
					rgb[base + 2] = (byte) (255 - Math.min(255f, y * kk + k));
				}
			}
			raster = Raster.createInterleavedRaster(new DataBufferByte(rgb,
					rgb.length), w, h, w * 3, 3, new int[] { 0, 1, 2 }, null);
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
			ColorModel cm = new ComponentColorModel(cs, false, true,
					Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
			return new BufferedImage(cm, (WritableRaster) raster, true, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
