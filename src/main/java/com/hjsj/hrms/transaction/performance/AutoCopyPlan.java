package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:AutoCopyPlan.java</p>
 * <p>Description>:自动复制考核计划</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2013-06-25 下午03:56:27</p>
 * <p>@version: 7.0</p>
 * <p>@author: JinChunhai
 */

public class AutoCopyPlan implements Job 
{

	ExamPlanBo khPlanBo = null;
	
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		Connection conn = null;
		RowSet rowSet = null;
		RowSet rs = null;
		try 
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			khPlanBo = new ExamPlanBo(conn);
			
			String autoTemplateid = SystemConfig.getPropertyValue("autoTemplateid"); // 模板编号
			if(autoTemplateid!=null && autoTemplateid.trim().length()>0) 
			{
				String[] matters = autoTemplateid.split(",");
				for (int i = 0; i < matters.length; i++)
				{
					String template_id = matters[i];
				
					StringBuffer buf = new StringBuffer("");
					buf.append("select plan_id,name,create_date from per_plan ");	
					buf.append(" where template_id = '"+ template_id +"' and (busitype is null or busitype='' or busitype='0') ");
					buf.append(" order by create_date desc ");
					rowSet = dao.search(buf.toString());
					if (rowSet.next())
					{
						String plan_id = isNull(rowSet.getString("plan_id")); 
						RecordVo planVo = khPlanBo.getPerPlanVo(plan_id);
						String object_type = planVo.getString("object_type"); 
						String method = planVo.getString("method");
						
						// 复制基本信息 
						String newId = copySelf(plan_id,template_id,conn,rs);
						// 复制考核主体类别
						copyKhMainbodytype(newId,plan_id,dao,rs);
						// 复制考核对象
						copyKhObject(newId,plan_id,object_type,conn,rs);																							
						// 复制计划的考核主体
						copyKhMainbody(newId,plan_id,"",conn,rs);												
						// 复制考核主体的动态权重
						copyBodyDynaRank(newId,plan_id,"",dao);//per_dyna_bodyrank													
						// 复制考核指标要素的动态权重
						CopyPointDynaRank(newId, plan_id,dao);
																										
						// 复制考核对象类别的动态项目分值/权重
						if ("2".equals(method))
						{							
							copyDynaItem(newId,plan_id,dao);							
						}	
							
						// 先新建表per_pointpriv_xxx
						khPlanBo.cper_pointpriv(newId);
						// 同步考核对象与考核主体						
						khPlanBo.synObjectAndBody(newId, "");									
						// 复制主体的考核指标的权限
						copyKhMainbodyPri(newId, plan_id, "",conn,rs);
													
						if ("2".equals(method))
						{							
							// 先新建表per_itempriv_xxx
							String fieldStr = cper_itempriv(newId,dao,conn,rs);														
							try
							{
								// 复制考核对象主体类别项目权限
								String insertSql = "insert into PER_ITEMPRIV_" + newId + "(" + fieldStr + ") select " + fieldStr + " from PER_ITEMPRIV_" + plan_id;
								DbWizard dbWizard = new DbWizard(conn);
								if (dbWizard.isExistTable("PER_ITEMPRIV_" + plan_id, false))// 存在老计划的项目权限表才复制到新表		
									dao.insert(insertSql, new ArrayList());
									
							} catch (SQLException e)
							{
								e.printStackTrace();
								throw GeneralExceptionHandler.Handle(e);
							}						
						}						
						//为了兼容cs 在此把结果表建了
						DbWizard dbWizard = new DbWizard(conn);
						Table table = null;
						if (!dbWizard.isExistTable("PER_RESULT_" + newId, false))
						{
							table = khPlanBo.cper_reslut_sql(newId,"0");
							dbWizard.createTable(table);
						}
						PerEvaluationBo pb = new PerEvaluationBo(conn);
						// 检查per_result_planid表中有没有调整后的表结构的字段，若没有就创建  JinChunhai 2011.03.08
						pb.editResult(newId);									
	
						
						
						
						
						// 对于另存得到的计划 做一些补充操作 为了在实施中的数据完整性
						// 有主体没有主体的考核指标权限补充
						String sql = "select * from per_mainbody where plan_id=" + newId;
						rs = dao.search(sql);
						if(rs.next())//主体表有数据
						{
							if (!dbWizard.isExistTable("per_pointpriv_" + newId, false))//相应的主体指标权限没有
							{
								khPlanBo.cper_pointpriv(newId);
								khPlanBo.synObjectAndBody(newId, "");
								khPlanBo.setKhMainbodyDefaultPri(newId);
							}
						}				
						// 创建per_table_计划号临时表
						if (!dbWizard.isExistTable("per_table_" + newId, false))
						{
							table = cper_table_sql(newId);
							dbWizard.createTable(table);						
							// 添加索引
							dao.update("create index per_table_" + newId+"_idx on per_table_" + newId+" (object_id,mainbody_id)");
						}
						// 创建per_reslut_计划号临时表
						if (!dbWizard.isExistTable("PER_RESULT_" + newId, false))
						{
							table = khPlanBo.cper_reslut_sql(newId,"0");
							dbWizard.createTable(table);
						}
					//	PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);
						// 检查per_result_planid表中有没有调整后的表结构的字段，若没有就创建  JinChunhai 2011.03.08
					//	pb.editResult(planId);
	
						// 创建per_pointpriv_计划号临时表
						// 由于另存的时候，当选中复制考核主体的指标权限的复选框时候也会创建该临时表，所以要先判断一下
						if (!dbWizard.isExistTable("per_pointpriv_" + newId, false))
						{
							khPlanBo.cper_pointpriv(newId);
						}
						
						if ("2".equals(method))// 目标管理计划要生成项目权限数据
						{
							if (!dbWizard.isExistTable("PER_ITEMPRIV_" + newId, false))
							{
								String fieldStr = cper_itempriv(newId,dao,conn,rs);
								// 对于另存的计划 如果复制了考核对象 却没有复制对象的各主体类别项目权限 在此处按默认有权限设置项目权限
								setDefaultItemPriv(newId, dao, fieldStr,rs);
							}
							//0 数据采集 1 网上打分 目标计划只能是网上打分 这里加如下代码是防止另存360后又改成了目标计划的时候 该参数没有相应改变
							LoadXml loadXml=new LoadXml(conn,newId);
							Hashtable params = loadXml.getDegreeWhole();					
							String scoreWay=(String)params.get("scoreWay");
							if("0".equals(scoreWay))
								scoreWay="1";						
							loadXml.saveAttribute("PerPlan_Parameter","ScoreWay",scoreWay);
						}
	
						// 更新绩效计划状态为发布状态
						dao.update("update per_plan set status='3' where plan_id=" + newId);
						
					}
				}
			}
								
		}catch (Exception e) 
		{
			e.printStackTrace();
		}finally
		{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(conn);
		}
	}
	
	/**
	 * 复制基本信息
	 * 
	 * @param planId
	 * @return
	 * @throws GeneralException
	 */
	public String copySelf(String planId,String template_id,Connection conn,RowSet rowSet) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(conn);				
		String creator = "su";
		IDGenerator idg = new IDGenerator(2,conn);
		String newPlanId = idg.getId("per_plan.plan_id");
		Integer planid = new Integer(newPlanId);
		newPlanId = planid.toString();
		ExamPlanBo bo = new ExamPlanBo(conn);
	//	RecordVo vo = bo.getPerPlanVo(planId);
		String planName = "";
		Date start_date = null;
		Date end_date = null;
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select plan_id,name,create_date,start_date,end_date from per_plan ");	
			buf.append(" where template_id = '"+ template_id +"' and (busitype is null or busitype='' or busitype='0') ");
			buf.append(" order by create_date desc ");
			rowSet = dao.search(buf.toString());
			if (rowSet.next())
			{
			//	start_date = isNull(PubFunc.FormatDate(rowSet.getDate("start_date")));
			//	end_date = isNull(PubFunc.FormatDate(rowSet.getDate("end_date")));
				
				planName = rowSet.getString("name");				
				if(rowSet.getDate("start_date")!=null)
					start_date = rowSet.getDate("start_date");
				if(rowSet.getDate("end_date")!=null)
					end_date = rowSet.getDate("end_date");
			}
			if(end_date!=null)
			{
				start_date = OperateDate.addDay(end_date, 1);
				end_date = OperateDate.addDay(end_date, 1);
			}
			else
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				start_date = sdf.parse(PubFunc.getStringDate("yyyy-MM-dd"));
				end_date = sdf.parse(PubFunc.getStringDate("yyyy-MM-dd"));
			}
			String startDate = isNull(PubFunc.FormatDate(start_date,"yyyy-MM-dd HH:mm:ss"));
			String endDate = isNull(PubFunc.FormatDate(end_date,"yyyy-MM-dd HH:mm:ss"));
			String _endDate = isNull(PubFunc.FormatDate(end_date,"yyyy-MM-dd"));
			planName = _endDate+planName.substring(10);
			
			
			String fields = "theyear,themonth,thequarter,descript,template_id,cycle,awoke_days,target,content,flow,result,";
			fields+="object_type,B0110,plan_type,parameter_content,gather_type,thefile,plan_visibility,";//agree_date,agree_user,agree_idea,approve_result, 去掉批准日期、批准人、批准意见、审批结果
			fields+="file_ext,method,busitype";				
			
			String newplanName = planName;
			if(newplanName.getBytes().length>50)
			{
				byte[] temp=new byte[50];
				for(int i=0;i<50;i++)
					temp[i]=newplanName.getBytes()[i];
				newplanName=new String (temp);	
			}	
			
			StringBuffer strsql = new StringBuffer();
			strsql.append("insert into per_plan (plan_id,name,status,"+fields+",start_date,end_date,create_user,create_date)");
			strsql.append("select '"+newPlanId+"','"+newplanName+"',0,"+fields+",'"+startDate+"','"+endDate+"','"+creator+"',"+Sql_switcher.sqlNow()+" from per_plan where plan_id="+planId);		
			dao.update(strsql.toString());

			strsql.setLength(0);
		/*	
			strsql.append("update per_plan set create_date=? where plan_id=?");
			
			ArrayList list = new ArrayList();
			list.add(java.sql.Date.valueOf(creatDate));
			list.add(newPlanId);
			
			dao.update(strsql.toString(), list);*/
			
			String sql = "update per_plan set a0000=a0000+1 where a0000 is not null" ;
			try
			{
				dao.update(sql);
				sql = "update per_plan set a0000=((select min(a0000) from per_plan)-1) where plan_id=" + newPlanId;
				dao.update(sql);				
			} catch (SQLException e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return newPlanId;
	}
	
	public void copyKhMainbodytype(String newId,String planId,ContentDAO dao,RowSet rowSet) throws GeneralException
	{		
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_plan_body where plan_id=");
		strsql.append(planId);
		try
		{
			rowSet = dao.search(strsql.toString());
			while (rowSet.next())
			{
				RecordVo vo = new RecordVo("per_plan_body");
				vo.setInt("body_id", rowSet.getInt("body_id"));
				vo.setInt("plan_id",Integer.parseInt(newId));
				vo.setDouble("rank", rowSet.getDouble("rank"));
				vo.setInt("flag", rowSet.getInt("flag"));
				vo.setInt("lead", rowSet.getInt("lead"));
				vo.setDouble("voterank", rowSet.getDouble("voterank"));
				vo.setInt("isgrade", rowSet.getInt("isgrade"));
				vo.setInt("grade_seq", rowSet.getInt("grade_seq"));
				dao.addValueObject(vo);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	public void copyKhObject(String newId, String planId, String object_type,Connection conn,RowSet rowSet) throws GeneralException
	{		
		ContentDAO dao = new ContentDAO(conn);

		StringBuffer strsql = new StringBuffer();
		if(object_type!=null && object_type.trim().length()>0 && "2".equalsIgnoreCase(object_type))
		{
			strsql.append("select * from per_object,usra01 where plan_id=");
			strsql.append(planId);
			strsql.append(" and per_object.object_id=usra01.a0100");//确保复制的考核对象在在职人员库中
		}
		else
		{
			strsql.append("select * from per_object where plan_id=");
			strsql.append(planId);
		}
		try
		{
			rowSet = dao.search(strsql.toString());
			while (rowSet.next())
			{  
				RecordVo vo = new RecordVo("per_object");
				IDGenerator idg = new IDGenerator(2, conn);
				String id = idg.getId("per_object.id");
				Integer idtemp = new Integer(id);
				vo.setInt("id", idtemp.intValue());
	
				vo.setString("b0110", rowSet.getString("b0110"));
				vo.setInt("plan_id", Integer.parseInt(newId));
				vo.setString("e0122", rowSet.getString("e0122"));
				vo.setString("e01a1", isNull(rowSet.getString("e01a1")));
				vo.setString("object_id", isNull(rowSet.getString("object_id")));
				vo.setString("a0101", isNull(rowSet.getString("a0101")));
				vo.setInt("a0000", rowSet.getInt("a0000"));
				// 以下两个指标发现CS程序没有复制过去
				// vo.setString("a0000",
				// isNull(this.frowset.getString("a0000")));
				if(rowSet.getString("body_id")!=null)
					vo.setInt("body_id", rowSet.getInt("body_id"));
				String kh_relations = rowSet.getString("kh_relations");
				if(kh_relations!=null)
					vo.setInt("kh_relations", rowSet.getInt("kh_relations"));
				vo.setInt("obj_body_id", rowSet.getInt("obj_body_id"));
				
				dao.addValueObject(vo);
				
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	/** 复制指定类别的考核主体信息 */
	public void copyKhMainbody(String newId, String planId, String bodyid,Connection conn,RowSet rowSet) throws GeneralException
	{		
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_mainbody pmb,usra01 ua where plan_id=");
		strsql.append(planId);
		if (bodyid.trim().length() > 0)
			strsql.append(" and body_id="+bodyid);
		strsql.append("and ua.A0100=pmb.mainbody_id"); //确保考核主体复制表数据是在职人员
		try
		{
			rowSet = dao.search(strsql.toString());
			while (rowSet.next())
			{
				RecordVo vo = new RecordVo("per_mainbody");
				IDGenerator idg = new IDGenerator(2, conn);
				String id = idg.getId("per_mainbody.id");
				Integer idtemp = new Integer(id);
				vo.setString("id", idtemp.toString());

				vo.setString("b0110", rowSet.getString("b0110"));
				vo.setString("plan_id", newId);
				vo.setString("e0122", rowSet.getString("e0122"));
				vo.setString("e01a1", isNull(rowSet.getString("e01a1")));
				vo.setString("object_id", isNull(rowSet.getString("object_id")));
				vo.setString("a0101", isNull(rowSet.getString("a0101")));
				vo.setString("mainbody_id", isNull(rowSet.getString("mainbody_id")));
				vo.setString("body_id", isNull(rowSet.getString("body_id")));
				vo.setString("status", "0");
//				vo.setString("description", isNull(rowSet.getString("description")));
				if(rowSet.getString("seq")!=null)
					vo.setInt("seq", rowSet.getInt("seq"));
				if(rowSet.getString("sp_seq")!=null)
					vo.setInt("sp_seq", rowSet.getInt("sp_seq"));
				if(rowSet.getString("fillctrl")!=null)
					vo.setInt("fillctrl", rowSet.getInt("fillctrl"));
				
				// 以下三个指标发现CS程序没有复制过去
				// vo.setString("whole_grade_id",
				// isNull(this.frowset.getString("whole_grade_id")));
				// vo.setString("fillctrl",
				// isNull(this.frowset.getString("fillctrl")));
				// vo.setString("know_id",
				// isNull(this.frowset.getString("know_id")));
				dao.addValueObject(vo);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
		
	//复制考核主体的动态权重
	public void copyBodyDynaRank(String newId, String planId,String body_id,ContentDAO dao) throws GeneralException
	{	
		StringBuffer buf = new StringBuffer();
		buf.append("insert into per_dyna_bodyrank(body_id,plan_id,Dyna_obj_type,Dyna_obj,Rank)");
		buf.append("select body_id,"+newId+",Dyna_obj_type,Dyna_obj,Rank from per_dyna_bodyrank ");
		buf.append(" where plan_id="+planId);
		if(body_id.trim().length()>0)
			buf.append(" and body_id="+body_id);
		try
		{
			dao.insert(buf.toString(), new ArrayList());
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**正在复制考核指标票素的动态权重*/
	public void CopyPointDynaRank(String newId, String planId,ContentDAO dao) throws GeneralException
	{
		StringBuffer buf = new StringBuffer();
		buf.append("insert into per_dyna_rank(point_id,plan_id,Dyna_obj_type,Dyna_obj,Rank)");
		buf.append("select point_id,"+newId+",Dyna_obj_type,Dyna_obj,Rank from per_dyna_rank ");
		buf.append(" where plan_id="+planId);
		try
		{
			dao.insert(buf.toString(), new ArrayList());
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
		//专版中还要在此添加per_dyna_pointrank(动态主体指标权重)表的复制信息				
	}
	
	/**复制考核对象类别的动态项目分值/权重*/
	public void copyDynaItem (String newId, String planId,ContentDAO dao) throws GeneralException
	{	
		StringBuffer buf = new StringBuffer();
		buf.append("insert into per_dyna_item(body_id,plan_id,item_id,Dyna_value,task_rule)");
		buf.append("select body_id,"+newId+",item_id,Dyna_value,task_rule from per_dyna_item ");
		buf.append(" where plan_id="+planId);
		try
		{
			dao.insert(buf.toString(), new ArrayList());
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public void copyKhMainbodyPri(String newId, String planId, String body_id,Connection conn,RowSet rowSet) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(conn);
		
		String tablename = "per_pointpriv_" + planId;		
		DbWizard dbWizard = new DbWizard(conn);	
		// 此表若存在
		if(dbWizard.isExistTable(tablename,false))
		{		
			StringBuffer strsql = new StringBuffer();
			strsql.append("select a.* from per_pointpriv_" + planId);   //确保复制的指标权限在在职人员库中
			strsql.append(" a,per_mainbody b ,usra01 ua where ua.a0100=a.mainbody_id and b.object_id = a.object_id and a.mainbody_id=b.mainbody_id");
			strsql.append(" and b.plan_id=" + planId);
			if (body_id.trim().length() > 0)
				strsql.append(" and b.body_id="+body_id.trim());
	
			ArrayList c_x = khPlanBo.getC_x(planId);
			if(c_x.size()==0)
				return;
			try
			{
				rowSet = dao.search(strsql.toString());
				while (rowSet.next())
				{
					StringBuffer c_xStr = new StringBuffer();
					ArrayList valueList = new ArrayList();
					for (int i = 0; i < c_x.size(); i++)
					{
						String col = (String) c_x.get(i);
						c_xStr.append(col + "=?,");
						valueList.add(PubFunc.round(rowSet.getString(col),0));
					}
					valueList.add(isNull(rowSet.getString("object_id")));
					valueList.add(isNull(rowSet.getString("mainbody_id")));
	
					strsql = new StringBuffer();
					strsql.append("update per_pointpriv_" + newId);
					strsql.append(" set " + c_xStr);
					strsql = new StringBuffer(strsql.substring(0, strsql.length() - 1));
					strsql.append(" where object_id=? and mainbody_id=?");
					dao.update(strsql.toString(), valueList);
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}			
		}
	}
	
	public String cper_itempriv(String planId, ContentDAO dao,Connection conn,RowSet rowSet) throws GeneralException   // 修改为：如果模板里共性项目为2级，且一级项目下也有指标，项目权限里把1级项目显示出来  JinChunhai  2011.03.11
	{
		StringBuffer fieldStr = new StringBuffer();
		Table table = new Table("PER_ITEMPRIV_" + planId);

		Field obj = new Field("object_id");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setNullable(false);
		obj.setKeyable(true);
		table.addField(obj);

		obj = new Field("body_id");
		obj.setDatatype(DataType.INT);
		obj.setLength(10);
		obj.setNullable(false);
		obj.setKeyable(true);
		table.addField(obj);
		
		try
		{
			String str="select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planId+") and child_id is null";
			rowSet = dao.search(str);
			HashMap keyMap=new HashMap();
			while (rowSet.next())
			{
				keyMap.put(rowSet.getString("item_id"),"");			    			   
			}
	    	
			String strSql="select item_id from per_template_point where item_id in(select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planId+"))";
			rowSet = dao.search(strSql);
			while (rowSet.next())
			{
				keyMap.put(rowSet.getString("item_id"),"");			    			   
			}
			StringBuffer buf = new StringBuffer();
			Set keySet=keyMap.keySet();
			java.util.Iterator t=keySet.iterator();
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值				
				buf.append("," + strKey);
			}
			String sqlStr = "";
    		if(buf!=null && buf.length()>0)	
	    	{	    			
    			sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
	    	}else
	    		sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planId+") and child_id is null";
			
			rowSet = dao.search(sqlStr);
			while (rowSet.next())
			{
				obj = new Field("C_" + rowSet.getString("item_id"));
				obj.setDatatype(DataType.INT);
				obj.setKeyable(false);
				table.addField(obj);
				fieldStr.append("C_" + rowSet.getString("item_id") + ",");
			}
			
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		DbWizard dbWizard = new DbWizard(conn);
		dbWizard.createTable(table);
		fieldStr.append("object_id,body_id");
		return fieldStr.toString();
	}
	
	public Table cper_table_sql(String planId)
	{

		Table table = new Table("PER_TABLE_" + planId);
		Field obj = new Field("id");
		obj.setDatatype(DataType.INT);
		obj.setNullable(false);
		obj.setKeyable(true);
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

		obj = new Field("score");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("amount");
		obj.setDatatype(DataType.FLOAT);
		obj.setLength(12);
		obj.setDecimalDigits(6);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("point_id");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);
		table.addField(obj);

		obj = new Field("degree_id");
		obj.setDatatype(DataType.STRING);
		obj.setLength(1);
		obj.setKeyable(false);
		table.addField(obj);
		return table;

		// StringBuffer strSql = new StringBuffer("create table ");
		// strSql.append("PER_TABLE_"+planId + "(");
		// strSql.append("id int primary key,");
		// strSql.append("object_id varchar(30),");
		// strSql.append("mainbody_id varchar(10),");
		// strSql.append("score float,");
		// strSql.append("amount float,");
		// strSql.append("point_id varchar(30),");
		// strSql.append("degree_id varchar(1))");
		// return strSql.toString();
	}
	
	public void setDefaultItemPriv(String planid, ContentDAO dao, String fieldStr,RowSet rowSet) throws GeneralException
	{
		String sql = "select count(*) from PER_ITEMPRIV_" + planid;
		try
		{
			rowSet = dao.search(sql);
			if (rowSet.next())
				if (rowSet.getInt(1) == 0)// 项目权限里没有纪录
				{
					sql = "select count(*) from per_object where plan_id=" + planid;
					rowSet = dao.search(sql);
					if (rowSet.next())
						if (rowSet.getInt(1) > 0)// 有考核对象
						{
							sql = "select count(*) from per_plan_body where plan_id=" + planid;
							rowSet = dao.search(sql);
							if (rowSet.next())
								if (rowSet.getInt(1) > 0)// 设置了考核主体类别
								{
									String insertSql = "insert into PER_ITEMPRIV_" + planid + "(" + fieldStr + ") ";
									StringBuffer buf = new StringBuffer();
									buf.append(" select ");
									String[] temp = fieldStr.split(",");
									for (int i = 0; i < temp.length - 2; i++)
									{
										if (temp[i].trim().length() > 0)
											buf.append("1,");
									}
									buf.append("p.object_id,b.body_id from per_object p,per_plan_body b where p.plan_id=" + planid + " and b.plan_id=" + planid);
									insertSql += buf.toString();
									dao.insert(insertSql, new ArrayList());
								}
						}
				}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	public String isNull(String str) 
	{
		if (str == null || str.trim().length() <= 0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
			return "";
		else
			return str;
	}

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
	
}
