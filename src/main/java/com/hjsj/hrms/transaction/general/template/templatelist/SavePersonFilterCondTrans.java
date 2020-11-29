package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePersonFilterCondTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ArrayList list = (ArrayList)this.getFormHM().get("personFilterList");
			String condName=(String)this.getFormHM().get("condName");
			String salaryid =(String)this.getFormHM().get("salaryid");
		    String condid=(String)this.getFormHM().get("filterCondId");
		    String expr=(String)this.getFormHM().get("expr");
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			String xml=bo.getCondXML(salaryid);
			SalaryLProgramBo sLPBo = new SalaryLProgramBo(xml);
			HashMap condMap = getCondMap(condName,list);
			if(condid!=null&&!"".equals(condid))
			{
				sLPBo.updateServiceItem(condid,condMap,expr);
			}
			else
			{
     			condid=String.valueOf(sLPBo.setSeiveItem(condMap,expr));
			}
			String newXml=sLPBo.outPutContent();
			bo.updateLprogram(salaryid,newXml);
			this.getFormHM().put("personFilterList",list);
			this.getFormHM().put("issave","2");
			this.getFormHM().put("filterCondId",condid);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	private HashMap getCondMap(String condName,ArrayList factorlist)
	{
		HashMap map = new HashMap();
		try
		{
			//StringBuffer sexpr= new StringBuffer();
			StringBuffer sfactor = new StringBuffer();
			for(int i=0;i<factorlist.size();i++)
	        {
	            Factor factor=(Factor)factorlist.get(i);
	          /*  if(i!=0)
	            {
	                sexpr.append(factor.getLog());
	            }
	            sexpr.append(i+1);*/
	            sfactor.append(factor.getFieldname().toUpperCase());
	            
	            sfactor.append(factor.getOper());
	            String q_value=factor.getValue().trim();
	            if("M".equals(factor.getFieldtype()))
	            {
	            	if(!("".equals(q_value)))
	            		sfactor.append("*");
	            }
	            sfactor.append(factor.getValue());  
	            /**对字符型指标有模糊*/
	            if("M".equals(factor.getFieldtype()))
	            {
	            	if(!("".equals(q_value)))
	                    sfactor.append("*");
	            }
	            sfactor.append("`");            
	        }
			map.put("Name",condName);
			map.put("Factor",sfactor.toString());
			//map.put("Expr",sexpr.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	

}
