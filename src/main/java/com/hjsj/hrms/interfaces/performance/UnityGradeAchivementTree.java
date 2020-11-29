package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * <p>Title:UnityGradeAchivementTree.java</p>
 * <p>Description>:目标卡制定组织机构树</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 18, 2010 09:15:57 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class UnityGradeAchivementTree {
	
	String flag;
	String codeid;
	String object_type;
	String init;	
	String target_id;

	private Connection con=null;
	private UserView userview=null;	

	public UnityGradeAchivementTree(Connection a_con,UserView userView) 
	{
		this.con=a_con;
		this.userview=userView;
	}
	
	public UnityGradeAchivementTree(String flag,String codeid,String object_type,String init,String target_id,UserView userView) 
	{
		this.flag=flag;
		this.codeid=codeid;
		this.object_type=object_type;
		this.init=init;
		this.target_id=target_id;
		this.userview=userView;
	 }
	/*
	 * 获得已经存在的考核对象	
	 */
			
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
		
		String actionname="/performance/achivement/achivementTask.do";
		String theaction = "";

		ArrayList list = getInfoList();
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String unitcode = (String) abean.get("codeitemid");
			String unitname = (String) abean.get("codeitemdesc");
			String codeset = (String) abean.get("codesetid");
			String flag = (String) abean.get("flag");
			child.setAttribute("id", unitcode);
			child.setAttribute("text", unitname);
			child.setAttribute("title", unitname);
				
			theaction = actionname + "?b_query=link&encryptParam="+PubFunc.encrypt("paramd=0&a_code="+codeset+unitcode);	
						
			child.setAttribute("href", theaction);
			child.setAttribute("target", "mil_bodyto");
			
			 if("UN".equals(codeset))
	                child.setAttribute("icon","/images/unit.gif");
			 else if("UM".equals(codeset))
	                child.setAttribute("icon","/images/dept.gif");
	         else if ("@K".equalsIgnoreCase(codeset))
					child.setAttribute("icon", "/images/pos_l.gif");
	         else if ("ps".equalsIgnoreCase(codeset))
	        	 child.setAttribute("icon", "/images/man.gif");
	         			
			child.setAttribute("xml", "/performance/achivement/achivementTask/achivement_taskBigtree.jsp?init=2&object_type="+this.object_type+"&flag="+flag+"&target_id="+this.target_id+"&codeid=" + unitcode);
						
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
		ResultSet rs = null;
		ResultSet rst = null;
		ResultSet ress = null;
		Connection conn=null;
		ArrayList list=new ArrayList();
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
//			ContentDAO dao1 = new ContentDAO(conn);
//			ContentDAO dao2 = new ContentDAO(conn);
			// SQL语句			
			StringBuffer strsql = new StringBuffer("select * from organization where ");			
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			if("-1".equals(this.codeid))
			{
				strsql.append("parentid=codeitemid and codesetid<>'@K'");
			}
			else		
			{					
				if("2".equals(this.init))
				{		
					if("1".equals(this.object_type))
					{
						strsql.append("parentid='"+this.codeid+"' and parentid<>codeitemid and codesetid<>'@K'");	
					}else{
						strsql.append("parentid='"+this.codeid+"' and parentid<>codeitemid");						
					}					
				}							
			}
			strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
			rs = dao.search(strsql.toString()+"  order by codesetid,A0000");	
			
			if(this.flag!=null && this.flag.length()>0){
				if("2".equals(this.flag)){
					
					String s=("select a0100 from usrA01 where E0122='"+this.codeid+"' order by A0000");					
					ress = dao.search(s.toString());	
				}
			}
			
			while (rs.next()) 
			{
				LazyDynaBean lazyDynaBean=new LazyDynaBean();
				String codesetid=rs.getString("codesetid");
				String codeitemid=rs.getString("codeitemid");
				StringBuffer sql = new StringBuffer();				
				int num=0;
				
				// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
				String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理			
				if("1".equals(this.object_type))
				{
					sql.append("select object_id from per_target_mx where object_id like '" +codeitemid+ "%' and target_id="+this.target_id+" ");															
					
					if (operOrg!=null && operOrg.length() > 3)
					{
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int i = 0; i < temp.length; i++)
						{
						    tempSql.append(" or per_target_mx.object_Id like '" + temp[i].substring(2) + "%'");
						}
						sql.append(" and ( " + tempSql.substring(3) + " ) ");
					}
					else if((!this.userview.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
					{
						String codeid=userview.getManagePrivCode();
						String codevalue=userview.getManagePrivCodeValue();
						String a_code=codeid+codevalue;
						
						if(a_code.trim().length()<=0)
						{
							sql.append(" and 1=2 ");
						}
						else if(!("UN".equals(a_code)))
						{
							sql.append(" and per_target_mx.object_Id like '"+codevalue+"%' "); 							
						}
					}
					sql.append(" group by object_id");						
					
					rst = dao.search(sql.toString());
					while(rst.next()){
						num=1;
					}
				}
				
				if("2".equals(this.object_type))
				{
					if("UN".equalsIgnoreCase(codesetid))
					{
						sql.append("select B0110 from per_target_mx where B0110 like '" +codeitemid+ "%' and target_id="+this.target_id+" ");
						
						StringBuffer buf = new StringBuffer();				
						if (operOrg!=null && operOrg.length() > 3)
						{					 
							StringBuffer tempSql = new StringBuffer("");
							String[] temp = operOrg.split("`");
							for (int i = 0; i < temp.length; i++)
							{
								if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
								    tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
								else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
								    tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
							}
							buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
							 
						}
						else if((!this.userview.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
						{
							String priStrSql = InfoUtils.getWhereINSql(this.userview,"Usr");
							if(priStrSql.length()>0)
							{
								buf.append("select usra01.A0100 ");
								buf.append(priStrSql);
							}
						}
						if(buf.length()>0)
						{
							sql.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
						}
						sql.append(" group by B0110");
						
						rst = dao.search(sql.toString());
						while(rst.next()){
							num=1;
						}
					}
					if("UM".equalsIgnoreCase(codesetid))
					{
						sql.append("select E0122 from per_target_mx where E0122 like '" +codeitemid+ "%' and target_id="+this.target_id+" ");
						
						StringBuffer buf = new StringBuffer();				
						if (operOrg!=null && operOrg.length() > 3)
						{					 
							StringBuffer tempSql = new StringBuffer("");
							String[] temp = operOrg.split("`");
							for (int i = 0; i < temp.length; i++)
							{
								if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
								    tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
								else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
								    tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
							}
							buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
							 
						}
						else if((!this.userview.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
						{
							String priStrSql = InfoUtils.getWhereINSql(this.userview,"Usr");
							if(priStrSql.length()>0)
							{
								buf.append("select usra01.A0100 ");
								buf.append(priStrSql);
							}
						}
						if(buf.length()>0)
						{
							sql.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
						}
						sql.append(" group by E0122");
						
						rst = dao.search(sql.toString());
						while(rst.next()){
							num=1;
							lazyDynaBean.set("flag","2");
						}
					}
					if("@K".equalsIgnoreCase(codesetid))
					{
						if(ress!=null)
						{
							ress.beforeFirst();
							while (ress.next()) 
							{
								ress.getRow();
								StringBuffer str = new StringBuffer();
								String a0100=ress.getString("a0100");	
								//此处需要考虑人员的岗位，否则会出现人员重复的现象  haosl update 2018年1月11日
								str.append("select DISTINCT ptm.a0100,ptm.a0101,ptm.A0000 from per_target_mx ptm,usra01 where usra01.a0100=ptm.a0100 and usra01.e01a1='"+codeitemid+"' and ptm.a0100='" + a0100 + "' and ptm.target_id="+this.target_id+" ");	
								
								StringBuffer buf = new StringBuffer();				
								if (operOrg!=null && operOrg.length() > 3)
								{					 
									StringBuffer tempSql = new StringBuffer("");
									String[] temp = operOrg.split("`");
									for (int i = 0; i < temp.length; i++)
									{
										if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
										    tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
										else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
										    tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
									}
									buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
									 
								}
								else if((!this.userview.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
								{
									String priStrSql = InfoUtils.getWhereINSql(this.userview,"Usr");
									if(priStrSql.length()>0)
									{
										buf.append("select usra01.A0100 ");
										buf.append(priStrSql);
									}
								}
								if(buf.length()>0)
								{
									str.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
								}
								str.append(" order by ptm.A0000");
								
								rst = dao.search(str.toString());
								while(rst.next())
								{
									LazyDynaBean abean=new LazyDynaBean();
									abean.set("codeitemdesc", rst.getString("a0101"));
									abean.set("codeitemid", rst.getString("a0100"));
									abean.set("codesetid","ps");								
									list.add(abean);
								}
							}
						}
					}					
				}								
				if(num==1)
				{
					lazyDynaBean.set("codesetid",rs.getString("codesetid"));
					lazyDynaBean.set("codeitemid",rs.getString("codeitemid"));
					lazyDynaBean.set("codeitemdesc",rs.getString("codeitemdesc"));
					list.add(lazyDynaBean);
				}				
			}										
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					PubFunc.closeDbObj(rs);
//					rs.close();
				}
				if (rst != null) {
					PubFunc.closeDbObj(rst);
					//rs.close();
				}
				if (ress != null) {
					PubFunc.closeDbObj(ress);
					//rs.close();
				}
				if (conn != null) {
					PubFunc.closeDbObj(conn);
					//conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
