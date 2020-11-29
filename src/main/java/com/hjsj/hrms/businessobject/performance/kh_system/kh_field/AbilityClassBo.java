package com.hjsj.hrms.businessobject.performance.kh_system.kh_field;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:AbilityClassBo.java</p>
 * <p>Description:能力素质课程</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-11-11 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class AbilityClassBo 
{
	
	private Connection con=null;
	private UserView userView=null;
	
	public AbilityClassBo(Connection a_con,UserView userView)
	{
		this.con=a_con;
		this.userView=userView;
	}
	
	/**	
	 * 新建能力素质课程表
	 */
	public void builtPointCourseTable()
	{		
		try
		{

			String tablename = "per_point_course";			
			DbWizard dbWizard = new DbWizard(this.con);			
			if(!dbWizard.isExistTable(tablename,false))
			{
				Table table = new Table(tablename);
		    	table.addField(getField("point_id", "A", 30, true));		    			    			    	
		    	table.addField(getField("r5000", "I", 4, true));			    			    	
		    	dbWizard.createTable(table);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		
	/**
	 * 取得与能力指标关联的培训课程
	 * return
	 */
    public ArrayList searchPointCourseList(String point_id) throws GeneralException
    {

		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
		try
		{	
			ContentDAO dao = new ContentDAO(this.con);										
			
			// 需要验证当前用户的机构权限 add by 刘蒙
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理,6：培训管理,7：招聘管理
			// 查询能力指标关联的培训课程数据								
			StringBuffer strSql = new StringBuffer();						
			
			strSql.append("select ");
			if ((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) {
				String[] arrOperOrg = operOrg.split("`"); // [ UN0101, UN010101 ]
				StringBuffer strOperOrg = new StringBuffer(" 1=2 "); // 预期:'0101','010101'
				for (int i = 0; i < arrOperOrg.length; i++) {
					strOperOrg.append(" or pr.r5020 like '").append(arrOperOrg[i].replace("UN", "").replace("UM", "")).append("%' ");
				}
				 
				strSql.append("case when ").append(strOperOrg.toString()).append(" or pr.r5020 is null then 1 else 0 end as isEnable,");
			}
			strSql.append(" pr.* from per_point_course pp,r50 pr ");	
			strSql.append(" where pp.point_id='").append(point_id).append("'");	
			strSql.append(" and pp.r5000=pr.r5000 ");	
			
			// 选取已发布的课程
			strSql.append(" and pr.r5022='04' ");
			
 			// 选取在有效期范围内课程
			StringBuffer buff = new StringBuffer();
			buff.append(Sql_switcher.year("pr.r5030")+ "<"+ getDatePart(creatDate,"y") +" or ");
			buff.append("("+Sql_switcher.year("pr.r5030")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("pr.r5030")+ "<"+ getDatePart(creatDate,"m") +") or ");
			buff.append("("+Sql_switcher.year("pr.r5030")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("pr.r5030")+ "="+ getDatePart(creatDate,"m") +" and ");
			buff.append(Sql_switcher.day("pr.r5030")+ "<="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and (("+buff.toString()+") or pr.r5030 is null or pr.r5030 = '' ) ");
		//	strSql.append(" and ("+buff.toString()+") ");
			
			StringBuffer buf = new StringBuffer();
			buf.append(Sql_switcher.year("pr.r5031")+ ">"+ getDatePart(creatDate,"y") +" or ");
			buf.append("("+Sql_switcher.year("pr.r5031")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("pr.r5031")+ ">"+ getDatePart(creatDate,"m") +") or ");
			buf.append("("+Sql_switcher.year("pr.r5031")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("pr.r5031")+ "="+ getDatePart(creatDate,"m") +" and ");
			buf.append(Sql_switcher.day("pr.r5031")+ ">="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and (("+buff.toString()+") or pr.r5031 is null or pr.r5031 = '' ) ");
		//	strSql.append(" and ("+buf.toString()+") ");
															
			strSql.append(" order by pr.norder ");			
			rowSet = dao.search(strSql.toString());
		    while (rowSet.next())
		    {			    	
		    	LazyDynaBean bean = new LazyDynaBean();		
		    	String classId = isNull(rowSet.getString("r5000"));
		    	bean.set("classId", SafeCode.encode(PubFunc.encrypt(classId)));			
		    	bean.set("className", isNull(rowSet.getString("r5003")));
		    	bean.set("classType", AdminCode.getCodeName("55",isNull(rowSet.getString("r5004"))));							
		    	bean.set("classDesc", isNull(rowSet.getString("r5012")));
		    	bean.set("classHour", isNull(rowSet.getString("r5009")));
		    	bean.set("classScore", isNull(rowSet.getString("r5007")));
		    	
		    	if ((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) {
		    		// 1:当前用户有操作课程对应机构的权限，0:没有 add by 刘蒙
					bean.set("isEnable", rowSet.getInt("isEnable") + "");
				} else {
					bean.set("isEnable", "1");
				}
				list.add(bean);	    			    				    	
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
    
    /**
	 * 撤销关联的培训课程	   
	 */ 
    public void cancelAbilityClass(String point_id,String classIds)
    {
    	try
		{
			ContentDAO dao = new ContentDAO(this.con);
		
			String[] matters = classIds.split("/");
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < matters.length; i++)
			{
			    ids.append(PubFunc.decrypt(SafeCode.decode(matters[i])));
			    ids.append(",");
			}
			
			if(ids!=null && ids.toString().trim().length()>0)
			{
				ids.setLength(ids.length() - 1);
						
				StringBuffer strSql = new StringBuffer();
				strSql.append("delete from per_point_course where point_id='" + point_id + "' and r5000 in (");
				strSql.append(ids.toString());
				strSql.append(")");	
			
			    dao.delete(strSql.toString(), new ArrayList());		
			}
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }
    
    
    
    
    /**
	 * 取得与能力指标可以关联的培训课程
	 * return
	 */
    public ArrayList searchAllowPointCourseList(String point_id,String itemize,String coursename,String courseintro) throws GeneralException
    {

		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
		try
		{	
			ContentDAO dao = new ContentDAO(this.con);										
			
			// 需要验证当前用户的机构权限 add by 刘蒙
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理,6：培训管理,7：招聘管理
			StringBuffer strOperOrg = new StringBuffer(); // 预期:'0101','010101'
			if ((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) {
				String[] arrOperOrg = operOrg.split("`"); // [ UN0101, UN010101 ]
				for (int i = 0; i < arrOperOrg.length; i++) {
					strOperOrg.append(" or r5020 like '").append(arrOperOrg[i].replace("UN", "").replace("UM", "")).append("%' ");
				}
			}
			
			// 查询能力指标关联的培训课程数据								
			StringBuffer strSql = new StringBuffer();						
			
			strSql.append("select * from r50 ");				
			// 已经关联的课程不再显示
			strSql.append(" where R5000 not in (select R5000 from per_point_course where point_id='" + point_id + "') ");						
			// 选取已发布的课程
			strSql.append(" and r5022='04' ");
			// 选取操作单位或管理范围内的课程
			strSql.append(getUserViewWhere());						
			
 			// 选取在有效期范围内课程
			StringBuffer buff = new StringBuffer();
			buff.append(Sql_switcher.year("r5030")+ "<"+ getDatePart(creatDate,"y") +" or ");
			buff.append("("+Sql_switcher.year("r5030")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("r5030")+ "<"+ getDatePart(creatDate,"m") +") or ");
			buff.append("("+Sql_switcher.year("r5030")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("r5030")+ "="+ getDatePart(creatDate,"m") +" and ");
			buff.append(Sql_switcher.day("r5030")+ "<="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and (("+buff.toString()+") or r5030 is null or r5030 = '' ) ");
			
			StringBuffer buf = new StringBuffer();
			buf.append(Sql_switcher.year("r5031")+ ">"+ getDatePart(creatDate,"y") +" or ");
			buf.append("("+Sql_switcher.year("r5031")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("r5031")+ ">"+ getDatePart(creatDate,"m") +") or ");
			buf.append("("+Sql_switcher.year("r5031")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("r5031")+ "="+ getDatePart(creatDate,"m") +" and ");
			buf.append(Sql_switcher.day("r5031")+ ">="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and (("+buf.toString()+") or r5031 is null or r5031 = '' ) ");
				
			if(itemize!=null && itemize.trim().length()>0) {
                strSql.append(" and r5004 ='"+itemize+"'");
            }
			if(coursename!=null && coursename.trim().length()>0) {
                strSql.append(" and r5003 like '%"+coursename+"%'");
            }
			if(courseintro!=null && courseintro.trim().length()>0) {
                strSql.append(" and r5012 like '%"+courseintro+"%'");
            }
			
			if ((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) {
				strSql.append(" and  ( 1<>1 ").append(strOperOrg.toString()).append(" or R5020 is null)"); // 过滤掉非当前用户可以操作的单位 add by 刘蒙
			}
			
			strSql.append(" order by norder ");			
			rowSet = dao.search(strSql.toString());
		    while (rowSet.next())
		    {			    	
		    	LazyDynaBean bean = new LazyDynaBean();		
		    	bean.set("classId", isNull(rowSet.getString("r5000")));				
		    	bean.set("className", isNull(rowSet.getString("r5003")));
		    	bean.set("classType", AdminCode.getCodeName("55",isNull(rowSet.getString("r5004"))));							
		    	bean.set("classDesc", isNull(rowSet.getString("r5012")));
		    	bean.set("classHour", isNull(rowSet.getString("r5009")));
		    	bean.set("classScore", isNull(rowSet.getString("r5007")));	    				        
				list.add(bean);	    			    				    	
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
    /**
	 * 确定要关联的培训课程	   
	 */ 
    public void decideAbilityClass(String point_id,String classIds)
    {
    	try
		{
			ContentDAO dao = new ContentDAO(this.con);
		
			String[] matters = classIds.split("/");			
			for (int i = 0; i < matters.length; i++)
			{
				ArrayList classList = new ArrayList();
				classList.add(point_id);
				classList.add(matters[i]);
				StringBuffer strSql = new StringBuffer();
				strSql.append("insert into per_point_course (point_id,r5000) values (?,?) ");
				dao.insert(strSql.toString(), classList);		
				
			}						
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }    
    
    
    /**
	 * 分解当前系统时间
	 */
	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart)) {
            str = mydate.substring(0, 4);
        } else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6))) {
                str = mydate.substring(6, 7);
            } else {
                str = mydate.substring(5, 7);
            }
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9))) {
                str = mydate.substring(9, 10);
            } else {
                str = mydate.substring(8, 10);
            }
		}
		return str;
	}		
	
	/**
	 * 新建能力素质课程表字段
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
		if(key) {
            obj.setNullable(false);
        }
		obj.setKeyable(key);	
		return obj;
    }
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewWhere()
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or r5020 like '" + temp[i].substring(2) + "%'");
                    }
				}
				buf.append(" and ( r5020 = '' or r5020 is null " + tempSql + " ) ");
			} 
			else if((!this.userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = this.userView.getManagePrivCode();
				String codevalue = this.userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else if("UN".equalsIgnoreCase(codeid)) {
                        buf.append(" and ( r5020 = '' or r5020 is null or r5020 like '" + codevalue + "%' )");
                    }
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}
		return str;		
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }
	
}
