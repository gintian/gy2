package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.businessobject.general.operation.OperationBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

public class UpdateOperationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo operationVo =new RecordVo("operation");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(reqhm==null){
			try {
				DynaBean operationBean = (LazyDynaBean) hm.get("operationvo");
				operationVo.setString("operationid",(String) operationBean.get("operationid"));
				operationVo.setString("operationname",(String) operationBean.get("operationname"));
				OperationBo.updateOperationVo(dao,operationVo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operation.message.update.fail"),"",""));
			} 
		}else{
			if(reqhm.containsKey("query")){
				reqhm.remove("query");
				String operationid=(String) reqhm.get("operationid");
				reqhm.remove("operationid");
				operationVo.setString("operationid",operationid);
				try {
					operationVo=dao.findByPrimaryKey(operationVo);
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operatin.find.fail"),"",""));
				}
			}
			hm.put("operationVo",operationVo);
		}

	}

}
