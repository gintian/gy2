package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.IResourceConstant;
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
import java.sql.SQLException;
import java.util.*;

public class SelectPointTree {
     private String templateID;
     private HashMap selectMap;
     private String pointsetid;
     private String flag;
     private String subsys_id;
     private UserView  userView=null;
     /**下面两个参数为岗位素质模型中选择指标用*/
     private String object_type;
     private String object_id;
     public SelectPointTree(String templateID,String pointsetid,String flag,String subsys_id,UserView userView,String object_type,String object_id)
     {
    	 this.templateID=templateID;
    	 this.pointsetid=pointsetid;
    	 this.flag=flag;
    	 this.subsys_id=subsys_id;
    	 this.object_id=object_id;
    	 this.object_type=object_type;
    	 this.selectMap=this.getSelectedPoint(templateID);
    	 this.userView=userView;
    	
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
 		ArrayList list = getInfoList();
 		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String aflag=(String)abean.get("aflag");
			/**aflag=-1是指标*/
			if("-1".equals(aflag))
				codeitemid="1_"+codeitemid;
			else
				codeitemid="2_"+codeitemid;
			child.setAttribute("id",codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			child.setAttribute("href", "");
			child.setAttribute("target", "mil_body"); 
			if(!"-1".equals(aflag))
				child.setAttribute("icon","/images/open1.png");
			else
				child.setAttribute("icon","/images/lock_co_1.gif");
			String a_xml="/performance/kh_system/kh_template/create_point_tree.jsp?templateID="+templateID+"&flag="+aflag+"&pointsetid="+codeitemid.substring(2)+"&subsys_id="+this.subsys_id+"&object_type="+this.object_type+"&object_id="+this.object_id;
            if(!"-1".equals(aflag))
            	child.setAttribute("xml", a_xml);
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
		ResultSet res=null;
 		Connection conn=null;
 		ArrayList list=new ArrayList();
 		try {
 			
 			conn = AdminDb.getConnection();
 			ContentDAO dao = new ContentDAO(conn);
 			ContentDAO dao1 = new ContentDAO(conn);
 			if("-1".equals(this.flag))
 				return list;
 			// SQL语句
 			StringBuffer strsql = new StringBuffer("");
 			if("0".equals(this.flag))  //指标类别
 			{
 				strsql.append("select * from per_pointset  where ");
 				if("0".equals(this.pointsetid))
 					strsql.append("  parent_id is null and UPPER(subsys_id)='"+subsys_id.toUpperCase()+"' and validflag='1'  order by seq ");
 				else
 					strsql.append("  parent_id="+this.pointsetid+" and UPPER(subsys_id)='"+subsys_id.toUpperCase()+"' and validflag='1' order by seq ");
 			}
 			else						//指标
 			{
 				strsql.append("select * from per_point where pointsetid="+this.pointsetid+" and validflag='1' order by seq");
 			
 			}
 			rs = dao.search(strsql.toString());
 			String sql="";
 			String pointsetid="";

 			while (rs.next()) {
				
 				LazyDynaBean lazyDynaBean=new LazyDynaBean();
 				if("0".equals(this.flag))  //指标类别
 				{
// 					if(!this.userView.isSuper_admin()&&!this.userView.getGroupId().equals("1"))
// 					{
// 						if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("pointsetid")))
// 							continue;
// 					}
 					String unit = rs.getString("b0110");
 					if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()) && !"HJSJ".equalsIgnoreCase(unit)){
 						if(!(unit.length()>KhTemplateBo.getyxb0110(userView, conn).length()?unit.substring(0, KhTemplateBo.getyxb0110(userView, conn).length()):unit).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, conn))){
 							continue;
 						}
 					}
 					lazyDynaBean.set("codeitemid",rs.getString("pointsetid"));
 					lazyDynaBean.set("codeitemdesc",rs.getString("pointsetname"));
// 					String child_id=rs.getString("child_id")!=null?rs.getString("child_id"):"";
// 					if(child_id.length()>0)
// 						lazyDynaBean.set("aflag","0");
// 					else
// 						lazyDynaBean.set("aflag","1");
 					//通过查父节点较准确
 					pointsetid=rs.getString("pointsetid");
 					sql="select * from per_pointset where parent_id='"+pointsetid+"'";
 					res=dao1.search(sql);
 					if(res.next()){
 						lazyDynaBean.set("aflag","0");
 					}else{
 						lazyDynaBean.set("aflag","1");
 					}
 				}
 				else			//指标
 				{

 					if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId()))
 					{
// 						if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
// 							continue;
	        			if(!userView.isRWHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id")) && !userView.isRWHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id")+"R"))
	        			{
	        				continue;
	        			}
 					}
 					if(selectMap.get(rs.getString("point_id"))!=null)
 						continue;
 					lazyDynaBean.set("codeitemid",rs.getString("point_id"));
 					lazyDynaBean.set("codeitemdesc",rs.getString("pointname"));
 					lazyDynaBean.set("aflag","-1");
 				}
 				
 				list.add(lazyDynaBean);
 			}
 			if("0".equals(this.flag))
 			{
 				strsql.setLength(0);
 				strsql.append("select * from per_point where pointsetid="+this.pointsetid+" and validflag='1' order by seq");
 				rs = dao.search(strsql.toString());
 				while (rs.next()) {
 					LazyDynaBean lazyDynaBean=new LazyDynaBean();
 					if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId()))
 					{
 						if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
 							continue;
 					}
 					if(selectMap.get(rs.getString("point_id"))!=null)
 						continue;
 					lazyDynaBean.set("codeitemid",rs.getString("point_id"));
 					lazyDynaBean.set("codeitemdesc",rs.getString("pointname"));
 					lazyDynaBean.set("aflag","-1");
 					list.add(lazyDynaBean);
 				}
 			}
 			rs.close();
 				
 		} catch (Exception e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if (res!=null){
 					res.close();
 				}
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
     public HashMap getSelectedPoint(String templateID)
     {
    	 HashMap map = new HashMap();
 	     ResultSet rs = null;	
 		 Connection conn=null;
    	 try
    	 {
    		 conn = AdminDb.getConnection();
    		 ContentDAO dao = new ContentDAO(conn);
		    	 
    		 if("-1".equals(this.templateID))
    		 {
    			 String codesetid="";
    			 String tablename="";
    			if("3".equals(object_type))
    			{
    				codesetid="@K";	
    				tablename="organization";
    			}else
    			{
    				String param="";
    				if("1".equals(object_type))
    				{
    					param="PS_CODE";//职务体系参数
    				}else if("2".equals(object_type))
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
    			 String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd HH:mm:ss");
    			 sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");    			 
 	    		 rs=dao.search(sql.toString());
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
 	    		 rs=dao.search(sql.toString());
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
 			 ContentDAO dao = new ContentDAO(conn);
	    	 rs=dao.search(sql.toString());
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
 				if (conn != null) {
 					conn.close();
 				}
 			} catch (SQLException e) {
 				e.printStackTrace();
 			}
    	 }
     }

}
