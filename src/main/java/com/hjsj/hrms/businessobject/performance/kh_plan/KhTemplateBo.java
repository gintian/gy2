package com.hjsj.hrms.businessobject.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>Title:KhTemplateBo.java</p>
 * <p>Description:设置动态项目权重(分值)</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-18 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class KhTemplateBo
{
    private UserView userView = null;
    
	private Connection conn;

	private int td_width = 130;

	private int td_height = 30;

	private String isVisible;

	DecimalFormat myformat1 = new DecimalFormat("########.########");//

	private ArrayList templateItemList = new ArrayList();

	private ArrayList leafItemList = new ArrayList();

	// 共性项目对应指标
	private HashMap itemToPointMap = new HashMap();

	// 个性项目对应指标
	private HashMap selfItemToPointMap = new HashMap();

	private int lay = 0;

	private HashMap itemPointNum = new HashMap();

	private HashMap leafItemLinkMap = new HashMap();

	private HashMap itemHaveFieldList = new HashMap();

	private HashMap childItemLinkMap = new HashMap();

	/** 当前模板权重分值标识 */
	private String status;

	private String score_str = "";

	public String getScore_str()
	{
		return this.score_str;
	}

	private HashMap layMap = new HashMap();

	private ArrayList parentList = new ArrayList();

	private HashMap ifHasChildMap = new HashMap();

	public String getStatus()
	{
		return this.status;
	}
	public KhTemplateBo(Connection conn) 
	{
		this.conn = conn;
	}
	private String planid = "";

	private String templateID = "";

	private String objTypeId = "";

	private int planStatus = 0;

	/** 动态项目的页面的所有可见的项目 */
	private HashMap dynaItemMap = new HashMap();

	/** 界面上可以选中删除的项目 */
	private HashMap canDelItems = new HashMap();

	public KhTemplateBo(Connection conn, String isVisible, String objTypeId, String planid) throws Exception
	{
		this.conn = conn;
		this.isVisible = isVisible;
		this.dynaItemMap = this.getDynaItemMap(objTypeId, planid);

		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setInt("plan_id", Integer.parseInt(planid));
		vo = dao.findByPrimaryKey(vo);
		String templateID = vo.getString("template_id");
		this.planStatus = vo.getInt("status");

		this.planid = planid;
		this.templateID = templateID;
		this.objTypeId = objTypeId;
		this.canDelItems = this.getCanDelItems();
		LoadXml loadxml = new LoadXml(this.conn, planid);
		planParameter = loadxml.getDegreeWhole();
		/** 项目对应指标 */
		this.itemToPointMap = this.getItemToPointMap(templateID);
		/** 模板对应的所有项目 */
		this.templateItemList = getTemplateItemList2(templateID);
		/** 得到项目中所有的叶子项目 */
		get_LeafItemList();
		/** 项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表 */
		this.leafItemLinkMap = getLeafItemLinkMap();
		/** 每个项目对应的叶子节点个数 */
		this.itemPointNum = getItemPointNum();
		/** 项目对应的指标个数 */
		// this.itemHasFieldNum=this.getItemHasFieldCount(templateID);//各项目包含的指标个数
		/** 项目id对应的指标的详细信息列表 */
		this.itemHaveFieldList = this.getItemHasFieldList();// 指标信息
		/** 当前模板的权重分值标识 */
		this.status = this.getTemplateStatus(templateID);
		// HashMap subItemMap=(HashMap)list.get(3); //各项目的子项目(hashmap)
		/** 除叶子节点外的节点的指标数量 */
		this.childItemLinkMap = this.getChildItemLinkMap();
		this.doMethod2();
	}

	public RecordVo get_TemplateVo()
	{		
		RecordVo vo=new RecordVo("per_template");
		try
		{
			vo.setString("template_id",this.planVo.getString("template_id"));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	//自助 我的目标 引入上级目标卡用
	public KhTemplateBo(Connection conn,String planid) throws Exception
	{
		this.conn = conn;
		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setInt("plan_id", Integer.parseInt(planid));
		this.planVo = dao.findByPrimaryKey(vo);
		String templateID = this.planVo.getString("template_id");
		this.planid = planid;
		this.templateID = templateID;
		this.targetPointsList = this.getTargetPointsList();
		LoadXml loadxml = new LoadXml(this.conn, planid);
		planParameter = loadxml.getDegreeWhole();
	}
	private String objCode = "";// 考核对象id 带前缀

	private RecordVo planVo = null;

	private ArrayList targetPointsList = new ArrayList();

	private HashMap canChangeColorCells = new HashMap();// 可以改变颜色的单元格

	private String operFlag = "";// 考核实施动态项目权重和目标卡制定公用了这个类 加个标志来区分

	private RecordVo template_vo = null;
	
	/** 考核实施目标卡制定用 */
	public KhTemplateBo(Connection conn, String isVisible, String codeid, String planid, String flag) throws Exception
	{
		this.conn = conn;
		this.isVisible = isVisible;
		this.objCode = codeid;
		this.operFlag = flag;

		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setInt("plan_id", Integer.parseInt(planid));
		planVo = dao.findByPrimaryKey(vo);
		template_vo=this.get_TemplateVo();
		String templateID = planVo.getString("template_id");
		this.planStatus = planVo.getInt("status");
		this.planid = planid;
		this.templateID = templateID;

		LoadXml loadxml = new LoadXml(this.conn, planid);
		planParameter = loadxml.getDegreeWhole();
		this.canChangeColorCells = this.getCanChangeColorCell();
		/** 项目对应指标 */
		this.itemToPointMap = this.getItemToPointMap(templateID);// 共性项目
		this.selfItemToPointMap = this.getSelfItemToPointMap();// 个性项目
		/** 模板对应的所有项目 */
		this.templateItemList = getTemplateItemList(templateID);
		/** 得到项目中所有的叶子项目 */
		get_LeafItemList();
		/** 项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表 */
		this.leafItemLinkMap = getLeafItemLinkMap();
		/** 每个项目对应的叶子节点个数 */
		this.itemPointNum = getItemPointNum();
		/** 项目对应的指标个数 */
		// this.itemHasFieldNum=this.getItemHasFieldCount(templateID);//各项目包含的指标个数
		/** 项目id对应的指标的详细信息列表 */
		this.itemHaveFieldList = this.getItemHasFieldList2();// 指标信息
		/** 当前模板的权重分值标识 */
		this.status = this.getTemplateStatus(templateID);
		// HashMap subItemMap=(HashMap)list.get(3); //各项目的子项目(hashmap)
		/** 除叶子节点外的节点的指标数量 */
		this.childItemLinkMap = this.getChildItemLinkMap();
		this.targetPointsList = this.getTargetPointsList();
		this.doMethod2();
	}
	
	/** 考核实施目标卡制定校验用 */
	public KhTemplateBo(Connection conn, String isVisible, String codeid, String planid, String flag, UserView _userView) throws Exception
	{
		this.conn = conn;
		this.isVisible = isVisible;
		this.objCode = codeid;
		this.operFlag = flag;
		this.userView=_userView;

		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setInt("plan_id", Integer.parseInt(planid));
		planVo = dao.findByPrimaryKey(vo);
		template_vo=this.get_TemplateVo();
		String templateID = planVo.getString("template_id");
		this.planStatus = planVo.getInt("status");
		this.planid = planid;
		this.templateID = templateID;

		LoadXml loadxml = new LoadXml(this.conn, planid);
		planParameter = loadxml.getDegreeWhole();
		this.canChangeColorCells = this.getCanChangeColorCell();
		/** 项目对应指标 */
		this.itemToPointMap = this.getItemToPointMap(templateID);// 共性项目
		this.selfItemToPointMap = this.getSelfItemToPointMap();// 个性项目
		/** 模板对应的所有项目 */
		this.templateItemList = getTemplateItemList(templateID);
		/** 得到项目中所有的叶子项目 */
		get_LeafItemList();
		/** 项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表 */
		this.leafItemLinkMap = getLeafItemLinkMap();
		/** 每个项目对应的叶子节点个数 */
		this.itemPointNum = getItemPointNum();
		/** 项目对应的指标个数 */
		// this.itemHasFieldNum=this.getItemHasFieldCount(templateID);//各项目包含的指标个数
		/** 项目id对应的指标的详细信息列表 */
		this.itemHaveFieldList = this.getItemHasFieldList2();// 指标信息
		/** 当前模板的权重分值标识 */
		this.status = this.getTemplateStatus(templateID);
		// HashMap subItemMap=(HashMap)list.get(3); //各项目的子项目(hashmap)
		/** 除叶子节点外的节点的指标数量 */
		this.childItemLinkMap = this.getChildItemLinkMap();
		this.targetPointsList = this.getTargetPointsList();
		this.doMethod2();
	}

	private String object_id = "";

	private Hashtable planParameter = new Hashtable();

	ArrayList pointList = new ArrayList();// 结果表指标

	ArrayList selfItemList = new ArrayList();// 结果表个性项目

	HashMap perResultScoreMap = new HashMap();// 当前分值

	HashMap perResultScoreMap_original = new HashMap();// 调整分值前的原始分值
	
	HashMap perTableScoreMap = new HashMap();// 打分表分值
	
	HashMap perTableDegreeMap = new HashMap();// 打分表标度

	HashMap templatePointsGradeMap = new HashMap();// 模板指标标度

	ArrayList pointTemplateGrade = new ArrayList();// 指标标准标度

	ArrayList planDegreeGrade = new ArrayList();// 计划等级标度

	HashMap unifiedScoreMap = new HashMap();// 定量统一打分指标
	
	ArrayList mainbodyList =  new ArrayList();
	/** 自助评分调整用 */
	public KhTemplateBo(Connection conn, String object_id, String planid,UserView _userView) throws Exception
	{
		this.conn = conn;
		this.object_id = object_id;
		this.operFlag = "scoreAjust";
		this.userView=_userView;
		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setInt("plan_id", Integer.parseInt(planid));
		planVo = dao.findByPrimaryKey(vo);
		String templateID = planVo.getString("template_id");
		this.planid = planid;
		this.templateID = templateID;
		LoadXml loadxml = new LoadXml(this.conn, planid);
		planParameter = loadxml.getDegreeWhole();
		/** 项目对应指标 */
		this.itemToPointMap = this.getItemToPointMap(templateID);// 共性项目
		/** 模板对应的所有项目 */
		this.templateItemList = getTemplateItemList(templateID);
		/** 得到项目中所有的叶子项目 */
		get_LeafItemList();
		/** 项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表 */
		this.leafItemLinkMap = getLeafItemLinkMap();
		/** 每个项目对应的叶子节点个数 */
		this.itemPointNum = getItemPointNum();
		/** 项目对应的指标个数 */
		// this.itemHasFieldNum=this.getItemHasFieldCount(templateID);//各项目包含的指标个数
		/** 项目id对应的指标的详细信息列表 */
		this.itemHaveFieldList = this.getItemHasFieldList();// 指标信息
		/** 当前模板的权重分值标识 */
		this.status = this.getTemplateStatus(templateID);
		// HashMap subItemMap=(HashMap)list.get(3); //各项目的子项目(hashmap)
		/** 除叶子节点外的节点的指标数量 */
		this.childItemLinkMap = this.getChildItemLinkMap();
		this.doMethod2();
		/** 获得结果表指标和个性项目的分值 包括调整前后的分值 */
		this.getPerResultScore();
		/** 获得模板指标的标度 */
		this.templatePointsGradeMap = this.getTemplatePointsGrade();
		/** 指标标准标度 */
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1) {
            per_comTable = "per_grade_competence"; // 能力素质标准标度
        }
		this.pointTemplateGrade = this.getPointTemplateGrade(per_comTable);
		/** 计划等级标度 */
		this.planDegreeGrade = this.getPlanDegreeGradeList();
		/** 定量统一打分指标 */
		this.unifiedScoreMap = this.getUnifiedScorePointList(templateID);
		this.mainbodyList = this.getMainBobyBean();
	}
	
	/** 获得考核主体信息 JinChunhai 2011.03.08 */
	public ArrayList getMainBodyList(String planid,String object_id)
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql="select * from per_mainbody where plan_id="+planid+" and object_id='"+object_id+"' and status=2";
			RowSet frowset = dao.search(sql);
			while (frowset.next())
			{
				String mainbody_id = frowset.getString("mainbody_id")==null?"":frowset.getString("mainbody_id");
				String b0110 = frowset.getString("b0110")==null?"":frowset.getString("b0110");
				String e0122 = frowset.getString("e0122")==null?"":frowset.getString("e0122");
				String e01a1 = frowset.getString("e01a1")==null?"":frowset.getString("e01a1");
				if(b0110.length()!=0) {
                    b0110=AdminCode.getCodeName("UN",b0110);
                }
				if(e0122.length()!=0) {
                    e0122=AdminCode.getCodeName("UM",e0122);
                }
				if(e01a1.length()!=0) {
                    e01a1=AdminCode.getCodeName("@K",e01a1);
                }
				
				String a0101 = frowset.getString("a0101")==null?"":frowset.getString("a0101");
				String status = frowset.getString("status")==null?"0":frowset.getString("status");
				if("0".equals(status)) {
                    status = "未打分";
                } else if("1".equals(status)) {
                    status = "正在编辑";
                } else if("2".equals(status)) {
                    status = "已提交";
                } else if("3".equals(status)) {
                    status = "不打分";
                }
				
				LazyDynaBean _abean = new LazyDynaBean();
				_abean.set("a0101", a0101);
				_abean.set("b0110", b0110);
				_abean.set("e0122", e0122);
				_abean.set("e01a1", e01a1);
				_abean.set("status", status);
				_abean.set("mainbody_id", mainbody_id);
				list.add(_abean);
			}
			if (frowset != null) {
                frowset.close();
            }
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/** 获得计划的等级列表 */
	public ArrayList getPlanDegreeGradeList()
	{

		ArrayList gradeList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			if (planParameter.get("GradeClass") != null)
			{
				String GradeClass = (String) planParameter.get("GradeClass");
				RowSet rowSet = dao.search("select pds.xishu,pds.topscore,pds.bottomscore,pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="
						+ GradeClass + " order by pds.topscore desc");
				while (rowSet.next())
				{
					LazyDynaBean abean = new LazyDynaBean();
					abean.set("gradedesc", rowSet.getString("itemname"));
					abean.set("gradecode", rowSet.getString("id"));
					abean.set("top_value", rowSet.getString("topscore") == null ? new Float("0") : new Float(rowSet.getFloat("topscore")));
					abean.set("bottom_value", rowSet.getString("bottomscore") == null ?  new Float("0") : new Float(rowSet.getFloat("bottomscore")));
					String gradevalue = rowSet.getString("xishu") != null ? rowSet.getString("xishu") : "1";
					abean.set("gradevalue", gradevalue);
					gradeList.add(abean);
				}
				if (rowSet != null) {
                    rowSet.close();
                }
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return gradeList;
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
			ContentDAO dao = new ContentDAO(this.conn);
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
	 * 取得模板引入的绩效指标标度
	 * 
	 * @return
	 */
	public HashMap getTemplatePointsGrade()
	{
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer(
					"select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status ");
			sql.append(" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg ");
			sql.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id ");
			sql.append(" and  po.point_id=pg.point_id  and template_id='" + this.templateID + "'  order by pp.seq,pg.gradecode");

			RowSet rowSet = dao.search(sql.toString());
			LazyDynaBean abean = null;
			String pointId = "";
			ArrayList tempList = new ArrayList();
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				String point_id = rowSet.getString("point_id") != null ? rowSet.getString("point_id") : "";
				String gradevalue = rowSet.getString("gradevalue") != null ? rowSet.getString("gradevalue") : "";
				String gradedesc = rowSet.getString("gradedesc") != null ? rowSet.getString("gradedesc") : "";
				String gradecode = rowSet.getString("gradecode") != null ? rowSet.getString("gradecode") : "";
				Float top_value = rowSet.getString("top_value") != null ? new Float(rowSet.getFloat("top_value")) :new Float(0);
				Float bottom_value = rowSet.getString("bottom_value") != null ? new Float(rowSet.getFloat("bottom_value")) : new Float(0);
				String pointkind = rowSet.getString("pointkind") != null ? rowSet.getString("pointkind") : "";
				point_id = point_id.toUpperCase();
				if ("".equals(pointId)) {
                    pointId = point_id;
                }

				if (!point_id.equalsIgnoreCase(pointId))
				{

					map.put(pointId.toUpperCase(), tempList);
					pointId = point_id;
					tempList = new ArrayList();
				}
				abean.set("point_id", point_id);
				abean.set("gradevalue", gradevalue);
				abean.set("gradedesc", gradedesc);
				abean.set("gradecode", gradecode);
				abean.set("top_value", top_value);
				abean.set("bottom_value", bottom_value);
				abean.set("pointname", rowSet.getString("pointname"));
				abean.set("score", rowSet.getString("score"));
				abean.set("pointkind", pointkind);
				tempList.add(abean);
			}
			map.put(pointId.toUpperCase(), tempList);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 指标的标准标度 */
	public ArrayList getPointTemplateGrade(String per_comTable)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("select * from "+per_comTable+" ");
			RowSet rowSet = dao.search(sql.toString());
			LazyDynaBean abean = null;
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				String gradecode = rowSet.getString("grade_template_id") != null ? rowSet.getString("grade_template_id") : "";
				String gradedesc = rowSet.getString("gradedesc") != null ? rowSet.getString("gradedesc") : "";
				Float top_value = rowSet.getString("top_value") != null ? new Float(rowSet.getFloat("top_value")) :new Float(0);
				Float bottom_value = rowSet.getString("bottom_value") != null ? new Float(rowSet.getFloat("bottom_value")) : new Float(0);
				String gradevalue = rowSet.getString("gradevalue") != null ? rowSet.getString("gradevalue") : "";

				abean.set("gradevalue", gradevalue);
				abean.set("gradedesc", gradedesc);
				abean.set("gradecode", gradecode);
				abean.set("top_value", top_value);
				abean.set("bottom_value", bottom_value);
				list.add(abean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	   public Field getField(String fieldname, String a_type, int length, boolean key)
	    {

		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("N".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key) {
            obj.setNullable(false);
        }
		obj.setKeyable(key);	
		return obj;
	    }
	   
	public ArrayList getMainBobyBean()
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
//			String sql = "select * from per_mainbody where plan_id=" + this.planid + " and object_id='" + this.object_id + "' order by body_id,b0110,e0122,e01a1";
			StringBuffer sql = new StringBuffer("select pmb.*,pms.name from per_mainbody pmb,PER_MAINBODYSET pms ");
			sql.append(" where pmb.body_id = pms.body_id and  pmb.plan_id=" + this.planid + " and pmb.object_id='" + this.object_id  + "' ");
			sql.append(" order by pms.seq,pmb.b0110,pmb.e0122,pmb.e01a1");
			
			RowSet rs = dao.search(sql.toString());
			while (rs.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("mainbody_id", rs.getString("mainbody_id"));
				String status = rs.getString("status")==null?"0":rs.getString("status");
				String name=rs.getString("a0101");
				if("0".equals(status)) {
                    name+="<br>(未评分)";
                } else if("1".equals(status)) {
                    name+="<br>(正评分)";
                } else if("2".equals(status)) {
                    name+="<br>(已提交)";
                } else if("3".equals(status)) {
                    name+="<br>(不打分)";
                }
				abean.set("a0101", name);
				list.add(abean);
			}
			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}	
		return list;
	}   
	   
	/** 获得结果表指标和个性项目的分值 */
	public void getPerResultScore()
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			ExamPlanBo khPlanBo = new ExamPlanBo(this.conn);
			this.pointList = khPlanBo.getC_x(this.planid);// 指标
			this.selfItemList = new ArrayList();// 个性项目
			String sql = "select item_id,itemdesc from per_template_item where template_id='" + templateID + "' and kind=2";
			RowSet rs = dao.search(sql);
			while (rs.next()) {
                this.selfItemList.add("T_" + rs.getString("item_id"));
            }

			//判断结果表的字段是否建完整了
			PerEvaluationBo pe = new PerEvaluationBo(this.conn, planid, this.templateID,this.userView);
			pe.testFields(this.planid);			
			
			sql = "select * from per_result_" + this.planid + " where object_id='" + this.object_id + "'";
			rs = dao.search(sql);
			if (rs.next())
			{
				for (int i = 0; i < this.pointList.size(); i++)
				{
					String c_x = (String) this.pointList.get(i);
					perResultScoreMap.put(c_x, new Float(rs.getFloat(c_x)));
				}
				for (int i = 0; i < this.selfItemList.size(); i++)
				{
					String t_x = (String) this.selfItemList.get(i);
					perResultScoreMap.put(t_x, new Float(rs.getFloat(t_x)));
				}
				perResultScoreMap.put("org_grade", rs.getString("Org_Grade")== null ? "" :rs.getString("Org_Grade"));
				perResultScoreMap.put("a0101", rs.getString("a0101"));
				perResultScoreMap.put("score", new Float(rs.getFloat("score")));
				perResultScoreMap.put("resultdesc", rs.getString("resultdesc") == null ? "" : rs.getString("resultdesc"));
				perResultScoreMap.put("ordering", rs.getString("ordering") == null ? "" : rs.getString("ordering"));
				perResultScoreMap.put("score_adjust", rs.getString("score_adjust") == null ? "0" : rs.getString("score_adjust"));
			}
			// perResultScoreMap_original 记录调整前的分值
			String tablename = "per_result_score";
			Table table = null;
			DbWizard dbWizard = new DbWizard(this.conn);	
			if(!dbWizard.isExistTable(tablename,false))
			{
				table = new Table(tablename);
	    		table.addField(getField("id", "I", 8, true));
	    		table.addField(getField("Object_id", "A", 30,false));
	    		table.addField(getField("Plan_id", "I", 8, false));
	    		table.addField(getField("Point_id", "A", 30, false));
	    		table.addField(getField("Score", "N",10, false));
	    		table.addField(getField("AdjustScore", "N",10, false));
	    		dbWizard.createTable(table);
			}
/*			
			if(!dbWizard.isExistField(tablename,"Adjustscore",false))
			{
			    table = new Table(tablename);
				table.addField(getField("AdjustScore", "N",10, false));
				dbWizard.addColumns(table);
			}
*/				
			sql = "select * from per_result_score where plan_id=" + this.planid + " and object_id='" + this.object_id + "'";
			rs = dao.search(sql);
			while (rs.next())
					// point_id 存指标id和项目id 如：QQZSHGJZG_27 158 没有"T_"和"C_"的前缀
            {
                perResultScoreMap_original.put(rs.getString("point_id").toUpperCase(), new Float(rs.getFloat("score")));
            }
		 
			if(this.planVo.getInt("method")==1||this.planVo.getInt("method")==0)
			{
			//	sql = "select * from per_table_" + this.planid + " where object_id='" + this.object_id + "'";
				sql="select per_table_" + this.planid + ".*,per_grade.gradedesc from per_table_" + this.planid + " left join per_grade "
					+" on lower(per_table_" + this.planid + ".point_id)=lower(per_grade.point_id) and  lower(per_table_" + this.planid + ".degree_id)=lower(per_grade.gradecode)";
			}
			else if(this.planVo.getInt("method")==2)
			{
				sql="select pte.plan_id,pte.object_id,p04.p0401 point_id,pte.score,pte.degree_id,pte.mainbody_id from per_target_evaluation pte,p04 "; 
				sql+=" where pte.p0400=p04.p0400 and pte.object_id='"+this.object_id+"' and pte.plan_id="+this.planid;				
			}
			rs = dao.search(sql);
			while (rs.next())
			{
				String key = rs.getString("point_id").toUpperCase()+"`"+rs.getString("mainbody_id");
				this.perTableScoreMap.put(key, new Float(rs.getFloat("score")));
				if(this.planVo.getInt("method")==1||this.planVo.getInt("method")==0)
				{
					if(rs.getString("gradedesc")!=null) {
                        this.perTableDegreeMap.put(key, rs.getString("gradedesc"));
                    } else {
                        this.perTableDegreeMap.put(key, rs.getString("degree_id"));
                    }
				}
				else {
                    this.perTableDegreeMap.put(key, rs.getString("degree_id"));
                }
			}

			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public ArrayList getTargetPointsList()
	{
		ArrayList list = new ArrayList();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		// 取得目标跟踪显示和采集指标
		// 1.取对应于考核计划的参数设置中定义的 目标跟踪显示和采集指标
		LoadXml parameter_content = new LoadXml(this.conn, this.planid);
		Hashtable params = parameter_content.getDegreeWhole();
		String targetTraceEnabled = (String) params.get("TargetTraceEnabled");
		String targetTraceItem = "";
		String targetCollectItem = "";
		String targetDefineItem = "";
		try
		{
			if ("true".equals(targetTraceEnabled))
			{
				targetTraceItem = (String) params.get("TargetTraceItem");
				targetCollectItem = (String) params.get("TargetCollectItem");
				targetDefineItem = (String) params.get("TargetDefineItem");
			} else
			// 2.从绩效模块参数配置中取目标跟踪显示和采集指标
			{

				rowSet = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
				if (rowSet.next())
				{
					String str_value = rowSet.getString("str_value");
					if (str_value == null || (str_value != null && "".equals(str_value)))
					{

					} else
					{
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "//Per_Parameters";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						Element child;
						if (ele != null)
						{

							child = ele.getChild("TargetDefineItem");
							if (child != null) {
                                targetDefineItem = child.getTextTrim();
                            }

							child = ele.getChild("TargetTraceItem");
							if (child != null) {
                                targetTraceItem = child.getTextTrim();
                            }

							child = ele.getChild("TargetCollectItem");
							if (child != null) {
                                targetCollectItem = child.getTextTrim();
                            }
						}
					}
				}
			}

			HashMap temMap = new HashMap();// 需要排除的指标
			String[] tempArray = targetTraceItem.split(",");
			for (int i = 0; i < tempArray.length; i++)
			{
				if (tempArray[i].trim().length() > 0) {
                    temMap.put(tempArray[i].toLowerCase(), tempArray[i]);
                }
			}
			tempArray = targetCollectItem.split(",");
			for (int i = 0; i < tempArray.length; i++)
			{
				if (tempArray[i].trim().length() > 0) {
                    temMap.put(tempArray[i].toLowerCase(), tempArray[i]);
                }
			}
			//temMap.put("p0413", "标准分值");
			temMap.put("p0415", "权重");
			temMap.put("p0421", "调整后分值");
			temMap.put("p0423", "调整后权重");
			temMap.put("p0425", "变更说明");

			ArrayList fieldList = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
			for (int i = 0; i < fieldList.size(); i++)
			{
				FieldItem item = (FieldItem) fieldList.get(i);
				String itemid = item.getItemid();
				if (targetDefineItem.toLowerCase().indexOf(itemid.toLowerCase()) != -1 && temMap.get(itemid.toLowerCase()) == null) {
                    list.add(item);
                }
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/** 考核对象类别之间粘帖动态项目任务规则 */
	public void pastObjTypeItemRule(String copyObjTypeId, String pastObjTypeId)
	{
		if (copyObjTypeId.equalsIgnoreCase(pastObjTypeId)) {
            return;
        }
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			// 先删除被黏贴的考核类别中与被复制的考核类别有相同项目的记录 然后再插入新的记录
			String sql = "delete from per_dyna_item  where plan_id=" + planid + " and body_id=" + pastObjTypeId + " and item_id in ";
			sql += " (select item_id from per_dyna_item where plan_id=" + planid + " and body_id=" + copyObjTypeId + ")";
			dao.delete(sql, new ArrayList());

			sql = "insert into per_dyna_item(plan_id,item_id,body_id,dyna_value,task_rule)";
			sql += "select " + this.planid + ",item_id," + pastObjTypeId + ",dyna_value,task_rule from per_dyna_item where plan_id=" + planid + " and body_id=" + copyObjTypeId;
			dao.insert(sql, new ArrayList());

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public HashMap getDynaItemMap(String objTypeId, String planid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select item_id from per_dyna_item  where plan_id=" + planid + " and body_id=" + objTypeId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while (rs.next()) {
                map.put(rs.getString(1), "");
            }

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	public String getObjName(String object_id)
	{
		String name="";
		try
		{
			String sql = "select a0101 from per_object  where plan_id=" + this.planid + " and object_id='" + object_id +"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while (rs.next()) {
                name=rs.getString(1);
            }

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	public void doMethod(LazyDynaBean bean, ArrayList list)
	{
		String itemid = (String) bean.get("item_id");
		String childid = (String) bean.get("child_id");
		if (childid.length() == 0)
		{
			// list.add(bean);
			return;
		} else
		{
			list.add(bean);
		}
		for (int j = 0; j < this.templateItemList.size(); j++)
		{
			LazyDynaBean a_bean = (LazyDynaBean) this.templateItemList.get(j);
			String parentid = (String) a_bean.get("parent_id");
			if (parentid.equals(itemid))
			{
				doMethod(a_bean, list);
			}
		}
	}

	/**
	 * 画单元格
	 * 
	 * @param context
	 * @param rowspan
	 * @param align
	 * @param width
	 * @param itemid
	 * @param type
	 * @return
	 */
	private String writeTd(String context, int rowspan, String align, int width, String itemid, int type, boolean canChangeColor)
	{
		StringBuffer td = new StringBuffer("");
		td.append("\r\n<td class='RecordRow' valign='middle'  nowrap align='" + align + "' style=\"border-left:0px;\"");
		if (canChangeColor)
		{
			td.append(" onclick='changeColor(\"" + itemid + "\",\"" + type + "\");' id='" + itemid + "'");
			
			// 陈总要求项目名称不可以编辑 故在此注释掉   JinChunhai 2011.12.31
		//	if (planStatus==8 || planStatus==3  || planStatus==5)  // 个性项目可以编辑
		//		td.append(" ondblclick='gaibian(\"" + itemid + "\",\"" + type + "\");'");
		}

		if (rowspan != 0) {
            td.append(" rowspan='" + (rowspan) + "' ");
        } else {
            td.append(" height='" + td_height + "' ");
        }
		td.append("  width='" + width + "'");
		td.append(" >");
		td.append(context);
		td.append("</td>");
		return td.toString();
	}

	private String writeTd2(String context, int rowspan, String align, int width, String itemid)
	{
		StringBuffer td = new StringBuffer("");
		td.append("\r\n<td class='RecordRow' valign='middle' align='" + align + "'  width='100' nowrap");
		if (this.canDelItems.get(itemid) != null) {
            td.append(" onclick='changeColor(\"" + itemid + "\");' id='" + itemid + "'");
        }
		if (rowspan != 0) {
            td.append(" rowspan='" + (rowspan) + "' ");
        } else {
            td.append(" height='" + td_height + "' ");
        }
		td.append("  width='" + width + "'");
		td.append(" >");
		td.append(context);
		td.append("</td>");
		return td.toString();
	}

	public void doMethod2()
	{
		for (int i = 0; i < parentList.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) parentList.get(i);
			String itemid = (String) bean.get("item_id");
			layMap.put(itemid, "1");
			doM(bean, 1);
		}
	}

	public void doM(LazyDynaBean bean, int lay)
	{
		lay++;
		for (int i = 0; i < this.templateItemList.size(); i++)
		{
			LazyDynaBean a_bean = (LazyDynaBean) this.templateItemList.get(i);
			String itemid = (String) bean.get("item_id");
			String a_itemid = (String) a_bean.get("item_id");
			String parentid = (String) a_bean.get("parent_id");
			if (parentid.equals(itemid))
			{
				ifHasChildMap.put(itemid, "1");
				layMap.put(a_itemid, lay + "");
				doM(a_bean, lay);
			}
		}
	}

	public HashMap getItemToPointMap(String templateID)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select ptp.*,pp.pointname from per_template_point ptp,per_point pp where  ptp.item_id in (");
			sql.append("select item_id from per_template_item where UPPER(template_id)='");
			sql.append(templateID + "') and ptp.point_id=pp.point_id order by ptp.seq");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while (rs.next())
			{
				bean = new LazyDynaBean();

				bean.set("point_id", rs.getString("point_id"));
				bean.set("score", rs.getString("score") == null ? "0" : this.myformat1.format(rs.getDouble("score")));
				bean.set("rank", rs.getString("rank") == null ? "0" : this.myformat1.format(rs.getDouble("rank")));
				bean.set("item_id", rs.getString("item_id"));
				bean.set("pointname", rs.getString("pointname"));
				String item_id = rs.getString("item_id");
				if (map.get(item_id) != null)
				{
					ArrayList list = (ArrayList) map.get(item_id);
					list.add(bean);
					map.put(item_id, list);
				} else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(item_id, list);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	// 个性项目对应的指标或任务
	public HashMap getSelfItemToPointMap()
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select * from p04 where plan_id=" + this.planid);
			if (this.planVo.getInt("object_type") == 2) {
                buf.append(" and a0100='" + this.objCode.substring(1) + "'");
            } else {
                buf.append(" and b0110='" + this.objCode.substring(2) + "'");
            }
			buf.append(" and item_id in (select item_id from per_template_item where template_id='" + templateID + "' and kind=2) order by item_id, seq");

			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString());
			LazyDynaBean bean = null;
			while (rs.next())
			{
				String chg_type = rs.getString("chg_type");
				if(chg_type!=null && "3".equals(chg_type)) {
                    continue;
                }
				
				bean = new LazyDynaBean();
				bean.set("point_id", rs.getString("p0401"));
				bean.set("score", rs.getString("p0413") == null ? "0" : this.myformat1.format(rs.getDouble("p0413")));
				bean.set("rank", rs.getString("p0415") == null ? "0" : this.myformat1.format(rs.getDouble("p0415")));
				bean.set("item_id", rs.getString("item_id"));
				bean.set("pointname", rs.getString("p0407"));
				String item_id = rs.getString("item_id");
				if (map.get(item_id) != null)
				{
					ArrayList list = (ArrayList) map.get(item_id);
					list.add(bean);
					map.put(item_id, list);
				} else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(item_id, list);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 取得 模板项目记录
	 * 
	 * @return
	 */
	public ArrayList getTemplateItemList(String templateID)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search("select * from  per_template_item where template_id='" + templateID + "'  order by seq");
			LazyDynaBean abean = null;
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				abean.set("item_id", rowSet.getString("item_id"));
				abean.set("parent_id", rowSet.getString("parent_id") != null ? rowSet.getString("parent_id") : "");
				abean.set("child_id", rowSet.getString("child_id") != null ? rowSet.getString("child_id") : "");
				abean.set("template_id", rowSet.getString("template_id"));
				abean.set("itemdesc", PubFunc.toHtml(rowSet.getString("itemdesc")));
				abean.set("seq", rowSet.getString("seq"));
				abean.set("kind", rowSet.getString("kind") != null ? rowSet.getString("kind") : "1");
				abean.set("score", rowSet.getString("score") == null ? "0" : this.myformat1.format(rowSet.getDouble("score")));
				abean.set("rank", rowSet.getString("rank") == null ? "0" : this.myformat1.format(rowSet.getDouble("rank")));
				abean.set("rank_type", rowSet.getString("rank_type") != null ? rowSet.getString("rank_type") : "");
				list.add(abean);
				if (rowSet.getString("parent_id") == null || "".equals(rowSet.getString("parent_id")))
				{
					this.parentList.add(abean);
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**动态项目权重展示的界面需要过滤掉动态项目表里没有的项目记录*/
	public ArrayList getTemplateItemList2(String templateID)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet =null;
			LazyDynaBean abean = null;
			StringBuffer buf = new StringBuffer();
			HashMap tempMap = new HashMap();
			buf.append("select item_id,parent_id from per_template_item where template_id='" + templateID + "' order by seq");
			rowSet = dao.search(buf.toString());
			while (rowSet.next()) {
                tempMap.put( rowSet.getString("item_id"),  rowSet.getString("parent_id"));
            }
			
			HashMap tempMap2 = new HashMap();
			Set keySet = this.dynaItemMap.keySet();
			for(Iterator it = keySet.iterator();it.hasNext();)
			{   
			   String item_id = (String)it.next();   
			   tempMap2.put(item_id, item_id);
			   
			   while(tempMap.get(item_id)!=null)
			   {
				   item_id=(String)tempMap.get(item_id);
				   tempMap2.put(item_id, item_id);
			   }
			   if(tempMap.get(item_id)==null) {
                   tempMap2.put(item_id, item_id);
               }
			} 
			
			rowSet = dao.search("select * from per_template_item where template_id='" + templateID + "' order by seq");			
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				abean.set("item_id", rowSet.getString("item_id"));
				if(tempMap2.get(rowSet.getString("item_id"))==null) {
                    continue;
                }
				abean.set("parent_id", rowSet.getString("parent_id") != null ? rowSet.getString("parent_id") : "");
				String child_id = rowSet.getString("child_id") != null ? rowSet.getString("child_id") : "";
			//	if(child_id.trim().length()>0 && tempMap2.get(child_id)==null)
			//		child_id="";
				abean.set("child_id",child_id);
				abean.set("template_id", rowSet.getString("template_id"));
				abean.set("itemdesc", PubFunc.toHtml(rowSet.getString("itemdesc")));
				abean.set("seq", rowSet.getString("seq"));
				abean.set("kind", rowSet.getString("kind") != null ? rowSet.getString("kind") : "1");
				abean.set("score", rowSet.getString("score") == null ? "0" : this.myformat1.format(rowSet.getDouble("score")));
				abean.set("rank", rowSet.getString("rank") == null ? "0" : this.myformat1.format(rowSet.getDouble("rank")));
				abean.set("rank_type", rowSet.getString("rank_type") != null ? rowSet.getString("rank_type") : "");
				list.add(abean);
				if (rowSet.getString("parent_id") == null || "".equals(rowSet.getString("parent_id")))
				{
					this.parentList.add(abean);
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 叶子项目列表
	 */
	public void get_LeafItemList()
	{
		LazyDynaBean abean = null;
		for (int i = 0; i < this.templateItemList.size(); i++)
		{
			abean = (LazyDynaBean) this.templateItemList.get(i);
			String parent_id = (String) abean.get("parent_id");
			if (parent_id.length() == 0)
			{
				setLeafItemFunc(abean);
			}
		}
	}

	// 递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean)
	{
		String item_id = (String) abean.get("item_id");
		String child_id = (String) abean.get("child_id");
		if (child_id.length() == 0 || isLeaf(item_id, child_id))
		{
			this.leafItemList.add(abean);
			return;
		}
		LazyDynaBean a_bean = null;
		for (int j = 0; j < this.templateItemList.size(); j++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(j);
			String parent_id = (String) a_bean.get("parent_id");
			if (parent_id.equals(item_id)) {
                setLeafItemFunc(a_bean);
            }
		}
	}

	public boolean isLeaf(String item_id, String child_id)
	{
		boolean flag = true;
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select * from per_template_item where parent_id=" + item_id + " or item_id=" + child_id);
			while (rs.next())
			{
				flag = false;
				break;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 叶子项目对应的继承关系
	 * 
	 * @return
	 */
	public HashMap getLeafItemLinkMap()
	{
		HashMap map = new HashMap();
		try
		{
			LazyDynaBean abean = null;
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
				String parent_id = (String) abean.get("parent_id");
				ArrayList linkList = new ArrayList();
				getParentItem(linkList, abean);
				if (linkList.size() > lay) {
                    lay = linkList.size();
                }
				map.put(item_id, linkList);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	// 寻找继承关系
	public void getParentItem(ArrayList list, LazyDynaBean abean)
	{
		String item_id = (String) abean.get("item_id");
		String parent_id = (String) abean.get("parent_id");
		/** 顶级节点 */
		if (parent_id.length() == 0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean = null;
		for (int i = 0; i < templateItemList.size(); i++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(i);
			String itemid = (String) a_bean.get("item_id");
			String parentid = (String) a_bean.get("parent_id");
			if (itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list, a_bean);
			}
		}
	}

	/**
	 * 取得模板权重分值标识
	 * 
	 * @param templateID
	 * @return
	 */
	public String getTemplateStatus(String templateID)
	{
		String status = "0";
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select status from per_template where UPPER(template_id)='" + templateID.toUpperCase() + "'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString());
			while (rs.next())
			{
				status = rs.getString("status") == null ? "0" : rs.getString("status");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * 除叶子节点外的节点的指标数量
	 * 
	 * @return
	 */
	public HashMap getChildItemLinkMap()
	{
		HashMap map = new HashMap();
		for (int i = 0; i < this.templateItemList.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) this.templateItemList.get(i);
			ArrayList list = new ArrayList();
			doMethod(bean, list);
			LazyDynaBean aa_bean = null;
			int n = 0;
			for (int j = 0; j < list.size(); j++)
			{
				aa_bean = (LazyDynaBean) list.get(j);
				String item_id = (String) aa_bean.get("item_id");
				if (itemToPointMap.get(item_id) != null) {
                    n += ((ArrayList) itemToPointMap.get(item_id)).size();
                }

			}
			map.put((String) bean.get("item_id"), new Integer(n));
		}
		return map;
	}

	public HashMap getItemHasFieldList()
	{

		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			for (int i = 0; i < this.templateItemList.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) this.templateItemList.get(i);
				String itemid = (String) bean.get("item_id");
				String kind = (String) bean.get("kind");
				StringBuffer sql = new StringBuffer();
				sql.append("select ptp.point_id,ptp.score,ptp.seq,ptp.rank,po.pointname from per_template_point ptp,per_point po where ptp.point_id=po.point_id and item_id=" + itemid
						+ " order by ptp.seq");
				rs = dao.search(sql.toString());
				ArrayList list = new ArrayList();
				while (rs.next())
				{
					LazyDynaBean a_bean = new LazyDynaBean();
					a_bean.set("itemid", itemid);
					a_bean.set("point_id", rs.getString("point_id"));
					a_bean.set("score", rs.getString("score") == null ? "0" : this.myformat1.format(rs.getDouble("score")));
					a_bean.set("seq", rs.getString("seq"));
					a_bean.set("name", rs.getString("pointname"));
					a_bean.set("rank", rs.getString("rank") == null ? "0" : this.myformat1.format(rs.getDouble("rank")));
					list.add(a_bean);
				}
				map.put(itemid, list);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getItemHasFieldList2()
	{

		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			for (int i = 0; i < this.templateItemList.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) this.templateItemList.get(i);
				String itemid = (String) bean.get("item_id");
				String kind = (String) bean.get("kind");
				StringBuffer sql = new StringBuffer();

					StringBuffer buf = new StringBuffer();
					buf.append("select * from p04 where plan_id=" + this.planid);
					if (this.planVo.getInt("object_type") == 2) {
                        buf.append(" and a0100='" + this.objCode.substring(1) + "'");
                    } else {
                        buf.append(" and b0110='" + this.objCode.substring(2) + "'");
                    }
					buf.append(" and item_id ='" + itemid + "' order by  seq");

					rs = dao.search(buf.toString());
					ArrayList list = new ArrayList();
					while (rs.next())
					{
						String chg_type = rs.getString("chg_type");
						if(chg_type!=null && "3".equals(chg_type)) {
                            continue;
                        }
						LazyDynaBean a_bean = new LazyDynaBean();
						a_bean.set("point_id", rs.getString("p0401"));
						a_bean.set("score", rs.getString("p0413") == null ? "0" : this.myformat1.format(rs.getDouble("p0413")));
						a_bean.set("rank", rs.getString("p0415") == null ? "0" : this.myformat1.format(rs.getDouble("p0415")));
						a_bean.set("itemid", itemid);
//						a_bean.set("name", rs.getString("p0407"));
						a_bean.set("name", rs.getString("p0407") == null ? "" :(rs.getString("p0407")));
						a_bean.set("seq", rs.getString("seq"));
						a_bean.set("fromflag", rs.getString("fromflag"));
						list.add(a_bean);
					}
					map.put(itemid, list);
			
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void getLeafItemList(LazyDynaBean abean, ArrayList list)
	{
		String item_id = (String) abean.get("item_id");
		String child_id = (String) abean.get("child_id");

		if (child_id.length() == 0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean = null;
		for (int j = 0; j < this.templateItemList.size(); j++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(j);
			String parent_id = (String) a_bean.get("parent_id");
			if (parent_id.equals(item_id)) {
                getLeafItemList(a_bean, list);
            }
		}

	}

	/**
	 * 取得项目拥有的叶子节点数
	 * 
	 * @return
	 */
	public HashMap getItemPointNum()
	{
		HashMap map = new HashMap();
		LazyDynaBean a_bean = null;
		LazyDynaBean aa_bean = null;
		for (int i = 0; i < templateItemList.size(); i++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(i);
			ArrayList list = new ArrayList();
			getLeafItemList(a_bean, list);
			int n = 0;
			for (int j = 0; j < list.size(); j++)
			{
				aa_bean = (LazyDynaBean) list.get(j);
				String item_id = (String) aa_bean.get("item_id");
				if (itemToPointMap.get(item_id) != null) {
                    n += ((ArrayList) itemToPointMap.get(item_id)).size();
                } else if (this.operFlag != null && "targetCard".equalsIgnoreCase(this.operFlag) && this.selfItemToPointMap.get(item_id) != null) {
                    n += ((ArrayList) selfItemToPointMap.get(item_id)).size();
                } else {
                    n += 1;
                }
			}
			map.put((String) a_bean.get("item_id"), new Integer(n));
		}
		return map;
	}

	/** 取得模板下各个项目的类型 */
	public HashMap getItemKind()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql = "select * from per_template_item where template_id='" + this.templateID + "'";
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				String item_id = rs.getString("item_id");
				String kind = rs.getString("kind");
				map.put(item_id, kind);
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

	/** 取得模板下各项目的分值或者权重 */
	public HashMap getItemValue(String templateID)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			// 共性项目的
			String sql = "select item_id,sum(rank) rank,sum(score) score from per_template_point where item_id in" + " (select item_id from per_template_item where template_id='" + templateID
					+ "' and kind=1) group by item_id";

			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				if ("1".equals(this.status)) {
                    map.put(rs.getString("item_id"), rs.getString("rank"));
                } else {
                    map.put(rs.getString("item_id"), rs.getString("score"));
                }
			}
			// 个性项目的
			sql = "select item_id,score,rank from per_template_item where template_id='" + templateID + "' and kind=2";
			rs = dao.search(sql);
			while (rs.next())
			{
				if ("1".equals(this.status)) {
                    map.put(rs.getString("item_id"), rs.getString("rank"));
                } else {
                    map.put(rs.getString("item_id"), rs.getString("score"));
                }
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
	
	/*按项目逐级汇总得到目标卡总分**/
	public double getTargetCardScore(HashMap itemScore_p04)
	{
		String scoreFromItem = (String) this.planParameter.get("ScoreFromItem");//按项目权重逐级计算总分 True，False 默认为False
		ContentDAO dao = new ContentDAO(this.conn);	
		RowSet rs=null;
		double targetCardScore=0;
		String sql="";
		try {
			if ("1".equals(this.status) && "true".equalsIgnoreCase(scoreFromItem))//权重模版 如果设置了该参数 则要求个性项目对应的任务指标权重之和为1
			{
				sql="select * from per_template_item where template_id='"+this.templateID+"'";
				rs = dao.search(sql);
				HashMap map = new HashMap();
				while(rs.next())
				{
					ArrayList list = new ArrayList();
					String parent_id = rs.getString("parent_id")==null?"": rs.getString("parent_id");
					String item_id = rs.getString("item_id");
					String rank = rs.getString("rank");
					LazyDynaBean abean = new LazyDynaBean();
					abean.set("item_id", item_id);
					abean.set("rank", rank);
					abean.set("parent_id", parent_id);
					if(map.get(parent_id)!=null) {
                        list=(ArrayList)map.get(parent_id);
                    }
					list.add(abean);
					map.put(parent_id, list);
				}
				return this.scoreFromItem("", map, itemScore_p04);
			}else
			{
				sql="select  sum(p0413*p0415)  from p04 where plan_id="+this.planid;
				if (this.planVo.getInt("object_type") == 2) {
                    sql+=" and a0100='" + this.objCode.substring(1) + "'";
                } else {
                    sql+=" and b0110='" + this.objCode.substring(2) + "'";
                }
				sql+="  and (chg_type is null or chg_type!=3)";
				rs = dao.search(sql);
				if (rs.next()) {
                    targetCardScore=rs.getDouble(1);
                }
			}
			if(rs!=null) {
                rs.close();
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return targetCardScore;
	}
	
	public double scoreFromItem(String parent_id,HashMap itemParentMap,HashMap itemScore_p04)
	{
		double targetCardScore=0;
		ArrayList list = (ArrayList)itemParentMap.get(parent_id);
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean abean = (LazyDynaBean)list.get(i);
			String item_id =(String)abean.get("item_id");
			String rank =(String)abean.get("rank");
			if(itemParentMap.get(item_id)==null)//叶子项目
			{
				if(itemScore_p04.get(item_id)==null) {
                    targetCardScore+= 0;
                } else {
                    targetCardScore+= Double.parseDouble(PubFunc.multiple(((Float)itemScore_p04.get(item_id)).toString(),rank,6));
                }
			}else {
                targetCardScore+=Double.parseDouble(PubFunc.multiple((new Double(this.scoreFromItem(item_id, itemParentMap, itemScore_p04))).toString(),rank,6));
            }
		}
		return targetCardScore;
	}	
	
	/**检验对象的目标卡*/
	public String testObjTargetCard()
	{
		String scoreFromItem = (String) this.planParameter.get("ScoreFromItem");//按项目权重逐级计算总分 True，False 默认为False
		StringBuffer buf = new StringBuffer();
		String sql = "";	
		ContentDAO dao = new ContentDAO(this.conn);		
		try
		{
			RowSet rs=null;
			RowSet rs1=null;
			RowSet rowSet=null;
			
			HashMap itemScore_p04=new HashMap();
			sql="select item_id,sum(p0413*p0415) from p04 where plan_id="+this.planid;
			if (this.planVo.getInt("object_type") == 2) {
                sql+=" and a0100='" + this.objCode.substring(1) + "'";
            } else {
                sql+=" and b0110='" + this.objCode.substring(2) + "'";
            }
			sql+="  and (chg_type is null or chg_type!=3)";
			sql+=" group by item_id";
			rs = dao.search(sql);
			while (rs.next()) {
                itemScore_p04.put(rs.getString(1), new Float(rs.getFloat(2)));
            }
			
			//模版总分
			double templateScore = this.template_vo.getDouble("topscore");
			//目标卡总分	
			
			String objectId = "";
			if (this.planVo.getInt("object_type") == 2) {
                objectId = this.objCode.substring(1);
            } else {
                objectId = this.objCode.substring(2);
            }
			ObjectCardBo bo = new ObjectCardBo(this.conn,this.planid,objectId,this.userView,"7","1","1"); 	
			bo.initData();
			String cardScore = bo.getTaskScore();
			
			double targetCardScore = 0.0;
			if(cardScore!=null && cardScore.trim().length()>0 && cardScore.indexOf("%")==-1) {
                targetCardScore = Double.parseDouble(cardScore.trim());
            } else {
                targetCardScore = this.getTargetCardScore(itemScore_p04);
            }
			
			double cz = 0;			
			buf.append("目标卡总分："+	DataCollectBo.roundAndRemoveZero(new Float(targetCardScore).toString(), 2)+"\n");
			buf.append("模板总分："+	DataCollectBo.roundAndRemoveZero(new Float(templateScore).toString(), 2)+"\n");
			buf.append("(说明：目标卡总分与模板总分可以不相等)\n");
																		
			HashMap taskCount_p04=new HashMap();
			HashMap maxScore_p04=new HashMap();
			StringBuffer itemTotalScoreTestBuf = new StringBuffer();
			StringBuffer taskRuleTestBuf = new StringBuffer();
			String body_id="";			
				
			sql="select item_id,count(*) from p04 where plan_id="+this.planid;
			if (this.planVo.getInt("object_type") == 2) {
                sql+=" and a0100='" + this.objCode.substring(1) + "'";
            } else {
                sql+=" and b0110='" + this.objCode.substring(2) + "'";
            }
			sql+=" and (chg_type is null or chg_type!=3)";
			sql+=" group by item_id";
			rs = dao.search(sql);
			while (rs.next()) {
                taskCount_p04.put(rs.getString(1), new Integer(rs.getInt(2)));
            }
				
			sql="select item_id,";
			if ("1".equals(this.status)) {
                sql+="max("+Sql_switcher.isnull("p0415","1" )+")";
            } else {
                sql+="max("+Sql_switcher.isnull("p0413","0" )+")";
            }
			sql+=" from p04 where plan_id="+this.planid;
			if (this.planVo.getInt("object_type") == 2) {
                sql+=" and a0100='" + this.objCode.substring(1) + "'";
            } else {
                sql+=" and b0110='" + this.objCode.substring(2) + "'";
            }
			sql+=" and (chg_type is null or chg_type!=3)";
			sql+=" group by item_id";
			rs = dao.search(sql);
			while (rs.next()) {
                maxScore_p04.put(rs.getString(1), new Float(rs.getFloat(2)));
            }
							
			sql="select body_id from per_object where plan_id="+this.planid+" and object_id='";
			if (this.planVo.getInt("object_type") == 2) {
                sql+= this.objCode.substring(1) + "'";
            } else {
                sql+= this.objCode.substring(2) + "'";
            }
			rs = dao.search(sql);
			if (rs.next()) {
                body_id=rs.getString(1)==null?"":rs.getString(1);
            }
				
			if(body_id.trim().length()>0)
			{
				//取得动态项目权重中设置的规则
				HashMap map = this.getItemValueRule(planid, body_id);
					
				sql = "select * from per_template_item where template_id='"+this.templateID+"' and kind=2";	
				rs = dao.search(sql);
				while(rs.next())
				{
					String item_id = rs.getString("item_id");
					String itemdesc= rs.getString("itemdesc");
					String itemscore_template = rs.getString("score")==null?"0":rs.getString("score");
					String itemrank = rs.getString("rank")==null?"0":rs.getString("rank");
					if(map.get(item_id)!=null)
					{
						LazyDynaBean abean = (LazyDynaBean) map.get(item_id);
						String minTaskNumber = (String)abean.get("minTaskNumber");
						String maxTaskNumber = (String)abean.get("maxTaskNumber");
						String maxScoreValue = (String)abean.get("maxScoreValue");
						String minScoreValue = (String)abean.get("minScoreValue");
						String dyna_value = (String)abean.get("dyna_value");							
							
						if(dyna_value!=null && dyna_value.length()>0 && !"0".equalsIgnoreCase(myformat1.format(Double.valueOf(dyna_value))))
						{											
							if("0".equals(this.template_vo.getString("status")))  //分值
							{									
								if(new Float(dyna_value).floatValue()>0) {
                                    itemscore_template=dyna_value;
                                }
							}									
							else if("1".equals(this.template_vo.getString("status")))  //权重
							{
								if(new Float(dyna_value).floatValue()>0) {
                                    itemrank=dyna_value;
                                }
								itemscore_template=new Double(new Double(itemscore_template).doubleValue()*new Double(itemrank).doubleValue()).toString();
							}								
							if(new Double(itemscore_template).doubleValue()==0) {
                                continue;
                            }
							Float itemTotalScore =itemScore_p04.get(item_id)!=null? (Float)itemScore_p04.get(item_id):new Float(0);
							cz = new Double(itemscore_template).doubleValue() -  itemTotalScore.floatValue();
							if(new Float(PubFunc.round(cz+"",6)).floatValue()!=0) {
                                itemTotalScoreTestBuf.append(itemdesc+"\n");
                            }
						}
							
						String temp="";
						if(minTaskNumber.length()>0)
						{
							temp="任务总数应大于等于"+minTaskNumber;
							if(maxTaskNumber.length()>0) {
                                temp+="并且小于等于"+maxTaskNumber;
                            }
						}else
						{
							if(maxTaskNumber.length()>0) {
                                temp+="任务总数应小于等于"+maxTaskNumber;
                            }
						}							
							
						String temp2 = "";
						if(minScoreValue!=null && minScoreValue.length()>0)
						{
							if("0".equals(this.template_vo.getString("status")))  //分值
							{
								temp2="每个任务分值应大于等于最小值"+minScoreValue;
								if(maxScoreValue!=null && maxScoreValue.length()>0) {
                                    temp2+="并且小于等于最大值"+maxScoreValue;
                                }
							}
							else if("1".equals(this.template_vo.getString("status")))  //权重
							{
								temp2="每个任务权重应大于等于最小值"+minScoreValue;
								if(maxScoreValue!=null && maxScoreValue.length()>0) {
                                    temp2+="并且小于等于最大值"+maxScoreValue;
                                }
							}
						}else
						{
							if("0".equals(this.template_vo.getString("status")))  //分值
							{									
								if(maxScoreValue!=null && maxScoreValue.length()>0) {
                                    temp2="每个任务分值应小于等于最大值"+maxScoreValue;
                                }
							}
							else if("1".equals(this.template_vo.getString("status")))  //权重
							{
								if(maxScoreValue!=null && maxScoreValue.length()>0) {
                                    temp2="每个任务权重应小于等于最大值"+maxScoreValue;
                                }
							}
						}
							
						if((temp==null && temp.trim().length()<=0) && (temp2==null && temp2.trim().length()<=0)) {
                            continue;
                        }
						float taskMaxScore = maxScore_p04.get(item_id)==null?0:((Float)maxScore_p04.get(item_id)).floatValue();
						int tastNumber = taskCount_p04.get(item_id)==null?0:((Integer)taskCount_p04.get(item_id)).intValue();
						
						// 判断个性项目下有无任务或指标
						String sqlStr = "select p0401 from p04 where plan_id = '" + this.planid + "' and item_id = '" + item_id + "' and (chg_type is null or chg_type!=3)";
						rowSet = dao.search(sqlStr);
						String p0401 = "";
						while(rowSet.next())
						{
							p0401 = rowSet.getString("p0401");
						}
						
						if(p0401!=null && p0401.trim().length()>0)
						{
							if((minTaskNumber.length()>0 && tastNumber<Integer.parseInt(minTaskNumber)) || (maxTaskNumber.length()>0 && tastNumber>Integer.parseInt(maxTaskNumber)))
							{
								taskRuleTestBuf.append("\n"+itemdesc+"\n");
								taskRuleTestBuf.append(temp);							
								if((maxScoreValue!=null && maxScoreValue.length()>0 && new Float(maxScoreValue).floatValue()<taskMaxScore) || (minScoreValue!=null && minScoreValue.length()>0 && new Float(minScoreValue).floatValue()>taskMaxScore)) {
                                    taskRuleTestBuf.append("\n"+temp2);
                                }
							}else
							{											
								if((maxScoreValue!=null && maxScoreValue.length()>0 && new Float(maxScoreValue).floatValue()<taskMaxScore) || (minScoreValue!=null && minScoreValue.length()>0 && new Float(minScoreValue).floatValue()>taskMaxScore))
								{
									taskRuleTestBuf.append("\n"+itemdesc+"\n");	
									taskRuleTestBuf.append(temp2);
								}								
							}
						}
					}
				}
			}else
			{
				sql = "select item_id,itemdesc,score*rank from per_template_item where template_id='"+this.templateID+"' and kind=2";	
				rs = dao.search(sql);
				while(rs.next())
				{
					String item_id = rs.getString("item_id");
					String itemdesc= rs.getString("itemdesc");
					float score = rs.getFloat(3);
					Float itemTotalScore =itemScore_p04.get(item_id)!=null? (Float)itemScore_p04.get(item_id):new Float(0);
					cz = score-itemTotalScore.floatValue();
					if(score!=0 && new Float(PubFunc.round(cz+"",6)).floatValue()!=0) {
                        itemTotalScoreTestBuf.append(itemdesc+"\n");
                    }
				}							
			}
				
			StringBuffer itemRankNoEqualOneBuf = new  StringBuffer();
			if ("1".equals(this.status) && "true".equalsIgnoreCase(scoreFromItem))//权重模版 如果设置了该参数 则要求个性项目对应的任务指标权重之和为1
			{	
				sql="select p04.item_id,sum(p04.p0415),pti.itemdesc from p04,per_template_item pti where p04.item_id=pti.item_id and p04.plan_id="+this.planid;
				sql+=" and p04.item_id in (select item_id from per_template_item where template_id='"+this.templateID+"' and kind=2)";
				if (this.planVo.getInt("object_type") == 2) {
                    sql+=" and p04.a0100='" + this.objCode.substring(1) + "'";
                } else {
                    sql+=" and p04.b0110='" + this.objCode.substring(2) + "'";
                }
				sql+="  and (p04.chg_type is null or p04.chg_type!=3)";
				sql+=" group by p04.item_id,pti.itemdesc";
				rs = dao.search(sql);
	
				HashMap notEqualOne = new HashMap();
				HashMap equalOne = new HashMap();
				while (rs.next())
				{
					String item_id = rs.getString("item_id");
					String itemdesc = rs.getString("itemdesc");
					float score = rs.getFloat(2);
					if(new Float(PubFunc.round(score+"",6)).floatValue()!=1) {
                        notEqualOne.put(item_id, itemdesc);
                    } else {
                        equalOne.put(item_id, itemdesc);
                    }
				}	
					
				sql="select item_id,itemdesc from per_template_item where template_id='"+this.templateID+"' and kind=2";
				rs = dao.search(sql);
				while (rs.next())
				{
					String item_id = rs.getString("item_id");
					String itemdesc = rs.getString("itemdesc");
					if(notEqualOne.get(item_id)==null && equalOne.get(item_id)==null) {
                        itemRankNoEqualOneBuf.append(itemdesc+"\n");
                    } else if(notEqualOne.get(item_id)!=null) {
                        itemRankNoEqualOneBuf.append(itemdesc+"\n");
                    }
				}					
			}
			
			if(this.planParameter.get("IsLimitPointValue")!=null && "true".equalsIgnoreCase((String)this.planParameter.get("IsLimitPointValue")))
			{
				if(itemTotalScoreTestBuf.length()>0)
				{
					if(buf.length()>0) {
                        buf.append("\n");
                    }
					buf.append("以下个性项目的总分和项目下的指标及任务总分不相等：\n");
					buf.append(itemTotalScoreTestBuf);
				}												
				if(itemRankNoEqualOneBuf.length()>0)
				{
					if(buf.length()>0) {
                        buf.append("\n");
                    }
					buf.append("以下个性项目下的指标及任务权重之和不为1：\n");
					buf.append(itemRankNoEqualOneBuf);
				}				
			}
			if(taskRuleTestBuf.length()>0)
			{
				if(buf.length()>0) {
                    buf.append("\n");
                }
				buf.append("以下个性项目任务规则校验不符：");
				buf.append(taskRuleTestBuf);
			}
			
			if (rs != null) {
                rs.close();
            }
			if (rs1 != null) {
                rs1.close();
            }
			if (rowSet != null) {
                rowSet.close();
            }
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if(buf.length()==0) {
            buf.append("目标卡检验通过！");
        }
		return buf.toString();
	}
	/**
	 * 判断p04表中是否已为对象产生模板内的共性指标，没有则自动产生
	 */
	public void insertObjTarget_commonPoint(String object_id)
	{
		String sql = "select * from per_object where plan_id=" + this.planid + " and object_id='"+object_id+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList pointList = this.getPointInfo();
		try
		{
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				String b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
				String e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
				String e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");
				String a0101 = rs.getString("a0101") == null ? "" : rs.getString("a0101");

				for (int i = 0; i < pointList.size(); i++)
				{
					LazyDynaBean abean = (LazyDynaBean) pointList.get(i);
					String point_id = (String) abean.get("point_id");
					String pointname = (String) abean.get("pointname");
					String item_id = (String) abean.get("item_id");
					String score = (String) abean.get("score");
					String rank = (String) abean.get("rank");
					Integer seq = (Integer) abean.get("seq");

					RecordVo vo = new RecordVo("P04");
					IDGenerator idg = new IDGenerator(2, conn);
					String id = idg.getId("P04.P0400");
					vo.setInt("p0400", Integer.parseInt(id));

					if (this.planVo.getInt("object_type") == 2)
					{
						vo.setString("b0110", b0110);
						vo.setString("e0122", e0122);
						vo.setString("e01a1", e01a1);
						vo.setString("nbase", "USR");
						vo.setString("a0100", object_id);
					} else
					{
						vo.setString("b0110", object_id);
						vo.setString("a0100", object_id);
					}
					vo.setString("a0101", a0101);
					vo.setString("p0401", point_id);
					vo.setString("p0407", pointname);
					vo.setInt("plan_id", Integer.parseInt(this.planid));
					vo.setString("p0413", score);
					vo.setString("p0415", rank);
					vo.setInt("item_id", Integer.parseInt(item_id));
					vo.setInt("fromflag", 2);
					vo.setInt("itemtype", 0);
					vo.setInt("state", 0);
					vo.setInt("p_p0400", -1);
					vo.setInt("seq", seq.intValue());
					dao.addValueObject(vo);
				}
			}
			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**引入上期目标卡
	 * @param copyObjList 被复制的考核对象*/
	public void importLastTargetCard(String planid_copy,ArrayList copyObjList)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		StringBuffer buf = new StringBuffer();
		try
		{
			
			for(int i=0;i<copyObjList.size();i++)
			{
				String object_id = (String)copyObjList.get(i);				
				//清除当前目标卡数据	
				buf.setLength(0);
				buf.append("delete from p04 where plan_id="+this.planid );
				if (this.planVo.getInt("object_type") == 2) {
                    buf.append(" and a0100='"+object_id+"'");
                } else {
                    buf.append(" and b0110='"+object_id+"'");
                }
				dao.delete(buf.toString(), new ArrayList());
				
				this.pastObjTarget(planid_copy, this.planid, object_id, object_id);				
			}
			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**删除目标卡中对应的原有的指标或者任务  同时删除原有的指标或者任务对应的上传附件 JinChunhai 2011.08.25 */
	public void delOldTarget(String pastPlanid,String pastObjId)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		
		String str="";					
		String sql = "delete from p04 where plan_id=" + pastPlanid;//" and itemtype=2 ";itemtype=2 硬性分配的任务
		if (this.planVo.getInt("object_type") == 2)
		{
			sql += " and a0100='" + pastObjId + "'";
			if(this.planParameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)this.planParameter.get("TaskSupportAttach"))) {
                str=("delete from per_article where plan_id="+pastPlanid+" and a0100='" + pastObjId + "' and article_type=3 ");
            }
		}
		else
		{
			sql += " and b0110='" + pastObjId + "'";
			if(this.planParameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)this.planParameter.get("TaskSupportAttach")))
			{
				// 获取团队负责人信息
				LazyDynaBean bean = getMainbodyBean(pastPlanid,pastObjId);	
				if(bean!=null)
				{
					String mainbody_id = (String)bean.get("mainbody_id");
					if(mainbody_id!=null && mainbody_id.trim().length()>0) {
                        str=("delete from per_article where plan_id="+pastPlanid+" and a0100='" + mainbody_id + "' and article_type=3 ");
                    }
				}
			}
		}
		sql+=" and item_id in (select item_id from per_template_item where kind=2 and template_id='"+this.templateID+"')";
		try 
		{
			dao.delete(sql, new ArrayList());
			if(this.planParameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)this.planParameter.get("TaskSupportAttach")))
			{
				if(str!=null && str.trim().length()>0) {
                    dao.delete(str.toString(),null);
                }
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**粘贴考核对象目标*/
	public void pastObjTarget(String copyPlanid,String pastPlanid,String pastObjId, String copyObjId)
	{
		LoadXml loadxml = new LoadXml(this.conn, copyPlanid);
		Hashtable parameter = loadxml.getDegreeWhole();
		
		String sql = "select * from per_object where plan_id=" + pastPlanid + " and object_id='" + pastObjId + "'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rwSet = null;
		InputStream inStrem=null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement pt = null;
		RowSet rs = null;
		try
		{
			String b0110 = "";
			String e0122 = "";  
			String e01a1 = "";
			String object_id = "";
			String a0101 = "";
			String sp_flag="";
			rs = dao.search(sql);
			if (rs.next())
			{
				b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
				e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
				e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");
				object_id = rs.getString("object_id");
				a0101 = rs.getString("a0101") == null ? "" : rs.getString("a0101");
				sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
			}

			HashMap existPointsMap = new HashMap();
			sql = "select * from p04 where plan_id=" + pastPlanid; //+ " and itemtype=2 ";//itemtype=2 硬性分配的任务
			if (this.planVo.getInt("object_type") == 2) {
                sql += " and a0100='" + pastObjId + "'";
            } else {
                sql += " and b0110='" + pastObjId + "'";
            }

			rs = dao.search(sql);
			while (rs.next())
			{
				int fromflag = rs.getInt("fromflag");
				if(fromflag==2 || fromflag==3)//来源KPI指标
                {
                    existPointsMap.put(rs.getString("p0401").toLowerCase(), rs.getString("p0400"));
                } else if(fromflag==1)//新建的任务
                {
                    existPointsMap.put(rs.getString("p0407"), rs.getString("p0400"));
                }
			}
				

			// 粘帖所有 包括共性指标和个性任务或指标
			int _objecttype=2;
			rs=dao.search("select object_type from per_plan where plan_id="+copyPlanid);
			if(rs.next()) {
                _objecttype=rs.getInt("object_type");
            }
			sql = "select p.*,pti.kind from p04 p left join per_template_item pti on p.item_id=pti.item_id where plan_id=" + copyPlanid ;//+ " and itemtype=2 ";
			if (_objecttype== 2) {
                sql += " and a0100='" + copyObjId + "'";
            } else {
                sql += " and b0110='" + copyObjId + "'";
            }

			rs = dao.search(sql);
			while (rs.next())
			{
				String p0400 = rs.getString("p0400");				
				String chg_type = rs.getString("chg_type");
				if(chg_type!=null && "3".equals(chg_type))//删除标记的记录不复制进来
                {
                    continue;
                }
				String  kind=rs.getString("kind")==null?"1":rs.getString("kind");
				RecordVo vo = new RecordVo("P04");
				String p0401 = rs.getString("p0401");
				String p0407 = rs.getString("p0407");
				int fromflag = rs.getInt("fromflag");

				String key="";
				if(fromflag==2 || fromflag==3)//来源KPI指标
                {
                    key=p0401.toLowerCase();
                } else if(fromflag==1)//新建的任务
                {
                    key=p0407;
                }
				
				String id = "";
				if (existPointsMap.get(key) == null)
				{
					IDGenerator idg = new IDGenerator(2, conn);
					id = idg.getId("P04.P0400");
					vo.setInt("p0400", Integer.parseInt(id));
					if(fromflag==2 || fromflag==3)//来源KPI指标
                    {
                        vo.setString("p0401", p0401);
                    } else if(fromflag==1)//新建的任务
                    {
                        vo.setString("p0401", id);
                    }
				} else
				{
					id = (String) existPointsMap.get(key);
					vo.setInt("p0400", Integer.parseInt((String) existPointsMap.get(key)));
					vo = dao.findByPrimaryKey(vo);
					vo.setString("p0401", p0401);
				}
					

				if (this.planVo.getInt("object_type") == 2)
				{
					vo.setString("b0110", b0110);
					vo.setString("e0122", e0122);
					vo.setString("e01a1", e01a1);
					vo.setString("nbase", "USR");
					vo.setString("a0100", object_id);
				} else
				{
					vo.setString("b0110", object_id);
					vo.setString("a0100", object_id);
				}
				vo.setString("a0101", a0101);
				
				vo.setString("p0407", rs.getString("p0407"));
				vo.setInt("plan_id", Integer.parseInt(pastPlanid));
				vo.setDouble("p0413", rs.getDouble("p0413"));
				vo.setDouble("p0415", rs.getDouble("p0415"));
				vo.setInt("item_id", rs.getInt("item_id"));
				vo.setInt("fromflag", rs.getInt("fromflag"));
				vo.setInt("itemtype", rs.getInt("itemtype"));
//				vo.setInt("state", rs.getInt("state"));
				vo.setInt("seq", rs.getInt("seq"));//this.getSeq(pastObjId));

				for (int i = 0; i < this.targetPointsList.size(); i++)
				{
					FieldItem fieldItem = (FieldItem) this.targetPointsList.get(i);
					String itemtype = fieldItem.getItemtype();
					String fieldid = fieldItem.getItemid().toLowerCase();
					int decwidth = fieldItem.getDecimalwidth();

					if ("N".equals(itemtype) && decwidth == 0) {
                        vo.setInt(fieldid, rs.getInt(fieldid));
                    } else if ("N".equals(itemtype) && decwidth > 0) {
                        vo.setDouble(fieldid, rs.getDouble(fieldid));
                    } else if ("D".equals(itemtype)) {
                        vo.setDate(fieldid, rs.getDate(fieldid));
                    } else {
                        vo.setString(fieldid, rs.getString(fieldid));
                    }
				}
				/**引入上级目标卡时，应把所以标记（如是否调整状态等）去掉，lizhenwei 2011-08-23*/
				if("07".equals(sp_flag))
				{
					if("2".equals(kind))
					{
			            vo.setInt("state",-1);
			            vo.setInt("chg_type", 2);
			            vo.setInt("processing_state", 0);
					}else if("1".equals(kind))
					{
						 vo.setInt("state",0);
				         vo.setInt("chg_type", 1);
				         vo.setInt("processing_state", 1);
					}
				}
				else
				{
					 vo.setInt("state",0);
			         vo.setInt("chg_type", 1);
			         vo.setInt("processing_state", 0);
				}
				if (existPointsMap.get(key) == null) {
                    dao.addValueObject(vo);
                } else {
                    dao.updateValueObject(vo);
                }
				
				
				//  复制指标任务附件  JinChunhai 2011.08.25		当考核计划勾选了"任务支持附件上传"参数：才复制任务附件
				if((this.planParameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)this.planParameter.get("TaskSupportAttach"))) && (parameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)parameter.get("TaskSupportAttach"))))
	        	{	
					LazyDynaBean bean = new LazyDynaBean();
					String strsql = "";
					if (this.planVo.getInt("object_type") == 2) {
                        strsql = ("select * from per_article where plan_id="+copyPlanid+" and a0100='"+copyObjId+"' and article_type=3 and task_id='"+p0400+"'");
                    } else
					{
						// 取得当前团队计划的负责人员
						bean = getMainbodyBean(pastPlanid,pastObjId);						
						strsql = ("select * from per_article where plan_id="+copyPlanid+" and article_type=3 and task_id='"+p0400+"'");					
					}													
					rwSet=dao.search(strsql);
					
					while(rwSet.next())
					{																						
						int article_id=0;
						RecordVo avo=new RecordVo("per_article");
						article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.conn);
						avo.setInt("article_id", article_id);
						avo.setInt("plan_id",Integer.parseInt(pastPlanid));							
						
						if(this.planVo.getInt("object_type") == 2)
						{							
							avo.setString("b0110",b0110);
							avo.setString("e0122",e0122);
							avo.setString("e01a1",e01a1);
							avo.setString("nbase","USR");																				
							avo.setString("a0100",object_id);
							avo.setString("a0101",a0101);
						}else
						{		
							avo.setString("b0110",(String)bean.get("b0110"));
							avo.setString("e0122",(String)bean.get("e0122"));
							avo.setString("e01a1",(String)bean.get("e01a1"));
							avo.setString("nbase","USR");																				
							avo.setString("a0100",(String)bean.get("mainbody_id"));
							avo.setString("a0101",(String)bean.get("a0101"));							
						}						
				//		avo.setString("article_name",rwSet.getString("article_name"));
						avo.setString("content",rwSet.getString("content"));
				//		avo.setString("affix",rwSet.getString("affix"));
				//		avo.setString("ext",rwSet.getString("ext"));
						avo.setInt("article_type", rwSet.getInt("article_type"));
						avo.setInt("fileflag",rwSet.getInt("fileflag"));
						avo.setInt("state",rwSet.getInt("state"));
						avo.setString("description",rwSet.getString("description"));										
						avo.setInt("task_id", Integer.parseInt(id));										
						dao.addValueObject(avo);
																						
	        	    	String sqlStr = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
	        	    	try {
							pt = this.conn.prepareStatement(sqlStr);
							pt.setString(1, rwSet.getString("ext"));
							// blob字段保存,数据库中差异
							switch (Sql_switcher.searchDbServer()) 
							{
								case Constant.ORACEL:
								    inStrem=rwSet.getBinaryStream("affix");
									Blob blob = getOracleBlob(inStrem, "per_article",article_id);
									pt.setBlob(2, blob);
									pt.setString(3, rwSet.getString("Article_name"));
									pt.setInt(4, article_id);
									break;
								default:
									byte[] data = rwSet.getBytes("affix");
									// a_vo.setObject("affix",data);
									pt.setBytes(2, data);
									pt.setString(3, rwSet.getString("Article_name"));
									pt.setInt(4, article_id);
									break;
							}
							// 打开Wallet
							dbS.open(this.conn, sqlStr);
							pt.execute();
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							PubFunc.closeDbObj(pt);
						}								
						
					}
	        	}				
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally{
		    PubFunc.closeResource(inStrem);
		    PubFunc.closeDbObj(rs);
		    PubFunc.closeDbObj(rwSet);
		    PubFunc.closeDbObj(pt);
		    try {
				// 关闭Wallet
				dbS.close(this.conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 取得当前团队计划的负责人员
	 * @param plan_id
	 * @param object_id
	 * @return
	 */
	public LazyDynaBean getMainbodyBean(String plan_id,String object_id)
	{
		LazyDynaBean abean=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1");
			if(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("id",rowSet.getString("id"));
				abean.set("body_id",rowSet.getString("body_id"));				
				abean.set("b0110",rowSet.getString("b0110"));
				abean.set("e0122",rowSet.getString("e0122"));
				abean.set("e01a1",rowSet.getString("e01a1"));				
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("mainbody_id",rowSet.getString("mainbody_id"));
				abean.set("status",rowSet.getString("status")!=null?rowSet.getString("status"):"");
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("know_id",rowSet.getString("know_id")!=null?rowSet.getString("know_id"):"");
				abean.set("whole_grade_id",rowSet.getString("whole_grade_id")!=null?rowSet.getString("whole_grade_id"):"");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	private Blob getOracleBlob(InputStream file,String tablename,int article_id) throws FileNotFoundException, IOException 
	{
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select affix from ");
		strSearch.append(tablename);
		strSearch.append(" where article_id=");
		strSearch.append(article_id);		
		strSearch.append(" FOR UPDATE");
			
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set affix=EMPTY_BLOB() where article_id=");
		strInsert.append(article_id);
		OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),file); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
	
	
	public void insertTargetTask(String taskContent,String itemid)
	{
		String sql = "select * from per_object where plan_id=" + this.planid + " and object_id='";
		if (this.planVo.getInt("object_type") == 2) {
            sql += this.objCode.substring(1) + "'";
        } else {
            sql += this.objCode.substring(2) + "'";
        }
		
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String b0110 = "";
			String e0122 = "";
			String e01a1 = "";
			String object_id = "";
			String a0101 = "";
			RowSet rs = dao.search(sql);
			if (rs.next())
			{
				b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
				e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
				e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");
				object_id = rs.getString("object_id");
				a0101 = rs.getString("a0101") == null ? "" : rs.getString("a0101");
			}
			
			RecordVo vo = new RecordVo("P04");
			IDGenerator idg = new IDGenerator(2, conn);
			String id = idg.getId("P04.P0400");
			vo.setInt("p0400", Integer.parseInt(id));
			
			if (this.planVo.getInt("object_type") == 2)
			{
				vo.setString("b0110", b0110);
				vo.setString("e0122", e0122);
				vo.setString("e01a1", e01a1);
				vo.setString("nbase", "USR");
				vo.setString("a0100", object_id);
			} else
			{
				vo.setString("b0110", object_id);
				vo.setString("a0100", object_id);
			}
			vo.setString("a0101", a0101);
			if("0".equals(this.template_vo.getString("status")))  //分值
			{
				vo.setDouble("p0413", 0);
				vo.setDouble("p0415",1);
			}
			else if("1".equals(this.template_vo.getString("status")))  //权重
			{
				vo.setDouble("p0413", this.template_vo.getDouble("topscore"));
				vo.setDouble("p0415",0);
			}
			vo.setString("p0401",  id);
			vo.setString("p0407", taskContent);
			vo.setInt("plan_id", Integer.parseInt(this.planid));	
			vo.setInt("item_id", Integer.parseInt(itemid));
			vo.setInt("fromflag", 1);
			vo.setInt("itemtype", 2);
			vo.setInt("state", 0);
			vo.setInt("seq", this.getSeq(object_id));
			
			dao.addValueObject(vo);

			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	   /**   
	 * @Title: insertTargetTaskFromP08   
	 * @Description: 把工作计划任务导入到目标卡  可返回新增的p0400，满足后续操作 
	 * @param @param taskContent
	 * @param @param itemid
	 * @param @param p0800
	 * @param @return 
	 * @return int 
	 * @author:wangrd   
	 * @throws   
	*/
	public int insertTargetTaskFromP08(String taskContent,String itemid,String p0800,double rank)
	{
        int p0400 = 0;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            String sql = "select * from per_object where plan_id=" + this.planid + " and object_id='";
            if (this.planVo.getInt("object_type") == 2) {
                sql += this.objCode.substring(1) + "'";
            } else {
                sql += this.objCode.substring(2) + "'";
            }
            String b0110 = "";
            String e0122 = "";
            String e01a1 = "";
            String object_id = "";
            String a0101 = "";
            RowSet rs = dao.search(sql);
            if (rs.next()) {
                b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
                e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
                e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");
                object_id = rs.getString("object_id");
                a0101 = rs.getString("a0101") == null ? "" : rs.getString("a0101");
            }

            int _p0400 =isExistsThisP08Task(object_id,taskContent,itemid,p0800);
            if (_p0400>0) {//判断是否存在此任务，存在则只更新
                p0400 = _p0400;
                RecordVo vo = new RecordVo("P04");
                vo.setInt("p0400", p0400);
                vo = dao.findByPrimaryKey(vo);
                if ("0".equals(this.template_vo.getString("status"))) // 分值
                {
                    vo.setDouble("p0413", this.template_vo.getDouble("topscore")*rank);                
                    vo.setDouble("p0415", 1);
                } else if ("1".equals(this.template_vo.getString("status"))) // 权重
                {
                    vo.setDouble("p0413", this.template_vo.getDouble("topscore"));
                    vo.setDouble("p0415", rank);
                }
                vo.setString("p0407", taskContent);
                dao.updateValueObject(vo);
            }
            else {
                RecordVo vo = new RecordVo("P04");
                IDGenerator idg = new IDGenerator(2, conn);
                String id = idg.getId("P04.P0400");
                vo.setInt("p0400", Integer.parseInt(id));
                p0400 = Integer.parseInt(id);
                if (this.planVo.getInt("object_type") == 2) {
                    vo.setString("b0110", b0110);
                    vo.setString("e0122", e0122);
                    vo.setString("e01a1", e01a1);
                    vo.setString("nbase", "USR");
                    vo.setString("a0100", object_id);
                } else {
                    vo.setString("b0110", object_id);
                    vo.setString("a0100", object_id);
                }
                vo.setString("a0101", a0101);
                if ("0".equals(this.template_vo.getString("status"))) // 分值
                {
                    // vo.setDouble("p0413", 0);
                    vo.setDouble("p0413", this.template_vo.getDouble("topscore")*rank);                
                    vo.setDouble("p0415", 1);
                } else if ("1".equals(this.template_vo.getString("status"))) // 权重
                {
                    vo.setDouble("p0413", this.template_vo.getDouble("topscore"));
                    vo.setDouble("p0415", rank);
                }
                vo.setString("p0401", p0800);
                vo.setString("p0407", taskContent);
                vo.setInt("plan_id", Integer.parseInt(this.planid));
                vo.setInt("item_id", Integer.parseInt(itemid));
                vo.setInt("fromflag", 5);
                vo.setInt("itemtype", 0);//原来是2,现在改为0. 因为要让考核对象自己可以调整分值和权重
                vo.setInt("state", 0);
                vo.setInt("seq", this.getSeq(object_id));
                dao.addValueObject(vo);
                p0400 = Integer.parseInt(id);
            }
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p0400;
    }
	
    /**   
     * @Title: isExistsThisP08Task   
     * @Description: 是否存在已导入的同名工作任务   
     * @param @param objectid
     * @param @param taskContent
     * @param @param itemid
     * @param @param p0800
     * @param @return 
     * @return int 返回大于0表示存在
     * @author:wangrd   
     * @throws   
    */
    public int isExistsThisP08Task(String objectid,String taskContent,String itemid,String p0800)
    {
        int p0400 = 0;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            String sql="select * from p04 where a0100='"+objectid 
                +"' and item_id ="+itemid+" and fromflag =5 and p0401= '"+p0800
                +"' and plan_id ="+this.planid;    
            RowSet rs = dao.search(sql);
       
            if (rs.next()) {//判断是否存在此任务
//                if (taskContent.equals(rs.getString("p0401"))){
                    p0400 = rs.getInt("p0400");
//                    break;
//                }
            }
      
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return p0400;
    }
	
	
	
	public int getSeq(String object_id)
	{
		int seq = 1;
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select " + Sql_switcher.isnull("max(seq)", "0") + "+1 from p04 where a0100='" + object_id + "' and plan_id=" + this.planid;
		if (planVo.getInt("object_type") == 1 || planVo.getInt("object_type") == 3 || planVo.getInt("object_type") == 4) {
            sql = "select " + Sql_switcher.isnull("max(seq)", "0") + "+1 from p04 where b0110='" + object_id + "' and plan_id=" + this.planid;
        }
		try
		{
			RowSet rowSet = dao.search(sql);
			if (rowSet.next())
			{
				seq = rowSet.getInt(1);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return seq;
	}

	/** 取共性项目对应指标分值权重 */
	public ArrayList getPointInfo()
	{
		ArrayList list = new ArrayList();

		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append("select ptp.point_id,pp.pointname,ptp.item_id,ptp.score,ptp.rank,ptp.seq from per_template_point ptp,per_point pp where ptp.item_id in ");
			buf.append("(select item_id from per_template_item where template_id='" + this.templateID + "' and kind=1) and ptp.point_id=pp.point_id order by ptp.seq");

			RowSet rowSet = dao.search(buf.toString());
			while (rowSet.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("point_id", rowSet.getString("point_id") != null ? rowSet.getString("point_id") : "");
				abean.set("pointname", rowSet.getString("pointname") != null ? rowSet.getString("pointname") : "");
				abean.set("item_id", rowSet.getString("item_id") != null ? rowSet.getString("item_id") : "");
				String score = rowSet.getString("score")==null?"0":rowSet.getString("score");
				abean.set("score", score);
				String rank = rowSet.getString("rank")==null?"0":rowSet.getString("rank");
				abean.set("rank", rank);				
				abean.set("seq", new Integer(rowSet.getInt("seq")));
				list.add(abean);
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**重新引入共性项目的分值或者权重*/
	public void reImportCommonItem(String item_id) throws SQLException
	{
		String scoreFromItem = (String) this.planParameter.get("ScoreFromItem");//按项目权重逐级计算总分 True，False 默认为False
		String sql="";
		if ("1".equals(this.status) && "true".equalsIgnoreCase(scoreFromItem))//权重模版 如果设置了该参数 则共性项目的权重为模板中定义的项目权重
		{
			sql = "select rank from per_template_item where template_id='" + this.templateID + "' and kind=1 and item_id="+item_id;
		}else
		{
			sql = "select ";
			if("1".equals(status)) {
                sql+="sum(rank)";
            } else {
                sql+="sum(score)";
            }
			sql+=" from per_template_point where item_id="+item_id;
		}
		String sql2="update per_dyna_item set dyna_value=("+sql+") where  plan_id=" + this.planid + " and body_id=" + this.objTypeId+" and item_id="+item_id;
		ContentDAO dao = new ContentDAO(this.conn);
		dao.update(sql2);
	}
	
	/** 新增动态项目 */
	public void insertDynaItem(String item_id)
	{
		String scoreFromItem = (String) this.planParameter.get("ScoreFromItem");//按项目权重逐级计算总分 True，False 默认为False
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "insert into per_dyna_item(body_id,plan_id,item_id,dyna_value)values(" + this.objTypeId + "," + this.planid + ",?,?)";
		try
		{
			// 共性项目的
			String sql0 = "";
			RowSet rs = null;
			if ("1".equals(this.status) && "true".equalsIgnoreCase(scoreFromItem))//权重模版 如果设置了该参数 则共性项目的权重为模板中定义的项目权重
			{
				sql0 = "select item_id,rank from per_template_item where template_id='" + this.templateID + "' and kind=1";
				rs = dao.search(sql0);
				while (rs.next())
				{
					String itemid = rs.getString("item_id");
					ArrayList list1 = new ArrayList();
					list1.add(new Integer(itemid));
					list1.add(new Float(rs.getFloat("rank")));

					if ("all".equalsIgnoreCase(item_id) || item_id.equals(itemid)) {
                        list.add(list1);
                    }
				}
			}else
			{
				sql0 = "select item_id,sum(rank) rank,sum(score) score from per_template_point where item_id in" + " (select item_id from per_template_item where template_id='" + this.templateID
				+ "' and kind=1) group by item_id";

				rs = dao.search(sql0);
				while (rs.next())
				{
					String itemid = rs.getString("item_id");
					ArrayList list1 = new ArrayList();
					list1.add(new Integer(itemid));

					if ("1".equals(this.status)) {
                        list1.add(new Float(rs.getFloat("rank")));
                    } else {
                        list1.add(new Float(rs.getFloat("score")));
                    }

					if ("all".equalsIgnoreCase(item_id) || item_id.equals(itemid)) {
                        list.add(list1);
                    }
				}
			}
			
			// 个性项目的
			sql0 = "select item_id,score,rank from per_template_item where template_id='" + templateID + "' and kind=2";
			rs = dao.search(sql0);
			while (rs.next())
			{
				String itemid = rs.getString("item_id");
				ArrayList list1 = new ArrayList();
				list1.add(new Integer(itemid));

				if ("1".equals(this.status)) {
                    list1.add(new Float(rs.getFloat("rank")));
                } else {
                    list1.add(new Float(rs.getFloat("score")));
                }

				if ("all".equalsIgnoreCase(item_id) || item_id.equals(itemid)) {
                    list.add(list1);
                }
			}
			if (rs != null) {
                rs.close();
            }
			dao.batchInsert(sql, list);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** 可以删除的项目 将来在界面上选中会变颜色 */
	public HashMap getCanDelItems()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			// 共性项目的
			String sql0 = "select per_template_point.item_id,per_template_item.itemdesc from per_template_point,per_template_item where per_template_point.item_id=per_template_item.item_id";
			sql0 += " and per_template_item.template_id='" + this.templateID + "' and per_template_item.kind=1";

			RowSet rs = dao.search(sql0);
			while (rs.next()) {
                map.put(rs.getString("item_id"), "");
            }

			// 个性项目的
			sql0 = "select item_id,itemdesc from per_template_item where template_id='" + templateID + "' and kind=2";
			rs = dao.search(sql0);
			while (rs.next()) {
                map.put(rs.getString("item_id"), "");
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

	/**
	 * 目标卡指定中选中单元格会变颜色的地方包括个性项目和个性项目对应的指标和任务 选中个性项目后可以新建任务和引入指标,选中任务指标后可以删除
	 */
	public HashMap getCanChangeColorCell()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			// 个性项目的
			String sql0 = "select item_id,itemdesc from per_template_item where template_id='" + templateID + "' and kind=2";
			RowSet rs = dao.search(sql0);
			while (rs.next()) {
                map.put(rs.getString("item_id"), "");
            }

			// 个性项目对应的指标或者任务
			StringBuffer buf = new StringBuffer();
			buf.append("select * from p04 where plan_id=" + this.planid);
			if (this.planVo.getInt("object_type") == 2) {
                buf.append(" and a0100='" + this.objCode.substring(1) + "'");
            } else {
                buf.append(" and b0110='" + this.objCode.substring(2) + "'");
            }
			buf.append(" and item_id in (select item_id from per_template_item where template_id='" + templateID + "' and kind=2)");
			rs = dao.search(buf.toString());
			while (rs.next()) {
                map.put(rs.getString("item_id") + ":" + rs.getString("p0401"), "");
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

	public HashMap getTargetPointScore()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			// 个性项目对应的指标或者任务
			StringBuffer buf = new StringBuffer();
			buf.append("select * from p04 where plan_id=" + this.planid);
			if (this.planVo.getInt("object_type") == 2) {
                buf.append(" and a0100='" + this.objCode.substring(1) + "'");
            } else {
                buf.append(" and b0110='" + this.objCode.substring(2) + "'");
            }

			RowSet rs = dao.search(buf.toString());
			while (rs.next())
			{
				String p0401 = rs.getString("p0401");
				for (int i = 0; i < this.targetPointsList.size(); i++)
				{
					FieldItem item = (FieldItem) this.targetPointsList.get(i);
					String col = item.getItemid().toLowerCase();
					String value="";
					if("A".equalsIgnoreCase(item.getItemtype()) || "M".equalsIgnoreCase(item.getItemtype()))
					{
						value = rs.getString(col) == null ? "": rs.getString(col);
						String codesetId = item.getCodesetid();
						if (!"0".equals(codesetId))
						{
							if("score_org".equalsIgnoreCase(col))
							{
								if(AdminCode.getCode("UM", value) != null) {
                                    value= AdminCode.getCode("UM", value).getCodename() ;
                                } else if(AdminCode.getCode("UN", value) != null) {
                                    value= AdminCode.getCode("UN", value).getCodename() ;
                                }
							}else {
                                value=AdminCode.getCode(codesetId, value) != null ? AdminCode.getCode(codesetId, value).getCodename() : "";
                            }
						}							
					}						
					else if("N".equalsIgnoreCase(item.getItemtype())) {
                        value=rs.getString(col) == null ? "0" : this.myformat1.format(rs.getDouble(col));
                    } else if("D".equalsIgnoreCase(item.getItemtype())) {
                        value=rs.getDate(col) == null ? "" : PubFunc.FormatDate(rs.getDate(col));
                    }
					map.put(p0401.toLowerCase() + ":" + col, value);
				}
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

	public ArrayList addItemList()
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			// 共性项目的
			String sql0 = "select distinct per_template_point.item_id,per_template_item.itemdesc from per_template_point,per_template_item where per_template_point.item_id=per_template_item.item_id";
			sql0 += " and per_template_item.template_id='" + this.templateID + "' and per_template_item.kind=1";

			RowSet rs = dao.search(sql0);
			while (rs.next())
			{
				CommonData data = new CommonData(rs.getString("item_id"), rs.getString("itemdesc"));
				if (this.dynaItemMap.get(rs.getString("item_id")) == null) {
                    list.add(data);
                }
			}
			// 个性项目的
			sql0 = "select item_id,itemdesc from per_template_item where template_id='" + this.templateID + "' and kind=2";
			rs = dao.search(sql0);
			while (rs.next())
			{
				CommonData data = new CommonData(rs.getString("item_id"), rs.getString("itemdesc"));
				if (this.dynaItemMap.get(rs.getString("item_id")) == null) {
                    list.add(data);
                }
			}
			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return list;
	}

	/** 取得模板下项目的子项目的个数 */
	public HashMap getChildItemCount(String templateID)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql = "select parent_id,count(*) from per_template_item where template_id='" + templateID + "' and parent_id is not null group by parent_id";
			RowSet rs = dao.search(sql);
			while (rs.next()) {
                map.put(rs.getString("parent_id"), new Integer(rs.getInt(2)));
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

	public ArrayList getChildItemList(String itemid)
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql = "select item_id from per_template_item where parent_id=" + itemid;
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				String item_id = rs.getString("item_id");
				if (this.dynaItemMap.get(item_id) != null) {
                    list.add(item_id);
                }
			}

			if (rs != null) {
                rs.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/** 取得模板下项目的子项目的个数 */
	public HashMap getItemValueRule()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql = "select * from per_dyna_item where plan_id=" + this.planid + " and body_id=" + this.objTypeId;
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				String item_id = rs.getString("Item_id");
				String rule = Sql_switcher.readMemo(rs, "Task_rule");

				String ruleContent = "";
				if (rule == null || (rule != null && "".equals(rule)))
				{

				} else
				{
					Document doc = PubFunc.generateDom(rule);
					String xpath = "//Task";
					XPath xpath_ = XPath.newInstance(xpath);
					Element ele = (Element) xpath_.selectSingleNode(doc);
					Element child;
					if (ele != null)
					{
						String maxTaskNumber = "";
						String minTaskNumber = "";
						String maxScoreValue = "";
						String minScoreValue = "";
						child = ele.getChild("TaskNumber");
						if (child != null)
						{
							maxTaskNumber = child.getAttributeValue("MaxCount")!=null?child.getAttributeValue("MaxCount"):"";
							minTaskNumber = child.getAttributeValue("MinCount")!=null?child.getAttributeValue("MinCount"):"";
						}
						child = ele.getChild("TaskScore");
						if (child != null)
						{
							maxScoreValue = child.getAttributeValue("MaxValue")!=null?child.getAttributeValue("MaxValue"):"";
							minScoreValue = child.getAttributeValue("MinValue")!=null?child.getAttributeValue("MinValue"):"";
						}
						ruleContent = ResourceFactory.getProperty("jx.implement.minTaskCount") + ":" + (minTaskNumber==null?"":minTaskNumber) + " " + ResourceFactory.getProperty("jx.implement.maxTaskCount") + ":"
								+ (maxTaskNumber==null?"":maxTaskNumber) + " ";// dml 2011年9月13日16:58:34 兼容null值
						if ("0".equals(this.status))
						{
							ruleContent += ResourceFactory.getProperty("jx.implement.minScore") + ":" + (minScoreValue==null?"":minScoreValue)+ " ";
							ruleContent += ResourceFactory.getProperty("jx.implement.maxScore") + ":" + (maxScoreValue==null?"":maxScoreValue);							
						}
						else if ("1".equals(this.status))
						{
							ruleContent += ResourceFactory.getProperty("jx.implement.minRank") + ":" + (minScoreValue==null?"":minScoreValue)+ " ";
							ruleContent += ResourceFactory.getProperty("jx.implement.maxRank") + ":" + (maxScoreValue==null?"":maxScoreValue);
						}
						child = ele.getChild("AddMinusScore");//dml 2011年9月13日16:57:21 
						String flag="";
						String scope="";
						//dml 2011年9月14日10:45:13
						LoadXml loadxml = new LoadXml(this.conn, planid);
						Hashtable params = loadxml.getDegreeWhole();
						String scoreflag=(String)params.get("scoreflag");
						String scoreFromItem=(String)params.get("ScoreFromItem");
						String canshow="";
						if(("1".equals(scoreflag)|| "2".equalsIgnoreCase(scoreflag))&&(scoreFromItem==null||scoreFromItem.trim().length()==0||!"True".equalsIgnoreCase(scoreFromItem))){
							canshow="true";
						}else{
							canshow="false";
						}
						if("true".equalsIgnoreCase(canshow))
						{
							if (child != null)
							{
								flag = child.getAttributeValue("flag")!=null?child.getAttributeValue("flag"):"";
								scope = child.getAttributeValue("scope")!=null?child.getAttributeValue("scope"):"";
							}
							if(flag!=null && flag.trim().length()>0 && "1".equalsIgnoreCase(flag))
							{
								if((child.getAttributeValue("f_scope")!=null) || (child.getAttributeValue("t_scope")!=null))//新加 加扣分定义分值范围 lizw 2011-11-05
								{
									scope=child.getAttributeValue("f_scope")!=null?child.getAttributeValue("f_scope"):"";
									String to_scope=child.getAttributeValue("t_scope")!=null?child.getAttributeValue("t_scope"):"";
									to_scope=(to_scope==null|| "".equals(to_scope)?"":to_scope);
									scope=(scope==null|| "".equalsIgnoreCase(scope)?"":scope);
									ruleContent =ruleContent +(" 任务得分范围："+scope+"~"+to_scope);
								}
								else
								{
									scope=(scope==null|| "".equalsIgnoreCase(scope)?"":scope);
									if("".equals(scope)) {
                                        ruleContent=ruleContent+(" 任务得分范围："+scope+"~"+scope);
                                    } else {
                                        ruleContent=ruleContent+(" 任务得分范围：-"+scope+"~"+scope);
                                    }
								}
							}else
							{
								if((minTaskNumber!=null && minTaskNumber.trim().length()>0) || (maxTaskNumber!=null && maxTaskNumber.trim().length()>0) || (maxScoreValue!=null && maxScoreValue.trim().length()>0) || (minScoreValue!=null && minScoreValue.trim().length()>0))
								{}
								else {
                                    ruleContent="";
                                }
							}
						}
					}
				}

				String dyna_value = rs.getString("Dyna_value");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("ruleContent", ruleContent);
				bean.set("dyna_value", dyna_value + "");
				map.put(item_id, bean);
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

	/** 取得模板下项目的子项目的个数 */
	public HashMap getItemValueRule(String planid,String body_id)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql = "select * from per_dyna_item where plan_id=" + planid + " and body_id=" + body_id;
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				String item_id = rs.getString("Item_id");
				String rule = Sql_switcher.readMemo(rs, "Task_rule");

				String maxTaskNumber = "";
				String minTaskNumber = "";
				String maxScoreValue = "";
				String minScoreValue = "";
				if (rule == null || (rule != null && "".equals(rule)))
				{

				} else
				{
					Document doc = PubFunc.generateDom(rule);
					String xpath = "//Task";
					XPath xpath_ = XPath.newInstance(xpath);
					Element ele = (Element) xpath_.selectSingleNode(doc);
					Element child;
					if (ele != null)
					{
						
						child = ele.getChild("TaskNumber");
						if (child != null)
						{
							maxTaskNumber = child.getAttributeValue("MaxCount")!=null?child.getAttributeValue("MaxCount"):"";
							minTaskNumber = child.getAttributeValue("MinCount")!=null?child.getAttributeValue("MinCount"):"";
						}
						child = ele.getChild("TaskScore");
						if (child != null)
						{
							maxScoreValue = child.getAttributeValue("MaxValue")!=null?child.getAttributeValue("MaxValue"):"";	
							minScoreValue = child.getAttributeValue("MinValue")!=null?child.getAttributeValue("MinValue"):"";	
						}
					}
				}

				String dyna_value = rs.getString("Dyna_value")!=null?rs.getString("Dyna_value"):"";	
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("minTaskNumber", minTaskNumber);
				bean.set("maxTaskNumber", maxTaskNumber);
				bean.set("maxScoreValue", maxScoreValue);
				bean.set("minScoreValue", minScoreValue);
				bean.set("dyna_value", dyna_value + "");
				map.put(item_id, bean);
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
	
	
	/** 得到项目所占的行数 */
	public int getItemRows(String itemid, HashMap itemKindMap)
	{
		int rowCount = 0;
		String kind = (String) itemKindMap.get(itemid);
		if ("2".equals(kind)) {
            return 1;
        } else
		{
			ArrayList pointList = (ArrayList) this.itemToPointMap.get(itemid);
			int pointNum = pointList == null ? 0 : pointList.size();
			if (pointNum > 0 && this.dynaItemMap.get(itemid)!=null)// 直接挂指标的 不管指标有几个 占一行 这个共性项目需要在动态项目表里有
            {
                rowCount = 1;
            }
			ArrayList childItemList = this.getChildItemList(itemid);
			for (int i = 0; i < childItemList.size(); i++)
			{
				String childItemid = (String) childItemList.get(i);
				rowCount += this.getItemRows(childItemid, itemKindMap);
			}
		}
		return rowCount;
	}

	/** 发布和暂停的计划才可以编辑 */
	public String getTemplateHtml()
	{
		StringBuffer htmlContext = new StringBuffer("");
		StringBuffer r_item = new StringBuffer("");
		HashMap existWriteItem = new HashMap();
		LazyDynaBean abean = null;
		LazyDynaBean a_bean = null;
		StringBuffer extendtHead = new StringBuffer();

		HashMap itemKindMap = this.getItemKind();
		HashMap iemValueRuleMap = this.getItemValueRule();
		// 输出表头
		extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  colspan='" + this.lay + "'>项目名称</td>\r\n");
		if ("1".equals(this.status)) {
            extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' >权重</td>\r\n");
        } else {
            extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\">分值</td>\r\n");
        }
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' >任务规则</td>\r\n");
		try
		{
			int rowNum = 0;
			int flag = 0;
			/** 所有的叶子项目 */
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
					
				/** 该项目的叶子节点(项目)个数 */
				int num = ((Integer) this.itemPointNum.get(item_id)).intValue();
				ArrayList pointList = (ArrayList) this.itemToPointMap.get(item_id);

				htmlContext.append("<tr>\r\n");
				rowNum++;
				/** 所有父亲列表 */
				ArrayList linkParentList = (ArrayList) this.leafItemLinkMap.get(item_id);
				int current = linkParentList.size();
				/** 叶子项目的继承关系列表 */
				for (int e = linkParentList.size() - 1; e >= 0; e--)
				{
					a_bean = (LazyDynaBean) linkParentList.get(e);
					String itemid = (String) a_bean.get("item_id");
					String kind = (String) itemKindMap.get(itemid);
					if (existWriteItem.get(itemid) != null) {
                        continue;
                    }
					if (this.dynaItemMap.get(item_id) == null) {
                        continue;
                    }
					existWriteItem.put(itemid, "1");
					String itemdesc = (String) a_bean.get("itemdesc");

					LazyDynaBean itemValueRule = (LazyDynaBean) iemValueRuleMap.get(itemid);
					String ruleContent = "";
					String dyna_value = "";
					if (itemValueRule != null)
					{
						ruleContent = (String) itemValueRule.get("ruleContent");
						dyna_value = (String) itemValueRule.get("dyna_value");
						dyna_value = this.myformat1.format(Double.parseDouble(dyna_value));
					}
					ruleContent = ruleContent == "" ? "无" : ruleContent;

					/** 该项目所占的行数 */
					// int x1 = itemPointNum.get(itemid)==null?0:((Integer)itemPointNum.get(itemid)).intValue();
					// int itemPointCount = this.itemToPointMap.get(itemid)!=null?((ArrayList)this.itemToPointMap.get(itemid)).size():0;
					// int childItemCount = childItemCountMap.get(itemid)==null?0:((Integer)childItemCountMap.get(itemid)).intValue();
					// int colspan=itemPointCount+childItemCount;
					// colspan=colspan==0?1:colspan;
					// int layer_self=Integer.parseInt((String)layMap.get(itemid));
					// if(this.lay==layer_self)//最后一层项目不跨行
					// colspan=1;
					// else if(this.lay>layer_self && colspan==0)
					// colspan=1;
					int rowspan = this.getItemRows(itemid, itemKindMap);

					/** 画出该项目 */
					htmlContext.append(writeTd2(itemdesc, rowspan, "left", this.td_width, itemid));
					if (e != 0)
					{
						/** 该项目的层数 */
						int layer = Integer.parseInt((String) layMap.get(itemid));
						/** 对应指标列表 */
						ArrayList fieldlistp = (ArrayList) this.itemHaveFieldList.get(itemid);
						if("1".equals(kind) && this.dynaItemMap.get(itemid)==null) {
                            fieldlistp=new ArrayList();
                        }
						/** 该项目有指标 */
						if (fieldlistp != null && fieldlistp.size() > 0)
						{
							// for(int h=0;h<fieldlistp.size();h++)
							// {
							// LazyDynaBean xbean = (LazyDynaBean)fieldlistp.get(h);
							// if(h!=0)
							// htmlContext.append("<tr>\r\n");
							// for(int f=0;f<this.lay-layer;f++)
							// {
							// htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
							// }//写指标开始
							// htmlContext.append("<td align=\"left\" class='RecordRow' ");
							// if(this.isVisible.equals("1"))
							// htmlContext.append(" onclick='changeColor(\""+(String)xbean.get("point_id")+"\",\""+2+"\")' id='"+(String)xbean.get("point_id")+"'");
							// htmlContext.append(">"+(String)xbean.get("name")+"</td>");
							// htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
							// if(this.isVisible.equals("1"))
							// {
							// htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"score\" id=\"s_"+(String)xbean.get("point_id")+"\"
							// value=\""+(String)xbean.get("score")+"\" maxlength='10'/>");
							// score.append(","+(String)xbean.get("point_id"));
							// }
							// else
							// htmlContext.append((String)xbean.get("score"));
							// htmlContext.append("</td>");
							// if(this.status.equals("1"))
							// {
							// htmlContext.append("<td align=\"right\" class='RecordRow'>");
							// if(this.isVisible.equals("1"))
							// {
							// htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\"
							// name=\"score\" id=\"r_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("rank")+"\" maxlength='10'/>");
							// }
							// else
							// htmlContext.append((String)xbean.get("rank"));
							// htmlContext.append("</td>");
							// }//写指标结束
							// htmlContext.append("</tr>\r\n");
							// }

							for (int f = 0; f < this.lay - layer; f++)
							{
								htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
							}
							htmlContext.append("<td align=\"right\" class='RecordRow'>");
							if ("2".equals(kind))
							{
								if (this.planStatus == 3 || this.planStatus == 5) {
                                    htmlContext.append("<input onBlur=\"checkValue(this," + this.status + "," + (String) a_bean.get("item_id")
                                            + ");\" onFocus=\"saveBeforeValue(this);\" onkeypress=\"event.returnValue=IsDigit(this);\" type=\"text\" class=\"Input_self\" name=\"r_score\" id=\"item_"
                                            + (String) a_bean.get("item_id") + "\" value=\"" + dyna_value + "\" maxlength='10'/>");
                                } else {
                                    htmlContext.append(dyna_value);
                                }
							} else {
                                htmlContext.append(dyna_value);
                            }
							htmlContext.append("</td>");
							htmlContext.append("<td align=\"center\" class='RecordRow'>");
							if ("2".equals(kind))
							{
								if (this.planStatus == 3 || this.planStatus == 5) {
                                    htmlContext.append("<a id=\"href_" + (String) a_bean.get("item_id") + "\" href=\"javascript:editRule(" + (String) a_bean.get("item_id") + ");\" >" + " "
                                            + ruleContent + "</a>");
                                } else {
                                    htmlContext.append(ruleContent);
                                }
							}
							htmlContext.append("</td>");
							htmlContext.append("</tr>\r\n");
						}
						/** 没有指标 */

						else
						{

							if (ifHasChildMap.get(itemid) == null)
							{
								for (int f = 0; f < this.lay - layer + 1; f++)
								{
									htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
								htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
								if ("1".equals(this.isVisible))
								{
									htmlContext.append("<input onkeypress=\"event.returnValue=IsDigit(this);\"  type=\"text\" class=\"Input_self\" name=\"i_score\" id=\"si_" + (String) a_bean.get("item_id")
											+ "\" value=\"" + (String) a_bean.get("score") + "\" maxlength='10'/>");
									r_item.append("," + (String) a_bean.get("item_id"));
								} else {
                                    htmlContext.append((String) a_bean.get("score"));
                                }
								htmlContext.append("</td>");
								if ("1".equals(this.status))
								{
									htmlContext.append("<td align=\"right\" class='RecordRow'>");
									if ("1".equals(this.isVisible))
									{
										htmlContext
												.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeypress=\"event.returnValue=IsDigit(this);\" type=\"text\" class=\"Input_self\" name=\"r_score\" id=\"ri_"
														+ (String) a_bean.get("item_id") + "\" value=\"" + (String) a_bean.get("rank") + "\" maxlength='10'/>");
									} else {
                                        htmlContext.append((String) a_bean.get("rank"));
                                    }
									htmlContext.append("</td>");
								}
								htmlContext.append("</tr>\r\n");
							}
						}
					}

					if (e == 0)
					{
						int layer = Integer.parseInt((String) layMap.get(itemid));
						ArrayList fieldlist = (ArrayList) this.itemHaveFieldList.get(item_id);
						if (fieldlist != null && fieldlist.size() != 0)
						{
							// for(int x=0;x<fieldlist.size();x++)
							// {
							// if(x!=0)
							// htmlContext.append("<tr>\r\n");
							// LazyDynaBean xbean = (LazyDynaBean)fieldlist.get(x);
							// for(int f=0;f<this.lay-layer;f++)
							// {
							// htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
							// }//写指标开始
							// htmlContext.append("<td align=\"left\" class='RecordRow' ");
							// if(this.isVisible.equals("1"))
							// htmlContext.append(" onclick='changeColor(\""+(String)xbean.get("point_id")+"\",\""+2+"\")' id='"+(String)xbean.get("point_id")+"'");
							// htmlContext.append(">"+(String)xbean.get("name")+"</td>");
							// htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
							// if(this.isVisible.equals("1"))
							// {
							// htmlContext.append("<input onkeydown=\"checkKeyCode();\" onFocus=\"clearValue('"+(String)xbean.get("score")+"','s_"+(String)xbean.get("point_id")+"')\" type=\"text\"
							// class=\"Input_self\" name=\"score\" id=\"s_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("score")+"\" maxlength='10'/>");
							// score.append(","+(String)xbean.get("point_id"));
							// }
							// else
							// htmlContext.append((String)xbean.get("score"));
							// htmlContext.append("</td>");
							// if(this.status.equals("1"))
							// {
							// htmlContext.append("<td align=\"right\" class='RecordRow'>");
							// if(this.isVisible.equals("1"))
							// {
							// htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\"
							// name=\"score\" id=\"r_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("rank")+"\" maxlength='10'/>");
							// }
							// else
							// htmlContext.append((String)xbean.get("rank"));
							// htmlContext.append("</td>");
							// }//写指标结束
							// htmlContext.append("</tr>\r\n");
							// }

							for (int f = 0; f < this.lay - layer; f++)
							{
								htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
							}
							htmlContext.append("<td align=\"right\" class='RecordRow'>");
							if ("2".equals(kind))
							{
								if (this.planStatus == 3 || this.planStatus == 5) {
                                    htmlContext.append("<input onBlur=\"checkValue(this," + this.status + "," + (String) a_bean.get("item_id")
                                            + ");\" onFocus=\"saveBeforeValue(this);\" onkeypress=\"event.returnValue=IsDigit(this);\" type=\"text\" class=\"Input_self\" name=\"r_score\" id=\"item_"
                                            + (String) a_bean.get("item_id") + "\" value=\"" + dyna_value + "\" maxlength='10'/>");
                                } else {
                                    htmlContext.append(dyna_value);
                                }
							} else {
                                htmlContext.append(dyna_value);
                            }
							htmlContext.append("</td>");
							htmlContext.append("<td align=\"center\" class='RecordRow'>");
							if ("2".equals(kind))
							{
								if (this.planStatus == 3 || this.planStatus == 5) {
                                    htmlContext.append("<a id=\"href_" + (String) a_bean.get("item_id") + "\" href=\"javascript:editRule(" + (String) a_bean.get("item_id") + ");\" >" + " "
                                            + ruleContent + "</a>");
                                } else {
                                    htmlContext.append(ruleContent);
                                }
							}

							htmlContext.append("</td>");
							htmlContext.append("</tr>\r\n");
						} else
						{ // 写指标开始
							// for(int f=0;f<this.lay-layer+1;f++)
							// {
							// htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
							// }
							// /**兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分*/
							// htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
							// if(this.isVisible.equals("1"))
							// {
							// htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"i_score\"
							// onFocus=\"clearValue('"+(String)a_bean.get("score")+"','si_"+(String)a_bean.get("item_id")+"')\" id=\"si_"+(String)a_bean.get("item_id")+"\"
							// value=\""+(String)a_bean.get("score")+"\" maxlength='10'/>");
							// r_item.append(","+(String)a_bean.get("item_id"));
							// }
							// else
							// htmlContext.append((String)a_bean.get("score"));
							// htmlContext.append("</td>");
							// if(this.status.equals("1"))
							// {
							// htmlContext.append("<td align=\"right\" class='RecordRow'>");
							// if(this.isVisible.equals("1"))
							// {
							// htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\"
							// name=\"r_score\"
							// id=\"ri_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("rank")+"\" maxlength='10'/>");
							// }
							// else
							// htmlContext.append((String)a_bean.get("rank"));
							// htmlContext.append("</td>");
							// }//写指标结束
							// htmlContext.append("</tr>\r\n");
							for (int f = 0; f < this.lay - layer; f++)
							{
								htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
							}
							htmlContext.append("<td align=\"right\" class='RecordRow'>");
							if ("2".equals(kind))
							{
								if (this.planStatus == 3 || this.planStatus == 5) {
                                    htmlContext.append("<input onBlur=\"checkValue(this," + this.status + "," + (String) a_bean.get("item_id")
                                            + ");\" onFocus=\"saveBeforeValue(this);\" onkeypress=\"event.returnValue=IsDigit(this);\"  type=\"text\" class=\"Input_self\" name=\"r_score\" id=\"item_"
                                            + (String) a_bean.get("item_id") + "\" value=\"" + dyna_value + "\" maxlength='10'/>");
                                } else {
                                    htmlContext.append(dyna_value);
                                }
							} else {
                                htmlContext.append(dyna_value);
                            }

							htmlContext.append("</td>");
							htmlContext.append("<td align=\"center\" class='RecordRow'>");
							if ("2".equals(kind))
							{
								if (this.planStatus == 3 || this.planStatus == 5) {
                                    htmlContext.append("<a id=\"href_" + (String) a_bean.get("item_id") + "\" href=\"javascript:editRule(" + (String) a_bean.get("item_id") + ");\" >" + " "
                                            + ruleContent + "</a>");
                                } else {
                                    htmlContext.append(ruleContent);
                                }
							}

							htmlContext.append("</td>");
							htmlContext.append("</tr>\r\n");
						}
					}
				}
			}
			// htmlContext.append("</table>");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		htmlContext.append("<tr><td style=\"height:35px\" align=\"center\" colspan=\"" + (this.lay + 2) + "\">");
		if (this.planStatus == 3 || this.planStatus == 5)
		{
			htmlContext.append("<input class=\"mybutton\" type=\"button\" name=\"aa0\" value=\"" + ResourceFactory.getProperty("button.insert") + "\" onclick=\"addItem('0');\"/>");
			htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"aa1\" value=\"" + ResourceFactory.getProperty("button.delete") + "\" onclick=\"delItem();\"/>");
		}
		htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"aa2\" value=\"" + ResourceFactory.getProperty("button.close") + "\" onclick=\"parent.parent.window.close();\"/>");
		htmlContext.append("</td></tr>");
		
		StringBuffer titleHtml = new StringBuffer("<table   class='ListTable_self' width='100%' >");
		StringBuffer html = new StringBuffer(titleHtml.toString());
		extendtHead.append("</tr>\r\n");
		html.append(extendtHead.toString());
		html.append(htmlContext.toString());
		html.append("</table>");
		return html.toString();

	}

	public String getTargetCardHtml()
	{

		StringBuffer htmlContext = new StringBuffer("");
		StringBuffer r_item = new StringBuffer("");
		StringBuffer score = new StringBuffer("");
		HashMap existWriteItem = new HashMap();
		LazyDynaBean abean = null;
		LazyDynaBean a_bean = null;
		StringBuffer extendtHead = new StringBuffer();
		String publicPointCannotEdit = (String)this.planParameter.get("PublicPointCannotEdit");//不允许修改目标卡中的共性指标		
		
		// 需要取得计划的TargetDefineItem参数,P0413 -> 标准分值,P0415 -> 权重 add by 刘蒙
		String targetDefineItem = (String) planParameter.get("TargetDefineItem");
//		boolean isP0413 = true;
//		boolean isP0415 = true;
		boolean isP0413 = targetDefineItem.indexOf("P0413") == -1 ? false : true;
		boolean isP0415 = targetDefineItem.indexOf("P0415") == -1 ? false : true;
		
		// 输出表头
		int colCount = this.lay + 1;
		extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
		extendtHead.append("<td class='TableRow_2rows' style='border-left:0;' valign='middle' align='center'  colspan='" + this.lay + "' nowrap >项目名称</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"200\" nowrap >指标/任务</td>\r\n");
//		if (isP0413) {
//			
//		} else {
//			colCount--; // 不显示“标准分值”列，则colCount减一
//		}
		if ("1".equals(this.status) && isP0415)
		{
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" nowrap>权重</td>\r\n");
			colCount++;
		}

		for (int i = 0; i < this.targetPointsList.size(); i++)
		{
			FieldItem item = (FieldItem) this.targetPointsList.get(i);
            if("p0413".equalsIgnoreCase(item.getItemid()) && isP0413) {
                extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" nowrap >分值</td>\r\n");
            } else if("p0405".equalsIgnoreCase(item.getItemid())
                    //代码型 指标统一加宽宽度 haosl 2019-6-20
                    || (!"0".equals(item.getCodesetid()) && "A".equalsIgnoreCase(item.getItemtype()))) {
                extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"180\" nowrap>" + item.getItemdesc() + "</td>\r\n");
            } else {
                extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" nowrap>" + item.getItemdesc() + "</td>\r\n");
            }
			colCount++;
		}
		HashMap targetPointScore = this.getTargetPointScore();
		try
		{
			boolean canChangeColor = false;
			int rowNum = 0;
			/** 所有的叶子项目 */
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
				String item_kind = (String) abean.get("kind");
				/** 该项目的叶子节点(项目)个数 */
				int num = ((Integer) this.itemPointNum.get(item_id)).intValue();
				ArrayList pointList = (ArrayList) this.itemToPointMap.get(item_id);

				htmlContext.append("<tr>\r\n");
				rowNum++;
				/** 所有父亲列表 */
				ArrayList linkParentList = (ArrayList) this.leafItemLinkMap.get(item_id);
				int current = linkParentList.size();
				/** 叶子项目的继承关系列表 */
				for (int e = linkParentList.size() - 1; e >= 0; e--)
				{
					a_bean = (LazyDynaBean) linkParentList.get(e);
					String itemid = (String) a_bean.get("item_id");
					String kind = (String) a_bean.get("kind");
					if (existWriteItem.get(itemid) != null) {
                        continue;
                    }
					existWriteItem.put(itemid, "1");
					String itemdesc = (String) a_bean.get("itemdesc");
					/** 该项目所占的行数 */
					int colspan = ((itemPointNum.get(itemid) == null ? 0 : ((Integer) itemPointNum.get(itemid)).intValue()) + (childItemLinkMap.get(itemid) == null ? 0 : ((Integer) childItemLinkMap
							.get(itemid)).intValue()));
					/** 画出该项目 */
					if (this.canChangeColorCells.get(itemid) == null) {
                        canChangeColor = false;
                    } else {
                        canChangeColor = true;
                    }
					htmlContext.append(writeTd(itemdesc, colspan, "left", this.td_width, itemid, 1, canChangeColor));
					if (e != 0)
					{
						/** 该项目的层数 */
						int layer = Integer.parseInt((String) layMap.get(itemid));
						/** 对应指标列表 */
						ArrayList fieldlistp = (ArrayList) this.itemHaveFieldList.get(itemid);
						/** 该项目有指标 */
						if (fieldlistp != null && fieldlistp.size() > 0)
						{
							for (int h = 0; h < fieldlistp.size(); h++)
							{
								LazyDynaBean xbean = (LazyDynaBean) fieldlistp.get(h);
								String pointid = (String) xbean.get("point_id");
								if (this.canChangeColorCells.get(itemid + ":" + pointid) == null) {
                                    canChangeColor = false;
                                } else {
                                    canChangeColor = true;
                                }
								if (h != 0) {
                                    htmlContext.append("<tr>\r\n");
                                }
								for (int f = 0; f < this.lay - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" width=\"200\" class='RecordRow' ");
								if (canChangeColor) {
                                    htmlContext.append(" onclick='changeColor(\"" + pointid + "\",\"" + 2 + "\",\""+itemid+"\")' id='" + pointid + "'");
                                }

								if ("2".equals(kind) && (planStatus==8 || planStatus==3  || planStatus==5))// 个性项目对应任务可以编辑
								{
									String fromflag = (String) xbean.get("fromflag");
									if ("1".equals(fromflag))
									{
										htmlContext.append(">");
										htmlContext.append("<input type=\"text\" class=\"TEXT_NB\" onFocus=\"beforeEditTask(this);\" onBlur=\"editTask(this);\" id=\"t_" + pointid + "\" value=\""
												+ (String) xbean.get("name") + "\" size='58' maxlength='30'/>");
										htmlContext.append("</td>");
									} else {
                                        htmlContext.append(">" + (String) xbean.get("name") + "</td>");
                                    }
								} else {
                                    htmlContext.append(">" + (String) xbean.get("name") + "</td>");
                                }
								if (isP0413) {
									htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
									if ("1".equals(this.isVisible)  && (planStatus==8 || planStatus==3  || planStatus==5))
									{
										if (("2".equals(kind) || ("1".equals(kind) && "false".equalsIgnoreCase(publicPointCannotEdit))) && "0".equals(this.status))// 个性指标或者任务，共性指标在参数（不允许修改目标卡中的共性指标）为False时候才能编辑分值 分值模板是前提
                                        {
                                            htmlContext.append("<input onBlur=\"checkValue(this,'score','" + pointid+ "');\" onkeypress=\"event.returnValue=IsDigit(this);\"  type=\"text\" class=\"Input_self\" name=\"score\" id=\"s_" + pointid + "\" value=\""
                                                    + (String) xbean.get("score") + "\" maxlength='10'/>");
                                        } else {
                                            htmlContext.append((String) xbean.get("score"));
                                        }
										score.append("," + pointid);
									} else {
                                        htmlContext.append((String) xbean.get("score"));
                                    }
									htmlContext.append("</td>");
								}
								if ("1".equals(this.status) && isP0415)
								{
									htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
									if ("1".equals(this.isVisible) && (planStatus==8 || planStatus==3  || planStatus==5))
									{
										if ("2".equals(kind) || ("1".equals(kind) && "false".equalsIgnoreCase(publicPointCannotEdit)))// 个性指标或者任务，共性指标在参数（不允许修改目标卡中的共性指标）为False时候才能编辑权重
                                        {
                                            htmlContext.append("<input onBlur=\"checkValue(this,'rank','" + pointid
                                                + "');\" onFocus=\"saveBeforeValue(this);\" onkeypress=\"event.returnValue=IsDigit(this);\"  type=\"text\" class=\"Input_self\" name=\"score\" id=\"r_" + pointid
                                                + "\" value=\"" + (String) xbean.get("rank") + "\" maxlength='10'/>");
                                        } else {
                                            htmlContext.append((String) xbean.get("rank"));
                                        }
									} else {
                                        htmlContext.append((String) xbean.get("rank"));
                                    }
									htmlContext.append("</td>");
								}
								for (int m = 0; m < this.targetPointsList.size(); m++)
								{
									FieldItem item = (FieldItem) this.targetPointsList.get(m);
									String field = pointid.toLowerCase() + ":" + item.getItemid().toLowerCase();
									String score1 = (String) targetPointScore.get(field);
									String itemtype = item.getCodesetid();
									htmlContext.append("<td align=\"center\" width=\"100\" nowrap class='RecordRow'>");
									if( planStatus==8 || planStatus==3  || planStatus==5) {
                                        htmlContext.append(this.getTargetPointCellHtml(field, score1, item));
                                    } else
									{
										if(!"M".equalsIgnoreCase(itemtype)) {
                                            htmlContext.append(score1);
                                        } else {
                                            htmlContext.append(score1.length()>20?score1.substring(0, 20)+"...":score1);
                                        }
									}
										
									htmlContext.append("</td>");
								}
								htmlContext.append("</tr>\r\n");
							}
						}
						/** 没有指标 */

						else
						{

							if (ifHasChildMap.get(itemid) == null)
							{
								for (int f = 0; f < this.lay - layer + 1; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
								htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
								if ("1".equals(this.isVisible) && "2".equals(kind))
								{
									// 分值模板 项目分也不要编辑了
									// if(this.status.equals("0"))//分值模板才能编辑分值
									// htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"i_score\" id=\"si_" + (String) a_bean.get("item_id")
									// + "\" value=\"" + (String) a_bean.get("score") + "\" maxlength='10'/>");
									// else
									htmlContext.append((String) a_bean.get("score"));
									r_item.append("," + (String) a_bean.get("item_id"));
								} else {
                                    htmlContext.append((String) a_bean.get("score"));
                                }
								htmlContext.append("</td>");
								if ("1".equals(this.status))
								{
									htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");// 项目权重不显示了
									// if (this.isVisible.equals("1") && kind.equals("2"))
									// {
									// htmlContext.append("<input onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"r_score\" id=\"ri_"
									// + (String) a_bean.get("item_id") + "\" value=\"" + (String) a_bean.get("rank") + "\" maxlength='10'/>");
									// } else
								    htmlContext.append((String) a_bean.get("rank"));
									htmlContext.append("</td>");
								}
								for (int m = 0; m < this.targetPointsList.size(); m++)
								{
									htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");

									htmlContext.append("</td>");
								}
								htmlContext.append("</tr>\r\n");
							}
						}
					}

					if (e == 0)
					{
						int layer = Integer.parseInt((String) layMap.get(itemid));
						ArrayList fieldlist = (ArrayList) this.itemHaveFieldList.get(itemid);

						if (fieldlist != null && fieldlist.size() != 0)
						{
							for (int x = 0; x < fieldlist.size(); x++)
							{
								if (x != 0) {
                                    htmlContext.append("<tr>\r\n");
                                }
								LazyDynaBean xbean = (LazyDynaBean) fieldlist.get(x);
								String pointid = (String) xbean.get("point_id");
								if (this.canChangeColorCells.get(itemid + ":" + pointid) == null) {
                                    canChangeColor = false;
                                } else {
                                    canChangeColor = true;
                                }

								for (int f = 0; f < this.lay - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" class='RecordRow' width=\"200\" ");
								if (canChangeColor) {
                                    htmlContext.append(" onclick='changeColor(\"" + (String) xbean.get("point_id") + "\",\"" + 2 + "\",\""+itemid+"\")' id='" + (String) xbean.get("point_id") + "'");
                                }

								if ("2".equals(kind) && (planStatus==8 || planStatus==3  || planStatus==5))// 个性项目对应任务可以编辑
								{
									String fromflag = (String) xbean.get("fromflag");
									if ("1".equals(fromflag))
									{
										htmlContext.append(">");
										htmlContext.append("<input type=\"text\" class=\"TEXT_NB\" onFocus=\"beforeEditTask(this);\" onBlur=\"editTask(this);\" id=\"t_"
												+ (String) xbean.get("point_id") + "\" value=\"" + (String) xbean.get("name") + "\" size='58' maxlength='30'/>");
										htmlContext.append("</td>");
									} else {
                                        htmlContext.append(">" + (String) xbean.get("name") + "</td>");
                                    }
								} else {
                                    htmlContext.append(">" + (String) xbean.get("name") + "</td>");
                                }

								if ("1".equals(this.status) && isP0415)
								{
									htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
									if ("1".equals(this.isVisible) && ( planStatus==8 || planStatus==3  || planStatus==5))
									{
										if ("2".equals(kind) || ("1".equals(kind) && "false".equalsIgnoreCase(publicPointCannotEdit)))// 个性指标或者任务，共性指标在参数（不允许修改目标卡中的共性指标）为False时候才能编辑权重
                                        {
                                            htmlContext.append("<input onBlur=\"checkValue(this,'rank','" + pointid
                                                + "');\" onFocus=\"saveBeforeValue(this);\" onkeypress=\"event.returnValue=IsDigit(this);\"  type=\"text\" class=\"Input_self\" name=\"score\" id=\"r_"
                                                + (String) xbean.get("point_id") + "\" value=\"" + (String) xbean.get("rank") + "\" maxlength='10'/>");
                                        } else {
                                            htmlContext.append((String) xbean.get("rank"));
                                        }
									} else {
                                        htmlContext.append((String) xbean.get("rank"));
                                    }
									htmlContext.append("</td>");
								}
								for (int m = 0; m < this.targetPointsList.size(); m++)
								{
									FieldItem item = (FieldItem) this.targetPointsList.get(m);	
									String itemtype = item.getItemtype();
									if ("P0413".equalsIgnoreCase(item.getItemid())) {
										if(isP0413) {
											htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
											if ("1".equals(this.isVisible) && (planStatus==8 || planStatus==3  || planStatus==5))
											{
												if (("2".equals(kind) || ("1".equals(kind) && "false".equalsIgnoreCase(publicPointCannotEdit))) && "0".equals(this.status))// 个性指标或者任务，共性指标在参数（不允许修改目标卡中的共性指标）为False时候才能编辑分值
                                                {
                                                    htmlContext.append("<input onkeypress=\"event.returnValue=IsDigit(this);\"  onBlur=\"checkValue(this,'score','" + pointid + "');\" onFocus=\"clearValue('"
                                                            + (String) xbean.get("score") + "','s_" + (String) xbean.get("point_id") + "')\" type=\"text\" class=\"Input_self\" name=\"score\" id=\"s_"
                                                            + (String) xbean.get("point_id") + "\" value=\"" + (String) xbean.get("score") + "\" maxlength='10'/>");
                                                } else {
                                                    htmlContext.append((String) xbean.get("score"));
                                                }
												score.append("," + (String) xbean.get("point_id"));
											} else {
                                                htmlContext.append((String) xbean.get("score"));
                                            }
											htmlContext.append("</td>");
										}
									}else {
										htmlContext.append("<td align=\"center\" width=\"100\" nowrap class='RecordRow'>");									
										String field = pointid.toLowerCase() + ":" + item.getItemid().toLowerCase();
										String score1 = (String) targetPointScore.get(field);
										if( planStatus==8 || planStatus==3  || planStatus==5) {
                                            htmlContext.append(this.getTargetPointCellHtml(field, score1, item));
                                        } else
										{
											if(!"M".equalsIgnoreCase(itemtype)) {
                                                htmlContext.append(score1);
                                            } else {
                                                htmlContext.append(score1.length()>20?score1.substring(0, 20)+"...":score1);
                                            }
										}
										htmlContext.append("</td>");
									}
								}
								htmlContext.append("</tr>\r\n");
							}
						} else
						{
							for (int f = 0; f < this.lay - layer + 1; f++)
							{
								htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
							}
							
							if ("1".equals(this.status) && isP0415)
							{
								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");// 项目权重不显示了
								// if (this.isVisible.equals("1") && kind.equals("2"))
								// {
								// htmlContext.append("<input onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"r_score\" id=\"ri_"
								// + (String) a_bean.get("item_id") + "\" value=\"" + (String) a_bean.get("rank") + "\" maxlength='10'/>");
								// } else
							    htmlContext.append((String) a_bean.get("rank"));
								htmlContext.append("</td>");
							}
							for (int m = 0; m < this.targetPointsList.size(); m++)
							{
								FieldItem item = (FieldItem) this.targetPointsList.get(m);
								if("P0413".equalsIgnoreCase(item.getItemid())) {
									if (isP0413) {
										/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
										htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
										if ("1".equals(this.isVisible) && "2".equals(kind))
										{
											// 分值模板 项目分也不要编辑了
											// if(this.status.equals("0"))//分值模板才能编辑分值
											// htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"i_score\" onFocus=\"clearValue('" + (String) a_bean.get("score")
											// + "','si_" + (String) a_bean.get("item_id") + "')\" id=\"si_" + (String) a_bean.get("item_id") + "\" value=\"" + (String) a_bean.get("score")
											// + "\" maxlength='10'/>");
											// else
											htmlContext.append((String) a_bean.get("score"));
											r_item.append("," + (String) a_bean.get("item_id"));
										} else {
                                            htmlContext.append((String) a_bean.get("score"));
                                        }
										htmlContext.append("</td>");
									}
								}else {
									htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
	
									htmlContext.append("</td>");
								}
							}
							htmlContext.append("</tr>\r\n");
						}
					}
				}
			}
			htmlContext.append("</table>");
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		String object_id = "";
		if ("p".equalsIgnoreCase(this.objCode.substring(0, 1))) {
            object_id = objCode.substring(1);
        } else if ("um".equalsIgnoreCase(this.objCode.substring(0, 2)) || "un".equalsIgnoreCase(this.objCode.substring(0, 2))) {
            object_id = objCode.substring(2);
        }

		htmlContext.append("<tr><td style=\"height:35px\" align=\"center\" colspan=\"" + colCount + "\">");
		if( planStatus==8 || planStatus==3  || planStatus==5){
		htmlContext.append("<input class=\"mybutton\" type=\"button\" name=\"bt1\" id='bt1' value=\"" + ResourceFactory.getProperty("label.performance.importPerPoint") + "\" onclick=\"importPerPoint('"
				+ this.planid + "','" + this.planVo.getString("object_type") + "','" + object_id + "');\"/>");
		htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt2\" id='bt2' value=\"" + ResourceFactory.getProperty("per.achivement.newtask") + "\" onclick=\"newtask();\"/>");
		htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt3\" id='bt3' value=\"" + ResourceFactory.getProperty("button.delete") + "\" onclick=\"delPoint();\"/>");
		htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt4\" id='bt4' value=\"" + ResourceFactory.getProperty("reportspacecheck.check") + "\" onclick=\"check();\"/>");
		}
		htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt5\" id='bt5' value=\"" + ResourceFactory.getProperty("button.close") + "\" onclick=\"parent.parent.window.close();\"/>");
		htmlContext.append("</td></tr>");

		StringBuffer titleHtml = new StringBuffer("<table  style='background-color:#FFF;' class='ListTable_self' >");
		titleHtml.append("<tr><td style=\"height:35px\" align=\"left\" colspan=\"" + colCount + "\">");
		titleHtml.append("名称： "+this.getObjName(object_id));
		if (this.planVo.getInt("object_type") == 2)//如果考核对象是人员，那么显示岗位名称 zhaoxg add 2016-8-18
        {
            titleHtml.append("&nbsp;&nbsp;&nbsp;岗位名称： "+this.getE01A1Name(object_id));
        }
		titleHtml.append("</td></tr>");
		StringBuffer html = new StringBuffer(titleHtml.toString());
		extendtHead.append("</tr>\r\n");
		html.append(extendtHead.toString());
		html.append(htmlContext.toString());
		html.append("</table>");
		return html.toString();

	}
	/**
	 * 
	 * @Title: getE01A1Name   
	 * @Description:获取人员岗位名称    
	 * @param @param object_id
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	public String getE01A1Name(String object_id)
	{
		String name="";
		try
		{
			String sql = "select e01a1 from per_object  where plan_id=" + this.planid + " and object_id='" + object_id +"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			String e01a1 = "";
			while (rs.next()) {
                e01a1=rs.getString(1);
            }

			if(e01a1!=null&&e01a1.length()>0){
				name = AdminCode.getCodeName("@K", e01a1);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	public String getTargetPointCellHtml(String field,String score1,FieldItem item)
	{
		StringBuffer htmlContext = new StringBuffer();
		String codesetId = item.getCodesetid();
		String itemdesc = item.getItemdesc();
		if("A".equalsIgnoreCase(item.getItemtype()))
		{
			htmlContext.append("<table><tr><td>");
			htmlContext.append("<input  type=\"text\" class=\"Input_self2\"  id=\"" + field+"\"");
			if (!"0".equals(codesetId))
			{
				// 代码型指标不接收键盘输入 lium
				htmlContext.append(" readonly name=\"" + field+".viewvalue\"  value=\"" + score1 + "\" />");	
				htmlContext.append("<input  type=\"hidden\" name=\"" + field+".value\" onchange='saveCodeValue(\"" + field+"\");'/>");					
				htmlContext.append("&nbsp;<img style='position:relative;top:4px;' src=\"/images/code.gif\"  BORDER=\"0\" style=\"cursor:hand;\"    onclick='javascript:");
				
				if(!"UN".equalsIgnoreCase(codesetId) && !"UM".equalsIgnoreCase(codesetId) && !"@k".equalsIgnoreCase(codesetId)) {
                    htmlContext.append("openKhTargetCardInputCode(\""+codesetId+"\",\"" + field+".viewvalue\");");
                } else
				{
					int x = field.toLowerCase().indexOf("score_org");
					if(x!=-1)
					{
						htmlContext.append("openInputCodeDialogOrg_handwork(\""+codesetId+"\",\"" + field+".viewvalue\",\"\",\"query\");");
					}else
					{
						if("UN".equalsIgnoreCase(codesetId)) {
                            htmlContext.append("openInputCodeDialogOrgInputPos(\""+codesetId+"\",\"" + field+".viewvalue\",\"\",\"1\");");
                        } else if("UM".equalsIgnoreCase(codesetId) || "@K".equalsIgnoreCase(codesetId)) {
                            htmlContext.append("openInputCodeDialogOrgInputPos(\""+codesetId+"\",\"" + field+".viewvalue\",\"\",\"2\");");
                        }
					}			
				}
				htmlContext.append("'/> ");				
			}else
			{
				htmlContext.append(" onBlur=\"saveTargetPointScore(this);\"   value=\"" + score1 + "\" maxlength='" + item.getItemlength() + "'/>");	
			}
			htmlContext.append("</td></tr></table>");
		}						
		else if("N".equalsIgnoreCase(item.getItemtype()))
		{
			// 数字需要加入中文字符的判断 lium
			htmlContext.append("<input  onBlur=\"saveTargetPointScore(this);\" onkeypress=\"event.returnValue=IsDigit(this);\"  type=\"text\" class=\"Input_self\"  id=\"" + field
					+ "\" value=\"" + score1 + "\" fieldType='N' maxlength='" + (item.getItemlength()+1+item.getDecimalwidth()) + "'/>");	
		}										
		else if("D".equalsIgnoreCase(item.getItemtype()))
		{
			htmlContext.append("<input  type=\"text\" extra=\"editor\" style=\"width:100px;font-size:10pt;text-align:left\" class=\"Input_self2\" 	dropDown=\"dropDownDate\"");
			htmlContext.append("  value=\"" + score1 + "\" onBlur=\"if(!validate(this,'"+itemdesc+"')) { this.value='';} else {if(this.value!=''){saveTargetPointScore_Date(this,'"+field+"');}}\"  />");	
		}else  if("M".equalsIgnoreCase(item.getItemtype()))
		{
			score1 = SafeCode.encode(score1);
			htmlContext.append("<img  src=\"/images/edit.gif\"  BORDER=\"0\" style=\"cursor:hand;\" onclick=\"updateBigField(this,'"+score1+"')\" id=\"" + field+ "\"  > ");	
		}		
		return htmlContext.toString();
	}
	
	/**
	 * 展示指标或者个性项目的分值或者标度
	 * 
	 * @param colname 为结果表中指标或者项目的列名
	 *        如：C_XXX T_XXX
	 * @param theScore
	 *        模板中指标的分数或者个性项目的分数
	 */
	public String getScoreHtml(String colname, String theScore)
	{
		StringBuffer html = new StringBuffer();
		String pointid = colname.substring(2).toUpperCase();
		colname = colname.toUpperCase();
		String keepDecimal = (String) this.planParameter.get("KeepDecimal");
		String adjustEvalDegreeType = (String) this.planParameter.get("AdjustEvalDegreeType"); // 调整使用标度 0=指标标度，1=等级标度.默认为0
		String adjustEvalDegreeNum = (String) this.planParameter.get("AdjustEvalDegreeNum"); // 调整浮动等级：整数值
		String adjustEvalRange = (String) this.planParameter.get("AdjustEvalRange"); // 调整范围：0=指标，1=总分.默认为0
		String scoreflag = (String) this.planParameter.get("scoreflag"); // 数据采集方式 =2混合，=1标度(默认值=混合) =4打分按加扣分处理
		String score_adjust = (String)this.perResultScoreMap.get("score_adjust");
		/** 调整前的分值 取自中间表 数据采集为标度时候，由这个值确定标度的下拉条目 */
		String scoreBeforeAjust = "";
		String scoreBeforeAjust_s ="";
		float scoreBeforeAjust_f=0;
		if(this.perResultScoreMap_original.get(pointid)!=null)
		{
			 scoreBeforeAjust = PubFunc.round(((Float) this.perResultScoreMap_original.get(pointid)).toString(), Integer.parseInt(keepDecimal));
			 scoreBeforeAjust_f = ((Float) this.perResultScoreMap_original.get(pointid)).floatValue();
			 scoreBeforeAjust_s = PubFunc.round(((Float) this.perResultScoreMap_original.get(pointid)).toString(), 6);
		}
		/** 调整后的分值 取自结果表 数据采集为标度时候，由这个值确定下拉条目中选中的条目 */
		String scoreAfterAjust = "";
		float scoreAfterAjust_f = 0;
		String scoreAfterAjust_s = "";
		String scoreAfterAjust_Grade = "";//按标度调整 scoreAfterAjust对应的标度
		if(this.perResultScoreMap.get(colname)!=null)
		{
			 scoreAfterAjust = PubFunc.round(((Float) this.perResultScoreMap.get(colname)).toString(), Integer.parseInt(keepDecimal));
			 scoreAfterAjust_f = ((Float) this.perResultScoreMap.get(colname)).floatValue();
			 scoreAfterAjust_s = PubFunc.round(((Float) this.perResultScoreMap.get(colname)).toString(), 6);
		}	
		if(scoreAfterAjust==null || scoreAfterAjust.trim().length()<=0) {
            scoreAfterAjust="0.0";
        }
		
		String align = "left";
		if("1".equals(adjustEvalRange) || ("0".equals(adjustEvalRange) && "2".equals(scoreflag)))//对于指标(个性项目)分值的展示 标度居左 分值居右
        {
            align = "right";
        }
		
		//调整前后的标度都是按照如下规则来确定标度的
		ArrayList gradeList = new ArrayList();
		if ("0".equals(adjustEvalDegreeType))// 指标标度
		{
			if ("C_".equalsIgnoreCase(colname.substring(0, 2)))
			{
				String point_id = colname.substring(2).toUpperCase();
				gradeList = (ArrayList) this.templatePointsGradeMap.get(point_id);
			} else if ("T_".equalsIgnoreCase(colname.substring(0, 2))) {
                gradeList = this.pointTemplateGrade;
            }
		} else if ("1".equals(adjustEvalDegreeType))// 等级标度
        {
            gradeList = this.planDegreeGrade;
        }
		
		
		//调整前分值 取自per_result_score
		html.append("<td align=\"right\" width=\"100\" class='RecordRow'>");		
		html.append(scoreBeforeAjust);									
		html.append("</td>");
		//调整前的标度
		if("1".equals(scoreflag))
		{
			html.append("<td align=\"left\" width=\"100\" class='RecordRow'>");
			if(this.perResultScoreMap_original.get(pointid)!=null)	
			{
				CommonData pointGradeItem_selected = null;
				for (int i = 0; i < gradeList.size(); i++)
				{

					LazyDynaBean abean = (LazyDynaBean) gradeList.get(i);
					String gradecode = (String) abean.get("gradecode");
					String gradedesc = (String) abean.get("gradedesc");
					Float top_value = (Float) abean.get("top_value");
					Float bottom_value = (Float) abean.get("bottom_value");
					String gradevalue = (String) abean.get("gradevalue");//比例
					String new_top_value = PubFunc.round(top_value.toString(),6);
					String new_bottom_value = PubFunc.round(bottom_value.toString(),6);
					//等级标度就是直接的范围 指标标度要乘以指标分
					if ("0".equals(adjustEvalDegreeType))// 指标标度
					{
						 new_top_value = PubFunc.multiple(top_value.toString(),String.valueOf(theScore), 6);
						 new_bottom_value = PubFunc.multiple(bottom_value.toString(),String.valueOf(theScore), 6);
					}
					// 如果模板分值设置了负分，则上下限需重新调整 chent 20160304 start
					
					if(Float.parseFloat(new_top_value) <  Float.parseFloat(new_bottom_value)){
						String tmp = "";
						tmp = new_top_value;
						new_top_value = new_bottom_value;
						new_bottom_value = tmp;
					}
					// 如果模板分值设置了负分，则上下限需重新调整 chent 20160304 end
					
					if("1".equals(scoreflag)){//标度
						String score = PubFunc.multiple(gradevalue.toString(),String.valueOf(theScore), 6);
						if(Float.parseFloat(scoreBeforeAjust_s) == Float.parseFloat(score)){
							pointGradeItem_selected = new CommonData(gradecode, gradedesc);
						}
					}else {
						if (Float.parseFloat(scoreBeforeAjust_s) != 0 && Float.parseFloat(scoreBeforeAjust_s) > Float.parseFloat(new_bottom_value) && Float.parseFloat(scoreBeforeAjust_s) <= Float.parseFloat(new_top_value)) {
                            pointGradeItem_selected = new CommonData(gradecode, gradedesc);
                        } else if (Float.parseFloat(scoreBeforeAjust_s) == 0 && Float.parseFloat(scoreAfterAjust_s) >= Float.parseFloat(new_bottom_value) && Float.parseFloat(scoreBeforeAjust_s) < Float.parseFloat(new_top_value)) {
                            pointGradeItem_selected = new CommonData(gradecode, gradedesc);
                        }
					}
					if(pointGradeItem_selected!=null)
					{
						String desc=pointGradeItem_selected.getDataName();
						if(desc.indexOf(":")!=-1)
						{
							desc=desc.substring(0,desc.indexOf(":"));
						}
						else if(desc.indexOf("：")!=-1)
						{
							desc=desc.substring(0,desc.indexOf("："));
						}
						
						html.append(desc);		
						break;
					}					
				}
			}
			html.append("</td>");	
		}	
		//调整后分值标度的显示	
		if ("1".equals(adjustEvalRange))// 调整总分 指标(个性项目)按实际分值展示 光显示调整前的分值 标度
		{
			
		} else if ("0".equals(adjustEvalRange))// 调整指标(个性项目)
		{
			if ("2".equals(scoreflag))// 如采用的是混合采集方式 用文本框输入分值 显示调整后分值 不显示调整后标度
			{
				html.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
				if (("C_".equalsIgnoreCase(colname.substring(0, 2)) && this.unifiedScoreMap.get(colname.substring(2).toUpperCase()) != null) || "2".equals(score_adjust)) {
                    html.append("<input readonly=\"readonly\"  type=\"text\" class=\"textColorRead\" name=\"" + colname + "\" id=\"" + colname + "\" value=\"" + scoreAfterAjust
                            + "\"  style='width: 65.0px' class=\"inputtext\"/>");
                } else {
                    html.append("<input onkeypress=\"event.returnValue=IsDigit(this);\"  type=\"text\" onblur='isNullVal(this);checkValue(this)' class=\"Input_self inputtext\" name=\"" + colname + "\" id=\"" + colname + "\" value=\"" + scoreAfterAjust
                            + "\"  style='width: 65.0px'  class=\"inputtext\"/>");
                }
				html.append("</td>");	

			} else if ("1".equals(scoreflag))// 如采用的是标度采集方式，则需要将结果表中的分值换算成标度 根据浮动的等级列出调整的标度等级范围  显示调整后分值和标度
			{
				html.append("<td align=\"right\" id=\""+pointid+"_rela\" width=\"100\" class='RecordRow'>");
				if(this.perResultScoreMap_original.get(pointid)!=null) {
                    html.append(scoreAfterAjust);
                }
				html.append("</td>");
				
				html.append("<td align=\"left\" width=\"100\" class='RecordRow'>");				
				boolean getGrade = false;
				ArrayList upperItems = new ArrayList();
				ArrayList bottomItems = new ArrayList();
				CommonData pointGradeItem_temp = null;// 上下浮动依据的下拉条目项
				CommonData pointGradeItem_selected = null;// 下拉条目中应该选中的那一项
		
				// 获得下拉的条目
				for (int i = 0; i < gradeList.size(); i++)
				{

					LazyDynaBean abean = (LazyDynaBean) gradeList.get(i);
					String gradecode = (String) abean.get("gradecode");
					String gradedesc = (String) abean.get("gradedesc");
					Float top_value = (Float) abean.get("top_value");
					Float bottom_value = (Float) abean.get("bottom_value");
					String gradevalue = (String) abean.get("gradevalue");//比例
					String new_top_value = PubFunc.round(top_value.toString(),6);
					String new_bottom_value = PubFunc.round(bottom_value.toString(),6);
					//等级标度就是直接的范围 指标标度要乘以指标分
					if ("0".equals(adjustEvalDegreeType))// 指标标度
					{
						 new_top_value = PubFunc.multiple(top_value.toString(),String.valueOf(theScore), 6);
						 new_bottom_value = PubFunc.multiple(bottom_value.toString(),String.valueOf(theScore), 6);
					}
						
					// 如果模板分值设置了负分，则上下限需重新调整 chent 20160304 start
					if(Float.parseFloat(new_top_value) <  Float.parseFloat(new_bottom_value)){
						String tmp = "";
						tmp = new_top_value;
						new_top_value = new_bottom_value;
						new_bottom_value = tmp;
					}
					// 如果模板分值设置了负分，则上下限需重新调整 chent 20160304 end
					
					if("1".equals(scoreflag)){//标度
						String score = PubFunc.multiple(gradevalue.toString(),String.valueOf(theScore), 6);
						if(Float.parseFloat(scoreAfterAjust_s) == Float.parseFloat(score)){
							pointGradeItem_selected = new CommonData(gradecode, gradedesc);
						}
					}else {
						
						if (Float.parseFloat(scoreAfterAjust_s) != 0 && Float.parseFloat(scoreAfterAjust_s) > Float.parseFloat(new_bottom_value) && Float.parseFloat(scoreAfterAjust_s) <= Float.parseFloat(new_top_value)) {
                            pointGradeItem_selected = new CommonData(gradecode, gradedesc);
                        } else if (Float.parseFloat(scoreAfterAjust_s) == 0 && Float.parseFloat(scoreAfterAjust_s) >= Float.parseFloat(new_bottom_value) && Float.parseFloat(scoreAfterAjust_s) < Float.parseFloat(new_top_value)) {
                            pointGradeItem_selected = new CommonData(gradecode, gradedesc);
                        }
					}
					
					if(this.perResultScoreMap_original.get(pointid)!=null)
					{
						if("1".equals(scoreflag)){//标度
							String score = PubFunc.multiple(gradevalue.toString(),String.valueOf(theScore), 6);
							if(Float.parseFloat(scoreBeforeAjust_s) == Float.parseFloat(score)){
								getGrade = true;
								pointGradeItem_temp = new CommonData(gradecode, gradedesc);
								continue;
							}
						}else {
							if (Float.parseFloat(scoreBeforeAjust_s) != 0 && Float.parseFloat(scoreBeforeAjust_s) > Float.parseFloat(new_bottom_value) && Float.parseFloat(scoreBeforeAjust_s) <= Float.parseFloat(new_top_value))
							{
								getGrade = true;
								pointGradeItem_temp = new CommonData(gradecode, gradedesc);
								continue;
							} else if (Float.parseFloat(scoreBeforeAjust_s) == 0 && Float.parseFloat(scoreBeforeAjust_s) >= Float.parseFloat(new_bottom_value)  && Float.parseFloat(scoreBeforeAjust_s) < Float.parseFloat(new_top_value) )
							{
								getGrade = true;
								pointGradeItem_temp = new CommonData(gradecode, gradedesc);
								continue;
							}
						}
					}

					if (getGrade == false) {
                        upperItems.add(new CommonData(gradecode, gradedesc));
                    } else {
                        bottomItems.add(new CommonData(gradecode, gradedesc));
                    }

				}

				if (pointGradeItem_temp != null)
				{
					html.append("<select  name=\"" + colname + "\" id=\"" + colname + "\" onchange='changeScore(this)' ");
					if (("C_".equalsIgnoreCase(colname.substring(0, 2)) && this.unifiedScoreMap.get(colname.substring(2).toUpperCase()) != null)  || "2".equals(score_adjust)) {
                        html.append("  disabled=\"disabled\" ");
                    }
					html.append(" />");
					
					int n = Integer.parseInt(adjustEvalDegreeNum);					
					if(upperItems.size()>n)
					{
						for (int i = upperItems.size() - n; i < upperItems.size(); i++)
						{
							CommonData item = (CommonData) upperItems.get(i);
							String datavalue = item.getDataValue();
							String dataname = item.getDataName();
							html.append("<option  ");
							if (datavalue.equals(pointGradeItem_selected.getDataValue())) {
                                html.append("selected=\"selected\"");
                            }
							html.append("  value=\"" + datavalue + "\">" + dataname + " </option>");
						}
					}else if(upperItems.size()<=n)
					{
						for (int i = 0; i< upperItems.size(); i++)
						{
							CommonData item = (CommonData) upperItems.get(i);
							String datavalue = item.getDataValue();
							String dataname = item.getDataName();
							html.append("<option  ");
							if (datavalue.equals(pointGradeItem_selected.getDataValue())) {
                                html.append("selected=\"selected\"");
                            }
							html.append("  value=\"" + datavalue + "\">" + dataname + " </option>");
						}
					}

					html.append("<option  ");
					if (pointGradeItem_temp.getDataValue().equals(pointGradeItem_selected.getDataValue())) {
                        html.append("selected=\"selected\"");
                    }
					html.append(" value=\"" + pointGradeItem_temp.getDataValue() + "\">" + pointGradeItem_temp.getDataName() + " </option>");

					scoreAfterAjust_Grade = pointGradeItem_selected.getDataValue();
					n = Integer.parseInt(adjustEvalDegreeNum);
					for (int i = 0; i < bottomItems.size() && n > 0; i++)
					{
						CommonData item = (CommonData) bottomItems.get(i);
						String datavalue = item.getDataValue();
						String dataname = item.getDataName();
						html.append("<option  ");
						if (datavalue.equals(pointGradeItem_selected.getDataValue())) {
                            html.append("selected=\"selected\"");
                        }
						html.append("  value=\"" + datavalue + "\">" + dataname + " </option>");
						n--;
					}
					html.append("</select>");
				}
				html.append("</td>");
			}
		}

		// 把指标分值存下来 方便保存用 score
		html.append("<input type=\"hidden\"  id=\"" + colname + "_score\" value=\"" + theScore + "\"  class=\"inputtext\"/>");// 指标分值
		html.append("<input type=\"hidden\"  id=\"" + colname + "_oldscore\" value=\"" + scoreAfterAjust+"`"+scoreAfterAjust_Grade + "\"  class=\"inputtext\"/>");// 点击调整进去用户操作前页面显示的分值
		//主体评分列的展示
		LazyDynaBean _abean=null;
		for(int i=0;i<this.mainbodyList.size();i++)
		{
			LazyDynaBean abean = (LazyDynaBean)mainbodyList.get(i);
			String mainbody_id = (String)abean.get("mainbody_id");
			String key = pointid+"`"+mainbody_id;
			html.append("<td align=\""+align+"\" width=\"100\" class='RecordRow'>");	
			if(this.perTableScoreMap.get(key)!=null)
			{
				String mainbodyscore = PubFunc.round(((Float) this.perTableScoreMap.get(key)).toString(), Integer.parseInt(keepDecimal));
				String mainbodydegree = this.perTableDegreeMap.get(key)==null?"":(String)this.perTableDegreeMap.get(key);
				
				float mainbodyscore_f = ((Float) this.perTableScoreMap.get(key)).floatValue();
				String mainbodyscore_s = PubFunc.round(((Float) this.perTableScoreMap.get(key)).toString(), 6);
				if ("2".equals(scoreflag))// 如采用的是混合采集方式
                {
                    html.append(mainbodyscore);
                } else if ("1".equals(scoreflag))//标度采集方式
			    {			
			    	String desc=mainbodydegree;
			    	if(this.planVo.getInt("method")==2)
			    	{
				    	for (int e = 0; e < gradeList.size(); e++)
						{ 
						    _abean = (LazyDynaBean) gradeList.get(e);
							String gradecode = (String) _abean.get("gradecode");
							String gradedesc = (String) _abean.get("gradedesc");
							if(mainbodydegree.trim().equalsIgnoreCase(gradecode.trim())) {
                                desc=gradedesc;
                            }
						}
			    	}
			    	
					if(desc.indexOf(":")!=-1)
					{
						desc=desc.substring(0,desc.indexOf(":"));
					}
					else if(desc.indexOf("：")!=-1)
					{
						desc=desc.substring(0,desc.indexOf("："));
					}
			    	html.append(desc);
//			    	if (colname.substring(0, 2).equalsIgnoreCase("C_"))
//					{
//			    		gradeList = new ArrayList();
//			    		if(this.planVo.getInt("method")==2)//目标的指标 采用指标标度
//			    			gradeList = (ArrayList) this.templatePointsGradeMap.get(pointid);
//			    		else//360 这样判断
//			    		{
//			    			String degreeShowType = (String) this.planParameter.get("DegreeShowType");  //1-标准标度 2-指标标度 3-采集标准标度,显示指标标度内容
//			    			if(degreeShowType.equals("1"))
//			    				gradeList = this.pointTemplateGrade;
//			    			else
//			    				gradeList = (ArrayList) this.templatePointsGradeMap.get(pointid);
//			    		}
//			    		CommonData pointGradeItem_selected = null;
//						for (int j = 0; j < gradeList.size(); j++)
//						{
//
//						    abean = (LazyDynaBean) gradeList.get(j);
//							String gradecode = (String) abean.get("gradecode");
//							String gradedesc = (String) abean.get("gradedesc");
//							Float top_value = (Float) abean.get("top_value");
//							Float bottom_value = (Float) abean.get("bottom_value");
//							String new_top_value = PubFunc.round(top_value.toString(),6);
//							String new_bottom_value = PubFunc.round(bottom_value.toString(),6);
//							
//							new_top_value = PubFunc.multiple(top_value.toString(),String.valueOf(theScore), 6);
//							new_bottom_value = PubFunc.multiple(bottom_value.toString(),String.valueOf(theScore), 6);							
//												
//							if (Float.parseFloat(mainbodyscore_s) != 0 && Float.parseFloat(mainbodyscore_s) > Float.parseFloat(new_bottom_value) && Float.parseFloat(mainbodyscore_s) <= Float.parseFloat(new_top_value))
//								pointGradeItem_selected = new CommonData(gradecode, gradedesc);
//							else if (Float.parseFloat(mainbodyscore_s) == 0 && Float.parseFloat(mainbodyscore_s) >= Float.parseFloat(new_bottom_value) && Float.parseFloat(mainbodyscore_s) < Float.parseFloat(new_top_value))
//								pointGradeItem_selected = new CommonData(gradecode, gradedesc);					
//
//							if(pointGradeItem_selected!=null)
//							{
//								html.append(pointGradeItem_selected.getDataName());		
//								break;
//							}					
//						}
//					} else if (colname.substring(0, 2).equalsIgnoreCase("T_"))//个性项目 不展示主体评分情况了
//					{
//						
//					}
			    }
			}
			html.append("</td>");
		}
		return html.toString();
	}

	/**得到调整后分值通过调整标度*/
	public String getAdjustScoreByGrade(ArrayList pointScoreList)
	{
		String theVal="";
		String adjustEvalDegreeType = (String) this.planParameter.get("AdjustEvalDegreeType"); // 调整使用标度 0=指标标度，1=等级标度.默认为0
		String adjustEvalRange = (String) this.planParameter.get("AdjustEvalRange"); // 调整范围：0=指标，1=总分.默认为0
		String scoreflag = (String) this.planParameter.get("scoreflag"); // 数据采集方式 =2混合，=1标度(默认值=混合) =4打分按加扣分处理
		String keepDecimal = (String) this.planParameter.get("KeepDecimal");
		String adjustEvalGradeStep = (String) this.planParameter.get("AdjustEvalGradeStep");//调整等级分值步长：十进制（如0.2），为0不处理。调整等级标度才可用。默认为空
		adjustEvalGradeStep=adjustEvalGradeStep.trim().length()==0?"0":adjustEvalGradeStep;
		
		String pointScoreStr = (String) pointScoreList.get(0);
		String[] pointScoreArray = pointScoreStr.split(":");
		String pointid = pointScoreArray[0];// T_XXX C_XXX totalScore
		String newScore = pointScoreArray[1];// 新值
		String pointScore = pointScoreArray[2];// 指标分
		String oldScore = pointScoreArray[3].split("`")[0];// 没有调整前的指标分
		String oldScore_grade = pointScoreArray[3].split("`")[1];// 没有调整前的等级
		if (!newScore.equals(oldScore_grade))
		{
			ArrayList gradeList = new ArrayList();// 标度列表
			int calcuRule = 1;//计算规则1: 指标标度 [分值=指标分值*标度比例] 等级标度 [分值=指标分值*等级系数] 计算规则2:等级标度的时候 分值=原来的值+-浮动的级数*每级浮动的分值
			if ("0".equals(adjustEvalDegreeType))// 指标标度 分值=指标分值*标度比例
			{
				if ("C_".equalsIgnoreCase(pointid.substring(0, 2)))
				{
					String point_id = pointid.substring(2).toUpperCase();
					gradeList = (ArrayList) this.templatePointsGradeMap.get(point_id);
				} else if ("T_".equalsIgnoreCase(pointid.substring(0, 2))) {
                    gradeList = this.pointTemplateGrade;
                }
			} else if ("1".equals(adjustEvalDegreeType) && Double.valueOf(adjustEvalGradeStep).doubleValue()==0)// 等级标度且每级浮动值为0  分值=指标分值*等级系数
            {
                gradeList = this.planDegreeGrade;
            } else if("1".equals(adjustEvalDegreeType) && Double.valueOf(adjustEvalGradeStep).doubleValue()!=0)//用计算规则2
			{
				gradeList = this.planDegreeGrade;
				calcuRule = 2;
			}
			
			if(calcuRule==1)
			{
				for (int j = 0; j < gradeList.size(); j++)
				{
					LazyDynaBean abean = (LazyDynaBean) gradeList.get(j);
					String gradecode = (String) abean.get("gradecode");
					String gradevalue = (String) abean.get("gradevalue");// 标度比例或者等级系数
					if (newScore.equalsIgnoreCase(gradecode))
					{
						double theValue = Double.parseDouble(pointScore) * Double.parseDouble(gradevalue);
						theVal = PubFunc.round((new Double(theValue)).toString(), Integer.parseInt(keepDecimal));
					}
				}
			}else if(calcuRule==2)
			{
				int gradeNum_oldScore=-1;
				int gradeNum_newScore=-1;
				for (int j = 0; j < gradeList.size(); j++)
				{
					LazyDynaBean abean = (LazyDynaBean) gradeList.get(j);
					String gradecode = (String) abean.get("gradecode");
					String gradevalue = (String) abean.get("gradevalue");// 标度比例或者等级系数

					if (oldScore_grade.equalsIgnoreCase(gradecode)) {
                        gradeNum_oldScore=j;
                    }
					
					if (newScore.equalsIgnoreCase(gradecode)) {
                        gradeNum_newScore=j;
                    }
				}
				
				double theValue = Double.parseDouble(oldScore)+ Double.valueOf(adjustEvalGradeStep).doubleValue()*(gradeNum_oldScore-gradeNum_newScore);
				theVal = PubFunc.round((new Double(theValue)).toString(), Integer.parseInt(keepDecimal));
			}
		}
		return theVal;
	}
	
	
	/** 保存评分调整 
	 * @throws GeneralException */
	public HashMap SaveScoreAjust(ArrayList pointScoreList, UserView userView, String oper) throws GeneralException
	{
		HashMap newResultMap = new HashMap();
		String totalScore = "";
		String resultdesc = "";
		String ordering = "";

		String adjustEvalDegreeType = (String) this.planParameter.get("AdjustEvalDegreeType"); // 调整使用标度 0=指标标度，1=等级标度.默认为0
		String adjustEvalRange = (String) this.planParameter.get("AdjustEvalRange"); // 调整范围：0=指标，1=总分.默认为0
		String scoreflag = (String) this.planParameter.get("scoreflag"); // 数据采集方式 =2混合，=1标度(默认值=混合) =4打分按加扣分处理
		String keepDecimal = (String) this.planParameter.get("KeepDecimal");
		String adjustEvalGradeStep = (String) this.planParameter.get("AdjustEvalGradeStep");//调整等级分值步长：十进制（如0.2），为0不处理。调整等级标度才可用。默认为空
		adjustEvalGradeStep=adjustEvalGradeStep.trim().length()==0?"0":adjustEvalGradeStep;
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer buf = new StringBuffer();
		StringBuffer buf2 = new StringBuffer();
		buf2.append("update per_result_score set AdjustScore=? where object_id='"+this.object_id+"' and plan_id="+this.planid+" and point_id=?");
		ArrayList list = new ArrayList();
		ArrayList list2 = new ArrayList();
		
		if ("1".equals(adjustEvalRange))// 调整总分
		{
			buf.append("update per_result_" + this.planid + " set score=?, ");
			for (int i = 0; i < pointScoreList.size(); i++)
			{
				String pointScoreStr = (String) pointScoreList.get(i);
				String[] pointScoreArray = pointScoreStr.split(":");
				String pointid = pointScoreArray[0];// T_XXX C_XXX totalScore
				if(pointid==null || pointid.trim().length()<=0) {
                    continue;
                }
				String newScore = pointScoreArray[1];// 新值
				String oldScore = pointScoreArray[3];// 进入页面时候的分值
				if ("totalScore".equalsIgnoreCase(pointid) && !newScore.equals(oldScore)) {
                    list.add(new Double(newScore));
                }
			}
		} else if ("0".equals(adjustEvalRange))// 调整指标(个性项目)
		{			
			if ("2".equals(scoreflag))// 如采用的是混合采集方式 用文本框输入分值
			{
				buf.append("update per_result_" + this.planid + " set ");				
				for (int i = 0; i < pointScoreList.size(); i++)
				{
					String pointScoreStr = (String) pointScoreList.get(i);
					String[] pointScoreArray = pointScoreStr.split(":");
					String pointid = pointScoreArray[0];// T_XXX C_XXX totalScore
					if(pointid==null || pointid.trim().length()<=0) {
                        continue;
                    }
					String newScore = pointScoreArray[1];// 新值
					String oldScore = pointScoreArray[3].split("`")[0];// 没有调整前的指标分或者等级
					
					if (!newScore.equals(oldScore))
					{
						list.add(new Double(newScore));
						buf.append(pointid + "=?,");
						
						ArrayList list3 = new ArrayList();
						list3.add(new Double(newScore));
						list3.add(pointid.substring(2).toUpperCase());
						list2.add(list3);
					}
				}
			} else if ("1".equals(scoreflag))// 如采用的是标度采集方式，则需要将结果表中的分值换算成标度 根据浮动的等级列出调整的标度等级范围
			{
				buf.append("update per_result_" + this.planid + " set ");
				for (int i = 0; i < pointScoreList.size(); i++)
				{
					String pointScoreStr = (String) pointScoreList.get(i);
					if(pointScoreStr!=null && pointScoreStr.trim().length()>0)
					{
						String[] pointScoreArray = pointScoreStr.split(":");
						String pointid = pointScoreArray[0];// T_XXX C_XXX totalScore
						if(pointid==null || pointid.trim().length()<=0) {
                            continue;
                        }
						
						String newScore = pointScoreArray[1];// 新值
						String pointScore = pointScoreArray[2];// 指标分
						String oldScore = pointScoreArray[3].split("`")[0];// 没有调整前的指标分
						String oldScore_grade = pointScoreArray[3].split("`")[1];// 没有调整前的等级
						if (!newScore.equals(oldScore_grade))
						{
							ArrayList gradeList = new ArrayList();// 标度列表
							int calcuRule = 1;//计算规则1: 指标标度 [分值=指标分值*标度比例] 等级标度 [分值=指标分值*等级系数] 计算规则2:等级标度的时候 分值=原来的值+-浮动的级数*每级浮动的分值
							if ("0".equals(adjustEvalDegreeType))// 指标标度 分值=指标分值*标度比例
							{
								if ("C_".equalsIgnoreCase(pointid.substring(0, 2)))
								{
									String point_id = pointid.substring(2).toUpperCase();
									gradeList = (ArrayList) this.templatePointsGradeMap.get(point_id);
								} else if ("T_".equalsIgnoreCase(pointid.substring(0, 2))) {
                                    gradeList = this.pointTemplateGrade;
                                }
							} else if ("1".equals(adjustEvalDegreeType) && Double.valueOf(adjustEvalGradeStep).doubleValue()==0)// 等级标度且每级浮动值为0  分值=指标分值*等级系数
                            {
                                gradeList = this.planDegreeGrade;
                            } else if("1".equals(adjustEvalDegreeType) && Double.valueOf(adjustEvalGradeStep).doubleValue()!=0)//用计算规则2
							{
								gradeList = this.planDegreeGrade;
								calcuRule = 2;
							}
							
							if(calcuRule==1)
							{
								for (int j = 0; j < gradeList.size(); j++)
								{
									LazyDynaBean abean = (LazyDynaBean) gradeList.get(j);
									String gradecode = (String) abean.get("gradecode");
									String gradevalue = (String) abean.get("gradevalue");// 标度比例或者等级系数
									if (newScore.equalsIgnoreCase(gradecode))
									{
										double theValue = Double.parseDouble(pointScore) * Double.parseDouble(gradevalue);
										list.add(new Double(theValue));
										buf.append(pointid + "=?,");
										
										ArrayList list3 = new ArrayList();
										list3.add(new Double(theValue));
										list3.add(pointid.substring(2).toUpperCase());
										list2.add(list3);
									}
								}
							}else if(calcuRule==2)
							{
								int gradeNum_oldScore=-1;
								int gradeNum_newScore=-1;
								for (int j = 0; j < gradeList.size(); j++)
								{
									LazyDynaBean abean = (LazyDynaBean) gradeList.get(j);
									String gradecode = (String) abean.get("gradecode");
									String gradevalue = (String) abean.get("gradevalue");// 标度比例或者等级系数
	
									if (oldScore_grade.equalsIgnoreCase(gradecode)) {
                                        gradeNum_oldScore=j;
                                    }
									
									if (newScore.equalsIgnoreCase(gradecode)) {
                                        gradeNum_newScore=j;
                                    }
								}
								
								double theValue = Double.parseDouble(oldScore)+ Double.valueOf(adjustEvalGradeStep).doubleValue()*(gradeNum_oldScore-gradeNum_newScore);
								list.add(new Double(theValue));
								buf.append(pointid + "=?,");
								
								ArrayList list3 = new ArrayList();
								list3.add(new Double(theValue));
								list3.add(pointid.substring(2).toUpperCase());
								list2.add(list3);
							}
						}
					}
				}
			}
		
		}
		
		try
		{
			PerEvaluationBo ebo = new PerEvaluationBo(this.conn, planid, "", userView);
			ebo.setScoreAjustOper(oper);
			
			// 自动计算
			LoadXml loadXml = new LoadXml(this.conn, this.planid);
			Hashtable param = loadXml.getDegreeWhole();

			HashMap map = new HashMap();
			map.put("ThrowHighCount", (String) param.get("ThrowHighCount"));
			map.put("ThrowLowCount", (String) param.get("ThrowLowCount"));
			map.put("KeepDecimal", (String) param.get("KeepDecimal"));
			map.put("UseWeight", (String) param.get("UseWeight"));
			map.put("UseKnow", (String) param.get("UseKnow"));
			map.put("KnowText", (String) param.get("KnowText"));
			map.put("AppUseWeight", (String) param.get("AppUseWeight"));
			map.put("EstBodyText", (String) param.get("EstBodyText"));
			map.put("ThrowBaseNum", (String) param.get("ThrowBaseNum"));
			if (param.get("formulaSql") != null) {
                map.put("formulaSql", (String) param.get("formulaSql"));
            }
			
			String EvalClass = (String)param.get("EvalClass");            //EvalClass在计划参数总体评价中的等级分类ID
			if(EvalClass==null || EvalClass.trim().length()<=0 || "0".equals(EvalClass.trim())) {
                EvalClass = (String)param.get("GradeClass");					//GradeClass 启动时等级分类ID
            }
			if(EvalClass!=null && EvalClass.trim().length()>0) {
                map.put("EvalClass",EvalClass);
            }
			if (param.get("GradeClass") != null) {
                map.put("GradeClass", (String) param.get("GradeClass"));
            }
			if (param.get("NodeKnowDegree") != null) {
                map.put("NodeKnowDegree", (String) param.get("NodeKnowDegree"));
            }
			if (param.get("WholeEval") != null) {
                map.put("WholeEval", (String) param.get("WholeEval"));
            }
			if (param.get("UnLeadSingleAvg") != null) {
                map.put("UnLeadSingleAvg", (String) param.get("UnLeadSingleAvg"));
            }
			
			if (list.size() > 0)
			{
				if ("2".equals(oper))// 提交调整
                {
                    buf.append("score_adjust=2 ");
                } else if ("1".equals(oper))// 保存调整
                {
                    buf.append("score_adjust=1 ");
                }

				buf.append(" where object_id='" + this.object_id + "'");
				dao.update(buf.toString(), list);
				//更新中间表
				dao.batchUpdate(buf2.toString(), list2);				
				
				
				try
				{
					if ("1".equals(adjustEvalRange))// 调整总分
                    {
                        ebo.calculatePlan(userView, map, 3);
                    } else if ("0".equals(adjustEvalRange))// 调整指标,个性项目分
                    {
                        ebo.calculatePlan(userView, map, 2);
                    }
				} catch (GeneralException e)
				{				
					//计算失败要恢复到点击保存或者提交前的分数
					list=new ArrayList();
					buf.setLength(0);
					if ("1".equals(adjustEvalRange))// 调整总分
					{
						buf.append("update per_result_" + this.planid + " set score=?, ");
						for (int i = 0; i < pointScoreList.size(); i++)
						{
							String pointScoreStr = (String) pointScoreList.get(i);
							String[] pointScoreArray = pointScoreStr.split(":");
							String pointid = pointScoreArray[0];// T_XXX C_XXX totalScore
							if(pointid==null || pointid.trim().length()<=0) {
                                continue;
                            }
							String newScore = pointScoreArray[1];// 新值
							String oldScore = pointScoreArray[3];// 进入页面时候的分值
							if ("totalScore".equalsIgnoreCase(pointid) && !newScore.equals(oldScore)) {
                                list.add(new Double(oldScore));
                            }
						}
					} else if ("0".equals(adjustEvalRange))// 调整指标(个性项目)
					{			
						if ("2".equals(scoreflag))// 如采用的是混合采集方式 用文本框输入分值
						{
							buf.append("update per_result_" + this.planid + " set ");
							for (int i = 0; i < pointScoreList.size(); i++)
							{
								String pointScoreStr = (String) pointScoreList.get(i);
								String[] pointScoreArray = pointScoreStr.split(":");
								String pointid = pointScoreArray[0];// T_XXX C_XXX totalScore
								if(pointid==null || pointid.trim().length()<=0) {
                                    continue;
                                }
								String newScore = pointScoreArray[1];// 新值
								String oldScore = pointScoreArray[3].split("`")[0];// 没有调整前的指标分或者等级
								if (!newScore.equals(oldScore))
								{
									list.add(new Double(oldScore));
									buf.append(pointid + "=?,");
								}
							}
						} else if ("1".equals(scoreflag))// 如采用的是标度采集方式，则需要将结果表中的分值换算成标度 根据浮动的等级列出调整的标度等级范围
						{
							buf.append("update per_result_" + this.planid + " set ");
							for (int i = 0; i < pointScoreList.size(); i++)
							{
								String pointScoreStr = (String) pointScoreList.get(i);
								String[] pointScoreArray = pointScoreStr.split(":");
								String pointid = pointScoreArray[0];// T_XXX C_XXX totalScore
								if(pointid==null || pointid.trim().length()<=0) {
                                    continue;
                                }
								String newScore = pointScoreArray[1];// 新值
								String pointScore = pointScoreArray[2];// 指标分
								String oldScore = pointScoreArray[3].split("`")[0];//恢复原来的分值
								list.add(new Double(oldScore));
								buf.append(pointid + "=?,");
							}
						}					
					}
					// 正调整状态
					buf.append("score_adjust=1 ");
					buf.append(" where object_id='" + this.object_id + "'");
					dao.update(buf.toString(), list);
					
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}				

				// 返回新的总分
				this.getPerResultScore();
				totalScore = PubFunc.round(((Float) this.perResultScoreMap.get("score")).toString(), Integer.parseInt(keepDecimal));
				resultdesc = (String) this.perResultScoreMap.get("resultdesc");
				ordering = (String) this.perResultScoreMap.get("ordering");

			} else
			// 没有做调整操作 直接保存或者提交
			{
				buf.setLength(0);
				buf.append("update per_result_" + this.planid + " set ");
				if ("2".equals(oper))// 提交调整
				{
					buf.append("score_adjust=2 ");
					String gradeID = (String) this.planParameter.get("GradeClass");
					String testResult = ebo.testGradeValueByHighSet(gradeID);
					if(!"success".equalsIgnoreCase(testResult)) {
                        throw new GeneralException(testResult);
                    }
				}			
				else if ("1".equals(oper))// 保存调整
                {
                    buf.append("score_adjust=1 ");
                }
				buf.append(" where object_id='" + this.object_id + "'");
				dao.update(buf.toString(), new ArrayList());
				
				
				try
				{
					if ("1".equals(adjustEvalRange))// 调整总分
                    {
                        ebo.calculatePlan(userView, map, 3);
                    } else if ("0".equals(adjustEvalRange))// 调整指标,个性项目分
                    {
                        ebo.calculatePlan(userView, map, 2);
                    }
				} catch (GeneralException e)
				{			
				
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		newResultMap.put("totalScore", totalScore);
		newResultMap.put("resultdesc", resultdesc);
		newResultMap.put("ordering", ordering);
		return newResultMap;
	}

	/** 评分调整 */
	public String getScoreAjustHtml(String ajustOper)
	{
		String keepDecimal = (String) this.planParameter.get("KeepDecimal");
		String score = PubFunc.round(((Float) this.perResultScoreMap.get("score")).toString(), Integer.parseInt(keepDecimal));
		if(score==null || score.trim().length()<=0) {
            score="0";
        }
		
		String resultdesc = (String) this.perResultScoreMap.get("resultdesc");
		String a0101 = (String) this.perResultScoreMap.get("a0101");
		String adjustEvalRange = (String) this.planParameter.get("AdjustEvalRange"); // 调整范围：0=指标，1=总分.默认为0
		String ordering = (String) this.perResultScoreMap.get("ordering");
		String scoreflag = (String) this.planParameter.get("scoreflag"); // 数据采集方式 =2混合，=1标度(默认值=混合) =4打分按加扣分处理
		String showGrpOrder = (String) this.planParameter.get("ShowGrpOrder");  //当前计划是否显示排名
		String gradeID = (String) this.planParameter.get("GradeClass");
		String org_grade = (String) this.perResultScoreMap.get("org_grade");//部门等级
	
		StringBuffer htmlContext = new StringBuffer("");		
		HashMap existWriteItem = new HashMap();
		LazyDynaBean abean = null;
		LazyDynaBean a_bean = null;
		StringBuffer extendtHead = new StringBuffer();
		int headCount= 2;//haosl 20170415 add  统计需要显示多少列
		String align = "left";
		if("1".equals(adjustEvalRange) || ("0".equals(adjustEvalRange) && "2".equals(scoreflag)))//对于指标(个性项目)分值的展示 标度居左 分值居右
        {
            align = "right";
        }
		
		// 输出表头
		int colCount =mainbodyList.size()+this.lay + 3;
		if("1".equals(scoreflag)) {
            colCount+=2;
        }
		
		extendtHead.append("<tr>");
		extendtHead.append("<td  class='RecordRow_right'  valign='middle' align='center' height='50'   colspan='" + colCount + "' style=\"border-top:0px;\">");
		extendtHead.append("<font face=宋体 style='font-weight:bold;font-size:15pt'>" + this.planVo.getString("name") + " </font></td> \r\n");
		extendtHead.append("</tr>\r\n");

		extendtHead.append("<tr>");
		extendtHead.append("<td  class='RecordRow'  valign='middle' align='left' height='50'   colspan='" + colCount + "'  style=\"border-left:0px;\">");
		extendtHead.append("<font face=宋体> 考核对象：" + a0101 + " </font></td> \r\n");
		extendtHead.append("</tr>\r\n");		
		
		extendtHead.append("<tr class='trDeep_self'  height='25' >\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' rowspan='2' colspan='" + this.lay + "'   width=\""+this.lay*150+"\"  style=\"border-left:0px;\" >项目</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  width=\"150\" rowspan='2' style=\"border-left:0px;\">指标名称</td>\r\n");
		
		
		if ("1".equals(adjustEvalRange))// 调整总分 指标(个性项目)按实际分值展示 光显示调整前的分值 标度
		{
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" rowspan='2' style=\"border-left:0px;\">分值</td>\r\n");
			headCount++;
			if("1".equals(scoreflag)){
				extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" rowspan='2' style=\"border-left:0px;\">标度</td>\r\n");	
				headCount++;
			}
		} else if ("0".equals(adjustEvalRange))// 调整指标(个性项目)
		{
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" rowspan='2' style=\"border-left:0px;\">分值(调整前)</td>\r\n");
			headCount++;
			if("1".equals(scoreflag)){
				extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" rowspan='2' style=\"border-left:0px;\">标度(调整前)</td>\r\n");		
				headCount++;
			}
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" rowspan='2' style=\"border-left:0px;\">分值(调整后)</td>\r\n");
			headCount++;
			if("1".equals(scoreflag)){
				extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" rowspan='2' style=\"border-left:0px;\">标度(调整后)</td>\r\n");
				headCount++;
			}
		}
		
		
		this.mainbodyList=new ArrayList();  //评分调整不显示主体评分明细 dengcan 2014-04-26
		if(mainbodyList.size()>0) {
            extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' colspan='" + mainbodyList.size() + "' width=\""+mainbodyList.size()*100+"\"  style=\"border-left:0px;\">考核人</td>\r\n");
        }
		
		
		extendtHead.append("</tr>\r\n");

		extendtHead.append("<tr class='trDeep_self'  height='25' >\r\n");
		for(int i=0;i<mainbodyList.size();i++)
		{
			 abean = (LazyDynaBean)mainbodyList.get(i);
			 String mainbodyname = (String)abean.get("a0101");
			 extendtHead.append("<td class='TableRow_2rows'   valign='middle' align='center' width=\"100\" style=\"border-left:0px;\">"+mainbodyname+"</td>\r\n");
		}
		extendtHead.append("</tr>\r\n");
		
		
		try
		{
			int rowNum = 0;
			/** 所有的叶子项目 */
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
				String item_kind = (String) abean.get("kind");
				/** 该项目的叶子节点(项目)个数 */
				int num = ((Integer) this.itemPointNum.get(item_id)).intValue();
				ArrayList pointList = (ArrayList) this.itemToPointMap.get(item_id);

				htmlContext.append("<tr>\r\n");
				rowNum++;
				/** 所有父亲列表 */
				ArrayList linkParentList = (ArrayList) this.leafItemLinkMap.get(item_id);
				int current = linkParentList.size();
				/** 叶子项目的继承关系列表 */
				for (int e = linkParentList.size() - 1; e >= 0; e--)
				{
					a_bean = (LazyDynaBean) linkParentList.get(e);
					String itemid = (String) a_bean.get("item_id");
					String kind = (String) a_bean.get("kind");
					if (existWriteItem.get(itemid) != null) {
                        continue;
                    }
					existWriteItem.put(itemid, "1");
					String itemdesc = (String) a_bean.get("itemdesc");
					/** 该项目所占的行数 */
					int colspan = ((itemPointNum.get(itemid) == null ? 0 : ((Integer) itemPointNum.get(itemid)).intValue()) + (childItemLinkMap.get(itemid) == null ? 0 : ((Integer) childItemLinkMap
							.get(itemid)).intValue()));
					/** 画出该项目 */
					htmlContext.append(writeTd(itemdesc, colspan, "left", 150, itemid, 1, false));
					if (e != 0)
					{
						/** 该项目的层数 */
						int layer = Integer.parseInt((String) layMap.get(itemid));
						/** 对应指标列表 */
						ArrayList fieldlistp = (ArrayList) this.itemHaveFieldList.get(itemid);
						/** 该项目有指标 */
						if (fieldlistp != null && fieldlistp.size() > 0)
						{
							for (int h = 0; h < fieldlistp.size(); h++)
							{
								LazyDynaBean xbean = (LazyDynaBean) fieldlistp.get(h);
								String pointid = (String) xbean.get("point_id");
								if (h != 0) {
                                    htmlContext.append("<tr>\r\n");
                                }
								for (int f = 0; f < this.lay - layer; f++)
								{
									htmlContext.append("<td align=\"left\"  width=\"150\" class='RecordRow' style=\"border-left:0px;\">&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" class='RecordRow'  width=\"150\" style=\"border-left:0px;\"");
								htmlContext.append(">" + (String) xbean.get("name") + "</td>");
								
//								htmlContext.append("<td align=\""+align+"\" width=\"100\" class='RecordRow'>");
								htmlContext.append(this.getScoreHtml("C_" + pointid, (String) xbean.get("score")));
								// htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"score\" id=\"s_" + pointid + "\" value=\""
								// + (String) xbean.get("score") + "\" style='width: 65.0px' />");

//								htmlContext.append("</td>");
								htmlContext.append("</tr>\r\n");
							}
						}
						/** 没有指标 */
						else
						{

							if (ifHasChildMap.get(itemid) == null)
							{
								for (int f = 0; f < this.lay - layer + 1; f++)
								{
									htmlContext.append("<td align=\"left\"  width=\"150\" class='RecordRow' style=\"border-left:0px;\">&nbsp;&nbsp;</td>");
								}
								/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
//								htmlContext.append("<td align=\""+align+"\" class='RecordRow' width=\"100\">");
								// htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"i_score\" id=\"si_" + (String) a_bean.get("item_id") + "\"
								// value=\""
								// + (String) a_bean.get("score") + "\" style='width: 65.0px'/>");
								htmlContext.append(this.getScoreHtml("T_" + (String) a_bean.get("item_id"), (String) a_bean.get("score")));
//								htmlContext.append("</td>");
								htmlContext.append("</tr>\r\n");
							}
						}
					}

					if (e == 0)
					{
						int layer = Integer.parseInt((String) layMap.get(itemid));
						ArrayList fieldlist = (ArrayList) this.itemHaveFieldList.get(itemid);

						if (fieldlist != null && fieldlist.size() != 0)
						{
							for (int x = 0; x < fieldlist.size(); x++)
							{
								if (x != 0) {
                                    htmlContext.append("<tr>\r\n");
                                }
								LazyDynaBean xbean = (LazyDynaBean) fieldlist.get(x);
								String pointid = (String) xbean.get("point_id");

								for (int f = 0; f < this.lay - layer; f++)
								{
									htmlContext.append("<td align=\"left\"  width=\"150\" class='RecordRow' style=\"border-left:0px;\">&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" class='RecordRow' width=\"150\" style=\"border-left:0px;\"");
								htmlContext.append(">" + (String) xbean.get("name") + "</td>");
//								htmlContext.append("<td align=\""+align+"\" width=\"100\" class='RecordRow'>");
								// htmlContext.append("<input onkeydown=\"checkKeyCode();\" onkeydown=\"checkValue(this,'score','" + pointid + "');\" onFocus=\"clearValue('"
								// + (String) xbean.get("score") + "','s_" + (String) xbean.get("point_id") + "')\" type=\"text\" class=\"Input_self\" name=\"score\" id=\"s_"
								// + (String) xbean.get("point_id") + "\" value=\"" + (String) xbean.get("score") + "\" style='width: 65.0px' />");
								htmlContext.append(this.getScoreHtml("C_" + pointid, (String) xbean.get("score")));
//								htmlContext.append("</td>");
								htmlContext.append("</tr>\r\n");
							}
						} else
						{
							for (int f = 0; f < this.lay - layer + 1; f++)
							{
								htmlContext.append("<td align=\"left\"  width=\"150\" class='RecordRow' style=\"border-left:0px;\">&nbsp;&nbsp;</td>");
							}
							/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
//							htmlContext.append("<td align=\""+align+"\" class='RecordRow' width=\"100\">");
							// htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self\" name=\"i_score\" onFocus=\"clearValue('" + (String) a_bean.get("score")
							// + "','si_" + (String) a_bean.get("item_id") + "')\" id=\"si_" + (String) a_bean.get("item_id") + "\" value=\"" + (String) a_bean.get("score")
							// + "\" style='width: 65.0px' />");
							htmlContext.append(this.getScoreHtml("T_" + (String) a_bean.get("item_id"), (String) a_bean.get("score")));
//							htmlContext.append("</td>");
							htmlContext.append("</tr>\r\n");
						}
					}
				}
			}
			htmlContext.append("<tr>\r\n");
			htmlContext.append("<td valign='middle' align='center'  colspan='" + (this.lay + 1) + "' class='RecordRow' style=\"border-left:0px;\">等级</td>");
			// 【1021】评分调整界面，内容不居中 lium 右对齐，同总分
			htmlContext.append("<td align=\"right\" class='RecordRow'  width=\"100\" id=\"resultdesc_td\" colspan='"+(colCount-this.lay-1)+"' style=\"border-left:0px;\">&nbsp");
			htmlContext.append(resultdesc);
			htmlContext.append("</td>");
			htmlContext.append("</tr>\r\n");

			htmlContext.append("<tr>\r\n");
			htmlContext.append("<td valign='middle' align='center'  colspan='" + (this.lay + 1) + "' class='RecordRow' style=\"border-left:0px;\">总分</td>");
			if ("1".equals(adjustEvalRange))
			{
				htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\"  colspan='"+(colCount-this.lay-1)+"' style=\"border-left:0px;\">");
				htmlContext.append("<input onkeypress=\"event.returnValue=IsDigit(this);\" onblur='isNullVal(this);checkValue(this)' type=\"text\" class=\"Input_self inputtext\" name='totalScore'  id='totalScore' value=\"" + score + "\"  style='width: 65.0px' />");
			} else
			{
				htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\" id=\"totalScore_td\" colspan='"+(colCount-this.lay-1)+"' style=\"border-left:0px;\">");
				htmlContext.append(score);
			}
			htmlContext.append("<input type=\"hidden\"  id=\"totalScore_score\" value=\"0\" />");// 指标分值
			htmlContext.append("<input type=\"hidden\"  id=\"totalScore_oldscore\" value=\"" + score + "\" />");// 点击调整进去用户操作前页面显示的分值
			htmlContext.append("&nbsp;</td>");
			htmlContext.append("</tr>\r\n");

			if (showGrpOrder != null && "false".equalsIgnoreCase(showGrpOrder))
			{

			} else
			{
				htmlContext.append("<tr>\r\n");
				htmlContext.append("<td valign='middle' align='center'  colspan='" + (this.lay + 1) + "' class='RecordRow' style=\"border-left:0px;\">排名</td>");
				htmlContext.append("<td align=\"right\" class='RecordRow' id=\"ordering_td\" width=\"100\" colspan='"+(colCount-this.lay-1)+"' style=\"border-left:0px;\">");
				htmlContext.append(ordering);
				htmlContext.append("&nbsp;</td>");
				htmlContext.append("</tr>\r\n");
			}

		
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		

		StringBuffer titleHtml = new StringBuffer("");		
		titleHtml.append("<script language='javascript' >");
		titleHtml.append("document.write(\"<div id='tbl-container'  style='position:absolute;top:10;left:5;height:\"+theHeight+\";width:99%' >\");");		
		titleHtml.append("</script>	");
		
		// 【1021】评分调整界面，内容不居中 lium 加右边框
		titleHtml.append("<table ");
		//haosl add 20170415因为屏蔽了主体评分明细列，根据colCount算表格宽度就不对了,所以添加headCount（页面显示的列总数）算出表格的固定宽度，超出屏幕显示滚动条
		titleHtml.append("width='"+headCount*180+"'");
		titleHtml.append(" class='ListTable_self common_border_color' style='border-right-width:1px;border-right-style:solid;'>");
		
		StringBuffer html = new StringBuffer(titleHtml.toString());
		html.append(extendtHead.toString());
		html.append(htmlContext.toString());
		html.append("</table>");
		html.append("<script language='javascript' >");
		html.append("document.write(\"</div>");		
		
		html.append("<div   style='position:absolute;left:5;top:\"+(theHeight+10)+\";width:99%'>\");");
		html.append("</script>	");
		
		html.append("<table width='100%'><tr><td style=\"height:35px\" align=\"center\" >");
		if ("adjust".equalsIgnoreCase(ajustOper))
		{
			if("0".equals(adjustEvalRange) && this.userView.hasTheFunction("06060501"))// 调整指标(个性项目)的时候才需要计算按钮
            {
                html.append("<input class=\"mybutton\" type=\"button\" name=\"bt0\" id='bt0' value=\"" + ResourceFactory.getProperty("button.computer") + "\" onclick=\"tempCompute();\"/>");
            }
			if(this.userView.hasTheFunction("06060502")) {
                html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt1\" id='bt1' value=\"" + ResourceFactory.getProperty("lable.func.main.save") + "\" onclick=\"saveAjust(1);\"/>");
            }
			if(this.userView.hasTheFunction("06060503")) {
                html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt2\" id='bt2' value=\"" + ResourceFactory.getProperty("button.submit") + "\" onclick=\"saveAjust(2);\"/>");
            }
			if(this.userView.hasTheFunction("06060504")) {
                html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt4\" id='bt4' value=\"" + ResourceFactory.getProperty("button.rejeect2") + "\" onclick=\"returnMark();\"/>");
            }
		}
	
		
		PerDegreeBo bo = new PerDegreeBo(this.conn,gradeID,this.planid);  
    	ArrayList degreeHighSet = bo.getDegreeHighSetList(true);//评分调整 忽略启用
    	ArrayList groupList = bo.getGroupList(this.planid);
    	ArrayList degreeItemList = bo.getDegrees();
    	titleHtml.setLength(0);
    	titleHtml.append("<table   class='ListTable' width='100%' >");
    	titleHtml.append("<tr>	<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">序号</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">方式</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">操作符</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">值</td>");
    	titleHtml.append("<td align=\"center\" nowrap colspan=\""+degreeItemList.size()+"\" class=\"TableRow\">等级项目</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">分组</td>");
    	
       	titleHtml.append("<tr>");
    	for(int j=0;j<degreeItemList.size();j++)
		{
			LazyDynaBean _abean = (LazyDynaBean)degreeItemList.get(j);
			String id = (String)_abean.get("id");
			titleHtml.append("<td align=\"center\"  class=\"TableRow\" nowrap>");
			titleHtml.append((String)_abean.get("itemname"));
			titleHtml.append("</td>");
		}
    	
    	
    	int index = 0;    	
    	StringBuffer tableBodyHtml = new StringBuffer();
       	for(int i=0;i<degreeHighSet.size();i++)
		{
       		abean = (LazyDynaBean)degreeHighSet.get(i);
			String mode = (String)abean.get("mode"); // 1 百分比 2 人数
			String oper = (String)abean.get("oper");//1 不少于 2 不多于
			String value = (String)abean.get("value");
			String grouped = (String)abean.get("grouped");// 分组指标
			String actIds = (String)abean.get("actIds");
			String um_grade = (String)abean.get("UMGrade");//部门考核等级
			
			//规则中定义了部门等级 考核对象也定义了 但是不相同 规则不显示
			if(um_grade.trim().length()>0 && org_grade.trim().length()>0 && !um_grade.trim().equalsIgnoreCase(org_grade.trim())) {
                continue;
            } else if(um_grade.trim().length()>0 && org_grade.trim().length()==0)//规则中定义了部门等级 考核对象没定义 规则不显示
            {
                continue;
            } else if(um_grade.trim().length()==0)//规则中没定义部门等级的 显示此规则
			{
				
			}
			mode= "1".equals(mode)?"百分比":"人数";
			oper= "1".equals(oper)?"不少于":"不多于";
			if("百分比".equalsIgnoreCase(mode)) {
                value=new Float(value).floatValue()*100+"";
            }
			value=DataCollectBo.roundAndRemoveZero(value, 2);
			tableBodyHtml.append("<tr><td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+(++index)+"</td>");
			tableBodyHtml.append("<td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+mode+"</td>");
			tableBodyHtml.append("<td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+oper+"</td>");
			tableBodyHtml.append("<td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+value+"</td>");
			for(int j=0;j<degreeItemList.size();j++)
			{
				LazyDynaBean _abean = (LazyDynaBean)degreeItemList.get(j);
				String id = (String)_abean.get("id");
				tableBodyHtml.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
				tableBodyHtml.append("<input type=\"checkbox\" disabled='true' ");
				if(actIds.indexOf(","+id+",")!=-1) {
                    tableBodyHtml.append(" checked ");
                }
				tableBodyHtml.append("/></td>");
			}

			tableBodyHtml.append("<td align=\"center\" class=\"RecordRow\" nowrap>");			
			tableBodyHtml.append("<select size='1' disabled='true' />");				
			for (int m = 0; m < groupList.size(); m++)
			{
				CommonData item = (CommonData) groupList.get(m);
				String datavalue = item.getDataValue();
				String dataname = item.getDataName();
				tableBodyHtml.append("<option  ");
				if (datavalue.equals(grouped)) {
                    tableBodyHtml.append("selected=\"selected\"");
                }
				tableBodyHtml.append("  value=\"" + datavalue + "\">" + dataname + " </option>");
			}
			tableBodyHtml.append("</select>"); 	  				
			
/*			
			tableBodyHtml.append("<input type=\"checkbox\" disabled='true' ");
			if(grouped.equalsIgnoreCase("true"))
				tableBodyHtml.append(" checked ");
			tableBodyHtml.append("/>");
*/			
			
			tableBodyHtml.append("</td></tr>");
		}
       	
    	html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt3\" id='bt3' value=\"" + ResourceFactory.getProperty("kq.search_feast.back") + "\" onclick=\"returnList();\"/>");
       	if(tableBodyHtml.length()>0)
       	{
       		html.append("&nbsp;<input  type=\"checkbox\"   onclick=\"dispRule(this);\"/>显示强制分布规则");
       	}
		html.append("</td></tr></table>");
		html.append("<script language='javascript' >");
		html.append("document.write(\"</div>\");");	
		html.append("</script>	");
       	
       	
       	if(tableBodyHtml.length()>0)
       	{
       		html.append("<script language='javascript' >");
       		html.append("document.write(\"<div  id='rule' style='position:absolute;left:5;top:\"+(theHeight+50)+\";width:99%;display:none' >\");");		
       		html.append("</script>	");
       		html.append(titleHtml);
       		html.append(tableBodyHtml);
       		html.append("</table>");
       		html.append("</div>");
       	}

		return html.toString();

	}
	
	
	
	public String  getHtml(String subHtml,String ajustOper,int colCount)
	{
		String gradeID = (String) this.planParameter.get("GradeClass");
		String org_grade = (String) this.perResultScoreMap.get("org_grade");//部门等级
		StringBuffer titleHtml = new StringBuffer("");		
		String a0101 = (String) this.perResultScoreMap.get("a0101");
		titleHtml.append("<table width='99%' ><tr><td width='100%' align='center' ><font face=宋体 style='font-weight:bold;font-size:15pt'>" + this.planVo.getString("name") + " </font>   </td></tr>");
		titleHtml.append("<tr>");
		titleHtml.append("<td align='left'    >");
		titleHtml.append("<font face=宋体> 考核对象：" + a0101 + " </font></td> \r\n");
		titleHtml.append("</tr>\r\n");		
		titleHtml.append("</table>");
		
		titleHtml.append("<script language='javascript' >");
		titleHtml.append("document.write(\"<div id='tbl-container'  style='position:absolute;top:55;left:5;height:\"+theHeight+\";width:99%' >\");");		
		titleHtml.append("</script>	");
		
		titleHtml.append("<table   class='ListTable_self' width='"+colCount*100 +"'  >");
		StringBuffer html = new StringBuffer(titleHtml.toString());
		html.append(subHtml);
		html.append("</table>");
		html.append("<script language='javascript' >");
		html.append("document.write(\"</div>");		
		
		html.append("<div   style='position:absolute;left:5;top:\"+(theHeight+50)+\";width:99%'>\");");
		html.append("</script>	");
		
		html.append("<table width='100%'><tr><td style=\"height:35px;padding-top:10px;\" align=\"center\" >");
		if ("adjust".equalsIgnoreCase(ajustOper))
		{
		    if(this.userView.hasTheFunction("06060502")) {
                html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt1\" id='bt1' value=\"" + ResourceFactory.getProperty("lable.func.main.save") + "\" onclick=\"saveAjust(1);\"/>");
            }
			if(this.userView.hasTheFunction("06060503")) {
                html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt2\" id='bt2' value=\"" + ResourceFactory.getProperty("button.submit") + "\" onclick=\"saveAjust(2);\"/>");
            }
			if(this.userView.hasTheFunction("06060504")) {
                html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt4\"  id='bt4'value=\"" + ResourceFactory.getProperty("button.rejeect2") + "\" onclick=\"returnMark();\"/>");
            }
		}
	
		
		PerDegreeBo bo = new PerDegreeBo(this.conn,gradeID,this.planid);  
    	ArrayList degreeHighSet = bo.getDegreeHighSetList(true);//评分调整 忽略启用
    	ArrayList groupList = bo.getGroupList(this.planid);
    	ArrayList degreeItemList = bo.getDegrees();
    	titleHtml.setLength(0);
    	titleHtml.append("<table   class='ListTable' width='100%' >");
    	titleHtml.append("<tr>	<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">序号</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">方式</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">操作符</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">值</td>");
    	titleHtml.append("<td align=\"center\" nowrap colspan=\""+degreeItemList.size()+"\" class=\"TableRow\">等级项目</td>");
    	titleHtml.append("<td align=\"center\" nowrap rowspan=\"2\" class=\"TableRow_2rows\">按部门分组</td>");
    	
       	titleHtml.append("<tr>");
    	for(int j=0;j<degreeItemList.size();j++)
		{
			LazyDynaBean _abean = (LazyDynaBean)degreeItemList.get(j);
			String id = (String)_abean.get("id");
			titleHtml.append("<td align=\"center\"  class=\"TableRow\" nowrap>");
			titleHtml.append((String)_abean.get("itemname"));
			titleHtml.append("</td>");
		}
    	
    	LazyDynaBean abean=null;
    	int index = 0;    	
    	StringBuffer tableBodyHtml = new StringBuffer();
       	for(int i=0;i<degreeHighSet.size();i++)
		{
       		abean = (LazyDynaBean)degreeHighSet.get(i);
			String mode = (String)abean.get("mode"); // 1 百分比 2 人数
			String oper = (String)abean.get("oper");//1 不少于 2 不多于
			String value = (String)abean.get("value");
			String grouped = (String)abean.get("grouped");// true 按部门分组
			String actIds = (String)abean.get("actIds");
			String um_grade = (String)abean.get("UMGrade");//部门考核等级
			
			//规则中定义了部门等级 考核对象也定义了 但是不相同 规则不显示
			if(um_grade.trim().length()>0 && org_grade.trim().length()>0 && !um_grade.trim().equalsIgnoreCase(org_grade.trim())) {
                continue;
            } else if(um_grade.trim().length()>0 && org_grade.trim().length()==0)//规则中定义了部门等级 考核对象没定义 规则不显示
            {
                continue;
            } else if(um_grade.trim().length()==0)//规则中没定义部门等级的 显示此规则
			{
				
			}
			mode= "1".equals(mode)?"百分比":"人数";
			oper= "1".equals(oper)?"不少于":"不多于";
			if("百分比".equalsIgnoreCase(mode)) {
                value=new Float(value).floatValue()*100+"";
            }
			value=DataCollectBo.roundAndRemoveZero(value, 2);
			tableBodyHtml.append("<tr><td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+(++index)+"</td>");
			tableBodyHtml.append("<td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+mode+"</td>");
			tableBodyHtml.append("<td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+oper+"</td>");
			tableBodyHtml.append("<td align=\"left\" class=\"RecordRow\" nowrap>&nbsp;"+value+"</td>");
			for(int j=0;j<degreeItemList.size();j++)
			{
				LazyDynaBean _abean = (LazyDynaBean)degreeItemList.get(j);
				String id = (String)_abean.get("id");
				tableBodyHtml.append("<td align=\"center\" class=\"RecordRow\" nowrap>");
				tableBodyHtml.append("<input type=\"checkbox\" disabled='true' ");
				if(actIds.indexOf(","+id+",")!=-1) {
                    tableBodyHtml.append(" checked ");
                }
				tableBodyHtml.append("/></td>");
			}

			tableBodyHtml.append("<td align=\"center\" class=\"RecordRow\" nowrap>");			
			tableBodyHtml.append("<select size='1' disabled='true' />");				
			for (int m = 0; m < groupList.size(); m++)
			{
				CommonData item = (CommonData) groupList.get(m);
				String datavalue = item.getDataValue();
				String dataname = item.getDataName();
				tableBodyHtml.append("<option  ");
				if (datavalue.equals(grouped)) {
                    tableBodyHtml.append("selected=\"selected\"");
                }
				tableBodyHtml.append("  value=\"" + datavalue + "\">" + dataname + " </option>");
			}
			tableBodyHtml.append("</select>"); 
			
/*			
			tableBodyHtml.append("<input type=\"checkbox\" disabled='true' ");
			if(grouped.equalsIgnoreCase("true"))
				tableBodyHtml.append(" checked ");
			tableBodyHtml.append("/>");
*/			
			
			tableBodyHtml.append("</td></tr>");
		}
       	
    	html.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bt3\" id='bt3' value=\"" + ResourceFactory.getProperty("kq.search_feast.back") + "\" onclick=\"returnList();\"/>");
       	if(tableBodyHtml.length()>0)
       	{
       		html.append("&nbsp;<input  type=\"checkbox\"   onclick=\"dispRule(this);\"/>显示强制分布规则");
       	}
		html.append("</td></tr></table>");
		html.append("<script language='javascript' >");
		html.append("document.write(\"</div>\");");	
		html.append("</script>	");
       	
       	
       	if(tableBodyHtml.length()>0)
       	{
       		html.append("<script language='javascript' >");
       		html.append("document.write(\"<div  id='rule' style='position:absolute;left:5;top:\"+(theHeight+90)+\";width:99%;display:none' >\");");		
       		html.append("</script>	");
       		html.append(titleHtml);
       		html.append(tableBodyHtml);
       		html.append("</table>");
       		html.append("</div>");
       	}

		return html.toString();
		
		
	}
	
	
	
	
	
	
	public Hashtable getPlanParameter() {
		return planParameter;
	}
	public void setPlanParameter(Hashtable planParameter) {
		this.planParameter = planParameter;
	}
}
