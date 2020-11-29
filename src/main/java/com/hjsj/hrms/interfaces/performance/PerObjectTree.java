package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title:PerObjectTree.java</p>
 * <p>Description:绩效分析机构树展现</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class PerObjectTree 
{
	String planId="";
	String codeid="";
	String codesetid="";
	String model="0";  // 0:数据采集 1:单指标趋势分析 2：多指标趋势分析 3：单人对比分析 4：综合评测标 5：多人对比分析 6：主体分类对比分析(单考核对象) 10主体分类对比分析(多考核对象)   7：了解程度对比分析 8：评语 9：选票统计  11:人岗匹配
	String object_type="";
	UserView userView=null;
	
	public PerObjectTree(String planId,String codeid,String codesetid,String model)
	{
		this.planId=planId;
		this.codeid=codeid;
		this.codesetid=codesetid;
		this.model=model;	
		this.object_type=this.getObjectType();
	}
	
	
	public PerObjectTree(String planId,String codeid,String codesetid,String model,UserView _userView)
	{
		this.planId=planId;
		this.codeid=codeid;
		this.codesetid=codesetid;
		this.model=model;	
		this.object_type=this.getObjectType();
		this.userView=_userView;
	}
	
	public String getObjectType()
	{
	    String object_type="2";
	    ResultSet rs = null;	
	    Connection conn=null;
		
	    try
	    {
		conn = AdminDb.getConnection();
		ContentDAO dao = new ContentDAO(conn);
		
		if(this.planId.indexOf(",")==-1&&this.planId.trim().length()>0)
		{
			rs=dao.search("select object_type from per_plan where plan_id="+this.planId);
			if(rs.next())
				object_type=rs.getString("object_type");
		
		}
	    } catch (GeneralException e)
	    {
		e.printStackTrace();
	    } catch (SQLException e)
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
	    return object_type;
	}
	
	
	public String getHrefContext(String flag,String model,String codeitemid,String codesetid)
	{
		String _itemId = codeitemid; // 保留codeitemid（明文）的副本 by lium
		codeitemid = PubFunc.encrypt(codeitemid);
		String context="";
		if("1".equals(flag))
		{
			if("0".equals(model))  //0:数据采集
			{
					context="javascript:pointObject('"+codeitemid+"')";
			}
			else if("1".equals(model)) //1:单指标趋势分析
			{
					context="javascript:singlePointAnalyse('"+codeitemid+"')";
			}
			else if("2".equals(model)) //2:多指标趋势分析
			{
					context="javascript:multiplePointAnalyse('"+codeitemid+"')";
			}
			else if("3".equals(model)) //3:单人对比分析
			{
					context="javascript:singleContrastAnalyse('"+codeitemid+"')";
			}
			else if("4".equals(model)) //4:综合评测表
			{
					context="javascript:getPerResultTable('"+codeitemid+"')";
			}
			else if("7".equals(model)) //7:了解程度对比分析
			{
					context="javascript:knowContrastAnalyse('"+codeitemid+"')";
			}
			else if("8".equals(model)) //8:评语
			{
					context="javascript:getPerRemark('"+codeitemid+"')";
			}
			else if("6".equals(model)) // 6：主体分类对比分析(单考核对象)
			{
					context="javascript:mainbodyAnalyse('"+codeitemid+"')";
			}
			else if("11".equals(model)) // 11：人岗匹配(单考核对象)
			{			
					context = "/performance/perAnalyse.do?b_personStation=query&opt=1&a0100="+codeitemid;					
				
			//		context="javascript:personStationAnalyse('"+codeitemid+"')";
			}
		}
		if("9".equals(model)) //9:选票统计
		{
				context="javascript:voteStat('"+PubFunc.encrypt(codesetid+_itemId)+"')";
		}
		
		return context;
	}
	
	
	public String outPut_Xml() throws GeneralException {
		
//		 生成的XML文件
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

		ArrayList list =getInfoList();
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String flag=(String)abean.get("flag");
			String codesetid=(String)abean.get("codesetid");
			if("UN".equals(codesetid))
				child.setAttribute("icon","/images/unit.gif");
			if("UM".equals(codesetid))
				child.setAttribute("icon","/images/dept.gif");
			if("1".equals(flag)&&!"UN".equals(codesetid)&&!"UM".equals(codesetid))
			{
				child.setAttribute("icon","/images/man.gif");
				child.setAttribute("type","true");						
			}
			else
				child.setAttribute("type","false");					
			
			
			if("9".equals(this.model))
			{
//				if(flag.equals("1"))
//					continue;
			}
			else if("5".equals(this.model)|| "10".equals(this.model))
			{
				if("1".equals(flag))
				{
					child.setAttribute("id","usr"+codeitemid);
					if("3".equals(object_type) && "UN".equalsIgnoreCase(codesetid))
					    child.setAttribute("type","true");
					else if("4".equals(object_type) && "UM".equalsIgnoreCase(codesetid))
					    child.setAttribute("type","true");
					else if("1".equals(object_type) && ("UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid)))
					    child.setAttribute("type","true");
				}
				else
					child.setAttribute("id",PubFunc.encrypt(codeitemid));
			}
			else
				child.setAttribute("id",PubFunc.encrypt(codeitemid));
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			child.setAttribute("href",getHrefContext(flag,model,codeitemid,codesetid));
			
			if("11".equals(model)) // 11：人岗匹配(单考核对象)
				child.setAttribute("target", "mil_body"); 
			else
				child.setAttribute("target", "_self"); 
			
			String a_xml="/performance/implement/dataGather/per_object_tree.jsp?planId="+planId+"&codeid="+codeitemid+"&model="+model+"&codesetid="+codesetid;
          //  if(!flag.equals("1"))
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
		Connection conn=null;
		ArrayList list=new ArrayList();
		try {
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			
//			String object_type="2";
//			if(this.planId.indexOf(",")==-1&&this.planId.trim().length()>0)
//			{
//				rs=stmt.executeQuery("select object_type from per_plan where plan_id="+this.planId);
//				if(rs.next())
//					object_type=rs.getString("object_type");
//			
//			}
//			ArrayList object_list=new ArrayList();
//			if(object_type.equals("1"))
//				object_list=getObjectList(object_type);
			
			ArrayList object_list=getObjectList(object_type);			
			if("-1".equals(this.codeid))
			{
				ArrayList alist=new ArrayList();
				LazyDynaBean abean=null;
				int length=0;
				rs=dao.search("select * from organization where parentid=codeitemid order by a0000");
				while(rs.next())
				{
					abean=new LazyDynaBean();
					abean.set("codeitemid",rs.getString("codeitemid"));
					abean.set("codeitemdesc",rs.getString("codeitemdesc"));
					abean.set("codesetid", "UN");
					if(object_list.size()>0)
					{
						boolean isExist=false;
						for(int i=0;i<object_list.size();i++)
						{
							LazyDynaBean bean=(LazyDynaBean)object_list.get(i);
							String object_id=(String)bean.get("object_id");
							if(object_id.equalsIgnoreCase(rs.getString("codeitemid")))
								isExist=true;
						}	
						if(isExist)
						{
							abean.set("flag","1");
						}
						else
							abean.set("flag","0");
					}
					else
						abean.set("flag","0");
					alist.add(abean);
					length=rs.getString("codeitemid").length();
				}
				
				StringBuffer un=new StringBuffer("/");
				String planIDs=this.planId.replaceAll(",","','");
				
				if(planIDs!=null && planIDs.trim().length()>0 && !"null".equalsIgnoreCase(planIDs))
				{
					String sql="select distinct "+Sql_switcher.substr("b0110","1",String.valueOf(length))+" from per_object where plan_id in ('"+planIDs+"') ";				
				
					if(this.userView!=null)
					{
						PerEvaluationBo bo=new PerEvaluationBo(conn,this.userView);
						String whl=bo.getPriv_where();
						if(whl!=null&&whl.trim().length()>0)
						{
							sql+=whl;
						}					
					}
					rs=dao.search(sql);
					
					while(rs.next())
					{
						un.append(rs.getString(1)+"/");
					}
				}
				
				for(int i=0;i<alist.size();i++)
				{
					abean=(LazyDynaBean)alist.get(i);
					String codeitemid=(String)abean.get("codeitemid");
					String flag=(String)abean.get("flag");
					if("1".equals(flag)||un.indexOf("/"+codeitemid+"/")!=-1)
						list.add(abean);
				}
			}
			else
			{
				
			
				rs=dao.search("select * from organization where parentid='"+this.codeid+"' and parentid<>codeitemid and codesetid<>'@K'  order by a0000 ");
				ArrayList alist=new ArrayList();
				LazyDynaBean abean=null;
				int length=0;
				
				while(rs.next())
				{
					abean=new LazyDynaBean();
					abean.set("codeitemid",rs.getString("codeitemid"));
					abean.set("codeitemdesc",rs.getString("codeitemdesc"));
					abean.set("codesetid", rs.getString("codesetid"));					
					if(object_list.size()>0)
					{
						boolean isExist=false;
						for(int i=0;i<object_list.size();i++)
						{
							LazyDynaBean bean=(LazyDynaBean)object_list.get(i);
							String object_id=(String)bean.get("object_id");
							if(object_id.equalsIgnoreCase(rs.getString("codeitemid")))
								isExist=true;
						}	
						if(isExist)
						{
							abean.set("flag","1");
						}
						else
							abean.set("flag","0");
					}
					else
						abean.set("flag","0");
					alist.add(abean);
					if(rs.getString("codeitemid").length()>length)
						length=rs.getString("codeitemid").length();
				}
				
				StringBuffer un=new StringBuffer("/");
				StringBuffer um=new StringBuffer("/");
				String planIDs=this.planId.replaceAll(",","','");
				
				if(planIDs!=null && planIDs.trim().length()>0)
				{
					String sql="select distinct "+Sql_switcher.substr("b0110","1",String.valueOf(length))+","+Sql_switcher.substr("e0122","1",String.valueOf(length))+" from per_object where plan_id  in ('"+planIDs+"')";
				
					if("UN".equals(this.codesetid))
						sql+=" and b0110 like '"+this.codeid+"%'";
					else if("UM".equals(this.codesetid))
						sql+=" and (e01a1 like '"+this.codeid+"%' or e01a1='' or e01a1 is null)";
										
					if(this.userView!=null)
					{
						PerEvaluationBo bo=new PerEvaluationBo(conn,this.userView);
						String whl=bo.getPriv_where();
						if(whl!=null&&whl.trim().length()>0)
						{
							sql+=whl;
						}
					
					}
				
					rs=dao.search(sql);
					while(rs.next())
					{
						if(rs.getString(1)!=null)
							un.append(rs.getString(1)+"/");
						if(rs.getString(2)!=null)
							um.append(rs.getString(2)+"/");
					}
				} 
				
				for(int i=0;i<alist.size();i++)
				{
					abean=(LazyDynaBean)alist.get(i);
					String codeitemid=(String)abean.get("codeitemid");
					String codesetid=(String)abean.get("codesetid");
					String flag=(String)abean.get("flag");
					if("UN".equals(codesetid))
					{
						if("1".equals(flag)||un.indexOf("/"+codeitemid+"/")!=-1)
							list.add(abean);
					}
					else if("UM".equals(codesetid))
					{
						if("1".equals(flag)||um.indexOf("/"+codeitemid+"/")!=-1)
							list.add(abean);
					}
				}
				
				if("2".equals(object_type))
				{
					ArrayList objList=getObjectList(object_type);
					for(int i=0;i<objList.size();i++)
					{
						abean=(LazyDynaBean)objList.get(i);
						String b0110=(String)abean.get("b0110");
						String e0122=(String)abean.get("e0122");
						String object_id = (String)abean.get("object_id");
						if("UN".equals(this.codesetid)&&e0122.length()==0&&b0110.equals(this.codeid))
						{
							
								LazyDynaBean a_bean=new LazyDynaBean();
								a_bean.set("codeitemid",(String)abean.get("object_id"));
								a_bean.set("codeitemdesc",(String)abean.get("a0101"));
								a_bean.set("codesetid","");
								a_bean.set("flag","1");
								list.add(a_bean);
								
						}
						if("UM".equals(this.codesetid)&&e0122.equals(this.codeid)&&!object_id.equals(e0122))
						{
							
								LazyDynaBean a_bean=new LazyDynaBean();
								a_bean.set("codeitemid",(String)abean.get("object_id"));
								a_bean.set("codeitemdesc",(String)abean.get("a0101"));
								a_bean.set("codesetid","");
								a_bean.set("flag","1");
								list.add(a_bean);
							
						}
					}
				}				
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
	
	public ArrayList getObjectList(String objectType)
	{
        // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		if("".equals(this.planId))
			return list;
		try {
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String planIDs=this.planId.replaceAll(",","','");
			
			String querySql="";
			//除了单指标趋势分析 多指标趋势分析是多计划  其余模块都是单计划
			//一个计划限定下找考核对象 排序按考核对象表来	
			//多个计划限定下找考核对象 排序按Usra01	
			boolean onlyOnePlan = planIDs.equals(this.planId);	
			if(!onlyOnePlan)
			{
				String sql="select distinct object_id,b0110,e0122,a0101  from per_object where plan_id in ('"+planIDs+"')";
				String sql2="select  object_id,b0110,e0122,a0101,a0000  from per_object where plan_id in ('"+planIDs+"')";
				if("UN".equals(this.codesetid))
				{	
					if(!"-1".equals(this.codeid))
					{
						sql+=" and b0110 like '"+this.codeid+"%' ";
						sql2+=" and b0110 like '"+this.codeid+"%' ";
					}
				}
				else if("UM".equals(this.codesetid))
				{
					if(!"-1".equals(this.codeid))
					{
						sql+=" and e0122 like '"+this.codeid+"%' ";
						sql2+=" and e0122 like '"+this.codeid+"%' ";
					}
				}

				if(this.userView!=null)
				{
					PerEvaluationBo bo=new PerEvaluationBo(conn,this.userView);
					String whl=bo.getPriv_where();
					if(whl!=null&&whl.trim().length()>0)
					{
							sql+=whl;
							sql2+=whl;
					}					
				}				
				String sql1 = "select a.*,usra01.a0000 from ("+sql+") a left join usra01 on usra01.a0100=a.object_id ";				
				if("2".equals(this.object_type))//对应一个人员计划或者很多计划（任意类型）
					querySql=sql1+"  order by usra01.B0110, usra01.E0122, usra01.E01A1, usra01.A0000";
				else//对应一个非人员计划
					querySql=sql2+"  order by a0000";
			}else
			{
				String sql2="select  object_id,b0110,e0122,a0101,a0000  from per_object where plan_id ="+planIDs;
				if("UN".equals(this.codesetid))
				{	
					if(!"-1".equals(this.codeid))
						sql2+=" and b0110 like '"+this.codeid+"%' ";
				}
				else if("UM".equals(this.codesetid))
				{
					if(!"-1".equals(this.codeid))
						sql2+=" and e0122 like '"+this.codeid+"%' ";
				}

				if(this.userView!=null)
				{
					PerEvaluationBo bo=new PerEvaluationBo(conn,this.userView);
					String whl=bo.getPriv_where();
					if(whl!=null&&whl.trim().length()>0)
							sql2+=whl;					
				}
	
				if("2".equals(this.object_type))
					querySql=sql2+" order by B0110, E0122, E01A1, A0000 ";
				else
					querySql=sql2+" order by  A0000";
	
			}
			
			rs = dao.search(querySql);
			LazyDynaBean abean=null;
			while(rs.next())
			{
				abean=new LazyDynaBean();
				abean.set("b0110",rs.getString("b0110")!=null?rs.getString("b0110"):"");
				abean.set("e0122",rs.getString("e0122")!=null?rs.getString("e0122"):"");
				abean.set("object_id",rs.getString("object_id"));
				abean.set("a0101",rs.getString("a0101"));
				list.add(abean);
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
	
}
