 package com.hjsj.hrms.transaction.performance.kh_plan;

 import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
 import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
 import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
 import com.hjsj.hrms.businessobject.sys.DbNameBo;
 import com.hjsj.hrms.utils.OracleBlobUtils;
 import com.hjsj.hrms.utils.PubFunc;
 import com.hrms.frame.dao.ContentDAO;
 import com.hrms.frame.dao.RecordVo;
 import com.hrms.frame.dbstruct.DataType;
 import com.hrms.frame.dbstruct.DbWizard;
 import com.hrms.frame.dbstruct.Field;
 import com.hrms.frame.dbstruct.Table;
 import com.hrms.frame.utility.DateStyle;
 import com.hrms.frame.utility.IDGenerator;
 import com.hrms.hjsj.sys.Constant;
 import com.hrms.hjsj.utils.Sql_switcher;
 import com.hrms.struts.exception.GeneralException;
 import com.hrms.struts.exception.GeneralExceptionHandler;
 import com.hrms.struts.facade.transaction.IBusiness;

 import javax.sql.RowSet;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.SQLException;
 import java.util.*;

/**
 * 另存考核计划交易类
 * 
 * @author: JinChunhai
 */

public class SaveAsExamPlanTrans extends IBusiness
{
	
	ExamPlanBo khPlanBo = null;
	
	public void execute() throws GeneralException
	{
		String busitype = (String) this.getFormHM().get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)		
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String plan_id = (String) hm.get("planId");
		plan_id = plan_id.replaceAll("／", "/");
		hm.remove("planId");

		String[] planIds = null;
		if(plan_id!=null && plan_id.trim().length()>0)
		{
			plan_id = plan_id.substring(0, plan_id.length() - 1);
			planIds = plan_id.split("/");		
		}
		
		khPlanBo = new ExamPlanBo(this.getFrameconn());
		String copyResultStr = (String) this.getFormHM().get("copyResultStr");
		// 复制基本信息
		String copy_self = (String) this.getFormHM().get("copy_self");
		// 复制考核主体类别
		String copy_khmainbodytype = (String) this.getFormHM().get("copy_khmainbodytype");
		// 复制考核对象
		String copy_khobject = (String) this.getFormHM().get("copy_khobject");
		// 复制考核主体
		String copy_khmainbody = (String) this.getFormHM().get("copy_khmainbody");
		// 复制考核主体的指标权限
		String copy_khmainbody_pri = (String) this.getFormHM().get("copy_khmainbody_pri");

