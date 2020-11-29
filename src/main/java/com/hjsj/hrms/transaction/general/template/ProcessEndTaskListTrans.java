package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessEndTaskListTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedlist");
		String sp_flag=(String) this.getFormHM().get("sp_flag");
		boolean backFlag =true;//是否需要将task_id转换回来，从任务监控中删除时不需要转换
		if(sp_flag!=null&&"2".equals(sp_flag)){
			backFlag=false;
		}
		if(selectedlist==null || selectedlist.size()==0)
			return;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap insmap=new HashMap(); 
		try{
			for(int i=0;i<selectedlist.size();i++)
			{
				LazyDynaBean rec=(LazyDynaBean)selectedlist.get(i);
				String task_id = (String)rec.get("task_id");
				String tabid = (String)rec.get("tabid");
				WF_Instance ins=new  WF_Instance(Integer.parseInt(tabid),this.getFrameconn(),this.userView);
				
				ins.processEnd(Integer.valueOf(task_id), Integer.valueOf(tabid), userView,1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
