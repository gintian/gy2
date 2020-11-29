package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.options.SearchTableCardConstantSet;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 交验薪酬表名称是否重复
 * <p>Title:CheckSetnameTitleTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 29, 2007 8:51:43 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CheckSetnameTitleTrans  extends IBusiness {

	public void execute() throws GeneralException {
		
		String fieldsetidss = (String) this.getFormHM().get("fieldsetid");
		String one_Array[]=fieldsetidss.split("`");
		String fieldsetid=one_Array[0];
		String old_title="";
		if(one_Array.length>1)
			old_title=one_Array[1];
		String retype="true";		
		String title=(String) this.getFormHM().get("title");
		if(title==null||title.length()<=0)
			title="";
		if(!title.equals(old_title))
		{
			SearchTableCardConstantSet constantSet=new SearchTableCardConstantSet(this.userView,this.getFrameconn());
			if(constantSet.check()){
				Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
				int num=sop.getCheckValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",title);
				if(num>0)
					retype="false";
			}
		}
		this.getFormHM().put("retype",retype);
	}

}
