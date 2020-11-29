package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ShowIniDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		String infokind=(String)this.getFormHM().get("infokind");
		String statId="1";
		boolean istabid=false;
		try{
			String sql="select id,type from sname where infokind=" + infokind + " order by id";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
	
	    	while(!istabid && this.frowset.next())
	    	{
	    		
	    		  if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
	    		  {
	    			  statId=this.frowset.getString("id");
	    		      istabid=true;
	    		  }
	    	}		

		   if(!istabid)
		   {
			   throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.stat.nostatitem"),"",""));
		   }
		   else
		   {
			  //System.out.println(this.frowset.getString("type"));
			   this.getFormHM().put("isonetwo", this.frowset.getString("type"));
		   }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
