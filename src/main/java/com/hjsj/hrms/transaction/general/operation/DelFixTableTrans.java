package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.businessobject.general.operation.TwfnodeBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class DelFixTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList delsel=(ArrayList) hm.get("delsel");
		TwfnodeBo twn=new TwfnodeBo();
		for(int i=0;i<delsel.size();i++){
			DynaBean dynabean =(DynaBean) delsel.get(i);
			RecordVo t_wf_defineVo =new RecordVo("t_wf_define");
			t_wf_defineVo.setString("tabid",(String)dynabean.get("tabid"));
			try {
				dao.deleteValueObject(t_wf_defineVo);
				twn.delnode(this.getFrameconn(),t_wf_defineVo.getString("tabid"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("codemaintence.delcode.fail"),"",""));
				
			} 
		}
	}

}
