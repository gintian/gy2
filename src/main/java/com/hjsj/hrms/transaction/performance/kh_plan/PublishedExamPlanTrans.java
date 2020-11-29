package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * 发布考核计划交易类
 * JinChunhai 
 */
public class PublishedExamPlanTrans extends IBusiness
{
	ExamPlanBo khPlanBo = null;

	public void execute() throws GeneralException
	{
		
		String busitype = (String) this.getFormHM().get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)				
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planIds = (String) hm.get("planIds");
		hm.remove("planIds");

		planIds = planIds.substring(0, planIds.length() - 1);
		planIds = planIds.replaceAll("／", "/");
		String[] plans = planIds.split("/");
		DbWizard dbWizard = new DbWizard(this.frameconn);
		khPlanBo = new ExamPlanBo(this.getFrameconn());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			for (int i = 0; i < plans.length; i++)
			{
				String planId = plans[i];
				if (testMainBodyType(planId))
				{
					// 对于另存得到的计划 做一些补充操作 为了在实施中的数据完整性
					// 有主体没有主体的考核指标权限补充
					String sql = "select * from per_mainbody where plan_id=" + planId;
					this.frowset = dao.search(sql);
					if(this.frowset.next())//主体表有数据
					{
						if (!dbWizard.isExistTable("per_pointpriv_" + planId, false))//相应的主体指标权限没有
						{
							khPlanBo.cper_pointpriv(planId);
							khPlanBo.synObjectAndBody(planId, "");
							khPlanBo.setKhMainbodyDefaultPri(planId);
						}
					}				
					
					Table table = null;
					// 创建per_table_计划号临时表
					if (!dbWizard.isExistTable("per_table_" + planId, false))
					{
						table = cper_table_sql(planId);
						dbWizard.createTable(table);
						
						// 添加索引
						dao.update("create index per_table_" + planId+"_idx on per_table_" + planId+" (object_id,mainbody_id)");

					}

					// 创建per_reslut_计划号临时表
					if (!dbWizard.isExistTable("PER_RESULT_" + planId, false))
					{
						table = khPlanBo.cper_reslut_sql(planId,busitype);
						dbWizard.createTable(table);
					}
					PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);
					// 检查per_result_planid表中有没有调整后的表结构的字段，若没有就创建  JinChunhai 2011.03.08
					pb.editResult(planId);

					// 创建per_pointpriv_计划号临时表
					// 由于另存的时候，当选中复制考核主体的指标权限的复选框时候也会创建该临时表，所以要先判断一下
					if (!dbWizard.isExistTable("per_pointpriv_" + planId, false))
					{
						khPlanBo.cper_pointpriv(planId);

					}

					RecordVo vo = new RecordVo("per_plan");
					try
					{
						vo.setInt("plan_id", Integer.parseInt(planId));
						vo = dao.findByPrimaryKey(vo);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					String method = vo.getString("method");
					if ("2".equals(method))// 目标管理计划要生成项目权限数据
					{
						if (!dbWizard.isExistTable("PER_ITEMPRIV_" + planId, false))
						{
							String fieldStr = cper_itempriv_sql(planId, dao);

							// 对于另存的计划 如果复制了考核对象 却没有复制对象的各主体类别项目权限 在此处按默认有权限设置项目权限
							setDefaultItemPriv(planId, dao, fieldStr);
						}
						//0 数据采集 1 网上打分 目标计划只能是网上打分 这里加如下代码是防止另存360后又改成了目标计划的时候 该参数没有相应改变
						LoadXml loadXml=new LoadXml(this.getFrameconn(),planId);
						Hashtable params = loadXml.getDegreeWhole();					
						String scoreWay=(String)params.get("scoreWay");
						if("0".equals(scoreWay))
							scoreWay="1";						
						loadXml.saveAttribute("PerPlan_Parameter","ScoreWay",scoreWay);
					}

					// 更新绩效计划状态为发布状态

					dao.update("update per_plan set status='3' where plan_id=" + planId);

				}
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void setDefaultItemPriv(String planid, ContentDAO dao, String fieldStr) throws GeneralException
	{
		String sql = "select count(*) from PER_ITEMPRIV_" + planid;
		try
		{
			this.frowset = dao.search(sql);
			if (this.frowset.next())
				if (this.frowset.getInt(1) == 0)// 项目权限里没有纪录
				{
					sql = "select count(*) from per_object where plan_id=" + planid;
					this.frowset = dao.search(sql);
					if (this.frowset.next())
						if (this.frowset.getInt(1) > 0)// 有考核对象
						{
							sql = "select count(*) from per_plan_body where plan_id=" + planid;
							this.frowset = dao.search(sql);
							if (this.frowset.next())
								if (this.frowset.getInt(1) > 0)// 设置了考核主体类别
								{
									String insertSql = "insert into PER_ITEMPRIV_" + planid + "(" + fieldStr + ") ";
									StringBuffer buf = new StringBuffer();
									buf.append(" select ");
									String[] temp = fieldStr.split(",");
									for (int i = 0; i < temp.length - 2; i++)
									{
										if (temp[i].trim().length() > 0)
											buf.append("1-" + Sql_switcher.isnull("b.opt", "0") + ","); // 复制的计划发布时根据opt调整项目权限 by 刘蒙
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

	public String cper_itempriv_sql(String planid, ContentDAO dao) throws GeneralException  // 修改为：如果模板里共性项目为2级，且一级项目下也有指标，项目权限里把1级项目显示出来  JinChunhai  2011.03.11
	{
		StringBuffer fieldStr = new StringBuffer();
		Table table = new Table("PER_ITEMPRIV_" + planid);

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
		
		RowSet rowSet = null;
		try
		{
			String str="select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
			rowSet = dao.search(str);
			HashMap keyMap=new HashMap();
			while (rowSet.next())
			{
				keyMap.put(rowSet.getString("item_id"),"");			    			   
			}
	    	
			String strSql="select item_id from per_template_point where item_id in(select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
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
	    		sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";

			rowSet = dao.search(sqlStr);
			while (rowSet.next())
			{
				obj = new Field("C_" + rowSet.getString("item_id"));
				obj.setDatatype(DataType.INT);
				obj.setKeyable(false);
				table.addField(obj);
				fieldStr.append("C_" + rowSet.getString("item_id") + ",");
			}
			
			if(rowSet!=null)
				rowSet.close();
				
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		DbWizard dbWizard = new DbWizard(this.frameconn);
		dbWizard.createTable(table);
		fieldStr.append("object_id,body_id");
		return fieldStr.toString();
	}

	/**
	 * 是否存在考核主体的指标权限表
	 */
	public boolean isExistKhMainbodyPri(String planId)
	{

		boolean flag = false;
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT name FROM sysobjects WHERE name = 'per_pointpriv_" + planId + "' AND type = 'U' ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next())
				flag = true;
		} catch (SQLException e)
		{
			flag = false;
		}
		return flag;
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

	/**
	 * 测试时候定义了考核主体类别,如果未定义则不能发布
	 * 
	 * @throws GeneralException
	 */
	public boolean testMainBodyType(String planId) throws GeneralException
	{

		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_plan_body where plan_id=");
		strsql.append(planId);
		try
		{
			this.frowset = dao.search(strsql.toString());
			if (!this.frowset.next())
				throw new GeneralException("计划必须设置考核主体类别，请在计划参数中设置！");
			else
				flag = true;

		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return flag;
	}

}
