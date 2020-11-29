package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddDsKeyItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		String a_itemid=(String)this.getFormHM().get("a_itemid");
		String a_typeid=(String)this.getFormHM().get("typeid");
		String a_itemname=(String)this.getFormHM().get("a_itemname");
		String a_keyFactors=(String)this.getFormHM().get("aa_keyFactors");
		String a_flag=(String)this.getFormHM().get("object");
		String codeItemValue=(String)this.getFormHM().get("codeItemValue");
		String fieldItemID=(String)this.getFormHM().get("fieldItemID");
		
		StatCutlineBo statCutlineBo=new StatCutlineBo(this.getFrameconn());
		statCutlineBo.saveOrUpdate_dskeyItem(a_itemid,a_typeid,a_itemname,a_keyFactors,a_flag,codeItemValue,fieldItemID);
		
		this.getFormHM().put("a_itemid","");
		this.getFormHM().put("a_itemname","");
		this.getFormHM().put("aa_keyFactors","");
		
	}

}
