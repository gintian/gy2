package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetTwoDimTrans extends IBusiness {

	public void execute() throws GeneralException {
		String sone=(String)this.getFormHM().get("selOne");
        String stwo=(String)this.getFormHM().get("selTwo");
        if(sone==null||stwo==null|| "#".equalsIgnoreCase(sone)|| "#".equalsIgnoreCase(stwo))
        {
        	throw new GeneralException(ResourceFactory.getProperty("error.static.notselect"));
        }		
		String titles=(String)this.getFormHM().get("mess");
		if("".equals(titles)||titles==null)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("planar.mess.selectn"),"",""));
		}
     	String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		 if(infor_Flag==null)
			 infor_Flag="1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sql=new StringBuffer();
		ArrayList list=new ArrayList();
		try{
				 sql.append("select name,id from sname where InfoKind=");
	             sql.append(infor_Flag);
	             sql.append(" and Type='2'");
	             this.frowset=dao.search(sql.toString());
	             while(this.frowset.next())
	             {
	          	   CommonData cdata=new CommonData(this.frowset.getString("id"),(this.frowset.getString("Name")));
	          	   if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
        		   {
	          		  list.add(cdata);
        		   }
	             }
		}
		catch(Exception exx)
	    {
  	       exx.printStackTrace();
  	       throw GeneralExceptionHandler.Handle(exx);
  	    }finally{
			//初始化标题
  	    	
	    	this.getFormHM().put("title",titles);	    	
  	    	this.getFormHM().put("infor_Flag",infor_Flag);
  	    	this.getFormHM().put("namelist",list);
  	    }

	}

}
