package com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * <p>Title:KpiOriginalDataBo.java</p>
 * <p>Description:KPI原始数据录入</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:23:41</p>
 * @author JinChunhai
 * @version 5.0
 */

public class KpiOriginalDataBo 
{

	private Connection con=null;
	private UserView userview=null;
	private HashMap keyMap = new HashMap();
	
	public KpiOriginalDataBo(Connection a_con,UserView userView)
	{
		this.con=a_con;
		this.userview=userView;
	}
	
	/**	
	 * 新建KPI指标表
	 */
	public void builtKpiTargetTable()
	{		
		try
		{

			String tablename = "per_kpi_item";			
			DbWizard dbWizard = new DbWizard(this.con);			
			if(!dbWizard.isExistTable(tablename,false))
			{
				Table table = new Table(tablename);
		    	table.addField(getField("id", "I", 4, true));
		    	table.addField(getField("item_id", "A", 30, false));
		    	table.addField(getField("itemdesc", "A", 100, false));		    	
		    	table.addField(getField("description", "M", 300, false));		    			    	
		    	table.addField(getField("cycle", "I", 4, false));			    	
		    	table.addField(getField("item_type_desc", "A", 200, false));
		    	table.addField(getField("start_date", "D", 8, false));
		    	table.addField(getField("end_date", "D", 8, false));
		    	table.addField(getField("seq", "I", 4, false));			    	
		    	table.addField(getField("b0110", "M", 250, false));
		    	dbWizard.createTable(table);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	/**	
	 * 新建业绩数据采集表
	 */
	public void builtAchievementTable()
	{		
		try
		{

			String tablename = "per_kpi_data";			
			DbWizard dbWizard = new DbWizard(this.con);			
			if(!dbWizard.isExistTable(tablename,false))
			{
				Table table = new Table(tablename);
		    	table.addField(getField("id", "I", 4, true));
		    	table.addField(getField("object_id", "A", 50, false));
		    	table.addField(getField("object_type", "I", 4, false));		    	
		    	table.addField(getField("item_id", "A", 30, false));
		    	table.addField(getField("actual_value", "F", 8, false));		    	
		    	table.addField(getField("cycle", "I", 4, false));		    	
		    	table.addField(getField("theyear", "A", 4, false));
		    	table.addField(getField("themonth", "A", 2, false));		    	
		    	table.addField(getField("thequarter", "A", 2, false));
		    	table.addField(getField("status", "A", 2, false));			    	
		    	table.addField(getField("modUserName", "A", 30, false));	    	
		    	table.addField(getField("modTime", "D", 8, false));
		    	dbWizard.createTable(table);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 取得KPI原始数据信息
	 * return
	 */
    public ArrayList searchKpiOriginalData(String cycle,String objectType,String year,String noYearCycle,String checkName,String a_code) throws GeneralException
    {
    	DecimalFormat myformat = new DecimalFormat("##########.#####");
		ArrayList list = new ArrayList();
		ArrayList arlist = new ArrayList();
		HashMap Map = new HashMap();
		RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.con);										
			
			// 查询业绩数据录入数据		
			String onlyFild = "";
			StringBuffer strSql = new StringBuffer();						
			if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
			{												
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
				onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				if(onlyFild==null || onlyFild.length()<=0)
					return list = new ArrayList();
//					throw new GeneralException("系统没有指定唯一性指标！请指定唯一性指标！");									
				
				strSql.append("select pk.*,pi.itemdesc,pi.description,ua.b0110,ua.e0122,ua.a0101 from per_kpi_data pk,per_kpi_item pi,usrA01 ua where pk.object_id=ua."+onlyFild+" ");	
				strSql.append(" and pk.item_id=pi.item_id ");
				strSql.append(" and pk.object_type ='2' ");
				strSql.append(getUserViewPersonWhere(this.userview)); // 操作单位或管理范围内的人	
				if(a_code!=null && a_code.trim().length()>0)
				{								
					if(a_code.indexOf("UN")!=-1)
					{
						strSql.append(" and ua.b0110 like '" + a_code.substring(2, a_code.length()) + "%'");
					}else if(a_code.indexOf("UM")!=-1)
					{
						strSql.append(" and ua.e0122 like '" + a_code.substring(2, a_code.length()) + "%'");
					}else if(a_code.indexOf("Usr")!=-1)
					{
						strSql.append(" and ua.a0100 like '" + a_code.substring(3, a_code.length()) + "%'");
					}else if(a_code.indexOf("@K")!=-1 || a_code.indexOf("@k")!=-1)
					{
						strSql.append(" and ua.e01a1 like '" + a_code.substring(2, a_code.length()) + "%'");
					}
				}
				
				if ((checkName!=null) && (!"".equalsIgnoreCase(checkName.trim())) && (checkName.trim().length()>0))
				{
					if(checkName.indexOf("*")!=-1)
					{
						checkName = checkName.replaceAll("\\*","%"); 		
						strSql.append(" and ua.a0101 like '" + checkName + "' ");	
					}
					else if(checkName.indexOf("?")!=-1)	
					{
						checkName = checkName.replaceAll("\\?","_"); 					
						strSql.append(" and ua.a0101 like '" + checkName + "' ");
						
					}else if(checkName.indexOf("？")!=-1)	
					{
						checkName = checkName.replaceAll("？","_"); 
						strSql.append(" and ua.a0101 like '" + checkName + "' ");
					}
					else				
						strSql.append(" and ua.a0101 like '%" + checkName + "%' ");
				}				
			}else
			{
				strSql.append("select pk.*,pi.itemdesc,pi.description,oz.codeitemdesc from per_kpi_data pk,per_kpi_item pi,organization oz where oz.codesetid<>'@K' ");	
				strSql.append(" and pk.object_id=oz.codeitemid ");				
				strSql.append(" and pk.item_id=pi.item_id ");
				strSql.append(" and pk.object_type ='" + objectType + "' ");	
				strSql.append(getUserViewUnitWhere(this.userview)); // 操作单位或管理范围内的单位
				if(a_code!=null && a_code.trim().length()>0)
				{																		
					strSql.append(" and oz.codeitemid like '" + a_code.substring(2, a_code.length()) + "%'");					
				}
				
				if ((checkName!=null) && (!"".equalsIgnoreCase(checkName.trim())) && (checkName.trim().length()>0))
				{
					if(checkName.indexOf("*")!=-1)
					{
						checkName = checkName.replaceAll("\\*","%"); 		
						strSql.append(" and oz.codeitemdesc like '" + checkName + "' ");	
					}
					else if(checkName.indexOf("?")!=-1)	
					{
						checkName = checkName.replaceAll("\\?","_"); 					
						strSql.append(" and oz.codeitemdesc like '" + checkName + "' ");
						
					}else if(checkName.indexOf("？")!=-1)	
					{
						checkName = checkName.replaceAll("？","_"); 
						strSql.append(" and oz.codeitemdesc like '" + checkName + "' ");
					}
					else				
						strSql.append(" and oz.codeitemdesc like '%" + checkName + "%' ");
				}
			}	
			
			if(cycle==null || cycle.trim().length()<=0 || "-1".equalsIgnoreCase(cycle))
				strSql.append(" and pk.cycle ='0' ");
			else
				strSql.append(" and pk.cycle ='"+ cycle +"' ");
			
			if(year!=null && year.trim().length()>0)
				strSql.append(" and pk.theyear ='"+ year +"' ");
			if(noYearCycle!=null && noYearCycle.trim().length()>0)
			{				
				if("1".equalsIgnoreCase(cycle)) // 半年
					strSql.append(" and pk.thequarter ='"+ noYearCycle +"' ");									
				else if("2".equalsIgnoreCase(cycle)) // 季度
					strSql.append(" and pk.thequarter ='"+ noYearCycle +"' ");								
				else if("3".equalsIgnoreCase(cycle)) // 月度
					strSql.append(" and pk.themonth ='"+ noYearCycle +"' ");				
			}
			
			if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
				strSql.append(" order by ua.b0110,ua.e0122,ua.a0100 ");
			else
				strSql.append(" order by pk.object_id ");
			
			rowSet = dao.search(strSql.toString());
			int n=0;
			StringBuffer object_id = new StringBuffer("");
		    while (rowSet.next())
		    {
		    	LazyDynaBean bean = new LazyDynaBean();		
		    	bean.set("id", isNull(rowSet.getString("id")));
		    	bean.set("object_id", isNull(rowSet.getString("object_id")));
		    	object_id.append(","+isNull(rowSet.getString("id")));
		    			    	
		    	bean.set("object_type", isNull(rowSet.getString("object_type")));				
		    	bean.set("item_id", isNull(rowSet.getString("item_id")));
		    	bean.set("itemdesc", isNull(rowSet.getString("itemdesc")));
		    	bean.set("description", isNull(rowSet.getString("description")));
		    	
		    	String actual_value=(String)rowSet.getString("actual_value")==null?"0.00":(String)rowSet.getString("actual_value");
		    	actual_value=myformat.format(Double.parseDouble(actual_value));//去掉小数点后面的0												    	
		    	bean.set("actual_value", actual_value);
		    	bean.set("cycle", isNull(rowSet.getString("cycle")));
		    	
		    	String theDate = "";
		    	String theTime = "";
		    	String theCycle = isNull(rowSet.getString("cycle"));
		    	if("0".equalsIgnoreCase(theCycle))  // 年度
		    	{
		    		theTime = theCycle+"`"+isNull(rowSet.getString("theyear"));
		    		theDate = isNull(rowSet.getString("theyear"))+"年";		
		    	}
				else if("1".equalsIgnoreCase(theCycle)) // 半年
				{
					if(isNull(rowSet.getString("thequarter")).trim().length()>0 && "01".equalsIgnoreCase(isNull(rowSet.getString("thequarter"))))
					{
						theTime = theCycle+"`"+isNull(rowSet.getString("theyear"))+"`"+isNull(rowSet.getString("thequarter"));
						theDate = isNull(rowSet.getString("theyear"))+"年  上半年";
					}
					else
					{
						theTime = theCycle+"`"+isNull(rowSet.getString("theyear"))+"`"+isNull(rowSet.getString("thequarter"));
						theDate = isNull(rowSet.getString("theyear"))+"年  下半年";
					}
					
				}else if("2".equalsIgnoreCase(theCycle)) // 季度
				{
					theTime = theCycle+"`"+isNull(rowSet.getString("theyear"))+"`"+isNull(rowSet.getString("thequarter"));
					theDate = isNull(rowSet.getString("theyear"))+"年  "+AdminCode.getCodeName("12", isNull(rowSet.getString("thequarter")));	
				}
				else if("3".equalsIgnoreCase(theCycle)) // 月度
				{
					theTime = theCycle+"`"+isNull(rowSet.getString("theyear"))+"`"+isNull(rowSet.getString("themonth"));
					theDate = isNull(rowSet.getString("theyear"))+"年  "+AdminCode.getCodeName("13", isNull(rowSet.getString("themonth")));		
				}
		    	
		    	bean.set("theTime", theTime);
		    	bean.set("theyear", theDate);
		    	bean.set("themonth", isNull(rowSet.getString("themonth")));
		    	bean.set("thequarter", isNull(rowSet.getString("thequarter")));
		    	bean.set("status", isNull(rowSet.getString("status")));
		    	bean.set("modusername", isNull(rowSet.getString("modusername")));				
		    	bean.set("modtime", isNull(String.valueOf(rowSet.getDate("modtime"))));
		    	
		    	bean.set("index",String.valueOf(n));
		    	n++;
		    	
				if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
				{		    		
//					bean.set("b0110", isNull(rowSet.getString("b0110")));
//					bean.set("e0122", isNull(rowSet.getString("e0122")));
					
					bean.set("codeitemdesc", AdminCode.getCodeName("UN",isNull(rowSet.getString("b0110")))+"/"+AdminCode.getCodeName("UM",isNull(rowSet.getString("e0122"))));					
					bean.set("a0101", isNull(rowSet.getString("a0101")));
		    		
		    	}else
		    	{
		    		bean.set("codeitemdesc", isNull(rowSet.getString("codeitemdesc")));
		    	}
				
				Map.put(isNull(rowSet.getString("id")),"signLogo");				
				arlist.add(bean);
		    		
		    }
		    list.add(arlist);
		    list.add(object_id.toString());
		    
		    this.keyMap = Map;
		    
		    if(rowSet!=null)
		    	rowSet.close();
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPersonWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or ua.b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or ua.e0122 like '" + temp[i].substring(2) + "%'");
				}
				if(tempSql!=null&&tempSql.length()>0){
					buf.append(" and ( " + tempSql.substring(3) + " ) ");
				}
				
			} 
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and ua.b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and ua.e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and ua.e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and ua.b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}

	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewUnitWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or oz.codeitemid like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or oz.codeitemid like '" + temp[i].substring(2) + "%'");
				}
				if(tempSql!=null&&tempSql.length()>0){
					buf.append(" and ( " + tempSql.substring(3) + " ) ");
				}
				
			} 
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");					
					else
						buf.append(" and oz.codeitemid like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}
    
    /**
	 * 获取对象类别
	 */ 
    public ArrayList getObjecType()
    {
		ArrayList list = new ArrayList();		
		
//		ContentDAO dao = new ContentDAO(this.con);
		try
		{
//			list.add(new CommonData("all","全部"));
			list.add(new CommonData("2", ResourceFactory.getProperty("label.query.employ")));
			list.add(new CommonData("1", ResourceFactory.getProperty("jx.jifen.group")));						
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	
	/**
	 *  生效或退回KPI原始数据
	 */ 
    public void comBackData(String opt, String[] matters)
    {

		ContentDAO dao = new ContentDAO(this.con);
	
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < matters.length; i++)
		{
			ids.append("'");
		    ids.append(matters[i]);
		    ids.append("',");
		}
		ids.setLength(ids.length() - 1);
	
		StringBuffer strSql = new StringBuffer();
		
		if("compare".equalsIgnoreCase(opt))  // 生效
			strSql.append("update per_kpi_data set status='03' where id in (");
		else
			strSql.append("update per_kpi_data set status='01' where id in (");
		
		strSql.append(ids.toString());
		strSql.append(")");		
		
		try
		{
		    dao.update(strSql.toString(), new ArrayList());
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }
	
    /**
	 *  删除KPI原始数据
	 *  只能删除未生效的KPI原始数据
	 */ 
    public String delDataValue(String[] matters)
    {
    	String msg="nohave03";
    	RowSet rs = null;
    	try
		{
			ContentDAO dao = new ContentDAO(this.con);
		
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < matters.length; i++)
			{
			    ids.append(matters[i]);
			    ids.append(",");
			}
			ids.setLength(ids.length() - 1);
		
			StringBuffer str = new StringBuffer();
			str.append("select id,status from per_kpi_data where id in (");
			str.append(ids.toString());
			str.append(")");			
			rs = dao.search(str.toString());
			
			StringBuffer idss = new StringBuffer();
			while(rs.next())
			{
				String status = rs.getString("status");
				if((status!=null) && (status.trim().length()>0) && ("01".equalsIgnoreCase(status)))
				{
					idss.append(rs.getString("id"));
					idss.append(",");
					
				}else if("03".equalsIgnoreCase(status))
				{
					msg = "have03";
				}
			}
			if(idss!=null && idss.toString().trim().length()>0)
			{
				idss.setLength(idss.length() - 1);
			}else
				return msg;
			
			StringBuffer strSql = new StringBuffer();
			strSql.append("delete from per_kpi_data where id in (");
			strSql.append(idss.toString());
			strSql.append(")");	
		
		    dao.delete(strSql.toString(), new ArrayList());		    		    
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return msg;
    }
    
	/**
	 * 年份从数据库中取得
	 */
    public ArrayList getYears(String objectType)
    {
		ArrayList list = new ArrayList();
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select distinct theyear from per_kpi_data ");
		
		if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
			sqlStr.append(" where object_type ='2' ");	
		else
			sqlStr.append(" where object_type ='" + objectType + "' ");		
		
		sqlStr.append(" order by theyear desc ");		
		ContentDAO dao = new ContentDAO(this.con);
		try
		{
		    RowSet rs = dao.search(sqlStr.toString());
		    while(rs.next())
		    {
				String theYear = rs.getString("theyear");
				CommonData data=new CommonData(theYear,theYear);	   
				list.add(data);
		    }
		    
		    if(rs!=null)
		    	rs.close();
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    
    /**
	 * 考核周期
	 */
	public ArrayList getCycles()
	{
		ArrayList list=new ArrayList();
//		list.add(new CommonData("-1",ResourceFactory.getProperty("hire.jp.pos.all")));
		
		list.add(new CommonData("0",ResourceFactory.getProperty("jx.khplan.yeardu")));
		list.add(new CommonData("1",ResourceFactory.getProperty("jx.khplan.halfyear")));
		list.add(new CommonData("2",ResourceFactory.getProperty("jx.khplan.quarter")));
		list.add(new CommonData("3",ResourceFactory.getProperty("jx.khplan.monthdu")));
		
		return list;
	}
    	
	/**
	 * 具体考核周期
	 */
	public ArrayList getCycleList(String checkCycle)
	{
		ArrayList checkCycleList = new ArrayList();
						
		try
		{															
			if("0".equalsIgnoreCase(checkCycle)) //年度
			{				
				
			}else if("1".equalsIgnoreCase(checkCycle)) //半年
			{
				checkCycleList.add(new CommonData("01", ResourceFactory.getProperty("report.pigeonhole.uphalfyear")));
				checkCycleList.add(new CommonData("02", ResourceFactory.getProperty("report.pigeonhole.downhalfyear")));					
				
			}else if("2".equalsIgnoreCase(checkCycle)) //季度
			{
				checkCycleList.add(new CommonData("01", ResourceFactory.getProperty("report.pigionhole.oneQuarter")));
				checkCycleList.add(new CommonData("02", ResourceFactory.getProperty("report.pigionhole.twoQuarter")));
				checkCycleList.add(new CommonData("03", ResourceFactory.getProperty("report.pigionhole.threeQuarter")));
				checkCycleList.add(new CommonData("04", ResourceFactory.getProperty("report.pigionhole.fourQuarter")));								
				
			}else if("3".equalsIgnoreCase(checkCycle)) //月度
			{		
				checkCycleList.add(new CommonData("01", ResourceFactory.getProperty("date.month.january")));
				checkCycleList.add(new CommonData("02", ResourceFactory.getProperty("date.month.february")));
				checkCycleList.add(new CommonData("03", ResourceFactory.getProperty("date.month.march")));
				checkCycleList.add(new CommonData("04", ResourceFactory.getProperty("date.month.april")));	
				checkCycleList.add(new CommonData("05", ResourceFactory.getProperty("date.month.may")));
				checkCycleList.add(new CommonData("06", ResourceFactory.getProperty("date.month.june")));
				checkCycleList.add(new CommonData("07", ResourceFactory.getProperty("date.month.july")));
				checkCycleList.add(new CommonData("08", ResourceFactory.getProperty("date.month.auguest")));	
				checkCycleList.add(new CommonData("09", ResourceFactory.getProperty("date.month.september")));
				checkCycleList.add(new CommonData("10", ResourceFactory.getProperty("date.month.october")));
				checkCycleList.add(new CommonData("11", ResourceFactory.getProperty("date.month.november")));
				checkCycleList.add(new CommonData("12", ResourceFactory.getProperty("date.month.december")));									
			}						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return checkCycleList;				
	}
    
    /**
	 * 新建业绩数据采集表字段
	 */
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
		} else if ("F".equals(a_type))
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
		if(key)
		    obj.setNullable(false);
		obj.setKeyable(key);	
		return obj;
    }
	
	/**
	 * 取得KPI指标维护信息
	 * return
	 */
    public ArrayList searchKpiTargetAssert(String targetName,String targetType,String affB0110) throws GeneralException
    {

		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
		try
		{	
			ContentDAO dao = new ContentDAO(this.con);										
			
			// 查询KPI指标维护信息数据								
			StringBuffer strSql = new StringBuffer();						
			
			strSql.append("select * from per_kpi_item where 1=1 ");	
/*			
 			//  修改把撤销的指标也显示出来  JinChunhai  2011.09.14
			StringBuffer buff = new StringBuffer();
			buff.append(Sql_switcher.year("start_date")+ "<"+ getDatePart(creatDate,"y") +" or ");
			buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("start_date")+ "<"+ getDatePart(creatDate,"m") +") or ");
			buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("start_date")+ "="+ getDatePart(creatDate,"m") +" and ");
			buff.append(Sql_switcher.day("start_date")+ "<="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and ("+buff.toString()+") ");
			
			StringBuffer buf = new StringBuffer();
			buf.append(Sql_switcher.year("end_date")+ ">"+ getDatePart(creatDate,"y") +" or ");
			buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("end_date")+ ">"+ getDatePart(creatDate,"m") +") or ");
			buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("end_date")+ "="+ getDatePart(creatDate,"m") +" and ");
			buf.append(Sql_switcher.day("end_date")+ ">="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and ("+buf.toString()+") ");
*/			
			if ((targetName!=null) && (!"".equalsIgnoreCase(targetName.trim())) && (targetName.trim().length()>0))
			{
				if(targetName.indexOf("*")!=-1)
				{
					targetName = targetName.replaceAll("\\*","%"); 		
					strSql.append(" and itemdesc like '" + targetName + "' ");	
				}
				else if(targetName.indexOf("?")!=-1)	
				{
					targetName = targetName.replaceAll("\\?","_"); 					
					strSql.append(" and itemdesc like '" + targetName + "' ");
					
				}else if(targetName.indexOf("？")!=-1)	
				{
					targetName = targetName.replaceAll("？","_"); 
					strSql.append(" and itemdesc like '" + targetName + "' ");
				}
				else				
					strSql.append(" and itemdesc like '%" + targetName + "%' ");
			}
			
			if ((targetType!=null) && (!"".equalsIgnoreCase(targetType.trim())) && (targetType.trim().length()>0))
			{								
				strSql.append(" and item_type_desc like '%" + targetType + "%' ");
			}
			if(affB0110!=null && affB0110.trim().length()>0)
			{																		
				strSql.append(" and b0110 like '%," + affB0110.substring(2, affB0110.length()) + "%'");	
			}				
			strSql.append(" order by cycle,item_type_desc,item_id ");
			
			rowSet = dao.search(strSql.toString());
			int num = 1;
		    while (rowSet.next())
		    {		
		    	LazyDynaBean bean = new LazyDynaBean();		
		    	bean.set("id", isNull(rowSet.getString("id")));
		    	bean.set("numbers", String.valueOf(num));
				
		    	bean.set("item_id", isNull(rowSet.getString("item_id")));
		    	bean.set("itemdesc", isNull(rowSet.getString("itemdesc")));				
//				bean.set("description", isNull(rowSet.getString("description")));				
		    	bean.set("cycle", isNull(rowSet.getString("cycle")));
		    	bean.set("item_type_desc", isNull(rowSet.getString("item_type_desc")));
//				bean.set("start_date", isNull(rowSet.getString("start_date")));
//				bean.set("end_date", isNull(rowSet.getString("end_date")));
		    	bean.set("seq", isNull(rowSet.getString("seq")));
				
		    	String b0110 = rowSet.getString("b0110")==null?"":rowSet.getString("b0110").toUpperCase(); // 归属单位
				String gsunit = "";//归属单位
				if(b0110!=null && b0110.trim().length()>0 && !"".equals(b0110.trim()))
				{										    	
				    String[] temp_arr=b0110.split(",");				    	
			    	for(int i=0;i<temp_arr.length;i++)
			    	{   					    		
			    		if(temp_arr[i]==null|| "".equals(temp_arr[i]))
			    			continue;
			    		String desc = "";
			    		if(AdminCode.getCodeName("UN",temp_arr[i])!=null && !"".equals(AdminCode.getCodeName("UN",temp_arr[i])))
			    		{
			    			desc = AdminCode.getCodeName("UN",temp_arr[i]);
			    		}
			    		else if(AdminCode.getCodeName("UM",temp_arr[i])!=null&&!"".equals(AdminCode.getCodeName("UM",temp_arr[i])))
			    		{
			    			desc = AdminCode.getCodeName("UM",temp_arr[i]);
			    		}				    							    	
			    		gsunit+=desc+",";
			    	}
			    	if(gsunit.length()>0)
			    	{
			    		gsunit = gsunit.substring(0,gsunit.length()-1);				    		
			    	}				    						    	
				}	    		
	    		bean.set("b0110desc", isNull(gsunit));
	    		
	    		bean.set("description", PubFunc.toHtml(isNull(rowSet.getString("description"))));												
		        String start_date=PubFunc.FormatDate(rowSet.getDate("start_date"));
		        bean.set("start_date",start_date);
		        String end_date=PubFunc.FormatDate(rowSet.getDate("end_date"));
		        bean.set("end_date",end_date);		        
		        num++;
		        		        
				list.add(bean);
		    			    	
/*		    	
				RecordVo vo = new RecordVo("per_kpi_item");
				vo.setString("id", isNull(rowSet.getString("id")));
//				vo.setString("numbers", String.valueOf(num));
				
				vo.setString("item_id", isNull(rowSet.getString("item_id")));
				vo.setString("itemdesc", isNull(rowSet.getString("itemdesc")));				
//				vo.setString("description", isNull(rowSet.getString("description")));				
				vo.setString("cycle", isNull(rowSet.getString("cycle")));
				vo.setString("item_type_desc", isNull(rowSet.getString("item_type_desc")));
//				vo.setString("start_date", isNull(rowSet.getString("start_date")));
//				vo.setString("end_date", isNull(rowSet.getString("end_date")));
				vo.setString("seq", isNull(rowSet.getString("seq")));
				
				String b0110 = isNull(rowSet.getString("b0110"));
	    		if(AdminCode.getCodeName("UN",b0110)!=null && AdminCode.getCodeName("UN",b0110).length()>0)
	    			vo.setString("b0110", AdminCode.getCodeName("UN",b0110));
	    		else if(AdminCode.getCodeName("UM",b0110)!=null && AdminCode.getCodeName("UM",b0110).length()>0)
	    			vo.setString("b0110", AdminCode.getCodeName("UM",b0110));
	    		
				vo.setString("b0110desc", isNull(rowSet.getString("b0110desc")));																				
				vo.setString("description", PubFunc.toHtml(isNull(rowSet.getString("description"))));												
		        String start_date=PubFunc.FormatDate(rowSet.getDate("start_date"));
		        vo.setDate("start_date",start_date);
		        String end_date=PubFunc.FormatDate(rowSet.getDate("end_date"));
		        vo.setDate("end_date",end_date);		        
//				vo.setString("busi_date", PubFunc.DoFormatDate(isNull(rs.getString("busi_date")).length() > 10 ? rs.getString("busi_date").substring(0, 10) : ""));	
		        num++;
		        		        
				list.add(vo);
*/	
		    	
		    }
		    
		    if(rowSet!=null)
		    	rowSet.close();
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    
    /**
	 * 分解当前系统时间
	 */
	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart))
			str = mydate.substring(0, 4);
		else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6)))
				str = mydate.substring(6, 7);
			else
				str = mydate.substring(5, 7);
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9)))
				str = mydate.substring(9, 10);
			else
				str = mydate.substring(8, 10);
		}
		return str;
	}
    
	/**
	 * 指标类别从数据库中取得
	 */
    public ArrayList getTargetType()
    {
    	String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获取当前系统时间
		ArrayList list = new ArrayList();
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select distinct ");
		sqlStr.append(" item_type_desc from per_kpi_item where item_type_desc is not null ");
		
		StringBuffer buff = new StringBuffer();
		buff.append(Sql_switcher.year("start_date")+ "<"+ getDatePart(creatDate,"y") +" or ");
		buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buff.append(Sql_switcher.month("start_date")+ "<"+ getDatePart(creatDate,"m") +") or ");
		buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buff.append(Sql_switcher.month("start_date")+ "="+ getDatePart(creatDate,"m") +" and ");
		buff.append(Sql_switcher.day("start_date")+ "<="+ getDatePart(creatDate,"d") +")");
		sqlStr.append(" and ("+buff.toString()+") ");
		
		StringBuffer buf = new StringBuffer();
		buf.append(Sql_switcher.year("end_date")+ ">"+ getDatePart(creatDate,"y") +" or ");
		buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buf.append(Sql_switcher.month("end_date")+ ">"+ getDatePart(creatDate,"m") +") or ");
		buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buf.append(Sql_switcher.month("end_date")+ "="+ getDatePart(creatDate,"m") +" and ");
		buf.append(Sql_switcher.day("end_date")+ ">="+ getDatePart(creatDate,"d") +")");
		sqlStr.append(" and ("+buf.toString()+") ");
		
		sqlStr.append(" order by item_type_desc desc ");
		
		ContentDAO dao = new ContentDAO(this.con);
		try
		{
			list.add(new CommonData("",""));
		    RowSet rs = dao.search(sqlStr.toString());
		    while(rs.next())
		    {
				String item_type_desc = rs.getString("item_type_desc");
				CommonData data=new CommonData(item_type_desc,item_type_desc);	   
				list.add(data);
		    }
		    
		    if(rs!=null)
		    	rs.close();
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /**
	 * 判断数据库中有没有此KPI指标
	 */
    public boolean isExist(String item_id) throws GeneralException
	{
		if(item_id==null || item_id.trim().length()==0)
			return false;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select item_id from per_kpi_item where item_id='" +item_id+ "' ");
		ContentDAO dao = new ContentDAO(this.con);
		try
		{
			RowSet rs = dao.search(strsql.toString());
			if (rs.next())
				return true;
			
			if(rs!=null)
				rs.close();
			
		} catch (SQLException e)
		{
			throw new GeneralException("查询数据异常！");
		}
		return false;
	} 
    
    /**
	 *  校验要删除的KPI指标是否被引用
	 */ 
    public String checkKpiTarget(String[] matters)
    { 
    	String msg = "nouseded";
    	RowSet rs = null;
    	try
		{
			ContentDAO dao = new ContentDAO(this.con);
		
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < matters.length; i++)
			{
				ids.append("'");
			    ids.append(matters[i]);
			    ids.append("',");
			}
			ids.setLength(ids.length() - 1);
		 
			StringBuffer str = new StringBuffer();			
			str.append("select item_id from per_kpi_data where item_id in (select item_id from per_kpi_item where id in (");
			str.append(ids.toString());
			str.append(")) ");			
			rs = dao.search(str.toString());			
			while(rs.next())
			{								
				String item_id = rs.getString("item_id");
				if(item_id!=null && item_id.trim().length()>0)	
					msg = "useded";
			}									
			if(rs!=null)
				rs.close();			
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return msg;
    }
    
    /**
	 *  删除/撤销 KPI指标
	 *  已引入的指标不可以执行删除操作  
	 */ 
    public void delAboKpiTarget(String opt, String[] matters)
    {
    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    	RowSet rs = null;
    	try
		{
			ContentDAO dao = new ContentDAO(this.con);
		
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < matters.length; i++)
			{
				ids.append("'");
			    ids.append(matters[i]);
			    ids.append("',");
			}
			ids.setLength(ids.length() - 1);
		
/*			// 已引入的指标不可以执行删除操作 
			StringBuffer str = new StringBuffer();
			str.append("select distinct item_id from per_kpi_data ");						
			rs = dao.search(str.toString());			
			StringBuffer idss = new StringBuffer();
			while(rs.next())
			{				
				idss.append("'");
				idss.append(rs.getString("item_id"));
				idss.append("',");								
			}									
*/			
			StringBuffer str = new StringBuffer();			
			StringBuffer strSql = new StringBuffer();			
			if(opt!=null && opt.trim().length()>0 && "del".equalsIgnoreCase(opt)) // 删除
			{
				strSql.append("delete from per_kpi_item where id in (");
				strSql.append(ids.toString());
				strSql.append(")");
/*				
				if(idss!=null && idss.toString().trim().length()>0)
				{
					idss.setLength(idss.length() - 1);
					strSql.append(" and item_id not in (");	
					strSql.append(idss.toString());
					strSql.append(")");
				}	
*/				
				str.append("delete from per_kpi_data where item_id in (select item_id from per_kpi_item where id in (");
				str.append(ids.toString());
				str.append(")) ");
				
				dao.delete(str.toString(), new ArrayList());
				dao.delete(strSql.toString(), new ArrayList());						
				
			}else if("abolish".equalsIgnoreCase(opt))  // 撤销
			{
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);    //得到当前时间的前一天
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
				String mDateTime=formatter.format(calendar.getTime());  
								    				
				strSql.append("update per_kpi_item set end_date="+Sql_switcher.dateValue(mDateTime)+" where id in (");				
				strSql.append(ids.toString());
				strSql.append(")");
										
				dao.update(strSql.toString(), new ArrayList());				
			}
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }
    
    /**
	 * 随机生成KPI指标编号
	 */
    public String getNextItemId(String keyCloumn,String tableName)
	{
		String key="A0001";
		try
		{			
			StringBuffer buf = new StringBuffer();
			buf.append("select MAX(");
			buf.append(keyCloumn+") as "+keyCloumn);
			buf.append(" from ");
			buf.append(tableName);
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rs =null;
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				String str=rs.getString(keyCloumn);
				if(str==null|| "".equals(str))
					return key;
				String[] temp=new String[str.length()] ;
				for(int i=0;i<str.length();i++)
				{
					temp[i]=str.substring(i,i+1);
				}
				for(int j=temp.length-1;j>=0;j--)
				{
					if(j==temp.length-1)
					{
						if("z".equalsIgnoreCase(temp[j]))
						{
							temp[j]="0";
							int i=1;
							while(j-i>=0)
							{
								String astr=temp[j-i];
								if(!"z".equals(astr))
								{
									if("9".equals(astr))
										temp[j-i]="A";
									else
										temp[j-i]=this.getNextLetter(temp[j-i]);
									break;
								}
								else
								{
									temp[j-i]="0";
									i++;
								}
							}
							if(i==temp.length)
							{
								String tem="";
								for(int ff=0;ff<temp.length;ff++)
								{
									tem+="0";
								}
								return "1"+tem;
							}
						}else if("9".equalsIgnoreCase(temp[j]))
						{
							temp[j]="A";
						}
						else
						{
							temp[j]=this.getNextLetter(temp[j]);
							break;
						}
					}
				}
				for(int i=0;i<temp.length;i++)
				{
					if(i==0)
					{
						key="";
					}
					key+=temp[i];
				}
				/*if(a)
			    	key="Z"+key;*/				
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return key;
	}
	public String getNextLetter(String args)
	{
		String str="";
		try
		{	
			String[] lettersUpper={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9","0"};		  
			if("9".equals(args))
				return "A";
			
			for(int i=0;i<(lettersUpper.length-1);i++)
			{
			    String x=lettersUpper[i];
			    if(args.equalsIgnoreCase(x))
			    {
			        str=lettersUpper[i+1];
			    	break;
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

/*
	// 从数据库中取得KPI指标类别
    public ArrayList getKPItemType()
    {
		ArrayList list = new ArrayList();				
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			
			StringBuffer sqlStr = new StringBuffer();
			sqlStr.append("select distinct item_type_desc from per_kpi_item ");
			sqlStr.append(" order by item_type_desc desc  ");
		    rs = dao.search(sqlStr.toString());
		    while(rs.next())
		    {
				String item_type_desc = rs.getString("item_type_desc");
				CommonData data=new CommonData(item_type_desc,item_type_desc);	   
				list.add(data);
		    }
		    
		    if(rs!=null)
		    	rs.close();
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }
*/ 
    
	/**
	 *  导入数据  
	 */
	public void importData(InputStream is,String objectType) throws GeneralException
	{		
		Workbook wb = null;
		Sheet sheet = null;
		//20/3/9 xus vfs改造
//		InputStream is = null;
		try
		{
			if(this.keyMap==null || this.keyMap.size()<=0)
			{
				throw new GeneralException("请新建记录后再进行数据的导入！");	
//				return;
			}
			ContentDAO dao=new ContentDAO(this.con);
//			is = new FileInputStream(file);
			wb = WorkbookFactory.create(is);							
			sheet = wb.getSheetAt(0);	
			
			Row row = sheet.getRow(0);
			if(row==null)
				throw new GeneralException("请用下载的模板导入KPI原始数据！");						
			
			// 找到数据列名
			int cols = row.getPhysicalNumberOfCells(); // Excel表中所有数据列数

			String sign = "false";  // 判断是否人员模板 true 是  false 不是
			for (int j = 0; j < cols; j++)
			{
				String colname = "";
				Cell cell=row.getCell((short)j);
				if (cell != null)
				{
					switch (cell.getCellType())
					{
						case Cell.CELL_TYPE_STRING:
							if (cell.getCellComment() == null)
								throw new GeneralException("请用下载的模板导入KPI原始数据！");	
//								throw new GeneralException("请设置列[" + cell.getStringCellValue() + "]的批注！");
							else
								colname = cell.getCellComment().getString().getString().trim();
							break;
						default:
							colname = "";
					}					
					if("name".equalsIgnoreCase(colname))
						sign = "true";																								
				}
			}			
			if((objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType)) && ("false".equalsIgnoreCase(sign)))
				throw new GeneralException("请用下载的模板导入KPI原始数据！");	
			else if((objectType != null && objectType.trim().length()>0 && !"2".equalsIgnoreCase(objectType)) && ("true".equalsIgnoreCase(sign)))
				throw new GeneralException("请用下载的模板导入KPI原始数据！");	
				
			// 数据行			
			int rows = sheet.getPhysicalNumberOfRows(); // Excel表中所有数据行数
			for (int j = 1; j < rows; j++)
			{
				row = sheet.getRow(j);
				if(row==null)
					continue;
								
				String sql = "";	
				String comment="";
				StringBuffer sqlStr = new StringBuffer();														
				for (int k = 0; k < cols; k++)
				{
					Cell cell = row.getCell((short) k);
					
					String value = "";
					if (cell != null)
					{
						switch (cell.getCellType())
						{
							case Cell.CELL_TYPE_FORMULA:
								break;
								
							case Cell.CELL_TYPE_NUMERIC:
								double y2 = cell.getNumericCellValue();
								value = Double.toString(y2);
								if (value.indexOf("E") > -1)
								{
									String x1 = value.substring(0, value.indexOf("E"));
									String y1 = value.substring(value.indexOf("E") + 1);

									value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
								}								
								sql = "update per_kpi_data set actual_value='" + value + "' where 1=1 ";														    
								break;
								
							case Cell.CELL_TYPE_STRING:
								value = cell.getRichStringCellValue().toString();								
								if(cell.getCellComment()!=null)
			   					{
									comment=cell.getCellComment().getString().getString().trim();																												
			   					}																																						
								break;
								
							case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0								
								break;
								
							default:
								
						}						
					}
				}								
				if(this.keyMap.get(comment)!=null)
					sqlStr.append(" and id='" + comment + "' ");
				else
					continue;
									
				sqlStr.append(" and status<>'03' ");								
				if(sql==null || sql.trim().length()<=0)
					sql = "update per_kpi_data set actual_value='0.0' where 1=1 ";	
				
				String strSql = sql + sqlStr.toString();				
				dao.update(strSql);				
				
			}
						
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
		    PubFunc.closeResource(is);
			PubFunc.closeResource(wb);
		}
	}	
	
}
