package com.hjsj.hrms.transaction.gz.templateset.salaryItem;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 查询薪资类别中的薪资项目列表
 *<p>Title:SearchSalarySetListTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 3, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SearchSalarySetListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)hm.get("salaryid");
			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			String queryvalue=(String)hm.get("queryvalue");
			SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			bo.syncGzTableStruct();
			bo.synchronismSalarySet();
			queryvalue=SafeCode.decode(queryvalue==null?"":queryvalue);
			StringBuffer buf = new StringBuffer();
			buf.append(" (UPPER(itemid)<>'A0000' and UPPER(itemid)<>'A0100'");
			if(!"".equals(queryvalue.trim()))
				buf.append(" and (UPPER(itemdesc) like '%"+PubFunc.getStr(queryvalue.toUpperCase())+"%' or UPPER(itemid) like '%"+PubFunc.getStr(queryvalue.toUpperCase())+"%')");
			buf.append(")");
			bo.setQueryvalue(buf.toString());
			ArrayList list=bo.getSalaryItemList();
			
			/*for(int i=0;i<list0.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list0.get(i);
				String itemid=(String)abean.get("itemid");
				String itemdesc=(String)abean.get("itemdesc");
				if(queryvalue!=null&&!queryvalue.trim().equals(""))
				{
					if(queryvalue.equalsIgnoreCase(itemid)||queryvalue.equalsIgnoreCase(itemdesc))
					{
						list.add(abean);
					}
				}
				else
				{
					if(!itemid.equals("A0000")&&!itemid.equals("A0100"))
						list.add(abean);
				}
				
			}*/
			
			this.getFormHM().put("salarySetName",bo.getTemplatevo().getString("cname"));
			this.getFormHM().put("salaryItemList",list);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("queryvalue", queryvalue==null?"":SafeCode.encode(queryvalue));
			hm.remove("queryvalue");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
