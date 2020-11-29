package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class EditTableInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String rsid=(String)this.getFormHM().get("rsid");
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn());
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			String opt=(String)this.getFormHM().get("opt");
			if("10".equalsIgnoreCase(opt))
			{
				String isResetSort=(String)this.getFormHM().get("isResetSort");
				ArrayList currentList=PubFunc.unzipBytes_object((byte[])this.getFormHM().get("cur_head_byte"));
				ArrayList tableHeadList=PubFunc.unzipBytes_object((byte[])this.getFormHM().get("table_head_byte"));
				ArrayList currentColWidthList=PubFunc.unzipBytes_object((byte[])this.getFormHM().get("currentColWidth_byte"));
				if("1".equals(isResetSort))
					bo.reSetSort(tableHeadList, currentList, rsdtlid);
				bo.resetWidth(currentColWidthList, currentList, tableHeadList, rsdtlid);
			}
			else
			{
			
				String itemid=(String)this.getFormHM().get("itemid");
				String newItemfmt = (String)this.getFormHM().get("itemdesc");
				bo.editColumnTitle(rsid, rsdtlid, itemid, newItemfmt,opt);
			}
		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
