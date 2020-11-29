/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:设置批量更新公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-13:下午03:07:20</p> 
 *@author cmq
 *@version 4.0
 */
public class SetBatchUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
//		String salaryid=(String)this.getFormHM().get("salaryid");	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String salaryid=(String)hm.get("salaryid");	
		String gz_module=(String)hm.get("gz_module");
		try
		{
			ArrayList itemlist=new ArrayList();
			ArrayList ref_itemlist=new ArrayList();
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getManager();
			ArrayList templist=gzbo.getSalaryItemList2();  //getSalaryItemList();
			
			
			HashMap map=new HashMap();
			if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
			{
				ArrayList formulaList=gzbo.getFormulaList(-1);
				for(int i=0;i<formulaList.size();i++)
				{
					  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
					  String itemname=(String)dbean.get("itemname");
					  map.put(itemname.toLowerCase(),"1");
				}
			}
			
			
			for(int i=0;i<templist.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)templist.get(i);
				String flag=(String)dynabean.get("initflag");
				String itemid=(String)dynabean.get("itemid");
				
				if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				{
					if(map.get(itemid.toLowerCase())!=null)
						continue;
				}
				
				
				CommonData vo=new CommonData(itemid,(String)dynabean.get("itemdesc")); 
				ref_itemlist.add(vo);
				/**系统项*/
				if("3".equalsIgnoreCase(flag)&&(!("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
					continue;
				
				if(manager.length()==0&&!this.userView.isSuper_admin())
				{
					if("3".equalsIgnoreCase(flag)&&(("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
						itemlist.add(vo);
					else if(!"3".equalsIgnoreCase(flag)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
						itemlist.add(vo);
				}
				else if(manager.equalsIgnoreCase(this.userView.getUserName())||this.userView.isSuper_admin())
				{
					if(this.userView.isSuper_admin())
						itemlist.add(vo);
					else
					{
						if("3".equalsIgnoreCase(flag)&&(("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
							itemlist.add(vo);
						else if(!"3".equalsIgnoreCase(flag)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
							itemlist.add(vo);
					}
				}
				else
				{
					if("3".equalsIgnoreCase(flag)&&(("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
						itemlist.add(vo);
					else if(!"3".equalsIgnoreCase(flag)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
						itemlist.add(vo);
				}
				
				
			}//for i loop end.
			this.getFormHM().put("itemlist", itemlist);	
			this.getFormHM().put("ref_itemlist", ref_itemlist);	
			this.getFormHM().put("gz_module",gz_module);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
