package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ChangePersonFilterListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			  String isclose=(String)this.getFormHM().get("isclose");
			  String salaryid=(String)this.getFormHM().get("salaryid");
			  String model=this.getFormHM().get("model")!=null?(String)this.getFormHM().get("model"):"";
			  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(),this.getUserView());//xieguiquan 增加参数this.getUserView() 20100827
			ArrayList filterCondList;
			if("history".equalsIgnoreCase(model)){//history 表示为薪资历史数据分析进入
				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				filterCondList=hbo.searchManFilterFromHistory();
			}else{
				filterCondList = bo.getFilterCondList(salaryid);
			}
			  this.getFormHM().put("filterCondList",filterCondList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
