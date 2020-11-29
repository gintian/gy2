package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class SetconditionsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String[] fields=(String[])hm.get("right_fields");
		if(fields==null||fields.length==0){
	       throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfield"),"",""));
	    }
		StringBuffer strexpr=new StringBuffer();
		ArrayList list =new ArrayList();
		ArrayList factorlist=(ArrayList)hm.get("factorlist");
		hm.remove("factorlist");
		FieldItem item=null;
		int j=0;
		int nInform=1;
		for(int i=0;i<fields.length;i++)
        {
            String fieldname=fields[i];
            if(fieldname==null|| "".equals(fieldname))
                continue;
            cat.debug("field_name="+fieldname);
            item=DataDictionary.getFieldItem(fieldname.toUpperCase());
            Factor factor=null;
            if(item!=null)
            {
            	/**已定义的因子再现*/
            	if(factorlist!=null)
            	{
            		factor=findFactor(fieldname,factorlist,i);
            		if(factor!=null)
            		{
            			list.add(factor);
            		    ++j;
                        strexpr.append(j);
                        strexpr.append("*");
            			continue;
            		}
            	}
                factor=new Factor(nInform);
                factor.setCodeid(item.getCodesetid());
                factor.setFieldname(item.getItemid());
                factor.setHz(item.getItemdesc());
                factor.setFieldtype(item.getItemtype());
                factor.setItemlen(item.getItemlength());
                factor.setItemdecimal(item.getDecimalwidth());
                factor.setOper("=");//default
                factor.setLog("*");//default
                list.add(factor);
                ++j;
                strexpr.append(j);
                strexpr.append("*");
            }                
        }
        if(strexpr.length()>0){
        	strexpr.setLength(strexpr.length()-1);
        }
        hm.put("expression",strexpr.toString());
        hm.put("factorlist",list);
        hm.put("expre","");
        hm.put("savecrond","0");
        hm.put("right_fields",fields);
	}
	 /**
     * 查找是否存在相同的因子对象
     * @param name
     * @param list
     * @return
     */
    private Factor findFactor(String name,ArrayList list,int index)
    {
    	Factor factor=null;
    	for(int i=0;i<list.size();i++){
    		factor=(Factor)list.get(i);
    		if(name.equalsIgnoreCase(factor.getFieldname())&&(i==index))
    			break;
    		factor=null;
    	}
    	return factor;
    }

}
