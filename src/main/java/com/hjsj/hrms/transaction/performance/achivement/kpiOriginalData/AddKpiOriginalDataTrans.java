package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:AddKpiOriginalDataTrans.java</p>
 * <p>Description:新增KPI原始数据录入记录</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 11:19:26</p>
 * @author JinChunhai
 * @version 5.0
 */

public class AddKpiOriginalDataTrans extends IBusiness
{

	public void execute() throws GeneralException
	{	
			
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");	
		String opt = (String) hm.get("opt"); // 考核周期	
		String acode=(String) hm.get("acode");
		String khcycle = "";
		String khTheyear = "";
		String khthequarter = "";
		String khThemonth = "";		
		if("1".equals(opt))
		{		
			khcycle = (String) hm.get("khcycle"); // 考核周期	
			hm.remove("khcycle");
			khTheyear = (String) hm.get("khTheyear"); // 考核年度	
			hm.remove("khTheyear");
			khthequarter = (String) hm.get("khthequarter"); // 考核半年度/季度	
			hm.remove("khthequarter");
			khThemonth = (String) hm.get("khThemonth"); // 考核月度
			hm.remove("khThemonth");
			
		}else if("2".equals(opt))
		{
			khcycle = (String)this.getFormHM().get("cycle");	// 考核周期	
			khTheyear = (String) this.getFormHM().get("year");  // 考核年度	
			if("1".equalsIgnoreCase(khcycle)) // 半年
				khthequarter = (String)this.getFormHM().get("noYearCycle");								
			else if("2".equalsIgnoreCase(khcycle)) // 季度
				khthequarter = (String)this.getFormHM().get("noYearCycle");								
			else if("3".equalsIgnoreCase(khcycle)) // 月度
				khThemonth = (String)this.getFormHM().get("noYearCycle");								
		}
		
		String objectType = (String) this.getFormHM().get("objectType"); // 对象类别：1 单位 2 人员
		String creator = this.getUserView().getUserName();
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
//		RecordVo vo = new RecordVo("per_kpi_data");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet = null;
		try
		{						
			// 查询符合条件的KPI指标								
			StringBuffer strSql = new StringBuffer();	
			strSql.append("select item_id,b0110 from per_kpi_item where 1=1 ");	
			strSql.append(" and cycle='"+ khcycle +"' ");
			if(acode!=null&&acode.length()>2&&!"root".equals(acode)){
				strSql.append(" and b0110 like '%,"+acode.substring(2)+"%'"); // 归属范围内的KPI指标
			}else{
				strSql.append(getUserViewPrivWhere(this.userView)); // 归属范围内的KPI指标
			}
			
			
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
			
			rowSet = dao.search(strSql.toString());
			ArrayList KpiList = new ArrayList();
		    while (rowSet.next())
		    {
		    	LazyDynaBean bean = new LazyDynaBean();	
				bean.set("item_id", isNull(rowSet.getString("item_id")));
				bean.set("b0110", isNull(rowSet.getString("b0110")));
				KpiList.add(bean);			    	
		    }		    
			
		    // 查询符合条件的需录入数据的对象
			String onlyFild = "";
			String onlyName="";
		    StringBuffer str = new StringBuffer();	
			if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
			{
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				if(onlyFild==null || onlyFild.length()<=0)
					throw new GeneralException("系统没有指定唯一性指标！请指定唯一性指标后再新建或重新引入数据！");					
				onlyName=DataDictionary.getFieldItem(onlyFild).getItemdesc();
				str.append("select b0110,e0122," + onlyFild + ",a0101 from usrA01 where 1=1 ");	
				if(acode!=null&&acode.length()>2&&!"root".equals(acode)){
					str.append(getSql(acode.substring(0, 2),acode.substring(2))); // 操作单位或管理范围内的人	
				}else{
					str.append(getUserViewPersonWhere(this.userView)); // 操作单位或管理范围内的人	
				}
							
				
			}else
			{
				str.append("select codeitemid from organization where codesetid<>'@K' ");	
				if(acode!=null&&acode.length()>2&&!"root".equals(acode)){
					str.append(" and codeitemid like '"+acode.substring(2)+"%'"); // 归属范围内的KPI指标
				}else{
					str.append(getUserViewUnitWhere(this.userView)); // 操作单位或管理范围内的单位	
				}																		
			}				
		   
			rowSet = dao.search(str.toString());
			ArrayList objectList = new ArrayList();
			HashMap map=new HashMap();
			StringBuffer _str=new StringBuffer("");
			StringBuffer _str1=new StringBuffer("");
			HashMap keyMap=new HashMap();
		    while (rowSet.next())
		    {
		    	LazyDynaBean bean = new LazyDynaBean();	
				ArrayList list=new ArrayList();
		    	if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
		    	{
		    		String a0101=rowSet.getString("a0101");
		    		String onlyValue=isNull(rowSet.getString(onlyFild));
		    		if(map.get(onlyValue)!=null)
		    		{
		    			list=(ArrayList) map.get(onlyValue);
		    			list.add(a0101);
		    			keyMap.put(onlyValue, list);
		    		}else{
			    		list.add(a0101)	;
		    		}
		    		map.put(onlyValue,list);	
		    		bean.set("b0110", isNull(rowSet.getString("b0110")));	
		    		bean.set("e0122", isNull(rowSet.getString("e0122")));	
		    		bean.set("object_id",onlyValue);	
		    	}else
					bean.set("object_id", isNull(rowSet.getString("codeitemid")));		    									
				objectList.add(bean);			    	
		    }
			Iterator iterator = (Iterator) keyMap.keySet().iterator();
			String a0101="";
			String key="";
			while (iterator.hasNext()) {
				key=(String) iterator.next();
				ArrayList list=(ArrayList) keyMap.get(key);
				if("".equals(key)){
					for(int i=0;i<list.size();i++){
						a0101=(String) list.get(i);
						if(i==list.size()-1){
							_str1.append(a0101+";<br>");
						}else{
							_str1.append(a0101+",");
						}
					}
				}else{
					for(int i=0;i<list.size();i++){
						a0101=(String) list.get(i);
						if(i==list.size()-1){
							_str.append(a0101+";<br>");
						}else{
							_str.append(a0101+",");
						}
					}
				}

			}
		    if(_str.length()>0&&_str1.length()>0)
		    	throw GeneralExceptionHandler.Handle(new Exception(_str.toString()+" 以上人员有重复的唯一键值("+onlyName+")!<br>"+_str1.toString()+" 以上人员唯一键值("+onlyName+")为空，操作失败!"));    
		    if(_str.length()>0)
		    	throw GeneralExceptionHandler.Handle(new Exception(_str.toString()+" 以上人员有重复的唯一键值("+onlyName+")，操作失败!"));
    		if(_str1.length()>0)
    			throw GeneralExceptionHandler.Handle(new Exception(_str1.toString()+" 以上人员唯一键值("+onlyName+")为空，操作失败!"));
		    
		    
		    // 录入数据
		    for(int i=0;i<objectList.size();i++)
	 		{
	 			LazyDynaBean abean=(LazyDynaBean)objectList.get(i);
	 			String b0110 = "";
	 			String e0122 = "";
				String object_id = "";
				if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
		    	{
					b0110 = (String)abean.get("b0110");
					e0122 = (String)abean.get("e0122");	
		    		object_id = (String)abean.get("object_id");
		    	}else
					object_id = (String)abean.get("object_id");
												
				for(int j=0;j<KpiList.size();j++)
		 		{
					String logoSign = "false";
		 			LazyDynaBean bean=(LazyDynaBean)KpiList.get(j);
					String item_id=(String)bean.get("item_id");					
					String kpiB0110=(String)bean.get("b0110");	
					
					if(kpiB0110!=null && kpiB0110.trim().length()>0)
					{
						kpiB0110 = kpiB0110.substring(1);
						String[] unit = kpiB0110.split(",");
						for(int k = 0; k < unit.length; k++)
						{													    
						    if(objectType == null || objectType.trim().length()<=0 || "2".equalsIgnoreCase(objectType))
					    	{
								if(unit[k].equalsIgnoreCase(b0110) || unit[k].equalsIgnoreCase(e0122))
								{
									logoSign = "true";
									break;
								}
					    	}else
					    	{
					    		if(unit[k].equalsIgnoreCase(object_id))
								{
					    			logoSign = "true";
					    			break;
								}
					    	}
						}
					}else
						logoSign = "true";
					
					//  判断此KPI指标的归属单位
					if("true".equalsIgnoreCase(logoSign))
					{
						// 判断是否已有此条记录 
						String ifornot = repetitionMsg(object_id,item_id,khcycle,khTheyear,khthequarter,khThemonth);
						
						if(!"true".equalsIgnoreCase(ifornot))
						{					
							RecordVo vo = new RecordVo("per_kpi_data");
							
							/**增加一条记录*/
							int maxid=0;				
					        rowSet = dao.search("select max(id) from per_kpi_data");
					        while(rowSet.next())
					        {
					        	String id = rowSet.getString(1);
					        	if((id!=null) && (id.trim().length()>0) && (id.indexOf(".")!=-1))
					        		id=id.substring(0,id.indexOf("."));
					        	if((id!=null) && (id.trim().length()>0))
					        		maxid=Integer.parseInt(id);	
					        }
					        ++maxid;	        															
			/*				
							IDGenerator idg = new IDGenerator(2, this.getFrameconn());
							String num = idg.getId("per_kpi_item.id");
							Integer id = new Integer(num);
			*/				
							vo.setInt("id", maxid);
							
							vo.setString("object_id", object_id);
							vo.setString("object_type", objectType);				
							vo.setString("item_id", item_id);
		//					vo.setString("actual_value", "");
							vo.setString("cycle", khcycle);
							
							if("0".equalsIgnoreCase(khcycle))  // 年度
								vo.setString("theyear", khTheyear);
							else if("1".equalsIgnoreCase(khcycle)) // 半年
							{
								vo.setString("theyear", khTheyear);
								vo.setString("thequarter", khthequarter);
								
							}else if("2".equalsIgnoreCase(khcycle)) // 季度
							{
								vo.setString("theyear", khTheyear);
								vo.setString("thequarter", khthequarter);
								
							}else if("3".equalsIgnoreCase(khcycle)) // 月度
							{
								vo.setString("theyear", khTheyear);
								vo.setString("themonth", khThemonth);
							}
							
							vo.setString("status", "01");
							vo.setString("modusername", creator);				
							vo.setDate("modtime", creatDate);
							
							dao.addValueObject(vo); // 新增记录
						}
						
					}
					
		 		}	
				
	 		}
		    
		    this.getFormHM().put("cycle",khcycle);
		    this.getFormHM().put("year",khTheyear);		   	
			if("1".equalsIgnoreCase(khcycle) || "2".equalsIgnoreCase(khcycle)) // 半年/季度
				this.getFormHM().put("noYearCycle",khthequarter);																	
			else if("3".equalsIgnoreCase(khcycle)) // 月度
				this.getFormHM().put("noYearCycle",khThemonth);
							
			if(rowSet!=null)
		    	rowSet.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 判断是否已有此条记录 	 
	 */
	public String repetitionMsg(String object_id,String item_id,String khcycle,String khTheyear,String khthequarter,String khThemonth)
	{
		String str="false";
		RowSet rowSet = null;							
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);		
			StringBuffer buf = new StringBuffer();									
			buf.append("select object_id from per_kpi_data where object_id='"+object_id+"' and item_id='"+ item_id +"' and cycle='"+ khcycle +"' ");
			
			if("0".equalsIgnoreCase(khcycle))  // 年度
				buf.append(" and theyear='"+ khTheyear +"' ");				
			else if("1".equalsIgnoreCase(khcycle)) // 半年
				buf.append(" and theyear='"+ khTheyear +"' and thequarter='"+ khthequarter +"' ");									
			else if("2".equalsIgnoreCase(khcycle)) // 季度
				buf.append(" and theyear='"+ khTheyear +"' and thequarter='"+ khthequarter +"' ");									
			else if("3".equalsIgnoreCase(khcycle)) // 月度
				buf.append(" and theyear='"+ khTheyear +"' and themonth='"+ khThemonth +"' ");											
			
			rowSet=dao.search(buf.toString());				
		    while(rowSet.next())	
		    {		    	
		    	String flag = rowSet.getString("object_id");				    																	
			    str="true";	    			
		    }
		    
		    if(rowSet!=null)
		    	rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere(UserView userView)
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
						tempSql.append(" or b0110 like '%," + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or b0110 like '%," + temp[i].substring(2) + "%'");
				}
//				buf.append(" and ( " + tempSql.substring(3) + " ) ");
				buf.append(" and ( b0110 is null " + tempSql + " ) ");
			} 
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");					
					else
						buf.append(" and ( b0110 is null or b0110 like '%," + codevalue + "%')");
						
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
						tempSql.append(" or b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or e0122 like '" + temp[i].substring(2) + "%'");
				}
				if(tempSql!=null&&tempSql.length()>0){
					buf.append(" and ( " + tempSql.substring(3) + " ) ");
				}
				
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and b0110 like '" + codevalue + "%'");
						
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
						tempSql.append(" or codeitemid like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or codeitemid like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			} 
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");					
					else
						buf.append(" and codeitemid like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
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
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }	
	public String getSql(String codeid,String codevalue){
		StringBuffer buf=new StringBuffer();
		try{
			String a_code=codeid+codevalue;
			if (a_code.trim().length() > 0)
			{
				if ("UN".equalsIgnoreCase(a_code))
					buf.append(" and 1=1 ");
				else if("UN".equalsIgnoreCase(codeid))
					buf.append(" and b0110 like '" + codevalue + "%'");
				else if("UM".equalsIgnoreCase(codeid))
					buf.append(" and e0122 like '" + codevalue + "%'");
				else if("@K".equalsIgnoreCase(codeid))
					buf.append(" and e01a1 like '" + codevalue + "%'");
				else
					buf.append(" and b0110 like '" + codevalue + "%'");
					
			} else
				buf.append(" and 1=2 ");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
}