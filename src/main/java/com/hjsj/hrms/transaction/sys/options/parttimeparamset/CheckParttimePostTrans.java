package com.hjsj.hrms.transaction.sys.options.parttimeparamset;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 判断关联兼任兼职是否关联@K指标
 * <p>Title:CheckParttimePostTrans.java</p>
 * <p>Description>:CheckParttimePostTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 24, 2011 11:44:48 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class CheckParttimePostTrans extends IBusiness{
	public void execute() throws GeneralException{
		String itemid=(String)this.getFormHM().get("itemid");
		String isk="";
		if(itemid!=null&&itemid.length()>0)
		{
			 FieldItem item = DataDictionary.getFieldItem(itemid);
			 if(item!=null)
			 {
				 String setid=item.getCodesetid();
				 if("@K".equalsIgnoreCase(setid))
					 isk="1";
			 }
		}
		this.getFormHM().put("isk", isk);
	}

}
