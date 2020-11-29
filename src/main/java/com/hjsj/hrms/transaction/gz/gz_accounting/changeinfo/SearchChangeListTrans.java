/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:变动对比</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-2:下午05:24:48</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchChangeListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
    	{
	    	String ajax=(String)this.getFormHM().get("ajax");
	    	if(ajax==null)
	    	{
	         	HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
	         	String salaryid=(String)map.get("salaryid");	
	        	String filterid=(String)map.get("filterid");
	        	String fieldstr=SafeCode.decode((String)map.get("fieldstr"));
	        	String add=(String)map.get("add");
	        	String del=(String)map.get("del");
	        	String info=(String)map.get("info");
		        String stop=(String)map.get("stop");
	        	if(filterid==null)
	        		filterid="null";
	        	if(fieldstr==null)
		        	fieldstr="null";
	       		ArrayList changeTabList=new ArrayList();
	    		if("1".equals(add))
		    		changeTabList.add(getLazyDynaBean("gz.info.addmen","/gz/gz_accounting/addman.do?b_query=link&chgtype=add&salaryid="+salaryid));
		    	if("1".equals(del))
			    	changeTabList.add(getLazyDynaBean("gz.info.delmen","/gz/gz_accounting/delman.do?b_query=link&chgtype=del&salaryid="+salaryid));
			    if("1".equals(info))
			    	changeTabList.add(getLazyDynaBean("gz.info.changeInfo","/gz/gz_accounting/changeinfo.do?b_query=link&chgtype=chginfo&filterid="+filterid+"&fieldstr="+fieldstr+"&salaryid="+salaryid));
			    if("1".equals(stop))
			     	changeTabList.add(getLazyDynaBean("gz.info.stopmen","/gz/gz_accounting/changeA01Z0.do?b_query=link&chgtype=chgA01Z0&salaryid="+salaryid));
			    this.getFormHM().put("changeTabList",changeTabList);
	    	}
	    	else
	    	{
	    		String salaryid=(String)this.getFormHM().get("salaryid");
	    	    String addcount=this.getCount("t#"+this.getUserView().getUserName()+"_gz_Ins")+"";
	    	    String delcount=this.getCount("t#"+this.getUserView().getUserName()+"_gz_Dec")+"";
	    	    String chgcount=this.getCount("t#"+this.getUserView().getUserName()+"_gz_Bd")+"";
		        String stpcount=this.getCount("t#"+this.getUserView().getUserName()+"_gz_Tf")+"";
			
	    		this.getFormHM().put("addcount",addcount);
	    		this.getFormHM().put("delcount", delcount);
	    		this.getFormHM().put("chgcount", chgcount);
	    		this.getFormHM().put("tfcount", stpcount);
	    		this.getFormHM().put("salaryid", salaryid);
	    	}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	private LazyDynaBean getLazyDynaBean(String label,String url)
	{
		LazyDynaBean abean=new LazyDynaBean("");
		abean.set("label", label);
		abean.set("url", url);
		return abean;
	}
	private int getCount(String tableName)
	{
		int count=0;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			DbWizard cc = new DbWizard(this.getFrameconn());
			if(!cc.isExistTable(tableName,false))
				return count;
			this.frowset=dao.search("select count(*) as count from "+tableName+" where state=1");
			while(this.frowset.next())
			{
				count=this.frowset.getInt("count");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}

}
