package com.hjsj.hrms.interfaces.report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;

public class ReportCollectUnitByXml {
	
	//查询参数
	private String params;
	private String isAction="1";  //1:有action 方法 0：无action 2:精算报表
	private String cycle_id="";   //精算报表填报周期
	
	public ReportCollectUnitByXml() {

	}
	public ReportCollectUnitByXml(String params,String isAction) {
		params = PubFunc.keyWord_reback(params);
		params = PubFunc.decrypt(params);
		this.params=params;
		this.isAction=isAction;
	}
	
	public ReportCollectUnitByXml(String params) {
		this.params=params;
	}
	public String outPutReportUnitXml() throws GeneralException  {

		//TTorganization ttorganization=new TTorganization(AdminDb.getConnection());		
		//生成的XML文件
		StringBuffer xmls = new StringBuffer();
		//SQL语句//填报单位信息表
		StringBuffer strsql = new StringBuffer();

		//DB相关
		ResultSet rs = null;		
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
		//创建xml文件的根元素
		Element root = new Element("TreeNode");
		//设置根元素属性
		root.setAttribute("id", "0");
		root.setAttribute("text","");
		root.setAttribute("title","");

		//创建xml文档自身
		Document myDocument = new Document(root);

		//设置跳转字符串
		String actionname = "/report/report_collect/reportOrgCollecttree.do";
		String theaction = null;

		try {
			//执行SQL
			dao = new ContentDAO(conn);
			//生成SQL语句
			strsql.append(" select * from tt_organization ");
			strsql.append(params);
			
			StringBuffer ext_sql=new StringBuffer("");
			if(isAction!=null&& "2".equals(isAction))
			{
				Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				if(cycle_id!=null&&cycle_id.trim().length()>0)
				{
					rs = dao.search("select bos_date from tt_cycle where id="+cycle_id);
					if(rs.next())
					{
						Date date=rs.getDate(1);
						d.setTime(date);
						yy=d.get(Calendar.YEAR);
						mm=d.get(Calendar.MONTH)+1;
						dd=d.get(Calendar.DATE);
					}
				}
				
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
				strsql.append(ext_sql.toString());
			}else{
				Calendar d=Calendar.getInstance();
				
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				
				
				strsql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				strsql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				strsql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				
				strsql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				strsql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				strsql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
				
			}
			
			
			
			strsql.append(" order by a0000");				
			HashMap isSubUnitMap=getIsSubNode(" select unitcode from tt_organization "+params+ext_sql.toString());
			
			rs = dao.search(strsql.toString());

			while (rs.next()) {

				//创建子元素
				Element child = new Element("TreeNode");
				//设置子元素属性
				String unitcode =  rs.getString("unitcode");
				String unitname =  rs.getString("unitname");
				
				child.setAttribute("id", unitcode);
				child.setAttribute("text", unitname);
				child.setAttribute("title", unitname);
				theaction = actionname + "?b_query=link&encryptParam=" + PubFunc.encrypt("a_code="+ unitcode+"&operateObject=2");	//update wangcq 2014-9-26			
				
				if("1".equals(this.isAction))
				{
					child.setAttribute("href", theaction);
					child.setAttribute("target", "mil_body");
				}
				
				if("2".equals(this.isAction))
				{
					child.setAttribute("href", "/report/actuarial_report/report_collect.do?b_query=link&encryptParam="+ PubFunc.encrypt("a_code="+ unitcode)); //update wangcq 2014-9-26	
					child.setAttribute("target", "mil_body");
				}
				if("3".equals(this.isAction))
				{
					//child.setAttribute("href", "/report/edit_report/printReport.do?b_query=link&a_code="+ unitcode);
					child.setAttribute("target", "mil_body");
				}
				//child.setAttribute("icon", "/images/dept.gif");
				child.setAttribute("icon", "/images/unit.gif");
				if(isSubUnitMap.get(unitcode)!=null)
					child.setAttribute("xml" ,"report_org_collect_tree.jsp?params="+PubFunc.encrypt("where parentid='" + unitcode + "' and parentid!=unitcode")+"&isAction="+this.isAction+"&cycle_id="+cycle_id);
				
				//将子元素作为内容添加到根元素
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();

			//格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			//将生成的XML文件作为字符串形式
			xmls.append(outputter.outputString(myDocument));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return xmls.toString();

	}
	
	
	public HashMap getIsSubNode(String where_str)
	{
		HashMap map=new HashMap();
//		DB相关
		Statement stmt = null;
		ResultSet rs = null;		
		Connection conn =null;	
		try {
			String sql="select * from tt_organization where parentid in ( "+where_str+" ) and parentid<>unitcode ";
			conn = AdminDb.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				map.put(rs.getString("parentid"), "1");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(stmt);
			PubFunc.closeDbObj(conn);
		}
		return map;
	}
	public String getCycle_id() {
		return cycle_id;
	}
	public void setCycle_id(String cycle_id) {
		this.cycle_id = cycle_id;
	}
}