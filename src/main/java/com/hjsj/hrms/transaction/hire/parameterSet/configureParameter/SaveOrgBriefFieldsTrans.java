package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SaveOrgBriefFieldsTrans</p> 
 *<p>Description:保存单位介绍指标参数</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 30, 2007:4:18:01 PM</p> 
 *@author lizhenwei
 *@version 4.0
 */

public class SaveOrgBriefFieldsTrans extends IBusiness{
	public void execute()throws GeneralException{
		try{
		    HashMap hm= (HashMap)this.getFormHM().get("requestPamaHM");
		    String orgFieldIDs=(String)hm.get("orgFieldIDs");
		    String contentType=(String)hm.get("contentType");
		    String type=(String)hm.get("type");
		    ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
		    parameterXMLBo.saveBriefPara(orgFieldIDs,contentType,"org_brief");
		    ArrayList orgFieldList= new ArrayList();
		    ArrayList contentTypeList = new ArrayList();
		    ArrayList orgIntroList = new ArrayList();
		    ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
		    orgFieldList=parameterSetBo.getOrgFieldList();
		    contentTypeList = parameterSetBo.getContentTypeList();
		    String orgId = (String)this.getFormHM().get("hiddenOrgId");
            String orgName = (String)this.getFormHM().get("orgName");
		    orgIntroList=parameterSetBo.getOrgIntroList(this.getUserView(),orgFieldIDs,contentType,type,orgId,orgName);
		    this.getFormHM().put("orgFieldList",orgFieldList);
			this.getFormHM().put("contentTypeList",contentTypeList);
			this.getFormHM().put("orgList",orgIntroList);
			this.getFormHM().put("orgName",orgName==null?"":orgName);
			this.getFormHM().put("hiddenOrgId",orgId==null?"":orgId);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
