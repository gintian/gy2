package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AutoComputeKqDaysTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			 
			String tabid=(String)this.getFormHM().get("tabid");
			String task_id=(String)this.getFormHM().get("task_id");
			String bEmploy=(String)this.getFormHM().get("bEmploy"); //是否为业务申请  1：是
			String key_value=(String)this.getFormHM().get("key");
			String type=(String)this.getFormHM().get("type"); //请假类型
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			if(bEmploy!=null&& "1".equals(bEmploy))
				tablebo.setBEmploy(true);
			int infor_type=tablebo.getInfor_type();
			
			String ins_id="0"; 
			if(!"0".equalsIgnoreCase(task_id))
			{
				this.frowset=dao.search("select ins_id from t_wf_task where task_id="+task_id);
				if(this.frowset.next())
					ins_id=this.frowset.getString(1);
			}
			
			String str=tablebo.computeKqDays(ins_id,key_value,type).toLowerCase();
		//	str="a030a_2:1,a030b_2:2";
			
			this.getFormHM().put("kqDays",str);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
