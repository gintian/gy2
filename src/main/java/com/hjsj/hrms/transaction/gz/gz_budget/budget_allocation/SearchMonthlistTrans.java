package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *  根据选择预算类别判断起始月份
 * <p>Title:SearchMonthlistTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 25, 2012 1:36:50 PM</p>
 * <p>@version: 5.0</p>
 * 
 */
public class SearchMonthlistTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			ArrayList firstMonthlist = new ArrayList();
			String type = (String)this.getFormHM().get("type");
			int n=1;
			if("2".equals(type))
				n=7;
			for(int ii=n;ii<13;ii++){
				String m=String.valueOf(ii);
				CommonData obj=new CommonData(m,m+ResourceFactory.getProperty("datestyle.month"));
				firstMonthlist.add(obj);
			}
			this.getFormHM().put("firstMonthlist", firstMonthlist);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
