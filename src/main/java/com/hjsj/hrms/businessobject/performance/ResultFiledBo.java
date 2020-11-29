package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.interview.PerformanceInterviewBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:ResultFiledBo.java</p>
 * <p>Description:结果归档</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-28 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ResultFiledBo
{
	private Connection cn = null;

	private int objType; // 考核对象类型[1-团队，2-人员]

	private String planid;

	private ArrayList topItems = null;

	private ArrayList bodyType = null;

	private String tableName = "Perform_Arch_Temp_Tab"; // 绩效归档临时表

	private String performArchiveScheType = "200";

	/** 归档类型 1--人员的归档 2--单位 部门 团队的归档 4--团队负责人的归档 */
	private String filedType;
	
	private String busiType ;

	public ResultFiledBo(String planid, Connection cn)
	{

		this.cn = cn;
		this.planid = planid;
		setObjType();
	}

	public ResultFiledBo(String planid, Connection cn, String filedType)
	{

		this.cn = cn;
		this.planid = planid;
		setObjType();

		if (filedType == null)
		{
			if (this.objType == 2) {
                this.filedType = "1";
            } else {
                this.filedType = "2";
            }
		} else {
            this.filedType = filedType;
        }
	}

	public ResultFiledBo(Connection cn)
	{

		this.cn = cn;
	}

	/*
	 * 获得考核对象类型[1-团队，2-人员]
	 */

	public void setObjType()
	{

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			String sql = "select object_type from per_plan where plan_id=" + this.planid;
			RowSet rowSet = dao.search(sql);
			LazyDynaBean abean = null;
			if (rowSet.next()) {
                this.objType = rowSet.getInt("object_type");
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/*
	 * 获得考核子集
	 */
	public ArrayList getSubSet()
	{

		ArrayList list = new ArrayList();
		StringBuffer strSql = new StringBuffer();
		strSql.append("select fieldsetid,customdesc from fieldset where useflag=1 ");// 显示已构库子集

		if ("1".equals(this.filedType) || "4".equals(this.filedType)) {
            strSql.append(" and  fieldsetid like 'A%' and fieldsetid!='A01'");
        } else if ("2".equals(this.filedType)) {
            strSql.append(" and fieldsetid like 'B%' and fieldsetid!='B01'");
        }

		strSql.append(" order by displayorder");
		try
		{

			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search(strSql.toString());
			while (rowSet.next())
			{
				CommonData vo = new CommonData(rowSet.getString("fieldsetid"), rowSet.getString("fieldsetid") + ":" + rowSet.getString("customdesc"));
				list.add(vo);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * 取得考核主体类别
	 */
	public HashMap getKhMainBodyType()
	{

		HashMap map = new HashMap();
		try
		{

			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search("select a.body_id,b.name from per_plan_body a, per_mainbodyset b where a.body_id=b.body_id and a.plan_id=" + this.planid + " order by a.body_id");
			bodyType = new ArrayList();
			while (rs.next())
			{
				String body_id = rs.getString("body_id");
				if ("-1".equals(body_id)) {
                    body_id = "X1";
                }
				map.put(body_id, rs.getString("name"));
				bodyType.add(body_id);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 获得考核指标的顶层项目
	 */
	public HashMap getTopItems()
	{

		HashMap map = new HashMap();
		try
		{

			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search("select item_id,itemdesc from per_template_item where parent_id is null and template_id=(select template_id from per_plan where plan_id=" + this.planid
					+ ") order by item_id");
			topItems = new ArrayList();
			while (rs.next())
			{
				map.put(rs.getString("item_id"), rs.getString("itemdesc"));
				topItems.add(rs.getString("item_id"));
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 取得源指标
	 */
	public ArrayList getPoints(String srcFldSetName,String busitype)
	{

		ArrayList list = new ArrayList();
		
		LazyDynaBean abean = new LazyDynaBean();
		abean.set("id", "plan_id");
		abean.set("name", "考核计划ID");
		abean.set("destFldId", this.getDestFldId("plan_id", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,0)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "planname");
		abean.set("name", "考核名称");
		abean.set("destFldId", this.getDestFldId("planname", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "A"));
		abean.set("destType", this.getDestCodeSet("planname", srcFldSetName));
		abean.set("dataType", "字符型(100)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "plancycle");
		abean.set("name", "考核周期");
		abean.set("destFldId", this.getDestFldId("plancycle", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "A"));
		abean.set("destType", this.getDestCodeSet("plancycle", srcFldSetName));
		abean.set("dataType", "字符型(50)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "create_date");
		abean.set("name", "考核时间");
		abean.set("destFldId", this.getDestFldId("create_date", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "D"));
		abean.set("destType", this.getDestCodeSet("create_date", srcFldSetName));
		abean.set("dataType", "日期型(8)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "plan_time");
		abean.set("name", "考核时间区间");
		abean.set("destFldId", this.getDestFldId("plan_time", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "A"));
		abean.set("destType", this.getDestCodeSet("plan_time", srcFldSetName));
		abean.set("dataType", "字符型(25)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "bodykind");
		abean.set("name", "对象类别");
		abean.set("destFldId", this.getDestFldId("bodykind", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "A"));
		abean.set("destType", this.getDestCodeSet("bodykind", srcFldSetName));
		abean.set("dataType", "字符型(25)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "original_score");
		abean.set("name", "计算得分");
		abean.set("destFldId", this.getDestFldId("original_score", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,4)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "score");
		abean.set("name", "总得分");
		abean.set("destFldId", this.getDestFldId("score", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,4)");
		list.add(abean);
		
		LoadXml loadxml = new LoadXml(this.cn, planid);
		Hashtable params = loadxml.getDegreeWhole();
		String WholeEvalMode = (String) params.get("WholeEvalMode");
		String deviationScoreUsed=(String) params.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是
		if("1".equals(deviationScoreUsed)){
			abean = new LazyDynaBean();
			abean.set("id", "reviseScore");
			abean.set("name", "纠偏总分");
			abean.set("destFldId", this.getDestFldId("reviseScore", srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,4)");
			list.add(abean);
		}

		if("1".equals(WholeEvalMode)){
			abean = new LazyDynaBean();
			abean.set("id", "whole_score");
			abean.set("name", "总体评价得分");
			abean.set("destFldId", this.getDestFldId("whole_score", srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,4)");
			list.add(abean);
		}

		// 业务分类字段 =0(绩效考核); =1(能力素质)
		if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
		{
			abean = new LazyDynaBean();
			abean.set("id", "postRuleScore");
			abean.set("name", "岗位标准分值");
			abean.set("destFldId", this.getDestFldId("postRuleScore", srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,4)");
			list.add(abean);
			
			abean = new LazyDynaBean();
			abean.set("id", "mateSurmise");
			abean.set("name", "匹配度");
			abean.set("destFldId", this.getDestFldId("mateSurmise", srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,4)");
			list.add(abean);			
		}
		
		abean = new LazyDynaBean();
		abean.set("id", "exS_GrpAvg");
		abean.set("name", "组内平均得分");
		abean.set("destFldId", this.getDestFldId("exS_GrpAvg", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,4)");
		list.add(abean);

		if(busitype==null || busitype.trim().length()<=0 || "0".equals(busitype))
		{
			abean = new LazyDynaBean();
			abean.set("id", "exX_object");
			abean.set("name", "等级系数");
			abean.set("destFldId", this.getDestFldId("exX_object", srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,4)");
			list.add(abean);
		}
		
		abean = new LazyDynaBean();//组内排名   zhaoxg  add  2014-4-16
		abean.set("id", "Ordering");
		abean.set("name", "组内排名");
		abean.set("destFldId", this.getDestFldId("Ordering", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,0)");
		list.add(abean);
		
		abean = new LazyDynaBean();//组内人数   zhaoxg  add  2014-4-16
		abean.set("id", "ex_GrpNum");
		abean.set("name", "组内人数");
		abean.set("destFldId", this.getDestFldId("ex_GrpNum", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,0)");
		list.add(abean);
		
		abean = new LazyDynaBean();//部门排名   chent add 20171011
		abean.set("id", "org_ordering");
		abean.set("name", "部门排名");
		abean.set("destFldId", this.getDestFldId("org_ordering", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,0)");
		list.add(abean);
		
		abean = new LazyDynaBean();//部门人数   chent add 20171011
		abean.set("id", "org_GrpNum");
		abean.set("name", "部门人数");
		abean.set("destFldId", this.getDestFldId("org_GrpNum", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
		abean.set("destType", "0");
		abean.set("dataType", "数值型(6,0)");
		list.add(abean);
		
		abean = new LazyDynaBean();
		abean.set("id", "resultdesc");
		abean.set("name", "等级");
		abean.set("destFldId", this.getDestFldId("resultdesc", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "A"));
		abean.set("destType", this.getDestCodeSet("resultdesc", srcFldSetName));
		abean.set("dataType", "字符型(50)");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "appraise");
		abean.set("name", "评语");
		abean.set("destFldId", this.getDestFldId("appraise", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
		abean.set("destType", "0");
		abean.set("dataType", "备注型");
		list.add(abean);

		// 业务分类字段 =0(绩效考核); =1(能力素质)
		if(busitype==null || busitype.trim().length()<=0 || "0".equals(busitype))
		{
			abean = new LazyDynaBean();
			abean.set("id", "article");
			abean.set("name", "绩效目标");
			abean.set("destFldId", this.getDestFldId("article", srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
			abean.set("destType", "0");
			abean.set("dataType", "备注型");
			list.add(abean);
	
			abean = new LazyDynaBean();
			abean.set("id", "summarize");
			abean.set("name", "绩效报告");
			abean.set("destFldId", this.getDestFldId("summarize", srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
			abean.set("destType", "0");
			abean.set("dataType", "备注型");
			list.add(abean);
		}
		
		abean = new LazyDynaBean();
		abean.set("id", "Interview");
		abean.set("name", "面谈");
		abean.set("destFldId", this.getDestFldId("Interview", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
		abean.set("destType", "0");
		abean.set("dataType", "备注型");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "self_idea");
		abean.set("name", "被考核人意见");
		abean.set("destFldId", this.getDestFldId("self_idea", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
		abean.set("destType", "0");
		abean.set("dataType", "备注型");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "reason");
		abean.set("name", "申诉理由");
		abean.set("destFldId", this.getDestFldId("reason", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
		abean.set("destType", "0");
		abean.set("dataType", "备注型");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "org_idea");
		abean.set("name", "单位复核意见");
		abean.set("destFldId", this.getDestFldId("org_idea", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
		abean.set("destType", "0");
		abean.set("dataType", "备注型");
		list.add(abean);

		abean = new LazyDynaBean();
		abean.set("id", "council_idea");
		abean.set("name", "委员会意见");
		abean.set("destFldId", this.getDestFldId("council_idea", srcFldSetName));
		abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "M"));
		abean.set("destType", "0");
		abean.set("dataType", "备注型");
		list.add(abean);
		// 评分
		HashMap map1 = this.getKhMainBodyType();
		
		RecordVo tempvo = this.getPerPlanVo(this.planid);
		String byModel = tempvo.getString("bymodel")==null?"":tempvo.getString("bymodel");
		if(!"1".equals(byModel)){//如果不是按岗位素质模型
			HashMap map = this.getTopItems();
			for (int i = 0; i < this.topItems.size(); i++)
			{
				String itemid = (String) this.topItems.get(i);
				String itemName = (String) map.get(itemid);
				for (int j = 0; j < this.bodyType.size(); j++)
				{
					String bodyTypeId = (String) this.bodyType.get(j);
					String bodyTypeName = (String) map1.get(bodyTypeId);
					abean = new LazyDynaBean();
					abean.set("id", "B" + bodyTypeId + "_I" + itemid);
					abean.set("name", "评分[" + bodyTypeName + "-" + itemName + "]");
					abean.set("destFldId", this.getDestFldId("B" + bodyTypeId + "_I" + itemid, srcFldSetName));
					abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
					abean.set("destType", "0");
					abean.set("dataType", "数值型(6,4)");
					list.add(abean);
				}
			}

			// 项目得分
			for (int i = 0; i < this.topItems.size(); i++)
			{
				String itemid = (String) this.topItems.get(i);
				String itemName = (String) map.get(itemid);
				abean = new LazyDynaBean();
				abean.set("id", "I" + itemid);
				abean.set("name", "项目得分[" + itemName + "]");
				abean.set("destFldId", this.getDestFldId("I" + itemid, srcFldSetName));
				abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
				abean.set("destType", "0");
				abean.set("dataType", "数值型(6,4)");
				list.add(abean);
			}
		}

		// 合计分
		for (int j = 0; j < this.bodyType.size(); j++)
		{
			String bodyTypeId = (String) this.bodyType.get(j);
			String bodyTypeName = (String) map1.get(bodyTypeId);
			abean = new LazyDynaBean();
			abean.set("id", "B" + bodyTypeId);
			abean.set("name", "合计分[" + bodyTypeName + "]");
			abean.set("destFldId", this.getDestFldId("B" + bodyTypeId, srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,4)");
			list.add(abean);
		}

		// 参评人数
		for (int j = 0; j < this.bodyType.size(); j++)
		{
			String bodyTypeId = (String) this.bodyType.get(j);
			String bodyTypeName = (String) map1.get(bodyTypeId);
			abean = new LazyDynaBean();
			abean.set("id", "BcpCou" + bodyTypeId);
			abean.set("name", "参评人数[" + bodyTypeName + "]");
			abean.set("destFldId", this.getDestFldId("BcpCou" + bodyTypeId, srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,0)");
			list.add(abean);
		}

		// 计划人数
		for (int j = 0; j < this.bodyType.size(); j++)
		{
			String bodyTypeId = (String) this.bodyType.get(j);
			String bodyTypeName = (String) map1.get(bodyTypeId);
			abean = new LazyDynaBean();
			abean.set("id", "BjhCou" + bodyTypeId);
			abean.set("name", "计划人数[" + bodyTypeName + "]");
			abean.set("destFldId", this.getDestFldId("BjhCou" + bodyTypeId, srcFldSetName));
			abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
			abean.set("destType", "0");
			abean.set("dataType", "数值型(6,0)");
			list.add(abean);
		}
		
		// 关联计划分数 创建引用的计划指标 lium
		List ids = loadxml.getRelatePlanValue("Plan","ID");
		if (ids != null && ids.size() > 0) {
			StringBuffer sqlId = new StringBuffer();
			sqlId.append("SELECT plan_id,name FROM per_plan WHERE plan_id IN (0");
			for (int i = 0, len = ids.size(); i < len; i++) {
				String id = (String) ids.get(i);
				sqlId.append(",").append(id);
			}
			sqlId.append(")");
			
			RowSet rs = null;
			try {
				rs = new ContentDAO(cn).search(sqlId.toString());
				while (rs.next()) {
					int id = rs.getInt("plan_id");
					String name = rs.getString("name");
					
					abean = new LazyDynaBean();
					abean.set("id", "G_" + id);
					abean.set("name", name);
					abean.set("destFldId", this.getDestFldId("G_" + id, srcFldSetName));
					abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, "N"));
					abean.set("destType", "0");
					abean.set("dataType", "数值型(6,4)");
					list.add(abean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeDbObj(rs);
			}
		}
		
		return list;
	}
	/**得到RecordVo*/
	public RecordVo getPerPlanVo(String plan_id){
    	RecordVo vo = new RecordVo("per_plan");
		try
		{
			vo.setString("plan_id", plan_id);
			ContentDAO dao = new ContentDAO(this.cn);
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }
	// 生成目标表结构和数据
	public void generateTempTable(String busitype) throws GeneralException
	{

		this.busiType = busitype;
		
		DbWizard dbWizard = new DbWizard(this.cn);
		if (dbWizard.isExistTable(tableName, false))
		{
			dbWizard.dropTable(tableName);
		}

		// 创建表
		Table table = new Table(tableName);
		Field obj = new Field("id");
		obj.setDatatype(DataType.INT);
		obj.setNullable(false);
		obj.setKeyable(true);
		table.addField(obj);

		obj = new Field("plan_id");
		obj.setDatatype(DataType.INT);
		obj.setNullable(false);
		obj.setKeyable(true);
		table.addField(obj);

		obj = new Field("planname");
		obj.setDatatype(DataType.STRING);
		obj.setLength(100);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("create_date");
		obj.setDatatype(DataType.DATE);
		obj.setLength(10);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("plan_time");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("bodykind");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("plancycle");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("B0110");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("E0122");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("E01A1");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("object_id");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("A0101");
		obj.setDatatype(DataType.STRING);
		obj.setLength(100);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("original_score");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("score");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);
		
		
		LoadXml loadxml = new LoadXml(this.cn, planid);
		Hashtable params = loadxml.getDegreeWhole();
		String deviationScoreUsed=(String) params.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是
		if("1".equals(deviationScoreUsed)){
			obj = new Field("reviseScore");
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(6);
			obj.setKeyable(false);
			table.addField(obj);
		}
		//在归档临时表中创建whole_score字段 2013.11.14 pjf
		String WholeEvalMode = (String) params.get("WholeEvalMode");
		if("1".equals(WholeEvalMode)){
			obj = new Field("whole_score");
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(5);
			obj.setKeyable(false);
			table.addField(obj);
		}
		
		// 业务分类字段 =0(绩效考核); =1(能力素质)
		if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
		{
			obj = new Field("postRuleScore"); // 岗位标准分值
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(6);
			obj.setKeyable(false);
			table.addField(obj);
			
			obj = new Field("mateSurmise"); // 匹配度
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(6);
			obj.setKeyable(false);
			table.addField(obj);
		}
		
		obj = new Field("grade_id");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("resultdesc");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("self_idea");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("reason");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("org_idea");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("council_idea");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("appraise");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("summarize");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("Interview");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("Affix");
		obj.setDatatype(DataType.BLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("Ext");
		obj.setDatatype(DataType.STRING);
		obj.setLength(10);
		table.addField(obj);

		obj = new Field("Ordering");//组内排名
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
		
		obj = new Field("ex_GrpNum");//组内人数   zhaoxg  add  2014-4-16
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("org_ordering");//部门排名 chent add 20171011
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
		
		obj = new Field("org_GrpNum");//部门人数   chent add 20171011
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("A0000");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("Body_id");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
		// 分组平均分
		obj = new Field("exS_GrpAvg");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);
		// 个人系数
		obj = new Field("exX_object");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);
		// 绩效目标
		obj = new Field("article");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		// 评分
		HashMap map = this.getTopItems();
		HashMap map1 = this.getKhMainBodyType();
		RecordVo tempvo = this.getPerPlanVo(this.planid);
		String byModel = tempvo.getString("bymodel")==null?"":tempvo.getString("bymodel");
		if(!"1".equals(byModel)){//如果不是按岗位素质模型测评
			if (dbWizard.isExistTable(tableName+"2", false))
			{
				dbWizard.dropTable(tableName+"2");
			}
			if(dbWizard.isExistTable(tableName+"3", false)){
				dbWizard.dropTable(tableName+"3");
			}
			// 创建表
			//这里进行分表（防止项目太多，导致创建临时表时，列超过1024个） 绩效归档临时表附加表（主体对目标项评分记录临时表）
			Table table2 = new Table(tableName+"2");
			Table table3 = new Table(tableName+"3");
			
			obj = new Field("id");
			obj.setDatatype(DataType.INT);
			obj.setNullable(false);
			obj.setKeyable(true);
			table2.addField(obj);
			table3.addField(obj);
			obj = new Field("object_id");
			obj.setDatatype(DataType.STRING);
			obj.setLength(50);
			obj.setKeyable(false);
			table2.addField(obj);
			table3.addField(obj);
			
			//如果表2的列仍然超过1024,则继续分出第三个临时表
			int midIdx = this.topItems.size();
			if(this.topItems.size()*this.bodyType.size()>1024) {
                midIdx = this.topItems.size()/2;
            }
			
			//分表操作  haosl  2017-9-29  start ==================
			for (int i = 0; i < this.topItems.size(); i++)
			{
				String itemid = (String) this.topItems.get(i);
				for (int j = 0; j < this.bodyType.size(); j++)
				{
					String bodyTypeId = (String) this.bodyType.get(j);
					String field = "B" + bodyTypeId + "_I" + itemid;
					obj = new Field(field);
					obj.setDatatype(DataType.FLOAT);
					obj.setLength(12);
					obj.setDecimalDigits(6);
					obj.setKeyable(false);
					if(i<midIdx) {
                        table2.addField(obj);
                    } else {
                        table3.addField(obj);
                    }
				}
			}
			if(table2.getCount()>2){
				dbWizard.createTable(table2);
			}
			if(table3.getCount()>2){
				dbWizard.createTable(table3);
			}
			//分表操作  haosl  2017-9-29  end ======================
			// 项目得分
			for (int i = 0; i < this.topItems.size(); i++)
			{
				String itemid = (String) this.topItems.get(i);
				String field = "I" + itemid;
				obj = new Field(field);
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);
			}
		}
		

		// 合计分
		for (int j = 0; j < this.bodyType.size(); j++)
		{
			String bodyTypeId = (String) this.bodyType.get(j);
			String field = "B" + bodyTypeId;
			obj = new Field(field);
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(6);
			obj.setKeyable(false);
			table.addField(obj);
		}

		// 参评人数
		for (int j = 0; j < this.bodyType.size(); j++)
		{
			String bodyTypeId = (String) this.bodyType.get(j);
			String field = "BcpCou" + bodyTypeId;
			obj = new Field(field);
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(6);
			obj.setKeyable(false);
			table.addField(obj);
		}

		// 计划人数
		for (int j = 0; j < this.bodyType.size(); j++)
		{
			String bodyTypeId = (String) this.bodyType.get(j);
			String field = "BjhCou" + bodyTypeId;
			obj = new Field(field);
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(6);
			obj.setKeyable(false);
			table.addField(obj);
		}
		
		// 关联计划 创建引用的计划字段 lium
		List ids = loadxml.getRelatePlanValue("Plan","ID");
		for (int i = 0, len = ids.size(); i < len; i++) {
			String id = (String) ids.get(i);
			
			String field = "G_" + id;
			obj = new Field(field);
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(12);
			obj.setDecimalDigits(6);
			obj.setKeyable(false);
			table.addField(obj);
		}
		
		dbWizard.createTable(table);

		this.insertData();

	}

	// 往临时表中插入数据
	public void insertData()
	{

		String planname = "";
		java.sql.Date create_date = null;
		String plan_time = this.getKhTime();
		String plancycle = "";
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{

			RowSet rs = dao.search("select name,create_date,cycle from per_plan where plan_id=" + this.planid);
			bodyType = new ArrayList();
			if (rs.next())
			{
				planname = isNull(rs.getString("name"));
				create_date = rs.getDate("create_date");
				int cycle = rs.getInt("cycle");
				switch (cycle)
				{
				case 0:
					plancycle = "年度";
					break;
				case 1:
					plancycle = "半年度";
					break;
				case 2:
					plancycle = "季度";
					break;
				case 3:
					plancycle = "月度";
					break;
				case 7:
					plancycle = "时间段";
					break;
				}
			}
			/************************/
			LoadXml loadxml = new LoadXml(this.cn, planid);
			Hashtable params = loadxml.getDegreeWhole();
			String deviationScoreUsed=(String) params.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是
			String WholeEvalMode = (String) params.get("WholeEvalMode");
			String wholescore="";
			String wholescore_Result="";
			if("1".equals(WholeEvalMode)){
				 wholescore=",whole_score";
				 wholescore_Result=",a.whole_score";
			}
			String reviseScore="";
			String reviseScore_result="";
			if("1".equals(deviationScoreUsed)){
				reviseScore=",reviseScore";
				reviseScore_result=",a.reviseScore";
			}
			/** 先插入可以直接取到的字段 */
			StringBuffer strSql = new StringBuffer();
			strSql.append("select "+this.planid+", b.id,b.b0110,b.e0122,b.e01a1,b.object_id,b.a0101,a.score"+reviseScore_result+",a.original_score,a.a0000,a.appraise,a.body_id,a.ordering,a.ex_GrpNum,a.org_ordering,a.org_GrpNum,a.grade_id,");
			strSql.append("a.resultdesc,a.self_idea,a.reason,a.org_idea,a.council_idea,a.summarize,'");   
			strSql.append(planname + "','" + plan_time + "','" + plancycle + "',");
			if(this.busiType!=null && this.busiType.trim().length()>0 && "1".equals(this.busiType)) {
                strSql.append("a.ext,a.affix,a.exS_GrpAvg,a.exX_object,b.body_id,a.mateSurmise"+wholescore_Result+" from per_result_"); //归档到子集时，任然需要写上要归档的字段  2013.11.27 pjf
            } else {
                strSql.append("a.ext,a.affix,a.exS_GrpAvg,a.exX_object,b.body_id"+wholescore_Result+" from per_result_");
            }
			strSql.append(this.planid);
			strSql.append(" a,per_object b where a.object_id in ");
			if ("1".equals(this.filedType)) {
                strSql.append(" (select a0100 from usra01)");
            } else if ("2".equals(this.filedType) || "4".equals(this.filedType)) {
                strSql.append(" (select b0110 from b01)");
            }
			strSql.append(" and b.plan_id=" + this.planid);
			strSql.append(" and a.object_id = b.object_id");

			StringBuffer insertsql = new StringBuffer();
			insertsql.append("insert into ");
			insertsql.append(this.tableName);
			insertsql.append("(plan_id,id,b0110,e0122,e01a1,object_id,a0101,score"+reviseScore+",original_score,a0000,appraise,body_id,ordering,ex_GrpNum,org_ordering,org_GrpNum,grade_id,");
			if(this.busiType!=null && this.busiType.trim().length()>0 && "1".equals(this.busiType)) {
                insertsql.append("resultdesc,self_idea,reason,org_idea,council_idea,summarize,planname,plan_time,plancycle,ext,affix,exS_GrpAvg,exX_object,bodykind,mateSurmise"+wholescore+")");
            } else {
                insertsql.append("resultdesc,self_idea,reason,org_idea,council_idea,summarize,planname,plan_time,plancycle,ext,affix,exS_GrpAvg,exX_object,bodykind"+wholescore+")");
            }
			insertsql.append(strSql.toString());

			dao.insert(insertsql.toString(), new ArrayList());

			PerEvaluationBo pe = new PerEvaluationBo(this.cn, planid, "");
			Hashtable planParamSet = pe.getPlanParamSet();
			String KeepDecimal = (String) planParamSet.get("KeepDecimal"); // 计算结果保留小数位

			// bodykind字段写名称
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update " + tableName + " set " + tableName + ".bodykind=");
			sqlstr.append("per_mainbodyset.name from per_mainbodyset ");
			sqlstr.append(" where " + tableName + ".bodykind=per_mainbodyset.body_id ");

			if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
			{
				sqlstr.setLength(0);
				sqlstr.append("update " + tableName + " set " + tableName + ".bodykind=");
				sqlstr.append("(select name from per_mainbodyset where Perform_Arch_Temp_Tab.bodykind=per_mainbodyset.body_id)");
			}
			dao.update(sqlstr.toString());

			// 更新create_date字段
			String sql = "update " + tableName + " set create_date=(select create_date from per_plan where plan_id=" + this.planid + ")";
			dao.update(sql);

			/** 再更新需要加工的字段 */
			HashMap teamLeader = new HashMap();
			sql = "select object_id,mainbody_id from per_mainbody where plan_id=" + planid + " and body_id=-1";
			rs = dao.search(sql);
			while (rs.next()) {
                teamLeader.put(rs.getString(1), rs.getString(2));
            }

			String interviewSql = "";
			ArrayList interviewList = new ArrayList();
			// 文字方式的面谈才归档
			if ("1".equals(getInterViewType()))
			{
				PerformanceInterviewBo bo = new PerformanceInterviewBo(this.cn);
				sql = "select * from " + tableName;
				rs = dao.search(sql);
				while (rs.next())
				{
					String object_id = rs.getString("object_id");
					String interview = bo.getInterviewContent(this.planid, object_id);
					if (interview.trim().length() > 0)
					{
						ArrayList list1 = new ArrayList();
						list1.add(interview);
						// 对团队负责人归档要将objectid改为团队负责人的mainbodyid.
						if ("4".equals(this.filedType)) {
                            list1.add((String) teamLeader.get(object_id));
                        } else {
                            list1.add(object_id);
                        }
						interviewList.add(list1);
					}
				}
				interviewSql = "update " + tableName + " set Interview=? where object_id=?";
			}
			
			// 关联计划分数 插入数据 lium
			List ids = loadxml.getRelatePlanValue("Plan","ID");
			int round = Integer.parseInt(KeepDecimal);
			if (ids != null && ids.size() > 0) {
				String resultTbl = "per_result_" + planid;
				StringBuffer refPlanSql = new StringBuffer();
				refPlanSql.append("UPDATE ").append(tableName).append(" SET ");
				for (int j = 0, len = ids.size(); j < len; j++) {
					String id = (String) ids.get(j);
					String field = "G_" + id;
					if (j != 0) {
						refPlanSql.append(",");
					}
					refPlanSql.append(field).append("=(SELECT ").append(Sql_switcher.round(field, round));
					refPlanSql.append(" FROM ").append(resultTbl).append(" WHERE object_id=");
					refPlanSql.append(tableName).append(".object_id)");
				}
				refPlanSql.append(" WHERE EXISTS (SELECT 1 FROM ").append(resultTbl);
				refPlanSql.append(" WHERE object_id=").append(tableName).append(".object_id)");
				dao.update(refPlanSql.toString());
			}

			// 对团队负责人归档要将objectid改为团队负责人的mainbodyid.
			if ("4".equals(this.filedType))
			{
				sqlstr = new StringBuffer();
				sqlstr.append("update " + tableName + " set " + tableName + ".object_id=");
				sqlstr.append("per_mainbody.mainbody_id from per_mainbody ");
				sqlstr.append(" where " + tableName + ".object_id=per_mainbody.object_id and per_mainbody.body_id=-1 and per_mainbody.plan_id=" + this.planid);

				if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
				{
					sqlstr.setLength(0);
					sqlstr.append("update " + tableName + " set " + tableName + ".object_id=");
					sqlstr.append("(select mainbody_id from per_mainbody where Perform_Arch_Temp_Tab.object_id=per_mainbody.object_id and per_mainbody.body_id=-1 ");
					sqlstr.append(" and per_mainbody.plan_id=" + this.planid + ")");
				}
				dao.update(sqlstr.toString());
			}

			// 文字方式的面谈才归档
			if ("1".equals(getInterViewType())) {
                dao.batchUpdate(interviewSql, interviewList);
            }

			StringBuffer updatesql = new StringBuffer();
			updatesql.append("update ");
			updatesql.append(this.tableName);
			updatesql.append(" set ");
			StringBuffer insertsql2 = new StringBuffer();
			insertsql2.append("insert into ");
			insertsql2.append(this.tableName+"2");
			insertsql2.append(" (id,object_id,");
			StringBuffer insertsql2_zwf = new StringBuffer();
			insertsql2_zwf.append("?,?,");
			StringBuffer insertsql3 = new StringBuffer();
			insertsql3.append("insert into ");
			insertsql3.append(this.tableName+"3");
			insertsql3.append(" (id,object_id,");
			StringBuffer insertsql3_zwf = new StringBuffer();
			insertsql3_zwf.append("?,?,");
			StringBuffer querySql = new StringBuffer();
			querySql.append("select id,object_id,score,original_score,exS_GrpAvg,exX_object from ");
			querySql.append(this.tableName);

			rs = dao.search(querySql.toString());
			int i = 0;
			ArrayList updatelist = new ArrayList();
			ArrayList insertlist2 = new ArrayList();
			ArrayList insertlist3 = new ArrayList();
			HashMap itemScore = new HashMap();// 项目得分
			HashMap body_item_PinFen = new HashMap();// 评分
			RecordVo tempvo = this.getPerPlanVo(this.planid);
			String byModel = tempvo.getString("bymodel")==null?"":tempvo.getString("bymodel");
			if(!"1".equals(byModel)){
				itemScore = this.getItemScore();// 项目得分
				body_item_PinFen = this.getBIScore();// 评分
			}
			HashMap item_sumFen = this.getSumScore();// 合计分
			HashMap empCountCP = this.getEmpCOuntCP();// 算参评人数
			HashMap empCountJH = this.getEmpCountJH();// 算计划人数
			while (rs.next())
			{
				ArrayList list = new ArrayList();
				ArrayList list2 = new ArrayList();
				ArrayList list3 = new ArrayList();
				
				i++;
				String object_id = rs.getString("object_id");
				int id = rs.getInt("id");
				list2.add(id);
				list2.add(object_id);
				list3.add(id);
				list3.add(object_id);
				this.getTopItems();
				this.getKhMainBodyType();

				if(!"1".equals(byModel)){
					// 评分
					int midIdx = this.topItems.size();
					if(this.topItems.size()*this.bodyType.size()>1024) {
                        midIdx = this.topItems.size()/2;
                    }
					for (int k = 0; k < this.topItems.size(); k++)
					{
						String itemid = (String) this.topItems.get(k);
						for (int j = 0; j < this.bodyType.size(); j++)
						{
							String bodyTypeId = (String) this.bodyType.get(j);
							String field = "B" + bodyTypeId + "_I" + itemid;
							if (i == 1) {
								if(k<midIdx){
									insertsql2.append(field+",");
									insertsql2_zwf.append("?,");
								}else{
									insertsql3.append(field+",");
									insertsql3_zwf.append("?,");
								}
							}
							Float value = body_item_PinFen.get("B" + bodyTypeId + "_I" + itemid + ":" + object_id) == null ? new Float(0) : (Float) body_item_PinFen.get("B" + bodyTypeId + "_I" + itemid
									+ ":" + object_id);
							if(k<midIdx) {
                                list2.add(PubFunc.round(value.toString(), Integer.parseInt(KeepDecimal)));
                            } else {
                                list3.add(PubFunc.round(value.toString(), Integer.parseInt(KeepDecimal)));
                            }
						}
					}
					if(list2.size()>2) {
                        insertlist2.add(list2);
                    }
					if(list3.size()>2) {
                        insertlist3.add(list3);
                    }
					// 项目得分
					for (int k = 0; k < this.topItems.size(); k++)
					{
						String itemid = (String) this.topItems.get(k);
						String field = "I" + itemid;
						if (i == 1) {
                            updatesql.append(field + "=?,");
                        }
						String temp = object_id + ":" + "T_" + itemid;

						Float value = itemScore.get(temp) == null ? new Float(0) : (Float) itemScore.get(temp);
						list.add(PubFunc.round(value.toString(), Integer.parseInt(KeepDecimal)));
					}
				}
				

				// 合计分
				for (int j = 0; j < this.bodyType.size(); j++)
				{
					String bodyTypeId = (String) this.bodyType.get(j);
					String field = "B" + bodyTypeId;
					if (i == 1) {
                        updatesql.append(field + "=?,");
                    }
					Float value = item_sumFen.get(field + ":" + object_id) == null ? new Float(0) : (Float) item_sumFen.get(field + ":" + object_id);
					list.add(PubFunc.round(value.toString(), Integer.parseInt(KeepDecimal)));
				}

				// 参评人数
				for (int j = 0; j < this.bodyType.size(); j++)
				{
					String bodyTypeId = (String) this.bodyType.get(j);
					String field = "BcpCou" + bodyTypeId;
					if (i == 1) {
                        updatesql.append(field + "=?,");
                    }
					Integer value = empCountCP.get(field + ":" + object_id) == null ? new Integer(0) : (Integer) empCountCP.get(field + ":" + object_id);
					list.add(PubFunc.round(value.toString(), Integer.parseInt(KeepDecimal)));
				}

				// 计划人数
				for (int j = 0; j < this.bodyType.size(); j++)
				{
					String bodyTypeId = (String) this.bodyType.get(j);
					String field = "BjhCou" + bodyTypeId;
					if (i == 1) {
                        updatesql.append(field + "=?,");
                    }
					Integer value = empCountJH.get(field + ":" + object_id) == null ? new Integer(0) : (Integer) empCountJH.get(field + ":" + object_id);
					list.add(PubFunc.round(value.toString(), Integer.parseInt(KeepDecimal)));
				}

				if (i == 1)
				{
					// 计算得分 总分 组平均分 考核系数 按照计算规则中的小数位数重新更新一下吧
					updatesql.append("score=?,original_score=?,exS_GrpAvg=?,exX_object=?");
					updatesql.append(" where id=? and object_id=?");
				}

				String value = rs.getString("score") == null ? "0" : rs.getString("score");
				list.add(PubFunc.round(value, Integer.parseInt(KeepDecimal)));

				value = rs.getString("original_score") == null ? "0" : rs.getString("original_score");
				list.add(PubFunc.round(value, Integer.parseInt(KeepDecimal)));

				value = rs.getString("exS_GrpAvg") == null ? "0" : rs.getString("exS_GrpAvg");
				list.add(PubFunc.round(value, Integer.parseInt(KeepDecimal)));

				value = rs.getString("exX_object") == null ? "0" : rs.getString("exX_object");
				list.add(PubFunc.round(value, Integer.parseInt(KeepDecimal)));

				list.add(new Integer(id));
				list.add(object_id);
				updatelist.add(list);
			}

			dao.batchUpdate(updatesql.toString(), updatelist);
			if(!"1".equals(byModel) && insertsql2_zwf.length()>4) {
				insertsql2_zwf.setLength(insertsql2_zwf.length()-1);
				insertsql2.setLength(insertsql2.length()-1);
				insertsql2.append(") values ("+insertsql2_zwf+")");
				dao.batchInsert(insertsql2.toString(), insertlist2);
			}
			if(!"1".equals(byModel) && insertsql3_zwf.length()>4) {
				insertsql3_zwf.setLength(insertsql3_zwf.length()-1);
				insertsql3.setLength(insertsql3.length()-1);
				insertsql3.append(") values ("+insertsql3_zwf+")");
				dao.batchInsert(insertsql3.toString(), insertlist3);
			}
			
			StringBuffer buf = new StringBuffer();
			if (this.objType == 2)
			{
				// 更新总结（绩效报告字段）
				buf.setLength(0);
				buf.append("UPDATE " + this.tableName + " SET summarize=content FROM per_article  WHERE per_article.plan_id=");
				buf.append(this.planid);
				buf.append(" AND article_type=2 and fileflag=1 and state=1 and a0100= Perform_Arch_Temp_Tab.object_id");

				if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
				{
					buf.setLength(0);
					buf.append("UPDATE " + this.tableName);
					buf.append(" SET summarize=(select content FROM per_article WHERE per_article.plan_id=");
					buf.append(this.planid);
					buf.append("  AND article_type=2 and fileflag=1 and state=1 AND per_article.a0100= Perform_Arch_Temp_Tab.object_id)");
				}
				dao.update(buf.toString(), new ArrayList());
				
				// 更新article字段 /绩效目标
				buf.setLength(0);
				buf.append("UPDATE " + this.tableName + " SET article=content FROM per_article  WHERE per_article.plan_id=");
				buf.append(this.planid);
				buf.append(" AND article_type=1  AND fileflag=1 ");
				buf.append(" AND per_article.A0100=Perform_Arch_Temp_Tab.object_id");

				if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
				{
					buf.setLength(0);
					buf.append("UPDATE " + this.tableName);
					buf.append(" SET article=(select content FROM per_article WHERE per_article.plan_id=");
					buf.append(this.planid);
					buf.append(" AND per_article.article_type=1  AND per_article.fileflag=1  AND per_article.A0100= Perform_Arch_Temp_Tab.object_id)");
				}
				dao.update(buf.toString(), new ArrayList());

			} else
			{
				// 团队绩效目标和报告都存在per_article的团队负责人记录里
				// 对于团队归档Perform_Arch_Temp_Tab表object_id此时存团队id,团队负责人归档object_id此时存团队负责人id
				buf.setLength(0);
				buf.append("UPDATE " + this.tableName + " SET summarize=content FROM per_article  WHERE per_article.plan_id=");
				buf.append(this.planid);
				buf.append(" AND article_type=2 and fileflag=1 and state=1 and a0100=? and Perform_Arch_Temp_Tab.object_id=?");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
				{
					buf.setLength(0);
					buf.append("UPDATE " + this.tableName);
					buf.append(" SET summarize=(select content FROM per_article WHERE per_article.plan_id=");
					buf.append(this.planid);
					buf.append("  AND article_type=2 and fileflag=1 and state=1 and a0100=? ) where object_id=?");
				}

				ArrayList list = new ArrayList();
				Set keyset = teamLeader.keySet();
				for (Iterator iter = keyset.iterator(); iter.hasNext();)
				{
					String object_id = (String) iter.next();
					String obj_teamLeader = (String) teamLeader.get(object_id);

					if (obj_teamLeader != null)
					{
						if ("4".equals(this.filedType))// 团队负责人归档
						{
							ArrayList list1 = new ArrayList();
							list1.add(obj_teamLeader);
							list1.add(obj_teamLeader);
							list.add(list1);

						} else if ("2".equals(this.filedType))// 团队归档
						{
							ArrayList list1 = new ArrayList();
							list1.add(obj_teamLeader);
							list1.add(object_id);
							list.add(list1);
						}
					}
				}
				dao.batchUpdate(buf.toString(), list);

				// 更新article字段 /绩效目标
				buf.setLength(0);
				buf.append("UPDATE " + this.tableName + " SET article=content FROM per_article  WHERE per_article.plan_id=");
				buf.append(this.planid);
				buf.append(" AND article_type=1");
				buf.append(" AND A0100=? and Perform_Arch_Temp_Tab.object_id=?");

				if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
				{
					buf.setLength(0);
					buf.append("UPDATE " + this.tableName);
					buf.append(" SET article=(select content FROM per_article WHERE per_article.plan_id=");
					buf.append(this.planid);
					buf.append(" AND per_article.article_type=1 and a0100=? ) where object_id=?");
				}
				dao.batchUpdate(buf.toString(), list);

			}
			
			String resultTbl = "per_result_" + planid; // lium
			// 总结 为了兼容cs的旧的计划 如果在per_article表没有找到值就从结果表再取一遍
			buf.setLength(0);
			buf.append("UPDATE " + this.tableName + " SET Perform_Arch_Temp_Tab.summarize=" + resultTbl + ".summarize FROM " + resultTbl + "  WHERE ");
			buf.append(this.tableName + ".summarize is null and Perform_Arch_Temp_Tab.object_id=" + resultTbl + ".object_id");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
			{
				buf.setLength(0);
				buf.append("UPDATE " + this.tableName);
				buf.append(" SET summarize=(select summarize FROM " + resultTbl + " WHERE object_id=" + this.tableName + ".object_id)");
				buf.append("where " + this.tableName + ".summarize is null");
			}

			dao.update(buf.toString(), new ArrayList());

			if (rs != null) {
                rs.close();
            }

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String isNull(String str)
	{

		if (str == null) {
            return "";
        } else {
            return str;
        }

	}

	/*
	 * 获得考核时间指标
	 */
	public String getKhTime()
	{

		String khTime = "";
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			RowSet rs = dao.search("select cycle,theyear,themonth,thequarter, start_date,end_date from per_plan where plan_id=" + this.planid);
			if (rs.next())
			{
				int cycle = rs.getInt("cycle");
				String theyear = isNull(rs.getString("theyear"));
				String themonth = isNull(rs.getString("themonth"));
				String thequarter = isNull(rs.getString("thequarter"));
				String start_date = rs.getDate("start_date") != null ? format.format(rs.getDate("start_date")) : "";
				String end_date = rs.getDate("start_date") != null ? format.format(rs.getDate("end_date")) : "";
				if (end_date.length() > 9) {
                    end_date = end_date.substring(0, 10);
                }
				if (start_date.length() > 9) {
                    start_date = start_date.substring(0, 10);
                }

				switch (cycle)
				{
				case 0:
					khTime = theyear + "年";
					break;
				case 1:
					if ("1".equals(thequarter)) {
                        khTime = theyear + "年上半年";
                    } else if ("2".equals(thequarter)) {
                        khTime = theyear + "年下半年";
                    }
					break;
				case 2:
					if ("01".equals(thequarter)) {
                        khTime = theyear + "年第一季度";
                    } else if ("02".equals(thequarter)) {
                        khTime = theyear + "年第二季度";
                    } else if ("03".equals(thequarter)) {
                        khTime = theyear + "年第三季度";
                    } else if ("04".equals(thequarter)) {
                        khTime = theyear + "年第四季度";
                    }
					break;
				case 3:
					if ("01".equals(themonth)) {
                        khTime = theyear + "年一月";
                    } else if ("02".equals(themonth)) {
                        khTime = theyear + "年二月";
                    } else if ("03".equals(themonth)) {
                        khTime = theyear + "年三月";
                    } else if ("04".equals(themonth)) {
                        khTime = theyear + "年四月";
                    } else if ("05".equals(themonth)) {
                        khTime = theyear + "年五月";
                    } else if ("06".equals(themonth)) {
                        khTime = theyear + "年六月";
                    } else if ("07".equals(themonth)) {
                        khTime = theyear + "年七月";
                    } else if ("08".equals(themonth)) {
                        khTime = theyear + "年八月";
                    } else if ("09".equals(themonth)) {
                        khTime = theyear + "年九月";
                    } else if ("10".equals(themonth)) {
                        khTime = theyear + "年十月";
                    } else if ("11".equals(themonth)) {
                        khTime = theyear + "年十一月";
                    } else if ("12".equals(themonth)) {
                        khTime = theyear + "年十二月";
                    }
					break;
				case 7:
					khTime = start_date + "－" + end_date;
					break;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return khTime;
	}

	/*
	 * 算评分
	 */
	public HashMap getBIScore()
	{

		HashMap map = new HashMap();
		StringBuffer strSql = new StringBuffer();
		// 取消乘以主体权重,与cs保持一致 lium
		strSql.append("select object_id,body_id,item_id,sum(score*point_rank) pingfen from Per_ScoreDetail where plan_id=");
		strSql.append(this.planid);
		strSql.append(" group by object_id,body_id,item_id");
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(strSql.toString());
			while (rs.next()) {
                map.put("B" + rs.getInt("body_id") + "_I" + rs.getInt("item_id") + ":" + rs.getString("object_id"), new Float(rs.getFloat("pingfen")));
            }
			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 算计划人数
	 */
	public HashMap getEmpCountJH()
	{

		HashMap map = new HashMap();
		StringBuffer strSql = new StringBuffer();
		strSql.append("select object_id,body_id,count(mainbody_id) BjhCoux from per_mainbody where  plan_id=");
		strSql.append(this.planid);
		strSql.append(" group by object_id,body_id");
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(strSql.toString());
			while (rs.next()) {
                map.put("BjhCou" + rs.getInt("body_id") + ":" + rs.getString("object_id"), new Integer(rs.getInt("BjhCoux")));
            }

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 算参评人数
	 */
	public HashMap getEmpCOuntCP()
	{

		HashMap map = new HashMap();
		StringBuffer strSql = new StringBuffer();
		strSql.append("select object_id,body_id,count(mainbody_id) BcpCoux from per_mainbody where status in (1,2) and plan_id=");
		strSql.append(this.planid);
		strSql.append("  group by object_id,body_id");
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(strSql.toString());
			while (rs.next()) {
                map.put("BcpCou" + rs.getInt("body_id") + ":" + rs.getString("object_id"), new Integer(rs.getInt("BcpCoux")));
            }

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 取得合计分
	 */
	public HashMap getSumScore()
	{

		HashMap map = new HashMap();
		StringBuffer strSql = new StringBuffer();
		strSql.append("select object_id,body_id,sum(score*body_rank*point_rank) bx from Per_ScoreDetail where plan_id=");
		strSql.append(this.planid);
		strSql.append(" and item_id!=-1 group by object_id,body_id");
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(strSql.toString());
			while (rs.next()) {
                map.put("B" + rs.getInt("body_id") + ":" + rs.getString("object_id"), new Float(rs.getFloat("bx")));
            }

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 取得项目均分
	 */
	public HashMap getItemAveScore(String object_id)
	{

		HashMap map = new HashMap();
		StringBuffer strSql = new StringBuffer();
		if (this.bodyType.size() == 0) {
            getKhMainBodyType();
        }
		strSql.append("select item_id,sum(score)/" + this.bodyType.size() + " Ix from Per_ScoreDetail where plan_id=");
		strSql.append(this.planid);
		strSql.append("and object_id ='");
		strSql.append(object_id);
		strSql.append("'  group by item_id");
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(strSql.toString());
			while (rs.next()) {
                map.put("I" + rs.getInt("item_id"), new Float(rs.getFloat("Ix")));
            }

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 项目得分取自结果表的项目分 */
	public HashMap getItemScore()
	{

		HashMap map = new HashMap();
		StringBuffer strSql = new StringBuffer();
		strSql.append("select ");
		for (int k = 0; k < this.topItems.size(); k++)
		{
			String itemid = (String) this.topItems.get(k);
			String field = "T_" + itemid;
			strSql.append(field + ",");
		}
		strSql.append("object_id from per_result_" + this.planid);

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(strSql.toString());
			while (rs.next())
			{
				for (int k = 0; k < this.topItems.size(); k++)
				{
					String itemid = (String) this.topItems.get(k);
					String field = "T_" + itemid;
					map.put(rs.getString("object_id") + ":" + field, new Float(rs.getFloat(field)));
				}

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取方案id
	 * 
	 * @return
	 */
	public int getXMLId()
	{

		int id = 0;
		String sqlStr = "select id from per_archive_schema where status=" + this.filedType;
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sqlStr);
			if (rs.next())
			{
				id = rs.getInt("id");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}

	/*
	 * 获得归档方案内容
	 */
	public String getXML()
	{

		String content = "";
		String sqlStr = "select bytes from per_archive_schema where status=" + this.filedType;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sqlStr);
			if (rs.next())
			{
				in = rs.getBinaryStream("bytes");
				int len;
				byte[] buff = new byte[1024];
				out = new ByteArrayOutputStream(2048);

				while ((len = in.read(buff)) != -1)
				{
					out.write(buff, 0, len);
				}
				in.close();
				content = out.toString();
				out.close();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(out);
		}
		return content;
	}

	/*
	 * 取得考核子集在方案中的当前值
	 */
	public String getSetName()
	{

		String str = "";
		String xmlContent = this.getXML();
		if ("".equals(xmlContent)) {
            return str;
        }
		try
		{
			Document doc = PubFunc.generateDom(xmlContent);
			String xpath = "//RelaSet[@SrcFldSet=\"" + this.tableName + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			if (ele != null)
			{
				str = ele.getAttributeValue("DestFldSet");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 取得考核归档方案中的对应关系
	 */
	public String getDestFldId(String srcFldId, String destFldSet)
	{

		String str = "";
		String xmlContent = this.getXML();
		if ("".equals(xmlContent)) {
            return str;
        }
		try
		{
			Document doc = PubFunc.generateDom(xmlContent);
			String xpath = "//RelaSet[@DestFldSet=\"" + destFldSet + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			if (ele == null) {
                return str;
            }

			xpath = "//RelaFld[@SrcFldId=\"" + srcFldId + "\"]";
			xpath_ = XPath.newInstance(xpath);
			ele = (Element) xpath_.selectSingleNode(doc);
			if (ele != null)
			{
				str = ele.getAttributeValue("DestFldId");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 取得考核归档方案中目标代码的类型
	 */
	public String getDestCodeSet(String srcFldId, String destFldSet)
	{

		String destCodeSet = "0";
		String xmlContent = this.getXML();
		if ("".equals(xmlContent)) {
            return destCodeSet;
        }
		try
		{
			Document doc = PubFunc.generateDom(xmlContent);
			String xpath = "//RelaSet[@DestFldSet=\"" + destFldSet + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			if (ele == null) {
                return destCodeSet;
            }

			xpath = "//RelaFld[@SrcFldId=\"" + srcFldId + "\"]";
			xpath_ = XPath.newInstance(xpath);
			ele = (Element) xpath_.selectSingleNode(doc);
			if (ele != null)
			{
				destCodeSet = ele.getAttributeValue("DestCodeSet");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return destCodeSet;
	}

	/**
	 * 得到归档子集指标
	 */
	public ArrayList getDestFldsByType(String setName, String type)
	{

		ArrayList list = new ArrayList();
		CommonData vo = new CommonData("", "");
		list.add(vo);

		if ("".equals(setName)) {
            setName = this.getSetName();
        }
		if ("".equals(setName)) {
            return list;
        }

		StringBuffer strSql = new StringBuffer();
		strSql.append("select itemid,itemdesc from fielditem where fieldsetid='");
		strSql.append(setName);
		strSql.append("' and useflag='1' and itemtype='");
		strSql.append(type);
		strSql.append("'");

		try
		{

			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search(strSql.toString());
			while (rowSet.next())
			{
				vo = new CommonData(rowSet.getString("itemid"), rowSet.getString("itemdesc"));
				list.add(vo);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * 获得trandb_code表Id值
	 */
	public int getID()
	{

		int id = 0;
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search("select id from trandb_Scheme where dbtype=" + this.performArchiveScheType);
			if (rowSet.next())
			{
				id = rowSet.getInt("id");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 取出未对应的源代码
	 */
	public ArrayList getNoAccordCodes(String sourceField, String destCode)
	{

		ArrayList list = new ArrayList();
		StringBuffer strSql = new StringBuffer();
		strSql.append("select distinct ");
		strSql.append(sourceField);
		strSql.append(" from Perform_Arch_Temp_Tab where ");
		strSql.append(sourceField);
		strSql.append(" not in (select codeName2 from trandb_code where codeset1='");
		strSql.append(destCode);
		strSql.append("')");

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search(strSql.toString());
			while (rowSet.next())
			{
				String temp = rowSet.getString(sourceField);
				CommonData vo = new CommonData(temp, temp);
				list.add(vo);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得目标代码
	 */
	public ArrayList getTargetCodes(String destCode)
	{

		ArrayList list = new ArrayList();

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search("select codeitemid,codeitemdesc from codeitem where codesetid='" + destCode + "'");
			while (rowSet.next())
			{
				CommonData vo = new CommonData(rowSet.getString("codeitemid"), rowSet.getString("codeitemid") + ":" + rowSet.getString("codeitemdesc"));
				list.add(vo);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得已经对应的代码
	 */
	public ArrayList getHaveAccordCodes(String destCode)
	{

		ArrayList list = new ArrayList();

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search("select code1,codename1,codename2 from trandb_code where codeset1='" + destCode + "'");
			while (rowSet.next())
			{
				String code1 = rowSet.getString("code1");
				String codename1 = rowSet.getString("codename1");
				String codename2 = rowSet.getString("codename2");
				String dataText = codename2 + "=>" + code1 + ":" + codename1;
				CommonData vo = new CommonData(dataText, dataText);
				list.add(vo);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 保存代码对应
	 */
	public void saveCodeAccord(String destCode, String accordCodes)
	{

		// 先删除原先该类别的所有代码对应
		String delStr = "delete from trandb_code where codeset1='" + destCode + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			dao.delete(delStr, new ArrayList());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if ("".equals(accordCodes)) {
            return;
        }

		// 保存代码对应
		String[] codes = accordCodes.split("<@>");
		String codeset2 = "";
		String code2 = "";
		int id = this.getID();
		String codeset1 = destCode;

		ArrayList list1 = new ArrayList();

		for (int i = 0; i < codes.length; i++)
		{
			ArrayList list2 = new ArrayList();
			list2.add(codeset2);
			list2.add(code2);
			list2.add(new Integer(id));
			list2.add(codeset1);

			if ("".equals(codes[i])) {
                continue;
            }

			String[] codeAccord = codes[i].split("=>");
			String codename2 = codeAccord[0];
			String[] targetCode = codeAccord[1].split(":");
			String code1 = targetCode[0];
			String codeName1 = targetCode[1];

			list2.add(codename2);
			list2.add(code1);
			list2.add(codeName1);

			list1.add(list2);
		}

		String insertSql = "insert into trandb_code(codeset2,code2,id,codeset1,codename2,code1,codeName1) values (?,?,?,?,?,?,?)";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			dao.batchInsert(insertSql, list1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 得到子集字段的名称
	 */
	public String getDestFldName(String setName, String destFldId)
	{

		String str = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search("select itemdesc from fielditem where fieldsetid='" + setName + "' and itemid='" + destFldId + "'");
			if (rowSet.next()) {
                str = rowSet.getString("itemdesc");
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 生成新的归档方案
	 * 
	 * @throws GeneralException
	 */
	public void genetateXML(ArrayList sourceCodes, ArrayList sourceNames, ArrayList destCodes, ArrayList destTypes, String setName) throws GeneralException
	{

		if (destCodes == null || destCodes.size() == 0) {
            return;
        }

		Element root = new Element("ArchScheme");

		Element relaSet = new Element("RelaSet");
		relaSet.setAttribute("SrcFldSet", this.tableName);
		relaSet.setAttribute("DestFldSet", setName);
		root.addContent(relaSet);

		for (int i = 0; i < destCodes.size(); i++)
		{
			String destFldId = (String) destCodes.get(i);
			if ("noValue".equals(destFldId)) {
                continue;
            }
			FieldItem item = DataDictionary.getFieldItem(destFldId);///指标若被构库掉了  per_archive_schema考核结果归档方案xml不保存
			if(item==null) {
                continue;
            } else{
				if("0".equals(item.getUseflag())) {
                    continue;
                }
			}
			String srcFldId = (String) sourceCodes.get(i);
			String srcFldName = (String) sourceNames.get(i);
			String srcCodeSet = "";
			String destFldName = this.getDestFldName(setName, destFldId);
			String destCodeSet = (String) destTypes.get(i);
			Element relaFld = new Element("RelaFld");
			relaFld.setAttribute("SrcFldId", srcFldId);
			relaFld.setAttribute("SrcFldName", srcFldName);
			relaFld.setAttribute("SrcCodeSet", srcCodeSet);
			relaFld.setAttribute("DestFldId", destFldId);
			relaFld.setAttribute("DestFldName", destFldName);
			relaFld.setAttribute("DestCodeSet", destCodeSet);
			relaSet.addContent(relaFld);
		}

		Document myDocument = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xmlContent = outputter.outputString(myDocument);

		RecordVo vo = new RecordVo("per_archive_schema");

		ContentDAO dao = new ContentDAO(this.cn);
		IDGenerator idg = new IDGenerator(2, this.cn);

		String id = new Integer(this.getXMLId()).toString();
		String oper = "";
		if ("0".equals(id))// 添加
        {
            oper = "add";
        } else {
            oper = "edit";
        }

		switch (Sql_switcher.searchDbServer())
		{
		case Constant.ORACEL:
			break;
		default:
			byte[] data = null;
			data = xmlContent.getBytes();
			vo.setObject("bytes", data);
			break;
		}

		try
		{
			if ("add".equals(oper))// 添加
            {
                id = idg.getId("per_archive_schema.id");
            }

		} catch (GeneralException e)
		{
			e.printStackTrace();
		}
		vo.setString("id", id);
		vo.setInt("status", Integer.parseInt(this.filedType));

		try
		{
			if ("add".equals(oper))// 添加
            {
                dao.addValueObject(vo);
            } else {
                dao.updateValueObject(vo);
            }

			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
				RecordVo updatevo = dao.findByPrimaryKey(vo);
				// updatevo.setString("bytes", xmlContent);
				Blob blob = getOracleBlob(xmlContent, id);
				updatevo.setObject("bytes", blob);
				dao.updateValueObject(updatevo);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	private Blob getOracleBlob(String xmlContent, String id)
	{

		InputStream isByte = new ByteArrayInputStream(xmlContent.getBytes());
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select bytes from per_archive_schema where id=");
		strSearch.append(id);
		strSearch.append(" FOR UPDATE");

		StringBuffer strInsert = new StringBuffer();
		strInsert.append("update  per_archive_schema set bytes=EMPTY_BLOB() where id=");
		strInsert.append(id);

		OracleBlobUtils blobutils = new OracleBlobUtils(this.cn);
		Blob blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(), isByte);
		return blob;
	}

	/**
	 * 获得归档方案中的代码对应
	 * 
	 * @throws GeneralException
	 */
	public HashMap getCodeAccord(String destFldSet) throws GeneralException
	{

		HashMap map = new HashMap();
		String xmlContent = this.getXML();
		try
		{
			Document doc = PubFunc.generateDom(xmlContent);
			String xpath = "//RelaSet[@DestFldSet=\"" + destFldSet + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			if (ele != null)
			{
				List list = (List) ele.getChildren("RelaFld");
				for (int i = 0; i < list.size(); i++)
				{
					Element temp = (Element) list.get(i);
					String srcFldId = temp.getAttributeValue("SrcFldId");
					String destFldId = temp.getAttributeValue("DestFldId");
					String destCodeSet = temp.getAttributeValue("DestCodeSet");
					LazyDynaBean abean = new LazyDynaBean();
					abean.set("srcFldId", srcFldId);
					abean.set("destCodeSet", destCodeSet);
					if (map.get(destFldId) != null)
					{
						throw GeneralExceptionHandler.Handle(new GeneralException("目标字段指定重复，重新指定！"));
					} else {
                        map.put(destFldId, abean);
                    }
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 获得面谈记录的类型* */
	public String getInterViewType()
	{
		AnalysePlanParameterBo abo = new AnalysePlanParameterBo(this.cn);
		Hashtable ht_table = abo.analyseParameterXml();
		String templet_id = "";
		String flag = "1"; // 1:传统绩效面谈 2：调用模版面谈(操作人：考核主体) 3：调用模版面谈(操作人：考核对象)
		if (ht_table != null)
		{
			if (ht_table.get("interview_template") != null) {
                templet_id = (String) ht_table.get("interview_template");
            }
		}
		if (templet_id.length() > 0 && !"-1".equalsIgnoreCase(templet_id))
		{
			flag = "0";// 模板方式
		} else {
            flag = "1";// 文字方式
        }
		return flag;
	}

	/**
	 * @param oper
	 *        1--试归档 2--归档 试归档不更新计划的状态 不将数据放到历史表中 只是往子集表里存放 保存历史记录到子集表 3--结束 更新计划的状态 将数据放到历史表
	 * @throws GeneralException
	 */
	public boolean save(ArrayList sourceCodes, ArrayList sourceNames, ArrayList destCodes, ArrayList destTypes, String subSet, String userName, String oper) throws GeneralException {

		boolean flag = true;
		String createUserName = userName;
		String modUserName = userName;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = sdf.format(new java.util.Date());

		StringBuffer inserSql1 = new StringBuffer();
		StringBuffer inserSql2 = new StringBuffer();
		HashMap map = this.getCodeAccord(subSet);
		DbWizard dbWizard = new DbWizard(this.cn);
		if (map.size() == 0) {
            return flag;
        }
		try {
			String insertSql = "";
			RowSet rs = null;
			RowSet rowSet = null;
			ContentDAO dao = new ContentDAO(this.cn);
			DataDictionary.refresh();
			// 试归档和归档 要归档到子集表
			if ("1".equals(oper) || "2".equals(oper)) {
				inserSql1.append("insert into ");
				if ("1".equals(this.filedType) || "4".equals(this.filedType))// 人员归档 团队负责人归档
                {
                    inserSql1.append("usr");
                }
				inserSql1.append(subSet);
				inserSql1.append("(createtime,modtime,createusername,modusername,");

				inserSql2.append(" values (");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    inserSql2.append("to_date('" + nowDate + "','yyyy-mm-dd hh24:mi:ss'),");
                } else {
                    inserSql2.append("'" + nowDate + "',");
                }
				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    inserSql2.append("to_date('" + nowDate + "','yyyy-mm-dd hh24:mi:ss'),");
                } else {
                    inserSql2.append("'" + nowDate + "',");
                }

				inserSql2.append("'" + createUserName + "','" + modUserName + "',");

				StringBuffer selStr = new StringBuffer();
				selStr.append("select ");
				Set keySet = map.keySet();
				ArrayList destFldIdList = new ArrayList();
				for (Iterator iter = keySet.iterator(); iter.hasNext(); ) {
					String destFldId = (String) iter.next();
					inserSql1.append(destFldId + ",");
					inserSql2.append("?,");
					LazyDynaBean abean = (LazyDynaBean) map.get(destFldId);
					String srcFldId = (String) abean.get("srcFldId");
					selStr.append(srcFldId + ",");
					destFldIdList.add(destFldId);
				}

				selStr.append("t1.object_id from " + this.tableName + " t1");
				if (dbWizard.isExistTable(tableName + "2", false)) {
                    selStr.append(" left join " + this.tableName + "2 t2 on t1.id=t2.id and t1.object_id=t2.object_id");
                }
				if (dbWizard.isExistTable(tableName + "3", false)) {
                    selStr.append(" left join " + this.tableName + "3 t3 on t1.id=t3.id and t1.object_id=t3.object_id");
                }
				if ("1".equals(this.filedType) || "4".equals(this.filedType)) {// 人员归档
					// 考核对象为人员时，只归档人员创建时间晚于计划创建时间的记录 chent 20171030 add start 
					selStr.append(" left join UsrA01 A01 on A0100=t1.object_id ");
					selStr.append(" where t1.create_date>=A01.CreateTime ");
					// 考核对象为人员时，只归档人员创建时间晚于计划创建时间的记录 chent 20171030 add end 

					// 团队负责人归档
					inserSql1.append("a0100,");
				} else if ("2".equals(this.filedType)) // 团队 单位 部门归档
                {
                    inserSql1.append("b0110,");
                }
				inserSql1.append("i9999)");
				inserSql2.append("?,?)");
				ArrayList list = new ArrayList();
				// 判断团队计划是否有团队负责人  2011.05.16  JinChunhai
				if ("4".equals(this.filedType)) // 团队负责人归档
				{
					StringBuffer strSql = new StringBuffer();
					strSql.append("select DISTINCT pm.object_id,pm.mainbody_id from per_mainbody pm," + this.tableName + " pa ");
					strSql.append(" where pm.body_id=-1 ");
					strSql.append(" and pm.plan_id = " + this.planid);
					rowSet = dao.search(strSql.toString());
//					ArrayList list = new ArrayList();
					while (rowSet.next()) {
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("object_id", rowSet.getString("object_id"));
						abean.set("mainbody_id", rowSet.getString("mainbody_id"));
						list.add(abean);
					}
				}

				// 归档，要先删掉子集中以前归档的数据，以防重复归档
				for (int i = 0; i < destFldIdList.size(); i++) {
					String destFldId = (String) destFldIdList.get(i);
					LazyDynaBean abean = (LazyDynaBean) map.get(destFldId);
					String srcFldId = (String) abean.get("srcFldId");
					if ("plan_id".equalsIgnoreCase(srcFldId)) {
						StringBuffer buf = new StringBuffer();
						buf.append("delete from ");
						// 人员归档 团队负责人归档
						if ("1".equals(this.filedType) || "4".equals(this.filedType)) {
							buf.append("usr");
						}
						buf.append(subSet);
						buf.append(" where ");
						buf.append(destFldId + "=?");
						dao.update(buf.toString(), Arrays.asList(new Object[] {Integer.valueOf(this.planid) }));
						break;
					}
				}

				insertSql = inserSql1.toString() + inserSql2.toString();
				rs = dao.search(selStr.toString());

				while (rs.next()) {
					ArrayList list2 = new ArrayList();
					// 归档方案中指定了的目标字段 验证数据有效性
					for (int i = 0; i < destFldIdList.size(); i++) {
						String destFldId = (String) destFldIdList.get(i);
						LazyDynaBean abean = (LazyDynaBean) map.get(destFldId);
						String srcFldId = (String) abean.get("srcFldId");
						String destCodeSet = (String) abean.get("destCodeSet");
						if ("0".equals(destCodeSet)) {
							FieldItem item = DataDictionary.getFieldItem(destFldId);
							if (item == null) {
                                throw GeneralExceptionHandler.Handle(new Exception("请刷新数据字典!"));
                            }
							int x = item.getItemlength();

							if ("D".equals(item.getItemtype())) {
                                list2.add(rs.getDate(srcFldId));
                            } else if ("M".equals(item.getItemtype())) {
                                list2.add(Sql_switcher.readMemo(rs, srcFldId));
                            } else if ("N".equals(item.getItemtype())) {
								if (rs.getObject(srcFldId) != null) {
									if (item.getDecimalwidth() == 0) {
										String str = (String) rs.getString(srcFldId) == null ? "" : (String) rs.getString(srcFldId);
										if (str != null && str.trim().length() > 0) {
                                            str = Double.toString(Double.parseDouble(str));//去掉小数点后面的0
                                        }

										int y = str.getBytes().length;
										if (x < y) {
                                            throw new GeneralException("[" + item.getItemdesc() + "]指标长度不够！");
                                        }

										list2.add(new Integer(PubFunc.round(rs.getString(srcFldId), 0)));
									} else if (item.getDecimalwidth() > 0) {
										String str = (String) rs.getString(srcFldId) == null ? "" : (String) rs.getString(srcFldId);
										if (str != null && str.trim().length() > 0) {
                                            str = Double.toString(Double.parseDouble(str));//去掉小数点后面的0
                                        }

										int y = str.getBytes().length;
										if (x < y) {
                                            throw new GeneralException("[" + item.getItemdesc() + "]指标长度不够！");
                                        }

										list2.add(new Float(rs.getFloat(srcFldId)));
									}
								} else {
                                    list2.add(null);
                                }
							} else {
								String theValue = rs.getString(srcFldId);
								if (theValue == null) {
                                    theValue = "";
                                }
								int y = theValue.getBytes().length;
								if (x < y) {
                                    throw new GeneralException("[" + item.getItemdesc() + "]指标长度不够！");
                                }
								list2.add(theValue);
							}

						} else
						// 目标字段为代码类型时候要通过代码对应表（trandb_code）来获得目标值
						{
							int id = this.getID();
							String codename2 = rs.getString(srcFldId);
							String codeset1 = destCodeSet;
							list2.add(getCode1(id, codename2, codeset1));
						}
					}
					String object_id = rs.getString("object_id");
					if (object_id == null) {
                        continue;
                    }

					// 判断团队计划是否有团队负责人  2011.05.16  JinChunhai
					String haveOrNot = "false";
					if ("4".equals(this.filedType)) // 团队负责人归档
					{
						for (int i = 0; i < list.size(); i++) {
							LazyDynaBean abean = (LazyDynaBean) list.get(i);
							String mainbody_id = (String) abean.get("mainbody_id");
							if (object_id.equalsIgnoreCase(mainbody_id)) {
								haveOrNot = "true";
								break;
							}
						}
						if ("true".equalsIgnoreCase(haveOrNot)) {
							list2.add(object_id);
						} else {
                            continue;
                        }
					} else {
						list2.add(object_id);
					}

					int i9999 = this.getI9999(subSet, object_id);
					list2.add(new Integer(i9999));
					dao.insert(insertSql, list2);
				}
			}

			// 归档和结束 往考核结果历史记录表(per_history_result)写归档数据 同时更新计划的状态
			if ("2".equals(oper) || "3".equals(oper)) {
				RecordVo tempvo = this.getPerPlanVo(this.planid);
				String byModel = tempvo.getString("bymodel") == null ? "" : tempvo.getString("bymodel");
				if ("1".equals(byModel)) {//如果是按岗位素质模型测评，则只需要更新归档日期。
					String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
					switch (Sql_switcher.searchDbServer()) {
						case Constant.ORACEL: {
							creatDate = "to_date('" + creatDate + "','yyyy-mm-dd')";
							break;
						}
						case Constant.MSSQL: {
							creatDate = "'" + creatDate + "'";
							break;
						}
					}
					StringBuffer inner = new StringBuffer("");
					inner.append("update per_history_result set archive_date=" + creatDate + " where plan_id=" + this.planid);
					dao.update(inner.toString());
				} else {
					String delSql = "delete from per_history_result where plan_id=" + this.planid;
					dao.delete(delSql, new ArrayList());

					String selSql = "select * from per_result_" + this.planid;
					insertSql = "insert into per_history_result(id,plan_id,b0110,e0122,e01a1,object_id,a0101,archive_date,point_id,score,status)" + "values(?,?,?,?,?,?,?,?,?,?,?)";

					rs = dao.search(selSql);
					ArrayList insertList = new ArrayList();
					map = this.getPointItems();
					ArrayList itemList = (ArrayList) map.get("itemList");
					ArrayList pointList = (ArrayList) map.get("pointList");
					String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
					while (rs.next()) {
						String b0110 = rs.getString("b0110");
						String e0122 = rs.getString("e0122");
						String e01a1 = rs.getString("e01a1");
						String object_id = rs.getString("object_id");
						String a0101 = rs.getString("a0101");

						for (int i = 0; i < pointList.size(); i++) {
							IDGenerator idg = new IDGenerator(2, this.cn);
							String id = idg.getId("per_history_result.id");
							String point = (String) pointList.get(i);

							ArrayList list = new ArrayList();
							list.add(new Integer(id));
							list.add(new Integer(this.planid));
							list.add(b0110);
							list.add(e0122);
							list.add(e01a1);
							list.add(object_id);
							list.add(a0101);
							list.add(java.sql.Date.valueOf(creatDate));
							list.add(point.toUpperCase());
							list.add(new Float(rs.getFloat("C_" + point.toUpperCase())));
							list.add(new Integer(0));
							insertList.add(list);

						}
						for (int i = 0; i < itemList.size(); i++) {
							IDGenerator idg = new IDGenerator(2, this.cn);
							String id = idg.getId("per_history_result.id");
							String item = (String) itemList.get(i);

							ArrayList list = new ArrayList();
							list.add(new Integer(id));
							list.add(new Integer(this.planid));
							list.add(b0110);
							list.add(e0122);
							list.add(e01a1);
							list.add(object_id);
							list.add(a0101);
							list.add(java.sql.Date.valueOf(creatDate));
							list.add(item.toUpperCase());
							list.add(new Float(rs.getFloat("T_" + item.toUpperCase())));
							list.add(new Integer(1));
							insertList.add(list);
						}

						IDGenerator idg = new IDGenerator(2, this.cn);
						String id = idg.getId("per_history_result.id");
						ArrayList list = new ArrayList();
						list.add(new Integer(id));
						list.add(new Integer(this.planid));
						list.add(b0110);
						list.add(e0122);
						list.add(e01a1);
						list.add(object_id);
						list.add(a0101);
						list.add(java.sql.Date.valueOf(creatDate));
						list.add("total_value");
						list.add(new Float(rs.getFloat("score")));
						list.add(new Integer(2));
						insertList.add(list);

					}

					dao.batchUpdate(insertSql, insertList);
				}

				if ("2".equals(oper))//归档
				{
					if ("2".equals(filedType))// 团队 单位 部门的归档
					{
						String sql = "select body_id  from per_plan_body where plan_id=" + this.planid + " and body_id=-1";
						rs = dao.search(sql);
						if (!rs.next()) {// 有团队负责人要继续进行团队负责人的归档 归档无团队负责人的计划
							dao.update("update per_plan set status='7' where plan_id=" + this.planid);
						}

					} else {
                        dao.update("update per_plan set status='7' where plan_id=" + this.planid);
                    }
				} else if ("3".equals(oper))//结束
                {
                    dao.update("update per_plan set status='7' where plan_id=" + this.planid);
                }

				dao.update("update per_object set sp_flag='06' where plan_id=" + this.planid);// 更新考核对象表中sp_flag的内容

				// 删除根据计算公式计算出总体评价的临时表 JinChunhai 2012.11.13
				String tablename = "t#_per_app_" + this.planid;
				// 此临时表若存在就drop掉
				if (dbWizard.isExistTable(tablename, false)) {
					dbWizard.dropTable(tablename);
				}
			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return flag;
	}

	public HashMap getPointItems()
	{

		HashMap map = new HashMap();
		ArrayList itemList = new ArrayList();
		ArrayList pointList = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			String sql = "select item_id from per_template_item where Template_id=(select template_id from per_plan where plan_id=" + this.planid + ") ";
			RowSet rowSet = dao.search(sql);
			while (rowSet.next()) {
                itemList.add(rowSet.getString("item_id"));
            }

			sql = "select * from  per_template_point where item_id in (select item_id from per_template_item where Template_id=(select template_id from per_plan where plan_id=" + this.planid + "))";
			rowSet = dao.search(sql);
			while (rowSet.next()) {
                pointList.add(rowSet.getString("point_id"));
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		map.put("itemList", itemList);
		map.put("pointList", pointList);
		return map;
	}

	/**
	 * 目标字段为代码类型时候要通过代码对应表（trandb_code）来获得目标值
	 */
	public String getCode1(int id, String codename2, String codeset1)
	{

		String str = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rowSet = dao.search("select code1 from trandb_code where id=" + new Integer(id).toString() + " and codeset1='" + codeset1 + "' and codename2='" + codename2 + "'");
			if (rowSet.next()) {
                str = rowSet.getString("code1");
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/*
	 * 取得对象的个数
	 */
	public int getI9999(String setName, String object_id)
	{

		int count = 0;
		StringBuffer buf = new StringBuffer("select " + Sql_switcher.isnull("max(i9999)", "0") + " n from ");
		// status:1-人员归档 2-单位 部门 团队的归档 4-团队负责人归档
		if ("4".equals(this.filedType) || "1".equals(this.filedType)) {
            buf.append("usr" + setName + " where a0100='" + object_id + "'");
        } else if ("2".equals(this.filedType)) {
            buf.append(setName + " where b0110='" + object_id + "'");
        }

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
			{
				double number = rs.getDouble(1);
				
//				System.out.println(number);
				
				int num = (int)number;
//				if(number == 0.0)
//					num = Integer.parseInt(number);				
				count = num + 1;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}

	public String getFiledType()
	{

		return filedType;
	}
}
