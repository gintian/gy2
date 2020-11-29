/*
 * Created on 2006-1-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveTransferTarOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 ArrayList transferorglist=(ArrayList)this.getFormHM().get("selectedlist");
		 this.getFormHM().put("transferorglist",transferorglist);
		 cat.debug("-----saveTransferOrg------>");
			String ishavedept="false";
			String ishavepos="false";
			String ishaveorg="false";
			for(int i=0;i<transferorglist.size();i++)
			{
				RecordVo vo=(RecordVo)transferorglist.get(i);
				if("UN".equalsIgnoreCase(vo.getString("codesetid")))
					ishaveorg="true";
				if("UM".equalsIgnoreCase(vo.getString("codesetid")))
					ishavedept="true";
				if("@K".equalsIgnoreCase(vo.getString("codesetid")))
					ishavepos="true";
			}		
			if("true".equalsIgnoreCase(ishaveorg))
				ishavedept="UN";
			else
				ishavedept="UM";
			this.getFormHM().put("ishavedept",ishavedept);
			String value = "UNIT_HISTORY_SET";
			RecordVo vo=(RecordVo)transferorglist.get(0);
			if("UN".equalsIgnoreCase(vo.getString("codesetid")) || "UM".equalsIgnoreCase(vo.getString("codesetid")))
				value = "UNIT_HISTORY_SET";
			else if("@K".equalsIgnoreCase(vo.getString("codesetid")))
				value = "POST_HISTORY_SET";
			String HISTORY_SET = SystemConfig
			.getPropertyValue(value);
			if (HISTORY_SET != null
					&& HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(HISTORY_SET)!=null) {
				ArrayList childfielditemlist = DataDictionary
						.getFieldList(HISTORY_SET.toUpperCase(),
								Constant.USED_FIELD_SET);
				childfielditemlist = childfielditemlist!=null?childfielditemlist:new ArrayList();
				this.getFormHM().put("childfielditemlist", childfielditemlist);
				this.getFormHM().put("HISTORY_SET", HISTORY_SET);
				this.getFormHM().put("changemsg", "yes");
			} else {
				this.getFormHM().put("changemsg", "no");
				this.getFormHM().put("childfielditemlist", new ArrayList());
			}
	}

}