		copyResultStr += "\n开始复制计划\n";
		if(planIds!=null && planIds.length>0)
		{					
			for (int i = 0; i < planIds.length; i++)
			{
				String planId = planIds[i];
			
				RecordVo planVo = khPlanBo.getPerPlanVo(planId);
				String object_type = planVo.getString("object_type"); 
				String newId = "";
				if ("1".equals(copy_self))
				{
					if(i==0)
						copyResultStr += "准备复制计划数据...";
					newId = copySelf(planId);
					if(i==0)
						copyResultStr += "OK\n";
				}
		
				if ("1".equals(copy_khmainbodytype))
				{
					if(i==0)
						copyResultStr += "正在复制计划的主体类别...";
					copyKhMainbodytype(newId, planId);
					if(i==0)
						copyResultStr += "OK\n";
				}
		
				if ("1".equals(copy_khobject))
				{
					if(i==0)
						copyResultStr += "正在复制计划的考核对象...";
					copyKhObject(newId, planId,object_type);
					if(i==0)
						copyResultStr += "OK\n";	
				}
				/** 新计划是否被复制了本人的主体类别 */
				boolean isHaveSelfBody = isHaveSelfBody(newId);
				if ("1".equals(copy_khmainbody))
				{
					if(i==0)
						copyResultStr += "正在复制计划的考核主体...";
					copyKhMainbody(newId, planId, "");
					if(i==0)
						copyResultStr += "OK\n";
					
					if(i==0)
						copyResultStr += "正在复制考核主体的动态权重...";
					copyBodyDynaRank(newId, planId,"");//per_dyna_bodyrank
					if(i==0)
						copyResultStr += "OK\n";			
					
				} else if (isHaveSelfBody && "1".equals(copy_khobject))//复制了考核对象且设置了本人主体类别
				{// 即使没有选中复制考核主体如果被复制的考核计划中有本人主体类别，还是要将本人主体复制过去的
					if(i==0)
						copyResultStr += "正在建立考核对象的\"本人\"主体信息...";
					copyKhMainbody(newId, planId, "5");//复制本人考核主体
					copyBodyDynaRank(newId, planId,"5");//per_dyna_bodyrank
					if(i==0)
						copyResultStr += "OK\n";
				}
		
				if ("1".equals(copy_khmainbody_pri))
				{
					if(i==0)
						copyResultStr += "正在复制考核指标票素的动态权重...";
					CopyPointDynaRank(newId, planId);
					if(i==0)
						copyResultStr += "OK\n";		
					
					PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn());
					String method = bo.getPlanVo(planId).getString("method");
		//			复制考核对象类别的动态项目分值/权重
					if ("2".equals(method))
					{
						String template_id =  bo.getPlanVo(planId).getString("template_id");
						
						if(template_id!=null && template_id.trim().length()>0)
						{					
							RecordVo templateVo=bo.getTemplateVo(template_id);
							String templateStatus=templateVo!=null?templateVo.getString("status"):"0";
							if("1".equals(templateStatus))
							{
								if(i==0)
									copyResultStr += "正在复制考核对象类别的动态项目权重...";
							}else
							{
								if(i==0)
									copyResultStr += "正在复制考核对象类别的动态项目分值...";
							}
						}else
						{
							if(i==0)
								copyResultStr += "正在复制考核对象类别的动态项目分值/权重...";
						}
						this.copyDynaItem(newId, planId);
						if(i==0)
							copyResultStr += "OK\n";	
					}	
					
					// 先新建表per_pointpriv_xxx
					khPlanBo.cper_pointpriv(newId);
					// 同步考核对象与考核主体
					if(i==0)
						copyResultStr += "同步考核对象与考核主体...";
					khPlanBo.synObjectAndBody(newId, "");
					if(i==0)
						copyResultStr += "OK\n";
		
					if(i==0)
						copyResultStr += "复制主体的考核指标的权限...";
					copyKhMainbodyPri(newId, planId, "");
					if(i==0)
						copyResultStr += "OK\n";
					
					if ("2".equals(method))
					{
						ContentDAO dao = new ContentDAO(this.getFrameconn());
						if(i==0)
							copyResultStr += "同步考核对象主体类别项目权限...";
						// 先新建表per_itempriv_xxx
						String fieldStr = cper_itempriv(newId, dao);
						if(i==0)
							copyResultStr += "OK\n";	
						
						try
						{
							if(i==0)
								copyResultStr += "复制考核对象主体类别项目权限...";
							String insertSql = "insert into PER_ITEMPRIV_" + newId + "(" + fieldStr + ") select " + fieldStr + " from PER_ITEMPRIV_" + planId;
							DbWizard dbWizard = new DbWizard(this.frameconn);
							if (dbWizard.isExistTable("PER_ITEMPRIV_" + planId, false))		//存在老计划的项目权限表才复制到新表		
								dao.insert(insertSql, new ArrayList());
							if(i==0)
								copyResultStr += "OK\n";
						} catch (SQLException e)
						{
							e.printStackTrace();
							throw GeneralExceptionHandler.Handle(e);
						}
					
					}
					
					//为了兼容cs 在此把结果表建了
					DbWizard dbWizard = new DbWizard(this.frameconn);
					Table table = null;
					if (!dbWizard.isExistTable("PER_RESULT_" + newId, false))
					{
						table = khPlanBo.cper_reslut_sql(newId,busitype);
						dbWizard.createTable(table);
					}
					PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);
					// 检查per_result_planid表中有没有调整后的表结构的字段，若没有就创建  JinChunhai 2011.03.08
					pb.editResult(newId);
		
					
				}
				/*准备把这部分代码 放到发布的时候做 这样可以在复制前四项的时候还可以修改模板。
				else  if (copy_khmainbody.equals("1"))//复制主体不复制权限 指标权限按照默认有权限处理
				{		
					khPlanBo.cper_pointpriv(newId);
					khPlanBo.synObjectAndBody(newId, "");
					khPlanBo.copyKhMainbodyPri2(newId, planId, "");		
				}else if(copy_khmainbody.equals("0") && isHaveSelfBody && copy_khobject.equals("1"))//只是复制到考核对象 如果有本人类别 本人主体权限也按默认给
				{
					khPlanBo.cper_pointpriv(newId);
					khPlanBo.synObjectAndBody(newId, "5");
					khPlanBo.copyKhMainbodyPri2(newId, planId, "5");	
				}	
				*/
				
