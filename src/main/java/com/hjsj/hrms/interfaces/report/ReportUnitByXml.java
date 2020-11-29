package com.hjsj.hrms.interfaces.report;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
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

public class ReportUnitByXml {
	
	//参数
	private String params;
	private String actionName;
	private String target;
	private String flag;//操作标识 (1 填报单位信息维护  2 报表状态 )
	private String backdate; //选择时间点
	
	/**
	 * 报表填报单位树
	 * @param params
	 * @param actionName
	 */
	public ReportUnitByXml(String params ,String actionName ,String target , String flag,String backdate) {
		params = PubFunc.keyWord_reback(params);
		params = PubFunc.decrypt(params);
		this.params=params;
		this.actionName = actionName;
		this.target = target;
		this.flag = flag;
		this.backdate = backdate;
	}
	
	public String outPutReportUnitXml() throws GeneralException  {

		//生成的XML文件
		StringBuffer xmls = new StringBuffer();
		//SQL语句//填报单位信息表
		StringBuffer strsql = new StringBuffer();

		//DB相关
		ContentDAO dao = null;
		ResultSet rs = null;		
		Connection conn = AdminDb.getConnection();	
		dao = new ContentDAO(conn);
		//创建xml文件的根元素
		Element root = new Element("TreeNode");
		//设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "reportunit");

		//创建xml文档自身
		Document myDocument = new Document(root);

		//设置跳转字符串
		String theaction = null;

		try {

			//生成SQL语句
			strsql.append(" select * from tt_organization ");
			strsql.append(params);
			
			
			if(params==null||params.trim().length()==0)
			{
				strsql.append(" where 1=1 ");
			}
			Calendar d=Calendar.getInstance();
			
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			if(backdate!=null&&backdate.trim().length()>0&&!"null".equalsIgnoreCase(backdate))
			{
				d.setTime(Date.valueOf(backdate));
				yy=d.get(Calendar.YEAR);
				mm=d.get(Calendar.MONTH)+1;
				dd=d.get(Calendar.DATE);
			}
			
			strsql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			strsql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			strsql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			
			strsql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			strsql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			strsql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			 
			
			strsql.append(" order by a0000");				
			
			
			HashMap isSubUnitMap=getIsSubNode(" select unitcode from tt_organization "+params);
			//执行SQL
			rs = dao.search(strsql.toString());
			
			String status = "";
			
			while (rs.next()) {

				//创建子元素
				Element child = new Element("TreeNode");
				//设置子元素属性
				String unitcode =  rs.getString("unitcode");
				String unitname =  rs.getString("unitname");
				
				child.setAttribute("id", unitcode);
				
				if("2".equals(flag)){
					status = this.getStatus(unitcode);
				}
				
				child.setAttribute("text", unitname + status);
				child.setAttribute("title", unitname);
				if("2".equals(flag)){
					theaction = this.actionName + "?b_query=link&encryptParam=" + PubFunc.encrypt("ucode="+ unitcode+"&backdate="+backdate);//update wangchaoqun 2014-9-28 加密参数				
				}else{
					theaction = this.actionName + "?b_query=link&encryptParam=" + PubFunc.encrypt("code="+ unitcode+"&backdate="+backdate);	//update wangchaoqun 2014-9-28 加密参数			
				}
					
				child.setAttribute("href", theaction);
				child.setAttribute("target", this.target);
				//child.setAttribute("icon", "/images/dept.gif");
				child.setAttribute("icon", "/images/unit.gif");
				if(isSubUnitMap.get(unitcode)!=null){
					//add by wangchaoqun on 2014-9-23 begin
					String encryCode = PubFunc.encrypt("where parentid='" + unitcode + "' and unitcode <> parentid ");
//					child.setAttribute("xml" ,"report_unit_tree.jsp?params=where parentid='" + unitcode + "' and unitcode <> parentid &backdate="+backdate+"");
					child.setAttribute("xml" ,"report_unit_tree.jsp?params=" + encryCode + "&backdate="+backdate+"");
					//add by wangchaoqun on 2014-9-23 end
				}
			//	else
			//		child.setAttribute("xml","");
				
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
	
	
	/**
	 * 填报单位状态
	 * 	
	 *	填报单位树提示填报单位报表状态
	 *	报表状态：
			 未填     所负责的的报表 均为 未填则填报单位提示未填
		     正在编辑  所负责的报表 只要有一个 为正在编辑则填报单位提示正在编辑（比驳回小） 
			 已上报   所负责的报表 都上报 则填报单位提示已上报
			 驳回	 所负责的报表 只要有一个 为驳回则填报单位提示驳回
			 封存     所负责的报表 都封存 则填报单位提示封存
		=-1，未填
		=0,正在编辑
		=1,已上报
		=2,驳回
		=3,封存（基层单位的数据不让修改）
		select status from treport_ctrl  where unitcode ='01'
	 * @param unitCode
	 * @return
	 */
	public boolean getReportStatus(String unitCode , String whereSql){
		boolean b = false;
		String sql="select status from treport_ctrl  where unitcode ='"+unitCode+"' " + whereSql;
		Connection conn1 = null;
		ContentDAO dao = null;
		ResultSet rs1 = null;		
		StringBuffer sql2=new StringBuffer();//dml 2011-04-02 如果某单位开始负责的报表有很多，重新划分表类使其只负责一张报表还能显示多个填报状态 修改为一个状态 
		sql2.append("select reporttypes,report from tt_organization where unitcode='");
		sql2.append(unitCode);
		sql2.append("'");
		String report="";
		String reporttypes="";
		try {
			conn1 = AdminDb.getConnection();	
			//执行SQL
			dao = new ContentDAO(conn1);
			rs1=dao.search(sql2.toString());
			if(rs1.next()){
				reporttypes=Sql_switcher.readMemo(rs1, "reporttypes");
				report=Sql_switcher.readMemo(rs1, "report");
			}
			if(reporttypes!=null&&reporttypes.length()!=0){
				sql2.setLength(0);
				sql2.append("select tabid from tname where tsortId in( ");
				sql2.append(reporttypes.subSequence(0, reporttypes.length()-1));
				sql2.append(")");
				sql+=" and tabid in (";
				sql+=sql2.toString();
				sql+=")";
				if(report!=null&&(report.length()!=1)&&report.length()!=0){
					while(report.indexOf(",,")!=-1){
					report = report.replace(",,", ",");
					}
					if(report.endsWith(",")){
						report = report.substring(0,report.length()-1);
					}
					if(report.startsWith(",")){
						report = report.substring(1,report.length());
					}
					sql+=" and tabid not in (";
						sql+=report;
					sql+=")";
				}
				rs1 = dao.search(sql);
				if (rs1.next()) {
					b = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (conn1 != null) {
					conn1.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return b;
	}
	
	
	public String getStatus(String unitcode){
		String status = "";
		
		if(this.getReportStatus(unitcode ," and status = '2' ")){//驳回
			status ="("+ResourceFactory.getProperty("info.appleal.state2")+")";
			return status;
		}
		if(this.getReportStatus(unitcode ," and status = '0' ")){//正在编辑
			status ="("+ResourceFactory.getProperty("edit_report.status.zzbj")+")";
			return status;
		}
		
		boolean wt = this.getReportStatus(unitcode ," and status = '-1' ");
		boolean ysb = this.getReportStatus(unitcode ," and status = '1' ");
		boolean fc = this.getReportStatus(unitcode ," and status = '3' ");

		if(wt && ! ysb && !fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+")";
			return status;
		}
		
		if(!wt && ysb && !fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.ysb")+")";
			return status;
		}
		
		if(!wt && !ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		
		if(!wt && ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.ysb")+","+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		if(wt && !ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+","+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		if(wt && ysb && !fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+","+ResourceFactory.getProperty("edit_report.status.ysb")+")";
			return status;
		}
		
		if(wt && ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+","+ResourceFactory.getProperty("edit_report.status.ysb")+","+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		
		//系统默认为未填
		status ="("+ResourceFactory.getProperty("edit_report.status.wt")+")";
		
		return status;
	}
}