package com.hjsj.hrms.taglib.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchMyCourseTag extends BodyTagSupport {

	private String r5000;
	private String filepath;

	public int doStartTag() throws JspException {
		Connection conn = null;
		RowSet rs = null;
		try
        {  
			UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			filepath = SafeCode.decode(filepath);
				String sql = "select a.r5000,b.r5003,b.r5033,c.r5100,c.r5103,c.R5117,c.r5119,c.r5105,c.r5113  "
						+ "from tr_selected_lesson a join r50 b on a.r5000=b.r5000 join r51 c on b.r5000=c.r5000"
						+ " where a.r5000=? and a.a0100=? and a.nbase=? and b.r5022='04'"
						+ " and c.r5105<>'4' and c.r5105<>'5'";
				ArrayList values = new ArrayList();
				values.add(r5000);
				values.add(userView.getA0100());
				values.add(userView.getDbname());
				List recs = ExecuteSQL.executePreMyQuery(sql, values,conn);
						String html  = this.dohtml(recs, filepath);
	        pageContext.getOut().println(html);
	        return EVAL_BODY_BUFFERED;           
        }
        catch(Exception ge)
        {
            ge.printStackTrace();
            return 0;
        }finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
        	if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        }
	}
	
	private String dohtml(List recs, String filepath) {

		StringBuffer html = new StringBuffer();
		if (recs != null && recs.size() > 0) {

		    ServletRequest request = pageContext.getRequest();
		    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
			LazyDynaBean bean = (LazyDynaBean) recs.get(0);
			html.append("<div data-role=\"page\"  class=\"type-interior\" data-inset=\"true\"><div data-role=\"header\"  data-theme=\"c\"><h1>"
					+ bean.get("r5003")
					+ "</h1></div><div data-role=\"content\">" );
			html.append("<div class=\"content-primary\">	<ul data-role=\"listview\">");
			for (int i = 0; i < recs.size(); i++) {

				bean = (LazyDynaBean) recs.get(i);
				String r5000 = (String) bean.get("r5000");
				String r5100 = (String) bean.get("r5100");
				String r5103 = (String) bean.get("r5103");
				String r5119 = (String) bean.get("r5119");
				String r5117 = (String) bean.get("r5117");
				String r5105 = (String) bean.get("r5105");// 课件类型 1普通课件 2文本课件
															// 3多媒体课件
				String r5113 = (String) bean.get("r5113");// 课件路径
				
				String r5033 = (String) bean.get("r5033");

				if (filepath.endsWith("/") || filepath.endsWith("\\"))
					filepath = filepath.substring(0, filepath.length() - 1);
				String filepaths = filepath + r5113.toString();
				filepaths = SafeCode.encode(PubFunc.encrypt(filepaths));
				html.append("<li>");
				if ("3".equals(r5105)) {
					if("1".equals(r5033)){
						html.append("<a href=\""+basePath+ "/train/resource/mylessons/learncoursebyweixin.jsp?lesson="
								+ SafeCode.encode(PubFunc.encrypt(r5000))
								+ "&course="
								+ SafeCode.encode(PubFunc.encrypt(r5100)) + "\" data-ajax=\"false\">");
					} else {
						html.append("<a onclick=\"javascript:alert('文件不允许下载！');\">");
					}

					html.append("<img src=\"/images/shipin.png\" />");
				} else if ("1".equals(r5105)) {
					if("1".equals(r5033)){
						html.append("<a  onclick=\"return changeHit('"
								+ r5000 + "','" + r5100 + "','"+filepaths+"');\" >");
					} else {
						html.append("<a onclick=\"javascript:alert('文件不允许下载！');\">");
					}

					if (r5113.endsWith("doc") || r5113.endsWith("docx")) {
						html.append("<img src=\"/images/word.png\" />");
					} else if (r5113.endsWith("pdf")) {
						html.append("<img src=\"/images/PDF.png\" />");
					} else if (r5113.endsWith("xls") || r5113.endsWith("xlsx")) {
						html.append("<img src=\"/images/excell.png\" />");
					} else if (r5113.endsWith("ppt") || r5113.endsWith("pptx")) {
						html.append("<img src=\"/images/ppt.png\" />");
					} else {
						html.append("<img src=\"/images/PDF.png\" />");
					}

				} else if ("2".equals(r5105)) {
					if("1".equals(r5033)){
						html.append("<a  onclick=\"return changeView('"
								+PubFunc.encrypt("r5000="+r5000+"&r5100="+r5100+"&filepath=```") + "');\" >");
					} else {
						html.append("<a onclick=\"javascript:alert('文件不允许下载！');\">");
					}
					html.append("<img src=\"/images/txt.png\" />");
				}
				html.append("<h2>" + r5103 + "</h2>");
				html.append("<div style=\"3px\"></div>");
				html.append("<p>");
				if ("3".equals(r5105)) {
					html.append("<img src=\"/images/bofang.png\"  width=\"20\" height=\"20\" align=\"top\" /><span class=\"descspan\">"
							+ r5119 + "次</span>");
				} else {
					html.append("<img src=\"/images/dakai.png\"  width=\"20\" height=\"20\" align=\"top\" /><span class=\"descspan\">"
							+ r5119 + "次</span>");
				}
				html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				if ("3".equals(r5105)) {
					html.append("<img src=\"/images/time.png\"  width=\"20\" height=\"20\" align=\"top\" /><span class=\"descspan\">"
							+ r5117 + "分钟</span>");
				}
				html.append(" </p>");
				html.append("</a></li>");
				
			}
			html.append("</ul></div>"); 
			html.append("</div>");
			html.append("</div>");
		}else{
			html.append("<div data-role=\"page\"  class=\"type-interior\" data-inset=\"true\"><div data-role=\"header\"  data-theme=\"c\"><h1>"
					+ "培训课程"
					+ "</h1></div><div data-role=\"content\">" );
			html.append("课程不存在或已暂停~~~");
			html.append("</div>");
			html.append("</div>");
		}
		return html.toString();
	}

	public String getR5000() {
		return r5000;
	}

	public void setR5000(String r5000) {
		this.r5000 = r5000;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

}
