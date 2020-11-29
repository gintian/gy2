package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ZpCondTemplateXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
public class SaveComplexTemaplteTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			ArrayList list = (ArrayList)this.getFormHM().get("factorlist");
			String templateName=(String)this.getFormHM().get("templateName");
		    String templateid=(String)this.getFormHM().get("templateid");
		    String expression=(String)this.getFormHM().get("expression");
		    if(expression!=null){
		    	expression=PubFunc.keyWord_reback(expression);
		    }
		    HashMap map =getTemplateMap(templateName,list);
		    ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.getFrameconn());
		    if(templateid!=null&&!"-1".equals(templateid))
		    {
		    	bo.updateComplexParamXML(templateid, map, expression);
		    }
		    else
		    {
		       String xml=bo.createComplexParamXML(map, expression);
		       bo.insert(xml);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	private HashMap getTemplateMap(String condName,ArrayList factorlist)
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
	            
	            sfactor.append(PubFunc.keyWord_reback(factor.getOper()));
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
