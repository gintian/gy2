package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
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
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Description:绩效分析 单指标分析 选择指标树</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-08-15</p> 
 * @author JinChunhai
 * @version 4.2
 */

public class AnalyseSelPointTree
{
	
	public String busitype = "0"; // 业务分类字段 =0(绩效考核); =1(能力素质)
	public String planids;
	private HashMap pointsMap;
	private HashMap pointSetMap;
	private String pointsetid;
	private String flag;
	private String subsys_id;
	private UserView userView = null;
	private ArrayList customItems = new ArrayList();
	private String objSelected="";//选中人
	
	
	public AnalyseSelPointTree(String _planids, String pointsetid, String flag, UserView userView, String busitype,String objSelected)
	{
		this.userView = userView;
		this.pointsetid = pointsetid;
		this.flag = flag;
		this.busitype = busitype;
		this.objSelected=objSelected;
		this.subsys_id = "33";
		if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			this.subsys_id = "35";
		// 将从Cookie中取到计划号做一个筛选 因为有些可能已经被彻底删除了
		if (_planids.trim().length() > 0)
			_planids = getExistPlans(_planids);

		if (_planids.trim().length() == 0)
			this.planids = this.getPlanids();
		else
			this.planids = _planids;

		this.pointsMap = this.getSelectedPoint();
		this.pointSetMap = this.getPointSetMap();
		this.customItems=this.getCustomItems();
	}
	public AnalyseSelPointTree(String _planids, UserView userView, String busitype,String objSelected)
	{
		this.userView = userView;
		this.busitype = busitype;
		this.objSelected=objSelected;
		this.subsys_id = "33";
		if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			this.subsys_id = "35";
		// 将从Cookie中取到计划号做一个筛选 因为有些可能已经被彻底删除了
		if (_planids.trim().length() > 0)
			_planids = getExistPlans(_planids);

		if (_planids.trim().length() == 0)
			this.planids = this.getPlanids();
		else
			this.planids = _planids;
		
		this.pointsMap = this.getSelectedPoint();
		this.pointSetMap = this.getPointSetMap();
	}
	public String getExistPlans(String planids)
	{
		StringBuffer _planids = new StringBuffer();
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from per_plan where plan_id in (" + planids + ")");
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			while (rs.next())
				_planids.append(rs.getString("plan_id") + ",");
			if (_planids.length() > 0)
				_planids.setLength(_planids.length() - 1);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return _planids.toString();
	}
	public String getChildLinkPointSet(ContentDAO dao,String parentid)
	{
		String child_id = null;
		if(parentid.trim().length()==0)
			return child_id;
		ResultSet rst = null;
		try
		{ 
			StringBuffer sql = new StringBuffer();
			sql.append("select pointsetid from per_pointset where parent_id="+parentid+" and UPPER(subsys_id)='" + this.subsys_id + "' and validflag='1' order by seq ");
			rst = dao.search(sql.toString());  
			if (rst.next())			
				 child_id = rst.getString(1);			
				
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rst != null)
				{
					rst.close();
				}				
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return child_id;
	}
	
	public HashMap getParentLinkPointSet()
	{
		HashMap map = new HashMap();
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from per_pointset where subsys_id='" + this.subsys_id + "' and validflag='1'");
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			while (rs.next())
				map.put(rs.getString("pointsetid"), rs.getString("parent_id"));
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}

