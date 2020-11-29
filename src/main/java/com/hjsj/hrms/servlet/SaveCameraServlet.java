package com.hjsj.hrms.servlet;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
/**
 * 
 * <p>Title: 拍照上传 </p>
 * <p>Description:接收Flash拍摄的照片并保存 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-8-8 下午2:01:52</p>
 * @author jingq
 * @version 1.0
 */
public class SaveCameraServlet extends HttpServlet {

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("GBK");
		BufferedInputStream inputStream = null;
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;
		PrintWriter out = null;
		Connection conn = null;
		File f1 = null;
		try{
			conn = (Connection)AdminDb.getConnection();
			PhotoImgBo bo = new PhotoImgBo(conn);
			UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
			String url = bo.getPhotoRootDir() + bo.getPhotoRelativeDir(userView.getDbname(),userView.getA0100()) ;
			url =url.replace("\\", File.separator);
			File tempDir = new File(url);
			if(!tempDir.exists()){
				tempDir.mkdirs();
			} else {
				bo.delFileByName(url,"h_source,h_img");//删除原有图片
			}
			int v = -1;
			inputStream = new BufferedInputStream(request.getInputStream());
			byte[] bytes = new byte[1024];
			baos = new ByteArrayOutputStream();
			while((v=inputStream.read(bytes))>0){
				baos.write(bytes, 0, v);
			}
			byte[] tmp = baos.toByteArray();
			f1 = new File(url+"temp.jpg");//临时图片，主要用来压缩，压缩完成后删除
			fos = new FileOutputStream(f1);
			
			fos.write(tmp);
			boolean bl = compressCamera(url,"temp","h_source","h_img",240,240);
			out = response.getWriter();
			if(bl)
			out.write("OK");
			fos.flush();
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeResource(conn);
			PubFunc.closeIoResource(fos);
			PubFunc.closeResource(inputStream);
			PubFunc.closeResource(baos);
			if(out!=null){
				out.flush();
				PubFunc.closeResource(baos);
				out = null;
				f1.delete();
			}
		}
		
	}
	//将图片压缩成固定大小并输出到指定目录
	public boolean compressCamera(String dir,String oldname,String newname,String secname,int width,int height) throws Exception{
		double Ratio = 0.0;
		File of = new File(dir,oldname+".jpg");
		if(!of.isFile()){
			throw new Exception(oldname+" is not image file error in compressCamera");
		}
		File nf = new File(dir,newname+".jpg");//h_source.jpg
		File sf = new File(dir,secname+".jpg");//h_img.jpg
		BufferedImage bi = ImageIO.read(of);
		Image Itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);
		if((bi.getWidth()>width)||(bi.getHeight()>height)){//缩小
			if(bi.getHeight()>bi.getWidth()){
				Ratio = ((double)height)/bi.getHeight();
			} else {
				Ratio = ((double)width)/bi.getWidth();
			}
		} else {//放大
			if(bi.getHeight()>bi.getWidth()){
				Ratio = ((double)height)/bi.getHeight();
			} else {
				Ratio = ((double)width)/bi.getWidth();
			}
		}
		
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(Ratio, Ratio), null);
		Itemp = op.filter(bi, null); 
		try{//将压缩后的图片写到指定位置
			ImageIO.write((BufferedImage)Itemp, "jpg", nf);
			ImageIO.write((BufferedImage)Itemp, "jpg", sf);
		} catch (Exception ex){
			throw new Exception(" ImageIO.write error in CreateNewFile.:"+ex.getMessage());
		}
		return true;
	}

}
