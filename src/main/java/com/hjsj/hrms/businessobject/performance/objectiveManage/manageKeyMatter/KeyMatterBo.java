package com.hjsj.hrms.businessobject.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:KeyMatterBo.java</p>
 * <p>Description:关健事件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class KeyMatterBo
{
    private Connection cn = null;

//    public KeyMatterBo()
//    {

//    }

    public KeyMatterBo(Connection cn)
    {
    	this.cn = cn;
    	
    	try 
    	{
    		/**
    		 *  初始化 检查 per_key_event 表中有没有 key_set, status 字段，若没有就创建
    		 */ 
			editPerKeyEvent();
			
		} catch (GeneralException e)
		{
			e.printStackTrace();
		}
    }

    public ArrayList searchKeyMatter(String year, String objectType, String orgCode, String kind, String userbase, UserView u, String checkName) throws GeneralException
    {

		ArrayList list = new ArrayList();
		try
		{
		
			StringBuffer strWhere = new StringBuffer();
		
			if (orgCode != null && kind != null)
			{
			    if ("0".equals(kind))
			    {
					String emps = searchEmpByPosition(orgCode,userbase);
					if(emps.length()>0)
					    strWhere.append("and a0100 in(" + emps  + ")");
					else
					    return list;//该职位下面没有人
			    }else if ("1".equals(kind))//部门
			    	strWhere.append("and e0122 like '" + orgCode+ "%' ");
			    else if ("2".equals(kind))//单位
			    	strWhere.append("and b0110 like '" +  orgCode + "%' ");
			    else if ("3".equals(kind))//人员
			    	strWhere.append("and a0100 ='" + orgCode + "' ");
			    else if ("4".equals(kind))//操作单位
			    	strWhere.append(" " + orgCode + " ");
			}
			if (objectType != null)
			    strWhere.append("and object_type ='" + objectType + "' ");
			if (year != null && !"".equals(year.trim()))
			    strWhere.append("and "+Sql_switcher.year("busi_date")+"=" + year + " ");
			//高级权限设置
			
			String operOrg =u.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg.length()<=2)
			{
				String priStrSql = InfoUtils.getWhereINSql(u, "Usr");
				if(priStrSql.length()>0)
				{
				    
			//	    if(kind.equals("1") && objectType.equals("1"))
			//		 strWhere.append("and e0122 in (select usra01.e0122 "+priStrSql+")");
			//	    else if(kind.equals("2") && objectType.equals("1"))
			//		 strWhere.append("and b0110 in (select usra01.b0110 "+priStrSql+")");
					
				    if("2".equals(objectType))//对人员的关键事件进行限制
				    	strWhere.append("and a0100 in (select usra01.A0100 "+priStrSql+")");
				}
			}
							
			StringBuffer str=new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.cn);
			StringBuffer _str = new StringBuffer();
			str.append("select codeitemid,codeitemdesc from codeitem where codesetid='67'");
			RowSet rs = dao.search(str.toString());
			while(rs.next())
			{
				String codeitemid = rs.getString("codeitemid");
				if(!(u.isSuper_admin()))
				{
					if(!u.isHaveResource(IResourceConstant.KEY_EVENT,codeitemid))      //关键事件权限
					{
						continue;
					}					
				}
				_str.append(",'"+rs.getString("codeitemid")+"'"); 
			}
			if(_str.length()>0)
				strWhere.append(" and ( key_set in ("+_str.substring(1)+") or key_set is null or key_set='' ) ");	
			else
				strWhere.append(" and ( key_set is null or key_set='' ) ");
			
			if ((checkName!=null) && (!"".equalsIgnoreCase(checkName.trim())) && (checkName.trim().length()>0))
			{
				if(checkName.indexOf("*")!=-1)
				{
					checkName = checkName.replaceAll("\\*","%"); 		
					strWhere.append(" and a0101 like '" + checkName + "' ");	
				}
				else if(checkName.indexOf("?")!=-1)	
				{
					checkName = checkName.replaceAll("\\?","_"); 					
					strWhere.append(" and a0101 like '" + checkName + "' ");
				}else if(checkName.indexOf("？")!=-1)	
				{
					checkName = checkName.replaceAll("？","_"); 
					strWhere.append(" and a0101 like '" + checkName + "' ");
				}
				else				
					strWhere.append(" and a0101 like '%" + checkName + "%' ");
			}
			
			StringBuffer strSql = new StringBuffer();	
			strSql.append("select per_key_event.* ,");
			strSql.append(Sql_switcher.year("busi_date") +" theyear, ");		
			strSql.append(Sql_switcher.month("busi_date") +" themonth, ");
			strSql.append(Sql_switcher.day("busi_date") +" theday ");		
			
			strSql.append(" from per_key_event ");
			if (strWhere.length() > 3)
			    strSql.append("where 1=1 " + strWhere + " order by theyear desc ,themonth desc ,theday desc");			
		   
		    rs = dao.search(strSql.toString());
		    while (rs.next())
		    {
				RecordVo vo = new RecordVo("per_key_event");
				vo.setString("event_id", rs.getString("event_id"));
				vo.setString("b0110", rs.getString("b0110"));
				vo.setString("e0122", rs.getString("e0122"));
				
				if (!"2".equals(objectType))
		    	{
			    	String b0110 = rs.getString("b0110");
		    		if(AdminCode.getCodeName("UN",b0110)!=null&&AdminCode.getCodeName("UN",b0110).length()>0)
		    			vo.setString("a0101", AdminCode.getCodeName("UN",b0110));
		    		else if(AdminCode.getCodeName("UM",b0110)!=null&&AdminCode.getCodeName("UM",b0110).length()>0)
		    			vo.setString("a0101", AdminCode.getCodeName("UM",b0110));
		    	}else  if("2".equals(objectType))
		    		vo.setString("a0101", rs.getString("a0101"));
				
				
				vo.setString("a0100", rs.getString("a0100"));
				vo.setString("nbase", rs.getString("nbase"));
				vo.setString("key_event", PubFunc.toHtml(isNull(rs.getString("key_event"))));// > 15 ? rs.getString("key_event").substring(0, 15)+"..." : isNull(rs.getString("key_event"))
					//isNull(rs.getString("key_event")).length() > 15 ? rs.getString("key_event").substring(0, 15)+"..." : isNull(rs.getString("key_event")));
				vo.setString("object_type", rs.getString("object_type"));
				
				if((AdminCode.getCodeName("67", isNull(rs.getString("key_set")))) != null && (AdminCode.getCodeName("67", isNull(rs.getString("key_set"))).length()) > 0)
					vo.setString("key_set", AdminCode.getCodeName("67", isNull(rs.getString("key_set"))));
	    		else
	    			vo.setString("key_set", "");
				
//				if((AdminCode.getCodeName("23", isNull(rs.getString("status")))) != null && (AdminCode.getCodeName("23", isNull(rs.getString("status"))).length()) > 0)
//					vo.setString("status", AdminCode.getCodeName("23", isNull(rs.getString("status"))));
//	    		else
//	    			vo.setString("status", "");

/*				String status = isNull(rs.getString("status"));
				if(status.equalsIgnoreCase("03"))
					vo.setString("status", "生效");
				else
					vo.setString("status", "起草");
*/				
				vo.setString("status", isNull(rs.getString("status")));
				
		        String temp=PubFunc.FormatDate(rs.getDate("busi_date"));
		        vo.setDate("busi_date",temp);
		        
				//vo.setString("busi_date", PubFunc.DoFormatDate(isNull(rs.getString("busi_date")).length() > 10 ? rs.getString("busi_date").substring(0, 10) : ""));
		
				String score = rs.getString("score");		
				vo.setString("score", getScore(PubFunc.round(score, 2)));	
				list.add(vo);
	
		    }
		    
		    if(rs!=null)
		    	rs.close();
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    public String isNull(String str)
    {

		if (str == null)
		    str = "";
		return str;
    }

    /**
	 *  检查 per_key_event 表中有没有 key_set 字段，若没有就创建  JinChunhai 2011.04.20
	 */  
    public void editPerKeyEvent( ) throws GeneralException
	{
		try
		{			
			String tablename = "per_key_event";
			Table table = new Table(tablename);
			DbWizard dbWizard = new DbWizard(this.cn);
			boolean flag = false;
			
			if (!dbWizard.isExistField(tablename, "key_set", false))
			{				
				Field obj = new Field("key_set");	
				obj.setDatatype(DataType.STRING);
				obj.setLength(30);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}			
			if (!dbWizard.isExistField(tablename, "status", false))
			{				
				Field obj = new Field("status");	
				obj.setDatatype(DataType.STRING);
				obj.setLength(2);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			
			if (flag)
				dbWizard.addColumns(table);// 更新列
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    /**
	 *  删除关键事件
	 *  只能删除未生效的关键事件
	 */ 
    public String delKeyMatters(String[] matters)
    {
    	String msg="nohave03";
    	RowSet rs = null;
    	try
		{
			ContentDAO dao = new ContentDAO(this.cn);
		
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < matters.length; i++)
			{
			    ids.append(matters[i]);
			    ids.append(",");
			}
			ids.setLength(ids.length() - 1);
		
			StringBuffer str = new StringBuffer();
			str.append("select * from per_key_event where event_id  in (");
			str.append(ids.toString());
			str.append(")");			
			rs = dao.search(str.toString());
			
			StringBuffer idss = new StringBuffer();
			while(rs.next())
			{
				String status = rs.getString("status");
				if((status!=null) && (status.trim().length()>0) && ("01".equalsIgnoreCase(status)))
				{
					idss.append(rs.getString("event_id"));
					idss.append(",");
					
				}else if(status==null || status.trim().length()<=0 || "03".equalsIgnoreCase(status))
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
			strSql.append("delete from per_key_event where event_id  in (");
			strSql.append(idss.toString());
			strSql.append(")");	
		
		    dao.delete(strSql.toString(), new ArrayList());		    		    
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return msg;
    }
    
    public String searchEmpByPosition(String orgcode,String userbase)
    {
		StringBuffer emps = new StringBuffer();	
		String strSql = "select a0100 from "+userbase+"a01 where e01a1='" + orgcode + "'";
		List list = ExecuteSQL.executeMyQuery(strSql);
		for (int i=0;i<list.size();i++)
		{
		    DynaBean bean = (DynaBean) list.get(i);
		    emps.append(",'"+(String) bean.get("a0100")+"'");	    
		}
		if(emps.length()>1)
		    return emps.substring(1);
		else
		    return "";	
    }
    /**
	 * 取得单位或部门信息
	 * return
	 */ 
    public HashMap getUnitUNUM(String orgcode,String userbase)
    {
		HashMap emp = new HashMap();	
		
		String strSql = "select codeitemid,codeitemdesc from organization where codesetid = '" + userbase + "' and codeitemid = '" + orgcode + "'";
		List list = ExecuteSQL.executeMyQuery(strSql);
		if(!list.isEmpty())
		{
		    DynaBean bean = (DynaBean) list.get(0);
		    emp.put("codeitemdesc",bean.get("codeitemdesc"));
		    emp.put("codeitemid",bean.get("codeitemid"));
		}
		return emp;	
    }
    /**
	 * 取得人员信息
	 * return
	 */ 
    public HashMap getUnitDept(String orgcode,String userbase)
    {
		HashMap emp = new HashMap();	
		
		String strSql = "select a0101 name,b0110 unit,e0122 dept from "+userbase+"a01 where a0100='" + orgcode + "'";
		List list = ExecuteSQL.executeMyQuery(strSql);
		if(!list.isEmpty())
		{
		    DynaBean bean = (DynaBean) list.get(0);
		    emp.put("name",bean.get("name"));
		    emp.put("unit",bean.get("unit"));
		    emp.put("dept",bean.get("dept"));
		}
		return emp;	
    }
   //小数位后如果都是0就不显示 
    public String getScore(String score)
    {
		String str="";
		if(score==null)
		    return str;
		if(score.indexOf(".")!=-1)
		{
		    int n = score.indexOf(".");
		    String temp = score.substring(n+1, score.length());
		    int count = score.length()-n-1;
		    String str2="";
		    for(int i=0;i<count;i++)
		    	str2=str2+"0";
		    
		    if(temp.equals(str2))
			str=score.substring(0,n);
		    else
			return score;
		}
		return str;
    }
    //年份从数据库中取得
    public ArrayList getYears()
    {
		ArrayList list = new ArrayList();
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select distinct ");
		sqlStr.append(Sql_switcher.year("busi_date"));
		sqlStr.append(" theyear from per_key_event order by theyear desc ");
		
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
		    RowSet rs = dao.search(sqlStr.toString());
		    while(rs.next())
		    {
				String theYear = rs.getString("theyear");
				CommonData data=new CommonData(theYear,theYear);	   
				list.add(data);
		    }
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    /**
	 * 获取事件对象类型
	 */ 
    public ArrayList getObjecType()
    {
		ArrayList list = new ArrayList();		
		
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
//			list.add(new CommonData("all","全部"));
			list.add(new CommonData("2", ResourceFactory.getProperty("jx.jifen.person")));
			list.add(new CommonData("1", ResourceFactory.getProperty("jx.jifen.group")));						
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    /**
	 *  生效关键事件
	 */ 
    public void compareKeyMatters(String[] matters)
    {

		ContentDAO dao = new ContentDAO(this.cn);
	
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < matters.length; i++)
		{
		    ids.append(matters[i]);
		    ids.append(",");
		}
		ids.setLength(ids.length() - 1);
	
		StringBuffer strSql = new StringBuffer();
		strSql.append("update per_key_event set status='03' where event_id  in (");
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
	 *  退回关键事件
	 */ 
    public void spBackKeyMatters(String[] matters)
    {

		ContentDAO dao = new ContentDAO(this.cn);
	
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < matters.length; i++)
		{
		    ids.append(matters[i]);
		    ids.append(",");
		}
		ids.setLength(ids.length() - 1);
	
		StringBuffer strSql = new StringBuffer();
		strSql.append("update per_key_event set status='01' where event_id  in (");
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
	 * 取得在职人员库的全库的人员列表
	 * @return
	 */
	public ArrayList getUsrDbNameVoList()throws GeneralException
	{

		StringBuffer sql=new StringBuffer();
		sql.append("select dbid,dbname,pre from dbname order by dbid");
		ContentDAO dao=new ContentDAO(this.cn);
		RowSet rs=null;
		ArrayList list=new ArrayList();
		try
		{
			rs=dao.search(sql.toString());
			while(rs.next())
			{	
				String pre = (String)rs.getString("pre");				
				if("Usr".equalsIgnoreCase(pre))
				{
					RecordVo vo=new RecordVo("dbname");				
					vo.setInt("dbid",rs.getInt("dbid"));					
					vo.setString("dbname",rs.getString("dbname"));
					vo.setString("pre",rs.getString("pre"));
					list.add(vo);
				}
			}
			
			if(rs!=null)
		    	rs.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);			
		}

		return list;
	}
}
