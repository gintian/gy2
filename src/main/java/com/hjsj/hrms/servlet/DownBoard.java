package com.hjsj.hrms.servlet;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * Company:hjsj
 * 
 *  公告附件下载
 * create time:2005-6-3:13:12:13
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class DownBoard extends HttpServlet {

	public void doGet(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws ServletException,
			IOException {
		doPost(httpservletrequest, httpservletresponse);
	}

	public void doPost(HttpServletRequest req,
			HttpServletResponse rep) throws ServletException,
			IOException
	{   
		//招聘外网不登录也需要下载公告附件
		String hireNetPortal = req.getParameter("hireNetPortal");
		//公告下载存在不登录也能下载问题，此处添加登陆验证 guodd 2017-12-15
		UserView userview = (UserView)req.getSession().getAttribute(WebConstant.userView);
		if(userview==null&&!"hireNetPortal".equals(hireNetPortal)){
			rep.getWriter().write("请登录系统！");
			return;
		}
			
		
		Connection connection = null;
		ResultSet resultset = null;
		InputStream inputstream = null;
		ServletOutputStream servletoutputstream = null;
		try {
			String s;
			s = req.getParameter("id");
			s = PubFunc.decrypt(s);
			int id = Integer.parseInt(s);
			//获取多媒体路径
			ConstantXml constantXml = new ConstantXml(connection,"FILEPATH_PARAM");
	        String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
			
	        String name=req.getParameter("topic");;
	        String ext = req.getParameter("ext");
	        
	        /**
	         * guodd 2016-01-22 解决公告附件下载导致数据库断开连接问题，附件放到硬盘中
	         * 如果设置了多媒体路径：
	         * (1)先从 多媒体 路径/announce 下查找相对于的附件文件
	         * (2)如果没有文件，从数据库里查询出文件，放到多媒体路径下
	         * (3)输出附件
	         * 如果没有设置多媒体路径：
	         *  还按原来的方式 从数据库读取文件下载
	         */
	        if(RootDir.length()>0){
	        	     
	        		RootDir = RootDir.endsWith(File.separator)?RootDir:RootDir+File.separator;
	        		
	        		//查询主题和文件后缀，根据路径规则（多媒体路径/announce/公告id/公告id_file.xxx）获取文件
	        		//String sql = "select topic,ext from announce where id=?";//thefile 
			    //ArrayList values = new ArrayList();
			    //values.add(id);
			    
				//resultset = dao.search(sql,values);
				String filePath = RootDir+"announce"+File.separator+id+File.separator;
				File file = new File(filePath+id+"_file."+ext);
				
//				if(resultset.next()){
//					name = resultset.getString("topic");
//					ext = resultset.getString("ext");	
//					filePath = RootDir+"announce"+File.separator+id+File.separator;
//					file = new File(filePath+id+"_file."+ext);
//				}else
//					return;
				
				
				//如果文件在多媒体文件夹中不存在，放进去
				if(!file.isFile()){
					connection = (Connection) AdminDb.getConnection();
					ContentDAO dao = new ContentDAO(connection);
					String sql = "select thefile from announce where id = ?";
					ArrayList values = new ArrayList();
				    values.add(id);
					resultset = dao.search(sql,values);
					if(resultset.next()){
						PubFunc.saveFileByPath(filePath, resultset.getBinaryStream("thefile"), id+"_file."+ext);
					}else
						return;
					file = new File(filePath+id+"_file."+ext);
				}
				
				inputstream = new FileInputStream(file);
	        }else{
	        		String sql = "select topic,ext,thefile from announce where id=?";
			    ArrayList values = new ArrayList();
			    values.add(id);
			    connection = (Connection) AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(connection);
				resultset = dao.search(sql,values);
				//获取到数据后关闭连接，防止下载数据比较大长时间占用连接
				PubFunc.closeIoResource(connection);
				
				inputstream = null;
				/**从数据库中取得数据*/
				if (resultset.next()) {
					ext = resultset.getString("ext");
					name=resultset.getString("topic");				
					inputstream = resultset.getBinaryStream("thefile");

				}
				else {
					return;
				}
	        	
	        }
			
			servletoutputstream = rep.getOutputStream();
			rep.setContentType(ServletUtilities.getMimeType("."+ext));	
			/**解决压缩文件，下载没有扩展名**/
			/*if(ext.equalsIgnoreCase("rar")||ext.equalsIgnoreCase("zip"))
			{
				rep.setHeader("Content-disposition", "note;filename=\"downboard." +ext  + "\"");
				rep.addHeader("Content-description",  "downboard."+ext);
			}*/
			name=new String((name+"."+ext).getBytes("gb2312"),"ISO8859_1");
			rep.setHeader("Content-disposition", "attachment;filename=\"" + name + "\"");
			rep.addHeader("Content-description",  name);

            int len;
            byte buf[] = new byte[1024];
			while ((len = inputstream.read(buf)) != -1) {
				servletoutputstream.write(buf,0,len);
			}			
			rep.setStatus(HttpServletResponse.SC_OK);
			rep.flushBuffer();		
			/**能过附件的形式打开文件*/
//			httpservletresponse.setHeader("Content-disposition",
//					"attachment;filename=" + lname);
//			httpservletresponse.setContentType("multipart/form-data");
//			String fileSize = Long.toString(length);
//			httpservletresponse.setHeader("Content-Length", fileSize);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		finally 
		{
		    PubFunc.closeIoResource(inputstream);
		    PubFunc.closeIoResource(resultset);
		    PubFunc.closeIoResource(connection);
			try 
			{
				if (servletoutputstream != null)
					servletoutputstream.flush();
				if (servletoutputstream != null)
					servletoutputstream.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return;
	}
}