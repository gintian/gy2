/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:查询薪资类别计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-28:下午04:46:13</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchFormulaListTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");		
		try
		{
			HashMap requestPamaHM = (HashMap)this.getFormHM().get("requestPamaHM");
			String  module=(String)requestPamaHM.get("module");
			ArrayList list=new ArrayList();
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			/**当薪资项目长度，小数位等发生变化时，税率表中的没有改变过来，这样导致在向税率表中写数据的时候，发生错误，在此同步表结构,*/
			TaxMxBo tmb = new TaxMxBo(this.getFrameconn());
			ArrayList rightlist = tmb.getRightField();
			String[] right_fields = new String[rightlist.size()];
			for(int i=0;i<rightlist.size();i++)
			{
				right_fields[i]=((CommonData)rightlist.get(i)).getDataValue();
			}
			tmb.syncTaxTable(right_fields);
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getManager();
			/**取得全部的计算公式*/
			list=gzbo.getFormulaList(-1);
			ArrayList formulaList=new ArrayList();
			
			for(int i=0;i<list.size();i++)
			{
				DynaBean abean=(DynaBean)list.get(i);
				String itemname=(String)abean.get("itemname");
				
			//	if(manager.length()==0||manager.equalsIgnoreCase(this.userView.getUserName()))
			//		formulaList.add(abean);
			//	else if(this.userView.analyseFieldPriv(itemname).equals("2")||this.userView.analyseFieldPriv(itemname,0).equals("2"))
			//		formulaList.add(abean);
				//北京移动要求加上权限 2009-11-20
				
				
				String field_priv=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FIELD_PRIV);
		        if(field_priv==null|| "".equals(field_priv))
		        	field_priv="1";
				
		        if("1".equals(field_priv))
		        {
		        	formulaList.add(abean);
		        }
		        else
		        {
					String state=this.userView.analyseFieldPriv(itemname);
					if("0".equals(state))
						state=this.userView.analyseFieldPriv(itemname,0);
					if("2".equals(state)||this.userView.isSuper_admin())
							formulaList.add(abean);
		        }
				
			}
			requestPamaHM.remove("module");
			this.getFormHM().put("formulalist", formulaList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
