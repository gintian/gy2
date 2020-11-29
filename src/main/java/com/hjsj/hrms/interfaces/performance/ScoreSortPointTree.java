package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
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
 * <p>Title:ScoreSortPointTree.java</p>
 * <p>Description:绩效评估/显示/排序/指标树</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-20 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ScoreSortPointTree
{
	
	private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)
    private String planId;
    private String code;
    private String itemHasChild;
    private String templateId;
    private String computeFashion;
    private String object_type;

    public ScoreSortPointTree(String planId, String code, String itemHasChild, String computeFashion,String busitype)
    {

		this.itemHasChild = itemHasChild;
		this.planId = planId;
		this.code = code;
		Connection conn = null;
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    RecordVo vo = new RecordVo("per_plan");
		    vo.setString("plan_id", this.planId);
		    vo = dao.findByPrimaryKey(vo);
		    templateId = vo.getString("template_id");
		    object_type = vo.getString("object_type");
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
				if (conn != null)
				    conn.close();
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }
		}
	
		this.computeFashion = computeFashion;
		this.busitype = busitype;
    }

    public String outPutXmlStr()
    {

		if ("1".equals(this.computeFashion))
		    return outPutXmlStr1();
		else if ("2".equals(this.computeFashion) || "4".equals(this.computeFashion))
		    return outPutXmlStr2();
		else if ("3".equals(this.computeFashion))
		    return outPutXmlStr3();
		return outPutXmlStr1();
    }

    public String outPutXmlStr2()
    {

		StringBuffer xml = new StringBuffer();
		try
		{
		    Element root = new Element("TreeNode");
		    // 设置根元素属性
		    root.setAttribute("id", "");
		    root.setAttribute("text", "root");
		    root.setAttribute("title", "organization");
		    // 创建xml文档自身
		    Document myDocument = new Document(root);
	
		    ArrayList list = new ArrayList();
		    if("2".equals(this.computeFashion))
			 list = getChildList2();
		    else if("4".equals(this.computeFashion))
			list = getChildList4();
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
				child.setAttribute("icon", "/images/icon_fbxgfx.gif");
		
				if (code.indexOf("S_") > -1 || code.indexOf("VoteNum") > -1)
				    child.setAttribute("icon", "/images/compute.gif");
		
				if ("point".equals(type))// 指标节点
				    child.setAttribute("href", "javascript:getSelPoint('" + code + "','" + disp + "','" + type + "')");
		
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

    public String outPutXmlStr3()
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
	
		    ArrayList list = getChildList3();
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
				child.setAttribute("icon", "/images/icon_fbxgfx.gif");
		
				if (code.indexOf("S_") > -1)
				    child.setAttribute("icon", "/images/compute.gif");
		
				if ("point".equals(type))// 指标节点
				    child.setAttribute("href", "javascript:getSelPoint('" + code + "','" + disp + "','" + type + "')");
		
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

    public String outPutXmlStr1()
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
	
		    ArrayList list = getChildList();
		    for (Iterator t = list.iterator(); t.hasNext();)
		    {
				LazyDynaBean bean = (LazyDynaBean) t.next();
				Element child = new Element("TreeNode");
				String code = (String) bean.get("code");
				String disp = (String) bean.get("disp");
				String type = (String) bean.get("type");
				String itemHasChild = (String) bean.get("itemHasChild");
		
				child.setAttribute("id", code);
				child.setAttribute("text", disp);
				child.setAttribute("title", disp);
				child.setAttribute("target", "_self");
		
				child.setAttribute("icon", "/images/close.png");
				if ("point".equals(type))// 指标节点
				    child.setAttribute("icon", "/images/cards.bmp");
				if ("score".equals(code) || "grade_id".equals(code) || "mateSurmise".equals(code))
				    child.setAttribute("icon", "/images/compute.gif");
				if ("a0101".equals(code) || "e01a1".equals(code) || "e0122".equals(code) || "b0110".equals(code) || "body_id".equals(code))
				    child.setAttribute("icon", "/images/icon_fbxgfx.gif");
		
				if ("point".equals(type))// 指标节点
				{
				    child.setAttribute("href", "javascript:getSelPoint('" + code + "','" + disp + "','" + type + "')");
				} else if ("item".equals(type) && (itemHasChild != null && "1".equals(itemHasChild)))// 有子项目,项目节点
				{
				    String a_xml = "/performance/evaluation/show/scoreSortTree.jsp?planID=" + this.planId + "&code=" + code + "&itemHasChild=1&computeFashion=" + this.computeFashion;
				    child.setAttribute("xml", a_xml);
				    child.setAttribute("href", "javascript:getSelPoint('T_" + code + "','" + disp + "','" + type + "')");
				} else if ("item".equals(type) && (itemHasChild != null && "0".equals(itemHasChild)))// 没有子项目,项目节点
				{
				    String a_xml = "/performance/evaluation/show/scoreSortTree.jsp?planID=" + this.planId + "&code=" + code + "&itemHasChild=0&computeFashion=" + this.computeFashion;
				    child.setAttribute("xml", a_xml);
				    child.setAttribute("href", "javascript:getSelPoint('T_" + code + "','" + disp + "','" + type + "')");
				}
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

    private ArrayList getPointsList()
    {

		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		String sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.status,pp.score,po.Kh_content,po.Gd_principle from per_template_item pi,per_template_point pp,per_point po "
			+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + this.templateId + "' "; // pi.seq,
		sql += " order by pp.seq";
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(sql);
		    while (rs.next())
		    {
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("point_id", rs.getString("point_id"));
				abean.set("pointname", rs.getString("pointname") == null ? "" : rs.getString("pointname"));
				list.add(abean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if (rs != null)
			    rs.close();
			if (conn != null)
			    conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
	
		return list;
    }

    
    private ArrayList getPerGrade()
    {

		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(this.busitype!=null && this.busitype.trim().length()>0 && "1".equalsIgnoreCase(this.busitype))
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		String sql = "select * from "+per_comTable+" order by gradevalue desc";
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(sql);
		    while (rs.next())
		    {
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("grade_template_id", rs.getString("grade_template_id"));
				abean.set("gradedesc", rs.getString("gradedesc") == null ? "" : rs.getString("gradedesc"));
				list.add(abean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if (rs != null)
			    rs.close();
			if (conn != null)
			    conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
	
		return list;
    }

    private ArrayList getMainBody()
    {

		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		String sql = "select per_mainbodyset.* from per_plan_body,per_mainbodyset where per_plan_body.body_id=per_mainbodyset.body_id and plan_id=" + this.planId + " order by per_mainbodyset.seq";
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(sql);
		    while (rs.next())
		    {
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("body_id", rs.getString("body_id"));
				abean.set("name", rs.getString("name") == null ? "" : rs.getString("name"));
				list.add(abean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if (rs != null)
			    rs.close();
			if (conn != null)
			    conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
	
		return list;
    }

    private ArrayList getChildList3()
    {

		ArrayList list = new ArrayList();
		ArrayList pointLists = this.getPointsList();
		ArrayList perGradeLists = this.getPerGrade();
		for (int i = 0; i < pointLists.size(); i++)
		{
		    LazyDynaBean bean = (LazyDynaBean) pointLists.get(i);
		    String pointid = (String) bean.get("point_id");
		    String pointname = (String) bean.get("pointname");
		    for (int j = 0; j < perGradeLists.size(); j++)
		    {
				LazyDynaBean bean2 = (LazyDynaBean) perGradeLists.get(j);
				String gradeId = (String) bean2.get("grade_template_id");
				String gradedesc = (String) bean2.get("gradedesc");
				LazyDynaBean mybean = new LazyDynaBean();
				mybean.set("code", "P_C_" + pointid + "_G_G" + gradeId);
				mybean.set("disp", pointname + ": " + gradedesc);
				mybean.set("type", "point");
				list.add(mybean);
		    }
		}
		/*
		for (int j = 0; j < perGradeLists.size(); j++)
		{
		    LazyDynaBean bean2 = (LazyDynaBean) perGradeLists.get(j);
		    String gradeId = (String) bean2.get("grade_template_id");
		    String gradedesc = (String) bean2.get("gradedesc");
		    LazyDynaBean mybean = new LazyDynaBean();
		    mybean.set("code", "S_" + gradeId);
		    mybean.set("disp", "总分: " + gradedesc);
		    mybean.set("type", "point");
		    list.add(mybean);
		}
		*/
		FieldItem fielditem = DataDictionary.getFieldItem("E0122");				
		
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("code", "b0110");
		bean.set("disp", "单位名称");
		bean.set("type", "point");
		list.add(bean);
	
		if ("2".equals(this.object_type))
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "e0122");
		    bean.set("disp", fielditem.getItemdesc());
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "e01a1");
		    bean.set("disp", "岗位名称");
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", "姓名");
		    bean.set("type", "point");
		    list.add(bean);
		} else if ("3".equals(this.object_type))// 单位
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", "单位名称");
		    bean.set("type", "point");
		    list.add(bean);
		} else if ("1".equals(this.object_type))// 团队
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", "单位/部门");
		    bean.set("type", "point");
		    list.add(bean);
		}else if("4".equals(this.object_type))//部门
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", fielditem.getItemdesc());
		    bean.set("type", "point");
		    list.add(bean);
		}
		return list;
    }

    private ArrayList getChildList2()
    {

		ArrayList list = new ArrayList();
		ArrayList perGradeLists = this.getPerGrade();
		ArrayList mainbodyLists = this.getMainBody();
		for (int i = 0; i < mainbodyLists.size(); i++)
		{
		    LazyDynaBean bean = (LazyDynaBean) mainbodyLists.get(i);
		    String bodyid = (String) bean.get("body_id");
		    String name = (String) bean.get("name");
		    for (int j = 0; j < perGradeLists.size(); j++)
		    {
				LazyDynaBean bean2 = (LazyDynaBean) perGradeLists.get(j);
				String gradeId = (String) bean2.get("grade_template_id");
				String gradedesc = (String) bean2.get("gradedesc");
				LazyDynaBean mybean = new LazyDynaBean();
				mybean.set("code", "B_B" + bodyid + "_G" + gradeId);
				mybean.set("disp", name + ": " + gradedesc);
				mybean.set("type", "point");
				list.add(mybean);
		    }
		}
		for (int j = 0; j < perGradeLists.size(); j++)
		{
		    LazyDynaBean bean2 = (LazyDynaBean) perGradeLists.get(j);
		    String gradeId = (String) bean2.get("grade_template_id");
		    String gradedesc = (String) bean2.get("gradedesc");
		    LazyDynaBean mybean = new LazyDynaBean();
		    mybean.set("code", "S_" + gradeId);
		    mybean.set("disp", "总分: " + gradedesc);
		    mybean.set("type", "point");
		    list.add(mybean);
		}
		FieldItem fielditem = DataDictionary.getFieldItem("E0122");				    		    
		LazyDynaBean bean = new LazyDynaBean();
	/*
		bean.set("code", "VoteNum");
		bean.set("disp", "总分: ");
		bean.set("type", "point");
		list.add(bean);
	*/
		bean = new LazyDynaBean();
		bean.set("code", "b0110");
		bean.set("disp", "单位名称");
		bean.set("type", "point");
		list.add(bean);
	
		if ("2".equals(this.object_type))
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "e0122");
		    bean.set("disp", fielditem.getItemdesc());
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "e01a1");
		    bean.set("disp", "岗位名称");
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", "姓名");
		    bean.set("type", "point");
		    list.add(bean);
		} else if ("3".equals(this.object_type))// 单位
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", "单位名称");
		    bean.set("type", "point");
		    list.add(bean);
		} else if ("1".equals(this.object_type))// 团队
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", "单位/部门");
		    bean.set("type", "point");
		    list.add(bean);
		}else if("4".equals(this.object_type))//部门
		{
		    bean = new LazyDynaBean();
		    bean.set("code", "a0101");
		    bean.set("disp", fielditem.getItemdesc());
		    bean.set("type", "point");
		    list.add(bean);
		}
		return list;
    }

    private ArrayList getPerMainBodySetList()
    {

		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		String sql = "select per_mainbodyset.* from per_plan_body,per_mainbodyset where per_plan_body.body_id=per_mainbodyset.body_id and plan_id=" + this.planId + " order by per_mainbodyset.seq";
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(sql);
		    while (rs.next())
		    {
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("body_id", rs.getString("body_id"));
				abean.set("name", rs.getString("name") == null ? "" : rs.getString("name"));
				list.add(abean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if (rs != null)
			    rs.close();
			if (conn != null)
			    conn.close();
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }
		}
	
		return list;
    }

    private ArrayList getChildList4()
    {

		ArrayList list = new ArrayList();
		Connection conn=null;
		try
		{
		    conn = AdminDb.getConnection();
		    RecordVo vo = new RecordVo("per_plan");
		    ContentDAO dao = new ContentDAO(conn);
		    vo.setInt("plan_id", Integer.parseInt(this.planId));
		    vo = dao.findByPrimaryKey(vo);
	
		    String template_id = vo.getString("template_id");
		    ArrayList mainbodySetList = getPerMainBodySetList(); // 主体类别列表
		    BatchGradeBo bb = new BatchGradeBo(conn, this.planId);
		    ArrayList list1 = bb.getPerformanceStencilList(template_id, false);
		    ArrayList items = (ArrayList) list1.get(0); // 模版顶层项目列表
	
		    for (int i = 0; i < mainbodySetList.size(); i++)
		    {
				LazyDynaBean abean = (LazyDynaBean) mainbodySetList.get(i);
				String body_id = (String) abean.get("body_id");
				String name = (String) abean.get("name");
		
				LazyDynaBean mybean = new LazyDynaBean();
				mybean.set("code", "B" + body_id + "_PCount");
				mybean.set("disp", name + ": 人数");
				mybean.set("type", "point");
				list.add(mybean);
		
				mybean = new LazyDynaBean();
				mybean.set("code", "B" + body_id + "_VCount");
				mybean.set("disp", name + ": 票数");
				mybean.set("type", "point");
				list.add(mybean);
		
				for (Iterator t = items.iterator(); t.hasNext();)
				{
				    String[] temp = (String[]) t.next();
				    if (temp[1] == null)
				    {
					String itemid = temp[0];
					String itemdesc = temp[3];
					mybean = new LazyDynaBean();
					mybean.set("code", "B" + body_id + "_I" + itemid);
					mybean.set("disp", name + ": " + itemdesc);
					mybean.set("type", "point");
					list.add(mybean);
				    }
				}
		    }
		    FieldItem fielditem = DataDictionary.getFieldItem("E0122");				    
		    
		    LazyDynaBean bean = new LazyDynaBean();
		    bean.set("code", "score");
		    bean.set("disp", "计算总分: ");
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "original_score");
		    bean.set("disp", "总分: ");
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "grade_id");
		    bean.set("disp", "等级");
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "exX_object");
		    bean.set("disp", "等级系数");
		    bean.set("type", "point");
		    list.add(bean);
	
		    bean = new LazyDynaBean();
		    bean.set("code", "b0110");
		    bean.set("disp", "单位名称");
		    bean.set("type", "point");
		    list.add(bean);
	
		    if ("2".equals(this.object_type))
		    {
				bean = new LazyDynaBean();
				bean.set("code", "e0122");
				bean.set("disp", fielditem.getItemdesc());
				bean.set("type", "point");
				list.add(bean);
		
				bean = new LazyDynaBean();
				bean.set("code", "e01a1");
				bean.set("disp", "岗位名称");
				bean.set("type", "point");
				list.add(bean);
		
				bean = new LazyDynaBean();
				bean.set("code", "a0101");
				bean.set("disp", "姓名");
				bean.set("type", "point");
				list.add(bean);
		    } else if ("3".equals(this.object_type))// 单位
		    {
				bean = new LazyDynaBean();
				bean.set("code", "a0101");
				bean.set("disp", "单位名称");
				bean.set("type", "point");
				list.add(bean);
		    } else if ("1".equals(this.object_type))// 团队
		    {
				bean = new LazyDynaBean();
				bean.set("code", "a0101");
				bean.set("disp", "单位/部门");
				bean.set("type", "point");
				list.add(bean);
		    } else if ("4".equals(this.object_type))// 部门
		    {
				bean = new LazyDynaBean();
				bean.set("code", "a0101");
				bean.set("disp", fielditem.getItemdesc());
				bean.set("type", "point");
				list.add(bean);
		    }
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {			
				if (conn != null)
				    conn.close();
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }
		}
		return list;
    }

    private ArrayList getChildList()
    {

		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		try
		{
		    if (this.templateId == null || "".equals(this.templateId))
		    	return list;
		    String sql = "";
		    if (this.itemHasChild == null || "".equals(this.itemHasChild)) // 项目的根节点
		    	sql = "select item_id code,itemdesc disp,'item' type,parent_id,child_id from  per_template_item  where template_id='" + this.templateId + "' and parent_id is null";
		    else if (this.itemHasChild != null && "1".equals(this.itemHasChild)) // 项目的子节点
		    	sql = "select item_id code,itemdesc disp,'item' type,parent_id,child_id from  per_template_item  where template_id='" + this.templateId + "' and parent_id=" + this.code;
		    else if (this.itemHasChild != null && "0".equals(this.itemHasChild)) // 指标节点
		    	sql = "select a.point_id code,b.pointname disp,'point' type,'' parent_id,'' child_id  from per_template_point a,per_point b where a.point_id=b.point_id and a.item_id="
		    		+ this.code;
	
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(sql);
		    while (rs.next())
		    {
				LazyDynaBean bean = new LazyDynaBean();
				String theCode = rs.getString("code");
				if (this.itemHasChild != null && "0".equals(this.itemHasChild)) // 指标节点
					theCode="C_"+theCode;
				bean.set("code", theCode);
				bean.set("disp", rs.getString("disp"));
				String type = rs.getString("type");
				bean.set("type", type);
				String child_id = rs.getString("child_id");
				if (child_id == null && "item".equals(type))
				    bean.set("itemHasChild", "0");
				else
				    bean.set("itemHasChild", "1");
				list.add(bean);
		    }
		    FieldItem fielditem = DataDictionary.getFieldItem("E0122");		
		    if (this.itemHasChild == null || "".equals(this.itemHasChild))
		    {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("code", "score");
				bean.set("disp", "总分");
				bean.set("type", "point");
				list.add(bean);
		
				if(this.busitype!=null && this.busitype.trim().length()>0 && "1".equals(this.busitype))
				{
					bean = new LazyDynaBean();
					bean.set("code", "mateSurmise");
					bean.set("disp", "匹配度");
					bean.set("type", "point");
					list.add(bean);
				}
				
				bean = new LazyDynaBean();
				bean.set("code", "grade_id");
				bean.set("disp", "等级");
				bean.set("type", "point");
				list.add(bean);
		
				bean = new LazyDynaBean();
				bean.set("code", "b0110");
				bean.set("disp", "单位名称");
				bean.set("type", "point");
				list.add(bean);
				if ("2".equals(this.object_type))
				{
				    bean = new LazyDynaBean();
				    bean.set("code", "e0122");
				    bean.set("disp", fielditem.getItemdesc());
				    bean.set("type", "point");
				    list.add(bean);
		
				    bean = new LazyDynaBean();
				    bean.set("code", "e01a1");
				    bean.set("disp", "岗位名称");
				    bean.set("type", "point");
				    list.add(bean);
		
				    bean = new LazyDynaBean();
				    bean.set("code", "a0101");
				    bean.set("disp", "姓名");
				    bean.set("type", "point");
				    list.add(bean);
				} else if ("3".equals(this.object_type))// 单位
				{
				    bean = new LazyDynaBean();
				    bean.set("code", "a0101");
				    bean.set("disp", "单位名称");
				    bean.set("type", "point");
				    list.add(bean);
				} else if ("1".equals(this.object_type))// 团队
				{
				    bean = new LazyDynaBean();
				    bean.set("code", "a0101");
				    bean.set("disp", "单位/部门");
				    bean.set("type", "point");
				    list.add(bean);
				} else if ("4".equals(this.object_type))// 部门
				{
				    bean = new LazyDynaBean();
				    bean.set("code", "a0101");
				    bean.set("disp", fielditem.getItemdesc());
				    bean.set("type", "point");
				    list.add(bean);
				}
				
				bean = new LazyDynaBean();
			    bean.set("code", "body_id");
			    bean.set("disp", "对象类别");
			    bean.set("type", "point");
			    list.add(bean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if (rs != null)
			    rs.close();
			if (conn != null)
			    conn.close();
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }
		}
	
		return list;
    }
}
