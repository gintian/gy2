package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.businessobject.general.operation.OperationBo;
import com.hjsj.hrms.businessobject.general.operation.OperationSQLStr;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddFixTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo t_wf_defineVo =new RecordVo("t_wf_define");
 		ContentDAO dao=new ContentDAO(this.getFrameconn());
 		String operationcode=(String) hm.get("operationcode");
		
		String selstr="";
		try {
			selstr=OperationSQLStr.getOperationname(dao,(String) hm.get("operationcode"),"0");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operatin.find.fail"),"",""));
		}
		int tabid=OperationBo.getTemplateSerial(dao);
		t_wf_defineVo.setInt("tabid",tabid);
		hm.put("t_wf_defineVo",t_wf_defineVo);
		hm.put("selstr",selstr);
		hm.put("uflag","0");
		hm.put("sbch",OperationSQLStr.getvalideflag("0"));
		hm.put("inputurl","");
		hm.put("appurl","");
		hm.put("validateflag","false");
		hm.put("edit_param", new ArrayList());
		hm.put("appeal_param", new ArrayList());
		hm.put("operationcode", operationcode);
		//reqhm.remove("operationcode");
	}	

}
