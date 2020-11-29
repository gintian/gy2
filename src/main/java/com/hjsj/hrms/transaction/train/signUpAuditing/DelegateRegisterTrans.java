package com.hjsj.hrms.transaction.train.signUpAuditing;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DelegateRegisterTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		    String info="申请成功！";
            
            String r3101 = (String)this.getFormHM().get("r3101");
            r3101 = r3101 != null && r3101.trim().length() > 0 ? r3101 : "";
            r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
            
            String personstr = (String)this.getFormHM().get("personstr");
            personstr = personstr != null && personstr.trim().length() > 0 ? personstr : "";
            
            TrainClassBo bo = new TrainClassBo(this.getFrameconn());
            
            String[] personarr = personstr.split("`");
            for(int i=0;i<personarr.length;i++){
                String person = personarr[i];
                if(person==null || person.length()==0)
                    continue;
                
                String[] arr = person.split("::");
                if(arr.length != 5)
                    continue;
                
                String a0100 = arr[0];
                String nbase = arr[4];
                a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
                nbase = PubFunc.decrypt(SafeCode.decode(nbase));
                info = bo.operateTrainClassManRecord("add", a0100, nbase, r3101,"02");
            }
            this.formHM.put("info", info);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
