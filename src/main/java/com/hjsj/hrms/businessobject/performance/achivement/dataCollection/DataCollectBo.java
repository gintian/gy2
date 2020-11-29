package com.hjsj.hrms.businessobject.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>Title:DataCollectBo.java</p>
 * <p>Description:数据采集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 13:00:00</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class DataCollectBo
{
	private Connection cn = null;
	private String errors = "";

	private String planid = "";

	private int columnWidth = 60;

	private String point = "";
	
	private String points = "";

	private ArrayList dataList = new ArrayList();

	UserView userview = null;

	private String planMethod = "1";// 考核计划的考核方法[1-360,2-目标管理]
	
	private  String display_e0122 ="0";
	
	private String  seprartor="";
	private RecordVo template_vo = null;
	private HashMap onlyValueMap1=new HashMap();/**(a0100,onlyFldValue)*/
	private HashMap onlyValueMap2=new HashMap();/**(onlyFldValue,a0100)*/
	private HashMap onlyValueMap3=new HashMap();/**(a0101,a0100)*/
	private String onlyname ="a0101";//唯一性标志指标
	private RecordVo planVo;
	public String getPlanMethod()
	{

		return planMethod;
	}

	public String getPoint()
	{

		return point;
	}

	public String getOnlyname()
	{
		return onlyname;
	}
	HashMap unifiedScoreMap = new HashMap();// 定量统一打分指标
	HashMap templatePointRank = new HashMap();// 模板指标权重
	HashMap objPointRanks=new HashMap();//动态指标权重
	Permission pointPrivBean=null;
	public DataCollectBo(Connection cn, String planid, String point, UserView u) throws GeneralException
	{
		
		this.userview = u;
		this.cn = cn;
		this.planid = planid;
		this.planMethod = getPlanMethod(planid);
		pointPrivBean=new Permission(this.cn,this.userview);
		if ((point!=null) && (point.trim().length()>0))
			this.point = point;
		else
		{
			ArrayList list = getPointList();
			// if (list.size() == 1) {
			// CommonData thePoint = (CommonData) list.get(0);
			// this.point = (String) thePoint.getDataValue();// 一进去显示下拉中的第一个
			// /** 如果第一个为非录分的基本指标则要继续判断其有没有和业绩任务书进行关联 */
			// String type = getTypeOfPoint();
			// if (type.equals("0") || type.equals(""))// 基本指标
			// {
			// String pointctrl = this.getPointctrl();
			// HashMap map = PointCtrlXmlBo.getAttributeValues(pointctrl);
			// String rule = (String) map.get("computeRule");
			// if (rule != null && !rule.equals("0")) // 基本指标非录分
			// {
			// if (!this.isHavePoint())
			// throw new GeneralException("该考核计划下存在没有和业绩任务书关联的指标！");
			// }
			// }
			// }
			if (list.size() >= 1)// 判断所有考核指标是否和业绩任务书关联的指标对应
			{
				CommonData thePoint = (CommonData) list.get(0);
				this.point = (String) thePoint.getDataValue();// 一进去显示下拉中的第一个
				for (int i = 0; i < list.size(); i++)
				{
					CommonData tempPoint = (CommonData) list.get(i);
					String pointId = (String) tempPoint.getDataValue();
					String pointName = (String) tempPoint.getDataName();
					String type = getTypeOfPoint(pointId);
					if ("0".equals(type) || "".equals(type))// 基本指标
					{
						StandardItemBo SIB = new StandardItemBo(this.cn);
						HashMap ruleMap = SIB.getRuleValue(pointId);
						if (ruleMap != null)
						{
							String convert = ruleMap.get("convert") != null ? (String) ruleMap.get("convert") : "";
							if ("1".equals(convert))// 按折算计分的指标不做如下验证
								continue;
						}

						String pointctrl = this.getPointctrl1(pointId);
						HashMap map = PointCtrlXmlBo.getAttributeValues(pointctrl);
						String rule = (String) map.get("computeRule");
						if (rule != null && !"0".equals(rule)) // 只是判断基本指标中的简单 分段 排名的指标 非录分
						{
							if (!this.isHavePoint(pointId))// 该考核计划下存在没有和业绩任务书关联的指标！
								throw new GeneralException("业绩任务书没有关联指标:[" + pointName + "]!");

						}
					}
				}
			}
		}
		
		this.planVo=this.getPlanVo();
		this.template_vo=this.get_TemplateVo();
		/** 定量统一打分指标 保存更新结果表指标值的时候要乘以权重 为了兼容cs */
		this.unifiedScoreMap = this.getUnifiedScorePointList(this.planVo.getString("template_id"));
		this.templatePointRank=this.getTemplatePointRank(this.planVo.getString("template_id"));	
	    if(this.planVo.getInt("method")!=2)
	    	this.objPointRanks=this.getObjPointRank();//考核对象的动态指标权重
	    
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.cn);
		this.display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(this.display_e0122==null|| "00".equals(this.display_e0122)|| "".equals(this.display_e0122))
			this.display_e0122="0";
		this.seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		this.seprartor=this.seprartor!=null&&this.seprartor.length()>0?this.seprartor:"/";
		 
		ContentDAO dao = new ContentDAO(this.cn);
		this.onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		
//		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
//		if(uniquenessvalid.equals("0") || this.onlyname.equals(""))
//			this.onlyname ="a0101";
		
		if(this.onlyname == null || this.onlyname.trim().length()<=0 || "#".equals(this.onlyname))
			this.onlyname ="a0101";
		
		if(this.onlyname!=null && this.onlyname.trim().length()>0 && !"#".equals(this.onlyname))
		{
			FieldItem fielditem = DataDictionary.getFieldItem(this.onlyname);
			String useFlag = fielditem.getUseflag(); 
			if("0".equalsIgnoreCase(useFlag))
				throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
		}
		
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
	
		try
		{
			if (this.planVo.getInt("object_type")==2 && this.onlyname != null && !"a0101".equals(this.onlyname))
			{
					sql.append("select a0100,"+onlyname+",a0101 from usra01 where a0100 in ");
					sql.append("(select object_id from per_object where plan_id="+this.planid+")");
					rs = dao.search(sql.toString());
					while(rs.next())
					{
						onlyValueMap1.put(rs.getString(1), rs.getString(2));
						onlyValueMap2.put(rs.getString(2), rs.getString(1));
					}						
			}
			sql.setLength(0);
			sql.append("select a0100,a0101 from usra01 where a0100 in ");
			sql.append("(select object_id from per_object where plan_id="+this.planid+")");
			rs = dao.search(sql.toString());
			while(rs.next())
				onlyValueMap3.put(rs.getString(2), rs.getString(1));
				
			if(rs!=null)
				rs.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**获得考核对象的动态指标权重*/
    public HashMap getObjPointRank()
    {
	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.cn);
	String sql="select * from per_dyna_rank where plan_id="+this.planid;
	try
	{
	    RowSet rs = dao.search(sql);
	    while(rs.next())
	    {
	        map.put(rs.getString("point_id").toUpperCase()+"_"+rs.getString("dyna_obj"),Double.toString(rs.getDouble("rank")));
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
    }
	
	/**
	 * 取得统一打分指标
	 * 
	 * @param template_id
	 * @return
	 */
	public HashMap getUnifiedScorePointList(String template_id)
	{
		HashMap amap = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			String sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.Pointtype,po.Pointctrl from per_template_item pi,per_template_point pp,per_point po "
					+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + template_id + "'  order by pp.seq";
			RowSet rowSet = dao.search(sql); // pi.seq,
			while (rowSet.next())
			{
				String point_id = rowSet.getString("point_id");
				String pointkind = rowSet.getString("pointkind");
				String status = rowSet.getString("status");
				String Pointtype = rowSet.getString("Pointtype");
				String Pointctrl = Sql_switcher.readMemo(rowSet, "Pointctrl");
				if ("1".equals(pointkind) && (status != null && "1".equals(status)))
				{
					HashMap map = PointCtrlXmlBo.getAttributeValues(Pointctrl);

					LazyDynaBean abean = new LazyDynaBean();
					abean.set("point_id", rowSet.getString("point_id"));
					abean.set("pointname", rowSet.getString("pointname"));
					abean.set("pointkind", rowSet.getString("pointkind"));
					abean.set("item_id", rowSet.getString("item_id"));
					abean.set("status", rowSet.getString("status") != null ? rowSet.getString("status") : "");
					abean.set("score", rowSet.getString("score"));
					abean.set("Pointtype", rowSet.getString("Pointtype") != null ? rowSet.getString("Pointtype") : "");
					abean.set("Pointctrl", Pointctrl);
					amap.put(rowSet.getString("point_id").toUpperCase(), abean);

				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return amap;
	}
	/**
	 * 取得模板指标权重
	 * 
	 * @param template_id
	 * @return
	 */
	public HashMap getTemplatePointRank(String template_id)
	{
		HashMap amap = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			String sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id='" + template_id + "') ";
			RowSet rowSet = dao.search(sql); // pi.seq,
			while (rowSet.next())
			{
//					LazyDynaBean abean = new LazyDynaBean();
//					abean.set("point_id", rowSet.getString("point_id"));
//					abean.set("rank", rowSet.getString("rank"));					
					amap.put(rowSet.getString("point_id").toUpperCase(), rowSet.getString("rank"));				
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return amap;
	}
	public RecordVo get_TemplateVo()
	{
		RecordVo vo=new RecordVo("per_template");
		try
		{
			vo.setString("template_id",this.planVo.getString("template_id"));
			ContentDAO dao = new ContentDAO(this.cn);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	public DataCollectBo(Connection cn)
	{

		this.cn = cn;
	}

	public DataCollectBo(Connection cn, String planid, UserView u) throws GeneralException
	{

		this.userview = u;
		this.planid = planid;
		this.cn = cn;
		this.planMethod = getPlanMethod(planid);
		this.planVo=this.getPlanVo();
		pointPrivBean=new Permission(this.cn,this.userview);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.cn);
		this.display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(this.display_e0122==null|| "00".equals(this.display_e0122)|| "".equals(this.display_e0122))
			this.display_e0122="0";
		this.seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		this.seprartor=this.seprartor!=null&&this.seprartor.length()>0?this.seprartor:"/";
		 
		ContentDAO dao = new ContentDAO(this.cn);
		this.onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		
//		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
//		if(uniquenessvalid.equals("0") || this.onlyname.equals(""))
//			this.onlyname ="a0101";		
		
		if(this.onlyname == null || this.onlyname.trim().length()<=0 || "#".equals(this.onlyname))
			this.onlyname ="a0101";
		
		if(this.onlyname!=null && this.onlyname.trim().length()>0 && !"#".equals(this.onlyname))
		{
			FieldItem fielditem = DataDictionary.getFieldItem(this.onlyname);
			String useFlag = fielditem.getUseflag(); 
			if("0".equalsIgnoreCase(useFlag))
				throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
		}
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
	
		try
		{
			if (this.planVo.getInt("object_type")==2 && this.onlyname != null && !"a0101".equals(this.onlyname))
			{
					sql.append("select a0100,"+onlyname+",a0101 from usra01 where a0100 in ");
					sql.append("(select object_id from per_object where plan_id="+this.planid+")");
					rs = dao.search(sql.toString());
					while(rs.next())
					{
						onlyValueMap1.put(rs.getString(1), rs.getString(2));
						onlyValueMap2.put(rs.getString(2), rs.getString(1));
					}						
			}
			sql.setLength(0);
			sql.append("select a0100,a0101 from usra01 where a0100 in ");
			sql.append("(select object_id from per_object where plan_id="+this.planid+")");
			rs = dao.search(sql.toString());
			while(rs.next())
				onlyValueMap3.put(rs.getString(2), rs.getString(1));
			if(rs!=null)
				rs.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public DataCollectBo(Connection cn, UserView u)
	{

		this.userview = u;
		this.cn = cn;
		pointPrivBean=new Permission(this.cn,this.userview);
	}

	public String getPlanCnName() throws GeneralException
	{

		String planName = "";
		ContentDAO dao = new ContentDAO(this.cn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", this.planid);
		try
		{
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		planName = vo.getString("name");
		return planName;
	}

	public RecordVo getPlanVo() throws GeneralException
	{

		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", this.planid);
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return vo;
	}

	/** 获得管理权限范围内的人员 */
	public String getPrivEmpStr()
	{

		StringBuffer buf = new StringBuffer();
		// this.userview.getDbname();取得当前的库前缀
		String priStrSql = InfoUtils.getWhereINSql(this.userview, "Usr");
		buf.append("select usra01.A0100 ");
		if (priStrSql.length() > 0)
			buf.append(priStrSql);
		else
			buf.append(" from usra01");

		return buf.toString();
	}

	/**
	 * 获得当前计划的状态,如果为评估或者是结束则不许录入业绩数据，为只读状态
	 */
	public String getPlanStatus()
	{

		String status = "";
		String sql = "select status from per_plan where plan_id=" + this.planid;
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				status = rs.getString("status");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * 移除小数点后面的零 是零就不显示了
	 * 
	 * @param number
	 */
	public String moveZero(String number)
	{

		DecimalFormat df = new DecimalFormat("###############.#####");
		if (number == null || number.length() == 0)
			return "0";
//		if (Float.parseFloat(number) == 0)
//			return "";
		return df.format(Double.parseDouble(number));
	}

	/** 过滤计划 （只列出执行和评估状态的计划）(只列出在管理权限范围内有考核对象的计划) 北京政法委信用档案 */
	public ArrayList getPlanList(String planContext)
	{
		String whlSql=" and status in (4,6) and cycle!=7 ";
		ExamPlanBo bo = new ExamPlanBo(this.cn);
	//	HashMap map = bo.getPlansByUserView(this.userview, whlSql);	
		HashMap map = bo.getPlansByUserViewNoTemp(this.userview, whlSql);//先找到
		
		ArrayList list = new ArrayList();
		String sql = "select * from per_plan where 1=1 "+whlSql+" order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			LoadXml loadxml = null;
			Hashtable planParam=null;
			while (rs.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				String plan_id = rs.getString("plan_id");
				if(map.get(plan_id)==null)
		    		continue;	
				
				String method = rs.getString("method");
				String name = rs.getString("name") == null ? "" : rs.getString("name");
				String object_type = rs.getString("object_type");
				abean.set("plan_id", rs.getString("plan_id"));
				abean.set("name", rs.getString("name") == null ? "" : rs.getString("name"));
				String content = Sql_switcher.readMemo(rs, "content");
				if (planContext != null && planContext.trim().length() > 0)
				{
					if ("zfyj".equals(planContext) && !"执法业绩".equals(content.trim()))
						continue;
				}
 
				
				String scoreOrg = "";
		        if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id)== null) {
		            loadxml = new LoadXml(this.cn, String.valueOf(plan_id));
		            BatchGradeBo.getPlanLoadXmlMap().put(plan_id, loadxml);
		        } else
		            loadxml = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
		        planParam = loadxml.getDegreeWhole();
		        String TargetDefineItem = "";
		        if (planParam.get("TargetDefineItem") != null
	                    && ((String) planParam.get("TargetDefineItem")).trim()
	                            .length() > 0) {
	                String temp = (String) planParam.get("TargetDefineItem");
	                temp = temp.replaceAll(",", "");
	                if (temp.trim().length() > 0)
	                    TargetDefineItem = (","
	                            + (String) planParam.get("TargetDefineItem") + ",")
	                            .toUpperCase();
	            }
		        if(TargetDefineItem.indexOf(",SCORE_ORG,") != -1)
		            scoreOrg = "score_org";
				
				String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
				String privCode = this.userview.getManagePrivCode() + this.userview.getManagePrivCodeValue();// 管理范围

				if ("".equals(operOrg.trim()) && "".equals(privCode.trim()))// 没有组织机构的权限
					continue;
				if (method != null && "2".equals(method))// 目标管理计划
				{
					String targetCollectItem = this.getTargetCollectItems(plan_id);// 先检测有没有设置目标卡采集指标
					if ("".equals(targetCollectItem) && getPointList(plan_id).size() == 0)
						continue;
					// 判断有没有权限范围内的考核对象
					// 对于考核对象的限制这样处理：不管考核对象是什么类型，先取操作单位，如果取不出来就按管理范围走

					if ("".equals(targetCollectItem))// 没有设置目标卡采集指标的目标计划
					{
						if ("".equals(operOrg.trim()))// 按照管理范围走
						{
							if ("2".equals(object_type))// 人员
							{
								if (!isExistPrivEmps(plan_id))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							} else
							// 非人员
							{
								if (!isExistTeamKhObj(plan_id, "per_object",""))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							}
						} else
						// 按照操作单位范围走
						{
							if ("2".equals(object_type))// 人员
							{
								if (!isExistPrivEmps2(plan_id, "per_object",""))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							} else
							// 非人员
							{
								if (!isExistTeamKhObj2(plan_id, "per_object",""))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							}
						}
					} else
					// 设置了目标卡采集指标的目标计划
					{
						if ("".equals(operOrg.trim()))// 按照管理范围走
						{
							if ("2".equals(object_type))// 人员
							{
								if (!isExistPrivEmps3(plan_id,scoreOrg))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							} else
							// 非人员
							{
								if (!isExistTeamKhObj(plan_id, "p04",scoreOrg))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							}
						} else
						// 按照操作单位范围走
						{
							if ("2".equals(object_type))// 人员
							{
								if (!isExistPrivEmps2(plan_id, "p04",scoreOrg))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							} else
							// 非人员
							{
								if (!isExistTeamKhObj2(plan_id, "p04",scoreOrg))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
									continue;
							}
						}
					}
				} else
				// 360计划 只是考虑了考核对象为人员的情况
				{
					if (getPointList(plan_id).size() == 0)// 如果某计划没有有效指标就不展现该计划的选项卡
						continue;
					if ("2".equals(object_type))// 人员
					{
						if ("".equals(operOrg.trim()))// 按照管理范围走
						{
							if (!isExistPrivEmps(plan_id))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
								continue;
						} else
						// 按照操作单位走
						{
							if (!isExistPrivEmps2(plan_id, "per_object",""))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
								continue;
						}
					} else
					{
						if ("".equals(operOrg.trim()))// 按照管理范围走
						{
							if (!isExistPrivTeamObjs(plan_id))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
								continue;
						} else
						// 按照操作单位走
						{
							if (!isExistPrivTeamObjs2(plan_id))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
								continue;
						}
					}
				}
				try
				{
					this.planMethod=method;
					this.planid=plan_id;
					ArrayList pointList=getPointList();
				}
				catch(Exception ee)
				{
					continue;
				}

				list.add(abean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/** 是否存在管理范围内的非人员考核对象 */
	public boolean isExistTeamKhObj(String plan_id, String tableName,String scoreorg)
	{

		boolean isExist = false;
		String objFld = "object_id";
		if ("p04".equalsIgnoreCase(tableName))
			objFld = "b0110";
		if(scoreorg!=null&&scoreorg.trim().length()>0)
		    objFld=scoreorg;
		
		StringBuffer buf = new StringBuffer();
		buf.append("select * from " + tableName + " where plan_id=" + plan_id);
        
		String privCode = this.userview.getManagePrivCode() + this.userview.getManagePrivCodeValue();
		if (!this.userview.isSuper_admin())
		{
			if (privCode != null && privCode.trim().length() > 0)
			{
				String codesetid = privCode.substring(0, 2);
				String value = privCode.substring(2);
				if (value.length() > 0)
				{
					buf.append(" and (" + objFld + " like '");
					buf.append(value);
					buf.append("%'");
					if(scoreorg!=null&&scoreorg.trim().length()>0)
					    buf.append(" or "+objFld+" is null ");
					buf.append(" )  ");
				}
			}
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				isExist = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return isExist;
	}

	public String getTargetCollectItems(String plan_id)
	{

		String targetCollectItem = "";
		// 1.取对应于考核计划的参数设置中定义的 目标跟踪显示和采集指标
		LoadXml parameter_content = new LoadXml(this.cn, plan_id);
		Hashtable params = parameter_content.getDegreeWhole();
		String targetTraceEnabled = (String) params.get("TargetTraceEnabled");
		if ("true".equals(targetTraceEnabled))
		{
			targetCollectItem = (String) params.get("TargetCollectItem");
		} else
		// 2.从绩效模块参数配置中取目标跟踪显示和采集指标
		{
			ConfigParamBo configParamBo = new ConfigParamBo(this.cn);
			targetCollectItem = configParamBo.getTargetCollectItem();
		}
		return targetCollectItem;
	}

	/** 过滤计划 （只列出执行和评估状态的计划）(只列出在管理权限范围内有考核对象的计划) */
	public ArrayList getPlanList()
	{

		ArrayList list = new ArrayList();
		String sql = "select * from per_plan where status in (4,6) and cycle!=7 order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				String plan_id = rs.getString("plan_id");
				abean.set("plan_id", rs.getString("plan_id"));
				abean.set("name", rs.getString("name") == null ? "" : rs.getString("name"));
				if (getPointList(plan_id).size() == 0)// 如果某计划没有有效指标就不展现该计划的选项卡
					continue;
				if (!isExistPrivEmps(plan_id))// 如果某计划不存在管理权限范围内的考核对象就不展现该计划的选项卡
					continue;
				list.add(abean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/** 判断某计划是否存在管理权限范围内的考核对象 */
	public boolean isExistPrivEmps(String planid)
	{

		boolean exist = false;
		String sql = "select * from per_object where plan_id=" + planid + " and object_id in (" + this.getPrivEmpStr() + ")";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				exist = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return exist;
	}

	/** 判断某计划是否存在管理权限范围内的团队考核对象 */
	public boolean isExistPrivTeamObjs(String planid)
	{

		boolean exist = false;
		String sql = "select * from per_object where plan_id=" + planid + " and object_id  like '" + this.userview.getManagePrivCodeValue() + "%'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				exist = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return exist;
	}

	/** 判断某计划目标任务表是否存在管理权限范围内的考核对象 */
	public boolean isExistPrivEmps3(String planid,String scoreorg)
	{

		boolean exist = false;
		String sql = "select * from p04 where plan_id=" + planid ;
		if(scoreorg==null||scoreorg.trim().length()==0)
		    sql+=" and a0100 in (" + this.getPrivEmpStr() + ")";
		else
		{
        		String privCode = this.userview.getManagePrivCode() + this.userview.getManagePrivCodeValue();
                if (!this.userview.isSuper_admin())
                {
                    if (privCode != null && privCode.trim().length() > 0)
                    {
                        String codesetid = privCode.substring(0, 2);
                        String value = privCode.substring(2);
                        if (value.length() > 0)
                        {
                            sql+=" and (" + scoreorg + " like '"+value+"%'"; 
                            sql+=" or "+scoreorg+" is null ) "; 
                        }
                    }
                }
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				exist = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return exist;
	}

	/** 是否存在操作单位范围内的非人员考核对象 
	 *  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public boolean isExistTeamKhObj2(String plan_id, String tableName,String scoreorg)
	{

		boolean isExist = false;
		String objFld = "object_id";
		if ("p04".equalsIgnoreCase(tableName))
			objFld = "b0110";
		if(scoreorg!=null&&scoreorg.trim().length()>0)
            objFld=scoreorg;
		StringBuffer buf = new StringBuffer();
		buf.append("select * from " + tableName + " where plan_id=" + plan_id);

		String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
		if (operOrg!=null && operOrg.length() > 3)
		{
			StringBuffer tempSql = new StringBuffer("");
			String[] temp = operOrg.split("`");
			for (int i = 0; i < temp.length; i++)
			{
				tempSql.append(" or " + objFld + " like '" + temp[i].substring(2) + "%'");
			}
			buf.append(" and ( " + tempSql.substring(3));
			if(scoreorg!=null&&scoreorg.trim().length()>0)
                buf.append(" or "+objFld+" is null ");
			buf.append( " ) ");
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				isExist = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return isExist;
	}

	/** 判断某计划是否存在操作单位范围内的人员类别的考核对象 
	 *  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public boolean isExistPrivEmps2(String planid, String tableName,String scoreOrg)
	{

		boolean exist = false;
		StringBuffer buf = new StringBuffer();
		buf.append("select * from " + tableName + " where plan_id=" + planid);

		if(scoreOrg==null||scoreOrg.trim().length()==0)
		{
    		String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
    		if (operOrg!=null && operOrg.length() > 3)
    		{
    			StringBuffer tempSql = new StringBuffer("");
    			String[] temp = operOrg.split("`");
    			for (int i = 0; i < temp.length; i++)
    			{
    
    				if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
    					tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
    				else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
    					tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
    
    			}
    			buf.append(" and ( " + tempSql.substring(3) + " ) ");
    		}
		}
		else
		{
		    String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
            if (operOrg!=null && operOrg.length() > 3)
            {
                StringBuffer tempSql = new StringBuffer("");
                String[] temp = operOrg.split("`");
                for (int i = 0; i < temp.length; i++){
                        tempSql.append(" or  "+scoreOrg+" like '" + temp[i].substring(2) + "%'");
    
                }
                buf.append(" and ( " + tempSql.substring(3) + " or "+scoreOrg+" is null ) ");
            }
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				exist = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return exist;
	}

	/** 判断某计划是否存在操作单位范围内的非人员类别的考核对象 
	 *  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public boolean isExistPrivTeamObjs2(String planid)
	{

		boolean exist = false;
		StringBuffer buf = new StringBuffer();
		buf.append("select * from per_object where plan_id=" + planid);

		String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
		if (operOrg!=null && operOrg.length() > 3)
		{
			StringBuffer tempSql = new StringBuffer("");
			String[] temp = operOrg.split("`");
			for (int i = 0; i < temp.length; i++)
			{
				tempSql.append(" or  object_id like '" + temp[i].substring(2) + "%'");
			}
			buf.append(" and ( " + tempSql.substring(3) + " ) ");
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				exist = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return exist;
	}

	public String getPointctrl()
	{

		String sql = "select * from per_point where point_id='" + this.point + "'";
		String pointctrl = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = Sql_switcher.readMemo(rs, "pointctrl");
				if (temp != null)
					pointctrl = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return pointctrl;
	}

	public String getPointctrl1(String pointid)
	{

		String sql = "select * from per_point where point_id='" + pointid + "'";
		String pointctrl = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = Sql_switcher.readMemo(rs, "pointctrl");
				if (temp != null)
					pointctrl = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return pointctrl;
	}

	/**
	 * 考核指标 打分权限范围内的指标列表， 统一打分指标 某计划的
	 */
	public ArrayList getPointList() throws GeneralException
	{
 
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		if ("2".equals(this.planMethod))// 目标管理的考核计划
		{
			// 取出指标(过滤出绩效的指标)包括个性和共性
			// 取出指标(过滤出绩效的指标)包括个性和共性
			sql.append("select per_point.point_id,per_point.pointname,per_point.seq from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(this.planid);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1");
			
			sql.append(" union all ");
			
			sql.append("select pp.point_id,pp.pointname,pp.seq from per_point pp join (");
			sql.append("select distinct p0401 from p04 where fromflag=2 and plan_id="+this.planid);
			sql.append(" and ((chg_type <> 3) or (chg_type is null)) ");
			sql.append(" and item_id in ");
			sql.append("(select item_id from per_template_item where kind=2 and template_id=(select template_id from per_plan where plan_id="+this.planid+"))");
			sql.append(") a on pp.point_id=a.p0401 and pp.status=1 and pp.pointkind=1 ");
		} else
		// 360考核计划
		{
			sql.append("select per_point.point_id,per_point.pointname from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(this.planid);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1 order by per_point.seq");
		}

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql.toString());
			while (rs.next())
			{
				String pointid = rs.getString("point_id");
				String pointname = rs.getString("pointname");
				if (!this.userview.isHaveResource(IResourceConstant.KH_FIELD, pointid))
					continue;
				String type = getTypeOfPoint1(pointid);
				if ("0".equals(type) || "".equals(type))// 基本指标
				{ // 如果计划下存在非录分的基本型指标，就判断该计划有没有关联任务书
					String pointctrl = this.getPointctrl1(pointid);
					HashMap map = PointCtrlXmlBo.getAttributeValues(pointctrl);
					String rule = (String) map.get("computeRule");
					if (rule != null && !"0".equals(rule))// 基本指标非录分
					{// 判断有没有关联业绩任务书
						if (!this.isHaveTask())
							throw new GeneralException("该计划没有关联业绩任务书!");
					}
				}
				CommonData temp = new CommonData(pointid, pointid+":"+pointname);
				list.add(temp);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("该计划没有关联业绩任务书!");

		}
		return list;
	}
	
	/**
	 * 考核指标数量 打分权限范围内的指标列表， 统一打分指标 某计划的  JinChunhai 2011.04.01
	 */
	public String getPrivPointList() throws GeneralException
	{

		String number = "false";		
		ArrayList list = new ArrayList();		
		ArrayList rlist = new ArrayList();
		HashMap map=new HashMap();
		StringBuffer sql = new StringBuffer();
		if ("2".equals(this.planMethod))// 目标管理的考核计划
		{
			// 取出指标(过滤出绩效的指标)包括个性和共性
			// 取出指标(过滤出绩效的指标)包括个性和共性
			sql.append("select per_point.point_id,per_point.pointname,per_point.seq from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(this.planid);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1");
			
			sql.append(" union all ");
			
			sql.append("select  pp.point_id,pp.pointname,pp.seq from per_point pp  join (");
			sql.append("select distinct p0401 from p04 where fromflag=2 and plan_id="+this.planid);
			sql.append(" and ((chg_type <> 3) or (chg_type is null)) ");
			sql.append(" and item_id in ");
			sql.append("(select item_id from per_template_item where kind=2 and template_id=(select template_id from per_plan where plan_id="+this.planid+"))");
			sql.append(") a on pp.point_id=a.p0401  and  pp.status=1 and pp.pointkind=1 ");
		} else
		// 360考核计划
		{
			sql.append("select per_point.point_id,per_point.pointname from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(this.planid);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1 order by per_point.seq");
		}
		RowSet rs = null;
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			rs = dao.search(sql.toString());
			Permission p=new Permission(this.cn,this.userview);
			// 获得计划下的指标总数
			while (rs.next())
			{
				String pointid = rs.getString("point_id");				
				CommonData temp = new CommonData(pointid, rs.getString("pointname"));
				list.add(temp);
			}
			// 获得计划下的对象
			String strSql = ("select object_id,b0110,e0122 from per_object where plan_id=" + this.planid );
			rowSet = dao.search(strSql);
			while (rowSet.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				String object_id=rowSet.getString("object_id");
				String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
				String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";						
								
				abean.set("object_id", object_id);
				abean.set("b0110", b0110);
				abean.set("e0122", e0122);
				
				rlist.add(abean);				
			}
			
			// 获得单个指标拥有的对象数
			for (int i = 0; i < list.size(); i++)
		    {
				CommonData d=(CommonData)list.get(i);
				String pointid=d.getDataValue();
				
				ArrayList alist = new ArrayList();
				for(int j = 0; j < rlist.size(); j++)
				{
		       		LazyDynaBean abean=(LazyDynaBean)rlist.get(j);
		       		String object_id = (String)abean.get("object_id");		       							
					String b0110 = (String)abean.get("b0110");	
					String e0122 = (String)abean.get("e0122");					
									
					boolean right = true;
					if(!"2".equals(this.planVo.getString("object_type")))  // 非2 团队
						right = p.getPrivPoint("", object_id, pointid);
					else if("2".equals(this.planVo.getString("object_type")))  // 2 人员
						right = p.getPrivPoint(b0110, e0122, pointid);
					if(right==true)					
						alist.add(object_id);																					
				}
				map.put(pointid, alist);				
			}
			
			double obj_total = (double)rlist.size();  // 计划下的对象总数 
			
			int cont = 0;
			Set keySet=map.keySet();
			java.util.Iterator t=keySet.iterator();
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值	    
				ArrayList ValueList = (ArrayList)map.get(strKey);   //value值   				
				double singleNum = (double)ValueList.size(); // 单个指标拥有的对象数
				if((singleNum/obj_total)<=0.5)
				{
					cont++;
				}				
			}
			
			if(cont>=5)
				number = "true";			
			
			if(rowSet!=null)    		
				rowSet.close();
			if(rs!=null)    		
				rs.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return number;
	}
			
	/** 判断某个指标是否为共性指标 */
	public boolean testPointIsCommon()
	{

		boolean flag = false;
		StringBuffer buf = new StringBuffer();
		buf.append("select * from per_template_point where point_id='");
		buf.append(this.point);
		buf.append("' and  item_id in (");
		buf.append("select item_id from per_template_item where template_id=(select template_id from per_plan where plan_id=");
		buf.append(this.planid);
		buf.append("))");
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				flag = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	public ArrayList getPointList(String plan_id)
	{

		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();

		if ("2".equals(this.getPlanMethod(plan_id)))// 目标管理的考核计划
		{
			// 取出指标(过滤出绩效的指标)包括个性和共性
			sql.append("select per_point.point_id,per_point.pointname from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(plan_id);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1");
			
			sql.append(" union all ");
			
			sql.append("select pp.point_id,pp.pointname from per_point pp join (");
			sql.append("select distinct p0401 from p04 where fromflag=2 and plan_id="+plan_id);
			sql.append(" and ((chg_type <> 3) or (chg_type is null)) ");
			sql.append(" and item_id in ");
			sql.append("(select item_id from per_template_item where kind=2 and template_id=(select template_id from per_plan where plan_id="+plan_id+"))");
			sql.append(") a on pp.point_id=a.p0401 and pp.status=1 and pp.pointkind=1 ");
		} else
		// 360考核计划
		{
			sql.append("select per_point.point_id,per_point.pointname from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(plan_id);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1 order by per_point.seq ");
		}

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql.toString());
			while (rs.next())
			{
				String pointid = rs.getString("point_id");
				if (!this.userview.isHaveResource(IResourceConstant.KH_FIELD, pointid))// 看是否有指标权限
					continue;
				CommonData temp = new CommonData(rs.getString("point_id"), rs.getString("pointname"));
				list.add(temp);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 指标类型 0|1|2:基本指标|加分指标|扣分指标
	 */
	public String getTypeOfPoint()
	{

		String type = "";
		String sql = "select pointtype from per_point where point_id='" + this.point + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("pointtype");
				if (temp == null)
					type = "0";
				else
					type = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return type;
	}

	/**
	 * 指标类型 0|1|2:基本指标|加分指标|扣分指标
	 */
	public String getTypeOfPoint(String pointid)
	{

		String type = "";
		String sql = "select pointtype from per_point where point_id='" + pointid + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("pointtype");
				if (temp == null)
					type = "0";
				else
					type = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return type;
	}

	/**
	 * 指标类型 0|1|2:基本指标|加分指标|扣分指标
	 */
	public String getTypeOfPoint1(String pointId)
	{

		String type = "";
		String sql = "select pointtype from per_point where point_id='" + pointId + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("pointtype");
				if (temp == null)
					type = "0";
				else
					type = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return type;
	}

	/**
	 * 画数据采集的表格
	 * 
	 * @throws GeneralException
	 */
	public String getTableHtml() throws GeneralException
	{

		StringBuffer html = new StringBuffer("");
		String type = getTypeOfPoint();
		if ("0".equals(type) || "".equals(type))// 基本指标
		{
			String pointctrl = this.getPointctrl();
			HashMap map = PointCtrlXmlBo.getAttributeValues(pointctrl);
			String rule = (String) map.get("computeRule");
			if (rule != null && "0".equals(rule))// 基本指标录分
				html.append(getTable3());
			else if (rule != null && !"0".equals(rule))
			// 基本指标非录分
			{
				if (!this.isHavePoint())
					throw new GeneralException("业绩任务书没有关联指标:" + this.getPointName());
				html.append(getTable1(rule));
			}
		} else
			html.append(getTable2(type)); // 加减分指标
		return html.toString();
	}

	/** 获得指标名称 */
	public String getPointName()
	{

		String sql = "select pointname from per_point where point_id='" + this.point + "'";
		String pointname = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("pointname");
				if (temp != null)
					pointname = temp;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return pointname;
	}

	public String getTh(String name, int lays, int opt, String idname, String className)
	{

		StringBuffer sb = new StringBuffer("");
		sb.append("<td   class='" + className + "' valign='middle' align='center' ");
		if (idname != null && idname.length() > 0)
			sb.append(" id='" + idname + "' ");
		if (lays > 0)
		{
			if (opt == 1)
			{
				if("cell_Collect".equalsIgnoreCase(className))
					sb.append("rowspan='" + lays + "'");
				else
					sb.append("rowspan='" + lays + "'  width='" + columnWidth + "'");
			}
			else if (opt == 2)
				sb.append("colspan='" + lays + "'  height='22'");
		} else
		{
			if("cell_Collect".equalsIgnoreCase(className))
				sb.append(" height='22' ");
			else
				sb.append(" height='22'  width='" + columnWidth + "'");
		}
		
		String memo = name;
		if(name!=null && name.length()>25)
		{
			
/*
//			memo = SafeCode.encode(memo);
//			System.out.println(memo.length());
			
			StringBuffer str = new StringBuffer(memo); 
			for (int k = 0; k < str.length(); k++)
			{				
				if((k!=0) && (k%15 == 0))
				{
					str.insert(k,"<br>"); 
				}
			}			
//			memo = SafeCode.decode(memo);			
//			System.out.println(str.toString());
*/			

//			memo=memo!=null&&memo.length()>0?memo.replaceAll("；","<br>"):"";
//			memo=memo!=null&&memo.length()>0?memo.replaceAll("，","<br>"):"";
			memo=memo!=null&&memo.length()>0?memo.replaceAll("\r\n","<br>"):"";
			memo=memo!=null&&memo.length()>0?memo.replaceAll(" ","<br>"):"";
			
			String text = memo;
			memo=memo.length()>25?memo.substring(0,25)+"...":memo;
			sb.append(" onmouseover=\"outContent('");
			sb.append(SafeCode.encode(text));
			sb.append("');\" onmouseout=\"UnTip()\"");
			sb.append(" style=\"word-break: break-all;word-wrap:break-word;\"");
		}				
		
		sb.append(" > ");
		sb.append(memo);
		sb.append("</td>");
		return sb.toString();
	}

	public String getTd(String name, String idname, String className)
	{

		StringBuffer sb = new StringBuffer("");
		sb.append("<td   class='" + className + "'  valign='middle' align='left' ");
		if (idname != null && idname.length() > 0)
			sb.append(" id='" + idname + "' ");
		sb.append(" > ");
		sb.append(name);
		sb.append("</td>");
		return sb.toString();
	}

	/*
	 * 基本指标－－录分
	 */
	public String getTable3() throws GeneralException
	{

		// String basicVal = this.getBasicFen();
		// String info = "(0~" + moveZero(basicVal) + ")";
		StringBuffer html = new StringBuffer("<table id='tbl'  class='ListTable'><thead>");
		html.append("<tr> ");
		int object_type = this.getPlanVo().getInt("object_type");
		if (object_type == 2)
		{
			
			FieldItem fielditem = DataDictionary.getFieldItem("B0110");
			html.append(getTh(fielditem.getItemdesc(), 0, 0, "a", "cell_Collect common_background_color common_border_color"));// cell_locked2
			
			fielditem = DataDictionary.getFieldItem("E0122");
			html.append(getTh(fielditem.getItemdesc(), 0, 0, "a", "cell_Collect common_background_color common_border_color"));// cell_locked2
			
			fielditem = DataDictionary.getFieldItem("E01A1");
			html.append(getTh(fielditem.getItemdesc(), 0, 0, "a", "cell_Collect common_background_color common_border_color"));// cell_locked2
			
			
			html.append(getTh(ResourceFactory.getProperty("gz.columns.a0101"), 0, 0, "a", "cell_Collectlocked2 common_background_color common_border_color"));// cell_locked2
			
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
			{
				FieldItem item = DataDictionary.getFieldItem(this.onlyname);
				html.append(getTh(item.getItemdesc(),  0, 0, "a", "cell_Collectlocked2 common_background_color common_border_color"));// cell_locked2
			}
		} else
			html.append(getTh(ResourceFactory.getProperty("jx.datacol.khobj"), 0, 0, "a", "cell_Collectlocked2 common_background_color common_border_color"));// cell_locked2

		if ("2".equals(this.planMethod))// 目标管理不显示录分范围
			html.append(getTh(ResourceFactory.getProperty("jx.param.mark"), 0, 0, "a", "header_Collectlocked common_background_color common_border_color"));// header_locked
		else
		// 360显示录分范围
		{
			String basicVal = this.getBasicFen360();
			String info = "(0~" + moveZero(basicVal) + ")";
			if(Double.parseDouble(basicVal)<0)
				info = "(" + moveZero(basicVal) + "~0)";
			html.append(getTh(ResourceFactory.getProperty("jx.param.mark") + info, 0, 0, "a", "header_Collectlocked common_background_color common_border_color"));// header_locked
		}
		html.append("</tr></thead> ");

		setData3();
		return html.toString();
	}

	/**
	 * 获得基本型指标录分规则的数据
	 * 
	 * @throws GeneralException
	 */
	public void setData3() throws GeneralException
	{

		this.dataList = new ArrayList();
		// 得到某考核计划下的所有考核对象,即为数据的行数
		ArrayList objs = getKhObjs();
		HashMap basicFenMap = this.getBasicFen();
		HashMap map = this.getFzScores();

		for (int p = 0; p < objs.size(); p++)
		{
			LazyDynaBean myBean = new LazyDynaBean();
			LazyDynaBean bean = (LazyDynaBean) objs.get(p);
			String object_id = (String) bean.get("object_id");
			String name = (String) bean.get("a0101");
			String e0122 = (String) bean.get("e0122Name");
			String b0110 = (String) bean.get("b0110"); //20141206 dengcan
			String e01a1 = (String) bean.get("e01a1"); //20141206 dengcan

			
			String fzScore = map.get(object_id)==null?"0":(String) map.get(object_id);	
			String basicVal = basicFenMap.get(object_id)==null?"0":(String) basicFenMap.get(object_id);
			
			int object_type = this.getPlanVo().getInt("object_type");
			if (object_type == 1)// 团队情况
			{
				if (AdminCode.getCodeName("UM", object_id) != null && AdminCode.getCodeName("UM", object_id).length() > 0)
					name = e0122;
			}

			if (this.onlyname != null && !"a0101".equals(this.onlyname))
				myBean.set("onlyname",(String)bean.get(this.onlyname));
			myBean.set("b0110", AdminCode.getCodeName("UN",b0110));   //20141206 dengcan
			myBean.set("e01a1", AdminCode.getCodeName("@K",e01a1));   //20141206 dengcan
			myBean.set("objName", name);
			myBean.set("e0122", e0122);
			myBean.set("object_id", object_id);
			myBean.set("fz", DataCollectBo.roundAndRemoveZero(fzScore, 3));
			myBean.set("basicVal", DataCollectBo.roundAndRemoveZero(basicVal, 3));
			this.dataList.add(myBean);
		}
	}
	/**保留scal位小数并且去掉小数位后面的连续0*/
	public static String roundAndRemoveZero(String numStr,int scal)
	{
		if(numStr==null || numStr.trim().length()==0)
			numStr="0";
		
		numStr=PubFunc.round(numStr, scal);
		DecimalFormat RemoveZeroFormat = new DecimalFormat("########.########");		
		numStr=RemoveZeroFormat.format(Double.parseDouble(numStr));
		return numStr;
	}
	/** 取得录分情况下的分值 */
	public HashMap getFzScores()
	{

		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer();
		buf.append("select a.object_id,b.T_" + this.point + "_s score ");
		buf.append("from per_gather_" + this.planid);
		buf.append(" a join per_gather_score_" + this.planid + " b on a.gather_id=b.gather_id ");
		buf.append(" where 1=1 ");
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			buf.append(" and object_id in (");
			buf.append(this.getObjsPrivWhere());
			buf.append(")");
		}		
		// StringBuffer buf = new StringBuffer();
		// buf.append("select C_");
		// buf.append(this.point);
		// buf.append(" score,object_id from per_result_" + this.planid);
		// buf.append(" where object_id in (");
		// buf.append(this.getPrivEmpStr());
		// buf.append(")");

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			while (rs.next())
			{
				String score = rs.getString("score") == null ? "0.0" : rs.getString("score");
				String object_id = rs.getString("object_id") == null ? "objectid" : rs.getString("object_id");
				map.put(object_id, score);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 设置考核计划的考核方法 */
	public String getPlanMethod(String plan_id)
	{

		String method = "1";
		ContentDAO dao = new ContentDAO(this.cn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", plan_id);
		try
		{
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (vo.getString("method") != null)
			method = vo.getString("method");
		return method;
	}

	/**
	 * 取得考核计划信息
	 * 
	 * @param planid
	 * @return
	 */
	public RecordVo getPerPlanVo(String planid)
	{

		RecordVo vo = new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			vo.setInt("plan_id", Integer.parseInt(planid));
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 取得某计划下某考核指标的某考核对象的基本分 对于目标管理的计划取自p04表和人和指标有关
	 */
	public HashMap getBasicFen()
	{

		HashMap map = new HashMap();
		String method = this.planMethod;
		ArrayList objs = this.getKhObjs();
		if(objs.size()==0)
			return map;
		// method=2:目标管理考核计划:基本分取法[个性指标从p04中取得，共性指标首先判断p04表中有没有改考核对象的基本分有就优先从p04中取得，没有就从模板中取得]
		if (method != null && "2".equals(method) && !this.testPointIsCommon())// method=2:目标管理考核计划中个性指标
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < objs.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) objs.get(i);
				String object_id = (String) bean.get("object_id");
				buf.append(",'" + object_id + "'");
			}
			map = this.getBashFenMB(buf.substring(1));
		} else if (method != null && "2".equals(method) && this.testPointIsCommon())// method=2:目标管理考核计划中共性指标
		{
			// 共性指标首先判断p04表中有没有改考核对象的基本分有就优先从p04中取得，没有就从模板中取得
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < objs.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) objs.get(i);
				String object_id = (String) bean.get("object_id");
				buf.append(",'" + object_id + "'");
			}
			map = this.getBashFenMB(buf.substring(1));
			if (map.size() < objs.size())
			{// 对于从p04表中娶不到基本分的考核对象从模板中取得
				String basicFen360 = this.getBasicFen360();
				for (int i = 0; i < objs.size(); i++)
				{
					LazyDynaBean bean = (LazyDynaBean) objs.get(i);
					String object_id = (String) bean.get("object_id");
					if (map.get(object_id) == null)
						map.put(object_id, basicFen360);
				}
			}
		} else
		{
			String baseFen360 = this.getBasicFen360();// 360考核计划--基本分从模板中取得
			for (int i = 0; i < objs.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) objs.get(i);
				String object_id = (String) bean.get("object_id");
				map.put(object_id, baseFen360);
			}
		}
		return map;
	}

	/** 取得目标管理考核计划下某考核指标的某考核对象的基本分 */
	public HashMap getBashFenMB(String objStr)
	{

		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer();
		try
		{
			if ("2".equals(this.getPlanVo().getString("object_type")))
			{
				buf.append("select a0100,p0413 from p04 where plan_id=");
				buf.append(this.planid);
				buf.append(" and p0401='");
				buf.append(this.point);
				buf.append("' and a0100 in (");
				buf.append(objStr);
				buf.append(")");
			} else
			{
				buf.append("select b0110 a0100,p0413 from p04 where plan_id=");
				buf.append(this.planid);
				buf.append(" and p0401='");
				buf.append(this.point);
				buf.append("' and b0110 in (");
				buf.append(objStr);
				buf.append(")");
			}

		} catch (GeneralException e1)
		{
			e1.printStackTrace();
		}
		FieldItem item = DataDictionary.getFieldItem("p0413");
		int itemDecimal = item.getDecimalwidth();
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			while (rs.next())
			{
				String a0100 = rs.getString("a0100") == null ? "" : rs.getString("a0100");
				String p0413 = rs.getString("p0413") == null ? "" : rs.getString("p0413");
				p0413 = p0413.length()==0?"0":PubFunc.round(p0413,itemDecimal);
				map.put(a0100, p0413);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return map;
	}

	/** 取得360考核计划下某考核指标的某考核对象的基本分 */
	public String getBasicFen360()
	{

		String value = "0.0";
		StringBuffer buf = new StringBuffer();
		buf.append("select point_id,score from PER_TEMPLATE_POINT where item_id in ");
		buf.append("(select item_id from per_template_item where template_id=");
		buf.append("(select template_id from per_plan where plan_id=");
		buf.append(this.planid);
		buf.append(")) and point_id='");
		buf.append(this.point);
		buf.append("'");

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
			{
				String temp = rs.getString("score");
				if (temp == null)
					temp = "0.0";
				value = temp;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/** 判断某计划是否关联了业绩任务书 */
	public boolean isHaveTask()
	{

		boolean flag = true;
		StringBuffer buf = new StringBuffer();
		buf.append("select list.target_id from per_target_list list,per_plan pl ");
		buf.append("where list.theyear=pl.theyear and list.cycle=pl.cycle ");
		try
		{
			if ("2".equals(this.getPlanVo().getString("object_type")))
				buf.append(" and list.object_type=2 and pl.object_type=2 ");
			else
				// 团队业绩任务书要兼容 单位 部门 团队三种类型的考核计划
				buf.append(" and list.object_type=1 and pl.object_type!=2 ");

			buf.append(" and pl.plan_id=" + this.planid);
			ContentDAO dao = new ContentDAO(this.cn);

			RowSet rs = dao.search(buf.toString());
			if (!rs.next())
				flag = false;
		} catch (Exception e)
		{
			e.printStackTrace();

		}
		return flag;
	}

	/** 判断某指标是否被业绩任务书关联 */
	public boolean isHavePoint()
	{

		boolean flag = true;
		// 判断某任务书下是否存在某指标
		StringBuffer buf = new StringBuffer();
		buf.append("select * from per_target_point where target_id in ");
		buf.append("(select list.target_id from per_target_list list,per_plan pl ");
		buf.append("where list.theyear=pl.theyear and list.cycle=pl.cycle and ((pl.object_type=2 and list.object_type=2) or (pl.object_type!=2 and list.object_type!=2)) and pl.plan_id=");
		buf.append(this.planid);
		buf.append(") and point_id='" + this.point + "'");
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			if (!rs.next())
				flag = false;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	/** 判断某指标是否被业绩任务书关联 */
	public boolean isHavePoint(String pointid)
	{

		boolean flag = true;
		// 判断某任务书下是否存在某指标
		StringBuffer buf = new StringBuffer();
		buf.append("select * from per_target_point where target_id in ");
		buf.append("(select list.target_id from per_target_list list,per_plan pl ");
		buf.append("where list.theyear=pl.theyear and list.cycle=pl.cycle and ((pl.object_type=2 and list.object_type=2) or (pl.object_type!=2 and list.object_type!=2))  and pl.plan_id=");
		buf.append(this.planid);
		buf.append(") and point_id='" + pointid + "'");
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			if (!rs.next())
				flag = false;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	/** 取得某计划下某考核指标的某考核对象的标准分 */
	public String getStandardFen(String object_id) throws GeneralException
	{

		String value = "0.0";
		StringBuffer buf2 = new StringBuffer();
		buf2.append("select T_" + this.point);
		buf2.append(" from per_target_mx where target_id in ");
		buf2.append("(select list.target_id from per_target_list list,per_plan pl ");
		buf2.append("where list.theyear=pl.theyear and list.cycle=pl.cycle and pl.plan_id=");
		buf2.append(this.planid);
		buf2.append(") and object_id='");
		buf2.append(object_id);
		buf2.append("' and  kh_cyle=(select case cycle 	when 1 then thequarter	when 2 then thequarter	when 3 then themonth when 0 then '01' END ");
		buf2.append(" from per_plan where plan_id=");
		buf2.append(this.planid + ")");

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf2.toString());
			while (rs.next())// 只要取到一个非空的数值就行（如果同一个考核对象，对应多个标准值的时候）
			{
				String temp = rs.getString("T_" + this.point);
				if (temp == null)
					continue;
				else
				{
					value = temp;
					return value;
				}
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/** 一次取出某计划下某考核指标的所有权限范围内考核对象的标准分 */
	public HashMap getStandardFens() throws GeneralException
	{

		HashMap map = new HashMap();
		StringBuffer buf2 = new StringBuffer();
		buf2.append("select object_id,T_" + this.point);
		buf2.append(" from per_target_mx where target_id in ");
		buf2.append("(select list.target_id from per_target_list list,per_plan pl ");
		buf2.append("where list.theyear=pl.theyear and list.cycle=pl.cycle and pl.plan_id=");
		buf2.append(this.planid);
		buf2.append(")");
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			buf2.append(" and object_id in (");
			buf2.append(this.getObjsPrivWhere());
			buf2.append(")");
		}				
		buf2.append(" and  kh_cyle=(select case cycle 	when 1 then thequarter	when 2 then thequarter	when 3 then themonth when 0 then '01' END ");
		buf2.append(" from per_plan where plan_id=");
		buf2.append(this.planid + ")");

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf2.toString());
			while (rs.next())
			{
				String objectId = rs.getString("object_id");
				String value = rs.getString("T_" + this.point);
				if (map.get(objectId) != null)// 只要取到一个非空的数值就行（如果同一个考核对象，对应多个标准值的时候）
				{
					String temp = (String) map.get(objectId);
					if (!"0.0".equals(temp) && !"".equals(temp))
						continue;
				}
				if (value == null || (value != null && "".equals(value)))
					value = "0.0";
				map.put(objectId, value);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 基本指标非录分
	 */
	public String getTable1(String rule) throws GeneralException
	{

		StringBuffer html = new StringBuffer("<table id='tbl' class='ListTable'>");
		html.append("<thead><tr> ");

		int object_type = this.getPlanVo().getInt("object_type");
		if (object_type == 2)
		{
			FieldItem fielditem = DataDictionary.getFieldItem("B0110"); //20141206 DENGCAN
			html.append(getTh(fielditem.getItemdesc(), 2, 1, "a", "cell_Collect common_background_color common_border_color"));
			 
			fielditem = DataDictionary.getFieldItem("E0122");
			html.append(getTh(fielditem.getItemdesc(), 2, 1, "a", "cell_Collect common_background_color common_border_color"));
			
			fielditem = DataDictionary.getFieldItem("E01A1");   //20141206 DENGCAN
			html.append(getTh(fielditem.getItemdesc(), 2, 1, "a", "cell_Collect common_background_color common_border_color"));
			
			
			html.append(getTh(ResourceFactory.getProperty("gz.columns.a0101"), 2, 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
			{
				FieldItem item = DataDictionary.getFieldItem(this.onlyname);
				html.append(getTh(item.getItemdesc(), 2, 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
			}
		} else
		{
			html.append(getTh(ResourceFactory.getProperty("jx.datacol.khobj"), 2, 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
		}
		html.append(getTh1(this.getPointName(), 5, 2, "a", "header_Collectlocked common_background_color common_border_color"));
		if (rule != null && "3".equals(rule))// 排名的基本指标要加上名次列
			html.append(getTh(ResourceFactory.getProperty("jx.param.mingci"), 2, 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
		html.append(getTh(ResourceFactory.getProperty("jx.datacol.result"), 2, 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
		html.append("</tr> ");
		html.append("<tr>");
		html.append(getTh(ResourceFactory.getProperty("jx.datacol.standard"), 0, 0, "a", "header_Collectlocked common_background_color common_border_color"));
		html.append(getTh(ResourceFactory.getProperty("jx.datacol.pratical"), 0, 0, "a", "header_Collectlocked common_background_color common_border_color"));
		html.append(getTh(ResourceFactory.getProperty("jx.datacol.basic"), 0, 0, "a", "header_Collectlocked common_background_color common_border_color"));
		html.append(getTh(ResourceFactory.getProperty("jx.datacol.add"), 0, 0, "a", "header_Collectlocked common_background_color common_border_color"));
		html.append(getTh(ResourceFactory.getProperty("jx.datacol.deduc"), 0, 0, "a", "header_Collectlocked common_background_color common_border_color"));
		html.append("</tr></thead>");

		setData2(rule);
		return html.toString();
	}
	/**
	 * 
	 * @Title: getTh1   
	 * @Description:指标生成表头，带查看标度或者解释的功能
	 * @param @param name
	 * @param @param lays
	 * @param @param opt
	 * @param @param idname
	 * @param @param className
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	public String getTh1(String name, int lays, int opt, String idname, String className)
	{
		StringBuffer sb = new StringBuffer("");
		String pointname = "";
		String visible = "";
		try
		{
			String sql = "select pointname,visible from per_point where point_id='" + this.point + "'";
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("pointname");
				if (temp != null)
					pointname = temp;
				visible = rs.getString("visible");
			}
			sb.append("<td   class='" + className + "' valign='middle' align='center' ");
			if (idname != null && idname.length() > 0)
				sb.append(" id='" + idname + "' ");
			if (lays > 0)
			{
				if (opt == 1)
				{
					if("cell_Collect".equalsIgnoreCase(className))
						sb.append("rowspan='" + lays + "'");
					else
						sb.append("rowspan='" + lays + "'  width='" + columnWidth + "'");
				}
				else if (opt == 2)
					sb.append("colspan='" + lays + "'  height='22'");
			} else
			{
				if("cell_Collect".equalsIgnoreCase(className))
					sb.append(" height='22' ");
				else
					sb.append(" height='22'  width='" + columnWidth + "'");
			}
			
			String memo = name;
			if(name!=null && name.length()>25)
			{
				memo=memo!=null&&memo.length()>0?memo.replaceAll("\r\n","<br>"):"";
				memo=memo!=null&&memo.length()>0?memo.replaceAll(" ","<br>"):"";
				String text = memo;
				memo=memo.length()>25?memo.substring(0,25)+"...":memo;
				sb.append(" onmouseover=\"outContent('");
				sb.append(SafeCode.encode(text));
				sb.append("');\" ");
				sb.append(" style=\"word-break: break-all;word-wrap:break-word;\"");
			}
			
			if ("1".equals(visible))
				sb.append("  onclick=\"showDateSelectBox(this,'"+this.point+"');\"  onmouseout=\"UnTip();hiddenPanel()\" onmouseover=\"this.style.cursor='pointer'\" >");
			else if ("2".equals(visible))
				sb.append("  onclick=\"showDateSelectBox2(this,'"+this.point+"');\"  onmouseout=\"UnTip();hiddenPanel()\" onmouseover=\"this.style.cursor='pointer'\" >");
			
			sb.append("<a>"+memo+"</a>");
			sb.append("</td>");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
	/** 获得基本指标非录分的数据 */
	public void setData2(String rule) throws GeneralException
	{

		// ArrayList list = new ArrayList();
		this.dataList = new ArrayList();
		StringBuffer standardVals = new StringBuffer();
		StringBuffer praticalVals = new StringBuffer();
		boolean flag = false;

		// 得到某考核计划下的所有考核对象,即为数据的行数
		ArrayList objs = getKhObjs();

		/** 优化代码一次从数据库取出基本分和标准分，避免每个考核对象都从数据库取一次 */
		HashMap basicFenMap = this.getBasicFen();// 某计划下某考核指标的基本分,和对象无关，所有考核对象都一样所以从数据库中取一次就可以了
		HashMap standardFenMap = this.getStandardFens();
		HashMap gatherIds = this.getGatherIds();
		HashMap basicDFMap = this.getBasicDFs();
		HashMap praticalValsMap = this.getPraticalVals();

		for (int p = 0; p < objs.size(); p++)
		{
			LazyDynaBean myBean = new LazyDynaBean();
			LazyDynaBean bean = (LazyDynaBean) objs.get(p);
			String object_id = (String) bean.get("object_id");
			String name = (String) bean.get("a0101");
			String e0122 = (String) bean.get("e0122Name");
			
			String b0110 = (String) bean.get("b0110"); //20141206 dengcan
			String e01a1 = (String) bean.get("e01a1"); //20141206 dengcan

			int object_type = this.getPlanVo().getInt("object_type");
			if (object_type == 1)// 团队情况
			{
				if (AdminCode.getCodeName("UM", object_id) != null && AdminCode.getCodeName("UM", object_id).length() > 0)
					name = e0122;
			}

			/** 优化代码修改 */
			// String standVal = this.getStandardFen(object_id);
			// String basicVal = this.getBasicFen();
			// String praticalVal = this.getPraticalVal(object_id);
			String praticalVal = (String) praticalValsMap.get(object_id);
			praticalVal = (praticalVal == null ? "0" : praticalVal);
			String standVal = (String) standardFenMap.get(object_id);
			standVal = (standVal == null ? "0.0" : standVal);
			// HashMap map = this.getBasicVal(this.getGatherId(object_id));
			String gather_id = (String) gatherIds.get(object_id);
			gather_id = (gather_id == null ? "" : gather_id);
			HashMap map = this.getBasicVal(gather_id);
			String addScore = (String) map.get("addScore");
			String deducScore = (String) map.get("deducScore");
			// String dfScore = this.getBasicDF(object_id);
			String dfScore = (String) basicDFMap.get(object_id);
			dfScore = (dfScore == null ? "" : dfScore);

			if (!"".equals(praticalVal))
				flag = true;// 实际值保存过
			standardVals.append(object_id + "_standard=" + standVal + "<@>");
			praticalVals.append(object_id + "_pratical=" + ("".equals(praticalVal) ? "0" : praticalVal) + "<@>");

			if (this.onlyname != null && !"a0101".equals(this.onlyname))
				myBean.set("onlyname",(String)bean.get(this.onlyname));			
			myBean.set("b0110", AdminCode.getCodeName("UN",b0110));   //20141206 dengcan
			myBean.set("e01a1", AdminCode.getCodeName("@K",e01a1));   //20141206 dengcan
			myBean.set("e0122", e0122);
			myBean.set("objName", name);
			myBean.set("object_id", object_id);
			myBean.set("standard", DataCollectBo.roundAndRemoveZero(standVal, 3)); 
			myBean.set("pratical",DataCollectBo.roundAndRemoveZero(praticalVal, 3)); 
			String basicVal = (String) basicFenMap.get(object_id);
			basicVal = basicVal == null ? "" : basicVal;
			myBean.set("basic", DataCollectBo.roundAndRemoveZero(basicVal, 3)); 
			myBean.set("deduc", DataCollectBo.roundAndRemoveZero(deducScore, 3)); 
			myBean.set("add", DataCollectBo.roundAndRemoveZero(addScore, 3));
			if (rule != null && !"3".equals(rule) && "".equals(dfScore))// 非排名的基本指标得分默认为基本分
				dfScore = basicVal;
			myBean.set("df",DataCollectBo.roundAndRemoveZero(dfScore, 3));
			this.dataList.add(myBean);
		}
		// this.dataList = new ArrayList();
		if (rule != null && "3".equals(rule))// 排名的基本指标要加上名次列
		{
			String[] praticalScores = praticalVals.toString().split("<@>");
			String[] standardScores = standardVals.toString().split("<@>");
			String[] theScores = new String[objs.size()];// 需要排名的差值或者比例值的数组
			String pointctrl = this.getPointctrl();
			HashMap xmlmap = PointCtrlXmlBo.getAttributeValues(pointctrl);
			String type = (String) xmlmap.get("computeType");// type=0|1（差额|比例）
			int index = 0;
			for (int i = 0; i < praticalScores.length; i++)
			{
				String scoreStr = praticalScores[i];
				if (scoreStr == null || scoreStr.trim().length()==0)
					continue;
				String[] scoreArray = scoreStr.split("=");
				String temp = scoreArray[0];
				String pricVal = scoreArray[1];
				String[] tempArray = temp.split("_");
				String object_id = tempArray[0];

				scoreStr = standardScores[i];
				if (scoreStr == null)
					continue;
				scoreArray = scoreStr.split("=");
				temp = scoreArray[0];
				String standardVal = scoreArray[1];

				float theValue = 0;
				// if (type.equals("0"))// type=0|1（差额|比例）
				// theValue = Float.parseFloat(pricVal)
				// - Float.parseFloat(standardVal);
				// else if (type.equals("1"))
				// theValue = (Float.parseFloat(pricVal) - Float
				// .parseFloat(standardVal))
				// / Float.parseFloat(standardVal);
				// theScores[index++] = object_id + "="
				// + new Float(theValue).toString();

				// 程序规则修改了：对于排名的情况，都按照差额来排名
				theValue = Float.parseFloat(pricVal) - Float.parseFloat(standardVal);
				theScores[index++] = object_id + "=" + new Float(theValue).toString();
			}

			for (int i = 0; i < this.dataList.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) this.dataList.get(i);
				if (flag)// 如果实际值被保存过
				{
					String object_id = (String) bean.get("object_id");
					HashMap rank = this.getRank(object_id, theScores);
					Integer qrank = (Integer) rank.get("qrank");
					bean.set("rank", qrank.toString());
				} else
					bean.set("rank", "");
				// this.dataList.add(bean);
			}
		}
		// else
		// this.dataList = list;
	}

	/** 取得基本型指标得得分 */
	public String getBasicDF(String object_id)
	{

		String value = "";
		StringBuffer buf = new StringBuffer();
		buf.append("select C_" + this.point);
		buf.append(" score from per_result_" + this.planid);
		buf.append(" where object_id='");
		buf.append(object_id);
		buf.append("'");

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				value = rs.getString("score") == null ? value : rs.getString("score");
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/** 取得基本型指标所有管理权限范围内考核对象的得分 */
	public HashMap getBasicDFs()
	{

		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer();
		// buf.append("select object_id,C_" + this.point);
		// buf.append(" score from per_result_" + this.planid);
		// buf.append(" where object_id in (");
		// buf.append(this.getPrivEmpStr());
		// buf.append(")");
		buf.append("select a.object_id,b.T_" + this.point + "_s score ");
		buf.append("from per_gather_" + this.planid);
		buf.append(" a join per_gather_score_" + this.planid + " b on a.gather_id=b.gather_id ");
		buf.append(" where 1=1 ");
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			buf.append(" and object_id in (");
			buf.append(this.getObjsPrivWhere());
			buf.append(")");
		}
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			while (rs.next())
				map.put(rs.getString("object_id"), rs.getString("score") == null ? "" : rs.getString("score"));
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 取得基本型指标-加减分 */
	public HashMap getBasicVal(String gather_id)
	{

		HashMap map = new HashMap();
		String addvalue = "";
		String deduvalue = "";
		StringBuffer buf = new StringBuffer();
		buf.append("select T_" + this.point + "_A addf,T_" + this.point + "_M deduf");
		buf.append(" from per_gather_score_" + this.planid);
		buf.append(" where gather_id='");
		buf.append(gather_id);
		buf.append("'");

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
			{
				addvalue = rs.getString("addf") == null ? addvalue : rs.getString("addf");
				deduvalue = rs.getString("deduf") == null ? deduvalue : rs.getString("deduf");
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		map.put("addScore", addvalue);
		map.put("deducScore", deduvalue);
		return map;
	}

	/** 取得基本型指标得实际值 */
	public String getPraticalVal(String object_id)
	{

		String value = "0";
		StringBuffer buf = new StringBuffer();
		buf.append("select T_" + this.point);
		buf.append(" score from per_gather_" + this.planid);
		buf.append(" where object_id='");
		buf.append(object_id);
		buf.append("'");

		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				value = rs.getString("score") == null ? value : rs.getString("score");
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/** 取得管理权限范围内所有考核对象的基本型指标得实际值 */
	public HashMap getPraticalVals()
	{

		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer();
		buf.append("select object_id,T_" + this.point);
		buf.append(" score from per_gather_" + this.planid);
		buf.append(" where 1=1 ");
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			buf.append(" and object_id in (");
			buf.append(this.getObjsPrivWhere());
			buf.append(")");
		}
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			RowSet rs = dao.search(buf.toString());
			while (rs.next())
			{
				String value = rs.getString("score");
				value = (value == null || (value != null && "".equals(value))) ? "0" : value;
				map.put(rs.getString("object_id"), value);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 基本指标计算规则为排名时候获得名次[包括前几名和后几名]
	 * 
	 * @param scores
	 *        object1=score<@>object2=score<@>
	 */
	public HashMap getRank(String objectId, String[] scores)
	{

		HashSet set = new HashSet();
		HashMap map = new HashMap();
		ArrayList list = new ArrayList();
		for (int i = 0; i < scores.length; i++)
		{
			String scoreStr1 = scores[i];
			if(scoreStr1!=null && scoreStr1.trim().length()>0)
			{							
				String[] scoreArray = scoreStr1.split("=");
				String object = scoreArray[0];
				Float value = new Float(scoreArray[1]);
				set.add(value);// 先把分数放到set里面，目的是为了去掉并列的分数
				map.put(object, value);
			}
		}
		Iterator it = set.iterator();
		while (it.hasNext())
		{
			Float temp = (Float) it.next();
			list.add(temp);// 再发分数从set里面转移到list里面，目的是为了对不重复的分数进行排名
		}
		Collections.sort(list);// 从小到大排名分数
		Float objVal = (Float) map.get(objectId);
		int qrank = 0;// 前几名
		int hrank = 0;// 后几名
		for (int i = 0; i < list.size(); i++)
		{
			Float theValue = (Float) list.get(i);
			if (objVal.floatValue() == theValue.floatValue())
			{
				hrank = (i + 1) * (-1);
				qrank = list.size() - i;
				break;
			}
		}
		HashMap rank = new HashMap();
		rank.put("qrank", Integer.valueOf(qrank));
		rank.put("hrank", Integer.valueOf(hrank));
		return rank;
	}

	/** 基本指标计分规则 */
	public String getRule()
	{

		if ("".equals(this.point))
			return "";
		String pointctrl = this.getPointctrl();
		HashMap xmlmap = PointCtrlXmlBo.getAttributeValues(pointctrl);
		String rule = (String) xmlmap.get("computeRule");// rule=0|1|2|3
		// 计分规则（录分｜简单｜分段｜排名）
		return rule;
	}

	/** 基本指标计算 */
	public HashMap basciPointCalcu(String pratical, String basic, String standard) throws GeneralException
	{

		HashMap map = new HashMap();
		String pointctrl = this.getPointctrl();
		HashMap xmlmap = PointCtrlXmlBo.getAttributeValues(pointctrl);
		String rule = (String) xmlmap.get("computeRule");// rule=0|1|2|3
		// 计分规则（录分｜简单｜分段｜排名）
		String type = (String) xmlmap.get("computeType");// type=0|1（差额|比例）

		if(pratical!=null && pratical.length()>0 && pratical.indexOf("E")==-1)
		{
			if (!this.isDataType("N", pratical))
			{
				String msg = "源数据:" + pratical + " 不符合格式,必须输入数值型数据！";
				throw new GeneralException(msg);
			}					
		}
		double pratiVal = Double.parseDouble(pratical);
		double basicVal = Double.parseDouble(basic);
		double standardVal = Double.parseDouble(standard);
		double addVal = 0;
		double deducVal = 0;

		if ("1".equals(rule))// 简单
		{
			String addValid = (String) xmlmap.get("addValid");
			String minusValid = (String) xmlmap.get("minusValid");
			String addType = (String) xmlmap.get("addType");
			String minusType = (String) xmlmap.get("minusType");
			String addValue = (String) xmlmap.get("addValue");
			String minusValue = (String) xmlmap.get("minusValue");
			String addScore = (String) xmlmap.get("addScore");
			String minusScore = (String) xmlmap.get("minusScore");
			if ("0".equals(type))// 差额
			{
				if (pratiVal > standardVal)// 实际大于标准
				{
					if (addValid != null && "1".equals(addValid))
					{
						if (addType != null && "1".equals(addType))// 每高
						{
							if (addValue != null && !"".equals(addValue) && addScore != null && !"".equals(addScore) && Double.parseDouble(PubFunc.subtract(String.valueOf(pratiVal), String.valueOf(standardVal), 4)) >= Double.parseDouble(addValue)
									&& Double.parseDouble(addValue) != 0)
								// addVal = Math.floor((pratiVal - standardVal)
								// / Double.parseDouble(addValue)) *
								// (Double.parseDouble(addScore));
								addVal = (pratiVal - standardVal) / Double.parseDouble(addValue) * (Double.parseDouble(addScore));// 不用取整了
						}
					}
					if (minusValid != null && "1".equals(minusValid))
					{
						if (minusType != null && "1".equals(minusType))// 每高
						{
							if (minusValue != null && !"".equals(minusValue) && minusScore != null && !"".equals(minusScore) && Double.parseDouble(PubFunc.subtract(String.valueOf(pratiVal), String.valueOf(standardVal), 4)) >= Double.parseDouble(minusValue)
									&& Double.parseDouble(minusValue) != 0)
								// deducVal = Math.floor((pratiVal -
								// standardVal) /
								// Double.parseDouble(minusValue)) *
								// (Double.parseDouble(minusScore));
								deducVal = (pratiVal - standardVal) / Double.parseDouble(minusValue) * (Double.parseDouble(minusScore));// 不用取整了
						}
					}
				} else
				{
					if (addValid != null && "1".equals(addValid))
					{
						if (addType != null && "0".equals(addType))// 每低
						{
							if (addValue != null && !"".equals(addValue) && addScore != null && !"".equals(addScore) && Double.parseDouble(PubFunc.subtract(String.valueOf(standardVal), String.valueOf(pratiVal), 4)) >= Double.parseDouble(addValue)
									&& Double.parseDouble(addValue) != 0)
								// addVal = Math.floor((standardVal - pratiVal)
								// / Double.parseDouble(addValue)) *
								// (Double.parseDouble(addScore));
								addVal = (standardVal - pratiVal) / Double.parseDouble(addValue) * (Double.parseDouble(addScore));// 不用取整了
						}
					}
					if (minusValid != null && "1".equals(minusValid))
					{
						if (minusType != null && "0".equals(minusType))// 每低
						{
							
						//	System.out.println(standardVal - pratiVal);
						//	System.out.println(PubFunc.subtract(String.valueOf(standardVal), String.valueOf(pratiVal), 4));
							
							if (minusValue != null && !"".equals(minusValue) && minusScore != null && !"".equals(minusScore) && Double.parseDouble(PubFunc.subtract(String.valueOf(standardVal), String.valueOf(pratiVal), 4)) >= Double.parseDouble(minusValue)
									&& Double.parseDouble(minusValue) != 0)
								// deducVal = Math.floor((standardVal -
								// pratiVal) / Double.parseDouble(minusValue)) *
								// (Double.parseDouble(minusScore));
								deducVal = (standardVal - pratiVal) / Double.parseDouble(minusValue) * (Double.parseDouble(minusScore));// 不用取整了
						}
					}
				}
			} else if ("1".equals(type))// 比例
			{
				if (pratiVal > standardVal)// 实际大于标准
				{
					if (addValid != null && "1".equals(addValid))
					{
						if (addType != null && "1".equals(addType))// 每高
						{
							if (addValue != null && !"".equals(addValue) && addScore != null && !"".equals(addScore) && standardVal != 0
									&& (Double.parseDouble(PubFunc.subtract(String.valueOf(pratiVal), String.valueOf(standardVal), 4)) / standardVal) >= Double.parseDouble(addValue) && Double.parseDouble(addValue) != 0)
								// addVal = Math.floor(((pratiVal - standardVal)
								// / standardVal) /
								// Double.parseDouble(addValue)) *
								// (Double.parseDouble(addScore));
								addVal = (Double.parseDouble(PubFunc.subtract(String.valueOf(pratiVal), String.valueOf(standardVal), 4)) / standardVal) / Double.parseDouble(addValue) * (Double.parseDouble(addScore));// 不用取整了
						}
					}
					if (minusValid != null && "1".equals(minusValid))
					{
						if (minusType != null && "1".equals(minusType))// 每低
						{
							if (minusValue != null && !"".equals(minusValue) && minusScore != null && !"".equals(minusScore) && standardVal != 0
									&& (Double.parseDouble(PubFunc.subtract(String.valueOf(pratiVal), String.valueOf(standardVal), 4)) / standardVal) >= Double.parseDouble(minusValue) && Double.parseDouble(minusValue) != 0)
								// deducVal = Math.floor(((pratiVal -
								// standardVal) / standardVal) /
								// Double.parseDouble(minusValue)) *
								// (Double.parseDouble(minusScore));
								deducVal = (Double.parseDouble(PubFunc.subtract(String.valueOf(pratiVal), String.valueOf(standardVal), 4)) / standardVal) / Double.parseDouble(minusValue) * (Double.parseDouble(minusScore));// 不用取整了

						}
					}
				} else
				{
					if (addValid != null && "1".equals(addValid))
					{
						if (addType != null && "0".equals(addType))// 每高
						{
							if (addValue != null && !"".equals(addValue) && addScore != null && !"".equals(addScore) && standardVal != 0
									&& (Double.parseDouble(PubFunc.subtract(String.valueOf(standardVal), String.valueOf(pratiVal), 4)) / standardVal) >= Double.parseDouble(addValue) && Double.parseDouble(addValue) != 0)
								// addVal = Math.floor(((standardVal - pratiVal)
								// / standardVal) /
								// Double.parseDouble(addValue)) *
								// (Double.parseDouble(addScore));
								addVal = (Double.parseDouble(PubFunc.subtract(String.valueOf(standardVal), String.valueOf(pratiVal), 4)) / standardVal) / Double.parseDouble(addValue) * (Double.parseDouble(addScore));// 不用取整了
						}
					}
					if (minusValid != null && "1".equals(minusValid))
					{
						if (minusType != null && "0".equals(minusType))// 每低
						{
							if (minusValue != null && !"".equals(minusValue) && minusScore != null && !"".equals(minusScore) && standardVal != 0
									&& (Double.parseDouble(PubFunc.subtract(String.valueOf(standardVal), String.valueOf(pratiVal), 4)) / standardVal) >= Double.parseDouble(minusValue) && Double.parseDouble(minusValue) != 0)
								// deducVal = Math.floor(((standardVal -
								// pratiVal) / standardVal) /
								// Double.parseDouble(minusValue)) *
								// (Double.parseDouble(minusScore));
								deducVal = (Double.parseDouble(PubFunc.subtract(String.valueOf(standardVal), String.valueOf(pratiVal), 4)) / standardVal) / Double.parseDouble(minusValue) * (Double.parseDouble(minusScore));
						}
					}
				}
			}
		} else if ("2".equals(rule))// 分段
		{
			if ("0".equals(type))// 差额
			{
				double value = getValueByFD(pratiVal - standardVal);
				if (value > 0)// 正负分分别对应加减分
					addVal = value;
				else
					deducVal = value * (-1);
			} else if ("1".equals(type) && standardVal != 0)// 比例
			{
				double value = getValueByFD(pratiVal / standardVal);
				if (value > 0)// 正负分分别对应加减分
					addVal = value;
				else
					deducVal = value * (-1);
			}
		}
		double df = basicVal + addVal - deducVal;
		if (df < 0 && basicVal > 0)// 如果基本分在模板中录入了正数，那么得分最小为0
			df = 0;
		map.put("addF", DataCollectBo.roundAndRemoveZero(Double.valueOf(addVal).toString(), 3));
		map.put("deducF", DataCollectBo.roundAndRemoveZero(Double.valueOf(deducVal).toString(), 3));
		map.put("objDF",DataCollectBo.roundAndRemoveZero(Double.valueOf(df).toString(), 3));
		return map;
	}

	/** 取到某一个分段对应的分值 */
	public double getValueByFD(double theVal)
	{

		double value = 0;
		String sql = "select score from per_standard_item where point_id='" + this.point + "' and top_value>=" + Double.valueOf(theVal).toString() + " and bottom_value<="
				+ Double.valueOf(theVal).toString();
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String temp = rs.getString("score");
				if (temp != null)
					value = Double.parseDouble(temp);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/*
	 * 加减指标
	 */
	public String getTable2(String type) throws GeneralException
	{

		StringBuffer html = new StringBuffer();
		// ///////////////////////////////写表头///////////////////////////////
		String maxlay = this.getMaxLay();
		if ("0".equals(maxlay))
			throw new GeneralException("没有为指标[" + this.getPointName() + "]定义规则！");
		html.append("<table id='tbl'  class='ListTable'><thead>");// style='position:absolute;left:5;'
		ArrayList nextLayChild = new ArrayList();
		for (int i = 1; i <= Integer.parseInt(maxlay); i++)
		{
			html.append("<tr>");
			ArrayList list = null;
			if (i == 1)// 最上层
			{
				int object_type = this.getPlanVo().getInt("object_type");
				if (object_type != 2)
				{
					html.append(getTh(ResourceFactory.getProperty("jx.datacol.khobj"), Integer.parseInt(maxlay), 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
				} else
				{
					FieldItem fielditem = DataDictionary.getFieldItem("B0110");					
					html.append(getTh(fielditem.getItemdesc(), Integer.parseInt(maxlay), 1, "a", "cell_Collect common_background_color common_border_color"));
					
					fielditem = DataDictionary.getFieldItem("E0122");					
					html.append(getTh(fielditem.getItemdesc(), Integer.parseInt(maxlay), 1, "a", "cell_Collect common_background_color common_border_color"));
					
					fielditem = DataDictionary.getFieldItem("E01A1");					
					html.append(getTh(fielditem.getItemdesc(), Integer.parseInt(maxlay), 1, "a", "cell_Collect common_background_color common_border_color"));
					
					
					html.append(getTh(ResourceFactory.getProperty("gz.columns.a0101"), Integer.parseInt(maxlay), 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
					if (this.onlyname != null && !"a0101".equals(this.onlyname))
					{
						FieldItem item = DataDictionary.getFieldItem(this.onlyname);
						html.append(getTh(item.getItemdesc(), Integer.parseInt(maxlay), 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
					}
				}

				list = getChildren("");
				for (int j = 0; j < list.size(); j++)
				{
					String itemid = (String) list.get(j);
					// 得到最后一代子孙的个数就是要占的列数
					int colspan = getLastChildrenCount(itemid);
					// 得到它的直接孩子存起来作为下一层用
					ArrayList theChildren = this.getChildren(itemid);
					for (int k = 0; k < theChildren.size(); k++)
					{
						String temp = (String) theChildren.get(k);
						nextLayChild.add(temp);
					}
					String childCount = getChildCount(itemid);
					int rowspan = 0;
					if ("0".equals(childCount) && i < Integer.parseInt(maxlay))// 没有下级节点并且不是最后一层要跨行
						rowspan = Integer.parseInt(maxlay) - i + 1;

					String itemdesc = getItemDesc(itemid);
					int lays = 0;
					int opt = 0; // 1-rowspan , 2-colspan
					if (rowspan > 1)// 跨行情况
					{
						lays = rowspan;
						opt = 1;
					} else if (colspan > 1)
					{
						lays = colspan;
						opt = 2;
					}
					html.append(getTh(itemdesc, lays, opt, "a", "header_Collectlocked common_background_color common_border_color"));
				}
				html.append(getTh(ResourceFactory.getProperty("jx.datacol.basic"), Integer.parseInt(maxlay), 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
				if (type != null && "1".equals(type))// 1-加分 2-扣分
					html.append(getTh(ResourceFactory.getProperty("jx.datacol.add"), Integer.parseInt(maxlay), 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
				else if (type != null && "2".equals(type))
					html.append(getTh(ResourceFactory.getProperty("jx.datacol.deduc"), Integer.parseInt(maxlay), 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
				html.append(getTh(ResourceFactory.getProperty("jx.datacol.result"), Integer.parseInt(maxlay), 1, "a", "cell_Collectlocked2 common_background_color common_border_color"));
			} else
			{
				list = nextLayChild;
				nextLayChild = new ArrayList();
				for (int j = 0; j < list.size(); j++)
				{
					String itemid = (String) list.get(j);
					// 得到最后一代子孙的个数就是要占的列数
					int colspan = getLastChildrenCount(itemid);
					// 得到它的直接孩子存起来作为下一层用
					ArrayList theChildren = this.getChildren(itemid);
					for (int k = 0; k < theChildren.size(); k++)
					{
						String temp = (String) theChildren.get(k);
						nextLayChild.add(temp);
					}
					String childCount = getChildCount(itemid);
					int rowspan = 0;
					if ("0".equals(childCount) && i < Integer.parseInt(maxlay))// 没有下级节点并且不是最后一层要跨行
						rowspan = Integer.parseInt(maxlay) - i + 1;

					String itemdesc = getItemDesc(itemid);
					int lays = 0;
					int opt = 0; // 1-rowspan , 2-colspan
					if (rowspan > 1)// 跨行情况
					{
						lays = rowspan;
						opt = 1;
					} else if (colspan > 1)
					{
						lays = colspan;
						opt = 2;
					}
					html.append(getTh(itemdesc, lays, opt, "a", "header_Collectlocked common_background_color common_border_color"));
				}

			}
			html.append("</tr>");
		}
		html.append("</thead>");
		setData1(type);
		return html.toString();
	}

	/**
	 * 获得加扣分的数据
	 * 
	 * @throws GeneralException
	 */
	public void setData1(String type) throws GeneralException
	{

		this.dataList = new ArrayList();

		// /////////////////////////////写数据开始/////////////////////////////////////////
		// 得到最低层的孩子,即为数据的列数
		ArrayList items = this.getAllItems();

		// 得到某考核计划下的所有考核对象,即为数据的行数
		ArrayList objs = getKhObjs();
		/** 代码优化修改 */
		HashMap sValsMap = this.getSvalues();// 一次性获得所有考核对象的所有项目的分值
		HashMap basicFenMap = this.getBasicFen();
		HashMap dfValsMap = this.getDFvalues();

		for (int p = 0; p < objs.size(); p++)
		{
			StringBuffer scoresStr = new StringBuffer();
			LazyDynaBean myBean = new LazyDynaBean();
			LazyDynaBean bean = (LazyDynaBean) objs.get(p);
			String object_id = (String) bean.get("object_id");
			String name = (String) bean.get("a0101");
			String e0122 = (String) bean.get("e0122Name");
			String b0110 = (String) bean.get("b0110"); //20141206 dengcan
			String e01a1 = (String) bean.get("e01a1"); //20141206 dengcan
			
			int object_type = this.getPlanVo().getInt("object_type");
			if (object_type == 1)// 团队情况
			{
				if (AdminCode.getCodeName("UM", object_id) != null && AdminCode.getCodeName("UM", object_id).length() > 0)
					name = e0122;
			}
			
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
				myBean.set("onlyname",(String)bean.get(this.onlyname));
			myBean.set("b0110", AdminCode.getCodeName("UN",b0110));   //20141206 dengcan
			myBean.set("e01a1", AdminCode.getCodeName("@K",e01a1));   //20141206 dengcan
			myBean.set("e0122", e0122);
			myBean.set("objName", name);
			myBean.set("object_id", object_id);
			for (int x = 0; x < items.size(); x++)
			{
				LazyDynaBean bean2 = (LazyDynaBean) items.get(x);
				String item = (String) bean2.get("item");
				// String s_value = getSvalue(object_id, item);
				String s_value = (String) sValsMap.get(object_id + ":" + item);
				s_value = (s_value == null || (s_value != null && "".equals(s_value))) ? "0" : s_value;
				scoresStr.append(object_id + "_" + item + "=" + s_value + "<@>");// 00000059_9=10<@>00000059_10=5
				myBean.set(item, DataCollectBo.roundAndRemoveZero(s_value, 3));
			}
			// String df_value = getDFvalue(object_id);
			String df_value = (String) dfValsMap.get(object_id);
			df_value = (df_value == null || (df_value != null && "".equals(df_value))) ? "0.00" : df_value; // 后面程序没有用 用计算后的得分了

			String basic_value = (String) basicFenMap.get(object_id);
			basic_value = basic_value == null ? "" : basic_value;
			myBean.set("basicf", DataCollectBo.roundAndRemoveZero(basic_value, 3));

			float theScore = 0;
			HashMap map = this.computDF(object_id, scoresStr.toString().split("<@>"));// 计算加扣分指标的加分或者扣分，不能反算啊
			String cz = (String) map.get("cz");
			theScore = Float.parseFloat("".equals(cz) ? "0" : cz);
			myBean.set("cz", DataCollectBo.roundAndRemoveZero(Float.toString(theScore), 3));
			String df = (String) map.get("df");
			theScore = Float.parseFloat("".equals(df) ? "0" : df);
			myBean.set("df", DataCollectBo.roundAndRemoveZero(Float.toString(theScore), 3)); // 关于得分 2010-07-02改

			// myBean.set("df", moveZero(Double.parseDouble(df_value) == Double
			// .parseDouble("0.00") ? basic_value : PubFunc.round(
			// df_value, 2)));// 如果得分为空就让得分等于基本值，这个时候是既不加也不减
			// myBean.set("df", moveZero(Double.parseDouble(df_value) == Double
			// .parseDouble("0.00") ? "" : PubFunc.round(
			// df_value, 2)));//得分还是显示得分吧 说不清了 --2010-04-13改
			this.dataList.add(myBean);
		}

	}

	/* 取得业绩数据 */
	public String getDFvalue(String object_id)
	{

		String value = "";
		String gather_id = this.getGatherId(object_id);
		if ("".equals(gather_id))
			return value;
		StringBuffer buf = new StringBuffer("select ");

		String type = this.getTypeOfPoint();
		String col = "";
		if ("1".equals(type))// 加分指标
			col = "T_" + this.point + "_A";
		else if ("2".equals(type))// 减分指标
			col = "T_" + this.point + "_M";
		buf.append(col);
		buf.append(" from per_gather_score_" + this.planid);
		buf.append(" where gather_id=" + gather_id);

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				value = rs.getString(col) != null ? rs.getString(col) : "";
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/* 取得权限范围内所有考核对象的得分 */
	public HashMap getDFvalues()
	{

		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer("select b.object_id,a.T_" + this.point + "_s score");

		// String type = this.getTypeOfPoint();
		// String col = "";
		// if (type.equals("1"))// 加分指标
		// col = "T_" + this.point + "_A";
		// else if (type.equals("2"))// 减分指标
		// col = "T_" + this.point + "_M";
		// buf.append(col);
		buf.append(" from per_gather_score_" + this.planid);
		buf.append(" a,per_gather_" + this.planid);
		buf.append(" b where a.gather_id=b.gather_id ");
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			buf.append(" and b.object_id in (");
			buf.append(this.getObjsPrivWhere());
			buf.append(")");
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			while (rs.next())
			{
				String object_id = rs.getString("object_id") != null ? rs.getString("object_id") : "objectid";
				String value = rs.getString("score") != null ? rs.getString("score") : "";
				map.put(object_id, value);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/* 取得加扣分指标的得分 */
	public String getDFvalue2(String object_id)
	{

		String value = null;
		String gather_id = this.getGatherId(object_id);
		if ("".equals(gather_id))
			return value;
		StringBuffer buf = new StringBuffer("select T_" + this.point + "_s score");

		// String type = this.getTypeOfPoint();
		// String col = "";
		// if (type.equals("1"))// 加分指标
		// col = "T_" + this.point + "_A";
		// else if (type.equals("2"))// 减分指标
		// col = "T_" + this.point + "_M";
		// buf.append(col);
		buf.append(" from per_gather_score_" + this.planid);
		buf.append(" where gather_id=" + gather_id);

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
				value = rs.getString("score");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/* 取得业绩数据 */
	public String getSvalue(String object_id, String item)
	{

		String value = "";
		String gather_id = this.getGatherId(object_id);
		if ("".equals(gather_id))
			return value;
		String sql = "select s_value from per_gather where item_id='" + item + "' and gather_id=" + gather_id;
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				value = rs.getString("s_value") != null ? rs.getString("s_value") : "";
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}

	/* 取得权限范围内所有考核对象的所有考核项目的业绩数据 */
	public HashMap getSvalues()
	{

		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer();
		buf.append("select per_gather.item_id,per_gather.s_value,a.object_id from per_gather,per_gather_");
		buf.append(this.planid);
		buf.append(" a where per_gather.gather_id=a.gather_id ");
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			buf.append(" and a.object_id in (");
			buf.append(this.getObjsPrivWhere());
			buf.append(")");
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(buf.toString());
			while (rs.next())
			{
				String value = rs.getString("s_value") != null ? rs.getString("s_value") : "";
				String item_id = rs.getString("item_id") != null ? rs.getString("item_id") : "itemid";
				String object_id = rs.getString("object_id") != null ? rs.getString("object_id") : "objid";
				map.put(object_id + ":" + item_id, value);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	public int getLastChildrenCount(String itemid)
	{

		int count = 0;

		ArrayList list = getChildren(itemid);
		for (int i = 0; i < list.size(); i++)
		{
			String temp = (String) list.get(i);
			if ("0".equals(getChildCount(temp)))
				count++;
			else
				count += getLastChildrenCount(temp);
		}
		return count;
	}

	public String getChild(String item_id)
	{

		String child = "none";
		String sql = "select item_id from per_standard_item where parent_id=" + item_id + " and point_id='" + this.point + "' order by seq";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				child = rs.getString("item_id");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return child;
	}

	/** 取得直接孩子 */
	public ArrayList getChildren(String item_id)
	{

		ArrayList children = new ArrayList();
		String sql = "";
		if ("".equals(item_id))
			sql = "select item_id from per_standard_item where parent_id is null and point_id='" + this.point + "' order by seq";
		else
			sql = "select item_id from per_standard_item where parent_id=" + item_id + " and point_id='" + this.point + "' order by seq";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			while (rs.next())
				children.add(rs.getString("item_id"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return children;
	}

	/** 得到层数 */
	public String getMaxLay()
	{

		String maxlay = "0";
		String sql = "select item_id from per_standard_item where parent_id is null and point_id='" + this.point + "' order by seq";
		ArrayList list = new ArrayList();
		ArrayList lays = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);

			while (rs.next())
			{
				String itemid = rs.getString("item_id");
				list.add(itemid);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				String item_id = (String) list.get(i);
				int x = 1;
				String temp = getChild(item_id);
				while (!"none".equals(temp))
				{
					x++;
					temp = getChild(temp);
				}
				lays.add(new Integer(x));
			}
		}
		if (lays.size() > 0)
		{
			Collections.sort(lays);
			maxlay = ((Integer) lays.get(lays.size() - 1)).toString();
		}
		return maxlay;
	}

	/* 获得孩子的个数 */
	public String getChildCount(String item_id)
	{

		String count = "";
		String sql = "select count(item_id) n from per_standard_item where parent_id=" + item_id + " and point_id='" + this.point + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				count = rs.getString("n");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}

	/* 取名称 */
	public String getItemDesc(String item_id)
	{

		String itemdesc = "";
		String sql = "select itemdesc from per_standard_item where item_id=" + item_id + " and point_id='" + this.point + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				itemdesc = rs.getString("itemdesc") == null ? "" : rs.getString("itemdesc");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return itemdesc;
	}

	public HashMap getItemsMap()
	{

		HashMap map = new HashMap();
		String sql = "select item_id,itemdesc from per_standard_item where  point_id='" + this.point + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				String itemdesc = rs.getString("itemdesc") == null ? "" : rs.getString("itemdesc");
				map.put(rs.getString("item_id"), itemdesc);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/* 得到某项目最下层项目 */
	public ArrayList getItems(String item_id)
	{

		ArrayList list = new ArrayList();
		ArrayList children = this.getChildren(item_id);
		for (int i = 0; i < children.size(); i++)
		{
			String itemId = (String) children.get(i);
			if ("none".equals(this.getChild(itemId)))
				list.add(itemId);
			else
			{
				ArrayList list1 = this.getItems(itemId);// 递归
				for (int j = 0; j < list1.size(); j++)
					list.add((String) list1.get(j));
			}
		}
		if (children.size() == 0)
			list.add(item_id);
		return list;
	}

	/* 得到所有最下层项目 */
	public ArrayList getAllItems()
	{

		ArrayList topItems = getChildren("");
		ArrayList items = new ArrayList();
		for (int k = 0; k < topItems.size(); k++)
		{
			String item = (String) topItems.get(k);
			ArrayList temp = getItems(item);
			for (int m = 0; m < temp.size(); m++)
			{
				LazyDynaBean bean = new LazyDynaBean();
				String itemChild = (String) temp.get(m);
				bean.set("item", itemChild);
				items.add(bean);
			}
		}
		return items;
	}

	/**
	 * 获得某考核计划的权限范围内的考核对象的条件子句 包括管理范围和操作单位都有考虑
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getObjsPrivWhere()
	{
		StringBuffer sql = new StringBuffer();
		RecordVo planVo = this.getPerPlanVo(this.planid);
		String object_type = planVo.getString("object_type");
		String method = planVo.getString("method");

		String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
		String privCode = this.userview.getManagePrivCode() + this.userview.getManagePrivCodeValue();// 管理范围

		if ((operOrg==null || "".equals(operOrg.trim())) && (privCode==null || "".equals(privCode.trim())))// 没有组织机构的权限
			return "'noObj'";
		if (method != null && "2".equals(method))// 目标管理计划
		{
			String targetCollectItem = this.getTargetCollectItems(this.planid);// 先检测有没有设置目标卡采集指标
			if ("".equals(targetCollectItem) && getPointList(this.planid).size() == 0)
				return "'noObj'";
			// 判断有没有权限范围内的考核对象
			// 对于考核对象的限制这样处理：不管考核对象是什么类型，先取操作单位，如果取不出来就按管理范围走

			if ("".equals(targetCollectItem))// 没有设置目标卡采集指标的目标计划
			{				
				if (operOrg!=null && operOrg.trim().length() > 3)
				// 按照操作单位范围走
				{
					if ("2".equals(object_type))// 人员
					{
						StringBuffer buf = new StringBuffer();
						buf.append("select object_id from per_object where plan_id=" + this.planid);
						
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int i = 0; i < temp.length; i++)
						{

							if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
								tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
							else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
								tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");

						
							buf.append(" and ( " + tempSql.substring(3) + " ) ");
						}
						sql.append(buf.toString());
					} else
					// 非人员
					{
						StringBuffer buf = new StringBuffer();
						buf.append("select object_id from per_object where plan_id=" + planid);
						
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int i = 0; i < temp.length; i++)
						{
							tempSql.append(" or object_id like '" + temp[i].substring(2) + "%'");
						}
						buf.append(" and ( " + tempSql.substring(3) + " ) ");
						
						sql.append(buf.toString());
					}
				}
				else if((!this.userview.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{
					if ("2".equals(object_type))// 人员
					{
						sql.append(this.getPrivEmpStr());
					} else
					// 非人员
					{
						StringBuffer buf = new StringBuffer();
						buf.append("select object_id from per_object where plan_id=" + this.planid);

						if (!this.userview.isSuper_admin())
						{
							if (privCode != null && privCode.trim().length() > 0)
							{
								String codesetid = privCode.substring(0, 2);
								String value = privCode.substring(2);
								if (value.length() > 0)
								{
									buf.append(" and object_id like '");
									buf.append(value);
									buf.append("%' ");
								}
							}else
								buf.append(" and 1=2 ");
						}
						sql.append(buf.toString());
					}
				}
			} else
			// 设置了目标卡采集指标的目标计划 由于界面都不一样了 所以不在此取考核对象 参照SearchDataCollectTrans.java 可以看出
			{
				// if (operOrg.trim().equals(""))// 按照管理范围走
				// {
				// if (object_type.equals("2"))// 人员
				// {
				// sql.append("select a0100 from p04 where plan_id=" + planid+ " and a0100 in (" + this.getPrivEmpStr() + ")");
				// } else
				// // 非人员
				// {
				// if (!isExistTeamKhObj(plan_id, "p04"))
				// continue;
				// }
				// } else
				// // 按照操作单位范围走
				// {
				// if (object_type.equals("2"))// 人员
				// {
				// if (!isExistPrivEmps2(plan_id, "p04"))
				// continue;
				// } else
				// // 非人员
				// {
				// if (!isExistTeamKhObj2(plan_id, "p04"))
				// continue;
				// }
				// }
			}
		} else
		// 360计划
		{
			if ("2".equals(object_type))// 人员
			{
				if (operOrg!=null && operOrg.trim().length() > 3)
				// 按照操作单位走
				{
					StringBuffer buf = new StringBuffer();
					buf.append("select object_id from per_object where plan_id=" + planid);
					
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
							tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
						else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
							tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
					}
					buf.append(" and ( " + tempSql.substring(3) + " ) ");
					
					sql.append(buf.toString());
				}
				else if((!this.userview.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{
					sql.append(this.getPrivEmpStr());
				} 
			} else
			{
				if (operOrg!=null && operOrg.trim().length() > 3)
				// 按照操作单位走
				{
					StringBuffer buf = new StringBuffer();
					buf.append("select object_id from per_object where plan_id=" + planid);
					
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						tempSql.append(" or  object_id like '" + temp[i].substring(2) + "%'");
					}
					buf.append(" and ( " + tempSql.substring(3) + " ) ");
					
					sql.append(buf.toString());
				}
				else if((!this.userview.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{
					StringBuffer buf = new StringBuffer();
					buf.append("select object_id from per_object where plan_id=" + planid);
					buf.append(" and object_id  like '" + this.userview.getManagePrivCodeValue() + "%'");
					sql.append(buf.toString());
				}
			}
		}
		return sql.toString();

	}
    /**
     * Description:业绩数据录入支持拼音简写查询 
     * @Version1.0 
     * Nov 21, 2012 9:56:20 AM Jianghe created
     * @return
     */
    public ArrayList getUserList(String name){
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.cn);
    	String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH); 
//    	if(pinyin_field.equals("")){
//    		pinyin_field = "c0103";
//    	}
    	//唯一性指标查询
    	InfoUtils iu = new InfoUtils();
    	FieldItem item = iu.getOnlyFieldItem(this.cn);
		ArrayList list = new ArrayList();

		RecordVo planVo = this.getPerPlanVo(this.planid);
		String object_type = planVo.getString("object_type");
		String method = planVo.getString("method");
		
		RowSet rs =null;
		try
		{
		
		ContentDAO dao = new ContentDAO(this.cn);	
		StringBuffer sql = new StringBuffer();
		sql.append("select object_id,a0101,b0110,e0122,e01a1,a0000 from per_object where plan_id=");
		sql.append(this.planid);
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			sql.append(" and object_id in (");
			sql.append(this.getObjsPrivWhere()+")");
		}		
		if ("2".equals(method))
			if (!this.testPointIsCommon() && this.point.trim().length()>0) // 个性指标还要过滤一遍
			{
				sql.append(" and object_id in (");
				sql.append("select distinct ");
				if ("2".equals(object_type))// p04表的考核对象 人员存在a0100 非人员存在 b0110
					sql.append("a0100");
				else
					sql.append("b0110");
				sql.append(" from p04  where p0401='" + this.point);
				sql.append("' and plan_id=" + this.planid +")");

			}
	    	sql.append("and (a0101 like '"+name+"%' or object_id in (select a0100 from usra01 where 1=2");
	    	if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )){
	    		sql.append(" or "+pinyin_field+" like '"+name+"%'");
	    	}
	    	if(item!=null){
    			sql.append(" or "+item.getItemid()+" like '"+name+"%'");
    		}
	    	sql.append(" ))");
		sql.append(" order by a0000, object_id");
	
			rs = dao.search(sql.toString());
			while (rs.next())
    		{
    			CommonData cd = new CommonData();
    			String[] temp = new String[3];
    			temp[0] = rs.getString(1);
    			temp[1] = rs.getString(2);
    			temp[2] = rs.getString(3);
				cd.setDataName(temp[1]);
				cd.setDataValue(temp[0]);
				list.add(cd);
    		}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	
    	
    }
	/**
	 * 获得某考核计划的管理权限范围内的考核对象 个性指标还要过滤一遍
	 * 在此加入指标对考核对象的权限 2010-11-19修改
	 */
	public ArrayList getKhObjs()
	{
		ArrayList list = new ArrayList();

		RecordVo planVo = this.getPerPlanVo(this.planid);
		String object_type = planVo.getString("object_type");
		String method = planVo.getString("method");
		
		RowSet rs =null;
		try
		{
		
		ContentDAO dao = new ContentDAO(this.cn);	
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_object where plan_id=");
		sql.append(this.planid);
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			sql.append(" and object_id in (");
			sql.append(this.getObjsPrivWhere()+")");
		}		
		if ("2".equals(method)){
			if (!this.testPointIsCommon() && this.point.trim().length()>0) // 个性指标还要过滤一遍
			{
				sql.append(" and object_id in (");
				sql.append("select distinct ");
				if ("2".equals(object_type))// p04表的考核对象 人员存在a0100 非人员存在 b0110
					sql.append("a0100");
				else
					sql.append("b0110");
				sql.append(" from p04  where p0401='" + this.point);
				sql.append("' and plan_id=" + this.planid +")");

			}
		}

		sql.append(" order by a0000, object_id");
	
			rs = dao.search(sql.toString());
			while (rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
				String e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
				String e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");
				String object_id = rs.getString("object_id") == null ? "" : rs.getString("object_id");
				
				if(this.point.trim().length()>0)
				{
					boolean right = true;
					if(!"2".equals(object_type))  // 非2 团队
						right = this.pointPrivBean.getPrivPoint("", object_id, this.point);
					else if("2".equals(object_type))  // 2 人员
						right = this.pointPrivBean.getPrivPoint(b0110, e0122, this.point);
					if(right==false)
						continue;	
				}	
				
				String a0101 = rs.getString("a0101") == null ? "" : rs.getString("a0101");
				if ("2".equals(object_type) && this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					String onlyA0100 = this.onlyValueMap1.get(object_id)==null?"":(String)this.onlyValueMap1.get(object_id);
					bean.set(this.onlyname, onlyA0100);
				}				
				
				bean.set("b0110", b0110);
				bean.set("e01a1", e01a1);
				bean.set("object_id", object_id);
				bean.set("a0101", a0101);
				bean.set("e0122", e0122);
				
				String e0122Name = "";
				if (Integer.parseInt(this.display_e0122) == 0)
					e0122Name = AdminCode.getCodeName("UM", e0122);
				else
				{
					CodeItem item = AdminCode.getCode("UM", e0122, Integer.parseInt(this.display_e0122));
					if (item != null)
					{
						e0122Name = item.getCodename();// 显示级联部门
					} else
					{
						e0122Name = AdminCode.getCodeName("UM", e0122);
					}
				}
				bean.set("e0122Name", e0122Name);
				list.add(bean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获得某考核计划的管理权限范围内的考核对象 个性指标还要过滤一遍
	 * 在此加入指标对考核对象的权限  用于下载模板  处理多个指标   zhaoxg add 2014-9-17
	 */
	public ArrayList getKhObjects(ArrayList list)
	{
		RecordVo planVo = this.getPerPlanVo(this.planid);
		String object_type = planVo.getString("object_type");
		String method = planVo.getString("method");
		
		RowSet rs =null;
		try
		{
		
		ContentDAO dao = new ContentDAO(this.cn);	
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_object where plan_id=");
		sql.append(this.planid);
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			sql.append(" and object_id in (");
			sql.append(this.getObjsPrivWhere()+")");
		}		
		if ("2".equals(method)){
			if (!this.testPointIsCommon() && this.point.trim().length()>0) // 个性指标还要过滤一遍
			{
				sql.append(" and object_id in (");
				sql.append("select distinct ");
				if ("2".equals(object_type))// p04表的考核对象 人员存在a0100 非人员存在 b0110
					sql.append("a0100");
				else
					sql.append("b0110");
				sql.append(" from p04  where p0401='" + this.point);
				sql.append("' and plan_id=" + this.planid +")");

			}
		}

		sql.append(" order by a0000, object_id");
	
			rs = dao.search(sql.toString());
			ok:
			while (rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
				String e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
				String e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");
				String object_id = rs.getString("object_id") == null ? "" : rs.getString("object_id");
				for(int i=0;i<list.size();i++){
					LazyDynaBean _bean = (LazyDynaBean) list.get(i);
					if(object_id.equals(_bean.get("object_id"))){
						break ok;
					}
				}
				if(this.point.trim().length()>0)
				{
					boolean right = true;
					if(!"2".equals(object_type))  // 非2 团队
						right = this.pointPrivBean.getPrivPoint("", object_id, this.point);
					else if("2".equals(object_type))  // 2 人员
						right = this.pointPrivBean.getPrivPoint(b0110, e0122, this.point);
					if(right==false)
						continue;	
				}
				
				String a0101 = rs.getString("a0101") == null ? "" : rs.getString("a0101");
				if ("2".equals(object_type) && this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					String onlyA0100 = this.onlyValueMap1.get(object_id)==null?"":(String)this.onlyValueMap1.get(object_id);
					bean.set(this.onlyname, onlyA0100);
				}				
				
				bean.set("b0110", b0110);
				bean.set("e01a1", e01a1);
				bean.set("object_id", object_id);
				bean.set("a0101", a0101);
				bean.set("e0122", e0122);
				
				String e0122Name = "";
				if (Integer.parseInt(this.display_e0122) == 0)
					e0122Name = AdminCode.getCodeName("UM", e0122);
				else
				{
					CodeItem item = AdminCode.getCode("UM", e0122, Integer.parseInt(this.display_e0122));
					if (item != null)
					{
						e0122Name = item.getCodename();// 显示级联部门
					} else
					{
						e0122Name = AdminCode.getCodeName("UM", e0122);
					}
				}
				bean.set("e0122Name", e0122Name);
				list.add(bean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/* 生成临时表 */
	public void generateTable() throws GeneralException
	{

		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();

		if ("2".equals(this.planMethod))// 目标管理的考核计划
		{
			// 取出指标(过滤出绩效的指标)包括个性和共性
			sql.append("select  pp.point_id,pp.pointname from per_point pp  join (");
			sql.append("select distinct p0401 from p04 where fromflag=2 and plan_id=");
			sql.append(this.planid);
			sql.append(" and ((chg_type <> 3) or (chg_type is null)) ");
			sql.append(") a on pp.point_id=a.p0401  and  pp.status=1 and pp.pointkind=1 ");
		} else
		// 360考核计划
		{
			sql.append("select per_point.point_id,per_point.pointname from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(this.planid);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1");
		}

		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql.toString());
			while (rs.next())
			{
				String temp = rs.getString("point_id");
				list.add(temp);
			}
			if(list.size()<1){//其实我感觉 360和目标都从模板里取就行了，没理解为什么目标要从p04里面弄下    zhaoxg add  2014-8-20
				sql.delete(0, sql.length());
				sql.append("select per_point.point_id,per_point.pointname from per_template_point,per_point where ");
				sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
				sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
				sql.append(this.planid);
				sql.append("')) and per_point.status=1 and per_point.pointkind=1");
				RowSet _rs = dao.search(sql.toString());
				while (_rs.next())
				{
					String _temp = _rs.getString("point_id");
					list.add(_temp);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		String tableName = "per_gather_" + this.planid;
		DbWizard dbWizard = new DbWizard(this.cn);
		if (!dbWizard.isExistTable(tableName, false))
		{
			Table table = new Table(tableName);
			Field obj = new Field("gather_id");
			obj.setDatatype(DataType.INT);
			obj.setNullable(false);
			obj.setKeyable(true);
			table.addField(obj);

			obj = new Field("object_id");
			obj.setDatatype(DataType.STRING);
			obj.setLength(30);
			obj.setKeyable(false);
			table.addField(obj);

			obj = new Field("NBASE");
			obj.setDatatype(DataType.STRING);
			obj.setLength(30);
			obj.setKeyable(false);
			table.addField(obj);

			obj = new Field("B0110");
			obj.setDatatype(DataType.STRING);
			obj.setLength(30);
			obj.setKeyable(false);
			table.addField(obj);

			obj = new Field("E0122");
			obj.setDatatype(DataType.STRING);
			obj.setLength(30);
			obj.setKeyable(false);
			table.addField(obj);

			obj = new Field("E01A1");
			obj.setDatatype(DataType.STRING);
			obj.setLength(30);
			obj.setKeyable(false);
			table.addField(obj);

			for (int i = 0; i < list.size(); i++)
			{
				String colName = (String) list.get(i);
				obj = new Field("T_" + colName);
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);
			}
			dbWizard.createTable(table);
		} else
		// 表存在 看看动态变动字段存在不
		{
			Table table = new Table(tableName);
			boolean addFilds = false;
			for (int i = 0; i < list.size(); i++)
			{
				String colName = (String) list.get(i);
				try
				{
					if (!dbWizard.isExistField(tableName, "T_" + colName))
					{
						addFilds = true;
						Field obj = new Field("T_" + colName);
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);

					}
				} catch (Exception e)
				{

				}
			}
			if (addFilds)
				dbWizard.addColumns(table);
		}

		tableName = "per_gather_score_" + this.planid;
		if (!dbWizard.isExistTable(tableName, false))
		{
			Table table = new Table(tableName);
			Field obj = new Field("gather_id");
			obj.setDatatype(DataType.INT);
			obj.setNullable(false);
			obj.setKeyable(true);
			table.addField(obj);

			for (int i = 0; i < list.size(); i++)
			{
				String colName = (String) list.get(i);
				obj = new Field("T_" + colName);// 存标准分
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);

				obj = new Field("T_" + colName + "_A");// 存加分
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);

				obj = new Field("T_" + colName + "_M");// 存扣分
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);

				obj = new Field("T_" + colName + "_s");// 存总分
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);
			}
			dbWizard.createTable(table);
		} else
		// 表存在 看看新加的_s字段存在不
		{
			Table table = new Table(tableName);
			boolean addFilds = false;
			for (int i = 0; i < list.size(); i++)
			{
				String colName = (String) list.get(i);
				try
				{
					if (!dbWizard.isExistField(tableName, "T_" + colName))
					{
						addFilds = true;
						Field obj = new Field("T_" + colName);// 存标准分
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
					}
					if (!dbWizard.isExistField(tableName, "T_" + colName + "_A"))
					{
						addFilds = true;
						Field obj = new Field("T_" + colName + "_A");// 存加分
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
					}
					if (!dbWizard.isExistField(tableName, "T_" + colName + "_M"))
					{
						addFilds = true;
						Field obj = new Field("T_" + colName + "_M");// 存扣分
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
					}
					if (!dbWizard.isExistField(tableName, "T_" + colName + "_s"))
					{
						addFilds = true;
						Field obj = new Field("T_" + colName + "_s");// 存总分
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
					}
				} catch (Exception e)
				{

				}
			}
			if (addFilds)
				dbWizard.addColumns(table);
		}
	}

	/** 判断per_gather_score_xxx中是否存在得分的记录 */
	public boolean isExistDF(String gather_id)
	{

		boolean flag = false;
		String sql = "select * from per_gather_score_" + this.planid + " where gather_id=" + gather_id;
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				flag = true;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	/** 基本指标录分情况的保存 */
	public void save3(String[] fzScores, ArrayList objs) throws GeneralException
	{
        HashMap objRangs = new HashMap();
        
		ContentDAO dao = new ContentDAO(this.cn);
		ArrayList list2 = new ArrayList();
		for (int i = 0; i < objs.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) objs.get(i);
			String object_id = (String) bean.get("object_id");
			objRangs.put(object_id, object_id);
			String b0110 = (String) bean.get("b0110");
			String e0122 = (String) bean.get("e0122");
			String e01a1 = (String) bean.get("e01a1");
			
			// 如果(per_gather_planid)表已经存在该考核对象的记录就不插入数据了
			if (!"".equals(this.getGatherId(object_id)))
				continue;
			IDGenerator idg = new IDGenerator(2, this.cn);
			String gather_id = idg.getId("per_gather.gather_id");
			ArrayList list3 = new ArrayList();
			list3.add(gather_id);
			list3.add(object_id);
			list3.add(b0110);
			list3.add(e0122);
			list3.add(e01a1);
			list2.add(list3);
		}
		// 先在per_gather_planId表中插入记录
		String sql1 = "insert into per_gather_" + this.planid + "(nbase,gather_id,object_id,b0110,e0122,e01a1) values ('Usr',?,?,?,?,?)";
		try
		{
			dao.batchInsert(sql1, list2);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		ArrayList updateList = new ArrayList();// 批量更新per_gather_score_planId表中的T_XXX_s字段
		ArrayList addList = new ArrayList();// 批量新增per_gather_score_planId表中的T_XXX_s字段

		ArrayList list9 = new ArrayList();
		ArrayList list13 = new ArrayList();// 批量更新per_result表
		HashMap gatherids = this.getGatherIds();
		// 基本型指标简单和分段计算规则的数据计算
		HashMap basicFenMap = this.getBasicFen();
		sql1 = "select pointname from per_point where point_id='"+this.point+"' ";
		RowSet rs = null;
		String pointName = "";
		try
		{
			rs = dao.search(sql1);
			if(rs.next()){
				pointName = rs.getString("pointname");
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		for (int i = 0; i < fzScores.length; i++)
		{
			String scoreStr = fzScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
			String object_id = scoreArray[0];
			String pricticalVal = scoreArray[1];
			String basicVal = (String) basicFenMap.get(object_id);
			basicVal = (basicVal == null || !(basicVal == null) && "".equals(basicVal)) ? "0" : basicVal;
			if((Float.parseFloat(basicVal)<0 && (Float.parseFloat(pricticalVal)>0 || Float.parseFloat(pricticalVal)<Float.parseFloat(basicVal)))
					|| (Float.parseFloat(basicVal)>0 && (Float.parseFloat(pricticalVal)<0 || Float.parseFloat(pricticalVal)>Float.parseFloat(basicVal)))){
				// 设置了不受标准分限制的话，业绩数据录入页面中输入超出标准分的分数不做限制 chent 20160314 start
				Hashtable planParam=null;
				LoadXml loadxml = null;
				loadxml = new LoadXml(this.cn, String.valueOf(this.planid));
				BatchGradeBo.getPlanLoadXmlMap().put(this.planid, loadxml);
				planParam = loadxml.getDegreeWhole();
				String evalOutLimitStdScore = (String)planParam.get("EvalOutLimitStdScore");//评分时得分不受标准分限制True, False, 默认为 False;都加
				if("false".equalsIgnoreCase(evalOutLimitStdScore)){//受限制的时候报错
					throw GeneralExceptionHandler.Handle(new Exception("指标[" + pointName + "]超出了指定的分值范围(标准分:"+basicVal+")！"));								
				}
				// 设置了不受标准分限制的话，业绩数据录入页面中输入超出标准分的分数不做限制 chent 20160314 end
			}
			if(objRangs.get(object_id)==null)
				continue;		
				
			String fz = scoreArray[1];
			ArrayList list10 = new ArrayList();
			list10.add(new Double(fz));
			list10.add(object_id);
			list9.add(list10);

			ArrayList list12 = new ArrayList();
			list12.add(new Double(fz));
			String gatherid = (String) gatherids.get(object_id);
			list12.add(gatherid);
			if (isExistDF(gatherid))
				updateList.add(list12);
			else
				addList.add(list12);			
			
			String status = this.template_vo.getString("status"); // 0:分值 1:权重
			if("1".equals(status) && this.unifiedScoreMap.get(this.point.toUpperCase())!=null)//权重模板的定量统一打分指标乘以权重更新到结果表
			{
				String rank = (String)this.templatePointRank.get(this.point.toUpperCase());
				if(this.objPointRanks.get(this.point.toUpperCase()+"_"+object_id)!=null)
				    rank = (String)objPointRanks.get(this.point.toUpperCase()+"_"+object_id);//取得考核对象的动态指标权重
				
				fz =PubFunc.multiple(fz,rank,6);
			}
			
			ArrayList list14 = new ArrayList();
			list14.add(new Double(fz));
			list14.add(object_id);
			list13.add(list14);
		}

		// 用实际值批量更新per_gather_planId表中的T_XXX字段
		StringBuffer sql = new StringBuffer();
		sql.append("update per_gather_" + this.planid);
		sql.append(" set T_" + this.point);
		sql.append("=? where object_id=?");// per_gather_planId表中object_id是不可重复的
		try
		{
			dao.batchUpdate(sql.toString(), list9);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		if (testPointIsCommon())// 共性指标
		{
			// 更新per_result_planid表

			StringBuffer update_sql = new StringBuffer("update per_result_" + this.planid);
			update_sql.append(" set C_");
			update_sql.append(this.point);
			update_sql.append("=? where object_id=?");

			try
			{
				dao.batchUpdate(update_sql.toString(), list13);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		// 用实际值批量更新per_gather_score_planId表中的T_XXX_s字段
		sql = new StringBuffer();
		sql.append("update per_gather_score_" + this.planid);
		sql.append(" set ");
		sql.append("T_" + this.point + "_s=?");
		sql.append(" where gather_id=?");// per_gather_planId表中object_id是不可重复的
		// 如果在per_gather_score_planId表中不存在gather_id就新增
		StringBuffer addSql = new StringBuffer();
		addSql.append("insert into per_gather_score_" + this.planid);
		addSql.append("(T_" + this.point + "_s,gather_id)");
		addSql.append(" values(?,?)");

		try
		{
			if (updateList.size() > 0)
				dao.batchUpdate(sql.toString(), updateList);
			if (addList.size() > 0)
				dao.batchInsert(addSql.toString(), addList);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	/** 判断per_gather_score_xxx中是否存在得分的记录 */
	public HashMap getPer_gather_keyMap()
	{

		HashMap map = new HashMap();
		String sql = "select gather_id,item_id from per_gather ";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				map.put(rs.getString(1)+":"+rs.getString(2), "");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 加减型指标的保存 对于分值的保存操作采取先删除后添加的方式 因为考核对象是变动的
	 */
	public void save(String[] scores, String[] dfScores, ArrayList objs) throws GeneralException
	{
        HashMap objRangs = new HashMap();
		ContentDAO dao = new ContentDAO(this.cn);
		StringBuffer objstr = new StringBuffer();
		if (objs.size() == 0)// 没有考核对象就不要保存了
			return;
		for (int i = 0; i < objs.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) objs.get(i);
			String object_id = (String) bean.get("object_id");
			objstr.append(",'" + object_id + "'");
			objRangs.put(object_id, object_id);
		}
		// 删除业绩数据信息表中保存的[当前考核对象]的业绩值,再重新保存
		ArrayList items = this.getAllItems();// 或得所有底层项目
		StringBuffer itemStr = new StringBuffer();
		for (int x = 0; x < items.size(); x++)
		{
			LazyDynaBean bean2 = (LazyDynaBean) items.get(x);
			String item = (String) bean2.get("item");
			itemStr.append(",'" + item + "'");
		}

		StringBuffer delStr = new StringBuffer();
		delStr.append("delete from per_gather where gather_id in (select gather_id from per_gather_");
		delStr.append(this.planid);
		delStr.append(" where object_id in (");
		delStr.append(objstr.substring(1));
		delStr.append(")) and item_id in (");
		delStr.append(itemStr.substring(1) + ")");

		try
		{
			dao.delete(delStr.toString(), new ArrayList());
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		ArrayList list2 = new ArrayList();
		for (int i = 0; i < objs.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) objs.get(i);
			String object_id = (String) bean.get("object_id");
			// 如果(per_gather_planid)表已经存在该考核对象的记录就不插入数据了
			if (!"".equals(this.getGatherId(object_id)))
				continue;
			String b0110 = (String) bean.get("b0110");
			String e0122 = (String) bean.get("e0122");
			String e01a1 = (String) bean.get("e01a1");
			IDGenerator idg = new IDGenerator(2, this.cn);
			String gather_id = idg.getId("per_gather.gather_id");
			ArrayList list3 = new ArrayList();
			list3.add(gather_id);
			list3.add(object_id);
			list3.add(b0110);
			list3.add(e0122);
			list3.add(e01a1);
			list2.add(list3);
		}
		// 先在per_gather_planId表中插入记录
		String sql1 = "insert into per_gather_" + this.planid + "(nbase,gather_id,object_id,b0110,e0122,e01a1) values ('Usr',?,?,?,?,?)";
		try
		{
			dao.batchInsert(sql1, list2);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		// 在per_gather表中插入[当前考核对象]的业绩值
		sql1 = "insert into per_gather(s_value,gather_id,item_id) values (?,?,?)";
		String sql2 = "update per_gather set s_value=? where gather_id=? and item_id=?";
		ArrayList list5_insert = new ArrayList();
		ArrayList list5_update = new ArrayList();
		HashMap tempMap = this.getPer_gather_keyMap();
		HashMap gatherids = this.getGatherIds();
		for (int i = 0; i < scores.length; i++)
		{
			String scoreStr = scores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
			String temp = scoreArray[0];
			String s_value = scoreArray[1];
			String[] tempArray = temp.split("_");
			String object_id = tempArray[0];
			
			if(objRangs.get(object_id)==null)
				continue;
			
			String gather_id = (String) gatherids.get(object_id);
			String item_id = tempArray[1];
			ArrayList list4 = new ArrayList();
			list4.add(new Double(s_value));
			list4.add(gather_id);
			list4.add(item_id);		
			if(tempMap.get(gather_id+":"+item_id)==null)
				list5_insert.add(list4);
			else
				list5_update.add(list4);
		}

		try
		{
			if(list5_insert.size()>0)
				dao.batchInsert(sql1, list5_insert);
			if(list5_update.size()>0)
				dao.batchUpdate(sql2, list5_update);
				
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		// 在per_gather_score_xxx中插入记录
		StringBuffer insert_sql = new StringBuffer("insert into per_gather_score_" + this.planid);
		String type = this.getTypeOfPoint();
		if ("1".equals(type))// 加分指标
			insert_sql.append("(T_" + this.point + "_A,T_" + this.point + "_s,gather_id)");
		else if ("2".equals(type))// 减分指标
			insert_sql.append("(T_" + this.point + "_M,T_" + this.point + "_s,gather_id)");
		insert_sql.append("values(?,?,?)");

		StringBuffer update_sql = new StringBuffer("update per_gather_score_" + this.planid);
		update_sql.append(" set ");
		if ("1".equals(type))// 加分指标
			update_sql.append("T_" + this.point + "_A=?,T_" + this.point + "_s=?");
		else if ("2".equals(type))// 减分指标
			update_sql.append("T_" + this.point + "_M=?,T_" + this.point + "_s=?");
		update_sql.append(" where gather_id=?");
		ArrayList list6 = new ArrayList();// 批量新增
		ArrayList list8 = new ArrayList();// 批量更新

		ArrayList list9 = new ArrayList();// 批量更新per_result表
		for (int i = 0; i < dfScores.length; i++)
		{
			ArrayList list7 = new ArrayList();
			String scoreStr = dfScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
			String object_id = scoreArray[0];
			
			if(objRangs.get(object_id)==null)
				continue;
			
			String df = scoreArray[1];
			String gather_id = (String) gatherids.get(object_id);
			list7.add(new Double(df));
			list7.add(new Double(df));
			list7.add(gather_id);
			if (isExistDF(gather_id))
				list8.add(list7);
			else
				list6.add(list7);

			ArrayList list10 = new ArrayList();
			// if (type.equals("2"))// 2,扣分指
			// list10.add("-" + df);
			// else if (type.equals("1"))// 1,加分指标
			// list10.add(df);
			// else
			
			String status = this.template_vo.getString("status"); // 0:分值 1:权重
			if("1".equals(status) && this.unifiedScoreMap.get(this.point.toUpperCase())!=null)//权重模板的定量统一打分指标乘以权重更新到结果表
			{
				String rank = (String)this.templatePointRank.get(this.point.toUpperCase());
				if(this.objPointRanks.get(this.point.toUpperCase()+"_"+object_id)!=null)
				    rank = (String)objPointRanks.get(this.point.toUpperCase()+"_"+object_id);//取得考核对象的动态指标权重
				
				df =PubFunc.multiple(df,rank,6);
			}
			 
			list10.add(new Float(df));// 扣分指标在界面上就是负值了		
			list10.add(object_id);
			list9.add(list10);
		}

		try
		{
			// 先更新再新增
			if (list8.size() > 0)
				dao.batchUpdate(update_sql.toString(), list8);
			if (list6.size() > 0)
				dao.batchInsert(insert_sql.toString(), list6);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		if (this.testPointIsCommon())// 共性指标
		{
			// 更新per_result_planid表
			update_sql = new StringBuffer("update per_result_" + this.planid);
			update_sql.append(" set C_");
			update_sql.append(this.point);
			update_sql.append("=? where object_id=?");

			try
			{
				dao.batchUpdate(update_sql.toString(), list9);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	/** 基本指标 简单|分段 规则的保存 */
	public void save1(String[] standardScores, String[] praticalScores, String[] basicScores, String[] addScores, String[] deducScores, String[] dfScores, ArrayList objs) throws GeneralException
	{
		HashMap objRangs = new HashMap();
		
		ContentDAO dao = new ContentDAO(this.cn);
		ArrayList list2 = new ArrayList();
		for (int i = 0; i < objs.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) objs.get(i);
			String object_id = (String) bean.get("object_id");
			objRangs.put(object_id, object_id);
			// 如果(per_gather_planid)表已经存在该考核对象的记录就不插入数据了
			if (!"".equals(this.getGatherId(object_id)))
				continue;
			String b0110 = (String) bean.get("b0110");
			String e0122 = (String) bean.get("e0122");
			String e01a1 = (String) bean.get("e01a1");
			IDGenerator idg = new IDGenerator(2, this.cn);
			String gather_id = idg.getId("per_gather.gather_id");
			ArrayList list3 = new ArrayList();
			list3.add(gather_id);
			list3.add(object_id);
			list3.add(b0110);
			list3.add(e0122);
			list3.add(e01a1);
			list2.add(list3);
		}
		// 先在per_gather_planId表中插入记录
		String sql1 = "insert into per_gather_" + this.planid + "(nbase,gather_id,object_id,b0110,e0122,e01a1) values ('Usr',?,?,?,?,?)";
		try
		{
			dao.batchInsert(sql1, list2);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		// 用实际值批量更新per_gather_planId表中的T_XXX字段
		StringBuffer sql = new StringBuffer();
		sql.append("update per_gather_" + this.planid);
		sql.append(" set T_" + this.point);
		sql.append("=? where object_id=?");// per_gather_planId表中object_id是不可重复的
		StringBuffer str = new StringBuffer();
		ArrayList list = new ArrayList();
		for (int i = 0; i < praticalScores.length; i++)
		{
			String scoreStr = praticalScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
						
			String temp = scoreArray[0];
			String pricVal = scoreArray[1];
						
			String[] tempArray = temp.split("_");
			String object_id = tempArray[0];
			
			if(objRangs.get(object_id)==null)
				continue;
			
			ArrayList list1 = new ArrayList();
			list1.add(new Double(pricVal));
			list1.add(object_id);
			list.add(list1);
			if(i==0){
				str.append(object_id);
			}else{
				str.append("','"+object_id);
			}
		}

		try
		{
			dao.batchUpdate(sql.toString(), list);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (testPointIsCommon())// 共性指标 用得分值批量更新per_result_planId表中的T_XXX字段
		{
			sql = new StringBuffer();
			sql.append("update per_result_" + this.planid);
			sql.append(" set C_" + this.point);
			sql.append("=? where object_id=?");// per_gather_planId表中object_id是不可重复的

			list = new ArrayList();
			for (int i = 0; i < dfScores.length; i++)
			{
				String scoreStr = dfScores[i];
				if (scoreStr == null || scoreStr.trim().length()<=0)
					continue;
				String[] scoreArray = scoreStr.split("=");
								
				String temp = scoreArray[0];
				String dfVal = scoreArray[1];				
				
				String[] tempArray = temp.split("_");
				String object_id = tempArray[0];
				
				if(objRangs.get(object_id)==null)
					continue;
				
				String status = this.template_vo.getString("status"); // 0:分值 1:权重
				if("1".equals(status) && this.unifiedScoreMap.get(this.point.toUpperCase())!=null)//权重模板的定量统一打分指标乘以权重更新到结果表
				{
					String rank = (String)this.templatePointRank.get(this.point.toUpperCase());
					if(this.objPointRanks.get(this.point.toUpperCase()+"_"+object_id)!=null)
					    rank = (String)objPointRanks.get(this.point.toUpperCase()+"_"+object_id);//取得考核对象的动态指标权重
					
					dfVal =PubFunc.multiple(dfVal,rank,6);
				}				
				
				ArrayList list1 = new ArrayList();
				list1.add(new Float(dfVal));
				list1.add(object_id);
				list.add(list1);
			}

			try
			{
				dao.batchUpdate(sql.toString(), list);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		// 如果在per_gather_score_planId表中不存在gather_id就新增
		StringBuffer addSql = new StringBuffer();
		addSql.append("insert into per_gather_score_" + this.planid);
		addSql.append("(T_" + this.point + ",");
		addSql.append("T_" + this.point + "_A,");
		addSql.append("T_" + this.point + "_s,");
		addSql.append("T_" + this.point + "_M,gather_id)");
		addSql.append(" values(?,?,?,?,?)");

		// 用标准分值批量更新per_gather_score_planId表中的T_XXX字段，用加分更新T_XXX_A字段
		// 用减分更新T_XXX_M字段
		sql = new StringBuffer();
		sql.append("update per_gather_score_" + this.planid);
		sql.append(" set T_" + this.point + "=?,");
		sql.append("T_" + this.point + "_A=?,");
		sql.append("T_" + this.point + "_s=?,");
		sql.append("T_" + this.point + "_M=?");
		sql.append(" where gather_id=?");// per_gather_planId表中object_id是不可重复的

		list = new ArrayList();
		ArrayList addList = new ArrayList();
		for (int i = 0; i < standardScores.length; i++)
		{
			String scoreStr = standardScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
						
			String temp = scoreArray[0];
			String standardVal = scoreArray[1];			
			
			String[] tempArray = temp.split("_");
			String object_id = tempArray[0];
			
			if(objRangs.get(object_id)==null)
				continue;
			
			String gather_id = this.getGatherId(object_id);
			scoreStr = addScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			scoreArray = scoreStr.split("=");
			String addVal = "".equals(scoreArray[1]) ? "0" : scoreArray[1];

			scoreStr = deducScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			scoreArray = scoreStr.split("=");
			String deducVal = "".equals(scoreArray[1]) ? "0" : scoreArray[1];

			scoreStr = dfScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			scoreArray = scoreStr.split("=");			
			String dfVal = scoreArray[1];

			ArrayList list1 = new ArrayList();
			list1.add(new Double(standardVal));
			list1.add(new Double(addVal));
			list1.add(new Double(dfVal));
			list1.add(new Double(deducVal));
			list1.add(gather_id);
			if (isExistDF(gather_id))
				list.add(list1);
			else
				addList.add(list1);
		}

		try
		{
			if (list.size() > 0)
				dao.batchUpdate(sql.toString(), list);
			if (addList.size() > 0)
				dao.batchInsert(addSql.toString(), addList);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 取到排名计算规则下各考核对象对应的加减分值 规则：前几名：[1,1] [2,2][3,3] 中间几名： [4,5][4,-3],[-5,-3] 后几名：[-1,-1][-2,-2] [-3,-3]
	 */
	public HashMap getAdDeF(HashMap map)
	{

		HashMap map1 = new HashMap();
		String sql = "select * from per_standard_item where point_id='" + this.point + "' order by item_id";

		HashMap tempMap = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				float top_value = rs.getString("top_value") == null || (rs.getString("top_value") != null && "".equals(rs.getString("top_value"))) ? 0 : Float.parseFloat(rs.getString("top_value"));
				float bottom_value = rs.getString("bottom_value") == null || (rs.getString("bottom_value") != null && "".equals(rs.getString("bottom_value"))) ? 0 : Float.parseFloat(rs
						.getString("bottom_value"));
				String score = rs.getString("score") == null || (rs.getString("score") != null && "".equals(rs.getString("score"))) ? "0" : rs.getString("score");
				if (top_value * bottom_value == 0)// 如果上下限填写了空或者0
					continue;
				Set objIDs = map.keySet();
				if (top_value == bottom_value && top_value > 0)// 前几名
				{
					Iterator it = objIDs.iterator();
					while (it.hasNext())
					{
						String object_id = (String) it.next();
						HashMap rankMap = (HashMap) map.get(object_id);
						Integer qrank = (Integer) rankMap.get("qrank");// 前几名
						if (top_value == qrank.intValue() && tempMap.get(object_id) == null)
						{
							map1.put(object_id, score);
							tempMap.put(object_id, score);
						}

					}
				} else if (top_value == bottom_value && top_value < 0)// 后几名
				{
					Iterator it = objIDs.iterator();
					while (it.hasNext())
					{
						String object_id = (String) it.next();
						HashMap rankMap = (HashMap) map.get(object_id);
						Integer hrank = (Integer) rankMap.get("hrank");// 后几名
						if (top_value == hrank.intValue() && tempMap.get(object_id) == null)
						{
							map1.put(object_id, score);
							tempMap.put(object_id, score);
						}
					}
				} else if (top_value != bottom_value)
				{
					if (bottom_value > top_value && bottom_value * top_value > 0)// 下限值和上限值同号时保证上大于小
					{
						float temp = bottom_value;
						bottom_value = top_value;
						top_value = temp;
					}
					if (top_value > 0 && bottom_value > 0)// eg:[3,5] ||
					// [5,3]
					{
						Iterator it = objIDs.iterator();
						while (it.hasNext())
						{
							String object_id = (String) it.next();
							HashMap rankMap = (HashMap) map.get(object_id);
							Integer qrank = (Integer) rankMap.get("qrank");// 前几名
							if (top_value >= qrank.intValue() && bottom_value <= qrank.intValue() && tempMap.get(object_id) == null)
							{
								map1.put(object_id, score);
								tempMap.put(object_id, score);
							}
						}
					} else if (top_value < 0 && bottom_value < 0)// eg:[-3,-5]
					// ||
					// [-5,-3]
					{
						Iterator it = objIDs.iterator();
						while (it.hasNext())
						{
							String object_id = (String) it.next();
							HashMap rankMap = (HashMap) map.get(object_id);
							Integer hrank = (Integer) rankMap.get("hrank");// 后几名
							if (top_value >= hrank.intValue() && bottom_value <= hrank.intValue() && tempMap.get(object_id) == null)
							{
								map1.put(object_id, score);
								tempMap.put(object_id, score);
							}
						}
					} else if (top_value * bottom_value < 0)// eg:[6,-5] ||
					// [-5,6]
					{
						// 对于异号的，要规范上下限值
						Integer maxRank = getMaxRank(map);
						if (top_value < 0)
						{
							float y = top_value + maxRank.intValue() + 1;
							if (y < bottom_value)
							{
								float temp = bottom_value;
								bottom_value = top_value;
								top_value = temp;
							}
						} else
						{
							float y = bottom_value + maxRank.intValue() + 1;
							if (y > top_value)
							{
								float temp = bottom_value;
								bottom_value = top_value;
								top_value = temp;
							}
						}

						Iterator it = objIDs.iterator();
						while (it.hasNext())
						{
							String object_id = (String) it.next();
							HashMap rankMap = (HashMap) map.get(object_id);
							Integer hrank = (Integer) rankMap.get("hrank");// 后几名
							Integer qrank = (Integer) rankMap.get("qrank");// 前几名
							if (top_value < 0) // eg:[6,-5]
							{
								if (qrank.intValue() >= bottom_value && hrank.intValue() <= top_value && tempMap.get(object_id) == null)
								{
									map1.put(object_id, score);
									tempMap.put(object_id, score);
								}
							} else if (bottom_value < 0)// eg:[-5,6]
							{
								if (hrank.intValue() >= bottom_value && qrank.intValue() <= top_value && tempMap.get(object_id) == null)
								{
									map1.put(object_id, score);
									tempMap.put(object_id, score);
								}
							}
						}
					}

				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map1;
	}

	/* 得到排名的最大名次 */
	public Integer getMaxRank(HashMap map)
	{

		ArrayList list = new ArrayList();
		Integer maxRank = null;
		Set objIDs = map.keySet();
		Iterator it = objIDs.iterator();
		while (it.hasNext())
		{
			String object_id = (String) it.next();
			HashMap rankMap = (HashMap) map.get(object_id);
			Integer qrank = (Integer) rankMap.get("qrank");// 前几名
			list.add(qrank);
		}
		maxRank = (Integer) Collections.max(list);
		return maxRank;
	}

	/** 基本指标 排名 规则的保存 */
	public void save2(String[] standardScores, String[] praticalScores, String[] basicScores, ArrayList objs) throws GeneralException
	{
		HashMap objRangs = new HashMap();
		for (int i = 0; i < objs.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) objs.get(i);
			String object_id = (String) bean.get("object_id");
			objRangs.put(object_id, object_id);
		}
		
		ContentDAO dao = new ContentDAO(this.cn);
		ArrayList list2 = new ArrayList();
		HashMap map = new HashMap();// 保存考核对象的名次

		String[] theScores = new String[objs.size()];// 需要排名的差值或者比例值的数组
		String pointctrl = this.getPointctrl();
		HashMap xmlmap = PointCtrlXmlBo.getAttributeValues(pointctrl);
		String type = (String) xmlmap.get("computeType");// type=0|1（差额|比例）
		int index = 0;
		for (int i = 0; i < praticalScores.length; i++)
		{
			String scoreStr = praticalScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
			String temp = scoreArray[0];
			String pricVal = scoreArray[1];
			String[] tempArray = temp.split("_");
			String object_id = tempArray[0];

			if(objRangs.get(object_id)==null)
				continue;			
			
			scoreStr = standardScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			scoreArray = scoreStr.split("=");
			temp = scoreArray[0];
			String standardVal = scoreArray[1];

			float theValue = 0;
			// if (type.equals("0"))// type=0|1（差额|比例）
			// theValue = Float.parseFloat(pricVal) -
			// Float.parseFloat(standardVal);
			// else if (type.equals("1"))
			// theValue = (Float.parseFloat(pricVal) -
			// Float.parseFloat(standardVal)) /
			// Float.parseFloat(standardVal);
			// 程序规则修改了：对于排名的情况，都按照差额来排名
			theValue = Float.parseFloat(pricVal) - Float.parseFloat(standardVal);
			theScores[index++] = object_id + "=" + new Float(theValue).toString();
		}

		for (int i = 0; i < objs.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) objs.get(i);
			String object_id = (String) bean.get("object_id");
			HashMap map4 = this.getRank(object_id, theScores);
			map.put(object_id, map4);
			// 如果(per_gather_planid)表已经存在该考核对象的记录就不插入数据了
			if (!"".equals(this.getGatherId(object_id)))
				continue;
			String b0110 = (String) bean.get("b0110");
			String e0122 = (String) bean.get("e0122");
			String e01a1 = (String) bean.get("e01a1");
			IDGenerator idg = new IDGenerator(2, this.cn);
			String gather_id = idg.getId("per_gather.gather_id");
			ArrayList list3 = new ArrayList();
			list3.add(gather_id);
			list3.add(object_id);
			list3.add(b0110);
			list3.add(e0122);
			list3.add(e01a1);
			list2.add(list3);

		}
		// 先在per_gather_planId表中插入记录
		String sql1 = "insert into per_gather_" + this.planid + "(nbase,gather_id,object_id,b0110,e0122,e01a1) values ('Usr',?,?,?,?,?)";
		try
		{
			dao.batchInsert(sql1, list2);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		// 用实际值批量更新per_gather_planId表中的T_XXX字段
		StringBuffer sql = new StringBuffer();
		sql.append("update per_gather_" + this.planid);
		sql.append(" set T_" + this.point);
		sql.append("=? where object_id=?");// per_gather_planId表中object_id是不可重复的

		ArrayList list = new ArrayList();
		for (int i = 0; i < praticalScores.length; i++)
		{
			String scoreStr = praticalScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
			String temp = scoreArray[0];
			String pricVal = scoreArray[1];
			String[] tempArray = temp.split("_");
			String object_id = tempArray[0];
			
			if(objRangs.get(object_id)==null)
				continue;				
			
			ArrayList list1 = new ArrayList();
			list1.add(new Double(pricVal));
			list1.add(object_id);
			list.add(list1);
		}

		try
		{
			dao.batchUpdate(sql.toString(), list);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		HashMap map1 = getAdDeF(map);// 获得各考核对象的加减分
		// 用标准分值批量更新per_gather_score_planId表中的T_XXX字段，用加分更新T_XXX_A字段
		// 用减分更新T_XXX_M字段
		sql = new StringBuffer();
		sql.append("update per_gather_score_" + this.planid);
		sql.append(" set T_" + this.point + "=?,");
		sql.append("T_" + this.point + "_A=?,");
		sql.append("T_" + this.point + "_s=?,");
		sql.append("T_" + this.point + "_M=?");
		sql.append(" where gather_id=?");// per_gather_planId表中object_id是不可重复的
		// 如果在per_gather_score_planId表中不存在gather_id就新增
		StringBuffer addSql = new StringBuffer();
		addSql.append("insert into per_gather_score_" + this.planid);
		addSql.append("(T_" + this.point + ",");
		addSql.append("T_" + this.point + "_A,");
		addSql.append("T_" + this.point + "_s,");
		addSql.append("T_" + this.point + "_M,gather_id)");
		addSql.append(" values(?,?,?,?,?)");

		list = new ArrayList();
		ArrayList addList = new ArrayList();
		for (int i = 0; i < standardScores.length; i++)
		{
			String scoreStr = standardScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			String[] scoreArray = scoreStr.split("=");
			String temp = scoreArray[0];
			String standardVal = scoreArray[1];
			String[] tempArray = temp.split("_");
			String object_id = tempArray[0];
			
			if(objRangs.get(object_id)==null)
				continue;				
			
			String gather_id = this.getGatherId(object_id);
			String score = (String) map1.get(object_id);
			String addScore = "0.0";
			String deducScore = "0.0";
			if (score != null && !"".equals(score))
			{
				if (Float.parseFloat(score) > 0)
					addScore = score;
				else if (Float.parseFloat(score) < 0)
					deducScore = new Float(Float.parseFloat(score) * (-1)).toString();
			}

			// /////////////////////////////////////
			scoreStr = basicScores[i];
			if (scoreStr == null || scoreStr.trim().length()<=0)
				continue;
			scoreArray = scoreStr.split("=");
			temp = scoreArray[0];
			String basicScore = scoreArray[1];

			tempArray = temp.split("_");
			String addScore1 = (String) map1.get(object_id);// 可正可负
			addScore1 = addScore1 == null ? "0.00" : addScore1;

			double df = Double.parseDouble(addScore1) + Double.parseDouble(basicScore);

			if (Double.parseDouble(basicScore) > 0 && df < 0)// 如果基本分是正数则得分最低为0
				df = 0;

			// /////////////////////////////////
			ArrayList list1 = new ArrayList();
			list1.add(new Double(standardVal));
			list1.add(new Double(addScore));
			list1.add(new Double(df));
			list1.add(new Double(deducScore));
			list1.add(gather_id);
			if (isExistDF(gather_id))
				list.add(list1);
			else
				addList.add(list1);

		}

		try
		{
			if (list.size() > 0)
				dao.batchUpdate(sql.toString(), list);
			if (addList.size() > 0)
				dao.batchInsert(addSql.toString(), addList);

		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (testPointIsCommon())// 共性指标
		{
			// 用得分值批量更新per_result_planId表中的T_XXX字段
			sql = new StringBuffer();
			sql.append("update per_result_" + this.planid);
			sql.append(" set C_" + this.point);
			sql.append("=? where object_id=?");// per_gather_planId表中object_id是不可重复的

			list = new ArrayList();
			for (int i = 0; i < basicScores.length; i++)
			{
				String scoreStr = basicScores[i];
				if (scoreStr == null || scoreStr.trim().length()<=0)
					continue;
				String[] scoreArray = scoreStr.split("=");
				String temp = scoreArray[0];
				String basicScore = scoreArray[1];

				String[] tempArray = temp.split("_");
				String object_id = tempArray[0];
				
				if(objRangs.get(object_id)==null)
					continue;	
				
				String addScore = (String) map1.get(object_id);// 可正可负
				addScore = addScore == null ? "0.00" : addScore;

				double df = Double.parseDouble(addScore) + Double.parseDouble(basicScore);

				if (Double.parseDouble(basicScore) > 0 && df < 0)// 如果基本分是正数则得分最低为0
					df = 0;				
				
				String status = this.template_vo.getString("status"); // 0:分值 1:权重
				if("1".equals(status) && this.unifiedScoreMap.get(this.point.toUpperCase())!=null)//权重模板的定量统一打分指标乘以权重更新到结果表
				{
					String rank = (String)this.templatePointRank.get(this.point.toUpperCase());
					if(this.objPointRanks.get(this.point.toUpperCase()+"_"+object_id)!=null)
					    rank = (String)objPointRanks.get(this.point.toUpperCase()+"_"+object_id);//取得考核对象的动态指标权重
					
					df =new Double(PubFunc.multiple(new Double(df).toString(),rank,6)).doubleValue();
				}				
				
				ArrayList list1 = new ArrayList();
				list1.add(new Float(df));
				list1.add(object_id);
				list.add(list1);
			}

			try
			{
				dao.batchUpdate(sql.toString(), list);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	/** 根据object_id取到gatherID */
	public String getGatherId(String object_id)
	{

		String gather_id = "";
		String sql = "select gather_id from per_gather_" + this.planid + " where object_id='" + object_id + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				gather_id = rs.getString("gather_id");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return gather_id;
	}

	/** 取得权限范围内的所有考核对象对应的gatherID */
	public HashMap getGatherIds()
	{

		HashMap map = new HashMap();		
		StringBuffer sql = new StringBuffer();
		sql.append("select object_id,gather_id from per_gather_");
		sql.append(this.planid);
		sql.append(" where 1=1 ");
		if((this.getObjsPrivWhere()!=null) && (this.getObjsPrivWhere().trim().length()>0))
		{
			sql.append(" and object_id in (");
			sql.append(this.getObjsPrivWhere()+")");
		}
		
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql.toString());
			while (rs.next())
			{
				String gather_id = rs.getString("gather_id");
				String objectId = rs.getString("object_id");
				map.put(objectId, gather_id);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 取得各个标准项目的标准分值 */
	public HashMap getItemScore()
	{

		HashMap map = new HashMap();
		String sql = "select item_id,score from per_standard_item ";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String item_id = rs.getString("item_id");
				map.put(item_id, rs.getString("score"));
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 计算加扣分指标的得分 */
	public HashMap computDF(String object_id, String[] scoreArray) throws GeneralException
	{

		HashMap map = new HashMap();
		String objDF = "0";
		double score = 0;
		HashMap itemScoreMap = this.getItemScore();
		for (int i = 0; i < scoreArray.length; i++)
		{
			String scoreStr = scoreArray[i];
			String[] scoreArray2 = scoreStr.split("=");
			String temp = scoreArray2[0];
			String value = scoreArray2[1];
			String[] tempArray = temp.split("_");
			String item_id = tempArray[1];
			String item_score = itemScoreMap.get(item_id)==null?"0":(String)itemScoreMap.get(item_id);
			
			if(value!=null && value.length()>0 && value.indexOf("E")==-1)
			{
				if (!this.isDataType("N", value))
				{
					String msg = "源数据:" + value + " 不符合格式,必须输入数值型数据！";
					throw new GeneralException(msg);
				}					
			}			
			double x = Float.parseFloat(item_score) * Float.parseFloat(value);
			score += x;
		}
		map.put("cz", DataCollectBo.roundAndRemoveZero(Double.toString(score), 3));
		HashMap basicFenMap = this.getBasicFen();
		String basicFen = (String) basicFenMap.get(object_id);
		basicFen = (basicFen == null || basicFen != null && "".equals(basicFen)) ? "0" : basicFen;
		float basicF = new Float(basicFen).floatValue();
		if ("2".equals(this.getTypeOfPoint()))// 如果该指标为扣分指标
		{
			score = basicF - score;
			if (score < 0)
				score = 0;
		} else
			score = basicF + score;
		objDF = DataCollectBo.roundAndRemoveZero(Double.toString(score), 3);
		map.put("df", objDF);
		return map;
	}

	/**
	 * 获得Excel表格的数据 1 如果标题行某一个单元格子为空，就报错。 2 如果某人员不属于该计划下的考核对象，则不进行数据的导入 3 如果某分值的单元格为空，则按0来导入其值 4.数据行之间不要有空行,则报错。 要求：第一行为标题行，下面的行数为数据行；第一列为姓名列，其余各列为分值列，第一行由姓名和最下层的项目名称组成。
	 */
//	public HashMap getExcelData(FormFile form_file) throws GeneralException
//	{
//
//		HashMap map = new HashMap();
//		HSSFWorkbook wb = null;
//		HSSFSheet sheet = null;
//		try
//		{
//			wb = new HSSFWorkbook(form_file.getInputStream());
//			sheet = wb.getSheetAt(0);
//		} catch (Exception e)
//		{
//			System.out.println(e);
//		}
//		int cols = 0;
//		// 获得列名
//		HSSFRow row = sheet.getRow(0);
//		ArrayList titles = new ArrayList();// 姓名，项目1，项目2，
//		if (row != null)
//		{
//			cols = row.getPhysicalNumberOfCells();
//			for (short c = 0; c < cols; c++)
//			{
//				String value = "";
//				HSSFCell cell = row.getCell(c);
//				if (cell != null)
//				{
//					switch (cell.getCellType())
//					{
//					case HSSFCell.CELL_TYPE_FORMULA:
//						break;
//					case HSSFCell.CELL_TYPE_NUMERIC:
//						value = String.valueOf((long) cell.getNumericCellValue());
//						break;
//					case HSSFCell.CELL_TYPE_STRING:
//						value = cell.getStringCellValue();
//						break;
//					default:
//						value = "";
//					}
//				}
//				value = value.trim();
//				if (value.equals(""))
//					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
//				titles.add(getItemidByName(value));
//			}
//		}
//
//		int rows = sheet.getPhysicalNumberOfRows();
//		if (rows * cols == 0 || rows == 1)
//			return map;
//
//		ArrayList objs = new ArrayList();
//		String[] scores = new String[(rows - 1) * (cols - 1)];
//		String[] dfScores = new String[rows - 1];
//		int s = 0;
//		int dfS = 0;
//		// 取数据
//		for (int j = 1; j < rows; j++)
//		{
//			HSSFRow row1 = sheet.getRow(j);
//			if (row1 != null)
//			{
//				// 获得姓名列，由姓名查找object_id
//				HSSFCell cell1 = row1.getCell((short) 0);
//				String name = cell1.getStringCellValue();
//				if (name.trim().equals(""))// 姓名列为空，这一行数据就不导入
//					continue;
//				String object_id = getObjIdByName(name);
//				if (object_id.equals(""))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
//					continue;
//				LazyDynaBean bean = new LazyDynaBean();
//				bean.set("object_id", object_id);
//				objs.add(bean);
//				String[] objDF = new String[cols - 1];
//				int objDfIndex = 0;
//				for (short c = 1; c < cols; c++)
//				{
//					String value = "";
//					cell1 = row1.getCell(c);
//					if (cell1 != null)
//					{
//						switch (cell1.getCellType())
//						{
//						case HSSFCell.CELL_TYPE_FORMULA:
//							break;
//						case HSSFCell.CELL_TYPE_NUMERIC:
//							value = String.valueOf((long) cell1.getNumericCellValue());
//							break;
//						case HSSFCell.CELL_TYPE_STRING:
//							value = cell1.getStringCellValue();
//							break;
//						default:
//							value = "";
//						}
//					}
//					value = value.trim();
//					if (value.equals(""))
//						value = "0";
//
//					String itemid = (String) titles.get(c);// 从第一个项目开始取,除去姓名列
//					scores[s++] = object_id + "_" + itemid + "=" + value;
//					objDF[objDfIndex++] = object_id + "_" + itemid + "=" + value;
//				}
//				dfScores[dfS++] = object_id + "=" + this.computDF(object_id, objDF);
//			} else
//				throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
//		}
//		map.put("scores", scores);
//		map.put("dfScores", dfScores);
//		map.put("objs", objs);
//		return map;
//	}

	/**
	 * 获得所有指标Excel表格的数据 1 如果标题行某一个单元格子为空，就报错。 2 如果某人员不属于该计划下的考核对象，则不进行数据的导入 3 如果某分值的单元格为空，则按0来导入其值 4.数据行之间不要有空行,则报错。 要求：第一行为标题行，下面的行数为数据行；第一列为姓名列，其余各列为分值列，第一行由姓名和最下层的项目名称组成。
	 */
	public HashMap getExcelDatas(File file) throws GeneralException
	{
		
		LoadXml loadxml = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(this.planVo.getString("plan_id"));
		Hashtable planParam = loadxml.getDegreeWhole();
        String EvalOutLimitStdScore = (String)planParam.get("EvalOutLimitStdScore");//评分时得分不受标准分限制True, False
	
		HashMap pointsMap = new HashMap();
		try{
		// HSSFWorkbook wb = null;
		// HSSFSheet sheet = null;
		Workbook wb = null;
		Sheet sheet = null;
		
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(file);
			//wb = WorkbookFactory.create(form_file.getInputStream());
			wb = WorkbookFactory.create(inputStream);
			sheet = wb.getSheetAt(0);

			// wb = new HSSFWorkbook(form_file.getInputStream());
			// sheet = wb.getSheetAt(0);
		} catch (Exception e)
		{
			System.out.println(e);
		} finally {
			PubFunc.closeIoResource(inputStream);
			PubFunc.closeIoResource(wb);
		}

		ArrayList list = this.getPointList();
		HashMap pMap = new HashMap();// 正确的应该导入的指标名称
		for (int i = 0; i < list.size(); i++)
		{
			CommonData bo = (CommonData) list.get(i);						
			int n = bo.getDataName().indexOf(":");
			String pointName = bo.getDataName().substring(n+1);			
			pMap.put(pointName, bo.getDataValue());
		}

		int cols = 0;
		// 获得列名
		// HSSFRow row = sheet.getRow(0);
		Row row = sheet.getRow(0);
		ArrayList points = new ArrayList();// 指标行
		if (row != null)
		{
			cols = row.getPhysicalNumberOfCells();
			//姓名列
			int pointStartCol = 1;
			if(this.planVo.getInt("object_type")!=2)
				pointStartCol = 1;
			else
			{
				if(this.onlyname != null && !"a0101".equals(this.onlyname))// 部门，姓名，唯一标志列都存在 取唯一标志列
					pointStartCol = 5;
				else// 部门，姓名 存在 取姓名列
					pointStartCol = 4;					
			}
			for (short c = (short)pointStartCol; c < cols; c++)
			{
				String value = "";
//				String common = "";
				// HSSFCell cell = row.getCell(c);
				Cell cell = row.getCell(c);
				if (cell != null)
				{
					switch (cell.getCellType())
					{
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_NUMERIC:
							value = String.valueOf((long) cell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						default:
							value = "";
					}
//					common = cell.getCellComment().getString().getString().trim();
				}
				if((cell.getCellComment())!=null)				
					throw GeneralExceptionHandler.Handle(new Exception("导入的Excel模板不正确，请选用下载的模板导入数据！"));
				
				value = value.trim();
				if ("".equals(value))
					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的指标名称，不能为空"));
				LazyDynaBean bean = new LazyDynaBean();
				String pointid = (String) pMap.get(value);

				if (pointid == null)
					throw GeneralExceptionHandler.Handle(new Exception("指标名称[" + value + "]不正确！"));
				// for (int i = 0; i < list.size(); i++)
				// {
				// CommonData bo = (CommonData) list.get(i);
				// if (bo.getDataName().equals(value))
				// pointid = bo.getDataValue();
				// }
				String itemCount = getItemCount(pointid);

				bean.set("pointId", pointid);
				bean.set("pointName", value);
				bean.set("itemcount", itemCount);
				bean.set("itemstart", Integer.toString(c));
				points.add(bean);
				if (!"0".equals(itemCount))
					c += Integer.parseInt(itemCount) - 1;
			}
		}
		for (int i = 0; i < points.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) points.get(i);
			String pointid1 = (String) bean.get("pointId");
			String itemcount = (String) bean.get("itemcount");
			String itemstart = (String) bean.get("itemstart");
			String pointName = (String) bean.get("pointName");

			DataCollectBo nowBo = new DataCollectBo(this.cn, this.planid, pointid1, this.userview);
			//姓名列
			int nameCol = 0;
			if(nowBo.planVo.getInt("object_type")!=2)
				nameCol = 0;
			else
			{
				if(nowBo.onlyname != null && !"a0101".equals(nowBo.onlyname))// 部门，姓名，唯一标志列都存在 取唯一标志列
					nameCol = 4;
				else// 部门，姓名 存在 取姓名列
					nameCol = 3;					
			}
			
			HashMap map = new HashMap();
			if ("0".equals(itemcount))// 基本型指标
			{
				String pointype = nowBo.getTypeOfPoint();
				String rule = nowBo.getRule();
				if (rule == null)
					throw new GeneralException("指标【" + pointName + "】的计分规则没有定义！");
				int rows = sheet.getPhysicalNumberOfRows();
				short valueCel = Short.parseShort(itemstart);				
					
				if ("0".equals(pointype) && "0".equals(rule)) // 基本指标录分规则导入Excel
				{
					StringBuffer objs = new StringBuffer();// 有效的考核对象
					HashMap basicFenMap = nowBo.getBasicFen();
					ArrayList list1 = new ArrayList();
					// 取数据
					for (int j = 2; j < rows; j++)
					{
						Row row1 = sheet.getRow(j);
						if (row1 != null)
						{
							// 获得姓名列，由姓名查找object_id
							Cell cell1 = row1.getCell((short) nameCol);
							String name = "";
							if(cell1!=null)
								name = cell1.getStringCellValue();
							if ("".equals(name.trim()))// 姓名列为空，这一行数据就不导入
								continue;
							String object_id = nowBo.getObjIdByName(name);
							if (object_id==null || object_id.trim().length()<=0 || "".equals(object_id))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
								continue;
							String pricticalVal = "";// 分值
							objs.append(object_id + "<@>");
							cell1 = row1.getCell(valueCel);
							if (cell1 != null)
							{
								switch (cell1.getCellType())
								{
								case Cell.CELL_TYPE_FORMULA:
									break;
								case Cell.CELL_TYPE_NUMERIC:
									double x = cell1.getNumericCellValue();
									// pricticalVal = String.valueOf((long)
									// cell1.getNumericCellValue());
									pricticalVal = Double.toString(x);
									break;
								case Cell.CELL_TYPE_STRING:
									pricticalVal = cell1.getStringCellValue();
									break;
								default:
									pricticalVal = "";
								}
							}
							pricticalVal = pricticalVal.trim();
							if ("".equals(pricticalVal))
								pricticalVal = "0";

							if("---".equalsIgnoreCase(pricticalVal))//zhaoxg add  “---”代表这个格不让填东西，入库即为0即可。
								pricticalVal = "0";
							if(pricticalVal!=null && pricticalVal.length()>0 && pricticalVal.indexOf("E")==-1)
							{
								if (!this.isDataType("N", pricticalVal))
								{
									String msg = "源数据:" + pricticalVal + " 不符合格式,必须输入数值型数据！";
									throw new GeneralException(msg);
								}					
							}							
							String basicVal = (String) basicFenMap.get(object_id);
							basicVal = (basicVal == null || !(basicVal == null) && "".equals(basicVal)) ? "0" : basicVal;
							
							if("false".equalsIgnoreCase(EvalOutLimitStdScore) && ((Float.parseFloat(basicVal)<0 && (Float.parseFloat(pricticalVal)>0 || Float.parseFloat(pricticalVal)<Float.parseFloat(basicVal)))
									|| (Float.parseFloat(basicVal)>0 && (Float.parseFloat(pricticalVal)<0 || Float.parseFloat(pricticalVal)>Float.parseFloat(basicVal)))))
								  throw GeneralExceptionHandler.Handle(new Exception("指标[" + pointName + "]超出了指定的分值范围(标准分:"+basicVal+")！"));									

							LazyDynaBean bean1 = new LazyDynaBean();
							bean1.set("pricticalVal", pricticalVal);
							bean1.set("object_id", object_id);
							list1.add(bean1);
						} else
							throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
					}

					String[] pricticalVal = new String[list1.size()];
					for (int k = 0; k < list1.size(); k++)
					{
						LazyDynaBean theBean = (LazyDynaBean) list1.get(k);
						String objId = (String) theBean.get("object_id");
						pricticalVal[k] = objId + "=" + (String) theBean.get("pricticalVal");
					}
					if (list1.size() > 0)
					{
						map.put("fzScores", pricticalVal);
						map.put("objs", objs.toString());
					}

				} else if ("0".equals(pointype) && !"0".equals(rule)) // 基本指标非录分规则导入Excel
				{
					StringBuffer objs = new StringBuffer();// 有效的考核对象
					if ("3".equals(rule))// 排名的保存
					{
						ArrayList list4 = new ArrayList();
						// 取数据
						for (int j = 2; j < rows; j++)
						{
							Row row1 = sheet.getRow(j);
							if (row1 != null)
							{
								// 获得姓名列，由姓名查找object_id
								Cell cell1 = row1.getCell((short) nameCol);
								String name = "";
								if(cell1!=null)
									name = cell1.getStringCellValue();
								if ("".equals(name.trim()))// 姓名列为空，这一行数据就不导入
									continue;
								String object_id = nowBo.getObjIdByName(name);
								if (object_id==null || object_id.trim().length()<=0 || "".equals(object_id))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
									continue;
								objs.append(object_id + "<@>");
								String pricticalVal = "";// 实际值
								cell1 = row1.getCell(valueCel);
								if (cell1 != null)
								{
									switch (cell1.getCellType())
									{
									case HSSFCell.CELL_TYPE_FORMULA:
										break;
									case HSSFCell.CELL_TYPE_NUMERIC:
										double x = cell1.getNumericCellValue();
										// pricticalVal = String.valueOf((long)
										// cell1.getNumericCellValue());
										pricticalVal = Double.toString(x);
										break;
									case HSSFCell.CELL_TYPE_STRING:
										pricticalVal = cell1.getStringCellValue();
										break;
									default:
										pricticalVal = "";
									}
								}
								pricticalVal = pricticalVal.trim();
								if ("".equals(pricticalVal))
									pricticalVal = "0";

								String standVal = nowBo.getStandardFen(object_id);
								String basicVal = (String) nowBo.getBasicFen().get(object_id);
								LazyDynaBean bean4 = new LazyDynaBean();
								bean4.set("pricticalVal", pricticalVal);
								bean4.set("object_id", object_id);
								bean4.set("standVal", standVal);
								bean4.set("basicVal", basicVal == null ? "" : basicVal);
								list4.add(bean4);
							} else
								throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
						}

						String[] pricticalVal = new String[list4.size()];
						String[] standVal = new String[list4.size()];
						String[] basicVal = new String[list4.size()];

						for (int n = 0; n < list4.size(); n++)
						{
							LazyDynaBean theBean = (LazyDynaBean) list4.get(n);
							String objId = (String) theBean.get("object_id");
							pricticalVal[n] = objId + "_pratical=" + (String) theBean.get("pricticalVal");
							standVal[n] = objId + "_standard=" + (String) theBean.get("standVal");
							basicVal[n] = objId + "_basic=" + (String) theBean.get("basicVal");
						}
						if (list4.size() > 0)
						{
							map.put("pricticalVal", pricticalVal);
							map.put("standVal", standVal);
							map.put("basicVal", basicVal);
							map.put("objs", objs.toString());
						}

					} else if ("1".equals(rule) || "2".equals(rule))// 简单｜分段导入Excel
					{
						// HashMap map = bo.getExcelData1(form_file);
						ArrayList list2 = new ArrayList();
						// 取数据
						for (int j = 2; j < rows; j++)
						{
							Row row1 = sheet.getRow(j);
							if (row1 != null)
							{
								// 获得姓名列，由姓名查找object_id
								Cell cell1 = row1.getCell((short) nameCol);
								String name = "";
								if(cell1!=null)
									name = cell1.getStringCellValue();
								if ("".equals(name.trim()))// 姓名列为空，这一行数据就不导入
									continue;
								String object_id = nowBo.getObjIdByName(name);
								if (object_id==null || object_id.trim().length()<=0 || "".equals(object_id))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
									continue;														
								
								objs.append(object_id + "<@>");
								String pricticalVal = "";// 实际值
								cell1 = row1.getCell(valueCel);
								if (cell1 != null)
								{
									switch (cell1.getCellType())
									{
									case Cell.CELL_TYPE_FORMULA:
										break;
									case Cell.CELL_TYPE_NUMERIC:
										double x = cell1.getNumericCellValue();
										// pricticalVal = String.valueOf((long)
										// cell1.getNumericCellValue());
										pricticalVal = Double.toString(x);
										break;
									case Cell.CELL_TYPE_STRING:
										pricticalVal = cell1.getStringCellValue();
										break;
									default:
										pricticalVal = "";
									}
								}
								pricticalVal = pricticalVal.trim();
								if ("".equals(pricticalVal))
									pricticalVal = "0";
								if("---".equalsIgnoreCase(pricticalVal))
									continue;
								String standVal = nowBo.getStandardFen(object_id);
								String basicVal = (String) nowBo.getBasicFen().get(object_id);
								basicVal = basicVal == null ? "0" : basicVal;
								// 基本型指标简单和分段计算规则的数据计算
								HashMap map1 = nowBo.basciPointCalcu(pricticalVal, basicVal, standVal);
								String addF = (String) map1.get("addF");
								String deducF = (String) map1.get("deducF");
								String objDF = (String) map1.get("objDF");

								LazyDynaBean bean1 = new LazyDynaBean();
								bean1.set("pricticalVal", pricticalVal);
								bean1.set("object_id", object_id);
								bean1.set("standVal", standVal);
								bean1.set("basicVal", basicVal);
								bean1.set("addF", "".equals(addF) ? "0" : addF);
								bean1.set("deducF", "".equals(deducF) ? "0" : deducF);
								bean1.set("objDF", "".equals(objDF) ? "0" : objDF);
								list2.add(bean1);
							} else
								throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
						}

						String[] pricticalVal = new String[list2.size()];
						String[] standVal = new String[list2.size()];
						String[] basicVal = new String[list2.size()];
						String[] addF = new String[list2.size()];
						String[] deducF = new String[list2.size()];
						String[] objDF = new String[list2.size()];

						for (int m = 0; m < list2.size(); m++)
						{
							LazyDynaBean theBean = (LazyDynaBean) list2.get(m);
							String objId = (String) theBean.get("object_id");
							pricticalVal[m] = objId + "_pratical=" + (String) theBean.get("pricticalVal");
							standVal[m] = objId + "_standard=" + (String) theBean.get("standVal");
							addF[m] = objId + "_add=" + (String) theBean.get("addF");
							basicVal[m] = objId + "_basic=" + (String) theBean.get("basicVal");
							objDF[m] = objId + "_df=" + (String) theBean.get("objDF");
							deducF[m] = objId + "_deduc=" + (String) theBean.get("deducF");
						}
						if (list2.size() > 0)
						{
							map.put("pricticalVal", pricticalVal);
							map.put("standVal", standVal);
							map.put("addF", addF);
							map.put("basicVal", basicVal);
							map.put("objDF", objDF);
							map.put("deducF", deducF);
							map.put("objs", objs.toString());
						}
					}
				}
			} else
			// 加减分指标
			{
				ArrayList allItems = nowBo.getAllItems();// 应该导入的加扣分指标的最底层项目
				HashMap testItems = new HashMap();
				for (int m = 0; m < allItems.size(); m++)
				{
					LazyDynaBean temp = (LazyDynaBean) allItems.get(m);
					String itemCode = (String) temp.get("item");
					testItems.put(itemCode, "");
				}

				StringBuffer objs_str = new StringBuffer();// 有效的考核对象
				// 获得列名
				row = sheet.getRow(1);
				ArrayList titles = new ArrayList();// 项目1，项目2，
				if (row != null)
				{
					short c = Short.parseShort(itemstart);
					int count = c + Short.parseShort(itemcount);
					for (; c < count; c++)
					{
						String value = "";
						Cell cell = row.getCell(c);
						if (cell != null)
						{
							switch (cell.getCellType())
							{
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								value = String.valueOf((long) cell.getNumericCellValue());
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							default:
								value = "";
							}
						}
						value = value.trim();
						if ("".equals(value))
							throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第二行为导入的指标项目名称，不能为空"));
						String itemid = nowBo.getItemidByName(value);
						if (testItems.get(itemid) == null)
							throw GeneralExceptionHandler.Handle(new Exception("指标[" + pointName + "]和它的底层项目名称不匹配！"));
						titles.add(itemid);
					}
				}

				int rows = sheet.getPhysicalNumberOfRows();
				if (rows * cols == 0 || rows < 3)
					return pointsMap;

				ArrayList objs = new ArrayList();
				String[] scores = new String[(rows - 2) * Short.parseShort(itemcount)];
				String[] dfScores = new String[rows - 2];
				int s = 0;
				int dfS = 0;
				// 取数据
				for (int j = 2; j < rows; j++)
				{
					Row row1 = sheet.getRow(j);
					if (row1 != null)
					{
						// 获得姓名列，由姓名查找object_id
						Cell cell1 = row1.getCell((short) nameCol);
						String name = "";
						if(cell1!=null)
							name = cell1.getStringCellValue();
						if ("".equals(name.trim()))// 姓名列为空，这一行数据就不导入
							continue;
						String object_id = nowBo.getObjIdByName(name);
						if (object_id==null || object_id.trim().length()<=0 || "".equals(object_id))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
							continue;
						objs_str.append(object_id + "<@>");
						LazyDynaBean mybean = new LazyDynaBean();
						mybean.set("object_id", object_id);
						objs.add(mybean);
						String[] objDF = new String[Short.parseShort(itemcount)];
						int objDfIndex = 0;

						short c = Short.parseShort(itemstart);
						int count = c + Short.parseShort(itemcount);
						int itemIndex = 0;
						for (; c < count; c++)
						{
							String value = "";
							cell1 = row1.getCell(c);
							if (cell1 != null)
							{
								switch (cell1.getCellType())
								{
								case Cell.CELL_TYPE_FORMULA:
									break;
								case Cell.CELL_TYPE_NUMERIC:
									double x = cell1.getNumericCellValue();
									value = Double.toString(x);
									break;
								case Cell.CELL_TYPE_STRING:
									value = cell1.getStringCellValue();
									break;
								default:
									value = "";
								}
							}
							value = value.trim();
							if ("".equals(value))
								value = "0";
							if("---".equalsIgnoreCase(value))
								value = "0";
							String itemid = (String) titles.get(itemIndex++);// 从第一个项目开始取,除去姓名列
							scores[s++] = object_id + "_" + itemid + "=" + value;
							objDF[objDfIndex++] = object_id + "_" + itemid + "=" + value;
						}
						HashMap dfMap = nowBo.computDF(object_id, objDF);
						String dfVal = (String) dfMap.get("df");
						dfScores[dfS++] = object_id + "=" + ("".equals(dfVal) ? "0" : dfVal);
					} else
						throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
				}
				map.put("scores", scores);
				map.put("dfScores", dfScores);
				map.put("objs", objs_str.toString());
			}
			pointsMap.put(pointid1, map);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e); 
		}
		return pointsMap;
	}
	
	/**
	 * 获得所有指标Excel表格的数据 1 如果标题行某一个单元格子为空，就报错。 2 如果某人员不属于该计划下的考核对象，则不进行数据的导入 3 如果某分值的单元格为空，则按0来导入其值 4.数据行之间不要有空行,则报错。 要求：第一行为标题行，下面的行数为数据行；第一列为姓名列，其余各列为分值列，第一行由姓名和最下层的项目名称组成。
	 * JinChunhai 2011.04.01
	 */
	public HashMap getExcelDatas_special(File file) throws GeneralException
	{	
		HashMap obj_pointsMap = new HashMap();
		HashMap pointsMap = new HashMap();
		try
		{
			// HSSFWorkbook wb = null;
			// HSSFSheet sheet = null;
			Workbook wb = null;
			Sheet sheet = null;
			InputStream stream=null;
			try
			{
				stream=new FileInputStream(file);
				wb = WorkbookFactory.create(stream);
				sheet = wb.getSheetAt(0);
	
				// wb = new HSSFWorkbook(form_file.getInputStream());
				// sheet = wb.getSheetAt(0);
			} catch (Exception e)
			{
				System.out.println(e);
			}finally
            {
                PubFunc.closeIoResource(stream);
                PubFunc.closeIoResource(wb);
            }
	
/*			ArrayList list = this.getPointList();
			HashMap nameMap = new HashMap();// 正确的应该导入的指标名称  key为指标name 
			HashMap idMap = new HashMap();  // 正确的应该导入的指标名称  key为指标id 
			for (int i = 0; i < list.size(); i++)
			{
				CommonData bo = (CommonData) list.get(i); 
				nameMap.put(bo.getDataName(), bo.getDataValue());
				idMap.put(bo.getDataValue(), bo.getDataName());
			}
*/	
			//数据行
//			int sheetRow = list.size();  // 单个考核对象所占的行数
//			int counter = 0;             // 计数器
			Row row = null;
			int rows = sheet.getPhysicalNumberOfRows();  // 获取导入Excel的行数
			StringBuffer objs = new StringBuffer();// 有效的考核对象
//			ArrayList titles = new ArrayList();    // 指标 项目1，项目2，
			HashMap testItems = new HashMap();     // 存放指标下项目id 
			String object_id = "";                 // 考核对象id
			String point_id = "";	   // 指标编号
			String point_Id = "";	   // 指标编号
			String logo = "false";          // 标识
			int intCount = 0;          // 某指标下项目的个数
			int s1 = 0; 
			int s2 = 0; 
			String[] scores = null;
			String[] obj_DF = null;
			for (int j = 1; j < rows; j++)
			{
				row = sheet.getRow(j);
				if(row==null)
					throw new GeneralException("请用下载的模板导入目标数据！");
				
//				if(row==null)
//					continue;
				
				int cols = row.getPhysicalNumberOfCells(); // 获取当前行的cell数
				
				//姓名列
				int nameCol = 0;
				int numCol = 0;
				if(this.planVo.getInt("object_type")!=2)
				{
					nameCol = 0;
					numCol = 1;
				}else
				{
					if(this.onlyname != null && !"a0101".equals(this.onlyname))// 部门，姓名，唯一标志列都存在 取唯一标志列
					{
						nameCol = 2;
						numCol = 3;
					}else// 部门，姓名 存在 取姓名列
					{
						nameCol = 1;
						numCol = 2;
					}
				}
				Cell cell = null;								
				String name = "";	
				DataCollectBo nowBo = null;
				boolean isPriv=true;
				for (short n = (short)numCol; n < cols; n++)
				{				
//					String value = "";
					cell = row.getCell(n);
					String poi_id = "";	
					String common="";
					if(n==numCol)
					{	
						poi_id = cell.getStringCellValue();						
						if(poi_id==null || poi_id.trim().length()<=0)
						{
							nowBo = new DataCollectBo(this.cn, this.planid, point_Id, this.userview);
							continue;
						}
						if((cell.getCellComment())==null)
							throw GeneralExceptionHandler.Handle(new Exception("导入数据不成功！可能的原因为:1.导入的Excel模板不正确; 2.未设置指标[" + poi_id + "]的批注！"));
						
						common = cell.getCellComment().getString().getString().trim();
						point_Id =common; //(String) nameMap.get(poi_id+":"+);
						nowBo = new DataCollectBo(this.cn, this.planid, point_Id, this.userview);	
									
						if (point_Id == null)
							throw GeneralExceptionHandler.Handle(new Exception("指标名称[" + poi_id + "]不正确！"));
						
						if(!(this.userview.isSuper_admin()))
						{
							if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD,point_Id))
							{
								isPriv=false;
								continue;
							}
						}
						
						cell = row.getCell((short) nameCol);						
						name = getCellValue(cell);
						if ((name!=null) && (name.trim().length()>0))																	
						{													
							logo = "true";
							object_id = "";  // 初始化
							objs.setLength(0);
							pointsMap = new HashMap();
																			
							object_id = nowBo.getObjIdByName(name);
							if ("".equals(object_id))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
								continue;														
							
							objs.append(object_id + "<@>");																		
						}
						
					}else
						break;
				}
				
				if(!isPriv)
					continue;
				
//				counter++;
				HashMap map = new HashMap();
				
				String pricticalVal = "";  // 实际值
				String standVal = "";      // 标准值
				ArrayList alist = new ArrayList();	
				
//				DataCollectBo nowBo = null;
				String itemCount = "";   // 某指标下项目的个数
				String pointId = "";
				String itemId = "";  // 指标项目
				String itemid = "";	// 指标项目 id
				for (short c = (short)numCol; c < cols; c++)
				{				
					cell = row.getCell(c);											
					if(c==numCol)
					{													
						pointId = cell.getStringCellValue();
						if(pointId==null || pointId.trim().length()<=0)	
							continue;
						else
						{     
							s1 = 0;                       // 初始化
							s2 = 0;                       // 初始化
							point_id = "";	             // 初始化 指标编号
							testItems = new HashMap();   // 初始化 存放指标下项目id 
//							titles = new ArrayList();    // 初始化 指标 项目1，项目2，
						}
						point_id = cell.getCellComment().getString().getString().trim();
						
						
					//	point_id = (String) nameMap.get(pointId);							
						itemCount = getItemCount(point_id);  // 某指标下项目的个数
						intCount = Integer.parseInt(itemCount);
						scores = new String[intCount];
						obj_DF = new String[intCount];
						
//						nowBo = new DataCollectBo(this.cn, this.planid, point_id, this.userview);
						ArrayList allItems = nowBo.getAllItems();// 应该导入的加扣分指标的最底层项目						
						for (int m = 0; m < allItems.size(); m++)
						{
							LazyDynaBean temp = (LazyDynaBean) allItems.get(m);
							String itemCode = (String) temp.get("item");
							testItems.put(itemCode, "");
						}
						
						if (point_id == null)
							throw GeneralExceptionHandler.Handle(new Exception("指标名称[" + pointId + "]不正确！"));
					}
					
					if(c==numCol+1)
					{
						itemId = cell.getStringCellValue();
						itemId = itemId.trim();
						if ("".equals(itemId))
							continue;						
																								
//						itemid = nowBo.getItemidByName(itemId);						
						itemid = cell.getCellComment().getString().getString().trim();
						if (testItems.get(itemid) == null)
							throw GeneralExceptionHandler.Handle(new Exception("指标[" + pointId + "]和它的底层项目名称不匹配！"));
//						titles.add(itemid);												
						
					}
					if(c==(cols-1))
					{	
						if (cell != null)
						{
							switch (cell.getCellType())
							{
								case Cell.CELL_TYPE_FORMULA:
									break;
								case Cell.CELL_TYPE_NUMERIC:
									double x = cell.getNumericCellValue();
									// pricticalVal = String.valueOf((long)
									// cell1.getNumericCellValue());
									pricticalVal = Double.toString(x);
									break;
								case Cell.CELL_TYPE_STRING:
									pricticalVal = cell.getStringCellValue();
									break;
								default:
									pricticalVal = "";
							}														
							
							if (!this.isDataType("N", pricticalVal))
							{
								String msg = "源数据(" + pointId + ")中数据:" + pricticalVal + " 不符合格式!";
								throw new GeneralException(msg);
							}
														
							if ("0".equals(itemCount))   // 基本型指标
							{
								String pointype = nowBo.getTypeOfPoint();
								String rule = nowBo.getRule();
								if (rule == null)
									throw new GeneralException("指标【" + pointId + "】的计分规则没有定义！");										
									
								if ("0".equals(pointype) && "0".equals(rule)) // 基本指标录分规则导入Excel
								{
									HashMap basicFenMap = nowBo.getBasicFen();
									ArrayList list1 = new ArrayList();
									
									pricticalVal = pricticalVal.trim();
									if ("".equals(pricticalVal))
										pricticalVal = "0";
		
									String basicVal = (String) basicFenMap.get(object_id);
									basicVal = (basicVal == null || !(basicVal == null) && "".equals(basicVal)) ? "0" : basicVal;
									if((Float.parseFloat(basicVal)<0 && (Float.parseFloat(pricticalVal)>0 || Float.parseFloat(pricticalVal)<Float.parseFloat(basicVal)))
											|| (Float.parseFloat(basicVal)>0 && (Float.parseFloat(pricticalVal)<0 || Float.parseFloat(pricticalVal)>Float.parseFloat(basicVal))))
										throw GeneralExceptionHandler.Handle(new Exception("指标[" + pointId + "]超出了指定的分值范围！"));								
		
									LazyDynaBean bean1 = new LazyDynaBean();
									bean1.set("pricticalVal", pricticalVal);
									bean1.set("object_id", object_id);
									list1.add(bean1);
								
		
									String[] prictical_Val = new String[list1.size()];
									for (int k = 0; k < list1.size(); k++)
									{
										LazyDynaBean theBean = (LazyDynaBean) list1.get(k);
										String objId = (String) theBean.get("object_id");
										prictical_Val[k] = objId + "=" + (String) theBean.get("pricticalVal");
									}
									if (list1.size() > 0)
									{
										map.put("fzScores", prictical_Val);
										map.put("objs", objs.toString());
									}
									
								}else if ("0".equals(pointype) && !"0".equals(rule)) // 基本指标非录分规则导入Excel
								{
									if ("3".equals(rule))// 排名的保存
									{
										ArrayList list4 = new ArrayList();
										
										pricticalVal = pricticalVal.trim();
										if ("".equals(pricticalVal))
											pricticalVal = "0";
		
										String standValme = nowBo.getStandardFen(object_id);
										String basicVal = (String) nowBo.getBasicFen().get(object_id);
										LazyDynaBean bean4 = new LazyDynaBean();
										bean4.set("pricticalVal", pricticalVal);
										bean4.set("object_id", object_id);
										bean4.set("standVal", standValme);
										bean4.set("basicVal", basicVal == null ? "" : basicVal);
										list4.add(bean4);
											
										String[] prictical_Val = new String[list4.size()];
										String[] stand_Val = new String[list4.size()];
										String[] basic_Val = new String[list4.size()];
		
										for (int n = 0; n < list4.size(); n++)
										{
											LazyDynaBean theBean = (LazyDynaBean) list4.get(n);
											String objId = (String) theBean.get("object_id");
											prictical_Val[n] = objId + "_pratical=" + (String) theBean.get("pricticalVal");
											stand_Val[n] = objId + "_standard=" + (String) theBean.get("standVal");
											basic_Val[n] = objId + "_basic=" + (String) theBean.get("basicVal");
										}
										if (list4.size() > 0)
										{
											map.put("pricticalVal", prictical_Val);
											map.put("standVal", stand_Val);
											map.put("basicVal", basic_Val);
											map.put("objs", objs.toString());
										}
		
									} else if ("1".equals(rule) || "2".equals(rule))// 简单｜分段导入Excel
									{
										ArrayList list2 = new ArrayList();
										
										pricticalVal = pricticalVal.trim();
										if ("".equals(pricticalVal))
											pricticalVal = "0";
										if("---".equalsIgnoreCase(pricticalVal))
											continue;
										String standValme = nowBo.getStandardFen(object_id);
										String basicVal = (String) nowBo.getBasicFen().get(object_id);
										basicVal = basicVal == null ? "0" : basicVal;
										// 基本型指标简单和分段计算规则的数据计算
										HashMap map1 = nowBo.basciPointCalcu(pricticalVal, basicVal, standValme);
										String addF = (String) map1.get("addF");
										String deducF = (String) map1.get("deducF");
										String objDF = (String) map1.get("objDF");
		
										LazyDynaBean bean1 = new LazyDynaBean();
										bean1.set("pricticalVal", pricticalVal);
										bean1.set("object_id", object_id);
										bean1.set("standVal", standValme);
										bean1.set("basicVal", basicVal);
										bean1.set("addF", "".equals(addF) ? "0" : addF);
										bean1.set("deducF", "".equals(deducF) ? "0" : deducF);
										bean1.set("objDF", "".equals(objDF) ? "0" : objDF);
										list2.add(bean1);																							
		
										String[] prictical_Val = new String[list2.size()];
										String[] stand_Val = new String[list2.size()];
										String[] basic_Val = new String[list2.size()];
										String[] addFne = new String[list2.size()];
										String[] deducFne = new String[list2.size()];
										String[] objDFne = new String[list2.size()];
		
										for (int m = 0; m < list2.size(); m++)
										{
											LazyDynaBean theBean = (LazyDynaBean) list2.get(m);
											String objId = (String) theBean.get("object_id");
											prictical_Val[m] = objId + "_pratical=" + (String) theBean.get("pricticalVal");
											stand_Val[m] = objId + "_standard=" + (String) theBean.get("standVal");
											addFne[m] = objId + "_add=" + (String) theBean.get("addF");
											basic_Val[m] = objId + "_basic=" + (String) theBean.get("basicVal");
											objDFne[m] = objId + "_df=" + (String) theBean.get("objDF");
											deducFne[m] = objId + "_deduc=" + (String) theBean.get("deducF");
										}
										if (list2.size() > 0)
										{
											map.put("pricticalVal", prictical_Val);
											map.put("standVal", stand_Val);
											map.put("addF", addFne);
											map.put("basicVal", basic_Val);
											map.put("objDF", objDFne);
											map.put("deducF", deducFne);
											map.put("objs", objs.toString());
										}
									}
								}
							} else   // 加减分指标
							{																																
								String[] dfScores = new String[1];								
								pricticalVal = pricticalVal.trim();
								if ("".equals(pricticalVal))
									pricticalVal = "0";
								if("---".equalsIgnoreCase(pricticalVal))
									continue;
								
								scores[s1++] = object_id + "_" + itemid + "=" + pricticalVal;
								obj_DF[s2++] = object_id + "_" + itemid + "=" + pricticalVal;
										
								intCount--;								
								if(intCount==0)
								{
									HashMap dfMap = nowBo.computDF(object_id, obj_DF);
									String dfVal = (String) dfMap.get("df");
									dfScores[0] = object_id + "=" + ("".equals(dfVal) ? "0" : dfVal);
									
									map.put("scores", scores);
									map.put("dfScores", dfScores);
									map.put("objs", objs.toString());									
								}								
							}
						}
						if(("0".equals(itemCount)) || (intCount==0))
							pointsMap.put(point_id, map);
					}
				}				
				
//				cell = row.getCell((short) nameCol);						
//				name = getCellValue(cell);
				if ("true".equalsIgnoreCase(logo))
				{				
					obj_pointsMap.put(object_id, pointsMap);
				}
			}
//			return obj_pointsMap;
		}catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e); 
		}
		return obj_pointsMap;
	}
	
	private String getCellValue(Cell cell) 
	{
		String result = "";
		switch (cell.getCellType()) 
		{
			case HSSFCell.CELL_TYPE_BLANK:
				result = "";
				break;
			case HSSFCell.CELL_TYPE_STRING:
				result = cell.getRichStringCellValue().getString();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				result = String.valueOf(cell.getNumericCellValue());
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				result = "";
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				result = String.valueOf(cell.getBooleanCellValue());
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				result = "";
				break;
			default:
				result = cell.getRichStringCellValue().getString();
		}
		return result;
	}
	
	/**
	 * 判断 值类型是否与 要求的类型一致
	 * 
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
	public boolean isDataType(String itemtype, String value)
	{

		boolean flag = true;
		if(value.trim().length()==0)
			return flag;
		if ("N".equals(itemtype))
		{			
			flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");			

		} else if ("D".equals(itemtype))
		{
			flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
	}
	
	public String getItemCount(String pointid) throws GeneralException
	{

		String num = "0";
		String type = getTypeOfPoint1(pointid);
		if ("0".equals(type) || "".equals(type))// 基本指标
			return num;
		else
		{
			DataCollectBo bo = new DataCollectBo(this.cn, this.planid, pointid, this.userview);
			int n = bo.getAllItems().size();
			num = Integer.toString(n);
		}
		return num;
	}

	/** 基本型指标－简单和分段情况的数据导入 */
//	public HashMap getExcelData1(FormFile form_file) throws GeneralException
//	{
//
//		HashMap map = new HashMap();
//		HSSFWorkbook wb = null;
//		HSSFSheet sheet = null;
//		try
//		{
//			wb = new HSSFWorkbook(form_file.getInputStream());
//			sheet = wb.getSheetAt(0);
//		} catch (Exception e)
//		{
//			System.out.println(e);
//		}
//		int cols = 0;
//		// 获得列名
//		HSSFRow row = sheet.getRow(0);
//		// 姓名，实际值，
//		if (row != null)
//		{
//			cols = row.getPhysicalNumberOfCells();
//			for (short c = 0; c < cols; c++)
//			{
//				String value = "";
//				HSSFCell cell = row.getCell(c);
//				if (cell != null)
//				{
//					switch (cell.getCellType())
//					{
//					case HSSFCell.CELL_TYPE_FORMULA:
//						break;
//					case HSSFCell.CELL_TYPE_NUMERIC:
//						value = String.valueOf((long) cell.getNumericCellValue());
//						break;
//					case HSSFCell.CELL_TYPE_STRING:
//						value = cell.getStringCellValue();
//						break;
//					default:
//						value = "";
//					}
//				}
//				value = value.trim();
//				if (value.equals(""))
//					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
//			}
//		}
//
//		int rows = sheet.getPhysicalNumberOfRows();
//		if (rows * cols == 0 || rows == 1)
//			return map;
//
//		ArrayList list = new ArrayList();
//		// 取数据
//		for (int j = 1; j < rows; j++)
//		{
//			HSSFRow row1 = sheet.getRow(j);
//			if (row1 != null)
//			{
//				// 获得姓名列，由姓名查找object_id
//				HSSFCell cell1 = row1.getCell((short) 0);
//				String name = cell1.getStringCellValue();
//				if (name.trim().equals(""))// 姓名列为空，这一行数据就不导入
//					continue;
//				String object_id = getObjIdByName(name);
//				if (object_id.equals(""))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
//					continue;
//				String pricticalVal = "";// 实际值
//				cell1 = row1.getCell((short) 1);
//				if (cell1 != null)
//				{
//					switch (cell1.getCellType())
//					{
//					case HSSFCell.CELL_TYPE_FORMULA:
//						break;
//					case HSSFCell.CELL_TYPE_NUMERIC:
//						pricticalVal = String.valueOf((long) cell1.getNumericCellValue());
//						break;
//					case HSSFCell.CELL_TYPE_STRING:
//						pricticalVal = cell1.getStringCellValue();
//						break;
//					default:
//						pricticalVal = "";
//					}
//				}
//				pricticalVal = pricticalVal.trim();
//				if (pricticalVal.equals(""))
//					pricticalVal = "0";
//
//				String standVal = this.getStandardFen(object_id);
//				HashMap basicFenMap = this.getBasicFen();
//				String basicVal = (String) basicFenMap.get(object_id);
//				basicVal = basicVal == null ? "" : basicVal;
//				// 基本型指标简单和分段计算规则的数据计算
//				HashMap map1 = this.basciPointCalcu(pricticalVal, basicVal, standVal);
//				String addF = (String) map1.get("addF");
//				String deducF = (String) map1.get("deducF");
//				String objDF = (String) map1.get("objDF");
//
//				LazyDynaBean bean = new LazyDynaBean();
//				bean.set("pricticalVal", pricticalVal);
//				bean.set("object_id", object_id);
//				bean.set("standVal", standVal);
//				bean.set("basicVal", basicVal);
//				bean.set("addF", addF);
//				bean.set("deducF", deducF);
//				bean.set("objDF", objDF);
//				list.add(bean);
//			} else
//				throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
//		}
//
//		String[] pricticalVal = new String[list.size()];
//		String[] standVal = new String[list.size()];
//		String[] basicVal = new String[list.size()];
//		String[] addF = new String[list.size()];
//		String[] deducF = new String[list.size()];
//		String[] objDF = new String[list.size()];
//
//		for (int i = 0; i < list.size(); i++)
//		{
//			LazyDynaBean theBean = (LazyDynaBean) list.get(i);
//			String objId = (String) theBean.get("object_id");
//			pricticalVal[i] = objId + "_pratical=" + (String) theBean.get("pricticalVal");
//			standVal[i] = objId + "_standard=" + (String) theBean.get("standVal");
//			addF[i] = objId + "_add=" + (String) theBean.get("addF");
//			basicVal[i] = objId + "_basic=" + (String) theBean.get("basicVal");
//			objDF[i] = objId + "_df=" + (String) theBean.get("objDF");
//			deducF[i] = objId + "_deduc=" + (String) theBean.get("deducF");
//		}
//		if (list.size() > 0)
//		{
//			map.put("pricticalVal", pricticalVal);
//			map.put("standVal", standVal);
//			map.put("addF", addF);
//			map.put("basicVal", basicVal);
//			map.put("objDF", objDF);
//			map.put("deducF", deducF);
//		}
//		return map;
//	}
//
//	/** 基本型指标－计算规则为排名情况的数据导入 */
//	public HashMap getExcelData2(FormFile form_file) throws GeneralException
//	{
//
//		HashMap map = new HashMap();
//		HSSFWorkbook wb = null;
//		HSSFSheet sheet = null;
//		try
//		{
//			wb = new HSSFWorkbook(form_file.getInputStream());
//			sheet = wb.getSheetAt(0);
//		} catch (Exception e)
//		{
//			System.out.println(e);
//		}
//		int cols = 0;
//		// 获得列名
//		HSSFRow row = sheet.getRow(0);
//		// 姓名，实际值，
//		if (row != null)
//		{
//			cols = row.getPhysicalNumberOfCells();
//			for (short c = 0; c < cols; c++)
//			{
//				String value = "";
//				HSSFCell cell = row.getCell(c);
//				if (cell != null)
//				{
//					switch (cell.getCellType())
//					{
//					case HSSFCell.CELL_TYPE_FORMULA:
//						break;
//					case HSSFCell.CELL_TYPE_NUMERIC:
//						value = String.valueOf((long) cell.getNumericCellValue());
//						break;
//					case HSSFCell.CELL_TYPE_STRING:
//						value = cell.getStringCellValue();
//						break;
//					default:
//						value = "";
//					}
//				}
//				value = value.trim();
//				if (value.equals(""))
//					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
//			}
//		}
//
//		int rows = sheet.getPhysicalNumberOfRows();
//		if (rows * cols == 0 || rows == 1)
//			return map;
//
//		ArrayList list = new ArrayList();
//		// 取数据
//		for (int j = 1; j < rows; j++)
//		{
//			HSSFRow row1 = sheet.getRow(j);
//			if (row1 != null)
//			{
//				// 获得姓名列，由姓名查找object_id
//				HSSFCell cell1 = row1.getCell((short) 0);
//				String name = cell1.getStringCellValue();
//				if (name.trim().equals(""))// 姓名列为空，这一行数据就不导入
//					continue;
//				String object_id = getObjIdByName(name);
//				if (object_id.equals(""))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
//					continue;
//				String pricticalVal = "";// 实际值
//				cell1 = row1.getCell((short) 1);
//				if (cell1 != null)
//				{
//					switch (cell1.getCellType())
//					{
//					case HSSFCell.CELL_TYPE_FORMULA:
//						break;
//					case HSSFCell.CELL_TYPE_NUMERIC:
//						pricticalVal = String.valueOf((long) cell1.getNumericCellValue());
//						break;
//					case HSSFCell.CELL_TYPE_STRING:
//						pricticalVal = cell1.getStringCellValue();
//						break;
//					default:
//						pricticalVal = "";
//					}
//				}
//				pricticalVal = pricticalVal.trim();
//				if (pricticalVal.equals(""))
//					pricticalVal = "0";
//
//				String standVal = this.getStandardFen(object_id);
//				String basicVal = (String) this.getBasicFen().get(object_id);
//				basicVal = basicVal == null ? "" : basicVal;
//				LazyDynaBean bean = new LazyDynaBean();
//				bean.set("pricticalVal", pricticalVal);
//				bean.set("object_id", object_id);
//				bean.set("standVal", standVal);
//				bean.set("basicVal", basicVal);
//				list.add(bean);
//			} else
//				throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
//		}
//
//		String[] pricticalVal = new String[list.size()];
//		String[] standVal = new String[list.size()];
//		String[] basicVal = new String[list.size()];
//
//		for (int i = 0; i < list.size(); i++)
//		{
//			LazyDynaBean theBean = (LazyDynaBean) list.get(i);
//			String objId = (String) theBean.get("object_id");
//			pricticalVal[i] = objId + "_pratical=" + (String) theBean.get("pricticalVal");
//			standVal[i] = objId + "_standard=" + (String) theBean.get("standVal");
//			basicVal[i] = objId + "_basic=" + (String) theBean.get("basicVal");
//		}
//		if (list.size() > 0)
//		{
//			map.put("pricticalVal", pricticalVal);
//			map.put("standVal", standVal);
//			map.put("basicVal", basicVal);
//		}
//		return map;
//	}
//
//	/** 基本型指标－录分规则导入Excel */
//	public HashMap getExcelData3(FormFile form_file) throws GeneralException
//	{
//
//		HashMap map = new HashMap();
//		HSSFWorkbook wb = null;
//		HSSFSheet sheet = null;
//		try
//		{
//			wb = new HSSFWorkbook(form_file.getInputStream());
//			sheet = wb.getSheetAt(0);
//		} catch (Exception e)
//		{
//			System.out.println(e);
//		}
//		
//		int cols = 0;
//		// 获得列名
//		HSSFRow row = sheet.getRow(0);
//		// 姓名，分值，
//		if (row != null)
//		{
//			cols = row.getPhysicalNumberOfCells();
//			for (short c = 0; c < cols; c++)
//			{
//				String value = "";
//				HSSFCell cell = row.getCell(c);
//				if (cell != null)
//				{
//					switch (cell.getCellType())
//					{
//					case HSSFCell.CELL_TYPE_FORMULA:
//						break;
//					case HSSFCell.CELL_TYPE_NUMERIC:
//						value = String.valueOf((long) cell.getNumericCellValue());
//						break;
//					case HSSFCell.CELL_TYPE_STRING:
//						value = cell.getStringCellValue();
//						break;
//					default:
//						value = "";
//					}
//				}
//				value = value.trim();
//				if (value.equals(""))
//					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
//			}
//		}
//
//		int rows = sheet.getPhysicalNumberOfRows();
//		if (rows * cols == 0 || rows == 1)
//			return map;
//		HashMap basicFenMap = this.getBasicFen();
//		ArrayList list = new ArrayList();
//		// 取数据
//		for (int j = 1; j < rows; j++)
//		{
//			HSSFRow row1 = sheet.getRow(j);
//			if (row1 != null)
//			{
//				// 获得姓名列，由姓名查找object_id
//				HSSFCell cell1 = row1.getCell((short) 0);
//				String name = cell1.getStringCellValue();
//				if (name.trim().equals(""))// 姓名列为空，这一行数据就不导入
//					continue;
//				String object_id = getObjIdByName(name);
//				if (object_id.equals(""))// 姓名不属于某考核计划的考核对象，这一行数据就不导入
//					continue;
//				String pricticalVal = "";// 分值
//				cell1 = row1.getCell((short) 1);
//				if (cell1 != null)
//				{
//					switch (cell1.getCellType())
//					{
//					case HSSFCell.CELL_TYPE_FORMULA:
//						break;
//					case HSSFCell.CELL_TYPE_NUMERIC:
//						pricticalVal = String.valueOf((long) cell1.getNumericCellValue());
//						break;
//					case HSSFCell.CELL_TYPE_STRING:
//						pricticalVal = cell1.getStringCellValue();
//						break;
//					default:
//						pricticalVal = "";
//					}
//				}
//				pricticalVal = pricticalVal.trim();
//				if (pricticalVal.equals(""))
//					pricticalVal = "0";
//				String basicVal = (String) basicFenMap.get(object_id);
//				basicVal = (basicVal == null || !(basicVal == null) && basicVal.equals("")) ? "0" : basicVal;
//				if (Float.parseFloat(basicVal) < Float.parseFloat(pricticalVal))
//					throw GeneralExceptionHandler.Handle(new Exception("该指标的最大值为" + basicVal + ",导入数值中存在大于它的数！"));
//
//				LazyDynaBean bean = new LazyDynaBean();
//				bean.set("pricticalVal", pricticalVal);
//				bean.set("object_id", object_id);
//				list.add(bean);
//			} else
//				throw GeneralExceptionHandler.Handle(new Exception("数据中间不允许有空行存在！"));
//		}
//
//		String[] pricticalVal = new String[list.size()];
//		for (int i = 0; i < list.size(); i++)
//		{
//			LazyDynaBean theBean = (LazyDynaBean) list.get(i);
//			String objId = (String) theBean.get("object_id");
//			pricticalVal[i] = objId + "=" + (String) theBean.get("pricticalVal");
//		}
//		if (list.size() > 0)
//			map.put("fzScores", pricticalVal);
//
//		return map;
//	}

	/**通过人员姓名找到编码 通过团队名称找到机构id
	 *@return 如果找不到这个对象返回空
	 *@return 如果找到了但是这个对象没有这个指标的权限返回空
	 * */
	public String getObjIdByName(String name)
	{
		String object_id = "";
		RowSet rs = null;
		try
		{
			String sql="";
			ContentDAO dao = new ContentDAO(this.cn);
			if(this.getPlanVo().getInt("object_type")!=2)
			{
				if(Integer.parseInt(this.display_e0122)!=0)
				{
					String[] temp =name.split(this.seprartor) ;
					name = temp[temp.length-1];
				}
				sql = "select b0110,e0122,object_id from per_object where a0101='" + name + "' and plan_id='" + this.planid + "'";
				rs = dao.search(sql);
				if (rs.next())
				{
					object_id = rs.getString("object_id") == null ? "" : rs.getString("object_id");
					String b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
					String e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
					if(this.point.trim().length()>0)
					{
						boolean right = true;
						right = this.pointPrivBean.getPrivPoint("", object_id, this.point);						
						if(right==false)
							object_id="";	
					}
				}			
			}else
			{
				if(this.onlyname != null && !"a0101".equals(this.onlyname))//由唯一性指标的值取得a0100
					object_id = (String)this.onlyValueMap2.get(name);
				else
					object_id = (String)this.onlyValueMap3.get(name);//由姓名字段取得a0100
				sql = "select b0110,e0122,object_id from per_object where object_id='" + object_id + "' and plan_id='" + this.planid + "'";
				rs = dao.search(sql);
				if (object_id!=null && object_id.trim().length()>0 && rs.next())
				{
					object_id = rs.getString("object_id") == null ? "" : rs.getString("object_id");
					String b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
					String e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
					if(this.point.trim().length()>0)
					{
						boolean right = true;						
						right = this.pointPrivBean.getPrivPoint(b0110, e0122, this.point);
						if(right==false)
							object_id="";	
					}
				}
			}
			if(rs!=null)
				rs.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return object_id;
	}

	/** 根据项目名称取项目编号 */
	public String getItemidByName(String name)
	{

		String item_id = "";
		String sql = "select item_id from per_standard_item where itemdesc='" + name + "' and point_id='" + this.point + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				item_id = rs.getString("item_id") == null ? "" : rs.getString("item_id");
			
			if(rs!=null)
				rs.close();
				
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return item_id;
	}

	/** 根据项目名称取项目编号 */
	public String getItemidByName(String name, String pointId)
	{

		String item_id = "";
		String sql = "select item_id from per_standard_item where itemdesc='" + name + "' and point_id='" + pointId + "'";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			RowSet rs = dao.search(sql);
			if (rs.next())
				item_id = rs.getString("item_id") == null ? "" : rs.getString("item_id");
			
			if(rs!=null)
				rs.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return item_id;
	}

	public ArrayList getDataList()
	{

		return dataList;
	}

	public HSSFRichTextString cellStr(String context)
	{

		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	// 生成Excel
	public void createExcel(HSSFWorkbook wb, String pointName, String title) throws GeneralException
	{
		try{
		// 定义两种格式HSSFCellStyle
		// 第一种style--字体20，水平居中
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 18);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font1);
//		style1.setWrapText(true);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBottomBorderColor((short) 8);
		style1.setLeftBorderColor((short) 8);
		style1.setRightBorderColor((short) 8);
		style1.setTopBorderColor((short) 8);
		
		// 第二种style--字体10，水平居中，垂直居中，黑色边框，自动换行
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setBottomBorderColor((short) 8);
		style2.setLeftBorderColor((short) 8);
		style2.setRightBorderColor((short) 8);
		style2.setTopBorderColor((short) 8);

		String type = getTypeOfPoint();
		if ("0".equals(type) || "".equals(type))// 基本指标
		{
			String pointctrl = this.getPointctrl();
			HashMap map = PointCtrlXmlBo.getAttributeValues(pointctrl);
			String rule = (String) map.get("computeRule");
			if (rule != null && "0".equals(rule))
				createExcel3(wb, pointName, title, style1, style2);// 基本指标录分
			else if (rule != null && !"0".equals(rule))
			{
				if (!this.isHavePoint())
					return;
				createExcel1(wb, pointName, title, rule, style1, style2); // 基本指标非录分
			}
		} else
			createExcel2(wb, pointName, title, type, style1, style2); // 加减分指标
		}catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e); 
		}
	}
	// 生成Excel
	public void createExcelByXGQ(HSSFWorkbook wb,Connection conn, String planID,String title,ArrayList list ,UserView userView) throws GeneralException
	{
		try{
		// 定义两种格式HSSFCellStyle
		// 第一种style--字体20，水平居中
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 18);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font1);
//		style1.setWrapText(true);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBottomBorderColor((short) 8);
		style1.setLeftBorderColor((short) 8);
		style1.setRightBorderColor((short) 8);
		style1.setTopBorderColor((short) 8);
		
		// 第二种style--字体10，水平居中，垂直居中，黑色边框，自动换行
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setBottomBorderColor((short) 8);
		style2.setLeftBorderColor((short) 8);
		style2.setRightBorderColor((short) 8);
		style2.setTopBorderColor((short) 8);
		
		
//		String type = getTypeOfPoint();
		createExcel2Byxgq(wb, title,list, style1, style2, conn,  planID, userView);
//		if (type.equals("0") || type.equals(""))// 基本指标
//		{
//			String pointctrl = this.getPointctrl();
//			HashMap map = PointCtrlXmlBo.getAttributeValues(pointctrl);
//			String rule = (String) map.get("computeRule");
//			if (rule != null && rule.equals("0"))
//				createExcel3(wb, "pointName", title, style1, style2);// 基本指标录分
//			else if (rule != null && !rule.equals("0"))
//			{
//				if (!this.isHavePoint())
//					return;
//				createExcel1(wb, "pointName", title, rule, style1, style2); // 基本指标非录分
//			}
//		} else
//			createExcel2(wb, "pointName", title, type, style1, style2); // 加减分指标
		}catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e); 
		}
	}
	/**
	 * 加减分指标导出Excel
	 * 
	 * @throws GeneralException
	 */
	public void createExcel2(HSSFWorkbook wb, String pointName, String title, String type, HSSFCellStyle style1, HSSFCellStyle style2) throws GeneralException
	{

		String maxlay = this.getMaxLay();
		if ("0".equals(maxlay))
			return;
		int object_type = this.getPlanVo().getInt("object_type");
		HSSFSheet sheet = wb.createSheet(pointName);
		int len = this.getAllItems().size();
		// 写表头
		HSSFRow row = sheet.createRow(0);
		if(object_type!=2)
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (len + 4));
		else
		{
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
				ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (len + 6));
			else
				ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (len + 5));
		}
		HSSFCell cell = row.createCell((short) 0);

		cell.setCellValue(cellStr(title));
		cell.setCellStyle(style1);

		sheet.setColumnWidth((short) 0, (short) 1500);
		sheet.setColumnWidth((short) 1, (short) 6000);
		ArrayList nextLayChild = new ArrayList();

		
		int tempColIndex=0;
		HashMap rowSpanCol = new HashMap();//跨行的列
		for (int i = 1; i <= Integer.parseInt(maxlay); i++)
		{
			row = sheet.getRow(i);
			if(row==null)
				row = sheet.createRow(i);
			ArrayList list = null;
			int colIndex=0;
			if (i == 1)// 最上层
			{
				
				this.executeCell(1, colIndex,  Integer.parseInt(maxlay), colIndex, "序号", style2, sheet);
				colIndex++;
				
				if(object_type!=2)
				{
					this.executeCell(1, colIndex,  Integer.parseInt(maxlay), colIndex, ResourceFactory.getProperty("jx.datacol.khobj"), style2, sheet);
					colIndex++;
				}
				else
				{		
					FieldItem fielditem = DataDictionary.getFieldItem("E0122");
					this.executeCell(1, colIndex,  Integer.parseInt(maxlay), colIndex, fielditem.getItemdesc(), style2, sheet);
					colIndex++;
					this.executeCell(1, colIndex,  Integer.parseInt(maxlay), colIndex, ResourceFactory.getProperty("gz.columns.a0101"), style2, sheet);
					colIndex++;		
					if (this.onlyname != null && !"a0101".equals(this.onlyname))
					{
						FieldItem item = DataDictionary.getFieldItem(this.onlyname);
						this.executeCell(1, colIndex,  Integer.parseInt(maxlay), colIndex, item.getItemdesc(), style2, sheet);
						colIndex++;	
					}
				}
				tempColIndex=colIndex;
				list = getChildren("");
				for (int j = 0; j < list.size(); j++)
				{
					String itemid = (String) list.get(j);
					// 得到最后一代子孙的个数就是要占的列数
					int colspan = getLastChildrenCount(itemid);
					// 得到它的直接孩子存起来作为下一层用
					ArrayList theChildren = this.getChildren(itemid);
					for (int k = 0; k < theChildren.size(); k++)
					{
						String temp = (String) theChildren.get(k);
						nextLayChild.add(temp);
					}
					String childCount = getChildCount(itemid);
					int rowspan = 0;
					if ("0".equals(childCount) && i < Integer.parseInt(maxlay))// 没有下级节点并且不是最后一层要跨行
						rowspan = Integer.parseInt(maxlay) - i + 1;

					String itemdesc = getItemDesc(itemid);
					int lays = 0;
					int opt = 0; // 1-rowspan , 2-colspan
					if (rowspan > 1)// 跨行情况
					{
						lays = rowspan;
						opt = 1;
					} else if (colspan > 1)
					{
						lays = colspan;
						opt = 2;
					}
					if (lays > 0 && opt == 1)// 跨行
					{
						rowSpanCol.put(colIndex+"", new Integer(lays));
						ExportExcelUtil.mergeCell(sheet, 1, (short) colIndex, lays, (short) colIndex);
						for (int k = 0; k < lays; k++)// 为合并行加边框
						{
							HSSFRow row1 = sheet.getRow(i + k);
							if(row1==null)
								row1 = sheet.createRow(i + k);
							HSSFCell cell1 = row1.getCell( colIndex);
							if(cell1==null)
								cell1 = row1.createCell( colIndex);
							cell1.setCellStyle(style2);
						}
					} else if (lays > 0 && opt == 2)// 跨列
					{
						ExportExcelUtil.mergeCell(sheet, 1, (short) colIndex, 1, (short) (colIndex + lays - 1));
						for (int k = 0; k < lays; k++)
						{
							cell = row.getCell((colIndex + k));
							if(cell==null)
								cell = row.createCell( (colIndex + k));
							cell.setCellStyle(style2);
						}
					}
					cell = row.getCell(colIndex);
					if(cell==null)
						cell = row.createCell( colIndex);
					cell.setCellValue(cellStr(itemdesc));
					cell.setCellStyle(style2);
					if (lays > 0 && opt == 2)// 跨列
						colIndex += lays;
					else
						colIndex++;
				}
				
				this.executeCell(1, len + tempColIndex,  Integer.parseInt(maxlay), len + tempColIndex, ResourceFactory.getProperty("jx.datacol.basic"), style2, sheet);
				
				String temptitle = "";
				if (type != null && "1".equals(type))// 1-加分 2-扣分
					temptitle=ResourceFactory.getProperty("jx.datacol.add");
				else if (type != null && "2".equals(type))
					temptitle=ResourceFactory.getProperty("jx.datacol.deduc");
				
				this.executeCell(1, len + tempColIndex+1,  Integer.parseInt(maxlay), len + tempColIndex+1, temptitle, style2, sheet);
				
				this.executeCell(1, len + tempColIndex+2,  Integer.parseInt(maxlay), len + tempColIndex+2, ResourceFactory.getProperty("jx.datacol.result"), style2, sheet);

			} else
			{
				list = nextLayChild;
				nextLayChild = new ArrayList();
				colIndex = tempColIndex;
				if(rowSpanCol.get(colIndex+"")!=null)
					colIndex++;
				for (int j = 0; j < list.size(); j++)
				{
					String itemid = (String) list.get(j);
					// 得到最后一代子孙的个数就是要占的列数
					int colspan = getLastChildrenCount(itemid);
					// 得到它的直接孩子存起来作为下一层用
					ArrayList theChildren = this.getChildren(itemid);
					for (int k = 0; k < theChildren.size(); k++)
					{
						String temp = (String) theChildren.get(k);
						nextLayChild.add(temp);
					}
					String childCount = getChildCount(itemid);
					int rowspan = 0;
					if ("0".equals(childCount) && i < Integer.parseInt(maxlay))// 没有下级节点并且不是最后一层要跨行
						rowspan = Integer.parseInt(maxlay) - i + 1;

					String itemdesc = getItemDesc(itemid);
					int lays = 0;
					int opt = 0; // 1-rowspan , 2-colspan
					if (rowspan > 1)// 跨行情况
					{
						lays = rowspan;
						opt = 1;
					} else if (colspan > 1)
					{
						lays = colspan;
						opt = 2;
					}
					if (lays > 0 && opt == 1)// 跨行
					{
						rowSpanCol=new HashMap();
						rowSpanCol.put(colIndex+"", new Integer(lays));
						ExportExcelUtil.mergeCell(sheet, i, (short) colIndex, i + lays - 1, (short) colIndex);
						for (int k = 0; k < lays; k++)// 为合并行加边框
						{
							HSSFRow row1 = sheet.getRow(i + k);
							if(row1==null)
								sheet.createRow(i + k);
							HSSFCell cell1 = row1.getCell( colIndex);
							if(cell1==null)
								cell1 = row1.createCell(colIndex);
							cell1.setCellStyle(style2);
						}
					} else if (lays > 0 && opt == 2)// 跨列
					{
						ExportExcelUtil.mergeCell(sheet, i, (short) colIndex, i, (short) (colIndex + lays - 1));
						for (int k = 0; k < lays; k++)// 为合并列加边框
						{
							cell = row.getCell( (colIndex + k));
							if(cell== null)
								cell = row.createCell( (colIndex + k));
							cell.setCellStyle(style2);
						}
					}
				
					cell = row.getCell( colIndex);
					if(cell== null)
						cell = row.createCell( colIndex);
					
					cell.setCellValue(cellStr(itemdesc));
					cell.setCellStyle(style2);
					if (lays > 0 && opt == 2)// 跨列
						colIndex += lays;
					else
						colIndex++;
				}
			}
		}

		setData1(type);
		ArrayList items = this.getAllItems();
		for (int x = 0; x < this.dataList.size(); x++)
		{
			row = sheet.createRow(Integer.parseInt(maxlay) + x + 1);
			LazyDynaBean myBean = (LazyDynaBean) this.dataList.get(x);
			String objName = (String) myBean.get("objName");
			String e0122 = (String) myBean.get("e0122");
			String basicf = (String) myBean.get("basicf");
			String df = (String) myBean.get("df");
			String cz = (String) myBean.get("cz");

			cell = row.createCell((short) 0);

			cell.setCellValue(x + 1);
			cell.setCellStyle(style2);

			int y=1;
			if(object_type!=2)
			{
				cell = row.createCell(y);
				cell.setCellValue(cellStr(objName));
				cell.setCellStyle(style2);
				y++;
			}
			else
			{					
				cell = row.createCell(y);
				cell.setCellValue(cellStr(e0122));
				cell.setCellStyle(style2);
				y++;
				
				cell = row.createCell(y);
				cell.setCellValue(cellStr(objName));
				cell.setCellStyle(style2);
				y++;	
				if (this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					String onlynamevalue = (String) myBean.get("onlyname");
					cell = row.createCell(y);
					cell.setCellValue(cellStr(onlynamevalue));
					cell.setCellStyle(style2);
					y++;
				}
			}
			
			int colIndex =y;
			for (int n = 0; n < items.size(); n++)
			{
				LazyDynaBean bean2 = (LazyDynaBean) items.get(n);
				String item = (String) bean2.get("item");
				String s_value = (String) myBean.get(item);

				cell = row.createCell((short) colIndex++);

				cell.setCellValue(cellStr(s_value));
				cell.setCellStyle(style2);
			}

			cell = row.createCell((short) colIndex++);

			cell.setCellValue(cellStr(basicf));
			cell.setCellStyle(style2);

			cell = row.createCell((short) colIndex++);

			cell.setCellValue(cellStr(cz));
			cell.setCellStyle(style2);

			cell = row.createCell((short) colIndex++);

			cell.setCellValue(cellStr(df));
			cell.setCellStyle(style2);

		}
	}

	/** 基本指标非录分导出Excel */
	public void createExcel1(HSSFWorkbook wb, String pointName, String title, String rule, HSSFCellStyle style1, HSSFCellStyle style2) throws GeneralException
	{

		HSSFSheet sheet = wb.createSheet(pointName);
		int object_type = this.getPlanVo().getInt("object_type");
		int len = 7;
		if(object_type==2)
		{
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
				len = 9;
			else				
				len = 8;
		}
		
		// 写表头
		HSSFRow row = sheet.createRow(0);
		if (rule != null && "3".equals(rule))// 排名的基本指标要加上名次列
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (len+1));
		else
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) len);
		HSSFCell cell = row.createCell((short) 0);

		cell.setCellValue(title);
		cell.setCellStyle(style1);

		sheet.setColumnWidth((short) 0, (short) 1500);
		sheet.setColumnWidth((short) 1, (short) 8000);

		row = sheet.createRow(1);
		
		int colIndex=0;
		this.executeCell(1, colIndex,  2, colIndex,"序号", style2, sheet);
		colIndex++;	
	
		if(object_type!=2)
		{
			this.executeCell(1, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.khobj"), style2, sheet);
			colIndex++;
		}
		else
		{		
			FieldItem fielditem = DataDictionary.getFieldItem("E0122");
			this.executeCell(1, colIndex,  2, colIndex, fielditem.getItemdesc(), style2, sheet);
			colIndex++;
			this.executeCell(1, colIndex,  2, colIndex, ResourceFactory.getProperty("gz.columns.a0101"), style2, sheet);
			colIndex++;			
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
			{
				FieldItem item = DataDictionary.getFieldItem(this.onlyname);
				this.executeCell(1, colIndex,  2, colIndex, item.getItemdesc(), style2, sheet);
				colIndex++;	
			}
		}		

		int tempColIndex = colIndex;
		this.executeCell(1, colIndex,  1, colIndex+4, pointName, style2, sheet);
		colIndex=colIndex+4;
		colIndex++;	

		if (rule != null && "3".equals(rule))
		{
			cell = row.createCell(colIndex);			
			
			this.executeCell(1, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.param.mingci"), style2, sheet);
			colIndex++;	
			
			this.executeCell(1, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.result"), style2, sheet);
			colIndex++;	
			
		} else
		{		
			this.executeCell(1, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.result"), style2, sheet);
			colIndex++;	
		}

		colIndex = tempColIndex;
		//第二行数据		
		this.executeCell(2, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.standard"), style2, sheet);
		colIndex++;	
		
		this.executeCell(2, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.pratical"), style2, sheet);
		colIndex++;
		
		this.executeCell(2, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.basic"), style2, sheet);
		colIndex++;

		this.executeCell(2, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.add"), style2, sheet);
		colIndex++;
		
		this.executeCell(2, colIndex,  2, colIndex, ResourceFactory.getProperty("jx.datacol.deduc"), style2, sheet);
		colIndex++;
		

		setData2(rule);
		int rowIndex = 3;
		colIndex=0;
		for (int x = 0; x < this.dataList.size(); x++)
		{
		
			LazyDynaBean myBean = (LazyDynaBean) this.dataList.get(x);
			String objName = (String) myBean.get("objName");
			String e0122 = (String) myBean.get("e0122");
			String standard = (String) myBean.get("standard");
			String pratical = (String) myBean.get("pratical");
			String basic = (String) myBean.get("basic");
			String deduc = (String) myBean.get("deduc");
			String add = (String) myBean.get("add");
			String rank = (String) myBean.get("rank");
			String df = (String) myBean.get("df");

			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,Integer.toString(x + 1), style2, sheet);
			colIndex++;
			
			if(object_type!=2)
			{
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,objName, style2, sheet);
				colIndex++;
			}
			else
			{					
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,e0122, style2, sheet);
				colIndex++;	
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,objName, style2, sheet);
				colIndex++;		
				if (this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					String onlynamevalue = (String) myBean.get("onlyname");
					this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,onlynamevalue, style2, sheet);
					colIndex++;	
				}
			}
			
			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,standard, style2, sheet);
			colIndex++;	

			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,pratical, style2, sheet);
			colIndex++;	
			
			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,basic, style2, sheet);
			colIndex++;	
			
			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,add, style2, sheet);
			colIndex++;	
			
			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,deduc, style2, sheet);
			colIndex++;	

			if (rule != null && "3".equals(rule))
			{				
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,rank, style2, sheet);
				colIndex++;	
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,df, style2, sheet);
				colIndex++;	
				
			} else
			{
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,df, style2, sheet);
				colIndex++;	
			}
			rowIndex++;
			colIndex=0;
		}
	}

	/**
	 * 基本指标录分导出Excel
	 * 
	 * @throws GeneralException
	 */
	public void createExcel3(HSSFWorkbook wb, String pointName, String title, HSSFCellStyle style1, HSSFCellStyle style2) throws GeneralException
	{

		HSSFSheet sheet = wb.createSheet(pointName);
		
		int object_type = this.getPlanVo().getInt("object_type");
		int len = 2;
		if(object_type==2)
		{
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
				len = 4;
			else
				len = 3;
		}
		
		// 写表头		
		this.executeCell(0, 0,  0, len, title, style1, sheet);
		

		sheet.setColumnWidth((short) 0, (short) 1800);
		sheet.setColumnWidth((short) 1, (short) 6000);
		sheet.setColumnWidth((short) 2, (short) 3000);
		
		//第一行
		this.executeCell(1, 0,  1, 0, "序号", style2, sheet);
		
		int colIndex=0;
		this.executeCell(1, colIndex,  1, colIndex, "序号", style2, sheet);
		colIndex++;	
	
		if(object_type!=2)
		{
			this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.khobj"), style2, sheet);
			colIndex++;
		}
		else
		{		
			FieldItem fielditem = DataDictionary.getFieldItem("E0122");
			this.executeCell(1, colIndex,  1, colIndex, fielditem.getItemdesc(), style2, sheet);
			colIndex++;
			this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("gz.columns.a0101"), style2, sheet);
			colIndex++;	
			if (this.onlyname != null && !"a0101".equals(this.onlyname))
			{
				FieldItem item = DataDictionary.getFieldItem(this.onlyname);
				this.executeCell(1, colIndex,  1, colIndex, item.getItemdesc(), style2, sheet);
				colIndex++;	
			}
			
		}		
		
		this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.param.mark"), style2, sheet);
		colIndex++;		

		String basicVal = this.getBasicFen360();// 待修改 录分方式分数的限制
		String info = "(0~" + moveZero(basicVal) + ")";
		
		setData3();
		int rowIndex = 2;
		colIndex=0;
		for (int x = 0; x < this.dataList.size(); x++)
		{
			LazyDynaBean myBean = (LazyDynaBean) this.dataList.get(x);
			String objName = (String) myBean.get("objName");
			String e0122 = (String) myBean.get("e0122");			
			String fz = (String) myBean.get("fz");
			
			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,Integer.toString(x + 1), style2, sheet);
			colIndex++;			
			
			if(object_type!=2)
			{
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,objName, style2, sheet);
				colIndex++;
			}
			else
			{					
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,e0122, style2, sheet);
				colIndex++;	
				this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,objName, style2, sheet);
				colIndex++;		
				if (this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					String onlynamevalue = (String) myBean.get("onlyname");
					this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,onlynamevalue, style2, sheet);
					colIndex++;	
				}
			}

			this.executeCell(rowIndex, colIndex,  rowIndex, colIndex,fz, style2, sheet);
			colIndex++;	
			
			rowIndex++;
			colIndex=0;
		}

	}

	/**
	 * @param a
	 *        起始 x坐标
	 * @param b
	 *        起始 y坐标
	 * @param c
	 *        终止 x坐标
	 * @param d
	 *        终止 y坐标
	 * @param content
	 *        内容
	 * @param style
	 *        表格样式
	 * @param fontEffect
	 *        字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
	 */
	public void executeCell(int a, int b, int c, int d, String content, HSSFCellStyle aStyle, HSSFSheet sheet)
	{
		try {
			HSSFRow row = sheet.getRow(a);
			if (row == null)
				row = sheet.createRow(a);
			HSSFCell cell = row.getCell(b);
			if (cell == null)
				cell = row.createCell(b);
			
			cell.setCellValue(new HSSFRichTextString(content));
			cell.setCellStyle(aStyle);
			int b1 = b;
			while (++b1 <= d)
			{
				cell = row.getCell(b1);
				if (cell == null)
					cell = row.createCell(b1);
				
				cell.setCellStyle(aStyle);
			}
			for (int a1 = a + 1; a1 <= c; a1++)
			{
				row = sheet.getRow(a1);
				if (row == null)
					row = sheet.createRow(a1);
				
				b1 = b;
				while (b1 <= d)
				{
					cell = row.getCell(b1);
					if (cell == null)
						cell = row.createCell(b1);
					
					cell.setCellStyle(aStyle);
					b1++;
				}
			}
			
			ExportExcelUtil.mergeCell(sheet, a, (short)b, c,(short) d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出模板  基本样式
	 * @throws GeneralException 
	 */
	public void downloadTemplate(HSSFWorkbook wb, String planName, LinkedHashMap points) throws GeneralException 
	{

		HSSFSheet sheet = wb.createSheet(planName);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setWrapText(true);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBottomBorderColor((short) 8);
		style1.setLeftBorderColor((short) 8);
		style1.setRightBorderColor((short) 8);
		style1.setTopBorderColor((short) 8);

		// 第二种style--字体10，水平居中，垂直居中，黑色边框，自动换行
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setBottomBorderColor((short) 8);
		style2.setLeftBorderColor((short) 8);
		style2.setRightBorderColor((short) 8);
		style2.setTopBorderColor((short) 8);

		HSSFRow row = sheet.createRow(0);
		try{

		// 唯一性指标
//		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.cn);
//		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		int object_type = this.getPlanVo().getInt("object_type");

		HSSFCell cell = null;
		int x = 0;
		if (object_type == 2)
		{
//			ExportExcelUtil.mergeCell(sheet, 0, (short) x, 1, (short) x));
//			cell = row.createCell((short) x);
//			cell.setCellValue(cellStr("部门"));
//			cell.setCellStyle(style2);
			
			FieldItem fielditem = DataDictionary.getFieldItem("B0110"); //20141206 DENGCAN
			this.executeCell(0, x, 1, x, fielditem.getItemdesc(), style2, sheet);
			x++;
			
			fielditem = DataDictionary.getFieldItem("E0122");
			this.executeCell(0, x, 1, x, fielditem.getItemdesc(), style2, sheet);
			x++;
			
			fielditem = DataDictionary.getFieldItem("E01A1");   //20141206 DENGCAN
			this.executeCell(0, x, 1, x, fielditem.getItemdesc(), style2, sheet);
			x++;

//			ExportExcelUtil.mergeCell(sheet, 0, (short) x, 1, (short) x));
//			cell = row.createCell((short) x);
//			cell.setCellValue(cellStr("姓名"));
//			cell.setCellStyle(style2);
			this.executeCell(0, x, 1, x, "姓名", style2, sheet);
			x++;

			if (this.onlyname != null && !"a0101".equals(this.onlyname))
			{
//				ExportExcelUtil.mergeCell(sheet, 0, (short) x, 1, (short) x));
				FieldItem item = DataDictionary.getFieldItem(this.onlyname);
//				cell = row.createCell((short) x);
//				cell.setCellValue(cellStr(item.getItemdesc()));
//				cell.setCellStyle(style2);
				this.executeCell(0, x, 1, x, item.getItemdesc(), style2, sheet);
				x++;
			}
		} else
		{
//			ExportExcelUtil.mergeCell(sheet, 0, (short) x, 1, (short) x));
//			cell = row.createCell((short) x);
//			cell.setCellValue(cellStr("考核对象"));
//			cell.setCellStyle(style2);
			this.executeCell(0, x, 1, x, "考核对象", style2, sheet);
			x++;
		}
		int y = x;
		sheet.setColumnWidth(0, 8000);
		Set pointNames = points.keySet();
		ArrayList list = new ArrayList();
		for (Iterator iter = pointNames.iterator(); iter.hasNext();)
		{
			String pointName = (String) iter.next();
			int n = pointName.indexOf("^");
			String point_Name = pointName.substring(0,n);
			
			ArrayList items = (ArrayList) points.get(pointName);
			if (items.size() > 1)
			{
				ExportExcelUtil.mergeCell(sheet, 0, (short) x, 0, (short) (x + items.size() - 1));
				cell = row.createCell((short) x);

				cell.setCellValue(cellStr(point_Name));
				cell.setCellStyle(style2);
				for (int j = 0; j < items.size(); j++)
				{
					if (j < items.size() - 1)
					{
						cell = row.createCell((short) (x + j + 1));

						cell.setCellStyle(style2);
					}
					list.add(items.get(j));
				}
			} else
			{
				cell = row.createCell((short) x);

				cell.setCellValue(cellStr(point_Name));
				cell.setCellStyle(style2);
				list.add(items.get(0));
			}
			x = x + items.size();
		}

		row = sheet.getRow(1);
		if(row==null)
			row = sheet.createRow(1);
		for (int k = 0; k < list.size(); k++)
		{
			String itemStr = (String) list.get(k);
			int index = itemStr.indexOf("^");
			String tempstr = itemStr.substring(0, index);
			cell = row.createCell(k + y);

			cell.setCellValue(cellStr(tempstr));
			cell.setCellStyle(style2);
		}
		
		
		//将指标设置为空串 再调用this.getKhObjs()取到不受指标权限控制的所有考核对象
		ArrayList objs = new ArrayList();
		for(int i=0;i<this.points.split(",").length;i++){
			this.point=this.points.split(",")[i];
			objs = this.getKhObjects(objs);
		}
		
		int method = this.getPlanVo().getInt("method");
		RowSet rs =null;
		HashMap pointmap = new HashMap();
		if(method==2)
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select  p0401,");
			if (object_type==2)// p04表的考核对象 人员存在a0100 非人员存在 b0110
				sql.append("a0100");
			else
				sql.append("b0110");
			sql.append(" from p04  where plan_id='" + this.planid+"' and fromflag=2 ");
			ContentDAO dao = new ContentDAO(this.cn);	
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				pointmap.put(rs.getString(1)+"`"+rs.getString(2), "");
			}
		}
		
		for (int p = 0; p < objs.size(); p++)
		{
			LazyDynaBean myBean = new LazyDynaBean();
			LazyDynaBean bean = (LazyDynaBean) objs.get(p);
			String a0101 = (String) bean.get("a0101");
			String object_id = (String) bean.get("object_id");
			String object_b0110 = (String) bean.get("b0110");
			String object_e0122 = (String) bean.get("e0122");
			String object_e01a1=(String)bean.get("e01a1");
			
			String e0122 = (String) bean.get("e0122Name");

			row = sheet.createRow(p + 2);
			int m = 0;
			if (object_type == 2)
			{
				//20141206 DENGCAN
				cell = row.createCell(m);
				cell.setCellValue(cellStr(AdminCode.getCodeName("UN",object_b0110)));
				cell.setCellStyle(style2);
				m++;
				
				
				cell = row.createCell(m);
				cell.setCellValue(cellStr(e0122));
				cell.setCellStyle(style2);
				m++;
				
				//20141206 DENGCAN
				cell = row.createCell(m);
				cell.setCellValue(cellStr(AdminCode.getCodeName("@K",object_e01a1)));
				cell.setCellStyle(style2);
				m++;

				cell = row.createCell(m);
				cell.setCellValue(cellStr(a0101));
				cell.setCellStyle(style2);
				m++;

				if (this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					String only_value = (String) bean.get(this.onlyname);
					cell = row.createCell(m);
					cell.setCellValue(cellStr(only_value));
					cell.setCellStyle(style2);
					m++;
				}
			} else
			{
				cell = row.createCell(m);
				if (object_type == 1)// 团队情况
				{
					if (AdminCode.getCodeName("UM", object_id) != null && AdminCode.getCodeName("UM", object_id).length() > 0)
						cell.setCellValue(cellStr(e0122));//考核对象为部门的情况
					else
						cell.setCellValue(cellStr(a0101));//考核对象为单位的情况
				} else
					cell.setCellValue(cellStr(a0101));

				cell.setCellStyle(style2);
				m++;
			}

			HashMap<String, String> valueMap = this.getSvalues();//业绩数据
			HashMap<String, String> valueMap1 = this.getFzScores();//录分项目
			for (int k = 0; k < list.size(); k++)
			{
				String itemStr = (String) list.get(k);
				int index = itemStr.indexOf(":");
				String pointid = itemStr.substring(index+1);
				
				String itemid = itemStr.substring(0, index);
				int _index = itemid.indexOf("^");
				itemid = itemid.substring(_index+1);
				
				cell = row.createCell(k + m );
				cell.setCellStyle(style2);
				
				boolean right = true;
				if(object_type!=2)  // 非2 团队
					right = this.pointPrivBean.getPrivPoint("", object_id, pointid);
				else if(object_type==2)  // 2 人员
					right = this.pointPrivBean.getPrivPoint(object_b0110, object_e0122, pointid);
				
				if(method==2 && pointmap.get(pointid+"`"+object_id)==null)
					right = false;
				
				if(right==false){
					cell.setCellValue(new HSSFRichTextString("---"));
				} else {//下载模板默认显示原始值 chent 20170518 add
					String key = object_id+":"+itemid;
					String value = valueMap.get(key);
					if(StringUtils.isEmpty(value)){
						value = valueMap1.get(object_id);
					}
					
					String cellValue = "";
					if(!StringUtils.isEmpty(value)){
						float aa = Float.parseFloat(value);
						cellValue = value.valueOf(aa);
						// --------15.0改为15 start----------
						int ii = (int)aa;
				        if (ii == aa) {
				        	cellValue = String.valueOf(ii);
				        } else {
				        	cellValue = String.valueOf(aa);
				        }
				     // --------15.0改为15 end----------
					}else {
						cellValue = "0";
					}
					cell.setCellValue(new HSSFRichTextString(cellValue));
				}
				
				
			}
		}
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e); 
		}
	}	
	/**
	 * 导出模板  特殊样式   JinChunhai  2011.03.30
	 * @throws GeneralException 
	 */
	public void downloadTemplate_special(HSSFWorkbook wb, String planName, LinkedHashMap points) throws GeneralException 
	{		
		HSSFSheet sheet = wb.createSheet(planName);	
		HSSFPatriarch patr = sheet.createDrawingPatriarch();
		HSSFRow hssRow = null;
		/**设置Excel单元格的格式*/		
		HSSFCellStyle titleStyle=style(wb, 0);
		HSSFCellStyle strStyle=style(wb, 1);
		HSSFCellStyle numStyle=style(wb, 2);
		HSSFCellStyle pointStyle=style(wb, 3);		
		
//		HashMap standardFenMap = this.getStandardFens();  //  标准值
//		HashMap praticalValsMap = this.getPraticalVals(); //  实际值
		try{
			
			/**获取目标任务明细表 per_target_mx 的表结构 */		
			RowSet rowSet =null;
			HashMap strmap = new HashMap();						
			ContentDAO dao = new ContentDAO(this.cn);						
			rowSet = dao.search("Select * from per_target_mx where 1=2");		           
			ResultSetMetaData metaData = rowSet.getMetaData();			
			for (int i = 1; i <= metaData.getColumnCount(); i++) 
			{				    
				String colnames = metaData.getColumnName(i);
				String pt = colnames.substring(0,2);
				if("T_".equalsIgnoreCase(pt))
				{
					strmap.put(colnames.substring(2).toUpperCase(),"");
				}				
			}						
//			HashMap sValsMap = this.getSvalues();      // 一次性获得所有考核对象的所有项目的分值
			
			int object_type = this.getPlanVo().getInt("object_type");	
			HSSFCell cell = null;
			int x = 0;
			
			//   输出表头				
			if(object_type == 2)
			{
				FieldItem fielditem = DataDictionary.getFieldItem("E0122");
				this.executeCreatCell(0, x, 0, x, fielditem.getItemdesc(), titleStyle, sheet,"str",patr,"yes","b0110");
				x++;
				this.executeCreatCell(0, x, 0, x, "姓名", titleStyle, sheet,"str",patr,"yes","a0101");
				x++;
				if (this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					FieldItem item = DataDictionary.getFieldItem(this.onlyname);
					this.executeCreatCell(0, x, 0, x, item.getItemdesc(), titleStyle, sheet,"str",patr,"yes","only_id");
					x++;
				}
			} else
			{
				this.executeCreatCell(0, x, 0, x, "考核对象", titleStyle, sheet,"str",patr,"yes","object");
				x++;
			}
			this.executeCreatCell(0, x, 0, x+1, "指标名称", titleStyle, sheet,"str",patr,"yes","point_id");
			x++;
			x++;
			this.executeCreatCell(0, x, 0, x, "标准值", titleStyle, sheet,"str",patr,"yes","normValue");
			x++;
			this.executeCreatCell(0, x, 0, x, "实际值", titleStyle, sheet,"str",patr,"yes","factValue");
			x++;
			
			//   输出表结构	
			
			this.point="";
			//将指标设置为空串 再调用this.getKhObjs()取到不受指标权限控制的所有考核对象
			ArrayList objs = new ArrayList();
			for(int i=0;i<this.points.split(",").length;i++){
				this.point=this.points.split(",")[i];
				objs = this.getKhObjects(objs);
			}
			int method = this.getPlanVo().getInt("method");
			RowSet rs =null;
			HashMap pointmap = new HashMap();
			if(method==2)
			{
				StringBuffer sql = new StringBuffer();
				sql.append("select  p0401,");
				if (object_type==2)// p04表的考核对象 人员存在a0100 非人员存在 b0110
					sql.append("a0100");
				else
					sql.append("b0110");
				sql.append(" from p04  where plan_id='" + this.planid+"' and fromflag=2 ");
//				ContentDAO dao = new ContentDAO(this.cn);	
				rs = dao.search(sql.toString());
				while(rs.next())
				{
					pointmap.put(rs.getString(1)+"`"+rs.getString(2), "");
				}
			}	
			int rowNumber = 1;
			HashMap DataCollectBoMap=new HashMap();
			DataCollectBo nowBo=null;
			for (int p = 0; p < objs.size(); p++)
			{
				LazyDynaBean myBean = new LazyDynaBean();
				LazyDynaBean bean = (LazyDynaBean) objs.get(p);
				String a0101 = (String) bean.get("a0101");
				String object_id = (String) bean.get("object_id");
				String object_b0110 = (String) bean.get("b0110");
				String object_e0122 = (String) bean.get("e0122");
				String e0122 = (String) bean.get("e0122Name");
							
				int m = 0;												
				Set keySet=points.keySet();
				java.util.Iterator t=keySet.iterator();
				int count=0;
				while(t.hasNext())
				{
					String strKey = (String)t.next();  //键值	    
					ArrayList strValue = (ArrayList)points.get(strKey);   //value值   
					
					String itemStr = (String) strValue.get(0);
					int index = itemStr.indexOf(":");
					String pointid = itemStr.substring(index+1);																																								
					
					boolean right = true;
					if(object_type!=2)  // 非2 团队
						right = this.pointPrivBean.getPrivPoint("", object_id, pointid);
					else if(object_type==2)  // 2 人员
						right = this.pointPrivBean.getPrivPoint(object_b0110, object_e0122, pointid);
					
					if(method==2 && pointmap.get(pointid+"`"+object_id)==null)
						right = false;
										
					if (strValue.size() > 1)
					{	
						if(right==true)	
						{
							for (int j = 0; j < strValue.size(); j++)
							{
								count++;		
							}
						}
					}else
					{
						if(right==true)							
							count++;	
					}
				}					
				/**画出一个考核对象*/
				if (object_type == 2)
				{
					if(count==0)
					{
//						this.executeCreatCell(rowNumber, m, rowNumber, m, e0122, strStyle, sheet,"str",patr,"yes",object_e0122);
					}
					else
						this.executeCreatCell(rowNumber, m, rowNumber+count-1, m, e0122, strStyle, sheet,"str",patr,"yes",object_e0122);
					m++;
					if(count==0)
					{
//						this.executeCreatCell(rowNumber, m, rowNumber, m, a0101, strStyle, sheet,"str",patr,"yes",object_id);
					}
					else
						this.executeCreatCell(rowNumber, m, rowNumber+count-1, m, a0101, strStyle, sheet,"str",patr,"yes",object_id);
					m++;
					if (this.onlyname != null && !"a0101".equals(this.onlyname))
					{
						String only_value = (String) bean.get(this.onlyname);
						if(count==0)
						{
//							this.executeCreatCell(rowNumber, m, rowNumber, m, only_value, strStyle, sheet,"str",patr,"yes",this.onlyname);
						}
						else
							this.executeCreatCell(rowNumber, m, rowNumber+count-1, m, only_value, strStyle, sheet,"str",patr,"yes",this.onlyname);
						m++;
					}
				} else
				{
					String objectName = "";
					if (object_type == 1)// 团队情况
					{
						if (AdminCode.getCodeName("UM", object_id) != null && AdminCode.getCodeName("UM", object_id).length() > 0)
							objectName = e0122;//考核对象为部门的情况
						else
							objectName = a0101;//考核对象为单位的情况
					}else
						objectName = a0101;
					if(count==0)
					{
//						this.executeCreatCell(rowNumber, m, rowNumber, m, objectName, strStyle, sheet,"str",patr,"yes",object_id);
					}
					else
						this.executeCreatCell(rowNumber, m, rowNumber+count-1, m, objectName, strStyle, sheet,"str",patr,"yes",object_id);
					m++;
				}
				
				/**画出考核对象下的考核指标*/
/*				String standVal = (String) standardFenMap.get(object_id);
				standVal = (standVal == null ? "0.0" : standVal);
				
				String praticalVal = (String) praticalValsMap.get(object_id);
				praticalVal = (praticalVal == null ? "0" : praticalVal);
*/												
				
				Set pointValue = points.keySet();
				ArrayList list = new ArrayList();
				for (Iterator iter = pointValue.iterator(); iter.hasNext();)
				{
					String pointName = (String) iter.next();
					int n = pointName.indexOf("^");
					String point_Name = pointName.substring(0,n);
					
					ArrayList items = (ArrayList) points.get(pointName);	
					
					String itemStr = (String) items.get(0);
					int index = itemStr.indexOf(":");
					String pointid = itemStr.substring(index+1);	
					if(DataCollectBoMap.get(pointid)!=null)
						nowBo=(DataCollectBo)DataCollectBoMap.get(pointid);
					else
					{
						nowBo = new DataCollectBo(this.cn, this.planid, pointid, this.userview);
						DataCollectBoMap.put(pointid,nowBo);
					}
					
					boolean right = true;
					if(object_type!=2)  // 非2 团队
						right = this.pointPrivBean.getPrivPoint("", object_id, pointid);
					else if(object_type==2)  // 2 人员
						right = this.pointPrivBean.getPrivPoint(object_b0110, object_e0122, pointid);
					
					if(method==2 && pointmap.get(pointid+"`"+object_id)==null)
						right = false;
					
					if (items.size() > 1)
					{												
						if(right==true)	
						{
							this.executeCreatCell(rowNumber, m, rowNumber+items.size()-1, m, point_Name, pointStyle, sheet,"str",patr,"yes",pointid);
							for (int j = 0; j < items.size(); j++)
							{
								String itemName = (String)items.get(j);
								int ind = itemName.indexOf("^");
								String itName = itemName.substring(0,ind);
								String itemid = itemName.substring((ind+1),itemName.indexOf(":"));
								
								this.executeCreatCell(rowNumber, m+1, rowNumber, m+1, itName, pointStyle, sheet,"str",patr,"yes",itemid);																						
								this.executeCreatCell(rowNumber, m+2, rowNumber, m+2, "---", strStyle, sheet,"str",patr,"no","no");	
								
//								String itemid = nowBo.getItemidByName(itName);
//								String s_value = "";
//								if(sValsMap.get(object_id + ":" + itemid)!=null)								
//									s_value = (String) sValsMap.get(object_id + ":" + itemid);									
//								if((s_value.equalsIgnoreCase("0")) || (s_value==null) || (s_value.trim().length()<=0) || (s_value.equalsIgnoreCase("null")))
//								{
									this.executeCreatCell(rowNumber, m+3, rowNumber, m+3, "", numStyle, sheet,"num",patr,"no","no");	
//								}else
//								{
//									String colorScore = DataCollectBo.roundAndRemoveZero(s_value, 3);								
//									this.executeCreatCell(rowNumber, m+3, rowNumber, m+3, colorScore, numStyle, sheet,"num",patr,"no","no");										
//								}
								
								rowNumber++;
							}
							rowNumber--;
						}	
					}else
					{
						if(right==true)	
						{
							this.executeCreatCell(rowNumber, m, rowNumber, m+1, point_Name, pointStyle, sheet,"str",patr,"yes",pointid);
							String standValScore = "";
							String upStr = pointid.toUpperCase();  //转换成大写
							if(strmap.get(upStr)!=null)
							{								
								standValScore = nowBo.getStandardFen(object_id);   // 取得某计划下某考核指标的某考核对象的标准分	
							}
													 									
//							String itemName = (String)items.get(0);
//							int ind = itemName.indexOf(":");
//							String itName = itemName.substring(0,ind);
							
							if(("0".equalsIgnoreCase(standValScore)) || (standValScore==null) || (standValScore.trim().length()<=0) || ("null".equalsIgnoreCase(standValScore)))
								this.executeCreatCell(rowNumber, m+2, rowNumber, m+2, "---", strStyle, sheet,"str",patr,"no","no");										
							else
								this.executeCreatCell(rowNumber, m+2, rowNumber, m+2, standValScore, numStyle, sheet,"num",patr,"no","no");																
							
/*							HashMap map = nowBo.getFzScores();							
							String fzScore = map.get(object_id)==null?"0":(String) map.get(object_id);									
							String colorScore = DataCollectBo.roundAndRemoveZero(fzScore, 3);													
														
							HashMap praticalValsMap = this.getPraticalVals();							
							String praticalVal = (String) praticalValsMap.get(object_id);
							praticalVal = (praticalVal == null ? "0" : praticalVal);
							String coScore = DataCollectBo.roundAndRemoveZero(fzScore, 3);
*/							
														
							this.executeCreatCell(rowNumber, m+3, rowNumber, m+3, "", numStyle, sheet,"num",patr,"no","no");		
						}
					}
					if(right==true)						
						rowNumber++;
				}
			}
			
			for (int i = 0; i <=x; i++)
			{
				sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)6000);
			}
			for (int i = 0; i <=rowNumber; i++)
			{
				hssRow = sheet.getRow(i);
			    if(hssRow==null)
			    	hssRow = sheet.createRow(i);
			    hssRow.setHeight((short) 600);
			}
				
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e); 
		}
	}	
	/**
	 * @param a
	 *        起始 x坐标
	 * @param b
	 *        起始 y坐标
	 * @param c
	 *        终止 x坐标
	 * @param d
	 *        终止 y坐标
	 * @param content
	 *        内容
	 * @param style
	 *        表格样式
	 * @param fontEffect
	 *        字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
	 */
	public void executeCreatCell(int a, int b, int c, int d, String content, HSSFCellStyle aStyle, HSSFSheet sheet,String type,HSSFPatriarch patr,String xorzb,String comment)
	{
		try {
			HSSFComment comm = null;
			HSSFRow row = sheet.getRow(a);
			if (row == null)
				row = sheet.createRow(a);
			HSSFCell cell = row.getCell(b);
			if (cell == null)
				cell = row.createCell(b);
			
			if("num".equalsIgnoreCase(type)&& (content!=null&&content.length()>0))
			{
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellValue(new Double(content).doubleValue());
			}else
			{
				cell.setCellValue(new HSSFRichTextString(content));
			}
			cell.setCellStyle(aStyle);
			
			if("yes".equalsIgnoreCase(xorzb))
			{
				comm = patr.createComment(new HSSFClientAnchor(a, 0, a, 1, (short)(b+1), a, (short)(b+2), c+1));
				comm.setString(new HSSFRichTextString(comment));
				cell.setCellComment(comm);
			}
			
			int b1 = b;
			while (++b1 <= d)
			{
				cell = row.getCell(b1);
				if (cell == null)
					cell = row.createCell(b1);
				
				cell.setCellStyle(aStyle);
			}
			for (int a1 = a + 1; a1 <= c; a1++)
			{
				row = sheet.getRow(a1);
				if (row == null)
					row = sheet.createRow(a1);
				
				b1 = b;
				while (b1 <= d)
				{
					cell = row.getCell(b1);
					if (cell == null)
						cell = row.createCell(b1);
					
					cell.setCellStyle(aStyle);
					b1++;
				}
			}
			
			ExportExcelUtil.mergeCell(sheet, a, (short)b, c,(short) d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 设置Excel的样式
	 * @param workbook
	 * @param styles
	 * @return
	 */
	public HSSFCellStyle style(HSSFWorkbook workbook, int styles)
	{		
		HSSFCellStyle style = workbook.createCellStyle();
		
		switch (styles)
		{		
			case 0:
			    HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 10);
			    fonttitle.setBold(false);// 加粗
			    style.setFont(fonttitle);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.CENTER);
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);			    			    
			    style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			    break;
			case 1:
			    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));			    			    				
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);				
				style.setVerticalAlignment(VerticalAlignment.CENTER);	
				style.setAlignment(HorizontalAlignment.CENTER);
			    break;
			case 2:
			    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.RIGHT);
			    break;
			case 3:
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.LEFT);
			    break;			
			default:
			    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			    style.setAlignment(HorizontalAlignment.LEFT);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    break;
		}
		style.setWrapText(true);
		return style;
	}
	/**
	 * 设置Excel的字体
	 * @param workbook
	 * @param fonts
	 * @param size
	 * @return
	 */
	public HSSFFont fonts(HSSFWorkbook workbook, String fonts, int size)
	{
	
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		return font;
	}
	/**
	 * 将中文公式转换成sql
	 * 
	 * @param c_expr
	 * @param varType
	 * @param uv
	 * @return
	 */
	public String getSql(String c_expr, String varType, UserView uv)
	{

		String temp = "";
		try
		{
			if (c_expr.trim().length() > 0)
			{
				ArrayList alUsedFields = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
				FieldItem item = new FieldItem();
				item.setItemid("per_target_evaluation.score");
				item.setItemdesc("评分");
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				alUsedFields.add(item); 
				
				YksjParser yp = new YksjParser(uv, alUsedFields, YksjParser.forNormal, getColumType(varType.trim()), 3, "usr", "");
				yp.run(c_expr);
				temp = yp.getSQL();// 公式的结果
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return temp;
	}

	/**
	 * 得到数据类型
	 * 
	 * @param type
	 * @return
	 */
	public int getColumType(String type)
	{

		int temp = 1;
		if ("A".equals(type))
		{
			temp = IParserConstant.STRVALUE;
		} else if ("D".equals(type))
		{
			temp = IParserConstant.DATEVALUE;
		} else if ("N".equals(type))
		{
			temp = IParserConstant.FLOAT;
		} else if ("L".equals(type))
		{
			temp = IParserConstant.LOGIC;
		} else
		{
			temp = IParserConstant.STRVALUE;
		}
		return temp;
	}
	
	/** 判断定量统一打分指标是否有计算公式  */
	public String getPointFormula(ArrayList list)
	{
		StringBuffer point_ids = new StringBuffer();		
		for(int j=0;j<list.size();j++)
		{
 			CommonData d=(CommonData)list.get(j);						
 			point_ids.append(",'");
			point_ids.append(d.getDataValue());
			point_ids.append("'");			
		}
		RowSet rs = null;
		String formula = "false";
		try
		{
			ContentDAO dao = new ContentDAO(this.cn);
			String sql = "";
			if(point_ids!=null && point_ids.toString().trim().length()>0)
			{
				sql = "select formula,pointctrl from per_point where point_id in (" + point_ids.toString().substring(1) + ") and pointkind=1 and status=1 and (pointtype=0 or pointtype is null ) and validflag=1 ";			
				rs = dao.search(sql);
				while (rs.next())
				{
					String Pointctrl=Sql_switcher.readMemo(rs,"pointctrl");
					HashMap map=PointCtrlXmlBo.getAttributeValues(Pointctrl);
					String computeRule=(String)map.get("computeRule");
					if(computeRule==null || "0".equals(computeRule))
						continue;
	//				if(!(this.userview.isSuper_admin()))
	//				{
	//					if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD,point_id))
	//					{
	//						continue;
	//					}
	//				}						
					if((rs.getString("formula") != null) && (rs.getString("formula").trim().length()>0))
						formula = "true";
				}
			}
			
			if(rs!=null)
				rs.close();
				
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return formula;
	}
	/**
	 * 指标导出Excel
	 * 
	 * @throws GeneralException
	 */
	public void createExcel2Byxgq(HSSFWorkbook wb, String title,ArrayList listall , HSSFCellStyle style1, HSSFCellStyle style2,Connection conn, String planID,UserView userView) throws GeneralException
	{
		// 第二种style--字体10，水平居左，垂直居中，黑色边框，自动换行
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style3 = wb.createCellStyle();
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setVerticalAlignment(VerticalAlignment.CENTER);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderTop(BorderStyle.THIN);
		style3.setBottomBorderColor((short) 8);
		style3.setLeftBorderColor((short) 8);
		style3.setRightBorderColor((short) 8);
		style3.setTopBorderColor((short) 8);
		style3.setWrapText(true);
		DataCollectBo nowBo=null;
		 this.planid = planID;
		 this.userview = userView;
		 this.pointPrivBean=new Permission(conn,this.userview);
		LazyDynaBean bean = (LazyDynaBean)listall.get(0);
		 String sheetname = bean.get("sheetname").toString();
		 String type = bean.get("type").toString();
		 String computeRule = bean.get("computeRule").toString();
		 boolean flag = false;
		 HSSFSheet sheet = null;
		 HSSFRow row=null;
		 HSSFCell cell=null;
		 int len = 0;
		 int object_type=0;
		 //获得最大层
		 String maxlay="";
		 int  rowIndex = 0;
		 HashMap map = new HashMap();
		 ArrayList listseq = new ArrayList();
		 ArrayList listobj = new ArrayList();
		 String pointName="";
		 for(int m=0;m<listall.size();m++){
			 bean = (LazyDynaBean)listall.get(m);
			 String pointId = bean.get("pointId").toString();
			 this.point = pointId;
			 String  maxlay2 = this.getMaxLay();
			 if("".equals(maxlay))
				 maxlay = maxlay2;
			 if(Integer.parseInt(maxlay)<Integer.parseInt(maxlay2))
				 maxlay = maxlay2;
			 
			  pointName = bean.get("pointName").toString();
			 this.point = pointId;
			 nowBo = new DataCollectBo(conn, planID,pointId,userView);
			 this.onlyname = nowBo.onlyname;
			 this.onlyValueMap1 = nowBo.onlyValueMap1;
			 this.onlyValueMap2 = nowBo.onlyValueMap2;
			 this.onlyValueMap3 = nowBo.onlyValueMap3;
			 this.planMethod = nowBo.planMethod;
			 this.display_e0122 = nowBo.display_e0122;
			 this.objPointRanks = nowBo.objPointRanks;
			 this.planVo = nowBo.planVo;
			 this.seprartor = nowBo.seprartor;
			 this.template_vo = nowBo.template_vo;
			 this.templatePointRank = nowBo.templatePointRank;
				
				ArrayList items = this.getAllItems();
				int  to_y = items.size();
				if ("0".equals(type) || "".equals(type))// 基本指标
				{
					if("1".equals(computeRule)|| "2".equals(computeRule)|| "3".equals(computeRule)){
						setData2(computeRule);
					}
					else if ("0".equals(computeRule)){
						setData3();
					}
					
					
					
				}else{
					if ("0".equals(maxlay))
						return;
					setData1(type);
				}
				for (int x = 0; x < this.dataList.size(); x++)
				{
					LazyDynaBean myBean = (LazyDynaBean) this.dataList.get(x);
					String object_id = (String) myBean.get("object_id");
					myBean.set("pointId", pointId);
					myBean.set("to_y",""+ to_y);
					myBean.set("pointName",pointName);
					if(map!=null&&map.get(object_id)!=null){
						ArrayList list2 = (ArrayList)map.get(object_id);
						list2.add(myBean);
						map.put(object_id, list2);
					}else{
						ArrayList list = new ArrayList();
						list.add(myBean);
						map.put(object_id, list);
						listseq.add(object_id);
					}
					
				}
				
		 }
		 
		 for(int n =0;n<listseq.size();n++){
			 String object_id = (String) listseq.get(n);
			 listobj.add(map.get(object_id));
		 }
			 bean = (LazyDynaBean)listall.get(0);
			 String pointId = bean.get("pointId").toString();
			  pointName = bean.get("pointName").toString();
				if(map==null||map.size()==0)
					return;
				if(!flag){
					sheet = wb.createSheet(sheetname);
					 len = Integer.parseInt(maxlay);
						if ("0".equals(type) || "".equals(type))// 基本指标
						{
							if("1".equals(computeRule)|| "2".equals(computeRule)|| "3".equals(computeRule)){
								
							}
							if("3".equals(computeRule)){
								len = 7;
							} else if ("1".equals(computeRule)|| "2".equals(computeRule)){
								len = 6;
							}else if ("0".equals(computeRule)){
								len = 1;
							}
							
						}else{
							len=len+4;
						}
					 object_type = this.getPlanVo().getInt("object_type");
					// 写表头
					 row = sheet.createRow(0);
					if(object_type!=2)
						ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (len + 2)); //20141213 dengcan
					else
					{
						if (this.onlyname != null && !"a0101".equals(this.onlyname))
							ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (len + 6));  //20141213 dengcan
						else
							ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (len + 5));   //20141213 dengcan
					}
					 cell = row.createCell((short) 0);

					cell.setCellValue(cellStr(title));
					cell.setCellStyle(style1);
					
					short n=0;
					if(object_type==2)
						n=2;
					if ("0".equals(type) || "".equals(type))// 基本指标
					{
						if("1".equals(computeRule)|| "2".equals(computeRule)|| "3".equals(computeRule)){
							sheet.setColumnWidth((short) 0, (short) 1500);
							sheet.setColumnWidth((short) 1, (short) 8000);
							if(object_type==2)
							{
								sheet.setColumnWidth((short) 2, (short) 6000);
								sheet.setColumnWidth((short) 3, (short) 6000);
							}
							if (this.onlyname != null && !"a0101".equals(this.onlyname)){
								sheet.setColumnWidth((short) (3+n), (short) 5000);
								sheet.setColumnWidth((short) (4+n), (short) 8000);
							}
							else
								sheet.setColumnWidth((short) (3+n), (short) 8000);
						}
						else if ("0".equals(computeRule)){
							if (this.onlyname != null && !"a0101".equals(this.onlyname)){
								sheet.setColumnWidth((short) (2+n), (short) 5000);
								sheet.setColumnWidth((short) (3+n), (short) 8000);
								sheet.setColumnWidth((short) (4+n), (short) 4000);
								sheet.setColumnWidth((short) (5+n), (short) 4000);
							}
							else{
								sheet.setColumnWidth((short) (2+n), (short) 8000);
								sheet.setColumnWidth((short) (3+n), (short) 4000);
								sheet.setColumnWidth((short) (4+n), (short) 4000);
							}
						}
						
					}else{
						sheet.setColumnWidth((short) 0, (short) 1500);
						sheet.setColumnWidth((short) 1, (short) 8000);
						if(object_type==2)
						{
							sheet.setColumnWidth((short) 2, (short) 6000);
							sheet.setColumnWidth((short) 3, (short) 6000);
						}
						if (this.onlyname != null && !"a0101".equals(this.onlyname)){
							sheet.setColumnWidth((short) (3+n), (short) 5000);
							sheet.setColumnWidth((short) (4+n), (short) 8000);
							sheet.setColumnWidth((short) (5+n), (short) 8000);
						}else{
							sheet.setColumnWidth((short) (3+n), (short) 8000);
							sheet.setColumnWidth((short) (4+n), (short) 8000);
						}
					}
					flag =true;
				}
		
		int tempColIndex=0;
			row = sheet.getRow(1);
			if(row==null)
				row = sheet.createRow(1);
			ArrayList list = null;
			int colIndex=0;
				
				this.executeCell(1, colIndex,  1, colIndex, "序号", style2, sheet);
				colIndex++;
				
				if(object_type!=2)
				{
					this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.khobj"), style2, sheet);
					colIndex++;
				}
				else
				{		
					
					FieldItem fielditem = DataDictionary.getFieldItem("B0110");
					this.executeCell(1, colIndex,  1, colIndex, fielditem.getItemdesc(), style2, sheet);
					colIndex++;
					
					fielditem = DataDictionary.getFieldItem("E0122");
					this.executeCell(1, colIndex,  1, colIndex, fielditem.getItemdesc(), style2, sheet);
					colIndex++;
					
					fielditem = DataDictionary.getFieldItem("E01A1");
					this.executeCell(1, colIndex,  1, colIndex, fielditem.getItemdesc(), style2, sheet);
					colIndex++;
					
					this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("gz.columns.a0101"), style2, sheet);
					colIndex++;		
					if (this.onlyname != null && !"a0101".equals(this.onlyname))
					{
						FieldItem item = DataDictionary.getFieldItem(this.onlyname);
						this.executeCell(1, colIndex,  1, colIndex, item.getItemdesc(), style2, sheet);
						colIndex++;	
					}
					
				}
				this.executeCell(1, colIndex,  1, colIndex,"指标名称", style2, sheet);
				colIndex++;	
				String temptitle = "";
				if ("0".equals(type) || "".equals(type))// 基本指标
				{
					if("1".equals(computeRule)|| "2".equals(computeRule)|| "3".equals(computeRule)){
						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.standard"), style2, sheet);
						colIndex++;	
						
						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.pratical"), style2, sheet);
						colIndex++;
						
						this.executeCell(1, colIndex, 1, colIndex, ResourceFactory.getProperty("jx.datacol.basic"), style2, sheet);
						colIndex++;

						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.add"), style2, sheet);
						colIndex++;
						
						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.deduc"), style2, sheet);
						colIndex++;
					}
					if("3".equals(computeRule)){
						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.param.mingci"), style2, sheet);
						colIndex++;	
						
						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.result"), style2, sheet);
						colIndex++;
					} else if ("1".equals(computeRule)|| "2".equals(computeRule)){
						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.datacol.result"), style2, sheet);
						colIndex++;
					}else if("0".equals(computeRule)){
						String basicVal = this.getBasicFen360();
						String info = "(0~" + moveZero(basicVal) + ")";
						if(Double.parseDouble(basicVal)<0)
							info = "(" + moveZero(basicVal) + "~0)";
						this.executeCell(1, colIndex,  1, colIndex, ResourceFactory.getProperty("jx.param.mark")+info, style2, sheet);
						colIndex++;
					}
					
				}else{
				if (type != null && "1".equals(type))// 1-加分 2-扣分
					temptitle=ResourceFactory.getProperty("jx.datacol.add");
				else if (type != null && "2".equals(type))
					temptitle=ResourceFactory.getProperty("jx.datacol.deduc");
				temptitle = temptitle+"项目";
				this.executeCell(1, colIndex,  1, colIndex+Integer.parseInt(maxlay)-1,temptitle, style2, sheet);
				colIndex=colIndex+Integer.parseInt(maxlay);	
				temptitle ="实际值";
				this.executeCell(1, colIndex,  1, colIndex,temptitle, style2, sheet);
				colIndex++;	
				tempColIndex=colIndex;

				
				this.executeCell(1,  tempColIndex,  1,  tempColIndex, ResourceFactory.getProperty("jx.datacol.basic"), style2, sheet);
				
				 temptitle = "";
				if (type != null && "1".equals(type))// 1-加分 2-扣分
					temptitle=ResourceFactory.getProperty("jx.datacol.add");
				else if (type != null && "2".equals(type))
					temptitle=ResourceFactory.getProperty("jx.datacol.deduc");
				
				this.executeCell(1,  tempColIndex+1,  1,  tempColIndex+1, temptitle, style2, sheet);
				
				this.executeCell(1,  tempColIndex+2,  1,  tempColIndex+2, ResourceFactory.getProperty("jx.datacol.result"), style2, sheet);
				}
			
			


		 if ("0".equals(type) || "".equals(type))// 基本指标
			{
			int x=0;
			Set keySet=	map.keySet();
			int tobj =0 ;
	    	for(int d=0;d<listobj.size();d++)
			{
	    		
					ArrayList listobject =  (ArrayList)listobj.get(d);
					int osize = listobject.size();
					rowIndex =0;
					//for(int a=0;a<listobject.size();a++){
						//LazyDynaBean myBean = (LazyDynaBean) listobject.get(a);
						//String to_yobject = (String) myBean.get("to_y");
						tobj = tobj+osize;
						
					//}
					if(tobj>=1)
						tobj = tobj -1;
					for(int a=0;a<listobject.size();a++){
					ArrayList nextLayChild = new ArrayList();
					LazyDynaBean myBean = (LazyDynaBean) listobject.get(a);
					String objName = (String) myBean.get("objName");
					String e0122 = (String) myBean.get("e0122");
					
					String b0110 = (String) myBean.get("b0110");
					String e01a1 = (String) myBean.get("e01a1");
					
					
					String standard = (String) myBean.get("standard");
					String pratical = (String) myBean.get("pratical");
					String basic = (String) myBean.get("basic");
					String deduc = (String) myBean.get("deduc");
					String add = (String) myBean.get("add");
					String rank = (String) myBean.get("rank");
					String df = (String) myBean.get("df");
					String fz = (String) myBean.get("fz");
					  pointId = myBean.get("pointId").toString();
					 this.point = pointId;
					  pointName = myBean.get("pointName").toString();
					ArrayList items = this.getAllItems();
					int  to_y = items.size();
					rowIndex  = rowIndex + to_y;
					if(to_y>=1)
						to_y = to_y-1;
//					if(rowIndex>=1)
//						rowIndex = rowIndex -1;
					HSSFRichTextString value =null;
					 colIndex = 0;
					if(a==0){
					this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,x + 1+"", style2, sheet);	
					colIndex++;
					if(object_type!=2)
					{
						value = cellStr(objName);
						this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
						colIndex++;
					}
					else
					{			
						if(AdminCode.getCodeName("UN",b0110)!=null&&AdminCode.getCodeName("UN",b0110).length()>0)
							b0110=AdminCode.getCodeName("UN",b0110);
						if(AdminCode.getCodeName("@K",b0110)!=null&&AdminCode.getCodeName("@K",b0110).length()>0)
							e01a1=AdminCode.getCodeName("@K",e01a1);
						value = cellStr(b0110);
						this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
						colIndex++;
						
						value = cellStr(e0122);
						this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
						colIndex++;
						
						value = cellStr(e01a1);
						this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
						colIndex++;
						
						
						value = cellStr(objName);
						//2+tobj*x+x
						this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
						colIndex++;
						if (this.onlyname != null && !"a0101".equals(this.onlyname))
						{
							String onlynamevalue = (String) myBean.get("onlyname");
							value = cellStr(onlynamevalue);
							this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style3, sheet);
							colIndex++;
						}
					}
					}else{
						colIndex++;
						if(object_type!=2)
						{
							colIndex++;
						}
						else
						{					
							colIndex++;
							colIndex++;
							colIndex++;
							colIndex++;
							if (this.onlyname != null && !"a0101".equals(this.onlyname))
							{
								colIndex++;
							}
						}	
					}
						value = cellStr(pointName);
						//2+tobj*x+x+a
						this.executeCell(2+tobj-osize+1+x+a, colIndex,2+tobj-osize+1+x+a, colIndex,value.toString(), style3, sheet);
						colIndex++;
						if("1".equals(computeRule)|| "2".equals(computeRule)|| "3".equals(computeRule)){
							value = cellStr(standard);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
							value = cellStr(pratical);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
							value = cellStr(basic);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
							value = cellStr(add);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
							
							value = cellStr(deduc);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
						}else if("0".equals(computeRule)){
							String basicVal = this.getBasicFen360();// 待修改 录分方式分数的限制
							String info = "(0~" + moveZero(basicVal) + ")";
							value = cellStr(fz);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
						}
						if("3".equals(computeRule)){
							value = cellStr(rank);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
							value = cellStr(df);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
						} else if ("1".equals(computeRule)|| "2".equals(computeRule)){
							value = cellStr(df);
							this.executeCell(2+tobj-osize+1+x+a, colIndex, 2+tobj-osize+1+x+a, colIndex,value.toString(), style2, sheet);
							colIndex++;
						}
				
				}
				x++;
				}
			}else{
				int x=0;
				Set keySet=	map.keySet();
				int tobj =0;
				for(int d=0;d<listobj.size();d++)
				{
		    		
						ArrayList listobject =  (ArrayList)listobj.get(d);
			int osize = 0;
			
			rowIndex =0;
			for(int a=0;a<listobject.size();a++){
				LazyDynaBean myBean = (LazyDynaBean) listobject.get(a);
				String to_yobject = (String) myBean.get("to_y");
				tobj = tobj+ Integer.parseInt(to_yobject);
				osize = osize+ Integer.parseInt(to_yobject);
			}
			if(tobj>=1)
				tobj = tobj -1;
			for(int a=0;a<listobject.size();a++){
			ArrayList nextLayChild = new ArrayList();
			LazyDynaBean myBean = (LazyDynaBean) listobject.get(a);
			String objName = (String) myBean.get("objName");
			String e0122 = (String) myBean.get("e0122");
			String b0110 = (String) myBean.get("b0110");
			String e01a1 = (String) myBean.get("e01a1");
			
			String basicf = (String) myBean.get("basicf");
			String df = (String) myBean.get("df");
			String cz = (String) myBean.get("cz");
			  pointId = myBean.get("pointId").toString();
			 this.point = pointId;
			  pointName = myBean.get("pointName").toString();
			ArrayList items = this.getAllItems();
			int  to_y = items.size();
			rowIndex  = rowIndex + to_y;
			if(to_y>=1)
				to_y = to_y-1;
//			if(rowIndex>=1)
//				rowIndex = rowIndex -1;
			HSSFRichTextString value =null;
			 colIndex = 0;
			if(a==0){
				//2+tobj*x+x
				//2+tobj-osize+1+x
			this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,x + 1+"", style2, sheet);	
			colIndex++;
			if(object_type!=2)
			{
				value = cellStr(objName);
				this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
				colIndex++;
			}
			else
			{				
				if(AdminCode.getCodeName("UN",b0110)!=null&&AdminCode.getCodeName("UN",b0110).length()>0)
					b0110=AdminCode.getCodeName("UN",b0110);
				if(AdminCode.getCodeName("@K",b0110)!=null&&AdminCode.getCodeName("@K",b0110).length()>0)
					e01a1=AdminCode.getCodeName("@K",e01a1);
				value = cellStr(b0110);
				this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
				colIndex++;
				
				value = cellStr(e0122);
				this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
				colIndex++;
				
				value = cellStr(e01a1);
				this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
				colIndex++; 
				
				value = cellStr(objName);
				this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style2, sheet);
				colIndex++;
				if (this.onlyname != null && !"a0101".equals(this.onlyname))
				{
					String onlynamevalue = (String) myBean.get("onlyname");
					value = cellStr(onlynamevalue);
					this.executeCell(2+tobj-osize+1+x, colIndex, 2+tobj+x, colIndex,value.toString(), style3, sheet);
					colIndex++;
				}
			}
			}else{
				colIndex++;//序号
				if(object_type!=2)
				{
					colIndex++;
				}
				else
				{					
					colIndex++;//b0110
					colIndex++;//e0122
					colIndex++;//e01a1
					colIndex++;//objName
					if (this.onlyname != null && !"a0101".equals(this.onlyname))
					{
						colIndex++;
					}
				}	
			}
				value = cellStr(pointName);
				this.executeCell(2+tobj-osize+1+x+rowIndex-to_y-1, colIndex,2+tobj-osize+1+x+rowIndex-1, colIndex,value.toString(), style3, sheet);
				colIndex++;
			
			 list = getChildren("");
			
			for(int  i=0;i<Integer.parseInt(maxlay);i++){
				int rowindex2 =0;
				if(i==0){
			for (int j = 0; j < list.size(); j++)
			{
				String itemid = (String) list.get(j);
				// 得到最后一代子孙的个数就是要占的列数
				int rowspan = getLastChildrenCount(itemid);
				// 得到它的直接孩子存起来作为下一层用
				if(rowspan>=1)
					rowspan = rowspan -1;
				rowindex2 = rowindex2+ rowspan;
				ArrayList theChildren = this.getChildren(itemid);
				if(theChildren.size()>0){
				for (int k = 0; k < theChildren.size(); k++)
				{
					String temp = (String) theChildren.get(k);
					nextLayChild.add(temp);
				}
				}else{
					nextLayChild.add("-1");
				}
				String childCount = getChildCount(itemid);
				int colspan = 0;
				if ("0".equals(childCount) && i < Integer.parseInt(maxlay))// 没有下级节点并且不是最后一层要跨列
					colspan = Integer.parseInt(maxlay);
				if(colspan>=1)
					colspan = colspan-1;
				String itemdesc = getItemDesc(itemid);
				value = cellStr(itemdesc);
				if(itemdesc!=null&&itemdesc.length()>20)
					sheet.setColumnWidth((short) colIndex, (short) itemdesc.length()*480);
				this.executeCell(2+tobj-osize+1+x+rowIndex-to_y+rowindex2-rowspan+j-1, colIndex, 2+tobj-osize+1+x+rowIndex-to_y+rowindex2+j-1, colIndex+colspan,value.toString(), style3, sheet);
				
			}
			}else{
				 list = (ArrayList)nextLayChild.clone();
				 nextLayChild = new ArrayList();
				for (int j = 0; j < list.size(); j++)
				{
					String itemid = (String) list.get(j);
					// 得到最后一代子孙的个数就是要占的列数
					int rowspan=0;
					int colspan = 0;
					if(!"-1".equals(itemid)){
						
					 rowspan = getLastChildrenCount(itemid);
					// 得到它的直接孩子存起来作为下一层用
					if(rowspan>=1)
						rowspan = rowspan -1;
					rowindex2 = rowindex2+ rowspan;
					ArrayList theChildren = this.getChildren(itemid);
					if(theChildren.size()>0){
					for (int k = 0; k < theChildren.size(); k++)
					{
						String temp = (String) theChildren.get(k);
						nextLayChild.add(temp);
					}
					}else{
						nextLayChild.add("-1");
					}
					String childCount = getChildCount(itemid);
					
					if ("0".equals(childCount) && i < Integer.parseInt(maxlay))// 没有下级节点并且不是最后一层要跨列
						colspan = Integer.parseInt(maxlay)-i;
					if(colspan>=1)
						colspan = colspan-1;
					String itemdesc = getItemDesc(itemid);
					value = cellStr(itemdesc);
					if(itemdesc!=null&&itemdesc.length()>20)
						sheet.setColumnWidth((short) colIndex+i, (short) itemdesc.length()*480);
					
					this.executeCell(2+tobj-osize+1+x+rowIndex-to_y+rowindex2-rowspan+j-1, colIndex+i, 2+tobj-osize+1+x+rowIndex-to_y+rowindex2+j-1, colIndex+i+colspan,value.toString(), style3, sheet);
					}else{
						nextLayChild.add("-1");
					}
					
				}
			}
			}
			colIndex = colIndex+Integer.parseInt(maxlay);
			for (int n = 0; n < items.size(); n++)
			{
				LazyDynaBean bean2 = (LazyDynaBean) items.get(n);
				String item = (String) bean2.get("item");
				String s_value = (String) myBean.get(item);

				value = cellStr(s_value);
				this.executeCell(2+tobj-osize+1+x+rowIndex-to_y+n-1, colIndex, 2+tobj-osize+1+x+rowIndex-to_y+n-1, colIndex,value.toString(), style2, sheet);
			}
			colIndex++;
			value = cellStr(basicf);
			this.executeCell(2+tobj-osize+1+x+rowIndex-to_y-1, colIndex, 2+tobj-osize+1+x+rowIndex-1, colIndex,value.toString(), style2, sheet);
			colIndex++;
			value = cellStr(cz);
			this.executeCell(2+tobj-osize+1+x+rowIndex-to_y-1, colIndex, 2+tobj-osize+1+x+rowIndex-1, colIndex,value.toString(), style2, sheet);
			colIndex++;
			value = cellStr(df);
			this.executeCell(2+tobj-osize+1+x+rowIndex-to_y-1, colIndex, 2+tobj-osize+1+x+rowIndex-1, colIndex,value.toString(), style2, sheet);
			colIndex++;
		}
			x++;
		}
		}
		
	}
	public void setPoint(String point) {
		this.point = point;
	}		
	/** 分析批量导入Excel字段
	 * file 导入Excel
	 * list 需要导出字段
	 * must 必须的指标
	 * readcomment 读取每个单元格的备注? ture 读false不读 为true时 list传入的值为 备注中的值，false时为表格里的值
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InvalidFormatException 
	 * */
	public ArrayList anExcel(FormFile file,ArrayList list,String must,boolean readcomment,HashMap filtermap)throws GeneralException, InvalidFormatException, FileNotFoundException, IOException{
		ArrayList beanlist=new ArrayList();
		Workbook wb = null;
		Sheet sheet = null;
		HashMap filedMap=new HashMap();
		HashMap colMap=new HashMap();//每个列指标所在的列
		LazyDynaBean bean=null;
		String cn="";//唯一标识指标所在列
		RecordVo vo=this.getPerPlanVo(planid);
		String object_type = String.valueOf(vo.getInt("object_type")); // 1部门 2：人员
		int matchcont=0;
		int allcont=0;
		HashMap obmap=this.getobject(object_type,must);//获得用户的操作单位或管理范围下所有 唯一性指标	
		InputStream inputStream = null;
		try{	
			inputStream = file.getInputStream();
			wb=WorkbookFactory.create(inputStream);
		
			sheet=wb.getSheetAt(0);
			Row row = null;
			int rows = sheet.getPhysicalNumberOfRows();
			if(rows==0){
				this.setErrors("请导入填写正确数据的文件！");
				return new ArrayList();
				//throw new GeneralException("请导入填写正确数据的文件！");
			}
			for(int i=1;i<list.size();i++){
				filedMap.put(list.get(i), list.get(i));
			}
			for (int j = 0; j < rows; j++)
			{
				row = sheet.getRow(j);
				if(row==null){
					this.setErrors("请用下载的模板导入目标数据！");
					return new ArrayList();
					//throw new GeneralException("请用下载的模板导入目标数据！");
				}
				int cols=row.getPhysicalNumberOfCells();
				Cell cell = null;
				
				if(colMap==null||colMap.size()==0){//首先载入各列的指标名字，然后看看是不是标准模板。载入各指标名字后，它就不为空了。
					for(int i=0;i<cols;i++){
						cell=row.getCell(i);
						if(cell == null){
						    continue;
                        }
						String value="";
						switch(cell.getCellType()){
							case Cell.CELL_TYPE_STRING:
								value=cell.getStringCellValue();
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell.getNumericCellValue();
								value = Double.toString(y);
						}
						if(value!=null){
							if(filedMap.get(value)!=null){
								if("唯一标识".equalsIgnoreCase(value)){
									cn=String.valueOf(i);
								}
								colMap.put(String.valueOf(i),value);
							}
						}
					}
					if(colMap.size()>0&&colMap.size()<3){//列记录数量小于三说明导入模板不正确
						this.setErrors("请完整制作导入模板！包括'姓名/单位/部门','唯一标识','修正分值','修正原因'.");
						return new ArrayList();
						//throw new GeneralException("请完整制作导入模板！包括'姓名/单位/部门','唯一标识','修正分值','修正原因'.");
					}
				}else{///开始装入各列的数据
					//colMap中存放着 {3=修正原因, 2=修正分值, 1=唯一标识}
					bean=new LazyDynaBean();
					String a0101="";
					Set set = colMap.entrySet();
					for(int i=0;i<cols;i++){   
						cell=row.getCell(i);
						if(cell==null){
							String name = (String) colMap.get(String.valueOf(i));
							this.setErrors("第"+j+"行,第"+(i+1)+"列，'"+name+"'不能为空，请检查填写！");
							return new ArrayList();
						}
						String value="";
						switch(cell.getCellType()){
							case Cell.CELL_TYPE_STRING:
								value=cell.getStringCellValue();
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell.getNumericCellValue();
								value = Double.toString(y);
								break;
						}
						if(value!=null&&value.trim().length()!=0){    //如果这一列有数据
							
							if(colMap.get(String.valueOf(i))!=null){  
								if("修正分值".equalsIgnoreCase((String)colMap.get(String.valueOf(i)))){//如果这一列是修正分值
									if(cell.getCellType()!=Cell.CELL_TYPE_NUMERIC){//如果数据格式错误
										this.setErrors("修正分值必须为数值类型！请校正！");
										return new ArrayList();
										//throw GeneralExceptionHandler.Handle(new Exception("修正分值必须为数值类型！请校正！"));
									}
								}
								if(i==Integer.parseInt(cn)){ //如果是唯一指标那一列，那么bean单独赋值
									
									LazyDynaBean abean=(LazyDynaBean)obmap.get(value);
									if(abean!=null){
										if("2".equalsIgnoreCase(object_type)){
											a0101= (String)abean.get("a0101");
											bean.set("a0101", abean.get("a0101"));
											bean.set("object_id", abean.get("a0100"));
											bean.set("唯一标识",value);
										}else{
											 a0101= (String)abean.get("desc");
											bean.set("a0101", abean.get("desc"));
											bean.set("object_id", abean.get("b0110"));
											bean.set("唯一标识",value);
										}
									}else{
										continue;
									}
								}else{
									bean.set((String)colMap.get(String.valueOf(i)), value);
								}
							}
						}else{    //如果这一列没有数据
							this.setErrors("第"+j+"行,第"+(i+1)+"列"+(String)colMap.get(String.valueOf(i))+"值为空！请填写！");
							return new ArrayList();
							//throw new GeneralException("第"+j+"行,第"+(i+1)+"列"+(String)colMap.get(String.valueOf(i))+"值为空！请填写！");
						}
					}
					if(filtermap.get(a0101)!=null){
						beanlist.add(bean);
					}else{
						continue;
					}
				}
				
			}
			if(colMap==null||colMap.size()==0){
				this.setErrors("导入文件不包含所需导入数据！");
				return new ArrayList();
				//throw new GeneralException("导入文件不包含所需导入数据！");
			}
			
			if(rows>1){
				allcont=rows-1;
			}
			if(beanlist.size()>0){
				matchcont=beanlist.size();
			}
			LazyDynaBean bean1=new LazyDynaBean();
			bean1.set("allcont", String.valueOf(allcont));
			bean1.set("matchcont", String.valueOf(matchcont));
			bean1.set("dismatchcont", String.valueOf(allcont-matchcont));
		ArrayList list1=new ArrayList();
		list1.add(beanlist);
		list1.add(bean1);
		return list1;
		} catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(inputStream);//资源释放 jingq 2014.12.29
			PubFunc.closeResource(wb);
		}
	}
	public HashMap getobject(String type,String only){
		HashMap objectMap=new HashMap();
		StringBuffer sql=new StringBuffer("select usra01.A0100,usra01.a0101,usra01."+only+" from usra01 where ");
		StringBuffer tem=new StringBuffer();
		if("2".equalsIgnoreCase(type)){
			if(this.userview.isSuper_admin()){
				tem.append(" or 1=1");
			}else{
				
				String unitcode=this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
				if(unitcode.indexOf("`")!=-1){
					if(unitcode.length()==3){
						tem.append(" or 1=1");
					}else{
						String []unit=unitcode.split("`");
						for(int i=0;i<unit.length;i++){
							if ("UN".equalsIgnoreCase(unit[i].substring(0, 2)))
								tem.append(" or  b0110 like '" + unit[i].substring(2) + "%'");
							else if ("UM".equalsIgnoreCase(unit[i].substring(0, 2))){
								tem.append(" or  e0122 like '" + unit[i].substring(2) + "%'");
							}
						}
					}
				}else{
					String codeid=userview.getManagePrivCode();
					String codevalue=userview.getManagePrivCodeValue();
					String a_code=codeid+codevalue;
					
					if(a_code.trim().length()>0)//说明授权了
					{
						if("UN".equalsIgnoreCase(a_code))//说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
							tem.append(" or 1=1 ");
						else
						{
							if(AdminCode.getCodeName("UN",codevalue)!=null&&AdminCode.getCodeName("UN",codevalue).length()>0)
								tem.append(" or b0110 like '"+codevalue+"%'");
							else if(AdminCode.getCodeName("UM",codevalue)!=null&&AdminCode.getCodeName("UM",codevalue).length()>0)
								tem.append(" or e0122 like '"+codevalue+"%'");	
						}
					}else
						tem.append(" or 1=2 ");
				}
			}
			sql.append(tem.substring(3));
		}else{
			sql.setLength(0);
			sql.append("select org.codeitemdesc,b01.b0110,b01."+only+" from b01 left join organization org on org.codeitemid=b01.b0110 where ");
			if(this.userview.isSuper_admin()){
				tem.append(" or 1=1");
			}else{
				
				String unitcode=this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
				if(unitcode.indexOf("`")!=-1){
					if(unitcode.length()==3){
						tem.append(" or 1=1");
					}else{
						String []unit=unitcode.split("`");
						for(int i=0;i<unit.length;i++){
							
								tem.append(" or  b01.b0110 like '" + unit[i].substring(2) + "%'");
							
						}
					}
				}else{
					String codeid=userview.getManagePrivCode();
					String codevalue=userview.getManagePrivCodeValue();
					String a_code=codeid+codevalue;
					
					if(a_code.trim().length()>0)//说明授权了
					{
						if("UN".equalsIgnoreCase(a_code))//说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
							tem.append(" or 1=1 ");
						else
						{
							if(AdminCode.getCodeName("UN",codevalue)!=null&&AdminCode.getCodeName("UN",codevalue).length()>0)
								tem.append(" or b01.b0110 like '"+codevalue+"%'");
							else if(AdminCode.getCodeName("UM",codevalue)!=null&&AdminCode.getCodeName("UM",codevalue).length()>0)
								tem.append(" or   b01.b0110  like '"+codevalue+"%'");	
						}
					}else
						tem.append(" or 1=2 ");
				}
			}
			sql.append(tem.substring(3));
		}
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.cn);
		try {
			rs=dao.search(sql.toString());
			while(rs.next()){
				LazyDynaBean bean =new LazyDynaBean();
				if("2".equalsIgnoreCase(type)){
					
					String a0100=rs.getString("a0100");
					String a0101=rs.getString("a0101");
					String oname=rs.getString(only);
					bean.set("a0100", a0100);
					bean.set("a0101", a0101);
					bean.set("only", oname);
					if(oname!=null&&oname.trim().length()>0){
						objectMap.put(oname, bean);
					}
				}else{
					String a0100=rs.getString("b0110");
					String a0101=rs.getString("codeitemdesc");
					String oname=rs.getString(only);
					bean.set("b0110", a0100);
					bean.set("desc", a0101);
					bean.set("only", oname);
					objectMap.put(oname, bean);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String unitcodevalue=this.userview.getManagePrivCode();
		return objectMap;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}
	
}