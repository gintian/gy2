package com.hjsj.hrms.transaction.dtgh.party;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * @author xujian
 *Jan 14, 2010
 */
public class AddorEditPartyInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		String param = (String)this.getFormHM().get("param");
		ArrayList fieldsetlist = new ArrayList();
		if(("Y".equals(param)))
			fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.PARTY_FIELD_SET);	    
	    else if(("V".equals(param)))
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.MEMBER_FIELD_SET);
	    else if(("W".equals(param)))
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.TRADEUNION_FIELD_SET);
	    else if(("H".equals(param)))
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.JOB_FIELD_SET);
		
		this.getFormHM().put("fieldsetlist", fieldsetlist);
		this.getFormHM().put("sign","");
	}

}
