package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchUploadAttachTrans.java</p>
 * <p>Description>:SearchUploadAttachTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-6-4 上午08:42:58</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchUploadAttachTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			//HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String a0100 = (String)this.getFormHM().get("a0100");
			HashMap requestMap=(HashMap)this.getFormHM().get("requestPamaHM");
			/*requestMap.remove("finished");
			requestMap.put("finished", "2");*/
			a0100=PubFunc.getReplaceStr(a0100);
			String nbase = (String)this.getFormHM().get("dbName");
			String writeable=(String)this.getFormHM().get("writeable");
			EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn(),"1");
			//ArrayList uploadFileList = bo.getUploadFileList(a0100, nbase);
			ResumeFileBo resumeFilebo = new ResumeFileBo(this.getFrameconn(), this.userView);
			ArrayList uploadFileList = resumeFilebo.getFiles(nbase, a0100, "1");
			//招聘渠道
			String hireChannel = (String) this.getFormHM().get("hireChannel");
			String isResumePerfection="1";
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap _map=parameterXMLBo.getAttributeValues();
            if(requestMap.get("finished")!=null&& "1".equals((String)requestMap.get("finished")))
            {
    			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
    			String workExperience=employNetPortalBo.getWorkExperience();
    			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
    			String value="";
    			ContentDAO dao = new ContentDAO(this.getFrameconn());
    			if("1".equals(isDefineWorkExperience))
    			{
    			//	value=(String)this.getFormHM().get("workExperience");
    				String workExperience_item=(String)_map.get("workExperience");
    				this.frowset=dao.search("select "+workExperience_item+" from "+nbase+"a01 where a0100='"+a0100+"'");
    				if(this.frowset.next())
    					value=this.frowset.getString(1)!=null?this.frowset.getString(1):"1";  //(String)this.getFormHM().get("workExperience");
    			
    			}
    			
				
    			//定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
    			//headHire、猎头招聘    01、校园招聘   02、社会招聘
    			if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
    				hireChannel = "02";
				
				isResumePerfection = employNetPortalBo.checkRequired(hireChannel,a0100);
				
    			requestMap.remove("finished");
            }
            this.getFormHM().put("isResumePerfection", isResumePerfection);
		//	this.getFormHM().put("onlyField",(String)this.getFormHM().get("onlyField"));
		//	this.getFormHM().put("isOnlyCheck", (String)this.getFormHM().get("isOnlyChecked"));
			
			String isOnlyChecked="0";
			String onlyField="";
			if(bo.isOnlyChecked())
			{
				isOnlyChecked="1";
				onlyField=EmployNetPortalBo.isOnlyChecked;
			}
			
			ArrayList list = bo.getAttachCodeset(_map, hireChannel);
			//如果启用了上传文件分类，则对已上传文件进行排序
			String attach_codeset = (String) _map.get("attachCodeset");
			if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset))
				uploadFileList = bo.sortFileList(list,uploadFileList);
			this.getFormHM().put("onlyField",onlyField);
			this.getFormHM().put("isOnlyCheck", isOnlyChecked);
			this.getFormHM().put("writeable", writeable);
			this.getFormHM().put("uploadFileList", uploadFileList);
			this.getFormHM().put("a0100",a0100);
			this.getFormHM().put("dbName",nbase);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
