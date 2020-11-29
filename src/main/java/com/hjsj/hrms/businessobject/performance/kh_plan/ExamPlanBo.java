package com.hjsj.hrms.businessobject.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.upload.FormFile;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * <p>Title:ExamPlanBo.java</p>
 * <p>Description:考核计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 13:00:00</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class ExamPlanBo
{
	private Connection conn;

	private String planid;

	private RecordVo planVo = null;
	
	private UserView userView;
	String targetCollectItemMust = "";

	/**
     * @return the targetCollectItemMust
     */
    public String getTargetCollectItemMust() {
        return targetCollectItemMust;
    }
    /**
     * @param targetCollectItemMust the targetCollectItemMust to set
     */
    public void setTargetCollectItemMust(String targetCollectItemMust) {
        this.targetCollectItemMust = targetCollectItemMust;
    }
    public ExamPlanBo(Connection conn)
	{
		this.conn = conn;
	}
	public ExamPlanBo(Connection conn,UserView u)
	{
		this.conn = conn;
		this.userView=u;
	}

	public ExamPlanBo()
	{

	}

	public ExamPlanBo(Connection conn,UserView u,String plan_id) throws GeneralException
	{
		this.conn = conn;
		this.userView=u;
		this.planid=plan_id;
		if (this.isExist(this.planid)) {
            this.planVo = getPerPlanVo(this.planid);
        }
	}
	
	public ExamPlanBo(String plan_id, Connection conn) throws GeneralException
	{
		this.planid = plan_id;
		this.conn = conn;
		if (this.isExist(this.planid)) {
            this.planVo = getPerPlanVo(this.planid);
        }

	}
	/**通过考核对象类型的权限相关设置得到计划的过滤条件*/
	public String getPlanWhlByObjTypePriv(UserView u,String busitype)
	{
		StringBuffer whlSql = new StringBuffer();
		try
		{
			if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype)) {
                return whlSql.toString();
            }
			
			// 修改不在按参数 RightCtrlByPerObjType 控制  JinChunhai 2011.12.31
			if(u.hasTheFunction("3260207") && u.hasTheFunction("3260208")) {
                return whlSql.toString();
            } else if(u.hasTheFunction("3260207")) // 负责人员和自己创建的绩效考核计划
            {
                whlSql.append(" and (object_type=2 or create_user='" + u.getUserName() + "')");
            } else if(u.hasTheFunction("3260208")) // 负责团队和自己创建的绩效考核计划
            {
                whlSql.append(" and (object_type!=2 or create_user='" + u.getUserName() + "')");
            } else if((!u.hasTheFunction("3260207")) && (!u.hasTheFunction("3260208"))) // 没有授权任何绩效计划,只显示自己创建的绩效考核计划
            {
                whlSql.append(" and (create_user='" + u.getUserName() + "')");
            }
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return whlSql.toString();
	}
	
	public static String getKhPeriod(String cycle,String theyear,String themonth,String thequarter,java.sql.Date start_date,java.sql.Date end_date)
	{
		String khPeriod = "";
		String yearTitle = ResourceFactory.getProperty("datestyle.year")+" ";
		if("0".equals(cycle)) {
            khPeriod=theyear+yearTitle;
        } else if("1".equals(cycle))
		{
			if("1".equals(thequarter)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("report.pigeonhole.uphalfyear");
            } else if("2".equals(thequarter)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("report.pigeonhole.downhalfyear");
            }
		}else if("2".equals(cycle))
		{
			if("01".equals(thequarter)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("report.pigionhole.oneQuarter");
            } else if("02".equals(thequarter)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("report.pigionhole.twoQuarter");
            } else if("03".equals(thequarter)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("report.pigionhole.threeQuarter");
            } else if("04".equals(thequarter)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("report.pigionhole.fourQuarter");
            }
		}else if("3".equals(cycle))
		{
			if("01".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.january");
            } else if("02".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.february");
            } else if("03".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.march");
            } else if("04".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.april");
            } else if("05".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.may");
            } else if("06".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.june");
            } else if("07".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.july");
            } else if("08".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.auguest");
            } else if("09".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.september");
            } else if("10".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.october");
            } else if("11".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.november");
            } else if("12".equals(themonth)) {
                khPeriod=theyear+yearTitle+ResourceFactory.getProperty("date.month.december");
            }
		}else if("7".equals(cycle)) {
            khPeriod=PubFunc.FormatDate(start_date).replaceAll("-","\\.")+'-'+PubFunc.FormatDate(end_date).replaceAll("-","\\.");
        }
		return khPeriod;
	}
	/**得到同考核周期的上一个考核区间*/  //  JinChunhai  2011.03.01 修改
	public static String getLasyPeriod(String cycle,String theyear,String themonth,String thequarter)
	{
		StringBuffer buf = new StringBuffer();
		if("0".equals(cycle))
		{
			buf.append(" and theyear<'"+theyear+"'");
			buf.append(" order by theyear desc ");
			
//			buf.append(" and theyear='"+(Integer.parseInt(theyear)-1)+"'");
		}
		else if(("1".equals(cycle)) || ("2".equals(cycle)))
		{
			buf.append(" and ( theyear<'"+theyear+"'");
			buf.append(" or ( theyear='"+theyear+"' and thequarter<'"+thequarter+"' ) )");
			buf.append(" order by theyear desc ,thequarter desc ");
			
/*			if(thequarter.equals("1"))
				buf.append(" and theyear='"+(Integer.parseInt(theyear)-1)+"' and thequarter='2'");
			else if(thequarter.equals("2"))
				buf.append(" and theyear='"+theyear+"' and thequarter='1'");	
		*/					
/*		}else if(cycle.equals("2"))
		{									
			if(thequarter.equals("01"))
				buf.append(" and theyear='"+(Integer.parseInt(theyear)-1)+"' and thequarter='04'");
			else if(thequarter.equals("02"))
				buf.append(" and theyear='"+theyear+"' and thequarter='01'");	
			else if(thequarter.equals("03"))
				buf.append(" and theyear='"+theyear+"' and thequarter='02'");	
			else if(thequarter.equals("04"))
				buf.append(" and theyear='"+theyear+"' and thequarter='03'");	
			*/
		}else if("3".equals(cycle))
		{			
			buf.append(" and ( theyear<'"+theyear+"'");
			buf.append(" or ( theyear='"+theyear+"' and themonth<'"+themonth+"' ) )");
			buf.append(" order by theyear desc ,themonth desc ");
			
/*			if(themonth.equals("01"))
				buf.append(" and theyear='"+(Integer.parseInt(theyear)-1)+"' and themonth='12'");
			else if(themonth.equals("02"))
				buf.append(" and theyear='"+theyear+"' and themonth='01'");	
			else if(themonth.equals("03"))
				buf.append(" and theyear='"+theyear+"' and themonth='02'");	
			else if(themonth.equals("04"))
				buf.append(" and theyear='"+theyear+"' and themonth='03'");	
			else if(themonth.equals("05"))
				buf.append(" and theyear='"+theyear+"' and themonth='04'");	
			else if(themonth.equals("06"))
				buf.append(" and theyear='"+theyear+"' and themonth='05'");	
			else if(themonth.equals("07"))
				buf.append(" and theyear='"+theyear+"' and themonth='06'");	
			else if(themonth.equals("08"))
				buf.append(" and theyear='"+theyear+"' and themonth='07'");	
			else if(themonth.equals("09"))
				buf.append(" and theyear='"+theyear+"' and themonth='08'");	
			else if(themonth.equals("10"))
				buf.append(" and theyear='"+theyear+"' and themonth='09'");	
			else if(themonth.equals("11"))
				buf.append(" and theyear='"+theyear+"' and themonth='10'");	
			else if(themonth.equals("12"))
				buf.append(" and theyear='"+theyear+"' and themonth='11'");	
			*/
		}
		return buf.toString();
	}
	/**评分调整满足条件的计划*/
	public ArrayList getScoreAjustPlans(String whlSql)
	{
		ContentDAO dao = new ContentDAO(this.conn);		
		ArrayList list = new ArrayList();		
		try
		{
			HashMap planMap = this.getPlansByUserView(this.userView, whlSql);
			Set planSet = planMap.keySet();
			StringBuffer buf = new StringBuffer();
			for (Iterator iter = planSet.iterator(); iter.hasNext();)
			{
				String planid = (String) iter.next();														
				buf.append(" or plan_id="+planid);
			}
			String sql = "select * from per_plan where 1=1 and ";
			if(buf.length()==0) {
                sql+="1=2";
            } else {
                sql+="("+buf.substring(4)+")";
            }
				
			RowSet rs = dao.search(sql);
			while (rs.next())
			{
				String parameter_content = Sql_switcher.readMemo(rs, "parameter_content");
				if (parameter_content.length() > 0)
				{
					LoadXml xmlBo = new LoadXml(this.conn, parameter_content, 1);
				    Hashtable ht = xmlBo.getDegreeWhole();
				    if (ht != null && ht.get("AllowAdjustEvalResult") != null && "true".equalsIgnoreCase((String) ht.get("AllowAdjustEvalResult"))) {
                        list.add(rs.getString("plan_id"));
                    }
				}
			}
			
			if(rs!=null) {
                rs.close();
            }
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
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
			ContentDAO dao = new ContentDAO(this.conn);
			vo.setInt("plan_id", Integer.parseInt(planid));
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}

	public boolean isExist(String planId) throws GeneralException
	{
		if(planId==null || planId.trim().length()==0) {
            return false;
        }
		StringBuffer strsql = new StringBuffer();
		strsql.append("select  plan_id from per_plan where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet rs = dao.search(strsql.toString());
			if (rs.next()) {
                return true;
            }
			if(rs!=null) {
                rs.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw new GeneralException("查询数据异常！");
		}
		return false;
	}

	public String getPlanName(String planId) throws GeneralException
	{

		String name = "";
		StringBuffer strsql = new StringBuffer();
		strsql.append("select  name from per_plan where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet rs = dao.search(strsql.toString());
			if (rs.next()) {
                name = rs.getString("name");
            }
			if(rs!=null) {
                rs.close();
            }
		} catch (SQLException e)
		{
			throw new GeneralException("查询数据异常！");
		}
		return name;
	}

	/**
     * @param targeItems
     * @param isCheck 是否选择支持任务上传附件，如果支持，目标卡中，将出现附件指标1支持，0不支持
     * @return
     */
	public ArrayList getTargetItemList(String targetDefineItems, String targeItems,String isCheck)
	{
		ArrayList list = new ArrayList();
		if (targetDefineItems.trim().length() == 0) {
            return list;
        }
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String[] items = targetDefineItems.split(",");
			StringBuffer buf = new StringBuffer();
			boolean flag1=false;
			boolean flag2=false;
			for (int i = 0; i < items.length; i++){
				if("rater".equalsIgnoreCase(items[i]))//下面指标中不显示评价人指标
                {
                    continue;
                }
				buf.append(",'" + items[i].toUpperCase() + "'");
				if("attachment".equalsIgnoreCase(items[i])) {
                    flag1=true;
                }
			}

			String sqlStr = "SELECT itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' AND useflag='1' and " + Sql_switcher.isnull("state", "1") + "<>0 AND upper(itemid) IN ("
					+ buf.substring(1) + ") ORDER BY displayid";
			RowSet rowSet = dao.search(sqlStr);

			items = targeItems.split(",");
			HashMap map = new HashMap();
			for (int i = 0; i < items.length; i++){
				map.put(items[i], "");
				if("attachment".equalsIgnoreCase(items[i])) {
                    flag2=true;
                }
			}
			LoadXml loadxml = new LoadXml(conn, planid);
            Hashtable params = loadxml.getDegreeWhole();
            String targetCollectItemMust = (String) params.get("TargetCollectItemMust");
            if(targetCollectItemMust==null || "".equals(targetCollectItemMust)) {
                targetCollectItemMust = this.targetCollectItemMust;
            }
            if(targetCollectItemMust!=null && targetCollectItemMust.length()>0){
                items = targetCollectItemMust.split(",");
                for (int i = 0; i < items.length; i++){
                    map.put(items[i]+"must", "");
                }
            }
			while (rowSet.next())
			{
				String itemdesc = rowSet.getString("itemdesc") == null ? "" : rowSet.getString("itemdesc");
				String itemid = rowSet.getString("itemid") == null ? "" : rowSet.getString("itemid");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", itemid);
				abean.set("itemdesc", itemdesc);
				abean.set("selected", map.get(itemid) == null ? "0" : "1");
				abean.set("selectedmust", map.get(itemid+"must")== null ? "0" : "1");
				list.add(abean);
			}
			if(flag1 && "1".equals(isCheck))
			{
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", "ATTACHMENT");
				abean.set("itemdesc", "附件");
				abean.set("selected", flag2? "1" : "0");
				list.add(abean);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得p04任务表的定义计算公式的指标串 *
	 * <p>
	 * create time:2010-10-25 14:00:00
	 * </p>
	 * 
	 * @author JinChunhai
	 * @return
	 */
	public String getComputeItemStr(String targetDefineItems)
	{
		StringBuffer _str = new StringBuffer("");
		if (targetDefineItems.trim().length() == 0) {
            return _str.toString();
        }
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String[] items = targetDefineItems.split(",");
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < items.length; i++) {
                buf.append(",'" + items[i].toUpperCase() + "'");
            }
			
			String sqlStr = "SELECT expression,itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' AND useflag='1' AND " + Sql_switcher.isnull("state", "1") + "<>0 AND upper(itemid) IN ("
						+ buf.substring(1) + ",'TASK_SCORE') ORDER BY displayid";
			RowSet rowSet = dao.search(sqlStr);
			while (rowSet.next())
			{
				String expression = rowSet.getString("expression");
				if (expression != null && expression.length() > 0) {
                    _str.append("," + rowSet.getString("itemid"));
                }
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return _str.toString();
	}

	/**
	 * 获得p04任务表的定义计算公式的指标
	 * <p>
	 * create time:2010-10-25 13:00:00
	 * </p>
	 * 
	 * @author JinChunhai
	 * @return
	 */
	public ArrayList getComputeItemList(String _targetItems, String targetDefineItems, String computeItems)
	{
		ArrayList list = new ArrayList();
		if (targetDefineItems.trim().length() == 0) {
            return list;
        }
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String[] items = targetDefineItems.split(",");
			RowSet rowSet;
			String sqlStr = "";
			if (items == null || items.length == 0)
			{
				sqlStr = "SELECT expression,itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' AND useflag='1' AND " + Sql_switcher.isnull("state", "1")
						+ "<>0 AND upper(itemid) IN ('TASK_SCORE')";
			} else
			{
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < items.length; i++) {
                    buf.append(",'" + items[i].toUpperCase() + "'");
                }

				sqlStr = "SELECT expression,itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' AND useflag='1' AND " + Sql_switcher.isnull("state", "1") + "<>0 AND upper(itemid) IN ("
						+ buf.substring(1) + ",'TASK_SCORE') ORDER BY displayid ";
				
			}
			rowSet = dao.search(sqlStr);
			ArrayList a1List = new ArrayList();
			String targetItems = "";
			if (computeItems != null && computeItems.trim().length() > 0) {
                targetItems = computeItems;
            }
			while (rowSet.next())
			{
				String expression = rowSet.getString("expression");
				if (expression == null || expression.length() <= 0)
				{
					String itemid = rowSet.getString("itemid") == null ? "" : rowSet.getString("itemid");
					targetItems = targetItems.replaceAll(itemid + ",", "");
				} else
				{
					String itemdesc = rowSet.getString("itemdesc") == null ? "" : rowSet.getString("itemdesc");
					String itemid = rowSet.getString("itemid") == null ? "" : rowSet.getString("itemid");
					a1List.add(itemid + ":" + itemdesc);

					if (computeItems.toLowerCase().indexOf(itemid.toLowerCase() + ",") == -1) {
                        targetItems += itemid + ",";
                    }
				}
			}

			String[] itemss = targetItems.split(",");
			String buff = "";
			int a = 0;
			int b = 0;
			for (int c = 0; c < a1List.size(); c++)
			{
				if ("task_score".equalsIgnoreCase(((String) a1List.get(c)).split(":")[0]))
				{
					b = 1;
				} 
			}
			if (b == 1)
			{
				if (targetItems.trim().length() == 0)
				{
					buff = "task_score,";
				} else
				{
					for (int y = 0; y < itemss.length; y++)
					{
						buff += itemss[y] + ",";
						if ("task_score".equalsIgnoreCase(itemss[y]))
						{
							a = 1;
						}
					}
					if (a != 1)
					{						
						buff = buff + "task_score,";
					}
				}
				itemss = buff.split(",");
			} else
			{
				for (int y = 0; y < itemss.length; y++)
				{
					if (itemss[y].trim().length() > 0)
					{
						if ("task_score".equalsIgnoreCase(itemss[y]))
						{}							
						else {
                            buff += itemss[y] + ",";
                        }
					}					
				}
				itemss=buff.split(",");
			}
			for (int j = 0; j < itemss.length; j++)
			{
				for (int k = 0; k < a1List.size(); k++)
				{
					if (itemss[j].equalsIgnoreCase(((String) a1List.get(k)).split(":")[0]))
					{
						itemss[j] = (String) a1List.get(k);
						break;
					}
				}
			}

			items = computeItems.split(",");
			HashMap map = new HashMap();
			for (int j = 0; j < items.length; j++)
			{
				map.put(items[j], "");
			}

			for (int x = 0; x < itemss.length; x++)
			{
				if (itemss[x].trim().length() > 0)
				{
					LazyDynaBean abean = new LazyDynaBean();
					abean.set("itemid", itemss[x].split(":")[0]);
					abean.set("itemdesc", itemss[x].split(":")[1]);
					abean.set("selected", map.get(itemss[x].split(":")[0]) == null ? "0" : "1");
					list.add(abean);
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
    /**
     * 
     * @param targeItems
     * @param isCheck 是否选择支持任务上传附件，如果支持，目标卡中，将出现附件指标1支持，0不支持
     * @return
     */
	public ArrayList getTargetDefineItemList(String targeItems,String isCheck)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sqlStr = "SELECT itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' AND useflag='1' and " + Sql_switcher.isnull("state", "1")
					+ "<>0 AND (upper(itemid) IN ('P0419','P0405','P0413','P0415','P0425','P0421','P0423','SCORE_ORG','TASK_SCORE','RATER')  OR ownflag<>'1') ORDER BY displayid";
			RowSet rowSet = dao.search(sqlStr);
			String[] items = targeItems.split(",");
			HashMap map = new HashMap();
			boolean flag=false;
			for (int i = 0; i < items.length; i++)
			{
				map.put(items[i], "");
				if("attachment".equalsIgnoreCase(items[i]))
				{
					flag=true;
				}
			}

			while (rowSet.next())
			{
				String itemdesc = rowSet.getString("itemdesc") == null ? "" : rowSet.getString("itemdesc");
				String itemid = rowSet.getString("itemid") == null ? "" : rowSet.getString("itemid");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", itemid);
				abean.set("itemdesc", itemdesc);
				abean.set("selected", map.get(itemid) == null ? "0" : "1");
				list.add(abean);
			}
			if("1".equals(isCheck))
			{
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", "attachment");
				abean.set("itemdesc", "附件");
				abean.set("selected", flag? "1" : "0");
				list.add(abean);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/** 反馈表设置 */
	public ArrayList getBackTableList(String cards, String templateId)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sqlStr = "select tabids from per_template where template_id = '" + templateId + "'";
			RowSet rowSet = dao.search(sqlStr);
			String tabids = "";
			if (rowSet.next()) {
                tabids = rowSet.getString(1);
            }

			if (tabids == null) {
                return list;
            }

			String[] items = cards.split(",");
			HashMap map = new HashMap();
			for (int i = 0; i < items.length; i++) {
                map.put(items[i], "");
            }

			sqlStr = "SELECT tabid,name FROM rname WHERE tabid in(" + tabids + ")";
			rowSet = dao.search(sqlStr);
			while (rowSet.next())
			{
				String name = rowSet.getString("name") == null ? "" : rowSet.getString("name");
				String tabid = rowSet.getString("tabid") == null ? "" : rowSet.getString("tabid");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("name", name);
				abean.set("tabid", tabid);
				abean.set("selected", map.get(tabid) == null ? "0" : "1");
				list.add(abean);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/** 结果全相同时不能保存的标度 */
	public ArrayList getGradeTemplateDegreeList(String degrees,String tableName)
	{
		ArrayList list = new ArrayList();

		String[] items = degrees.split(",");
		HashMap map = new HashMap();
		for (int i = 0; i < items.length; i++) {
            map.put(items[i], "");
        }

		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sqlStr = "select * from "+tableName+" order by gradeValue desc";
			RowSet rowSet = dao.search(sqlStr);

			while (rowSet.next())
			{
				String grade_template_id = rowSet.getString("grade_template_id");
				String gradedesc = rowSet.getString("gradedesc");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("grade_template_id", grade_template_id);
				abean.set("gradedesc", gradedesc);
				abean.set("selected", map.get(grade_template_id) == null ? "0" : "1");
				list.add(abean);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public RecordVo getPlanVo()
	{
		return planVo;
	}

	public void setPlanVo(RecordVo planVo)
	{
		this.planVo = planVo;
	}

	/**
	 * 根据指标权限表取得 计划中的指标字段
	 * 
	 * @param planid
	 * @return
	 */
	public ArrayList getPerPointList(String planid)
	{

		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search("select * from per_pointpriv_" + planid + " where 1=2");
			ResultSetMetaData mt = rowSet.getMetaData();
			for (int i = 0; i < mt.getColumnCount(); i++)
			{
				String columnName = mt.getColumnName(i + 1);
				if (columnName.length() > 2 && "C_".equalsIgnoreCase(columnName.substring(0, 2))) {
                    list.add(columnName);
                }
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * Description: 根据模板号找到对应的指标list
	 * @Version1.0 
	 * Nov 12, 2012 3:03:50 PM Jianghe created
	 * @param templateId
	 * @return
	 */
	public ArrayList getTemPointList(String templateId){
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search("select pt.point_id,pp.pointname from per_template_point pt,per_point pp where pt.item_id in(select pti.item_id from per_template_item pti where pti.template_id='"+templateId+"') and pp.point_id=pt.point_id");
			list.add(new CommonData("",""));
			while(rowSet.next())
		    {
				String point_id = isNull(rowSet.getString("point_id"));
				String pointname = isNull(rowSet.getString("pointname"));
				CommonData data=new CommonData("[" + pointname + "]",pointname);	   
				list.add(data);
		    }
			list.add(new CommonData("[总分]","总分"));
			list.add(new CommonData("[指标个数]","指标个数"));
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	// 定义公式可选择的指标
	public ArrayList getSelectList(String templateId)
	{
		ArrayList filelist = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			//加指标
			String sql = "select pt.point_id,pp.pointname from per_template_point pt,per_point pp where pt.item_id in(select pti.item_id from per_template_item pti where pti.template_id='"+templateId+"') and pp.point_id=pt.point_id";
				       
			rowSet = dao.search(sql);
			while(rowSet.next())
			{
				FieldItem item = new FieldItem();
				item.setItemid(rowSet.getString("point_id"));
				item.setItemdesc(PubFunc.keyWord_reback(rowSet.getString("pointname")));
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				filelist.add(item);
			}
			FieldItem item = new FieldItem();
			item.setItemid("totalscore");
			item.setItemdesc("总分");
			item.setItemtype("N");
			item.setDecimalwidth(2);
			item.setItemlength(12);
			filelist.add(item);	
			
			item = new FieldItem();
			item.setItemid("pointnumber");
			item.setItemdesc("指标个数");
			item.setItemtype("N");
			item.setDecimalwidth(0);
			item.setItemlength(10);
			filelist.add(item);		
			if (rowSet != null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return filelist;
	}
	/**
	 * Description: 检查公式定义是否正确
	 * @Version1.0 
	 * Nov 12, 2012 4:06:59 PM Jianghe created
	 * @param formula
	 * @return
	 * @throws GeneralException
	 */
	public String testformula(String formula,String templateId) throws GeneralException
	{
		String errorInfo = "ok";
//		ContentDAO dao = new ContentDAO(this.conn);
		if (formula != null && formula.trim().length() > 0)
		{												
			YksjParser yp=new YksjParser(this.userView, this.getSelectList(templateId), YksjParser.forNormal, YksjParser.STRVALUE, YksjParser.forPerson, "Ht", "");			
			yp.setCon(this.conn);
			boolean b = false;
			b = yp.Verify_where(formula.trim());

			if (b) // 校验通过
            {
                errorInfo = "ok";
            } else {
                errorInfo = yp.getStrError();
            }
			
		}else {
            errorInfo = "noHave";
        }
		
		return errorInfo;
	}

	/** 在考核主体表和主体指标权限表中增加本人类别的考核主体的纪录 */
	public void insertSelBody(String plan_id)
	{
		try
		{
			boolean isExistPointPriv = false;
			DbWizard dbWizard = new DbWizard(this.conn);
			if (dbWizard.isExistTable("per_pointpriv_" + plan_id, false)) {
                isExistPointPriv = true;
            }

			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("");
			sql.append("select B0110,E0122,E01A1,object_ID A0100,A0101 from per_object where plan_id=" + plan_id);

			String sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status)values(?,?,?,?,?,?,?,?,?,?)";
			ArrayList pointList = new ArrayList();
			if (isExistPointPriv) {
                pointList = getPerPointList(plan_id);
            }

			StringBuffer sql3 = new StringBuffer("insert into per_pointpriv_" + plan_id + " (id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname");
			StringBuffer sql_extend3 = new StringBuffer("?,?,?,?,?,?,?");
			for (int i = 0; i < pointList.size(); i++)
			{
				sql3.append("," + pointList.get(i));
				sql_extend3.append(",?");
			}
			RowSet rowSet = dao.search(sql.toString());

			ArrayList recordList2 = new ArrayList();
			ArrayList recordList3 = new ArrayList();
			while (rowSet.next())
			{
				ArrayList tempList2 = new ArrayList();
				ArrayList tempList3 = new ArrayList();

				IDGenerator idg = new IDGenerator(2, conn);
				String id = idg.getId("per_mainbody.id");
				tempList2.add(new Integer(id));
				tempList2.add(rowSet.getString("b0110"));
				tempList2.add(rowSet.getString("e0122"));
				tempList2.add(rowSet.getString("e01a1"));
				tempList2.add(rowSet.getString("a0100"));
				tempList2.add(rowSet.getString("a0100"));
				tempList2.add(rowSet.getString("a0101"));
				tempList2.add(new Integer(5));
				tempList2.add(new Integer(plan_id));
				tempList2.add(new Integer(0));
				recordList2.add(tempList2);

				tempList3.add(new Integer(id));
				tempList3.add(rowSet.getString("b0110"));
				tempList3.add(rowSet.getString("e0122"));
				tempList3.add(rowSet.getString("e01a1"));
				tempList3.add(rowSet.getString("a0100"));
				tempList3.add(rowSet.getString("a0100"));
				tempList3.add(rowSet.getString("a0101"));
				for (int i = 0; i < pointList.size(); i++) {
                    tempList3.add(new Integer(1));
                }
				recordList3.add(tempList3);
			}
			dao.batchInsert(sql2, recordList2);

			if (isExistPointPriv)
			{
				DBMetaModel dbmodel = new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("per_pointpriv_" + plan_id);
				dao.batchInsert(sql3.toString() + ")values(" + sql_extend3.toString() + ")", recordList3);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	
	
	
	
	
	// 添加计划时候这样传递参数 object_type_add：1-团队 2-人员（考核对象类别）3-单位 4-部门 如果为非人员类型本人不显示
	public ArrayList searchCheckBody2(String planId, String object_type_add, String bodyTypeIds) throws GeneralException
	{
		ArrayList list = new ArrayList();
		if ("".equals(planId)) {
            return list;
        }

		String object_type_edit = "";
		boolean existPlan = this.isExist(planId);
		if (existPlan)
		{
			RecordVo vo = this.getPerPlanVo(planId);
			object_type_edit = vo.getString("object_type");
		}

		HashMap map = new HashMap();
		try
		{
			StringBuffer strsql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet frowset = null;
			if (bodyTypeIds.trim().length() == 0)
			{
				// 加入新字段opt（打分确认标识），0或null→打分，1→确认 by 刘蒙
				strsql.append("select body_id,isgrade,grade_seq,opt from per_plan_body where plan_id=" + planId);
				frowset = dao.search(strsql.toString());
				while (frowset.next())
				{
					String isgrade = frowset.getString("isgrade"); // 参与评分，0或null→参与，1→不参与
					String grade_seq = frowset.getString("grade_seq"); // 评分顺序
					String opt = frowset.getString("opt"); // 打分确认标识
					
					isgrade = isgrade != null ? isgrade : "0";
					grade_seq = (grade_seq != null && !"0".equals(grade_seq)) ? grade_seq : "";
					opt = opt != null ? opt : "0";
					if("1".equals(isgrade)) {
						grade_seq="";
					}
					
					map.put(frowset.getString("body_id"), isgrade + "/" + grade_seq + "/" + opt);
				}
			} else
			{
				String[] temp = bodyTypeIds.split(",");
				for (int i = 0; i < temp.length; i++)
				{
					if (temp[i].length() > 0)
					{
						if(temp[i].indexOf("/")==-1) {
                            map.put(temp[i], "");
                        } else
						{
							String[] temps=temp[i].split("/");
							String isgrade_seq = "";
							for (int k = 1; k < temps.length; k++)
							{
								isgrade_seq += temps[k].trim()+"/";
							}
							map.put(temps[0].trim(), isgrade_seq);
							
						}						
					}
				}
			}

			strsql.setLength(0);
			strsql.append("select * from (");
			strsql.append("select * from per_mainbodyset where status=1 and (body_type=0 or body_type is null)");
			strsql.append(" union all ");
			strsql.append("select * from per_mainbodyset where status<>1 and (body_type=0 or body_type is null) ");
			strsql.append(" and body_id in (select body_id from per_plan_body where plan_id=" + planId + ")");
			strsql.append(") a  where 1=1 ");
			if (existPlan)
			{
				String myObjectType = object_type_edit;
				if (!myObjectType.equals(object_type_add)) {
                    myObjectType = object_type_add; // 编辑存在计划时候 先修改了考核对象类型
                }

				if (myObjectType != null && !"2".equals(myObjectType))// 如果考核对象类型不是人员,本人不显示
                {
                    strsql.append("  and a.body_id<>5 ");
                } else if (myObjectType != null && "2".equals(myObjectType))// 团队负责人类别不显示
                {
                    strsql.append("  and a.body_id<>-1 ");
                }
			} else
			{
				if (object_type_add != null && !"2".equals(object_type_add))// 如果考核对象类型不是人员,本人不显示
                {
                    strsql.append("  and a.body_id<>5 ");
                } else if (object_type_add != null && "2".equals(object_type_add))// 目标人员 团队负责人类别不显示
                {
                    strsql.append("  and a.body_id<>-1 ");
                }

			}
			strsql.append("  order by a.seq");

			frowset = dao.search(strsql.toString());
			while (frowset.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				String body_id = frowset.getString("body_id");
				abean.set("body_id", body_id);
				String name = frowset.getString("name") == null ? "" : frowset.getString("name");

				String level = "";
				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    level = frowset.getString("level_o") == null ? "" : frowset.getString("level_o");
                } else {
                    level = frowset.getString("level") == null ? "" : frowset.getString("level");
                }
				abean.set("level", level);

				if ("0".equals(level)) {
                    level = "上上级";
                } else if ("1".equals(level)) {
                    level = "直接上级";
                } else if ("2".equals(level)) {
                    level = "同事";
                } else if ("3".equals(level)) {
                    level = "下属";
                } else if ("4".equals(level)) {
                    level = "下下级";
                } else if ("5".equals(level)) {
                    level = "本人";
                } else if ("6".equals(level)) {
                    level = "一般";
                } else if ("-1".equals(level)) {
                    level = "第三级领导";
                } else if ("-2".equals(level)) {
                    level = "第四级领导";
                } else {
                    level = "一般";
                }

				abean.set("name", name + "(" + level + ")");
				abean.set("selected", map.get(body_id) != null ? "1" : "0");
				
				String temp_str = (String)map.get(body_id);
				if(temp_str != null && !"".equals(temp_str.trim()))
				{
					String[] temps=temp_str.split("/");
					abean.set("isgrade",temps[0]);
					abean.set("grade_seq",temps[1]);
					abean.set("opt_" + body_id,temps[2]); // 加入打分确认标识
				}
				else
				{
					if("1".equals((String)abean.get("selected"))) {
                        abean.set("isgrade","0");
                    } else {
                        abean.set("isgrade","1");
                    }
					abean.set("grade_seq","");
					abean.set("opt","0");
				}
				list.add(abean);

			}
			if(frowset!=null) {
                frowset.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	
	public ArrayList searchMainbodyGradeBody(String planId, String object_type_add, String bodyTypeIds,String mainbodyGradeCtl,String allmainbodyGradeCtl) throws GeneralException
	{
		ArrayList list = new ArrayList();
		if ("".equals(planId)) {
            return list;
        }

		String object_type_edit = "";
		boolean existPlan = this.isExist(planId);
		if (existPlan)
		{
			RecordVo vo = this.getPerPlanVo(planId);
			object_type_edit = vo.getString("object_type");
		}

		HashMap map = new HashMap();
		try
		{
			StringBuffer strsql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet frowset = null;
			if(bodyTypeIds.trim().length() == 0){
				return list;
			} else {
				if (mainbodyGradeCtl.trim().length() == 0)
				{
					String[] temp = bodyTypeIds.split(",");
					for (int i = 0; i < temp.length; i++)
					{
						if (temp[i].length() > 0)
						{
							if(temp[i].indexOf("/")==-1) {
                                map.put(temp[i], "");
                            } else
							{
								String[] temps=temp[i].split("/");
								String isgrade_seq = "";
								for (int k = 1; k < temps.length; k++)
								{
									isgrade_seq += temps[k].trim()+"/";
								}
								map.put(temps[0].trim(), isgrade_seq);
								
							}						
						}
					}
				} else
				{
					//2013.11.15 pjf   
					String tempstr = "";
					String[] arry1 = bodyTypeIds.split(",");//记录当前的主体类别
					String[] arry2 = allmainbodyGradeCtl.split(",");//记录上一次的主体类别
					for(int m=0; m<arry1.length; m++){
						boolean flag = false;
						for(int n=0; n<arry2.length; n++){
							if(arry2[n].equals(arry1[m])){
								flag = true;
								break;
							}
						}
						if(!flag){
							tempstr +=arry1[m]+",";//得到当前新增的主体类别 ，为了让其默认选中
						}
					}
					String[] reststr = tempstr.split(",");
					for (int i = 0; i < reststr.length; i++)
					{
						if (reststr[i].length() > 0)
						{
							map.put(reststr[i], "");					
						}
					}
					String[] temp = mainbodyGradeCtl.split(",");
					for (int i = 0; i < temp.length; i++)
					{
						if (temp[i].length() > 0)
						{
							if(temp[i].indexOf("/")==-1) {
                                map.put(temp[i], "");
                            } else
							{
								String[] temps=temp[i].split("/");
								String isgrade_seq = "";
								for (int k = 1; k < temps.length; k++)
								{
									isgrade_seq += temps[k].trim()+"/";
								}
								map.put(temps[0].trim(), isgrade_seq);
								
							}						
						}
					}
				}
	
				strsql.setLength(0);
				bodyTypeIds = bodyTypeIds.substring(0,bodyTypeIds.length()-1);
				strsql.append("select * from per_mainbodyset where body_id in ("+bodyTypeIds+")");
				strsql.append("  and body_id<>5 ");
				strsql.append("  and body_id<>-1 ");
				strsql.append("  order by seq");
	
				frowset = dao.search(strsql.toString());
				while (frowset.next())
				{
					LazyDynaBean abean = new LazyDynaBean();
					String body_id = frowset.getString("body_id");
					abean.set("body_id", body_id);
					String name = frowset.getString("name") == null ? "" : frowset.getString("name");
	
					String level = "";
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                        level = frowset.getString("level_o") == null ? "" : frowset.getString("level_o");
                    } else {
                        level = frowset.getString("level") == null ? "" : frowset.getString("level");
                    }
					abean.set("level", level);
	
					if ("0".equals(level)) {
                        level = "上上级";
                    } else if ("1".equals(level)) {
                        level = "直接上级";
                    } else if ("2".equals(level)) {
                        level = "同事";
                    } else if ("3".equals(level)) {
                        level = "下属";
                    } else if ("4".equals(level)) {
                        level = "下下级";
                    } else if ("5".equals(level)) {
                        level = "本人";
                    } else if ("6".equals(level)) {
                        level = "一般";
                    } else if ("-1".equals(level)) {
                        level = "第三级领导";
                    } else if ("-2".equals(level)) {
                        level = "第四级领导";
                    } else {
                        level = "一般";
                    }
	
					abean.set("name", name + "(" + level + ")");
					abean.set("selected", map.get(body_id) != null ? "1" : "0");
					
					String temp_str="";
					if(map.get(body_id) != null) {
                        temp_str=(String)map.get(body_id);
                    }
					
					if(temp_str.length()>0)
					{
						String[] temps=temp_str.split("/");
						
						if(temps.length>1)
						{
							abean.set("isgrade",temps[0]);
							abean.set("grade_seq",temps[1]);
						}
						else
						{
							abean.set("isgrade",temps[0]);
							abean.set("grade_seq","");
						}
					}
					else
					{
						if("1".equals((String)abean.get("selected"))) {
                            abean.set("isgrade","0");
                        } else {
                            abean.set("isgrade","1");
                        }
						abean.set("grade_seq","");
					}
					list.add(abean);
				}
			}
			if(frowset!=null) {
                frowset.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围 再看用户所在单位部门
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getB0110Where(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.trim().length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    }
				}
				if(tempSql!=null && tempSql.toString().trim().length()>0) {
                    buf.append(" and ( " + tempSql.substring(3) + " ) ");
                }
			} 
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg)))
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else {
                        buf.append(" and b0110 like '" + codevalue + "%'");
                    }
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = " and ((B0110 = 'HJSJ' or B0110 = '' ) ";// 任何权限都没有设置就显示公共资源的计划
			if (buf.length() > 3) {
                str += " or (" + buf.substring(4) + ")";
            }
			str += ")";

		}

		return str;
	}

	/** 根据模板和组织机构的权限控制计划 */
	public HashMap getHaveTempPrivPlans(UserView userView)
	{
		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer();
		buf.append("select plan_id,template_id from per_plan where 1=1 ");
		buf.append(this.getB0110Where(userView));

		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String controlByKHMoudle = getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			RowSet rowSet = dao.search(buf.toString());
			while (rowSet.next())
			{
				String plan_id = rowSet.getString(1);
				
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rowSet.getString(2);				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
				
				map.put(plan_id, "");
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/** 标准标度 */
	public ArrayList getGradeTemplate(String tableName) throws GeneralException
	{

		ArrayList grade_template = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet rowSet = dao.search("select * from "+tableName+" order by gradeValue desc");
			while (rowSet.next())
			{
				String grade_template_id = rowSet.getString("grade_template_id");
				String gradedesc = rowSet.getString("gradedesc");
				CommonData temp = new CommonData(grade_template_id, grade_template_id + "【" + gradedesc + "】");
				grade_template.add(temp);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return grade_template;

	}
	/**获得计划的主体类别*/
	public ArrayList getPlanBodys(String planid) throws GeneralException
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet rowSet = dao.search("select body_id,name from per_mainbodyset where body_id in (select body_id from per_plan_body where plan_id="+planid+")");
			while (rowSet.next())
			{
				String body_id = rowSet.getString(1);
				String name = rowSet.getString(2);
				CommonData temp = new CommonData(body_id, name);
				list.add(temp);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**获得部门的最大层级*/
	public ArrayList getDepartmentLeveList() throws GeneralException
	{
		ArrayList list = new ArrayList();
		list.add(new CommonData("",""));		
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search("select max(layer) from organization where codesetid = 'UM'");
			String layer = "";
			while (rowSet.next())
			{
				layer = rowSet.getString(1);				
			}			
			if(layer!=null && layer.trim().length()>0)
			{
				for (int i = 1; i <= Integer.parseInt(layer); i++)
				{
					list.add(new CommonData(String.valueOf(i),String.valueOf(i)+"级"));
				}
			}						
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/** 人员子集 */
	public ArrayList getItemFilterList()
	{

		ArrayList list = new ArrayList();
//		CommonData temp = new CommonData("", "");
//		list.add(temp);
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rset = dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'A%' and useflag='1' and fieldsetid !='A01' order by displayorder");
			while (rset.next())
			{
				CommonData temp = new CommonData(rset.getString("fieldsetid"), rset.getString("customdesc"));
				list.add(temp);
			}
			if(rset!=null) {
                rset.close();
            }
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return list;
	}
	/** 单位子集 */
	public ArrayList getItemFilterDWList()
	{
		ArrayList list = new ArrayList();
//		CommonData temp = new CommonData("", "");
//		list.add(temp);
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rset = dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'B%' and useflag='1' and fieldsetid !='B01' order by displayorder");
			while (rset.next())
			{
				CommonData temp = new CommonData(rset.getString("fieldsetid"), rset.getString("customdesc"));
				list.add(temp);
			}
			if(rset!=null) {
                rset.close();
            }
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return list;
	}
	
	public String getIsBrowse(String planId, UserView u) throws GeneralException
	{

		String isBrowse = "0";
		if (planId.trim().length() == 0) {
            return isBrowse;
        }
		String tableName = "per_plan";
		DbWizard dbWizard = new DbWizard(this.conn);
		if (!this.isExist(planId) && dbWizard.isExistTable("t#"+u.getUserId()+"_per_file", false)) {
            tableName ="t#"+u.getUserId()+"_per_file"; // "per_plan_file_" + planId + "_" + u.getUserId();
        }
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet rset = dao.search("select thefile,file_ext from " + tableName + " where  plan_id=" + planId);
			if (rset.next())
			{
				String file_ext = rset.getString("file_ext");
				if (file_ext != null && !"".equals(file_ext)) {
                    isBrowse = "1";
                }
			}
			if (rset != null) {
                rset.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return isBrowse;

	}

	/** 模板类型 */
	public String getTemplateType(String template_id)
	{
		String status = "0";// 分值
		if (template_id.length() == 0) {
            return status;
        }
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select status from per_template where template_id='" + template_id + "'";
			RowSet rset = dao.search(sql);
			if (rset.next()) {
                status = rset.getString(1);
            }
			status = status == null ? "0" : status;
			if (rset != null) {
                rset.close();
            }
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return status;
	}

	/** 得到计划的主体类别 */
	public String getBodyTypeIds(String planid)
	{
		String bodyTypeIds = "";
		if (planid.length() == 0) {
            return bodyTypeIds;
        }
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer strsql = new StringBuffer();
			strsql.append("select body_id from per_plan_body where plan_id=");
			strsql.append(planid);
			RowSet rset = dao.search(strsql.toString());
			while (rset.next()) {
                bodyTypeIds += rset.getString(1) + ",";
            }
			if (rset != null) {
                rset.close();
            }
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return bodyTypeIds;
	}
	/** 得到岗位素质模型参数 */
	public String getByModelById(String planid)
	{
		String byModel = "0";
		if (planid.length() == 0) {
            return byModel;
        }
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer strsql = new StringBuffer();
			strsql.append("select ByModel from per_plan where plan_id=");
			strsql.append(planid);
			RowSet rset = dao.search(strsql.toString());
			if (rset.next()) {
                byModel = rset.getString("ByModel");
            }
			if (rset != null) {
                rset.close();
            }
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		if(byModel!=null&&!"".equals(byModel)){
			return zeroToFalse(byModel);
		}else{
			return "False";
		}
		
	}
	public String zeroToFalse(String str)
	{
		if ("0".equals(str)) {
            return "False";
        } else if ("1".equals(str)) {
            return "True";
        } else {
            return str;
        }
	}

	/**
	 * 检查是否存在type子节点
	 * 
	 * @param parameter_content
	 * @param type
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public boolean isExists(String parameter_content, String type) throws Exception {

		boolean flag = true;

		if (parameter_content == null || "".equals(parameter_content)) {
            flag = false;
        } else
		{
			// 当parameter_content不为空时候再看有没有FineMax子节点
			Document doc = PubFunc.generateDom(parameter_content);
			String xpath = "//PerPlan_Parameter";
			XPath xpath_ = XPath.newInstance(xpath);
			Element root = (Element) xpath_.selectSingleNode(doc);
			Element child = root.getChild(type);
			if (child == null) {
                flag = false;
            } else
			{
/*				
				Element child1 = root.getChild("BadlyMax");
				List attriNames1 = child1.getAttributes();				
				Element child2 = root.getChild("FineMax");
				List attriNames2 = child2.getAttributes();
				
				if(attriNames1.size()!=attriNames2.size())
					flag = false;
*/				
				List attriNames = child.getAttributes();	
				for (int i = 0; i < attriNames.size(); i++)
				{
					Attribute attri = (Attribute) attriNames.get(i);
					if(attriNames.size()==1 && "C_whole_grade".equalsIgnoreCase(attri.getName()))
					{
						flag = false;
					}else {
                        break;
                    }
				}
								
			}
		}
		return flag;

	}

	public ArrayList notExists(String templateId) throws GeneralException
	{
		ArrayList partRestrict = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strSql = new StringBuffer();
		strSql.append("select point_id,pointname from per_point where point_id in (");
		strSql.append("select distinct point_id from per_template_point where item_id in (");
		strSql.append("select item_id from per_template_item where Template_id='");
		strSql.append(templateId);
		strSql.append("')) and pointkind='0'");

		try
		{
			HashMap partRestrictMap = new HashMap();
			RowSet frowset = dao.search(strSql.toString());
			while (frowset.next())
			{
				String point_id = frowset.getString("point_id");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("point_id", "C_" + point_id);
				bean.set("pointname", frowset.getString("pointname"));
				bean.set("value", "");
				partRestrictMap.put(point_id.toUpperCase(), bean);
				// partRestrict.add(bean);
			}

			// 给指标排序
			ArrayList tempPointList = new ArrayList();
			ArrayList seqList = new ArrayList();
			frowset = dao.search("select distinct point_id,item_id,seq from per_template_point where item_id in " + "(select item_id from per_template_item where Template_id='" + templateId + "') order by seq");
			while (frowset.next())
			{
				String[] temp = new String[11];
				temp[0] = frowset.getString(1);
				temp[3] = frowset.getString(2);
				tempPointList.add(temp);
			}
			BatchGradeBo bo = new BatchGradeBo(this.conn);
			bo.get_LeafItemList(templateId, tempPointList, seqList);
			for (int i = 0; i < seqList.size(); i++)
			{
				String pointId = (String) seqList.get(i);
				pointId = pointId.toUpperCase();
				if (partRestrictMap.get(pointId) != null) {
                    partRestrict.add(partRestrictMap.get(pointId));
                }
			}

			LazyDynaBean bean = new LazyDynaBean();
			bean.set("point_id", "C_whole_grade");
			bean.set("pointname", "总体评价");
			bean.set("value", "");
			partRestrict.add(bean);
			if (frowset != null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			throw new GeneralException("查询数据异常！");
		}

		return partRestrict;
	}

	public float getSumPointValue(ArrayList list)
	{
		float sum=0;
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String value = (String)bean.get("value");
			if(value.length()==0 || "-1".equals(value)) {
                value="0";
            }
			sum+=Float.valueOf(value).floatValue();
		}
		return sum;
	}
	
   /**   
 * @Title: isPointValueOverOne   
 * @Description: 最高、最低标度对象数，设置的指标数值是否超过1，超过1 按数值处理；小于1则是按比例控制   
 * @param @param list
 * @param @return 
 * @return float 
 * @author:wangrd   
 * @throws   
*/
	public boolean isPointValueOverOne(ArrayList list)
    {
        boolean b=true;
        
        for(int i=0;i<list.size();i++)
        {
            LazyDynaBean bean = (LazyDynaBean)list.get(i);
            String value = (String)bean.get("value");
            if(value.length()==0 || "-1".equals(value)) {
                value="0";
            }
            
            if ((Float.parseFloat(value)>0) && (Float.parseFloat(value)<1)){
              b=false;
              break;
            }
        }
        return b;
    }

	
	public ArrayList getRestrictList(String parameter_content, String type) throws GeneralException, JDOMException, IOException
	{

		ArrayList partRestrict = new ArrayList();
		Document doc = null;
		try {
			doc = PubFunc.generateDom(parameter_content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String xpath = "//PerPlan_Parameter";
		XPath xpath_ = XPath.newInstance(xpath);
		Element root = (Element) xpath_.selectSingleNode(doc);
		Element child = root.getChild(type);
		List attriNames = child.getAttributes();
		boolean C_whole_grade = false;
		for (int i = 0; i < attriNames.size(); i++)
		{
			Attribute attri = (Attribute) attriNames.get(i);
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("point_id", attri.getName());
			bean.set("pointname", this.getCnName(attri.getName()));
			bean.set("value", attri.getValue());
			partRestrict.add(bean);
			if ("C_whole_grade".equals(attri.getName())) {
                C_whole_grade = true;
            }
		}
		// 如果不存在总体评价的指标就再自动添加一条,再前台根据用户只否选择是否显示总体评价来显示
		if (C_whole_grade == false)
		{
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("point_id", "C_whole_grade");
			bean.set("pointname", "总体评价");
			bean.set("value", "");
			partRestrict.add(bean);
		}
		return partRestrict;
	}

	public ArrayList getRestrictList(String parameter_content, String type, String templateId) throws Exception
	{

		ArrayList partRestrict = new ArrayList();

		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strSql = new StringBuffer();
		strSql.append("select point_id,pointname from per_point where point_id in (");
		strSql.append("select distinct point_id from per_template_point where item_id in (");
		strSql.append("select item_id from per_template_item where Template_id='");
		strSql.append(templateId);
		strSql.append("')) and pointkind='0'");
		HashMap partRestrictMap_b = new HashMap();
		RowSet frowset = dao.search(strSql.toString());
		while (frowset.next())
		{
			String point_id = frowset.getString("point_id");
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("point_id", "C_" + point_id);
			bean.set("pointname", frowset.getString("pointname"));
			bean.set("value", "");
			partRestrictMap_b.put(point_id.toUpperCase(), bean);
		}
		Document doc = PubFunc.generateDom(parameter_content);
		String xpath = "//PerPlan_Parameter";
		XPath xpath_ = XPath.newInstance(xpath);
		Element root = (Element) xpath_.selectSingleNode(doc);
		Element child = root.getChild(type);
		List attriNames = child.getAttributes();
		boolean C_whole_grade = false;

		LazyDynaBean wholeGradeBean = new LazyDynaBean();
		HashMap partRestrictMap = new HashMap();
		for (int i = 0; i < attriNames.size(); i++)
		{
			Attribute attri = (Attribute) attriNames.get(i);
			if ("C_whole_grade".equals(attri.getName()))
			{
				C_whole_grade = true;
				wholeGradeBean.set("point_id", attri.getName());
				wholeGradeBean.set("pointname", this.getCnName(attri.getName()));
				wholeGradeBean.set("value", attri.getValue());
			} else
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("point_id", attri.getName());
				bean.set("pointname", this.getCnName(attri.getName()));
				bean.set("value", attri.getValue());
				// partRestrict.add(bean);
				String pointid = attri.getName().substring(2).toUpperCase();
				partRestrictMap.put(pointid, bean);
			}
		}

		// 给指标排序
		ArrayList tempPointList = new ArrayList();
		ArrayList seqList = new ArrayList();		
		frowset = dao.search("select distinct point_id,item_id,seq from per_template_point where item_id in " + "(select item_id from per_template_item where Template_id='" + templateId + "') order by seq");
		while (frowset.next())
		{
			String[] temp = new String[11];
			temp[0] = frowset.getString(1);
			temp[3] = frowset.getString(2);
			tempPointList.add(temp);
		}
		if(frowset!=null) {
            frowset.close();
        }
		BatchGradeBo bo = new BatchGradeBo(this.conn);
		bo.get_LeafItemList(templateId, tempPointList, seqList);
		for (int i = 0; i < seqList.size(); i++)
		{
			String pointId = (String) seqList.get(i);
			pointId = pointId.toUpperCase();
			if (partRestrictMap.get(pointId) != null) {
                partRestrict.add(partRestrictMap.get(pointId));
            } else if (partRestrictMap_b.get(pointId) != null) {
                partRestrict.add(partRestrictMap_b.get(pointId));
            }
		}

		// 如果不存在总体评价的指标就再自动添加一条,再前台根据用户只否选择是否显示总体评价来显示
		if (C_whole_grade == false)
		{
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("point_id", "C_whole_grade");
			bean.set("pointname", "总体评价");
			bean.set("value", "");
			partRestrict.add(bean);
		}else {
            partRestrict.add(wholeGradeBean);
        }
		return partRestrict;
	}

	public String getCnName(String code)
	{

		String name = "";
		String codeTemp = code.substring(2, code.length());
		if ("whole_grade".equals(codeTemp))
		{
			name = "总体评价";
			return name;
		}
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet frowset = dao.search("select pointname from per_point where point_id='" + codeTemp + "'");
			if (frowset.next()) {
                name = frowset.getString("pointname");
            }
			if (frowset != null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return name;
	}

	public String getParameter_content(String planId) throws GeneralException
	{

		String parameter_content = "";
		ContentDAO dao = new ContentDAO(this.conn);
		if (planId == null || planId.length() == 0) {
            return parameter_content;
        }
		try
		{
			RowSet frowset = dao.search("select parameter_content from per_plan where plan_id=" + planId);
			if (frowset.next()) {
                parameter_content = frowset.getString("parameter_content");
            }
			if (frowset != null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw new GeneralException("查询数据异常！");
		}

		return parameter_content;
	}

	/**
	 * 上传参数指标说明文件
	 * 
	 * @throws GeneralException
	 */
	public void saveThefile(String planId, FormFile form_file, UserView u) throws GeneralException
	{
		String fname = form_file.getFileName();
		int indexInt = fname.lastIndexOf(".");
		String ext = fname.substring(indexInt, fname.length());
		ContentDAO dao = new ContentDAO(this.conn);
		java.io.InputStream fis = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement prestmt = null;
		try
		{
			if (this.isExist(planId))
			{
				RecordVo vo = new RecordVo("per_plan");
				vo.setString("plan_id", planId);
				if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				{
					RecordVo updatevo = dao.findByPrimaryKey(vo);
					Blob blob = getOracleBlob(form_file, "per_plan", planId);
					updatevo.setObject("thefile", blob);
					updatevo.setString("file_ext", ext);
					dao.updateValueObject(updatevo);
				}
				if (Sql_switcher.searchDbServer() != Constant.ORACEL)
				{
					fis = form_file.getInputStream();
					String sql = "update per_plan set thefile=?,file_ext=? where plan_id=?";
					prestmt = conn.prepareStatement(sql);
					prestmt.setBinaryStream(1, fis, form_file.getFileSize());
					prestmt.setString(2, ext);
					prestmt.setInt(3, Integer.parseInt(planId));
					// 打开Wallet
					dbS.open(conn, sql);
					prestmt.executeUpdate();
				}
			} else
			// 新增情况 还没保存过计划 就将文件二进制流先保存在临时表中
			{
				String tempTable ="t#"+u.getUserId()+"_per_file"; // "per_plan_file_" + planId + "_" + u.getUserId();
				DbWizard dbWizard = new DbWizard(this.conn);
				DBMetaModel dbmodel = new DBMetaModel(this.conn);
				if (dbWizard.isExistTable(tempTable, false))
				{
					dbWizard.dropTable(tempTable);
				}
				
				{

					Table table = new Table(tempTable);
					Field obj = new Field("plan_id");
					obj.setDatatype(DataType.INT);
					obj.setNullable(false);
					obj.setKeyable(true);
					table.addField(obj);

					obj = new Field("thefile");
					obj.setDatatype(DataType.BLOB);
					obj.setKeyable(false);
					table.addField(obj);

					obj = new Field("file_ext");
					obj.setDatatype(DataType.STRING);
					obj.setLength(10);
					obj.setKeyable(false);
					table.addField(obj);
					dbWizard.createTable(table);
					dbmodel.reloadTableModel(tempTable);
				} 
				//else
				//	dao.delete("delete  from " + tempTable, new ArrayList());
				if (Sql_switcher.searchDbServer() != Constant.ORACEL)
				{
					fis = form_file.getInputStream();
					String sql = "insert into " + tempTable + "(thefile,file_ext,plan_id) values(?,?,?)";
					prestmt = conn.prepareStatement(sql);
					prestmt.setBinaryStream(1, fis, form_file.getFileSize());
					prestmt.setString(2, ext);
					prestmt.setInt(3, Integer.parseInt(planId));
					// 打开Wallet
					dbS.open(conn, sql);
					prestmt.executeUpdate();
				} else
				{
					String sql = "insert into " + tempTable + "(file_ext,plan_id) values(?,?)";
					prestmt = conn.prepareStatement(sql);
					prestmt.setString(1, ext);
					prestmt.setInt(2, Integer.parseInt(planId));
					// 打开Wallet
					dbS.open(conn, sql);
					prestmt.executeUpdate();

					RecordVo vo = new RecordVo(tempTable);
					vo.setInt("plan_id", Integer.parseInt(planId));
					RecordVo updatevo = dao.findByPrimaryKey(vo);
					Blob blob = getOracleBlob(form_file, tempTable, planId);
					updatevo.setObject("thefile", blob);
					dao.updateValueObject(updatevo);

				}

			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(fis); //关闭资源 guodd 2014-12-29
			PubFunc.closeIoResource(prestmt);
			try {
				// 关闭Wallet
				dbS.close(this.conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

    private Blob getOracleBlob(FormFile file, String tablename, String planId) throws FileNotFoundException, IOException {
        InputStream in = null;
        Blob blob = null;
        try {
            StringBuffer strSearch = new StringBuffer();
            strSearch.append("select thefile from ");
            strSearch.append(tablename);
            strSearch.append(" where plan_id=");
            strSearch.append(planId);
            strSearch.append(" FOR UPDATE");

            StringBuffer strInsert = new StringBuffer();
            strInsert.append("update  ");
            strInsert.append(tablename);
            strInsert.append(" set thefile=EMPTY_BLOB() where plan_id=");
            strInsert.append(planId);

            OracleBlobUtils blobutils = new OracleBlobUtils(this.conn);
            in = file.getInputStream();
            blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(), in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                PubFunc.closeResource(in);
            }
        }
        return blob;
    }

	public String getKhObjName(String object_id, String plan_id) throws GeneralException
	{
		String objectname = "";
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select a0101 from per_object where plan_id=" + plan_id + " and object_id='" + object_id + "'";
		try
		{
			RowSet frowset = dao.search(sql.toString());
			if (frowset.next()) {
                objectname = frowset.getString(1);
            }
			if (frowset != null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return objectname;
	}

	/**
	 * 根据用户权限获得计划列表
	 * 1、业务用户：先取业务操作单位->操作单位->管理范围
     * 2、自助用户：先取关联的业务用户的（业务操作单位->操作单位）->自身的业务操作单位->管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.trim().length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    }
				}
				if(tempSql!=null && tempSql.toString().trim().length()>0) {
                    buf.append(" and ( " + tempSql.substring(3) + " ) ");
                }
			} 
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg)))
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else {
                        buf.append(" and b0110 like '" + codevalue + "%'");
                    }
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}

		return str;
	}

	/** 根据登录用户的权限(操作单位加管理范围,操作单位优先的原则)和计划的适用范围来控制计划的显示 受模板权限控制方法 */
	public HashMap getPlansByUserView(UserView userView, String otherWhl)
	{
		HashMap map = new HashMap();
		try
		{
			String controlByKHMoudle = getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			// 第一部分公共资源计划不授控制可以显示
			String sql = "select plan_id,template_id from per_plan where B0110 = 'HJSJ' " + otherWhl + " ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search(sql);
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");
				
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rowSet.getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
				
				map.put(plan_id, "");
			}
			// 第二部分非公共资源计划 适用范围为默认的计划
			sql = "select plan_id,b0110,template_id from per_plan where B0110 is not null and B0110 != 'HJSJ'  " + otherWhl + " ";
			StringBuffer buf = new StringBuffer(sql);
			buf.append(" and (plan_visibility is null or plan_visibility=0) ");
			buf.append(this.getUserViewPrivWhere(userView));
			rowSet = dao.search(buf.toString());
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");
				
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rowSet.getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
				
				map.put(plan_id, "");
			}

			// 第三部分非公共资源计划 适用范围=下级组织单元可用的计划
			buf = new StringBuffer(sql);
			buf.append(" and plan_visibility=1  ");
			// 得到管理范围
			String a_codeid = userView.getManagePrivCode();
			String a_codevalue = userView.getManagePrivCodeValue();
			String manageCode = a_codeid + a_codevalue;
			// 操作单位
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			rowSet = dao.search(buf.toString());
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");
				String b0110 = rowSet.getString("b0110");
				
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rowSet.getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
				
				if (operOrg.length() > 2)
				{
					if ("UN`".equalsIgnoreCase(operOrg))// 授权了全部
					{

					} else
					{
						boolean isHavaUpperOrg = false;// 存在上级操作单位
						boolean isHavaLowerOrg = false;// 存在下级操作单位
						String[] operOrgs = operOrg.split("`");
						for (int i = 0; i < operOrgs.length; i++)
						{
							if ("UN".equalsIgnoreCase(operOrgs[i].substring(0, 2)) || "UM".equalsIgnoreCase(operOrgs[i].substring(0, 2)))
							{
								String operOrgCode = operOrgs[i].substring(2);
								if (b0110.length() < operOrgCode.length())
								{
									if (operOrgCode.substring(0, b0110.length()).equalsIgnoreCase(b0110))// 操作单位是其下级
									{
										isHavaLowerOrg = true;
									} else
										// 不属于上下级关系
                                    {
                                        continue;
                                    }
								} else if (b0110.length() > operOrgCode.length())
								{
									if (b0110.substring(0, operOrgCode.length()).equalsIgnoreCase(operOrgCode))// 操作单位是其上级
									{
										isHavaUpperOrg = true;
									} else
										// 不属于上下级关系
                                    {
                                        continue;
                                    }
								} else if (b0110.length() == operOrgCode.length())
								{
									if (!b0110.equalsIgnoreCase(operOrgCode))// 不属于上下级关系
                                    {
                                        continue;
                                    } else
									{// 相同的情况
										isHavaUpperOrg = true;
										isHavaUpperOrg = true;
									}
								}
							}
						}

						if (isHavaUpperOrg == false && isHavaLowerOrg == true)// 只是设置了下级操作单位
						{

						} else if (isHavaUpperOrg == false && isHavaLowerOrg == false)// 不属于上下级关系
                        {
                            continue;
                        } else if (isHavaUpperOrg == true && isHavaUpperOrg == false)
						{

						} else if (isHavaUpperOrg == true && isHavaLowerOrg == true)
						{

						}
					}
				} else
				{
					if (manageCode.trim().length() > 0)// 说明授权了
					{
						if ("UN".equalsIgnoreCase(manageCode))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串 范围最大
						{

						} else
						{
							if (b0110.length() < a_codevalue.length())
							{
								if (a_codevalue.substring(0, b0110.length()).equalsIgnoreCase(b0110))// 管理范围是其下级
								{

								} else
									// 不属于上下级关系
                                {
                                    continue;
                                }
							} else if (b0110.length() > a_codevalue.length())
							{
								if (b0110.substring(0, a_codevalue.length()).equalsIgnoreCase(a_codevalue))// 管理范围是其上级
								{

								} else
									// 不属于上下级关系
                                {
                                    continue;
                                }
							} else if (b0110.length() == a_codevalue.length())
							{
								if (!b0110.equalsIgnoreCase(a_codevalue))// 不属于上下级关系
                                {
                                    continue;
                                }
							}
						}
					} else {
                        continue;
                    }
				}
				map.put(plan_id, "");
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/** 根据登录用户的权限(操作单位加管理范围,操作单位优先的原则)和计划的适用范围来控制计划的显示 不受模板权限控制方法 */
	public HashMap getPlansByUserViewNoTemp(UserView userView, String otherWhl)
	{
		HashMap map = new HashMap();
		try
		{
			// 第一部分公共资源计划不授控制可以显示
			String sql = "select plan_id,template_id from per_plan where B0110 = 'HJSJ' " + otherWhl + " ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search(sql);
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");												
				map.put(plan_id, "");
			}
			// 第二部分非公共资源计划 适用范围为默认的计划
			sql = "select plan_id,b0110,template_id from per_plan where B0110 is not null and B0110 != 'HJSJ'  " + otherWhl + " ";
			StringBuffer buf = new StringBuffer(sql);
			buf.append(" and (plan_visibility is null or plan_visibility=0) ");
			buf.append(this.getUserViewPrivWhere(userView));
			rowSet = dao.search(buf.toString());
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");
				map.put(plan_id, "");
			}

			// 第三部分非公共资源计划 适用范围=下级组织单元可用的计划
			buf = new StringBuffer(sql);
			buf.append(" and plan_visibility=1  ");
			// 得到管理范围
			String a_codeid = userView.getManagePrivCode();
			String a_codevalue = userView.getManagePrivCodeValue();
			String manageCode = a_codeid + a_codevalue;
			// 操作单位
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			rowSet = dao.search(buf.toString());
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");
				String b0110 = rowSet.getString("b0110");
				
				if (operOrg.length() > 2)
				{
					if ("UN`".equalsIgnoreCase(operOrg))// 授权了全部
					{

					} else
					{
						boolean isHavaUpperOrg = false;// 存在上级操作单位
						boolean isHavaLowerOrg = false;// 存在下级操作单位
						String[] operOrgs = operOrg.split("`");
						for (int i = 0; i < operOrgs.length; i++)
						{
							if ("UN".equalsIgnoreCase(operOrgs[i].substring(0, 2)) || "UM".equalsIgnoreCase(operOrgs[i].substring(0, 2)))
							{
								String operOrgCode = operOrgs[i].substring(2);
								if (b0110.length() < operOrgCode.length())
								{
									if (operOrgCode.substring(0, b0110.length()).equalsIgnoreCase(b0110))// 操作单位是其下级
									{
										isHavaLowerOrg = true;
									} else
										// 不属于上下级关系
                                    {
                                        continue;
                                    }
								} else if (b0110.length() > operOrgCode.length())
								{
									if (b0110.substring(0, operOrgCode.length()).equalsIgnoreCase(operOrgCode))// 操作单位是其上级
									{
										isHavaUpperOrg = true;
									} else
										// 不属于上下级关系
                                    {
                                        continue;
                                    }
								} else if (b0110.length() == operOrgCode.length())
								{
									if (!b0110.equalsIgnoreCase(operOrgCode))// 不属于上下级关系
                                    {
                                        continue;
                                    } else
									{// 相同的情况
										isHavaUpperOrg = true;
										isHavaUpperOrg = true;
									}
								}
							}
						}

						if (isHavaUpperOrg == false && isHavaLowerOrg == true)// 只是设置了下级操作单位
						{

						} else if (isHavaUpperOrg == false && isHavaLowerOrg == false)// 不属于上下级关系
                        {
                            continue;
                        } else if (isHavaUpperOrg == true && isHavaUpperOrg == false)
						{

						} else if (isHavaUpperOrg == true && isHavaLowerOrg == true)
						{

						}
					}
				} else
				{
					if (manageCode.trim().length() > 0)// 说明授权了
					{
						if ("UN".equalsIgnoreCase(manageCode))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串 范围最大
						{

						} else
						{
							if (b0110.length() < a_codevalue.length())
							{
								if (a_codevalue.substring(0, b0110.length()).equalsIgnoreCase(b0110))// 管理范围是其下级
								{

								} else
									// 不属于上下级关系
                                {
                                    continue;
                                }
							} else if (b0110.length() > a_codevalue.length())
							{
								if (b0110.substring(0, a_codevalue.length()).equalsIgnoreCase(a_codevalue))// 管理范围是其上级
								{

								} else
									// 不属于上下级关系
                                {
                                    continue;
                                }
							} else if (b0110.length() == a_codevalue.length())
							{
								if (!b0110.equalsIgnoreCase(a_codevalue))// 不属于上下级关系
                                {
                                    continue;
                                }
							}
						}
					} else {
                        continue;
                    }
				}
				map.put(plan_id, "");
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/** 根据登录用户的权限(操作单位加管理范围,操作单位优先的原则)和计划的适用范围来控制计划的显示    2011.04.02 JinChunhai   代码暂时保留  */
	public HashMap getPlansUserView(UserView userView, String otherWhl)
	{
		HashMap map = new HashMap();
		try
		{
			String controlByKHMoudle = getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			// 第一部分公共资源计划不授控制可以显示
			String sql = "select plan_id,template_id from per_plan where B0110 = 'HJSJ' " + otherWhl + " ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search(sql);
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");
				
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rowSet.getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
								
				map.put(plan_id, "");
			}
			// 第二部分非公共资源计划 适用范围为默认的计划
			sql = "select plan_id,b0110,template_id from per_plan where B0110 is not null and B0110 != 'HJSJ'  " + otherWhl + " ";
			StringBuffer buf = new StringBuffer(sql);
	//		buf.append(" and (plan_visibility is null or plan_visibility=0) ");
			buf.append(this.getUserViewPrivWhere(userView));
			rowSet = dao.search(buf.toString());
			while (rowSet.next())
			{
				String plan_id = rowSet.getString("plan_id");
				
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rowSet.getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
				
				map.put(plan_id, "");
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/** 创建指标权限表 */
	public void cper_pointpriv(String planId) throws GeneralException
	{
		DbWizard dbWizard = new DbWizard(this.conn);
		Table table = new Table("PER_POINTPRIV_" + planId);
		Field obj = new Field("id");
		obj.setDatatype(DataType.INT);
		obj.setNullable(false);
		obj.setKeyable(true);
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
		obj = new Field("object_id");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);
		table.addField(obj);
		obj = new Field("mainbody_id");
		obj.setDatatype(DataType.STRING);
		obj.setLength(10);
		obj.setKeyable(false);
		table.addField(obj);
		obj = new Field("bodyname");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);
		table.addField(obj);

		ArrayList list = this.getC_x(planId);
		for (int i = 0; i < list.size(); i++)
		{
			String c_x = (String) list.get(i);
			obj = new Field(c_x);
			obj.setDatatype(DataType.INT);
			obj.setKeyable(false);
			table.addField(obj);
		}
		dbWizard.createTable(table);
	}

	public ArrayList getC_x(String planId) throws GeneralException
	{

		BatchGradeBo bo = new BatchGradeBo(this.conn, planId);

		ArrayList list = new ArrayList();
		// 解决排列顺序问题
		ArrayList seqList = new ArrayList();
		ArrayList tempPointList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String templateID = "";
			RowSet frowset = dao.search("select template_id from per_plan where plan_id=" + planId);
			if (frowset.next()) {
                templateID = frowset.getString(1);
            }

			frowset = dao.search("select distinct point_id,item_id from  per_template_point where item_id in " + "(select item_id from per_template_item where Template_id="
					+ "(select template_id from per_plan where plan_id=" + planId + "))");
			while (frowset.next())
			{
				String[] temp = new String[11];
				temp[0] = frowset.getString(1);
				temp[3] = frowset.getString(2);
				tempPointList.add(temp);
			}
			bo.get_LeafItemList(templateID, tempPointList, seqList);
			for (int i = 0; i < seqList.size(); i++)
			{
				String pointId = (String) seqList.get(i);
				list.add("C_" + pointId.toUpperCase());
			}

			if (frowset != null) {
                frowset.close();
            }

		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	public String isNull(String str)
	{

		if (str == null) {
            str = "";
        }
		return str;
	}

	/**
	 * 按照默认方式更新指标权限
	 * 
	 * @param newId
	 * @param planId
	 * @param body_id
	 * @throws GeneralException
	 */
	public void copyKhMainbodyPri2(String newId, String planId, String body_id) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select a.* from per_pointpriv_" + planId);
		strsql.append(" a,per_mainbody b where b.object_id = a.object_id and a.mainbody_id=b.mainbody_id");
		strsql.append(" and b.plan_id=" + planId);
		if (body_id.trim().length() > 0) {
            strsql.append(" and b.body_id=" + body_id.trim());
        }

		ArrayList c_x = this.getC_x(planId);
		if (c_x.size() == 0) {
            return;
        }
		try
		{
			RowSet frowset = dao.search(strsql.toString());
			while (frowset.next())
			{
				StringBuffer c_xStr = new StringBuffer();
				ArrayList valueList = new ArrayList();
				for (int i = 0; i < c_x.size(); i++)
				{
					String col = (String) c_x.get(i);
					c_xStr.append(col + "=1,");
				}
				valueList.add(isNull(frowset.getString("object_id")));
				valueList.add(isNull(frowset.getString("mainbody_id")));

				strsql = new StringBuffer();
				strsql.append("update per_pointpriv_" + newId);
				strsql.append(" set " + c_xStr);
				strsql = new StringBuffer(strsql.substring(0, strsql.length() - 1));
				strsql.append(" where object_id=? and mainbody_id=?");
				dao.update(strsql.toString(), valueList);
			}
			if (frowset != null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	public void synObjectAndBody(String newId, String body_id) throws GeneralException
	{

		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT distinct B.id, B.b0110, B.e0122, B.e01a1, B.object_id, B.mainbody_id, B.a0101 ");
		strsql.append("FROM per_object O INNER JOIN per_mainbody B ON O.object_id = B.object_id ");
		strsql.append("and B.plan_id=" + newId + " and O.plan_id=" + newId);
		if (body_id.trim().length() > 0) {
            strsql.append(" and B.body_id=" + body_id);
        }

		try
		{
			RowSet frowset = dao.search(strsql.toString());
			ArrayList valueList = null;
			while (frowset.next())
			{
				valueList = new ArrayList();
				valueList.add(isNull(frowset.getString("id")));
				valueList.add(isNull(frowset.getString("b0110")));
				valueList.add(isNull(frowset.getString("e0122")));
				valueList.add(isNull(frowset.getString("e01a1")));
				valueList.add(isNull(frowset.getString("object_id")));
				valueList.add(isNull(frowset.getString("mainbody_id")));
				valueList.add(isNull(frowset.getString("a0101")));

				strsql = new StringBuffer();
				strsql.append("insert into per_pointpriv_" + newId);
				strsql.append("(id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname)");
				strsql.append(" values (?,?,?,?,?,?,?)");

				if (valueList.size() > 0) {
                    dao.insert(strsql.toString(), valueList);
                }
			}
			if (frowset != null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw new GeneralException("查询数据异常！");
		}
	}

	public void setKhMainbodyDefaultPri(String planId) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_mainbody where plan_id=" + planId);

		ArrayList c_x = this.getC_x(planId);
		if (c_x.size() == 0) {
            return;
        }
		try
		{
			RowSet frowset = dao.search(strsql.toString());
			while (frowset.next())
			{
				StringBuffer c_xStr = new StringBuffer();
				ArrayList valueList = new ArrayList();
				for (int i = 0; i < c_x.size(); i++)
				{
					String col = (String) c_x.get(i);
					c_xStr.append(col + "=1,");
				}
				valueList.add(isNull(frowset.getString("object_id")));
				valueList.add(isNull(frowset.getString("mainbody_id")));

				strsql = new StringBuffer();
				strsql.append("update per_pointpriv_" + planId);
				strsql.append(" set " + c_xStr);
				strsql = new StringBuffer(strsql.substring(0, strsql.length() - 1));
				strsql.append(" where object_id=? and mainbody_id=?");
				dao.update(strsql.toString(), valueList);
			}

			if (frowset != null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/** 同步本人考核主体类别 */
	public void synchronizeSelPerson(String planid) throws GeneralException
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append("delete from per_mainbody");
			buf.append(" where body_id not in (select body_id from per_plan_body where plan_id=" + planid + ") and plan_id=" + planid);
			dao.delete(buf.toString(), new ArrayList());
			DbWizard dbWizard = new DbWizard(this.conn);
			if (dbWizard.isExistTable("per_pointpriv_" + planid, false))
			{
				buf.setLength(0);
				String tableA = "PER_pointpriv_" + planid;
				buf.append("delete from " + tableA);
				buf.append("  where  not exists (select * from per_mainbody b where b.plan_id=" + planid);
				buf.append(" and  b.object_id=" + tableA + ".object_id and b.mainbody_id=" + tableA + ".mainbody_id)");
				dao.delete(buf.toString(), new ArrayList());
			}

			buf.setLength(0);
			buf.append("select * from per_plan_body where plan_id=");
			buf.append(planid);
			buf.append(" and body_id=5");

			RowSet frowset = dao.search(buf.toString());
			if (frowset.next())
			{
				buf.setLength(0);
				buf.append("select count(*) from per_mainbody where body_id=5 and plan_id=" + planid);
				frowset = dao.search(buf.toString());
				if (frowset.next()) {
                    if (frowset.getInt(1) == 0)// 考核主体表中没有本人类别 需要添加
                    {
                        this.insertSelBody(planid);
                    }
                }
			}
			if (frowset != null) {
                frowset.close();
            }

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	// 更新项目权限表
	public void updateItemPriv(String planid) throws GeneralException
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard dbWizard = new DbWizard(this.conn);
			if (!dbWizard.isExistTable("PER_ITEMPRIV_" + planid, false)) {
                return;
            }

			StringBuffer buf = new StringBuffer();
			buf.append("delete from PER_ITEMPRIV_" + planid);
			buf.append(" where body_id not in (select body_id from per_plan_body where plan_id=" + planid + ")");
			dao.delete(buf.toString(), new ArrayList());

			String sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id=" + planid + ") and child_id is null";
			buf.setLength(0);
			RowSet frowset = dao.search(sqlStr);
			while (frowset.next()) {
                buf.append("C_" + frowset.getString("item_id") + ",");
            }
			buf.append("object_id,body_id");

			String insertSql = "insert into PER_ITEMPRIV_" + planid + "(" + buf.toString() + ") ";
			StringBuffer insertBuf = new StringBuffer();
			insertBuf.append(" select ");
			String[] temp = buf.toString().split(",");
			for (int i = 0; i < temp.length - 2; i++)
			{
				if (temp[i].trim().length() > 0) {
                    insertBuf.append("1-" + Sql_switcher.isnull("b.opt", "0") + ","); // 根据打分确认标识确定权限的值（相反） by 刘蒙
                }
			}
			insertBuf.append("p.object_id,b.body_id from per_object p,per_plan_body b where p.plan_id=" + planid + " and b.plan_id=" + planid);
			insertBuf.append(" and b.body_id not in (select distinct body_id from PER_ITEMPRIV_" + planid + ")");
			insertSql += insertBuf.toString();
			dao.insert(insertSql, new ArrayList());

			if (frowset != null) {
                frowset.close();
            }
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public void saveMainBodyType(String[] bodyids, String planId) throws GeneralException
	{

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			HashMap existBodyids = new HashMap();
			String sql = "select body_id from per_plan_body where plan_id=" + planId;
			RowSet frowset = dao.search(sql);
			while (frowset.next()) {
                existBodyids.put(frowset.getString(1), "");
            }

			ArrayList addList = new ArrayList();
			ArrayList delList = new ArrayList();

			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < bodyids.length; i++)
			{
				if (bodyids[i].trim().length() > 0)
				{
					String bodyid="";
					if(bodyids[i].indexOf("/")==-1) {
                        bodyid=bodyids[i].trim();
                    } else
					{
						String[] temps=bodyids[i].split("/");
						bodyid=temps[0].trim();
					}
					
					ArrayList list = new ArrayList();					
					String object_type = this.planVo.getString("object_type");
					if(("2".equalsIgnoreCase(object_type) && "-1".equalsIgnoreCase(bodyid)) || (!"2".equalsIgnoreCase(object_type) && "5".equalsIgnoreCase(bodyid)))
					{}
					else
					{
						buf.append("," +bodyid);
						list.add(new Integer(bodyid));
					}
					if (existBodyids.get(bodyid) == null&&list.size()!=0) {
                        addList.add(list);
                    }
				}
			}

			// 先删除原来选择的现在废除的主体类别
			if (buf.length() > 0)
			{
				sql = "select body_id from per_plan_body where plan_id=" + planId + " and body_id not in (" + buf.substring(1) + ")";
				frowset = dao.search(sql);
				while (frowset.next())
				{
					ArrayList list = new ArrayList();
					list.add(new Integer(frowset.getString(1)));
					delList.add(list);
				}
				if (delList.size() > 0)
				{
					String delSql = "delete per_plan_body where plan_id=" + planId + " and body_id=?";
					dao.batchUpdate(delSql, delList);
				}
			}
			// 新增新选的主体类别
			if (addList.size() > 0)
			{
				String insertSql = "insert into per_plan_body(body_id,rank,plan_id) values (?,0.0," + planId + ")";
				dao.batchInsert(insertSql, addList);
			}

			// 批量更新计划的主体评分确认标识 by 刘蒙
			List values = new ArrayList(); // 批量更新主体评分确认标识所需的参数
			
			// 将表单数据与数据库中的记录比较，得到opt将要更新的记录
			StringBuffer srcOptSQL = new StringBuffer("SELECT t.body_id,t.opt FROM per_plan_body t WHERE ( 1<>1");
			for (int i = 0; i < bodyids.length; i++) { // [0/0//0, 1/0//0, 2/0//0, 3/0//1]
				if (bodyids[i].trim().length() > 0) {
					if(bodyids[i].indexOf("/") != -1) {
						List value = new ArrayList();
						String[] temps = bodyids[i].split("/");
						String bodyid = temps[0].trim();
						String isgrade = temps[1].trim();
						String grade_seq = temps[2] == null || "".equals(temps[2].trim()) ? null : temps[2].trim();
						String opt = temps[3];
						
						srcOptSQL.append(" OR (t.body_id = ").append(bodyid);
						srcOptSQL.append(" AND ").append(Sql_switcher.isnull("t.opt", "0")).append(" <> ").append(opt).append(")");

						value.add(isgrade);
						value.add(grade_seq);
						value.add(opt);
						value.add(bodyid);
						value.add(planId);
						values.add(value);
					}
				}
			}
			srcOptSQL.append(") AND t.plan_id = ").append(planId);
			
			DbWizard dbWizard = new DbWizard(this.conn);
			if (dbWizard.isExistTable("PER_ITEMPRIV_" + planid, false)) {
				Map srcBodyIds = new HashMap(); // opt将要改变的记录的body_id
				rs = dao.search(srcOptSQL.toString());
				while (rs.next()) {
					srcBodyIds.put(rs.getString("body_id"), new Integer(rs.getInt("opt")));
				}
				
				// 先更新指标权限表中body_id(评分确认标识)产生变动的主体记录，如果表存在的话
				if (srcBodyIds.size() != 0) { // 只有改变了opt的值，以下的操作才是有意义的
					// 得到当前计划对应的项目权限表(per_itempriv)的权限字段
					String itemPrivColumnsSQL = "SELECT * FROM PER_ITEMPRIV_" + planid + " WHERE 1=2";
					rs = dao.search(itemPrivColumnsSQL);
					ResultSetMetaData columns = rs.getMetaData(); // 项目权限列元数据
					int columnNum = columns.getColumnCount();

					for (int i = 0; i < bodyids.length; i++) { // [0/0//0, 1/0//0, 2/0//0, 3/0//1]
						if (bodyids[i].trim().length() > 0) {
							if(bodyids[i].indexOf("/") != -1) {
								String[] temps = bodyids[i].split("/");
								String body_id = temps[0].trim();
								String opt = temps[3]; // 更新后的opt
								opt = opt == null || "".equals(opt.trim()) ? "0" : opt;
								
								if (srcBodyIds.get(body_id) != null) {
									StringBuffer itemPrivUpdateSQL = new StringBuffer("UPDATE PER_ITEMPRIV_").append(planid).append(" SET ");
									for (int j = 1; j <= columnNum; j++) {
										String column = columns.getColumnName(j);
										if (column.startsWith("C_")) {
											itemPrivUpdateSQL.append(column).append("=1-").append(opt).append(",");
										}
									}
									itemPrivUpdateSQL.deleteCharAt(itemPrivUpdateSQL.length() - 1);
									itemPrivUpdateSQL.append(" WHERE body_id=").append(body_id);
									dao.update(itemPrivUpdateSQL.toString());
								}
							}
						}
					}
				}
			}
			
			StringBuffer updateSql = new StringBuffer("update per_plan_body set isgrade=?");
			updateSql.append(",grade_seq=?");
			updateSql.append(",opt=?");
			updateSql.append(" where body_id=?");
			updateSql.append(" and plan_id=?");
			dao.batchUpdate(updateSql.toString(), values);
		} catch (SQLException e) {
			throw new GeneralException("插入数据异常！");
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/** 取所有项目号 */
	public ArrayList getT_x(String planId) throws GeneralException
	{

		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet frowset = dao.search("select distinct item_id from per_template_item where Template_id=" + "(select template_id from per_plan where plan_id=" + planId + ")");
			while (frowset.next()) {
                list.add("T_" + frowset.getString("item_id"));
            }
			if(frowset!=null) {
                frowset.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	public Table cper_reslut_sql(String planId,String busitype) throws GeneralException
	{

		Table table = new Table("PER_RESULT_" + planId);
		Field obj = new Field("id");
		obj.setDatatype(DataType.INT);
		obj.setNullable(false);
		obj.setKeyable(true);
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
		obj = new Field("object_id");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);
		table.addField(obj);
		obj = new Field("A0101");
		obj.setDatatype(DataType.STRING);
		obj.setLength(100);
		obj.setKeyable(false);
		table.addField(obj);
		boolean isByModel = SingleGradeBo.getByModel(planId, this.conn);
		if(!isByModel){//如果不是按岗位素质模型测评   郭峰修改
			ArrayList list = this.getC_x(planId);
			for (int i = 0; i < list.size(); i++)
			{
				String c_x = (String) list.get(i);
				obj = new Field(c_x);
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);
			}

			list = this.getT_x(planId);
			for (int i = 0; i < list.size(); i++)
			{
				String t_x = (String) list.get(i);
				obj = new Field(t_x);
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);
			}
		}

		LoadXml parameter_content = new LoadXml(this.conn, planId);
		Hashtable params = parameter_content.getDegreeWhole();
		String wholeEval = (String) params.get("WholeEval");
		if ("true".equalsIgnoreCase(wholeEval))// 只有进行总体评价的考核计划才建本字段
		{
			String gradeClass = (String) params.get("GradeClass");
			if (gradeClass != null)
			{
				try
				{
					ContentDAO dao = new ContentDAO(this.conn);
					RowSet frowset = dao.search("select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id=" + gradeClass
							+ " order by pds.topscore desc");

					while (frowset.next())
					{
						String id = frowset.getString("id");
						obj = new Field("V_" + id);
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
					}
					if(frowset!=null) {
                        frowset.close();
                    }
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				obj = new Field("V_SUM");
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);
			}
		}
		
		String showEvalDirector = (String) params.get("ShowEvalDirector");
		if ("True".equalsIgnoreCase(showEvalDirector))// 只有选择了"负责人"指标的考核计划才建本字段
		{
			obj = new Field("director");
			obj.setDatatype(DataType.STRING);
			obj.setLength(50);
			obj.setKeyable(false);
			table.addField(obj);
		}
		
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
		obj.setLength(200);
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

		obj = new Field("Affix");
		obj.setDatatype(DataType.BLOB);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("Ext");
		obj.setDatatype(DataType.STRING);
		obj.setLength(10);
		table.addField(obj);

		obj = new Field("Ordering");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("score_adjust");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
		
		obj = new Field("Org_Grade");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
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
		/*
		 * cs的建字段语句小数点前面4位后面2位 if not dbOperator.FieldExist(tbName_Result, exS_GrpAvg) then dbOperator.FieldAdd(tbName_Result, exS_GrpAvg, 'N', 4, 2);
		 */
		// 个人系数
		obj = new Field("exX_object");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);

		// 分组最高分
		obj = new Field("exS_GrpMax");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);

		// 分组最低分
		obj = new Field("exS_GrpMin");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);

		// 组人数
		obj = new Field("ex_GrpNum");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);

		// 评估结果中的备注
		obj = new Field("evalRemark");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);

		return table;
	}
	
	/**
	 * 系统角色集合
	 * @return
	 */
    public HashMap getRoleMap()
    {
        HashMap mapRole = new HashMap();
        RowSet rs = null;			
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String strOrgSql = "select role_id,role_name from t_sys_role";
			rs = dao.search(strOrgSql);	
//			String strPre = "RL";
			String strId = "";
			String strName = "";
			while(rs.next())
			{
				strId = rs.getString("role_id");
				strName = rs.getString("role_name");
				mapRole.put(strId, strName );
//				mapRole.put( strPre+strId, strName );
			}
			
			if(rs!=null) {
                rs.close();
            }
			
		}catch (Exception sqle)
		{
			sqle.printStackTrace();
		}					
		return mapRole;
    }
    
    /** 考核计划按模板权限控制, True,False(默认) */
	public String getControlByKHMoudle()
	{
		String controlByKHMoudle = "False";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if ( rowSet.next())
		    {
				String str_value = rowSet.getString("str_value");
				if (str_value != null && str_value.trim().length()>0)
				{						
				    Document doc = PubFunc.generateDom(str_value);
				    String xpath = "//Per_Parameters";
				    XPath xpath_ = XPath.newInstance(xpath);
				    Element ele = (Element) xpath_.selectSingleNode(doc);
				    Element child;
				    if (ele != null)
				    {
						child = ele.getChild("Plan");
						if (child != null)
						{
						    controlByKHMoudle = child.getAttributeValue("ControlByKHMoudle");
						}
				    }
				}
		    }						
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return controlByKHMoudle;
	}
    //取得计划参数中的考核等级分类信息
	   
	 public ArrayList getPlanPerDegreeList(String GradeClass,String busitype)
	    {
	    	ArrayList list=new ArrayList();
	    	StringBuffer buf=new StringBuffer();
	    	list.add(new CommonData("0",""));
	    	try
	    	{	    		
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		
	    		buf.append("select * from per_degree where used=1 ");    		   		    		    		
	    		if(busitype==null || busitype.trim().length()<=0 || "0".equalsIgnoreCase(busitype)) {
                    buf.append(" and flag in(0,1,2,3) ");
                } else {
                    buf.append(" and flag in(4,5) ");
                }
	    		buf.append(" order by degree_id ");
	    		
	    		RowSet rowSet=dao.search(buf.toString());
	    		while(rowSet.next())
	    		{
	    			if(GradeClass.equalsIgnoreCase(rowSet.getString("degree_id"))) {
                        list.add(new CommonData(rowSet.getString("degree_id"),rowSet.getString("degreename")+"(当前值)"));
                    } else {
                        list.add(new CommonData(rowSet.getString("degree_id"),rowSet.getString("degreename")));
                    }
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return list;
	    }
	
	/**
	 * 切换模板时，清空必填指标参数
	 * @throws GeneralException 如果发生了文档解析错误或数据库访问错误
	 * @author 刘蒙
	 */
	public void clearRequiredField() throws GeneralException {
		Document doc = null;
		Element root = null;
		try {
			String params = planVo.getString("parameter_content");
			if (params == null || "".equals(params)) {
				return;
			}
			doc = PubFunc.generateDom(params);
			root = doc.getRootElement();
			
			Element mustFillOptions = root.getChild("MustFillOptions");
			
			if (mustFillOptions == null) { // 未设置必填指标或必填规则参数时，该元素不存在
				return;
			}
			
			List options = mustFillOptions.getChildren("MustFillOption");
			for (int i = 0, len = options.size(); i < len; i++) {
				Element option = (Element) options.get(i);
				String pointId = option.getAttributeValue("PointId");
				
				if (pointId == null || "".equals(pointId)) {
					continue;
				}
				
				option.setAttribute("PointId", "");
			}
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			planVo.setString("parameter_content", outputter.outputString(doc));
			
			ContentDAO dao = new ContentDAO(this.conn);
			dao.updateValueObject(planVo);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
     * 创建等级分类高级设置中临时表
     */
	public void createTempTable(String tableName) throws GeneralException
	{

		DbWizard dbWizard = new DbWizard(this.conn);
		if (dbWizard.isExistTable(tableName, false))
		{
		    dbWizard.dropTable(tableName);
		}
	
		Table table = new Table(tableName);
		Field obj = new Field("id");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
		
		obj = new Field("seq");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
		
		obj = new Field("value");
		obj.setDatatype(DataType.CLOB);
		obj.setKeyable(false);
		table.addField(obj);
		
		obj = new Field("num1");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
		
		dbWizard.createTable(table);
	
		// 向临时表插入数据
		this.insertTempData(tableName);
	}
	/**
     * 向临时表中插入数据
     */
	public void insertTempData(String tableName)
	{

		String extpro = this.getExtpro();
		if (extpro == null || "".equals(extpro)) {
            return;
        }
		String id = "";
		String seq = "";
		String value = "";
		StringBuffer strInsert = new StringBuffer();
		strInsert.append("insert into " + tableName + " (id,seq,value,num1)");
		strInsert.append(" values (?,?,?,?)");
		ArrayList list = new ArrayList();
		try
		{
		    Document doc = PubFunc.generateDom(extpro);
		    String xpath = "//descriptive_evaluate";
		    XPath xpath_ = XPath.newInstance(xpath);
   	    
		    Element ele = (Element) xpath_.selectSingleNode(doc);		    
		    
		    if (ele != null)
		    {
				List list1 = (List) ele.getChildren("option");
				for (int i = 0; i < list1.size(); i++)
				{
				    ArrayList list2 = new ArrayList();
				    Element temp = (Element) list1.get(i);
				    id = temp.getAttributeValue("id");
				    seq = temp.getAttributeValue("seq");
				    value = temp.getText();
				   
				    list2.add(id);
				    list2.add(seq);
				    list2.add(value);
				    list2.add(seq);
				    	
				    list.add(list2);
				}
				ContentDAO dao = new ContentDAO(this.conn);
				String strSql = strInsert.toString();
				dao.batchInsert(strSql, list);
		    }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	}
   
	/**
     * 取得设置的描述性评议项xml内容
     */
	public String getExtpro()
	{
		RowSet rs = null;
		String extpro = "";
		StringBuffer strsql = new StringBuffer();		
		strsql.append("select parameter_content from per_plan where plan_id=");
		strsql.append(this.planid);
						
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search(strsql.toString());
		    if (rs.next())
		    {
				String temp = rs.getString(1);
				if (extpro != null) {
                    extpro = temp;
                }
		    }
		    if(rs!=null) {
                rs.close();
            }
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return extpro;
	}
    
	/**
     * 从临时表中取出数据
     */
	public ArrayList getTempData(String tableName)
	{

		ArrayList list = new ArrayList();
		StringBuffer selSql = new StringBuffer();
		selSql.append("select id,seq,value,num1 from " + tableName);
		selSql.append(" order by seq");
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    RowSet rs = dao.search(selSql.toString());
		    int count = 1;
		    while (rs.next())
		    {
				LazyDynaBean abean = new LazyDynaBean();
				String id = rs.getString("id");
				String seq = rs.getString("seq");
				String num = rs.getString("num1");
				String value = rs.getString("value")==null?"":rs.getString("value");
				
				abean.set("id", id);
				abean.set("seq", seq);
				abean.set("value", value);
				abean.set("num", num);
				abean.set("count", String.valueOf(count));
				abean.set("plan_id", this.planid);
				
				count++;
				list.add(abean);
		    }
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
   	}
	
	/**
     * 保存描述性评议项设置
     */
	public void saveHighSet(String tableName)
	{
		ArrayList list = this.getTempData(tableName);
		LoadXml loadxml = new LoadXml(this.conn, this.planid);						
		loadxml.saveAppraiseValue(tableName,list);
					
	//	DbWizard dbWizard = new DbWizard(this.conn);
	//	if (dbWizard.isExistTable(tableName, false))
		{
	//	    dbWizard.dropTable(tableName);
		}
		
   	}
	
	public void moveRecord(String tableName,String num1,String move) throws GeneralException
    {
    	HashMap map = new HashMap();
    	HashMap map2 = new HashMap();
    	int count = 0;
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    StringBuffer sql = new StringBuffer("");
    	    sql.append("select num1,seq from "+ tableName +" order by seq");

    	    RowSet rowSet = dao.search(sql.toString());
    	    while (rowSet.next())
    	    {    	
    	    	String num=rowSet.getString("num1");
    	    	String seq=rowSet.getString("seq");
    	    	if(seq==null) {
                    continue;
                }
    	    	
    	    	count++;
    	    	map.put("num"+num, Integer.toString(count));
    	    	map.put(Integer.toString(count), seq); 
    	    	map2.put("seq"+seq, "num"+num);
    	    }
    	    String num1Index  =(String)map.get("num"+num1);
    	    String num1_seq=(String)map.get(num1Index);
    	    if(num1Index==null || num1_seq==null) {
                throw new GeneralException("排序字段为空值,无法重新排序！");
            }
    	    if("up".equalsIgnoreCase(move) && "1".equals(num1Index)) {
                throw new GeneralException("已经是第一条记录,不允许上移！");
            }
    	    if("down".equalsIgnoreCase(move) && Integer.parseInt(num1Index)==count) {
                throw new GeneralException("已经是最后一条记录,不允许下移！");
            }
    	    
    	    String objid2="";//用于交换的对象
    	    String objid2_seq="";
    	    
    	    if("up".equalsIgnoreCase(move)) {
                objid2_seq = (String)map.get(Integer.toString(Integer.parseInt(num1Index)-1));
            } else  if("down".equalsIgnoreCase(move)) {
                objid2_seq = (String)map.get(Integer.toString(Integer.parseInt(num1Index)+1));
            }
    	  
    	    objid2 =  ((String)map2.get("seq"+objid2_seq)).substring(3);
    	    //相邻对象交换排序字段 a0000
    	    dao.update("update "+ tableName +" set seq="+objid2_seq+" where num1='"+num1+"' ");
    	    dao.update("update "+ tableName +" set seq="+num1_seq+" where num1='"+objid2+"' ");    	    
   	    
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
	
	public void deleteTemp(String tableName) throws GeneralException
    {
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    
    	    DbWizard dbWizard = new DbWizard(this.conn);
    		if (dbWizard.isExistTable(tableName, false))
    		{
    			String strSql = "delete from "+tableName; 
        	    dao.delete(strSql, new ArrayList()); 
    		}
    	      	       	    
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
	
}