		/*		//为了兼容cs 在此把结果表原样复制一份  JinChunhai 2011.03.03
				
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				try {
					
					switch(Sql_switcher.searchDbServer())
					{
						case 1: //MSSQL				
							dao.update("Select * Into PER_RESULT_"+newId+" from PER_RESULT_"+planId+" where 1=2");
						break;
						case 2://oracle
							dao.update("Create table PER_RESULT_"+newId+" as Select * from PER_RESULT_"+planId+" where 1=2");	
			            break;
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		*/		
				if(i==0)
					copyResultStr += "计划复制完成\n复制简报:\n";
			
				copyResultStr += "    生成的新计划:" + khPlanBo.getPlanName(newId) + "[id=" + newId + "].\n";
			}
		}
		this.getFormHM().put("copyResultStr", copyResultStr);
		
	}	
	
	/**正在复制考核指标票素的动态权重*/
	public void CopyPointDynaRank(String newId, String planId) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
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

	public void CopyBodyDynaRank(String newId, String planId) throws GeneralException
	{

	
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_dyna_bodyrank where plan_id=");
		strsql.append(planId);

		try
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next())
			{
				RecordVo vo = new RecordVo("per_dyna_bodyrank");
				vo.setString("body_id", this.frowset.getString("body_id"));
				vo.setString("plan_id", newId);
				vo.setString("dyna_obj_type", this.frowset.getString("dyna_obj_type"));
				vo.setString("dyna_obj", isNull(this.frowset.getString("dyna_obj")));
				vo.setDouble("rank", this.frowset.getDouble("rank"));
				dao.addValueObject(vo);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	public String cper_itempriv(String planId, ContentDAO dao) throws GeneralException   // 修改为：如果模板里共性项目为2级，且一级项目下也有指标，项目权限里把1级项目显示出来  JinChunhai  2011.03.11
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
		
		RowSet rowSet = null;
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
		DbWizard dbWizard = new DbWizard(this.frameconn);
		dbWizard.createTable(table);
		fieldStr.append("object_id,body_id");
		return fieldStr.toString();
	}

	public void copyKhMainbodyPri(String newId, String planId, String body_id) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String tablename = "per_pointpriv_" + planId;		
		DbWizard dbWizard = new DbWizard(this.frameconn);	
		
		// 此表若存在
		if(dbWizard.isExistTable(tablename,false))
		{	
			ArrayList c_x = khPlanBo.getC_x(planId);
			//2013.11.13 begin
			//判断指标列是否存在，不存在则创建
			for (int i = 0; i < c_x.size(); i++)
			{
				String col = (String) c_x.get(i);
				Table table = new Table(tablename);
				boolean flag = false;
				if (!dbWizard.isExistField(tablename, col,false))
				{
					Field obj = new Field(col);
					obj.setDatatype(DataType.INT);
					obj.setKeyable(false);
					table.addField(obj);
					flag = true;
				}
				if (flag){
					dbWizard.addColumns(table);// 更新列
					String tempsql = "update per_pointpriv_"+planId+" set "+col+" = 1";
					try {
						dao.update(tempsql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}                        
			}
			//2013.11.13 end
			StringBuffer strsql = new StringBuffer();
			strsql.append("select a.* from per_pointpriv_" + planId);   //确保复制的指标权限在在职人员库中
			strsql.append(" a,per_mainbody b ,usra01 ua where ua.a0100=a.mainbody_id and b.object_id = a.object_id and a.mainbody_id=b.mainbody_id");
			strsql.append(" and b.plan_id=" + planId);
			if (body_id.trim().length() > 0)
				strsql.append(" and b.body_id="+body_id.trim());
	
			if(c_x.size()==0)
				return;
			try
			{
				this.frowset = dao.search(strsql.toString());
				while (this.frowset.next())
				{
					StringBuffer c_xStr = new StringBuffer();
					ArrayList valueList = new ArrayList();
					for (int i = 0; i < c_x.size(); i++)
					{
						String col = (String) c_x.get(i);
						c_xStr.append(col + "=?,");
						valueList.add(PubFunc.round(this.frowset.getString(col),0));
					}
					valueList.add(isNull(this.frowset.getString("object_id")));
					valueList.add(isNull(this.frowset.getString("mainbody_id")));
	
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
	
	//复制考核主体的动态权重
	public void copyBodyDynaRank(String newId, String planId,String body_id) throws GeneralException
	{	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
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
	/**复制考核对象类别的动态项目分值/权重*/
	public void copyDynaItem (String newId, String planId) throws GeneralException
	{	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
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
	/** 复制指定类别的考核主体信息 */
	public void copyKhMainbody(String newId, String planId, String bodyid) throws GeneralException
	{

		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String date=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_mainbody pmb,usra01 ua where plan_id=");
		strsql.append(planId);
		if (bodyid.trim().length() > 0)
			strsql.append(" and body_id="+bodyid);
		strsql.append(" and ua.A0100=pmb.mainbody_id"); //确保考核主体复制表数据是在职人员
		strsql.append("  and (pmb.object_id in (select a0100 from usra01) or pmb.object_id in (select codeitemid from organization where "+Sql_switcher.dateValue(date)+"  between start_date and end_date)) ");//防止离职后复制过来此人的考核主体  zhaoxg 2014-4-14
		strsql.append(" and "+Sql_switcher.isnull("pmb.Un_planned", "0")+"<> 1");//复制的时候 不加入因为是评价人被加入的考核 主体
		try
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next())
			{
				RecordVo vo = new RecordVo("per_mainbody");
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String id = idg.getId("per_mainbody.id");
				Integer idtemp = new Integer(id);
				vo.setString("id", idtemp.toString());

				vo.setString("b0110", this.frowset.getString("b0110"));
				vo.setString("plan_id", newId);
				vo.setString("e0122", this.frowset.getString("e0122"));
				vo.setString("e01a1", isNull(this.frowset.getString("e01a1")));
				vo.setString("object_id", isNull(this.frowset.getString("object_id")));
				vo.setString("a0101", isNull(this.frowset.getString("a0101")));
				vo.setString("mainbody_id", isNull(this.frowset.getString("mainbody_id")));
				vo.setString("body_id", isNull(this.frowset.getString("body_id")));
				vo.setString("status", "0");
//				vo.setString("description", isNull(this.frowset.getString("description")));
				if(this.frowset.getString("seq")!=null)
					vo.setInt("seq", this.frowset.getInt("seq"));
				if(this.frowset.getString("sp_seq")!=null)
					vo.setInt("sp_seq", this.frowset.getInt("sp_seq"));
				if(this.frowset.getString("fillctrl")!=null)
					vo.setInt("fillctrl", this.frowset.getInt("fillctrl"));
				
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

	public void copyKhObject(String newId, String planId, String object_type) throws GeneralException
	{		
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		StringBuffer strsql = new StringBuffer();
		if(object_type!=null && object_type.trim().length()>0 && "2".equalsIgnoreCase(object_type))
		{
			strsql.append("select * from per_object,usra01 where plan_id=");
			strsql.append(planId);
			strsql.append(" and per_object.object_id=usra01.a0100");//确保复制的考核对象在在职人员库中
		}
		else
		{
			String date=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			strsql.append("select * from per_object where plan_id=");
			strsql.append(planId);
			strsql.append(" and object_id in (select codeitemid from organization where "+Sql_switcher.dateValue(date)+"  between start_date and end_date) ");//过期的单位&部门不复制  zhaoxg add 2014-12-15
		}
		try
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next())
			{  
				RecordVo vo = new RecordVo("per_object");
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String id = idg.getId("per_object.id");
				Integer idtemp = new Integer(id);
				vo.setInt("id", idtemp.intValue());
	
				vo.setString("b0110", this.frowset.getString("b0110"));
				vo.setInt("plan_id", Integer.parseInt(newId));
				vo.setString("e0122", this.frowset.getString("e0122"));
				vo.setString("e01a1", isNull(this.frowset.getString("e01a1")));
				vo.setString("object_id", isNull(this.frowset.getString("object_id")));
				vo.setString("a0101", isNull(this.frowset.getString("a0101")));
				vo.setInt("a0000", this.frowset.getInt("a0000"));
				// 以下两个指标发现CS程序没有复制过去
				// vo.setString("a0000",
				// isNull(this.frowset.getString("a0000")));
				if(this.frowset.getString("body_id")!=null)
					vo.setInt("body_id", this.frowset.getInt("body_id"));
				String kh_relations = this.frowset.getString("kh_relations");
				if(kh_relations!=null)
					vo.setInt("kh_relations", this.frowset.getInt("kh_relations"));
				vo.setInt("obj_body_id", this.frowset.getInt("obj_body_id"));
				
				dao.addValueObject(vo);
				
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	public void copyKhMainbodytype(String newId, String planId) throws GeneralException
	{
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_plan_body where plan_id=");
		strsql.append(planId);

		try
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next())
			{
				RecordVo vo = new RecordVo("per_plan_body");
				vo.setInt("body_id", this.frowset.getInt("body_id"));
				vo.setInt("plan_id",Integer.parseInt(newId));
				vo.setDouble("rank", this.frowset.getDouble("rank"));
				vo.setInt("flag", this.frowset.getInt("flag"));
				vo.setInt("lead", this.frowset.getInt("lead"));
				vo.setDouble("voterank", this.frowset.getDouble("voterank"));
				vo.setInt("isgrade", this.frowset.getInt("isgrade"));
				vo.setInt("grade_seq", this.frowset.getInt("grade_seq"));
				vo.setInt("opt", this.frowset.getInt("opt")); // 复制计划时加入opt字段 by 刘蒙
				dao.addValueObject(vo);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/** 是否有本人主体类别 */
	public boolean isHaveSelfBody(String planId) throws GeneralException
	{
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_plan_body where plan_id=");
		strsql.append(planId);
		strsql.append(" and body_id=5");
		try
		{
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next())
			{
				flag = true;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

	/**
	 * 复制基本信息
	 * 
	 * @param planId
	 * @return
	 * @throws GeneralException
	 */
	public String copySelf(String planId) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		
		String creator = this.getUserView().getUserName();
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		String newPlanId = idg.getId("per_plan.plan_id");
		Integer planid = new Integer(newPlanId);
		newPlanId = planid.toString();
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		RecordVo vo = bo.getPerPlanVo(planId);
		String planName = vo.getString("name");
		
		String fields = "theyear,themonth,thequarter,start_date,end_date,descript,template_id,cycle,awoke_days,target,content,flow,result,";
		fields+="object_type,B0110,plan_type,parameter_content,gather_type,thefile,plan_visibility,";//agree_date,agree_user,agree_idea,approve_result, 去掉批准日期、批准人、批准意见、审批结果
		fields+="file_ext,method,busitype,bymodel";				
		
		String newplanName = planName+"-1";
		if(newplanName.getBytes().length>50)
		{
			byte[] temp=new byte[50];
			for(int i=0;i<50;i++)
				temp[i]=newplanName.getBytes()[i];
			newplanName=new String (temp);	
		}	
		
		StringBuffer strsql = new StringBuffer();
		strsql.append("insert into per_plan (plan_id,name,status,"+fields+",create_user,create_date)");
		strsql.append("select '"+newPlanId+"','"+newplanName+"',0,"+fields+",'"+creator+"',"+Sql_switcher.sqlNow()+" from per_plan where plan_id="+planId);
		InputStream inputStream = null;
		try
		{
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
				sql = "update per_plan set a0000=((select min(a0000)-1 from per_plan)) where plan_id=" + newPlanId;
				dao.update(sql);				
			} catch (SQLException e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
			// 拷贝绩效报告模板 by lium
			String articleSQL = "SELECT article_id,Affix FROM per_article WHERE Fileflag=3 AND plan_id=?";
			frowset = dao.search(articleSQL, Arrays.asList(new Object[] { Integer.valueOf(planId) }));
			if (frowset.next()) {
				RecordVo article = new RecordVo("per_article");
				article.setInt("article_id", frowset.getInt("article_id"));
				article = dao.findByPrimaryKey(article);
				int newArticleId = DbNameBo.getPrimaryKey("per_article", "article_id", this.frameconn);
				article.setInt("article_id", newArticleId);
				article.setInt("plan_id", Integer.parseInt(newPlanId));
				//blob 类型直接使用RecordVo会有问题。先置空好更新 haosl update
				article.removeValue("affix");
				dao.addValueObject(article);
				// blob字段保存,数据库中差异
				List values = new ArrayList();
				switch (Sql_switcher.searchDbServer()) {
					case Constant.ORACEL:
						inputStream = frowset.getBinaryStream("Affix");
						Blob blob = getOracleBlob(inputStream, "per_article", newArticleId);
						values.add(blob);
						break;
					default:
						byte[] data = frowset.getBytes("affix");
						values.add(data);
						break;
				}
				values.add(newArticleId);
				dao.update("update per_article set Affix=? where article_id=?",values);

			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return newPlanId;
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
		OracleBlobUtils blobutils=new OracleBlobUtils(this.frameconn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),file); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
	public String isNull(String str)
	{

		if (str == null)
			str = "";
		return str;
	}

}
