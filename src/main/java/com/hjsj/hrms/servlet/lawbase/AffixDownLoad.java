package com.hjsj.hrms.servlet.lawbase;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.servlet.DownLawBase;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
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
import java.sql.Statement;

public class AffixDownLoad extends HttpServlet {

	public AffixDownLoad() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String ext_file_id = request.getParameter("ext_file_id");
		ext_file_id = PubFunc.decrypt(SafeCode.decode(ext_file_id));
		String modeflag = request.getParameter("modeflag");
		Statement stmt = null;
		ResultSet rs = null;
		String ext = "";
		ServletOutputStream sos = response.getOutputStream();
		String name = "";
		Connection con=null;
		InputStream inputStream = null;
		UserView userView = (UserView)request.getSession().getAttribute("userView");
		DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			con = AdminDb.getConnection();
			
			try {
				stmt = con.createStatement();
				if("2".equals(modeflag)){//人事异动查看文件
					String sql="select ext, name,content from t_wf_file where file_id = '"
							+ ext_file_id + "'";
					if(PubFunc.isUseNewPrograme(userView)){//bug38004 71包60锁没有filepath字段报错
						sql="select ext, name,content,filepath from t_wf_file where file_id = '"
								+ ext_file_id + "'";
					}
					
					dbS.open(con, sql);
					rs = stmt.executeQuery(sql);
				}else{
					String sql="select ext, name,content from law_ext_file where ext_file_id = '"
							+ ext_file_id + "'";
					dbS.open(con, sql);
					rs = stmt.executeQuery(sql);
				}
				
				if (rs.next()) {

					ext = rs.getString("ext");
					if ("2".equals(modeflag)) {// 人事异动查看文件
						name = userView.getUserName() + PubFunc.getStrg() + "." + ext;
					} else {
						//zhangh 2020-1-6 【56214】发文管理和制度政策中，涉及到导出的（pdf、word、excel、zip等等）命名，请统一成： 登陆用户_相应信息
						name =  userView.getUserName() + "_" + replacechar(request, rs.getString("name"))+ "." + ext;
					}
					inputStream = rs.getBinaryStream("content");
					if ("2".equals(modeflag)) {
					    //liuyz 新人事异动上传附件数据库中content不再存二进制数据，存储文件路径filepath
					    if(inputStream==null)
					    {
					    	if(PubFunc.isUseNewPrograme(userView)){//bug38004 71包60锁没有filepath字段报错
						    	String filePath=rs.getString("filepath");
						        if(filePath!=null&&filePath.trim().length()>0)
						        {
						            File file=new File(filePath);
						            if(file.exists())
						                inputStream=new FileInputStream(rs.getString("filepath"));
						            else
						                throw new GeneralException("文件路径不存在！");
						        }
						        else
						            throw new GeneralException("content无数据且文件路径不存在！");
					    	}else{
								throw new GeneralException("content无数据！");
							}
					    }
					}
					
				}
				String mime = ServletUtilities.getMimeType("." + ext);
				// 设置http头部声明为下载模式
				if (DownLawBase.checkCanOpenFile(ext)) {
					response.setContentType(mime);
					name=new String(name.getBytes("gb2312"),"ISO8859_1");
					response.setHeader("Content-Disposition", "attachment;filename=\"" + name +/*"."+ ext +*/ "\"");
				} else {
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition",
							"attachment;   filename=\"" + name +"."+ ext + "\"");
				}
				byte buf[] = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) != -1) {
					sos.write(buf, 0, len);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(inputStream);
			try {
				// 关闭Wallet
				dbS.close(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			PubFunc.closeResource(rs);
			PubFunc.closeResource(stmt);
			PubFunc.closeResource(con);
		}

	}
	/**
	 * 过滤文件名称中的特殊字符
	 * @param name 自定义文件名称
	 * @return name 过滤后的文件名称
	 */
	private String replacechar(HttpServletRequest request, String name){
		try{
			
			name=name.replace("\\", "");
			name=name.replace("/", "");
			name=name.replace(":", "");
			name=name.replace("*", "");
			name=name.replace("?", "");
			name=name.replace("\"", "");
			name=name.replace("<", "");
			name=name.replace(">", "");
			name=name.replace("[", "");
			name=name.replace("]", "");
			name=name.replace("[|]", "");
			name=name.replace("=", "");
			name=name.replace("%", "");
			name=name.replace("'", "");
			name=name.replace(";", "");
			name=name.replace("&", "");
			
			//zhaoxj 20150626 ie6有“—”前台得到的是乱码
			if (isIE6(request))
			    name = name.replace("—", "_");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	private boolean isIE6(HttpServletRequest request) {
	   String userAgent = request.getHeader("user-agent");
	   return (userAgent != null) && (userAgent.contains("MSIE 6.0"));
	}
}
