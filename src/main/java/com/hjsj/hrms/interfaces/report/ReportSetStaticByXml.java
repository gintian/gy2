package com.hjsj.hrms.interfaces.report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
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
public class ReportSetStaticByXml {
	
	String flag;
	String codeid;
	UserView userView;
	
	
	String scopeid;
	String reportSet;
	String status;
	public ReportSetStaticByXml() {
	}
	
	public ReportSetStaticByXml(String flag,String codeid,UserView userView) {
		this.flag=flag;
		this.codeid=codeid;
		this.userView=userView;
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
			actionname="/report/edit_report/editReport/staticStatement.do?b_queryStatic=init";
		String theaction = "";
		
		ArrayList list=getInfoList();
		
			for(Iterator t=list.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				
				//创建子元素
				Element child = new Element("TreeNode");
				//设置子元素属性
				String scopeid =temp[0];
				String unitname =temp[1];
				
				child.setAttribute("id", scopeid);
				child.setAttribute("text",unitname);
				child.setAttribute("title", unitname);	
			
					
						theaction=actionname+"&scopeid="+scopeid+"";
				
				child.setAttribute("href", theaction);
				child.setAttribute("target", "mil_body");
				
					child.setAttribute("icon", "/images/open.png");
			
					
			
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
				if("1".equals(flag))
				{			
					//	SQL语句
					StringBuffer strsql = new StringBuffer("");
					//权限范围下的统计口径
					//用户根据自己的操作单位是否与统计口径的所属机构相匹配来展现范围内的可选口径。
					//如果用户没有定义操作单位则按管理范围来匹配。
					ContentDAO dao=new ContentDAO(conn);
					ArrayList list2 = new ArrayList();
					StringBuffer  scopeidstr = new StringBuffer();
						rs = dao.search("select * from tscope ");
						while(rs.next()){
							list2.add(rs.getString("scopeid"));
					
						}
						for(int a=0;a<list2.size();a++){
							String scopeid2 = (String)list2.get(a);
							StringBuffer str = new StringBuffer(" select * from tscope where scopeid ="+scopeid2+" ");
							String temps="";
							
							if (!userView.isSuper_admin())
							{
								String operOrg = userView.getUnit_id();// 操作单位
								StringBuffer tempstr = new StringBuffer();
								if (operOrg.length() > 2)
								{
									String[] temp = operOrg.split("`");
									for (int i = 0; i < temp.length; i++)
									{
										if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))|| "UM".equalsIgnoreCase(temp[i].substring(0, 2))){
											tempstr.append(" or  owner_unit like 'UM" + temp[i].substring(2) + "%'");
											tempstr.append(" or  owner_unit like 'UN" + temp[i].substring(2) + "%'");
										}
									}
									if(tempstr.length()>3){
										temps+=tempstr.toString().substring(3);
									}
									flag="1";
								} else
								{	//走管理范围
									
									String code = "-1";
									if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)// 管理范围
									{
										code = userView.getManagePrivCodeValue();
										if (code!=null)
											{
											if(code.indexOf("UN")!=-1||code.indexOf("UM")!=-1){
												tempstr.append(" or  owner_unit like 'UM" + code.substring(2) + "%'");
												tempstr.append(" or  owner_unit like 'UN" + code.substring(2) + "%'");
											}else{
												tempstr.append(" or  owner_unit like 'UN" + code + "%'");
												tempstr.append(" or  owner_unit like 'UM" + code + "%'");
											}
											}else{
												tempstr.append("and  1=2");
											}
											}else{
												tempstr.append("and  1=2");
											}
									if(tempstr.length()>3){
										temps+=tempstr.toString().substring(3);
									}
									flag="0";
									}
							
							if(temps.length()>0){
								str.append(" and ("+temps+")");
							}
							rs = dao.search(str.toString());
							if(rs.next()){
								scopeidstr.append(","+scopeid2);
							}
							}else{
								scopeidstr.append(","+scopeid2);
							}
						}
					
					//所属机构
					String sql = "";
					if(scopeidstr.toString().length()>0){
						sql = "select * from tscope  where scopeid in("+scopeidstr.substring(1)+") order by displayid";
					}else{
						sql = "select * from tscope where 1=2 order by displayid";
					}
					
						strsql.append(sql);
						
						rs2=dao.search(strsql.toString());
						while(rs2.next())
						{
							
								String[] temp=new String[3];
								temp[0]=rs2.getString("scopeid");
								temp[1]=rs2.getString("name");
								temp[2]=Sql_switcher.readMemo(rs2, "units");
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