package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SaveAsTaxTableTrans extends IBusiness{

	public void execute() throws GeneralException 
	{
		try
		{
			TaxTableSetBo bo = new TaxTableSetBo(this.getFrameconn());
			String id=(String)this.getFormHM().get("taxid");
			String description = SafeCode.decode((String)this.getFormHM().get("description"));
			String k_p=bo.getTaxTableInfo(id);
			/*IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String taxid = idg.getId("gz_tax_rate.taxid");*/
			int taxid=bo.getTaxId("gz_tax_rate","taxid");
			/**税率表信息*/
			bo.saveAsTax(taxid,description,k_p);
			/**明细表的信息*/
			ArrayList list = bo.getTaxDetailTableList(id);
			StringBuffer  sb= new StringBuffer();
			int taxitem=0;
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean =(LazyDynaBean)list.get(i);
				sb.append("'");
				sb.append(bean.get("ynse_down"));
				sb.append("','");
				sb.append(bean.get("ynse_up"));
				sb.append("','");
				sb.append(bean.get("sl"));
				sb.append("','");
				sb.append(bean.get("sskcs"));
				sb.append("','");
				sb.append(bean.get("flag"));
				sb.append("','");
				sb.append(bean.get("description"));
				sb.append("','");
				sb.append(bean.get("kc_base"));
				sb.append("'");
				taxitem=bo.getTaxId("gz_taxrate_item","taxitem");
				/**明细另存*/
				bo.saveAsTaxDetail(taxitem,taxid,sb.toString());
				sb.setLength(0);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	

}
