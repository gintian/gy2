package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ParseXmlBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:任务回顾</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 21, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class TaskReviewTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String object_id=(String)this.getFormHM().get("object_id");
			String p0400=(String)hm.get("p0400");
			String planid=(String)this.getFormHM().get("planid");
			String txid = (String)this.getFormHM().get("txid");
			if(txid==null|| "".equals(txid))
				txid="-1";
			String myView="";
			String otherView="";
			if("-1".equals(p0400))
			{
		    	ParseXmlBo bo=new ParseXmlBo(this.getFrameconn());
		    	HashMap map=null;
		    	map=bo.getTotalReviewMap(object_id,planid,"summarizes",txid);
		    	ArrayList alist = (ArrayList)map.get("alist");
			    for(Iterator t=alist.iterator();t.hasNext();)
			    {
			    	LazyDynaBean abean = (LazyDynaBean)t.next();
				    otherView+="\r\n\r\n"+(String)abean.get("name")+"    "+(String)abean.get("date");
				    otherView+="\r\n"+(String)abean.get("context");
			   	}
			    ArrayList txList = (ArrayList)map.get("txlist");
			    this.getFormHM().put("txid", txid);
			    this.getFormHM().put("txList", txList);
	    		this.getFormHM().put("myView",myView);
	    		this.getFormHM().put("otherView",otherView);
			}else{
				ParseXmlBo bo=new ParseXmlBo(this.getFrameconn());
		    	HashMap map=null;
				map=bo.getPointReviewMap(p0400,"summarizes",txid);
	
			
		        ArrayList alist = (ArrayList)map.get("alist");
		    	for(Iterator t=alist.iterator();t.hasNext();)
		    	{
		    		LazyDynaBean abean = (LazyDynaBean)t.next();
			    	otherView+="\r\n\r\n"+(String)abean.get("name")+"    "+(String)abean.get("date");
			       	otherView+="\r\n"+(String)abean.get("context");
		   		}
		    	ArrayList txList = (ArrayList)map.get("txlist");
		    	this.getFormHM().put("txid", txid);
		    	this.getFormHM().put("txList", txList);
	    		this.getFormHM().put("myView",myView);
	    		this.getFormHM().put("otherView",otherView);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
