package com.hjsj.hrms.transaction.train.job;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddOrDelTrainClassMenTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String r3101=(String)this.getFormHM().get("r3101");
			r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
			String dbpre=(String)this.getFormHM().get("dbpre");
			dbpre = PubFunc.decrypt(SafeCode.decode(dbpre));
			String a0100=(String)this.getFormHM().get("a0100");
			a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
			String operator=(String)this.getFormHM().get("operator");
			TrainClassBo bo=new TrainClassBo(this.getFrameconn());
			String info=bo.operateTrainClassManRecord(operator,a0100,dbpre,r3101,"08");
			this.getFormHM().put("info",info);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
