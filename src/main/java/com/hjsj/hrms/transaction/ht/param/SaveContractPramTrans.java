package com.hjsj.hrms.transaction.ht.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:SaveContractPramTrans.java</p>
 * <p>Description:保存合同参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-03-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveContractPramTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String menuid = (String) hm.get("menuid");
	String paramStr = (String)this.getFormHM().get("paramStr");

	ConstantXml xml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
	if(menuid!=null && "1".equals(menuid))
	{
	    xml.setTextValue("/Params/nbase", paramStr);
	}
	
	if(menuid!=null && "2".equals(menuid))
	{
	    xml.setTextValue("/Params/mfield", paramStr);
	}
	
	if(menuid!=null && "3".equals(menuid))
	{	    
	    
	    
	    String htmain =(String)this.getFormHM().get("htSubSet");
	    xml.setTextValue("/Params/htmain", htmain);
	    
	    String httype =(String)this.getFormHM().get("httype");
	    xml.setTextValue("/Params/httype", httype);	    
	    
	    String[] itemSel=(String[])this.getFormHM().get("right_fields");   
	    StringBuffer buf = new StringBuffer();
	    if(itemSel!=null)
		for(int i=0;i<itemSel.length;i++)	    
		    buf.append(","+itemSel[i]);	    
	    xml.setTextValue("/Params/htset", buf.length()>0?buf.substring(1):"");
	}	
	xml.saveStrValue();
	//与以前坂本兼容
	if(menuid!=null && "3".equals(menuid))
	{
	    String[] itemSel=(String[])this.getFormHM().get("right_fields");   
	    StringBuffer buf = new StringBuffer();
	    if(itemSel!=null)
		for(int i=0;i<itemSel.length;i++)	    
		    buf.append(",'"+itemSel[i]+"'");	
	    xml.saveValue("HETONGSET", buf.length()>0?buf.substring(1):"");	
	    
	    String htmain =(String)this.getFormHM().get("htSubSet");
	    xml.saveValue("HETONGMAIN",htmain);	     
	}
    }
}
