package com.hjsj.hrms.interfaces.hire;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
public class OrganizationByXml {
	
	String flag;
	String codeid;
	String model;
	String init;
	public OrganizationByXml() {
	}
	
	public OrganizationByXml(String flag,String codeid,String model,String init) {
		this.flag=flag;
		this.codeid=codeid;
		this.model=model;
		this.init=init;
	 }

	public String getRootName()
	{
		String rootdesc="";
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
			rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
			if(rootdesc==null||rootdesc.length()<=0)
			{
				rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
			}
			
		}
		catch (Exception e) {
				e.printStackTrace();
		} 
		finally {
				try {					
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return rootdesc;
	}
	
	public LazyDynaBean getOrgInfo(String acodeid)
	{
		Statement stmt = null;
		ResultSet rs = null;	
		Connection conn=null;
		LazyDynaBean lazyDynaBean=new LazyDynaBean();
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			//stmt = conn.createStatement();
			// SQL语句
			StringBuffer strsql = new StringBuffer("select * from organization where codeitemid='"+acodeid+"' ");
			//rs = stmt.executeQuery(strsql.toString());
			rs = dao.search(strsql.toString());
			if (rs.next()) {
				
				lazyDynaBean.set("codesetid",rs.getString("codesetid"));
				lazyDynaBean.set("codeitemid",rs.getString("codeitemid"));
				lazyDynaBean.set("codeitemdesc",rs.getString("codeitemdesc"));
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(stmt);
			PubFunc.closeDbObj(conn);
		}
		return lazyDynaBean;
		
	}
	
	
	public String outPutOrgXml() throws GeneralException {

		// 生成的XML文件
		StringBuffer xmls = new StringBuffer();
		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "organization");
		// 创建xml文档自身
		Document myDocument = new Document(root);
		// 设置跳转字符串
		String actionname ="";
		if("1".equals(this.model)|| "2".equals(this.model)|| "3".equals(this.model))
			actionname="/hire/demandPlan/positionDemand/positionDemandTree.do";
		else if("4".equals(this.model))	//人员筛选
			actionname="/hire/employActualize/personnelFilter/personnelFilterTree.do";
		else if("5".equals(this.model)|| "6".equals(this.model))	//面试安排 || 面试通知
			actionname="/hire/interviewEvaluating/interviewArrange.do";
		else if("7".equals(this.model))   //面试考核
			actionname="/hire/interviewEvaluating/interviewExamine.do";
		else if("8".equals(this.model))   //员工录用
			actionname="/hire/employSummarise/personnelEmploy.do";
		else if("10".equals(this.model))   //培训计划审核
			actionname="/train/plan/searchCreatPlanList.do";
		else if("11".equals(this.model))   //绩效实施
			actionname="/performance/implement/performanceImplement.do";
		else if("12".equals(this.model) || "15".equals(this.model))   //绩效评估
			actionname="/performance/evaluation/performanceEvaluation.do";
		else if("13".equals(this.model))   //招聘订单
			actionname="/hire/demandPlan/hireOrder.do";		
		else if("14".equals(this.model))   //奖金管理
			actionname="/gz/bonus/inform.do";
		else if("16".equals(this.model))//考核计划
			actionname="/performance/kh_plan/examPlanList.do";		
		else if("17".equals(this.model))//考核关系
			actionname="/performance/options/kh_relation.do";	
		else if("18".equals(this.model))//培训自助计划审核
			actionname="/train/plan/searchCreatPlanList.do";	
		else if("19".equals(this.model))//绩效自助考核表分发
			actionname="/selfservice/performance/performanceImplement.do";
		else if("20".equals(this.model)|| "21".equals(this.model)|| "22".equals(this.model))//绩效自助 业绩数据录入
			actionname="/performance/achivement/dataCollection/dataCollect.do";
		else if("25".equals(this.model)|| "26".equals(this.model))//人事异动 审批关系
			actionname="/general/relation/relationobjectlist.do";
		String theaction = "";

		ArrayList list= null;
			if("26".equals(this.model)){
				list = getGenrelationInfoList();	
			}else{
			list = getInfoList();
			}

		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String unitcode = (String) abean.get("codeitemid");
			String unitname = (String) abean.get("codeitemdesc");
			String codeset=(String)abean.get("codesetid");
			child.setAttribute("id", unitcode);
			child.setAttribute("text", unitname);
			child.setAttribute("title", unitname);
			
			if("11".equals(this.model)|| "12".equals(this.model))
			{
				theaction = actionname + "?b_query=link&encryptParam="+PubFunc.encrypt("codeset="+codeset+"&code=" + unitcode+"&operate=init");
			}
			else if("13".equals(this.model) || "14".equals(this.model) || "19".equals(this.model))   //招聘订单|奖金管理|考核表分发
			    theaction = actionname + "?b_query=link&encryptParam="+PubFunc.encrypt("a_code="+codeset+unitcode);
			else if("16".equals(this.model))//考核计划
				theaction = actionname + "?b_query=query&encryptParam="+PubFunc.encrypt("a_code="+codeset+unitcode);
			else if("15".equals(this.model))//绩效评估统一打分
				  theaction = actionname + "?b_rate=link&encryptParam="+PubFunc.encrypt("codeset="+codeset+"&code=" + unitcode);
			else if("17".equals(this.model))//考核关系
				 theaction = actionname + "?b_queryObj=link&encryptParam="+PubFunc.encrypt("a_code="+codeset+unitcode);
			else if("18".equals(this.model))   //培训自助计划审核
			    theaction = actionname + "?b_query=link&operate=init&model=2&encryptParam="+PubFunc.encrypt("a_code="+codeset+unitcode);
			else if("20".equals(this.model)|| "21".equals(this.model)|| "22".equals(this.model))//绩效自助 业绩数据录入
				theaction = actionname + "?b_query2=link&encryptParam="+PubFunc.encrypt("a_code="+codeset+unitcode);
			else if("25".equals(this.model)|| "26".equals(this.model))
			{
				theaction = actionname + "?b_query=link&encryptParam="+PubFunc.encrypt("codeset="+codeset+"&code=" + unitcode+"&operate=init2");
			}
			else
			{
				if("10".equals(this.model))
					theaction = actionname + "?b_query=link&encryptParam="+PubFunc.encrypt("codeset="+codeset+"&code=" + unitcode+"&model=2");
				else if(!"2".equals(this.model)&&!"3".equals(this.model))
					theaction = actionname + "?b_query=link&encryptParam="+PubFunc.encrypt("codeset="+codeset+"&code=" + unitcode+"&model="+this.model);
				else if("2".equals(this.model))
					theaction = actionname + "?b_query2=link&encryptParam="+PubFunc.encrypt("codeset="+codeset+"&code=" + unitcode+"&model="+this.model);
				else if("3".equals(this.model))
					theaction = actionname + "?b_query3=link&encryptParam="+PubFunc.encrypt("codeset="+codeset+"&code=" + unitcode+"&model="+this.model);
				
				
				if("4".equals(this.model)|| "5".equals(this.model)|| "6".equals(this.model)|| "7".equals(this.model)|| "8".equals(this.model)|| "10".equals(this.model))
					theaction+="&operate=init";
			}
			child.setAttribute("href", theaction);
			
			if("11".equals(this.model) || "17".equals(this.model)|| "25".equals(this.model)|| "26".equals(this.model) )   //绩效实施,人事异动审批关系
				child.setAttribute("target", "ril_body1");
			else
				child.setAttribute("target", "mil_body");
			
			 if("UN".equals(codeset))
	                child.setAttribute("icon","/images/unit.gif");
			 else if("UM".equals(codeset))
	                child.setAttribute("icon","/images/dept.gif");
	         else if ("@K".equalsIgnoreCase(codeset))
					child.setAttribute("icon", "/images/pos_l.gif");
	         else if("26".equals(this.model))
				{
	        	 child.setAttribute("icon", "/images/groups.gif");
				}
			 
	         
			child.setAttribute("xml", "/hire/demandPlan/positionDemand/position_demand_tree.jsp?flag="
					+ this.flag + "&codeid=" + unitcode+"&model="+this.model+"&init=2");
			// 将子元素作为内容添加到根元素
			root.addContent(child);
		}

