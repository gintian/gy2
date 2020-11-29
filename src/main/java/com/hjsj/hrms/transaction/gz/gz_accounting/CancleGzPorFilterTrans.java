package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 
 *<p>Title:CancleGzPorFilterTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 25, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class CancleGzPorFilterTrans extends IBusiness{

	public void execute() throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String salaryid=(String)this.getFormHM().get("salaryid");
		String model = (String)this.getFormHM().get("model");
		ArrayList list = new ArrayList();
		if(!"history".equalsIgnoreCase(model)) {//history 表示为薪资历史数据分析进入
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
			gzbo.synchronismSalarySet();
			gzbo.syncGzTableStruct();
			String filterid = gzbo.getFiltersIds(salaryid);
			ArrayList itemfilterlist = gzbo.getItemFilterList(filterid);

			for (int i = 0; i < itemfilterlist.size(); i++) {
				CommonData cd = (CommonData) itemfilterlist.get(i);
				CommonData ncd = new CommonData(cd.getDataValue(), SafeCode.encode(cd.getDataName()));
				list.add(ncd);
			}
		}else{
			HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
			String filterId=hbo.getFilterIdFromHistory();
			if(filterId.endsWith(",")) {
				filterId = filterId.substring(0, filterId.length() - 1);
			}
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn());
			gzbo.setUserview(this.getUserView());
			list = gzbo.getItemFilterList(filterId);
		}
		this.getFormHM().put("salaryid",salaryid);
		this.getFormHM().put("model",model);
		this.getFormHM().put("itemfilterlist",list);
	}
	
	
}
