package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.RowSet;
import java.sql.Connection;

public class GetPersonIcon extends TagSupport {

	String dbpre;
	String a0100;
	String width;
	String height;

	public int doStartTag() throws JspException {
		Connection conn = null;
		RowSet rs = null;
		StringBuffer str_html = new StringBuffer();
			
		HttpServletRequest req = (HttpServletRequest)this.pageContext.getRequest();
		HttpSession session = req.getSession();
		try {
			
			if(a0100.trim().length()<1 || dbpre.trim().length()<1){
				throw new Exception("参数不能为空！");
			}
			
			String imagePath = "";
			conn = AdminDb.getConnection();
			PhotoImgBo pib = new PhotoImgBo(conn);
			String absPath = pib.getPhotoRootDir();
			// 获取头像

			// 是否使用缺省图片
			boolean useDefault = true;
			imagePath = "/images/m.jpg";
			// 如果设置了文件路径
			if (absPath.length() > 0) {
				absPath += pib.getPhotoRelativeDir(dbpre, a0100);
				
                String guid = pib.getGuid();
                
				// 获取头像图片
				String fileWName = pib
						.getPersonImageWholeName(absPath, "h_img");
				if (fileWName.length() > 0) {
					absPath += fileWName;
					
					//String repalcechar = File.separator;
					//if (File.separator.equals("\\"))
					//	repalcechar = "\\\\";
					//imagePath = "/servlet/DisplayOleContent?filePath="
					//		+ absPath.replaceAll(repalcechar, "`");

					imagePath = "/servlet/DisplayOleContent?perguid="+guid;
					// 走到这里说明读取人员头像成功，不用缺省头像
					useDefault = false;
					
					session.setAttribute(guid, absPath);
				}
			}

			// 根据男女获取缺省图片
			if (useDefault) {

				String sql = "select A0107 from " + dbpre + "A01 where a0100='"
						+ a0100 + "'";
				rs = new ContentDAO(conn).search(sql);
				String sex = "1";
				if (rs.next())
					sex = rs.getString("A0107");
				if (!"1".equals(sex))
					imagePath = "/images/fm.jpg";
			}

			str_html.append("<img src=\"" + imagePath + "\" ");
			if (height != null) {
				str_html.append(" height=\"");
				str_html.append(height);
				str_html.append("\"");
			}
			if (width != null) {
				str_html.append(" width=\"");
				str_html.append(width);
				str_html.append("\"");
			}
			str_html.append(" >");
			
			pageContext.getOut().println(str_html.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}finally{
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}

		return EVAL_PAGE;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
