package com.hjsj.hrms.transaction.general.statics;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SaveStaticInitTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
     	String infor_Flag=(String)this.getFormHM().get("infor_Flag");
     	String history=(String)this.getFormHM().get("history");
     	String find=(String)this.getFormHM().get("find");
        String result=(String)this.getFormHM().get("result");
		 if(infor_Flag==null)
			 infor_Flag="1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sql=new StringBuffer();
		ArrayList list=new ArrayList();
		String title=null;

		try{
				 sql.append("select name,id from sname where Type='1' and  InfoKind=");
	             sql.append(infor_Flag);
	             this.frowset=dao.search(sql.toString());
	             while(this.frowset.next())
	             {
	          	   CommonData cdata=new CommonData(this.frowset.getString("id"),(this.frowset.getString("Name")));
	          	   if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
        		   {
	          		  list.add(cdata);
        		   }	          	 
	             }
	             if(flist.size()>0)
	             {
						Factor fc=(Factor)flist.get(0);
						title=fc.getHz();	            	 
	             }
		}
		catch(Exception exx)
	    {
  	       exx.printStackTrace();
  	       throw GeneralExceptionHandler.Handle(exx);
  	    }finally{
			//初始化标题 
	    	this.getFormHM().put("title",title);	    	
  	    	this.getFormHM().put("infor_Flag",infor_Flag);
  	    	this.getFormHM().put("namelist",list);
  	    	this.getFormHM().put("history",history);
  	    	this.getFormHM().put("find",find);
  	    	this.getFormHM().put("result",result);
  	    }
	}

}
