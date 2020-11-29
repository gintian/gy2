package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 设置权限
 * 
 * @author JinChunhai
 */

public class SetExamPlanPrivTrans extends IBusiness
{  

    public void execute() throws GeneralException
    {
  
		try
		{
			DbWizard dbWizard = new DbWizard(this.frameconn);
			// 如果per_plan_body.opt字段不存在，则新建
			if (!dbWizard.isExistField("per_plan_body", "opt")) {
				Table table=new Table("per_plan_body");
				Field obj = new Field("opt");
				obj.setDatatype(DataType.INT);
				obj.setNullable(true);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.addColumns(table);
			}
			// 如果per_object.score_process字段不存在，则新建
			if (!dbWizard.isExistField("per_object", "score_process")) {
				Table table=new Table("per_object");
				Field obj = new Field("score_process");
				obj.setDatatype(DataType.CLOB);
				obj.setNullable(true);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.addColumns(table);
			}
			
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String busitype = (String)map.get("busitype"); // =0(绩效考核); =1(能力素质)
			
			if(busitype==null || busitype.trim().length()<=0)
				busitype = "0";
			
			// 动态增加业务分类字段 =0(绩效考核); =1(能力素质) 
			// editArticle();  架构自动升级 所以在此去掉
															
		    // 权限设置 1 表示有------------待完善的功能
		    this.getFormHM().put("priv", "1");
		    
		    this.getFormHM().put("busitype", busitype);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}

    }
    
    // 检查per_plan表中有没有busitype字段，若没有就创建  JinChunhai 2011.11.22
    public void editArticle() throws GeneralException
	{
		try
		{			
			Table table = new Table("per_plan");
			DbWizard dbWizard = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
			if (!dbWizard.isExistField("per_plan", "busitype",false))
			{
			    Field obj = new Field("busitype");
			    obj.setDatatype(DataType.INT);
			    obj.setKeyable(false);
			    table.addField(obj);
			    dbWizard.addColumns(table);// 更新列		
			    dbmodel.reloadTableModel("per_plan");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    }

}
