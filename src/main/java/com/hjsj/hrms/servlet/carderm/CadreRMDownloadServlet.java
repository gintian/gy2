package com.hjsj.hrms.servlet.carderm;

import com.hjsj.hrms.businessobject.general.cadrerm.CadreAppointAndRemove;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;


public class CadreRMDownloadServlet extends HttpServlet {

	public static final int BUFFER = 2048; //缓存大小
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		String fileName = "干部任免表.zip";
		response.setCharacterEncoding("GB2312");
	    response.setContentType("APPLICATION/OCTET-STREAM");   
	    response.setHeader("Content-Disposition",   "attachment;   filename=\""  
	    		+   new String(fileName.getBytes("gb2312"),"iso8859-1") +   "\""); 
	      
		ServletOutputStream out = response.getOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(out);
		
		String userName = request.getParameter("username");
		String dbPre = request.getParameter("dbpre");
		String cadreIds = request.getParameter("cadreids");
		
		Connection con = null;
		InputStream is = null;
		try {
			con = AdminDb.getConnection();
			String [] cadreids = cadreIds.split(",");
			for(int i = 0; i<cadreids.length; i++){
				String cadreid = cadreids[i];
				CadreAppointAndRemove	caar = new CadreAppointAndRemove(con,dbPre,userName);
				String message = caar.cadreInfoTableToLrm(cadreid);
				is = caar.cadreImageStream(cadreid);
				try {
					String fn = caar.getCadreName(cadreid);
					this.addFileToZip(zipOut,fn+".lrm" ,this.stringToStream(message));
					this.addFileToZip(zipOut,fn+".PIC",is);	
				}finally{					
					PubFunc.closeIoResource(is);
				}
			}
			out.flush();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(zipOut);
			try {
				if(con != null){
					con.close();					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			PubFunc.closeIoResource(out);
		}
	}

	
	
	/**
	 * 字符串到输入流
	 * @param str
	 * @return
	 */
	public  InputStream stringToStream(String str){
		if(str == null){
			return null;
		}
		byte[] temp = str.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(temp);
		return bais;
	}
	
	
	/**
	 * 动态添加文件流到ZIP包
	 * @param out
	 * @param fileName
	 * @param fileInputStream
	 */
	public void addFileToZip(ZipOutputStream zipOut , String fileName, InputStream fileInputStream){
		try {
			if (fileInputStream == null) {
				return;
			}			
			ZipEntry entry = new ZipEntry(fileName);
			zipOut.putNextEntry(entry);
			
			byte data[] = new byte[BUFFER];
			BufferedInputStream bis  = new BufferedInputStream(fileInputStream, BUFFER);
			
			int count = 0;
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				zipOut.write(data, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
