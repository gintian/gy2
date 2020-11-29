package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
public class ReportSetByXml {
	
	String flag;
	String codeid;
	String userName;
	UserView userView;
	String operate;
	
	
	String unitCode;
	String reportSet;
	String status;
	public ReportSetByXml() {
	}
	
	
	public ReportSetByXml(String statusInfo,String flag)
	{
		String[] temp=statusInfo.split("/");
		this.unitCode=temp[0];
		this.reportSet=temp[1];
		this.status=temp[2];
		this.flag=flag;
	}
	
	
	public String outPutReportSetXml2() throws GeneralException  {

		//生成的XML文件
		StringBuffer xmls = new StringBuffer();		
		//创建xml文件的根元素
		Element root = new Element("TreeNode");
		//设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "reportunit");
		//创建xml文档自身
		Document myDocument = new Document(root);
		//设置跳转字符串
		
		String actionname ="";
		String theaction = "";
		
		ArrayList list=getInfoList2();
		
			for(Iterator t=list.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				//创建子元素
				Element child = new Element("TreeNode");
				//设置子元素属性
				String unitcode =temp[0];
				String unitname =temp[1];
				child.setAttribute("id", unitcode);
				child.setAttribute("text",unitcode+":"+unitname);
				child.setAttribute("title", unitname);	
				if("2".equals(flag))
				{
						theaction="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code="+unitCode+"&tabid="+unitcode+"&operateObject=2";
				}
				child.setAttribute("href", theaction);
				child.setAttribute("target", "mil_body");
				if("1".equals(flag))
					child.setAttribute("icon", "/images/open.png");
				else
					child.setAttribute("icon", "/images/prop_ps.gif");
				if(!"2".equals(flag))
				{	
						child.setAttribute("xml" ,"report_set_tree.jsp?statusInfo="+this.unitCode+"/"+unitcode+"/"+this.status+"&flag=2");				
				}
				
				
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
			//System.out.println(xmls.toString());
			return xmls.toString();
	}
	
	
	
	
	
	
	
	
	
	public ReportSetByXml(String flag,String codeid,String userName,UserView userView,String operate) {
		this.flag=flag;
		this.codeid=codeid;
		this.userName=userName;
		this.userView=userView;
		this.operate=operate;
	 }

	public String outPutReportSetXml() throws GeneralException  {

		//生成的XML文件
		StringBuffer xmls = new StringBuffer();		
		//创建xml文件的根元素
		Element root = new Element("TreeNode");
		//设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "reportunit");
		//创建xml文档自身
		Document myDocument = new Document(root);
		//设置跳转字符串
		
		String actionname ="";
		if(!"collect".equals(operate))
			actionname="/report/edit_report/reportSettree.do";
		else
			actionname="/report/report_collect/reportOrgCollecttree.do";
		String theaction = "";
		
		ArrayList list=getInfoList();
		
			for(Iterator t=list.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				
				//创建子元素
				Element child = new Element("TreeNode");
				//设置子元素属性
				String unitcode =temp[0];
				String unitname =temp[1];
				
				child.setAttribute("id", unitcode);
				child.setAttribute("text",unitcode+":"+unitname);
				child.setAttribute("title", unitname);	
				if("2".equals(flag))
				{
					if(!"collect".equals(operate))
						theaction = "javascript:void(0)";				
					else
						theaction="javascript:void(0)";
				}
				child.setAttribute("href", theaction);
				child.setAttribute("target", "mil_body");
				if("1".equals(flag))
					child.setAttribute("icon", "/images/open.png");
				else
					child.setAttribute("icon", "/images/prop_ps.gif");
				
				if(!"2".equals(flag))
				{
					
						child.setAttribute("xml" ,"/system/options/customreport/report_set_tree.jsp?flag=2&operate="+operate+"&codeid="+unitcode+"&userName="+userName);				
					
				}
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
			
		

		return xmls.toString();

	}
	
	public ArrayList getInfoList2()
	{
        //		DB相关
		ResultSet rs = null;	
		ResultSet rs2=null;
		Connection conn=null;
		ArrayList list=new ArrayList();
		try
		{
				conn = AdminDb.getConnection();					
				ContentDAO dao = new ContentDAO(conn);
				if("1".equals(flag))
				{			
					//	SQL语句
					StringBuffer strsql = new StringBuffer("");
					String[] reportTypes=null;
					if("-1".equals(this.reportSet))
					{
						strsql.append("select reporttypes from tt_organization  where unitcode='"+this.unitCode+"'");		
						rs = dao.search(strsql.toString());	
						if(rs.next())
							 reportTypes=(Sql_switcher.readMemo(rs,"reporttypes")).trim().split(",");
					}
					else
					{
						reportTypes=new String[1];
						reportTypes[0]=this.reportSet;
					}
					StringBuffer sql=new StringBuffer("select tsortid,name from tsort where ");
					if(reportTypes!=null)
					{						
						StringBuffer sql_sub=new StringBuffer("");
						for(int i=0;i<reportTypes.length;i++)
						{
							sql_sub.append(" or ");
							sql_sub.append(" tsortid=");
							sql_sub.append(reportTypes[i]);
						}
						String sql1=sql.append(sql_sub.substring(3)).toString();						
						rs2=dao.search(sql1+" order by tsortid");
						while(rs2.next())
						{
							
							String[] temp=new String[3];
							temp[0]=rs2.getString(1);
							temp[1]=rs2.getString(2);
							temp[2]="";
							list.add(temp);
						}
						
					}	
				}
				else
				{
					ArrayList treport_list=new ArrayList(); 
					StringBuffer sql=new StringBuffer("");
					{
						sql.append("select tname.tabid,tname.name,treport_ctrl.status from treport_ctrl,tname where treport_ctrl.tabid=tname.tabid and unitcode='");
						sql.append(this.unitCode);
						sql.append("' and treport_ctrl.tabid in (select TabId from tname  where TSortId="+this.reportSet+" ) ");
						if(!"-2".equals(this.status))
							sql.append(" and status="+this.status);
					}				
					rs = dao.search(sql.toString());
					while(rs.next())
					{
						String[] temp=new String[3];
						String tabid=rs.getString("tabid");
						String tabName=rs.getString("Name");
						String status1=rs.getString("status");
	
						temp[0]=tabid;
						temp[1]=tabName; //+a_status;
						temp[2]=status1;
						list.add(temp);
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if(rs2!=null)
					rs2.close();
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * 判断表类下是否有报表
	 * @param sortid
	 * @return
	 */
	public boolean isNode(String sortid)
	{
		boolean flag=false;
		ResultSet rs = null;
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();					
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql=new StringBuffer("");
			sql.append("select * from treport_ctrl where unitcode=(select unitcode from operUser where userName='");
			sql.append(userName);
			sql.append("') and tabid in (select TabId from tname  where TSortId="+sortid+" )");
			rs = dao.search(sql.toString());
			int num=0;
			while(rs.next())
			{
				String tabid=rs.getString("tabid");
				if(!this.userView.isHaveResource(IResourceConstant.REPORT,tabid))
					continue;
				num++;
			}
			if(num>0)
				flag=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
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
		return flag;
	}
	
	
	public ArrayList getInfoList()
	{
        //		DB相关
		ResultSet rs = null;	
		ResultSet rs2=null;
		Connection conn=null;
		ArrayList list=new ArrayList();
		try
		{
				conn = AdminDb.getConnection();					
				ContentDAO dao = new ContentDAO(conn);
				if("1".equals(flag))
				{			
					//	SQL语句
					StringBuffer strsql = new StringBuffer("");
					String[] reportTypes=null;
					//if(!operate.equals("collect"))
					{
						strsql.append("select reporttypes from operUser,tt_organization  where operUser.unitcode=tt_organization.unitcode  and  username='");
						strsql.append(userName);
						strsql.append("'");				
						
						rs = dao.search(strsql.toString());	
						if(rs.next())
						{
							    reportTypes=(Sql_switcher.readMemo(rs,"reporttypes")).trim().split(",");						
						}
					}
					StringBuffer sql=new StringBuffer("select tsortid,name from tsort where ");
					
					if(reportTypes!=null)
					{						
						StringBuffer sql_sub=new StringBuffer("");
						for(int i=0;i<reportTypes.length;i++)
						{
							sql_sub.append(" or ");
							sql_sub.append(" tsortid=");
							sql_sub.append(reportTypes[i]);
						}
						String sql1=sql.append(sql_sub.substring(3)).toString();						
						rs2=dao.search(sql1+" order by tsortid");
						while(rs2.next())
						{
							if(isNode(rs2.getString("tsortid")))
							{
								String[] temp=new String[3];
								temp[0]=rs2.getString(1);
								temp[1]=rs2.getString(2);
								temp[2]="";
								list.add(temp);
							}
						}
						
					}	
				}
				else
				{
					ArrayList treport_list=new ArrayList(); 
					StringBuffer sql=new StringBuffer("");
					sql.append("select * from treport_ctrl where unitcode=(select unitcode from operUser where userName='");
					sql.append(userName);
					sql.append("') and tabid in (select TabId from tname  where TSortId="+codeid+" )");
					rs = dao.search(sql.toString());
					while(rs.next())
					{
						String[] temp=new String[2];
						temp[0]=rs.getString("tabid");
						if(!"collect".equals(operate))
							temp[1]=rs.getString("status");
						else
							temp[1]="";
						treport_list.add(temp);
					}
					String sql2="select TabId,Name from tname  where TSortId="+codeid+"  order by tabid";
					rs = dao.search(sql2.toString());
					while(rs.next())
					{
						String[] temp=new String[3];
						String tabid=rs.getString("TabId");
						String tabName=rs.getString("Name");
						String status="-1";
						
						String a_status="";
						if(!"collect".equals(operate))
						{
							a_status="(未填)";
							if(!this.userView.isHaveResource(IResourceConstant.REPORT,tabid))
								continue;
							
							boolean is=false;
							for(Iterator t=treport_list.iterator();t.hasNext();)
							{
								String[] temp2=(String[])t.next();
								if(temp2[0].equals(tabid))
								{
									is=true;
									if("0".equals(temp2[1]))
									{
										a_status="(正在编辑)";
									}
									else if("1".equals(temp2[1]))
									{
										a_status="(已上报)";
									}
									else if("2".equals(temp2[1]))
									{
										a_status="(驳回)";
									}
									else if("3".equals(temp2[1]))
									{
										a_status="(封存)";
									}						
									status=temp2[1];
									break;
								}
							}
							
							if(!is)
								continue;
						}
						else
						{
							boolean is=false;
							for(Iterator t=treport_list.iterator();t.hasNext();)
							{
								String[] temp2=(String[])t.next();
								if(temp2[0].equals(tabid))
								{
									if(!this.userView.isHaveResource(IResourceConstant.REPORT,tabid))
										continue;
									is=true;
								}
							}
							if(!is)
								continue;
							
						}
						temp[0]=tabid;
//						temp[1]=tabName+a_status;
						temp[1] = tabName;
						temp[2]=status;
						list.add(temp);
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if(rs2!=null)
					rs2.close();
				
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	

}