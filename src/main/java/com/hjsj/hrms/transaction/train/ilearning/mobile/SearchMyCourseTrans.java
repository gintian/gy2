package com.hjsj.hrms.transaction.train.ilearning.mobile;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 查询本次推算的课程列表
 * 
 * @author xuj 2015-5-7
 * 
 */
public class SearchMyCourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String r5000 = (String) this.getFormHM().get("r5000");
		String filepath = (String) this.getFormHM().get("filepath");
		if(filepath==null){
			filepath = (String)hm.get("filepath");
		}
		
		try {
			if("```".equals(filepath)){//文本课件查看内容
				String r5115="";
				try {
					String r5100 =  (String)hm.get("r5100");
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					String sql = "update r51 set r5119=("+Sql_switcher.isnull("r5119", "0")+"+1) where r5000="+r5000+" and r5100="+r5100;
					dao.update(sql);
					
					sql = "select r5115 from r51 where r5100="+r5100;
					
				
					this.frowset=dao.search(sql);
					if(this.frowset.next())
						r5115=this.frowset.getString("r5115");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				this.getFormHM().put("courseintro", r5115);
			}else{
				filepath = SafeCode.decode(filepath);
				String sql = "select a.r5000,b.r5003,c.r5100,c.r5103,c.R5117,c.r5119,c.r5105,c.r5113  "
						+ "from tr_selected_lesson a join r50 b on a.r5000=b.r5000 join r51 c on b.r5000=c.r5000 where a.r5000=? and a.a0100=? and a.nbase=?";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				ArrayList values = new ArrayList();
				values.add(r5000);
				values.add(userView.getA0100());
				values.add(userView.getDbname());
				List recs = ExecuteSQL.executePreMyQuery(sql, values,
						this.getFrameconn());
				this.dohtml(recs, filepath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	/**
	 * <div data-role="page" class="type-interior" id="smain" >
	 * 
	 * <div data-role="header" data-theme="c"> <h1><%=r5003 %></h1> </div> <div
	 * data-role="content"> <div class="content-primary">
	 * <ul data-role="listview">
	 * <%for(int i=0;i<recs.size();i++){
	 * 
	 * LazyDynaBean bean = (LazyDynaBean)recs.get(i); String r5000 =
	 * (String)bean.get("r5000"); String r5100 = (String)bean.get("r5100");
	 * String r5103 =(String) bean.get("r5103"); String r5119 =(String)
	 * bean.get("r5119"); String r5117 =(String) bean.get("r5117"); String r5105
	 * = (String)bean.get("r5105");//课件类型 1普通课件 2文本课件 3多媒体课件 String r5113 =
	 * (String)bean.get("r5113");//课件路径
	 * 
	 * if(filepath.endsWith("/") || filepath.endsWith("\\")) filepath =
	 * filepath.substring(0,filepath.length()-1); String filepaths = filepath +
	 * r5113.toString(); filepaths =
	 * SafeCode.encode(PubFunc.encrypt(filepaths)); %>
	 * <li>
	 * <%if("3".equals(r5105)){ %> <a href=
	 * "/train/resource/mylessons/learncoursebyweixin.jsp?lesson=<%=SafeCode.encode(PubFunc.encrypt(r5000))  %>&course=<%=SafeCode.encode(PubFunc.encrypt(r5100)) %>"
	 * >
	 * 
	 * <img src="/images/shipin.png" /> <%}else if("1".equals(r5105)){%> <a
	 * target="_about" href="/DownLoadCourseware?url=<%=filepaths %>"
	 * onclick="return changeHit('<%=r5000 %>','<%=r5100 %>');"
	 * type="application/msword"> <%
	 * if(r5113.endsWith("doc")||r5113.endsWith("docs")){ %>
	 * 
	 * <img src="/images/word.png" /> <%}else if(r5113.endsWith("pdf")){ %> <img
	 * src="/images/PDF.png" /> <%}else if(r5113.endsWith("excell")){ %> <img
	 * src="/images/excell.png" /> <%}else if(r5113.endsWith("ppt")){ %> <img
	 * src="/images/ppt.png" /> <%}else{ %> <img src="/images/PDF.png" /> <%}
	 * 
	 * }else if("2".equals(r5105)){%> <img src="/images/txt.png" /> <%} %>
	 * <h2><%=r5103 %></h2>
	 * <div style="3px"></div>
	 * <p>
	 * <%if("3".equals(r5105)){ %> <img src="/images/bofang.png" width="20"
	 * height="20" align="top" /><span class="descspan"><%=r5119 %></span>
	 * <%}else{ %> <img src="/images/dakai.png" width="20" height="20"
	 * align="top" /><span class="descspan"><%=r5119 %></span> <%} %>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <%if("3".equals(r5105)){ %> <img
	 * src="/images/time.png" width="20" height="20" align="top" /><span
	 * class="descspan"><%=r5117 %>分钟</span> <%} %>
	 * </p>
	 * </a></li>
	 * <%} %>
	 * 
	 * 
	 * </ul>
	 * </div> </div>
	 */
	private void dohtml(List recs, String filepath) {

		StringBuffer html = new StringBuffer();
		if (recs != null && recs.size() > 0) {
			LazyDynaBean bean = (LazyDynaBean) recs.get(0);
			html.append("<div data-role=\"page\"  class=\"type-interior\" id=\"smain\" data-inset=\"true\"><div data-role=\"header\"  data-theme=\"c\"><h1>"
					+ bean.get("r5003")
					+ "</h1></div><div data-role=\"content\"><div class=\"content-primary\">	<ul data-role=\"listview\">");
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

				if (filepath.endsWith("/") || filepath.endsWith("\\"))
					filepath = filepath.substring(0, filepath.length() - 1);
				String filepaths = filepath + r5113.toString();
				filepaths = SafeCode.encode(PubFunc.encrypt(filepaths));
				html.append("<li>");
				if ("3".equals(r5105)) {
					html.append("<a href=\"/train/resource/mylessons/learncoursebyweixin.jsp?lesson="
							+ SafeCode.encode(PubFunc.encrypt(r5000))
							+ "&course="
							+ SafeCode.encode(PubFunc.encrypt(r5100)) + "\" data-ajax=\"false\">");

					html.append("<img src=\"/images/shipin.png\" />");
				} else if ("1".equals(r5105)) {
					html.append("<a  target=\"_about\"  href=\"/DownLoadCourseware?url="+filepaths +"\"   onclick=\"return changeHit('"
							+ r5000 + "','" + r5100 + "');\"  data-ajax=\"false\">");

					if (r5113.endsWith("doc") || r5113.endsWith("docs")) {
						html.append("<img src=\"/images/word.png\" />");
					} else if (r5113.endsWith("pdf")) {
						html.append("<img src=\"/images/PDF.png\" />");
					} else if (r5113.endsWith("excell")) {
						html.append("<img src=\"/images/excell.png\" />");
					} else if (r5113.endsWith("ppt")) {
						html.append("<img src=\"/images/ppt.png\" />");
					} else {
						html.append("<img src=\"/images/PDF.png\" />");
					}

				} else if ("2".equals(r5105)) {
					html.append("<img src=\"/images/txt.png\" />");
				}
				html.append("<h2>" + r5103 + "</h2>");
				html.append("<div style=\"3px\"></div>");
				html.append("<p>");
				if ("3".equals(r5105)) {
					html.append("<img src=\"/images/bofang.png\"  width=\"20\" height=\"20\" align=\"top\" /><span class=\"descspan\">"
							+ r5119 + "</span>");
				} else {
					html.append("<img src=\"/images/dakai.png\"  width=\"20\" height=\"20\" align=\"top\" /><span class=\"descspan\">"
							+ r5119 + "</span>");
				}
				html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				if ("3".equals(r5105)) {
					html.append("<img src=\"/images/time.png\"  width=\"20\" height=\"20\" align=\"top\" /><span class=\"descspan\">"
							+ r5117 + "分钟</span>");
				}
				html.append(" </p>");
				html.append("</a></li>");
				
			}
			html.append("</ul></div></div>");
		}
		this.getFormHM().put("html", html.toString());
	}
}
