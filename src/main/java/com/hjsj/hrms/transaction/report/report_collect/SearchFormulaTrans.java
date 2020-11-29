package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.TformulaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFormulaTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String tabids=(String)hm.get("tabids");
			String type=(String)hm.get("type");  // 1:表内计算 2：表间计算
			
			TformulaBo formulaBo=new TformulaBo(this.getFrameconn());
			String[] arr=tabids.split(",");
			ArrayList list=new ArrayList();
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]!=null&&arr[i].trim().length()>0)
					list.add(arr[i]);
			}
			ArrayList formulaList=formulaBo.getFormulaList(list,type);						//得到计算公式
			this.getFormHM().put("formulaList",formulaList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
