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

public class AddOperationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo operationVo =new RecordVo("operation");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		String[] serial=new String[2];
		if(reqhm==null){		
				DynaBean operationBean = (LazyDynaBean) hm.get("operationvo");
				operationVo.setString("operationid",(String) operationBean.get("operationid"));
				operationVo.setString("operationcode",(String) operationBean.get("operationcode"));
				operationVo.setString("operationname",(String) operationBean.get("operationname"));
				operationVo.setString("static",(String) operationBean.get("statid"));
				operationVo.setString("operationtype","10");
				try{
				OperationBo.addOperationVo(dao,operationVo);
				}catch(Exception e){
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operation.message.add.fail"),"",""));
				}
			
		}else{
			if(reqhm.containsKey("query")){
				reqhm.remove("query");
				if(reqhm.containsKey("operationcode")&&!reqhm.containsKey("root")){
					String operationcode=(String) reqhm.get("operationcode");
					try {
						serial=OperationBo.getSerial(dao,operationcode);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operatin.find.fail"),"",""));
					} 
					reqhm.remove("operationcode");
				}else{
					try {
						serial=OperationBo.getSerial(dao,null);
						reqhm.remove("root");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operatin.find.fail"),"",""));
					} 
				}
				operationVo.setString("operationid",serial[0]);
				operationVo.setString("operationcode",serial[1]);
				hm.put("operationVo",operationVo);	
			}
			
		}
		
	}

}
