package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SaveGeneralInitTrans extends IBusiness {
	
	public void execute() throws GeneralException {
	
	ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
 	String infor_Flag=(String)this.getFormHM().get("infor_Flag");
 	String find=(String)this.getFormHM().get("find");
    String htory=(String)this.getFormHM().get("history");
    String result=(String)this.getFormHM().get("result");
	 if(infor_Flag==null)
		 infor_Flag="1";
	String[] sel=(String[])this.getFormHM().get("selects");
	if(sel==null)
	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("general.mess.nexist"),"",""));
	//System.out.println("=ss="+mm+flist.toString()+infor_Flag);
	ContentDAO dao=new ContentDAO(this.getFrameconn());
	StringBuffer sql=new StringBuffer();
	ArrayList list=new ArrayList();
	String title=null;
	try{
			 sql.append("select name,id from sname where Type='1' and InfoKind=");
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
	    	this.getFormHM().put("find",find);
	        this.getFormHM().put("history",htory);
	        this.getFormHM().put("result",result);
	    }
	}

}
