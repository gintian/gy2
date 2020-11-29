package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:WarnNoscoreBo.java</p>
 * <p>Description:绩效预警设置</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2012-05-21</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WarnNoscoreBo 
{

	private Connection conn = null;
	private UserView userView = null;

	public WarnNoscoreBo(Connection conn, UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
	}
	
	
	/**
	 * 获得符合当前登录用户预警条件的绩效考核计划
	 * @return
	 */
	public ArrayList getWarnPlanList(HashMap roleMap,String a0100)
	{
		ArrayList planList = new ArrayList();	
		try
		{									
									
			// 获得当前登录用户权限范围内的人员没有在规定期限内制订及审批目标卡的绩效考核计划
			HashMap planCardMap = getNoForAppPlanMap(roleMap,"");
			Set keySet = planCardMap.keySet();
			java.util.Iterator t=keySet.iterator();
			while(t.hasNext())
			{
				String plan_id = (String)t.next();  //键值	    
				String name = (String)planCardMap.get(plan_id);   //value值   				
				
			//	CommonData data = new CommonData(plan_id, (name.substring(0,name.indexOf("`"))+"有 "+(name.substring(name.indexOf("`")+1,name.length()))+" 人未完成目标卡制订及审批"));
				CommonData data = new CommonData(plan_id, name);
				planList.add(data);
			}
			
			// 获得当前登录用户权限范围内的人员没有在规定期限内评分的绩效考核计划
			ArrayList noScorePlanList = getNoScorePlanList(roleMap,a0100);
												
			for (int i = 0; i< noScorePlanList.size(); i++)
			{
				CommonData item = (CommonData) noScorePlanList.get(i);				
				planList.add(item);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return planList;
	}
	
	/**
	 * 获得当前登录用户权限范围内的人员没有在规定期限内制订及审批目标卡的绩效考核计划
	 * @return
	 */
	public HashMap getNoForAppPlanMap(HashMap roleMap,String a0100)
	{
		HashMap planMap = new HashMap();
		HashMap noPlanMap = new HashMap();
		try
		{
			// 获得当前登录用户权限范围内的人员没有在规定期限内制订目标卡的绩效考核计划
			ArrayList noFormulateCardPlanList = getNoFormulateCardPlanList(roleMap,a0100);			
			// 获得当前登录用户权限范围内的人员没有在规定期限内审批目标卡的绩效考核计划
			ArrayList noApproveCardPlanList = getNoApproveCardPlanList(roleMap,a0100);
						
			for (int i = 0; i< noFormulateCardPlanList.size(); i++)
			{
				CommonData item = (CommonData) noFormulateCardPlanList.get(i);	
				String plan_id = item.getDataValue();
				String name = item.getDataName();
			//	planMap.put(plan_id, name);
				noPlanMap.put(plan_id, name);
				planMap.put(plan_id, (name.substring(0,name.indexOf("`"))+"有 "+(name.substring(name.indexOf("`")+1,name.length()))+" 人未完成目标卡制订"));
			}
			for (int j = 0; j< noApproveCardPlanList.size(); j++)
			{
				CommonData item = (CommonData) noApproveCardPlanList.get(j);	
				String plan_id = item.getDataValue();
				String name = item.getDataName();
				int nameNum = Integer.parseInt(name.substring(name.indexOf("`")+1,name.length()));
				if(noPlanMap.get(plan_id)!=null)
				{
					String dataName = (String)noPlanMap.get(plan_id);
					int dataNameNum = Integer.parseInt(dataName.substring(dataName.indexOf("`")+1,dataName.length()));
				//	planMap.put(plan_id, (dataName.substring(0,dataName.indexOf("`"))+"`"+(nameNum+dataNameNum)));	
					planMap.put(plan_id, (dataName.substring(0,dataName.indexOf("`"))+"有 "+dataNameNum+" 人未完成目标卡制订、有 "+nameNum+" 人未完成目标卡审批"));
				}else
				{
				//	planMap.put(plan_id, name);
					planMap.put(plan_id, (name.substring(0,name.indexOf("`"))+"有 "+(name.substring(name.indexOf("`")+1,name.length()))+" 人未完成目标卡审批"));
				}
			}						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return planMap;
	}
	
	/**
	 * 获得当前登录用户权限范围内的人员没有在规定期限内评分的绩效考核计划
	 * @return
	 */
	public ArrayList getNoScorePlanList(HashMap roleMap,String a0100)
	{
		ArrayList planList = new ArrayList();
		RowSet rowSet = null;	
		RowSet rs = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 系统当前日期
		try
		{						
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqlstr = new StringBuffer();			
			sqlstr.append("select pm.plan_id,count(distinct pm.mainbody_id) num from per_mainbody pm,per_plan pa,per_plan_body ppb ");
			sqlstr.append(" where ( pa.busitype is null or pa.busitype='' or pa.busitype = '0') and (pa.status=4 or pa.status=6) and pm.plan_id=pa.plan_id ");	
			sqlstr.append(" and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and (ppb.isgrade is null or ppb.isgrade='0') ");
			if(a0100!=null && a0100.trim().length()>0) {
                sqlstr.append(" and pm.mainbody_id='" + a0100 + "' ");
            } else {
                sqlstr.append(getUserViewPrivWhere(this.userView,"pm"));
            }
			sqlstr.append(" and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status=1) ");
			sqlstr.append(" group by pm.plan_id order by pm.plan_id desc ");
			
			rowSet = dao.search(sqlstr.toString());
			while(rowSet.next())
			{				
				String plan_id = rowSet.getString("plan_id");
				String number = rowSet.getString("num");
								
				LoadXml loadxml = new LoadXml(this.conn, plan_id);
				Hashtable params = loadxml.getDegreeWhole();
				String gradeByBodySeq = (String)params.get("GradeByBodySeq"); // 按考核主体顺序号控制评分流程(True, False默认为False)				 
				String noApproveTargetCanScore = (String)params.get("NoApproveTargetCanScore"); // 目标卡未审批也允许打分 True, False, 默认为 False
				ArrayList warnRoleScopeList = (ArrayList)params.get("WarnRoleScopeList");
				String delayTime = ""; // 考核评分延期多少天预警  
			    String roleScope = ""; // 考核评分预警对象编号（角色）
			    boolean haveRole = false; // 判断关联的角色是否符合预警对象
				if(warnRoleScopeList!=null && warnRoleScopeList.size()>0)
				{
					for (int i = 0; i < warnRoleScopeList.size(); i++)
			    	{
			    		LazyDynaBean bean = (LazyDynaBean) warnRoleScopeList.get(i);
			    		String opt = (String) bean.get("opt");
			    		if(opt!=null && opt.trim().length()>0 && "2".equalsIgnoreCase(opt))
			    		{
			    			delayTime = (String) bean.get("delayTime");
			    			roleScope = (String) bean.get("roleScope");
			    			if(roleScope!=null && roleScope.trim().length()>0)
			    			{
				    			String[] matters = roleScope.split(",");
				    			for (int j = 0; j < matters.length; j++)
				    			{
				    				if(roleMap.get(matters[j])!=null)
				    				{
				    					haveRole = true;
				    					break;
				    				}
				    			}
				    			if(haveRole) {
                                    break;
                                }
			    			}
			    		}   		   		   		
			    	}
				}
				if(a0100!=null && a0100.trim().length()>0) // 如果是弹出框不受角色控制
                {
                    haveRole = true;
                }
				// 判断关联的角色是否符合预警对象
				if(!haveRole) {
                    continue;
                }
				
				
				String deferDays = ""; // 延期天数
				RecordVo vo = getPlanVo(plan_id);
				
				if(gradeByBodySeq!=null && gradeByBodySeq.trim().length()>0 && "True".equalsIgnoreCase(gradeByBodySeq) && vo.getInt("method")==2)
				{
					StringBuffer str = new StringBuffer();						
					if(a0100!=null && a0100.trim().length()>0)
					{
						str.append("select pm.object_id,pa.sub_date from per_object pm,per_mainbody pa,per_plan_body pp ");
						str.append(" where pm.plan_id="+plan_id+" and pm.plan_id=pa.plan_id and pm.object_id=pa.object_id and pa.body_id=pp.body_id ");
						if(noApproveTargetCanScore!=null && noApproveTargetCanScore.trim().length()>0 && "False".equalsIgnoreCase(noApproveTargetCanScore)) {
                            str.append(" and pm.sp_flag='03' ");
                        }
						str.append(" and pa.plan_id=pp.plan_id ");
						str.append(" and pa.object_id not in ( select c.object_id from per_mainbody c where c.plan_id="+plan_id+" and exists ( ");
						str.append("                           select null from per_mainbody a where a.object_id=c.object_id and c.seq<a.seq and a.plan_id="+plan_id+" ");
						str.append("                           and a.status<>2 and a.mainbody_id='" + a0100 + "' ) and c.status<>2 ) ");
						str.append(" and not exists (select null from per_mainbody pmb where pmb.plan_id=pa.plan_id and pmb.object_id=pa.object_id and pmb.mainbody_id<>pa.mainbody_id ");
						str.append("                 and pmb.mainbody_id='" + a0100 + "' and pmb.seq=pa.seq ) ");
						str.append(" and pa.object_id not in (select object_id from per_mainbody where plan_id="+plan_id+" and mainbody_id='" + a0100 + "' and status='2' ) ");
						str.append(" and pa.object_id not in ( select object_id from per_mainbody where plan_id="+plan_id+" and mainbody_id='" + a0100 + "' ");
						str.append("	                       and body_id in( select body_id from per_plan_body where plan_id="+plan_id+" and isgrade='1')) ");
						str.append(" order by pm.object_id,pa.sub_date desc ");
					}
					else
					{
						str.append(" select max(sub_date) sub_date,object_id from per_mainbody pmb0 where pmb0.plan_id="+plan_id+" ");
						if(noApproveTargetCanScore!=null && noApproveTargetCanScore.trim().length()>0 && "False".equalsIgnoreCase(noApproveTargetCanScore)) {
                            str.append(" and pmb0.object_id in ( select object_id from per_object where plan_id="+plan_id+" and sp_flag='03') ");
                        }
					//	str.append(" and "+ Sql_switcher.isnull("pmb0.seq", "0") +" is not null ");	
						str.append(" and nullif(pmb0.seq,0) is not null " );
						str.append(" and not exists (select null from  ");
						str.append("                   (  select * from per_mainbody b where b.plan_id="+plan_id+"  ");
						str.append("                      and exists (select null from  ");
						str.append("                      (  select pm.object_id,min(seq) seq from per_mainbody pm ");
						str.append("                         where plan_id="+plan_id+" "+getUserViewPrivWhere(this.userView,"pm")+" ");
						if(noApproveTargetCanScore!=null && noApproveTargetCanScore.trim().length()>0 && "False".equalsIgnoreCase(noApproveTargetCanScore)) {
                            str.append("                     and pm.object_id in ( select object_id from per_object where plan_id="+plan_id+" and sp_flag='03') ");
                        }
					//	str.append("                         and "+ Sql_switcher.isnull("pm.seq", "0") +" is not null " );
						str.append("                         and nullif(pm.seq,0) is not null " );
						str.append("                         and pm.status<>2 group by pm.object_id ");
						str.append("                      ) a where a.object_id=b.object_id and a.seq=b.seq ) and b.status<>2");						
						str.append("                   ) c where c.seq=pmb0.seq and c.object_id=pmb0.object_id  ");
						str.append("                )  ");
						str.append(" group by object_id ");
					//	str.append("  ");
					}
					
					rs = dao.search(str.toString());
					String sub_date = String.valueOf(vo.getDate("execute_date"));
					int count = 0;
					HashMap objMap = new HashMap();
					boolean haveOutTime = false; // 判断审批时间是否超过预警设置时间
					while(rs.next())
					{				
						String object_id = isNull(rs.getString("object_id"));
						String date = isNull(String.valueOf(rs.getDate("sub_date")));
						
						sub_date = String.valueOf(vo.getDate("execute_date"));
						if(date!=null && date.trim().length()>0) {
                            sub_date = date;
                        }
						if(objMap.get(object_id)==null)
						{
							if(isNull(sub_date).trim().length()>0 && isNull(delayTime).trim().length()>0)
							{
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
								Date nowDate = df.parse(creatDate);
								Date d2 = df.parse(String.valueOf(sub_date));
								long diff = nowDate.getTime() - d2.getTime();
								long days = diff / (1000 * 60 * 60 * 24);
				
								// 判断是否超过预警期限
								if(days>=Long.parseLong(delayTime))
								{
									deferDays = String.valueOf((days-Long.parseLong(delayTime)));
									haveOutTime = true;
									objMap.put(object_id, "obj");
								}
								else
								{
									count++;
									objMap.put(object_id, "obj");
								}
							}												
						}
						
					}
					if(!haveOutTime) {
                        continue;
                    }
					
					number = String.valueOf(objMap.size()-count);
				}
				else
				{					
					if(isNull(String.valueOf(vo.getDate("execute_date"))).trim().length()>0 && isNull(delayTime).trim().length()>0)
					{
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
						Date nowDate = df.parse(creatDate);
						Date d2 = df.parse(String.valueOf(vo.getDate("execute_date")));
						long diff = nowDate.getTime() - d2.getTime();
						long days = diff / (1000 * 60 * 60 * 24);
		
						// 判断是否超过预警期限
						if(days<Long.parseLong(delayTime)) {
                            continue;
                        } else {
                            deferDays = String.valueOf((days-Long.parseLong(delayTime)));
                        }
					}else {
                        continue;
                    }
				}
				
				if(a0100!=null && a0100.trim().length()>0)
				{
					CommonData data = new CommonData(deferDays+"@"+plan_id, (vo.getString("name")+"有 "+number+" 人未完成评分"));
					planList.add(data);	
				}else
				{
					CommonData data = new CommonData("@"+plan_id, (vo.getString("name")+"有 "+number+" 人未完成评分"));
					planList.add(data);	
				}
			}									
			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return planList;
	}
	
	
	/**
	 * 获得当前登录用户权限范围内的人员没有在规定期限内制定目标卡的绩效考核计划
	 * @return
	 */
	public ArrayList getNoFormulateCardPlanList(HashMap roleMap,String a0100)
	{
		ArrayList planList = new ArrayList();
		RowSet rowSet = null;	
		RowSet rs = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 系统当前日期
		try
		{						
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqlstr = new StringBuffer();			
			sqlstr.append("select pm.plan_id,count(pm.plan_id) num from per_object pm,per_plan pa ");
			sqlstr.append(" where ( pa.busitype is null or pa.busitype='' or pa.busitype = '0') and pa.method=2 and pa.status=8 and pm.plan_id=pa.plan_id ");
			if(a0100!=null && a0100.trim().length()>0) // 考虑团队计划的情况
            {
                sqlstr.append(" and (pm.object_id='" + a0100 + "' or pm.object_id in (select object_id from per_mainbody where mainbody_id='" + a0100 + "' and body_id = '-1')) ");
            } else {
                sqlstr.append(getUserViewPrivWhere(this.userView,"pm"));
            }
			sqlstr.append(" and (pm.sp_flag is null or pm.sp_flag='' or pm.sp_flag='01' or pm.sp_flag='07') ");
			sqlstr.append(" group by pm.plan_id order by pm.plan_id desc ");
						
			rowSet = dao.search(sqlstr.toString());
			while(rowSet.next())
			{				
				String plan_id = rowSet.getString("plan_id");
				String number = rowSet.getString("num");
								
				LoadXml loadxml = new LoadXml(this.conn, plan_id);
				Hashtable params = loadxml.getDegreeWhole();
				ArrayList warnRoleScopeList = (ArrayList)params.get("WarnRoleScopeList");
				String delayTime = ""; // 目标卡制定及审批延期多少天预警  
			    String roleScope = ""; // 目标卡制定及审批预警对象编号（角色）
			    boolean haveRole = false; // 判断关联的角色是否符合预警对象
				if(warnRoleScopeList!=null && warnRoleScopeList.size()>0)
				{
					for (int i = 0; i < warnRoleScopeList.size(); i++)
			    	{
			    		LazyDynaBean bean = (LazyDynaBean) warnRoleScopeList.get(i);
			    		String opt = (String) bean.get("opt");
			    		if(opt!=null && opt.trim().length()>0 && "1".equalsIgnoreCase(opt))
			    		{
			    			delayTime = (String) bean.get("delayTime");
			    			roleScope = (String) bean.get("roleScope");
			    			if(roleScope!=null && roleScope.trim().length()>0)
			    			{
				    			String[] matters = roleScope.split(",");
				    			for (int j = 0; j < matters.length; j++)
				    			{
				    				if(roleMap.get(matters[j])!=null)
				    				{
				    					haveRole = true;
				    					break;
				    				}
				    			}
				    			if(haveRole) {
                                    break;
                                }
			    			}
			    		}   		   		   		
			    	}
				}
				if(a0100!=null && a0100.trim().length()>0) // 如果是弹出框不受角色控制
                {
                    haveRole = true;
                }
				// 判断关联的角色是否符合预警对象
				if(!haveRole) {
                    continue;
                }
				
				
				StringBuffer str = new StringBuffer();			
				str.append("select po.object_id,po.report_date from per_object po where po.plan_id="+plan_id+" and po.sp_flag='07'");
				if(a0100!=null && a0100.trim().length()>0) {
                    str.append(" and po.object_id ='" + a0100 + "' ");
                } else {
                    str.append(getUserViewPrivWhere(this.userView,"po"));
                }
				str.append(" order by po.report_date desc ");
				  			
				rs = dao.search(str.toString());
				int count = 0;
				String deferDays = ""; // 延期天数
				HashMap objMap = new HashMap();
				boolean haveObject = false; 
				boolean haveOutTime = false; // 判断审批时间是否超过预警设置时间
				while(rs.next())
				{	
					haveObject = true;
					String object_id = rs.getString("object_id");
					String report_date = String.valueOf(rs.getDate("report_date"));
					
					if(objMap.get(object_id)==null)
					{
						if(isNull(report_date).trim().length()>0 && isNull(delayTime).trim().length()>0)
						{
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
							Date nowDate = df.parse(creatDate);
							Date d2 = df.parse(String.valueOf(report_date));
							long diff = nowDate.getTime() - d2.getTime();
							long days = diff / (1000 * 60 * 60 * 24);
			
							// 判断是否超过预警期限
							if(days>=Long.parseLong(delayTime))
							{
								deferDays = String.valueOf((days-Long.parseLong(delayTime)));								
								objMap.put(object_id, "obj");
							}
							else
							{
								haveOutTime = true;
								objMap.put(object_id, "obj");
								count++;
							}
						}
						else {
                            haveOutTime = true;
                        }
					}					
				}
				if(haveOutTime) {
                    continue;
                }
				
				RecordVo vo = getPlanVo(plan_id);
				if(!haveObject)
				{												
					if(isNull(String.valueOf(vo.getDate("distribute_date"))).trim().length()>0 && isNull(delayTime).trim().length()>0)
					{
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
						Date nowDate = df.parse(creatDate);
						Date d2 = df.parse(String.valueOf(vo.getDate("distribute_date")));
						long diff = nowDate.getTime() - d2.getTime();
						long days = diff / (1000 * 60 * 60 * 24);
		
						// 判断是否超过预警期限
						if(days<Long.parseLong(delayTime)) {
                            continue;
                        } else {
                            deferDays = String.valueOf((days-Long.parseLong(delayTime)));
                        }
					}else {
                        continue;
                    }
				}
				
				if(a0100!=null && a0100.trim().length()>0)
				{
					CommonData data = new CommonData(deferDays+"$"+plan_id, vo.getString("name")+"`"+(Integer.parseInt(number)-count));
					planList.add(data);
				}else
				{
					CommonData data = new CommonData("$"+plan_id, vo.getString("name")+"`"+(Integer.parseInt(number)-count));
					planList.add(data);
				}
			}									
			
			if(rowSet!=null) {
                rowSet.close();
            }
			if(rs!=null) {
                rs.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return planList;
	}
	
	/**
	 * 获得当前登录用户权限范围内的人员没有在规定期限内审批目标卡的绩效考核计划
	 * @return
	 */
	public ArrayList getNoApproveCardPlanList(HashMap roleMap,String a0100)
	{
		ArrayList planList = new ArrayList();
		RowSet rowSet = null;
		RowSet rs = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 系统当前日期
		try
		{						
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqlstr = new StringBuffer();			
			sqlstr.append("select distinct pm.plan_id from per_object pm,per_mainbody pa where pm.object_id=pa.object_id ");
			sqlstr.append(" and pm.currappuser=pa.mainbody_id and pm.plan_id=pa.plan_id and (pm.sp_flag='02' or pm.sp_flag='07') ");
			if(a0100!=null && a0100.trim().length()>0) {
                sqlstr.append(" and pa.mainbody_id='" + a0100 + "' ");
            } else {
                sqlstr.append(getUserViewPrivWhere(this.userView,"pa"));
            }
			sqlstr.append(" and pm.plan_id in (select plan_id from per_plan where ");
			sqlstr.append(" ( busitype is null or busitype='' or busitype = '0') and method=2 and status=8) ");
			sqlstr.append(" order by pm.plan_id desc ");
			  			
			rowSet = dao.search(sqlstr.toString());
			while(rowSet.next())
			{				
				String plan_id = rowSet.getString("plan_id");
								
				LoadXml loadxml = new LoadXml(this.conn, plan_id);
				Hashtable params = loadxml.getDegreeWhole();
				ArrayList warnRoleScopeList = (ArrayList)params.get("WarnRoleScopeList");
				String delayTime = ""; // 目标卡制定及审批延期多少天预警  
			    String roleScope = ""; // 目标卡制定及审批预警对象编号（角色）
			    boolean haveRole = false; // 判断关联的角色是否符合预警对象
				if(warnRoleScopeList!=null && warnRoleScopeList.size()>0)
				{
					for (int i = 0; i < warnRoleScopeList.size(); i++)
			    	{
			    		LazyDynaBean bean = (LazyDynaBean) warnRoleScopeList.get(i);
			    		String opt = (String) bean.get("opt");
			    		if(opt!=null && opt.trim().length()>0 && "1".equalsIgnoreCase(opt))
			    		{
			    			delayTime = (String) bean.get("delayTime");
			    			roleScope = (String) bean.get("roleScope");
			    			if(roleScope!=null && roleScope.trim().length()>0)
			    			{
				    			String[] matters = roleScope.split(",");
				    			for (int j = 0; j < matters.length; j++)
				    			{
				    				if(roleMap.get(matters[j])!=null)
				    				{
				    					haveRole = true;
				    					break;
				    				}
				    			}
				    			if(haveRole) {
                                    break;
                                }
			    			}
			    		}   		   		   		
			    	}
				}
				if(a0100!=null && a0100.trim().length()>0) // 如果是弹出框不受角色控制
                {
                    haveRole = true;
                }
				// 判断关联的角色是否符合预警对象
				if(!haveRole) {
                    continue;
                }
				
				
				StringBuffer str = new StringBuffer();			
				str.append("select pm.object_id,CASE WHEN (pa.sp_date is null or pa.sp_date='') then pm.report_date else pa.sp_date end as sp_date ");
				str.append(" from per_object pm,per_mainbody pa where pm.plan_id="+plan_id+" and (pm.sp_flag='02' or pm.sp_flag='07') ");
			//	str.append(" and pm.currappuser=pa.mainbody_id ");
				if(a0100!=null && a0100.trim().length()>0)
				{
					str.append(" and pa.object_id in (select object_id from per_mainbody where plan_id="+plan_id+" and mainbody_id='" + a0100 + "') ");					
				//	str.append(" and pa.mainbody_id='" + a0100 + "' ");					
				}else {
                    str.append(getUserViewPrivWhere(this.userView,"pa"));
                }
				str.append(" and pm.plan_id=pa.plan_id and pm.object_id=pa.object_id ");
				str.append(" order by sp_date desc ");
				  			
				rs = dao.search(str.toString());
				int count = 0;
				String deferDays = ""; // 延期天数
				HashMap objMap = new HashMap();
				boolean haveOutTime = false; // 判断审批时间是否超过预警设置时间
				while(rs.next())
				{				
					String object_id = rs.getString("object_id");
					String sp_date = String.valueOf(rs.getDate("sp_date"));
					
					if(objMap.get(object_id)==null)
					{
						if(a0100==null || a0100.trim().length()<=0)  
						{
							String newSp_date = getNewDate(plan_id,object_id); 
							if(isNull(newSp_date).trim().length()>0) {
                                sp_date = newSp_date;
                            }
						}
						
						if(isNull(sp_date).trim().length()>0 && isNull(delayTime).trim().length()>0)
						{
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
							Date nowDate = df.parse(creatDate);
							Date d2 = df.parse(String.valueOf(sp_date));
							long diff = nowDate.getTime() - d2.getTime();
							long days = diff / (1000 * 60 * 60 * 24);
			
							// 判断是否超过预警期限
							if(days>=Long.parseLong(delayTime))
							{
								deferDays = String.valueOf((days-Long.parseLong(delayTime)));
								haveOutTime = true;
								objMap.put(object_id, "obj");
							}
							else
							{
								objMap.put(object_id, "obj");
								count++;
							}
						}												
					}					
				}
				if(!haveOutTime) {
                    continue;
                }
																
				RecordVo vo = getPlanVo(plan_id);					
				if(a0100!=null && a0100.trim().length()>0)
				{
					CommonData data = new CommonData(deferDays+"*"+plan_id, vo.getString("name")+"`"+(objMap.size()-count));
					planList.add(data);
				}else
				{
					CommonData data = new CommonData("$"+plan_id, vo.getString("name")+"`"+(objMap.size()-count));
					planList.add(data);
				}
			}									
			
			if(rowSet!=null) {
                rowSet.close();
            }
			if(rs!=null) {
                rs.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return planList;
	}
	
	public String getNewDate(String plan_id,String object_id)
	{
		String sp_date = "";
		try
		{			 
			StringBuffer sql = new StringBuffer();
			sql.append("select sp_date from per_mainbody where plan_id = "+plan_id+" and object_id = '" + object_id + "' order by sp_date desc ");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			int count = 0; 
			while(rs.next())
			{
				if(count==0) {
                    sp_date = isNull(String.valueOf(rs.getDate("sp_date")));
                }
				count++;
			}					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sp_date;
	}
	
	/**
	 * plan_id 计划号
	 * 获得某编号的考核计划的所有信息
	 */
	public RecordVo getPlanVo(String plan_id)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere(UserView userView,String base)
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
					if (temp[i].length()>2&& "UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or "+base+".b0110 like '" + temp[i].substring(2) + "%'");
                    } else if (temp[i].length()>2&& "UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or "+base+".e0122 like '" + temp[i].substring(2) + "%'");
                    }
				}
				if(tempSql.length()>3) {
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
                    {
                        buf.append(" and 1=1 ");
                    } else if("UN".equalsIgnoreCase(codeid)) {
                        buf.append(" and "+base+".b0110 like '" + codevalue + "%'");
                    } else if("UM".equalsIgnoreCase(codeid)) {
                        buf.append(" and "+base+".e0122 like '" + codevalue + "%'");
                    } else if("@K".equalsIgnoreCase(codeid)) {
                        buf.append(" and "+base+".e01a1 like '" + codevalue + "%'");
                    } else {
                        buf.append(" and "+base+".b0110 like '" + codevalue + "%'");
                    }
						
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}

		return str;		
	}
	
}

