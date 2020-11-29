package com.hjsj.hrms.servlet;

import com.hrms.frame.codec.SafeCode;
import com.swetake.util.Qrcode;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * <p>Title: TwodimensionCodeServlet </p>
 * <p>Description: 根据传入地址生成二维码</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-9-9 下午2:20:57</p>
 * @author hej
 * @version 1.0
 */
public class TwodimensionCodeServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String objwidth = req.getParameter("width");
		String objheight = req.getParameter("height");
		int width = 118;
		int height = 118;
		if(objwidth!=null&&!"".equals(objwidth)){
			width = Integer.parseInt(objwidth);
		}
		if(objheight!=null&&!"".equals(objheight)){
			height = Integer.parseInt(objheight);
		}
		String url = SafeCode.keyWord_reback(req.getParameter("url"));
		String decodeurl = URLDecoder.decode(URLDecoder.decode(url, "UTF-8"),"UTF-8");
		if(decodeurl.indexOf('`')!=-1){
			decodeurl = decodeurl.replaceAll("`", "&");
		}
		try {
			create_image(decodeurl, resp ,width ,height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void create_image(String sms_info, HttpServletResponse resp ,int width , int height)throws Exception {
		try {
			Qrcode testQrcode = new Qrcode();
			testQrcode.setQrcodeErrorCorrect('L');
			testQrcode.setQrcodeEncodeMode('B');
			testQrcode.setQrcodeVersion(10);
			String testString = sms_info;
			byte[] d = testString.getBytes("gbk");
			BufferedImage bi = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, width, height);
			g.setColor(Color.BLACK);

			if (d.length > 0 && d.length < 280) {
				boolean[][] s = testQrcode.calQrcode(d);
				for (int i = 0; i < s.length; i++) {
					for (int j = 0; j < s.length; j++) {
						if (s[j][i]) {
							g.fillRect(j * 2, i * 2, 2, 2);
						}
					}
				}
			}
			g.dispose();
			bi.flush();

			ImageIO.write(bi, "jpg", resp.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
