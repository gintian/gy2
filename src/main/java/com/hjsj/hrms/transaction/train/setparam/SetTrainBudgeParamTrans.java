package com.hjsj.hrms.transaction.train.setparam;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SetTrainBudgeParamTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
    	if(hm!=null){
			ArrayList itemlist = DataDictionary.getFieldList("r25", Constant.USED_FIELD_SET);
			for (int i = 0; i < itemlist.size(); i++) {
				FieldItem item = (FieldItem) itemlist.get(i);
				if("r2506".equalsIgnoreCase(item.getItemid())||!"N".equalsIgnoreCase(item.getItemtype())){
					itemlist.remove(i--);
				}
			}
			
			this.getFormHM().put("itemlist", itemlist);
			
			String budget = constantbo.getValue("train_budget");
			this.getFormHM().put("budget", budget);
    	}else{
    		String budget = (String)this.getFormHM().get("budget");
    		constantbo.setValue("train_budget", budget);
    		constantbo.saveStrValue();
    		this.getFormHM().put("flag", "ok");
    	}
    }
}
