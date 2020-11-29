package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchInterviewingRevertCodeTrans extends IBusiness{

	//9028000409
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String enterytype=(String)map.get("entery");
			//String username=(String)this.getFormHM().get("userName");
			String a0100=(String)map.get("aid");
			a0100=PubFunc.getReplaceStr(a0100);
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
    		HashMap mp=bo2.getAttributeValues();
    		String isAttach = "0";
    		if(mp.get("attach")!=null&&((String)mp.get("attach")).length()>0)
    			isAttach=(String)mp.get("attach");
    		EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
    		if(EmployNetPortalBo.interviewingRevertItemid==null)
    		{
    			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
	    		HashMap hm=parameterXMLBo.getAttributeValues();
	     		if(hm!=null&&hm.get("interviewing_itemid")!=null)
	    		{
	    			bo.getInterviewingRevertItemCodeList((String)hm.get("interviewing_itemid"));
	    		}
	    	}
    		String lfType="0";
			if(mp!=null&&mp.get("lftype")!=null)
				lfType=(String)mp.get("lftype");
			this.getFormHM().put("lfType", lfType);
			String dbName=bo.getZpkdbName();
			String userName="";
			String person_type="";
			String info="0";
			String resume_state="";
			if(mp.get("resume_state")!=null&&((String)mp.get("resume_state")).length()>0)
			{
				resume_state=(String)mp.get("resume_state");
			}
            String  person_type_field="";
		
			
			if(mp!=null&&mp.get("person_type")!=null)
				person_type_field=((String)mp.get("person_type")).toLowerCase(); 
			String isDefinitionActive="2";
			if(mp.get("active_field")!=null&&!"".equals((String)mp.get("active_field")))
				isDefinitionActive="1";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer();
			String field="";
			sql.append("select a0101,a0100,"+person_type_field); 
			if("1".equals(isDefinitionActive))
			{
				field=(String)mp.get("active_field");
				sql.append(","+field);
			}
			sql.append(" from "+dbName+"a01 where a0100='"+a0100+"'");
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
		    {
		    	userName=this.frowset.getString("a0101");
		    	a0100=this.frowset.getString("a0100");
		    	person_type=this.frowset.getString(person_type_field);
		    	if("1".equals(isDefinitionActive))
		    	{
		    		this.getFormHM().put("activeValue", this.frowset.getString(field)==null?"1":this.frowset.getString(field));
		    	}
		    	info="1";
		    }
			String writeable=bo.getWriteable(dao, a0100, dbName);
			this.getFormHM().put("person_type",person_type);
		    this.getFormHM().put("userName",userName);
			this.getFormHM().put("dbName",dbName);
			this.getFormHM().put("a0100",a0100);
			this.getFormHM().put("info",info);
			this.getFormHM().put("isDefinitionActive", isDefinitionActive);
			/**简历是否可修改*/
			this.getFormHM().put("writeable", writeable);
			String isOnlyChecked="0";
			String onlyField="";
			if(bo.isOnlyChecked())
			{
				isOnlyChecked="1";
				onlyField=EmployNetPortalBo.isOnlyChecked;
			}
			this.getFormHM().put("onlyField",onlyField);
			this.getFormHM().put("isOnlyCheck", isOnlyChecked);
	    	String value=bo.getInterviewingRevertCodeValue(a0100, EmployNetPortalBo.interviewingRevertItemid);
	    	this.getFormHM().put("interviewingCodeValue",value);
	    	this.getFormHM().put("interviewingRevertItemCodeList", EmployNetPortalBo.interviewingRevertItemCodeList);
	    	this.getFormHM().put("interviewingRevertItemid",EmployNetPortalBo.interviewingRevertItemid);
	    	this.getFormHM().put("a0100", a0100);
	    	this.getFormHM().put("userName",this.getFormHM().get("userName"));
			map.remove("entery");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
}
