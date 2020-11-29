/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPropertyBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:SetBatchImportTrans</p> 
 *<p>Description:设置批量引入项目</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-13:下午12:00:45</p> 
 *@author cmq
 *@version 4.0
 */
public class SetBatchImportTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String gz_module=(String)hm.get("gz_module");	
		String fromModel=(String)hm.get("fromModel");
		hm.remove("fromModel");
		try
		{
			ArrayList list=new ArrayList();
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			/**取得全部的薪资项目*/
			ArrayList templist=gzbo.getSalaryItemList2(); //gzbo.getSalaryItemList();
			String manager=gzbo.getManager();
			int j=0;
			String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  			
			
			
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
			
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,Integer.parseInt(gz_module),this.getUserView());
			/**读权限的指标是否允许重新引入=0不可以*/
			String read_field=bo.getCtrlparam().getValue(SalaryCtrlParamBo.READ_FIELD);
	        if(read_field==null|| "".equals(read_field))
	        	read_field="0";
			for(int i=0;i<templist.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)templist.get(i);
				String flag=(String)dynabean.get("initflag");
				String itemid=(String)dynabean.get("itemid");
				
				if(fromModel!=null&&("ff".equals(fromModel)|| "sp".equals(fromModel))) //重新导入
				{
					if(!("1".equals(flag)|| "2".equals(flag)))
						continue;
				}
				
				if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				{
					if(map.get(itemid.toLowerCase())!=null)
						continue;
				}
				
				
				String nlock=(String)dynabean.get("nlock");
				if("3".equalsIgnoreCase(flag)&&!"A01Z0".equalsIgnoreCase(itemid))
					continue;
				LazyDynaBean tmp=new LazyDynaBean();
				tmp.set("itemid", dynabean.get("itemid")==null?"":dynabean.get("itemid"));
				tmp.set("itemdesc",dynabean.get("itemdesc")==null?"":dynabean.get("itemdesc"));
				//System.out.println("-->"+(++j)+dynabean.get("itemid")+"="+dynabean.get("itemdesc"));
				
				if("a01z0".equalsIgnoreCase(itemid)&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
					continue;
		    	if(manager.length()==0||manager.equalsIgnoreCase(this.userView.getUserName()))
		    	{
		    		if(!"3".equalsIgnoreCase(flag))
		    		{
		    			if((fromModel!=null&&("ff".equals(fromModel)|| "sp".equals(fromModel)))&&(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //重新导入
		    			{
		    				if("2".equals(this.userView.analyseFieldPriv(itemid))|| "1".equals(this.userView.analyseFieldPriv(itemid)))
			    				list.add(tmp);
		    			}
		    			else
		    			{
		    				if("0".equals(read_field)){
			    		    	if("2".equals(this.userView.analyseFieldPriv(itemid)))
			    			    	list.add(tmp);
		    				}else{
		    					if("2".equals(this.userView.analyseFieldPriv(itemid))|| "1".equals(this.userView.analyseFieldPriv(itemid)))
			    			    	list.add(tmp);
		    				}
		    			}
		    		}
		    		else
		    			list.add(tmp);
		    	}
				else
				{
					if("A01Z0".equalsIgnoreCase(itemid))
						list.add(tmp);
					else if(!"3".equalsIgnoreCase(flag))
					{
						if("0".equals(read_field)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
						    list.add(tmp);
						else if("1".equals(read_field)&&("2".equals(this.userView.analyseFieldPriv(itemid))|| "1".equals(this.userView.analyseFieldPriv(itemid))))
							list.add(tmp);
					}
				}
			}//for i loop end.
			this.getFormHM().put("formulalist", list);	
			this.getFormHM().put("gz_module",gz_module);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
