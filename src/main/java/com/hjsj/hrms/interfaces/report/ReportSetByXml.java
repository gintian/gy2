package com.hjsj.hrms.interfaces.report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.PubFunc;
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
import java.util.*;
public class ReportSetByXml {
	
	String flag;
	String codeid;
	String userName;
	UserView userView;
	String operate;
	
	
	String unitCode;
	String reportSet;
	String status;
	private Connection conn=null;
	public ReportSetByXml() {
	}
	
	
	public ReportSetByXml(String statusInfo,String flag)
	{
	    statusInfo = PubFunc.keyWord_reback(statusInfo);//add by wangchaoqun on 2014-9-16
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
						theaction="/report/report_collect/reportOrgCollecttree.do?b_query=link&encryptParam=" + PubFunc.encrypt("a_code="+unitCode+"&tabid="+unitcode+"&operateObject=2");
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
	
	
	
	
	
	
	
	
	
	public ReportSetByXml(String flag,String codeid,String userName,UserView userView,String operate) throws GeneralException, SQLException {
		this.flag=flag;
		this.codeid=codeid;
		//----------------报表上报是否支持审批  编辑报表  zhaoxg 2013-2-17------------
		if(isApprove(userName)){
			this.userName=userName;
		}else{
			this.userName=approve(userName);
			if("".equals(this.userName)){
				this.userName=userName;
			}
		}
		//-----------------------------------------------------------------------
		this.userView=userView;
		this.operate=operate;
	 }
	public ReportSetByXml(String flag,String codeid,String userName,UserView userView,String operate,Connection conn) throws Exception {
		this.flag=flag;
		this.codeid=codeid;
		//----------------报表上报是否支持审批  编辑报表  zhaoxg 2013-2-17------------
		if(isApprove(userName)){
			this.userName=userName;
		}else{
			this.userName=approve(userName);
			userView=new UserView(this.userName, conn); 
			userView.canLogin();
			if("".equals(this.userName)){
				this.userName=userName;
				userView=new UserView(this.userName, conn); 
				userView.canLogin();
			}
		}
		//-----------------------------------------------------------------------
		this.userView=userView;
		System.out.println(this.userView.getUserName()+"aa");
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
						theaction = actionname + "?b_query=link&encryptParam="+PubFunc.encrypt("operateObject=1&code="+ unitcode+"&status="+temp[2]);				
					else
						theaction=actionname+"?b_query=link&encryptParam="+PubFunc.encrypt("tabid="+unitcode+"&operateObject=2");
				}
				child.setAttribute("href", theaction);
				child.setAttribute("target", "mil_body");
				if("1".equals(flag))
					child.setAttribute("icon", "/images/open.png");
				else
					child.setAttribute("icon", "/images/prop_ps.gif");
				
				if(!"2".equals(flag))
				{
					
						child.setAttribute("xml" ,"report_set_tree.jsp?encryptParam="+PubFunc.encrypt("flag=2&operate="+operate+"&codeid="+unitcode+"&userName="+userName));				
					
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
		ContentDAO dao = null;
		ArrayList list=new ArrayList();
		try
		{
				conn = AdminDb.getConnection();					
				dao = new ContentDAO(conn);
				TnameBo tnamebo  = new TnameBo(conn);
				HashMap scopeMap = tnamebo.getScopeMap();
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
							if(reportTypes[i].length()>0){
							sql_sub.append(" or ");
							sql_sub.append(" tsortid=");
							sql_sub.append(reportTypes[i]);
							}
						}
						String sql1="";
						if(sql_sub.length()>0){
							sql1 =	sql.append(sql_sub.substring(3)).toString();
						}else{
							sql1 ="select tsortid,name from tsort where 1<>1";
						}
												
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
						if(scopeMap!=null&&scopeMap.get(rs.getString("tabid"))!=null&& "1".equals(scopeMap.get(rs.getString("tabid"))))
							continue;
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
		ContentDAO dao = null;
		ResultSet rs = null;
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();					
			dao = new ContentDAO(conn);
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
		ContentDAO dao = null;
		ArrayList list=new ArrayList();
		String report="";
		String reportTypes2="";
		try
		{
				conn = AdminDb.getConnection();					
				dao = new ContentDAO(conn);
				userView=new UserView(this.userName, conn); 
				userView.canLogin();
				if("1".equals(flag))
				{			
					//	SQL语句
					StringBuffer strsql = new StringBuffer("");
					String[] reportTypes=null;
					//if(!operate.equals("collect"))
					//{
					Calendar d=Calendar.getInstance();
					int yy=d.get(Calendar.YEAR);
					int mm=d.get(Calendar.MONTH)+1;
					int dd=d.get(Calendar.DATE);
					StringBuffer ext_sql = new StringBuffer();
					ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.end_date")+">"+yy);
					ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+">"+mm+" ) ");
					ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.end_date")+">="+dd+" ) ) ");
					ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.start_date")+"<"+yy);
					ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"<"+mm+" ) ");
					ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.start_date")+"<="+dd+" ) ) ");	 			
					
						strsql.append("select reporttypes from operUser,tt_organization  where operUser.unitcode=tt_organization.unitcode  and  username='");
						strsql.append(userName);
						strsql.append("' "+ext_sql+"");				
						
						rs = dao.search(strsql.toString());	
						if(rs.next())
						{
							    reportTypes=(Sql_switcher.readMemo(rs,"reporttypes")).trim().split(",");						
						}
					//}
					StringBuffer sql=new StringBuffer("select tsortid,name from tsort where ");
					
					if(reportTypes!=null)
					{		
						if(reportTypes.length<=0 || reportTypes[0].trim().length()<=0)
							return list; 
							
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
						
					}else {
						//从资源里找
//						SysPrivBo privbo=null;
//						if(userView.getStatus()==4) //自助用户关联业务用户
//						{
//							privbo=new SysPrivBo(userView.getDbname()+""+userView.getUserId(),"4",conn,"warnpriv");
//						}else{
//							privbo=new SysPrivBo(userView.getUserName(),"0",conn,"warnpriv");
//						}
//						 
//						String res_str=privbo.getWarn_str();
//						 if(res_str!=null&&res_str.indexOf("<Report>")!=-1){
//							 report =  res_str.substring(res_str.indexOf("<Report>")+8,res_str.indexOf("</Report>"));
//						 }
						sql.delete(0, sql.length());
						sql.append("select tabid  from tname");
						rs2=dao.search(sql.toString());
						while(rs2.next())
			    		{
						
						if(userView.isHaveResource(IResourceConstant.REPORT,rs2.getString("tabid")))
						{
							report+=	rs2.getString("tabid")+",";
						}
			    		}

						if(userView.isSuper_admin()){
							sql.delete(0, sql.length());
							
							sql.append("select tabid,name,paper,tsortid  from tname  ");
						
						}else{
						report = report.replace(" ", "");
						report = report.replace("R", "");
						while(report.indexOf(",,")!=-1){
							report = report.replace(",,", ",");
						}
						if (report.length()>0&&report.charAt(report.length() - 1) == ',') {
							report = report.substring(0, report.length() - 1);
						}
						if (report.length()>0&&report.charAt(0) == ',') {
							report = report.substring(1, report.length());
						}
						
						sql.delete(0, sql.length());
					
						sql.append("select tabid,name,paper,tsortid  from tname where tabid in ("+report+") order by tsortid");
						
						}
						if(report.length()==0&&!userView.isSuper_admin()){
							// 用户没有权限操作任何报表
							
						}else{
							
						rs2=dao.search(sql.toString());
						while(rs2.next())
						{
							if(reportTypes2.indexOf(""+rs2.getInt("tsortid"))==-1)
								reportTypes2+=rs2.getInt("tsortid")+",";
						}
						sql.delete(0, sql.length());
						sql.append("select tsortid,name from tsort where ");
						StringBuffer sql_sub=new StringBuffer("");
						if (!"".equals(reportTypes2)&&reportTypes2.charAt(reportTypes2.length() - 1) == ',') {
							reportTypes2 = reportTypes2.substring(0, reportTypes2.length() - 1);
						}
						reportTypes = reportTypes2.split(",");
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
				}
				else
				{
					ArrayList treport_list=new ArrayList(); 
					StringBuffer sql=new StringBuffer("");
					sql.append("select * from treport_ctrl where unitcode=(select unitcode from operUser where userName='");
					sql.append(userName);
					sql.append("') and tabid in (select TabId from tname  where TSortId="+codeid+" )");
					if("collect".equals(operate)){
					TnameBo tnamebo  = new TnameBo(conn);
					HashMap scopeMap = tnamebo.getScopeMap();
					java.util.Iterator it = scopeMap.entrySet().iterator();
					String tabids = "";
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						String keys = (String) entry.getKey();
						tabids+= keys+",";
						
					}
					if(tabids.length()>0)
						tabids=tabids.substring(0,tabids.length()-1);
					
					if(tabids.length()>0)
						sql.append(" and tabid not in("+tabids+") ");
					}
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
					sql.delete(0, sql.length());
					sql.append("select tabid  from tname");
					rs2=dao.search(sql.toString());
					while(rs2.next())
		    		{
					if(userView.isHaveResource(IResourceConstant.REPORT,rs2.getString("tabid")))
					{
						report+=	rs2.getString("tabid")+",";
					}
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
							if(treport_list.size()>0){
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
									else if("4".equals(temp2[1]))
									{
										a_status="(审批中)";
									}							
									status=temp2[1];
									break;
								}
							}
							}else{//从资源分配里取表
//								SysPrivBo privbo=null;
//								if(userView.getStatus()==4) //自助用户关联业务用户
//								{
//									privbo=new SysPrivBo(userView.getDbname()+""+userView.getUserId(),"4",conn,"warnpriv");
//								}else{
//									privbo=new SysPrivBo(userView.getUserName(),"0",conn,"warnpriv");
//								}
//								String res_str=privbo.getWarn_str();
//								 if(res_str!=null&&res_str.indexOf("<Report>")!=-1){
//									 report =  res_str.substring(res_str.indexOf("<Report>")+8,res_str.indexOf("</Report>"));
//								 }
								
								 report = report.replace(" ", "");
								 report = report.replace("R", "");
								 report = ","+report+",";
								 a_status="(正在编辑)";
								 if(report.indexOf(","+tabid+",")!=-1)
									 is=true;
								if( userView.isSuper_admin())
									 is=true;
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
						temp[1]=tabName+a_status;
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
	/**
	 * 获取报批人信息   zhaoxg 2013-2-17
	 * @param userName
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String approve(String userName) throws GeneralException, SQLException{
		String approve = "";

		ResultSet rs = null;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		try{
			String sql = "select appuser,username from treport_ctrl";
			rs = dao.search(sql.toString());
			while(rs.next()){
				String appuser = rs.getString("appuser");
				if(appuser!=null){
					String[] aa = appuser.split(";");
					for(int i=0;i<aa.length;i++){
						if(aa[i]!=null&&!"".equals(aa[i])&&aa[i].equals(this.getFullName(userName))){
							approve = rs.getString("username");
						}
					}
				}

			}
		}catch (Exception e) {
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
		
		return approve;
	}
	/**
	 * 获取报表填报人姓名 zhaoxg 2013-2-17
	 * @param username
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String getFullName(String username) throws GeneralException, SQLException{
		String fullname = "";
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		ResultSet rs = null;
		try{

			String sql = "select fullname from operuser where username = '"+username+"'";
			rs = dao.search(sql.toString());
			if(rs.next()){
				fullname = rs.getString("fullname");
			}
		}catch(Exception e)
		{
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
		return fullname;
	}
	/**
	 * 判断当前用户是否负责报表  zhaoxg 2013-2-17
	 * @param username
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public boolean isApprove(String username) throws GeneralException, SQLException{
		boolean isapprove = false;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		ResultSet rs = null;
		try{

			String sql = "select username from operUser,tt_organization  where operUser.unitcode=tt_organization.unitcode";
			rs = dao.search(sql.toString());
			while(rs.next()){
				if(username.equals(rs.getString("username"))){
					isapprove = true;
				}
			}
		}catch(Exception e)
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
		
		return isapprove;
	}
}