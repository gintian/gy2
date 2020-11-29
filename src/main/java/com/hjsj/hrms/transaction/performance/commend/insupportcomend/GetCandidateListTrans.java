package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCandidateListTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			if(hm==null || hm.size()==0){
				return;
			}
			String p0201=(String)hm.get("p0201");
			ArrayList list = DataDictionary.getFieldList("P03",Constant.USED_FIELD_SET);
			String sql ="select * from p03 where p0201 = "+p0201;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
