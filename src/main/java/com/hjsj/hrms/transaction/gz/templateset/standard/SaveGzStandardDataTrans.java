package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveGzStandardDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String item=(String)this.getFormHM().get("item");
			String item_id=(String)this.getFormHM().get("item_id");
			String description=(String)this.getFormHM().get("description");//PubFunc.keyWord_reback((String)this.getFormHM().get("description"));
			String type=(String)this.getFormHM().get("type");	
			String lowerValue=PubFunc.keyWord_reback((String)this.getFormHM().get("lowerValue"));
			String lowerOperate=PubFunc.keyWord_reback((String)this.getFormHM().get("lowerOperate"));
			String heightValue=PubFunc.keyWord_reback((String)this.getFormHM().get("heightValue"));
			String heightOperate=PubFunc.keyWord_reback((String)this.getFormHM().get("heightOperate"));
			String middleValue="";
			if("D".equals(type))
				middleValue=PubFunc.keyWord_reback((String)this.getFormHM().get("middleValue"));
			String isAccuratelyDay=(String)this.getFormHM().get("isAccuratelyDay");  //是否精确到天
			
			SalaryStandardBo salaryStandardBo=new SalaryStandardBo(this.getFrameconn());
			salaryStandardBo.saveStandardData(item,item_id,description,type,lowerValue,lowerOperate,heightValue,heightOperate,middleValue,isAccuratelyDay);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
