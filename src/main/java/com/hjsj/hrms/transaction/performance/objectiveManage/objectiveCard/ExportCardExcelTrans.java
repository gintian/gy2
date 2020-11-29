package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 30200710254
 * <p>Title:ExportCardExcelTrans.java</p>
 * <p>Description>:ExportCardExcelTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 8, 2009 5:21:53 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ExportCardExcelTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String planid=PubFunc.decryption((String)this.getFormHM().get("plan_id"));
			String object_id=PubFunc.decryption((String)this.getFormHM().get("object_id"));
			String model=(String)this.getFormHM().get("model");
			String body_id=(String)this.getFormHM().get("body_id");
			String opt=(String)this.getFormHM().get("opt");
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,opt);
			bo.setBody_id(body_id);
			String fileName=bo.getObjectCardExcel();
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
