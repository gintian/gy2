package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;

public class ProcessEndTaskListTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedList");
		if(selectedlist==null || selectedlist.size()==0)
			return;
		try{
			for(int i=0;i<selectedlist.size();i++)
			{
				MorphDynaBean rec=(MorphDynaBean)selectedlist.get(i);
				String task_id = (String)rec.get("task_id_e");
				task_id = PubFunc.decrypt(task_id);
				String tabid = (String)rec.get("tabid");
				WF_Instance ins=new  WF_Instance(Integer.parseInt(tabid),this.getFrameconn(),this.userView);
				TemplateParam paramBo=new TemplateParam(this.frameconn,this.userView,Integer.parseInt(tabid));
				if(paramBo.getIsAotuLog()||paramBo.getIsRejectAotuLog()){//终止任务调用删除变动日志，删除对应单子的变动信息
					TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
					chgLogBo.deleteChangeInfoInProcess(task_id, tabid);
				}
				ins.processEnd(Integer.valueOf(task_id), Integer.valueOf(tabid), userView,1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
