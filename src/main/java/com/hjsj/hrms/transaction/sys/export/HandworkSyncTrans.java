package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 手工同步
 * <p>Title:HandworkSyncTrans.java</p>
 * <p>Description>:HandworkSyncTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 26, 2010 10:02:47 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class HandworkSyncTrans extends IBusiness {
	
	public void execute() throws GeneralException {	
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String emporg=(String)this.getFormHM().get("emporg");
		String up = (String)hm.get("up");
		try {
			
			HrSyncBo hsb = new HrSyncBo(this.getFrameconn());
			if(up==null)
				up = "yes";
			if("yes".equalsIgnoreCase(up)){
				Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
				String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
		    	if(uplevel==null||uplevel.length()==0)
		    		uplevel="0";
		    	int nlevel=Integer.parseInt(uplevel);
		    	if(nlevel>0)
		    	{
		    		hsb.setNlevel(nlevel);
		    		hsb.uplevelDeptTable("dept_table");
		    	}
				if(emporg==null)
				{
					hsb.operUserSynchronization();//业务人员
					hsb.organizationSynchronization();//组织机构视图
					/***********人员***********/
					String fields = hsb.getTextValue(HrSyncBo.FIELDS);
					//hsb.setCodefields(codefields);
					
					if(fields==null)
						fields = "";
					hsb.setCodefields(fields);
					String codefields = hsb.getTextValue(HrSyncBo.CODE_FIELDS);
					if(codefields==null)
						codefields = "";
					hsb.setCodefields(codefields);
					hsb.empSynchronization(fields);
					/***********人员end***********/
					/***********单位************/
					String orgfields = hsb.getTextValue(HrSyncBo.ORG_FIELDS);
					if(orgfields==null)
						orgfields = "";
					hsb.setOrgcodefields(orgfields);
					String orgcodefields = hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS);
					if(orgcodefields==null)
						orgcodefields = "";
					hsb.setOrgcodefields(orgcodefields);
					hsb.orgSynchronization(orgfields);
					/***********单位end************/
				}else if("1".equals(emporg))
				{
					hsb.operUserSynchronization();//业务人员
					String fields = hsb.getTextValue(HrSyncBo.FIELDS);
					//hsb.setCodefields(codefields);
					if(fields==null)
						fields = "";
					hsb.setCodefields(fields);
					String codefields = hsb.getTextValue(HrSyncBo.CODE_FIELDS);
					if(codefields==null)
						codefields = "";
					hsb.setCodefields(codefields);
					hsb.empSynchronization(fields);
				}else if("2".equals(emporg))
				{
					hsb.organizationSynchronization();//组织机构视图
					/***********单位************/
					String orgfields = hsb.getTextValue(HrSyncBo.ORG_FIELDS);
					if(orgfields==null)
						orgfields = "";
					hsb.setOrgcodefields(orgfields);
					String orgcodefields = hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS);
					if(orgcodefields==null)
						orgcodefields = "";
					hsb.setOrgcodefields(orgcodefields);
					hsb.orgSynchronization(orgfields);
					/***********单位end************/
				}
				else if("3".equals(emporg))
				{
					
					/***********岗位************/
					String postfields = hsb.getTextValue(HrSyncBo.POST_FIELDS);
					if(postfields==null)
						postfields = "";
					//hsb.setPostcodefields(postfields);
					String postcodefields = hsb.getTextValue(HrSyncBo.POST_CODE_FIELDS);
					if(postcodefields==null)
						postcodefields = "";
					hsb.setPostcodefields(postcodefields);
					hsb.postSynchronization(postfields);
					/***********岗位end************/
				}
			}
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
	}
		

}
