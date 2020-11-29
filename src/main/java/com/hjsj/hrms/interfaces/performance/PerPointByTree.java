package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.servlet.performance.KhFieldTree;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PerPointByTree {
	String objectid;	  
	String objectType;   // 1:部门  2：人员
	String flag;         //0:指标类别 1：指标
	String planid;
	String id;
	UserView userView;
	static String pointsetid="";
	
	public PerPointByTree(String a_objectid,String a_objectType,String a_flag,String a_planid,String a_id,UserView userView)
	{
		this.objectid=a_objectid;
		this.objectType=a_objectType;
		this.flag=a_flag;
		this.planid=a_planid;
		this.id=a_id;
		this.userView=userView;
	}
	
	
	public String outPut_Xml() throws GeneralException {

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
		String theaction = "";

		ArrayList list = getInfoList();
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String aflag=(String)abean.get("aflag");
			if("-1".equals(aflag))
				codeitemid="pp_"+codeitemid+"``"+codeitemdesc;
			child.setAttribute("id",codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			child.setAttribute("href", theaction);
			child.setAttribute("target", "mil_body"); 
			if(!"-1".equals(aflag))
				child.setAttribute("icon","/images/open.png");
			else
				child.setAttribute("icon","/images/table.gif");
			String a_xml="/performance/objectiveManage/objectiveCard/per_point_tree.jsp?objectid="+objectid+"&objectType="+objectType+"&flag="+aflag+"&id="+codeitemid+"&planid="+planid;
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
	
	
	public HashMap getExistPoint()
	{
		HashMap map=new HashMap();
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sql="";
			if("1".equals(this.objectType)|| "3".equals(this.objectType)|| "4".equals(this.objectType))
				sql="select p0401 from p04 where plan_id="+this.planid+" and  b0110='"+objectid+"' and Fromflag=2 and ( ( state=-1 and chg_type!=3 ) or state is null or state<>-1 )";
			else
				sql="select p0401 from p04 where plan_id="+this.planid+" and  a0100='"+objectid+"' and Fromflag=2 and ( ( state=-1 and chg_type!=3 ) or state is null or state<>-1 )";
			rs=dao.search(sql);
			while(rs.next())
				map.put(rs.getString(1).toUpperCase(),"1");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
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
	/**
	 * 递归 取得权限范围内指标的所有上级分类
	 * @param pointSetId
	 * @param conn
	 * @param stmt
	 * @param res
	 * @param pointSet_id
	 */
	public String getParentPointSetId(String pointSetId,Connection conn,String pointSet_id ){
		String sql="select parent_id from per_pointset where pointsetid='"+pointSetId+"'";
		ResultSet res=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			res=dao.search(sql);
			while(res.next()){
				pointSetId=res.getString("parent_id");
				if(pointSetId!=null){
					if(pointSet_id.indexOf("'"+pointSetId+"',")==-1){
						pointSet_id+="'"+pointSetId+"',";
					}
					
					pointSet_id=this.getParentPointSetId(pointSetId, conn, pointSet_id);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
					if(res!=null)
						res.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return pointSet_id;
		}

	
	public ArrayList getInfoList()
	{
        // DB相关
		ResultSet rs = null;	
		ResultSet res = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		String fields="";
		String point_id="";
		String pointSet_id="";
		String pointSetids="";
		try {
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			ContentDAO dao1 = new ContentDAO(conn);
			if("-1".equals(this.flag))
				return list;
			
			
			HashMap existPointMap=getExistPoint();
			/*先取得权限范围内的指标 指标类*/
			if("0".equals(this.id)){
//				String sql="select point_id from per_point ";
//				res=stmt.executeQuery(sql);
//				while(res.next()){
//					point_id=res.getString("point_id");
//					if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD,point_id))
//					{
//						continue;
//					}
//					if(existPointMap.get(point_id)!=null){
//						continue;
//					}
//					fields+="'"+point_id+"',";
//				}
//				if(fields.length()>0)
//				fields=fields.substring(0, fields.length()-1);
//				else
//				fields="''"	;
//				sql="select distinct pointsetid from per_point where point_id in("+fields+")";
//				res=stmt.executeQuery(sql);
//				while(res.next()){
//					pointSet_id+="'"+res.getString("pointsetid")+"',";
//					pointSetids+=res.getString("pointsetid")+",";
//				}	
//				String pointSetid[]=pointSetids.split(",");
//				for(int i=0;i< pointSetid.length;i++){
//					pointSet_id=this.getParentPointSetId(pointSetid[i], conn, pointSet_id);
//				}
//				if(pointSet_id.length()>0)
//					pointSet_id=pointSet_id.substring(0, pointSet_id.length()-1);
//				else
//					pointSet_id="''";
//				pointsetid=pointSet_id;
			}
			/*先取得权限范围内的指标 指标类*/  //上面注释的代码 通过指标着手 在指标条数过多的情况下可能会造成sql语句超长 故采用下面方法 从指标分类着手
			if("0".equals(this.id)){
				String sql=" select distinct pointsetid from per_point";
				String aql="";
				res=dao.search(sql);
				while(res.next()){//取得所有指标分类
					if(StringUtils.isEmpty(res.getString("pointsetid"))){
						continue;
					}
					aql="select point_id  from per_point where pointsetid='"+res.getString("pointsetid")+"'";
					rs=dao1.search(aql);
					while(rs.next()){//判断用户在指标分类内是否有操作权限指标
						point_id=rs.getString("point_id");
						if(existPointMap.get(point_id)!=null){
							continue;
						}
						if(this.userView.isAdmin()||this.userView.isHaveResource(IResourceConstant.KH_FIELD,point_id)){
							pointSet_id+="'"+res.getString("pointsetid")+"',";
							pointSetids+=res.getString("pointsetid")+",";
							break;
						}
					}
				}
				String pointSetid[]=pointSetids.split(",");
				for(int i=0;i< pointSetid.length;i++){
					pointSet_id=this.getParentPointSetId(pointSetid[i], conn, pointSet_id);
				}
				if(pointSet_id.length()>0)
					pointSet_id=pointSet_id.substring(0, pointSet_id.length()-1);
				else
					pointSet_id="''";
				pointsetid=pointSet_id;
			}
			// SQL语句
			StringBuffer strsql = new StringBuffer("");
			ArrayList setList=new ArrayList();
			
			if("0".equals(this.flag))  //指标类别
			{
				strsql.append("select * from per_pointset  where ");
				if("0".equals(this.id)){
					strsql.append("  parent_id is null and validflag=1 and subsys_id='33' and pointsetid in(");
					strsql.append(pointsetid);
					strsql.append(")order by seq ");
				}
				else{
					strsql.append("  parent_id="+this.id+" and validflag=1 and subsys_id='33'  and pointsetid in(");
					strsql.append(pointsetid);
					strsql.append(")order by seq ");
					
				}
				
				rs=dao.search("select * from per_pointset");
				while(rs.next())
				{
					/*if(!userView.isSuper_admin()&&!userView.getGroupId().equals("1"))
	    			{
	        			if(!userView.isHaveResource(IResourceConstant.KH_FIELD,rs.getString("pointsetid")))
	        			{
	        				continue;
	        			}
	        		}*/
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("codeitemid",rs.getString("pointsetid"));
					abean.set("codeitemdesc",rs.getString("pointsetname"));
					String parent_id=rs.getString("parent_id")!=null?rs.getString("parent_id"):"";
					abean.set("parent_id", parent_id);
					setList.add(abean);
				}
				
			}
			else						//指标
			{
				strsql.append("select * from per_point where pointsetid="+this.id+"   and validflag=1 order by seq");
			
			}
			
			

			rs = dao.search(strsql.toString());
			while (rs.next()) {
				LazyDynaBean lazyDynaBean=new LazyDynaBean();
				String b0110 = "";
				String scope=""; //指标类别 0 共享   1私有
				if("0".equals(this.flag))  //指标类别
				{
				    b0110 = rs.getString("b0110");
				    scope= rs.getString("scope");
				    String yxb0110 = KhFieldTree.getyxb0110(this.userView,conn);//根据业务范围得到一个单位编码
		            String b01s0 =  this.userView.getUserOrgId();//所在单位
		            int yxb0110le = yxb0110.length();
		            int b0110le = b0110.length();
		            if(yxb0110le<b0110le)
		                yxb0110le = yxb0110.length();
		            else 
		                yxb0110le = b0110.length();
		            if(!"0".equals(scope)&&!b0110.substring(0,yxb0110le).equals(yxb0110)&&!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId())&&!"hjsj".equalsIgnoreCase(b0110)){
		                continue;
		            }
					/*if(!userView.isSuper_admin()&&!userView.getGroupId().equals("1"))
	    			{
	        			if(!userView.isHaveResource(IResourceConstant.KH_FIELD,rs.getString("pointsetid")))
	        			{
	        				continue;
	        			}
	        		}*/
					lazyDynaBean.set("codeitemid",rs.getString("pointsetid"));
					lazyDynaBean.set("codeitemdesc",rs.getString("pointsetname"));
					String child_id=rs.getString("child_id")!=null?rs.getString("child_id"):"";
					
					boolean isLeaf=true;
					for(int i=0;i<setList.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)setList.get(i);
						if(((String)abean.get("parent_id")).equals(rs.getString("pointsetid")))
						{
							isLeaf=false;
							break;
						}
					}
					
					if(isLeaf)
					{
						lazyDynaBean.set("aflag","1");
					}
					else
						lazyDynaBean.set("aflag","0");
				/*	if(child_id.length()>0)
						lazyDynaBean.set("aflag","0");
					else
						lazyDynaBean.set("aflag","1");*/
				}
				else			//指标
				{
					if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
	    			{
	        			if(!userView.isHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id")))
	        			{
	        				continue;
	        			}
	        		}
					if(existPointMap.get(rs.getString("point_id").toUpperCase())!=null)
						continue;
					lazyDynaBean.set("codeitemid",rs.getString("point_id"));
					lazyDynaBean.set("codeitemdesc",rs.getString("pointname"));
					lazyDynaBean.set("aflag","-1");
				}
				
				list.add(lazyDynaBean);
			}
			
			if("0".equals(this.flag))  //指标类别
			{
				strsql.setLength(0);
				strsql.append("select * from per_point where pointsetid="+this.id+" and validflag=1 order by seq");
				rs = dao.search(strsql.toString());
				while (rs.next()) {
					if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
	    			{
	        			if(!userView.isHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id")))
	        			{
	        				continue;
	        			}
	        		}
					LazyDynaBean lazyDynaBean=new LazyDynaBean();
					if(existPointMap.get(rs.getString("point_id").toUpperCase())!=null)
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
				if (rs != null) {
					rs.close();
				}
				if(res != null){
				    res.close();
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
	
}
