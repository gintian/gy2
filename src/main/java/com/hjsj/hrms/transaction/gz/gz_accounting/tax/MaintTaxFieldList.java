package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class MaintTaxFieldList extends IBusiness{
	public void execute()throws GeneralException 
	{
		try
		{
			HashMap hm = (HashMap)this.getFormHM();
			TaxMxBo tmb = new TaxMxBo(this.getFrameconn());
			ArrayList gzmxtypelist = tmb.getGzMxType(this.userView);
			ArrayList gzmxprolist = new ArrayList();
			hm.put("gzmxtypelist",gzmxtypelist);
			String getsalaryid = (String)hm.get("salaryid");
		    hm.remove("salaryid");
//		    hm.remove("right_fields");
			if(!(getsalaryid==null || "".equals(getsalaryid)))
			{
				gzmxprolist = this.getgzmxprolist(getsalaryid);
				hm.put("gzmxprolist",gzmxprolist);
			}
			else
			{
				CommonData gzmxprocd = new CommonData();
				gzmxprocd = (CommonData)gzmxtypelist.get(0);
				getsalaryid = (String)gzmxprocd.getDataValue();
				gzmxprolist = this.getgzmxprolist(getsalaryid);
				hm.put("gzmxprolist",gzmxprolist);
			}
			ArrayList rightlist = tmb.getRightField();
			String deptid=tmb.getDeptID();
			this.getFormHM().put("deptid", deptid);
			hm.put("rightlist",rightlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	/**获得薪资项目
	 * @param 薪资类别
	 * @return
	 */
	public ArrayList getgzmxprolist(String salaryid)
	{
		ArrayList gzmxprolist = new ArrayList();
		ArrayList retlist = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql = "select * from salaryset where salaryid = "+salaryid;
			gzmxprolist = dao.searchDynaList(sql);
			for(Iterator it=gzmxprolist.iterator();it.hasNext();)
			{
				DynaBean dynabean = (DynaBean)it.next();
				String itemid = (String)dynabean.get("itemid");
				String itemdesc = (String)dynabean.get("itemdesc");
				if(!("a0000".equalsIgnoreCase(itemid) || "a0100".equalsIgnoreCase(itemid)
						|| "a00Z0".equalsIgnoreCase(itemid) || "a00Z1".equalsIgnoreCase(itemid)
						|| "a00Z2".equalsIgnoreCase(itemid) || "a00Z3".equalsIgnoreCase(itemid)
						|| "nbase".equalsIgnoreCase(itemid) || "SalaryId".equalsIgnoreCase(itemid)))
				{
					CommonData gzmxprocd = new CommonData(itemid,itemdesc);
					retlist.add(gzmxprocd);
				}
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retlist;
	}
}

