package com.hjsj.hrms.transaction.report.actuarial_report.validate_rule;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:AddSubclass.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class AddSubclassTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tag = (String)this.getFormHM().get("tag");
		//String tagname = (String)this.getFormHM().get("tagname");
		ArrayList subclass_value=(ArrayList)this.getFormHM().get("subclass_value");
		String[] subclass = new String[subclass_value.size()];
		for(int i=0;i<subclass_value.size();i++){
			subclass[i] = (String)subclass_value.get(i);
		}
		TargetsortBo infoxml = new TargetsortBo(this.getFrameconn());
		infoxml.saveView_Value(tag,subclass,this.getFrameconn());
		String mess = "";
		
			for(int i=1;i<subclass.length+1;i++){
				
					FieldItem fielditem =DataDictionary.getFieldItem(subclass[i-1].toUpperCase());
					if(fielditem!=null)
						mess += fielditem.getItemdesc()+",";
				
				if(i%5==0)
					mess += "<br>";
			}
		
		
		mess = mess.substring(0,mess.length()-1);
		this.getFormHM().put("mess",mess);

	}

}
