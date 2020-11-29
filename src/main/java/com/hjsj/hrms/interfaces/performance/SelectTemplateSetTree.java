package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class SelectTemplateSetTree{
     private String templatesetid;
     private String subsys_id;
     private UserView userView;
     
     public SelectTemplateSetTree(UserView userView,String templatesetid,String subsys_id)
     {
    	 this.userView = userView;
         this.templatesetid = templatesetid;
         this.subsys_id = subsys_id;
     }
     public String GetTreeXMLString() throws GeneralException
     {
//    	 生成的XML文件
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
 		Connection conn=null;
 		ArrayList list = getInfoList();
 		try {
 			conn = AdminDb.getConnection();
	 		for (Iterator t = list.iterator(); t.hasNext();) {
				LazyDynaBean abean = (LazyDynaBean) t.next();
	
				// 创建子元素
				Element child = new Element("TreeNode");
				// 设置子元素属性
				String template_setid = (String) abean.get("template_setid");
				String name = (String) abean.get("name");
				String validflag = (String) abean.get("validflag");
				//codeitemid="2_"+codeitemid;
				child.setAttribute("id",template_setid);
				child.setAttribute("text", name);
				child.setAttribute("title", name);
				child.setAttribute("href", "");
				child.setAttribute("target", "mil_body"); 
				
				if("1".equals(validflag))
			    	child.setAttribute("icon","/images/open1.png");
				else
					child.setAttribute("icon","/images/open.png");
				if(isHaveChild(template_setid,conn)){
					String a_xml="/performance/kh_system/kh_template/create_templateset_tree.jsp?templatesetid="+template_setid+"&subsys_id="+subsys_id;
		            child.setAttribute("xml", a_xml);			
				}
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
 		} catch (Exception e) {
 			e.printStackTrace();
		}finally
		{
			try
			{
				if(conn!=null)
				{
					conn.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
 		
        return xmls.toString();
     }
     public ArrayList getInfoList()
 	{
         // DB相关
 		ResultSet rs = null;	
 		Connection conn=null;
 		ArrayList list=new ArrayList();
 		try {
 			
 			conn = AdminDb.getConnection();
 			ContentDAO dao = new ContentDAO(conn);
 			// SQL语句
 			StringBuffer strsql = new StringBuffer("");
 			 if("-1".equals(templatesetid))
 		    {
 				strsql.append(" select * from per_template_set where parent_id is null ");
// 		    	if(!method.equals("0"))
// 		    		sql.append(" and validflag='1' ");
 				strsql.append(" and subsys_id='");
 				strsql.append(subsys_id+"' order by template_setid");
 		    }
 		    else
 		    {
 		    	strsql.append(" select * from per_template_set where parent_id='"+templatesetid+"'  ");
 		    	//sql.append(" and (((scope is null || scope = 0) or (scope is not null and b0110='"+userView.getUnit_id()+"')) or b0110='HJSJ')");
// 		    	if(!method.equals("0"))
// 		    		sql.append(" and validflag='1' ");
 		    	strsql.append(" and subsys_id='");
 		    	strsql.append(subsys_id+"' order by template_setid");
 		    }
 			rs = dao.search(strsql.toString());
 			while (rs.next()) {
 				String scope = rs.getString("scope");
				String unit = rs.getString("b0110");
 				LazyDynaBean lazyDynaBean=new LazyDynaBean();
 				if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()) && !"HJSJ".equalsIgnoreCase(unit)){
					if(!(unit.length()>KhTemplateBo.getyxb0110(userView, conn).length()?unit.substring(0, KhTemplateBo.getyxb0110(userView, conn).length()):unit).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, conn))){
						continue;
					}
				}
				lazyDynaBean.set("template_setid",rs.getString("template_setid"));
				lazyDynaBean.set("name",rs.getString("name"));
				lazyDynaBean.set("validflag",String.valueOf(rs.getInt("validflag")));
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
 				if (conn != null) {
 					conn.close();
 				}
 			} catch (SQLException e) {
 				e.printStackTrace();
 			}
 		}
 		return list;
 	}
     public boolean isHaveChild(String templatesetid,Connection con)
 	{
 		boolean flag=false;
 		RowSet rs = null;
 		try
 		{
 			String sql ="select template_setid,b0110 from per_template_set where parent_id ='"+templatesetid+"'";
// 			if(!userView.isAdmin() && !userView.getGroupId().equals("1"))
//			{
//				String unit = KhTemplateBo.getyxb0110(userView, con);
//				sql+=" and (b0110='"+unit+"' or b0110='HJSJ')";
//			}
 			ContentDAO dao = new ContentDAO(con);
 			
 			rs = dao.search(sql);
 			while(rs.next())
 			{
 				String b0110 = (String)rs.getString("b0110");
 				if(!userView.isAdmin() && !"1".equals(userView.getGroupId()))
 				{
 					if(b0110!=null && !"".equalsIgnoreCase(b0110) && "HJSJ".equalsIgnoreCase(b0110)){
 						flag=true;
 					}else{
 						if(!(b0110.length()>KhTemplateBo.getyxb0110(userView, con).length()?b0110.substring(0, KhTemplateBo.getyxb0110(userView, con).length()):b0110).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, con))){
 							flag=true;
 						}
 					}
 				}else{
 					flag=true;
 				}
 				break;
 			}
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 		}
 		finally
 		{
 			try
 			{
 				if(rs!=null)
 				{
 					rs.close();
 				}				
 			}
 			catch(Exception e)
 			{
 				e.printStackTrace();
 			}
 		}
 		return flag;
 	}
     /*
     public HashMap getSelectedPoint(String templateID)
     {
    	 HashMap map = new HashMap();
 	     ResultSet rs = null;	
 		 Connection conn=null;
    	 try
    	 {
    		 conn = AdminDb.getConnection();
		    	 
    		 if(this.templateID.equals("-1"))
    		 {
    			 String codesetid="";
    			 String tablename="";
    			if(object_type.equals("3"))
    			{
    				codesetid="@K";	
    				tablename="organization";
    			}else
    			{
    				String param="";
    				if(object_type.equals("1"))
    				{
    					param="PS_CODE";//职务体系参数
    				}else if(object_type.equals("2"))
    				{
    					param="PS_C_CODE";//岗位体系参数
    				}
    				RecordVo constantuser_vo = ConstantParamter.getRealConstantVo(param);
    			    codesetid = constantuser_vo.getString("str_value");
    			    tablename="codeitem";
    			}
    			StringBuffer buf = new StringBuffer("");
    			this.getParentLink(tablename, codesetid, buf, this.object_id);
    			 StringBuffer sql = new StringBuffer();
    			 sql.append(" select point_id from per_competency_modal");
    			 sql.append(" where object_type="+this.object_type);
    			 sql.append(" and UPPER(object_id) in ("+buf.substring(1).toUpperCase()+")");
    			 Calendar calendar = Calendar.getInstance();
    			 String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
    			 sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");    			 
 	    		 rs=stmt.executeQuery(sql.toString());
 		    	 while(rs.next())
 		    	 {
 		    		 map.put(rs.getString("point_id"), "1");
 		    	 }
    		 }
    		 else{
        		 StringBuffer sql = new StringBuffer();
        		 sql.append("select point_id from per_template_point where item_id in (");
        		 sql.append(" select item_id from per_template_item where UPPER(template_id)='");
        		 sql.append(templateID.toUpperCase()+"')");        		 
 	    		 rs=stmt.executeQuery(sql.toString());
 		    	 while(rs.next())
 		    	 {
 		    		 map.put(rs.getString("point_id"), "1");
 		    	 }
    		 }
    		 
    	 }
    	 catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 finally
    	 {
    		 try {
 				if (rs != null) {
 					rs.close();
 				}
 				if (stmt != null) {
 					stmt.close();
 				}
 				if (conn != null) {
 					conn.close();
 				}
 			} catch (SQLException e) {
 				e.printStackTrace();
 			}
    	 }
    	 return map;
     }
     public void getParentLink(String tablename,String codesetid,StringBuffer buf,String itemid)
     {
 	     ResultSet rs = null;	
 		 Connection conn=null;
 		 try
 		 {
 			 StringBuffer sql = new StringBuffer("");
 			 sql.append("select codeitemid,parentid from ");
 			 sql.append(tablename);
 			 sql.append(" where UPPER(codeitemid)='"+itemid.toUpperCase()+"'");
 			 sql.append(" and UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
 			 conn = AdminDb.getConnection();
	    	 rs=stmt.executeQuery(sql.toString());
	    	 while(rs.next())
	    	 {
	    		 String parent_id=rs.getString("parentid");
	    		 String codeitemid=rs.getString("codeitemid");
	    		 if(parent_id==null||parent_id.equalsIgnoreCase(codeitemid))
	    		 {
	    			 buf.append(",'"+codeitemid+"'");
	    			 return;
	    		 }else{
	    			 buf.append(",'"+codeitemid+"'");
	    		     this.getParentLink(tablename, codesetid, buf, parent_id);
	    		 }
	    		 
	    	 }
 		 }
 		 catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 finally
    	 {
    		 try {
 				if (rs != null) {
 					rs.close();
 				}
 				if (stmt != null) {
 					stmt.close();
 				}
 				if (conn != null) {
 					conn.close();
 				}
 			} catch (SQLException e) {
 				e.printStackTrace();
 			}
    	 }
     }*/

}
