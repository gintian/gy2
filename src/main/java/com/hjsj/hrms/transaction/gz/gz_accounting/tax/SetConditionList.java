package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class SetConditionList extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			HashMap hm=this.getFormHM();
			HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
			String querystr = (String)reqhm.get("querystr");
			reqhm.remove("querystr");
			String[] fields=this.getStringArr(querystr);
			String strexprsave=(String)this.getFormHM().get("expression");
			hm.remove("conright_fields");
			if(fields==null||fields.length==0){
		       throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfield"),"",""));
		    }
			StringBuffer strexpr=new StringBuffer();
			ArrayList list =new ArrayList();
			int j=0;
			int nInform=1;
			TaxMxBo taxbo = new TaxMxBo(this.getFrameconn());
			ArrayList tempfieldlist = taxbo.getFieldlist();
			ArrayList itemlist = taxbo.getitemlist(tempfieldlist);
			for(int i=0;i<fields.length;i++)
	        {
	            String fieldname=fields[i];
	            if(fieldname==null|| "".equals(fieldname))
	                continue;
	            cat.debug("field_name="+fieldname);
	            Factor factor=new Factor(nInform);
                for(int t=0;t<itemlist.size();t++)
                {
                	Field field = (Field)itemlist.get(t);
                	if(field.getName().equalsIgnoreCase(fieldname))
                	{
                		if(field.getDatatype()==DataType.DATE)
        				{
                			factor.setFieldtype("D");
        				}
        				else if(field.getDatatype()==DataType.STRING)
        				{
        					factor.setFieldtype("A");

        				}
        				else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
        				{
        					factor.setFieldtype("N");
        				}
        				else if(field.getDatatype()==DataType.CLOB)
        				{
        					factor.setFieldtype("M");
        				}
        				else 
        					factor.setFieldtype("A");
                		factor.setCodeid(field.getCodesetid());
                        factor.setFieldname(field.getName());
                        factor.setHz(field.getLabel());
                        factor.setItemlen(field.getLength());
                        factor.setItemdecimal(field.getDecimalDigits());
                        break;
                	}
                }
//	               
                factor.setOper("=");//default
                factor.setLog("*");//default
                list.add(factor);
                ++j;
                strexpr.append(j);
                strexpr.append("*");
	           
	        }
	        if(strexpr.length()>0){
	        	strexpr.setLength(strexpr.length()-1);
	        }
	        if(!(strexprsave==null|| "".equals(strexprsave)))  //chenmengqing changed at 20070601
	        	this.getFormHM().put("expression",strexprsave);            	
	        else
	        	this.getFormHM().put("expression",strexpr.toString());
	        hm.put("factorlist",list);
	        hm.put("expre","");
	        hm.put("conright_fields",fields);
	        ArrayList logiclist = new ArrayList();
	        logiclist.add(new CommonData("*","并且"));
	        logiclist.add(new CommonData("+","或"));
	        hm.put("logiclist",logiclist);
//	        String condtionsql = (String)this.getFormHM().get("condtionsql");
	        hm.put("condtionsql","");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public String[] getStringArr (String str)
	{
		String[] Stringarr = null;
		int tempnum = str.split(",").length;
		if(tempnum>0)
		{
			Stringarr = str.split(",");
		}
		return Stringarr;
	}
	/*
	 * 数据类型转换
	 */
    public String tranDataType(int num)
    {
    	String datatype = "";
    	if(num==4)
    		datatype = "N";  
    	else if(num==6)
    		datatype = "N";  
    	else if(num==1)
    		datatype = "A"; 
    	else if(num==10)
    		datatype = "D";      	 
    	return datatype;
    }
    

}
