package com.hjsj.hrms.interfaces.hire;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 
 * dml*/

public class CreatepointunitByXml {
	String flag="";//是否按照 操作单位 管理范围走1.是 0.不是
	String a_code=""; //管理范围代码
	String init="";//是否为第一次进入
	/**
	 *flag显示单位范围
	 *target 定位页面
	 *actionname 动作路径
	 *codeid 操作单位代码
	 *param 动作页面*/
	public CreatepointunitByXml(String flag,String codeid,String init){
		this.flag=flag;
		this.a_code=codeid;
		this.init=init;
		
	}
	public CreatepointunitByXml(){}
	public String outPutTreeByXml(){
		 StringBuffer xmls = new StringBuffer();
		 ArrayList unitlist=new ArrayList();
		 Element root = new Element("TreeNode");
		 root.setAttribute("id", "00");
		 root.setAttribute("text", "root");
		 root.setAttribute("title", "organization");
		 Document myDocument = new Document(root);
		
		 unitlist=this.getUnitList();
		 for(Iterator t = unitlist.iterator(); t.hasNext();){
			 LazyDynaBean abean = (LazyDynaBean) t.next();
			 Element child = new Element("TreeNode");
			 String unitcode = (String) abean.get("codeitemid");
			 String unitname = (String) abean.get("codeitemdesc");
			 String theaction ="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&encryptParam="+PubFunc.encrypt("a_code="+unitcode);
			 String codeset=(String)abean.get("codesetid");
			 child.setAttribute("id", unitcode);
			 child.setAttribute("text", unitname);
			 child.setAttribute("title", unitname);
			 child.setAttribute("href", theaction);
			 child.setAttribute("target", "mil_body");
			 if("UN".equals(codeset))
	                child.setAttribute("icon","/images/unit.gif");
			 else if("UM".equals(codeset))
	                child.setAttribute("icon","/images/dept.gif");
			 child.setAttribute("xml","/performance/kh_system/kh_field/add_point_Tree.jsp?flag="+this.flag+"&a_code="+unitcode+"&init=2");
			 root.addContent(child);
			
		 }
		 XMLOutputter outputter = new XMLOutputter();
         Format format=Format.getPrettyFormat();
         format.setEncoding("UTF-8");
         outputter.setFormat(format);
         xmls.append(outputter.outputString(myDocument));
		 return xmls.toString();
	}
	public ArrayList getUnitList(){
		ArrayList unitlist=new ArrayList();
		StringBuffer sql=new StringBuffer("");
		//Statement stmt = null;
		ResultSet rs = null;	
		Connection conn=null;
		if(this.a_code!=null&&this.a_code.length()!=0){
			try {
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);  
				//stmt = conn.createStatement();
				String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
				sql.append("select * from organization where ");
				if("0".equals(this.flag)){
					if("-1".equals(this.a_code)){
						sql.append("parentid=codeitemid and codesetid<>'@K'");
					}
					else{
						if(this.a_code!=null&&this.a_code.length()!=0){
							if(a_code.indexOf("`")==-1){
								if("1".equals(this.init))
									sql.append("codeitemid='"+this.a_code+"'and codesetid<>'@K'");
								else{
									sql.append("parentid='"+this.a_code+"'and parentid<>codeitemid and  codesetid<>'@K'");
								}
							}else{
								sql.append(" ( ");
								StringBuffer tempsql=new StringBuffer("");
								String[] temp=this.a_code.split("`");
								HashMap map = this.getPrivMange(temp);
								for(int i=0;i<temp.length;i++)
								{
									if(map.get(temp[i].substring(2))==null)
								    	tempsql.append(" or codeitemid='"+temp[i].substring(2)+"'");
								}
								sql.append(tempsql.substring(3)+" ) ");
							}
						}
						
				}
				sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
				
				sql.append("  order by codesetid,A0000");
				rs = dao.search(sql.toString());
				while (rs.next()) {
					LazyDynaBean lazyDynaBean=new LazyDynaBean();
					lazyDynaBean.set("codesetid",rs.getString("codesetid"));
					lazyDynaBean.set("codeitemid",rs.getString("codeitemid"));
					lazyDynaBean.set("codeitemdesc",rs.getString("codeitemdesc"));
					unitlist.add(lazyDynaBean);
				}
				rs.close();
				
			 }}catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if (rs != null) {
						rs.close();
					}
//					if (stmt != null) {
//						stmt.close();
//					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}else{
			
		}
		return unitlist;
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