		XMLOutputter outputter = new XMLOutputter();

		// 格式化输出类
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);

		// 将生成的XML文件作为字符串形式
		xmls.append(outputter.outputString(myDocument));

		return xmls.toString();

	}
	
	
	
	
	public ArrayList getInfoList()
	{
        // DB相关
		//Statement stmt = null;
		ResultSet rs = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			//stmt = conn.createStatement();

			// SQL语句
			StringBuffer strsql = new StringBuffer("select * from organization where ");
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			if("-1".equals(this.codeid))
			{
				strsql.append("parentid=codeitemid and codesetid<>'@K'");
		        //strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");

			}
			else		
			{	
				if(this.codeid.indexOf("`")==-1)
				{
					if("2".equals(this.init))
					{
						if("17".equals(this.model)|| "20".equals(this.model)|| "25".equals(this.model))//绩效模块 考核关系 业绩数据录入人员计划 机构树展示到职位
							strsql.append("parentid='"+this.codeid+"' and parentid<>codeitemid ");
						else if("21".equals(this.model))//绩效模块 业绩数据录入单位计划 机构树展示到单位
							strsql.append("parentid='"+this.codeid+"' and parentid<>codeitemid and codesetid<>'UM'");
						else 							
							strsql.append("parentid='"+this.codeid+"' and parentid<>codeitemid and codesetid<>'@K'");
				     	//strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
					}
					else
					{
						strsql.append("codeitemid='"+this.codeid+"' and codesetid<>'@K'");
						//strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
					}
				}
				else
				{
					strsql.append(" ( ");
					StringBuffer tempsql=new StringBuffer("");
					String[] temp=this.codeid.split("`");
					HashMap map = this.getPrivMange(temp);
					for(int i=0;i<temp.length;i++)
					{
						if(map.get(temp[i].substring(2))==null)
					    	tempsql.append(" or codeitemid='"+temp[i].substring(2)+"'");
					}
					strsql.append(tempsql.substring(3)+" ) ");
				}
			
			}
			strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
			strsql.append("  order by a0000,codeitemid ");
			//rs = stmt.executeQuery(strsql.toString()+"");
			rs = dao.search(strsql.toString());
			while (rs.next()) {
				LazyDynaBean lazyDynaBean=new LazyDynaBean();
				lazyDynaBean.set("codesetid",rs.getString("codesetid"));
				lazyDynaBean.set("codeitemid",rs.getString("codeitemid"));
				lazyDynaBean.set("codeitemdesc",rs.getString("codeitemdesc"));
				list.add(lazyDynaBean);
			}
			rs.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
//				if (stmt != null) {
//					stmt.close();
//				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public ArrayList getGenrelationInfoList()
	{
        // DB相关
		
		ResultSet rs = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		try{
			StringBuffer strcontent=new StringBuffer();
			StringBuffer strsql=new StringBuffer();			
			String mygroupid=null;
			boolean bflag=false;
				bflag=true;
				String groupid = this.codeid;
				if(this.init==null|| "2".equals(this.init))
				{
					if("1".equals(groupid))
					{
						return list;
					}else{
					strsql.append("select username,fullname,a.groupid as groupid,b.groupid as sss,userflag,roleid from operuser a left join usergroup b ");
					strsql.append(" on a.username=b.groupname ");		
					strsql.append(" where roleid=1 and a.groupid="+groupid+" and a.groupid<>b.groupid order by sss");	
					}
				}else{
					
					strsql.append("select username,fullname,a.groupid as groupid,b.groupid as sss,userflag,roleid from operuser a left join usergroup b ");
					strsql.append(" on a.username=b.groupname ");		
					if("1".equals(groupid))
						strsql.append(" where roleid=1 and a.groupid="+groupid+" order by sss");
					else
						strsql.append(" where roleid=1 and b.groupid="+groupid+" order by sss");
				}
			
				conn = (Connection) AdminDb.getConnection();
				ContentDAO dao=new ContentDAO(conn);

			rs = dao.search(strsql.toString());
			while (rs.next()) {
				LazyDynaBean lazyDynaBean=new LazyDynaBean();
				lazyDynaBean.set("codesetid",rs.getString("groupid"));
				lazyDynaBean.set("codeitemid",rs.getString("sss"));
				lazyDynaBean.set("codeitemdesc",rs.getString("username"));
				list.add(lazyDynaBean);
			}
			
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
	
		return list;
	}
	public HashMap getPrivMange(String[] temp)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer("");
			StringBuffer b_buf = new StringBuffer("");
			for(int i=0;i<temp.length;i++)
			{
				String str=temp[i].substring(2);
				buf.append("`"+str);
				for(int j=0;j<temp.length;j++)
				{
					String str2=temp[j].substring(2);;
					if(!str2.equalsIgnoreCase(str)&&str2.startsWith(str))
					{
						map.put(str2, str2);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}