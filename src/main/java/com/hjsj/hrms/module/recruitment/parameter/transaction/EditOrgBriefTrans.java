package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:EditOrgBriefTrans</p> 
 *<p>Description:编辑单位介绍指标参数</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 6, 2007:11:51:47 AM</p> 
 *@author lizhenwei
 *@version 4.0
 */
public class EditOrgBriefTrans  extends IBusiness{
	@Override
    public void execute() throws GeneralException{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String B0110=(String)hm.get("codeitemid");
		String type=(String)hm.get("type");
		ParameterXMLBo bo = new ParameterXMLBo(this.getFrameconn());
		String org_brief=bo.getBriefParaValue();
		String orgFieldID="";
		String contentType="";
		if(B0110 == null || "".equalsIgnoreCase(B0110))
			return;
		if(org_brief != null && org_brief.trim().length()>0){
			String[] org_brief_Arr = org_brief.split(",");
		    orgFieldID=org_brief_Arr[0];
		    contentType=org_brief_Arr[1];
		}
		StringBuffer sql = new StringBuffer();
		boolean flag1=false;
		boolean flag2=false;
		String contentTypeValue="";
		sql.append("select B0110 ");
		if(orgFieldID!=null&&orgFieldID.trim().length()>0){
			sql.append(","+orgFieldID);
			flag1 = true;
		}
		if(contentType!=null&&contentType.trim().length()>0){
			sql.append(","+contentType);
			flag2=true;
		}
		sql.append(" from B01 where b0110='");
		sql.append(B0110+"'");
		try{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    this.frowset=dao.search(sql.toString());
		    while(this.frowset.next()){
		    	if(flag1&&flag2){
		    	     contentTypeValue = this.frowset.getString(contentType);
		    	     if(contentTypeValue == null){
		    	    	 this.getFormHM().put("url","");
		    	    	 this.getFormHM().put("content","");
		    	    	 this.getFormHM().put("urlDisplay","display:block");
		    	    	 this.getFormHM().put("contentDisplay","display:block");
		    	     }else if("0".equals(contentTypeValue)){
		    	    	 this.getFormHM().put("url",this.frowset.getString(orgFieldID));
		    	    	 this.getFormHM().put("content","");
		    	    	 this.getFormHM().put("urlDisplay","display:block");
		    	    	 this.getFormHM().put("contentDisplay","display:none");
		    	     }else if("1".equals(contentTypeValue)){
		    	    	 this.getFormHM().put("url","");
		    	    	 this.getFormHM().put("content",this.frowset.getString(orgFieldID));
		    	    	 this.getFormHM().put("urlDisplay","display:none");
		    	    	 this.getFormHM().put("contentDisplay","display:block");
		    	     }
		    	}else{
		    		 this.getFormHM().put("url","");
   	    	         this.getFormHM().put("content","");
   	    	         this.getFormHM().put("urlDisplay","display:block");
	    	    	 this.getFormHM().put("contentDisplay","display:block");
		    	}
		    	
		    }
		    this.getFormHM().put("type",type);
		    this.getFormHM().put("codeitemid",B0110);
		    this.getFormHM().put("contentTypeValue",type);
		    this.getFormHM().put("isClose","1");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
