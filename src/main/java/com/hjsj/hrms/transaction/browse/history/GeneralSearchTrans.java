package com.hjsj.hrms.transaction.browse.history;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:GeneralSearchTrans.java</p>
 * <p>Description:GeneralSearchTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 24, 2010 2:57:23 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class GeneralSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"1";
		reqhm.remove("type");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		
		String tablename = (String)reqhm.get("tablename");
		tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		reqhm.remove("tablename");

		ArrayList fieldsetlist = new ArrayList();
		String fieldsetid="0";
		String fieldsetdesc="";
		CommonData obj=new CommonData("hr_emp_hisdata","人员历史时点信息");
	    fieldsetlist.add(obj);
	    if(reqhm.get("fieldsetid")!=null&&!"".equals((String)reqhm.get("fieldsetid")))
	    {
			fieldsetid=(String)reqhm.get("fieldsetid");
			FieldSet ff=DataDictionary.getFieldSetVo(fieldsetid);
			fieldsetdesc=ff.getCustomdesc();
			reqhm.remove("fieldsetid");
		}
		this.getFormHM().put("setlist",fieldsetlist);
		this.getFormHM().put("type",type);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("tablename",tablename);
		this.getFormHM().put("fieldSetId", fieldsetid);
		this.getFormHM().put("fieldSetDesc", fieldsetdesc);
	}

}
