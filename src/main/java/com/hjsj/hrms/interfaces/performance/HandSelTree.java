package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
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
import java.util.Set;

/**
 * <p>Title:HandSelTree.java</p>
 * <p>Description:绩效评估/显示/手工选择树/</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-22 11:11:11</p> *
 * @author JinChunhai
 * @version 1.0
 */

public class HandSelTree
{
	private String planId;

	private String code;

	private String type;

	Connection conn = null;

	private String object_type="2";
	
	public HandSelTree(String planId, String code, String type, Connection conn)
	{

		this.type = type;
		this.planId = planId;
		this.code = code;
		this.conn = conn;
		PerformanceImplementBo pb=new PerformanceImplementBo(this.conn);
		RecordVo vo=pb.getPerPlanVo(this.planId);
		object_type=String.valueOf(vo.getInt("object_type"));   //1部门 2：人员
	}


	private void closeRs(ResultSet rs)
	{

		try
		{
			if (rs != null)
				rs.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	// 子节点的类型是否为stype
	public boolean childIsType(String code, String codesetid)
	{

		ResultSet rs = null;
		boolean flag = false;

		StringBuffer sql = new StringBuffer();
		sql.append("select  count(*) from organization where codeitemid!=parentid and parentid='");
		sql.append(code);
		sql.append("' ");

		if ("UN".equals(codesetid))
			sql.append("and codeitemid in (" + this.fielterUN() + ")");
		else if ("UM".equals(codesetid))
			sql.append("and codeitemid in (" + this.fielterUM() + ")");

		sql.append(" and codesetid='");
		sql.append(codesetid + "'");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			if (rs.next())
				if(rs.getInt(1)>0)
					flag = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		return flag;
	}

	// 取得当前节点的类型
	public String getType(String code)
	{

		ResultSet rs = null;
		String codesetid = "";
		String sql = "select codesetid from organization where codeitemid='" + code + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("codesetid");
				if (temp != null)
					codesetid = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		return codesetid;
	}

	// 取得上级编码
	public String getParentCode(String code)
	{

		ResultSet rs = null;
		String parentCode = "";
		String sql = "select parentid from organization where codeitemid='" + code + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("parentid");
				if (temp != null)
					parentCode = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		return parentCode;
	}

	public String fielterUN()
	{

		ResultSet rs = null;
		String strUN = "";
		ArrayList un = new ArrayList();
		ArrayList list = new ArrayList();// 保存所有包括自己在内的上级结点
		HashMap unMap = new HashMap();

		String sql = "select distinct b0110 from PER_RESULT_" + this.planId;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				String code = rs.getString("b0110");
				if (code != null)
				{
					un.add(code);
					unMap.put(code, code);
				}

			}

			Set keyset = unMap.keySet();
			for (Iterator iter = keyset.iterator(); iter.hasNext();)
			{
				String uncode = (String) iter.next();
				String strSql = "select codeitemid from organization where childid!=codeitemid and  childid='" + uncode + "'";
				rs = dao.search(strSql);
				if (!rs.next())// 如果per_result_X表中的b0110指定的单位值为顶级单位节点则要通过e0122部门节点来取部门的直接上级单位节点
				{
					String sql1 = "select distinct e0122 from per_result_" + this.planId + " where b0110='" + uncode + "'";
					rs = dao.search(sql1);
					while (rs.next())
					{
						String e0122 = rs.getString("e0122");
						while (true)// 防止部门上级还是部门，采用循环来取到最上面部门的单位
						{
							String parentId = getParentId(e0122);
							if("".equals(parentId))
								break;
							String theType = getType(parentId);
							if ("UN".equals(theType))
							{
								if(unMap.get(parentId)==null)
									un.add(parentId);
								break;
							} else
								e0122 = parentId;
						}
					}
				}
			}
			
//			for (int i = 0; i < un.size(); i++)
//			{
//				String code = (String) un.get(i);
//				String strSql = "select codeitemid from organization where childid!=codeitemid and  childid='" + code + "'";
//				rs = stmt.executeQuery(strSql);
//				if (!rs.next())// 如果per_result_X表中的b0110指定的单位值为顶级单位节点则要通过e0122部门节点来取部门的直接上级单位节点
//				{
//					String sql1 = "select distinct e0122 from per_result_" + this.planId + " where b0110='" + code + "'";
//					rs = stmt.executeQuery(sql1);
//					while (rs.next())
//					{
//						String e0122 = rs.getString("e0122");
//						while (true)// 防止部门上级还是部门，采用循环来取到最上面部门的单位
//						{
//							String parentId = getParentId(e0122);
//							String theType = getType(parentId);
//							if (theType.equals("UN"))
//							{
//								System.out.println(parentId);
//								un.add(parentId);
//								break;
//							} else
//								e0122 = parentId;
//						}
//					}
//				}
//			}

			list = un;
			for (int i = 0; i < un.size(); i++)
			{
				String code = (String) un.get(i);

				while (true)
				{
					String parentId = getParentId(code);// 通过单位代码找得到直接上级单位节点
					if ("".equals(parentId))
						break;
					list.add(parentId); // 把所有存在人的单位节点都放到list中
					code = parentId;
				}

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}

		HashMap map = new HashMap();
		for (int j = 0; j < list.size(); j++)
		{
			String code = (String) list.get(j);
			if(map.get(code)==null)
			{
				map.put(code, code);
				strUN += ",'" + code + "'";
			}
			map.put(code, code);
		}
		if (!"".equals(strUN))
			strUN = strUN.substring(1);

		return strUN;
	}

	// 得到直接上级节点
	public String getParentId(String codeitemid)
	{

		ResultSet rs = null;
		String parentId = "";
		// String sql = "select codeitemid from organization where childid!=codeitemid and childid='" + codeitemid + "'";
		String sql = "select parentid from organization where parentid!=codeitemid and  codeitemid='" + codeitemid + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if (rs.next())
			{
				String code = rs.getString("parentid");
				if (code != null)
					parentId = code;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		return parentId;
	}

	public String fielterUM()
	{

		ResultSet rs = null;
		String strUM = "";
		ArrayList um = new ArrayList();
		ArrayList list = new ArrayList();// 保存所有包括自己在内的上级结点
		String sql = "select distinct e0122 from PER_RESULT_" + this.planId;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				String code = rs.getString("e0122");
				if (code != null)
					um.add(code);
			}

			list = um;
			for (int i = 0; i < um.size(); i++)
			{
				String code = (String) um.get(i);

				while (true)
				{
					String parentId = getParentId(code);// 得到直接上级节点
					if(parentId.length()==0)
						break;
					String theType = this.getType(parentId);
					if ("UN".equals(theType))
						break;
					list.add(parentId);
					code = parentId;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		HashMap map = new HashMap();
		for (int j = 0; j < list.size(); j++)
		{
			String code = (String) list.get(j);
			if(map.get(code)==null)
			{
				strUM += ",'" + code + "'";
				map.put(code, code);
			}
			map.put(code, code);
		}
		if (!"".equals(strUM))
			strUM = strUM.substring(1);
		return strUM;
	}

	// 取单位
	public ArrayList getB0110()
	{

		ResultSet rs = null;

		HashMap map = new HashMap();
		String sql = "select distinct b0110 from PER_RESULT_" + this.planId;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				String code = rs.getString("b0110");
				// list.add(code);
				String parentCode = "";
				while (true)
				{
					parentCode = this.getParentCode(code);
					String type = this.getType(parentCode);
					if ("".equals(parentCode))
						break;
					if (parentCode.equals(code))// 说明无上级
						break;
					else if ("UN".equals(type))
					{
						// list.add(parentCode);
						code = parentCode;
					} else
						break;
				}
				map.put(code,code);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		
		ArrayList list = new ArrayList();
		Set set = map.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();)
		{
			String element = (String) iter.next();
			list.add(element);
		}
		return list;
	}

	// 取部门（有父节点就放父节点否则放自己）
	public ArrayList getE0122()
	{

		ResultSet rs = null;
		HashMap map = new HashMap();
		String sql = "select distinct e0122 from PER_RESULT_" + this.planId;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				String code = rs.getString("e0122");
				// list.add(code);
				String parentCode = "";
				while (true)
				{
					parentCode = this.getParentCode(code);
					String type = this.getType(parentCode);
					if ("".equals(parentCode))
						break;
					if (parentCode.equals(code))// 说明无上级
						break;
					else if ("UM".equals(type))
					{
						code = parentCode;
					} else
						break;
				}
				map.put(code,code);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		
		ArrayList list = new ArrayList();
		Set set = map.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();)
		{
			String element = (String) iter.next();
			list.add(element);
		}		
		return list;
	}

	public ArrayList getObjs()
	{

		ResultSet rs = null;

		ArrayList list = new ArrayList();
		String sql = "select distinct object_id,a0000 from PER_RESULT_" + this.planId + " order by a0000 ";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				list.add(rs.getString("object_id"));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		return list;
	}
	
	public HashMap getObjMap()
	{

		ResultSet rs = null;

		HashMap map= new HashMap();
		String sql = "select distinct object_id,a0000 from PER_RESULT_" + this.planId + " order by a0000 ";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				map.put(rs.getString("object_id"),"");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		return map;
	}
	
	private ArrayList getChildList() throws GeneralException
	{

		ResultSet rs = null;
		ArrayList list = new ArrayList();

		String b0110 = "";
		String e0122 = "";
		String objs = "";

		ArrayList tempList = this.getB0110();
		for (int i = 0; i < tempList.size(); i++)
			b0110 += ",'" + tempList.get(i) + "'";

		tempList = this.getE0122();
		for (int i = 0; i < tempList.size(); i++)
			e0122 += ",'" + tempList.get(i) + "'";

		tempList = this.getObjs();
		for (int i = 0; i < tempList.size(); i++)
			objs += ",'" + tempList.get(i) + "'";

		try
		{
			String sql = "";
			if (this.type == null || "".equals(this.type)) // 单位节点--点击跟节点
				sql = "select codeitemid code,codeitemdesc disp,'UN' type,codesetid from organization where codeitemid in (" + (b0110.length() > 0 ? b0110.substring(1) : "''") + ")";
			if (this.type != null && "UN".equals(this.type) && childIsType(this.code, "UN")) // 单位节点--点击单位节点(只出现存在人的单位节点)
				sql = "select codeitemid code,codeitemdesc disp,'UN' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codeitemid in ("
						+ ("".equals(fielterUN()) ? "''" : fielterUN()) + ")";
			else
			// 单位的下级节点除了可能是单位外还有可能是部门，或者职位
			{
				if (this.type != null && "UN".equals(this.type) && childIsType(this.code, "UM") && !childIsType(this.code, "@K")) // 部门节点--点击单位节点(只出现存在人的部门节点)
					sql = "select codeitemid code,codeitemdesc disp,'UM' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codeitemid in ("
							+ (e0122.length() > 0 ? e0122.substring(1) : "''") + ")";
				else if (this.type != null && "UN".equals(this.type) && childIsType(this.code, "UM") && childIsType(this.code, "@K")) // 部门节点和职位节点--点击单位节点(只出现存在人的部门节点)
					sql = "select codeitemid code,codeitemdesc disp,'UM' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codeitemid in ("
							+ (e0122.length() > 0 ? e0122.substring(1) : "''") + ") union all "
							+ "select codeitemid code,codeitemdesc disp,'@K' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codesetid='@K'";
				else if (this.type != null && "UN".equals(this.type) && !childIsType(this.code, "UM") && childIsType(this.code, "@K")) // 职位节点--点击单位节点(只出现存在人的部门节点)
					sql = "select codeitemid code,codeitemdesc disp,'@K' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codesetid='@K'";
			}
			if (this.type != null && "UM".equals(this.type) && childIsType(this.code, "UM")) // 部门节点--点击部门节点(只出现存在人的部门节点)
				sql = "select codeitemid code,codeitemdesc disp,'UM' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codeitemid in ("
						+ ("".equals(fielterUM()) ? "''" : fielterUM()) + ")";
			if (this.type != null && "UM".equals(this.type) && childIsType(this.code, "@K")) // 职位节点--点击部门节点(不存在人的职位节点也要出来)
			{
				if(sql.length()>0)
					sql+=" union all select codeitemid code,codeitemdesc disp,'@K' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "'  and codesetid='@K'";
				else
					sql = "select codeitemid code,codeitemdesc disp,'@K' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "'  and codesetid='@K'";
			}				

			if (this.type != null && "@K".equals(this.type)) // 人员节点--点击职位节点
				sql = "select object_id code,a0101 disp,'emp' type,'codesetid' codesetid from per_result_" + this.planId + " where e01a1 ='" + this.code + "' order by a0000 ";

			ContentDAO dao = new ContentDAO(this.conn);

			if (!"".equals(sql))
			{
				rs = dao.search(sql);
				while (rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("code", rs.getString("code"));
					bean.set("disp", rs.getString("disp"));
					String type = rs.getString("type");
					
					if (this.type!=null&&rs.getString("codesetid").equals(this.type))
						bean.set("type", this.type);
					else
						bean.set("type", type);
					list.add(bean);
				}
			}
			if (this.type != null && "UM".equals(this.type)) // 点击部门节点,子节点可能就是人员
			{
				sql = "select object_id code,a0101 disp,'emp' type from per_result_" + this.planId + " where e01a1 is null and e0122='" + this.code + "' order by a0000 ";
				rs = dao.search(sql);
				while (rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("code", rs.getString("code"));
					bean.set("disp", rs.getString("disp"));
					bean.set("type", rs.getString("type"));
					list.add(bean);
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}

		return list;
	}
	/**
	 * 考核对象类别是非人员的
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getChildList2() throws GeneralException
	{

		ResultSet rs = null;
		ArrayList list = new ArrayList();

		String b0110 = "";
		String e0122 = "";
		String objs = "";

		ArrayList tempList = this.getB0110();
		for (int i = 0; i < tempList.size(); i++)
			b0110 += ",'" + tempList.get(i) + "'";

		tempList = this.getE0122();
		for (int i = 0; i < tempList.size(); i++)
			e0122 += ",'" + tempList.get(i) + "'";

		tempList = this.getObjs();
		for (int i = 0; i < tempList.size(); i++)
			objs += ",'" + tempList.get(i) + "'";

		try
		{
			String sql = "";
			if (this.type == null || "".equals(this.type)) // 单位节点--点击跟节点
				sql = "select codeitemid code,codeitemdesc disp,'UN' type,codesetid from organization where codeitemid in (" + (b0110.length() > 0 ? b0110.substring(1) : "''") + ")";
			if (this.type != null && "UN".equals(this.type) && childIsType(this.code, "UN")) // 单位节点--点击单位节点(只出现存在人的单位节点)
				sql = "select codeitemid code,codeitemdesc disp,'UN' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codeitemid in ("
						+ ("".equals(fielterUN()) ? "''" : fielterUN()) + ")";
			
			if (this.type != null && "UN".equals(this.type) && childIsType(this.code, "UM") && !childIsType(this.code, "@K")) // 部门节点--点击单位节点(只出现存在人的部门节点)
			{
				if(sql.length()>0)
					sql+=" union all ";
				sql += "select codeitemid code,codeitemdesc disp,'UM' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codeitemid in ("
				+ (e0122.length() > 0 ? e0122.substring(1) : "''") + ")";
			}				
			
			if (this.type != null && "UM".equals(this.type) && childIsType(this.code, "UM")) // 部门节点--点击部门节点(只出现存在人的部门节点)
				sql = "select codeitemid code,codeitemdesc disp,'UM' type,codesetid from organization where codeitemid!=parentid and parentid ='" + this.code + "' and codeitemid in ("
						+ ("".equals(fielterUM()) ? "''" : fielterUM()) + ")";
			
			ContentDAO dao = new ContentDAO(this.conn);

			if (!"".equals(sql))
			{
				rs = dao.search(sql);
				while (rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("code", rs.getString("code"));
					bean.set("disp", rs.getString("disp"));
					String type = rs.getString("type");
					if (this.type!=null&&rs.getString("codesetid").equals(this.type))
						bean.set("type", this.type);
					else
						bean.set("type", type);
					list.add(bean);
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}

		return list;
	}
	public boolean childIsEmp()
	{

		ResultSet rs = null;
		boolean flag = false;
		String sql = "select object_id from per_result_" + this.planId + " where e01a1 is null and e0122='" + this.code + "' order by a0000 ";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if (rs.next())
				flag = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.closeRs(rs);
		}
		return flag;
	}

	public String outPutXmlStr()
	{

		StringBuffer xml = new StringBuffer();
		try
		{
			// 创建xml文件的根元素
			Element root = new Element("TreeNode");
			// 设置根元素属性
			root.setAttribute("id", "");
			root.setAttribute("text", "root");
			root.setAttribute("title", "organization");
			// 创建xml文档自身
			Document myDocument = new Document(root);
			
			HashMap map = this.getObjMap();
			ArrayList list = new ArrayList();
			if("2".equals(object_type))
				list = getChildList();
			else
				list = getChildList2();
			for (Iterator t = list.iterator(); t.hasNext();)
			{
				LazyDynaBean bean = (LazyDynaBean) t.next();
				Element child = new Element("TreeNode");
				String code = (String) bean.get("code");
				String disp = (String) bean.get("disp");
				String type = (String) bean.get("type");

				child.setAttribute("id", code);
				child.setAttribute("text", disp);
				child.setAttribute("title", disp);
				child.setAttribute("target", "_self");

				child.setAttribute("icon", "/images/close.png");
				if ("UN".equals(type))
					child.setAttribute("icon", "/images/unit.gif");
				if ("UM".equals(type))
					child.setAttribute("icon", "/images/dept.gif");
				if ("@K".equals(type))
					child.setAttribute("icon", "/images/vpos_l.gif");
				if ("emp".equals(type))
					child.setAttribute("icon", "/images/man.gif");

//				if (type.equals("emp"))// 人员节点
//				{
//					child.setAttribute("href", "javascript:getSelPoint('" + code + "','" + disp + "','" + type + "')");
//				} else if (!type.equals("emp"))// 有子项目,项目节点
//				{
//					String a_xml = "/performance/evaluation/show/handSelTree.jsp?planID=" + this.planId + "&code=" + code + "&type=" + type;
//					child.setAttribute("xml", a_xml);
//					child.setAttribute("href", "javascript:getSelPoint('" + code + "','" + disp + "','" + type + "')");
//				}
				
				if (!"emp".equals(type))// 有子项目,项目节点
				{
					String a_xml = "/performance/evaluation/show/handSelTree.jsp?planID=" + this.planId + "&code=" + code + "&type=" + type;
					child.setAttribute("xml", a_xml);				
				}		
				if (map.get(code)!=null)// 叶子节点				
					child.setAttribute("href", "javascript:getSelPoint('" + code + "','" + disp + "','1')");
				else
					child.setAttribute("href", "javascript:getSelPoint('" + code + "','" + disp + "','0')");
				
				root.addContent(child);
			}
			XMLOutputter outputter = new XMLOutputter();
			// 格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			// 将生成的XML文件作为字符串形式
			xml.append(outputter.outputString(myDocument));

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return xml.toString();
	}

}
