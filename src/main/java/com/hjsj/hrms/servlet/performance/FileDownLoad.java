package com.hjsj.hrms.servlet.performance;

import com.hjsj.hrms.servlet.DownLawBase;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;



public class FileDownLoad extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public FileDownLoad() {
		super();
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String planid = request.getParameter("planid");
		String objectid=request.getParameter("objectid");
		String opt=request.getParameter("opt");   // hire: 招聘  reportwork:干部考察/述职报告  无：绩效  workView:工作纪实下载附件
		
		ResultSet rs = null;
		String ext = "";
		ServletOutputStream sos = response.getOutputStream();
		String name = "";
		Connection con=null;
		InputStream inputStream = null;
		try {
			con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			
			try {
				if(opt!=null&& "hire".equals(opt))
				{
					String e01a1=request.getParameter("e01a1");
					/**交易中这个参数是加密的,解密回来**/
					e01a1 = PubFunc.decrypt(e01a1);
					rs = dao.search("select ole, ext  from K00 where e01a1= '"
							+ e01a1 + "' and flag='K' ");
					if (rs.next()) {
						ext = rs.getString("ext").substring(1);
						inputStream = rs.getBinaryStream("ole");						
					}
					
				}
				else if(opt!=null&& "reportwork".equalsIgnoreCase(opt))
				{
					rs = dao.search("select affix, ext  from per_result_"+planid+" where object_id='"+objectid+"'");
					if (rs.next()) {
						ext = rs.getString("ext");
						inputStream = rs.getBinaryStream("affix");						
					}
				}else if(opt!=null&& "workView".equalsIgnoreCase(opt))
				{
					String file_id=request.getParameter("file_id");
					String p0100=request.getParameter("p0100");
					rs = dao.search("select content,ext from per_diary_file where file_id="+file_id+" and p0100="+p0100);
					if (rs.next()) {
						ext = rs.getString("ext");
						inputStream = rs.getBinaryStream("content");	
					}
				}
				else
				{
					if(request.getParameter("optUrl")==null)
					{
						String article_id=request.getParameter("article_id");
						rs = dao.search("select article_name,affix, ext  from per_article  where article_id="+article_id);
					}
					else
					{
						rs = dao.search("select article_name,affix, ext  from per_article  where lower(NBase)='usr' and plan_id="+planid+" and  a0100= '"
								+ objectid + "' and article_type=1 ");
					}
					if (rs.next()) {
						ext = rs.getString("ext");						
						inputStream = rs.getBinaryStream("affix");
						name = rs.getString("article_name");
					}
				}
				
				String mime = ServletUtilities.getMimeType("." + ext);
				// 设置http头部声明为下载模式
				if (DownLawBase.checkCanOpenFile(ext)) {
					response.setContentType(mime);
					if(StringUtils.isEmpty(name)) {
						name="fileDown."+ext;//加上这句，支持中文文件名，但IE6却多出了一个窗口
					}
				 	final String userAgent = request.getHeader("USER-AGENT");
                    //支持中文文件名 haosl delete
                    if(userAgent.contains("Firefox")){
                        name = "=?utf-8?b?"+ Base64.encodeBase64String(name.getBytes("utf-8"))+"?=";
                    }else{
                        name = URLEncoder.encode(name, "utf-8");
                    }
					response.setHeader("Content-disposition", "attachment;filename=\"" +  name + "\"");
				} else {
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition",
							"attachment;   filename=\"extFile\"");
				}
				byte buf[] = new byte[1024];
				int len =0;
				while ((len = inputStream.read(buf)) != -1) {
					sos.write(buf, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(con);
			
			PubFunc.closeIoResource(inputStream);
		}
		
		
		
	}

	

}