	public HashMap getPointSetMap()
	{
		HashMap map = new HashMap();
		if(this.planids.length()==0)
			return map;
		HashMap parentLinkPointSet = this.getParentLinkPointSet();
		ResultSet rs = null;
		Connection conn = null;
		String id="";
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(" select E01A1 from UsrA01 where A0100='"+this.objSelected+"'");
			while (rs.next())
			{
				id=rs.getString("E01A1");
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct pointsetid from per_point where UPPER(point_id) in (");//oracle查出的字符串自动转成大写  2013.12.12 pjf
			if(this.busitype!=null&& "1".equals(this.busitype)){
				sql.append("  select distinct point_id from per_history_result where status='0' and  plan_id in (" + this.planids + ") and object_id='"+this.objSelected+"' )");
			}else{
				sql.append("select  distinct point_id from per_template_point where item_id in (");
				sql.append(" select item_id from per_template_item where UPPER(template_id) in ");
				sql.append("(select UPPER(template_id) from per_plan where (busitype='"+this.busitype+"' or busitype is null) and plan_id in (" + this.planids + "))))");

			}
			

			rs = dao.search(sql.toString());

			while (rs.next())
			{
				Object pointsetid = rs.getString("pointsetid");
				while (pointsetid != null)
				{
					map.put((String) pointsetid, (String) pointsetid);
					pointsetid = parentLinkPointSet.get((String) pointsetid);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return map;
	}

	/** 获得个性项目列表 */
	public ArrayList getCustomItems()
	{
		ArrayList list = new ArrayList();
		if(this.planids.length()==0)
			return list;
		
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct item_id,itemdesc from per_template_item  where kind=2 and  UPPER(template_id) in ");
			sql.append("(select UPPER(template_id) from per_plan where plan_id in (" + this.planids + "))");

			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());

			while (rs.next())
			{
				LazyDynaBean lazyDynaBean = new LazyDynaBean();
				lazyDynaBean.set("codeitemid", "i_"+rs.getString("item_id"));
				lazyDynaBean.set("codeitemdesc", rs.getString("itemdesc"));
				lazyDynaBean.set("aflag", "-1");
				list.add(lazyDynaBean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return list;
	}

	public String getPlanids()
	{
		StringBuffer planids = new StringBuffer();

		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			PerformanceAnalyseBo bo = new PerformanceAnalyseBo(conn,this.userView);
			ArrayList planList=new ArrayList();
			if(!"".equals(this.objSelected)){
				 planList = bo.getPlanListByModel(1, "-1", "-1", this.userView,this.busitype,this.objSelected);
			}else{
				 planList=bo.getPlanList(1, "-1", "-1", this.userView,this.busitype);
			}
			
			for (int i = 0; i < planList.size(); i++)
			{
				LazyDynaBean abean = (LazyDynaBean) planList.get(i);
				String planid = (String) abean.get("plan_id");
				planids.append(planid + ",");
			}
			if (planids.length() > 0)
				planids.setLength(planids.length() - 1);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return planids.toString();
	}

	public String GetTreeXMLString() throws GeneralException
	{
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
		ArrayList list = getInfoList();
		for (Iterator t = list.iterator(); t.hasNext();)
		{
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String aflag = (String) abean.get("aflag");
			/** aflag=-1是指标 个性项目 */
			if ("-1".equals(aflag))
				child.setAttribute("type", "true");
			else
				child.setAttribute("type", "false");

			child.setAttribute("id", codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			child.setAttribute("href", "");
			child.setAttribute("target", "mil_body");
			if (!"-1".equals(aflag))
				child.setAttribute("icon", "/images/open1.png");
			else
				child.setAttribute("icon", "/images/lock_co_1.gif");
			String a_xml = "/performance/perAnalyse/create_point_tree.jsp?planids=" + planids + "&flag=" + aflag + "&pointsetid=" + codeitemid + "&busitype=" +this.busitype;
			if (!"-1".equals(aflag))
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
		Connection conn = null;
		ArrayList list = new ArrayList();
		try
		{

			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
//			 SQL语句
			StringBuffer strsql = new StringBuffer("");			
			
			if ("-1".equals(this.flag))
				return list;
			
			if ("customItems".equals(this.flag))//点击个性项目节点
				list.addAll(this.customItems);	
			else 
			{
				if ("0".equals(this.flag)) // 指标类别
				{
					strsql.append("select * from per_pointset  where ");
					if ("0".equals(this.pointsetid))
						strsql.append("  parent_id is null and UPPER(subsys_id)='" + subsys_id.toUpperCase() + "' and validflag='1'  order by seq ");
					else
						strsql.append("  parent_id=" + this.pointsetid + " and UPPER(subsys_id)='" + subsys_id.toUpperCase() + "' and validflag='1' order by seq ");
				} else if ("1".equals(this.flag))	// 指标
					strsql.append("select * from per_point where pointsetid=" + this.pointsetid + " and validflag='1' order by seq");				
				
				rs = dao.search(strsql.toString());
				while (rs.next())
				{
					LazyDynaBean lazyDynaBean = new LazyDynaBean();
					if ("0".equals(this.flag)) // 指标类别
					{
						//指标分类的权限不控制   2013.11.26 pjf
						/*if (!this.userView.isSuper_admin() && !this.userView.getGroupId().equals("1"))
						{
							if (!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("pointsetid")))
								continue;
						}
						*/
						if (this.pointSetMap.get(rs.getString("pointsetid")) == null)
							continue;

						lazyDynaBean.set("codeitemid", rs.getString("pointsetid"));
						lazyDynaBean.set("codeitemdesc", rs.getString("pointsetname"));
						String child_id = rs.getString("child_id") != null ? rs.getString("child_id") : "";
						if (child_id.length() > 0)
							lazyDynaBean.set("aflag", "0");
						else
							lazyDynaBean.set("aflag", "1");
					} else 	if ("1".equals(this.flag))
					// 指标
					{

						if (!this.userView.isSuper_admin() && !"1".equals(this.userView.getGroupId()))
						{
							if (!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
								continue;
						}
						if (pointsMap.get(rs.getString("point_id").toUpperCase()) == null)
							continue;
						lazyDynaBean.set("codeitemid", rs.getString("point_id"));
						lazyDynaBean.set("codeitemdesc", rs.getString("pointname"));
						lazyDynaBean.set("aflag", "-1");
					}

					list.add(lazyDynaBean);
				}
				
				if ("0".equals(this.pointsetid)) // 顶层指标类别后面加个性项目类别
				{
					if(this.customItems.size()>0)
					{
						LazyDynaBean lazyDynaBean = new LazyDynaBean();
						lazyDynaBean.set("codeitemid","customItemType" );
						lazyDynaBean.set("codeitemdesc", "个性项目");
						lazyDynaBean.set("aflag", "customItems");
						list.add(lazyDynaBean);
					}
				}
			}					
			
			if ("0".equals(this.flag))
			{
				strsql.setLength(0);
				strsql.append("select * from per_point where pointsetid=" + this.pointsetid + " and validflag='1' order by seq");
				rs = dao.search(strsql.toString());
				while (rs.next())
				{
					LazyDynaBean lazyDynaBean = new LazyDynaBean();
					if (!this.userView.isSuper_admin() && !"1".equals(this.userView.getGroupId()))
					{
						if (!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
							continue;
					}
					if (pointsMap.get(rs.getString("point_id").toUpperCase()) == null)
						continue;
					lazyDynaBean.set("codeitemid", rs.getString("point_id"));
					lazyDynaBean.set("codeitemdesc", rs.getString("pointname"));
					lazyDynaBean.set("aflag", "-1");
					list.add(lazyDynaBean);
				}

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	/** 可以选择的指标 */
	public HashMap getSelectedPoint()
	{
		HashMap map = new HashMap();
		if(this.planids.length()==0)
			return map;
		ResultSet rs = null;
		Connection conn = null;
		String id="";
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(" select distinct e01a1 from usra01,per_competency_modal where a0100='"+this.objSelected+"' and e01a1=object_id");
			
			while (rs.next())
			{
				id=rs.getString("E01A1");
			}
			StringBuffer sql = new StringBuffer();
			if(this.busitype!=null&& "1".equals(this.busitype)){
				sql.append("  select distinct point_id from per_history_result where status='0' and  plan_id in (" + this.planids + ") and object_id='"+this.objSelected+"'");
			}else{
				sql.append("select  distinct point_id from per_template_point where item_id in (");
				sql.append(" select item_id from per_template_item where UPPER(template_id) in ");
				sql.append("(select UPPER(template_id) from per_plan where (busitype='"+this.busitype+"' or busitype is null ) and plan_id in (" + this.planids + ")))");
		
			}

			rs = dao.search(sql.toString());
			while (rs.next())
			{
				map.put(rs.getString("point_id").toUpperCase(), "1");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	/** 可以选择的指标 */
	public HashMap getFirstSelectedPoint()
	{
		HashMap map = new HashMap();
		if(this.planids.length()==0)
			return map;
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			
			StringBuffer sql = new StringBuffer();
			sql.append("select * from per_pointset  where ");
			sql.append("parent_id is null and subsys_id='" + this.subsys_id + "' and validflag='1' order by seq ");
			rs = dao.search(sql.toString());	
			String firstPointSet="";//指标顶层类中的第一个
			while (rs.next())
			{
				String temp=rs.getString("pointsetid");
				if (!this.userView.isSuper_admin() && !"1".equals(this.userView.getGroupId()))
				{
					if (!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("pointsetid")))
						continue;
				}

				if (this.pointSetMap.get(rs.getString("pointsetid")) == null)
					continue;
				
				if(temp!=null)
				{
					firstPointSet=temp;
					break;
				}
			}
			
			String lastPointSet = this.getChildLinkPointSet(dao,firstPointSet);//第一个顶层指标类的最后一个子孙指标类		
			if(lastPointSet==null)
				lastPointSet=firstPointSet;
			else
			{
				String temp=lastPointSet;				
			    while(lastPointSet!=null && this.pointSetMap.get(lastPointSet) != null)
			    {
			    	temp=lastPointSet;
			    	lastPointSet=this.getChildLinkPointSet(dao,lastPointSet);		    
			    }
			    lastPointSet=temp;
			}			
		    
			if(lastPointSet.trim().length()>0)
			{
				sql.setLength(0);
				sql.append("select point_id,pointname from per_point where pointsetid ="+lastPointSet);
				sql.append(" order by seq");			
			
				rs = dao.search(sql.toString());
				while (rs.next())
				{
					if (!this.userView.isSuper_admin() && !"1".equals(this.userView.getGroupId()))
					{
						if (!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
							continue;
					}
					if (pointsMap.get(rs.getString("point_id").toUpperCase()) == null)
						continue;
					
					map.put("point_id",rs.getString("point_id"));
					map.put("pointname",rs.getString("pointname"));
					if(map.size()>0)
						break;
				}
			}
			
			
			if(map.size()==0)
			{
				sql.setLength(0);
				sql.append("select  item_id,itemdesc from per_template_item where kind=2 and item_id in (");
				sql.append("select distinct item_id from per_template_item  where   UPPER(template_id) in ");
				sql.append("(select UPPER(template_id) from per_plan where plan_id in (" + this.planids + ")) ");
				sql.append(" ) order by seq");

				rs = dao.search(sql.toString());
				if (rs.next())
				{
					map.put("point_id","i_"+rs.getString("item_id"));
					map.put("pointname",rs.getString("itemdesc"));
				}			
			}			
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	
}
